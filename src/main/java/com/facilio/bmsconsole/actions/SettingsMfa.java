package com.facilio.bmsconsole.actions;

import java.util.*;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMUser;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;


public class SettingsMfa extends FacilioAction {
	
	private static final Logger LOGGER = Logger.getLogger(SettingsMfa.class.getName());
	
	private String verificationCode;

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	public String totpEnabled() throws Exception{
		
		IAMOrgUtil.enableTotp(AccountUtil.getCurrentOrg().getOrgId(), AppDomain.GroupType.FACILIO);
		return "success";
	}
    
	public String totpDisabled() throws Exception{
	
		IAMOrgUtil.disableTotp(AccountUtil.getCurrentOrg().getOrgId(), AppDomain.GroupType.FACILIO);
		return "success";
	}

	
	private String generateKey() {
	    SecretGenerator secretGenerator = new DefaultSecretGenerator();
	    String secret = secretGenerator.generate();
	    
	    return secret;
	}
	
	public String totpVerification() throws Exception{
		generateMfaData(AccountUtil.getCurrentUser().getUid());
		return "success";
	}

	private String totpSecret;
	private String qrCode;

	public String getQrCode() {
		return this.qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public void generateMfaData(long uid) throws Exception {
		String totpKey = generateKey();
		IAMUserUtil.updateUserMfaSettingsSecretKey(uid, totpKey);

		this.totpSecret = totpKey;
		this.qrCode = QrCode(uid);

		HashMap<String,String> data = new HashMap<>();
		data.put("secret",totpKey);
		data.put("qrCode",this.qrCode);
		setResult("totpData",data);
	}

	private String QrCode(long userId) throws Exception{

		String totpKey;
		Map<String,Object> values = new HashMap<>();
		values = IAMUserUtil.getUserMfaSettings(userId);
		IAMUser facilioUser = IAMUserUtil.getFacilioUser(-1, userId);
		totpKey = (String) values.get("totpSecret");

		if(totpKey != null) {
			QrData data = new QrData.Builder()
					.label(facilioUser.getEmail())
					.secret(totpKey)
					.issuer("Facilio")
					.algorithm(HashingAlgorithm.SHA1)
					.digits(6)
					.period(30)
					.build();

			QrGenerator generator = new ZxingPngQrGenerator();
			byte[] imageData = generator.generate(data);
			String mimeType = generator.getImageMimeType();
			String dataUri = getDataUriForImage(imageData, mimeType);

			return dataUri;
		}
		return "error";
	}

	public String totpsetup() throws Exception{

		if(IAMUserUtil.totpChecking(verificationCode, AccountUtil.getCurrentUser().getUid())){
			IAMUserUtil.updateUserMfaSettingsStatus(AccountUtil.getCurrentUser().getUid(),true);
		    setResponseCode(0);
		}
		else{
		    throw new IllegalArgumentException("invalid verification code");
		}
		return "success";
	}

	public String totpstatus() throws Exception{

		Map<String,Object> values = new HashMap<>();
		values = IAMUserUtil.getUserMfaSettings(AccountUtil.getCurrentUser().getUid());
		if(values == null || values.isEmpty()){
			LOGGER.info("values is empty");
			LOGGER.info("USERID is "+ AccountUtil.getCurrentUser().getIamOrgUserId());
		}
		boolean totpstatus = (boolean) values.get("totpStatus");
		setResult("totpstatus",totpstatus);
		return "success";

	}

	public String totpexit() throws Exception{

		IAMUserUtil.clearUserMfaSettings(AccountUtil.getCurrentUser().getUid());

		return "success";

	}

	public String totpchange() throws Exception{

		IAMUserUtil.clearUserMfaSettings(AccountUtil.getCurrentUser().getUid());

		String totpKey;
		totpKey = generateKey();
		IAMUserUtil.updateUserMfaSettingsSecretKey(AccountUtil.getCurrentUser().getUid(),totpKey);

		HashMap<String,String> data = new HashMap<>();
		data.put("secret",totpKey);
		data.put("qrCode",QrCode(AccountUtil.getCurrentUser().getUid()));
		setResult("totpData",data);
		return "success";
	}

	public String getTotpSecret() {
		return totpSecret;
	}

	public void setTotpSecret(String totpKey) {
		this.totpSecret = totpKey;
	}
}