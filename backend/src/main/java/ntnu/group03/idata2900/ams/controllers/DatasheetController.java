package ntnu.group03.idata2900.ams.controllers;

import lombok.extern.slf4j.Slf4j;
import ntnu.group03.idata2900.ams.dto.DatasheetDto;
import ntnu.group03.idata2900.ams.model.Datasheet;
import ntnu.group03.idata2900.ams.services.DatasheetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/datasheets")
public class DatasheetController {

    private final DatasheetService datasheetService;

    private static final String DATASHEET_NOT_FOUND = "Datasheet not found with id: {}";

    /**
     * Creates a new instance of DatasheetController.
     *
     * @param datasheetService datasheetService
     */
    public DatasheetController(DatasheetService datasheetService) {
        this.datasheetService = datasheetService;
    }

    /**
     * Returns list of all datasheets in database
     *
     * @return List of all datasheets in database
     */
    @GetMapping
    public List<Datasheet> getAll() {
        return datasheetService.getAll();
    }

    /**
     * Get a datasheet from database matching given id if it exists.
     *
     * @param id potential id of a datasheet
     * @return a ModelAndView containing datasheet in JSON format
     */
    @GetMapping("/{id}")
    public ResponseEntity<Datasheet> getDatasheet(@PathVariable int id) {
        Optional<Datasheet> datasheet = this.datasheetService.getDatasheet(id);
        if (datasheet.isEmpty()) {
            log.warn(DATASHEET_NOT_FOUND, id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            log.info("Datasheet found with ID: {}", id);
            return new ResponseEntity<>(datasheet.get(), HttpStatus.OK);
        }
    }


    /**
     * Creates a new datasheet.
     *
     * @param datasheet The datasheet object to be created.
     * @return ResponseEntity containing the created datasheet and HTTP status code 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Datasheet> createDatasheet(@RequestBody DatasheetDto datasheet) {
        try {
            Datasheet createdDatasheet = datasheetService.createDatasheet(datasheet);
            log.info("Datasheet created with ID: {}", createdDatasheet.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDatasheet);
        } catch (Exception e) {
            log.error("Error creating datasheet", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    /**
     * Updates an existing datasheet.
     *
     * @param id             The ID of the datasheet to be updated.
     * @param updatedDatasheet The updated datasheet object.
     * @return ResponseEntity containing the updated datasheet (Optional) and HTTP status code 200 (OK) if successful,
     * or HTTP status code 404 (NOT_FOUND) if the datasheet with the given ID doesn't exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Datasheet> updateDatasheet(@PathVariable int id, @RequestBody DatasheetDto updatedDatasheet) {
        Optional<Datasheet> existingDatasheet = datasheetService.getDatasheet(id);
        if (existingDatasheet.isEmpty()) {
            log.warn(DATASHEET_NOT_FOUND, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Datasheet datasheetToUpdate = existingDatasheet.get();
            datasheetToUpdate.setName(updatedDatasheet.getName());
            datasheetToUpdate.setPdfUrl(updatedDatasheet.getPdfUrl());
            datasheetToUpdate.setReferenceNumber(updatedDatasheet.getReferenceNumber());
            datasheetService.updateDatasheet(datasheetToUpdate);
            log.info("Datasheet updated with ID: {}", id);
            return new ResponseEntity<>(datasheetToUpdate, HttpStatus.OK);
        }
    }

    /**
     * Deletes a datasheet.
     *
     * @param id The ID of the datasheet to be deleted.
     * @return ResponseEntity with HTTP status code 204 (NO_CONTENT) if successful,
     * or HTTP status code 404 (NOT_FOUND) if the datasheet with the given ID doesn't exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Datasheet> deleteDatasheet(@PathVariable int id) {
        Optional<Datasheet> existingDatasheet = datasheetService.getDatasheet(id);
        if (existingDatasheet.isEmpty()) {
            log.warn(DATASHEET_NOT_FOUND, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            datasheetService.deleteDatasheet(id);
            log.info("Datasheet deleted with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
