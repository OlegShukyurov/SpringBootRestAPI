package ru.shukyurov.SpringBootRestAPI.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;
import ru.shukyurov.SpringBootRestAPI.services.SensorsService;

@Component
public class SensorsValidator implements Validator {

    private final SensorsService sensorsService;

    @Autowired
    public SensorsValidator(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Sensor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Sensor sensor = (Sensor) target;

        if (!sensorsService.findByName(sensor.getName()).isEmpty()) {
            errors.rejectValue("name", "", "Sensor with this name already exists");
        }
    }
}
