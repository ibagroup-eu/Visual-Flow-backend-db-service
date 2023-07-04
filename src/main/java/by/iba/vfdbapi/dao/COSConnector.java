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
import by.iba.vfdbapi.dto.dbs.CosConnectionDTO;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.Protocol;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.regions.Regions;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static by.iba.vfdbapi.utils.ErrorMessageUtil.handleConnectError;

/**
 * Repository-class for interaction with IBM COS.
 */
@Slf4j
@Repository
public class COSConnector {

    private static final String HMAC_AUTH = "HMAC";

    /**
     * Secondary method to establish a connection to the COS database and receive it.
     * Uses 2 COS auth types: {@link #HMAC_AUTH HMAC} and IAM, depending on
     * {@link CosConnectionDTO#getAuthType() authType value}.
     *
     * @param dto an object containing data to connect to the database.
     * @return database connection object.
     */
    private AmazonS3 connect(CosConnectionDTO dto) {
        AWSCredentials credentials;
        if (dto.getAuthType().equals(HMAC_AUTH)) {
            credentials = new BasicAWSCredentials(
                    dto.getAccessKey(),
                    dto.getSecretKey()
            );
        } else {
            credentials = new BasicIBMOAuthCredentials(
                    dto.getIamApiKey(),
                    dto.getIamServiceId()
            );
        }
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dto.getEndpoint(),
                        Regions.DEFAULT_REGION.getName()))
                .withClientConfiguration(new ClientConfiguration()
                        .withProtocol(Protocol.HTTP)
                        .withClientExecutionTimeout(1000))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    /**
     * DAO method for checking the connection to the IBM COS database.
     *
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    public PingStatusDTO ping(CosConnectionDTO dto) {
        try {
            return PingStatusDTO.builder().status(!connect(dto).listBuckets().isEmpty()).build();
        } catch (Exception e) {
            LOGGER.error("An error has been occurred during connecting to COS: {}", e.getMessage());
            return PingStatusDTO.builder()
                    .status(false)
                    .message(handleConnectError(e.getMessage()))
                    .build();
        }
    }
}
