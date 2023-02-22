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
package by.iba.vfdbapi;

import by.iba.vfdbapi.controllers.DatabasesController;
import by.iba.vfdbapi.dao.*;
import by.iba.vfdbapi.services.DatabasesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class VfDBApiApplicationTest {

    @Autowired
    private DatabasesController controller;
    @Autowired
    private DatabasesService service;
    @Autowired
    private JDBCConnector jdbc;
    @Autowired
    private MongoConnector mongo;
    @Autowired
    private ElasticConnector elastic;
    @Autowired
    private CassandraConnector cassandra;
    @Autowired
    private RedisConnector redis;
    @Autowired
    private RedShiftConnector redShift;
    @Autowired
    private COSConnector cos;
    @Autowired
    private AmazonS3Connector s3;

    @Test
    void testContextLoads() {
        assertNotNull(controller, "DatabasesController should not be null!");
        assertNotNull(service, "DatabasesService should not be null!");
        assertNotNull(jdbc, "JDBCConnector should not be null!");
        assertNotNull(mongo, "MongoConnector should not be null!");
        assertNotNull(elastic, "ElasticConnector should not be null!");
        assertNotNull(cassandra, "CassandraConnector should not be null!");
        assertNotNull(redis, "RedisConnector should not be null!");
        assertNotNull(redShift, "RedShiftConnector should not be null!");
        assertNotNull(cos, "COSConnector should not be null!");
        assertNotNull(s3, "AmazonS3Connector should not be null!");
    }
}
