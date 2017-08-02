package com.facilio.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.fw.UserInfo;

public class HasPermission extends BodyTagSupport {

	private String permission;

	public int doAfterBody() throws JspException {
		try {
			RoleContext role = UserInfo.getCurrentUser().getRole(); 

			if (role != null && role.hasPermission(getPermission())) {
				
				BodyContent bodycontent = getBodyContent();
				String body = bodycontent.getString();
				JspWriter out = bodycontent.getEnclosingWriter();
				if (body != null) {
					out.print(body);
				}
			}
		} catch(Exception e) {
			throw new JspException("Error: "+e.getMessage(), e);
		}
		return SKIP_BODY;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}