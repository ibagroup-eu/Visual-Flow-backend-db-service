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

import by.iba.vfdbapi.dao.answers.AWSBuilderAnswer;
import by.iba.vfdbapi.dto.PingStatusDTO;
import by.iba.vfdbapi.dto.dbs.AmazonS3DTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.auth.AnonymousAWSCredentials;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmazonS3ConnectorTest {

    private AmazonS3Connector connector;

    @BeforeEach
    void setUp() {
        connector = new AmazonS3Connector();
    }

    @SneakyThrows
    @Test
    void testPingNonAnonymous() {
        String jsonString = "{\n" +
                "        \"endpoint\": \"my.host.rs\",\n" +
                "        \"anonymousAccess\": false,\n" +
                "        \"accessKey\": \"access\",\n" +
                "        \"secretKey\": \"secret\"" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        AmazonS3DTO dto = mapper.treeToValue(node, AmazonS3DTO.class);
        @Cleanup MockedConstruction<BasicAWSCredentials> basicAWSCredentials =
                mockConstruction(BasicAWSCredentials.class);
        @Cleanup MockedConstruction<AnonymousAWSCredentials> anonymousAWSCredentials =
                mockConstruction(AnonymousAWSCredentials.class);
        AmazonS3ClientBuilder builder = mock(AmazonS3ClientBuilder.class, new AWSBuilderAnswer());
        @Cleanup MockedStatic<AmazonS3ClientBuilder> staticBuilder = mockStatic(AmazonS3ClientBuilder.class);
        staticBuilder.when(AmazonS3ClientBuilder::standard).thenReturn(builder);
        AmazonS3 s3 = mock(AmazonS3.class);
        when(builder.build()).thenReturn(s3);
        when(s3.listBuckets()).thenReturn(Collections.emptyList());
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because bucket list is empty!");
        verify(builder, times(1)).build();
        assertEquals(1, basicAWSCredentials.constructed().size(), "BasicAWSCredentials should" +
                "be constructed because anonymousAccess is false.");
        assertEquals(0, anonymousAWSCredentials.constructed().size(), "AnonymousAWSCredentials " +
                "should not be constructed because anonymousAccess is false.");
    }

    @SneakyThrows
    @Test
    void testPingAnonymousWithException() {
        String jsonString = "{\n" +
                "        \"endpoint\": \"my.host.rs\",\n" +
                "        \"anonymousAccess\": true,\n" +
                "        \"accessKey\": \"access\",\n" +
                "        \"secretKey\": \"secret\"" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        AmazonS3DTO dto = mapper.treeToValue(node, AmazonS3DTO.class);
        @Cleanup MockedConstruction<BasicAWSCredentials> basicAWSCredentials =
                mockConstruction(BasicAWSCredentials.class);
        @Cleanup MockedConstruction<AnonymousAWSCredentials> anonymousAWSCredentials =
                mockConstruction(AnonymousAWSCredentials.class);
        AmazonS3ClientBuilder builder = mock(AmazonS3ClientBuilder.class, new AWSBuilderAnswer());
        @Cleanup MockedStatic<AmazonS3ClientBuilder> staticBuilder = mockStatic(AmazonS3ClientBuilder.class);
        staticBuilder.when(AmazonS3ClientBuilder::standard).thenReturn(builder);
        when(builder.build()).thenThrow(new SdkClientException("Unable to connect to S3"));
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because SdkClientException has been thrown!");
        verify(builder, times(1)).build();
        assertEquals(0, basicAWSCredentials.constructed().size(), "BasicAWSCredentials should not" +
                "be constructed because anonymousAccess is true.");
        assertEquals(1, anonymousAWSCredentials.constructed().size(), "AnonymousAWSCredentials " +
                "should be constructed because anonymousAccess is true.");
    }
}
