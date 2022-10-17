package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.regex.Pattern;

import com.facilio.command.FacilioCommand;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;

public class PeopleValidationCommand extends FacilioCommand {
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PeopleContext> peopleList = (List<PeopleContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(peopleList)) {
			for (PeopleContext people : peopleList) {
				if(StringUtils.isNotEmpty(people.getEmail())){
					String trimmedEmail = people.getEmail().trim();
					people.setEmail(trimmedEmail);
					if(!VALID_EMAIL_ADDRESS_REGEX.matcher(people.getEmail()).find()){
						throw new RESTException(ErrorCode.VALIDATION_ERROR, "Not a valid email - "+ people.getEmail());
					}
					if(PeopleAPI.checkForDuplicatePeople(people)) {
						throw new RESTException(ErrorCode.VALIDATION_ERROR, "People with the same email id already exists");
					}}
				//setting tenant's site to all the contacts
				if(people instanceof TenantContactContext && ((TenantContactContext)people).getTenant() != null && ((TenantContactContext)people).getTenant().getId() > 0) {
					TenantContext tenant = (TenantContext)RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT, ((TenantContactContext)people).getTenant().getId());
					people.setSiteId(tenant.getSiteId());
				}
			}
		}
		return false;
	}
}