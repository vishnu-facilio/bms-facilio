package com.facilio.wmsv2.message;

import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.logging.Logger;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class MessageEncoder implements Encoder.Text<WebMessage>
{
    private final Logger log = Logger.getLogger(getClass().getName());

    public String encode(WebMessage message) throws EncodeException
    {
        try {
            JSONObject json = FieldUtil.getAsJSON(message);
            return json.toJSONString();
        } catch (Exception ex) {
            throw new EncodeException(message, ex.getMessage());
        }
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
