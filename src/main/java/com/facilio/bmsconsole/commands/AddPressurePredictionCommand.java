package com.facilio.bmsconsole.commands;
        
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;

public class AddPressurePredictionCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddPressurePredictionCommand.class.getName());
    private static final long DAYS_IN_MILLISECONDS = 24 * 60 * 60 * 1000l;

    @Override
    public boolean executeCommand(Context jc) throws Exception {
        V3MLServiceContext mlServiceContext = (V3MLServiceContext) jc.get(FacilioConstants.ContextNames.ML_SERVICE_DATA);

        long mlServiceId = -1;
        if (mlServiceContext != null) {
            mlServiceId = mlServiceContext.getId();
        }
        try {
            LOGGER.info("Inside Pressure Prediction Command");
            long assetId = (long) jc.get("assetId");
            AssetContext assetContext = AssetsAPI.getAssetInfo(assetId);

            if (assetContext != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                            /*
                FacilioModule module = modBean.getModule("pressureprediction");
                List<FacilioField> fields = modBean.getAllFields(module.getName()) ;
                MLAPI.addReading(Collections.singletonList(assetId),"pressureprediction", fields, ModuleFactory.getMLReadingModule().getTableName(),module);
                */
                FacilioModule logModule = modBean.getModule("pressurepredictionnmllogreadingsnew");
                List<FacilioField> fields = logModule != null ? modBean.getAllFields(logModule.getName()) : FieldFactory.getPressurePredictionLogReadingFields();
                MLAPI.addReading(Collections.singletonList(assetId), "pressurepredictionnmllogreadingsnew", fields, ModuleFactory.getMLLogReadingModule().getTableName(), FacilioModule.ModuleType.PREDICTED_READING, logModule);

                FacilioModule module = modBean.getModule("pressurepredictionnmlreadingsnew");
                fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getPressurePredictionReadingFields();
                MLAPI.addReading(Collections.singletonList(assetId), "pressurepredictionnmlreadingsnew", fields, ModuleFactory.getMLReadingModule().getTableName(), module);

                long mlID = createMlForPressurePrediction(assetContext, (JSONObject) jc.get("mlModelVariables"), (JSONObject) jc.get("mlVariables"), (String) jc.get("modelPath"), mlServiceId);
                if (mlServiceContext != null) {
                    mlServiceContext.setMlID(mlID);
                }
                scheduleJob(mlID, mlServiceContext);
                LOGGER.info("After updating pressure prediction");
            } else {
                String errMsg = "Asset ID " + assetId + " is not available";
                if (mlServiceContext != null) {
                    throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.RESOURCE_NOT_FOUND, errMsg);
                }
                LOGGER.info(errMsg);
            }
            return false;
        } catch (Exception e) {
            String errMsg = "Error while adding Pressure Prediction Job";
            if (mlServiceContext != null) {
                throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, errMsg);
            }
            LOGGER.error(errMsg, e);
            throw e;
        }
    }

    private long createMlForPressurePrediction(AssetContext assetContext, JSONObject mlModelVariables, JSONObject mlVariables, String modelPath, long mlServiceId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule logReadingModule = modBean.getModule("pressurepredictionnmllogreadingsnew");
        FacilioModule readingModule = modBean.getModule("pressurepredictionnmlreadingsnew");
        FacilioField pressureField = modBean.getField("bagFilterDifferentialPressure", FacilioConstants.ContextNames.AHU_READINGS_GENERAL);
        FacilioField pressureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.AHU_READINGS_GENERAL);
        long mlID = MLAPI.addMLModel(modelPath, assetContext.getName(), logReadingModule.getModuleId(), readingModule.getModuleId(), mlServiceId);
        Map<String, Long> maxSamplingPeriodMap = new HashMap<String, Long>();
        Map<String, Long> futureSamplingPeriodMap = new HashMap<String, Long>();
        Map<String, String> aggregationMap = new HashMap<String, String>();
        if (mlVariables != null) {
            for (Object entry : mlVariables.entrySet()) {
                Map.Entry en = (Map.Entry) entry;
                if (((JSONObject) en.getValue()).containsKey("maxSamplingPeriod")) {
                    maxSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject) en.getValue()).get("maxSamplingPeriod").toString()));
                }
                if (((JSONObject) en.getValue()).containsKey("futureSamplingPeriod")) {
                    futureSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject) en.getValue()).get("futureSamplingPeriod").toString()));
                }
                if (((JSONObject) en.getValue()).containsKey("aggregation")) {
                    aggregationMap.put(en.getKey().toString(), ((JSONObject) en.getValue()).get("aggregation").toString().toUpperCase());
                }
            }
        }
        MLAPI.addMLVariables(mlID, pressureField.getModuleId(), pressureField.getFieldId(), pressureParentField.getFieldId(),
                assetContext.getId(), maxSamplingPeriodMap.containsKey(pressureField.getName()) ?
                        maxSamplingPeriodMap.get(pressureField.getName()) : 7776000000l,
                futureSamplingPeriodMap.containsKey(pressureField.getName()) ? futureSamplingPeriodMap.get(pressureField.getName()) : 0,
                true, aggregationMap.containsKey(pressureField.getName()) ? aggregationMap.get(pressureField.getName()) : "SUM");
        MLAPI.addMLModelVariables(mlID, "timezone", AccountUtil.getCurrentAccount().getTimeZone());
        MLAPI.addMLModelVariables(mlID, "asset_id", String.valueOf(assetContext.getId()));

        for (Object entry : mlModelVariables.entrySet()) {
            Map.Entry<String, Object> en = (Map.Entry) entry;
            MLAPI.addMLModelVariables(mlID, en.getKey(), String.valueOf(en.getValue()));
        }
        return mlID;
    }

    private void scheduleJob(long mlID, V3MLServiceContext mlServiceContext) throws Exception {
        boolean isPastData = false;
        if (mlServiceContext != null) {
            isPastData = mlServiceContext.isHistoric();
        }
        try {
            if (!isPastData) {
                ScheduleInfo info = new ScheduleInfo();
                info = FormulaFieldAPI.getSchedule(FacilioFrequency.HOURLY);
                MLAPI.addJobs(mlID, "DefaultMLJob", info, "ml");
            }
        } catch (InterruptedException e) {
            Thread.sleep(1000);
        }
    }
}