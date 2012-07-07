/*
 * Copyright 2012 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.component.output.cache;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Date;
import java.util.Map;

/**
 * Base class that can be used by Map based caches that don't support time to live semantics natively.
 * 
 * @since 1.1
 * @author Arjan Tijms
 *
 */
public abstract class TimeToLiveCache implements Cache {

	private final Integer defaultTimeToLive;
	private final Map<String, Object> cacheStore;
	
	public TimeToLiveCache(Integer defaultTimeToLive, Integer maxCapacity) {
		this.defaultTimeToLive = defaultTimeToLive;
		cacheStore = createCacheStore(maxCapacity);
	}

	@Override
	public String get(String key) {
		Object value = cacheStore.get(key);
		
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof CacheEntry) {
			CacheEntry entry = (CacheEntry) value;
			if (entry.isValid()) {
				return entry.getValue();
			} else {
				cacheStore.remove(key);
			}
		}
		
		return null;
	}

	@Override
	public void put(String key, String value) {
		if (defaultTimeToLive != null) {
			put(key, value, defaultTimeToLive);
		} else {
			cacheStore.put(key, value);
		}
	}
	
	@Override
	public void put(String key, String value, int timeToLive) {
		cacheStore.put(key, new CacheEntry(value, new Date(currentTimeMillis() + SECONDS.toMillis(timeToLive))));
	}
	
	protected abstract Map<String, Object> createCacheStore(Integer maxCapacity);
	
	
	
}