package it.ficr.services;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ficr.elements.Event;
import it.ficr.elements.EventInfo;
import it.ficr.exceptions.ElementNotFoundException;
import it.ficr.exceptions.MalformattedElementException;
import it.ficr.repositories.EventInfoRepository;
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
    @Autowired
    private EventInfoRepository eventInfoRepository;




    public Event createEvent(Event event){
        eventRepository.saveAndFlush(event);
        return event;
    }

    public Event updateEvent(Event event){
        try{
            eventInfoRepository.saveAndFlush(event.getEventInfo());
            eventRepository.saveAndFlush(event);
        }catch(Exception e){
            log.error("Error", e);
        }

        return event;
    }



    public Event buildEvent(String name, JsonNode node, String url) throws MalformattedElementException {
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

        Optional<EventInfo> dbEvent = null;
        if(url!=null && !url.isEmpty()){
            dbEvent=eventInfoRepository.findByUrl(url);
        }else{
            dbEvent=eventInfoRepository.findByNameAndPlaceAndDate(name, place, date);
        }
        if(dbEvent.isPresent())
            return eventRepository.findByEventIdentifier(dbEvent.get().getEventIdentifier()).get();
        else{
            EventInfo info = new EventInfo(name, place, date, url);
            eventInfoRepository.saveAndFlush(info);
            Event nEvent = new Event(info.getEventIdentifier(), name, place, date);
            nEvent.setEventInfo(info);

            eventRepository.saveAndFlush(nEvent);
            return nEvent;
        }

    }

    public List<EventInfo> getEvents(String name, String place, Integer year) {
        List<EventInfo> events = eventInfoRepository.findAll();
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

    public Event getEvent(UUID eventIdentifier) throws ElementNotFoundException {
        Optional<Event> event = eventRepository.findByEventIdentifier(eventIdentifier);
        if(event.isPresent()){
            return event.get();
        }else throw new ElementNotFoundException("Event not found:"+eventIdentifier);
    }
}
