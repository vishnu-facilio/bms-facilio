package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFactoryFields;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.v3.annotation.Module;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class GetReportsAsOptionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long webtabId = (Long) context.get("webTabId");
        String searchText = (String) context.get("searchText");
        Boolean isPivot = (Boolean) context.get("isPivot");
        Long appId = (Long) context.get("appId");
        String moduleName = (String) context.get("moduleName");
        if (false && webtabId != null && webtabId > 0) {
            List<Long> moduleIds = getAllWebTabModules(webtabId, isPivot);
            if (moduleIds != null) {
                HashMap<Long, Long> moduleId_vs_id = new HashMap<>();
                for(Long moduelId : moduleIds){
                    moduleId_vs_id.put(moduelId, moduelId);
                }
               context.put("reports",getAllReportOptions(moduleId_vs_id, searchText, appId, isPivot));
            }
        }
        else if(moduleName != null){
            List<Long> folderIds = getFolders(moduleName, isPivot);
            context.put("reports", getAllReportOptionsFromFolderIds(folderIds, searchText));
        }
        return false;
    }

    private List<Long> getFolders(String moduleName, Boolean isPivot)throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(isPivot ? "energydata" : moduleName);

        List<Long> folders = new ArrayList<>();
        if(moduleName.contains("custom_"))
        {
            List<ReportFolderContext> reportFolders = ReportUtil.getAllCustomModuleReportFolder(false, null);
            if(reportFolders != null && reportFolders.size() > 0){
                for(ReportFolderContext folder : reportFolders){
                    folders.add(folder.getId());
                }
            }
        }
        else if(module != null)
        {
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getReport1FolderFields())
                    .table(ModuleFactory.getReportFolderModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", module.getModuleId()+"", NumberOperators.EQUALS));

            if(isPivot){
                selectBuilder.andCondition(CriteriaAPI.getCondition("FOLDER_TYPE", "folderType", String.valueOf(ReportFolderContext.FolderType.PIVOT.getValue()), NumberOperators.EQUALS));
            }else {
                selectBuilder.andCondition(CriteriaAPI.getCondition("FOLDER_TYPE", "folderType", String.valueOf(ReportFolderContext.FolderType.PIVOT.getValue()), NumberOperators.NOT_EQUALS));
            }
            List<Map<String, Object>> props = selectBuilder.get();
            if(props != null && props.size() > 0)
            {
                for(Map<String, Object> prop : props)
                {
                    ReportFolderContext folderContext = FieldUtil.getAsBeanFromMap(prop, ReportFolderContext.class);
                    if(folderContext != null){
                        folders.add(folderContext.getId());
                    }
                }
            }
        }
        return folders;
    }
    private List<Long> getAllWebTabModules(Long webtabId, Boolean isPivot) throws Exception
    {
        ModuleBean modBean = null;
        List<Long> moduleIds = new ArrayList<>();
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
        WebTabContext webTabContext = tabBean.getWebTab(webtabId);
        JSONObject config = webTabContext.getConfigJSON();
        if(config != null && config.containsKey("type") && config.get("type").equals("module_reports"))
        {
            List<Long> webtab_moduleIds = ApplicationApi.getModuleIdsForTab(webtabId);
            if (webtab_moduleIds != null && webtab_moduleIds.size() > 0)
            {
                modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (Long moduleId : webtab_moduleIds)
                {
                    FacilioModule module = modBean.getModule(moduleId);
                    if (module != null && !module.isCustom()) {
                        Set<FacilioModule> subModules = ReportFactoryFields.getSubModulesList(module.getName());
                        if (subModules != null) {
                            moduleIds.addAll(subModules.stream().map(m -> m.getModuleId()).collect(Collectors.toList()));
                        }
                    }
                    if (module != null) {
                        moduleIds.add(moduleId);
                    }
                }
                return moduleIds;
            }
        }
        else if((config != null && config.containsKey("type") && config.get("type").equals("analytics_reports")) || isPivot){
            modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule energyData = modBean.getModule("energydata");
            moduleIds.add(energyData.getModuleId());
        }
        return moduleIds;
    }
    private JSONArray getAllReportOptions(HashMap<Long, Long> moduleId_vs_id, String searchText, Long appId, Boolean isPivot)throws Exception
    {
        JSONArray report_list = new JSONArray();
        FacilioModule reportModule = ModuleFactory.getReportModule();
        List<FacilioField> fields = FieldFactory.getReport1Fields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selected_fields = listOfSelectedFields();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(selected_fields)
                .table(reportModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));

        if (searchText != null) {
            select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
        }
        if(isPivot){
            select.andCondition(CriteriaAPI.getCondition("REPORT_TYPE", "type", String.valueOf(ReportContext.ReportType.PIVOT_REPORT.getValue()), NumberOperators.EQUALS));
        }else{
            select.andCondition(CriteriaAPI.getCondition("REPORT_TYPE", "type", String.valueOf(ReportContext.ReportType.PIVOT_REPORT.getValue()), NumberOperators.NOT_EQUALS));
        }

        List<Map<String, Object>> props = select.get();
        if(props != null && !props.isEmpty())
        {
            JSONObject report_json = null;
            for(Map<String, Object> prop :props) {
                ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
                if(isPivot || moduleId_vs_id.containsKey(report.getModuleId())){
                    report_json = new JSONObject();
                    report_json.put("name", report.getName());
                    report_json.put("id", report.getId());
                    report_list.add(report_json);
                }
            }
        }
        return report_list;
    }

    private List<FacilioField> listOfSelectedFields()throws Exception
    {
        List<FacilioField> fields = FieldFactory.getReport1Fields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selected_fields = new ArrayList<>();
        selected_fields.add(fieldMap.get("id"));
        selected_fields.add(fieldMap.get("name"));
        selected_fields.add(fieldMap.get("moduleId"));
        return selected_fields;
    }

    private JSONArray getAllReportOptionsFromFolderIds(List<Long> folderIds, String searchText)throws Exception
    {
        JSONArray report_list = new JSONArray();
        List<FacilioField> fields = FieldFactory.getReport1Fields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selected_fields = listOfSelectedFields();
        selected_fields.add(fieldMap.get("reportFolderId"));
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selected_fields)
                .table(ModuleFactory.getReportModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));

        if (searchText != null) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
        }
        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null && props.size() > 0)
        {
            JSONObject report_json = null;
            for(Map<String, Object> prop : props)
            {
                ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
                if(report != null && folderIds.contains(report.getReportFolderId())){
                    report_json = new JSONObject();
                    report_json.put("name", report.getName());
                    report_json.put("id", report.getId());
                    report_list.add(report_json);
                }
            }
        }
        return report_list;
    }
}
