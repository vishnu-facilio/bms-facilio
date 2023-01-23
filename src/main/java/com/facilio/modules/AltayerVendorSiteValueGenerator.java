package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerVendorSiteValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        if(appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            try {
                V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId(), true);
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
        V3VendorContactContext vendorContact = V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT,pplId,V3VendorContactContext.class,true);

        PeopleAPI.getVendorContacts(pplId,false);
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
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), "26327", NumberOperators.EQUALS));

        if(vendorContact != null && !vendorContact.isPrimaryContact()){
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("contacts"), String.valueOf(pplId), PickListOperators.IS));
        }

        builder.skipScopeCriteria();
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

    @Override
    public String getValueGeneratorName() {
        return "Altayer Vendors Sites";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerVendorSiteValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.VENDORS;
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }
}