package fr.xebia.xoverflow.model;

public interface IdBuilder<T> {

    void setId(String id);

    T build();

}
