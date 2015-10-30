package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MyVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer("OnLogin", new Handler<Message<Object>>() {
            @Override
            public void handle(Message<Object> objectMessage) {
                String msg = (String)objectMessage.body();
                objectMessage.reply(msg);
            }
        });

        vertx.eventBus().consumer("OnLogout", new Handler<Message<Object>>() {
            @Override
            public void handle(Message<Object> objectMessage) {
                String msg = (String) objectMessage.body();
                objectMessage.reply(msg);
            }
        });

        vertx.setPeriodic(30000, new Handler<Long>() {

            @Override
            public void handle(Long aLong) {

            }
        });

    }

    @Override
    public void stop(Future stopFuture) throws Exception {
        System.out.println("MyVerticle stopped!");
    }

}