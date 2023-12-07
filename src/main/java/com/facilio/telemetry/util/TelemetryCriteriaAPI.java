package com.facilio.telemetry.util;

import bsh.NameSpace;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.NamespaceBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.*;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.telemetry.beans.TelemetryCriteriaBean;
import com.facilio.telemetry.context.FacilioReadingData;
import com.facilio.telemetry.context.NamespaceFieldsWrapper;
import com.facilio.telemetry.context.TelemetryCriteriaCacheContext;
import com.facilio.telemetry.handler.AggregationHandler;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;
import java.util.*;

import static com.facilio.constants.FacilioConstants.ReadingKpi.DEFAULT_RES_ID;

@Log4j
public class TelemetryCriteriaAPI {

    public static boolean getTelemetryCriteriaResult(Long telemetryCriteriaId,Long assetId) throws Exception {
        TelemetryCriteriaBean telemetryCriteriaBean = (TelemetryCriteriaBean) BeanFactory.lookup("TelemetryCriteriaBean");
        TelemetryCriteriaCacheContext telemetryCriteria = telemetryCriteriaBean.fetchTelemetryCriteria(telemetryCriteriaId);
        NameSpaceContext ns = telemetryCriteria.getNamespace();
        NamespaceFieldsWrapper nsFieldsWrapper = resolveRelatedFieldsAndSetResourceId(ns.getFields(), assetId).orElse(new NamespaceFieldsWrapper(new ArrayList<>(), assetId));
        Map<String, Object> paramsMap = fetchReadingsParam(ns,nsFieldsWrapper);
        return executeWorkflow(ns,paramsMap);
    }

    private static Map<String,Object> fetchReadingsParam(NameSpaceContext ns,NamespaceFieldsWrapper nsFieldsWrapper) throws Exception {
        TreeMap<Long, Map<String, List<Double>>> resReadingsMap = new TreeMap<>();

        if(nsFieldsWrapper != null) {
            for (NameSpaceField nsField : nsFieldsWrapper) {
                if (!Objects.equals(nsField.getResourceId(), DEFAULT_RES_ID)) {
                    fetchReadingsForNsField(resReadingsMap, nsField);
                }
            }
        }
        for (Map.Entry<Long, Map<String, List<Double>>> entry : resReadingsMap.entrySet()) {
            Map<String, Object> values = groupByVarNameAndGetVarNameVsValueMap(ns, entry.getValue()).orElse(new HashMap<>());
            return values;
        }
        return null;
    }


    public static void fetchReadingsForNsField(TreeMap<Long, Map<String, List<Double>>> readingsMap, NameSpaceField nsField) throws Exception {
        Double value = fetchAggregationValue(nsField);
        if (value != null) {
            Map<String,Object> prop = new HashMap<>();
            prop.put(nsField.getField().getName(),value);
            prop.put("ttime",-1l);
            ReadingKpiAPI.populateReadingsMapFromProps(null, readingsMap, nsField.getVarName(), nsField.getField(), new ArrayList<>(Arrays.asList(prop)));
        }
    }

    public static boolean executeWorkflow(NameSpaceContext nameSpace, Map<String, Object> params) throws Exception {
        ScriptContext scriptContext;
        try {
            scriptContext = ScriptUtil.executeScript(nameSpace.getWorkflowContext().getWorkflowV2String(), params);
        } catch (Exception e) {
            return false;
        }
        if (scriptContext != null && scriptContext.getReturnTypeEnum() == WorkflowFieldType.BOOLEAN) {
            if (scriptContext.getReturnValue() instanceof Boolean) {
                return (Boolean) scriptContext.getReturnValue();
            }
        }
        return false;
    }

    public static Map<String, Object> constructArgs(NameSpaceContext ns, Map<NameSpaceField, FacilioReadingData> depsMap) throws Exception {
        Map<String, Object> varNameVsValueMap = new HashMap<>(); // is in order of the ns fields

        for(Map.Entry<NameSpaceField, FacilioReadingData> entrySet : depsMap.entrySet()) {
            varNameVsValueMap.put(entrySet.getKey().getVarName(), entrySet.getValue().getValue());
        }
        return varNameVsValueMap;
    }

