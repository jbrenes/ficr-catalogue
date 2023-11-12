package it.ficr.repositories;

import it.ficr.elements.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByResultUrl(String url);
}
