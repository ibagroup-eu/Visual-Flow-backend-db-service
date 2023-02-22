# About Visual Flow

Visual Flow is an ETL tool designed for effective data manipulation via convenient and user-friendly interface. The tool has the following capabilities:

- Can integrate data from heterogeneous sources:
    - DB2;
    - IBM COS;
    - AWS S3;
    - Elastic Search;
    - PostgreSQL;
    - MySQL/Maria;
    - MSSQL;
    - Oracle;
    - Cassandra;
    - Mongo;
    - Redis;
    - Redshift.
- Leverage direct connectivity to enterprise applications as sources and targets;
- Perform data processing and transformation;
- Run custom code;
- Leverage metadata for analysis and maintenance.

Visual Flow application is divided into the following repositories:

- [Visual-Flow-frontend](https://github.com/ibagomel/Visual-Flow-frontend)
- [Visual-Flow-backend](https://github.com/ibagomel/Visual-Flow-backend)
- _**Visual-Flow-backend-db-service**_ (current)
- [Visual-Flow-jobs](https://github.com/ibagomel/Visual-Flow-jobs)
- [Visual-Flow-deploy](https://github.com/ibagomel/Visual-Flow-deploy)

## Visual Flow Backend DB Service

Visual Flow Backend DB Service is the REST API app, which allows to interact with the databases listed 
above using the following operations:
- Ping connection.

To interact with the microservice, you should use corresponding endpoints in 
[the main microservice](https://github.com/ibagomel/Visual-Flow-backend).

## Development

[Check the official guide](./DEVELOPMENT.md).

## Contribution

[Check the official guide](https://github.com/ibagomel/Visual-Flow/blob/main/CONTRIBUTING.md).

## License

Visual Flow is an open-source software licensed under the [Apache-2.0 license](./LICENSE).