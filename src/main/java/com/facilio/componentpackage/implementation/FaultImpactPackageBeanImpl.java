package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingrule.faultimpact.FaultImpactAPI;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.readingrule.util.ConnectedRuleUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FaultImpactPackageBeanImpl implements PackageBean<FaultImpactContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> allImpactIds = ConnectedRuleUtil.getAllConnectedRuleIdsAndModuleIds(FacilioConstants.FaultImpact.MODULE_NAME);
        if (MapUtils.isNotEmpty(allImpactIds)) {
            return allImpactIds;
        }
        return new HashMap<>();
    }

    @Override
    public Map<Long, FaultImpactContext> fetchComponents(List<Long> ids) throws Exception {
        List<FaultImpactContext> faultImpactList = FaultImpactAPI.getFaultImpactRecords(ids);
        if (CollectionUtils.isNotEmpty(faultImpactList)) {
            return faultImpactList.stream().collect(Collectors.toMap(FaultImpactContext::getId, Function.identity()));
        }
        return new HashMap<>();
    }

    @Override
    public void convertToXMLComponent(FaultImpactContext faultImpact, XMLBuilder faultImpBuilder) throws Exception {

        faultImpBuilder.e(PackageConstants.NAME).cData(faultImpact.getName());
        faultImpBuilder.e(PackageConstants.DESCRIPTION).cData(faultImpact.getDescription());
        faultImpBuilder.e(PackageConstants.LINK_NAME).cData(faultImpact.getLinkName());
        faultImpBuilder.e(PackageConstants.FaultImpactConstants.PM_ASSIGNMENT_TYPE).text(String.valueOf(faultImpact.getType()));

        faultImpBuilder.addElement(PackageBeanUtil.constructBuilderFromWorkFlowContext(faultImpact.getWorkflow(),faultImpBuilder.e(PackageConstants.WorkFlowRuleConstants.WORKFLOW)));
        if (CollectionUtils.isNotEmpty(faultImpact.getFields())) {
            List<NameSpaceField> nameSpaceFields = faultImpact.getFields().stream().map(m -> m.getNameSpaceField()).collect(Collectors.toList());
            faultImpBuilder.addElement(PackageBeanUtil.constructBuilderFromNameSpaceFields(nameSpaceFields, faultImpBuilder));
        }
        convertAssetCategory(faultImpBuilder,faultImpact);
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

        FaultImpactContext faultImpactContext;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder faultElement = idVsData.getValue();
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule module = moduleBean.getModule(FacilioConstants.FaultImpact.MODULE_NAME);

            faultImpactContext = constructImpactFromXMLBuilder(faultElement);
            FacilioContext context = V3Util.createRecord(module, FieldUtil.getAsProperties(faultImpactContext));

            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<FaultImpactContext> faultImpacts = recordMap.get(FacilioConstants.FaultImpact.MODULE_NAME);

            if (CollectionUtils.isNotEmpty(faultImpacts)) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), faultImpacts.get(0).getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long impactId = idVsData.getKey();
            XMLBuilder impactElement = idVsData.getValue();

            FaultImpactContext faultImpact = constructImpactFromXMLBuilder(impactElement);
            faultImpact.setId(impactId);

            V3Util.processAndUpdateSingleRecord(FacilioConstants.FaultImpact.MODULE_NAME, faultImpact.getId(), FieldUtil.getAsJSON(faultImpact), null, null, null, null, null, null, null, null, null);

        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private static void constructFaultImpactNsFields(XMLBuilder faultElement, FaultImpactContext faultImpactContext) throws Exception {
        XMLBuilder impactNsBuilder = faultElement.getElement("Fields");
        if (impactNsBuilder != null) {
            List<NameSpaceField> nsFields = PackageBeanUtil.constructNamespaceFieldsFromBuilder(impactNsBuilder);
            if (CollectionUtils.isNotEmpty(nsFields)) {
                List<FaultImpactNameSpaceFieldContext> impactNsFlds = nsFields.stream().map(m -> FaultImpactNameSpaceFieldContext.getImpactNsFieldFromNsFld(m)).collect(Collectors.toList());
                faultImpactContext.setFields(impactNsFlds);
            }
        }
    }

    private FaultImpactContext constructImpactFromXMLBuilder(XMLBuilder faultElement) throws Exception {
        FaultImpactContext faultImpactContext = new FaultImpactContext();
        constructFaultImpactFields(faultElement, faultImpactContext);
        convertAssetCategoryFromBuilder(faultElement,faultImpactContext);
        constructFaultImpactNsFields(faultElement, faultImpactContext);
        WorkflowContext workflowContext=PackageBeanUtil.constructWorkflowContextFromBuilder(faultElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW));
        faultImpactContext.setWorkflow(workflowContext);
        return faultImpactContext;
    }

    private void constructFaultImpactFields(XMLBuilder faultElement, FaultImpactContext faultImpactContext)  {

        faultImpactContext.setName(faultElement.getElement(PackageConstants.NAME).getCData());
        faultImpactContext.setDescription(faultElement.getElement(PackageConstants.DESCRIPTION).getCData());
        faultImpactContext.setLinkName(faultElement.getElement(PackageConstants.LINK_NAME).getCData());
        faultImpactContext.setType(Integer.valueOf(faultElement.getElement(PackageConstants.FaultImpactConstants.PM_ASSIGNMENT_TYPE).getText()));

    }

    private void convertAssetCategory(XMLBuilder faultElement,FaultImpactContext faultImpact) throws Exception {
        XMLBuilder assetCategoryBuilder = faultElement.e(PackageConstants.AssetCategoryConstants.ASSET_CATEGORY);
        AssetCategoryContext assetCategory = faultImpact.getAssetCategory();
        V3AssetCategoryContext categoryContext = AssetsAPI.getAssetCategories(Collections.singletonList(assetCategory.getId())).get(0);
        assetCategoryBuilder.e("name").text(String.valueOf(categoryContext.getDisplayName()));
    }

    private void convertAssetCategoryFromBuilder(XMLBuilder faultElement, FaultImpactContext faultImpactContext) throws Exception {
        XMLBuilder assetBuilder = faultElement.getElement(PackageConstants.AssetCategoryConstants.ASSET_CATEGORY);
        String categoryName = assetBuilder.getElement(PackageConstants.NAME).getText();

        Map<String, Long> assetNameVsId = PackageBeanUtil.getAssetCategoryNameVsId(null);
        AssetCategoryContext assetCategory = new AssetCategoryContext();
        assetCategory.setId(assetNameVsId.get(categoryName));

        faultImpactContext.setAssetCategory(assetCategory);
    }


}
