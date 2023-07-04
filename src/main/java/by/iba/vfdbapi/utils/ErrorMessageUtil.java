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

import lombok.experimental.UtilityClass;

import java.util.Locale;

/**
 * Utility class, designed to handle database driver errors.
 */
@UtilityClass
public class ErrorMessageUtil {

    /**
     * Method for handling DB driver's error messages.
     * Finds the closest semantically error message by keywords and sends it instead of the driver message.
     *
     * @param errorMessage is a driver error message.
     * @return the closest semantically error message.
     */
    public static String handleConnectError(String errorMessage) {
        for (ErrorKeyWord category : ErrorKeyWord.values()) {
            for (String key : category.getKeyWords()) {
                if (errorMessage.toLowerCase(Locale.getDefault()).contains(key)) {
                    return category.getMessage();
                }
            }
        }
        return ErrorKeyWord.DEFAULT.getMessage();
    }
}
