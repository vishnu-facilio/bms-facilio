package com.facilio.bmsconsole.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.SkillAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class SkillActions extends ActionSupport {
	
	public String execute() {
		
		try {
			setSkills(SkillAPI.getAllSkill(OrgInfo.getCurrentOrgInfo().getOrgid()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	private List formlayout;
	public List getFormlayout() {
		return this.formlayout;
	}
	
	public void setFormlayout(List formlayout) {
		this.formlayout = formlayout;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<FacilioField> fields;
	
	public String newSkill() throws Exception {
		
		Connection conn = null;
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", OrgInfo.getCurrentOrgInfo().getOrgid());
			fields = modBean.getAllFields(FacilioConstants.ContextNames.SKILL);
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			setModuleName("Skill");
			setFormlayout(FormLayout.getNewSkillLayout(fields));
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			if(conn != null) {
				try {
					conn.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	    return SUCCESS;
	}
	
	public String newSkillDialog() throws Exception {
		
		Connection conn = null;
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", OrgInfo.getCurrentOrgInfo().getOrgid());
			fields = modBean.getAllFields(FacilioConstants.ContextNames.SKILL);
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			setModuleName("Skill");
			setFormlayout(FormLayout.getNewSkillLayout(fields));
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			if(conn != null) {
				try {
					conn.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	    return SUCCESS;
	}
	
	public String addSkill() throws Exception {
		
		skill.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		long skillId = SkillAPI.addSkill(skill);
		
		setSkillId(skillId);
		
		return SUCCESS;
	}
	
	private SkillContext skill;
	public SkillContext getSkill() {
		return skill;
	}
	public void setSkill(SkillContext skill) {
		this.skill = skill;
	}
	
	private long skillId;
	public long getSkillId() {
		return skillId;
	}

	public void setSkillId(long skillId) {
		this.skillId = skillId;
	}
	
	private List<SkillContext> skills = null;
	public List<SkillContext> getSkills() {
		return skills;
	}
	public void setSkills(List<SkillContext> skills) {
		this.skills = skills;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.SKILL;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewSkillLayout();
	}
	
	public String getViewName()
	{
		return "Skills";
	}

	public String getNewActionType() {
		return "dialog"; // dialog or empty string
	}
	
	public List<SkillContext> getRecords() 
	{
		return skills;
	}
}