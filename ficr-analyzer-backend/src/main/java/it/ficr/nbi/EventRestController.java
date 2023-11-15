package it.ficr.nbi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.ficr.elements.Athlete;
import it.ficr.elements.Event;
import it.ficr.elements.EventInfo;
import it.ficr.exceptions.ApiError;
import it.ficr.exceptions.ElementNotFoundException;
import it.ficr.services.AthleteCatalogueService;
import it.ficr.services.EventCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ficr/events")

public class EventRestController {


    private static final Logger log = LoggerFactory.getLogger(EventRestController.class);

    @Autowired
    private EventCatalogueService eventCatalogueService;


    @Operation(
            summary = "Get Events",
            description = "Endpoint to query registered Events",
            tags = {"events"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation =  EventInfo.class)))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping("/")
    private ResponseEntity getAllEvents(@RequestParam(required = false) String name,
                                          @RequestParam(required = false) String place,
                                          @RequestParam(required = false) Integer year){
        log.debug("Received request to retrieve all events");
        try {

            return  new ResponseEntity(eventCatalogueService.getEvents(name, place, year), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity(new ApiError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }



    @Operation(
            summary = "Get Events",
            description = "Endpoint to query registered Events",
            tags = {"events"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation =  EventInfo.class)))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping("/{eventIdentifier}")
    private ResponseEntity getEvent(@PathVariable UUID eventIdentifier){
        log.debug("Received request to event {}", eventIdentifier);
        try {

            return  new ResponseEntity(eventCatalogueService.getEvent(eventIdentifier), HttpStatus.OK);
        }catch(ElementNotFoundException e){
            log.warn("Athlete not found", e.getMessage());
            return new ResponseEntity(new ApiError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity(new ApiError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }



}



