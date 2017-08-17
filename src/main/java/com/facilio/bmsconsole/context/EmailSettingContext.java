package com.facilio.bmsconsole.context;

public class EmailSettingContext {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String bccEmail;
	public String getBccEmail() {
		return bccEmail;
	}
	public void setBccEmail(String bccEmail) {
		this.bccEmail = bccEmail;
	}
	
	private int flags = -1;
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	public boolean isUseAgentNameInReply() {
		return EmailFlags.USE_AGENT_NAME_IN_REPLY.isSet(flags);
	}
	public void setUseAgentNameInReply(boolean set) {
		flags = flags == -1?0:flags;
		if(set) {
			this.flags = EmailFlags.USE_AGENT_NAME_IN_REPLY.turnOn(flags);
		}
		else {
			this.flags = EmailFlags.USE_AGENT_NAME_IN_REPLY.turnOff(flags);
		}
	}
	
	public boolean isUseReplyToAddrForRequestCreation() {
		return EmailFlags.USE_REPLY_TO_ADDRESS_FOR_REQUESTOR_CREATION.isSet(flags);
	}
	public void setUseReplyToAddrForRequestCreation(boolean set) {
		flags = flags == -1?0:flags;
		if(set) {
			this.flags = EmailFlags.USE_REPLY_TO_ADDRESS_FOR_REQUESTOR_CREATION.turnOn(flags);
		}
		else {
			this.flags = EmailFlags.USE_REPLY_TO_ADDRESS_FOR_REQUESTOR_CREATION.turnOff(flags);
		}
	}
	
	public enum EmailFlags {
		USE_AGENT_NAME_IN_REPLY(1),
		USE_REPLY_TO_ADDRESS_FOR_REQUESTOR_CREATION(2);
		
		private int setting;
		private EmailFlags(int setting) {
			this.setting = setting;
		}
		
		public int getSetting() {
			return setting;
		}
		
		public boolean isSet(int setting) {
			return (setting & this.setting) == this.setting;
		}
		
		public int turnOn(int setting) {
			return setting | this.setting;
		}
		
		public int turnOff(int setting) {
			return setting & ~this.setting;
		}
		
		public int toggle(int setting) {
			return setting ^ this.setting;
		}
	}
}
