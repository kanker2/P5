package common.manageConcurrency;

import java.util.concurrent.Semaphore;

public class SemaphoreAccesManager implements AccesManager{

	private Semaphore e, w, r;
	private int nw, nr, dw, dr;
	
	public SemaphoreAccesManager() {
		e = new Semaphore(1);
		w = new Semaphore(0);
		r = new Semaphore(0);
		
		nw = nr = dw = dr = 0;
	}
	
	private void signal() {
		if (nw == 0 && dr > 0) {
			dr--;
			r.release();
		}
		else if (nw == 0 && nr == 0 && dw > 0) {
			dw--;
			w.release();
		}
		else
			e.release();
	}
	
	@Override
	public void requestRead() {
		try {
			e.acquire();
			if (nw > 0) {
				dr++;
				e.release();
				r.acquire();
			}
			nr++;
			signal();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void releaseRead() {
		try {
			e.acquire();
			nr--;
			signal();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void requestWrite() {
		try {
			e.acquire();
			if (nw > 0 || nr > 0)
			{
				dw++;
				e.release();
				w.acquire();
			}
			nw++;
			signal();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void releaseWrite() {
		try {
			e.acquire();
			nw--;
			signal();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

}
