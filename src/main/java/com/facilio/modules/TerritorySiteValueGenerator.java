package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.TerritoryContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TerritorySiteValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        if(appType == AppDomain.AppDomainType.FACILIO.getIndex()) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

                long peopleId = AccountUtil.getCurrentUser().getPeopleId();
                List<Long> territoryIds = getTerritoryForPeople(peopleId);

                if(CollectionUtils.isNotEmpty(territoryIds)){
                    FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
                    List<FacilioField> fields = modBean.getAllFields(siteModule.getName());
                    Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
                    Criteria criteria = new Criteria();
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("territory"),StringUtils.join(territoryIds,","),NumberOperators.EQUALS));
                    List<SiteContext> sites = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.SITE, null, SiteContext.class, criteria, null, null, null, true );
                    if(CollectionUtils.isNotEmpty(sites)){
                        List<Long> siteIds = sites.stream().map(SiteContext::getId).collect(Collectors.toList());
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

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.SITES_IN_CURRENT_PEOPLE_TERRITORIES;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.TerritorySiteValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SITE;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }

    public static List<Long> getTerritoryForPeople(long pplId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> fields = modBean.getAllFields(peopleModule.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        MultiLookupMeta territories = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("territories"));
        fetchLookupsList.add(territories);
        List<V3PeopleContext> people = V3RecordAPI.getRecordsListWithSupplements(peopleModule.getName(), Collections.singletonList(pplId), V3PeopleContext.class, null, fetchLookupsList, null, null, true );
        if(CollectionUtils.isNotEmpty(people)) {
            List<Long> territoryIds = new ArrayList<>();
            for(V3PeopleContext ppl : people){
                if(ppl != null && CollectionUtils.isNotEmpty(ppl.getTerritories())){
                    territoryIds.addAll(ppl.getTerritories().stream().map(TerritoryContext::getId).collect(Collectors.toList()));
                }
            }
            return territoryIds;
        }
        return null;
    }

}
