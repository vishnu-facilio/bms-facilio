package com.facilio.flows.blockconfigcommand;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.BaseCreateAndUpdateRecordFlowTransitionContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

public class AddOrUpdateCreateRecordBlockConfigCommand extends BaseCreateAndUpdateBlockCommand {
    private boolean isUpdate;
    public AddOrUpdateCreateRecordBlockConfigCommand(boolean isUpdate){
        this.isUpdate = isUpdate;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        BaseCreateAndUpdateRecordFlowTransitionContext createRecordFlowTransitionContext = (BaseCreateAndUpdateRecordFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        long blockId = createRecordFlowTransitionContext.getId();
        JSONObject rawRecordData = createRecordFlowTransitionContext.getRecordData();
        Map<String,Object> configMap = BlockFactory.getAsMapFromJsonString(createRecordFlowTransitionContext.getConfigData());
        String recordModuleName = (String) configMap.get(Constants.MODULE_NAME);


        if(isUpdate){
            delete(blockId);
        }
        insert(blockId,rawRecordData,recordModuleName);


        return false;
    }
}
