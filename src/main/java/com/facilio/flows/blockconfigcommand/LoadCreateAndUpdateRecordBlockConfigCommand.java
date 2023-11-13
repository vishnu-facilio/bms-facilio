package com.facilio.flows.blockconfigcommand;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.BaseCreateAndUpdateRecordFlowTransitionContext;
import com.facilio.flows.context.RawRecordData;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoadCreateAndUpdateRecordBlockConfigCommand extends BaseCreateAndUpdateBlockCommand {
    boolean listMode;
    public LoadCreateAndUpdateRecordBlockConfigCommand(boolean listMode){
        this.listMode = listMode;
    }
    public LoadCreateAndUpdateRecordBlockConfigCommand(){}
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BaseCreateAndUpdateRecordFlowTransitionContext> createRecordList = getFlowTransitionListContextFromContext(context);

        if(CollectionUtils.isEmpty(createRecordList)){
            return false;
        }

        Map<Long, BaseCreateAndUpdateRecordFlowTransitionContext> blockIdVsCreateRecordContextMap = createRecordList.stream().collect(Collectors.toMap(BaseCreateAndUpdateRecordFlowTransitionContext::getId, Function.identity()));

        Set<Long> blockIds = blockIdVsCreateRecordContextMap.keySet();


        List<RawRecordData> rawRecordDataList = getRawRecordsFromDB(blockIds);

        if(CollectionUtils.isEmpty(rawRecordDataList)){
            return false;
        }
        Map<Long,List<RawRecordData>> blockIdVsRawRecordsMap = rawRecordDataList.stream()
                .collect(Collectors.groupingBy(RawRecordData::getBlockId));

        for(Long blockId:blockIdVsRawRecordsMap.keySet()){
            BaseCreateAndUpdateRecordFlowTransitionContext transitionContext = blockIdVsCreateRecordContextMap.get(blockId);
            Map<String,Object> configMap = BlockFactory.getAsMapFromJsonString(transitionContext.getConfigData());
            String recordModuleName = (String) configMap.get(Constants.MODULE_NAME);

            List<RawRecordData> rawDataList = blockIdVsRawRecordsMap.get(blockId);

            JSONObject jsonObject = deserializeRawRecordList(rawDataList,recordModuleName);
            transitionContext.setRecordData(jsonObject);
        }


        return false;
    }
    private List<BaseCreateAndUpdateRecordFlowTransitionContext> getFlowTransitionListContextFromContext(Context context){
        List<BaseCreateAndUpdateRecordFlowTransitionContext> list = new ArrayList<>();
        if(listMode){
            list = (List<BaseCreateAndUpdateRecordFlowTransitionContext>) context.get(FacilioConstants.ContextNames.FLOW_TRANSITIONS);
        }else{
            BaseCreateAndUpdateRecordFlowTransitionContext createRecordFlowTransitionContext = (BaseCreateAndUpdateRecordFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
            if(createRecordFlowTransitionContext!=null){
                list.add(createRecordFlowTransitionContext);
            }

        }
        return list;
    }
}
