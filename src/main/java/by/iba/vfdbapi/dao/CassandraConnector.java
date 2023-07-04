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

import by.iba.vfdbapi.dao.common.CertCommon;
import by.iba.vfdbapi.dto.PingStatusDTO;
import by.iba.vfdbapi.dto.dbs.CassandraConnectionDTO;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.JdkSSLOptions;
import com.datastax.driver.core.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.stereotype.Repository;

import java.security.KeyStore;

import static by.iba.vfdbapi.utils.ErrorMessageUtil.handleConnectError;

/**
 * Repository-class for interaction with Cassandra.
 */
@Slf4j
@Repository
public class CassandraConnector {

    /**
     * Secondary method to establish a connection to the Cassandra database and receive it.
     *
     * @param dto an object containing data to connect to the database.
     * @return database connection object.
     */
    public Cluster connect(CassandraConnectionDTO dto) {
        Cluster.Builder builder = Cluster.builder()
                .addContactPoint(dto.getHost())
                .withPort(dto.getPort())
                .withClusterName(dto.getCluster())
                .withCredentials(dto.getUsername(), dto.getPassword());
        if (Boolean.TRUE.equals(dto.getSsl())) {
            try {
                KeyStore trustStore = CertCommon.loadTrustStoreFromCert(dto.getCertData());
                return builder.withSSL(JdkSSLOptions.builder().withSSLContext(new SSLContextBuilder()
                        .loadTrustMaterial(trustStore, null)
                        .build()).build()).build();
            } catch (Exception e) {
                LOGGER.error("Could not establish Cassandra connection: {}", e.getMessage());
            }
        }
        return builder.build();
    }

    /**
     * DAO method for checking the connection to the Cassandra database.
     *
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(CassandraConnectionDTO dto) {
        Cluster cluster = connect(dto);
        try (Session session = cluster.connect()) {
            return PingStatusDTO.builder().status(!session.isClosed()).build();
        } catch (Exception e) {
            LOGGER.error("Could not establish Cassandra connection: {}", e.getMessage());
            return PingStatusDTO.builder()
                    .status(false)
                    .message(handleConnectError(e.getMessage()))
                    .build();
        }
    }
}
