package ru.shukyurov.SpringBootRestAPI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.shukyurov.SpringBootRestAPI.dto.SensorDTO;
import ru.shukyurov.SpringBootRestAPI.exceptions.SensorNotRegisteredException;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.services.SensorsService;
import ru.shukyurov.SpringBootRestAPI.util.SensorsConverter;
import ru.shukyurov.SpringBootRestAPI.util.SensorsValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensors")
public class SensorsController {

    private final SensorsService sensorsService;
    private final SensorsValidator sensorsValidator;
    private final SensorsConverter sensorsConverter;

    @Autowired
    public SensorsController(SensorsService sensorsService,
                             SensorsValidator sensorsValidator,
                             SensorsConverter sensorsConverter) {
        this.sensorsService = sensorsService;
        this.sensorsValidator = sensorsValidator;
        this.sensorsConverter = sensorsConverter;
    }

    @GetMapping
    public ResponseEntity<List<SensorDTO>> index() {
        List<SensorDTO> sensors = sensorsService.findAll().stream()
                .map(sensor -> sensorsConverter.convertToSensorDTO(sensor))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(sensors);
    }

    @PostMapping("/registration")
    public ResponseEntity<SensorDTO> register(@RequestBody @Valid SensorDTO sensorDTO,
            BindingResult bindingResult) {

        sensorsValidator.validate(sensorsConverter.convertToSensor(sensorDTO), bindingResult);

        if (bindingResult.hasErrors()) {

            StringBuilder errorsMsg = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            fieldErrors.stream().forEach(err -> errorsMsg
                    .append(err.getField())
                    .append(" - ")
                    .append(err.getDefaultMessage())
                    .append(";"));

            throw new SensorNotRegisteredException(errorsMsg.toString());
        }

        sensorsService.save(sensorsConverter.convertToSensor(sensorDTO));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sensorDTO);
    }
}
