module fr.xebia.xoverflow.backend {

    requires fr.xebia.xoverflow.model;

    requires commons.io;
    requires commons.lang;
    requires javaslang;
    requires gson;
    requires okhttp;
    requires okio;

    requires slf4j.api;

    exports fr.xebia.xoverflow.backend.service;
    exports fr.xebia.xoverflow.backend.es;

}