    public static Optional<NamespaceFieldsWrapper> resolveRelatedFieldsAndSetResourceId(List<NameSpaceField> fields, Long resourceId) throws Exception {
        List<NameSpaceField> flattenedNsFields = new ArrayList<>();
        for (NameSpaceField fld : fields) {
            switch (fld.getNsFieldType()) {
                case RELATED_READING : {
                    ReadingKpiAPI.resolveRelatedFieldsAndAddToList(resourceId, flattenedNsFields, fld);
                    break;
                }
                case ASSET_READING:
                case ASSET:
                case METER_READING:
                case METER:
                    case SITE:
                case SITE_READING: {
                    if (fld.getResourceId() == null) {
                        NameSpaceField nsField = getNsFieldClone(resourceId, fld);
                        flattenedNsFields.add(nsField);
                    } else {
                        flattenedNsFields.add(fld);
                    }
                    break;
                }
                case SPACE:
                case SPACE_READING: {
                    flattenedNsFields.add(fld);
                    break;
                }
            }
            LOGGER.debug("fld type : " + fld.getNsFieldType() + " flat fields : " + flattenedNsFields);
        }
        return Optional.of(new NamespaceFieldsWrapper(flattenedNsFields, resourceId));
    }



    private static NameSpaceField getNsFieldClone(Long resourceId, NameSpaceField fld) throws CloneNotSupportedException {
        NameSpaceField nsField = (NameSpaceField) fld.clone();
        nsField.setResourceId(resourceId);
        return nsField;
    }

    public static Optional<Map<String, Object>> groupByVarNameAndGetVarNameVsValueMap(NameSpaceContext ns, Map<String, List<Double>> varNameVsValues) {
        Map<String, Object> groupedVarNameVsValueMap = new HashMap<>();

        for (NameSpaceField fld : ns.getFields()) {
            List<Double> values = ReadingKpiAPI.getDefaultOnNullData(fld, varNameVsValues.get(fld.getVarName()));
            Double value = null;
            if (values != null) {
                NamespaceFieldRelated relatedInfo = fld.getRelatedInfo();
                AggregationType relAggregationType = AggregationType.valueOf(relatedInfo != null && relatedInfo.getRelAggregationTypeInt() != null
                        ? relatedInfo.getRelAggregationTypeInt()
                        : AggregationType.AVG.getIndex());

                value = ReadingKpiAPI.getValueForAggregation(relAggregationType != null ? relAggregationType : AggregationType.AVG, values);
            }
            groupedVarNameVsValueMap.put(fld.getVarName(), value);
        }
        return Optional.of(groupedVarNameVsValueMap);
    }

    private static Double fetchAggregationValue(NameSpaceField field) throws Exception {
        AggregationHandler handler = field.getAggregation().getHandler();
        Double aggregationValue = handler.aggregate(field);
        return aggregationValue;
    }

    public static FacilioReadingData fetchReadingsForResource(NameSpaceContext ns, NameSpaceField nsField, Long ttime, NsFieldType nsType) throws Exception {
        String moduleName = null;
        switch (nsType) {
            case METER :
                moduleName = FacilioConstants.ContextNames.METER;
                break;
            case SPACE :
                moduleName = FacilioConstants.ContextNames.SPACE;
                break;
            case ASSET :
                moduleName = FacilioConstants.ContextNames.ASSET;
                break;
            case SITE :
                moduleName = FacilioConstants.ContextNames.SITE;
                break;
            default : throw new IllegalStateException("Unexpected value: " + nsType);
        };
        Long resId = nsField.getResourceId();

        if (resId == null) {
            throw new IllegalArgumentException("orgId: " + ns.getOrgId() + " nsid: " + ns.getId() + " Resource ID is null for asset/space field");
        }

        FacilioField field = nsField.getField();
        Map<String, Object> resourceMap = getResourceAsMapFromId(ns, resId, field, moduleName);
        Object fieldObj = resourceMap.get(field.getName());

        if (fieldObj == null && field.getDataTypeEnum() == FieldType.BOOLEAN) {
            fieldObj = false;
        }
        return constructReadingData(ns.getOrgId(), resId, nsField.getFieldId(), fieldObj, ttime);
    }


    public static Map<String, Object> getResourceAsMapFromId(NameSpaceContext ns, Long resId, FacilioField field, String moduleName) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule baseModule = modBean.getModule(moduleName);
        FacilioModule resModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(baseModule.getTableName())
                .innerJoin(resModule.getTableName())
                .on(baseModule.getTableName() + ".ID = " + resModule.getTableName() + ".ID")
                .select(Arrays.asList(field, FieldFactory.getIdField(resModule)))
                .andCondition(CriteriaAPI.getIdCondition(resId, resModule));

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return props.get(0);
        }
        throw new IllegalArgumentException("orgId: " + ns.getOrgId() + " nsId: " + ns.getId() + " field" + field.getId() + " getResourceAsMapFromId: Invalid Resource Id" + resId + ": qry: " + builder);
    }

    public static FacilioReadingData constructReadingData(long orgId, Long resourceId, Long fieldId, Object value, long time) {
        FacilioReadingData readingData = new FacilioReadingData();
        readingData.setOrgId(orgId);
        readingData.setResourceId(resourceId);
        readingData.setFieldId(fieldId);
        readingData.setValue(value);
        readingData.setTtime(time);
        return readingData;
    }
}