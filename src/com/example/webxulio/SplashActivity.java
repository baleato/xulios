package com.example.webxulio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.webxulio.utils.AppKeyHandler;

public class SplashActivity extends Activity {

    private static String APP_TAG = "Xulio's";

	private boolean errors = false;

    private AppKeyHandler appKeyHandler;

	private GestureDetectorCompat mDetector;

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        private final List<String> list = new ArrayList<String>();
        private final String[] key = {"A", "B", "C", "D"};
        private final float wShorted, hShorted;
        private final static int MARGIN = 90;

        public MyGestureListener(){
            Display display = getWindowManager().getDefaultDisplay();
            wShorted = display.getWidth() - MARGIN;
            hShorted = display.getHeight() - MARGIN;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            // 720 - 1183
            if(event.getY() < MARGIN){
                if(event.getX() < MARGIN){
                    list.add("A");
                }else if(event.getX() > wShorted){
                    list.add("B");
                }else{
                    list.clear();
                }
            }else if(event.getY() > hShorted){
                if(event.getX() < MARGIN){
                    list.add("C");
                }else if(event.getX() > wShorted){
                    list.add("D");
                }else{
                    list.clear();
                }
            }else{
                list.clear();
            }

            if(list.size() == key.length){
                if(list.equals(Arrays.asList(key))){
                    Intent intent = new Intent(
                            getApplicationContext().getApplicationContext(),
                            EditURLsActivity.class);
                    startActivity(intent);
                    list.clear();
                }else{
                    list.remove(0);
                }
            }

            Log.d(DEBUG_TAG,"onDown: " +
                    list.toString() + "-" + event.getX() + " - " + event.getY());
            return true;
        }

    }

    private WebView myWebView;
    private String loadURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		myWebView = new WebView(this);
		myWebView.getSettings().setJavaScriptEnabled(true);
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());

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

		myWebView.setOnTouchListener(new OnTouchListener() {

		    @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return false;
            }
        });

		appKeyHandler = new AppKeyHandler(this);
		loadURL = appKeyHandler.getURL();
		myWebView.loadUrl(loadURL);
	}

    @Override
    protected void onResume() {
        super.onResume();
        if(!appKeyHandler.getURL().equals(loadURL)){
            loadURL = appKeyHandler.getURL();
            myWebView.loadUrl(loadURL);
        }
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
