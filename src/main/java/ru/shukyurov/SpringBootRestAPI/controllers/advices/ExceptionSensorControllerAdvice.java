package ru.shukyurov.SpringBootRestAPI.controllers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shukyurov.SpringBootRestAPI.exceptions.SensorNotFoundException;
import ru.shukyurov.SpringBootRestAPI.exceptions.SensorNotRegisteredException;
import ru.shukyurov.SpringBootRestAPI.util.ErrorDetails;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionSensorControllerAdvice {

    @ExceptionHandler(SensorNotRegisteredException.class)
    public ResponseEntity<ErrorDetails> exceptionSensorNotRegisteredHandler(SensorNotRegisteredException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage(e.getMessage());
        errorDetails.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SensorNotFoundException.class)
    public ResponseEntity<ErrorDetails> exceptionSensorNotFoundHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Sensor with this name was not found");
        errorDetails.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
