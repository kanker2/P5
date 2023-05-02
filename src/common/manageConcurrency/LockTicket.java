package common.manageConcurrency;

import java.util.concurrent.Semaphore;

//Esta clase unicamente la empleamos como mutex sobre el generador de IDs del Server y para cubrir los fin y fout de StreamProxy Monitor

public class LockTicket {

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
