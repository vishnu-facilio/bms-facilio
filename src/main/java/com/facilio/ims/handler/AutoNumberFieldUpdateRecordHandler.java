package com.facilio.ims.handler;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AutoNumberFieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.AutoNumberField;
import com.facilio.modules.fields.FacilioField;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AutoNumberFieldUpdateRecordHandler extends ImsHandler {

    public static final Logger LOGGER = LogManager.getLogger(AutoNumberFieldUpdateRecordHandler.class.getName());

    public static final String KEY = "__auto_number_field_update_record_handle__";

    private static final int RECORD_SELECT_LIMIT = 5000;

    @SneakyThrows
    @Override
    public void processMessage(Message message) {

        int minRecordId = 0;
        int maxRecordId = 0;
        AutoNumberField autoNumberField = null;
        List<Map<String, Object>> props = new ArrayList<>();

        try {

            JSONObject content = message.getContent();
            autoNumberField = FieldUtil.getAsBeanFromMap((Map<String, Object>) content.get(FacilioConstants.FormContextNames.AUTO_NUMBER_FIELD), AutoNumberField.class);
            minRecordId = Integer.parseInt(content.get(FacilioConstants.Reports.MIN_FUNC).toString());
            maxRecordId = Integer.parseInt(content.get(FacilioConstants.Reports.MAX_FUNC).toString());
            int recordCount = Integer.parseInt(content.get(FacilioConstants.Reports.COUNT_COLUMN).toString());

            LOGGER.debug("ims after pick autonumber AutoNumberFieldUpdateRecordHandler." + autoNumberField);

            int idStartsFrom = autoNumberField.getIdStartsFrom();
            long fieldId = autoNumberField.getFieldId();
            FacilioModule module = autoNumberField.getModule();
            long moduleId = module.getModuleId();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            FacilioField idField = FieldFactory.getIdField(module);
            long orgId = AccountUtil.getCurrentOrg().getOrgId();

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            int autoNumberId = idStartsFrom;

            SelectRecordsBuilder selectBuilder = new SelectRecordsBuilder<>();
            selectBuilder.module(module)
                    .select(fields)
                    .skipModuleCriteria()
                    .limit(RECORD_SELECT_LIMIT)
                    .andCondition(CriteriaAPI.getCondition(idField, String.valueOf(minRecordId), NumberOperators.GREATER_THAN_EQUAL))
                    .andCondition(CriteriaAPI.getCondition(idField, String.valueOf(maxRecordId), NumberOperators.LESS_THAN_EQUAL))
                    .orderBy(idField.getCompleteColumnName());

            props = selectBuilder.getAsProps();

            LOGGER.debug("ims after pick autonumber AutoNumberFieldUpdateRecordHandler field size:" + props.size());

            List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

            if (CollectionUtils.isEmpty(props)) {
                return;
            }

            for (Map<String, Object> prop : props) {

                GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                updateVal.addWhereValue("id", prop.get("id"));
                updateVal.addUpdateValue(autoNumberField.getName(), AutoNumberFieldUtil.constructAutoNumberValue(autoNumberField, autoNumberId));
                batchUpdateList.add(updateVal);

                minRecordId = Integer.parseInt(prop.get(idField.getName()).toString());
                autoNumberId++;
            }

            autoNumberField.setIdStartsFrom(autoNumberId);
            minRecordId++;

            List<FacilioField> whereFields = new ArrayList<>();
            whereFields.add(idField);

            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(Collections.singletonList(fieldMap.get(autoNumberField.getName())));
            updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);

            LOGGER.debug("Auto number field :" + autoNumberField.getFieldId() + " records size :" + props.size() + "updated");

            if (autoNumberField != null && minRecordId <= maxRecordId) {

                JSONObject updateContent = new JSONObject();
                updateContent.put(FacilioConstants.FormContextNames.AUTO_NUMBER_FIELD, autoNumberField);
                updateContent.put(FacilioConstants.Reports.MAX_FUNC, maxRecordId);
                updateContent.put(FacilioConstants.Reports.MIN_FUNC, minRecordId);
                updateContent.put(FacilioConstants.Reports.COUNT_COLUMN, recordCount);

                Messenger.getMessenger().sendMessage(new Message()
                        .setKey(AutoNumberFieldUpdateRecordHandler.KEY + "/" + orgId + "/" + moduleId + "/" + fieldId)
                        .setOrgId(orgId)
                        .setContent(updateContent));

            }

        } catch (Exception e) {
            LOGGER.error("Exception on auto number field record update", e);
        } finally {

            LOGGER.debug("ims finally pick autonumber AutoNumberFieldUpdateRecordHandler maxRecordId :" +maxRecordId+" minRecordId : "+minRecordId);

            if (minRecordId >= maxRecordId) {

                FacilioModule autoNumberFieldsModule = ModuleFactory.getAutoNumberFieldModule();
                autoNumberField.setStatus(AutoNumberField.AutoNumberFieldStatus.COMPLETED);
                Map<String, Object> fieldProps = FieldUtil.getAsProperties(autoNumberField);

                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(autoNumberFieldsModule.getTableName())
                        .fields(Collections.singletonList(FieldFactory.getField("status", "STATUS", autoNumberFieldsModule, FieldType.NUMBER)))
                        .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(autoNumberField.getFieldId()), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(autoNumberField.getModuleId()), NumberOperators.EQUALS));

                updateRecordBuilder.update(fieldProps);

                LOGGER.debug("Auto number field :" + autoNumberField.getFieldId() + " records update Completed");

            }
        }
    }
}