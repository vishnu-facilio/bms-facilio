package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GetGeoLocationFiledsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("DISPLAY_TYPE","displayTypeInt", String.valueOf(FacilioField.FieldDisplayType.GEO_LOCATION.getIntValForDB()), PickListOperators.IS));

        List<FacilioField> fields = modBean.getAllFields(moduleName,null,null,criteria);

        context.put(FacilioConstants.ContextNames.GEOLOCATION_FIELDS,fields);


        return false;
    }
}
