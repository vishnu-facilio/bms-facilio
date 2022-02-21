package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLModelVariableContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MLServiceAfterUpdateCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(MLServiceAfterUpdateCommand.class.getName());
    private boolean isUpdateNeeded = false;
    private List<MLContext> mlContexts;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3MLServiceContext oldRecord = (V3MLServiceContext) context.get(MLServiceUtil.MLSERVICE_OLD_CONTEXT);
        mlContexts = (List<MLContext>) context.get(MLServiceUtil.ML_CONTEXT);

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3MLServiceContext> mlServiceContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(mlServiceContexts)) {
            V3MLServiceContext currentRecord = mlServiceContexts.get(0);
            currentRecord.setFailed(false);
            if(mlContexts == null) {
                FacilioChain chain  = TransactionChainFactoryV3.addMLServiceChain();
                FacilioContext subContext = chain.getContext();
                subContext.put(MLServiceUtil.MLSERVICE_CONTEXT, currentRecord);
                chain.execute();
            } else {
                updateMLServiceInSequence(currentRecord, oldRecord);
                if(!isUpdateNeeded) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "No changes has been made");
                }
                MLServiceUtil.updateMLModelMeta(currentRecord);
                FacilioChain chain  = TransactionChainFactoryV3.updateMLServiceChain();
                FacilioContext subContext = chain.getContext();
                subContext.put(MLServiceUtil.MLSERVICE_CONTEXT, currentRecord);
                subContext.put(MLServiceUtil.ML_CONTEXT, mlContexts);
                subContext.put(MLServiceUtil.IS_UPDATE, isUpdateNeeded);
//                subContext.put(FacilioConstants.ContextNames.EVENT_TYPE, isUpdateNeeded);
                chain.execute();
            }
            MLServiceUtil.updateMLModelMeta(currentRecord);

        }

        return false;
    }

    private void updateMLServiceInSequence(V3MLServiceContext currentRecord, V3MLServiceContext oldRecord) throws Exception {
        updateMLModelVariables(currentRecord, oldRecord);
        updateDateTime(currentRecord, oldRecord);
    }

    private void updateMLModelVariables(V3MLServiceContext currentRecord, V3MLServiceContext oldRecord) throws Exception {
        boolean isMLModelVarDiff = false;
        boolean isWorkFlowDiff = false;


        JSONObject currentMlModelVariables = currentRecord.getMlModelVariables();
        JSONObject oldMlModelVariables = oldRecord.getMlModelVariables();
        if(oldMlModelVariables!=null && currentMlModelVariables!=null) {
            for(Object key : currentMlModelVariables.keySet()) {
                if(oldMlModelVariables.keySet().contains(key)) {
                    String oldValue = ""+oldMlModelVariables.get(key);
                    String currValue = ""+currentMlModelVariables.get(key);
                    if(!oldValue.equals(currValue)) {
                        isMLModelVarDiff = true;
                        break;
                    }
                }
            }
        } else if(oldMlModelVariables == null) {
            isMLModelVarDiff = true;
        }

//        boolean isMLModelVarDiff = MLServiceUtil.isDiff(currentRecord.getMlModelVariables(), oldRecord.getMlModelVariables());
        if(MLServiceUtil.isDiff(currentRecord.getWorkflowInfo(), oldRecord.getWorkflowInfo())) {
            MLServiceUtil.extractAndUpdateWorkflowID(currentRecord);
            isWorkFlowDiff = MLServiceUtil.isDiff(currentRecord.getWorkflowId(), oldRecord.getWorkflowId());
        }

        if(!isMLModelVarDiff && !isWorkFlowDiff) {
            return;
        }
        isUpdateNeeded = true;

        for(MLContext mlContext : mlContexts) {
            List<MLModelVariableContext> mlModelVariables = mlContext.getMLModelVariable();


            for(Object key : currentMlModelVariables.keySet()) {
                List<MLModelVariableContext> foundedMLModelVariables = mlModelVariables.stream().filter( row -> row.getVariableKey().equals((String)key) ).collect(Collectors.toList());
                String value = ""+currentMlModelVariables.get(key);
                if(foundedMLModelVariables == null) {
                    MLAPI.addMLModelVariables(mlContext.getId(), (String) key, value);
                } else {
                    for(MLModelVariableContext singleRow : foundedMLModelVariables) {
                        singleRow.setVariableValue(value);
                        MLAPI.updateMLModelVariables(singleRow);
                    }
                }
            }
        }

        for(Object key: oldMlModelVariables.keySet()) {
            if(currentMlModelVariables.keySet().contains(key)) {
                continue;
            }
            currentMlModelVariables.put(key, oldMlModelVariables.get(key));
        }
        currentRecord.setMlModelVariables(currentMlModelVariables);

    }


    private void updateDateTime(V3MLServiceContext currentRecord, V3MLServiceContext oldRecord) throws Exception {
        boolean isStartTimeDiff = MLServiceUtil.isDiff(currentRecord.getStartTime(), oldRecord.getStartTime());
        boolean isEndTimeDiff = MLServiceUtil.isDiff(currentRecord.getEndTime(), oldRecord.getEndTime());
        if(!isStartTimeDiff && !isEndTimeDiff) {
            return;
        }
        isUpdateNeeded = true;
        MLServiceUtil.validateDateRange(currentRecord);


        Long maxSamplingPeriod = currentRecord.getTrainingSamplingPeriod();
        long oneYearSampling = 365 * MLServiceUtil.DAYS_IN_MILLISECONDS;
        long tempMaxSamplingPeriod = maxSamplingPeriod;

        if(tempMaxSamplingPeriod < oneYearSampling) {
            tempMaxSamplingPeriod = oneYearSampling;
        }

        List<Long> assetIds = MLServiceUtil.getAllAssetIds(currentRecord);
        for(MLContext mlContext : mlContexts) {
            List<MLVariableContext> oldMlVariables = mlContext.getMLVariable();
            for(MLVariableContext mlVariableContext : oldMlVariables) {
                if(assetIds.contains(mlVariableContext.getParentID()) || !currentRecord.getModelName().equals("energyprediction")) {
                    mlVariableContext.setMaxSamplingPeriod(maxSamplingPeriod);
                } else {
                    mlVariableContext.setMaxSamplingPeriod(tempMaxSamplingPeriod);
                }

                MLAPI.updateMLVariables(mlVariableContext);
            }
        }
    }

}
