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
import by.iba.vfdbapi.dto.dbs.RedshiftConnectionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@Repository
public class RedShiftConnector {

    /**
     * Secondary method to establish a connection to the RedShift database and receive it.
     * @param dto an object containing data to connect to the database.
     * @return database connection object.
     */
    private Connection connect(RedshiftConnectionDTO dto) throws SQLException {
        StringBuilder host = new StringBuilder("jdbc:redshift");
        Properties properties = new Properties();
        properties.setProperty("user", dto.getUser());
        properties.setProperty("password", dto.getPassword());
        properties.setProperty("ssl", dto.getSsl().toString());
        properties.setProperty("connectTimeout", "2");
        if(!dto.getAccessKey().isEmpty() && !dto.getSecretKey().isEmpty() && Boolean.TRUE.equals(dto.getSsl())) {
            host.append(":iam");
            properties.setProperty("AccessKeyID", dto.getAccessKey());
            properties.setProperty("SecretAccessKey", dto.getSecretKey());
        }
        host.append(String.format("://%s:%d/", dto.getHost(), dto.getPort()));
        return DriverManager.getConnection(host.toString(), properties);
    }

    /**
     * DAO method for checking the connection to the RedShift database.
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(RedshiftConnectionDTO dto) {
        try(Connection connection = connect(dto)) {
            return PingStatusDTO.builder().status(connection.isValid(500)).build();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return PingStatusDTO.builder().status(false).build();
        }
    }
}
