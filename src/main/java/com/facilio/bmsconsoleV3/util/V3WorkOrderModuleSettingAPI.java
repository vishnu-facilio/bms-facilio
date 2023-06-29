package com.facilio.bmsconsoleV3.util;

import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderModuleSettingContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.Map;

@Log4j
public class V3WorkOrderModuleSettingAPI {

    public static final String HIDE_GALLERY = "hideGallery";
    public static final String AUTO_RESOLVE_STATE_ID = "autoResolveStateId";

    public static void addOrUpdateSetting(V3WorkOrderModuleSettingContext workOrderModuleSettingContext) throws Exception {

        if (workOrderModuleSettingContext.getId() > 0L ){
            update(workOrderModuleSettingContext);
        }else {
            add(workOrderModuleSettingContext);
        }
    }

    private static void add(V3WorkOrderModuleSettingContext workOrderModuleSettingContext) throws Exception {
        Map<String,Object> props = FieldUtil.getAsProperties(workOrderModuleSettingContext,true);

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getWorkorderModuleSettingFields(ModuleFactory.getWoSettingModule()))
                .table(ModuleFactory.getWoSettingModule().getTableName());
        builder.insert(props);
    }

    public static Map<String, Object> fetchWorkOrderModuleSettings() throws Exception {

        JSONObject workorderModuleSetting = new JSONObject();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getWorkorderModuleSettingFields(ModuleFactory.getWoSettingModule()))
                .table(ModuleFactory.getWoSettingModule().getTableName());

        return builder.fetchFirst();

    }

    public static V3WorkOrderModuleSettingContext fetchWorkOrderModuleSettingsAsObject() throws Exception {
        Map<String, Object> workOrderModuleSetting =  fetchWorkOrderModuleSettings();
        if(workOrderModuleSetting == null){
            return null;
        }
        return FieldUtil.getAsBeanFromMap(workOrderModuleSetting, V3WorkOrderModuleSettingContext.class);
    }

    /**
     * Setting the following values to true so that null value will get updated in Db, as it's required to reset the setting(auto resolve state column value).
     * - includeDefaultValues to true,
     * - ignoreSplNullHandling to true by calling ignoreSplNullHandling() on GenericUpdateRecordBuilder,
     *
     * @param workOrderModuleSettingContext
     * @throws Exception
     */
    private static void update(V3WorkOrderModuleSettingContext workOrderModuleSettingContext) throws Exception {

        Map<String,Object> props = FieldUtil.getAsProperties(workOrderModuleSettingContext,true);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getWorkorderModuleSettingFields(ModuleFactory.getWoSettingModule()))
                .table(ModuleFactory.getWoSettingModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(workOrderModuleSettingContext.getId(), ModuleFactory.getWoSettingModule()))
                .ignoreSplNullHandling();
        int updated = builder.update(props);
        LOGGER.info("Updated WorkOrder_Module_Setting: " + updated);
    }
}
