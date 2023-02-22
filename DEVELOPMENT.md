# Set up development environment

## Requirements

1) Make sure that you have the following software installed

| Software       | Version                    | Purpose                                              |
|----------------|----------------------------|------------------------------------------------------|
| Java           | 11+                        | To work with this API as it has been written in Java |
| Maven          | 3+                         | To manage project dependencies                       |                    |

2) Following environment variables have to be set

| Name           | Explanation                                                |
|----------------|------------------------------------------------------------|
| BASE_PATH      | The sub path after hostname on which API will be available |   |

## SSL configuration

This API doesn't require SSL certificate, and a private key packed up in p12 store.

## Application configuration
Now, when you are done with everything described above, it's time to create application.yaml
Take a look at [application.yaml.example](./src/main/resources/application.yaml.example). It will serve as a template to your future configuration.

1. Create a new application.yaml file inside src/main/resources/ and fill it with the contents from application.yaml.example.
2. Change and fill values within configuration. Existing comments can help to understand what is required.

## How to run and use

1. Build and run the application.
2. Make sure there is access to resources by making, for example, the following request:
    - Try to ping:
      ```bash
      curl --location --request POST '{host}:{port}/${BASE_PATH}' --header 'Content-Type: application/json' \
      --data-raw '{"key": "key", "value": {}}'
      ```
      should return false.

## Notice
When configuring the Redis database, keep in mind that you have the current version of the Redis server, since versions 
older than 2.6 have a vulnerability that is solved only by configuring the server: **CVE-2021-32626**

_Redis is an open source, in-memory database that persists on disk. In affected versions specially crafted Lua scripts 
executing in Redis can cause the heap-based Lua stack to be overflowed, due to incomplete checks for this condition. 
This can result with heap corruption and potentially remote code execution. This problem exists in all versions of 
Redis with Lua scripting support, starting from 2.6. The problem is fixed in versions 6.2.6, 6.0.16 and 5.0.14. 
For users unable to update an additional workaround to mitigate the problem without patching the redis-server 
executable is to prevent users from executing Lua scripts. This can be done using ACL to restrict EVAL and 
EVALSHA commands._


### Swagger
Visual Flow comes with preconfigured Swagger.

You should be able to access swagger in main backend microservice, section "Databases API".
