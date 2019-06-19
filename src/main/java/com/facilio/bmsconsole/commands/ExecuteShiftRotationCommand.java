package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.ShiftRotationApplicableForContext;
import com.facilio.bmsconsole.context.ShiftRotationApplicableForContext.ApplicableFor;
import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.context.ShiftRotationDetailsContext;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class ExecuteShiftRotationCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long shiftRotationId = (long) context.get(FacilioConstants.ContextNames.ID);
		if (shiftRotationId > 0) {
			ShiftRotationContext shiftRotation = ShiftAPI.getShiftRotation(shiftRotationId);
			
			long[] startAndEndTime = shiftRotation.getStartAndEndTime();
			List<ShiftUserRelContext> shiftUserRel = new ArrayList<>();
//			List<ShiftRotationApplicableForContext> applicableFors = ShiftAPI
//					.getApplicableForShiftRotation(shiftRotationId);
//			List<ShiftRotationDetailsContext> shiftRotationDetails = ShiftAPI
//					.getShiftRotationDetailsForShiftRotation(shiftRotationId);
			Set<Long> userIds = new HashSet();
			if (!CollectionUtils.isEmpty(shiftRotation.getApplicableFor())) {
				for (ShiftRotationApplicableForContext applicableFor : shiftRotation.getApplicableFor()) {
					if (applicableFor.getApplicableForTypeEnum() == ApplicableFor.USERS) {
						userIds.add(applicableFor.getApplicableForId());
					} else if (applicableFor.getApplicableForTypeEnum() == ApplicableFor.ROLES) {
						List<User> roleUsers = AccountUtil.getUserBean()
								.getUsersWithRole(applicableFor.getApplicableForId());
						if (!CollectionUtils.isEmpty(roleUsers)) {
							for (User user : roleUsers) {
								userIds.add(user.getOuid());
							}
						}
					} else if (applicableFor.getApplicableForTypeEnum() == ApplicableFor.GROUPS) {
						List<GroupMember> groupMem = AccountUtil.getGroupBean()
								.getGroupMembers(applicableFor.getApplicableForId());
						if (!CollectionUtils.isEmpty(groupMem)) {
							for (GroupMember groupMember : groupMem) {
								userIds.add(groupMember.getOuid());
							}
						}
					}
				}
			}
			if (!CollectionUtils.isEmpty(shiftRotation.getShiftRotations())) {
				for (ShiftRotationDetailsContext detail : shiftRotation.getShiftRotations()) {
					for (Long userId : userIds) {
						List<ShiftUserRelContext> shiftUsers = ShiftAPI.getShiftUserMapping(startAndEndTime[0], startAndEndTime[1], userId, detail.getFromShiftId(), true);
						if (!CollectionUtils.isEmpty(shiftUsers)) {
							for (ShiftUserRelContext shiftUserContext : shiftUsers) {
								long shiftId = shiftUserContext.getShiftId();
//								if (shiftId > 0) {
//									if (shiftId == detail.getFromShiftId()) {
//										ShiftUserRelContext shiftUser = new ShiftUserRelContext();
//										ShiftContext shift = ShiftAPI.getShift(detail.getToShiftId());
//										shiftUser.setStartTime(shift.getStartTime());
//										shiftUser.setEndTime(shift.getEndTime());
//										shiftUser.setShiftId(detail.getToShiftId());
//										shiftUser.setOuid(userId);
//										shiftUser.setId(shiftUserContext.getId());
								shiftUserContext.setShiftId(detail.getToShiftId());
								shiftUserRel.add(shiftUserContext);
//									}
//								}
							}
						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(shiftUserRel)) {
				System.out.println(shiftUserRel);
				for (ShiftUserRelContext shiftUser : shiftUserRel) {
					ShiftAPI.addShiftUserMapping(shiftUser.getShiftId(), shiftUser.getOuid(), shiftUser.getStartTime(), shiftUser.getEndTime());
				}
//				ShiftAPI.updateShiftUserMapping(shiftUserRel);
			}
		}
		return false;
	}

}
