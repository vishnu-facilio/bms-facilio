package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchCraftAndSkillsCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<CraftContext> crafts = Constants.getRecordList((FacilioContext) context);

		List<Long> craftIds = crafts.stream().map(CraftContext::getId).collect(Collectors.toList());

		SelectRecordsBuilder<SkillsContext> select = new SelectRecordsBuilder<SkillsContext>().moduleName(FacilioConstants.CraftAndSKills.SKILLS).select(Constants.getModBean().getAllFields(FacilioConstants.CraftAndSKills.SKILLS)).beanClass(SkillsContext.class).andCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("parentId", FacilioConstants.CraftAndSKills.SKILLS), craftIds, NumberOperators.EQUALS));

		List<SkillsContext> skillsContextList = select.get();

		if(CollectionUtils.isNotEmpty(skillsContextList)){

			Map<Long, List<SkillsContext>> skillsMap = skillsContextList.stream().collect(Collectors.groupingBy(s -> s.getParentId().getId()));

			crafts.forEach(craft -> craft.setSkills(skillsMap.get(craft.getId())));

		}

		return false;
	}
}
