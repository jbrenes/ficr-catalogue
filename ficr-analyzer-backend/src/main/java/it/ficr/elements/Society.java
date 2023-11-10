package it.ficr.elements;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.List;
import java.util.UUID;

@Entity
public class Society {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private UUID societyIdentifier;

    @ManyToMany
    private List<Athlete> athletes;

    public Society(String name, List<Athlete> athletes) {
        this.name = name;
        this.societyIdentifier = UUID.fromString(name);
        this.athletes = athletes;
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



    public List<Athlete> getAthletes() {
        return athletes;
    }

    public void setAthletes(List<Athlete> athletes) {
        this.athletes = athletes;
    }
}
