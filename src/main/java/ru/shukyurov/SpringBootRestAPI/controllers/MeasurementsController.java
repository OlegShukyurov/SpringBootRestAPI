package ru.shukyurov.SpringBootRestAPI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.shukyurov.SpringBootRestAPI.dto.MeasurementDTO;
import ru.shukyurov.SpringBootRestAPI.dto.SensorDTO;
import ru.shukyurov.SpringBootRestAPI.exceptions.MeasurementNotAddedException;
import ru.shukyurov.SpringBootRestAPI.exceptions.SensorNotFoundException;
import ru.shukyurov.SpringBootRestAPI.services.MeasurementsService;
import ru.shukyurov.SpringBootRestAPI.services.SensorsService;
import ru.shukyurov.SpringBootRestAPI.util.MeasurementsConverter;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/measurements")
public class MeasurementsController {

    private final MeasurementsConverter measurementsConverter;
    private final MeasurementsService measurementsService;
    private final SensorsService sensorsService;

    @Autowired
    public MeasurementsController(MeasurementsConverter measurementsConverter,
                                  MeasurementsService measurementsService,
                                  SensorsService sensorsService) {
        this.measurementsConverter = measurementsConverter;
        this.measurementsService = measurementsService;
        this.sensorsService = sensorsService;
    }


    @PostMapping("/add")
    public ResponseEntity<MeasurementDTO> add(@RequestBody @Valid MeasurementDTO measurementDTO,
                                          BindingResult bindingResult) {

        if (sensorsService.findByName(measurementDTO.getSensor().getName()).isEmpty())
            throw new SensorNotFoundException();

        if (bindingResult.hasErrors()) {
            StringBuilder errorsMsg = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            fieldErrors.stream().forEach(err -> errorsMsg
                    .append(err.getField())
                    .append(" - ")
                    .append(err.getDefaultMessage())
                    .append(";"));

            throw new MeasurementNotAddedException(errorsMsg.toString());
        }

        measurementsService.save(measurementsConverter.convertToMeasurement(measurementDTO));

        return new ResponseEntity<>(measurementDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MeasurementDTO>> index() {
        List<MeasurementDTO> measurements = measurementsService.findAll().stream()
                .map(measurement -> measurementsConverter.convertToMeasurementDTO(measurement))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(measurements);
    }

    @GetMapping("/rainyDaysCount")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(measurementsService.countByRainingIsTrue(), HttpStatus.OK);
    }
}
