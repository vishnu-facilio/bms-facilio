package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.*;

public class VMTemplateModule extends BaseModuleConfig{

    public VMTemplateModule() throws Exception {
        setModuleName(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {

        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> virtualMeterTemplate = new ArrayList<FacilioView>();
        virtualMeterTemplate.add(getAllVirtualMeterTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
        groupDetails.put("views", virtualMeterTemplate);
        groupVsViews.add(groupDetails);

        return groupVsViews;

    }
    private static FacilioView getAllVirtualMeterTemplateViews() throws Exception {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "Created Time", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Virtual Meter Templates");
        allView.setModuleName(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

}
