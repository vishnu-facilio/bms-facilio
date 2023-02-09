package com.facilio.bmsconsoleV3.signup.labour;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

import java.util.*;

public class AddLabourModuleFields extends BaseModuleConfig {

    public AddLabourModuleFields(){
        setModuleName(FacilioConstants.ContextNames.LABOUR);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();

        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LABOUR);

        FacilioUtil.throwIllegalArgumentException(module==null,"Labour module should not be null..");

        List<FacilioField> fields = new ArrayList<>();

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", module, FieldType.LOOKUP);
        moduleStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        moduleStateField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(moduleStateField);

        FacilioField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", module, FieldType.NUMBER);
        stateFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", module, FieldType.LOOKUP);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(approvalStateField);

        FacilioField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", module, FieldType.NUMBER);
        approvalFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(approvalFlowIdField);


        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> labourModule = new ArrayList<FacilioView>();
        labourModule.add(getLabourAllViews().setOrder(order++));
        labourModule.add(getLabourHiddenAllViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.LABOUR);
        groupDetails.put("views", labourModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getLabourAllViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Labour.ID", FieldType.NUMBER), true));

        FacilioView LabourView = new FacilioView();
        LabourView.setName("all");
        LabourView.setDisplayName("Labour");

        LabourView.setModuleName(FacilioConstants.ContextNames.LABOUR);
        LabourView.setSortFields(sortFields);

        List<ViewField> LabourViewFields = new ArrayList<>();

        LabourViewFields.add(new ViewField("name","Name"));
        LabourViewFields.add(new ViewField("cost","Cost"));

        LabourView.setFields(LabourViewFields);

        return LabourView;
    }

    private FacilioView getLabourHiddenAllViews(){
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Labour.ID", FieldType.NUMBER), true));

        FacilioView LabourView = new FacilioView();
        LabourView.setName("hidden-all");
        LabourView.setDisplayName("Labour");

        LabourView.setModuleName(FacilioConstants.ContextNames.LABOUR);
        LabourView.setSortFields(sortFields);

        List<ViewField> LabourViewFields = new ArrayList<>();

        LabourViewFields.add(new ViewField("name","Labor"));
        LabourViewFields.add(new ViewField("cost","Rate per Hour"));

        LabourView.setFields(LabourViewFields);

        return LabourView;
    }
}
