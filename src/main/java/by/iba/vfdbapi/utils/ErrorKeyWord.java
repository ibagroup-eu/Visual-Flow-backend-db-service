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
package by.iba.vfdbapi.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Enum, contains error messages keywords.
 * Note, that more specific categories should be placed before other categories.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorKeyWord {

    REGION(List.of("region"),
            "Oops! It seems the region is incorrectly specified for the COS authentication."),

    CERTIFICATE(List.of(" base64 ", " cert ", " certificate"),
            "Oops! It seems there are problems with certificate. Please, check if it is valid or it " +
                    "doesn't contain illegal characters."),

    DATABASE(List.of(" db ", " database"),
            "Oops! It seems there are problems with the specified database. Please, check if its name " +
                    "is set correctly."),

    AUTH(List.of(" auth", " unauth", "pass", " log", " credent", " id ", " user", " 403"),
            "Oops! It seems there are problems with authentication. Please, check your credentials."),

    DEFAULT(List.of(""),
            "Oops! It seems the host is currently unavailable. Please, check if the host is set correctly.");

    private final List<String> keyWords;
    private final String message;

}
