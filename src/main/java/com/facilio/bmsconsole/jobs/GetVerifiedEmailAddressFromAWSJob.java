package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.mssql.SelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.ResponseContext;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class GetVerifiedEmailAddressFromAWSJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> emailFromAddressField = modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(emailFromAddressField);
		
		SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
				.beanClass(EmailFromAddress.class)
				.select(emailFromAddressField)
				.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("verificationStatus"), Boolean.FALSE.toString(), BooleanOperators.IS));
		
		List<EmailFromAddress> unverifiedEmailFromAddress = select.get();
		
		if(unverifiedEmailFromAddress != null) {
			
			Map<String, List<EmailFromAddress>> emailMap = unverifiedEmailFromAddress.stream().collect(Collectors.groupingBy(EmailFromAddress::getEmail));
			
			Map<String, Boolean> result = AwsUtil.getVerificationMailStatus(new ArrayList<String>(emailMap.keySet()));
			
			for(String email : result.keySet()) {
				if(result.get(email)) {
					EmailFromAddress formAddressContext = emailMap.get(email).get(0);
					
					formAddressContext.setVerificationStatus(Boolean.TRUE);
					
					V3RecordAPI.updateRecord(formAddressContext, modBean.getModule(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME), emailFromAddressField);
				}
			}
		}
		
	}

}
