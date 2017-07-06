package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.util.SkillAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class SkillActions extends ActionSupport {
	
	public String execute() throws Exception {
		
		setSkills(SkillAPI.getAllSkill(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
	    return SUCCESS;
	}
	
	public String add() throws Exception {
		
		skill.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		long skillId = SkillAPI.addSkill(skill);
		
		setSkillId(skillId);
		
		return "add_skill_success";
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
}