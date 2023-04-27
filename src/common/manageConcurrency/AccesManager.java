package common.manageConcurrency;

public interface AccesManager {
	public void requestRead();
	public void releaseRead();
	public void requestWrite();
	public void releaseWrite();
}
