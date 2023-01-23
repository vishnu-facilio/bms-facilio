package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerWorkorderCategoryValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId(), true);
            if (vendor != null) {
                List<Long> woCategoryIds = getWorkorderCategoryData(vendor.getId());
                if (CollectionUtils.isNotEmpty(woCategoryIds)) {
                    return StringUtils.join(woCategoryIds, ",");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Long> getWorkorderCategoryData(long vendorId) throws Exception {
        List<Long> categoryIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("custom_vendormapping");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(ModuleBaseWithCustomFields.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("vendor"), String.valueOf(vendorId), PickListOperators.IS))
                ;
        builder.skipScopeCriteria();
        List<Map<String, Object>> props = builder.getAsProps();
        if(CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                Map<String, Object> category = (Map<String, Object>) prop.get("category");
                Long categoryId = (Long)category.get("id");
                if(!categoryIds.contains(categoryId)) {
                    categoryIds.add(categoryId);
                }
            }
            if(CollectionUtils.isNotEmpty(categoryIds)){
               return categoryIds;
            }
        }

        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Altayer Workorder category";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerWorkorderCategoryValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TICKET_CATEGORY;
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
