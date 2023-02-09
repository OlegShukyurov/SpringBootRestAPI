package ru.shukyurov.SpringBootRestAPI.dto;

import org.springframework.stereotype.Component;

import javax.validation.constraints.*;

@Component
public class MeasurementDTO {

    @Min(value = -100, message = "Temperature should be greater than '-100'")
    @Max(value = 100, message = "Temperature should be smaller than 100")
    @NotNull(message = "Temperature should not be null")
    @NotEmpty(message = "Temperature should not be empty")
    @Pattern(regexp = "^[-+]?[0-9]*[.]?[0-9]+$", message = "Temperature must be number")
    private String value;

    @NotNull(message = "Raining should not be null")
    @NotEmpty(message = "Raining should not be empty")
    @Pattern(regexp = "(^true$)|(^false$)", message = "Raining must be 'true' or 'false'")
    private String raining;

    private SensorDTO sensor;

    public MeasurementDTO(String value, String raining, SensorDTO sensor) {
        this.value = value;
        this.raining = raining;
        this.sensor = sensor;
    }

    public MeasurementDTO() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRaining() {
        return raining;
    }

    public void setRaining(String raining) {
        this.raining = raining;
    }

    public SensorDTO getSensor() {
        return sensor;
    }

    public void setSensor(SensorDTO sensor) {
        this.sensor = sensor;
    }
}
