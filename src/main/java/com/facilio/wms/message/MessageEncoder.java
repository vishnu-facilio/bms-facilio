package com.facilio.wms.message;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.logging.Logger;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class MessageEncoder implements Encoder.Text<Message> 
{
    private final Logger log = Logger.getLogger(getClass().getName());

    public String encode(Message message) throws EncodeException 
    {
        Gson gson = new Gson();
        String json = gson.toJson(message);
        return json;
    }

    public void init(EndpointConfig endpointConfig) 
    {
        // do nothing
    }

    public void destroy() 
    {
        // do nothing
    }
}
