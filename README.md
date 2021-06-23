# low-version Spring Boot(2.1.3RELEASE) integrate Elasticsearch Demo

As you see, this is a demo that teaches you how to integrate Elasticsearch with a lower version of Spring Boot(2.1.3RELEASE) 

- elasticsearch version:6.4.3

## Usage

1. start Elasticsearch service
2. use docker start MySQL service
     ```
     docker run --name my-mysql -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=test -p 3306:3306 -d mysql
    ```
3. use flyway migrate to init db and data

    ```
    mvn flyway:clean flyway:migrate
    ```
