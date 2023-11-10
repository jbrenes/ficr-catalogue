package it.ficr.services;

import com.fasterxml.jackson.databind.JsonNode;
import it.ficr.elements.Athlete;
import it.ficr.exceptions.MalformattedElementException;
import it.ficr.nbi.AthleteRestController;
import it.ficr.repositories.AthleteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AthleteCatalogueService {

    private static final Logger log = LoggerFactory.getLogger(AthleteCatalogueService.class);
    @Autowired
    private AthleteRepository athleteRepository;

    public void createAthlete(Athlete athlete){

        athleteRepository.saveAndFlush(athlete);


    }

    public Optional<Athlete> findByIdentifier(UUID identifier){
        return athleteRepository.findByAthleteIdentifier(identifier);
    }

    public Optional<Athlete> findByNameSurname(String name, String surname){
        return athleteRepository.findByNameAndSurname(name, surname);
    }

    public void updateAthlete(Athlete athlete){

        athleteRepository.saveAndFlush(athlete);


    }

    public List<Athlete> getAthletes(String name, String surname, String society, Integer year){
        List<Athlete> athletes = athleteRepository.findAll();

        //TODO implement JPA level filters
        if(name!=null && !name.isEmpty()){
            athletes = athletes.stream().filter(a -> a.getName().equals(name)).collect(Collectors.toList());
        }

        if(surname!=null && !surname.isEmpty()){
            athletes = athletes.stream().filter(a -> a.getSurname().equals(surname)).collect(Collectors.toList());
        }

        if(year!=null){
            athletes = athletes.stream().filter(a -> a.getBirthday().getYear()==year).collect(Collectors.toList());
        }

        if(society!=null&& !society.isEmpty()){
            athletes = athletes.stream().filter(a -> a.inSociety(society)).collect(Collectors.toList());
        }
        return athletes;
    }

    public Athlete buildAthlete(JsonNode node) throws MalformattedElementException {
        String name = node.get("PlaName").asText();
        String surname = node.get("PlaSurname").asText();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


        try {
            Date date = formatter.parse(node.get("PlaBirth").asText());
            log.info("Building athlete {} {} {}", name, surname, date);
            Athlete nAthlete = new Athlete(name, surname, date);
            Optional<Athlete> dbAthlete = athleteRepository.findByAthleteIdentifier(nAthlete.getAthleteIdentifier());
            if(dbAthlete.isPresent()){
                return dbAthlete.get();
            }else{
                athleteRepository.saveAndFlush(nAthlete);
                return nAthlete;
            }
        } catch (ParseException e) {
            log.error("Error", e);
            throw new MalformattedElementException("Malformatted element athlete:"+node);
        }
    }

    public void deleteAthlete(String identifier){

    }


}
