package ru.shukyurov.SpringBootRestAPI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.repositories.SensorsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SensorsService {

    private final SensorsRepository sensorsRepository;

    @Autowired
    public SensorsService(SensorsRepository sensorsRepository) {
        this.sensorsRepository = sensorsRepository;
    }

    @Transactional
    public Sensor save(Sensor sensor) {
        enrichSensor(sensor);
        return sensorsRepository.save(sensor);
    }

    public List<Sensor> findAll() {
        return sensorsRepository.findAll();
    }

    public Optional<Sensor> findByName(String name) {
        return sensorsRepository.findByName(name);
    }

    public void enrichSensor(Sensor sensor) {
        sensor.setMeasurements(new ArrayList<>());
        sensor.setRegisteredAt(LocalDateTime.now());
        sensor.setUpdatedAt(LocalDateTime.now());
    }
}
