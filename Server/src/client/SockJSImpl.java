package client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by jwy on 2015. 10. 17..
 * maven library 추가: org.java-websocket:Java-WebSocket:1.3.0
 * 최대한 코드 간결히 작성했으니 실행먼저 해보고 차근히 분석해봐라 어렵지 않을거야.
 * 서버는 기존 서버 그대로 사용.
 */
public class SockJSImpl extends WebSocketClient {

    private final Map<String, String> openHandShakeFields;

    public SockJSImpl(String uri) throws URISyntaxException {
        super(new URI(generatePrimusUrl(uri)), new Draft_17());
        openHandShakeFields = new HashMap<String, String>();
    }

    private final static String dictionary = "abcdefghijklmnopqrstuvwxyz0123456789_";

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("open");
        Iterator<String> it = handshakedata.iterateHttpFields();
        while(it.hasNext()) {
            String key = it.next();
            System.out.printf("%s %s%n", key, handshakedata.getFieldValue(key)); // TODO Remove this
            openHandShakeFields.put(key, handshakedata.getFieldValue(key));
        }

        scheduleHeartbeat();
        registAddress("to.client.BroadcastNewsfeed");
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
    public void onClose(int i, String s, boolean b) {
        System.out.println("onClose "+s);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
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

    public static void main(String [] args) {
        try {
            String url = "http://43.230.3.62:8080/eventbus";
            SockJSImpl test = new SockJSImpl(url);
            boolean bConnect = test.connectBlocking();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
