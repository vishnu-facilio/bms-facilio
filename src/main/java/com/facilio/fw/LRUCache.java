package com.facilio.fw;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.PubSubLRUCache;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.cache.RedisManager;

import redis.clients.jedis.Jedis;

public class LRUCache<K, V> implements FacilioCache<K, V> {

	public static void main(String args []) throws InterruptedException {

    	LRUCache testcache =  	new LRUCache<String,Object>("test", 68);
    	String arrya[] = {"yoge","babu","karry" ,"ram","manthosh","shivaraj","vikram","magesh","vivek","radhakrishnan","swami","manthosh","krishna","praveen","simran","madhura","gowtham","dhivya","aravind"};
   
    	for(int i=0;i<arrya.length;i++) {
    		Thread.sleep(500);
      		testcache.put(arrya[i], arrya[i]);
    	}
    	Thread.sleep(2000);
   // 	testcache.get("yoge");
    	Thread.sleep(2000);

        System.out.println("value for babu old -" + testcache.get("babu"));
    	Thread.sleep(1500);
    	//testcache.putInRedis("babu" , "babu1");private Long getFromLocalRedisCache ()

    	//testcache.get("manthosh");
        System.out.println("value for babu new - " + testcache.get("babu"));
    	/*System.out.println("Before "+testcache);
    	
    	testcache.clearTenPercentile();
    	System.out.println("After "+testcache);*/
    }

	private static final Logger LOGGER = LogManager.getLogger(LRUCache.class.getName());

	private static FacilioCache<String, Object> fieldCache = new LRUCache<>("fieldCache", 2000);
	private static FacilioCache<String, Object> fieldNameCache = new LRUCache<>("fieldNameCache", 2000);
	private static FacilioCache<String, Object> modulefieldCache = new LRUCache<>("moduleFieldCache", 2000);
	private static FacilioCache<String, Object> userSessionCache = new LRUCache<>("userSessionCache", 300);
	private static FacilioCache<String, Object> moduleCache = new LRUCache<>("moduleCache", 2000);
	private static FacilioCache<String, Long> queryCache = new LRUCache<>("queryCache", 500);
	private static FacilioCache<String, Object> responseCache = new LRUCache<>("responseCache", 5000);

	private static FacilioCache<String, Object> fieldCachePS = new PubSubLRUCache<>("fieldCache", 2000);
	private static FacilioCache<String, Object> fieldNameCachePS = new PubSubLRUCache<>("fieldNameCache", 2000);
	private static FacilioCache<String, Object> moduleFieldCachePS = new PubSubLRUCache<>("moduleFieldCache", 2000);
	private static FacilioCache<String, Object> userSessionCachePS = new PubSubLRUCache<>("userSessionCache", 300);
	private static FacilioCache<String, Object> moduleCachePS = new PubSubLRUCache<>("moduleCache", 2000);
	private static FacilioCache<String, Long> queryCachePS = new PubSubLRUCache<>("queryCache", 500);
	private static FacilioCache<String, Object> responseCachePS = new PubSubLRUCache<>("responseCache", 5000);
	private static FacilioCache<String, Long> featureLicenseCachePS = new PubSubLRUCache<>("featureLicense", 1000);
	private static FacilioCache<String, Object> orgUnitCachePs = new PubSubLRUCache<>("orgUnit",1000);

	private static final List<FacilioCache> CACHE_LIST = initCacheList();

	// Add the cache in the following method for automatic purging
	private static List<FacilioCache> initCacheList() {
		List<FacilioCache> cacheList = new ArrayList<>();
		cacheList.add(fieldCachePS);
		cacheList.add(fieldNameCachePS);
		cacheList.add(moduleFieldCachePS);
		cacheList.add(userSessionCache);
		cacheList.add(moduleCachePS);
		cacheList.add(queryCachePS);
		cacheList.add(responseCachePS);
		cacheList.add(featureLicenseCachePS);
		cacheList.add(orgUnitCachePs);

		return Collections.unmodifiableList(cacheList);
	}

	public static void purgeAllCache() {
		for (FacilioCache cache : CACHE_LIST) {
			cache.purgeCache();
		}
	}


	private long hitcount = 0;
    private long misscount = 1;
	private String name;
	private RedisManager redis;
	private ConcurrentHashMap<K, Node<K, V>> cache;
	private int maxSize;

	private LRUCache(String name, int maxSize){
		this.name = FacilioProperties.getConfig("environment")+'_'+name;
		this.maxSize = maxSize;
		cache = new ConcurrentHashMap<K, Node<K, V>>();
		redis = RedisManager.getInstance();
	}


