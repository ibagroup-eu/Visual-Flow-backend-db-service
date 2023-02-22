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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class containing common logic for all databases for which
 * no specific classes are provided for db operations.
 */
@Slf4j
@Repository
public class JDBCConnector {

    /**
     * DAO method for checking the connection to the JDBC database.
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(JDBCConnectionDTO dto) {
        try(Connection con = DriverManager.getConnection(dto.getJdbcUrl(), dto.getUser(), dto.getPassword())) {
            return PingStatusDTO.builder().status(con.isValid(500)).build();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return PingStatusDTO.builder().status(false).build();
        }
    }

}
