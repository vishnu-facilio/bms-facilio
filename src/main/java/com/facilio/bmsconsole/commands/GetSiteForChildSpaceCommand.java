package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetSiteForChildSpaceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long childId = (long) context.get("childId");
        FacilioModule resourceModule = Constants.getModBean().getModule("resource");
        List<FacilioField> siteField = new ArrayList<>();
        siteField.add(FieldFactory.getIdField(resourceModule));
        siteField.add(FieldFactory.getSiteField(resourceModule));
        siteField.add(FieldFactory.getField("name","name",resourceModule, FieldType.STRING));
        FacilioModule baseSpaceModule = Constants.getModBean().getModule("basespace");
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(resourceModule.getTableName())
                .select(siteField)
                .andCustomWhere("id = (select site_id from "+baseSpaceModule.getTableName()+" where id = "+childId+")");
        Map<String,Object> prop = builder.fetchFirst();
        prop.put("unit",getAreaUnit());
        context.put("site",prop);
        return false;
    }

    private String getAreaUnit() throws Exception{
        FacilioField areaField = Constants.getModBean().getField("area", "basespace");
        String unitLabel = null;
        if(areaField instanceof NumberField) {
            NumberField numberField = (NumberField) areaField;
            unitLabel = numberField.getUnit();
        }
        if(unitLabel == null)
        {
            unitLabel = "sq.ft";
        }
        return unitLabel;
    }
}