	public static FacilioCache<String, Object> getModuleFieldsCache() {
//		if(FacilioProperties.isProduction()) {
//			return modulefieldCache;
//		}
		return moduleFieldCachePS;
	}
	public static FacilioCache<String, Object> getFieldsCache() {
//		if(FacilioProperties.isProduction()) {
//			return fieldCache;
//		}
		return fieldCachePS;
	}
	public static FacilioCache<String, Object> getFieldNameCache() {
//		if(FacilioProperties.isProduction()) {
//			return fieldNameCache;
//		}
		return fieldNameCachePS;
	}
	public static FacilioCache<String, Object> getUserSessionCache() {
//		if(FacilioProperties.isProduction()) {
//			return userSessionCache;
//		}
		return userSessionCachePS;
	}
	public static FacilioCache<String, Object> getModuleCache() {
//		if(FacilioProperties.isProduction()) {
//			return moduleCache;
//		}
		return moduleCachePS;
	}
	public static FacilioCache<String, Long> getQueryCache() {
//		if(FacilioProperties.isProduction()) {return queryCache;}
		return queryCachePS;
	}
	public static FacilioCache<String, Object> getResponseCache() {
//		if(FacilioProperties.isProduction()){return responseCache;}
		return responseCachePS;
	}

	public static FacilioCache<String, Long> getFeatureLicenseCache() {
		return featureLicenseCachePS;
	}

	public static FacilioCache<String, Object> getOrgUnitCachePs(){
		return orgUnitCachePs;
	}

	public String toString() {
		double hitc =  ((hitcount*100d)/(hitcount+misscount) );
		return (" The current size "+cache.size()+"\n hitcount "+hitcount+"\n Cache Hit Ratio= "+ hitc +"\n\n"+cache );
	}

    // Define Node with pointers to the previous and next items and a key, value pair
    class Node<T, U> implements Comparable<Node> {
        Node<T, U> previous;
        Node<T, U> next;
        T key;
        U value;
        long lastaccessedtime;
        long addedTime;

        Node(Node<T, U> previous, Node<T, U> next, T key, U value) {
            this.previous = previous;
            this.next = next;
            this.key = key;
            this.value = value;
            this.addedTime = System.currentTimeMillis();
			this.lastaccessedtime= System.currentTimeMillis();
        }

        public  int compareTo(Node other) {
        	 return (int)( (this.lastaccessedtime - other.lastaccessedtime)/1000);
        }

        long getLastAccessTime() {
         return	 this.lastaccessedtime;
        }

        public String toString() {
        	return "\n Key "+key +": value" +value +" ---Last access time " + new java.util.Date(lastaccessedtime)+"\n";
        }

        public U getValue() {
        	this.lastaccessedtime = System.currentTimeMillis();
        	return value;
        }
    }

    private void clearTenPercentile() {
    	   long mintime = 0;
    	   long maxtime = 0;
    	   long totalcount = cache.size();
    	   long tenpercentile =  (totalcount / 10 ) > 1 ? (totalcount / 10) : 1 ;
    	   
    	   SortedArrayList<Node> list = new SortedArrayList<>();
    	   Enumeration<LRUCache<K,V>.Node<K,V>> enums =  cache.elements();
    	   
    	  while (enums.hasMoreElements()) {
    		  Node node=   enums.nextElement();

    		  if(mintime == 0 && maxtime == 0) { // first time
    			long lastaccesstime = node.getLastAccessTime();
    			mintime = lastaccesstime;
    			maxtime = lastaccesstime;
    		  }

    		  if(list.size() > tenpercentile) {
    			  // Queeue is full
    			  if(node.lastaccessedtime > maxtime) {
    				  // do nothing
    				  continue;
    			  } else {
    				  list.remove(list.size()-1);
    				if(list.size() > 0) {
						maxtime = list.get(list.size() - 1).lastaccessedtime;
					}
    			  }
    		  }
    		  if(mintime > node.lastaccessedtime) {
				  mintime  = node.lastaccessedtime;
			  }
			  if(maxtime < node.lastaccessedtime) {
				  maxtime  = node.lastaccessedtime;
			  }
    		  list.insertSorted(node);
    	  }

    	  for(int i = 0; i<list.size(); i++) {
    		  cache.remove(list.get(i).key);
    		  LOGGER.debug("Removing "+list.get(i).key);
    	  }
    }
    class SortedArrayList<T> extends ArrayList<T> {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
        void insertSorted(T value) {
            add(value);
            Comparable<T> cmp = (Comparable<T>) value;
            for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
                Collections.swap(this, i, i-1);
        }
    }

