package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * Created by jwy on 2015-10-08.
 */
public class DBVerticle extends AbstractVerticle {

    JDBCClient client;

    private void init() {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:mysql://localhost:3306/testdb?useUnicode=true&amp;characterEncoding=utf-8&amp;autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull")
                .put("user", "testuser")
                .put("password", "testpass")
                .put("max_pool_size", 30);

        client = JDBCClient.createShared(vertx, config);
    }

    // 얘들아, 이 매소드 이쁘게 리팩토링 하는 사람 상줄게 :)
    void testQuery() {
        client.getConnection(res -> {
            if (res.succeeded()) {

                SQLConnection connection = res.result();

                connection.query("SELECT * FROM test_table", res2 -> {
                    if (res2.succeeded()) {

                        ResultSet rs = res2.result();
                        // Do something with results
                    }
                });
            } else {
                // Failed to get connection - deal with it
            }
        });
    }


    @Override
    public void start() throws Exception {

        init();
        testQuery();
    }

}
