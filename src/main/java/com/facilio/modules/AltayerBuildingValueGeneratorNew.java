package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.VendorsAPI;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
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

public class AltayerBuildingValueGeneratorNew extends ValueGenerator{

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
        return "Altayer Vendors Buildings New";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerBuildingValueGeneratorNew";
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

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }
    public List<Long> getBuildingIdsFromVendorMappingData(long vendorID, long pplId) throws Exception {
        V3VendorContactContext vendorContact = V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT,pplId,V3VendorContactContext.class,true);
        List<Long> buildingIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("custom_vendormapping");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(ModuleBaseWithCustomFields.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("vendor"), String.valueOf(vendorID), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), "26327", NumberOperators.EQUALS));

        if(!(vendorContact != null && vendorContact.getData() != null && vendorContact.getData().containsKey("is_bu_primary_contact_vendorcontact") && (boolean) vendorContact.getData().get("is_bu_primary_contact_vendorcontact"))){
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("contacts"), String.valueOf(pplId), PickListOperators.IS));
        }

        builder.skipScopeCriteria();
        List<Map<String, Object>> props = builder.getAsProps();
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
                return buildingIds;
            }
        }

        return null;
    }

}
