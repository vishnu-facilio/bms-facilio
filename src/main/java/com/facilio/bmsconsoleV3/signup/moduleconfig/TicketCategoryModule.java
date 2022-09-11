package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class TicketCategoryModule extends BaseModuleConfig{
    public TicketCategoryModule(){
        setModuleName(FacilioConstants.ContextNames.TICKET_CATEGORY);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> ticketCategory = new ArrayList<FacilioView>();
        ticketCategory.add(getAllTicketCategory().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TICKET_CATEGORY);
        groupDetails.put("views", ticketCategory);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTicketCategory() {

        FacilioModule ticketCategoryMod = ModuleFactory.getTicketCategoryModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("id");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("ID");
        createdTime.setModule(ticketCategoryMod);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioField nameField = new FacilioField();
        nameField.setName("name");
        nameField.setDataType(FieldType.STRING);
        nameField.setColumnName("NAME");
        nameField.setModule(ticketCategoryMod);
        ViewField nameViewFiled = new ViewField();
        nameViewFiled.setField(nameField);

        FacilioField descField = new FacilioField();
        descField.setName("description");
        descField.setDataType(FieldType.STRING);
        descField.setColumnName("DESCRIPTION");
        descField.setModule(ticketCategoryMod);
        ViewField descViewFiled = new ViewField();
        descViewFiled.setField(nameField);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(sortFields);
        allView.setFields(Arrays.asList(nameViewFiled, descViewFiled));

        return allView;
    }
}
