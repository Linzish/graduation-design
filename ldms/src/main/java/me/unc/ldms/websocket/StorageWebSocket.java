package me.unc.ldms.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description 仓储点websocket，用于接收订单信息
 * @Date 2020/4/2 16:38
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{sid}")   //此注解相当于设置访问URL
public class StorageWebSocket {

    //与某个客户端的连接会话
    private Session session;
    //存放每个客户端对应的StorageWebSocket对象。
    private static CopyOnWriteArraySet<StorageWebSocket> webSockets =new CopyOnWriteArraySet<>();
    //存储客户端对应Session
    private static Map<String, Session> sessionPool = new HashMap<>();
    //当前连接数
    private static int onlineCount = 0;
    //客户端唯一标识sid
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        addOnlineCount();
        log.info("新连接：sid=" + sid + " 当前在线人数" + getOnlineCount());
        //存储session
        this.session = session;
        webSockets.add(this);
        this.sid = sid;
        sessionPool.put(sid, session);
    }

    /**
     * 客户端断开连接时调用
     */
    @OnClose
    public void onClose() {
        log.info("连接关闭：sid=" + sid + " 当前在线人数" + getOnlineCount());
        webSockets.remove(this);
        subOnlineCount();
    }

    /**
     * 发送信息
     * @param message 消息
     * @param sid 客户端id
     */
    public void sendMsg(String message, @PathParam("sid") String sid) {
        Session session = sessionPool.get(sid);
        if (session != null) {
            try {
                log.info("sid = " + sid + "推送消息");
                session.getAsyncRemote().sendText(message);
                log.debug("sid = " + sid + "推送成功");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("websocket推送消息失败；sid = " + sid);
            }
        }
    }

    /**
     * 广播消息
     * @param message 消息
     */
    public void broadcast(String message) {
        log.debug("webSocket broadcast");
        for (StorageWebSocket webSocket : webSockets) {
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.error("webSocket broadcast error");
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误调用
     * @param session 会话
     * @param error 错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("webSocket error, session = " + session.getId());
        error.printStackTrace();
    }

    public String getSid() {
        return sid;
    }

    protected static synchronized int getOnlineCount() {
        return onlineCount;
    }

    protected static synchronized void addOnlineCount() {
        StorageWebSocket.onlineCount++;
    }

    protected static synchronized void subOnlineCount() {
        if (StorageWebSocket.onlineCount <= 0) {
            StorageWebSocket.onlineCount = 0;
            return;
        }
        StorageWebSocket.onlineCount--;
    }

}
