/*
 * Copyright 2013  Séven Le Mesle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.xebia.xoverflow.server.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.xebia.xoverflow.server.XoverflowServer;
import com.xebia.xoverflow.server.model.Post;
import com.xebia.xoverflow.server.service.es.ESApiService;
import com.xebia.xoverflow.server.service.es.ESPostRepositoryService;
import dagger.Module;
import dagger.Provides;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import javax.inject.Singleton;
import java.util.Date;

/**
 * Created by slemesle on 03/09/2014.
 */
@Module(complete = false,
        injects = {
            ESPostRepositoryService.class,
            XoverflowServer.class, Node.class
}, library = true)
public class DaggerModule {


    @Provides @Singleton ObjectMapper provideJacksonMapper() {
        return new ObjectMapper();
    }


    @Provides @Singleton PostRepositoryService providePostRepository(){
        return new ESPostRepositoryService();
    }

    @Provides @Singleton
    ESApiService provideESApiService(){

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://127.0.0.1:9200/")
                .setConverter(new GsonConverter(gson)).build();

        return restAdapter.create(ESApiService.class);
    }

    @Provides @Singleton Node provideEsNode(){
        Node res;

        res = NodeBuilder.nodeBuilder().data(true).clusterName("xoverflow").local(true).node();
        return res;
    }

}
