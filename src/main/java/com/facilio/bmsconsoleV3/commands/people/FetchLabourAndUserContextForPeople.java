package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class FetchLabourAndUserContextForPeople extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<V3PeopleContext> peoples = Constants.getRecordList((FacilioContext) context);

		if(CollectionUtils.isNotEmpty(peoples)){

			for (V3PeopleContext people : peoples) {
				if (people.isLabour()) {
					people.setLabourContext(fetchLabour(people.getId()));
				}

				List<User> user = V3PeopleAPI.fetchUserContext(Collections.singletonList(people.getId()), false);
				if (CollectionUtils.isNotEmpty(user)) {
					people.setUserContext(user.get(0));
				}
			}
		}

		return false;
	}

	private LabourContextV3 fetchLabour(long id) throws Exception{

		ModuleBean bean = Constants.getModBean();
		FacilioModule module = bean.getModule(FacilioConstants.ContextNames.LABOUR);

		SelectRecordsBuilder<LabourContextV3> builder = new SelectRecordsBuilder<LabourContextV3>()
																.moduleName(module.getName())
																.select(bean.getAllFields(module.getName()))
																.beanClass(LabourContextV3.class)
																.andCondition(CriteriaAPI.getCondition("PEOPLE_ID","people",id+"", NumberOperators.EQUALS));
		List<LabourContextV3> labours = builder.get();
		return CollectionUtils.isNotEmpty(labours) ? labours.get(0) : null;
	}
}
