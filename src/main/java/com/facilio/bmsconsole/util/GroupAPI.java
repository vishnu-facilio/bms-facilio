package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.GroupMemberContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class GroupAPI {
	
	public static long addGroup(GroupContext groupContext) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Groups (ORGID, GROUP_NAME, GROUP_EMAIL, GROUP_DESC, IS_ACTIVE, CREATED_TIME, CREATED_BY, PARENT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, groupContext.getOrgId());
			pstmt.setString(2, groupContext.getName());
			pstmt.setString(3,  groupContext.getEmail());
			pstmt.setString(4, groupContext.getDescription());
			pstmt.setBoolean(5, groupContext.isActive());
			pstmt.setLong(6, groupContext.getCreatedTime());
			pstmt.setLong(7, groupContext.getCreatedBy());
			pstmt.setLong(8, groupContext.getParent());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add group");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long groupId = rs.getLong(1);
				System.out.println("Added group with id : "+groupId);
				return groupId;
			}
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static boolean updateGroup(GroupContext groupContext) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Groups SET GROUP_NAME=?, GROUP_EMAIL=?, GROUP_DESC=?, IS_ACTIVE=?, PARENT=? WHERE GROUPID=?");
			
			pstmt.setString(1, groupContext.getName());
			pstmt.setString(2,  groupContext.getEmail());
			pstmt.setString(3, groupContext.getDescription());
			pstmt.setBoolean(4, groupContext.isActive());
			pstmt.setLong(5, groupContext.getParent());
			pstmt.setLong(6, groupContext.getGroupId());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to update group");
			}
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<GroupContext> getGroupsOfOrg(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT t1.*, COUNT(t2.MEMBERID) as MEMBERS_COUNT FROM Groups t1 LEFT JOIN GroupMembers t2 ON t1.GROUPID=t2.GROUPID WHERE t1.ORGID = ? GROUP BY t1.GROUPID ORDER BY t1.GROUP_NAME");
			pstmt.setLong(1, orgId);
			
			List<GroupContext> groups = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				GroupContext gc = getGroupObjectFromRS(rs, true);
				groups.add(gc);
			}
			
			return groups;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<GroupContext> getGroupsOfOrg(long orgId, boolean isActive) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT t1.*, COUNT(t2.MEMBERID) as MEMBERS_COUNT FROM Groups t1 LEFT JOIN GroupMembers t2 ON t1.GROUPID=t2.GROUPID WHERE t1.ORGID = ? AND t1.IS_ACTIVE=? GROUP BY t1.GROUPID ORDER BY t1.GROUP_NAME");
			pstmt.setLong(1, orgId);
			pstmt.setBoolean(2, isActive);
			
			List<GroupContext> groups = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				GroupContext gc = getGroupObjectFromRS(rs, true);
				groups.add(gc);
			}
			
			return groups;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static GroupContext getGroup(long groupId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT t1.*, COUNT(t2.MEMBERID) as MEMBERS_COUNT FROM Groups t1 LEFT JOIN GroupMembers t2 ON t1.GROUPID=t2.GROUPID WHERE t1.ORGID = ? GROUP BY t1.GROUPID ORDER BY t1.GROUP_NAME");
			pstmt.setLong(1, groupId);
			
			rs = pstmt.executeQuery();
			GroupContext gc = null;
			while(rs.next()) {
				gc = getGroupObjectFromRS(rs, true);
				break;
			}
			
			return gc;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static boolean deleteGroup(long groupId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE FROM Groups WHERE GROUPID = ?");
			pstmt.setLong(1, groupId);
			
			if (pstmt.executeUpdate() < 1) {
				return false;
			}
			return true;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static GroupContext getGroupObjectFromRS(ResultSet rs, boolean withMembersCount) throws SQLException {
		GroupContext tc = new GroupContext();
		tc.setGroupId(rs.getLong("GROUPID"));
		tc.setOrgId(rs.getLong("ORGID"));
		tc.setName(rs.getString("GROUP_NAME"));
		tc.setEmail(rs.getString("GROUP_EMAIL"));
		tc.setDescription(rs.getString("GROUP_DESC"));
		tc.setActive(rs.getBoolean("IS_ACTIVE"));
		tc.setCreatedTime(rs.getLong("CREATED_TIME"));
		tc.setCreatedBy(rs.getLong("CREATED_BY"));
		tc.setParent(rs.getLong("PARENT"));
		if (withMembersCount) {
			tc.setMembersCount(rs.getInt("MEMBERS_COUNT"));
		}
		return tc;
	}
	
	public static boolean addGroupMember(long groupId, long members[], int memberRole) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			pstmt = conn.prepareStatement("INSERT INTO GroupMembers (GROUPID, ORG_USERID, MEMBER_ROLE) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			for (long userId : members) {
				pstmt.setLong(1, groupId);
				pstmt.setLong(2, userId);
				pstmt.setInt(3, memberRole);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static boolean deleteGroupMembers(long groupId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM GroupMembers WHERE GROUPID=?");
			pstmt.setLong(1, groupId);
			pstmt.executeUpdate();
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<GroupMemberContext> getGroupMembers(long groupId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM GroupMembers, ORG_Users, Users WHERE GroupMembers.ORG_USERID = ORG_Users.ORG_USERID AND ORG_Users.USERID = Users.USERID AND GroupMembers.GROUPID=?");
			pstmt.setLong(1, groupId);
			
			List<GroupMemberContext> members = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				GroupMemberContext gmc = getGroupMemberObjectFromRS(rs);
				members.add(gmc);
			}
			
			return members;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static Map<Long, String> getGroupMembersMap(long groupId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> groupUsers = new LinkedHashMap<>();
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT GroupMembers.ORG_USERID, Users.EMAIL FROM GroupMembers, ORG_Users, Users WHERE GroupMembers.ORG_USERID = ORG_Users.ORG_USERID AND ORG_Users.USERID = Users.USERID AND GroupMembers.GROUPID=?");
			pstmt.setLong(1, groupId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				long userId = rs.getLong("ORG_USERID");
				String email = rs.getString("EMAIL");
				groupUsers.put(userId, email);
			}
			return groupUsers;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static GroupMemberContext getGroupMemberObjectFromRS(ResultSet rs) throws SQLException {
		GroupMemberContext memberContext = new GroupMemberContext();
		memberContext.setMemberId(rs.getLong("MEMBERID"));
		memberContext.setGroupId(rs.getLong("GROUPID"));
		memberContext.setOrgId(rs.getLong("ORGID"));
		memberContext.setUserId(rs.getLong("USERID"));
		memberContext.setOrgUserId(rs.getLong("ORG_USERID"));
		memberContext.setMemberRole(rs.getInt("MEMBER_ROLE"));
		memberContext.setEmail(rs.getString("EMAIL"));
		memberContext.setInvitedTime(rs.getLong("INVITEDTIME"));
		memberContext.setInviteAcceptStatus(rs.getBoolean("INVITATION_ACCEPT_STATUS"));
		memberContext.setRole(rs.getInt("ROLE"));
		return memberContext;
	}
}
