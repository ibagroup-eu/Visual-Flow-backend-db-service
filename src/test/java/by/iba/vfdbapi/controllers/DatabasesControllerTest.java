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
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabasesControllerTest {

    @Mock
    private DatabasesService dbService;
    private DatabasesController controller;

    @BeforeEach
    void setUp() {
        controller = new DatabasesController(dbService);
    }

    @Test
    void testPing() {
        ConnectDto dto = ConnectDto.builder().key("test").value(new TextNode("node")).build();
        when(dbService.ping(dto)).thenReturn(PingStatusDTO.builder().status(true).build());
        PingStatusDTO actual = controller.ping(dto);
        assertTrue(actual.isStatus(), "Ping(ConnectDto) should return true!");
        verify(dbService).ping(dto);
    }
}