# About Visual Flow

Visual Flow is an ETL tool designed for effective data manipulation via convenient and user-friendly interface. The tool has the following capabilities:

- Can integrate data from heterogeneous sources:
  - AWS S3
  - Cassandra
  - Click House
  - DB2
  - Dataframe (for reading)
  - Elastic Search
  - IBM COS
  - Kafka
  - Local File
  - MS SQL
  - Mongo
  - MySQL/Maria
  - Oracle
  - PostgreSQL
  - Redis
  - Redshift
- Leverage direct connectivity to enterprise applications as sources and targets;
- Perform data processing and transformation;
- Run custom code;
- Leverage metadata for analysis and maintenance.

Visual Flow application is divided into the following repositories:

- [Visual-Flow-frontend](https://github.com/ibagroup-eu/Visual-Flow-frontend)
- [Visual-Flow-backend](https://github.com/ibagroup-eu/Visual-Flow-backend)
- [Visual-Flow-jobs](https://github.com/ibagroup-eu/Visual-Flow-jobs)
- [Visual-Flow-deploy](https://github.com/ibagroup-eu/Visual-Flow-deploy)
- _**Visual-Flow-backend-db-service**_ (current)
- [Visual-Flow-backend-history-service](https://github.com/ibagroup-eu/Visual-Flow-backend-history-service)

## Visual Flow Backend DB Service

Visual Flow Backend DB Service is the REST API app, which allows to interact with the databases listed 
above using the following operations:
- Ping connection.

To interact with the microservice, you should use corresponding endpoints in 
[the main microservice](https://github.com/ibagroup-eu/Visual-Flow-backend).

## Development

[Check the official guide](./DEVELOPMENT.md).

## Contribution

[Check the official guide](https://github.com/ibagroup-eu/Visual-Flow/blob/main/CONTRIBUTING.md).

## License

Visual Flow is an open-source software licensed under the [Apache-2.0 license](./LICENSE).
