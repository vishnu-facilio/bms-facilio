package com.facilio.blockfactory;

import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.blockfactory.enums.BlockType;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flows.context.FlowTransitionContext;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BlockFactory {
    public static BaseBlock createBlock(Map<String,Object> config,BlockType blockType) {
        Objects.requireNonNull(config);
        return blockType.getInstance(config);
    }
    public static BaseBlock buildFlowGraph(List<FlowTransitionContext> blocks) throws Exception{
        Map<Long,BaseBlock> blockIdVsBlock = new HashMap<>();
        BaseBlock startBlock = null;

        for (FlowTransitionContext block:blocks){

            block.updateConfig();

            Map<String, Object> blockConfig = block.getConfig();

            blockConfig.put(Constants.BLOCK_ID,block.getId());

            BaseBlock baseBlock = createBlock(blockConfig,block.getBlockType());

            blockIdVsBlock.put(baseBlock.getId(), baseBlock);

            boolean isStartBlock = block.getIsStartBlock();

            if(isStartBlock){
                startBlock = baseBlock;
            }
        }

        if(startBlock == null){
            throw new FlowException("startBlock is not configured");
        }

        for (FlowTransitionContext block : blocks) {
            Long id = block.getId();
            Long parentId = block.getConnectedFrom();
            if (parentId!=-1l) {
                BaseBlock baseBlock = blockIdVsBlock.get(id);
                BaseBlock parentBlock = blockIdVsBlock.get(parentId);
                parentBlock.addBlockAtPosition(block.getHandlePosition(), baseBlock);
            }
        }

        return startBlock;
    }
    public static Map<String, Object> getAsMapFromJsonString(String jsonString) throws Exception {
        if(StringUtils.isEmpty(jsonString)){
            return new HashMap<>();
        }
       return (Map<String, Object>) new JSONParser().parse(jsonString);
    }
}
