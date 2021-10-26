package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;

public class AltayerVendorAssetValueGenerator extends ValueGenerator{

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(AltayerVendorAssetValueGenerator.class);

    @Override
    public Object generateValueForCondition(int appType) {
        try {
            V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId());
            if (vendor != null) {
                List<Long> assetCategoryIds = getAssetCategoryData(vendor.getId());
                if (CollectionUtils.isNotEmpty(assetCategoryIds)) {
                    return StringUtils.join(assetCategoryIds, ",");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.DEBUG, ""+ e.getMessage());

        }
        return null;
    }

    public List<Long> getAssetCategoryData(long vendorId) throws Exception {
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

        List<SupplementRecord> fetchCatLookupsList = new ArrayList<>();
        MultiLookupMeta categories = new MultiLookupMeta((MultiLookupField) fieldMap.get("category"));
        fetchCatLookupsList.add(categories);
        builder.fetchSupplements(fetchCatLookupsList);

        List<Map<String, Object>> props = builder.getAsProps();
        if(CollectionUtils.isNotEmpty(props)) {
            LOGGER.log(Level.DEBUG, "fetched result 1");

            for(Map<String, Object> prop : props) {
                if(!prop.containsKey("category")) {
                    continue;
                }
                List<Map<String, Object>> mappedCategories = (List<Map<String, Object>>) prop.get("category");
                if(CollectionUtils.isNotEmpty(mappedCategories)) {
                    for(Map<String, Object> category : mappedCategories) {
                        Long categoryId = (Long) category.get("id");
                        if (!categoryIds.contains(categoryId)) {
                            categoryIds.add(categoryId);
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(categoryIds)){
                FacilioModule catModule = modBean.getModule("custom_workordercustomcategory");
                Map<String, FacilioField> catFieldMap = FieldFactory.getAsMap(modBean.getAllFields(catModule.getName()));
                SelectRecordsBuilder<ModuleBaseWithCustomFields> builderCategory = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                        .module(catModule)
                        .beanClass(ModuleBaseWithCustomFields.class)
                        .select(modBean.getAllFields(catModule.getName()))
                        .andCondition(CriteriaAPI.getCondition(catFieldMap.get("workordercategory"), StringUtils.join(categoryIds, ","), PickListOperators.IS))
                        ;

                List<SupplementRecord> fetchLookupsList = new ArrayList<>();
                MultiLookupMeta assetCategories = new MultiLookupMeta((MultiLookupField) catFieldMap.get("assetcategorynew"));
                fetchLookupsList.add(assetCategories);
                builderCategory.fetchSupplements(fetchLookupsList);


                List<ModuleBaseWithCustomFields> catProps = builderCategory.get();
                if(CollectionUtils.isNotEmpty(catProps)) {
                    LOGGER.log(Level.DEBUG, "fetched result 2");

                    List<Long> assetCategoriesList = new ArrayList<>();
                    for (ModuleBaseWithCustomFields mod : catProps) {
                        Map<String, Object> prop = FieldUtil.getAsProperties(mod);
                        if(!prop.containsKey("assetcategorynew")) {
                            continue;
                        }
                        List<Map<String, Object>> mappedAssetCategories = (List<Map<String, Object>>) prop.get("assetcategorynew");
                        if(CollectionUtils.isNotEmpty(mappedAssetCategories)) {
                            for(Map<String, Object> map : mappedAssetCategories) {
                                Long categoryId = (Long) map.get("id");
                                if (!assetCategoriesList.contains(categoryId)) {
                                    assetCategoriesList.add(categoryId);
                                }
                            }
                        }
                    }
                    if(CollectionUtils.isNotEmpty(assetCategoriesList)) {
                        return assetCategoriesList;
                    }
                }
            }
        }

        return null;
    }
}
