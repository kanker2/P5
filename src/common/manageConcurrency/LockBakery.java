package common.manageConcurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class LockBakery implements Lock{

	private volatile Map<Integer, Integer> turns;
	private Semaphore idGenMutex;
	private Integer idGen;
	
	public LockBakery() {
		idGenMutex = new Semaphore(1);
		idGen = 0;
	}
	
	private boolean pairLower(int a, int b, int c, int d) {
		if (a < c)
			return true;
		else if (a == c && b < d)
			return true;
		else return false;
	}
	
	private Integer genId() {
		
	}
	
	@Override
	public void lock() {
		int max = 0;
		
		turns = turns;
		for (Integer integer : turns)
			if (max < integer)
				max = integer;
		turns.set(i, max + 1);
		turns = turns;
		for(int j = 0; j < turns.size(); j++)
			while (i != j && turns.get(j) > 0 && pairLower(turns.get(j), j, turns.get(i), i));
	}

	@Override
	public void unlock() {
		turns.set(i, 0);
		turns = turns;
	}
}
