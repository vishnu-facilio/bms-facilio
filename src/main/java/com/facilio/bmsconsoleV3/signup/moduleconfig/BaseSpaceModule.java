package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class BaseSpaceModule extends BaseModuleConfig{
    public BaseSpaceModule(){
        setModuleName(FacilioConstants.ContextNames.BASE_SPACE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> baseSpace = new ArrayList<FacilioView>();
        baseSpace.add(getAllBasespaces().setOrder(order++));
        baseSpace.add(getHiddenBaseSpaceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BASE_SPACE);
        groupDetails.put("views", baseSpace);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public static FacilioView getHiddenBaseSpaceView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioView baseSpaceView = new FacilioView();
        baseSpaceView.setName("TenantSpaceListView");
        baseSpaceView.setDisplayName("Tenant Space List View");
        baseSpaceView.setHidden(true);
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        List<FacilioField> allFields =  modBean.getAllFields(baseSpaceModule.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"name","spaceType","area"};
        List<ViewField> viewFields = new ArrayList<>();
        for(String viewFieldName:viewFieldNames){
            FacilioField field = fieldMap.get(viewFieldName);
            ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
            viewField.setField(field);
            viewFields.add(viewField);
        }
        baseSpaceView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        baseSpaceView.setAppLinkNames(appLinkNames);
        return baseSpaceView;
    }


    @Override
    public void addClassificationDataModule() throws Exception {
        String tableName="BaseSpace_Classification_Data";
        ClassificationUtil.addClassificationDataModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.BASE_SPACE),tableName);
    }

    private static FacilioView getAllBasespaces() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Basespaces");
        allView.setSortFields(sortFields);

        return allView;
    }
}
