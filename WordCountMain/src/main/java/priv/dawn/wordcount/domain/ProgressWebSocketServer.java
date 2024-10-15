package priv.dawn.wordcount.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/progress/{fileUID}")
@Component
@Slf4j
public class ProgressWebSocketServer {

    private static final ConcurrentHashMap<Integer, ProgressWebSocketServer> wsMap = new ConcurrentHashMap<>();

    private Session session;
    private int fileUID;

    @OnOpen
    public void onOpen(Session session, @PathParam("fileUID") int fileUID) {
        this.session = session;
        this.fileUID = fileUID;
        wsMap.put(fileUID, this);
        log.info("File " + fileUID + " On WebSocket ");
    }

    @OnClose
    public void onClose() {
        wsMap.remove(fileUID);
        log.info("File " + fileUID + " Off WebSocket");
    }

    @OnMessage
    public void onMessage(String message) {
        // 不做任何处理
        log.info("message.length:" + message.length());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("File-" + fileUID + " WebSocket-" + session + " error:" + throwable.toString());
    }

    public static void sendMessage(int fileUID, String msg) {
        try {
            if (wsMap.containsKey(fileUID)) wsMap.get(fileUID).send(msg);
        } catch (IOException e) {
            log.error("FileUID: " + fileUID + " Error: " + e);
        }
    }

    private void send(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }

}

