package spark;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;
import static org.junit.Assert.assertEquals;
import static spark.Spark.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

public class Issue986Test {

    public static final int PORT = 4567;

    public static final String CHAT = "/hat";

    public static final String HELLO = "/hello";

    public static final String OTHER = "/:param";

    private static final SparkTestUtil http = new SparkTestUtil(4567);

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        staticFiles.location("/chatPage"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);
        webSocket("/chat", ChatWebSocketHandler.class);
        get(HELLO, (req, res) -> "Hello!");
        get(OTHER, (req, res) -> "other");
        init();
    }

    // CS304 Issue link: https://github.com/perwendel/spark/issues/986
    @Test
    public void testUrl1() throws Exception {
        try {
            SparkTestUtil.UrlResponse response = http.doMethod("GET",HELLO, "");
            assertEquals(200, response.status);
            assertEquals("Hello!",response.body);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CS304 Issue link: https://github.com/perwendel/spark/issues/986
    @Test
    public void testUrl2() throws Exception {
        try {
            SparkTestUtil.UrlResponse response = http.doMethod("GET","/aya", "");
            assertEquals(200, response.status);
            assertEquals("other",response.body);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CS304 Issue link: https://github.com/perwendel/spark/issues/986
    @Test
    public void testUrl3() throws Exception {
        try {
            SparkTestUtil.UrlResponse response = http.doMethod("GET","/chat", "");
            assertEquals(200, response.status);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CS304 Issue link: https://github.com/perwendel/spark/issues/986
    @Test
    public void testUrl4() throws Exception {
        try {
            SparkTestUtil.UrlResponse response = http.doMethod("GET","/","");
            assertEquals(200, response.status);
            System.out.print(response.body);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Chat{
    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user

    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article(
            b(sender + " says:"),
            span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
            p(message)
        ).render();
    }

    public static void setNextUserNumber(final int nextUserNumber) {
        Chat.nextUserNumber = nextUserNumber;
    }
}

@WebSocket
class ChatWebSocketHandler {

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        Chat.setNextUserNumber(Chat.nextUserNumber+1);
        String username = "User" + Chat.nextUserNumber;
        Chat.userUsernameMap.put(user, username);
        Chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message);
    }
}
