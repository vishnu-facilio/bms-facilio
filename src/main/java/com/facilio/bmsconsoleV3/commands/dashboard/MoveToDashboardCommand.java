package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.xpath.operations.Bool;

import java.util.Map;

public class MoveToDashboardCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        String dashboard_link = (String) context.get("dashboard_link");
        Long folder_id = (Long) context.get("folder_id");
        if(dashboard_link != null && folder_id != null && folder_id > 0)
        {
            Boolean isMoved = moveDashboardToApp(dashboard_link, folder_id);
            context.put("isMoved", isMoved);
        }

        return false;
    }

    private Boolean moveDashboardToApp(String dashboard_link, Long folder_id)throws Exception
    {
        DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(dashboard_link, null);
        if(dashboard != null)
        {
            dashboard.setDashboardFolderId(folder_id);
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getDashboardModule().getTableName())
                    .fields(FieldFactory.getDashboardFields())
                    .andCustomWhere("ID = ?", dashboard.getId());
            Map<String, Object> props = FieldUtil.getAsProperties(dashboard, true);

            return updateBuilder.update(props) > 0 ? Boolean.TRUE : Boolean.FALSE ;
        }
        return false;
    }
}
