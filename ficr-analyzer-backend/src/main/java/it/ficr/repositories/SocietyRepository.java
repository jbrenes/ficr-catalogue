package it.ficr.repositories;

import it.ficr.elements.Society;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocietyRepository extends JpaRepository<Society, Long> {


    Optional<Society> findByName(String name);
}
