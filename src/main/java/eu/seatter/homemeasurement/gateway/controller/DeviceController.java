package eu.seatter.homemeasurement.gateway.controller;

import eu.seatter.homemeasurement.gateway.commands.DeviceCommand;
import eu.seatter.homemeasurement.gateway.commands.SensorCommand;
import eu.seatter.homemeasurement.gateway.converters.DeviceToDeviceCommand;
import eu.seatter.homemeasurement.gateway.converters.SensorToSensorCommand;
import eu.seatter.homemeasurement.gateway.exceptions.DeviceNotFoundException;
import eu.seatter.homemeasurement.gateway.model.Device;
import eu.seatter.homemeasurement.gateway.model.Sensor;
import eu.seatter.homemeasurement.gateway.service.DeviceService;
import eu.seatter.homemeasurement.gateway.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 10/01/2019
 * Time: 21:31
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/", produces = "application/json;charset=UTF-8")

public class DeviceController {

    private final DeviceService deviceService;

    private final SensorService sensorService;

    private final DeviceToDeviceCommand converterDeviceToDeviceCommand;

    private final SensorToSensorCommand converterSensorToSensorCommand;

    @Autowired
    public DeviceController(DeviceService deviceService, SensorService sensorService, DeviceToDeviceCommand converterDeviceToDeviceCommand, SensorToSensorCommand converterSensorToSensorCommand) {
        this.deviceService = deviceService;
        this.sensorService = sensorService;
        this.converterDeviceToDeviceCommand = converterDeviceToDeviceCommand;
        this.converterSensorToSensorCommand = converterSensorToSensorCommand;
    }

    /**
     * @return Return a JSON formatted List of DeviceCommand objects
     */
    @GetMapping(value = {"devices","devices/"})
    @ResponseStatus(HttpStatus.OK)
    public List<DeviceCommand> getAllDevices() {
        log.debug("Entered getAllSensors");
        Set<Device> foundSensors = deviceService.findAll();

        return foundSensors.stream()
                .sorted()
                .map(converterDeviceToDeviceCommand::convert)
                .collect(Collectors.toList());
    }

    /**
     * @param id The ID of the Device
     * @return A JSON formatted DeviceCommand object
     */
    @GetMapping(value = "device/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DeviceCommand getDeviceById(@PathVariable Long id) {
        log.debug("Entered getDeviceById, id=" + id);
        Device foundDevice =  deviceService.findById(id).orElseThrow(() ->
                new DeviceNotFoundException("Device with ID " + id + " not found",
                        "Verify the ID is correct and the device is registered with the system."));
        return converterDeviceToDeviceCommand.convert(foundDevice);
    }

    @GetMapping(value = {"device","device/"}, params = "name")
    @ResponseStatus(HttpStatus.OK)
    public DeviceCommand getDeviceByName(@RequestParam String name) {
        log.debug("Entered getDeviceByName, name=" + name);
        Device foundDevice = deviceService.findByName(name).orElseThrow(() ->
                new DeviceNotFoundException("Device with Name " + name + " not found",
                        "Verify the Name is correct and the device is registered with the system."));

        return converterDeviceToDeviceCommand.convert(foundDevice);
    }

    /**
     * @param uniqueid The uniqueID of a Device.
     * @return A JSON formatted DeviceCommand object.
     */
    @GetMapping(value = {"device","device/"}, params = "uniqueid")
    @ResponseStatus(HttpStatus.OK)
    public DeviceCommand getDeviceByUniqueId(@RequestParam String uniqueid) {
        log.debug("Entered getDeviceByUniqueId, uniqueid = " + uniqueid);
        Device foundDevice =  deviceService.findByUniqueId(uniqueid).orElseThrow(() ->
                new DeviceNotFoundException("Device with unique ID " + uniqueid + " not found",
                        "Verify the Unique ID is correct and the device is registered with the system."));

        return converterDeviceToDeviceCommand.convert(foundDevice);
    }

    /**
     * @param id The uniqueID of a Device.
     * @return Return a JSON formatted List of SensorCommand objects
     */
    @GetMapping(value={"device/{id}/sensors","device/{id}/sensors/"})
    @ResponseStatus(HttpStatus.OK)
    public List<SensorCommand> getDeviceSensors(@PathVariable Long id) {
        log.debug("Entered getSensorMeasurements, id=" + id);
        Device foundDevice =  deviceService.findById(id).orElseThrow(() ->
                new DeviceNotFoundException("Device with ID " + id + " not found",
                        "Verify the ID is correct and the device is registered with the system."));

        Set<Sensor> sensors = sensorService.findAllByDevice(foundDevice);
        return sensors.stream()
                .sorted()
                .map(converterSensorToSensorCommand::convert)
                .collect(Collectors.toList());
    }

}
