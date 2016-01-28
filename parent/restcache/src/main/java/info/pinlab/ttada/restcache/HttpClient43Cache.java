package info.pinlab.ttada.restcache;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.ttada.core.cache.CacheLevel;
import info.pinlab.ttada.core.cache.Pointer;
import info.pinlab.ttada.core.cache.RemoteCache;
import info.pinlab.ttada.core.cache.RemoteCacheBuilder;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseHeader;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;
import info.pinlab.utils.FileStringTools;

/**
 * 
 * HttpClient 4.3.x version implementation of rest cache. 
 * There was a big API change between 4.2 > 4.3
 * 
 * @author Gabor Pinter
 *
 */
public class HttpClient43Cache implements RemoteCache, CacheLevel{
	public static Logger LOG = LoggerFactory.getLogger(RemoteCache.class);

	private final String scheme, host, restRoot;
	private final int port;
	private final String loginPath, loginId, loginPwd, pingPath, appPingPath; 

	private static final String CSRF_TOKEN = "csrftoken";
	private String csrf_val = null;
	private SimpleJsonSerializer jsonSerializer = null;

	public int TIMEOUT_IN_SEC = 5; 

	public static class HttpClient43CacheBuilder implements RemoteCacheBuilder{
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


		public HttpClient43CacheBuilder(){}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setScheme(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setScheme(String scheme) {
			this.scheme = scheme;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setHost(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setHost(String host) {
			this.host = host;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setPort(int)
		 */
		@Override
		public HttpClient43CacheBuilder setPort(int port) {
			this.port = port;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setPingPath(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setPingPath(String pingPath) {
			this.pingPath = pingPath;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setAppPingPath(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setAppPingPath(String appPingPath) {
			this.appPingPath = appPingPath;
			return this;
		};
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setLoginPath(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setLoginPath(String loginPath) {
			this.loginPath = loginPath;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setLoginId(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setLoginId(String loginId) {
			this.loginId = loginId;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setLoginPwd(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setLoginPwd(String loginPwd) {
			this.loginPwd = loginPwd;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setRestRoot(java.lang.String)
		 */
		@Override
		public HttpClient43CacheBuilder setRestRoot(String restRoot) {
			this.restRoot = restRoot;
			return this;
		}
		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#setSerializer(info.pinlab.ttada.core.ser.SimpleJsonSerializer)
		 */
		@Override
		public HttpClient43CacheBuilder setSerializer(SimpleJsonSerializer jsonSerializer) {
			this.jsonSerializer = jsonSerializer;
			return this;
		}


		/* (non-Javadoc)
		 * @see info.pinlab.ttada.restcache.HttpClient43CacheBuilder#build()
		 */
		@Override
		public HttpClient43Cache build() {
			return new HttpClient43Cache(scheme, host, port, loginPath, loginId, loginPwd, restRoot, pingPath, appPingPath, jsonSerializer);
		};
	}

	private HttpClientContext httpContext;



	private static final CacheLevel level = new HttpClient43CacheLevel();
	private static final class HttpClient43CacheLevel implements CacheLevel{
		private static final String levelTag = "rest";
		@Override
		public String getPrefix() {
			return levelTag;
		}
	}
	
	public static CacheLevel getCacheLevel(){
		return level;
	}

	@Override
	public String getPrefix() {
		return HttpClient43CacheLevel.levelTag;
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
	private HttpClient43Cache(String scheme, String host, int port,
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


		//-- init connection manager
		connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(5);  //-- max # of  open connections
		connManager.setDefaultMaxPerRoute(3); //-- max # of concurrent connections


		reqConfig = RequestConfig.custom()
				.setConnectTimeout(TIMEOUT_IN_SEC*1000)
				.setSocketTimeout(TIMEOUT_IN_SEC*1000)
				.build();


		
		httpContext = HttpClientContext.create();
		httpContext.setCookieStore(new BasicCookieStore());
		
		
		defaultHeaders = new ArrayList<Header>();
		defaultHeaders.add(new BasicHeader("Accept-Charset", "utf-8"));
		defaultHeaders.add(new BasicHeader(HTTP.CONTENT_ENCODING, "utf-8"));



		loginClient = HttpClientBuilder
				.create()
				.setDefaultRequestConfig(reqConfig)
				.setConnectionManager(connManager)
				.setDefaultHeaders(defaultHeaders)
				.build();

		URI loginUriTmp = null; 
		try{
			loginUriTmp = new URIBuilder()
			.setScheme	(this.scheme)
			.setHost	(this.host)
			.setPath	(this.loginPath)
			.setPort	(this.port)
			.build();
		} catch (URISyntaxException ignore){}
		loginUri = loginUriTmp;
	}

	private final PoolingHttpClientConnectionManager connManager;
	private final RequestConfig reqConfig;
	private CloseableHttpClient loginClient, dataClient;
	private List<Header> defaultHeaders ;
	private final URI loginUri ;


	
	static private void closeResponse(CloseableHttpResponse resp){
		if(resp==null)
			return;
		try {
			resp.close();
		} catch (IOException ignore) {		}
	};
	
	
	/**
	 * Logs in to remote server and gets csrf token.
	 * 
	 * @return
	 * @throws IllegalStateException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Override
	synchronized public Exception loginApp(){
		HttpPost httpPost = new HttpPost(loginUri);
		
//		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login-id", this.loginId));
		nameValuePairs.add(new BasicNameValuePair("login-pwd", this.loginPwd));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Charset.forName("UTF-8")));
		
		CloseableHttpResponse loginResp = null;
		try{
			LOG.info("Attempting to login to '"+ httpPost.getRequestLine().getUri() +"'");
		    loginResp = loginClient.execute(httpPost, httpContext);
			final int statusCode = loginResp.getStatusLine().getStatusCode();
			LOG.info("status: " + statusCode );			
			
			if((statusCode != 200)){
				LOG.error("Http Error code '" + statusCode  + "'");
				HttpEntity entity = loginResp.getEntity();
				String errMsg = "";
				if (entity != null) {
					try {
						errMsg = FileStringTools.getStreamAsString(entity.getContent());
						EntityUtils.consume(entity);
						throw new IllegalStateException("Couldn't connect login to server '"+ host +"'." + "Err : '" + errMsg +"'");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					errMsg = "";
				}
			}
		}catch (SocketTimeoutException e){
			LOG.error("Socket timed out to '" + loginUri.toString());
			closeResponse(loginResp);
			return new IllegalStateException("Couldn't connect login to server '"+ host +"'." + "Err : '" + "Socket Timed out!"  +"'");
		}catch (ConnectTimeoutException e){
			LOG.error("Connection timed out to '" + loginUri.toString());
			closeResponse(loginResp);
			return new IllegalStateException("Couldn't connect login to server '"+ host +"'." + "Err : '" + "Connection Timed out!"  +"'");
		}catch (IOException e){
			LOG.error("IOException : " + e.getMessage());			
			closeResponse(loginResp);
			return e;
		}
		
		Header[] hdrs = loginResp.getHeaders("Set-Cookie");
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
						LOG.debug("CSRF value was set to '"+ csrf_val +"'");
					}
				}
			}
		}

		assert(connManager.getTotalStats().getLeased() == 1);
//		httpPost.releaseConnection();

		//-- create httpClient for data transfer
		
//		defaultHeaders = new ArrayList<Header>();
		defaultHeaders.add(new BasicHeader("Accept-Charset", "utf-8"));
		defaultHeaders.add(new BasicHeader("X-CSRFToken", csrf_val));
		defaultHeaders.add(new BasicHeader(HTTP.CONTENT_TYPE, "application/json")); //-- from now own

		//		defaultHeaders.add(new BasicHeader(HTTP.CONTENT_ENCODING, "utf8"));
//		defaultHeaders.add(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		
		dataClient = HttpClientBuilder
				.create()
				.setDefaultRequestConfig(reqConfig)
				.setConnectionManager(connManager)
				.setDefaultHeaders(defaultHeaders)
				.build()
				;
		return null;
	}

	
	
	@Override
	synchronized public Exception loginUsr(){
		//TODO: implement user ID!
		return null;
	}
	
	
	
	
	public String getLoginUri(){
		if(loginUri==null){
			return "";
		}else{
			return loginUri.toString();
		}
	}
	
	@Override
	public <T> Pointer put(T obj, Class<T> clazz) {
		HttpResponse httpResp = null;
		String uri = this.scheme +"://" + this.host + ":" + this.port + this.restRoot + "/" + obj.getClass().getSimpleName().toLowerCase() + "/"  + obj.hashCode() +"/";
		
		HttpPut httpPut = new HttpPut(uri);
		
		final String json =  jsonSerializer.toJson(obj, clazz);  
		httpPut.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
		Pointer returnPtr = null;
		
//		httpContext = new BasicHttpContext();
//		httpContext.setAttribute(id, httpContext);
		try {
			LOG.debug("Inserting DB data '"+ uri +"'");
			httpResp = dataClient.execute(httpPut, httpContext);
			String returnVal = EntityUtils.toString(httpResp.getEntity());
			LOG.debug("PUT response return value '"+ returnVal +"'");
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
//			httpPut.reset();
		}
		if(httpResp.getStatusLine().getStatusCode()!=200){
			LOG.error("Failed to insert to DB '"+ uri +"'");
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
		// TODO Auto-generated method stub

	}

	@Override
	public CacheLevel getLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean connect() {
		if(isConnected()){
			return true;
		}
		
		try {
			final Exception loginstat = loginApp();
			return loginstat==null;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	@Override
	/**
	 *  Pings server
	 */
	synchronized public boolean isConnected(){
		if (csrf_val == null){
			return false;
		}
		HttpResponse httpResp = null;
		final HttpPost httpPost =  new HttpPost(this.scheme +"://" + this.host + ":" + this.port + this.appPingPath);

		
		try {
			httpPost.addHeader("X-CSRFToken", csrf_val);
			httpResp = loginClient.execute(httpPost, httpContext);
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
			httpResp = loginClient.execute(httpGet);
			if(httpResp != null && 200 == httpResp.getStatusLine().getStatusCode()){
				LOG.info("Normal ping is working fine! Server is not down!");
			}
		} catch (ClientProtocolException e) {
			LOG.error("The app couldn't ping server! " +  this.host  +" (ClientProtocolException) " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			LOG.error("The app couldn't ping server! " +  this.host + " (IOException) " + e.getMessage());
			e.printStackTrace();
			return false;
		}finally{
			httpGet.reset();
		}
		return false;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

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
	
	
	
	
	public static void main(String [] args) throws Exception{
		
		SimpleJsonSerializer gson = new SimpleGsonSerializerFactory().build();
		
		HttpClient43Cache client = new HttpClient43CacheBuilder()
				.setScheme("http")
				.setHost("pinlab.info")
				.setLoginPath("django/app-login")
				.setLoginId("pinplayer-app")
				.setLoginPwd("wrong pwd")
				
				.setRestRoot("django/rest/")
				.setLoginPwd("wrong pwd")

				.setSerializer(gson)

				
				.setHost("localhost")
				.setRestRoot("echo")
				.setPort(8000)
				.build();
		
		if(client.loginApp()==null){
			System.out.println("Login OK!");
		}else{
			System.out.println("Login ERROR!");
			return;
		}
		ResponseHeader hdr = new ResponseHeaderBuilder().build();
		ResponseContent content = new ResponseContentText(System.currentTimeMillis(), 1234, "HttpClient 4.3.x testing");
		Response resp = new Response(hdr, content);
		
		client.put(resp, Response.class);
	}


}
