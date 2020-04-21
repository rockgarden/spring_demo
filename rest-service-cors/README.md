# Enabling Cross Origin Requests for a RESTful Web Service

You can enable cross-origin resource sharing (CORS) from either in individual controllers or globally. The following topics describe how to do so:
* Controller Method CORS Configuration
* Global CORS configuration

## Controller Method CORS Configuration

So that the RESTful web service will include CORS access control headers in its response, you have to add a @CrossOrigin annotation to the handler method.

This @CrossOrigin annotation enables cross-origin resource sharing only for this specific method. By default, its allows all origins, all headers, and the HTTP methods specified in the @RequestMapping annotation.

You can customize this behavior by specifying the value of one of the following annotation attributes:

* origins

* methods

* allowedHeaders

* exposedHeaders

* allowCredentials

* maxAge.

You can also add the @CrossOrigin annotation at the controller class level as well, to enable CORS on all handler methods of this class.

### Global CORS configuration

In addition (or as an alternative) to fine-grained annotation-based configuration, you can define some global CORS configuration as well. This is similar to using a Filter but can be declared within Spring MVC and combined with fine-grained @CrossOrigin configuration. By default, all origins and GET, HEAD, and POST methods are allowed.

Add CORS mapping in the application class.

## Build an executable JAR

You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar so makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

If you use Gradle, you can run the application by using `./gradlew bootRun`. Alternatively, you can build the JAR file by using `./gradlew build` and then run the JAR file, as follows:

java -jar build/libs/rest-service-cors-0.0.1-SNAPSHOT.jar

## Create a jQuery Controller

This controller module is represented as a simple JavaScript function. It uses jQuery’s $.ajax() method to consume the REST service at http://rest-service.guides.spring.io/greeting. If successful, it will assign the JSON received to data, effectively making it a Greeting model object. The id and content are then appended to the greeting-id and greeting-content DOM elements respectively.

Note the use of the jQuery promise .then(). This directs jQuery to execute the anonymous function when the $.ajax() method completes, passing the data result from the completed AJAX request.

## Test the service

Provide a name query string parameter by visiting http://localhost:8080/greeting?name=User. 

This change demonstrates that the @RequestParam arrangement in GreetingController works as expected. The name parameter has been given a default value of World but can always be explicitly overridden through the query string.

``` json
{"id":2,"content":"Hello, User!"}
```

Now you can test that the CORS headers are in place and allow a Javascript client from another origin to access the service. To do so, you need to create a Javascript(`hello.js`) client to consume the service.

现在，您可以测试CORS标头是否存在，并允许其他来源的Javascript客户端访问该服务。 为此，您需要创建一个Javascript(`hello.js`)客户端来使用该服务。

This script uses jQuery to consume the REST service at http://localhost:8080/greeting. It is loaded by index.html.

This is essentially the REST client created in Consuming a RESTful Web Service with jQuery, modified slightly to consume the service when it runs on localhost at port 8080. 

从本质上讲，这是在使用jQuery的RESTful Web服务中创建的REST客户端，当它在localhost的端口8080上运行时，对其进行了少许修改以使用该服务。

Because the REST service is already running on localhost at port 8080, you need to be sure to start the client from another server or port. Doing so not only avoids a collision between the two applications but also ensures that the client code is served from a different origin than the service. 

由于REST服务已经在本地主机的端口8080上运行，因此需要确保从其他服务器或端口启动客户端。 这样做不仅避免了两个应用程序之间的冲突，而且还确保了从与服务不同的来源提供客户端代码。

To start the client running on localhost at port 9000, run the following Maven command:

``` bash
./mvnw spring-boot:run -Dserver.port=9000
```

**错误: 不能运行在9000上，设置无效。**

If the service response includes the CORS headers, then the ID and content are rendered into the page. But if the CORS headers are missing (or insufficiently defined for the client), the browser fails the request and the values are not rendered into the DOM. 

如果服务响应包含CORS标头，则ID和内容将呈现到页面中。 但是，如果缺少CORS标头（或未为客户端充分定义），则浏览器将使请求失败，并且不会将值呈现到DOM中。

# Consuming a RESTful Web Service with AngularJS

## Create an AngularJS Controller
First, you will create the AngularJS controller module that will consume the REST service:

public/hello.js

This controller module is represented as a simple JavaScript function that is given AngularJS’s `$scope` and `$http` components. It uses the `$http` component to consume the REST service at "/greeting".

If successful, it will assign the JSON returned back from the service to `$scope.greeting`, effectively setting a model object named "greeting". By setting that model object, AngularJS can bind it to the application page’s DOM, rendering it for the user to see.

## Create the Application Page
Now that you have an AngularJS controller, you will create the HTML page that will load the controller into the user’s web browser:

