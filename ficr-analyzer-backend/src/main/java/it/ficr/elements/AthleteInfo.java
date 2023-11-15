package it.ficr.elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
public class AthleteInfo {

    @Id
    @GeneratedValue
    private UUID athleteIdentifier;
    private String name;
    private String surname;





    public AthleteInfo() {
    }

    @JsonCreator
    public AthleteInfo(
                       @JsonProperty("name") String name,
                       @JsonProperty("surname")String surname) {

        this.name = name;
        this.surname = surname;

    }


    public UUID getAthleteIdentifier() {
        return athleteIdentifier;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }



}
