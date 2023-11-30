package com.facilio.blockfactory.blocks;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public abstract class BaseLoopBlock extends BaseBlock{
    protected String executablePosition;

    @Getter@Setter
    protected boolean breakLoop;
    @Getter@Setter
    protected boolean continueLoop;
    public BaseLoopBlock(Map<String, Object> config) {
        super(config);
    }
    public abstract boolean hasNext();
    public  abstract Object next();
    public abstract void putToMemory(Map<String, Object> memory,Object itrObject);
    protected void setExecutablePosition(String executablePosition) {
        this.executablePosition = executablePosition;
    }
    public void resetExecutablePosition(){
        setExecutablePosition("1");
    }
}
