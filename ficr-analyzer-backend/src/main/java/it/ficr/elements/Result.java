package it.ficr.elements;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Result {
    @Id
    @GeneratedValue

    private Long id;

    @ManyToMany
    @JsonBackReference

    private List<Athlete> crew = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    private Event event;

    @ManyToOne
    @JsonBackReference
    private Society society;

    private String result;

    private String competitionName;

    private String competitionCode;

    private String categoryCode;

    private String categoryName;

    public String getResult() {
        return result;
    }

    public Result(List<Athlete> crew, String result, Event event, String categoryCode, String categoryName, String competitionCode, String competitionName, Society society) {
        if(crew!=null) this.crew = crew;
        this.result = result;
        this.event=event;
        this.competitionCode=competitionCode;
        this.competitionName=competitionName;
        this.categoryCode=categoryCode;
        this.categoryName=categoryName;
        this.society=society;
    }

    public Event getEvent() {
        return event;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getCompetitionCode() {
        return competitionCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Result() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Athlete> getCrew() {
        return crew;
    }

    public void setCrew(List<Athlete> crew) {
        this.crew = crew;
    }

    public void addCrew(Athlete athlete){
        crew.add(athlete);
    }

    @JsonProperty
    public List<String> crewNames(){
        return crew.stream().map(a -> a.getName()+ " "+a.getSurname()).collect(Collectors.toList());
    }

    @JsonProperty
    public String eventName(){
        if(event!=null){
            return event.getName()+" "+event.getDate();
        }else return "";

    }
}

