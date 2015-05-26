package com.clubee.marketnow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends Activity {

    private TextView mainTextView;
    private static final int request_code = 5;
    public CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        mainTextView = (TextView) findViewById(R.id.lol);
        loginButton.setReadPermissions("user_friends");
        LoginManager.getInstance().logOut(); //ensures that whenever we start the app we're logged out

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Profile curProfile = Profile.getCurrentProfile();
                        startNewIntent(curProfile);

                        new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "118940821555244/feed",
                                null,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                response.getJSONObject()
                                                        + "",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                        ).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        mainTextView.setText("Status: Logging in cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        mainTextView.setText("Status: EXCEPTION: " + exception.toString());
                    }
                });
    }

    private void startNewIntent(Profile elUsero){
        Intent i = new Intent(this, RetornoFacebook.class);

        i.putExtra("firstName", elUsero.getFirstName());
        i.putExtra("lastName", elUsero.getLastName());
        i.putExtra("picURI", elUsero.getProfilePictureUri(1500,1500).toString());
        startActivityForResult(i, request_code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == request_code) && (resultCode == RESULT_OK)) {
            LoginManager.getInstance().logOut();
            mainTextView.setText("Status: Logged Out!");
        }
    }
}
