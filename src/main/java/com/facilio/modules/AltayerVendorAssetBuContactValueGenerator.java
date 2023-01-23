package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.*;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AltayerVendorAssetBuContactValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {

        try {
            long peopleId = AccountUtil.getCurrentUser().getPeopleId();


            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule vendorContactModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);

            boolean isBuPrimary = false;
            V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId(), true);
            List<Long> siteIds = new ArrayList<>();

            List<V3VendorContactContext> vendorContacts = V3RecordAPI.getRecordsListWithSupplements(vendorContactModule.getName(), Collections.singletonList(peopleId), V3VendorContactContext.class, null, null, null, null, true);
            if (CollectionUtils.isNotEmpty(vendorContacts)) {
                V3VendorContactContext vendorContact = vendorContacts.get(0);
                if(vendorContact != null && vendorContact.getData() != null && vendorContact.getData().containsKey("is_bu_primary_contact_vendorcontact") && (boolean) vendorContact.getData().get("is_bu_primary_contact_vendorcontact")){
                    isBuPrimary = true;
                    Long vcId = vendorContact.getId();
                    Criteria criteria = new Criteria();
                    criteria.addAndCondition(CriteriaAPI.getCondition("LEFT_ID", "left", String.valueOf(vcId), NumberOperators.EQUALS));
                    List<RelRecord> relRecs = V3RecordAPI.getRecordsListWithSupplements("vendorcontact-site-accessible_sites_vendorcontact_1-rel", null, RelRecord.class, criteria, null, null, null, true);
                    if (CollectionUtils.isNotEmpty(relRecs)) {
                        for (RelRecord relRecord : relRecs) {
                            Map<String, Object> site = (Map<String, Object>) relRecord.getRight();
                            siteIds.add((Long) site.get("id"));
                        }
                    }
                }
            }

            List<Map<String, Object>> props = getAllVendorMappingForContact(isBuPrimary,siteIds,vendor,peopleId);
            Criteria assetCriteria = getAssetCriteria(props);
            if(assetCriteria == null) {
                return "";
            }
            List<V3AssetContext> assets = V3RecordAPI.getRecordsListWithSupplements("asset", null, V3AssetContext.class,assetCriteria, null, null, null, true);
            if(CollectionUtils.isNotEmpty(assets)) {
                List<Long> assetIds = assets.stream().map(V3AssetContext::getId).collect(Collectors.toList());
                return StringUtils.join(assetIds,",");
            }
        } catch (Exception e) {

        }
        return null;
    }


    private List<Map<String, Object>>  getAllVendorMappingForContact(boolean isBuPrimary,List<Long> siteIds,V3VendorContext vendor,long peopleId) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("custom_vendormapping");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(ModuleBaseWithCustomFields.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), "26327", NumberOperators.EQUALS));
        if(isBuPrimary) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("vendor"), String.valueOf(vendor.getId()), PickListOperators.IS));
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("siteId"), StringUtils.join(siteIds,","), PickListOperators.IS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("contacts"), String.valueOf(peopleId), PickListOperators.IS));
        }

        List<SupplementRecord> fetchCatLookupsList = new ArrayList<>();
        MultiLookupMeta categories = new MultiLookupMeta((MultiLookupField) fieldMap.get("category"));
        fetchCatLookupsList.add(categories);
        builder.fetchSupplements(fetchCatLookupsList);
        builder.skipScopeCriteria();

        List<Map<String, Object>> props = builder.getAsProps();
        if(CollectionUtils.isNotEmpty(props)) {
            return props;
        }
        return null;
    }

    private Criteria getAssetCriteria(List<Map<String, Object>> props) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria parentCriteria = null;
        List<Long> categoryIds = new ArrayList<>();
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields("asset"));
        if(CollectionUtils.isNotEmpty(props)) {
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
        }
        if(CollectionUtils.isNotEmpty(categoryIds)) {
            Map<Long,List<Long>> woCatAssetCatMap = woAssetCatMap(categoryIds);
            if(MapUtils.isNotEmpty(woCatAssetCatMap)) {
                for(Map<String, Object> prop : props) {
                    Long spaceId = null;
                    Map<String,Object> dataMap = new HashMap<>();
                    if (prop.containsKey("space")) {
                        Map<String,Object> map = (Map<String, Object>) prop.get("space");
                        spaceId = (Long) map.get("id");
                    } else if (prop.containsKey("tenantunit")) {
                        Map<String,Object> map = (Map<String, Object>) prop.get("tenantunit");
                        spaceId = (Long) map.get("id");
                    } else if (prop.containsKey("building")) {
                        Map<String,Object> map = (Map<String, Object>) prop.get("building");
                        spaceId = (Long) map.get("id");
                    }
                    List<Long> allAssetCatIds = new ArrayList<>();
                    List<Map<String, Object>> mappedCategories = (List<Map<String, Object>>) prop.get("category");
                    if(CollectionUtils.isNotEmpty(mappedCategories)) {
                        for(Map<String, Object> category : mappedCategories) {
                            Long woCategoryId = (Long) category.get("id");
                            if(woCatAssetCatMap.containsKey(woCategoryId)) {
                                List<Long> assetCatIds = woCatAssetCatMap.get(woCategoryId);
                                if (CollectionUtils.isNotEmpty(assetCatIds)) {
                                    allAssetCatIds.addAll(assetCatIds);
                                }
                            }
                        }
                    }

                    if(spaceId != null && CollectionUtils.isNotEmpty(allAssetCatIds)) {
                        if(parentCriteria == null) {
                            parentCriteria = new Criteria();
                        }
                        Criteria criteria = new Criteria();
                        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("space"),String.valueOf(spaceId), BuildingOperator.BUILDING_IS));
                        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("category"),StringUtils.join(allAssetCatIds,","),NumberOperators.EQUALS));
                        parentCriteria.orCriteria(criteria);
                    }
                }
            }
        }
        return parentCriteria;
    }

    private Map<Long,List<Long>> woAssetCatMap(List<Long> woCatIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(woCatIds)){
            FacilioModule catModule = modBean.getModule("custom_workordercustomcategory");
            Map<String, FacilioField> catFieldMap = FieldFactory.getAsMap(modBean.getAllFields(catModule.getName()));
            SelectRecordsBuilder<ModuleBaseWithCustomFields> builderCategory = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                    .module(catModule)
                    .beanClass(ModuleBaseWithCustomFields.class)
                    .select(modBean.getAllFields(catModule.getName()))
                    .andCondition(CriteriaAPI.getCondition(catFieldMap.get("workordercategory"), StringUtils.join(woCatIds, ","), PickListOperators.IS))
                    ;

            List<SupplementRecord> fetchLookupsList = new ArrayList<>();
            MultiLookupMeta assetCategories = new MultiLookupMeta((MultiLookupField) catFieldMap.get("assetcategorynew"));
            LookupField woCatField = (LookupField) catFieldMap.get("workordercategory");
            fetchLookupsList.add(assetCategories);
            fetchLookupsList.add(woCatField);
            builderCategory.fetchSupplements(fetchLookupsList);

            List<ModuleBaseWithCustomFields> catProps = builderCategory.get();
            if(CollectionUtils.isNotEmpty(catProps)) {

                Map<Long,List<Long>> wocatAssetcatMap = new HashMap<>();
                for (ModuleBaseWithCustomFields mod : catProps) {
                    Map<String, Object> prop = FieldUtil.getAsProperties(mod);
                    if(!prop.containsKey("assetcategorynew")) {
                        continue;
                    }
                    List<Map<String, Object>> mappedAssetCategories = (List<Map<String, Object>>) prop.get("assetcategorynew");
                    Map<String, Object> mappedWOCategory = (Map<String, Object>) prop.get("workordercategory");
                    if(CollectionUtils.isNotEmpty(mappedAssetCategories) && MapUtils.isNotEmpty(mappedWOCategory)) {
                        List<Long> assetCategoriesList = new ArrayList<>();
                        for(Map<String, Object> map : mappedAssetCategories) {
                            Long categoryId = (Long) map.get("id");
                            if (!assetCategoriesList.contains(categoryId)) {
                                assetCategoriesList.add(categoryId);
                            }
                        }
                        wocatAssetcatMap.put((Long) mappedWOCategory.get("id"),assetCategoriesList);
                    }
                }
                if(MapUtils.isNotEmpty(wocatAssetcatMap)) {
                    return wocatAssetcatMap;
                }
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
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), "26327", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("contacts"), String.valueOf(pplId), PickListOperators.IS));


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
        return "Altayer Vendor Asset Value Generator";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerVendorAssetBuContactValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.ASSET;
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
