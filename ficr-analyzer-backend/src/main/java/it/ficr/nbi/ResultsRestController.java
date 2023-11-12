package it.ficr.nbi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.ficr.elements.Event;
import it.ficr.elements.Result;
import it.ficr.exceptions.ApiError;
import it.ficr.services.EventCatalogueService;
import it.ficr.services.ResultCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/ficr/results")

public class ResultsRestController {


    private static final Logger log = LoggerFactory.getLogger(ResultsRestController.class);

    @Autowired
    private ResultCatalogueService resultCatalogueService;




    @Operation(
            summary = "Onboard Results file",
            description = "Endpoint to onboard Results file",
            tags = {"results"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK"),

            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })

    @PostMapping("/onboard")
    private ResponseEntity onboard(@RequestParam("file") MultipartFile file){
        log.debug("Received result file onboard request");
        try {
            File tempFile = File.createTempFile("result-", null);
            file.transferTo(tempFile);
            resultCatalogueService.onboardFile(tempFile);
            return  new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
           log.error("Error", e);
           return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @Operation(
            summary = "Onboard Season",
            description = "Endpoint to onboard Season",
            tags = {"results"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK"),

            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })

    @PostMapping("/onboardSeason/{season}")
    private ResponseEntity onboardSeason(@PathVariable(value="season") Integer season){
        log.debug("Received season onboard request");
        try {
            resultCatalogueService.onboardSeason(season.intValue());
            return  new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(
            summary = "Onboard Event",
            description = "Endpoint to onboard Event",
            tags = {"results"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK"),

            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })

    @PostMapping("/onboard/{codPub}")
    private ResponseEntity onboard(@PathVariable(value="codPub") String codPub){
        log.debug("Received event onboard request");
        try {
            resultCatalogueService.onboardEvent(codPub);
            return  new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @Operation(
            summary = "Get Results",
            description = "Endpoint to query registered Results",
            tags = {"results"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation =  Result.class)))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })

    @GetMapping("/")
    private ResponseEntity getAllResults(@RequestParam(required = false) String categoryCode,
                                         @RequestParam(required = false) String categoryName,
                                         @RequestParam(required = false) String competitionCode,
                                         @RequestParam(required = false) String competitionName){

        try {

            return  new ResponseEntity(resultCatalogueService.getResults(categoryCode, categoryName, competitionCode, competitionName), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}



