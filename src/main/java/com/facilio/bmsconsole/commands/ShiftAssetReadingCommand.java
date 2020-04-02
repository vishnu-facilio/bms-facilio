package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShiftAssetReadingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
        long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
        Long resourceList = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        long readingFieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
        long duration = (long) context.get(FacilioConstants.ContextNames.DURATION);
        long type = (long) context.get(FacilioConstants.ContextNames.TYPE);
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField readingField = bean.getField(readingFieldId);
        FacilioModule readingModule = bean.getModule(readingField.getModuleId());
        List<Long> parentIds = new ArrayList<>();

        ReadingContext reading = new ReadingContext();

        List<FacilioField> readingsFields = bean.getAllFields(readingModule.getName());
        Map<String, FacilioField> sourcefieldMap = FieldFactory.getAsMap(readingsFields);
            if (readingFieldId > -1) {
                readingsFields = bean.getAllFields(readingModule.getName());
                sourcefieldMap = FieldFactory.getAsMap(readingsFields);
            } else
            {
                return false;
            }
        UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
                .module(readingModule)
                .fields(readingsFields);
         if (resourceList != null ) {
             updateBuilder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"), String.valueOf(resourceList), NumberOperators.EQUALS));
         }
        if (startTime > -1 && endTime > -1) {
           updateBuilder.andCondition( CriteriaAPI.getCondition(sourcefieldMap.get("ttime"), startTime+","+endTime, DateOperators.BETWEEN));
        }
        if (type == 1) {

        }
        if (type == 2) {

        }
        updateBuilder.update(reading);
        return false;
    }
}
