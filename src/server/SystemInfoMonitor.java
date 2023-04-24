package server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SystemInfoMonitor {
	
	private int nw, nr;
	private Lock e;
	private Condition okToRead;
	private Condition okToWrite;
	
	public SystemInfoMonitor() {
		nw = nr = 0;
		e = new ReentrantLock();
		okToRead = e.newCondition(); okToWrite = e.newCondition();
	}
	public void request_read() {};
	public void release_read() {};
	public void request_write() {};
	public void release_write() {};
//	public void request_read() {
//		e.lock();
//		while (nw > 0) {
//			try {
//				okToRead.await();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		nr++;
//		e.unlock();
//	}
//	
//	public void release_read() {
//		e.lock();
//		nr--;
//		if (nr == 0)
//			okToWrite.signal();
//		e.unlock();
//	}
//	
//	public void request_write() {
//		e.lock();
//		while (nw > 0 || nr > 0) {
//			try {
//				okToWrite.await();
//			} catch(InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		nw++;
//		e.unlock();
//	}
//	
//	public void release_write() {
//		e.lock();
//		nw--;
//		okToWrite.signal();
//		okToRead.signalAll();
//		e.unlock();
//	}
}
