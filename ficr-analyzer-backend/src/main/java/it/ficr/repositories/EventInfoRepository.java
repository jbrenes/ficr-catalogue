package it.ficr.repositories;

import it.ficr.elements.Event;
import it.ficr.elements.EventInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface EventInfoRepository extends JpaRepository<EventInfo, UUID > {


    Optional<EventInfo> findByEventIdentifier(UUID eventIdentifier);
    Optional<EventInfo> findByUrl(String  url);

    Optional<EventInfo> findByNameAndPlaceAndDate(String name, String place, Date date);
}
