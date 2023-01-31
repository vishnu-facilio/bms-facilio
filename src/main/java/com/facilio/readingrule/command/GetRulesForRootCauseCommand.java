package com.facilio.readingrule.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetRulesForRootCauseCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<AssetCategoryContext> assetCategory= getAssetCategoryFromModuleId(module.getModuleId());
        List<NewReadingRuleContext> rules = getRulesByCategoryId(Collections.singletonList(assetCategory.get(0).getId()));
        fetchRelatedRules(rules, module);
        context.put(FacilioConstants.ReadingRules.NEW_READING_RULE_LIST, rules);
        return false;
    }

    private void fetchRelatedRules(List<NewReadingRuleContext> rules, FacilioModule moduleName) throws Exception {
        List<RelationRequestContext> relations = RelationUtil.getAllRelations(moduleName);
        if (CollectionUtils.isNotEmpty(relations)) {
            Set<Long> toModuleAssetCategory = new HashSet<>();
            for (RelationRequestContext rel : relations) {
                List<AssetCategoryContext> assetCategoryContext = getAssetCategoryFromModuleId(rel.getToModuleId());
                if(CollectionUtils.isNotEmpty(assetCategoryContext)) {
                    toModuleAssetCategory.add(assetCategoryContext.get(0).getId());
                }
            }
            if(CollectionUtils.isNotEmpty(toModuleAssetCategory)) {
                rules.addAll(getRulesByCategoryId(toModuleAssetCategory.stream().collect(Collectors.toList())));
            }
        }
    }
    
    private  List<AssetCategoryContext> getAssetCategoryFromModuleId(Long moduleId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
        SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
                .select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
                .module(module)
                .beanClass(AssetCategoryContext.class)
                .skipModuleCriteria()
                .andCondition(CriteriaAPI.getCondition("ASSET_MODULEID", "assetmoduleId", String.valueOf(moduleId), NumberOperators.EQUALS));
        List<AssetCategoryContext> assetCategoryContext = selectBuilder.get();
        return assetCategoryContext;
    }

    private  List<NewReadingRuleContext> getRulesByCategoryId(List<Long> assetCategory) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);
        SelectRecordsBuilder<NewReadingRuleContext> selectBuilder = new SelectRecordsBuilder<NewReadingRuleContext>()
                .select(modBean.getAllFields(FacilioConstants.ReadingRules.NEW_READING_RULE))
                .module(module)
                .beanClass(NewReadingRuleContext.class)
                .skipModuleCriteria()
                .andCondition(CriteriaAPI.getConditionFromList(" ASSET_CATEGORY_ID ", "assetCategoryId", assetCategory, NumberOperators.EQUALS));
        List<NewReadingRuleContext> rules = selectBuilder.get();
        return rules;
    }

}
