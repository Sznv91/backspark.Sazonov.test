package ru.backspark.test.sazonov.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.backspark.test.sazonov.model.Sock;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class SockRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Sock findByColorAndCottonPercentage(String color, int cottonPercentage) {
        try {
            return entityManager.createQuery("SELECT s FROM Sock s WHERE s.color=:color AND s.cottonPercentage =:cottonPercentage", Sock.class)
                    .setParameter("color", color)
                    .setParameter("cottonPercentage", cottonPercentage)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public Sock save(Sock sock) {
        entityManager.persist(sock);
        return sock;
    }

    @Transactional
    public Sock update(Sock exist) {
        return entityManager.merge(exist);
    }

    public List<Sock> findAll() {
        return entityManager.createQuery("SELECT s FROM Sock s", Sock.class).getResultList();
    }

    public List<Sock> findAll(String color) {
        return entityManager.createQuery("SELECT s FROM Sock s WHERE s.color=:color", Sock.class).setParameter("color", color).getResultList();
    }

    public List<Sock> findAll(int percentCotton, Operator operator) {
        StringBuilder builder = new StringBuilder("SELECT s FROM Sock s WHERE s.cottonPercentage ");
        String query = buildQueryByOperator(builder, operator, percentCotton);
        return entityManager.createQuery(query, Sock.class).getResultList();
    }

    public List<Sock> findAll(String color, int percentCotton, Operator operator) {
        StringBuilder builder = new StringBuilder("SELECT s FROM Sock s WHERE s.color=:color AND s.cottonPercentage");
        String query = buildQueryByOperator(builder, operator, percentCotton);
        return entityManager.createQuery(query, Sock.class).setParameter("color", color).getResultList();
    }

    private String buildQueryByOperator(StringBuilder sb, Operator operator, int percentCotton) {
        switch (operator) {
            case lessThan:
                sb.append("<").append(percentCotton + 1); //Для попадания числа "percentCotton" в фильтруемый диапазон. <= x
                break;
            case moreThan:
                sb.append(">").append(percentCotton - 1); //Для попадания числа "percentCotton" в фильтруемый диапазон. >= x
                break;
            default:
                sb.append("=").append(percentCotton);
        }
        return sb.toString();
    }

    public Sock findById(Long id) {
        try {
            return entityManager.createQuery("SELECT s FROM Sock s WHERE s.id = :id", Sock.class).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Sock> findWithCriteria(int minCottonPercentage, int maxCottonPercentage, SortingType sortingType, SortingDirection sortingDirection) {
        StringBuilder ordering = new StringBuilder();
        if (sortingType != null) {
            ordering.append("ORDER BY s.");
            ordering.append(sortingType.name());
        }
        if (sortingDirection != null && !ordering.isEmpty()) {
            ordering.append(sortingDirection);
        }
        return entityManager.createQuery("SELECT s FROM Sock s WHERE s.cottonPercentage BETWEEN :minCottonPercentage AND :maxCottonPercentage " + ordering, Sock.class).setParameter("minCottonPercentage", minCottonPercentage).setParameter("maxCottonPercentage", maxCottonPercentage).getResultList();
    }
}
