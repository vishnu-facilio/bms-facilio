package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddAnswerSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.QAndA.ANSWER));

        List<SupplementRecord> supplements = new ArrayList<>(2); // Change size when new field is added
        supplements.add((SupplementRecord) fieldMap.get("multiEnumAnswer"));
        supplements.add((SupplementRecord) fieldMap.get("multiFileAnswer"));

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
        return false;
    }
}
