package com.facilio.wms.endpoints;

import com.facilio.wms.message.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

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
    	SessionManager.getInstance().sendMessage(message);
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