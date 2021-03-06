# Spring Boot Docker

Many people are using containers to wrap their Spring Boot applications, and building containers is not a simple thing to do. This is a guide for developers of Spring Boot applications, and containers are not always a good abstraction for developers - they force you to learn about and think about very low level concerns - but you will on occasion be called on to create or use a container, so it pays to understand the building blocks. Here we aim to show you some of the choices you can make if you are faced with the prospect of needing to create your own container.


## A Basic Dockerfile
A Spring Boot application is easy to convert into an executable JAR file. All the Getting Started Guides do this, and every app that you download from Spring Initializr will have a build step to create an executable JAR. With Maven you ./mvnw install and with Gradle you ./gradlew build. A basic Dockerfile to run that JAR would then look like this, at the top level of your project:

``` docker
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

The JAR_FILE could be passed in as part of the docker command (it will be different for Maven and Gradle). E.g. for Maven:
``` bash
$ docker build --build-arg JAR_FILE=target/*.jar -t myorg/myapp .
```
and for Gradle:
``` bash
$ docker build --build-arg JAR_FILE=build/libs/*.jar -t myorg/myapp .
```

Of course, once you have chosen a build system, you don’t need the ARG - you can just hard code the jar location. E.g. for Maven:

``` docker
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Then we can simply build an image with
``` bash
$ docker build -t myorg/myapp .
```

and run it like this:
``` bash
$ docker run -p 8080:8080 myorg/myapp
```

If you want to poke around inside the image you can open a shell in it like this (the base image does not have bash):
``` bash
$ docker run -ti --entrypoint /bin/sh myorg/myapp
/ # ls
app.jar  dev      home     media    proc     run      srv      tmp      var
bin      etc      lib      mnt      root     sbin     sys      usr
/ #
```

> The alpine base container we used in the example does not have bash, so this is an ash shell. It has some of the features of bash but not all. 

If you have a running container and you want to peek into it, use docker exec you can do this:
``` bash
$ docker run --name myapp -ti --entrypoint /bin/sh myorg/myapp
$ docker exec -ti myapp /bin/sh
/ #
```
where myapp is the `--name` passed to the docker run command. If you didn’t use --name then docker assigns a mnemonic name which you can scrape from the output of docker ps. You could also use the sha identifier of the container instead of the name, also visible from docker ps.

### The Entry Point

The exec form of the Dockerfile ENTRYPOINT is used so that there is no shell wrapping the java process. The advantage is that the java process will respond to KILL signals sent to the container. In practice that means, for instance, that if you docker run your image locally, you can stop it with CTRL-C. If the command line gets a bit long you can extract it out into a shell script and COPY it into the image before you run it. Example:
``` docker
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY run.sh .
COPY target/*.jar app.jar
ENTRYPOINT ["run.sh"]
```
Remember to use exec java …​ to launch the java process (so it can handle the KILL signals):

run.sh
```bash
#!/bin/sh
exec java -jar /app.jar
```
Another interesting aspect of the entry point is whether or not you can inject environment variables into the java process at runtime. For example, suppose you want to have the option to add java command lline options at runtime. You might try to do this:

Dockerfile
``` docker
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","${JAVA_OPTS}","-jar","/app.jar"]
```
and
```bash
$ docker build -t myorg/myapp .
$ docker run -p 9000:9000 -e JAVA_OPTS=-Dserver.port=9000 myorg/myapp
```
This will fail because the ${} substitution requires a shell; the exec form doesn’t use a shell to launch the process, so the options will not be applied. You can get round that by moving the entry point to a script (like the run.sh example above), or by explicitly creating a shell in the entry point. For example:
Dockerfile
``` docker
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
```
You can then launch this app with
```bash
$ docker run -p 8080:8080 -e "JAVA_OPTS=-Ddebug -Xmx128m" myorg/myapp
...
2019-10-29 09:12:12.169 DEBUG 1 --- [           main] ConditionEvaluationReportLoggingListener :

============================
CONDITIONS EVALUATION REPORT
============================
...
```
(Showing parts of the full DEBUG output that is generated with -Ddebug by Spring Boot.)

Using an ENTRYPOINT with an explicit shell like the above means that you can pass environment variables into the java command, but so far you cannot also provide command line arguments to the Spring Boot application. This trick doesn’t work to run the app on port 9000:
```bash
$ docker run -p 9000:9000 myorg/myapp --server.port=9000

...
2019-10-29 09:20:19.718  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8080
```
The reason it didn’t work is because the docker command (the --server.port=9000 part) is passed to the entry point (sh), not to the java process which it launches. To fix that you need to add the command line from the CMD to the ENTRYPOINT:
```docker
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]
```

```bash
$ docker run -p 9000:9000 myorg/myapp --server.port=9000

...
2019-10-29 09:30:19.751  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 9000
```
Note the use of `${0}` for the "command" (in this case the first program argument) and `${@}` for the "command arguments" (the rest of the program arguments). If you use a script for the entry point, then you don’t need the `${0}` (that would be /app/run.sh in the example above). Example:
```bash
#!/bin/sh
exec java ${JAVA_OPTS} -jar /app.jar ${@}
```
The docker configuration is very simple so far, and the generated image is not very efficient. The docker image has a single filesystem layer with the fat jar in it, and every change we make to the application code changes that layer, which might be 10MB or more (even as much as 50MB for some apps). We can improve on that by splitting the JAR up into multiple layers.

## Smaller Images

Notice that the base image in the example above is openjdk:8-jdk-alpine. The alpine images are smaller than the standard openjdk library images from Dockerhub. There is no official alpine image for Java 11 yet (AdoptOpenJDK had one for a while but it no longer appears on their Dockerhub page). You can also save about 20MB in the base image by using the "jre" label instead of "jdk". Not all apps work with a JRE (as opposed to a JDK), but most do, and indeed some organizations enforce a rule that every app has to because of the risk of misuse of some of the JDK features (like compilation).

Another trick that could get you a smaller image is to use JLink, which is bundled with OpenJDK 11. JLink allows you to build a custom JRE distribution from a subset of modules in the full JDK, so you don’t need a JRE or JDK in the base image. In principle this would get you a smaller total image size than using the openjdk official docker images. In practice, you won’t (yet) be able to use the alpine base image with JDK 11, so your choice of base image will be limited and will probably result in a larger final image size. Also, a custom JRE in your own base image cannot be shared amongst other applications, since they would need different customizations. So you might have smaller images for all your applications, but they still take longer to start because they don’t benefit from caching the JRE layer.

That last point highlights a really important concern for image builders: the goal is not necessarily always going to be to build the smallest image possible. Smaller images are generally a good idea because they take less time to upload and download, but only if none of the layers in them are already cached. Image registries are quite sophisticated these days and you can easily lose the benefit of those features by trying to be clever with the image construction. If you use common base layers, the total size of an image is less of a concern, and will probably become even less of one as the registries and platforms evolve. Having said that, it is still important, and useful, to try and optimize the layers in our application image, but the goal should always be to put the fastest changing stuff in the highest layers, and to share as many of the large, lower layers as possible with other applications.

## A Better Dockerfile
A Spring Boot fat jar naturally has "layers" because of the way that the jar itself is packaged. If we unpack it first it will already be divided into external and internal dependencies. To do this in one step in the docker build, we need to unpack the jar first. For example (sticking with Maven, but the Gradle version is pretty similar):

```bash
$ mkdir target/dependency
$ (cd target/dependency; jar -xf ../*.jar)
$ docker build -t myorg/myapp .
```

with this Dockerfile

```docker
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]
```

There are now 3 layers, with all the application resources in the later 2 layers. If the application dependencies don’t change, then the first layer (from BOOT-INF/lib) will not change, so the build will be faster, and so will the startup of the container at runtime as long as the base layers are already cached.

> We used a hard-coded main application class hello.Application. This will probably be different for your application. You could parameterize it with another ARG if you wanted. You could also copy the Spring Boot fat JarLauncher into the image and use it to run the app - it would work and you wouldn’t need to specify the main class, but it would be a bit slower on startup.

## Tweaks
If you want to start your app as quickly as possible (most people do) there are some tweaks you might consider. Here are some ideas:

* Use the [spring-context-indexer](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-scanning-index) . It’s not going to add much for small apps, but every little helps.

* Don’t use [actuators](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready) if you can afford not to.

* Use Spring Boot 2.1 and Spring 5.1.

* Fix the location of the [Spring Boot config file(s)](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-application-property-files) with `spring.config.location` (command line argument or System property etc.).

* Switch off JMX - you probably don’t need it in a container - with `spring.jmx.enabled=false`

* Run the JVM with `-noverify`. Also consider `-XX:TieredStopAtLevel=1` (that will slow down the JIT later at the expense of the saved startup time).

* Use the container memory hints for Java 8: `-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap.` With Java 11 this is automatic by default.

Your app might not need a full CPU at runtime, but it will need multiple CPUs to start up as quickly as possible (at least 2, 4 are better). If you don’t mind a slower startup you could throttle the CPUs down below 4. If you are forced to start with less than 4 CPUs it might help to set `-Dspring.backgroundpreinitializer.ignore=true` since it prevents Spring Boot from creating a new thread that it probably won’t be able to use (this works with Spring Boot 2.1.0 and above).

### Multi-Stage Build
The Dockerfile above assumed that the fat JAR was already built on the command line. You can also do that step in docker using a multi-stage build, copying the result from one image to another. Example, using Maven:

```docker
FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]
```

The first image is labelled "build" and it is used to run Maven and build the fat jar, then unpack it. The unpacking could also be done by Maven or Gradle (this is the approach taken in the Getting Started Guide) - there really isn’t much difference, except that the build configuration would have to be edited and a plugin added.

Notice that the source code has been split into 4 layers. The later layers contain the build configuration and the source code for the app, and the earlier layers contain the build system itself (the Maven wrapper). This is a small optimization, and it also means that we don’t have to copy the target directory to a docker image, even a temporary one used for the build.

Every build where the source code changes will be slow because the Maven cache has to be re-created in the first <font color=Blue>RUN</font> section. But you have a completely standalone build that anyone can run to get your application running as long as they have docker. That can be quite useful in some environments, e.g. where you need to share your code with people who don’t know Java.

### Experimental Features

Docker 18.06 comes with some ["experimental" features](https://github.com/moby/buildkit/blob/master/frontend/dockerfile/docs/experimental.md) that includes a way to cache build dependencies. To switch them on you need a flag in the daemon (dockerd) and also an environment variable when you run the client, and then you can add a magic first line to your <font color=Blue>Dockerfile</font> :
`# syntax=docker/dockerfile:experimental`
and the RUN directive then accepts a new flag `--mount`. 

```docker
# syntax=docker/dockerfile:experimental
FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]
```

Then run it:
```bash
$ DOCKER_BUILDKIT=1 docker build -t myorg/myapp .
...
 => /bin/sh -c ./mvnw install -DskipTests              5.7s
 => exporting to image                                 0.0s
 => => exporting layers                                0.0s
 => => writing image sha256:3defa...
 => => naming to docker.io/myorg/myapp
```

With the experimental features you get a different output on the console, but you can see that a Maven build now only takes a few seconds instead of minutes, once the cache is warm.

The Gradle version of this Dockerfile configuration is very similar:

```docker
# syntax=docker/dockerfile:experimental
FROM openjdk:8-jdk-alpine AS build
WORKDIR /workspace/app

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]
```
## Security Aspects
Just as in classic VM-deployments, processes should not be run with root permissions. Instead the image should contain a non-root user that runs the app.

In a Dockerfile, this can be achieved by adding another layer that adds a (system) user and group, then set it as the current user (instead of the default, root):
```docker
FROM openjdk:8-jdk-alpine

RUN addgroup -S demo && adduser -S demo -G demo
USER demo

...
```
In case someone manages to break out of your app and run system commands inside the container, this will limit their capabilities (principle of least privilege).

> Some of the further Dockerfile commands only work as root, so maybe you have to move the USER command further down (e.g. if you plan to install more packages into the container, which only works as root).

> Other approaches, not using a Dockerfile, might be more amenable. For instance, in the buildpack approach described later, most implementations will use a non-root user by default.

Another consideration is that the full JDK is probably not needed by most apps at runtime, so we can safely switch to the JRE base image, once we have a multi-stage build. So in the multi-stage build above we can use: `FROM openjdk:8-jre-alpine`.