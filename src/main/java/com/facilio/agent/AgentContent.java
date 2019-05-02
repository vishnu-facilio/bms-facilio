package com.facilio.agent;

public enum AgentContent
{
    IGNORED("Ignored",0),
    PROCESSING("Processing",1),
    EXECUTED("Executed",2),
    DISCONNECTED("Agent disconnected to Facilio",3),
    CONNECTED("Agent connected to Facilio",1000),
    RESTARTED("Restarted",5),
    SUBSCRIBED("Subscribed",6);
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
