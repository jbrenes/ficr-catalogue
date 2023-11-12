package it.ficr.elements;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Athlete {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    private String name;
    private String surname;

    @JsonProperty("birthday")
    private Date birthday;

    private String fickId;
    private UUID athleteIdentifier;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonManagedReference

    private List<Result> results = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Society> societies = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Event> events = new ArrayList<>();




    @JsonCreator()
    public Athlete(@JsonProperty("name") String name,
                   @JsonProperty("surname") String surname,
                   @JsonProperty("birthday") Date birthday) {
        this.name = name;
        this.birthday = birthday;
        this.fickId = fickId;
        if(results!=null) this.results = results;
        if(societies!=null) this.societies= societies;
        this.surname= surname;
        this.athleteIdentifier = generateIdentifier();
    }

    @JsonCreator()
    public Athlete(@JsonProperty("name") String name,
                   @JsonProperty("surname") String surname,
                   @JsonProperty("birthday") Date birthday,
                   @JsonProperty("fickId")String fickId,
                   @JsonProperty("results") List<Result> results,
                   @JsonProperty("societies") List<Society> societies,
                   @JsonProperty("events") List<Event> events) {
        this.name = name;
        this.birthday = birthday;
        this.fickId = fickId;
        if(results!=null) this.results = results;
        if(societies!=null) this.societies= societies;
        if(events!=null) this.events=events;
        this.surname= surname;
        this.athleteIdentifier = generateIdentifier();
    }

    public Long getId() {
        return id;
    }

    public UUID getAthleteIdentifier() {
        return athleteIdentifier;
    }

    public Date getBirthday() {
        return birthday;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFickId() {
        return fickId;
    }

    public void setFickId(String fickId) {
        this.fickId = fickId;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void addResult(Result result){
        this.results.add(result);
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void addEvent(Event event){
        Optional<Event> cEvent = events.stream().filter(e -> e.getEventIdentifier().equals(event.getEventIdentifier())).findAny();
        if(!cEvent.isPresent()){
            events.add(event);
        }
    }

    public boolean inSociety(String societyName){
        Optional<Society> society = societies.stream().filter(s ->s.getName().equals(societyName)).findAny();
        return society.isPresent();
    }

    public void addSociety(Society soc){
        societies.add(soc);
    }

    @JsonProperty("participations")
    public List<String> eventParticipations(){
        return events.stream().map(e->e.getName()+" "+e.getYear()).collect(Collectors.toList());
    }
    private UUID generateIdentifier(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String seed = name+surname+ formatter.format(birthday);
        seed = seed.replaceAll("\\s", "");
        return UUID.nameUUIDFromBytes(seed.getBytes());
    }
    public Athlete() {
    }
}
