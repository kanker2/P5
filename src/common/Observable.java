package common;

import java.util.ArrayList;

public class Observable {
	
	ArrayList<Observer> observers;
	
	public Observable() {
		observers = new ArrayList<>();
	}
	
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void notifyObservers(Object arg) {
		for(Observer o : observers) 
			o.update(this, arg);
	}
}
