package ru.shukyurov.SpringBootRestAPI.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.shukyurov.SpringBootRestAPI.models.Measurement;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.repositories.MeasurementsRepository;
import ru.shukyurov.SpringBootRestAPI.repositories.SensorsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("dev")
@SpringBootTest
public class MeasurementsServiceIntegrationTests {

    @Autowired
    private MeasurementsService measurementsService;

    @MockBean
    private SensorsRepository sensorsRepository;

    @MockBean
    private MeasurementsRepository measurementsRepository;

    @BeforeEach
    public void setUp() {
        Sensor sensor = new Sensor("test");

        Measurement measurement1 = new Measurement(24.5, true);
        Measurement measurement2 = new Measurement(15, true);
        Measurement measurement3 = new Measurement(10, false);

        measurement1.setId(10);

        measurement1.setSensor(sensor);
        measurement2.setSensor(sensor);
        measurement3.setSensor(sensor);

        List<Measurement> measurements = Arrays.asList(measurement1, measurement2, measurement3);

        Mockito.when(measurementsRepository.findById(measurement1.getId())).thenReturn(Optional.of(measurement1));
        Mockito.when(measurementsRepository.findById(99)).thenReturn(Optional.empty());
        Mockito.when(measurementsRepository.findAll()).thenReturn(measurements);
        Mockito.when(measurementsRepository.countByRainingIsTrue()).thenReturn(2L);
    }

    @Test
    public void whenValidId_thenMeasurementShouldBeFound() {
        Measurement measurement = new Measurement(24.5, true);
        measurement.setId(10);

        Optional<Measurement> found = measurementsRepository.findById(10);

        assertThat(found).isPresent();
        assertThat(found.get().getValue()).isEqualTo(measurement.getValue());
        assertThat(found.get().isRaining()).isTrue();
        assertThat(found.get().getId()).isEqualTo(10);

        Mockito.verify(measurementsRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.reset(measurementsRepository);
    }

    @Test
    public void whenInvalidId_thenMeasurementShouldNotBeFound() {
        Measurement measurement = new Measurement();
        measurement.setId(99);

        Optional<Measurement> found = measurementsRepository.findById(measurement.getId());

        assertThat(found).isNotPresent();

        Mockito.verify(measurementsRepository, Mockito.times(1)).findById(measurement.getId());
        Mockito.reset(measurementsRepository);
    }

    @Test
    public void given3Measurements_whenGetAllMeasurements_thenShouldReturn3Measurements() {
        Measurement measurement1 = new Measurement(24.5, true);
        Measurement measurement2 = new Measurement(15, true);
        Measurement measurement3 = new Measurement(10, false);

        Sensor sensor = new Sensor("test");

        measurement1.setSensor(sensor);
        measurement2.setSensor(sensor);
        measurement3.setSensor(sensor);

        List<Measurement> measurements = measurementsService.findAll();

        Mockito.verify(measurementsRepository, Mockito.times(1)).findAll();
        Mockito.reset(measurementsRepository);

        assertThat(measurements).isNotEmpty()
                .hasSize(3);
        assertAll("measurements",
                () -> assertEquals(measurements.get(0).getValue(), measurement1.getValue()),
                () -> assertEquals(measurements.get(0).isRaining(), measurement1.isRaining()),
                () -> assertEquals(measurements.get(1).getValue(), measurement2.getValue()),
                () -> assertEquals(measurements.get(1).isRaining(), measurement2.isRaining()),
                () -> assertEquals(measurements.get(2).getValue(), measurement3.getValue()),
                () -> assertEquals(measurements.get(2).isRaining(), measurement3.isRaining())
        );
    }

    @Test
    public void given2RainyDays_whenGetCounterOfRainyDays_mustReturn2() {
        Long rainyDaysCounter = measurementsRepository.countByRainingIsTrue();

        assertThat(rainyDaysCounter)
                .isNotNegative()
                .isEqualTo(2);
    }
}
