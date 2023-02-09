package ru.shukyurov.SpringBootRestAPI.controllers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shukyurov.SpringBootRestAPI.exceptions.MeasurementNotAddedException;
import ru.shukyurov.SpringBootRestAPI.util.ErrorDetails;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionMeasurementControllerAdvice {

    @ExceptionHandler(MeasurementNotAddedException.class)
    public ResponseEntity<ErrorDetails> exceptionMeasurementNotAddedException(MeasurementNotAddedException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage(e.getMessage());
        errorDetails.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
