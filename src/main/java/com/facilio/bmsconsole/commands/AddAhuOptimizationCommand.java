package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;

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
public class AddAhuOptimizationCommand extends FacilioCommand {


        private static final Logger LOGGER = Logger.getLogger(AddAhuOptimizationCommand.class.getName());
        private static final long DAYS_IN_MILLISECONDS = 24*60*60*1000l;
        @Override
        public boolean executeCommand(Context jc) throws Exception {
            V3MLServiceContext mlServiceContext = (V3MLServiceContext) jc.get(FacilioConstants.ContextNames.ML_SERVICE_DATA);

            long mlServiceId = -1;
            if(mlServiceContext!=null) {
                mlServiceId = mlServiceContext.getId();
            }
            try
            {
                LOGGER.info("Inside Ahu Optimization Command");
                long assetId=(long) jc.get("assetId");
                AssetContext assetContext = AssetsAPI.getAssetInfo(assetId);

                if(assetContext != null){
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

                FacilioModule module = modBean.getModule("ahustarttimeprediction");
                List<FacilioField> fields = modBean.getAllFields(module.getName()) ;
                MLAPI.addReading(Collections.singletonList(assetId),"ahustarttimeprediction", fields, ModuleFactory.getMLReadingModule().getTableName(),module);



                    long mlID = createMlForAhuOptimization(mlServiceContext,assetContext, (JSONObject) jc.get("mlModelVariables"), (JSONObject) jc.get("mlVariables"),mlServiceId);
                    if(mlServiceContext!=null) {
                        mlServiceContext.setMlID(mlID);
                    }
                    scheduleJob(mlID, mlServiceContext);
                    LOGGER.info("After updating ahu optimization");
                }else{
                    String errMsg = "Asset ID "+ assetId+ "is not available";
                    if(mlServiceContext!=null) {
                        throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.RESOURCE_NOT_FOUND, errMsg);
                    }
                    LOGGER.info(errMsg);
                }
                return false;
            }
            catch(Exception e)
            {
                String errMsg = "Error while adding Ahu Optimization Job";
                if(mlServiceContext!=null) {
                    throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, errMsg);
                }
                LOGGER.error(errMsg, e);
                throw e;
            }
        }
        private long createMlForAhuOptimization(V3MLServiceContext mlServiceContext,AssetContext assetContext, JSONObject mlModelVariables, JSONObject mlVariables,long mlServiceId)throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule readingModule = modBean.getModule("ahustarttimeprediction");
            long mlID = MLAPI.addMLModel("ahu_model_building",assetContext.getName(),-1,readingModule.getModuleId(), mlServiceId);
            Map<String, Long> maxSamplingPeriodMap = new HashMap<String, Long>();
            Map<String, Long> futureSamplingPeriodMap = new HashMap<String, Long>();
            Map<String, String> aggregationMap = new HashMap<String, String>();
            List<Map<String , Object>> readingFieldsDetails = mlServiceContext.getModelReadings().get(0);

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
            boolean isSource = true;
            for(Map<String, Object> map : readingFieldsDetails) {
                String fieldName = (String) map.get("name");
                if(fieldName.equalsIgnoreCase("temperature")){
                    continue;
                }
                long fieldId = (long)map.get("fieldId");
                FacilioField variableField = modBean.getField(fieldId);
                FacilioModule module = variableField.getModule();
                FacilioField parentField = modBean.getField("parentId", module.getName());
                AddAhuOptimizationCommand.addMLVariables(mlID,variableField,parentField,assetContext.getId(), isSource,maxSamplingPeriodMap, futureSamplingPeriodMap, aggregationMap);
                isSource = false;
            }
            FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
            FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
            AddAhuOptimizationCommand.addMLVariables(mlID,temperatureField,temperatureParentField,assetContext.getSiteId(),false,maxSamplingPeriodMap,futureSamplingPeriodMap,aggregationMap);

            MLAPI.addMLModelVariables(mlID, "timezone", AccountUtil.getCurrentAccount().getTimeZone());
            MLAPI.addMLModelVariables(mlID, "asset_id", String .valueOf(assetContext.getId()));
            MLAPI.addMLModelVariables(mlID,"dataInterval",String.valueOf(ReadingsAPI.getOrgDefaultDataIntervalInMin()));

            for (Object entry : mlModelVariables.entrySet()) {
                Map.Entry<String, Object> en = (Map.Entry) entry;
                MLAPI.addMLModelVariables(mlID, en.getKey(), String.valueOf(en.getValue()));
            }
            return mlID;
        }
        private void scheduleJob(long mlID, V3MLServiceContext mlServiceContext) throws Exception {
            boolean isPastData = false;
            if(mlServiceContext!=null) {
                isPastData = mlServiceContext.isHistoric();
            }
            try {
                if(!isPastData) {
                    ScheduleInfo info = new ScheduleInfo();
                    info = FormulaFieldAPI.getSchedule(FacilioFrequency.HOURLY);
                    MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
                }
            } catch (InterruptedException e) {
                Thread.sleep(1000);
            }
        }
        private static void addMLVariables(Long mlID , FacilioField field , FacilioField parentField , Long assetId,boolean isSource,
                                           Map<String,Long> maxSamplingPeriodMap,
                                           Map<String,Long>futureSamplingPeriodMap, Map<String,String> aggregationMap)throws Exception{
            Long futureSamplingPeriod=0L;
            if(field.getName().equalsIgnoreCase("temperature")) {
                futureSamplingPeriod = 172800000L;
            }
            MLAPI.addMLVariables(mlID, field.getModuleId(), field.getFieldId(), parentField.getFieldId(),
                    assetId, maxSamplingPeriodMap.containsKey(field.getName()) ?
                            maxSamplingPeriodMap.get(field.getName()) : 7776000000l,
                    futureSamplingPeriodMap.containsKey(field.getName()) ? futureSamplingPeriodMap.get(field.getName()) : futureSamplingPeriod,
                    isSource, aggregationMap.containsKey(field.getName()) ? aggregationMap.get(field.getName()) : "SUM");
        }
}
