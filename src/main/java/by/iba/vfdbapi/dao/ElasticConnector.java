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
import by.iba.vfdbapi.dto.dbs.ElasticConnectionDTO;
import com.ibm.cloud.objectstorage.Protocol;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Repository;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;

/**
 * Repository-class for interaction with ElasticSearch.
 */
@Repository
@Slf4j
public class ElasticConnector {

    /**
     * Secondary method to establish a connection to the ES database and receive it.
     * @param dto an object containing data to connect to the database.
     * @return database connection object.
     */
    private RestHighLevelClient connect(ElasticConnectionDTO dto) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if(dto.getUser() != null && dto.getPassword() != null) {
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(dto.getUser(), dto.getPassword()));
        }
        RestClientBuilder builder;
        if(Boolean.TRUE.equals(dto.getSsl())) {
            try {
                KeyStore trustStore = CertCommon.loadTrustStoreFromCert(dto.getCertData());
                SSLContext sslContext = SSLContexts.custom()
                        .loadTrustMaterial(trustStore, null).build();
                builder = RestClient.builder(new HttpHost(dto.getNodes(), dto.getPort(), Protocol.HTTPS.toString()))
                        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setSSLContext(sslContext));
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException |
                     IOException | CertificateException | IllegalArgumentException e) {
                LOGGER.error("Could not establish ES connection: {}", e.getMessage());
                return null;
            }
        } else {
            SSLFactory sslFactory = SSLFactory.builder()
                    .withUnsafeTrustMaterial()
                    .withUnsafeHostnameVerifier()
                    .build();
            builder = RestClient.builder(new HttpHost(dto.getNodes(), dto.getPort(), Protocol.HTTPS.toString()))
                    .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider)
                                    .setSSLContext(sslFactory.getSslContext())
                                    .setSSLHostnameVerifier(sslFactory.getHostnameVerifier()));
        }
        return new RestHighLevelClient(builder);
    }

    /**
     * DAO method for checking the connection to the ElasticSearch database.
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(ElasticConnectionDTO dto) {
        try(RestHighLevelClient elasticsearchClient = connect(dto)) {
            return PingStatusDTO.builder().status(
                    Objects.requireNonNull(elasticsearchClient).ping(RequestOptions.DEFAULT)).build();
        } catch (IOException | NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
            return PingStatusDTO.builder().status(false).build();
        }
    }
}
