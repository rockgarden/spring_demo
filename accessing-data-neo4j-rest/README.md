This guide walks you through the process of using Spring Data Neo4j to build an application that stores data in and retrieves it from Neo4j, a graph-based database.

use Neo4j’s NoSQL graph-based data store to build an embedded Neo4j server, store entities and relationships, and develop queries.

## Standing up a Neo4j Server
Before you can build this application, you need to set up a Neo4j server.

Neo4j has an open source server you can install for free.

On a Mac that has Homebrew installed, run the following command:

``` bash
$ brew install neo4j
```
For other options, visit https://neo4j.com/download/community-edition/.

Once installed, launch it with its default settings by running the following command:
``` bash
$ neo4j start
Starting Neo4j.
Started neo4j (pid 96416). By default, it is available at http://localhost:7474/
There may be a short delay until the server is ready.
See /usr/local/Cellar/neo4j/3.0.6/libexec/logs/neo4j.log for current status.
```
By default, Neo4j has a username and password of neo4j and neo4j. However, it requires that the new account password be changed. To do so, run the following command:
``` bash
curl -v -u neo4j:neo4j POST localhost:7474/user/neo4j/password -H "Content-type:application/json" -d "{\"password\":\"secret\"}"
```
This changes the password from neo4j to secret — something to NOT do in production! With that step completed, you should be ready to run the rest of this guide.

## Define a Simple Entity
Neo4j captures entities and their relationships, with both aspects being of equal importance. Imagine you are modeling a system where you store a record for each person. However, you also want to track a person’s co-workers (teammates in this example). With Spring Data Neo4j, you can capture all that with some simple annotations.

## Create Simple Queries
Spring Data Neo4j is focused on storing data in Neo4j. But it inherits functionality from the Spring Data Commons project, including the ability to derive queries. Essentially, you need not learn the query language of Neo4j. Instead, you can write a handful of methods and let the queries be written for you.

To see how this works, create an interface that queries Person nodes (PersonRepository.java). 

PersonRepository extends the CrudRepository interface and plugs in the type on which it operates: Person. This interface comes with many operations, including standard CRUD (create, read, update, and delete) operations.

But you can define other queries by declaring their method signatures. In this case, you added findByName, which seeks nodes of type Person and finds the one that matches on name. You also have findByTeammatesName, which looks for a Person node, drills into each entry of the teammates field, and matches based on the teammate’s name.

## Permissions to Access Neo4j
Neo4j Community Edition requires credentials to access it. You can configure these credential by setting a couple of properties (in src/main/resources/application.properties), as the following listing shows:

spring.data.neo4j.username=neo4j
spring.data.neo4j.password=secret
This includes the default username (neo4j) and the newly set password we picked earlier (secret).

Do NOT store real credentials in your source repository. Instead, configure them in your runtime using Spring Boot’s property overrides.

## Create an Application Class
Spring Boot automatically handles those repositories as long as they are included in the same package (or a sub-package) of your @SpringBootApplication class. For more control over the registration process, you can use the @EnableNeo4jRepositories annotation.

By default, @EnableNeo4jRepositories scans the current package for any interfaces that extend one of Spring Data’s repository interfaces. You can use its basePackageClasses=MyRepository.class to safely tell Spring Data Neo4j to scan a different root package by type if your project layout has multiple projects and it does not find your repositories.
Logging output is displayed. The service should be up and running within a few seconds.

Now autowire the instance of PersonRepository that you defined earlier. Spring Data Neo4j dynamically implements that interface and plugs in the needed query code to meet the interface’s obligations.

The main method uses Spring Boot’s SpringApplication.run() to launch the application and invoke the CommandLineRunner that builds the relationships.

In this case, you create three local Person instances: Greg, Roy, and Craig. Initially, they only exist in memory. Note that no one is a teammate of anyone (yet).

At first, you find Greg, indicate that he works with Roy and Craig, and then persist him again. Remember, the teammate relationship was marked as UNDIRECTED (that is, bidirectional). That means that Roy and Craig have been updated as well.

That is why when you need to update Roy. It is critical that you fetch that record from Neo4j first. You need the latest status on Roy’s teammates before adding Craig to the list.

Why is there no code that fetches Craig and adds any relationships? Because you already have it! Greg earlier tagged Craig as a teammate, and so did Roy. That means there is no need to update Craig’s relationships again. You can see it as you iterate over each team member and print their information to the console.

Finally, check out that other query where you look backwards, answering the question of "Who works with whom?", see the AccessingDataNeo4jApplication.java