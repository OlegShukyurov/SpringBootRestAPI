package ru.shukyurov.SpringBootRestAPI.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.shukyurov.SpringBootRestAPI.models.Measurement;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("dev")
@SpringBootTest
public class MeasurementsRepositoryIntegrationTests {

    @Autowired
    private MeasurementsRepository measurementsRepository;

    @Autowired
    private SensorsRepository sensorsRepository;

    @AfterEach
    public void resetDB() {
        measurementsRepository.deleteAll();
        sensorsRepository.deleteAll();
    }

    @Test
    public void whenFindMeasurementByValidId_thenReturnMeasurement() {
        Sensor sensor = new Sensor("sensor");
        Measurement measurement = new Measurement(24.5, true);
        measurement.setSensor(sensor);

        sensorsRepository.save(sensor);
        sensorsRepository.flush();
        measurementsRepository.save(measurement);
        measurementsRepository.flush();

        Optional<Measurement> found = measurementsRepository.findById(measurement.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getSensor().getName()).isEqualTo("sensor");
        assertThat(found.get().getValue()).isEqualTo(24.5);
        assertThat(found.get().isRaining()).isTrue();

    }
    @Test
    public void whenFindMeasurementByInvalidId_thenReturnEmpty() {
        Optional<Measurement> found = measurementsRepository.findById(-11);

        assertThat(found).isNotPresent();
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

        sensorsRepository.save(sensor);
        sensorsRepository.flush();
        measurementsRepository.save(measurement1);
        measurementsRepository.save(measurement2);
        measurementsRepository.save(measurement3);
        measurementsRepository.flush();

        List<Measurement> measurements = measurementsRepository.findAll();

        assertThat(measurements).isNotEmpty().hasSize(3);

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
        Measurement measurement1 = new Measurement(24.5, true);
        Measurement measurement2 = new Measurement(15, true);
        Measurement measurement3 = new Measurement(10, false);

        Sensor sensor = new Sensor("test");

        measurement1.setSensor(sensor);
        measurement2.setSensor(sensor);
        measurement3.setSensor(sensor);

        sensorsRepository.save(sensor);
        sensorsRepository.flush();
        measurementsRepository.save(measurement1);
        measurementsRepository.save(measurement2);
        measurementsRepository.save(measurement3);
        measurementsRepository.flush();

        Long rainyDaysCounter = measurementsRepository.countByRainingIsTrue();

        assertThat(rainyDaysCounter)
                .isNotNegative()
                .isEqualTo(2);
    }
}
