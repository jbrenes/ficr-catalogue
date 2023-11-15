package it.ficr.repositories;

import it.ficr.elements.AthleteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface AthleteInfoRepository extends JpaRepository<AthleteInfo, UUID>, JpaSpecificationExecutor<AthleteInfo> {



    Optional<AthleteInfo> findByAthleteIdentifier(UUID identifier);
}
