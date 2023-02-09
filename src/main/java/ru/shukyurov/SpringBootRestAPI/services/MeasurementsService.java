package ru.shukyurov.SpringBootRestAPI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shukyurov.SpringBootRestAPI.models.Measurement;
import ru.shukyurov.SpringBootRestAPI.repositories.MeasurementsRepository;
import ru.shukyurov.SpringBootRestAPI.repositories.SensorsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasurementsService {

    private final MeasurementsRepository measurementsRepository;
    private final SensorsRepository sensorsRepository;

    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository, SensorsRepository sensorsRepository) {
        this.measurementsRepository = measurementsRepository;
        this.sensorsRepository = sensorsRepository;
    }

    @Transactional
    public Measurement save(Measurement measurement) {
        enrichMeasurement(measurement);
        measurement.setSensor(sensorsRepository.findByName(measurement.getSensor().getName()).get());
        measurement.getSensor().getMeasurements().add(measurement);
        return measurementsRepository.save(measurement);
    }

    public List<Measurement> findAll() {
        return measurementsRepository.findAll();
    }

    public void enrichMeasurement(Measurement measurement) {
        measurement.setMadeAt(LocalDateTime.now());
    }

    public Long countByRainingIsTrue() {
        return measurementsRepository.countByRainingIsTrue();
    }
}
