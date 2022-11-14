package com.facilio.bmsconsoleV3.commands.formrelation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FeedbackTypeContext;
import com.facilio.bmsconsole.util.DevicesAPI;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingFormRelationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateFormRelationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceBookingFormRelationModule = ModuleFactory.getSpaceBookingFormRelationModule();
        List<FacilioField> fields = FieldFactory.getSpaceCategoryFormRelationFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selectFields = Arrays.asList(FieldFactory.getIdField(spaceBookingFormRelationModule));


        V3SpaceBookingFormRelationContext relationcontext = (V3SpaceBookingFormRelationContext) context.get("FORM_RELATION_CONTEXT");
        FacilioUtil.throwIllegalArgumentException(relationcontext == null, "Form Relation context should not be null");

        Long appId = relationcontext.getAppId() ;
        Long categoryId = relationcontext.getCategoryId();
        Long moduleFormId = relationcontext.getModuleFormId();
        String parentModuleName = relationcontext.getParentModuleName();
        FacilioModule module = modBean.getModule(parentModuleName);
        Long parentModuleId = module.getModuleId();
        relationcontext.setParentModuleId(parentModuleId);

        Map<String, Object> props = FieldUtil.getAsProperties(relationcontext);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSpaceBookingFormRelationModule().getTableName())
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.APP_ID), String.valueOf(appId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.PARENT_MODULE_ID), String.valueOf(parentModuleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.CATEGORY_ID), String.valueOf(categoryId), NumberOperators.EQUALS));

        List<Map<String, Object>> selectedProps = selectBuilder.get();
        Long id;
        if(selectedProps!=null && !selectedProps.isEmpty()){
            //update
            for (Map<String, Object> selectedProp : selectedProps) {
                id = (Long) selectedProp.get("id");
                relationcontext.setId(id);
            }

            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .fields(FieldFactory.getSpaceCategoryFormRelationFields())
                    .table(ModuleFactory.getSpaceBookingFormRelationModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(relationcontext.getId(), ModuleFactory.getSpaceBookingFormRelationModule()));

            updateBuilder.update(props);

        }
        else {
            //add
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getSpaceBookingFormRelationModule().getTableName())
                    .fields(FieldFactory.getSpaceCategoryFormRelationFields());

            insertBuilder.addRecord(props);
            insertBuilder.save();

            long importId = (Long) props.get("id");
            relationcontext.setId(importId);
            context.put("FORM_RELATION_DATA", relationcontext);
        }


        return false;
    }
}
