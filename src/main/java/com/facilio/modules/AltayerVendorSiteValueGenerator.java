package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerVendorSiteValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        if(appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            try {
                V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId());
                if (vendor != null) {
                    List<Long> buildingIds = getVendorMappingData();
                    if(CollectionUtils.isNotEmpty(buildingIds)) {
                        List<Long> siteIds = new ArrayList<>();
                        for(Long id : buildingIds) {
                            BuildingContext building = SpaceAPI.getBuildingSpace(id, false);
                            if(!siteIds.contains(building.getSiteId())) {
                                siteIds.add(building.getSiteId());
                            }
                        }
                        return siteIds;
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Long> getVendorMappingData() throws Exception {
        List<Long> buildingIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("custom_vendormapping");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(ModuleBaseWithCustomFields.class)
                .select(modBean.getAllFields(module.getName()))
                ;
        List<Map<String, Object>> props = builder.getAsProps();
        if(CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                Long id = (Long)prop.get("building");
                if(!buildingIds.contains(id)) {
                    buildingIds.add(id);
                }
            }
            if(CollectionUtils.isNotEmpty(buildingIds)){
                return buildingIds;
            }
        }

        return null;
    }
}
