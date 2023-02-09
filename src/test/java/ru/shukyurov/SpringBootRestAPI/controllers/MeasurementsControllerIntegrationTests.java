package ru.shukyurov.SpringBootRestAPI.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.shukyurov.SpringBootRestAPI.exceptions.MeasurementNotAddedException;
import ru.shukyurov.SpringBootRestAPI.exceptions.SensorNotFoundException;
import ru.shukyurov.SpringBootRestAPI.models.Measurement;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.repositories.MeasurementsRepository;
import ru.shukyurov.SpringBootRestAPI.repositories.SensorsRepository;
import ru.shukyurov.SpringBootRestAPI.services.MeasurementsService;
import ru.shukyurov.SpringBootRestAPI.services.SensorsService;
import ru.shukyurov.SpringBootRestAPI.util.MeasurementsConverter;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class MeasurementsControllerIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MeasurementsService measurementsService;

    @Autowired
    private MeasurementsRepository measurementsRepository;

    @Autowired
    private SensorsService sensorsService;

    @Autowired
    private MeasurementsConverter measurementsConverter;

    @Autowired
    private SensorsRepository sensorsRepository;

    @Autowired
    private MockMvc mockMvc;


    @AfterEach
    private void resetDB() {
        measurementsRepository.deleteAll();
        sensorsRepository.deleteAll();
    }

    @Test
    @DisplayName("Test to add correct measurement with " +
            "existed sensor then status " +
            "must be OK and correct measurement returned")
    public void givenMeasurementAndSensor_whenAddMeasurement_thenStatusIsOk_andMeasurementReturned() throws Exception {
        Measurement measurement = new Measurement(24.7, true);
        Sensor sensor = createTestSensor("sensor");
        measurement.setSensor(sensor);

        mockMvc.perform(
                post("/measurements/add")
                        .content(objectMapper.writeValueAsString(measurement))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensor.name").value(sensor.getName()))
                .andExpect(jsonPath("$.value").value(measurement.getValue()))
                .andExpect(jsonPath("$.raining").value("true"));
    }

    @Test
    @DisplayName("Test to add correct measurement with " +
            "non existed sensor then status must be " +
            "NotFound and SensorNotFoundException thrown")
    public void givenMeasurementAndNonExistedSensor_whenAddMeasurement_thenStatusIsNotFound_andExceptionThrown() throws Exception {
        Measurement measurement = new Measurement(24.7, true);
        measurement.setSensor(new Sensor("nonExisted"));

        mockMvc.perform(
                post("/measurements/add")
                        .content(objectMapper.writeValueAsString(measurement))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(SensorNotFoundException.class));
    }

    @Test
    @DisplayName("Test to add incorrect measurement with " +
            "existed sensor then status must be BadRequest " +
            "and MeasurementNotAddedException thrown")
    public void givenIncorrectMeasurementAndExistedSensor_whenAddMeasurement_thenStatusIsBadRequest_andExThrown() throws Exception {
        Measurement measurement = new Measurement(9999999, true);
        Sensor sensor = createTestSensor("sensor");
        measurement.setSensor(sensor);

        mockMvc.perform(
                post("/measurements/add")
                        .content(objectMapper.writeValueAsString(measurement))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(MeasurementNotAddedException.class));
    }

    @Test
    @DisplayName("Test to get rainy days counter " +
            "then status must be OK and correct value returned")
    public void givenRainyDays_whenGetRainyDaysCounter_thenStatusIsOk_andCounterReturned() throws Exception {
        createTestMeasurements();

        mockMvc.perform(
                get("/measurements/rainyDaysCount"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(1)));
    }

    @Test
    @DisplayName("Test to get all measurements " +
            "then status must be OK and correct values returned")
    public void givenMeasurements_whenGetMeasurements_thenStatusIsOk_andMeasurementsReturned() throws Exception {
        createTestMeasurements();

        mockMvc.perform(
                get("/measurements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(24.7))
                .andExpect(jsonPath("$[0].raining").value("true"))
                .andExpect(jsonPath("$[1].value").value(12.3))
                .andExpect(jsonPath("$[1].raining").value("false"));
    }

    private Sensor createTestSensor(String name) {
        Sensor sensor = new Sensor(name);
        return sensorsService.save(sensor);
    }

    private List<Measurement> createTestMeasurements() {
        Sensor sensor = createTestSensor("sensor");

        Measurement measurement1 = new Measurement(24.7, true);
        Measurement measurement2 = new Measurement(12.3, false);

        measurement1.setSensor(sensor);
        measurement2.setSensor(sensor);

        measurementsService.save(measurement1);
        measurementsService.save(measurement2);

        return Arrays.asList(measurement1, measurement2);
    }
}
