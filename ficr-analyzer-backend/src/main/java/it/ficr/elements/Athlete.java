package it.ficr.elements;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Athlete {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String surname;

    @JsonProperty("birthday")
    private Date birthday;

    private String fickId;

    private UUID athleteIdentifier;

    @ManyToMany()
    @JsonManagedReference

    private List<Result> results = new ArrayList<>();


    private Set<UUID> events = new HashSet<>();

    @ManyToMany()
    @JsonManagedReference
    private List<Society> societies = new ArrayList<>();



    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "athlete_info_identifier")
    private AthleteInfo athleteInfo;


    @JsonCreator()
    public Athlete(@JsonProperty("athleteIdentifier") UUID athleteIdentifier,
            @JsonProperty("name") String name,
                   @JsonProperty("surname") String surname,
                   @JsonProperty("birthday") Date birthday) {
        this.name = name;
        this.birthday = birthday;
        this.surname= surname;
        this.athleteIdentifier = athleteIdentifier;
    }



    public UUID getAthleteIdentifier() {
        return athleteIdentifier;
    }

    public Date getBirthday() {
        return birthday;
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

    public void addEvent(EventInfo event){
        //this.athleteInfo.a
        this.events.add(event.getEventIdentifier());
    }

    public boolean inSociety(String societyName){
        Optional<Society> society = societies.stream().filter(s ->s.getName().equals(societyName)).findAny();
        return society.isPresent();
    }

    public void addSociety(Society soc){
        societies.add(soc);
    }

    public Set<UUID> getEvents() {
        return events;
    }

    public List<Society> getSocieties() {
        return societies;
    }

    public void setAthleteInfo(AthleteInfo athleteInfo) {
        this.athleteInfo = athleteInfo;
    }

    public AthleteInfo getAthleteInfo() {
        return athleteInfo;
    }

    public Athlete() {
    }
}
