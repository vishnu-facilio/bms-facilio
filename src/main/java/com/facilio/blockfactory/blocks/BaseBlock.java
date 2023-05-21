package com.facilio.blockfactory.blocks;

import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public abstract class BaseBlock {
    protected long id;
    protected Map<String, Object> config;

    public Long getId() {
        return id;
    }

    BaseBlock(Map<String, Object> config) {
        Objects.requireNonNull(config);
        this.config = config;
        this.id = (long) config.get(Constants.BLOCK_ID);
    }

    protected Map<String, BaseBlock> blockMap = new HashMap<>();

    public void addBlockAtPosition(String position, BaseBlock block) {
        blockMap.put(position, Objects.requireNonNull(block));
    }

    public BaseBlock getNextBlock() {
        return MapUtils.isNotEmpty(blockMap) ? blockMap.get("1") : null;
    }

    public abstract void execute(Map<String, Object> memory) throws FlowException;

    public abstract void validateBlockConfiguration() throws FlowException;
}
