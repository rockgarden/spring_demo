To upload files with Servlet containers, you need to register a MultipartConfigElement class (which would be <multipart-config> in web.xml). 

As part of auto-configuring Spring MVC, Spring Boot will create a MultipartConfigElement bean and make itself ready for file uploads.

## Create a File Upload Controller
The FileUploadController class is annotated with @Controller so that Spring MVC can pick it up and look for routes. Each method is tagged with @GetMapping or @PostMapping to tie the path and the HTTP action to a particular controller action.

In this case:

GET /: Looks up the current list of uploaded files from the StorageService and loads it into a Thymeleaf template. It calculates a link to the actual resource by using MvcUriComponentsBuilder.

GET /files/{filename}: Loads the resource (if it exists) and sends it to the browser to download by using a Content-Disposition response header.

POST /: Handles a multi-part message file and gives it to the StorageService for saving.

In a production scenario, you more likely would store the files in a temporary location, a database, or perhaps a NoSQL store (such as Mongo’s GridFS). It is best to NOT load up the file system of your application with content.

You will need to provide a StorageService so that the controller can interact with a storage layer (such as a file system).

## Creating an HTML Template
This Thymeleaf template (src/main/resources/templates/uploadForm.html) shows an example of how to upload files and show what has been uploaded, it has three parts:
* An optional message at the top where Spring MVC writes a flash-scoped message.
* A form that lets the user upload files.
* A list of files supplied from the backend.

## Tuning File Upload Limits
When configuring file uploads, it is often useful to set limits on the size of files. Imagine trying to handle a 5GB file upload! With Spring Boot, we can tune its auto-configured MultipartConfigElement with some property settings.

## Run the Application
You want a target folder to which to upload files, so you need to enhance the basic UploadingFilesApplication class that Spring Initializr created and add a Boot CommandLineRunner to delete and re-create that folder at startup. 

## Testing Your Application
Uses MockMvc so that it does not require starting the servlet container.

`src/test/java/com/example/uploadingfiles/FileUploadTests.java` in those tests, you use various mocks to set up the interactions with your controller and the StorageService but also with the Servlet container itself by using MockMultipartFile.

