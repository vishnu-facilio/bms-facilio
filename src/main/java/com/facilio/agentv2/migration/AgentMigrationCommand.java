package com.facilio.agentv2.migration;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.migration.beans.AgentMigrationBean;
import com.facilio.agentv2.point.Point;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AgentMigrationCommand extends FacilioCommand {

    long sourceOrgId;
    long targetOrgId;
    long sourceAgentId;
    long targetAgentId;
    List<ReadingDataMeta> rdmList = new ArrayList<>();


    AgentMigrationBean sourceBean;
    AgentMigrationBean targetBean;
    DataMigrationBean dataMigrationBean;

    Map<Long, Long> sourceVsTargetResources = new HashMap<>();


    @Override
    public boolean executeCommand(Context context) throws Exception {

        sourceOrgId = (long) context.get(AgentMigrationConstants.SOURCE_ORG_ID);
        sourceAgentId = (long) context.get(AgentMigrationConstants.SOURCE_AGENT_ID);
        targetOrgId = (long) context.get(AgentMigrationConstants.TARGET_ORG_ID);
        targetAgentId = (long) context.get(AgentMigrationConstants.TARGET_AGENT_ID);
        long migrationId = (long) context.get("migrationId");

        sourceBean = (AgentMigrationBean) BeanFactory.lookup("AgentMigrationBean", true, sourceOrgId);
        targetBean = (AgentMigrationBean) BeanFactory.lookup("AgentMigrationBean", true, targetOrgId);


        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);
        DataMigrationBean dataMigrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        Map<Long, Long> sourceVsTargetCategories = AgentMigrationUtil.getSourceVsTargetCategories(sourceOrgId, targetOrgId);

        Map<Long, Map<Long,String>> sourceReadingFields = new HashMap<>();
        Map<Long, Map<String,Long>> targetReadingFields = new HashMap<>();
        Map<Long, Long> oldVsTargetFields = new HashMap<>();

        FacilioModule controllerModule = moduleBean.getModule(FacilioConstants.ContextNames.CONTROLLER);
        FacilioModule assetModule = moduleBean.getModule(FacilioConstants.ContextNames.ASSET);

        Map<FacilioControllerType, List<Long>> controllerMap = sourceBean.fetchControllers(sourceAgentId);
        for (Map.Entry<FacilioControllerType, List<Long>> entry : controllerMap.entrySet()) {
            FacilioControllerType type = entry.getKey();
            List<Long> ids = entry.getValue();

            Map<Long, Long> sourceVsTargetControllers = dataMigrationBean.getOldVsNewId(migrationId, controllerModule.getModuleId(), ids);
            targetBean.updateController(type, sourceAgentId, targetAgentId);

            List<Point> points = sourceBean.fetchPoints(sourceAgentId, ids);
            fetchSourceVsTargetResources(dataMigrationBean, migrationId, assetModule.getModuleId(), points);
            migratePoints(points, type, sourceVsTargetControllers, sourceVsTargetCategories, sourceReadingFields, targetReadingFields, oldVsTargetFields);
            LOGGER.info("Migration completed for " + type.toString());
        }

        if (!rdmList.isEmpty()) {
            List<String> fields = Arrays.asList("inputType", "readingType", "value");
            ReadingsAPI.updateReadingDataMetaList(rdmList, fields);
        }

        // TODO migrate input values for enum,boolean points if applicable

        LOGGER.info("Agent migration completed for " + targetAgentId);
        return false;
    }

    private void fetchSourceVsTargetResources(DataMigrationBean dataMigrationBean, long migrationId, long moduleId, List<Point> points) throws Exception {
        List<Long> resourceIds = new ArrayList<>();
        for (Point point: points) {
            if (point.getResourceId() != null && point.getResourceId() > 0) {
                if (!sourceVsTargetResources.containsKey(point.getResourceId())) {
                    resourceIds.add(point.getResourceId());
                }
            }
        }

        if (!resourceIds.isEmpty()) {
            Map<Long, Long> oldVsNewIds = dataMigrationBean.getOldVsNewId(migrationId, moduleId, resourceIds);
            sourceVsTargetResources.putAll(oldVsNewIds);
        }
    }

    private Map<Long,String> getSourceFieldsForCategory(long categoryId) throws Exception {
        List<FacilioField> fields = sourceBean.fetchReadings(categoryId);
        Map<Long,String> sourceFields = fields.stream().collect(Collectors.toMap(FacilioField::getId, FacilioField::getName));
        return sourceFields;
    }

    private Map<String,Long> getTargetFieldsForCategory(long categoryId) throws Exception {
        List<FacilioField> fields = targetBean.fetchReadings(categoryId);
        Map<String,Long> targetFields = fields.stream().collect(Collectors.toMap(FacilioField::getName, FacilioField::getId));
        return targetFields;
    }

    private void migratePoints(List<Point> points, FacilioControllerType type,
                               Map<Long, Long> sourceVsTargetControllers,
                               Map<Long, Long> sourceVsTargetCategories,
                               Map<Long, Map<Long,String>> sourceReadingFields,
                               Map<Long, Map<String,Long>> targetReadingFields,
                               Map<Long, Long> sourceVsTargetFields) throws Exception {

        List<Map<String, Object>> pointsToAdd = new ArrayList<>();
        for(Point point: points) {
            point.setAgentId(targetAgentId);
            point.setControllerId(sourceVsTargetControllers.get(point.getControllerId()));

            Long sourceCategoryId = point.getCategoryId();
            Long targetCategoryId = null;
            if (sourceCategoryId != null && sourceCategoryId > 0) {
                targetCategoryId = sourceVsTargetCategories.get(sourceCategoryId);
                point.setCategoryId(targetCategoryId);
            }
            Long newResourceId = null;
            Long newFieldId = null;
            if (point.getResourceId() != null && point.getResourceId() > 0) {
                newResourceId = sourceVsTargetResources.get(point.getResourceId());
                point.setResourceId(newResourceId);
            }

            if (point.getFieldId() != null && point.getFieldId() > 0) {
                newFieldId = sourceVsTargetFields.get(point.getFieldId());
                if (newFieldId == null) {

                    Map<Long, String> sourceFieldMap = sourceReadingFields.get(sourceCategoryId);
                    if (sourceFieldMap == null) {
                        sourceFieldMap = getSourceFieldsForCategory(sourceCategoryId);
                        sourceReadingFields.put(sourceCategoryId, sourceFieldMap);

                        Map<String, Long> targetFieldMap = getTargetFieldsForCategory(targetCategoryId);
                        targetReadingFields.put(targetCategoryId, targetFieldMap);
                    }

                    String fieldName = sourceFieldMap.get(point.getFieldId());
                    newFieldId = targetReadingFields.get(targetCategoryId).get(fieldName);
                }
                point.setFieldId(newFieldId);
            }
            if(newResourceId != null && newFieldId != null) {
                addToRdmList(newResourceId, newFieldId, point.isWritable());
            }

            pointsToAdd.add(FieldUtil.getAsProperties(point.toJSON()));
        }
        targetBean.addPoints(type, pointsToAdd);
    }

    private void addToRdmList(long resourceId, long fieldId, boolean writable) {
        ReadingDataMeta meta = new ReadingDataMeta();
        meta.setResourceId(resourceId);
        meta.setFieldId(fieldId);
        meta.setInputType(ReadingDataMeta.ReadingInputType.CONTROLLER_MAPPED);
        if (writable) {
            meta.setReadingType(ReadingDataMeta.ReadingType.WRITE);
        }
        meta.setValue("-1");
        rdmList.add(meta);
    }

}
