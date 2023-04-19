package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.signup.Point.AddBacnetIpPointModule;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.StringField;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class ExportPointsCommandV2 extends FacilioCommand {

    Map<Long, Map<String, Object>> fields = new HashMap<>();
    List<Long>controllerIds = new ArrayList<>();
    String filters = null;
    Criteria criteria = new Criteria();
    Integer viewLimit = null;

    @Override
    public boolean executeCommand(Context context) throws Exception {

        FileInfo.FileFormat fileFormat = (FileInfo.FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
        String viewName = (String) context.get(FacilioConstants.ContextNames.VIEW_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if (context.containsKey(FacilioConstants.ContextNames.VIEW_LIMIT)) {
            viewLimit = (int) context.get(FacilioConstants.ContextNames.VIEW_LIMIT);
        }
        if ( context.containsKey(AgentConstants.AGENT_ID)){
            long agentId = (long) context.get(AgentConstants.AGENT_ID);
            Condition condition = CriteriaAPI.getCondition("AGENT_ID",AgentConstants.AGENT_ID,String.valueOf(agentId),NumberOperators.EQUALS);
            criteria.addAndCondition(condition);
        }
        if(context.containsKey(AgentConstants.CONTROLLERIDS)){
            controllerIds = (List<Long>) context.get(AgentConstants.CONTROLLERIDS);
            Condition condition = CriteriaAPI.getCondition("Point.CONTROLLER_ID",AgentConstants.CONTROLLER_ID, StringUtils.join(controllerIds, ","), NumberOperators.EQUALS);
            criteria.addAndCondition(condition);
        }
        if(context.containsKey(FacilioConstants.ContextNames.FILTERS)) {
            filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
        }

        List<ViewField> viewFields = new ArrayList<ViewField>();
        List<ModuleBaseWithCustomFields> records = new ArrayList<ModuleBaseWithCustomFields>();
        prepareExportModuleConfig(moduleName, viewName, filters, criteria,  viewLimit, viewFields, records);

        Set<Long> fieldIds = new HashSet<>();
        List<Long> pointIds = new ArrayList<>();

        for (ModuleBaseWithCustomFields record:records){
            Map<String,Object>data = record.getData();
            if (data.containsKey(AgentConstants.FIELD_ID)) {
                long fieldId = (long) data.get(AgentConstants.FIELD_ID);
                fieldIds.add(fieldId);
            }
            if (data.containsKey(AgentConstants.UNIT)){
                int unitId = (int) data.get(AgentConstants.UNIT);
                if (unitId != 0) {
                    String unit = Unit.valueOf(unitId).getSymbol();
                    data.put(AgentConstants.UNIT,unit);
                }
            }
            if (record.getId() > 0){
                pointIds.add(record.getId());
            }
        }

        if (!fieldIds.isEmpty()) {
            addReading(fieldIds, records);
        }

        if (!pointIds.isEmpty()){
            addEnumStates(pointIds, records, viewFields,module);
        }

        String fileUrl = ExportUtil.exportData(fileFormat,module , viewFields, records, false);
        context.put(FacilioConstants.ContextNames.FILE_URL,fileUrl);
        return false;
    }

    private void addReading(Set<Long> fieldIds , List<ModuleBaseWithCustomFields>records) throws Exception {
        fields.putAll(CommissioningApi.getFields(fieldIds));
        for (ModuleBaseWithCustomFields record : records){
            Map<String,Object>point = record.getData();
            Map<String, Object> field = fields.get(point.get(AgentConstants.FIELD_ID));
            point.put(AgentConstants.FIELD_ID,field.get(AgentConstants.NAME));
        }

    }

    private void addEnumStates(List<Long> pointIds, List<ModuleBaseWithCustomFields> records, List<ViewField> viewFields,FacilioModule module) throws Exception {
        List<Map<String, Object>> readingInputValues = ReadingsAPI.getReadingInputValues(pointIds);
        Map<Long, String> map = new HashMap<>();

        for (Map<String,Object> readingInputValue: readingInputValues){
            if (!readingInputValue.containsKey(AgentConstants.INPUT_LABEL)) continue;

            long pointId = (long) readingInputValue.get(AgentConstants.POINT_ID);
            String label = (String) readingInputValue.get(AgentConstants.INPUT_LABEL);
            if (map.containsKey(pointId)){
                String s = map.get(pointId)  + "\n" +label;
                map.put(pointId, s);
            } else {
                map.put(pointId, String.valueOf(label));
            }
        }
        if(!map.isEmpty()){
            for (ModuleBaseWithCustomFields record : records) {
                record.getData().put("enumStates", map.get(record.getId()));
            }
            addCustomViewField(viewFields,"enumStates","Enum states",module,FieldType.STRING);
        }
    }

    private static void addCustomViewField(List<ViewField> viewFields, String name, String displayName,FacilioModule module, FieldType fieldType) throws Exception {
        ViewField viewField = new ViewField(name,displayName);
        if (fieldType.equals(FieldType.STRING)){
            StringField field = (StringField) FieldFactory.getStringField(name,displayName,module);
            viewField.setField(field);
        }
        viewFields.add(viewField);
    }

    public static void prepareExportModuleConfig(String moduleName, String viewName, String filters, Criteria criteria,Integer viewLimit, List<ViewField> viewFields, List<ModuleBaseWithCustomFields> records) throws Exception {

        FacilioChain listChain = ChainUtil.getListChain(moduleName);
        FacilioContext context = listChain.getContext();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
        context.put(Constants.WITHOUT_CUSTOMBUTTONS, true);

        int limit = 5000;

        if (viewLimit != null) {
            limit = viewLimit;
        }

        JSONObject pagination = new JSONObject();
        pagination.put("page", 1);
        pagination.put("perPage", limit);
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);

        if (filters != null) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
            context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
        }
        if (criteria != null) {
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
        }

        context.put("checkPermission", true);
        listChain.execute();

        records.addAll(Constants.getRecordListFromContext(context, moduleName));
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        viewFields.addAll(view.getFields());

        if(CollectionUtils.isNotEmpty(viewFields)){
            viewFields.removeIf(viewField -> viewField.getField()!=null &&!viewField.getField().isExportable());
        }
    }
}
