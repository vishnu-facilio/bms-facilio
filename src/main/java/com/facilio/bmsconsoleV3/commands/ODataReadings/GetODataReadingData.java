package com.facilio.bmsconsoleV3.commands.ODataReadings;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.util.ODataReadingsUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

import static com.facilio.odataservice.util.ODataReadingsUtil.*;

public class GetODataReadingData extends FacilioCommand {
    Boolean isModified;
    private static final Logger LOGGER = LogManager.getLogger(GetODataReadingData.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ODataReadingsContext readingsContext = (ODataReadingsContext) context.get(FacilioConstants.ContextNames.ODATA_READING_VIEW);
        Map<String, List<Long>> moduleAndFields = (Map<String, List<Long>>) context.get(FacilioConstants.ContextNames.MODULE_AND_FIELDS);
        getFilters(context,readingsContext);
        if(moduleAndFields.size() == 1){            //if only one module is available for the chosen readings
            LOGGER.info("getdata 1");
            for(Map.Entry<String,List<Long>> entry:moduleAndFields.entrySet()){
                String moduleName = entry.getKey();
                List<Long> fieldIds = new ArrayList<>();
                if (entry.getValue() != null)
                {
                    fieldIds = entry.getValue();
                }
                getData(moduleName,fieldIds,context);
                break;
            }
        }
        else if(moduleAndFields.size()>1){                                                 //if multiple modules available for the chosen readings like AHU
            List<FacilioModule> moduleList = new ArrayList<>();                           // -- ahu_readings_general and ahu_readings_exhaust_air
            LOGGER.info("getdata 2");
            for(Map.Entry<String,List<Long>> entry:moduleAndFields.entrySet()){
                String moduleName = entry.getKey();
                moduleList.add(Constants.getModBean().getModule(moduleName));
            }
            getData(moduleList,context);
        }
        return false;
    }

    private void getData(List<FacilioModule> moduleList, Context context) throws Exception {
        long dateOpId =(long) context.get("DateOperator");
        long aggrId = (long) context.get("Aggr");
        DateOperators dateOperator = Arrays.stream(DateOperators.values()).filter(dateOperators -> dateOperators.getOperatorId() == dateOpId).findFirst().get();
        BmsAggregateOperators.DateAggregateOperator aggregateOperator = null;
        if(aggrId != -2) {      //-2 is for high res data
            aggregateOperator = Arrays.stream(BmsAggregateOperators.DateAggregateOperator.values()).filter(dateOperators -> dateOperators.getValue() == aggrId).findFirst().get();
        }
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
        List<Map<String, Object>> totalProps = new ArrayList<>();
        List<Map<String, Object>> props ;
        List<Map<String, Object>> booleanFieldsData;
        if((Boolean)context.get("isDateValue")){
            long startTime = (long)context.get("startTime");
            long endTime = (long)context.get("endTime");
            for (FacilioModule module : moduleList) {
                LOGGER.info("specific data");
                booleanFieldsData = getSpecificTableData(module, context, Arrays.asList(startTime, endTime), dateOperator, aggregateOperator,selectRecordBuilder);
                if(booleanFieldsData!=null && !booleanFieldsData.isEmpty()){
                    totalProps.addAll(booleanFieldsData);
                }
                props = selectRecordBuilder.get();
                if(props!=null && !props.isEmpty()){
                    totalProps.addAll(props);
                }
                selectRecordBuilder = new GenericSelectRecordBuilder();
            }
        }else{
            for (FacilioModule module : moduleList) {
                LOGGER.info("specific data");
                booleanFieldsData = getSpecificTableData(module, context, null, dateOperator, aggregateOperator,selectRecordBuilder);
                if(booleanFieldsData!=null && !booleanFieldsData.isEmpty()){
                    totalProps.addAll(booleanFieldsData);
                }
                props = selectRecordBuilder.get();
                if(props!=null && !props.isEmpty()){
                    totalProps.addAll(props);
                }
                selectRecordBuilder = new GenericSelectRecordBuilder();
            }
        }
        if( !totalProps.isEmpty()) {
            if (aggregateOperator != null) {
                totalProps = getAdjustedTimeStammp(totalProps, aggregateOperator);
            }
            totalProps = getListAsTimeMap(totalProps, context);
        }
        context.put("result",totalProps);
    }

