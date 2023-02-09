package ru.shukyurov.SpringBootRestAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shukyurov.SpringBootRestAPI.models.Measurement;


@Repository
public interface MeasurementsRepository extends JpaRepository<Measurement, Integer> {

    long countByRainingIsTrue();
}
