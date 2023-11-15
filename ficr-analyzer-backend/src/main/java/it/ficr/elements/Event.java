package it.ficr.elements;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class Event {


    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String place;
    private Date date;

    private UUID eventIdentifier;

    @OneToMany(mappedBy="event",fetch = FetchType.EAGER)
    private List<Result> results = new ArrayList<>();


    private Set<UUID> athletes = new HashSet<>();




    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_info_identifier")
    private EventInfo eventInfo;

    @JsonCreator
    public Event(@JsonProperty("eventIdentifier") UUID eventIdentifier,@JsonProperty("name") String name,@JsonProperty("place") String place, @JsonProperty("date")Date date) {
        this.name = name;
        this.place = place;
        this.date = date;

        this.eventIdentifier = eventIdentifier;
    }


    public Event() {
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }


    public void addAthlete(AthleteInfo a){
        this.athletes.add(a.getAthleteIdentifier());
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




    public Set<UUID> getAthletes() {
        return athletes;
    }

    @JsonProperty("year")
    public int getYear(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }


    public EventInfo getEventInfo() {
        return eventInfo;
    }
}
