package com.facilio.agentv2.migration.beans;

import com.facilio.agent.FacilioAgent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AgentMigrationBeanImpl implements AgentMigrationBean{
    @Override
    public FacilioAgent getAgent(long agentId) {
        return null;
    }

    @Override
    public Map<FacilioControllerType,List<Long>> fetchControllers(long agentId) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.CONTROLLER);
        List<FacilioField> fields = moduleBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField agentField = fieldMap.get(AgentConstants.AGENT_ID);
        Map<FacilioControllerType,List<Long>> controllerMap = new HashMap<>();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(Arrays.asList(fieldMap.get(AgentConstants.CONTROLLER_TYPE), FieldFactory.getIdField(module)))
                .andCondition(CriteriaAPI.getCondition(agentField, String.valueOf(agentId), NumberOperators.EQUALS))
                ;
        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<Controller> controllers = FieldUtil.getAsBeanListFromMapList(props, Controller.class);
            for (Controller controller: controllers) {
                FacilioControllerType controllerType = FacilioControllerType.valueOf(controller.getControllerType());
                List<Long> controllerIds = controllerMap.get(controllerType);
                if (controllerIds == null) {
                    controllerIds = new ArrayList<>();
                    controllerMap.put(controllerType, controllerIds);
                }
                controllerIds.add(controller.getId());
            }
        }
        return controllerMap;
    }

    @Override
    public void updateController(FacilioControllerType type, long sourceAgentId, long targetAgentId) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = ControllerApiV2.getControllerModuleName(type);
        FacilioField agentField = moduleBean.getField(AgentConstants.AGENT_ID, moduleName);

        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder()
                .moduleName(moduleName)
                .fields(Collections.singletonList(agentField))
                .andCondition(CriteriaAPI.getCondition(agentField, String.valueOf(sourceAgentId), NumberOperators.EQUALS));
        Map<String, Object> prop = Collections.singletonMap(agentField.getName(), targetAgentId);
        updateRecordBuilder.updateViaMap(prop);

    }

    @Override
    public List<Point> fetchPoints(long agentId, List<Long> controllerIds) throws Exception {

        GetPointRequest getPointRequest = new GetPointRequest()
                .withAgentId(agentId)
                .withControllerIds(controllerIds)
                .orderBy("ID")
                .limit(-1);
        List<Point> points = getPointRequest.getPoints();
        return points;
    }

    @Override
    public void addPoints(FacilioControllerType controllerType,  List<Map<String, Object>> points) throws Exception {
        PointsUtil.addPoints(controllerType, points);
    }

    @Override
    public List<FacilioField> fetchReadings(long categoryId) throws Exception {
        List<FacilioModule> readings = fetchModuleReadings(categoryId);
        return readings.stream().map(r -> r.getFields()).flatMap(r -> r.stream()).collect(Collectors.toList());

    }

    @Override
    public List<FacilioModule> fetchModuleReadings(long categoryId) throws Exception {
        FacilioChain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
        FacilioContext context = getCategoryReadingChain.getContext();
        context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
        context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
        getCategoryReadingChain.execute();

        return (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
    }

    @Override
    public Map<Long, List<FacilioField>> fetchAllReadings() throws Exception {
        FacilioChain getCategoryReadingChain = ReadOnlyChainFactory.getAllAssetReadingsChain();
        FacilioContext context = getCategoryReadingChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, FacilioModule.ModuleType.READING.name());
        getCategoryReadingChain.execute();

        Map<Long, List<FacilioField>> fields = (Map<Long, List<FacilioField>>) context.get(FacilioConstants.ContextNames.READING_FIELDS);
        return fields;
    }

    @Override
    public FacilioModule addReadingModule(List<FacilioModule> modules, FacilioModule assetModule) throws Exception {

        FacilioChain chain = TransactionChainFactory.commonAddModuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        context.put(FacilioConstants.Module.SKIP_EXISTING_MODULE_WITH_SAME_NAME_CHECK, true);
        context.put(FacilioConstants.ContextNames.PARENT_MODULE, assetModule.getName());
        context.put(FacilioConstants.ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME, false);
        context.put(FacilioConstants.ContextNames.APPEND_MODULE_NAME,false);
        chain.execute();


        return  new FacilioModule();
    }

    @Override
    public FacilioModule addReadingModule(FacilioModule module, FacilioModule assetModule) throws Exception {
        FacilioModule newModule = new FacilioModule(module);
        List<FacilioField> defaultReadingFields = FieldFactory.getDefaultReadingFields(newModule);
        newModule.setFields(defaultReadingFields);

        FacilioChain chain = TransactionChainFactory.commonAddModuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(newModule));
        context.put(FacilioConstants.Module.SKIP_EXISTING_MODULE_WITH_SAME_NAME_CHECK, true);
        context.put(FacilioConstants.ContextNames.PARENT_MODULE, assetModule.getName());
        context.put(FacilioConstants.ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME, true);
        context.put(FacilioConstants.ContextNames.APPEND_MODULE_NAME,false);
        chain.execute();


        return newModule;
    }

    @Override
    public void updateFields(List<FacilioField> fields) throws Exception {

        clearColumnName(fields);

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSelectFieldFields());
        List<FacilioField> updateFields = Arrays.asList(fieldMap.get("name"), fieldMap.get("moduleId"), fieldMap.get("columnName"));

        for (FacilioField field : fields) {
            GenericUpdateRecordBuilder.BatchUpdateContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateContext();
            batchValue.addWhereValue("fieldId", field.getFieldId());

            batchValue.addUpdateValue("name", field.getName());
            batchValue.addUpdateValue("moduleId", field.getModuleId());
            batchValue.addUpdateValue("columnName", field.getColumnName());

            batchUpdateList.add(batchValue);
        }

        FacilioModule module = ModuleFactory.getFieldsModule();
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(updateFields)
                ;
        updateBuilder.batchUpdate(Collections.singletonList(fieldMap.get("fieldId")), batchUpdateList);


    }

    private void clearColumnName(List<FacilioField> fields) throws Exception {
        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSelectFieldFields());
        List<FacilioField> updateFields = Arrays.asList(fieldMap.get("columnName"));

        for (FacilioField field : fields) {
            GenericUpdateRecordBuilder.BatchUpdateContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateContext();
            batchValue.addWhereValue("fieldId", field.getFieldId());

            batchValue.addUpdateValue("columnName", null);

            batchUpdateList.add(batchValue);
        }

        FacilioModule module = ModuleFactory.getFieldsModule();
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(updateFields)
                ;

        updateBuilder.batchUpdate(Collections.singletonList(fieldMap.get("fieldId")), batchUpdateList);
    }
}
