package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

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
                    long pplId = PeopleAPI.getPeopleIdForUser(AccountUtil.getCurrentUser().getId());
                    List<Long> siteIds = getSiteIdsFromVendorMappingData(vendor.getId() , pplId);
                    if(CollectionUtils.isNotEmpty(siteIds)) {
                        return StringUtils.join(siteIds, ",");
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Long> getSiteIdsFromVendorMappingData(long vendorID, long pplId) throws Exception {
        List<Long> siteIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("custom_vendormapping");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(ModuleBaseWithCustomFields.class)
                .select(modBean.getAllFields(module.getName()))
                .fetchSupplement((LookupField) fieldMap.get("building"))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("vendor"), String.valueOf(vendorID), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), "26327", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("contacts"), String.valueOf(pplId), PickListOperators.IS))

                ;
        List<Map<String, Object>> props = builder.getAsProps();
        if(CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                Map<String, Object> building = (Map<String, Object>) prop.get("building");
                if(MapUtils.isNotEmpty(building)) {
                    Long siteId = (Long) building.get("siteId");
                    if (!siteIds.contains(siteId)) {
                        siteIds.add(siteId);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(siteIds)){
                return siteIds;
            }
        }

        return null;
    }
}
