package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AccountsAction extends FacilioAction{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String url;
	private String permaLinkUrlToken;
	private String email;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPermaLinkUrlToken() {
		return permaLinkUrlToken;
	}

	public void setPermaLinkUrlToken(String url) {
		this.permaLinkUrlToken = url;
	}

	private String sessionString;
	public String getSessionString() {
		return sessionString;
	}
	public void setSessionString(String sessionString) {
		this.sessionString = sessionString;
	}

	public String getPermalinkToken() throws Exception {

		FacilioChain permaLinkTokenChain = FacilioChainFactory.getPermaLinkTokenChain();
		permaLinkTokenChain.getContext().put(FacilioConstants.ContextNames.PERMALINK_FOR_URL, getUrl());
		permaLinkTokenChain.getContext().put(FacilioConstants.ContextNames.USER_EMAIL, getEmail());

		if (StringUtils.isNotEmpty(sessionString)) {
			JSONParser parser = new JSONParser();
			JSONObject sessionObject = (JSONObject) parser.parse(sessionString);
			permaLinkTokenChain.getContext().put(FacilioConstants.ContextNames.SESSION, sessionObject);
		}
		permaLinkTokenChain.execute();

		setPermaLinkUrlToken((String)permaLinkTokenChain.getContext().get(FacilioConstants.ContextNames.PERMALINK_TOKEN_FOR_URL));

		return SUCCESS;
	}

	public String details() throws Exception {

		FacilioChain chain = FacilioChainFactory.getPermaLinkDetailsChain();
		String token = ServletActionContext.getRequest().getHeader("X-Permalink-Token");
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.TOKEN, token);

		chain.execute();

		setResult(FacilioConstants.ContextNames.SESSION, context.get(FacilioConstants.ContextNames.SESSION));
		return SUCCESS;
	}

}
