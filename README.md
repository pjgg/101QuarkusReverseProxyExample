# code-with-quarkus Project

Launch reverse proxy example: `mvn quarkus:dev`

Available endpoints:

Wolf
```
curl -v http://localhost:8080/wolf/bark
```

Sheep
```
curl -v http://localhost:8080/sheep/bark
```

Code example:

`org.acme.ReverseProxyExample` -> Vertx like reverse proxy example

`org.acme.ReverseProxyReactiveRouteExample` -> Quarkus Reactive routes, reverse proxy example