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
import ru.shukyurov.SpringBootRestAPI.dto.SensorDTO;
import ru.shukyurov.SpringBootRestAPI.exceptions.SensorNotRegisteredException;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.repositories.SensorsRepository;
import ru.shukyurov.SpringBootRestAPI.services.SensorsService;
import ru.shukyurov.SpringBootRestAPI.util.SensorsConverter;
import ru.shukyurov.SpringBootRestAPI.util.SensorsValidator;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("dev")
@WebMvcTest(SensorsController.class)
public class SensorsControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SensorsValidator sensorsValidator;

    @MockBean
    private SensorsConverter sensorsConverter;

    @MockBean
    private SensorsService sensorsService;

    @MockBean
    private SensorsRepository sensorsRepository;

    @Test
    @DisplayName("Test registration with correct sensor " +
            "then sensor should be added " +
            "and no exceptions occurs")
    public void givenCorrectSensor_whenRegistration_thenStatusIsOk_andRightSensorReturned() throws Exception {
        Sensor sensor = new Sensor("sensor");

        Mockito.when(sensorsService.save(Mockito.any())).thenReturn(sensor);
        Mockito.when(sensorsConverter.convertToSensor(Mockito.any())).thenReturn(sensor);
        Mockito.when(sensorsConverter.convertToSensorDTO(Mockito.any())).thenReturn(new SensorDTO("sensor"));

        mockMvc.perform(
                post("/sensors/registration")
                        .content(objectMapper.writeValueAsString(sensor))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("sensor"));
    }
    @Test
    @DisplayName("Test registration with incorrect sensor " +
            "then SensorNotRegisteredException must be thrown " +
            "and status is BadRequest")
    public void givenEmptySensor_whenRegistration_thenStatusIsBadRequest_andExceptionThrown() throws Exception {
        Sensor sensor = new Sensor("");

        Mockito.when(sensorsConverter.convertToSensor(Mockito.any())).thenReturn(sensor);

        mockMvc.perform(
                        post("/sensors/registration")
                                .content(objectMapper.writeValueAsString(sensor))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(SensorNotRegisteredException.class));

        Mockito.verify(sensorsConverter, Mockito.never()).convertToSensorDTO(Mockito.any());
        Mockito.verify(sensorsConverter, Mockito.times(1)).convertToSensor(Mockito.any());
    }

    @Test
    @DisplayName("Test to get all sensors from repository " +
            "then status must be OK and correct values returned")
    public void givenSensors_whenGetAllSensors_thenStatusIsOk_andCorrectSensorsReturned() throws Exception {
        Sensor sensor1 = new Sensor("sensor1");
        Sensor sensor2 = new Sensor("sensor2");

        Mockito.when(sensorsService.findAll()).thenReturn(Arrays.asList(sensor1,sensor2));
        Mockito.when(sensorsConverter.convertToSensorDTO(sensor1)).thenReturn(new SensorDTO("sensor1"));
        Mockito.when(sensorsConverter.convertToSensorDTO(sensor2)).thenReturn(new SensorDTO("sensor2"));

        mockMvc.perform(
                get("/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("sensor1"))
                .andExpect(jsonPath("$[1].name").value("sensor2"));

        Mockito.verify(sensorsConverter, Mockito.times(2)).convertToSensorDTO(Mockito.any());
    }
}
