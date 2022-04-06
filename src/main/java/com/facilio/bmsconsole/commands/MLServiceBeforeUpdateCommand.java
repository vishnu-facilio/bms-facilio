package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class MLServiceBeforeUpdateCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(MLServiceBeforeUpdateCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3MLServiceContext> mlServiceContexts = recordMap.get(moduleName);

        if(CollectionUtils.isEmpty(mlServiceContexts)) {
            return true;
        }

        V3MLServiceContext currentRecord = MLServiceUtil.extractMLMeta(mlServiceContexts.get(0));
        V3MLServiceContext oldRecord = getOldRecord(currentRecord);
        context.put(MLServiceUtil.MLSERVICE_OLD_CONTEXT, oldRecord);

        List<MLContext> mlContexts = MLServiceUtil.getMLRecordsByMLServiceId(currentRecord);
        context.put(MLServiceUtil.ML_CONTEXT, mlContexts);

        MLServiceUtil.assertEquals("modelName",currentRecord.getModelName(), oldRecord.getModelName());
        if(mlContexts != null) {
            unModifiableVariables(currentRecord, oldRecord);
            if(currentRecord.getMlID() == null) {
                currentRecord.setMlID(mlContexts.get(0).getId());
            }
            context.put(MLServiceUtil.MLSERVICE_CONTEXT, currentRecord);
        } else {
            updateCurrentContext(currentRecord, oldRecord);
        }
        return false;
    }

    private void updateCurrentContext(V3MLServiceContext currentRecord, V3MLServiceContext oldRecord) throws RESTException {
        if(currentRecord.getParentAssetId() == null) {
            currentRecord.setParentAssetId(oldRecord.getParentAssetId());
        }
        if(currentRecord.getProjectName() == null) {
            currentRecord.setProjectName(oldRecord.getProjectName());
        }
        if(currentRecord.getMlModelVariables() == null) {
            currentRecord.setMlModelVariables(oldRecord.getMlModelVariables());
        } else {
            currentRecord.setMlModelVariables(update(currentRecord.getMlModelVariables(), oldRecord.getMlModelVariables()));
        }

        if(currentRecord.getStartTime() == null) {
            currentRecord.setStartTime(oldRecord.getStartTime());
        }
        if(currentRecord.getEndTime() == null) {
            currentRecord.setEndTime(oldRecord.getEndTime());
        }
        if(currentRecord.getWorkflowInfo() == null) {
            currentRecord.setWorkflowInfo(oldRecord.getWorkflowInfo());
        } else {
            currentRecord.setWorkflowInfo(update(currentRecord.getWorkflowInfo(), oldRecord.getWorkflowInfo()));
        }
        if(currentRecord.getServiceType() == null) {
            currentRecord.setServiceType(oldRecord.getServiceType());
        }
    }

    private JSONObject update(JSONObject newData, JSONObject oldData) {
        if(oldData!=null) {
            for (Object key : oldData.keySet()) {
                if (newData.keySet().contains(key)) {
                    continue;
                }
                newData.put(key, oldData.get(key));
            }
        }
        return newData;
    }

    private V3MLServiceContext getOldRecord(V3MLServiceContext currentRecord) throws Exception {
        V3MLServiceContext oldRecord = MLServiceUtil.getMLServiceRecord(currentRecord.getId());
        oldRecord = MLServiceUtil.extractMLMeta(oldRecord);
        return oldRecord;
    }


    private void unModifiableVariables(V3MLServiceContext currentRecord, V3MLServiceContext oldRecord) throws RESTException {
        MLServiceUtil.assertEquals("modelName",currentRecord.getModelName(), oldRecord.getModelName());
        MLServiceUtil.assertEquals("projectName",currentRecord.getProjectName(), oldRecord.getProjectName());
        MLServiceUtil.assertEquals("serviceType",currentRecord.getServiceType(), oldRecord.getServiceType());
        MLServiceUtil.assertEquals("parentAssetId",currentRecord.getParentAssetId(), oldRecord.getParentAssetId());
        MLServiceUtil.assertEquals("failed",currentRecord.isFailed(), oldRecord.isFailed());
        MLServiceUtil.assertEquals("historic",currentRecord.isHistoric(), oldRecord.isHistoric());
        MLServiceUtil.assertEquals("mlID",currentRecord.getMlID(), oldRecord.getMlID());
        MLServiceUtil.assertEquals("status",currentRecord.getStatus(), oldRecord.getStatus());
    }

}
