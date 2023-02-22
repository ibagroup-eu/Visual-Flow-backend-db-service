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
import by.iba.vfdbapi.dto.dbs.JDBCConnectionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JDBCConnectorTest {
    private JDBCConnector connector;

    @BeforeEach
    void setUp() {
        connector = new JDBCConnector();
    }

    @SneakyThrows
    @Test
    void testPing() {
        String jsonString = "{\n" +
                "        \"jdbcUrl\": \"url\",\n" +
                "        \"user\": \"usr\",\n" +
                "        \"password\": \"pass\"\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        JDBCConnectionDTO dto = mapper.treeToValue(node, JDBCConnectionDTO.class);
        Connection connection = Mockito.mock(Connection.class);
        @Cleanup MockedStatic<DriverManager> manager = mockStatic(DriverManager.class);
        manager.when(() -> DriverManager.getConnection(dto.getJdbcUrl(),dto.getUser(),dto.getPassword()))
                .thenReturn(connection);
        when(connection.isValid(500)).thenReturn(true);
        PingStatusDTO actual = connector.ping(dto);
        manager.verify(() -> DriverManager.getConnection(dto.getJdbcUrl(),dto.getUser(),dto.getPassword()));
        assertTrue(actual.isStatus(), "Ping() should return true!");
    }
}