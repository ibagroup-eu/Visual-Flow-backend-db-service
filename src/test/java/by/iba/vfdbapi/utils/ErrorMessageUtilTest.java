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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMessageUtilTest {

    @Test
    void testHandleDB2ErrorMessages() {
        String failedAuth = "An error has been occurred during connecting to db2: [jcc][t4][2013][11249][4.29.24] " +
                "Connection authorization failure occurred.  Reason: User ID or Password invalid. ERRORCODE=-4214, " +
                "SQLSTATE=28000";
        String failedDatabase = "An error has been occurred during connecting to db2: [jcc][t4][2057][11264][4.29.24] " +
                "The application server rejected establishment of the connection. An attempt was made to access a " +
                "database, EXAMPLE1, which was either not found or does not support transactions. ERRORCODE=-4499, " +
                "SQLSTATE=08004";
        String failedEndpoint = "An error has been occurred during connecting to db2: [jcc][t4][10380][11951][4.29.24] " +
                "Required property 'test' is unknown host. ERRORCODE=-4222, SQLSTATE=08001";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DATABASE.getMessage(), ErrorMessageUtil.handleConnectError(failedDatabase));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleMSSQLErrorMessages() {
        String failedAuth = "An error has been occurred during connecting to mssql: Login failed for user 'sfa'. " +
                "ClientConnectionId:742273a4-a3ca-48f0-997e-541e98ddaad1";
        String failedDatabase = "An error has been occurred during connecting to mssql: Cannot open database \"masterf\" " +
                "requested by the login. The login failed. ClientConnectionId:a52d1b53-d344-4120-bd84-2ea90aabe82e";
        String failedEndpoint = "An error has been occurred during connecting to mssql: The TCP/IP connection to the host " +
                "test, port 31344 has failed. Error: test. Verify the connection properties. Make sure that an instance " +
                "of SQL Server is running on the host and accepting TCP/IP connections at the port. Make sure that TCP " +
                "connections to the port are not blocked by a firewall.";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DATABASE.getMessage(), ErrorMessageUtil.handleConnectError(failedDatabase));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleMySQLErrorMessages() {
        String failedAuth = "An error has been occurred during connecting to mysql: Access denied for user " +
                "'MYSQL_USEkR'@'10.129.2.1' (using password: YES)";
        String failedDatabase = "An error has been occurred during connecting to mysql: Access denied for user " +
                "'MYSQL_USER'@'%' to database 'MYSQL_DATABAfSE'";
        String failedEndpoint = "An error has been occurred during connecting to mysql: Communications link failure";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DATABASE.getMessage(), ErrorMessageUtil.handleConnectError(failedDatabase));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandlePostgresErrorMessages() {
        String failedAuth = "An error has been occurred during connecting to postgresql: FATAL: password authentication " +
                "failed for user \"postgvres\"";
        String failedDatabase = "An error has been occurred during connecting to postgresql: FATAL: database \"examplne\" " +
                "does not exist";
        String failedEndpoint = "An error has been occurred during connecting to postgresql: The connection attempt failed.";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DATABASE.getMessage(), ErrorMessageUtil.handleConnectError(failedDatabase));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleOracleErrorMessages() {
        String failedAuth = "An error has been occurred during connecting to oracle: ORA-01017: invalid username/password; " +
                "logon denied";
        String failedDatabase = "An error has been occurred during connecting to oracle: Listener refused the connection " +
                "with the following error: ORA-12514, TNS:listener does not currently know of service requested in connect descriptor" +
                "  (CONNECTION_ID=9WcLzbuhTmqw+hMITMcw9g==)";
        String failedEndpoint = "An error has been occurred during connecting to oracle: IO Error: Unknown host specified  " +
                "(CONNECTION_ID=pheWNgDDTv2vIlQXWWs3qA==)";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedDatabase));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleRedshiftErrorMessages() {
        String failedEndpoint = "An error has been occurred during connecting to redshift-jdbc: The connection attempt failed.";

        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleESErrorMessages() {
        String failedAuth = "method [HEAD], host [https://host], URI [/], status line [HTTP/1.1 401 Unauthorized]";
        String failedCert = "Illegal base64 character 5f";
        String failedEndpoint = "The host is unknown.";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.CERTIFICATE.getMessage(), ErrorMessageUtil.handleConnectError(failedCert));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleMongoErrorMessages() {
        String failedAuth = "Exception authenticating MongoCredential{mechanism=SCRAM-SHA-1, userName='mongousesr', " +
                "source='example', password=<hidden>, mechanismProperties=<hidden>}";
        String failedEndpoint = "Timed out after 3000 ms while waiting to connect. Client view of cluster state is " +
                "{type=UNKNOWN, servers=[{address=host:31017, type=UNKNOWN, state=CONNECTING, " +
                "exception={com.mongodb.MongoSocketException: host}, caused by " +
                "{java.net.UnknownHostException: host}}]";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleRedisErrorMessages() {
        String failedAuth = "WRONGPASS invalid username-password pair or user is disabled.";
        String failedEndpoint = "Caused by: java.net.UnknownHostException: byg";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleCassandraErrorMessages() {
        String failedEndpoint = "Could not establish Cassandra connection: All host(s) tried for query failed (tried: " +
                "host/10.25.0.246:31142 (com.datastax.driver.core.exceptions.TransportException: [host/10.25.0.246:31142] " +
                "Error writing))";

        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleS3ErrorMessages() {
        String failedAuth = "An error has been occurred during connecting to AWS S3: null (Service: Amazon S3; " +
                "Status Code: 403; Error Code: SignatureDoesNotMatch; Request ID: " +
                "tx00000a7d38b25c35d22ac-00644be453-424787b-gml-okd-object; S3 Extended Request ID: " +
                "424787b-gml-okd-object-gml-okd-object; Proxy: null)";
        String failedEndpoint = "An error has been occurred during connecting to AWS S3: Unable to execute HTTP request: host";

        assertEquals(ErrorKeyWord.AUTH.getMessage(), ErrorMessageUtil.handleConnectError(failedAuth));
        assertEquals(ErrorKeyWord.DEFAULT.getMessage(), ErrorMessageUtil.handleConnectError(failedEndpoint));
    }

    @Test
    void testHandleCOSErrorMessages() {
        String failedRegion = "An error has been occurred during connecting to COS: The authorization header is malformed; " +
                "the region 'us-west-2' is wrong; expecting 'us-east-1' (Service: Amazon S3; Status Code: 400; " +
                "Error Code: AuthorizationHeaderMalformed; Request ID: 7PXXBJC9XGK1SNTN; S3 Extended Request ID: " +
                "ItrGZlm0ZNfaKZZezK9XdMXqnxpYEXq2U9xRSQJSFduYIZjF4FpYudxuA109onxvNgOIkQI0D28=; Proxy: null)";

        assertEquals(ErrorKeyWord.REGION.getMessage(), ErrorMessageUtil.handleConnectError(failedRegion));
    }
}
