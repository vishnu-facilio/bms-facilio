package com.facilio.bmsconsole.workflow;

import java.util.List;

import com.facilio.bmsconsole.criteria.Criteria;

public class WorkflowRuleContext {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long eventId = -1;
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	private WorkflowEventContext event;
	public WorkflowEventContext getEvent() {
		return event;
	}
	public void setEvent(WorkflowEventContext event) {
		this.event = event;
	}

	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private int executionOrder = -1;
	public int getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public boolean isActive() {
		if(status != null) {
			return status.booleanValue();
		}
		return false;
	}
	
	private List<ActionContext> actions;
	
	public List<ActionContext> getActions() {
		return actions;
	}
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}

	private RuleType ruleType;
	public int getRuleType() {
		if(ruleType != null) {
			return ruleType.getIntVal();
		}
		return -1;
	}
	public void setRuleType(int ruleType) {
		this.ruleType = RULE_TYPES[ruleType - 1];
	}
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	public RuleType getRuleTypeEnum() {
		return ruleType;
	}
	
	private static final RuleType[] RULE_TYPES = RuleType.values();
	public static enum RuleType {
		READING_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return false;
			}
		},
		WORKORDER_AGENT_NOTIFICATION_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return false;
			}
		},
		WORKORDER_REQUESTER_NOTIFICATION_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return false;
			}
		},
		ALARM_NOTIFICATION_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return false;
			}
		},
		SLA_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return true;
			}
		},
		ASSIGNMENT_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return true;
			}
		},
		PM_READING_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return false;
			}
		},
		READING_THRESHOLD_RULE {
			@Override
			public boolean stopFurtherRuleExecution() {
				// TODO Auto-generated method stub
				return false;
			}
		}
		;
		
		public int getIntVal() {
			return ordinal()+1;
		}
		
		public abstract boolean stopFurtherRuleExecution();
		
		public static RuleType valueOf(int val) {
			try {
				return RULE_TYPES[val - 1];
			}
			catch(ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
	}
}
