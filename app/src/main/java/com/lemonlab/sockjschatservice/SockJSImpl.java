package com.lemonlab.sockjschatservice;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lk on 2015. 10. 24..
 */
public class SockJSImpl extends WebSocketClient {

    private Map<String, String> openHandShakeFields;
    private final static String dictionary = "abcdefghijklmnopqrstuvwxyz0123456789_";
    private String roomname;

    public SockJSImpl(String serverURI, String roomname) throws URISyntaxException{
        super(new URI(generatePrimusUrl(serverURI)), new Draft_17());
        Log.i("test", "Test");
        this.openHandShakeFields = new HashMap<>();
        this.roomname = roomname;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("SockJS", "Open");
        Iterator<String> it = handshakedata.iterateHttpFields();
        while(it.hasNext()){
            String key = it.next();
            openHandShakeFields.put(key, handshakedata.getFieldValue(key));
        }

        scheduleHeartbeat();
        registAddress("to.client."+roomname);

    }

    @Override
    public void onMessage(String s) {
        JSONObject response;
        if (s.charAt(0) == 'o' || s.charAt(0) == 'h') {
            // ignore
        }
        else if (s.charAt(0) == 'a') {
            parseSockJS(s);
        } else {
            System.out.println("onMessage "+s);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("SockJS", reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("SockJS", ex.toString());
    }

    void parseSockJS(String s) {
        try {
            s = s.replace("\\\"", "\"");
            s = s.substring(3, s.length() - 2); // a[" ~ "] 없애기

            JSONObject json = new JSONObject(s);
            String type = json.getString("type");
            String address = json.getString("address");
            String body = json.getString("body");

            if("to.client.BroadcastNewsfeed".equals(address))
                System.out.printf("%s, %s, %s\n", type, address, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * JSON을 websocket 전송용 문자열로 변환하여 전
     * @param json
     */
    void send(JSONObject json) {
        String str = json.toString();
        str = str.replaceAll("\"", "\\\\\"");
        str = "[\"" + str + "\"]";
        send(str);
    }

    void registAddress(String address) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "register");
            obj.put("address", address);

            send(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 9초마다 Heartbeat. 10초 이내로 보내야 하기 때문에 9초 설정.
     */
    void scheduleHeartbeat() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String ping = "[\"{\\\"type\\\":\\\"ping\\\"}\"]";
                send(ping);

                scheduleHeartbeat();
            }
        }, 9000);
    }

    private static char randomCharacterFromDictionary() {
        int rand = (int) (Math.random() * dictionary.length());
        return dictionary.charAt(rand);
    }


    private static String randomStringOfLength(int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(randomCharacterFromDictionary());
        }
        return s.toString();
    }

    private static String generatePrimusUrl(String baseUrl) {
        Random r = new Random();
        int server = r.nextInt(1000);
        String connId = randomStringOfLength(8);
        return baseUrl + "/" + server + "/" + connId + "/websocket";
    }
}
