package com.facilio.wms.endpoints;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.facilio.wms.message.Message;

/**
 *
 * @author Shivaraj
 */
@ClientEndpoint
public class FacilioClientEndpoint 
{
    Session session = null;
    private MessageHandler messageHandler;

    public FacilioClientEndpoint(URI endpointURI) 
    {
        try 
        {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } 
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param session the session which is opened.
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("opening websocket");
        this.session = session;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param session the session which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("closing websocket");
        this.session = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     * @throws EncodeException 
     * @throws IOException 
     */
    public void sendMessage(Message message) throws IOException, EncodeException 
    {
    	FacilioServerEndpoint.sendMessage(message);
    }

    /**
     * Message handler.
     *
     * @author Shivaraj
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}