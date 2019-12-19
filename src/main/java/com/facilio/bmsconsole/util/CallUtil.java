package com.facilio.bmsconsole.util;

import java.net.URI;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PublicFileContext;
import com.facilio.services.filestore.PublicFileUtil;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;

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
		
		LOGGER.info("url --- "+url);

//		url = url.replace("http://localhost:8080/", "https://be92cea3.ngrok.io/"); //for local testing
		
		Call call = Call.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(FROM_NUMBER),
                new URI(url))
            .create();
		
		LOGGER.info("Called successfully. ID : " + call.getSid());
		log(CommonAPI.NotificationType.CALL, to, message, call.getSid());
		return call.getSid();

	}
}
