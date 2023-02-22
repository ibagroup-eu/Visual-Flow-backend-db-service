/*
 * Copyright (c) 2022 IBA Group, a.s. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package by.iba.vfdbapi.dao;

import by.iba.vfdbapi.dto.PingStatusDTO;
import by.iba.vfdbapi.dto.dbs.MongoConnectionDTO;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Repository-class for interaction with MongoDB.
 */
@Repository
@Slf4j
public class MongoConnector {

    /**
     * Secondary method to establish a connection to the Mongo database and receive it.
     * @param dto an object containing data to connect to the database.
     * @return database connection object.
     */
    private MongoClient connect(MongoConnectionDTO dto) {
        ConnectionString connectionString = new ConnectionString(
                String.format("mongodb://%s:%d/%s", dto.getHost(), dto.getPort(), dto.getDatabase()));
        MongoClientSettings.Builder mongoClientSettings = MongoClientSettings.builder()
                .applyToSocketSettings(builder -> {
                    builder.connectTimeout(3, SECONDS);
                    builder.readTimeout(3, SECONDS);
                })
                .applyToClusterSettings( builder -> builder.serverSelectionTimeout(3, SECONDS))
                .applyConnectionString(connectionString);
        if(dto.getUser() != null && dto.getPassword() != null) {
            mongoClientSettings.credential(MongoCredential.createCredential(
                    dto.getUser(), dto.getDatabase(), dto.getPassword().toCharArray()));
        }
        return MongoClients.create(mongoClientSettings.build());
    }

    /**
     * DAO method for checking the connection to the Mongo database.
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(MongoConnectionDTO dto) {
        try(MongoClient client = connect(dto)) {
            return PingStatusDTO.builder().status(
                    client
                            .getDatabase(dto.getDatabase())
                            .runCommand(new Document("ping", 1))
                            .containsKey("ok")).build();
        } catch (MongoTimeoutException | MongoSocketOpenException e) {
            LOGGER.error(e.getMessage(), e);
            return PingStatusDTO.builder().status(false).build();
        }
    }
}
