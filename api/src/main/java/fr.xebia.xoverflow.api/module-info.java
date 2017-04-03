module fr.xebia.xoverflow.api {

    requires fr.xebia.xoverflow.model;
    requires fr.xebia.xoverflow.backend;
    requires spark.core;
    requires gson;
    requires javax.inject;
    requires commons.lang;
    requires slf4j.api;
    requires javaslang;
    requires okhttp;

    exports fr.xebia.xoverflow.api;

}