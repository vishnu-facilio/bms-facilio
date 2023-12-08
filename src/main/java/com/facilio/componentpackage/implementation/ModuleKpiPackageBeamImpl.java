package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static com.facilio.componentpackage.utils.PackageBeanUtil.getNewRecordIds;

public class ModuleKpiPackageBeamImpl implements PackageBean<KPIContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long,Long> moduleKPIIds = new HashMap<>();
        FacilioModule module = ModuleFactory.getKpiModule();
        List<Map<String, Object>> props = DashboardUtil.getIdAndLinkNameAsProp(module);
        if(CollectionUtils.isNotEmpty(props)) {
            props.forEach(prop -> moduleKPIIds.put((Long) prop.get(PackageConstants.DashboardConstants.ID),-1L));
        }
        return moduleKPIIds;
    }

    @Override
    public Map<Long, KPIContext> fetchComponents(List<Long> ids) throws Exception {
        Map<String,FacilioField> kpiFields = FieldFactory.getAsMap(FieldFactory.getKPIFields());
        FacilioField field = kpiFields.get(PackageConstants.ModuleKpiConstants.METRIC_ID);
        FacilioModule module= ModuleFactory.getKpiModule();
        List<Map<String,Object>> metrics = KPIUtil.getFieldIds(module,field);
        List<Long> metricIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(metrics)){
            for(Map<String,Object> metric : metrics){
                if(metric.get(PackageConstants.ModuleKpiConstants.METRIC_ID)!=null){
                    metricIds.add((Long) metric.get(PackageConstants.ModuleKpiConstants.METRIC_ID));
                }
            }
        }
        Map<Long, Map<String,String>> metricIdVsMap = DashboardUtil.getFieldIdVsLinkName(metricIds);
        List<Map<String,Object>> dateFields = KPIUtil.getFieldIds(module,kpiFields.get("dateFieldId"));
        List<Long> dateFieldIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dateFields)){
            for(Map<String,Object> dateField : dateFields){
                if(dateField.get(PackageConstants.ModuleKpiConstants.DATE_FIELD_ID)!=null){
                    dateFieldIds.add((Long) dateField.get(PackageConstants.ModuleKpiConstants.DATE_FIELD_ID));
                }
            }
        }
        Map<Long,Map<String,String>> dateFieldIdsVsMap = DashboardUtil.getFieldIdVsLinkName(dateFieldIds);
       return KPIUtil.getModuleKpiWithId(ids,metricIdVsMap,dateFieldIdsVsMap);
    }

    @Override
    public void convertToXMLComponent(KPIContext component, XMLBuilder kpiElement) throws Exception {
        kpiElement.element(PackageConstants.DashboardConstants.NAME).text(component.getName());
        kpiElement.element(PackageConstants.DashboardConstants.MODULE_NAME).text(component.getModuleName());
        kpiElement.element(PackageConstants.DashboardConstants.DESCRIPTION).text(component.getDescription());
        kpiElement.element(PackageConstants.ModuleKpiConstants.METRIC_NAME).text(component.getMetricName());
        kpiElement.element(PackageConstants.DashboardConstants.DATE_OPERATOR).text(String.valueOf(component.getDateOperator()));
        kpiElement.element(PackageConstants.DashboardConstants.DATE_VALUE).text(String.valueOf(component.getDateValue()));
        kpiElement.element(PackageConstants.ModuleKpiConstants.AGGREGATION).text(String.valueOf(component.getAggr()));
        kpiElement.element(PackageConstants.ModuleKpiConstants.ACTIVE).text(String.valueOf(component.getActive()));
        kpiElement.element(PackageConstants.ModuleKpiConstants.SITE_ID).text(String.valueOf(component.getSiteId()));
        kpiElement.element(PackageConstants.ModuleKpiConstants.TARGET).text(String.valueOf(component.getTarget()));
        kpiElement.element(PackageConstants.ModuleKpiConstants.MIN_TARGET).text(String.valueOf(component.getMinTarget()));
        if (component.getCriteria() != null && !component.getCriteria().isEmpty()) {
            kpiElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(component.getCriteria(), kpiElement.element(PackageConstants.CriteriaConstants.CRITERIA), component.getModuleName()));
        }
        if(component.getWorkFlowString()!=null){
            kpiElement.element(PackageConstants.FunctionConstants.WORKFLOW_STRING).cData(component.getWorkFlowString());
        }

        if(component.getMetricFieldObj()!=null){
            Map<String,String> metricFieldObj = component.getMetricFieldObj();
            kpiElement.element(PackageConstants.ModuleKpiConstants.METRIC_FIELD_NAME).text(metricFieldObj.get(PackageConstants.DashboardConstants.FIELD_NAME));
            kpiElement.element(PackageConstants.ModuleKpiConstants.METRIC_MODULE_NAME).text(metricFieldObj.get(PackageConstants.MODULENAME));
        }
        if(component.getDateFieldObj()!=null){
            Map<String,String> dateFieldObj = component.getDateFieldObj();
            kpiElement.element(PackageConstants.ModuleKpiConstants.DATE_FIELD_NAME).text(dateFieldObj.get(PackageConstants.DashboardConstants.FIELD_NAME));
            kpiElement.element(PackageConstants.ModuleKpiConstants.DATE_MODULE_NAME).text(dateFieldObj.get(PackageConstants.MODULENAME));
        }
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
        Map<String,Long> uniqueIdentifierVsComponentId = new HashMap<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()){
            XMLBuilder kpiElement = idVsData.getValue();
            KPIContext kpiContext = createKpiContext(kpiElement);
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateKPICommand();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.KPI,kpiContext);
            chain.execute();
            KPIContext kpi = (KPIContext) context.get(FacilioConstants.ContextNames.KPI);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(),kpi.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder kpiElement = idVsComponent.getValue();
            KPIContext kpi = createKpiContext(kpiElement);
            kpi.setId(idVsComponent.getKey());
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateKPICommand();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.KPI,kpi);
            chain.execute();
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
        Map<String, FacilioField> kpiFields = FieldFactory.getAsMap(FieldFactory.getKPIFields());
        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            Long kpiId = idVsComponent.getKey();
            XMLBuilder kpiElement = idVsComponent.getValue();
            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addWhereValue(PackageConstants.DashboardConstants.ID, kpiId);
            long siteId = Long.parseLong(kpiElement.getElement(PackageConstants.ModuleKpiConstants.SITE_ID).getText());
            if (siteId > 0) {
                List<Long> siteIds = Arrays.asList(siteId);
                Map<Long, Long> siteIdMap = getNewRecordIds("site", siteIds);
                siteId = siteIdMap.get(siteId) != null ? siteIdMap.get(siteId) : -1;
            }
            updateVal.addUpdateValue("siteId", siteId);
            batchUpdateList.add(updateVal);
        }
        if (CollectionUtils.isNotEmpty(batchUpdateList)) {
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(Collections.singletonList(kpiFields.get("siteId")))
                    .table(ModuleFactory.getKpiModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(kpiFields.get(PackageConstants.DashboardConstants.ID)), batchUpdateList);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for(Long id : ids){
            FacilioChain chain = TransactionChainFactory.getDeleteKPICommand();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.ID, id);
            chain.execute();
        }
    }
    public KPIContext createKpiContext(XMLBuilder kpiElement) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        KPIContext kpiContext = new KPIContext();
        String name = kpiElement.getElement(PackageConstants.DashboardConstants.NAME).getText();
        String description = kpiElement.getElement(PackageConstants.DashboardConstants.DESCRIPTION).getText();
        String moduleName = kpiElement.getElement(PackageConstants.DashboardConstants.MODULE_NAME).getText();
        int dateOperator = Integer.parseInt(kpiElement.getElement(PackageConstants.DashboardConstants.DATE_OPERATOR).getText());
        String dateValue =  kpiElement.getElement(PackageConstants.DashboardConstants.DATE_VALUE).getText();
        int aggregation = Integer.parseInt(kpiElement.getElement(PackageConstants.ModuleKpiConstants.AGGREGATION).getText());
        FacilioModule module = modBean.getModule(moduleName);
        String metricName = kpiElement.getElement(PackageConstants.ModuleKpiConstants.METRIC_NAME).getText();
        Double target = Double.parseDouble(kpiElement.getElement(PackageConstants.ModuleKpiConstants.TARGET).getText());
        kpiContext.setTarget(target);
        Double minTarget = Double.parseDouble(kpiElement.getElement(PackageConstants.ModuleKpiConstants.MIN_TARGET).getText());
        kpiContext.setMinTarget(minTarget);
        XMLBuilder metricModuleBuilder = kpiElement.getElement(PackageConstants.ModuleKpiConstants.METRIC_MODULE_NAME);
        XMLBuilder metricFieldBuilder = kpiElement.getElement(PackageConstants.ModuleKpiConstants.METRIC_FIELD_NAME);
        if(metricModuleBuilder !=null && metricFieldBuilder !=null){
            String metricModuleName = kpiElement.getElement(PackageConstants.ModuleKpiConstants.METRIC_MODULE_NAME).getText();
            String metricFieldName = kpiElement.getElement(PackageConstants.ModuleKpiConstants.METRIC_FIELD_NAME).getText();
            FacilioField field = modBean.getField(metricFieldName,metricModuleName);
            kpiContext.setMetricId(field.getFieldId());
        }
        XMLBuilder criteriaElement = kpiElement.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            kpiContext.setCriteria(criteria);
        }
        Boolean active = Boolean.parseBoolean(kpiElement.getElement(PackageConstants.ModuleKpiConstants.ACTIVE).getText());
        XMLBuilder dateModuleBuilder = kpiElement.getElement(PackageConstants.ModuleKpiConstants.DATE_MODULE_NAME);
        XMLBuilder dateFieldBuilder = kpiElement.getElement(PackageConstants.ModuleKpiConstants.DATE_FIELD_NAME);
        if(dateModuleBuilder!=null && dateFieldBuilder !=null){
            String dateModuleName = kpiElement.getElement(PackageConstants.ModuleKpiConstants.DATE_MODULE_NAME).getText();
            String dateFieldName = kpiElement.getElement(PackageConstants.ModuleKpiConstants.DATE_FIELD_NAME).getText();
            FacilioField field = modBean.getField(dateFieldName,dateModuleName);
            kpiContext.setDateFieldId(field.getFieldId());
        }

        if(kpiElement.getElement(PackageConstants.FunctionConstants.WORKFLOW_STRING)!=null){
            String customScript = kpiElement.getElement(PackageConstants.FunctionConstants.WORKFLOW_STRING).getCData();
            Long workflowId = WorkflowUtil.addWorkflow(customScript);
            kpiContext.setWorkflowId(workflowId);
        }

        kpiContext.setName(name);
        kpiContext.setDescription(description);
        kpiContext.setModuleId(module.getModuleId());
        kpiContext.setMetricName(metricName);
        kpiContext.setActive(active);
        kpiContext.setDateOperator(dateOperator);
        kpiContext.setDateValue(dateValue);
        kpiContext.setAggr(aggregation);
        return kpiContext;
    }
}
