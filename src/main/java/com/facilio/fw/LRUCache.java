package com.facilio.fw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.cache.RedisManager;

import redis.clients.jedis.Jedis;

public class LRUCache<K, V>{

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
    	//testcache.putInRedis("babu" , "babu1");

    	//testcache.get("manthosh");
        System.out.println("value for babu new - " + testcache.get("babu"));
    	/*System.out.println("Before "+testcache);
    	
    	testcache.clearTenPercentile();
    	System.out.println("After "+testcache);*/
    }

	private static final Logger LOGGER = LogManager.getLogger(LRUCache.class.getName());

	private static LRUCache<String, Object> fieldCache = new LRUCache<>("fieldCache", 2000);
	private static LRUCache<String, Object> modulefieldCache = new LRUCache<>("moduleFieldCache", 2000);
	private static LRUCache<String, Object> userSessionCache = new LRUCache<>("userSessionCache", 300);
	private static LRUCache<String, Object> moduleCache = new LRUCache<>("moduleCache", 2000);

    private long hitcount = 0;
    private long misscount = 1;
	private String name;
	private RedisManager redis;
	private ConcurrentHashMap<K, Node<K, V>> cache;
	private int maxSize;
	private int currentSize;

	private LRUCache(String name, int maxSize){
		this.name = AwsUtil.getConfig("environment")+'_'+name;
		this.maxSize = maxSize;
		this.currentSize = 0;
		cache = new ConcurrentHashMap<K, Node<K, V>>();
		redis = RedisManager.getInstance();
	}


	public static LRUCache<String, Object> getModuleFieldsCache() {
		return modulefieldCache;
	}
	public static LRUCache<String, Object> getFieldsCache() {
		return fieldCache;
	}
	public static LRUCache<String, Object> getUserSessionCache() {
		return userSessionCache;
	}
	public static LRUCache<String, Object> getModuleCache() {
		return moduleCache;
	}

	public String toString() {
		double hitc =  ((hitcount*100d)/(hitcount+misscount) );
		return (" The current size "+currentSize+"\n hitcount "+hitcount+"\n Cache Hit Ratio= "+ hitc +"\n\n"+cache );
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
    				  // remove last element
    				Node removedelement =   list.remove(list.size()-1);
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
    		  currentSize--;
    	  }
    }
    class SortedArrayList<T> extends ArrayList<T> {

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
        this.currentSize = 0;
    }

    private String getRedisKey(String key) {
		return name + '_' + key;
	}

    public V get(K key){
    	try {
            long redisTimeStamp = getFromRedis(key);
            Node<K, V> tempNode = cache.get(key);
            if (tempNode != null) {
            	if(tempNode.addedTime >= redisTimeStamp){
					hitcount++;
					return tempNode.getValue();
				} else {
            		cache.remove(key);
            		currentSize--;
				}
            }
    	} catch (Exception e) {
    		LOGGER.info("Exception occurred ", e);
    	}
		misscount++;
		return null;
    }

    private long getFromRedis(K key) {
		try {
			if (redis != null) {
				Jedis jedis = redis.getJedis();
				String value = jedis.get(getRedisKey((String) key));
				jedis.close();
				if (value == null) {
					return Long.MAX_VALUE;
				}
				try {
					return Long.parseLong(value);
				} catch (NumberFormatException e) {
					return Long.MAX_VALUE;
				}
			}
		} catch (Exception e) {
			LOGGER.debug("Exception while getting key from Redis");
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
        currentSize++;

        // Delete the left-most entry and update the LRU pointer
        if (currentSize >= maxSize){
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
			currentSize--;
		}
    }

    private void deleteInRedis(K key) {
		try {
			if (redis != null) {
				Jedis jedis = redis.getJedis();
				jedis.del(getRedisKey((String) key));
				jedis.close();
			}
		} catch (Exception e) {
			LOGGER.debug("Exception while removing key in Redis. ");
		}
    }

    private void putInRedis(K key, Node<K, V> node) {
		try {
			if (redis != null) {
				Jedis jedis = redis.getJedis();
				jedis.set(getRedisKey((String) key), String.valueOf(node.addedTime));
				jedis.close();
			}
		} catch (Exception e) {
			LOGGER.debug("Exception while putting key in Redis. ");
		}
    }

    private void purgeInRedis() {
		try {
			if (redis != null) {
				Jedis jedis = redis.getJedis();
				jedis.flushAll();
				jedis.close();
			}
		} catch (Exception e) {
			LOGGER.info("Exception while purging data in Redis. ", e);
		}
	}
}