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
package by.iba.vfdbapi.dto;

import by.iba.vfdbapi.dto.dbs.ConnectionDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration contains constant to determinate database type connection.
 */
@Getter
@RequiredArgsConstructor
public enum Storages {
    DB2("db2"),
    MSSQL("mssql"),
    MYSQL("mysql"),
    POSTGRES("postgresql"),
    ORACLE("oracle"),
    REDSHIFT("redshift"),
    REDSHIFT_JDBC("redshift-jdbc"),
    ELASTIC("elastic"),
    MONGO("mongo"),
    REDIS("redis"),
    CASSANDRA("cassandra"),
    S3("s3"),
    COS("cos"),
    CLICK_HOUSE("clickhouse"),
    UNKNOWN("unknown");

    private final String id;

    /**
     * Method to determinate storage type from {@link ConnectionDTO#getStorage() DTO storage} field value.
     * @param dtoStorage storage type string, from which the database type should be determinated.
     * @return constant depending on DB type.
     */
    public static Storages findStorage(String dtoStorage) {
        for(Storages storage: values()) {
            if(dtoStorage.equals(storage.id)) {
                return storage;
            }
        }
        return UNKNOWN;
    }
}
