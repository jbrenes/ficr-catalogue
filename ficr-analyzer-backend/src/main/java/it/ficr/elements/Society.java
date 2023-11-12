package it.ficr.elements;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity

public class Society {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String name;
    private String societyCode;

    private UUID societyIdentifier;

    @OneToMany(mappedBy="society", fetch = FetchType.EAGER)
    private List<Result> results = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Athlete> athletes = new ArrayList<>();

    public Society() {
    }



    @JsonCreator
    public Society(@JsonProperty("name") String name,@JsonProperty("societyCode") String societyCode, @JsonProperty("athletes") List<Athlete> athletes) {
        this.name = name;
        this.societyCode=societyCode;
        this.societyIdentifier = UUID.nameUUIDFromBytes(societyCode.getBytes());

        if(athletes!=null) this.athletes = athletes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getSocietyIdentifier() {
        return societyIdentifier;
    }


    public void addResult(Result r){
        results.add(r);
    }

    public List<Athlete> getAthletes() {
        return athletes;
    }

    public String getSocietyCode() {
        return societyCode;
    }

    @JsonProperty
    public List<String> societyAthletes(){
        return athletes.stream().map(a -> a.getName()+ " "+a.getSurname()).collect(Collectors.toList());
    }
    public void addAthlete(Athlete athlete) {
        this.athletes.add(athlete);
    }
}
