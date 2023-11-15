package it.ficr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import it.ficr.elements.Athlete;
import it.ficr.elements.Event;
import it.ficr.elements.Result;
import it.ficr.elements.Society;
import it.ficr.exceptions.MalformattedElementException;
import it.ficr.repositories.ResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ResultCatalogueService {

    private static final Logger log = LoggerFactory.getLogger(ResultCatalogueService.class);

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private AthleteCatalogueService athleteCatalogueService;

    @Autowired
    private EventCatalogueService eventCatalogueService;


    @Autowired
    private SocietyCatalogueService societyCatalogueService;

    @Value("${onboard-season-enable:false}")
    private boolean enableSeasonOnboarding;

    public void createResult(Result r){
        log.debug("Received request to create result");
        resultRepository.saveAndFlush(r);
    }

    public void updateResult(Result r){
        log.debug("Received request to update result");
        resultRepository.saveAndFlush(r);
    }



    @Async
    public void onboardSeason(int year) throws MalformattedElementException {
        log.info("Received request to onboard season:{}", year);
        if(enableSeasonOnboarding){
            String urlTemplate= "https://apimanvarie.ficr.it/VAR/mpcache-30/get/schedule/%s/*/19";
            String url = String.format(urlTemplate, year);
            RestTemplate restTemplate = new RestTemplate();
            String  response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();


            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(response, JsonNode.class);
            } catch (JsonProcessingException e) {
                log.error("Error",e);
                return;
            }
            if(rootNode.has("data")) {
                log.info("Iterating over data");
                Iterator<JsonNode> resultIterator = rootNode.get("data").elements();
                while(resultIterator.hasNext()){
                    JsonNode eventNode = resultIterator.next();
                    onboardEvent(eventNode.get("CodicePub").asText());
                }
            }

        }else    throw new MalformattedElementException("Not supported");



    }


    public void onboardEvent(String codePub) {
        String url = "https://apicanoavelocita.ficr.it/CAV/mpcache-30/get/programdate/"+codePub;
        log.info("Received request to onboard event from URL {}", url);
        RestTemplate restTemplate = new RestTemplate();

        String  response = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;


        try {
            rootNode = mapper.readValue(response, JsonNode.class);
        } catch (JsonProcessingException e) {
            log.error("Error",e);
        }
        if(rootNode!=null && rootNode.has("data")){
            log.info("Iterating over data");
            Iterator<JsonNode> resultIterator = rootNode.get("data").elements();
            while(resultIterator.hasNext()){
                JsonNode resultRef = resultIterator.next();
                //code=data['codS']
                //date=data['gi']
                String code = resultRef.get("codS").asText();

                Iterator<JsonNode> elementIterator = resultRef.get("e").elements();
                log.info("Iterating over elements");

                while(elementIterator.hasNext()){
                    JsonNode element = elementIterator.next();
                    // cat=e['c0']
                    // codeC1=e['c1']
                    // codeC2=e['c2'][-2:]
                    // codeC3=e['c3']
                    // r=requests.get("https://apicanoavelocita.ficr.it/CAV/mpcache-10/get/result/%s/%s/%s/%s/%s/%s"%(gara,code,cat,codeC1,codeC2,codeC3))
                    String cat = element.get("c0").asText();
                    String codeC1 = element.get("c1").asText();
                    String codeC2 = element.get("c2").asText();
                    codeC2 = codeC2.substring(codeC2.length()-2);
                    String codeC3 = element.get("c3").asText();
                    String urlTemplate="https://apicanoavelocita.ficr.it/CAV/mpcache-10/get/result/%s/%s/%s/%s/%s/%s";
                    String urlResults =String.format(urlTemplate, codePub,code, cat, codeC1, codeC2, codeC3 );
                    log.info("Retrieving results from: {}", urlResults);
                    String  responseResults = restTemplate.getForObject(urlResults, String.class);
                    //deleteResultsFromUrl(urlResults);
                    JsonNode elementJson = null;
                    try {
                        elementJson= mapper.readValue(responseResults, JsonNode.class);
                        onboardJson(elementJson, url, urlResults);
                    } catch (JsonMappingException e) {
                        log.error("Error", e);
                    } catch (JsonProcessingException e) {
                        log.error("Error", e);
                    } catch (MalformattedElementException e) {
                        log.error("Error", e);
                    }

                }
            }

        }else log.warn("No DATA element");


    }

    public void deleteResultsFromUrl(String url){
        log.info("Deleting results from URL:{}", url);
        List<Result> results = resultRepository.findByResultUrl(url);
        for(Result res : results){
            resultRepository.delete(res);
        }
    }

    public void onboardJson(JsonNode resultsObject, String eventUrl, String raceUrl) throws MalformattedElementException {
        if(resultsObject.has("data")){
            JsonNode data = resultsObject.get("data");
            Event event=null;
            try {
                event = eventCatalogueService.buildEvent(data.get("Export").get("ExpName").asText(), data.get("Event"), eventUrl);
              //  event.addUrl(eventUrl);

            }catch (Exception e){
                log.warn("Could not read Event data from: {}", raceUrl, e);
                return;
            }

            String categoryCode= data.get("Category").get("Cod").asText();
            String categoryName= data.get("Category").get("Ita").asText();

            String competitionCode= data.get("Competition").get("Cod").asText();
            String competitionName= data.get("Competition").get("Ita").asText();
            List<Result> results = new ArrayList<>();
            if(data.has("data")){
                Iterator<JsonNode> resultRowIterator = data.get("data").elements();

                while (resultRowIterator.hasNext()){
                    JsonNode result = resultRowIterator.next();
                    Society society = societyCatalogueService.buildSociety(result.get("TeamDescrIta").asText(),
                            result.get("PlaTeamCod").asText());
                    String time = result.get("MemPrest").textValue();
                    Result nResult = new Result(null,
                            time,
                            event,
                            categoryCode,
                            categoryName,
                            competitionCode,
                            competitionName,
                            society,
                            raceUrl);

                    society.addResult(nResult);


                    createResult(nResult);
                    event.addResult(nResult);

                    List<Athlete> crew = new ArrayList<>();

                    if(!result.has("Players")){
                        log.debug("Single person crew result");
                        crew.add(athleteCatalogueService.buildAthlete(result));

                    }else{
                        log.debug("Multiple person crew result");
                        Iterator<JsonNode> playerRowIterator = result.get("Players").elements();
                        while(playerRowIterator.hasNext()){
                            JsonNode currentPlayer = playerRowIterator.next();
                            crew.add(athleteCatalogueService.buildAthlete(currentPlayer));
                        }

                    }
                    nResult.setCrew(crew);
                    for(Athlete athlete : crew){
                        athlete.addResult(nResult);
                        if(!athlete.inSociety(society.getName())){
                            athlete.addSociety(society);
                        }
                        athlete.addEvent(event.getEventInfo());
                        event.addAthlete(athlete.getAthleteInfo());
                        eventCatalogueService.updateEvent(event);

                        athleteCatalogueService.updateAthlete(athlete);
                    }
                    societyCatalogueService.updateSociety(society);
                    resultRepository.saveAndFlush(nResult);
                    results.add(nResult);


                }

            }



        }

    }

    public void onboardFile(File file) throws IOException, MalformattedElementException {
        log.debug("Received request to onboard results from file");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode resultsObject = mapper.readValue(file, JsonNode.class);
        onboardJson(resultsObject, null, null);


    }




    public List<Result> getResults(String categoryCode, String categoryName, String competitionCode, String competitionName){
        return resultRepository.findAll();
    }
}
