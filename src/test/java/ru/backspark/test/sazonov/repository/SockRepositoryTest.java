package ru.backspark.test.sazonov.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.backspark.test.sazonov.model.Sock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SockRepositoryTest {

    @Autowired
    private SockRepository repository;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("initDB.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("populate.sql"));
        }
    }

    @Test
    public void testFindByColorAndCottonPercentage() {
        Sock sock = repository.findByColorAndCottonPercentage("red", 80);
        assertNotNull(sock);
        assertEquals("red", sock.getColor());
        assertEquals(80, sock.getCottonPercentage());
        assertEquals(20, sock.getQuantity());
    }

    @Test
    public void testSave() {
        Sock sock = new Sock("yellow", 50, 20);
        Sock savedSock = repository.save(sock);
        assertNotNull(savedSock.getId());
        assertEquals("yellow", savedSock.getColor());
        assertEquals(50, savedSock.getCottonPercentage());
        assertEquals(20, savedSock.getQuantity());
    }

    @Test
    public void testUpdate() {
        Sock sock = repository.findByColorAndCottonPercentage("red", 80);
        sock.setQuantity(15);
        Sock updatedSock = repository.update(sock);
        assertEquals(15, updatedSock.getQuantity());
    }

    @Test
    public void testFindAll() {
        List<Sock> socks = repository.findAll();
        assertEquals(10, socks.size());
    }

    @Test
    public void testFindAllByColor() {
        List<Sock> socks = repository.findAll("Зелёный");
        assertEquals(2, socks.size());
        assertEquals("Зелёный", socks.get(0).getColor());
    }

    @Test
    public void testFindAllByOperator() {
        List<Sock> socks = repository.findAll(60, Operator.moreThan);
        assertEquals(3, socks.size());
    }

    @Test
    public void testFindAllByColorAndOperator() {
        List<Sock> sock = repository.findAll("blue", 60, Operator.equal);
        assertNotNull(sock);
        assertEquals(1, sock.size());
        assertEquals("blue", sock.get(0).getColor());
        assertEquals(60, sock.get(0).getCottonPercentage());
    }

    @Test
    public void testFindById() {
        Sock sock = repository.findById(1L);
        assertNotNull(sock);
        assertEquals("red", sock.getColor());
        assertEquals(80, sock.getCottonPercentage());
        assertEquals(20, sock.getQuantity());
    }

    @Test
    public void testFindWithCriteria() {
        List<Sock> socks = repository.findWithCriteria(30, 70, SortingType.color, SortingDirection.asc);
        assertEquals(4, socks.size());
    }
}