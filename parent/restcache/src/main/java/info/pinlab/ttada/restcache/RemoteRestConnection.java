package info.pinlab.ttada.restcache;


import info.pinlab.utils.FileStringTools;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
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
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class RemoteRestConnection {
	public static Logger logger = Logger.getLogger(RemoteRestConnection.class);

	private final String scheme, host;
	private final int port;
	private final String loginPath, loginId, loginPwd; 
	private final String restRoot;

	private final HttpContext httpContext;
	private final HttpClient httpClient;
	private static final String CSRF_TOKEN = "csrftoken";
	private String csrf_val = null;


	
	static class RemoteRestConnectionBuilder{
		String scheme;
		String host;
		int port;
		String loginPath;
		String loginId;
		String loginPwd;
		String restRoot;
		public RemoteRestConnectionBuilder setScheme(String scheme) {
			this.scheme = scheme;
			return this;
		}
		public RemoteRestConnectionBuilder setHost(String host) {
			this.host = host;
			return this;
		}
		public RemoteRestConnectionBuilder setPort(int port) {
			this.port = port;
			return this;
		}
		public RemoteRestConnectionBuilder setLoginPath(String loginPath) {
			this.loginPath = loginPath;
			return this;
		}
		public RemoteRestConnectionBuilder setLoginId(String loginId) {
			this.loginId = loginId;
			return this;
		}
		public RemoteRestConnectionBuilder setLoginPwd(String loginPwd) {
			this.loginPwd = loginPwd;
			return this;
		}
		public RemoteRestConnectionBuilder setRestRoot(String restRoot) {
			this.restRoot = restRoot;
			return this;
		}
		public RemoteRestConnection build(){
			return new RemoteRestConnection(scheme, host, port, loginPath, loginId, loginPwd, restRoot);
		}
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
	private RemoteRestConnection(String scheme, String host, int port,
			String loginPath, String loginId, String loginPwd, String restRoot) {
		super();
		this.scheme = scheme;
		this.host = host;
		this.port = port;
		this.loginPath = loginPath;
		this.loginId = loginId;
		this.loginPwd = loginPwd;
		this.restRoot = restRoot;

		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
		
//		httpClient = HttpClients.createMinimal();	//	httpClient = new DefaultHttpClient();
		
		
			
		HttpClientBuilder clientBuilder = HttpClients.custom();
		
		HttpClientConnectionManager connManager = null;
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setCharset(Consts.UTF_8)
				.build();
		clientBuilder.setConnectionManager(connManager);
		httpClient = clientBuilder.build();
		
		
//		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
//		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, 60*1000L);
//		this.serverRoot = scheme +"://"+ host + ":" + port;
	}



	public boolean login() throws URISyntaxException, IllegalStateException, ClientProtocolException, IOException{
		if(this.loginId != null && this.loginPwd !=  null){
			HttpPost httpPost = null; 
//			HttpGet httpGet = null;
			HttpResponse httpResp = null;
			URI uri = new URIBuilder()
			.setScheme	(this.scheme)
			.setHost	(this.host)
			.setPath	(this.loginPath)
			.build();

//			httpGet = new HttpGet(uri);
			httpPost = new HttpPost(uri);
//			httpGet.setHeader(this.loginId, this.loginPwd);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("login-id", this.loginId));
			nameValuePairs.add(new BasicNameValuePair("login-pwd", this.loginPwd));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			try{
//				httpResp = httpClient.execute(httpGet, httpContext);
				httpResp = httpClient.execute(httpPost, httpContext);
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
//				httpGet.reset();//-- very important!
			}

			int errCode = httpResp.getStatusLine().getStatusCode();
			if((errCode != 200)){
	        	logger.error("Http Error code '" + errCode  + "'");
	            HttpEntity entity = httpResp.getEntity();
	            String errMsg = "";
	            if (entity != null) {
	            	try {
						EntityUtils.consume(entity);
						errMsg = FileStringTools.getStreamAsString(entity.getContent());
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }else{
	            	errMsg = "";
	            }
				throw new IllegalStateException("Couldn't connect login to server '"+ host +"'." + "Err : '" + errMsg +"'");
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
		return false;
	}



	private class PostWorker implements Runnable{
		private final URI uri;
		private final HttpPost httpPost;
		private final String json;

		PostWorker(String json) throws URISyntaxException{
			uri = new URIBuilder()
			.setScheme		(RemoteRestConnection.this.scheme)
			.setHost		(RemoteRestConnection.this.host)
			.setPath		(RemoteRestConnection.this.restRoot)
			.build();
			this.json = json;
			httpPost = new HttpPost(uri);
			httpPost.setEntity(
					new StringEntity(json, ContentType.create("application/json", "UTF-8"))
				);
		}

		@Override
		public void run() {
			try {
				httpClient.execute(httpPost, httpContext);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}





}
