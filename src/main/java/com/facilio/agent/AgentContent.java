package com.facilio.agent;

public enum AgentContent
{
    Ignored("Ignored",0),
    Processing("Processing",1),
    Executed("Executed",2),
    Disconnected("Agent disconnected to Facilio",3),
    Connected("Agent connected to Facilio",1000),
    Restarted("Restarted",5),
    Subscribed("Subscribed",6);
    private int key;
    private String content;

    AgentContent(String content, int key) {
        this.content = content;
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public String getContent() {
        return content;
    }
}
