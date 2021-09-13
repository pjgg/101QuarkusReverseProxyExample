package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import io.vertx.httpproxy.HttpProxy;


/**
 * Reactive routes example / Reverse proxy
 * */
@ApplicationScoped
public class ReverseProxyReactiveRouteExample {

    private static final Logger LOG = Logger.getLogger(ReverseProxyReactiveRouteExample.class);
    private static final Integer DEFAULT_SHEEP_PORT = 7071;
    private static final Integer DEFAULT_WOLF_PORT = 7070;

    @Inject
    Vertx vertx;

    Router proxyRouter;

    void init(@Observes Router router) {
        this.proxyRouter = router;
    }

    void onStart(@Observes StartupEvent ev) {
        //launch mocks apps
        launchSheepApplication(vertx);
        launchWolfApplication(vertx);
        LOG.info("Mocks apps started");

        // setup reverse proxy
        HttpClient proxyClient = vertx.createHttpClient();

        HttpProxy proxyWolf = HttpProxy.reverseProxy(proxyClient);
        proxyWolf.origin(DEFAULT_WOLF_PORT, "localhost");

        HttpProxy proxySheep = HttpProxy.reverseProxy(proxyClient);
        proxySheep.origin(DEFAULT_SHEEP_PORT, "localhost");

        addRoute(HttpMethod.GET, "/wolf/*", ProxyHandler.create(proxyWolf));
        addRoute(HttpMethod.GET, "/sheep/*", ProxyHandler.create(proxySheep));
        LOG.info("Application reverseProxy started");
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("Application reverseProxy stopped.");
    }

    private void addRoute(HttpMethod method, String path, Handler<RoutingContext> handler) {
        Route route = this.proxyRouter.route(method, path)
                .handler(CorsHandler.create("*"))
                .handler(LoggerHandler.create())
                .handler(handler);

        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT))
            route.handler(BodyHandler.create());
    }

    // MOCK apps

    private int launchSheepApplication(Vertx vertx) {
        HttpServer backendServer = vertx.createHttpServer();
        Router backendRouter = Router.router(vertx);
        backendRouter.route(HttpMethod.GET, "/sheep/bark").handler(rc -> rc.response().end("baaaa-aaahh !!!"));
        backendServer.requestHandler(backendRouter).listen(DEFAULT_SHEEP_PORT);
        return 0;
    }

    private int launchWolfApplication(Vertx vertx) {
        HttpServer backendServer = vertx.createHttpServer();
        Router backendRouter = Router.router(vertx);
        backendRouter.route(HttpMethod.GET, "/wolf/bark").handler(rc -> rc.response().end("hooooooowwwww !!!"));
        backendServer.requestHandler(backendRouter).listen(DEFAULT_WOLF_PORT);
        return 0;
    }
}
