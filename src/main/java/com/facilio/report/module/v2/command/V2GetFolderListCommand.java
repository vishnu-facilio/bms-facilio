package com.facilio.report.module.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsReportResponseContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFolderContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class V2GetFolderListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        String searchText = (String) context.get("searchText");
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        JSONArray folders = null;
        if(searchText != null && !"".equals(searchText))
        {
            List<ReportContext> reports =  getFieldsForReportList(searchText,appId);
            folders = getFoldersList(reports, searchText, appId);

        }
        else
        {
            folders = getFoldersList(null, null,appId);
        }
        context.put("folders", folders);
        return false;
    }

    private JSONArray getFoldersList(List<ReportContext> reports, String searchText, Long appId)throws Exception
    {
        JSONArray folders_list = new JSONArray();
        JSONObject folder_obj = null;
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReportFolderModule().getTableName())
                .select(FieldFactory.getReport1FolderFields())
                .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null && props.size() > 0)
        {
           List<ReportFolderContext> folder_list =  FieldUtil.getAsBeanListFromMapList(props, ReportFolderContext.class);
            Map<Long, List<Map<String, Object>>> grouped_reports = null;
            if(reports != null)
            {
                grouped_reports = reports.stream().collect(Collectors.groupingBy(ReportContext::getReportFolderId,
                                Collectors.mapping( report -> { Map<String, Object> map = new HashMap<>();
                                    map.put("name", report.getName());
                                    map.put("id", report.getId());
                                    return map;
                                }, Collectors.toList())
                        ));
            }
            for(ReportFolderContext folder : folder_list)
            {
                folder_obj = new JSONObject();
                folder_obj.put("name", folder.getName());
                folder_obj.put("id", folder.getId());
                if(searchText == null)
                {
                    folders_list.add(folder_obj);
                }
                else
                {
                    if (searchText != null && !"".equals(searchText) && grouped_reports != null && grouped_reports.containsKey(folder.getId())) {
                        folder_obj.put("reports", grouped_reports.get(folder.getId()));
                        folders_list.add(folder_obj);
                    } else if (searchText != null && !"".equals(searchText) && folder.getName().contains(searchText)) {
                        folders_list.add(folder_obj);
                    }
                }
            }
        }
        return folders_list;
    }

    private List<ReportContext> getFieldsForReportList(String searchText, Long appId)throws Exception
    {
        JSONObject folder_obj = null;
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReportModule().getTableName())
                .select(V2AnalyticsOldUtil.getFieldsForReportList())
                .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));

        if(searchText != null){
            selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", searchText, StringOperators.CONTAINS));
        }
        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null && props.size() > 0)
        {
            return  FieldUtil.getAsBeanListFromMapList(props, ReportContext.class);
        }
        return null;
    }
}
