package com.facilio.fw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LRUCache<K, V>{

	private static Logger LOGGER = LogManager.getLogger(LRUCache.class.getName());

	public static void main(String args []) throws InterruptedException
    {
    	LRUCache testcache =  	new LRUCache<String,Object>("test", 18);
    	String arrya[] = {"yoge","babu","karry" ,"ram","manthosh","shivaraj","vikram","magesh","vivek","radhakrishnan","swami","manthosh","krishna","praveen","simran","madhura","gowtham","dhivya","aravind"};
   
    	for(int i=0;i<arrya.length;i++)
    	{
    		Thread.sleep(2000);
      	testcache.put(arrya[i], arrya[i]);
    	}
    	Thread.sleep(2000);
   // 	testcache.get("yoge");
    	Thread.sleep(2000);

    	testcache.get("babu");
    	Thread.sleep(1500);

    	testcache.get("manthosh");

System.out.println("Before "+testcache);
    	
    	testcache.clearTenPercentile();
    	System.out.println("After "+testcache);

    	
    }

	private static final Logger logger = LogManager.getLogger(LRUCache.class.getName());
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
	private static LRUCache<String,Object> fieldCache = new LRUCache<String,Object>("fieldCache", 2000);
	private static LRUCache<String,Object> modulefieldCache = new LRUCache<String,Object>("moduleFieldCache", 2000);
	private static LRUCache<String,Object> userSessionCache = new LRUCache<String,Object>("userSessionCache", 300);
	private static LRUCache<String,Object> moduleCache = new LRUCache<String,Object>("moduleCache", 2000);

    private long hitcount = 0;
    private long misscount = 1;
    // Define Node with pointers to the previous and next items and a key, value pair
    class Node<T, U> implements Comparable<Node> {
        Node<T, U> previous;
        Node<T, U> next;
        T key;
        U value;
        long lastaccessedtime;

        public Node(Node<T, U> previous, Node<T, U> next, T key, U value){
            this.previous = previous;
            this.next = next;
            this.key = key;
            this.value = value;
            this.lastaccessedtime= System.currentTimeMillis();
        }
        
        public  int compareTo(Node o)
        {
        	 return (int)( (this.lastaccessedtime-o.lastaccessedtime)/1000);
        }

        public long getLastAccessTime()
        {
         return	 this.lastaccessedtime;
        }
        
        public String toString()
        {
        	return "\n Key "+key +": value" +value +" ---Last access time " + new java.util.Date(lastaccessedtime)+"\n";
        }
        
        public U getValue()
        {
        	this.lastaccessedtime= System.currentTimeMillis();
        	return value;
        }
    }

    private ConcurrentHashMap<K, Node<K, V>> cache;
  //  private Node<K, V> leastRecentlyUsed;
   // private Node<K, V> mostRecentlyUsed;
    private int maxSize;
    private int currentSize;


    private void clearTenPercentile()
    {
    	   long mintime = 0;
    	   long maxtime = 0;
    	   long totalcount = cache.size();
    	   long tenpercentile =  totalcount/10 >1 ? totalcount/10 :1 ;
    	   
    	   SortedArrayList<Node> list = new SortedArrayList<>();
    	   Enumeration<LRUCache<K,V>.Node<K,V>> enums =  cache.elements();
    	   
    	  while (enums.hasMoreElements())
    	  {
    		  Node node=   enums.nextElement();

    		  if(mintime==0 && maxtime==0)
    		  {
    			  // first time 
    			long lastaccesstime = node.getLastAccessTime();
    			mintime = lastaccesstime;
    			maxtime = lastaccesstime;
    		  }
    		   if(list.size()>tenpercentile)
    		  {
    			  // Queeue is full
    			  if(node.lastaccessedtime >maxtime)
    			  {
    				  // do nothing
    				  continue;
    			  }
    			  else
    			  {
    				  // remove last element
    				 

    				Node removedelement =   list.remove(list.size()-1);
    				if(list.size()>0)
    				maxtime = list.get(list.size()-1).lastaccessedtime;
    			  }
    		  }
    		  if(mintime > node.lastaccessedtime)
			  {
				  mintime  = node.lastaccessedtime;
			  }
			  if(maxtime < node.lastaccessedtime)
			  {
				  maxtime  = node.lastaccessedtime;
			  }
    		  list.insertSorted(node);
    	  }
    	  for(int i = 0;i<list.size();i++)
    	  {
    		  cache.remove(list.get(i).key);
    		  System.out.println("Removing "+list.get(i).key);
    		  currentSize--;
    	  }
    }
    class SortedArrayList<T> extends ArrayList<T> {

        @SuppressWarnings("unchecked")
        public void insertSorted(T value) {
            add(value);
            Comparable<T> cmp = (Comparable<T>) value;
            for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
                Collections.swap(this, i, i-1);
        }
    }
    public void purgeCache()
    {
    	   this.currentSize = 0;
    //     leastRecentlyUsed = new Node<K, V>(null, null, null, null);
      //   mostRecentlyUsed = leastRecentlyUsed;
         cache = new ConcurrentHashMap<K, Node<K, V>>();
    }
    
    private String name;

    public LRUCache(String name, int maxSize){
    	this.name = name;
        this.maxSize = maxSize;
        this.currentSize = 0;
      //  this.tenPercent = (maxSize/10);
    //    leastRecentlyUsed = new Node<K, V>(null, null, null, null);
     //   mostRecentlyUsed = leastRecentlyUsed;
        cache = new ConcurrentHashMap<K, Node<K, V>>();
    }

    public V get(K key){
    	try {
	        Node<K, V> tempNode = cache.get(key);
	        if (tempNode == null){
	         	misscount++;
	            return null;
	        }
	        // If MRU leave the list as it is
	      
	
	        // Get the next and previous nodes
	      
	
	        // Finally move our item to the MRU
	       
	        hitcount++;
	        
	        return tempNode.getValue();
    	}
    	catch (Exception e) {
    		LOGGER.info("Exception occurred ", e);
    		return null;
    	}
    }

    public void put(K key, V value){

        if (cache.containsKey(key)){

            return;
        }

        // Put the new node at the right-most end of the linked-list
        Node<K, V> myNode = new Node<K, V>(null, null, key, value);
     //   mostRecentlyUsed.next = myNode;
        cache.put(key, myNode);
        currentSize++;
       // mostRecentlyUsed = myNode;

        // Delete the left-most entry and update the LRU pointer
        if (currentSize >= maxSize){
            synchronized (cache) {
                try {
                    clearTenPercentile();
                } catch (Exception e) {
                    logger.log(Level.INFO, "Error in put " + this , e);
                    throw e;
                }
            }
        }

        // Update cache size, for the first added entry update the LRU pointer
        else if (currentSize < maxSize){
            
           
        }
    }
    
    public void remove(K key)
    {
    	  Node<K, V> tempNode = cache.get(key);
          if (tempNode == null){
              return ;
          }
          

          // Get the next and previous nodes
      /*    Node<K, V> nextNode = tempNode.next;
          Node<K, V> previousNode = tempNode.previous;

          // If at the left-most, we update LRU 
          if ( leastRecentlyUsed!= null && tempNode.key == leastRecentlyUsed.key){
              nextNode.previous = null;
              leastRecentlyUsed = nextNode;
          }

          // If we are in the middle, we need to update the items before and after our item
          else if (mostRecentlyUsed !=null && tempNode.key != mostRecentlyUsed.key){
              previousNode.next = nextNode;
              nextNode.previous = previousNode;
          } else 
          {
        	  // at the end of the list 
        	  if(previousNode!=null)
        	  {
        	     previousNode.next = null;
        	     mostRecentlyUsed = previousNode;
        	  }
        	  
          }
          */
          System.out.println("Key :"+key);
          cache.remove(key);

          currentSize--;

          return ;

    }

}