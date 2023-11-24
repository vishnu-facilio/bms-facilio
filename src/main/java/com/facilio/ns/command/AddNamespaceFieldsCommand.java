package com.facilio.ns.command;

import com.facilio.command.FacilioCommand;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

@Log4j
public class AddNamespaceFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long nsId = (long) context.get(NamespaceConstants.NAMESPACE_ID);
        List<NameSpaceField> fields = (List<NameSpaceField>) context.get(NamespaceConstants.NAMESPACE_FIELDS);
        if (CollectionUtils.isEmpty(fields)) {
            throw new Exception("Fields cannot be empty!");
        }

        fields.forEach(field -> {
            if (field.getDefaultExecutionMode() == null) {
                field.setDefaultExecutionMode(NameSpaceField.DefaultExecutionMode.SKIP);
            }
        });
        Constants.getNsBean().addNamespaceFields(nsId, fields);
        LOGGER.info("id: " + nsId + ", fields : " + fields);
        return false;
    }
}
