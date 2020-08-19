package com.facilio.wmsv2.message;

import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.util.logging.Logger;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class MessageDecoder implements Decoder.Text<Message>
{
    private final Logger log = Logger.getLogger(getClass().getName());

    public Message decode(String s) throws DecodeException
    {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(s);
            Message message = FieldUtil.getAsBeanFromJson(json, Message.class);
            return message;
        } catch (Exception ex) {
            throw new DecodeException(s, ex.getMessage());
        }
    }

    public boolean willDecode(String s) 
    {
        return (s != null);
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
