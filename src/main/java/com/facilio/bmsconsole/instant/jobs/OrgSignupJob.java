package com.facilio.bmsconsole.instant.jobs;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.cache.LRUCache;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.Organization;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class OrgSignupJob implements Runnable {
	
	private static final Logger LOGGER = LogManager.getLogger(OrgSignupJob.class.getName());

	private long orgId;
	private long iamUserId;
	private boolean retry;

	public OrgSignupJob(long orgId, long iamUserId, boolean retry) {
		this.orgId = orgId;
		this.iamUserId = iamUserId;
		this.retry = retry;
	}

	@Override
	public void run() {
		try {
			LOGGER.info("Org Signup Job called. orgId: "+ orgId+" iamUserId: "+iamUserId);
			Account account = new Account(IAMOrgUtil.getOrg(orgId), new User(IAMUserUtil.getFacilioUser(orgId, iamUserId)));
			
			AccountUtil.setCurrentAccount(account);

			String currentOrgInitStatus = CommonCommandUtil.getOrgInfo(FacilioConstants.ContextNames.ORG_INITIALIZATION_STATUS, "");
			if (StringUtils.isEmpty(currentOrgInitStatus) || (retry && "failed".equalsIgnoreCase(currentOrgInitStatus))) {
				if (retry) {
					CommonCommandUtil.updateOrgInfo(FacilioConstants.ContextNames.ORG_INITIALIZATION_STATUS, "started");
				}
				else {
					CommonCommandUtil.insertOrgInfo(FacilioConstants.ContextNames.ORG_INITIALIZATION_STATUS, "started");
				}

				JSONObject signupInfo = new JSONObject();
				signupInfo.put("isFacilioAuth", true);

				FacilioContext signupContext = new FacilioContext();
				signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupInfo);
				signupContext.put(FacilioConstants.ContextNames.ORGID, orgId);

				FacilioChain c = TransactionChainFactory.getOrgSignupChain();
				c.execute(signupContext);

				CommonCommandUtil.updateOrgInfo(FacilioConstants.ContextNames.ORG_INITIALIZATION_STATUS, "completed");
			}
			else {
				LOGGER.info("Org Signup Job already started hence skipping this job execution. orgId: "+ orgId+" iamUserId: "+iamUserId+" orgInitStatus: "+currentOrgInitStatus);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error in org signup thread. orgId: "+orgId+" iamUserId: "+iamUserId, e);
			try {
				CommonCommandUtil.updateOrgInfo(FacilioConstants.ContextNames.ORG_INITIALIZATION_STATUS, "failed");
				LRUCache.getFeatureLicenseCache().remove(CacheUtil.ORG_KEY(orgId));
				LRUCache.getSuperAdminCache().remove(CacheUtil.ORG_KEY(orgId));
				IAMOrgUtil.rollBackSignedUpOrgv2(orgId, iamUserId);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		finally {
			try {
				AccountUtil.cleanCurrentAccount();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
