package info.pinlab.ttada.core.cache;

import info.pinlab.ttada.core.ser.SimpleJsonSerializer;

public interface RemoteCacheBuilder {
	public abstract RemoteCacheBuilder setScheme(String scheme);

	public abstract RemoteCacheBuilder setHost(String host);

	public abstract RemoteCacheBuilder setPort(int port);

	public abstract RemoteCacheBuilder setPingPath(String pingPath);

	public abstract RemoteCacheBuilder setAppPingPath(String appPingPath);

	public abstract RemoteCacheBuilder setLoginPath(String loginPath);

	public abstract RemoteCacheBuilder setLoginId(String loginId);

	public abstract RemoteCacheBuilder setLoginPwd(String loginPwd);

	public abstract RemoteCacheBuilder setRestRoot(String restRoot);

	public abstract RemoteCacheBuilder setSerializer(
			SimpleJsonSerializer jsonSerializer);

	public abstract RemoteCache build();
}
