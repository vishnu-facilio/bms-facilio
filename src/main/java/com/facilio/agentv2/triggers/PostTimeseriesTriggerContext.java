package com.facilio.agentv2.triggers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostTimeseriesTriggerContext extends BaseTriggerContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FacilioAgent agent;
	private long agentId = -1;
	private Criteria criteria;
	private long criteriaId;

	@Override
	public long getModuleId() {
		return 0;
	}

	@Override
	public int getType() {
		return getTypeEnum().getValue();
	}

	@Override
	public TriggerType getTypeEnum() {
		return TriggerType.AGENT_TRIGGER;
	}

	@Override
	public void validateTrigger() {
		try {

			if (getId() < 0) {
				if (agentId <= 0) {
					throw new IllegalArgumentException("Agent cannot be empty");
				}
				if (criteria == null) {
					throw new IllegalArgumentException("Criteria cannot be empty");
				}
			}
			super.validateTrigger();
		}catch (Exception e){

		}
	}

	@Override
	public List<Long> fetchRecordIds() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ASSET);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.beanClass(ModuleBaseWithCustomFields.class)
				.select(Collections.singletonList(FieldFactory.getIdField(module)))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(agent.getSiteId()), NumberOperators.EQUALS))
				.andCriteria(criteria);
				;
		
		List<ModuleBaseWithCustomFields> props = builder.get();
		return props.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
	}
}
