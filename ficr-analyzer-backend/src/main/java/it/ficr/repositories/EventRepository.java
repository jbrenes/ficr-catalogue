package it.ficr.repositories;

import it.ficr.elements.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long > {


    Optional<Event> findByEventIdentifier(UUID eventIdentifier);

}
