package fr.xebia.xoverflow.api.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new GsonBuilder().create();

    @Override
    public String render(Object model) throws Exception {
        return gson.toJson(model);
    }

}