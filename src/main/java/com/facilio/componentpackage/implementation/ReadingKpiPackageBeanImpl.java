package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.NamespaceBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
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
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.util.ConnectedRuleUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReadingKpiPackageBeanImpl implements PackageBean<ReadingKPIContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> recordIdVsModuleId = ConnectedRuleUtil.getAllConnectedRuleIdsAndModuleIds(FacilioConstants.ReadingKpi.READING_KPI);
        if (MapUtils.isNotEmpty(recordIdVsModuleId)) {
            return recordIdVsModuleId;
        }
        return new HashMap<>();
    }

    @Override
    public Map<Long, ReadingKPIContext> fetchComponents(List<Long> ids) throws Exception {
        List<ReadingKPIContext> readingKPIList = ReadingKpiAPI.getReadingKpi(ids);
        if (CollectionUtils.isNotEmpty(readingKPIList)) {
            return readingKPIList.stream().collect(Collectors.toMap(ReadingKPIContext::getId, Function.identity()));
        }
        return new HashMap<>();
    }

    @Override
    public void convertToXMLComponent(ReadingKPIContext readingKPIContext, XMLBuilder kpiBuilder) throws Exception {
        new ReadingKPISerializer(readingKPIContext, kpiBuilder).execute();
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

        ReadingKPIContext readingKpi;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder ruleElement = idVsData.getValue();
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule module = moduleBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);

            readingKpi = constructKPIFromXMLBuilder(ruleElement);
            FacilioContext context = V3Util.createRecord(module, FieldUtil.getAsProperties(readingKpi));

            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<ReadingKPIContext> readingKpiList = recordMap.get((FacilioConstants.ReadingKpi.READING_KPI));

            if (CollectionUtils.isNotEmpty(readingKpiList)) {
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), readingKpiList.get(0).getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    private ReadingKPIContext constructKPIFromXMLBuilder(XMLBuilder ruleElement) throws Exception {
        ReadingKPIContext kpiContext = new ReadingKPIContext();
        new ReadingKPIDeSerializer(kpiContext, ruleElement).execute();
        return kpiContext;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            XMLBuilder ruleElement = idVsData.getValue();

            ReadingKPIContext readingKpi = constructKPIFromXMLBuilder(ruleElement);
            readingKpi.setId(ruleId);

            V3Util.processAndUpdateSingleRecord(FacilioConstants.ReadingKpi.READING_KPI, readingKpi.getId(), FieldUtil.getAsJSON(readingKpi), null, null, null, null, null, null, null, null, null);

        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> xmlNdIds : idVsXMLComponents.entrySet()) {
            Long kpiId = xmlNdIds.getKey();
            XMLBuilder kpiBuilder = xmlNdIds.getValue();

//            ResourceType resourceType = ResourceType.valueOf(Integer.valueOf(kpiBuilder.getElement("resourceType").getText()));
//            PackageBeanUtil.updateDataIdForConnected(kpiId, NSType.KPI_RULE, resourceType);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    class ReadingKPIDeSerializer {

        ReadingKPIContext readingKPI;
        XMLBuilder kpiBuilder;

        public ReadingKPIDeSerializer(ReadingKPIContext readingKPI, XMLBuilder kpiBuilder) {
            this.readingKPI = readingKPI;
            this.kpiBuilder = kpiBuilder;
        }

        public void execute() throws Exception {
            constructBasicFieldsFromXML();
            constructNsFromXML();
            convertAssetCategoryFromBuilder();
        }

        private void constructNsFromXML() throws Exception {
            NameSpaceContext ns = PackageBeanUtil.constructNamespaceNdFieldsFromBuilder(kpiBuilder);
            readingKPI.setNs(ns);
        }

        private void convertAssetCategoryFromBuilder() throws Exception {
            XMLBuilder assetBuilder = kpiBuilder.getElement("category");
            String categoryName = assetBuilder.getElement(PackageConstants.NAME).getText();

            Long categoryId = PackageBeanUtil.getCategoryIdForFDDBasedOnResourceType(ResourceType.valueOf(readingKPI.getResourceType()), categoryName);
            readingKPI.setCategoryId(categoryId);
        }

        private void constructBasicFieldsFromXML() throws Exception {
            //  TODO:need to support site once data supported
            FacilioField facilioField = PackageBeanUtil.getFacilioFieldFromBuilder(kpiBuilder);

            readingKPI.setName(kpiBuilder.getElement(PackageConstants.NAME).getCData());
            readingKPI.setDescription(kpiBuilder.getElement(PackageConstants.DESCRIPTION).getCData());
            readingKPI.setKpiCategoryStr(kpiBuilder.getElement(PackageConstants.ReadingKPIConstants.KPI_CATEGORY).getCData());
            readingKPI.setResourceType(Integer.valueOf(kpiBuilder.getElement(PackageConstants.ReadingKPIConstants.RESOURCE_TYPE).getText()));
            readingKPI.setFrequency(Integer.valueOf(kpiBuilder.getElement(PackageConstants.ReadingKPIConstants.FREQUENCY).getText()));
            readingKPI.setKpiType(Integer.valueOf(kpiBuilder.getElement(PackageConstants.ReadingKPIConstants.KPI_TYPE).getText()));
            readingKPI.setMetricId(Integer.valueOf(kpiBuilder.getElement(PackageConstants.ReadingKPIConstants.METRIC_UNIT).getText()));
            readingKPI.setUnitId(Integer.valueOf(kpiBuilder.getElement(PackageConstants.ReadingKPIConstants.UNIT).getText()));
            readingKPI.setReadingFieldId(facilioField.getFieldId());
            readingKPI.setReadingModuleId(facilioField.getModuleId());
            readingKPI.setResourceType(Integer.valueOf(kpiBuilder.getElement("resourceType").getText()));
        }

    }

    class ReadingKPISerializer {
        ReadingKPIContext readingKPI;
        XMLBuilder kpiBuilder;

        public ReadingKPISerializer(ReadingKPIContext readingKPI, XMLBuilder kpiBuilder) {
            this.readingKPI = readingKPI;
            this.kpiBuilder = kpiBuilder;
        }

        public void execute() throws Exception {
            constructBasicFields();
            constructNs();
            convertAssetCategory();
        }

        private void convertAssetCategory() throws Exception {
            XMLBuilder assetCategoryBuilder = kpiBuilder.e("category");
            ResourceCategory category=readingKPI.getCategory();
            assetCategoryBuilder.e(PackageConstants.NAME).text(String.valueOf(category.fetchDisplayName()));
        }

        private void constructBasicFields() throws Exception {
            //TODO:site support
            KPICategoryContext kpiCategory=KPIUtil.getKPICategoryContext(readingKPI.getKpiCategory());

            kpiBuilder.e(PackageConstants.NAME).cData(readingKPI.getName());
            kpiBuilder.e(PackageConstants.DESCRIPTION).cData(readingKPI.getDescription());
            kpiBuilder.e(PackageConstants.ReadingKPIConstants.KPI_CATEGORY).cData(kpiCategory.getName());
            kpiBuilder.e(PackageConstants.ReadingKPIConstants.RESOURCE_TYPE).t(String.valueOf(readingKPI.getResourceType()));
            kpiBuilder.e(PackageConstants.ReadingKPIConstants.FREQUENCY).t(String.valueOf(readingKPI.getFrequency()));
            kpiBuilder.e(PackageConstants.ReadingKPIConstants.KPI_TYPE).t(String.valueOf(readingKPI.getKpiType()));
            kpiBuilder.e(PackageConstants.ReadingKPIConstants.METRIC_UNIT).t(String.valueOf(readingKPI.getMetricId()!=null?readingKPI.getMetricId():-1));
            kpiBuilder.e(PackageConstants.ReadingKPIConstants.UNIT).text(String.valueOf(readingKPI.getUnitId()!=null?readingKPI.getUnitId():-1));
            kpiBuilder.e("resourceType").t(String.valueOf(readingKPI.getResourceType()));
            if (readingKPI.getReadingModuleId() > 0) {
                FacilioField readingField = Constants.getModBean().getField(readingKPI.getReadingFieldId());
                kpiBuilder.e(PackageConstants.MODULENAME).text(readingField.getModule().getName());
                kpiBuilder.e(PackageConstants.NameSpaceConstants.FIELD_NAME).text(readingField.getName());
            }
        }

        private void constructNs() throws Exception {
            kpiBuilder.addElement(PackageBeanUtil.constructBuilderFromNameSpaceNdFields(readingKPI.getNs(), kpiBuilder));
        }

    }
}
