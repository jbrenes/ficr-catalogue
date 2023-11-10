package it.ficr.nbi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.ficr.elements.Athlete;
import it.ficr.exceptions.ApiError;
import it.ficr.services.AthleteCatalogueService;
import it.ficr.services.ResultCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/v1/ficr/athletes")
@Tag(name = "FICR Analyzer API")

public class AthleteRestController {


    private static final Logger log = LoggerFactory.getLogger(AthleteRestController.class);

    @Autowired
    private AthleteCatalogueService athleteCatalogueService;


    @Operation(
            summary = "Get Athletes",
            description = "Endpoint to query registered Athletes",
            tags = {"athletes"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation =  Athlete.class)))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping("/")
    private ResponseEntity getAllAthletes(@RequestParam(required = false) String name,
                                          @RequestParam(required = false) String surname,
                                          @RequestParam(required = false) String society,
                                          @RequestParam(required = false) Integer year){
        log.debug("Received request to retrieve all athletes");
        try {

            return  new ResponseEntity(athleteCatalogueService.getAthletes(name, surname, society, year), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity(new ApiError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}



