package ru.backspark.test.sazonov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.backspark.test.sazonov.exception.NotFoundException;
import ru.backspark.test.sazonov.model.Sock;
import ru.backspark.test.sazonov.repository.Operator;
import ru.backspark.test.sazonov.repository.SockRepository;
import ru.backspark.test.sazonov.repository.SortingDirection;
import ru.backspark.test.sazonov.repository.SortingType;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SockServiceTest {

    @InjectMocks
    private SockService service;

    @Mock
    private SockRepository repository;

    @Test
    public void registerIncome() {
        when(repository.findByColorAndCottonPercentage(anyString(), anyInt())).thenReturn(new Sock(1L, "red", 10, 5));
        when(repository.update(new Sock(1L, "red", 10, 5))).thenReturn(new Sock(1L, "red", 10, 10));

        Sock actualAddQuantity = service.registerIncome("red", 10, 5);
        assertEquals(new Sock(1L, "red", 10, 10), actualAddQuantity);


        when(repository.findByColorAndCottonPercentage(eq("blue"), anyInt())).thenReturn(nullable(Sock.class));
        when(repository.save(new Sock("blue", 10, 5))).thenReturn(new Sock(2L, "blue", 10, 5));

        Sock actualCreateSock = service.registerIncome("blue", 10, 5);
        assertEquals(new Sock(2L, "blue", 10, 5), actualCreateSock);
    }

    @Test
    public void registerOutcome() {
        when(repository.findByColorAndCottonPercentage(anyString(), anyInt())).thenReturn(new Sock(1L, "red", 10, 5));
        when(repository.update(new Sock(any(), "red", 10, 3))).thenReturn(new Sock(1L, "red", 10, 2));
        Sock actualOutcomeQuantity = service.registerOutcome("red", 10, 3);
        assertEquals(new Sock(1L, "red", 10, 2), actualOutcomeQuantity);
        assertThrows(NotFoundException.class, () -> service.registerOutcome("red", 10, 6));
    }

    @Test
    public void getAllSocks() {
        when(repository.findAll(15, Operator.moreThan)).thenReturn(asList(new Sock(1L, "red", 10, 50), new Sock(2L, "red", 15, 50)));
        List<Sock> actual = service.getAllSocks(15, Operator.moreThan);
        assertEquals(100, actual.get(0).getQuantity() + actual.get(1).getQuantity());
    }

    @Test
    public void getFilteredSocks() {
        when(repository.findWithCriteria(10, 15, SortingType.cottonPercentage, SortingDirection.desc)).thenReturn(asList(new Sock(1L, "red", 11, 50), new Sock(2L, "red", 14, 50)));
        List<Sock> actual = service.getFilteredSocks(10,15, SortingType.cottonPercentage, SortingDirection.desc);
        assertEquals(100, actual.get(0).getQuantity() + actual.get(1).getQuantity());
    }

    @Test
    public void updateSock(){
        when(repository.findById(1L)).thenReturn(new Sock(1L, "red", 11, 50));
        Sock expected = new Sock(1L, "blue", 11, 10L);
        when(repository.update(new Sock(1L, "blue", 11, 10))).thenReturn(expected);
        Sock actual = service.updateSock(1L, "blue", 11, 10L);
        assertEquals(expected.getColor(), actual.getColor());

        when(repository.findById(anyLong())).thenReturn(nullable(Sock.class));
        assertThrows(NotFoundException.class, ()->service.updateSock(2L, "yellow", null, 10L));

    }
}