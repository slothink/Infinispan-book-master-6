package com.packtpub.infinispan.chapter4;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStartedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStoppedEvent;
import org.infinispan.remoting.transport.Address;

/**
 * Created by slothink on 2015-04-20.
 */
@Listener
public class SampleListener {

    public void print(String message) {
        System.out.println(message);
    }

    @CacheEntryCreated
    public void dataAdded(CacheEntryCreatedEvent event) {
        if(event.isPre()) {
            this.print("Going to add new Entry "+event.getKey()+" to the cache");
        }else {
            print("Added new entry "+ event.getKey() + " to the cache");
        }
    }

    @CacheEntryRemoved
    public void dataRemoved(CacheEntryRemovedEvent event) {
        if(event.isPre()) {
            this.print("Going to remove entry "+event.getKey()+" created in the cache");
        }else {
            print("Removed entry "+ event.getKey() + " from the cache");
        }
    }

    @CacheStarted
    public void cacheStarted(CacheStartedEvent event) {
        System.out.println("My address:"+event.getCacheManager().getAddress());
        for(Address address: event.getCacheManager().getMembers()) {
            System.out.println("Member In Cluster:" + address);
        }
        print("Cache started");
    }

    @CacheStopped
    public void cacheStopped(CacheStoppedEvent event) {
        print("Cache Stopped");
    }
}
