package ru.shukyurov.SpringBootRestAPI.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.shukyurov.SpringBootRestAPI.dto.MeasurementDTO;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@WebMvcTest(MeasurementsController.class)
public class MeasurementsControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeasurementsService measurementsService;

    @MockBean
    private SensorsService sensorsService;

    @MockBean
    private MeasurementsConverter measurementsConverter;

    @MockBean
    private MeasurementsRepository measurementsRepository;

    @MockBean
    private SensorsRepository sensorsRepository;

    @Test
    @DisplayName("Test to add correct measurement with existed sensor " +
            "then status is OK and correct measurement returned")
    public void givenCorrectMeasurementAndExistedSensor_whenAddMeasurement_thenStatusIsOk_andMeasurementReturned() throws Exception {
        Sensor sensor = new Sensor("sensor");
        Measurement measurement = new Measurement(24.7, true);
        measurement.setSensor(sensor);

        Mockito.when(sensorsService.findByName("sensor")).thenReturn(Optional.of(sensor));
        Mockito.when(measurementsService.save(Mockito.any())).thenReturn(measurement);
        Mockito.when(measurementsConverter.convertToMeasurement(Mockito.any())).thenReturn(measurement);

        mockMvc.perform(
                post("/measurements/add")
                        .content(objectMapper.writeValueAsString(measurement))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensor.name").value("sensor"))
                .andExpect(jsonPath("$.value").value(24.7))
                .andExpect(jsonPath("$.raining").value("true"));
    }

    @Test
    @DisplayName("Test to add correct measurement with " +
            "non existed sensor then status must be " +
            "NotFound and SensorNotFoundException thrown")
    public void givenMeasurementAndNonExistedSensor_whenAddMeasurement_thenStatusIsNotFound_andExceptionThrown() throws Exception {
        Measurement measurement = new Measurement(24.7, true);
        measurement.setSensor(new Sensor("nonExisted"));

        Mockito.when(sensorsService.findByName("nonExisted")).thenReturn(Optional.empty());

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
        Sensor sensor = new Sensor("existed");
        measurement.setSensor(sensor);

        Mockito.when(sensorsService.findByName("existed")).thenReturn(Optional.of(sensor));

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

        Mockito.when(measurementsService.countByRainingIsTrue()).thenReturn(1L);

        mockMvc.perform(
                        get("/measurements/rainyDaysCount"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(1)));
    }
    @Test
    @DisplayName("Test to get all measurements " +
            "then status must be OK and correct values returned")
    public void givenMeasurements_whenGetMeasurements_thenStatusIsOk_andMeasurementsReturned() throws Exception {
        Measurement measurement1 = new Measurement(24.7, true);
        Measurement measurement2 = new Measurement(12.3, false);

        Sensor sensor = new Sensor("sensor");

        MeasurementDTO measurementDTO1 = createMeasurementDTO(measurement1, sensor);
        MeasurementDTO measurementDTO2 = createMeasurementDTO(measurement2, sensor);

        Mockito.when(measurementsService.findAll()).thenReturn(Arrays.asList(measurement1, measurement2));
        Mockito.when(measurementsConverter.convertToMeasurementDTO(measurement1)).thenReturn(measurementDTO1);
        Mockito.when(measurementsConverter.convertToMeasurementDTO(measurement2)).thenReturn(measurementDTO2);

        mockMvc.perform(
                        get("/measurements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(24.7))
                .andExpect(jsonPath("$[0].raining").value("true"))
                .andExpect(jsonPath("$[1].value").value(12.3))
                .andExpect(jsonPath("$[1].raining").value("false"));
    }

    private MeasurementDTO createMeasurementDTO(Measurement measurement, Sensor sensor) {
        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurement.setSensor(sensor);
        measurementDTO.setRaining(String.valueOf(measurement.isRaining()));
        measurementDTO.setValue(String.valueOf(measurement.getValue()));
        return measurementDTO;
    }
}
