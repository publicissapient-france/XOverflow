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

import com.xebia.xoverflow.server.model.Post;

import java.util.List;

/**
 * Created by slemesle on 03/09/2014.
 */
public interface PostRepositoryService {

    /**
     * Persist a Post in DB and return it
     * @param post
     * @return
     */
    Post create(Post post);

    /**
     * Retrieve last 10 posts
     */
    List<Post> listLast10Posts();

    Post findPost(String id);

    Post updatePost(Post post);
}
