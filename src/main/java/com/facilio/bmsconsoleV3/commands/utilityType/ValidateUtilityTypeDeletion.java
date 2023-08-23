package com.facilio.bmsconsoleV3.commands.utilityType;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ValidateUtilityTypeDeletion extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayList<Long> recordIDs = (ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        long recordID = recordIDs.get(0);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.Meter.METER);
        GenericSelectRecordBuilder meterSelectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(FieldFactory.getIdField(meterModule)))
                .table(meterModule.getTableName())
                .andCustomWhere(" Resources.SYS_DELETED IS NULL AND Meters.UTILITY_TYPE = ?", recordID)
                .innerJoin(" Resources ")
                .on(" Resources.ID = Meters.ID ")
                .limit(1);
        List<Map<String, Object>> result = meterSelectBuilder.get();
        List<String> moduleNames = new ArrayList<>();

        boolean stopChain = false;
        if (!result.isEmpty()) {
            stopChain = true;
            moduleNames.add(meterModule.getName());
        }

        FacilioModule utilityTypeModule = modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE);
        GenericSelectRecordBuilder utilityTypeSelectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(FieldFactory.getIdField(utilityTypeModule)))
                .table(utilityTypeModule.getTableName())
                .andCustomWhere("PARENT_UTILITY_TYPE_ID = ? AND SYS_DELETED IS NULL", recordID)
                .limit(1);
        result = utilityTypeSelectBuilder.get();

        if (!result.isEmpty()) {
            stopChain = true;
            moduleNames.add(utilityTypeModule.getDisplayName());
        }

        SelectRecordsBuilder<V3UtilityTypeContext> selectRecordsBuilder = new SelectRecordsBuilder<V3UtilityTypeContext>()
                .beanClass(V3UtilityTypeContext.class)
                .moduleName(utilityTypeModule.getName())
                .select(modBean.getAllFields(utilityTypeModule.getName()))
                .andCondition(CriteriaAPI.getIdCondition(recordID, utilityTypeModule));
        List<V3UtilityTypeContext> list = selectRecordsBuilder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            long meterModuleId = list.get(0).getMeterModuleID();
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table("SubModulesRel")
                    .select(Collections.singletonList(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER)))
                    .andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(meterModuleId), NumberOperators.EQUALS));
            result = builder.get();
            if (!result.isEmpty()) {
                stopChain = true;
                moduleNames.add("Meter Readings");
            }
        }

        if(moduleNames.size() > 0){
            StringBuilder builder = new StringBuilder("This Utility Type is being used by following modules: ");
            for (String name : moduleNames) {
                builder.append(" ").append(name);
            }
            throw new RESTException(ErrorCode.VALIDATION_ERROR, builder.toString());
        }
        return stopChain;
    }


}
