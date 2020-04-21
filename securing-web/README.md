## login.html

This Thymeleaf template presents a form that captures a username and password and posts them to /login. As configured, Spring Security provides a filter that intercepts that request and authenticates the user. If the user fails to authenticate, the page is redirected to /login?error, and your page displays the appropriate error message. Upon successfully signing out, your application is sent to /login?logout, and your page displays the appropriate success message.

Thymeleaf 模板提供了一种捕获用户名和密码并将其发布到/ login 的形式。 按照配置，Spring Security 提供了一个过滤器来拦截该请求并验证用户身份。 如果用户认证失败，则页面将重定向到/ login？error，并且您的页面将显示相应的错误消息。 成功注销后，您的应用程序将发送到/ login？logout，并且页面会显示相应的成功消息。


## hello.html

We display the username by using Spring Security’s integration with HttpServletRequest#getRemoteUser(). The “Sign Out” form submits a POST to /logout. Upon successfully logging out, it redirects the user to /login?logout.

我们通过使用Spring Security与HttpServletRequest＃getRemoteUser（）的集成来显示用户名。 “注销”表单将POST提交到/ logout。 成功注销后，它将用户重定向到/ login？logout。