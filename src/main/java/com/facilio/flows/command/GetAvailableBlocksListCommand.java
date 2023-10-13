package com.facilio.flows.command;

import com.facilio.blockfactory.enums.BlockType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.context.FlowType;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAvailableBlocksListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {


        int type = (int) context.get(FacilioConstants.ContextNames.TYPE);
        FlowType flowType = FlowType.valueOf(type);
        FacilioUtil.throwIllegalArgumentException(flowType==null,"Unsupported flow type");
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        List<Map<String, Object>> availableBlocks = getSupportedBlocks(flowType);

        context.put(FacilioConstants.ContextNames.AVAILABLE_BLOCKS,availableBlocks);


        return false;
    }
    private static List<Map<String, Object>> getSupportedBlocks(FlowType flowType){
        List<Map<String, Object>> supportedGroupBlockList = new ArrayList<>();
        List<Map<String, Object>> groupBlockList = BlockType.getGroupBlockList();
        for(Map<String,Object> group:groupBlockList){
            Map<String,Object> supportedGroup = new HashMap<>();
            supportedGroup.putAll(group);
            List<Map<String, Object>> supportedBlocks = new ArrayList<>();
            supportedGroup.put("blocks",supportedBlocks);
            List<Map<String, Object>> blocks = (List<Map<String, Object>>) group.get("blocks");
            for(Map<String,Object> blockMap : blocks){
                BlockType blockType = (BlockType) blockMap.get("name");
                if(blockType.isSupportedBlock(flowType)){
                    supportedBlocks.add(blockMap);
                }
            }
            supportedGroupBlockList.add(supportedGroup);
        }
        return supportedGroupBlockList;
    }
}
