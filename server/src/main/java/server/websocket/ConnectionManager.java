package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Session, Integer> gameIDs = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        connections.put(session, session);
        gameIDs.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
        gameIDs.remove(session);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        String json = new Gson().toJson(message);
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession) && gameIDs.get(c) == gameID) {
                    c.getRemote().sendString(json);
                }
            }
        }
    }

    public void send(Session session, ServerMessage message) throws IOException {
        String json = new Gson().toJson(message);
        if (session.isOpen()) {
            session.getRemote().sendString(json);
        }
    }
}