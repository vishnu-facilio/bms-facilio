package com.facilio.bmsconsoleV3.signup.ocr;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;

public class AddBillTemplateModule extends BaseModuleConfig {

    public AddBillTemplateModule(){
        setModuleName(FacilioConstants.Ocr.BILL_TEMPLATE);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        FacilioModule billTemplateModule = addBillTemplateModule(modBean,orgId);
//        addTemplateLookupToPreUtilityBill(modBean,billTemplateModule);
        addListSystemButtons();
    }


    private FacilioModule addBillTemplateModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule ocrTemplate = moduleBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE);
        
        FacilioModule billTemplateModule = new FacilioModule(FacilioConstants.Ocr.BILL_TEMPLATE,"Bill Templates","OCR_TEMPLATE_BILL",
                FacilioModule.ModuleType.BASE_ENTITY,ocrTemplate,false);
        billTemplateModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        MultiEnumField utilityType = FieldFactory.getDefaultField("utilityType","Utility Type",null, FieldType.MULTI_ENUM, FacilioField.FieldDisplayType.SELECTBOX);
        List<String> types = Arrays.asList( "Electricity", "Water", "Gas");
        List<EnumFieldValue<Integer>> typeValues = types.stream().map(val -> {
            int index = types.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        utilityType.setValues(typeValues);
        fields.add(utilityType);

        SystemEnumField billFrequency = (SystemEnumField) FieldFactory.getDefaultField("billFrequency", "Bill Frequency", "BILL_FREQUENCY", FieldType.SYSTEM_ENUM);
        billFrequency.setEnumName("BillFrequencyEnum");
        fields.add(billFrequency);

        SystemEnumField utilityProvider = (SystemEnumField) FieldFactory.getDefaultField("utilityProvider", "Utility Provider", "UTILITY_PROVIDER", FieldType.SYSTEM_ENUM);
        utilityProvider.setEnumName("UtilityProviderEnum");
        fields.add(utilityProvider);

        fields.add(FieldFactory.getDefaultField("startMonth","Start Month","START_MONTH",FieldType.NUMBER));

        billTemplateModule.setFields(fields);
        
        modules.add(billTemplateModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return billTemplateModule;
    }

    public static void addListSystemButtons() throws Exception {
        // Table Top Bar buttons
        SystemButtonRuleContext createButtonListTop = new SystemButtonRuleContext();
        createButtonListTop.setName("Create Template");
        createButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButtonListTop.setIdentifier("createTemplate");
        createButtonListTop.setPermissionRequired(true);
        createButtonListTop.setPermission("CREATE");
        addSystemButton(FacilioConstants.Ocr.BILL_TEMPLATE, createButtonListTop);

        // Upload Bill
        SystemButtonRuleContext uploadBillButton = new SystemButtonRuleContext();
        uploadBillButton.setName("Upload Bill");
        uploadBillButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        uploadBillButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        uploadBillButton.setIdentifier("uploadBill");
        uploadBillButton.setPermissionRequired(true);
        uploadBillButton.setPermission("CREATE");
        addSystemButton(FacilioConstants.Ocr.BILL_TEMPLATE, uploadBillButton);
    }

    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlAction = new ArrayList<FacilioView>();
        controlAction.add(getAllBillTemplateViews().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Ocr.BILL_TEMPLATE);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupDetails.put("views", controlAction);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllBillTemplateViews() throws Exception{

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Bill Templates");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<ViewField> fields = new ArrayList<>();
        fields.add(new ViewField("name","Name"));
        fields.add(new ViewField("description","Description"));
        fields.add(new ViewField("utilityType","Utility Type"));
        fields.add(new ViewField("billFrequency","Bill Frequency"));
        fields.add(new ViewField("utilityProvider","Utility Provider"));
        fields.add(new ViewField("pageBreak","Page Break"));
        fields.add(new ViewField("startMonth","Start Month"));
        fields.add(new ViewField("sampleBill","Sample Bill"));
        fields.add(new ViewField("status","Status"));
        fields.add(new ViewField("utilityType","Utility Type"));
        allView.setFields(fields);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "OCR_BILL_TEMPLATE.ID", FieldType.NUMBER), true));
        allView.setSortFields(sortFields);

        return allView;
    }
}
