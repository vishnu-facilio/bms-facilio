package com.facilio.odataservice.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.util.ODataReadingViewsUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.odataservice.util.ODataReadingViewsUtil.addODataReadingViewName;

@Getter @Setter
public class ODataReadingViewsAction extends FacilioAction {
    ODataReadingsContext readingView;
    String readingName;
    long assetCategoryId;
    Object data;
    String tableName = ModuleFactory.getODataReadingModule().getTableName();
    List<FacilioField> fields = FieldFactory.getODataReadingFields();
    Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
    Boolean status;
    int page;
    int perPage;
    String search;
    int count;
    private static final Logger LOGGER = LogManager.getLogger(ODataModuleAction.class.getName());


    public String addODataReadings() throws Exception {
        ODataReadingsContext context = addODataReadingViewName(readingView);
        ODataReadingViewsUtil.updateODataReadingsCriteriaId(context);
        Map<String, Object> prop = FieldUtil.getAsProperties(context);
        prop.put("isEnabled",true);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(tableName)
                .ignoreSplNullHandling()
                .fields(fields);
        insertBuilder.addRecord(prop);
        insertBuilder.save();
        return SUCCESS;
    }
    public String fetchODataReadings() throws Exception{
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(tableName)
                .select(fields);
        if(search!=null && !search.isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("displayName"),search,StringOperators.CONTAINS));
        }
        selectRecordBuilder.limit(perPage)
                            .offset(perPage * (page-1));

        List<Map<String, Object>> result = selectRecordBuilder.get();
        List<ODataReadingsContext> resobj = FieldUtil.getAsBeanListFromMapList(result, ODataReadingsContext.class);
        for(int i=0;i<resobj.size();i++){
            ODataReadingsContext context = resobj.get(i);
            context.setCriteria(CriteriaAPI.getCriteria(context.getCriteriaId()));
            context.setEnabled((Boolean)result.get(i).get("isEnabled"));
        }
        setData(resobj);
        return SUCCESS;
    }
    public String readingsCount() throws Exception{
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(tableName)
                .select(fields);
        if(search!=null && !search.isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("displayName"),search,StringOperators.CONTAINS));
        }
        setCount(selectRecordBuilder.get().size());
        return SUCCESS;
    }
    public String updateODataReadings() throws Exception {
        ODataReadingsContext context = readingView;
        context.setName(ODataReadingViewsUtil.getReadingViewName(context));
        ODataReadingViewsUtil.updateODataReadingsCriteriaId(context);
        Map<String, Object> prop = FieldUtil.getAsProperties(context);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(tableName)
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),readingName, StringOperators.IS));
        updateBuilder.update(prop);
        return SUCCESS;
    }
    public String changeStatus() throws Exception{
        ODataReadingsContext context = ODataReadingViewsUtil.getReadingView(readingName);
        context.setEnabled(status);
        Map<String, Object> prop = new HashMap<>();
        prop.put("isEnabled",status);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(tableName)
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),readingName, StringOperators.IS));
        updateBuilder.update(prop);

        return SUCCESS;
    }

    public String deleteODataReadings() throws Exception {
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(tableName)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),readingName, StringOperators.IS));
        int deleted = deleteRecordBuilder.delete();
        return SUCCESS;
    }

}
