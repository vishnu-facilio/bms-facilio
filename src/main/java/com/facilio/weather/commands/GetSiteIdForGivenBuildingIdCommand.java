package com.facilio.weather.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class GetSiteIdForGivenBuildingIdCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long buildingId = (long) context.get("buildingId");

        V3Util.throwRestException(buildingId <= 0, ErrorCode.VALIDATION_ERROR, "invalid building id :: "+buildingId);

        String moduleName = FacilioConstants.ContextNames.BUILDING;
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
                .table(module.getTableName())
                .moduleName(moduleName)
                .beanClass(BuildingContext.class)
                .select(fields)
                .maxLevel(1)
                .andCustomWhere(module.getTableName()+".ID = ?", buildingId)
                .orderBy("ID");
        List<BuildingContext> buildings = builder.get();

        V3Util.throwRestException(CollectionUtils.isEmpty(buildings), ErrorCode.VALIDATION_ERROR, "no buildings found for building id :: "+buildingId);
        long siteId = buildings.get(0).getSiteId();
        context.put("siteId", siteId);
        return false;
    }
}
