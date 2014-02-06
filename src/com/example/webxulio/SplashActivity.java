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

import com.example.webxulio.utils.AppKeyHandler;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    
    private static String APP_TAG = "Xulio's";

	private boolean errors = false;

    private AppKeyHandler appKeyHandler;

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

		appKeyHandler = new AppKeyHandler(this);
		myWebView.loadUrl(appKeyHandler.getURL());
	}

	public void postData(final int errorCode, final String description, final String failingUrl) {
	    final String postURL = appKeyHandler.getErrorPostURL();
	    if(TextUtils.isEmpty(postURL))
	        return;

		(new Thread(){
			@Override
			public void run() {
				super.run();
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(postURL); 

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
