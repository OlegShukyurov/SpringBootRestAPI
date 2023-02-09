package ru.shukyurov.SpringBootRestAPI.exceptions;

public class SensorNotRegisteredException extends RuntimeException {
    public SensorNotRegisteredException(String message) {
        super(message);
    }
}
