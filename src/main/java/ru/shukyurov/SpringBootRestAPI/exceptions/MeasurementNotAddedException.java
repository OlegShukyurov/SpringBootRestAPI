package ru.shukyurov.SpringBootRestAPI.exceptions;

public class MeasurementNotAddedException extends RuntimeException {
    public MeasurementNotAddedException(String message) {
        super(message);
    }
}
