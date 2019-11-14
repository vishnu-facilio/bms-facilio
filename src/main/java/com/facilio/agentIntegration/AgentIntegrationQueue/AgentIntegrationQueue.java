package com.facilio.agentIntegration.AgentIntegrationQueue;

import com.facilio.agentIntegration.AgentIntegrationQueue.preprocessor.AgentIntegrationPreprocessor;

public abstract class AgentIntegrationQueue implements Runnable{
    long orgiD = -1;
    private AgentIntegrationPreprocessor preprocessor = null;

    public long getOrgiD() {
        return orgiD;
    }

    public void setOrgiD(long orgiD) {
        this.orgiD = orgiD;
    }
    abstract void startProcessor(long orgiD);
    @Override
    public void run(){
        if (orgiD!=-1)
        startProcessor(orgiD);
    }
    public void setPreprocessor(AgentIntegrationPreprocessor preprocessor) {
        this.preprocessor=preprocessor;
    }
    public AgentIntegrationPreprocessor getPreProcessor(){return preprocessor; }
}
