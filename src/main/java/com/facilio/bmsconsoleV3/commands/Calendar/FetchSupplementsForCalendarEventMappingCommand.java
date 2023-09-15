package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchSupplementsForCalendarEventMappingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> calendarEventMappingModule = modBean.getAllFields(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(calendarEventMappingModule);

        List<LookupField> additionaLookups = new ArrayList<>();
        additionaLookups.add((LookupField) fieldsAsMap.get("event"));

        if(CollectionUtils.isNotEmpty(additionaLookups)) {
            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, additionaLookups);
        }
        return false;
    }
}
