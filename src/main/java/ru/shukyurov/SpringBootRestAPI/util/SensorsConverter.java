package ru.shukyurov.SpringBootRestAPI.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shukyurov.SpringBootRestAPI.dto.SensorDTO;
import ru.shukyurov.SpringBootRestAPI.models.Sensor;

@Component
public class SensorsConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public SensorsConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Sensor convertToSensor(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
    }

    public SensorDTO convertToSensorDTO(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }
}
