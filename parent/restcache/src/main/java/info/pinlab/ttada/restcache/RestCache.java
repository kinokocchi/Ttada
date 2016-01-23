package info.pinlab.ttada.restcache;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import info.pinlab.ttada.core.cache.CacheLevel;
import info.pinlab.ttada.core.cache.Pointer;
import info.pinlab.ttada.core.cache.RemoteCache;
import info.pinlab.ttada.core.cache.RemoteCacheBuilder;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;


public class RestCache implements RemoteCache {
	public static Logger logger = Logger.getLogger(RestCache.class);
	private final String scheme, host, restRoot;
	private final int port;
	private final String loginPath, loginId, loginPwd, pingPath, appPingPath; 

	private final HttpContext httpContext;
	private final HttpClient httpClient;
	private static final String CSRF_TOKEN = "csrftoken";
	private String csrf_val = null;
	private SimpleJsonSerializer jsonSerializer = null;
//	private JsonAdapterIF jsonSerializer = null;
	
	private static final CacheLevel level = new RestCacheLevel();
	private static final class RestCacheLevel implements CacheLevel{
		private static final String levelTag = "rest";
		@Override
		public String getPrefix() {
			return levelTag;
		}
	}
	
	public static CacheLevel getCacheLevel(){
		return level;
	}

	
	
	
	public static class RestCacheBuilder implements RemoteCacheBuilder{
		private String scheme = "http";
		private String host = "localhost";
		private int port = 80;
		private String loginPath = "";
		private String loginId = "";
		private String loginPwd = "";
		private String restRoot = "/rest/";
		
		private String pingPath = "/ping/";
		String appPingPath = "/app-ping/";

