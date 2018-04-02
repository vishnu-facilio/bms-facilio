package com.facilio.fw;

import java.util.concurrent.ConcurrentHashMap;

public class LRUCache<K, V>{

	public static LRUCache<Integer, Object> getModuleFieldsCache() {
		return modulefieldCache;
	}
	public static LRUCache<Integer, Object> getFieldsCache() {
		return fieldCache;
	}
	public String toString() {
		 return (" The current size "+currentSize+"\n Cache Hit Ratio= "+ ((hitcount)/(hitcount+misscount) )*100 +"\n\n"+cache);
	}
	private static LRUCache<Integer,Object> fieldCache = new LRUCache<Integer,Object>(300);
	private static LRUCache<Integer,Object> modulefieldCache = new LRUCache<Integer,Object>(300);
    private long hitcount = 0;
    private long misscount = 1;
    // Define Node with pointers to the previous and next items and a key, value pair
    class Node<T, U> {
        Node<T, U> previous;
        Node<T, U> next;
        T key;
        U value;

        public Node(Node<T, U> previous, Node<T, U> next, T key, U value){
            this.previous = previous;
            this.next = next;
            this.key = key;
            this.value = value;
        }
        
        public String toString()
        {
        	return "\n"+key +":" +value;
        }
    }

    private ConcurrentHashMap<K, Node<K, V>> cache;
    private Node<K, V> leastRecentlyUsed;
    private Node<K, V> mostRecentlyUsed;
    private int maxSize;
    private int currentSize;
    

    public void purgeCache()
    {
    	   this.currentSize = 0;
         leastRecentlyUsed = new Node<K, V>(null, null, null, null);
         mostRecentlyUsed = leastRecentlyUsed;
         cache = new ConcurrentHashMap<K, Node<K, V>>();
    }

    public LRUCache(int maxSize){
        this.maxSize = maxSize;
        this.currentSize = 0;
        leastRecentlyUsed = new Node<K, V>(null, null, null, null);
        mostRecentlyUsed = leastRecentlyUsed;
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
	        else if (tempNode.key == mostRecentlyUsed.key){
	        	hitcount++;
	            return mostRecentlyUsed.value;
	        }
	
	        // Get the next and previous nodes
	        Node<K, V> nextNode = tempNode.next;
	        Node<K, V> previousNode = tempNode.previous;
	
	        // If at the left-most, we update LRU 
	        if (tempNode.key == leastRecentlyUsed.key){
	            nextNode.previous = null;
	            leastRecentlyUsed = nextNode;
	        }
	        else if(nextNode==null)
	        {
	        	   // do nothing if this node is the recently used one ..
	        }
	        else if (tempNode.key != mostRecentlyUsed.key){
		        // If we are in the middle, we need to update the items before and after our item
	            previousNode.next = nextNode;
	            nextNode.previous = previousNode;
	        }
	
	        // Finally move our item to the MRU
	        tempNode.previous = mostRecentlyUsed;
	        mostRecentlyUsed.next = tempNode;
	        mostRecentlyUsed = tempNode;
	        mostRecentlyUsed.next = null;
	        hitcount++;
	        return tempNode.value;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    public void put(K key, V value){
        if (cache.containsKey(key)){
            return;
        }

        // Put the new node at the right-most end of the linked-list
        Node<K, V> myNode = new Node<K, V>(mostRecentlyUsed, null, key, value);
        mostRecentlyUsed.next = myNode;
        cache.put(key, myNode);
        mostRecentlyUsed = myNode;

        // Delete the left-most entry and update the LRU pointer
        if (currentSize == maxSize){
            cache.remove(leastRecentlyUsed.key);
            leastRecentlyUsed = leastRecentlyUsed.next;
            leastRecentlyUsed.previous = null;
        }

        // Update cache size, for the first added entry update the LRU pointer
        else if (currentSize < maxSize){
            if (currentSize == 0){
                leastRecentlyUsed = myNode;
            }
            currentSize++;
        }
    }
    
    public void remove(K key)
    {
    	  Node<K, V> tempNode = cache.get(key);
          if (tempNode == null){
              return ;
          }
          

          // Get the next and previous nodes
          Node<K, V> nextNode = tempNode.next;
          Node<K, V> previousNode = tempNode.previous;

          // If at the left-most, we update LRU 
          if (tempNode.key == leastRecentlyUsed.key){
              nextNode.previous = null;
              leastRecentlyUsed = nextNode;
          }

          // If we are in the middle, we need to update the items before and after our item
          else if (tempNode.key != mostRecentlyUsed.key){
              previousNode.next = nextNode;
              nextNode.previous = previousNode;
          } else 
          {
        	  // at the end of the list 
        	     previousNode.next = null;
        	     mostRecentlyUsed = previousNode;
        	  
          }
          cache.remove(key);

          currentSize--;

          return ;

    }
}