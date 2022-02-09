package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PeopleAPI;
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
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerBuildingValueGenerator extends ValueGenerator{
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(AltayerBuildingValueGenerator.class);

    @Override
    public Object generateValueForCondition(int appType) {
        if(appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            try {
                V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId(), true);
                if (vendor != null) {
                    long pplId = PeopleAPI.getPeopleIdForUser(AccountUtil.getCurrentUser().getId());
                    List<Long> buildingIds = getBuildingIdsFromVendorMappingData(vendor.getId() , pplId);
                    if(CollectionUtils.isNotEmpty(buildingIds)) {
                        return StringUtils.join(buildingIds, ",");
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Altayer Vendors Buildings";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerBuildingValueGenerator";
    }

    @Override
    public String getModuleName() {
        return "building";
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 9;
    }

    public List<Long> getBuildingIdsFromVendorMappingData(long vendorID, long pplId) throws Exception {
        List<Long> buildingIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("custom_vendormapping");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(ModuleBaseWithCustomFields.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("vendor"), String.valueOf(vendorID), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), "26327", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("contacts"), String.valueOf(pplId), PickListOperators.IS))

                ;
        builder.skipScopeCriteria();
        List<Map<String, Object>> props = builder.getAsProps();
        LOGGER.debug("_____query---" + builder.toString());

        if(CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                Map<String, Object> building = (Map<String, Object>) prop.get("building");
                if(MapUtils.isNotEmpty(building)) {
                    Long id = (Long) building.get("id");
                    if (!buildingIds.contains(id)) {
                        buildingIds.add(id);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(buildingIds)){
                LOGGER.debug("_____building id---" + buildingIds.size());

                return buildingIds;
            }
        }

        return null;
    }

}
