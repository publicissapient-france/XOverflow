package com.xebia.xoverflow.server;

import static spark.Spark.*;

public class XoverflowServer {

    public static void main(String[] args) {
        get("/hello",(request, response) -> "Hello World!");
    }

}