package com.facilio.bmsconsole.jobs;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.BmsPointsTaggingUtil;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class MLBmsPointsTaggingJob extends FacilioJob {
    private static final Logger LOGGER = Logger.getLogger(MLBmsPointsTaggingJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            LOGGER.info("Inside MLBmsPointsTaggingJob, JOB ID :" + jc.getJobId());
            JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
            String fieldNameString = (String) props.get("fieldNames");
            String[] fieldNames = fieldNameString.split(",");
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule assetModule = modBean.getModule("asset");
            List<FacilioField> assetFields = modBean.getAllFields(assetModule.getName());
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(assetFields);
            List<FacilioField> selectedFields = new ArrayList<FacilioField>();
            selectedFields.add(fieldsMap.get("name"));
            selectedFields.add(FieldFactory.getIdField(assetModule));
            for (String eachField : fieldNames) {
                selectedFields.add(fieldsMap.get(eachField));
            }
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(selectedFields)
                    .table("Assets")
                    .innerJoin("Resources")
                    .on("Assets.ID = Resources.ID");
            List<Map<String, Object>> selectedRecords = selectBuilder.get();
            List<AssetCategoryContext> categoryContextList = AssetsAPI.getCategoryList();
            Map<Long, String> categoryIdVsName = new HashMap<>();
            for (AssetCategoryContext eachContext : categoryContextList) {
                categoryIdVsName.put(eachContext.getId(), eachContext.getDisplayName());
            }
            Set<Long> categoryIds = new HashSet<>();
            for (Map<String, Object> eachRecord : selectedRecords) {
                categoryIds.add((Long) eachRecord.get("category"));
            }
            List<Long> categoryIdsList = new ArrayList<>(categoryIds);
            FacilioModule module = ModuleFactory.getAssetCategoryReadingRelModule();
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, categoryIdsList);
            FacilioChain getCategoryReadingChain = FacilioChainFactory.getAllCategoryReadingsChain();
            getCategoryReadingChain.execute(context);
            Map<Long, List<FacilioModule>> moduleMap = (Map<Long, List<FacilioModule>>) context.get(FacilioConstants.ContextNames.MODULE_MAP);
            Map<Long, Map<Long, String>> categoryVsFields = new HashMap<>();
            for (long categoryId : moduleMap.keySet()) {
                List<FacilioModule> modulesList = moduleMap.get(categoryId);
                Map<Long, String> fieldsMapNew = new HashMap<>();
                for (FacilioModule eachModule : modulesList) {
                    fieldsMapNew.putAll(eachModule.getFields().stream().collect(Collectors.toMap(field -> field.getFieldId(), field -> field.getDisplayName())));
                }
                categoryVsFields.put(categoryId, fieldsMapNew);
            }
            BmsPointsTaggingUtil.createMlDb(categoryVsFields, categoryIdVsName, selectedRecords, jc.getOrgId());
            LOGGER.info("Finished MLBmsPointsTaggingJob, JOB ID :" + jc.getJobId());
        } catch (Exception e) {
            LOGGER.fatal("Error in MLBmsPointsTaggingJob" + e);
        }
    }
}
