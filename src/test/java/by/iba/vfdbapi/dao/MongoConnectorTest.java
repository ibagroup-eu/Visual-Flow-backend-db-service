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

import by.iba.vfdbapi.dao.answers.BuilderAnswer;
import by.iba.vfdbapi.dto.PingStatusDTO;
import by.iba.vfdbapi.dto.dbs.MongoConnectionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoConnectorTest {

    private MongoConnector connector;

    @BeforeEach
    void setUp() {
        connector = new MongoConnector();
    }

    @SneakyThrows
    @Test
    void testPing() {
        String jsonString = "{\n" +
                "        \"host\": \"my.host.rs\",\n" +
                "        \"port\": \"1111\",\n" +
                "        \"user\": \"usr\",\n" +
                "        \"password\": \"pass\",\n" +
                "        \"database\": \"example\",\n" +
                "        \"ssl\": \"true\"\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        MongoConnectionDTO dto = mapper.treeToValue(node, MongoConnectionDTO.class);
        MongoClientSettings.Builder builder = mock(MongoClientSettings.Builder.class, new BuilderAnswer());
        @Cleanup MockedStatic<MongoClientSettings> mongoClientSettingsStatic = mockStatic(MongoClientSettings.class);
        mongoClientSettingsStatic.when(MongoClientSettings::builder).thenReturn(builder);
        MongoClientSettings mongoClientSettings = mock(MongoClientSettings.class);
        when(builder.build()).thenReturn(mongoClientSettings);
        @Cleanup MockedStatic<MongoClients> mongoClients = mockStatic(MongoClients.class);
        MongoClient mongoClient = mock(MongoClient.class);
        mongoClients.when(() -> MongoClients.create(mongoClientSettings)).thenReturn(mongoClient);
        when(mongoClient.getDatabase(dto.getDatabase())).thenThrow(new MongoSocketOpenException("Unable to get DB",
                new ServerAddress()));
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because of MongoSocketOpenException!");
    }
}