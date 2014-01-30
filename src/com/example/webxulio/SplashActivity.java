package com.example.webxulio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    
    private static String APP_TAG = "Xulio's";

    /*
     * TODO:
     * "La URL debe ser configurable."
     * "La app debe tener un campo de texto para metar la URL 
     * que se cargará. para entrar en esa pantalla de de configuración
     * se podría hacer algo así como tocar las 4 esquinas en un
     *  orden concreto o algo similar." 
     */
	private static final String URL = "http://www.google.com";
	private static final String POST_ERROR_URL = null;
	
	private boolean errors = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		final WebView myWebView = new WebView(this);
		myWebView.getSettings().setJavaScriptEnabled(true);

		myWebView.setWebViewClient(
				new WebViewClient(){
					@Override
					public void onReceivedError(WebView view, int errorCode,
							String description, String failingUrl) {
						super.onReceivedError(view, errorCode, description, failingUrl);
						errors = true;

                        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                        imageView.setImageResource(R.drawable.connection_error);

						Log.d(APP_TAG, "onReceivedError: " +
							"\n\terrorCode:" + errorCode +
							"\n\tdescription:" + description +
							"\n\tfailingUrl:" + failingUrl);

						postData(errorCode, description, failingUrl);
						
					}

					@Override
		            public void onPageFinished(WebView view, String url) {
					    Log.d(APP_TAG, "onPageFinished: " + url);
					    if(!errors){
					        setContentView(myWebView);
					    }
		            }
				});

		myWebView.loadUrl(URL);
	}

	public void postData(final int errorCode, final String description, final String failingUrl) {
	    if(POST_ERROR_URL == null)
	        return;

		(new Thread(){
			@Override
			public void run() {
				super.run();
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(POST_ERROR_URL);

			    try {
			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			        nameValuePairs.add(new BasicNameValuePair("errorCode", String.valueOf(errorCode)));
			        nameValuePairs.add(new BasicNameValuePair("description", description));
			        nameValuePairs.add(new BasicNameValuePair("failingUrl", failingUrl));
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			        HttpResponse response = httpclient.execute(httppost);
			    } catch (ClientProtocolException e) {
			        Log.d(APP_TAG, "ClientProtocolException posting data");
			    } catch (IOException e) {
			        Log.d(APP_TAG, "IOException posting data");
			    }
			}
			
		}).start();
	} 

}
