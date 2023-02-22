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
import by.iba.vfdbapi.dto.*;
import by.iba.vfdbapi.dto.ConnectDto;
import by.iba.vfdbapi.dto.dbs.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Main service class determinate DAO methods will be invoked according storage param.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabasesService {

    private final JDBCConnector jdbc;
    private final MongoConnector mongo;
    private final ElasticConnector elastic;
    private final CassandraConnector cassandra;
    private final RedisConnector redis;
    private final RedShiftConnector redShift;
    private final COSConnector cos;
    private final AmazonS3Connector s3;

    /**
     * Method for checking the connection to the database. Determinate connection DTO type.
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(ConnectDto dto) {
        JsonNode value = dto.getValue();
        if(value.hasNonNull("storage")) {
            Storages storage = Storages.findStorage(value.get("storage").asText());
            ObjectMapper mapper = new ObjectMapper();
            try {
                switch (storage) {
                    case DB2:
                    case MYSQL:
                    case MSSQL:
                    case REDSHIFT_JDBC:
                    case POSTGRES:
                    case ORACLE:
                        return jdbc.ping(mapper.treeToValue(dto.getValue(), JDBCConnectionDTO.class));
                    case CLICK_HOUSE:
                        ClickHouseConnectionDTO connectionDTO = mapper.treeToValue(dto.getValue(), ClickHouseConnectionDTO.class);
                        JDBCConnectionDTO jdbcConnectionDTO = new JDBCConnectionDTO();
                        String host = connectionDTO.getHost()
                                .replace("http://", "")
                                .replace("https://", "");
                        jdbcConnectionDTO.setJdbcUrl(String.format("jdbc:clickhouse://%s:%d/%s", host,
                                connectionDTO.getPort(),
                                connectionDTO.getDatabase() == null ? "" : connectionDTO.getDatabase()));
                        jdbcConnectionDTO.setUser(connectionDTO.getUser());
                        jdbcConnectionDTO.setPassword(connectionDTO.getPassword());
                        return jdbc.ping(jdbcConnectionDTO);
                    case MONGO:
                        return mongo.ping(mapper.treeToValue(dto.getValue(), MongoConnectionDTO.class));
                    case ELASTIC:
                        return elastic.ping(mapper.treeToValue(dto.getValue(), ElasticConnectionDTO.class));
                    case CASSANDRA:
                        return cassandra.ping(mapper.treeToValue(dto.getValue(), CassandraConnectionDTO.class));
                    case REDIS:
                        return redis.ping(mapper.treeToValue(dto.getValue(), RedisConnectionDTO.class));
                    case REDSHIFT:
                        return redShift.ping(mapper.treeToValue(dto.getValue(), RedshiftConnectionDTO.class));
                    case COS:
                        return cos.ping(mapper.treeToValue(dto.getValue(), CosConnectionDTO.class));
                    case S3:
                        return s3.ping(mapper.treeToValue(dto.getValue(), AmazonS3DTO.class));
                    default:
                        return PingStatusDTO.builder().status(false).build();
                }
            } catch (JsonProcessingException e) {
                LOGGER.error("Database configuration has not been found: ", e);
                return PingStatusDTO.builder().status(false).build();
            }
        }
        LOGGER.error("Storage type should not be null!");
        return PingStatusDTO.builder().status(false).build();
    }
}