public/angularjs-test.html

The AngularJS library enables several custom attributes for use with standard HTML tags. In index.html, two such attributes are in play:

The <html> tag has the ng-app attribute to indicate that this page is an AngularJS application.

The <div> tag has the ng-controller attribute set to reference Hello, the controller module.

Also note the two <p> tags which use placeholders (identified by double-curly-braces).

The placeholders reference the id `{{greeting.id}}` and content `{{greeting.content}}` properties of the greeting model object which will be set upon successfully consuming the REST service.

## Run the client
To run the client, you’ll need to serve it from a web server to your browser. The Spring Boot CLI (Command Line Interface) includes an embedded Tomcat server, which offers a simple approach to serving web content. See Building an Application with Spring Boot for more information about installing and using the CLI.

In order to serve static content from Spring Boot’s embedded Tomcat server, you’ll also need to create a minimal amount of web application code so that Spring Boot knows to start Tomcat. The following app.groovy script is sufficient for letting Spring Boot know that you want to run Tomcat:

app.groovy

``` bash
@Controller class JsApp { }
```

You can now run the app using the Spring Boot CLI:

```bash
spring run app.groovy
```


# Consuming a RESTful Web Service with rest.js

First install node.js than install [bower](https://bower.io).

## Create bower configuration files
First, create a bower control file, .bowerrc. This file tells bower where to put the JavaScript dependencies. The .bowerrc file should be located at the root of the project ({project_id}/initial) and formatted as JSON:

From a command prompt at the root of the project, run bower init. This will create a bower.json file that describes each JavaScript package required by the project. Bower will ask for several bits of information such as a project name, license, etc. If in doubt, just press Enter to accept the defaults.

Next, use Bower to install rest.js and an AMD module loader such as curl.js. From the command prompt, type:
```bash
bower install --save rest#~1

bower install --save curl#~0.8
```

Bower will install rest.js and curl.js into the directory we listed in `.bowerrc`. Since we specified the --save option, bower will store the package information in the `bower.json` file.

Bower should discover that rest.js depends on when.js and install a compatible version.
When done, the `bower.json` file should have a "dependencies" object property that lists "curl" and "rest" as property names and their semver information as values.

## Create a render module
First, create a render function to inject data into an HTML document.

public/hello/render.js

This AMD module [Asynchronous Module Definition]() uses simple DOM querying and manipulation to inject text into the document. To ensure that the DOM is not used before it is ready, the render module imports and uses curl.js’s domReady function-module.

> In a real application, you’ll want to use data binding or templating, rather than DOM manipulation as shown here.

## Create an application composition module
Next, create a module that will compose the application.

public/hello/main.js

The main module reads the query string from the document’s location object, configures a rest.js mime client, and calls the REST endpoint.

rest.js returns a Promises/A+ promise, which will call the render function-module when the endpoint returns data. The render function expects the entity, but the rest.js client normally returns a response object. The "rest/interceptor/entity" interceptor plucks the entity from the response and forwards that onto the render function.

## Create a boot script
Next, create the boot script, run.js:

public/run.js

This script configures the AMD loader: curl.config(). The main configuration property tells curl.js where to find the application’s main module, which will be fetched and evaluated automatically. The packages config object tells curl.js where to find modules in our application’s packages or in third-party packages.

## Create the application page
Finally, create an index.html file and add the following HTML:

public/restjs-test.html

The script element loads curl.js and then loads an application boot script named "run.js". The boot script initializes and configures an AMD module environment and then starts the client-side application code.

## Run the client

``` bash
@Controller class JsApp { }
```

```bash
spring run app.groovy
```

# Consuming a RESTful Web Service with jQuery

## Create a jQuery Controller
First, you will create the jQuery controller module that will consume the REST service:

public/hello-jQuery.js

This controller module is represented as a simple JavaScript function. It uses jQuery’s $.ajax() method to consume the REST service at http://rest-service.guides.spring.io/greeting. If successful, it will assign the JSON received to data, effectively making it a Greeting model object. The id and content are then appended to the greeting-id and greeting-content DOM elements respectively.

Note the use of the jQuery promise .then(). This directs jQuery to execute the anonymous function when the $.ajax() method completes, passing the data result from the completed AJAX request.

## Create the Application Page
Now that you have a jQuery controller, you will create the HTML page that will load the client into the user’s web browser:

public/index.html.

The first script tag loads the minified jQuery library (jquery.min.js) from a content delivery network (CDN) so that you don’t have to download jQuery and place it in your project. It also loads the controller code (hello.js) from the application’s path.

Also note that the <p> tags include class attributes.

These class attributes help jQuery to reference the HTML elements and update the text with the values from the id and content properties of the JSON received from the REST service.

## Run the client

```bash
spring run app.groovy
```