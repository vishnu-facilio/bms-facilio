package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardListPropsContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.*;

public class GetDashboardListResponseCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        DashboardListPropsContext dashboard_list_prop = (DashboardListPropsContext) context.get("dashboard_list_prop");
        if(dashboard_list_prop != null)
        {
            setResponse(dashboard_list_prop);
        }
        return false;
    }

    private void setResponse(DashboardListPropsContext dashboard_list_prop) throws Exception
    {
        if(dashboard_list_prop.getDashboards() != null && dashboard_list_prop.getDashboards().size() > 0)
        {
            List<DashboardContext> dashboards = dashboard_list_prop.isOnlyPublished() ? dashboard_list_prop.getDashboards() : DashboardUtil.sortDashboardByOrder(dashboard_list_prop.getDashboards());
            for(DashboardFolderContext folder : dashboard_list_prop.getFolders()) {
                for(DashboardContext dashboard : dashboards) {
                    if(dashboard.getDashboardFolderId() == folder.getId()) {
                        folder.addDashboard(dashboard);
                    }
                }
            }
            if(dashboard_list_prop.isOnlyPublished())
            {
                /**P
                    code is added for sorting the folders base on dashboard for published dashboard for portals
                 */
                List<Long> folder_ids = new ArrayList<>();
                List<DashboardFolderContext> folders = new ArrayList<>();
                for(DashboardContext dashboard : dashboards)
                {
                    for(DashboardFolderContext folder : dashboard_list_prop.getFolders())
                    {
                        if (dashboard.getDashboardFolderId() == folder.getId()) {
                            if (folder_ids != null && !folder_ids.contains(dashboard.getDashboardFolderId())) {
                                folders.add(folder);
                                folder_ids.add(dashboard.getDashboardFolderId());
                            }
                        }
                    }
                }
                if(!folders.isEmpty()){
                    dashboard_list_prop.setFolders(folders);
                }
            }
        }
        if(!dashboard_list_prop.isWithEmptyFolders() && dashboard_list_prop.getFolders() != null && dashboard_list_prop.getFolders().size() > 0)
        {
            dashboard_list_prop.getFolders().removeIf(folder -> (folder.getDashboards() == null || folder.getDashboards().size() == 0));
        }
        if(dashboard_list_prop.getFolders() != null && dashboard_list_prop.getFolders().size() > 1){
            dashboard_list_prop.setFolders(DashboardUtil.sortDashboardFolderByOrder(dashboard_list_prop.getFolders()));
        }

    }
}
