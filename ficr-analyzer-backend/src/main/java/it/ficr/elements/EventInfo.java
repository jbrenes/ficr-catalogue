package it.ficr.elements;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class EventInfo {



    private String name;
    private String place;
    private Date date;
    private String url;




    @Id
    @GeneratedValue
    private UUID eventIdentifier;


    @JsonCreator
    public EventInfo(@JsonProperty("name") String name, @JsonProperty("place") String place, @JsonProperty("date")Date date, @JsonProperty("url") String url) {
        this.name = name;
        this.place = place;
        this.date = date;
        this.url=url;


    }


    public EventInfo() {
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }



    public Date getDate() {
        return date;
    }

    public UUID getEventIdentifier() {
        return eventIdentifier;
    }




    @JsonProperty("year")
    public int getYear(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }




}
