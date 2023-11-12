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
    @JsonIgnore
    private Long id;

    private String name;
    private String place;
    private Date date;
    private UUID eventIdentifier;

    @OneToMany(mappedBy="event",fetch = FetchType.EAGER)
    private List<Result> results = new ArrayList<>();


    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> eventUrls = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Athlete> athletes = new ArrayList<>();

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


    public void addUrl(String url){
        if(!eventUrls.contains(url)){
            eventUrls.add(url);
        }
    }

    public void addAthlete(Athlete a){
        Optional<Athlete> cAthlete = athletes.stream().filter(aux -> aux.getAthleteIdentifier().equals(a.getAthleteIdentifier())).findAny();
        if(!cAthlete.isPresent()){
            athletes.add(a);
        }
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

    public List<String> getEventUrls() {
        return eventUrls;
    }

    public List<Athlete> getAthletes() {
        return athletes;
    }

    @JsonProperty("year")
    public int getYear(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
    private UUID generateIdentifier(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String seed = name+place+formatter.format(date);
        seed = seed.replaceAll("\\s", "");
        return UUID.nameUUIDFromBytes(seed.getBytes());
    }
}
