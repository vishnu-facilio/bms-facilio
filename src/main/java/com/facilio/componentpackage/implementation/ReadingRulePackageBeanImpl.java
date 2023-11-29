package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.NamespaceBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.context.RuleAlarmDetails;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.rca.context.RCAConditionScoreContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.ConnectedRuleUtil;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class ReadingRulePackageBeanImpl implements PackageBean<NewReadingRuleContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> allRuleIds = ConnectedRuleUtil.getAllConnectedRuleIdsAndModuleIds(FacilioConstants.ReadingRules.NEW_READING_RULE);
        if (MapUtils.isNotEmpty(allRuleIds)) {
            return allRuleIds;
        }
        return new HashMap<>();
    }

    @Override
    public Map<Long, NewReadingRuleContext> fetchComponents(List<Long> ids) throws Exception {
        List<NewReadingRuleContext> rules = NewReadingRuleAPI.getReadingRules(ids);
        if (CollectionUtils.isNotEmpty(rules)) {
            return rules.stream().collect(Collectors.toMap(NewReadingRuleContext::getId, Function.identity()));
        }
        return new HashMap<>();
    }

    @Override
    public void convertToXMLComponent(NewReadingRuleContext rule, XMLBuilder ruleElement) throws Exception {
        new RuleXMLSerializer(rule, ruleElement).execute();
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();

        NewReadingRuleContext readingRule;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder ruleElement = idVsData.getValue();
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule module = moduleBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);

            readingRule = constructRuleFromXMLBuilder(ruleElement);
            FacilioContext context = V3Util.createRecord(module, FieldUtil.getAsProperties(readingRule));

            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<NewReadingRuleContext> readingRules = recordMap.get(FacilioConstants.ReadingRules.NEW_READING_RULE);

            if (CollectionUtils.isNotEmpty(readingRules)) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), readingRules.get(0).getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            XMLBuilder ruleElement = idVsData.getValue();

            NewReadingRuleContext readingRuleContext = constructRuleFromXMLBuilder(ruleElement);
            readingRuleContext.setId(ruleId);

            V3Util.processAndUpdateSingleRecord(FacilioConstants.ReadingRules.NEW_READING_RULE, readingRuleContext.getId(), FieldUtil.getAsJSON(readingRuleContext), null, null, null, null, null, null, null, null, null);

        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        for (Map.Entry<Long, XMLBuilder> xmlNdIds : idVsXMLComponents.entrySet()) {

            Long ruleId = xmlNdIds.getKey();
            XMLBuilder ruleBuilder = xmlNdIds.getValue();

            NewReadingRuleContext ruleCtx = new NewReadingRuleContext();
            ruleCtx.setId(ruleId);

            new RuleXMLdeSerializer(ruleCtx, xmlNdIds.getValue()).convertRootCause();

            ResourceType resourceType = ResourceType.valueOf(Integer.valueOf(ruleBuilder.getElement("resourceType").getText()));
            NameSpaceContext nsCtx = PackageBeanUtil.updateDataIdForConnected(ruleId, NSType.READING_RULE, resourceType);
            NamespaceBean nsBean = (NamespaceBean) BeanFactory.lookup("NamespaceBean");
            nsBean.updateNamespace(nsCtx);

            if (ruleCtx.getRca() != null) {
                FacilioChain rcaChain = TransactionChainFactoryV3.addRCARuleChain();
                FacilioContext rcaContext = rcaChain.getContext();
                rcaContext.put(FacilioConstants.ContextNames.NEW_READING_RULE, ruleCtx);

                rcaChain.execute();
            }

        }

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private NewReadingRuleContext constructRuleFromXMLBuilder(XMLBuilder ruleBuilder) throws Exception {
        NewReadingRuleContext rule = new NewReadingRuleContext();
        new RuleXMLdeSerializer(rule, ruleBuilder).execute();
        return rule;
    }


    private static class RuleXMLSerializer {

        NewReadingRuleContext rule;
        XMLBuilder ruleBuilder;

        RuleXMLSerializer(NewReadingRuleContext rule, XMLBuilder ruleBuilder) {
            this.rule = rule;
            this.ruleBuilder = ruleBuilder;
        }


        public void execute() throws Exception {
            convertBasicFields();
            convertAssetCategory();
            convertAlarmDetails();
            ruleBuilder.addElement(PackageBeanUtil.constructBuilderFromNameSpaceNdFields(rule.getNs(), ruleBuilder));
            convertFaultImpact();
            convertRootCause();
        }

        private void convertBasicFields() throws Exception {
            FacilioField readingField = Constants.getModBean().getField(rule.getReadingFieldId());

            ruleBuilder.e(PackageConstants.NAME).cData(rule.getName());
            ruleBuilder.e(PackageConstants.DESCRIPTION).cData(rule.getDescription());
            ruleBuilder.e(PackageConstants.ReadingRuleConstants.AUTO_CLEAR).text(value(rule.getAutoClear()));
            ruleBuilder.e(PackageConstants.STATUS).text(value(rule.getStatus()));
            ruleBuilder.e(PackageConstants.LINK_NAME).cData(rule.getLinkName());
            ruleBuilder.e(PackageConstants.MODULENAME).t(readingField.getModule().getName());
            ruleBuilder.e(PackageConstants.NameSpaceConstants.FIELD_NAME).t(readingField.getName());
            ruleBuilder.e("resourceType").t(String.valueOf(rule.getResourceType()));
        }

        private void convertFaultImpact() throws Exception {

            FaultImpactContext faultImpact = rule.getImpact();
            if (faultImpact != null) {
                XMLBuilder faultImpactBuilder = ruleBuilder.e(PackageConstants.FaultImpactConstants.FAULT_IMPACT);
                faultImpactBuilder.e(PackageConstants.LINK_NAME).cData(value(rule.getImpact().getLinkName()));
            }
        }

        //change category
        private void convertAssetCategory() throws Exception {
            XMLBuilder assetCategoryBuilder = ruleBuilder.e("category");
            ResourceCategory category = rule.getCategory();
            assetCategoryBuilder.e(PackageConstants.NAME).text(value(category.fetchDisplayName()));
        }

        private void convertRootCause() throws Exception {
            ReadingRuleRCAContext rca = rule.getRca();
            if (rca != null) {
                XMLBuilder rootCause = ruleBuilder.e(PackageConstants.ReadingRuleConstants.RCA);

                if (rca.getDataSetInterval() != null) {
                    rootCause.e(PackageConstants.ReadingRuleConstants.DATA_SET_INTERVAL).text(value(rca.getDataSetInterval()));
                }
                if (rca.getRuleInterval() != null) {
                    rootCause.e(PackageConstants.ReadingRuleConstants.RULE_INTERVAL).text(value(rca.getRuleInterval()));
                }

                if (CollectionUtils.isNotEmpty(rca.getRcaRuleIds())) {
                    XMLBuilder rcaRule = rootCause.e(PackageConstants.ReadingRuleConstants.RCA_RULE_IDS);
                    for (Long rcaRuleId : rca.getRcaRuleIds()) {
                        rcaRule.e("ruleLinkName").text(NewReadingRuleAPI.getNewReadingRuleLinkNameWithRuleId(rcaRuleId));
                    }
                }

                if (CollectionUtils.isNotEmpty(rca.getGroups())) {
                    XMLBuilder rcaGroups = rootCause.e(PackageConstants.ReadingRuleConstants.RCA_GROUPS);
                    for (RCAGroupContext rcagroup : rca.getGroups()) {
                        XMLBuilder group = rcaGroups.e("Group");
                        group.e(PackageConstants.NAME).cData(value(rcagroup.getName()));
                        group.e(PackageConstants.STATUS).text(value(rcagroup.getStatus()));

                        if (rcagroup.getCriteria() != null) {
                            LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + FacilioConstants.ContextNames.NEW_READING_ALARM + " RCA GroupName - " + rcagroup.getName());
                            group.addElement(PackageBeanUtil.constructBuilderFromCriteria(rcagroup.getCriteria(), group.e(PackageConstants.CriteriaConstants.CRITERIA), FacilioConstants.ContextNames.NEW_READING_ALARM));
                        }

                        convertRcaConditions(rcagroup, group);
                    }

                }
            }
        }

        private void convertRcaConditions(RCAGroupContext rcaGroup, XMLBuilder group) throws Exception {
            if (CollectionUtils.isNotEmpty(rcaGroup.getConditions())) {
                XMLBuilder rcaCondition = group.e(PackageConstants.ReadingRuleConstants.RCA_CONDITIONS);
                for (RCAConditionScoreContext conditionScoreContext : rcaGroup.getConditions()) {
                    XMLBuilder conditions = rcaCondition.e("Conditions");
                    conditions.e("score").text(value(conditionScoreContext.getScore()));

                    if (conditionScoreContext.getCriteria() != null) {
                        conditions.addElement(PackageBeanUtil.constructBuilderFromCriteria(conditionScoreContext.getCriteria(), conditions.e(PackageConstants.CriteriaConstants.CRITERIA), FacilioConstants.ContextNames.NEW_READING_ALARM));
                    }

                }
            }
        }

        private void convertAlarmDetails() throws Exception {
            RuleAlarmDetails alarmDetails = rule.getAlarmDetails();
            XMLBuilder alarmDetailsBuilder = ruleBuilder.e(PackageConstants.ReadingRuleConstants.ALARM_DETAILS);
            alarmDetailsBuilder.e(PackageConstants.ReadingRuleConstants.MESSAGE).cData(value(alarmDetails.getMessage()));
            alarmDetailsBuilder.e(PackageConstants.ReadingRuleConstants.SEVERITY).t(value(alarmDetails.getSeverity()));
            alarmDetailsBuilder.e(PackageConstants.ReadingRuleConstants.FAULT_TYPE).t(value(alarmDetails.getFaultType()));
            alarmDetailsBuilder.e(PackageConstants.ReadingRuleConstants.PROBLEM).cData(value(alarmDetails.getProblem()));
            alarmDetailsBuilder.e(PackageConstants.ReadingRuleConstants.POSSIBLE_CAUSES).cData(value(alarmDetails.getPossibleCausesStr()));
            alarmDetailsBuilder.e(PackageConstants.ReadingRuleConstants.RECOMMENDATIONS).cData(value(alarmDetails.getRecommendationsStr()));
        }

        private String value(Object val) {
            return val == null ? null : String.valueOf(val);
        }
    }

    private static class RuleXMLdeSerializer {

        NewReadingRuleContext rule;
        XMLBuilder ruleBuilder;

        RuleXMLdeSerializer(NewReadingRuleContext rule, XMLBuilder ruleBuilder) {
            this.rule = rule;
            this.ruleBuilder = ruleBuilder;
        }

        public void execute() throws Exception {
            convertBasicFields();
            convertAssetCategory();
            convertAlarmDetails();
            constructNs();
            convertFaultImpact();
        }

        private void convertRootCause() throws Exception {
            XMLBuilder rootCauseBuilder = ruleBuilder.getElement(PackageConstants.ReadingRuleConstants.RCA);
            if (rootCauseBuilder != null) {
                ReadingRuleRCAContext rca = new ReadingRuleRCAContext();

                if (rootCauseBuilder.getElement(PackageConstants.ReadingRuleConstants.DATA_SET_INTERVAL) != null) {
                    rca.setDataSetInterval(Long.valueOf(rootCauseBuilder.getElement(PackageConstants.ReadingRuleConstants.DATA_SET_INTERVAL).getText()));
                }
                if (rootCauseBuilder.getElement(PackageConstants.ReadingRuleConstants.RULE_INTERVAL) != null) {
                    rca.setRuleInterval(Long.valueOf((rootCauseBuilder.getElement(PackageConstants.ReadingRuleConstants.RULE_INTERVAL).getText())));
                }

                XMLBuilder rcaRule = rootCauseBuilder.getElement(PackageConstants.ReadingRuleConstants.RCA_RULE_IDS);
                if (rcaRule != null) {
                    List<XMLBuilder> rcaRuleIdBuilder = rootCauseBuilder.getElementList("ruleLinkName");
                    List<Long> rcaRuleIds = new ArrayList<>();
                    for (XMLBuilder builder : rcaRuleIdBuilder) {
                        Long ruleId = ConnectedRuleUtil.getConnectedRuleIdWithLinkName(builder.getText(), FacilioConstants.ReadingRules.NEW_READING_RULE);
                        rcaRuleIds.add(ruleId);
                    }

                    rca.setRcaRuleIds(rcaRuleIds);
                }

                XMLBuilder rcaGroups = rootCauseBuilder.getElement(PackageConstants.ReadingRuleConstants.RCA_GROUPS);
                if (rcaGroups != null) {
                    List<XMLBuilder> rcaxmlGroupList = rcaGroups.getFirstLevelElementListForTagName("Group");
                    List<RCAGroupContext> rcaGroupContexts = new ArrayList<>();

                    for (XMLBuilder rcaGroupBuilder : rcaxmlGroupList) {
                        RCAGroupContext rcaGroup = new RCAGroupContext();

                        rcaGroup.setName(rcaGroupBuilder.getElement(PackageConstants.NAME).getCData());
                        rcaGroup.setStatus(Boolean.valueOf(rcaGroupBuilder.getElement(PackageConstants.STATUS).getText()));
                        rcaGroup.setCriteria(PackageBeanUtil.constructCriteriaFromBuilder(rcaGroupBuilder.getElement(PackageConstants.CriteriaConstants.CRITERIA)));

                        rcaGroupContexts.add(rcaGroup);
                        XMLBuilder rcaCondition = rcaGroupBuilder.getElement(PackageConstants.ReadingRuleConstants.RCA_CONDITIONS);
                        convertRcaConditions(rcaGroup, rcaCondition);

                    }
                    rca.setGroups(rcaGroupContexts);
                }
                rule.setRca(rca);

            }
        }

        private void convertRcaConditions(RCAGroupContext rcaGroup, XMLBuilder rcaGroupBuilder) throws Exception {

            if (rcaGroupBuilder != null) {
                List<XMLBuilder> rcaxmlGroupList = rcaGroupBuilder.getFirstLevelElementListForTagName("Conditions");
                List<RCAConditionScoreContext> rcaGroupConditions = new ArrayList<>();

                for (XMLBuilder rcaCondition : rcaxmlGroupList) {
                    RCAConditionScoreContext rcaConditionScoreContext = new RCAConditionScoreContext();

                    rcaConditionScoreContext.setScore(Double.valueOf(rcaCondition.getElement("score").getText()));
                    rcaConditionScoreContext.setCriteria(PackageBeanUtil.constructCriteriaFromBuilder(rcaGroupBuilder.getElement(PackageConstants.CriteriaConstants.CRITERIA)));
                    rcaGroupConditions.add(rcaConditionScoreContext);
                }
                rcaGroup.setConditions(rcaGroupConditions);
            }
        }

        private void convertFaultImpact() throws Exception {
            XMLBuilder faultImpactBuilder = ruleBuilder.getElement(PackageConstants.FaultImpactConstants.FAULT_IMPACT);

            if (faultImpactBuilder != null) {
                Long impactId = ConnectedRuleUtil.getConnectedRuleIdWithLinkName(faultImpactBuilder.getElement(PackageConstants.LINK_NAME).getCData(), FacilioConstants.FaultImpact.MODULE_NAME);
                if (impactId != null) {
                    rule.setImpactId(impactId);
                }
            }
        }

        private void constructNs() throws Exception {
            NameSpaceContext ns = PackageBeanUtil.constructNamespaceNdFieldsFromBuilder(ruleBuilder);
            rule.setNs(ns);
        }

        private void convertBasicFields() throws Exception {
            FacilioField facilioField = PackageBeanUtil.getFacilioFieldFromBuilder(ruleBuilder);

            rule.setName(ruleBuilder.getElement(PackageConstants.NAME).getCData());
            rule.setDescription(ruleBuilder.getElement(PackageConstants.DESCRIPTION).getCData());
            rule.setAutoClear(Boolean.valueOf(ruleBuilder.getElement(PackageConstants.ReadingRuleConstants.AUTO_CLEAR).getText()));
            rule.setAutoClear(Boolean.valueOf(ruleBuilder.getElement(PackageConstants.STATUS).getText()));
            rule.setLinkName(ruleBuilder.getElement(PackageConstants.LINK_NAME).getCData());
            rule.setReadingFieldId(facilioField.getFieldId());
            rule.setReadingModuleId(facilioField.getModuleId());
            rule.setResourceType(Integer.valueOf(ruleBuilder.getElement("resourceType").getText()));
        }

        private void convertAssetCategory() throws Exception {
            XMLBuilder assetBuilder = ruleBuilder.getElement("category");
            String categoryName = assetBuilder.getElement(PackageConstants.NAME).getText();

            Long categoryId = PackageBeanUtil.getCategoryIdForFDDBasedOnResourceType(ResourceType.valueOf(rule.getResourceType()), categoryName);
            rule.setCategoryId(categoryId);
            //TODO:need to remove once meter support is live in alarms
            AssetCategoryContext assetCategoryContext = new AssetCategoryContext();
            assetCategoryContext.setId(categoryId);
            rule.setAssetCategory(assetCategoryContext);
        }

        private void convertAlarmDetails() throws Exception {

            XMLBuilder alarmDetailBuilder = ruleBuilder.getElement(PackageConstants.ReadingRuleConstants.ALARM_DETAILS);

            RuleAlarmDetails alarmDetails = new RuleAlarmDetails();
            alarmDetails.setMessage(alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.MESSAGE).getCData());
            alarmDetails.setSeverity(alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.SEVERITY).getText());
            alarmDetails.setFaultType(Integer.parseInt(alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.FAULT_TYPE).getText()));
            alarmDetails.setProblem(alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.PROBLEM).getCData());
            if (alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.POSSIBLE_CAUSES).getCData() != null) {
                alarmDetails.setPossibleCausesStr(alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.POSSIBLE_CAUSES).getCData());
            }
            if (alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.RECOMMENDATIONS).getText() != null) {
                alarmDetails.setRecommendationsStr(alarmDetailBuilder.getElement(PackageConstants.ReadingRuleConstants.RECOMMENDATIONS).getCData());
            }

            rule.setAlarmDetails(alarmDetails);
        }

    }

}
