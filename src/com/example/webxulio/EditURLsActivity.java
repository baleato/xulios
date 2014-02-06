package com.example.webxulio;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.webxulio.utils.AppKeyHandler;

public class EditURLsActivity extends Activity {
    private EditText editTextURL;
    private EditText editTextURLPost;
    private AppKeyHandler appKeyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_urls);
        appKeyHandler = new AppKeyHandler(this);

        editTextURL = (EditText) findViewById(R.id.editTextUrl);
        editTextURLPost = (EditText) findViewById(R.id.editTextUrlPost);
        
        editTextURL.setText(appKeyHandler.getURL());

        editTextURLPost.setText(appKeyHandler.getErrorPostURL());

        findViewById(R.id.btnSave)
            .setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG", "storing url: " + editTextURL.getText().toString());
                    Log.d("DEBUG", "storing post url: " + editTextURLPost.getText().toString());
                    appKeyHandler.setURL(
                            editTextURL.getText().toString());
                    appKeyHandler.setErrorPostURL(
                            editTextURLPost.getText().toString());
                    
                    finish();
                }
            });
    }
}
