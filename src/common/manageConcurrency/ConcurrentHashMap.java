package common.manageConcurrency;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ConcurrentHashMap<K, V> extends HashMap<K, V>{
	
	private AccesManager accMan;
	
	public ConcurrentHashMap(AccesManager accMan) {
		this.accMan = accMan;
	}
	
	@Override
	public boolean containsKey(Object key) {
		accMan.requestRead();
		boolean val = super.containsKey(key);
		accMan.releaseRead();
		return val;
	}
	
	@Override
	public V get(Object key) {
		accMan.requestRead();
		V val = super.get(key);
		accMan.releaseRead();
		return val;
	}
	
	@Override
	public Collection<V> values() {
		accMan.requestRead();
		Collection<V> val = super.values();
		accMan.releaseRead();
		return val;
	}
	
	@Override
	public Set<K> keySet() {
		accMan.requestRead();
		Set<K> val = super.keySet();
		accMan.releaseRead();
		return val;
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		accMan.requestRead();
		Set<Entry<K,V>> val = super.entrySet();
		accMan.releaseRead();
		return val;
	}
	
	@Override
	public V remove(Object key) {
		accMan.requestWrite();
		V val = super.remove(key);
		accMan.releaseWrite();
		return val;
	}
	
	@Override
	public V put(K key, V value) {
		accMan.requestWrite();
		V val = super.put(key, value);
		accMan.releaseWrite();
		return val;
	}
}
