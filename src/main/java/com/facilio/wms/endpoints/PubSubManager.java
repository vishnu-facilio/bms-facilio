package com.facilio.wms.endpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.cache.RedisManager;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wms.endpoints.LiveSession.LiveSessionType;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;
import com.facilio.wms.util.WmsApi;

import redis.clients.jedis.Jedis;

public class PubSubManager {
	
	private static final Logger logger = Logger.getLogger(PubSubManager.class.getName());
	
	private JSONObject readingWatchersLocal = new JSONObject();
	
	private static PubSubManager INSTANCE; 
	
	public static PubSubManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PubSubManager();
		}
		return INSTANCE;
	}
	
	public void subscribe(LiveSession ls, Message message) {
		
		try {
			long orgId = (message.getOrgId() != null && message.getOrgId() > 0) ? message.getOrgId() : AccountUtil.getUserBean().getUserInternal(ls.getId()).getOrgId();
			
			JSONObject content = message.getContent();
			String topic = (String) content.get("topic");
			if ("readingChange".equalsIgnoreCase(topic)) {
				if (orgId != 146) {
					return;
				}
				String uniqueKey = (String) content.get("uniqueKey");
				ArrayList readings = (ArrayList) content.get("readings");
				
				for (int i = 0; i < readings.size(); i++) {
					Map<String, Object> reading = (Map<String, Object>) readings.get(i);
					Long parentId = getAsLong(reading.get("parentId").toString());
					Long fieldId = reading.containsKey("fieldId") ? getAsLong(reading.get("fieldId").toString()): null;
					if (fieldId == null) {
						String fieldName = (String) reading.get("fieldName");
						String moduleName = (String) reading.get("moduleName");
						
						try {
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
							FacilioField field = modBean.getField(fieldName, moduleName);
							if (field != null) {
								fieldId = field.getId();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (parentId != null && fieldId != null) {
						String readingKey = parentId + "_" + fieldId;
						
						JSONObject watchProps = new JSONObject();
						watchProps.put("uid", message.getTo());
						watchProps.put("sessionType", message.getSessionType().toString());
						watchProps.put("uniqueKey", uniqueKey);
						watchProps.put("timestamp", System.currentTimeMillis());
						
						JSONArray watchers = null;
						
						JSONObject readingWatchers = this.getReadingWatchers(orgId);
						if (readingWatchers == null) {
							readingWatchers = new JSONObject();
						}
						
						if (readingWatchers.containsKey(readingKey)) {
							watchers = (JSONArray) readingWatchers.get(readingKey);
						}
						else {
							watchers = new JSONArray();
						}
						watchers.add(watchProps);
						
						readingWatchers.put(readingKey, watchers);
						
						this.setReadingWatchers(orgId, readingWatchers);
					}
				}
			}
			else if ("importStatusChange".equalsIgnoreCase(topic)) {
				String uniqueKey = (String) content.get("uniqueKey");
				Long importProcessId = Long.parseLong(content.get("importProcessId").toString());
				
				String importProcessKey = importProcessId + "";
				
				JSONObject watchProps = new JSONObject();
				watchProps.put("uid", message.getTo());
				watchProps.put("uniqueKey", uniqueKey);
				watchProps.put("timestamp", System.currentTimeMillis());
				
				JSONArray watchers = null;
				
				JSONObject readingWatchers = this.getReadingWatchers(orgId);
				if (readingWatchers == null) {
					readingWatchers = new JSONObject();
				}
				
				if (readingWatchers.containsKey(importProcessKey)) {
					watchers = (JSONArray) readingWatchers.get(importProcessKey);
				}
				else {
					watchers = new JSONArray();
				}
				watchers.add(watchProps);
				
				readingWatchers.put(importProcessKey, watchers);
				
				this.setReadingWatchers(orgId, readingWatchers);
			}
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Error occurred in subscribe:::: ", e);
		}
	}
	
	private Long getAsLong(String value) {
		if (value != null) {
			if (value.indexOf(".") >= 0) {
				return Double.valueOf(value).longValue();
			}
			else {
				return Long.parseLong(value);
			}
		}
		return null;
	}
	
	public void unsubscribe(LiveSession ls, Message message) {
		
		try {
			if (message.getOrgId() == null || message.getOrgId() <= 0) {
				return;
			}
			long orgId = message.getOrgId();
			
			JSONObject content = message.getContent();
			String topic = (String) content.get("topic");
			if ("readingChange".equalsIgnoreCase(topic)) {
				if (orgId != 146) {
					return;
				}
				String uniqueKey = (String) content.get("uniqueKey");
				ArrayList readings = (ArrayList) content.get("readings");
				
				for (int i = 0; i < readings.size(); i++) {
					Map<String, Object> reading = (Map<String, Object>) readings.get(i);
					Long parentId = getAsLong(reading.get("parentId").toString());
					Long fieldId = reading.containsKey("fieldId") ? getAsLong(reading.get("fieldId").toString()): null;
					if (fieldId == null) {
						String fieldName = (String) reading.get("fieldName");
						String moduleName = (String) reading.get("moduleName");
						
						try {
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
							FacilioField field = modBean.getField(fieldName, moduleName);
							if (field != null) {
								fieldId = field.getId();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (parentId != null && fieldId != null) {
						String readingKey = parentId + "_" + fieldId;
						
						JSONObject readingWatchers = this.getReadingWatchers(orgId);
						if (readingWatchers != null && readingWatchers.containsKey(readingKey)) {
							JSONArray watchers = (JSONArray) readingWatchers.get(readingKey);
							List<Integer> removedWatchers = new ArrayList<>();
							for (int j = 0; j < watchers.size(); j++) {
								JSONObject props = (JSONObject) watchers.get(j);
								String uKey = (String) props.get("uniqueKey");
								if (uniqueKey.equals(uKey)) {
									removedWatchers.add(j);
								}
							}
							
							for (Integer idx : removedWatchers) {
								watchers.remove((int) idx);
							}
							
							if (watchers.size() > 0) {
								readingWatchers.put(readingKey, watchers);
							}
							else {
								readingWatchers.remove(readingKey);
							}
							
							this.setReadingWatchers(orgId, readingWatchers);
						}
					}
				}
			}
			else if ("importStatusChange".equalsIgnoreCase(topic)) {
				String uniqueKey = (String) content.get("uniqueKey");
				Long importProcessId = Long.parseLong(content.get("importProcessId").toString());
				
				String importProcessKey = importProcessId + "";
				
				JSONObject readingWatchers = this.getReadingWatchers(orgId);
				if (readingWatchers != null && readingWatchers.containsKey(importProcessKey)) {
					JSONArray watchers = (JSONArray) readingWatchers.get(importProcessKey);
					List<Integer> removedWatchers = new ArrayList<>();
					for (int j = 0; j < watchers.size(); j++) {
						JSONObject props = (JSONObject) watchers.get(j);
						String uKey = (String) props.get("uniqueKey");
						if (uniqueKey.equals(uKey)) {
							removedWatchers.add(j);
						}
					}
					
					for (Integer idx : removedWatchers) {
						watchers.remove((int) idx);
					}
					
					if (watchers.size() > 0) {
						readingWatchers.put(importProcessKey, watchers);
					}
					else {
						readingWatchers.remove(importProcessKey);
					}
					
					this.setReadingWatchers(orgId, readingWatchers);
				}
			}
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred in unsubscribe.. ", e);
		}
	}
	
	private JSONObject getReadingWatchers(long orgId) {
		if (RedisManager.getInstance() != null) {
			try {
				String redisKey = orgId + "_readingChange_watchers"; 
			
				Jedis jedis = RedisManager.getInstance().getJedis();
				String readingWatchersJSON = jedis.get(redisKey);
				jedis.close();
				
				if (readingWatchersJSON != null) {
					JSONParser parser = new JSONParser();
					try {
						return (JSONObject) parser.parse(readingWatchersJSON);
					} catch (ParseException e) {
						logger.log(Level.WARNING, "Exception occurred ", e);
					}
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception while getting key in Redis. ");
			}
			return null;
		}
		else {
			return this.readingWatchersLocal;
		}
	}
	
	private void setReadingWatchers(long orgId, JSONObject readingWatchers) {
		if (RedisManager.getInstance() != null) {
			try {
				String redisKey = orgId + "_readingChange_watchers"; 
			
				Jedis jedis = RedisManager.getInstance().getJedis();
				jedis.set(redisKey, readingWatchers.toJSONString());
				jedis.close();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception while getting key in Redis. ");
			}
		}
		else {
			this.readingWatchersLocal = readingWatchers;
		}
	}
	
	public boolean isReadingChangeSubscribed(long orgId, String readingKey) {
		try {
			JSONObject readingWatchers = this.getReadingWatchers(orgId);
			
			if (readingWatchers == null || !readingWatchers.containsKey(readingKey)) {
				return false;
			}
			
			JSONArray watchers = (JSONArray) readingWatchers.get(readingKey);
			if (watchers != null && watchers.size() > 0) {
				
				JSONArray newWatchers = new JSONArray();
				
				for (int i = 0; i < watchers.size(); i++) {
					JSONObject watcher = (JSONObject) watchers.get(i);
					
					long timestamp = (Long) watcher.get("timestamp");
					
					long diff = System.currentTimeMillis() - timestamp;
					if (diff < 1296000000) { // 15 days before subscribed
						newWatchers.add(watcher);
					}
				}
				
				if (watchers.size() > 0) {
					
					if (newWatchers.size() > 0) {
						readingWatchers.put(readingKey, newWatchers);
					}
					else {
						readingWatchers.remove(readingKey);
					}
					
					this.setReadingWatchers(orgId, readingWatchers);
				}
				
				return (watchers.size() > 0);
			}
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Error occurred while publish reading change : "+readingKey, e);
		}
		return false;
	}
	
	public void publishReadingChange(long orgId, String readingKey) {
		try {
			JSONObject readingWatchers = this.getReadingWatchers(orgId);
			
			if (readingWatchers == null || !readingWatchers.containsKey(readingKey)) {
				return;
			}
			
			JSONArray watchers = (JSONArray) readingWatchers.get(readingKey);
			if (watchers != null) {
				for (int i = 0; i < watchers.size(); i++) {
					JSONObject watcher = (JSONObject) watchers.get(i);
					
					long uid = (Long) watcher.get("uid");
					String uniqueKey = (String) watcher.get("uniqueKey");
					LiveSessionType liveSessionType = LiveSessionType.valueOf((String) watcher.get("sessionType"));
					
					Message msg = new Message();
					msg.setMessageType(MessageType.PUBSUB);
					msg.setAction("publish");
					msg.setNamespace("pubsub");
					if (liveSessionType != null) {
						msg.setSessionType(liveSessionType);
					}
					msg.setTo(uid);
					msg.addData("uniqueKey", uniqueKey);
					
					List<Long> recipients = new ArrayList<>();
					recipients.add(uid);
					WmsApi.sendPubSubMessage(recipients, msg);
				}
			}
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Error occurred while publish reading change : "+readingKey, e);
		}
	}
	
	public void publishImportStatusChange(long orgId, long importProcessId, JSONObject additionalInfo) {
		
		String importProcessKey = importProcessId + "";
		try {
			JSONObject readingWatchers = this.getReadingWatchers(orgId);
			
			if (readingWatchers == null || !readingWatchers.containsKey(importProcessKey)) {
				return;
			}
			
			JSONArray watchers = (JSONArray) readingWatchers.get(importProcessKey);
			if (watchers != null) {
				for (int i = 0; i < watchers.size(); i++) {
					JSONObject watcher = (JSONObject) watchers.get(i);
					
					long uid = (Long) watcher.get("uid");
					String uniqueKey = (String) watcher.get("uniqueKey");
					
					Message msg = new Message();
					msg.setMessageType(MessageType.PUBSUB);
					msg.setAction("publish");
					msg.setNamespace("pubsub");
					msg.setTo(uid);
					msg.addData("uniqueKey", uniqueKey);
					msg.addData("additionalInfo", additionalInfo);
					
					List<Long> recipients = new ArrayList<>();
					recipients.add(uid);
					WmsApi.sendPubSubMessage(recipients, msg);
				}
			}
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Error occurred while publish reading change : "+importProcessKey, e);
		}
	}
}
