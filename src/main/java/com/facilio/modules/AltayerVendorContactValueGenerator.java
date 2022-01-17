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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerVendorContactValueGenerator extends ValueGenerator{
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
                    List<Long> ids = getVendorMappingData(baseSpaceIds);
                    if (CollectionUtils.isNotEmpty(ids)) {
                        return StringUtils.join(ids, ",");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Long> getVendorMappingData(List<Long> bsIds) throws Exception {
        List<Long> contactIds = new ArrayList<>();
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
                Map<String, Object> vendor = (Map<String, Object>) prop.get("contacts");
                Long contactId = (Long)vendor.get("id");
                if(!contactIds.contains(contactId)) {
                    contactIds.add(contactId);
                }
            }
            if(CollectionUtils.isNotEmpty(contactIds)){
                return contactIds;
            }
        }

        return null;
    }

    @Override
    public Object getValueGeneratorName() {
        return "Altayer Vendor Contacts";
    }

    @Override
    public Object getLinkName() {
        return "com.facilio.modules.AltayerVendorContactValueGenerator";
    }

    @Override
    public Object getModuleName() {
        return FacilioConstants.ContextNames.VENDOR_CONTACT;
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
