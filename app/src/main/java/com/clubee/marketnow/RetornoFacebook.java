package com.clubee.marketnow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by barcat on 5/17/15.
 */

public class RetornoFacebook extends Activity
{

    private TextView textView;
    private MediaPlayer mp;

    public CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();


        setContentView(R.layout.retorno_facebook);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        String firstName = extras.getString("firstName");
        String lastName = extras.getString("lastName");
        String pic = extras.getString("picURI");

        textView = (TextView) findViewById(R.id.textviewNew);
        textView.setText("Olá, " + firstName + "!");
        setTitle(firstName + " " + lastName);
        new SetImageURI().execute(pic);

    }

    public void logout(View view){
        finish();
    }

    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void pictureClick(View v){
        mp.start();
    }

    private class SetImageURI extends AsyncTask<String, Void, Drawable> {

        @Override
        protected Drawable doInBackground(String... params) {
            Drawable dr = null;
            try {
                URL url = new URL(params[0]);
                InputStream img = (InputStream) url.getContent();
                dr = Drawable.createFromStream(img, "src");
            } catch (Exception e) {
                textView.setText("Uh oh! An error occurred: " + e.toString());
            }
            return dr;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            ImageButton imgView = (ImageButton) findViewById(R.id.imgView);
            imgView.setImageDrawable(result);
        }
    }
}