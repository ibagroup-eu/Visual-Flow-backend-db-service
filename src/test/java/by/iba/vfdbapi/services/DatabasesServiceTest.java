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
package by.iba.vfdbapi.services;

import by.iba.vfdbapi.dao.*;
import by.iba.vfdbapi.dto.ConnectDto;
import by.iba.vfdbapi.dto.PingStatusDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabasesServiceTest {

    @Spy
    private JDBCConnector jdbc;
    @Mock
    private MongoConnector mongo;
    @Mock
    private ElasticConnector elastic;
    @Mock
    private CassandraConnector cassandra;
    @Mock
    private RedisConnector redis;
    @Mock
    private RedShiftConnector redShift;
    @Mock
    private COSConnector cos;
    @Mock
    private AmazonS3Connector s3;
    private DatabasesService service;

    @BeforeEach
    void setUp() {
        service = new DatabasesService(jdbc, mongo, elastic, cassandra, redis, redShift, cos, s3);
    }

    @Test
    void testPingFailedNullStorage() throws JsonProcessingException {
        String jsonString = "{\"storage\":null,\"acc\": \"no-type\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        ConnectDto dto = ConnectDto.builder().key("key").value(node).build();
        PingStatusDTO actual = service.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false!");
    }

    @SneakyThrows
    @Test
    void testClickHouseChoice() {
        String jsonString = "{\"storage\": \"clickhouse\"," +
                            "\"user\": \"default\"," +
                            "\"password\": \"some\"," +
                            "\"database\": null," +
                            "\"host\": \"https://host\"," +
                            "\"port\": \"1993\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        ConnectDto dto = ConnectDto.builder().key("key").value(node).build();
        Connection connection = Mockito.mock(Connection.class);
        String expectedHost = "jdbc:clickhouse://host:1993/";
        String expectedUser = "default";
        String expectedPass = "some";
        @Cleanup MockedStatic<DriverManager> manager = mockStatic(DriverManager.class);
        manager.when(() -> DriverManager.getConnection(expectedHost, expectedUser, expectedPass))
                .thenReturn(connection);
        when(connection.isValid(500)).thenReturn(true);
        service.ping(dto);
        manager.verify(() -> DriverManager.getConnection(expectedHost, expectedUser, expectedPass));
    }
}
