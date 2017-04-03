package fr.xebia.xoverflow.model;

import java.io.Serializable;

public class Origine implements Serializable {

    private final OrigineType origineType;

    public Origine(OrigineType origineType) {
        this.origineType = origineType;
    }

    public enum OrigineType {
        MAIL,
        SLACK,
        UNKNOW
    }


}
