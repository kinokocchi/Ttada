package info.pinlab.ttada.core.cache;

public interface Remote {
	public boolean connect();
	public boolean isConnected();
	public void disconnect();

	public Exception loginApp();
	public Exception loginUsr();
	
}
