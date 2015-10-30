package server;

import io.vertx.core.Vertx;

import java.nio.ByteBuffer;

public class Main {

    public static Vertx vertx;

    public static void createVertx() {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new MyVerticle());
        vertx.deployVerticle(new DBVerticle());
        vertx.deployVerticle(new SockJSVerticle());
    }

    public static void publishEvent(String type, String msg)
    {
        vertx.eventBus().publish(type, msg);
    }

    public static void publishEvent(String type, ByteBuffer buffer)
    {
        try
        {
            byte data[] = new byte[buffer.capacity()];
            buffer.get(data);
            vertx.eventBus().publish(type, data);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void publishEvent(String type, byte[] buffer)
    {
        try
        {
            vertx.eventBus().publish(type, buffer);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createVertx();
    }
}

