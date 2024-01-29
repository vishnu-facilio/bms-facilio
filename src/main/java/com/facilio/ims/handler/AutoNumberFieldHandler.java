package com.facilio.ims.handler;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.AutoNumberFieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.AutoNumberField;
import com.facilio.modules.fields.FacilioField;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoNumberFieldHandler extends ImsHandler {

    public static final Logger LOGGER = LogManager.getLogger(AutoNumberFieldHandler.class.getName());

    public static final String KEY = "__auto_number_field_handle__";

    protected static final Object LOCK = new Object();

    private static final String ID_COUNT = "__idCount";
    private static final String ID_MIN = "__idMin";
    private static final String ID_MAX = "__idMax";

    @SneakyThrows
    @Override
    public void processMessage(Message message) {

        Connection conn = null;
        boolean olderCommit = false;
        AutoNumberField autoNumberField = null;

        try {

            JSONObject content = message.getContent();
            autoNumberField = FieldUtil.getAsBeanFromMap(content, AutoNumberField.class);
            FacilioModule module = autoNumberField.getModule();
            long moduleId = module.getModuleId();
            long fieldId = autoNumberField.getFieldId();

            FacilioField idField = FieldFactory.getIdField(module);

            List<FacilioField> aggregateFields = new ArrayList<>();

            FacilioField count = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(idField);
            count.setName(ID_COUNT);
            aggregateFields.add(count);
            FacilioField min = BmsAggregateOperators.NumberAggregateOperator.MIN.getSelectField(idField);
            min.setName(ID_MIN);
            aggregateFields.add(min);
            FacilioField max = BmsAggregateOperators.NumberAggregateOperator.MAX.getSelectField(idField);
            max.setName(ID_MAX);
            aggregateFields.add(max);

            SelectRecordsBuilder<ModuleBaseWithCustomFields> aggregateBuilder = new SelectRecordsBuilder<>()
                    .module(module)
                    .setAggregation()
                    .skipModuleCriteria()
                    .select(aggregateFields)
                    .orderBy(idField.getCompleteColumnName());

            Map<String, Object> aggregateValue = aggregateBuilder.getAsProps().get(0);

            if (MapUtils.isEmpty(aggregateValue)) {
                return;
            }

            int minRecordId = Integer.parseInt(aggregateValue.get(ID_MIN).toString());
            int maxRecordId = Integer.parseInt(aggregateValue.get(ID_MAX).toString());
            int recordCount = Integer.parseInt(aggregateValue.get(ID_COUNT).toString());
            long orgId = AccountUtil.getCurrentOrg().getOrgId();

            conn = FacilioConnectionPool.getInstance().getDirectConnection();
            olderCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            AutoNumberFieldUtil.updateAutoNumberIdValue(autoNumberField, autoNumberField.getIdStartsFrom() + recordCount, conn);
            conn.commit();

            if (recordCount > 0) {

                LOGGER.debug("Auto number field :" + fieldId + " records update initiated");

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

            if (conn != null) {
                conn.rollback();
                LOGGER.debug("Rolled back connection while getting auto number records value update for field : " + autoNumberField.getFieldId());
            }

        } finally {
            if (conn != null) {
                conn.setAutoCommit(olderCommit);
            }
            DBUtil.close(conn);
        }

    }
}