		private SimpleJsonSerializer jsonSerializer = null;

		
		public RestCacheBuilder(){}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setScheme(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setScheme(String scheme) {
			this.scheme = scheme;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setHost(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setHost(String host) {
			this.host = host;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setPort(int)
		 */
		@Override
		public RestCacheBuilder setPort(int port) {
			this.port = port;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setPingPath(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setPingPath(String pingPath) {
			this.pingPath = pingPath;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setAppPingPath(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setAppPingPath(String appPingPath) {
			this.appPingPath = appPingPath;
			return this;
		};
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setLoginPath(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setLoginPath(String loginPath) {
			this.loginPath = loginPath;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setLoginId(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setLoginId(String loginId) {
			this.loginId = loginId;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setLoginPwd(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setLoginPwd(String loginPwd) {
			this.loginPwd = loginPwd;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setRestRoot(java.lang.String)
		 */
		@Override
		public RestCacheBuilder setRestRoot(String restRoot) {
			this.restRoot = restRoot;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#setSerializer(info.pinlab.ttada.core.ser.SimpleJsonSerializer)
		 */
		@Override
		public RestCacheBuilder setSerializer(SimpleJsonSerializer jsonSerializer) {
			this.jsonSerializer = jsonSerializer;
			return this;
		}
		
		
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.RestCacheBuilder#build()
		 */
		@Override
		public RestCache build() {
			return new RestCache(scheme, host, port, loginPath, loginId, loginPwd, restRoot, pingPath, appPingPath, jsonSerializer);
		};
	}
	
	/**
	 * @param scheme  http most usually
	 * @param host	hostname of appserver
	 * @param port port of the appserver
	 * @param loginPath  path to login
	 * @param id
	 * @param pwd
	 * @param restRoot  root path for rest resources
	 * @throws IllegalArgumentException
	 * @throws URISyntaxException
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private RestCache(String scheme, String host, int port,
			String loginPath, String loginId, String loginPwd, String restRoot, String pingPath, String appPingPath, SimpleJsonSerializer adapter) {
		super();
		this.scheme = scheme;
		this.host = host;
		this.port = port;
		//-- paths make starts/ends with '/'
		if(!loginPath.startsWith("/")){ loginPath = "/" + loginPath; }
		if(!loginPath.endsWith("/")){ loginPath =  loginPath + "/" ; }
		this.loginPath = loginPath; 
		this.loginId = loginId;
		this.loginPwd = loginPwd;
		
		//-- make sure restRoot does NOT end with '/' (but it starts with one)
		if(!restRoot.startsWith("/")){ restRoot = "/" + restRoot; }
		if(restRoot.endsWith("/")){restRoot = restRoot.substring(0, restRoot.length()-1);} 
		this.restRoot = restRoot;

		if(!pingPath.startsWith("/")){ pingPath = "/" + pingPath; }
		if(!pingPath.endsWith("/")){ pingPath =  pingPath + "/" ; }
		this.pingPath = pingPath;
		
		//-- correct form: starts/ends with '/' e.g., "/ping/" 
		if(!appPingPath.startsWith("/")){ appPingPath = "/" + appPingPath; }
		if(!appPingPath.endsWith("/")){ appPingPath =  appPingPath + "/" ; }
		this.appPingPath = appPingPath ;
		
		this.jsonSerializer = adapter;
		
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
		
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, 60*1000L);
//		this.serverRoot = scheme +"://"+ host + ":" + port;
	}
	
	
	
	@Override
	public <T> Pointer put(T obj, Class<T> clazz) {
		HttpResponse httpResp = null;
		String uri = this.scheme +"://" + this.host + ":" + this.port + this.restRoot + "/" + obj.getClass().getSimpleName().toLowerCase() + "/"  + obj.hashCode() +"/";
		HttpPut httpPut = new HttpPut(uri);
		httpPut.addHeader("X-CSRFToken", csrf_val);
		httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
		
		
		final String json =  jsonSerializer.toJson(obj, clazz);  
		httpPut.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
		Pointer returnPtr = null;
		
		try {
			logger.debug("Inserting DB data '"+ uri +"'");
			httpResp = httpClient.execute(httpPut, httpContext);
			String returnVal = EntityUtils.toString(httpResp.getEntity());
			logger.debug("PUT response return value '"+ returnVal +"'");
			returnPtr = new Pointer(returnVal);
		} catch (ClientProtocolException e) {
//			e.printStackTrace();
			return null;
		}catch (HttpHostConnectException e){
//			e.printStackTrace();
			return null;
		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		}finally{
			httpPut.reset();
		}
		if(httpResp.getStatusLine().getStatusCode()!=200){
			logger.error("Failed to insert to DB '"+ uri +"'");
			return null;
		}
		return returnPtr;
	}

	@Override
	public <T> T get(Pointer ptr, Class<T> clazz) {
		return null;
	}

	@Override
	public <T> void remove(Pointer ptr, Class<T> clazz) {
	}

	@Override
	public CacheLevel getLevel() {
		return level;
	}

	@Override
	public boolean connect() {
		if(isConnected()){
			return true;
		}
		
		
		try {
			final boolean loginstat = login();
			return loginstat;
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}
	
	
	
	
	synchronized private boolean login() throws IllegalStateException, ClientProtocolException, IOException{
		if(this.loginId == null){
			logger.error("login id is missing!");
			return false;
		}else if (this.loginPwd ==  null){
			logger.error("login pwd is missing!");
			return false;
		}else{
			HttpPost httpPost = null; 
			HttpResponse httpResp = null;
			
			
			httpPost = new HttpPost(this.scheme +"://" + this.host + ":" + this.port + this.loginPath);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("login-id", this.loginId));
			nameValuePairs.add(new BasicNameValuePair("login-pwd", this.loginPwd));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			try{
				httpResp = httpClient.execute(httpPost, httpContext);
				int errCode = httpResp.getStatusLine().getStatusCode();
				HttpEntity entity = httpResp.getEntity();
				if((errCode != 200)){
					String errMsg ="";
					if (entity != null) {
						try {
//							EntityUtils.consume(entity);
							InputStream is = entity.getContent();
							if(is != null){
//								errMsg = FileStringTools.getStreamAsString(is);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
		        	logger.error("Http Error code '" + errCode  + "'");
					throw new IllegalStateException("Couldn't login to server '"+ host +"'. " + "Err : '" + errMsg +"'");
		        }
				
				
			}catch (ClientProtocolException e) {
				csrf_val = null;
				throw new ClientProtocolException("ClientProtocolException: Couldn't connect to server '"+ host +"' to login. "+  e.getMessage());
			}catch (IOException e) {
				csrf_val = null;
				throw new  IOException("IOException: Couldn't connect to server '"+ host +"' to login. " +  e.getMessage());
			}catch (IllegalStateException e){
				csrf_val = null;
				throw new IllegalStateException("IllegalStateException: Couldn't connect to server '"+ host +"' to login. " +  e.getMessage());
			}finally{
//				httpResp.getEntity();
				httpPost.reset(); //-- very important!
			}
			

			Header[] hdrs = httpResp.getHeaders("Set-Cookie");
			for(Header h : hdrs){
				final String val = h.getValue();
				final int colIx = val.indexOf(";");
				if (colIx > 0){
					final String keyval = val.substring(0,colIx);
					final int eqIx = keyval.indexOf("=");
					if(eqIx > 0){
						String token = keyval.substring(0,eqIx).toLowerCase();
						System.out.println("-->HEADER '" + val + "' :: '" + token +"'");
						if(CSRF_TOKEN.equals(token)){
							csrf_val = keyval.substring(eqIx+1);
							logger.debug("CSRF value was set to '"+ csrf_val +"'");
							return true;
						}
					}
				}
			}
			return true;
		}
	}

	
	
	

	private String getAppPingUri(){
		return this.scheme +"://" + this.host + ":" + this.port + this.appPingPath;
	}
	
	@Override
	/**
	 *  Pings server
	 */
	synchronized public boolean isConnected(){
		HttpResponse httpResp = null;
		final HttpPost httpPost =  new HttpPost(getAppPingUri());
				//this.scheme +"://" + this.host + ":" + this.port + this.appPingPath
		try {
			httpPost.addHeader("X-CSRFToken", csrf_val);
			httpResp = httpClient.execute(httpPost, httpContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		}catch(HttpHostConnectException e){
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally{
			if(httpPost!=null){
				httpPost.reset();
			}
		}
		
		if(httpResp!=null){
			int errCode = httpResp.getStatusLine().getStatusCode();
			if(errCode == 200){
				return true;
			}
		}
		

		//-- failed app-ping -> try normal ping 
		final HttpGet httpGet = new HttpGet(this.scheme +"://" + this.host + ":" + this.port + "/" + this.pingPath);
		System.out.println("------------------------- " + httpGet.getURI());
		try {
			httpResp = httpClient.execute(httpGet);
			if(httpResp != null && 200 == httpResp.getStatusLine().getStatusCode()){
				logger.info("Normal ping is working fine! Server is not down!");
			}
		} catch (ClientProtocolException e) {
			logger.error("The app couldn't ping server! " +  this.host  +" (ClientProtocolException) " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			logger.error("The app couldn't ping server! " +  this.host + " (IOException) " + e.getMessage());
			e.printStackTrace();
			return false;
		}finally{
			httpGet.reset();
		}
		return false;
	}

	
	
	@Override
	public void disconnect() {
	}

	@Override
	public void setResourceLink(String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResourceLink() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPendingTransactionN() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String getRemoteUri() {
		return this.host;
	}
	
	
	
	

}