    private void getData(String moduleName, List<Long> fieldIds,Context context) throws Exception {
        long dateOpId = -1;
        if (context.get("DateOperator") != null) {
            dateOpId = (long) context.get("DateOperator");
        }
        long aggrId = -1;
        if(context.get("Aggr") != null){
            aggrId = (long) context.get("Aggr");
        }
        DateOperators dateOperator = null;
        if (dateOpId != -1) {
            long finalDateOpId = dateOpId;
            dateOperator = Arrays.stream(DateOperators.values()).filter(dateOperators -> dateOperators.getOperatorId() == finalDateOpId).findFirst().get();
        }
        BmsAggregateOperators.DateAggregateOperator aggregateOperator = null;
        if (aggrId!=-1 && aggrId != -2) {
            long finalAggrId = aggrId;
            aggregateOperator = Arrays.stream(BmsAggregateOperators.DateAggregateOperator.values()).filter(dateOperators -> dateOperators.getValue() == finalAggrId).findFirst().get();
        }
        List<Map<String, Object>> booleanFieldsData = new ArrayList<>();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
        isModified = false;
        if ((Boolean) context.get("isDateValue")) {
            long startTime = (long) context.get("startTime");
            long endTime = (long) context.get("endTime");
            booleanFieldsData = getSelectBuilder(moduleName, context, fieldIds, Arrays.asList(startTime, endTime), null, aggregateOperator, selectRecordBuilder);
        } else {
            booleanFieldsData = getSelectBuilder(moduleName, context, fieldIds, null, dateOperator, aggregateOperator, selectRecordBuilder);
        }
        List<Map<String, Object>> props = new ArrayList<>();
        if (isModified) {
            Criteria criteria = getCriteria(fieldIds, new ArrayList<>());
            if (criteria != null && !criteria.isEmpty()) {
                selectRecordBuilder.andCriteria(criteria);
            }

            int odataReadingRecordsLimit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.ODATA_READING_RECORDS_LIMIT, 20000));
            selectRecordBuilder.limit(odataReadingRecordsLimit);
            props = selectRecordBuilder.get();
        }
        if (aggregateOperator != null && !booleanFieldsData.isEmpty()) {
            props.addAll(booleanFieldsData);
        }
        if (!props.isEmpty()) {
            if(aggrId != -2) {
                props = getAdjustedTimeStammp(props, aggregateOperator);
            }
            props = getListAsTimeMap(props, context);
        }
        context.put("result",props);
    }
    public List<Map<String, Object>> getSelectBuilder(String moduleName, Context context , List<Long> fieldIds, List<Long> range, DateOperators dateOp, BmsAggregateOperators.DateAggregateOperator aggr, GenericSelectRecordBuilder selectRecordBuilder) throws Exception{
        ModuleBean modBean  = (ModuleBean) BeanFactory.lookup("ModuleBean") ;
        if(moduleName.equalsIgnoreCase("weather")){
            moduleName = FacilioConstants.ContextNames.NEW_WEATHER_READING;
        }
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = new ArrayList<>();
        if(fieldIds != null && !fieldIds.isEmpty()){
            fields =  ODataReadingsUtil.getFields(fieldIds);
        }else{
            fields = ODataReadingsUtil.getModuleFields(module.getName());
        }
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        fieldMap.put("id",FieldFactory.getIdField(module));
        List<FacilioField> aggrFields = new ArrayList<>();
        ODataReadingsContext context1 = (ODataReadingsContext) context.get(FacilioConstants.ContextNames.ODATA_READING_VIEW);
        boolean isAggr = true;
        if(context1.getAggregateOperator()==-2){
            isAggr = false;
        }
        List<FacilioField> booleanFields = new ArrayList<>();
        String groupBy = "";
        if (isAggr) {
            groupBy = aggr.getSelectField(fieldMap.get("ttime")).getCompleteColumnName();
            groupBy = groupBy.concat(","+fieldMap.get("parentId").getCompleteColumnName());
            aggrFields.add(aggr.getTimestampField(fieldMap.get("ttime")));
            for (int i = 0; i < fields.size(); i++) {
                if(FieldType.BOOLEAN == fields.get(i).getDataTypeEnum()) {
                    booleanFields.add(fields.get(i));
                }else {
                    FacilioField field = BmsAggregateOperators.NumberAggregateOperator.AVERAGE.getSelectField(fields.get(i));
                    String fieldName = "ROUND("+field.getCompleteColumnName()+",3)";
                    field.setColumnName(fieldName);
                    aggrFields.add(field);
                }
            }
            aggrFields.add(fieldMap.get("parentId"));
        }else {
            aggrFields.addAll(fields);
            aggrFields.add(fieldMap.get("ttime"));
            aggrFields.add(fieldMap.get("parentId"));
        }
        if(aggrFields.size()>2) {
            isModified = true;
            selectRecordBuilder
                    .table(module.getTableName())
                    .select(aggrFields);
            if (range == null) {
                selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dateOp));
            } else {
                selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(range.get(1)), NumberOperators.LESS_THAN))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(range.get(0)), NumberOperators.GREATER_THAN_EQUAL));
            }
            List<Long> parentId = new ArrayList<>();
            if (context.get(FacilioConstants.ContextNames.PARENT_ID) != null) {
                parentId = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID);
            }
            if (parentId != null && !parentId.isEmpty()) {
                selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), parentId, NumberOperators.EQUALS));
            }
            if (groupBy != null && !groupBy.isEmpty()) {
                selectRecordBuilder.groupBy(groupBy);
            }
            selectRecordBuilder.orderBy(module.getTableName() + ".ttime" + " ASC");
        }
        List<FacilioField> otherFields = new ArrayList<>();
        if(isAggr && !booleanFields.isEmpty()) {
            otherFields.add(aggr.getTimestampField(fieldMap.get("ttime")));
            otherFields.add(fieldMap.get("parentId"));
            return getBooleanFieldsData(booleanFields, module.getTableName(),groupBy,otherFields,dateOp,fieldMap,range, aggr,context);
        }else{
            return new ArrayList<>();
        }
    }
    private List<Map<String, Object>> getSpecificTableData(FacilioModule module, Context context, List<Long> range, DateOperators dateOperator, BmsAggregateOperators.DateAggregateOperator aggr, GenericSelectRecordBuilder selectRecordBuilder) throws Exception{
        ModuleBean modBean  = (ModuleBean) BeanFactory.lookup("ModuleBean") ;
        Map<String, List<Long>> moduleAndFields = (Map<String, List<Long>>) context.get(FacilioConstants.ContextNames.MODULE_AND_FIELDS);
        List<Long> fieldIds = new ArrayList<>();
        List<FacilioField> booleanFields = new ArrayList<>();
        if(moduleAndFields != null){
            fieldIds  = moduleAndFields.get(module.getName());
        }
        List<FacilioField> fields ;
        if(fieldIds != null && !fieldIds.isEmpty()){
            fields = ODataReadingsUtil.getFields(fieldIds);
        }else{
            fields = ODataReadingsUtil.getModuleFields(module.getName());
        }
        LOGGER.info("fields fetched succesfully");
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        fieldMap.put("id",FieldFactory.getIdField(module));
        ODataReadingsContext context1 = (ODataReadingsContext) context.get(FacilioConstants.ContextNames.ODATA_READING_VIEW);
        List<Long> parentId = new ArrayList<>();
        if(context.containsKey(FacilioConstants.ContextNames.PARENT_ID) && context.get(FacilioConstants.ContextNames.PARENT_ID) != null){
            parentId = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID);
        }
        Boolean isAggr = true;
        if(context1.getAggregateOperator()==-2){
            isAggr = false;
        }
        selectRecordBuilder
                .table(module.getTableName());
        List<FacilioField> aggrFields = new ArrayList<>();
        String groupBy = "";
        if (isAggr) {
            groupBy = aggr.getSelectField(fieldMap.get("ttime")).getCompleteColumnName();
            groupBy = groupBy.concat(","+fieldMap.get("parentId").getCompleteColumnName());
            aggrFields.add(aggr.getTimestampField(fieldMap.get("ttime")));
            for (int i = 0; i < fields.size(); i++) {
                if(FieldType.BOOLEAN == fields.get(i).getDataTypeEnum()){
                    booleanFields.add(fields.get(i));
                }else {
                    FacilioField field = BmsAggregateOperators.NumberAggregateOperator.AVERAGE.getSelectField(fields.get(i));
                    String fieldName = "ROUND("+field.getCompleteColumnName()+",3)";
                    field.setColumnName(fieldName);
                    aggrFields.add(field);
                }
            }
            aggrFields.add(fieldMap.get("parentId"));
        }else {
            aggrFields.addAll(fields);
            aggrFields.add(fieldMap.get("ttime"));
            aggrFields.add(fieldMap.get("parentId"));
        }

        selectRecordBuilder.select(aggrFields);
        if(range == null) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dateOperator));
        }else{
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"),String.valueOf(range.get(0)), NumberOperators.LESS_THAN))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"),String.valueOf(range.get(1)), NumberOperators.GREATER_THAN_EQUAL));
        }
        if (isAggr) {
            selectRecordBuilder.groupBy(groupBy);
        }

        if(parentId!= null && !parentId.isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"),parentId,NumberOperators.EQUALS));
        }
        Criteria criteria = getCriteria(fieldIds,new ArrayList<>());
        if(criteria!=null && !criteria.isEmpty()){
            selectRecordBuilder.andCriteria(criteria);
        }
        selectRecordBuilder.orderBy(module.getTableName()+".ttime"+" ASC");
        selectRecordBuilder.limit(20000);
        List<FacilioField> otherFields = new ArrayList<>();
        if(isAggr && !booleanFields.isEmpty()) {
            otherFields.add(aggr.getTimestampField(fieldMap.get("ttime")));
            otherFields.add(fieldMap.get("parentId"));
            return getBooleanFieldsData(booleanFields, module.getTableName(),groupBy,otherFields,dateOperator,fieldMap,range,aggr,context);
        }else{
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getBooleanFieldsData(List<FacilioField> booleanFields, String tableName, String groupBy, List<FacilioField> otherFields, DateOperators dateOperator, Map<String, FacilioField> fieldMap, List<Long> range, BmsAggregateOperators.DateAggregateOperator aggr,Context context) throws Exception{
        LOGGER.info("boolean fields");
        List<FacilioField> allFields = new ArrayList<>();
        Criteria criteria = getCriteria(new ArrayList<>(),booleanFields);
        allFields.addAll(booleanFields);
        for(int i=0;i< allFields.size();i++){
          FacilioField tfield = BmsAggregateOperators.NumberAggregateOperator.AVERAGE.getSelectField(allFields.get(i));
          String fieldName = "round("+tfield.getCompleteColumnName()+")";
          FieldType fieldType = FieldType.BOOLEAN;
          allFields.remove(i);
          tfield.setDataType(fieldType);
          tfield.setColumnName(fieldName);
          allFields.add(i,tfield);
        }
        allFields.addAll(otherFields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(tableName)
                .select(allFields);
        if(range == null) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dateOperator));
        }else{
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"),String.valueOf(range.get(0)), NumberOperators.LESS_THAN))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"),String.valueOf(range.get(1)), NumberOperators.GREATER_THAN_EQUAL));
        }
        if(criteria!=null && !criteria.isEmpty()) {
            selectRecordBuilder.andCriteria(criteria);
        } List<Long> parentId = null;
        if(context.containsKey(FacilioConstants.ContextNames.PARENT_ID) && context.get(FacilioConstants.ContextNames.PARENT_ID) != null){
            parentId = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID);
        }
        if(parentId!= null && !parentId.isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"),parentId,NumberOperators.EQUALS));
        }
        selectRecordBuilder.groupBy(groupBy);
        selectRecordBuilder.orderBy("ttime DESC");
        return selectRecordBuilder.get();
    }
}