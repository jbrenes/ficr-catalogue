package it.ficr.services;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.JsonNode;
import it.ficr.elements.Event;
import it.ficr.exceptions.MalformattedElementException;
import it.ficr.repositories.EventRepository;
import org.hibernate.event.service.spi.EventListenerRegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
        try {
            Date date = formatter.parse(node.get("Date").asText());
            String place = node.get("Place").asText();
            Event nEvent = new Event(name, place, date);
            Optional<Event> dbEvent = eventRepository.findByEventIdentifier(nEvent.getEventIdentifier());
            if(dbEvent.isPresent())
                return dbEvent.get();
            else{
                eventRepository.saveAndFlush(nEvent);
                return nEvent;
            }
        } catch (ParseException e) {
            log.error("Error", e);
            throw new MalformattedElementException("Malformatted element event:"+node);
        }
    }

    public List<Event> getEvents(String name, String place, Integer year) {
        List<Event> events = eventRepository.findAll();
        if(name!=null && !name.isEmpty()){
            events = events.stream().filter(e -> e.getName().contains(name)).collect(Collectors.toList());
        }

        if(place!=null && !place.isEmpty()){
            events = events.stream().filter(e -> e.getPlace().contains(place)).collect(Collectors.toList());
        }

        if(year!=null ){
            events = events.stream().filter(e -> e.getDate().getYear()==year.intValue()).collect(Collectors.toList());
        }

        return events;
    }
}
