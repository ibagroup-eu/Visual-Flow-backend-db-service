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
package by.iba.vfdbapi.controllers;

import by.iba.vfdbapi.dto.ConnectDto;
import by.iba.vfdbapi.dto.PingStatusDTO;
import by.iba.vfdbapi.services.DatabasesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main microservice controller. Has common methods for manipulations with all databases.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DatabasesController {

    private final DatabasesService service;

    /**
     * Method for checking the connection to the database.
     * @param dto an object containing data to connect to the database.
     * @return true, if a connection to the database has been established, otherwise - false.
     */
    @PostMapping
    public PingStatusDTO ping(@RequestBody ConnectDto dto) {
        LOGGER.debug("In DB-SERVICE: Retrieving ping info for {} connection...", dto.getKey());
        return service.ping(dto);
    }
}
