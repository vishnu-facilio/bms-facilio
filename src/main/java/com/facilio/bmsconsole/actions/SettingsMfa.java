package com.facilio.bmsconsole.actions;

import java.util.*;

import dev.samstevens.totp.time.NtpTimeProvider;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;


public class SettingsMfa extends FacilioAction {
	
	private static final Logger LOGGER = Logger.getLogger(SettingsMfa.class.getName());
	
	private boolean totpStatus;
	
	private String verificationCode;

	public boolean isTotpStatus() {
		return totpStatus;
	}

	public void setTotpStatus(boolean totpStatus) {
		this.totpStatus = totpStatus;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getMfa() throws Exception{
	
		Map<String,Boolean> orgMfaSettings = IAMOrgUtil.getMfaSettings(AccountUtil.getCurrentOrg().getOrgId());
		setResult("mfaSettings",orgMfaSettings);
		return "success" ;	
	}
	
	public String totpEnabled() throws Exception{
		
		IAMOrgUtil.enableTotp(AccountUtil.getCurrentOrg().getOrgId());
		return "success";
	}
    
	public String totpDisabled() throws Exception{
	
		IAMOrgUtil.disableTotp(AccountUtil.getCurrentOrg().getOrgId());
		return "success";
	}

	
	private static String generateKey() {
	    SecretGenerator secretGenerator = new DefaultSecretGenerator();
	    String secret = secretGenerator.generate();
	    
	    return secret;
	}
	
	public String totpVerification() throws Exception{


		String totpKey;
		Map<String,Object> values = new HashMap<>();
		values = IAMUserUtil.getUserMfaSettings(AccountUtil.getCurrentUser().getIamOrgUserId());

		if(values.get("totpSecret") == null) {
			totpKey = generateKey();
		    IAMUserUtil.updateUserMfaSettingsSecretKey(AccountUtil.getCurrentUser().getIamOrgUserId(), totpKey);
		} else {
		totpKey = values.get("totpSecret").toString();
		}
	    HashMap<String,String> data = new HashMap<>();
		data.put("secret",totpKey);
		data.put("qrCode",QrCode());
		setResult("totpData",data);
		return "success";
	}
	
	private String QrCode() throws Exception{

		String totpKey;
		Map<String,Object> values = new HashMap<>();
		values = IAMUserUtil.getUserMfaSettings(AccountUtil.getCurrentUser().getIamOrgUserId());
		totpKey = (String) values.get("totpSecret");

		if(totpKey != null) {
			QrData data = new QrData.Builder()
					.label(AccountUtil.getCurrentUser().getEmail())
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
	
	private boolean totpChecking(String code) throws Exception{

		String totpKey;
		Map<String,Object> values = new HashMap<>();
		values = IAMUserUtil.getUserMfaSettings(AccountUtil.getCurrentUser().getIamOrgUserId());
		totpKey = (String) values.get("totpSecret");

		LOGGER.error(totpKey);
		TimeProvider timeProvider = new NtpTimeProvider("pool.ntp.org");
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
		
		boolean successful = verifier.isValidCode(totpKey,code);
		
		return successful;
	}

	public String totpsetup() throws Exception{

		LOGGER.error(verificationCode);
		LOGGER.error(totpChecking(verificationCode));
		return "success";
	}
}