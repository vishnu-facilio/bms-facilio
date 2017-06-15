package com.facilio.wms.message;

import org.json.simple.JSONObject;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class Message 
{
    private int from;
    private int to;
    private JSONObject content;

    public Message() 
    {
    	
	}
    
    @Override
    public String toString() 
    {
        return super.toString();
    }

    public int getFrom() 
    {
        return from;
    }

    public void setFrom(int from) 
    {
        this.from = from;
    }

    public int getTo() 
    {
        return to;
    }

    public Message setTo(int to) 
    {
        this.to = to;
        return this;
    }

    public JSONObject getContent() 
    {
        return content;
    }

    public Message setContent(JSONObject content) 
    {
        this.content = content;
        return this;
    }
}
