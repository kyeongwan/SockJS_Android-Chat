package server;

import io.vertx.core.*;
import io.vertx.core.eventbus.*;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.*;

/**
 * Created by jwy on 2015. 8. 27..
 */
public class SockJSVerticle extends AbstractVerticle {

    String permissions[] = {
            "to.client.BroadcastNewsfeed",
            "to.server.RequestNewsfeed"
    };
    DeliveryOptions deliveryOptions = new DeliveryOptions();


    int count;
    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        HandleingSockJS(router);
        createHttpSvr(router);
        addCustomEvent();

        vertx.setPeriodic(1000, t -> vertx.eventBus().publish("to.client.BroadcastNewsfeed", "news from the server! " + (count++)));

    }

    private void createHttpSvr(Router router) {
        StaticHandler sHandler = StaticHandler.create("./www");
        sHandler.setCachingEnabled(false);
        router.route().handler(sHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {

                // 특정 path에 대한 처리
                if (req.uri().indexOf("/test") != -1) {

                }

                req.response().headers().set("Access-Control-Allow-Origin", "*");

                // 테스트용 JSON
                JsonObject json = new JsonObject();
                json.put("test", "1");
                req.response().end(json.toString());

            }
        }).listen(8888);
    }

    // only for HandleingSockJS method
    private BridgeOptions createOptions() {
        BridgeOptions options = new BridgeOptions();
        for(String permission : permissions) {
            if(permission.startsWith("to.client"))
                options.addOutboundPermitted(new PermittedOptions().setAddress(permission));
            else if(permission.startsWith("to.server"))
                options.addInboundPermitted(new PermittedOptions().setAddress(permission));
            else
                System.out.println(permission+" is wrong permission!!");
        }
        return options;
    }

    private void HandleingSockJS(Router router) {
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(createOptions(), event -> {

            String uuid = event.socket().writeHandlerID();
            if (event.type() == BridgeEvent.Type.SOCKET_CREATED) {
                String host = event.socket().remoteAddress().host();
                System.out.println(host+" connected, uuid : "+ uuid);
            }else if(event.type()   == BridgeEvent.Type.SOCKET_CLOSED){
                System.out.println("Closed uuid : " + uuid);
            }

            event.complete(true);

        }));
    }

    private void addCustomEvent()
    {
        EventBus eb = vertx.eventBus();

        // 타 버티클에서 접속자에게 실시간 뉴스피드 전송
        eb.consumer("to.server.RequestNewsfeed", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
                System.out.println("Messaage : " + objectMessage.body());
                vertx.eventBus().publish("to.client.BroadcastNewsfeed", objectMessage.body());

            }
        });


        // 유저가 서버로 뉴스피드 요청
        eb.consumer("to.server.RequestNewsfeed").handler(msg -> {
            msg.reply("test");
        });
    }

}
