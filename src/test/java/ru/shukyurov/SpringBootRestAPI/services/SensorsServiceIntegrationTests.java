package ru.shukyurov.SpringBootRestAPI.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.repositories.SensorsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
public class SensorsServiceIntegrationTests {

    @Autowired
    private SensorsService sensorsService;

    @MockBean
    private SensorsRepository sensorsRepository;

    @BeforeEach
    public void setUp() {
        Sensor sensor1 = new Sensor("sensor1");
        sensor1.setId(10);

        Sensor sensor2 = new Sensor("sensor2");
        Sensor sensor3 = new Sensor("sensor3");

        List<Sensor> allSensors = Arrays.asList(sensor1, sensor2, sensor3);

        Mockito.when(sensorsRepository.findByName(sensor1.getName())).thenReturn(Optional.of(sensor1));
        Mockito.when(sensorsRepository.findById(sensor1.getId())).thenReturn(Optional.of(sensor1));
        Mockito.when(sensorsRepository.findByName(sensor2.getName())).thenReturn(Optional.of(sensor2));
        Mockito.when(sensorsRepository.findByName("invalid")).thenReturn(Optional.empty());
        Mockito.when(sensorsRepository.findById(99)).thenReturn(Optional.empty());
        Mockito.when(sensorsRepository.findAll()).thenReturn(allSensors);
    }

    @Test
    public void whenValidName_thenSensorShouldBeFound() {
        Sensor sensor = new Sensor("sensor1");
        Optional<Sensor> found = sensorsRepository.findByName("sensor1");
        Sensor sensor2 = new Sensor("sensor2");
        Optional<Sensor> found2 = sensorsRepository.findByName("sensor2");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(sensor.getName());
        assertThat(found2).isPresent();
        assertThat(found2.get().getName()).isEqualTo(sensor2.getName());
    }

    @Test
    public void whenInvalidName_thenSensorShouldNotBeFound() {
        Sensor sensor = new Sensor("invalid");

        Optional<Sensor> found = sensorsRepository.findByName("invalid");

        assertThat(found).isEmpty();
        Mockito.verify(sensorsRepository, Mockito.times(1)).findByName("invalid");
        Mockito.reset(sensorsRepository);
    }

    @Test
    public void whenValidId_thenSensorShouldBeFound() {
        Sensor sensor = new Sensor("sensor1");
        sensor.setId(10);

        Optional<Sensor> found = sensorsRepository.findById(sensor.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(10);
        assertThat(found.get().getName()).isEqualTo("sensor1");

        Mockito.verify(sensorsRepository, Mockito.times(1)).findById(10);
        Mockito.reset(sensorsRepository);
    }

    @Test
    public void whenInvalidId_thenSensorShouldNotBeFound() {
        Sensor sensor = new Sensor("sensor1");
        sensor.setId(99);

        Optional<Sensor> found = sensorsRepository.findById(sensor.getId());

        assertThat(found).isNotPresent();

        Mockito.verify(sensorsRepository, Mockito.times(1)).findById(sensor.getId());
        Mockito.reset(sensorsRepository);
    }

    @Test
    public void given3Sensors_whenGetAllSensors_thenReturnSensors() {
        Sensor sensor1 = new Sensor("sensor1");
        Sensor sensor2 = new Sensor("sensor2");
        Sensor sensor3 = new Sensor("sensor3");

        List<Sensor> all3Sensors = sensorsService.findAll();

        Mockito.verify(sensorsRepository, Mockito.times(1)).findAll();

        assertThat(all3Sensors)
                .isNotEmpty()
                .extracting(sensor -> sensor.getName())
                .containsOnly(sensor1.getName(), sensor2.getName(), sensor3.getName());
    }
}
