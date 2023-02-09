package ru.shukyurov.SpringBootRestAPI.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shukyurov.SpringBootRestAPI.dto.MeasurementDTO;
import ru.shukyurov.SpringBootRestAPI.models.Measurement;

@Component
public class MeasurementsConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public MeasurementsConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        return modelMapper.map(measurementDTO, Measurement.class);
    }

    public MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }
}
