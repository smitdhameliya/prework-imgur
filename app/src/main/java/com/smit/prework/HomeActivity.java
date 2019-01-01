package com.smit.prework;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private EditText et_imageId;
    private ImageView imgView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;


    String imgur_url = "https://api.imgur.com/3/gallery/image/";
    String ClientID = "5574ba3cfe4ad56";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar_HomeActivity);
        toolbar.setTitle("Search");
        setSupportActionBar(toolbar);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();
            TextView username = findViewById(R.id.username);
            ImageView profilePic = findViewById(R.id.profile_image);

            username.setText(personName);
            GlideApp.with(this)
                    .load(personPhoto)
                    .into(profilePic);
        }

        findViews();

    }

    private void findViews() {
        // find layouts
        et_imageId = findViewById(R.id.etImgId);
        imgView = findViewById(R.id.imgView);
    }

    public void searchImage(View view) {
        if (!et_imageId.getText().toString().isEmpty()) {

            // hide keyboard, So user can see image...
            hideKeyboard(this);

            OkHttpClient httpClient = new OkHttpClient.Builder().build();

            Request request = new Request.Builder()
                    .url(imgur_url + et_imageId.getText().toString())
                    .header("Authorization", "Client-ID " + ClientID)
                    .build();

            httpClient.newCall(request).enqueue(
                    new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e("ERROR", "An error has occurred " + e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            Log.e("RESPONSE", "Got");

                            JSONObject data = null;
                            try {
                                if (response.body() != null) {
                                    data = new JSONObject(response.body().string());

                                    JSONObject item = data.getJSONObject("data");
                                    final String img_url = item.getString("link");

                                    Log.e("img_url: ", img_url);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            GlideApp.with(HomeActivity.this)
                                                    .load(img_url)
                                                    .placeholder(R.drawable.loading)
                                                    .into(imgView);

                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

        } else {
            Toast.makeText(this, "Please provide a image ID!", Toast.LENGTH_SHORT).show();
        }
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) view = new View(activity);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
