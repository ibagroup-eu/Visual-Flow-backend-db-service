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
import by.iba.vfdbapi.dao.common.CertCommon;
import by.iba.vfdbapi.dto.PingStatusDTO;
import by.iba.vfdbapi.dto.dbs.CassandraConnectionDTO;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CassandraConnectorTest {

    private CassandraConnector connector;

    @BeforeEach
    void setUp() {
        connector = new CassandraConnector();
    }

    @SneakyThrows
    @Test
    void testPing() {
        String jsonString = "{\n" +
                "        \"host\": \"my.host.rs\",\n" +
                "        \"port\": \"1111\",\n" +
                "        \"username\": \"usr\",\n" +
                "        \"password\": \"pass\",\n" +
                "        \"keyspace\": \"test\",\n" +
                "        \"ssl\": \"true\",\n" +
                "        \"certData\": \"mycert\"" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        CassandraConnectionDTO dto = mapper.treeToValue(node, CassandraConnectionDTO.class);
        MockedStatic<Cluster> cluster = mockStatic(Cluster.class);
        Cluster clusterObj = mock(Cluster.class);
        Cluster.Builder builder = mock(Cluster.Builder.class, new BuilderAnswer());
        cluster.when(Cluster::builder).thenReturn(builder);
        when(builder.build()).thenReturn(clusterObj);
        @Cleanup MockedStatic<CertCommon> certCommon = mockStatic(CertCommon.class);
        certCommon.when(() -> CertCommon.loadTrustStoreFromCert(dto.getCertData())).thenThrow(CertificateException.class);
        Session session = mock(Session.class);
        when(clusterObj.connect()).thenReturn(session);
        when(session.isClosed()).thenThrow(new NoHostAvailableException(Map.of(InetSocketAddress.createUnresolved("host", 1994),
                new Exception("Unable to connect"))));
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because NoHostAvailableException has been thrown!");
        verify(clusterObj).connect();
        verify(session).isClosed();
    }
}