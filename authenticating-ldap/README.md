## Set up Spring Security
To configure Spring Security, you first need to add some extra dependencies to your build.

```
dependencies {
compile("org.springframework.boot:spring-boot-starter-web")
compile("org.springframework.boot:spring-boot-starter-security")
compile("org.springframework.ldap:spring-ldap-core")
compile("org.springframework.security:spring-security-ldap")
compile("org.springframework:spring-tx")
compile("com.unboundid:unboundid-ldapsdk")
testCompile("org.springframework.security:spring-security-test")
	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
}
```

Due to an artifact resolution issue with Gradle, spring-tx must be pulled in. Otherwise, Gradle fetches an older one that doesn’t work.

These dependencies add Spring Security and UnboundId, an open source LDAP server. With those dependencies in place, you can then use pure Java to configure your security policy.

The @EnableWebSecurity annotation turns on a variety of beans that you need to use Spring Security.

You also need an LDAP server. Spring Boot provides auto-configuration for an embedded server written in pure Java, which is being used for this guide. The `ldapAuthentication()` method configures things.

## Set up User Data
LDAP servers can use LDIF (LDAP Data Interchange Format) files to exchange user data. The spring.ldap.embedded.ldif property inside application.properties lets Spring Boot pull in an LDIF data file. This makes it easy to pre-load demonstration data. 

Using an LDIF file is not standard configuration for a production system. However, it is useful for testing purposes or guides.
If you visit the site at http://localhost:8080, you should be redirected to a login page provided by Spring Security.

Enter a user name of ben and a password of benspassword. You should see the following message in your browser: