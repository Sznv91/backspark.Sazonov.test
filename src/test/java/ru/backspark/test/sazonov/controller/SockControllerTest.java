package ru.backspark.test.sazonov.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.backspark.test.sazonov.exception.IncorrectInputException;
import ru.backspark.test.sazonov.exception.NotFoundException;
import ru.backspark.test.sazonov.model.Sock;
import ru.backspark.test.sazonov.repository.Operator;
import ru.backspark.test.sazonov.repository.SortingDirection;
import ru.backspark.test.sazonov.repository.SortingType;
import ru.backspark.test.sazonov.service.SockService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SockControllerTest {

    @InjectMocks
    private SockController sockController;

    @Mock
    private SockService sockService;

    @Test
    public void testGetAllSocks() {
        List<Sock> socks = Arrays.asList(new Sock("red", 80, 10), new Sock("blue", 60, 5));
        when(sockService.getAllSocks()).thenReturn(socks);

        ResponseEntity<?> response = sockController.getFilteredSockCount(null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15L, response.getBody());
    }

    @Test
    public void testGetFilteredSock() {
        when(sockService.getAllSocks(50, Operator.moreThan)).thenReturn(List.of(new Sock("red", 80, 10)));
        when(sockService.getAllSocks("blue", 60, Operator.equal)).thenReturn(List.of(new Sock("blue", 60, 5)));

        ResponseEntity<?> response1 = sockController.getFilteredSockCount(null, Operator.moreThan, 50);
        ResponseEntity<?> response2 = sockController.getFilteredSockCount("blue", Operator.equal, 60);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(10L, response1.getBody());
        assertEquals(5L, response2.getBody());
    }

    @Test
    public void testRegisterIncome() {
        Sock sock = new Sock("red", 80, 10);
        when(sockService.registerIncome(anyString(), anyInt(), anyLong())).thenReturn(sock);

        ResponseEntity<Sock> response = sockController.registerIncome("red", 80, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sock, response.getBody());
    }

    @Test
    public void testRegisterOutcome() {
        Sock sock = new Sock("red", 80, 10);
        when(sockService.registerOutcome(anyString(), anyInt(), anyLong())).thenReturn(sock);

        ResponseEntity<Sock> response = sockController.registerOutcome("red", 80, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sock, response.getBody());
    }

    @Test
    public void testUpdateSock() {
        Sock sock = new Sock("red", 80, 10);
        when(sockService.updateSock(anyLong(), anyString(), anyInt(), anyLong())).thenReturn(sock);

        ResponseEntity<?> response = sockController.updateSock(1L, "red", 80, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sock, response.getBody());

        when(sockService.updateSock(anyLong(), anyString(), anyInt(), anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> sockController.updateSock(100L, "red", 80, 10L));

        assertThrows(IncorrectInputException.class, () -> sockController.updateSock(1L, null, null, null));
    }

    @Test
    public void testGetFilteredSockCountWithRange() {
        when(sockService.getFilteredSocks(anyInt(), anyInt(), any(SortingType.class), any(SortingDirection.class)))
                .thenReturn(Arrays.asList(new Sock("red", 80, 10), new Sock("blue", 60, 5), new Sock("yellow", 10, 5)));

        ResponseEntity<?> response = sockController.getFilteredSockCount(30, 70, SortingType.color, SortingDirection.asc);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<Sock>) response.getBody()).size() - 1);
        assertEquals("red", ((List<Sock>) response.getBody()).get(0).getColor());
    }
}