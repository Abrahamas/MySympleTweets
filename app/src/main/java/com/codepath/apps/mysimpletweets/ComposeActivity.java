package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    User user;
    TwitterClient client;
    EditText tvCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);


        user = (User) getIntent().getSerializableExtra("user");
        client = TwitterApplication.getRestClient();

        // setup views
        setupViews();
    }

    private void setupViews() {

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfile);
        ivProfileImage.setImageResource(0);


        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvCompose = (EditText) findViewById(R.id.tvCompose);

        tvName.setText(user.getName());

        String profileImageUrl = user.getProfileImageUrl();

        Glide.with(this).load(profileImageUrl).into(ivProfileImage);
    }

    public void onCompose(View view) {
        // get status in body edit text
        String status = tvCompose.getText().toString();

        client.postTweet(status, new JsonHttpResponseHandler() {
            // On SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // get the tweet that is just created
                Tweet tweet = Tweet.fromJSON(response);
                // return the tweet to the TimelineActivity
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }

            // On FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                if (throwable.getMessage().contains("resolve host")) {
                    Toast.makeText(ComposeActivity.this,
                            "please verify your connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
