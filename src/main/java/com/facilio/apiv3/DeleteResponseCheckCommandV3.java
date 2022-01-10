package com.facilio.apiv3;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.VisitResponseContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
public class DeleteResponseCheckCommandV3 extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> responseIds = Constants.getRecordIds(context);
		for(Long rId:responseIds)
		{
			if(VisitorManagementAPI.checkBeforeVisitResponseDelete(rId))
			{
				throw new IllegalArgumentException("Can not Delete an Active Response");
			}
		}
		return false;
	}
	

}
