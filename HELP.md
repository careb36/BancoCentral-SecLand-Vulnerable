# Getting Started

> **Note:** This project is intentionally vulnerable for educational and research purposes.

This project follows best practices for modern Spring Boot development.
Below you will find reference links and guides for further documentation and typical usage scenarios.

## Reference Documentation

- [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.3/maven-plugin)
- [Create an OCI image](https://docs.spring.io/spring-boot/3.5.3/maven-plugin/build-image.html)
- [Spring Web](https://docs.spring.io/spring-boot/3.5.3/reference/web/servlet.html)
- [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.3/reference/data/sql.html#data.sql.jpa-and-spring-data)
- [Validation](https://docs.spring.io/spring-boot/3.5.3/reference/io/validation.html)
- [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.3/reference/using/devtools.html)

## Guides

These guides illustrate concrete examples for commonly used features:

- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
- [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Validation](https://spring.io/guides/gs/validating-form-input/)

## Maven Parent Overrides

Due to Maven’s inheritance model, unwanted elements (such as `<license>` and `<developers>`) are inherited from the parent POM.
This project includes empty overrides to avoid this.  
If you switch to a different parent and want to re-enable inheritance, remove these overrides.
