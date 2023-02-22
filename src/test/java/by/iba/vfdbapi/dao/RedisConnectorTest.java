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
import by.iba.vfdbapi.dto.dbs.RedisConnectionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisConnectorTest {

    private RedisConnector connector;

    @BeforeEach
    void setUp() {
        connector = new RedisConnector();
    }

    @SneakyThrows
    @Test
    void testPing() {
        String jsonString = "{\n" +
                "        \"host\": \"my.host.rs\",\n" +
                "        \"port\": \"1111\",\n" +
                "        \"user\": \"usr\",\n" +
                "        \"password\": \"pass\",\n" +
                "        \"ssl\": \"true\"\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        RedisConnectionDTO dto = mapper.treeToValue(node, RedisConnectionDTO.class);
        JedisConnectionFactory jedisConnectionFactory = mock(JedisConnectionFactory.class);
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because RedisConnectionFailureException " +
                "has been thrown!");
        verify(jedisConnectionFactory, never()).getConnection();
    }
}
