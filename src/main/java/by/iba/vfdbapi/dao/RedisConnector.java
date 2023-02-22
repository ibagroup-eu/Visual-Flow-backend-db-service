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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.data.redis.RedisConnectionFailureException;

/**
 * Repository-class for interaction with Redis.
 */
@Repository
@Slf4j
public class RedisConnector {

    /**
     * Secondary method to establish a connection to the Redis database and receive it.
     * @param dto an object containing data to connect to the database.
     * @return database connection object.
     */
    private JedisConnectionFactory connect(RedisConnectionDTO dto) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(dto.getHost(),
                dto.getPort());
        redisStandaloneConfiguration.setUsername(dto.getUser());
        redisStandaloneConfiguration.setPassword(dto.getPassword());
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /**
     * DAO method for checking the connection to the Redis database.
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(RedisConnectionDTO dto) {
        try {
            return PingStatusDTO.builder().status(!connect(dto).getConnection().isClosed()).build();
        } catch (RedisConnectionFailureException e) {
            LOGGER.error(e.getMessage(), e);
            return PingStatusDTO.builder().status(false).build();
        }
    }
}
