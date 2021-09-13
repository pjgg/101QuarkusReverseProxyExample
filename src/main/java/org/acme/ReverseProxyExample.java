//package org.acme;
//
//import io.quarkus.runtime.Quarkus;
//import io.quarkus.runtime.QuarkusApplication;
//import io.quarkus.runtime.annotations.QuarkusMain;
//import io.vertx.core.Future;
//import io.vertx.core.Vertx;
//import io.vertx.core.http.HttpClient;
//import io.vertx.core.http.HttpMethod;
//import io.vertx.core.http.HttpServer;
//import io.vertx.ext.web.Router;
//import io.vertx.ext.web.proxy.handler.ProxyHandler;
//import io.vertx.httpproxy.HttpProxy;
//
// THIS IS A VERTX LIKE EXAMPLE
//@QuarkusMain
//public class ReverseProxyExample implements QuarkusApplication {
//
//    private static final String DEFAULT_HOST = "0.0.0.0";
//    private static final Integer DEFAULT_PORT = 8083;
//    private static final Integer DEFAULT_SHEEP_PORT = 7071;
//    private static final Integer DEFAULT_WOLF_PORT = 7070;
//
//    public static void main(String... args) {
//        Quarkus.run(ReverseProxyExample.class, args);
//    }
//
//    @Override
//    public int run(String... args) {
//        Vertx vertx = Vertx.vertx();
//
//        //launch mocks apps
//        launchSheepApplication(vertx);
//        launchWolfApplication(vertx);
//
//        // setup reverse proxy
//        Router proxyRouter = Router.router(vertx);
//
//        HttpClient proxyClient = vertx.createHttpClient();
//
//        HttpProxy proxyWolf = HttpProxy.reverseProxy(proxyClient);
//        proxyWolf.origin(DEFAULT_WOLF_PORT, "localhost");
//
//        HttpProxy proxySheep = HttpProxy.reverseProxy(proxyClient);
//        proxySheep.origin(DEFAULT_SHEEP_PORT, "localhost");
//
//        proxyRouter.route(HttpMethod.GET, "/wolf/*").handler(ProxyHandler.create(proxyWolf));
//        proxyRouter.route(HttpMethod.GET, "/sheep/*").handler(ProxyHandler.create(proxySheep));
//
//        Future<HttpServer> proxyServer = vertx.createHttpServer()
//                .requestHandler(proxyRouter)
//                .listen(DEFAULT_PORT)
//                .onComplete(httpServer -> System.out.println(String.format("HTTP server started on http://%s:%d", DEFAULT_HOST, DEFAULT_PORT)))
//                .onFailure(exception -> System.err.println(String.format("HTTP server failed: %s ", exception.getMessage())));
//
//        Quarkus.waitForExit();
//        proxyServer.result().close();
//        return 0;
//    }
//
//    private int launchSheepApplication(Vertx vertx) {
//        HttpServer backendServer = vertx.createHttpServer();
//        Router backendRouter = Router.router(vertx);
//        backendRouter.route(HttpMethod.GET, "/sheep/bark").handler(rc -> rc.response().end("baaaa-aaahh !!!"));
//        backendServer.requestHandler(backendRouter).listen(DEFAULT_SHEEP_PORT);
//        return 0;
//    }
//
//    private int launchWolfApplication(Vertx vertx) {
//        HttpServer backendServer = vertx.createHttpServer();
//        Router backendRouter = Router.router(vertx);
//        backendRouter.route(HttpMethod.GET, "/wolf/bark").handler(rc -> rc.response().end("hooooooowwwww !!!"));
//        backendServer.requestHandler(backendRouter).listen(DEFAULT_WOLF_PORT);
//        return 0;
//    }
//}
