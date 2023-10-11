package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;


public class AutoCommissionPoints extends FacilioCommand {

    // WARNING: Not using this command now. Might be using this command later.
    private static final Logger LOGGER = LogManager.getLogger(AutoCommissionPoints.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        if (!agent.isAllowAutoMapping()) {
            return false;
        }
        JSONObject payload = (JSONObject) context.remove(AgentConstants.PAYLOAD);
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        Map<String, Point> pointRecords = (Map<String, Point>) context.get(FacilioConstants.ContextNames.DataProcessor.POINT_RECORDS);
        List<ReadingDataMeta> rdmList = new ArrayList<>();

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();

        if (pointRecords.isEmpty()) {
            LOGGER.info("Auto Commission :: " + " Points are empty");
            return true;
        }
        String fieldValue = getFieldValue(controller.getName(), payload);
        AssetContext asset = getAsset(agent.getAutoMappingParentFieldId(), fieldValue);
        if (asset == null) {
            LOGGER.info("Auto Commission :: Asset not found with field value of " + fieldValue + ", field id " + agent.getAutoMappingParentFieldId());
            return false;
        }
        commissionPoints(asset, pointRecords, batchUpdateList, rdmList);
        if (!batchUpdateList.isEmpty()) {
            updatePoint(batchUpdateList);
            if (!rdmList.isEmpty()) {
                List<String> fields = Arrays.asList("inputType", "readingType");
                ReadingsAPI.updateReadingDataMetaList(rdmList, fields);
            }
            if (!asset.isConnected()) {
                AssetsAPI.updateAssetConnectionStatus(Collections.singletonList(asset.getId()), true);
            }
        }
        return false;
    }

    private String getFieldValue(String controllerName, JSONObject payload) {
        if (payload.containsKey(AgentConstants.UNIQUE_ID)) {
            return payload.get(AgentConstants.UNIQUE_ID).toString();
        }
        return controllerName;
    }

    private void commissionPoints(AssetContext asset, Map<String, Point> pointRecords, List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList, List<ReadingDataMeta> rdm) throws Exception {
        long assetCategoryId = asset.getCategory().getId();
        List<FacilioField> categoryReadings = ReadingsAPI.getCategoryReadings(ModuleFactory.getAssetCategoryReadingRelModule(), assetCategoryId);
        Map<String, FacilioField> nameVsField = new HashMap<>();
        for (FacilioField categoryReading : categoryReadings) {
            nameVsField.put(categoryReading.getDisplayName(), categoryReading);
        }
        for (Point point : pointRecords.values()) {
            boolean isPointMapped = false;
            if (point.getCategoryId() != null && point.getResourceId() != null && point.getFieldId() != null) {
                LOGGER.debug("Auto Commission :: " + point.getName() + " already Commissioned!");
                continue;
            }
            if (point.getCategoryId() == null) {
                point.setCategoryId(assetCategoryId);
                isPointMapped = true;
            }
            if (point.getResourceId() == null) {
                point.setResourceId(asset.getId());
                isPointMapped = true;
            }
            if (point.getFieldId() == null) {
                FacilioField field = nameVsField.get(point.getName());
                if (field != null) {
                    point.setFieldId(field.getFieldId());
                    point.setMappedTime(System.currentTimeMillis());
                    point.setMappedType(PointEnum.MappedType.AUTO.getIntValue());
                    rdm.add(getRMD(point));
                    isPointMapped = true;
                }
            }
            if (isPointMapped) {
                LOGGER.info("Auto Commission :: " + point.getName() + " point Mapped. Field id : " + point.getFieldId() + ", Asset Id: " + point.getResourceId());
                addPointToBatchUpdateProp(point, batchUpdateList);
            }
        }
    }

    private ReadingDataMeta getRMD(Point point) {
        ReadingDataMeta meta = new ReadingDataMeta();
        meta.setResourceId(point.getResourceId());
        meta.setFieldId(point.getFieldId());

        meta.setInputType(ReadingDataMeta.ReadingInputType.CONTROLLER_MAPPED);
        if (point.isWritable()) {
            meta.setReadingType(ReadingDataMeta.ReadingType.WRITE);
        }
        meta.setValue("-1");
        return meta;
    }

    private AssetContext getAsset(long fieldId, String controllerName) throws Exception {
        return null;
    }

    public static void updatePoint(List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
        Map<String, FacilioField> fieldMap;
        if (pointModule == null) {
            pointModule = ModuleFactory.getPointModule();
            fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());

        } else {
            List<FacilioField> fields = new ArrayList<>(moduleBean.getAllFields(AgentConstants.POINT));
            fields.add(FieldFactory.getIdField(pointModule));
            fieldMap = FieldFactory.getAsMap(fields);
        }

        List<FacilioField> updateFields = getFacilioFieldList(fieldMap);

        updatePoints(pointModule, updateFields, batchUpdateList);
    }

    private static List<FacilioField> getFacilioFieldList(Map<String, FacilioField> fieldMap) {
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get(AgentConstants.ASSET_CATEGORY_ID));
        updateFields.add(fieldMap.get(AgentConstants.RESOURCE_ID));
        updateFields.add(fieldMap.get(AgentConstants.FIELD_ID));
        updateFields.add(fieldMap.get(AgentConstants.MAPPED_TIME));
        updateFields.add(fieldMap.get(AgentConstants.MAPPED_TYPE));
        return updateFields;
    }

    private static void updatePoints(FacilioModule pointModule, List<FacilioField> updateFields, List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList) throws SQLException {
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(pointModule.getTableName())
                .fields(updateFields);

        updateBuilder.batchUpdateById(batchUpdateList);
    }

    private void addPointToBatchUpdateProp(Point point, List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList) {
        GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
        batchValue.setWhereId(point.getId());
        batchValue.addUpdateValue(AgentConstants.MAPPED_TIME, point.getMappedTime());

        Long categoryId = point.getCategoryId();
        if (categoryId != null && categoryId > 0) {
            batchValue.addUpdateValue(AgentConstants.ASSET_CATEGORY_ID, point.getCategoryId());

            Long resourceId = point.getResourceId();
            if (resourceId != null && resourceId > 0) {
                batchValue.addUpdateValue(AgentConstants.RESOURCE_ID, resourceId);
            }

            Long fieldId = point.getFieldId();
            if (fieldId != null && fieldId > 0) {
                batchValue.addUpdateValue(AgentConstants.FIELD_ID, fieldId);
            }
            batchValue.addUpdateValue(AgentConstants.MAPPED_TYPE, point.getMappedType());
        }

        batchUpdateList.add(batchValue);
    }
}
