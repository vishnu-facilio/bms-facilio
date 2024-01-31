package com.facilio.bmsconsoleV3.signup.ocr;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ocr.ActualBillContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.*;

public class AddActualBillModule extends BaseModuleConfig {

    public AddActualBillModule(){
        setModuleName(FacilioConstants.Ocr.ACTUAL_BILL);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        addActualBillModule(modBean,orgId);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;


        List<FacilioView> actualBill =  getAllActualBillView();

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Ocr.ACTUAL_BILL);
        groupDetails.put("views", actualBill);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static List<FacilioView> getAllActualBillView() throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.Ocr.ACTUAL_BILL,"Actual Bill","OCR_ACTUAL_BILL",
                FacilioModule.ModuleType.BASE_ENTITY,true);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        ArrayList<FacilioView> actualBill = new ArrayList<FacilioView>();
        int order = 1;

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Bills");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);
        allView.setFields(getAllViewColumns());

        FacilioView billUploadedView = new FacilioView();
        billUploadedView.setName("billUploadedView");
        billUploadedView.setDisplayName("Bill Uploaded");
        billUploadedView.setModuleName(module.getName());
        billUploadedView.setSortFields(sortFields);
        billUploadedView.setFields(getAllViewColumns());
        billUploadedView.setCriteria(getActualBillViewCriteria(ActualBillContext.ActualBillStatus.BILL_UPLOADED));


        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        allView.setAppLinkNames(appLinkNames);
        billUploadedView.setAppLinkNames(appLinkNames);

        actualBill.add(allView.setOrder(order++));
        actualBill.add(billUploadedView.setOrder(order++));

        return actualBill;
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("billFile","Name"));
        columns.add(new ViewField("status","Bill Status"));
        columns.add(new ViewField("sysCreatedTime","Uploaded At"));
        columns.add(new ViewField("sysCreatedByPeople","Uploaded By"));

        return columns;
    }

    private static Criteria getActualBillViewCriteria(ActualBillContext.ActualBillStatus status) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria billUploadedViewCriteria = new Criteria();
        FacilioField statusField = modBean.getField("status", FacilioConstants.Ocr.ACTUAL_BILL);
        if(statusField != null) {
            Condition billUploadedViewCondition = new Condition();
            billUploadedViewCondition.setField(statusField);
            billUploadedViewCondition.setOperator(NumberOperators.EQUALS);
            billUploadedViewCondition.setValue(String.valueOf(status.getIndex()));
            billUploadedViewCriteria.addAndCondition(billUploadedViewCondition);
        }
        return billUploadedViewCriteria;
    }

    private void addActualBillModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule actualBillModule = new FacilioModule(FacilioConstants.Ocr.ACTUAL_BILL,"Actual Bill","OCR_ACTUAL_BILL",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        actualBillModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("billMonth","Bill Month","BILL_MONTH",FieldType.DATE));

        LookupField billTemplate =  FieldFactory.getDefaultField("billTemplate","Bill Template","TEMPLATE_ID",FieldType.LOOKUP);
        billTemplate.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.BILL_TEMPLATE),"Bill Template module doesn't exists."));
        fields.add(billTemplate);

        SystemEnumField billStatus = (SystemEnumField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.SYSTEM_ENUM);
        billStatus.setEnumName("ActualBillStatus");
        fields.add(billStatus);

        fields.add(FieldFactory.getDefaultField("billFile","Bill File","BILL_FILE_ID",FieldType.FILE));

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add(FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        actualBillModule.setFields(fields);
        modules.add(actualBillModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }
}
