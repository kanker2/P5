package common.manageConcurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorAccesManager implements AccesManager {
	
	private int nw, nr;
	private Lock e;
	private Condition okToRead;
	private Condition okToWrite;
	
	public MonitorAccesManager() {
		nw = nr = 0;
		e = new ReentrantLock();
		okToRead = e.newCondition(); okToWrite = e.newCondition();
	}

	public void requestRead() {
		e.lock();
		while (nw > 0) {
			try {
				okToRead.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nr++;
		e.unlock();
	}
	
	public void releaseRead() {
		e.lock();
		nr--;
		if (nr == 0)
			okToWrite.signal();
		e.unlock();
	}
	
	public void requestWrite() {
		e.lock();
		while (nw > 0 || nr > 0) {
			try {
				okToWrite.await();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		nw++;
		e.unlock();
	}
	
	public void releaseWrite() {
		e.lock();
		nw--;
		okToWrite.signal();
		okToRead.signalAll();
		e.unlock();
	}
}
