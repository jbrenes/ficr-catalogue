package it.ficr.services;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import it.ficr.elements.Athlete;
import it.ficr.elements.AthleteInfo;
import it.ficr.elements.specifications.AthleteSpecification;
import it.ficr.elements.specifications.SearchCriteria;
import it.ficr.exceptions.ElementNotFoundException;
import it.ficr.exceptions.MalformattedElementException;
import it.ficr.nbi.AthleteRestController;
import it.ficr.repositories.AthleteInfoRepository;
import it.ficr.repositories.AthleteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AthleteCatalogueService {

    private static final Logger log = LoggerFactory.getLogger(AthleteCatalogueService.class);
    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private AthleteInfoRepository athleteInfoRepository;

    public void createAthlete(Athlete athlete){

        athleteRepository.saveAndFlush(athlete);


    }



    public void updateAthlete(Athlete athlete){

        try{
            athleteInfoRepository.saveAndFlush(athlete.getAthleteInfo());
            athleteRepository.saveAndFlush(athlete);

        }catch(Exception e){
            log.error("error",e);
        }


    }

    public List<AthleteInfo> getAthletes(String name, String surname, String society, Integer year){

        List<Specification<AthleteInfo>> searchSpecs = new ArrayList<>();


        if(name!=null && !name.isEmpty()){
            searchSpecs.add(new AthleteSpecification(new SearchCriteria("name", ":", name)));
        }
        if(surname!=null && !surname.isEmpty()){
            searchSpecs.add(new AthleteSpecification(new SearchCriteria("surname", ":", surname)));
        }


        return athleteInfoRepository.findAll(Specification.allOf(searchSpecs));
    }


    public Athlete getAthlete(UUID athleteIdentifier) throws ElementNotFoundException {
        Optional<Athlete> athlete = athleteRepository.findByAthleteIdentifier(athleteIdentifier);
        if(athlete.isPresent()){
            return athlete.get();
        }else throw new ElementNotFoundException("Could not find athlete with ID:"+athleteIdentifier);
    }

    public Athlete buildAthlete(JsonNode node) throws MalformattedElementException {
        String name = node.get("PlaName").asText();
        String surname = node.get("PlaSurname").asText();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterYear= new SimpleDateFormat("yyyy");


        Date date  = null;
        if(!node.get("PlaBirth").asText().isEmpty()){
            String dateTxt = node.get("PlaBirth").asText();
            try {
                if (dateTxt.length() > 4) {
                    date = formatter.parse(dateTxt);
                } else {
                    date = formatterYear.parse(dateTxt);
                }
            }catch (Exception e){
                log.warn("Could not read date: {}", dateTxt);
            }
        }


        log.debug("Building athlete {} {} {}", name, surname, date);
        Optional<Athlete> dbAthlete = null;
        if(date!=null){
            dbAthlete = athleteRepository.findByNameAndSurnameAndBirthday(name, surname, date);
        }else{
            dbAthlete = athleteRepository.findByNameAndSurname(name, surname);
        }



        if(dbAthlete.isPresent()){
            return dbAthlete.get();
        }else{

            AthleteInfo info = new AthleteInfo(name, surname);
            athleteInfoRepository.saveAndFlush(info);
            Athlete nAthlete = new Athlete(info.getAthleteIdentifier(), name, surname, date);
            nAthlete.setAthleteInfo(info);
            athleteRepository.saveAndFlush(nAthlete);
            return nAthlete;
        }
    }

    public void deleteAthlete(String identifier){

    }


}
