package it.ficr.services;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ficr.elements.Event;
import it.ficr.exceptions.MalformattedElementException;
import it.ficr.repositories.EventRepository;
import org.hibernate.event.service.spi.EventListenerRegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventCatalogueService {


    private static final Logger log = LoggerFactory.getLogger(EventCatalogueService.class);

    @Autowired
    private EventRepository eventRepository;




    public Event createEvent(Event event){
        eventRepository.saveAndFlush(event);
        return event;
    }

    public Event updateEvent(Event event){
        eventRepository.saveAndFlush(event);
        return event;
    }



    public Event buildEvent(String name, JsonNode node) throws MalformattedElementException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterYear = new SimpleDateFormat("yyyy");

        Date date = new Date();
        String dateTxt = node.get("Date").asText();
        try{
            if(dateTxt.length()>4){
                date = formatter.parse(dateTxt);
            }else{
                date = formatterYear.parse(dateTxt);
            }

        }catch(Exception e){
            log.warn("Unable to parse Athlete birthday:{}", dateTxt);
        }

        String place = node.get("Place").asText();
        Event nEvent = new Event(name, place, date);
        Optional<Event> dbEvent = eventRepository.findByEventIdentifier(nEvent.getEventIdentifier());
        if(dbEvent.isPresent())
            return dbEvent.get();
        else{
            eventRepository.saveAndFlush(nEvent);
            return nEvent;
        }

    }

    public List<Event> getEvents(String name, String place, Integer year) {
        List<Event> events = eventRepository.findAll();
        if(name!=null && !name.isEmpty()){
            events = events.stream().filter(e -> e.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        }

        if(place!=null && !place.isEmpty()){
            events = events.stream().filter(e -> e.getPlace().contains(place)).collect(Collectors.toList());
        }

        if(year!=null ){
            events = events.stream().filter(e -> e.getYear()==year.intValue()).collect(Collectors.toList());
        }

        return events;
    }
}
