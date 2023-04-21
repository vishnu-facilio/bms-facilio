package com.facilio.bmsconsole.commands;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.featureAccess.FeatureLockEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import com.facilio.util.FacilioUtil;

import java.util.*;

public class UpdateFeatureLockCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isLocked = (boolean) context.get(FacilioConstants.FeatureAccessConstants.ACCESS_PERMISSION);
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.FeatureAccessConstants.RECORD_IDS);
        Integer featureInt = (Integer) context.get(FacilioConstants.FeatureAccessConstants.FEATURE);
        FeatureLockEnum featureEnum = FeatureLockEnum.TYPE_MAP.get(featureInt);

        FacilioUtil.throwIllegalArgumentException(featureInt < 1, "Feature cannot be null");

        FacilioModule module;
        if (CollectionUtils.isNotEmpty(recordIds)) {
            switch (featureEnum) {
                case FORMS:
                    module = ModuleFactory.getFormModule();
                    updateRecords(module, FieldFactory.getIdField(module), recordIds, isLocked);
                    break;

                case FIELDS:
                    module = ModuleFactory.getFieldsModule();
                    updateRecords(module, FieldFactory.getNumberField("fieldId", "FIELDID", module), recordIds, isLocked);
                    break;

                case WORKFLOW:
                case STATEFLOW:
                case CUSTOM_BUTTON:
                    module = ModuleFactory.getWorkflowRuleModule();
                    updateRecords(module, FieldFactory.getIdField(module), recordIds, isLocked);
                    break;

                default:
                    break;
            }
        }

        return false;
    }

    private void updateRecords(FacilioModule module, FacilioField idField, List<Long> recordIds, boolean isLocked) throws Exception {
        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

        for (Long recordId : recordIds) {
            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addUpdateValue("locked", isLocked);
            updateVal.addWhereValue(idField.getName(), recordId);
            batchUpdateList.add(updateVal);
        }

        List<FacilioField> whereFields = new ArrayList<>(Collections.singleton(idField));
        FacilioField isLockedField = FieldFactory.getBooleanField("locked", "IS_LOCKED", module);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(isLockedField));

        updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);;
    }
}
