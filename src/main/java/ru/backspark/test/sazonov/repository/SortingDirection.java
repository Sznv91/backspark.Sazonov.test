package ru.backspark.test.sazonov.repository;

public enum SortingDirection {
    asc, desc;

    public String toString() {
        return " " + name();
    }
}
