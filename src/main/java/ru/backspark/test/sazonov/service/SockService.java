package ru.backspark.test.sazonov.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.test.sazonov.exception.FileException;
import ru.backspark.test.sazonov.exception.NotFoundException;
import ru.backspark.test.sazonov.model.Sock;
import ru.backspark.test.sazonov.repository.Operator;
import ru.backspark.test.sazonov.repository.SockRepository;
import ru.backspark.test.sazonov.repository.SortingDirection;
import ru.backspark.test.sazonov.repository.SortingType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
@Slf4j
public class SockService {

    @Autowired
    private SockRepository sockRepository;

    public Sock registerIncome(String color, int cottonPercentage, long quantity) {
        Sock exist = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            log.info("Increased the number of socks by {}, for the entity id {} with the color {} and cotton percentage {}. The current number of socks is {}", quantity, exist.getId(), color, cottonPercentage, exist.getQuantity());
            return sockRepository.update(exist);
        }
        Sock sock = new Sock(color, cottonPercentage, quantity);
        Sock result = sockRepository.save(sock);
        log.info("Registered income of entity id {}, {} socks with color {} and cotton percentage {}", result.getId(), quantity, color, cottonPercentage);
        return result;
    }

    public Sock registerOutcome(String color, int cottonPercentage, long quantity) {
        Sock exist = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage);
        if (exist != null && exist.getQuantity() >= quantity) {
            long remainsQuantity = exist.getQuantity() - quantity;
            exist.setQuantity(remainsQuantity);
            Sock result = sockRepository.update(exist);
            log.info("Registered outcome of entity id {}, {} socks with color {} and cotton percentage {}", result.getId(), quantity, color, cottonPercentage);
            return result;
        } else {
            log.error("Not enough socks in stock for color {} and cotton percentage {}", color, cottonPercentage);
            throw new NotFoundException("Нехватка носков на складе.", HttpStatus.OK);
        }
    }

    public List<Sock> getAllSocks() {
        List<Sock> result = sockRepository.findAll();
        log.info("Retrieved all socks summary: {}", result);
        return result;
    }

    public List<Sock> getAllSocks(int percentCotton, Operator operator) {
        List<Sock> result = sockRepository.findAll(percentCotton, operator);
        log.info("Retrieved filtered socks by percentCotton {} and operator {} summary: {}", percentCotton, operator, result);
        return result;
    }

    public Object getAllSocks(String color, int percentCotton, Operator operator) {
        log.info("Retrieved filtered socks by color {} percentCotton {} and operator {}", color, percentCotton, operator);
        List<Sock> result = sockRepository.findAll(color, percentCotton, operator);
        if (result.size() == 0) {
            return 0L;
        } else if (result.size() == 1 && operator.equals(Operator.equal)) {
            return result.get(0).getQuantity();
        }
        return result;
    }

    public List<Sock> getAllSocks(String color) {
        List<Sock> result = sockRepository.findAll(color);
        log.info("Retrieved filtered socks by color {}", color);
        return result;
    }

    public List<Sock> getFilteredSocks(int minCottonPercentage,
                                       int maxCottonPercentage,
                                       SortingType sortingType,
                                       SortingDirection sortingDirection) {
        return sockRepository.findWithCriteria(minCottonPercentage, maxCottonPercentage, sortingType, sortingDirection);
    }

    public Sock updateSock(Long id, String color, Integer cottonPercentage, Long quantity) {
        Sock existUpdatable = sockRepository.findById(id);
        if (existUpdatable != null) {
            if (color != null && !color.isEmpty()) {
                existUpdatable.setColor(color);
            }
            if (cottonPercentage != null) {
                existUpdatable.setCottonPercentage(cottonPercentage);
            }
            if (quantity != null) {
                existUpdatable.setQuantity(quantity);
            }
            Sock result = sockRepository.update(existUpdatable);
            log.info("Updated sock with ID {} to color {}, cotton percentage {}, and quantity {}", id, color, cottonPercentage, quantity);
            return result;
        } else {
            log.error("Sock not found with ID {}", id);
            throw new NotFoundException(String.format("Запись с id %d в БД не найдена", id), HttpStatus.NOT_FOUND);
        }
    }

    public void uploadSocksFromCSV(MultipartFile file) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            try {
                while ((nextLine = csvReader.readNext()) != null) {
                    String color = nextLine[0];
                    int cottonPercentage = Integer.parseInt(nextLine[1]);
                    long quantity = Long.parseLong(nextLine[2]);
                    registerIncome(color, cottonPercentage, quantity);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("error iterate CSV: {}", file.getOriginalFilename());
                throw new FileException("Ошибки при итерации по таблице. Возможно вы не убрали заголовок", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Uploaded socks from CSV file: {}", file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Failed to parse CSV file: {}", file.getOriginalFilename(), e);
            throw new FileException("Ошибки при чтении файла", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (CsvValidationException e) {
            log.error("Failed to validate CSV file: {}", file.getOriginalFilename(), e);
            throw new FileException("Ошибки при валидации файла", HttpStatus.BAD_REQUEST);
        }
    }
}