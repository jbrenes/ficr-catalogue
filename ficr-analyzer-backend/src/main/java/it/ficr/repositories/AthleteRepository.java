package it.ficr.repositories;

import it.ficr.elements.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    Optional<Athlete> findByNameAndSurname(String name, String surname);

    Optional<Athlete> findByAthleteIdentifier(UUID identifier);
}
