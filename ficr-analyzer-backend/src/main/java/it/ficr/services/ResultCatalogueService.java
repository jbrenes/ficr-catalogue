package it.ficr.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import it.ficr.elements.Athlete;
import it.ficr.elements.Event;
import it.ficr.elements.Result;
import it.ficr.exceptions.MalformattedElementException;
import it.ficr.repositories.ResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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



    public void createResult(Result r){
        log.debug("Received request to create result");
        resultRepository.saveAndFlush(r);
    }

    public void updateResult(Result r){
        log.debug("Received request to update result");
        resultRepository.saveAndFlush(r);
    }

    public void onboardFile(File file) throws IOException, MalformattedElementException {
        log.debug("Received request to onboard results from file");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode resultsObject = mapper.readValue(file, JsonNode.class);

        if(resultsObject.has("data")){
            JsonNode data = resultsObject.get("data");
            Event event =  eventCatalogueService.buildEvent(data.get("Export").get("ExpName").asText(), data.get("Event"));
            String categoryCode= data.get("Category").get("Cod").asText();
            String categoryName= data.get("Category").get("Ita").asText();

            String competitionCode= data.get("Competition").get("Cod").asText();
            String competitionName= data.get("Competition").get("Ita").asText();
            List<Result> results = new ArrayList<>();
            if(data.has("data")){
                Iterator<JsonNode> resultRowIterator = data.get("data").elements();

               while (resultRowIterator.hasNext()){
                   JsonNode result = resultRowIterator.next();

                   String time = result.get("MemPrest").textValue();
                   Result nResult = new Result(null,
                           time,
                           event,
                           categoryCode,
                           categoryName,
                           competitionCode,
                           competitionName);

                   createResult(nResult);

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
                       athleteCatalogueService.updateAthlete(athlete);
                   }
                   resultRepository.saveAndFlush(nResult);
                   results.add(nResult);
                   event.addResult(nResult);
                   eventCatalogueService.updateEvent(event);
               }

            }



        }

    }




    public List<Result> getResults(String categoryCode, String categoryName, String competitionCode, String competitionName){
        return resultRepository.findAll();
    }
}
