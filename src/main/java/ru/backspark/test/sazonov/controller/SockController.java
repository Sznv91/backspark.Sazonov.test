package ru.backspark.test.sazonov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.test.sazonov.exception.IncorrectInputException;
import ru.backspark.test.sazonov.model.Sock;
import ru.backspark.test.sazonov.repository.Operator;
import ru.backspark.test.sazonov.repository.SortingDirection;
import ru.backspark.test.sazonov.repository.SortingType;
import ru.backspark.test.sazonov.service.SockService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "SockController", description = "Operations pertaining to socks")
@Slf4j
public class SockController {

    @Autowired
    private SockService sockService;

    @GetMapping(value = "/")
    @Operation(summary = "Get all sock count or get the filtered count of socks", description = "Returns the count of socks with the specified filters. The \"operator\" and the \"cottonPercentage\" are used together.")
    public ResponseEntity<Long> getFilteredSockCount(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Operator operator,
            @RequestParam(required = false) Integer cottonPercentage) {
        long result = 0;
        List<Sock> resultList = new ArrayList<>();
        if (color == null && operator == null && cottonPercentage == null) {
            log.info("Received request to get all socks summary");
            resultList = sockService.getAllSocks();
        } else if (color != null && operator == null && cottonPercentage == null) {
            log.info("Received request to get all socks summary with filter color:{}", color);
            resultList = sockService.getAllSocks(color);
        } else {
            try {
                log.info("Received request to get all socks summary with filter color:{}, operator:{}, cottonPercentage{}", color, operator, cottonPercentage);
                resultList = color == null ? sockService.getAllSocks(cottonPercentage, operator) : sockService.getAllSocks(color, cottonPercentage, operator);
            } catch (NullPointerException e) {
                throw new IncorrectInputException("Поля \"operator\" и \"cottonPercentage\" должны использоваться совместно", HttpStatus.BAD_REQUEST);
            }
        }

        result = resultList.stream().mapToLong(Sock::getQuantity).sum();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping(value = "/rage")
    @Operation(summary = "Get the filtering by cotton percentage range and sorting by color and percentage of cotton", description = "Returns the count of socks with the specified filters")
    public ResponseEntity<List<Sock>> getFilteredSockCount(
            @RequestParam int minCottonPercentage,
            @RequestParam int maxCottonPercentage,
            @RequestParam(required = false) SortingType sortingType,
            @RequestParam(required = false) SortingDirection sortingDirection) {
        log.info("Received request to get socks with filter minCottonPercentage:{}, maxCottonPercentage:{}, sortingType{}, sortingDirection{}", minCottonPercentage, maxCottonPercentage, sortingType, sortingDirection);
        return new ResponseEntity<>(sockService.getFilteredSocks(minCottonPercentage, maxCottonPercentage, sortingType, sortingDirection), HttpStatus.OK);
    }

    @PostMapping("/income")
    @Operation(summary = "Register the income of socks", description = "Increases the quantity of socks in stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    public ResponseEntity<Sock> registerIncome(@RequestParam String color, @RequestParam int cottonPercentage, @RequestParam long quantity) {
        log.info("Received request to register income of {} socks with color {} and cotton percentage {}", quantity, color, cottonPercentage);
        Sock result = sockService.registerIncome(color, cottonPercentage, quantity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/outcome")
    @Operation(summary = "Register the outcome of socks", description = "Decreases the quantity of socks in stock if enough are available")
    public ResponseEntity<Sock> registerOutcome(@RequestParam String color, @RequestParam int cottonPercentage, @RequestParam long quantity) {
        log.info("Received request to register outcome of {} socks with color {} and cotton percentage {}", quantity, color, cottonPercentage);
        Sock result = sockService.registerOutcome(color, cottonPercentage, quantity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sock details", description = "Updates the details of a sock by its ID")
    public ResponseEntity<Sock> updateSock(
            @PathVariable Long id,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer cottonPercentage,
            @RequestParam(required = false) Long quantity) {
        log.info("Received request to update sock with ID {} to color {}, cotton percentage {}, and quantity {}", id, color, cottonPercentage, quantity);
        if (color == null && cottonPercentage == null && quantity == null) {
            throw new IncorrectInputException("Не указанны поля для обновления", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(sockService.updateSock(id, color, cottonPercentage, quantity), HttpStatus.OK);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload socks from CSV file", description = "Uploads a batch of socks from a CSV file. The order of the fields: \"color,cotton Percentage,quantity\". Sample content: \"red,80,10\"")
    public void uploadSocksFromCSV(@RequestPart("file") MultipartFile file) {
        log.info("Received request to upload socks from CSV file: {}", file.getOriginalFilename());
        sockService.uploadSocksFromCSV(file);
    }
}
