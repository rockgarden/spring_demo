## Build an HTML Front End
Now build the from page: src/main/resources/templates/form.html.

The page contains a simple form, with each of its field in a separate cell in a table. The form is geared to post to /. It is marked as being backed up by the personForm object that you saw in the GET method in the web controller. This is known as a “bean-backed form”. There are two fields in the PersonForm bean, and you can see that they are tagged with th:field="{name}" and th:field="{age}". Next to each field is a secondary element that is used to show any validation errors.

Finally, you have a button that submits the form. In general, if the user enters a name or age that violates the @Valid constraints, it bounces back to this page with the error message displayed. If a valid name and age is entered, the user is routed to the next web page.