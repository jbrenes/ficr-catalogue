package it.ficr.nbi;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "FICR Results Catalogue API")

public class ResultsRestController {


    private static final Logger log = LoggerFactory.getLogger(ResultsRestController.class);

    @Autowired
    private ResultCatalogueService resultCatalogueService;

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


    @GetMapping("/")
    private ResponseEntity getAllResults(){
        log.debug("Received result file onboard request");
        try {

            return  new ResponseEntity(resultCatalogueService.getResults(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}