    public void purgeCache() {
		purgeInRedis();
        cache.clear();
    }

	@Override
	public Set<K> keys() {
		return cache.keySet();
	}

	public boolean contains(K key) {
    	return cache.containsKey(key);
    }

    public V get(K key){
    	try {
            Node<K, V> tempNode = cache.get(key);
            if (tempNode != null) {
				long redisTimeStamp = getFromRedis(key);
            	if(tempNode.addedTime >= redisTimeStamp){
					hitcount++;
					return tempNode.getValue();
				} else {
            		cache.remove(key);
				}
            }
    	} catch (Exception e) {
    		LOGGER.info("Exception occurred ", e);
    	}
		misscount++;
		return null;
    }

    private Long getFromLocalRedis (String key) {
		if (AccountUtil.getCurrentAccount() != null) {
			return AccountUtil.getCurrentAccount().getFromRedisLocalCache(key);
		}
		return null;
	}

	private void addToLocalRedis (String key, Long value) {
		if (AccountUtil.getCurrentAccount() != null) {
			AccountUtil.getCurrentAccount().addToRedisLocalCache(key, value);
		}
	}

	private Long removeFromLocalRedis (String key) {
		if (AccountUtil.getCurrentAccount() != null) {
			return AccountUtil.getCurrentAccount().removeFromRedisLocalCache(key);
		}
		return null;
	}

    private long getFromRedis(K key) {
		if (redis != null) {
			String redisKey = key.toString();
			Long redisTimestamp = getFromLocalRedis(redisKey);
			if (redisTimestamp == null) {
				long startTime = System.currentTimeMillis();
				AccountUtil.incrementRedisGetCount(1);
				try (Jedis jedis = redis.getJedis()) {
					String value = jedis.get(redisKey);
					if (value == null) {
						redisTimestamp = Long.MAX_VALUE;
					}
					try {
						redisTimestamp = Long.parseLong(value);
					} catch (NumberFormatException e) {
						redisTimestamp = Long.MAX_VALUE;
					}
					addToLocalRedis(redisKey, redisTimestamp);
				} catch (Exception e) {
					LOGGER.debug("Exception while getting key from Redis");
				} finally {
					AccountUtil.incrementRedisGetTime((System.currentTimeMillis() - startTime));
				}
			}
			return redisTimestamp;
		}
        return -1L;
    }

    public void put(K key, V value) {

        if (cache.containsKey(key)) {
            return;
        }

        // Put the new node at the right-most end of the linked-list
        Node<K, V> myNode = new Node<>(null, null, key, value);
        cache.put(key, myNode);
        putInRedis(key, myNode);

        // Delete the left-most entry and update the LRU pointer
        if (cache.size() >= maxSize){
            synchronized (cache) {
                try {
                    clearTenPercentile();
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Error in put " + this , e);
                    throw e;
                }
            }
        }
    }
    
    public void remove(K key) {
		if(cache.containsKey(key)) {
			cache.remove(key);
			deleteInRedis(key);
		}
    }

	@Override
	public void removeStartsWith(K keyStartsWith) {}

	private void deleteInRedis(K key) {
		if (redis != null) {
			long startTime = System.currentTimeMillis();
			AccountUtil.incrementRedisDeleteCount(1);
			try (Jedis jedis = redis.getJedis()) {
				String redisKey = key.toString();
				jedis.del(redisKey);
				removeFromLocalRedis(redisKey);
			} catch (Exception e) {
				LOGGER.debug("Exception while removing key in Redis. ");
			} finally {
				AccountUtil.incrementRedisDeleteTime((System.currentTimeMillis()-startTime));
			}
		}
    }

    private void putInRedis(K key, Node<K, V> node) {
		if (redis != null) {
			long startTime = System.currentTimeMillis();
			AccountUtil.incrementRedisPutCount(1);
			try (Jedis jedis = redis.getJedis()) {
				String redisKey = key.toString();
				jedis.setnx(redisKey, String.valueOf(node.addedTime));
				addToLocalRedis(redisKey, node.addedTime);
			} catch (Exception e) {
				LOGGER.debug("Exception while putting key in Redis. ");
			} finally {
				AccountUtil.incrementRedisPutTime((System.currentTimeMillis()-startTime));
			}
		}
    }

    private void purgeInRedis() {
		if (redis != null) {
			try (Jedis jedis = redis.getJedis()) {
				jedis.flushDB();
			} catch (Exception e) {
				LOGGER.info("Exception while purging data in Redis. ", e);
			}
		}
	}
}