package com.facilio.bmsconsole.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PublicFileContext;
import com.facilio.services.filestore.PublicFileUtil;
import com.twilio.sdk.resource.api.v2010.account.Call;
import com.twilio.sdk.type.PhoneNumber;
import com.twilio.twiml.Say;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;

public class CallUtil extends TwilioUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(CallUtil.class.getName());

	public static String makeCall(JSONObject obj) throws Exception {

		String to = (String) obj.get("to");
		String message = (String) obj.get("message");

		Say say = new Say.Builder(message).build();
		VoiceResponse response = new VoiceResponse.Builder().say(say).build();

		String twilioML = response.toXml();
		
		PublicFileContext publicFileContext = PublicFileUtil.createPublicFile(twilioML, "twilioML", "xml", "text/xml");
		
		String url = publicFileContext.getPublicUrl();
		
		LOGGER.error("url --- "+url);

		//url = url.replace("http://localhost:8080/", "https://8eb69070.ngrok.io/"); //for local testing
		
		Call call = Call.create(ACCOUNTS_ID, new PhoneNumber(to), new PhoneNumber("+16106248741"), new URI(url))
				.execute();

		LOGGER.info("Called successfully. ID : " + call.getSid());
		return call.getSid();

	}
}
