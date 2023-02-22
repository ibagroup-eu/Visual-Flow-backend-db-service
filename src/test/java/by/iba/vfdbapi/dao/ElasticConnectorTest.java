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
import by.iba.vfdbapi.dto.dbs.ElasticConnectionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.Protocol;
import lombok.Cleanup;
import lombok.SneakyThrows;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticConnectorTest {

    private ElasticConnector connector;

    @BeforeEach
    void setUp() {
        connector = new ElasticConnector();
    }

    @SneakyThrows
    @Test
    void testPingWithSSL() {
        String jsonString = "{\n" +
                "        \"nodes\": \"my.host.rs\",\n" +
                "        \"port\": \"1111\",\n" +
                "        \"user\": \"usr\",\n" +
                "        \"password\": \"pass\",\n" +
                "        \"ssl\": \"true\",\n" +
                "        \"certData\": \"mycert\"" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        ElasticConnectionDTO dto = mapper.treeToValue(node, ElasticConnectionDTO.class);
        @Cleanup MockedStatic<CertCommon> certCommon = mockStatic(CertCommon.class);
        KeyStore keyStore = mock(KeyStore.class);
        certCommon.when(() -> CertCommon.loadTrustStoreFromCert(dto.getCertData())).thenReturn(keyStore);
        @Cleanup MockedStatic<SSLContexts> sslContexts = mockStatic(SSLContexts.class);
        SSLContextBuilder sslContextBuilder = mock(SSLContextBuilder.class, new BuilderAnswer());
        sslContexts.when(SSLContexts::custom).thenReturn(sslContextBuilder);
        SSLContext sslContext = mock(SSLContext.class);
        when(sslContextBuilder.build()).thenReturn(sslContext);
        RestClientBuilder restClientBuilder = mock(RestClientBuilder.class, new BuilderAnswer());
        @Cleanup MockedStatic<RestClient> restClient = mockStatic(RestClient.class);
        restClient.when(() -> RestClient.builder(
                new HttpHost(dto.getNodes(), dto.getPort(), Protocol.HTTPS.toString()))).thenReturn(restClientBuilder);
        @Cleanup MockedConstruction<RestHighLevelClient> restHighLevelClient = mockConstruction(RestHighLevelClient.class);
        @Cleanup MockedStatic<SSLFactory> sslFactory = mockStatic(SSLFactory.class);
        SSLFactory.Builder sslBuilder = mock(SSLFactory.Builder.class);
        sslFactory.when(SSLFactory::builder).thenReturn(sslBuilder);
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because NullPointerException has been thrown!");
        sslFactory.verify(SSLFactory::builder, never());
        restClient.verify(() -> RestClient.builder(
                new HttpHost(dto.getNodes(), dto.getPort(), Protocol.HTTPS.toString())), atMostOnce());
    }

    @SneakyThrows
    @Test
    void testPingWithoutSSL() {
        String jsonString = "{\n" +
                "        \"nodes\": \"my.host.rs\",\n" +
                "        \"port\": \"1111\",\n" +
                "        \"user\": \"usr\",\n" +
                "        \"password\": \"pass\",\n" +
                "        \"ssl\": \"false\",\n" +
                "        \"certData\": \"mycert\"" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        ElasticConnectionDTO dto = mapper.treeToValue(node, ElasticConnectionDTO.class);
        @Cleanup MockedStatic<CertCommon> certCommon = mockStatic(CertCommon.class);
        KeyStore keyStore = mock(KeyStore.class);
        certCommon.when(() -> CertCommon.loadTrustStoreFromCert(dto.getCertData())).thenReturn(keyStore);
        @Cleanup MockedStatic<SSLFactory> sslFactory = mockStatic(SSLFactory.class);
        SSLFactory sslFactoryNonStatic = mock(SSLFactory.class);
        SSLFactory.Builder sslFactoryBuilder = mock(SSLFactory.Builder.class, new BuilderAnswer());
        sslFactory.when(SSLFactory::builder).thenReturn(sslFactoryBuilder);
        when(sslFactoryBuilder.build()).thenReturn(sslFactoryNonStatic);
        RestClientBuilder restClientBuilder = mock(RestClientBuilder.class, new BuilderAnswer());
        @Cleanup MockedStatic<RestClient> restClient = mockStatic(RestClient.class);
        restClient.when(() -> RestClient.builder(
                new HttpHost(dto.getNodes(), dto.getPort(), Protocol.HTTPS.toString()))).thenReturn(restClientBuilder);
        @Cleanup MockedConstruction<RestHighLevelClient> restHighLevelClient = mockConstruction(RestHighLevelClient.class);
        @Cleanup MockedStatic<SSLContexts> sslContexts = mockStatic(SSLContexts.class);
        SSLContextBuilder sslContextBuilder = mock(SSLContextBuilder.class);
        sslContexts.when(SSLContexts::custom).thenReturn(sslContextBuilder);
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because NullPointerException has been thrown!");
        sslContexts.verify(SSLContexts::custom, never());
        restClient.verify(() -> RestClient.builder(
                new HttpHost(dto.getNodes(), dto.getPort(), Protocol.HTTPS.toString())), atMostOnce());
    }
}