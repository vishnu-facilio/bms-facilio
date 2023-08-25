package com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments;

import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidateContactDirectoryEmailCommand extends FacilioCommand {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+&-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ContactDirectoryContext> contacts = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(contacts)) {
            for(ContactDirectoryContext contact : contacts){
                if(StringUtils.isNotEmpty(contact.getContactEmail())){
                    String trimmedEmail = contact.getContactEmail().trim();
                    contact.setContactEmail(trimmedEmail);
                    if(!VALID_EMAIL_ADDRESS_REGEX.matcher(contact.getContactEmail()).find()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Not a valid email - "+ contact.getContactEmail());
                    }
                }
            }
        }
        return false;
    }
}
