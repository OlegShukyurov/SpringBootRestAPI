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
import ru.shukyurov.SpringBootRestAPI.exceptions.SensorNotRegisteredException;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.repositories.SensorsRepository;
import ru.shukyurov.SpringBootRestAPI.services.SensorsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class SensorsControllerIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SensorsRepository sensorsRepository;

    @Autowired
    private SensorsService sensorsService;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetDB() {
        sensorsRepository.deleteAll();
    }

    @Test
    @DisplayName("Test registration correct sensor" +
            " then sensor should be added " +
            "and no exceptions occurs")
    public void givenCorrectSensor_whenRegistration_thenStatusIsOk_andRightSensorReturned() throws Exception{
        Sensor sensor = new Sensor("sensor");

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
        "then SensorNotRegisteredException must be thrown and " +
            "status is BadRequest")
    public void givenIncorrectSensor_whenRegistration_thenStatusIsBadRequest_andExceptionThrown() throws Exception {
        Sensor sensor = new Sensor("");

        mockMvc.perform(
                post("/sensors/registration")
                        .content(objectMapper.writeValueAsString(sensor))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(SensorNotRegisteredException.class));
    }

    @Test
    @DisplayName("Test registration when add existed sensor " +
        "then SensorNotRegisteredException must be thrown and " +
        "status is BadRequest")
    public void givenExistingSensor_whenRegistration_thenStatusIsBadRequest_andExceptionThrown() throws Exception {
        Sensor existingSensor = createTestSensor("sensor");

        mockMvc.perform(
                post("/sensors/registration")
                        .content(objectMapper.writeValueAsString(existingSensor))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(SensorNotRegisteredException.class));
    }
    @Test
    @DisplayName("Test to get all sensors from repository " +
            "then status is OK and correct array returned")
    public void givenSensors_whenGetSensors_thenStatusIsOk_andCorrectSensorsReturned() throws Exception {
        Sensor s1 = createTestSensor("test1");
        Sensor s2 = createTestSensor("test2");

        mockMvc.perform(get("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[1].name").value("test2"));
    }

    private Sensor createTestSensor(String name) {
        Sensor sensor = new Sensor(name);
        sensorsService.save(sensor);
        return sensor;
    }
}
