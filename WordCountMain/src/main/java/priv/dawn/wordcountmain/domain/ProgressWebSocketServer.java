package priv.dawn.wordcountmain.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/wsServer/{fileUID}")
@Component // WebSocket 好像默认是多例的
@Slf4j
public class ProgressWebSocketServer {

    private static final ConcurrentHashMap<Integer, ProgressWebSocketServer> wsMap = new ConcurrentHashMap<>();

    private Session session;
    private int fileUID;

    @OnOpen
    public void onOpen(Session session, @PathVariable("fileUID") int fileUID) {
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
    }

    @OnError
    public void onError() {
        log.error("File " + fileUID + " WebSocket error");
    }

    public static void sendMessage(int fileUID, String msg) throws IOException {
        wsMap.get(fileUID).send(msg);
    }

    private void send(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }

}

