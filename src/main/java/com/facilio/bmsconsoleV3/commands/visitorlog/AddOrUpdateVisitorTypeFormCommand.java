package com.facilio.bmsconsoleV3.commands.visitorlog;

import com.facilio.bmsconsole.context.V3VisitorTypeContext;
import com.facilio.bmsconsole.context.VisitorTypeFormsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI.checkAddOrUpdateVisitorTypeForm;

public class AddOrUpdateVisitorTypeFormCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String parentModule = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        long currentAppId= (long) context.get(FacilioConstants.ContextNames.APP_ID);
        FacilioModule visitorTypeFormsModule= ModuleFactory.getVisitorTypeFormsModule();
        List<FacilioField> visitorTypeFormsFields= FieldFactory.getVisitorTypeFormsFields();
        List<Map<String, Object>> visitorTypeFormProps = new ArrayList<Map<String,Object>>();
        V3VisitorTypeContext visitorType = (V3VisitorTypeContext) context.get(FacilioConstants.ContextNames.VISITOR_TYPE);
        VisitorTypeFormsContext visitorTypeForm = new VisitorTypeFormsContext();
        Boolean isEdit = checkAddOrUpdateVisitorTypeForm(currentAppId,visitorType.getId());
        if(parentModule == null)
        {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Parent Module Name Missing");
        }
        else if(currentAppId<0)
        {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Selected App ID Missing");
        }

        if(isEdit)
        {
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(visitorTypeFormsModule.getTableName())
                    .fields(visitorTypeFormsFields)
                    .andCondition(CriteriaAPI.getCondition("VISITOR_TYPE_ID", "visitorTypeId", visitorType.getId()+"", NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", currentAppId + "", NumberOperators.EQUALS));
            Map<String, Object> props = new HashMap<>();
            if(parentModule.equals(FacilioConstants.ContextNames.VISITOR_LOG)) {
              visitorTypeForm.setVisitorLogEnabled(visitorType.getFormEnabled());
              visitorTypeForm.setVisitorLogFormId(visitorType.getVisitorFormId());
                props.put("visitorLogEnabled", visitorType.getFormEnabled());
                props.put("visitorLogFormId", visitorType.getVisitorFormId());

            }
            else if(parentModule.equals(FacilioConstants.ContextNames.INVITE_VISITOR)) {
                visitorTypeForm.setInviteEnabled(visitorType.getFormEnabled());
                visitorTypeForm.setVisitorInviteFormId(visitorType.getVisitorFormId());
                props.put("inviteEnabled", visitorType.getFormEnabled());
                props.put("visitorInviteFormId", visitorType.getVisitorFormId());

            }
            updateBuilder.update(props);
        }
        else
        {
            GenericInsertRecordBuilder insertVisitorTypeFormsBuilder=new GenericInsertRecordBuilder();
            insertVisitorTypeFormsBuilder.table(visitorTypeFormsModule.getTableName()).fields(visitorTypeFormsFields);
            if(parentModule.equals(FacilioConstants.ContextNames.VISITOR_LOG))
            {
                visitorTypeForm.setVisitorTypeId(visitorType.getId());
                visitorTypeForm.setVisitorLogFormId(visitorType.getVisitorFormId());
                visitorTypeForm.setVisitorLogEnabled(true);
                visitorTypeForm.setAppId(currentAppId);
                visitorTypeFormProps.add(FieldUtil.getAsProperties(visitorTypeForm,true));
            }
            else if(parentModule.equals(FacilioConstants.ContextNames.INVITE_VISITOR)) {
                visitorTypeForm.setVisitorTypeId(visitorType.getId());
                visitorTypeForm.setVisitorInviteFormId(visitorType.getVisitorFormId());
                visitorTypeForm.setInviteEnabled(true);
                visitorTypeForm.setAppId(currentAppId);
                visitorTypeFormProps.add(FieldUtil.getAsProperties(visitorTypeForm,true));
            }
            insertVisitorTypeFormsBuilder.addRecords(visitorTypeFormProps);
            insertVisitorTypeFormsBuilder.save();
        }

    return false;
    }
}
