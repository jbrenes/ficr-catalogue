package it.ficr.elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String place;
    private Date date;
    private UUID eventIdentifier;

    @OneToMany(mappedBy="event")
    private List<Result> results = new ArrayList<>();



    @JsonCreator
    public Event(@JsonProperty("name") String name,@JsonProperty("place") String place, @JsonProperty("date")Date date) {
        this.name = name;
        this.place = place;
        this.date = date;

        this.eventIdentifier = generateIdentifier();
    }
    @JsonCreator
    public Event(@JsonProperty("name") String name,@JsonProperty("place") String place, @JsonProperty("date")Date date, @JsonProperty("results")List<Result> results) {
        this.name = name;
        this.place = place;
        this.date = date;
        if(results!=null) this.results = results;
        this.eventIdentifier = generateIdentifier();
    }

    public Event() {
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public void addResult(Result r){
        results.add(r);
    }

    public Date getDate() {
        return date;
    }

    public UUID getEventIdentifier() {
        return eventIdentifier;
    }

    public List<Result> getResults() {
        return results;
    }

    private UUID generateIdentifier(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String seed = name+place+formatter.format(date);
        seed = seed.replaceAll("\\s", "");
        return UUID.nameUUIDFromBytes(seed.getBytes());
    }
}
