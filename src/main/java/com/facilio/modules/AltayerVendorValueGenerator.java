package com.facilio.modules;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AltayerVendorValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
        GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
                .select(AccountConstants.getAccessbileSpaceFields())
                .table(accessibleSpaceMod.getTableName())
                .andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());
        List<Map<String, Object>> props = null;
        try {
            props = selectAccessibleBuilder.get();
            List<Long> baseSpaceIds = new ArrayList<Long>();
            if (props != null && !props.isEmpty()) {
                for(Map<String, Object> prop : props) {
                    Long bsId = (Long) prop.get("bsid");
                    if (bsId != null) {
                        baseSpaceIds.add(bsId);
                    }
                }

                if(CollectionUtils.isNotEmpty(baseSpaceIds)) {
                    List<Long> vendorIds = getVendorMappingData(baseSpaceIds);
                    if (CollectionUtils.isNotEmpty(vendorIds)) {
                        return StringUtils.join(vendorIds, ",");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Long> getVendorMappingData(List<Long> bsIds) throws Exception {
        List<Long> vendorIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("custom_vendormapping");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(ModuleBaseWithCustomFields.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), StringUtils.join(bsIds, ","), PickListOperators.IS))
                ;
        List<Map<String, Object>> props = builder.getAsProps();
        if(CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                Map<String, Object> vendor = (Map<String, Object>) prop.get("vendor");
                Long vendorId = (Long)vendor.get("id");
                if(!vendorIds.contains(vendorId)) {
                    vendorIds.add(vendorId);
                }
            }
            if(CollectionUtils.isNotEmpty(vendorIds)){
                return vendorIds;
            }
        }

        return null;
    }

    @Override
    public Object getValueGeneratorName() {
        return "Altayer Vendors";
    }

    @Override
    public Object getLinkName() {
        return "com.facilio.modules.AltayerVendorValueGenerator";
    }

    @Override
    public Object getModuleName() {
        return FacilioConstants.ContextNames.VENDORS;
    }

    @Override
    public Object getIsHidden() {
        return true;
    }

    @Override
    public Object getOperatorId() {
        return 36;
    }
}
