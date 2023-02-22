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
package by.iba.vfdbapi.dto.dbs;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data Transfer Object Class, contains MongoDB connection params.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MongoConnectionDTO extends ConnectionDTO {
    private String host;
    private Integer port;
    private String database;
    private String user;
    private String password;
    private Boolean ssl;
}
