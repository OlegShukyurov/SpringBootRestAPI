package ru.shukyurov.SpringBootRestAPI.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
public class SensorsRepositoryIntegrationTests {

    @Autowired
    private SensorsRepository sensorsRepository;

    @AfterEach
    public void resetDB() {
        sensorsRepository.deleteAll();
    }

    @Test
    public void whenFindByCorrectName_thenReturnSensor() {
        Sensor sensor = new Sensor("sensor");
        sensorsRepository.save(sensor);
        sensorsRepository.flush();

        Optional<Sensor> found = sensorsRepository.findByName("sensor");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("sensor");

        sensorsRepository.deleteAll();
    }

    @Test
    public void whenFindByInvalidName_thenReturnEmpty() {
        Optional<Sensor> found = sensorsRepository.findByName("invalid");
        sensorsRepository.flush();

        assertThat(found).isNotPresent();
    }

    @Test
    public void whenFindById_thenReturnSensor() {
        Sensor sensor = new Sensor("sensor");
        sensorsRepository.save(sensor);
        sensorsRepository.flush();

        Optional<Sensor> found = sensorsRepository.findById(sensor.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(sensor.getId());
        assertThat(found.get().getName()).isEqualTo(sensor.getName());
    }

    @Test
    public void whenFindByInvalidId_thenReturnEmpty() {

        Optional<Sensor> found = sensorsRepository.findById(-15);

        assertThat(found).isNotPresent();
    }

    @Test
    public void givenSetOfSensors_whenFindAll_mustReturnAllSensors() {
        Sensor sensor1 = new Sensor("sensor1");
        Sensor sensor2 = new Sensor("sensor2");
        Sensor sensor3 = new Sensor("sensor3");

        sensorsRepository.save(sensor1);
        sensorsRepository.save(sensor2);
        sensorsRepository.save(sensor3);
        sensorsRepository.flush();

        List<Sensor> sensors = sensorsRepository.findAll();

        assertThat(sensors).isNotEmpty()
                .hasSize(3)
                .extracting(sensor -> sensor.getName())
                .containsOnly(sensor1.getName(), sensor2.getName(), sensor3.getName());
    }
}
