package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SLARuleAPI;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.SLAEntityContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SLAEntityPackageBeanImpl implements PackageBean<SLAEntityContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long,Long> entityIdVsModuleId = new HashMap<>();
        FacilioModule entityModule = ModuleFactory.getSLAEntityModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(entityModule));
            add(FieldFactory.getNumberField("moduleId","MODULEID",entityModule));
        }};

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(entityModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("MODULEID","moduleId", String.valueOf(-1), NumberOperators.NOT_EQUALS));

        List<Map<String, Object>> entityProps = builder.get();

        if (CollectionUtils.isNotEmpty(entityProps)) {
            long moduleId;
            long entityId;
            for (Map<String, Object> prop : entityProps) {
                moduleId = prop.containsKey("moduleId") ? (Long) prop.get("moduleId") : -1;
                entityId = prop.containsKey("id") ? (Long) prop.get("id") : -1;
                entityIdVsModuleId.put(entityId, moduleId);
            }
        }

        return entityIdVsModuleId;
    }

    @Override
    public Map<Long, SLAEntityContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long,SLAEntityContext> slaEntityMap = new HashMap<>();

        for (Long id : ids){
            SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(id);
            slaEntityMap.put(id,slaEntity);
        }

        return slaEntityMap;
    }

    @Override
    public void convertToXMLComponent(SLAEntityContext slaEntity, XMLBuilder element) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        long moduleId = slaEntity.getModuleId();
        FacilioModule module = moduleBean.getModule(moduleId);
        long baseFieldId = slaEntity.getBaseFieldId();
        long dueFieldId = slaEntity.getDueFieldId();
        FacilioField baseField = moduleBean.getField(baseFieldId);
        FacilioField dueField = moduleBean.getField(dueFieldId);
        element.element(PackageConstants.WorkFlowRuleConstants.BASE_FIELD_NAME).text(baseField.getName());
        element.element(PackageConstants.WorkFlowRuleConstants.DUE_FIELD_NAME).text(dueField.getName());
        element.element(PackageConstants.MODULENAME).text(module.getName());
        element.element(PackageConstants.NAME).text(slaEntity.getName());
        if (slaEntity.getDescription() != null){
            element.element(PackageConstants.DESCRIPTION).text(slaEntity.getDescription());
        }
        if (slaEntity.getVerbName() != null){
            element.element(PackageConstants.WorkFlowRuleConstants.VERBNAME).text(slaEntity.getVerbName());
        }
        if (slaEntity.getCriteria() != null){
            element.addElement(PackageBeanUtil.constructBuilderFromCriteria(slaEntity.getCriteria(), element.element(PackageConstants.CriteriaConstants.CRITERIA), module.getName()));
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
        Map<String,Long> uniqueNameVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        SLAEntityContext slaEntity = new SLAEntityContext();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            slaEntity = constructComponentFromXML(builder, moduleBean);

            long slaEntityId  = addOrUpdateSLAEntity(slaEntity);
            if (slaEntityId > 0) {
                uniqueNameVsComponentId.put(idVsData.getKey(), slaEntityId);
            }
        }

        return uniqueNameVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        SLAEntityContext slaEntity;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long entityId = idVsData.getKey();
            if (entityId == null || entityId < 0) {
                continue;
            }

            XMLBuilder builder = idVsData.getValue();

            slaEntity = constructComponentFromXML(builder, moduleBean);
            slaEntity.setId(entityId);

            addOrUpdateSLAEntity(slaEntity);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private long addOrUpdateSLAEntity(SLAEntityContext slaEntity) throws Exception{

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(slaEntity.getModuleId());

        SLAEntityContext existingSLAEntity = SLAWorkflowAPI.getSLAEntity(slaEntity.getModuleId(),slaEntity.getName());

        if (existingSLAEntity != null){
            slaEntity.setId(existingSLAEntity.getId());
        }

        FacilioChain chain = TransactionChainFactory.getAddOrUpdateSLAEntityChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        context.put(FacilioConstants.ContextNames.SLA_ENTITY, slaEntity);
        chain.execute();

        long slaEntityId = (long) context.getOrDefault(FacilioConstants.ContextNames.ID, -1L);

        return slaEntityId;
    }

    private SLAEntityContext constructComponentFromXML(XMLBuilder builder, ModuleBean moduleBean) throws Exception{
        SLAEntityContext slaEntity = new SLAEntityContext();
        String name = builder.getElement(PackageConstants.NAME).getText();
        if (builder.getElement(PackageConstants.DESCRIPTION) != null){
           String description = builder.getElement(PackageConstants.DESCRIPTION).getText();
           slaEntity.setDescription(description);
        }
        String baseFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.BASE_FIELD_NAME).getText();
        String dueFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.DUE_FIELD_NAME).getText();
        String moduleName = builder.getElement(PackageConstants.MODULENAME).getText();
        FacilioModule module = moduleBean.getModule(moduleName);
        long baseFieldId = moduleBean.getField(baseFieldName,moduleName).getFieldId();
        long dueFieldId = moduleBean.getField(dueFieldName,moduleName).getFieldId();

        slaEntity.setName(name);
        slaEntity.setModuleId(module.getModuleId());
        slaEntity.setBaseFieldId(baseFieldId);
        slaEntity.setDueFieldId(dueFieldId);

        XMLBuilder criteriaElement = builder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            slaEntity.setCriteria(criteria);
        }

        return slaEntity;
    }
}
