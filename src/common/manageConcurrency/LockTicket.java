package common.manageConcurrency;

import java.util.concurrent.Semaphore;

public class LockTicket implements Lock{

	private Semaphore numberMutex;
	private Integer number;
	private Integer next;
	
	public LockTicket() {
		numberMutex = new Semaphore(1);
		next = number = 0;
	}
	
	public void lock() {
		int myTurn;
		try {
		numberMutex.acquire();
		myTurn = number;
		number++;
		numberMutex.release();
		
		while(next != myTurn);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void unlock() {
		next++;
	}
}
