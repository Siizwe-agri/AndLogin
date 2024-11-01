package com.example.androidlogin;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client; // Define the client as a class variable
    private Button btnLogin;
    private TextView txtForgot;
    private EditText txtUser;
    private EditText txtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize OkHttpClient
        client = new OkHttpClient();
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUser = findViewById(R.id.txtUser);
                txtPass = findViewById(R.id.txtPass);
                if (txtUser.getText().toString().isEmpty() && txtPass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
                }
                else if(txtUser.getText().toString().isEmpty()) {
                    //makePostRequest();
                    Toast.makeText(MainActivity.this, "Please enter your username!", Toast.LENGTH_SHORT).show();
                } else if (txtPass.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your password!", Toast.LENGTH_SHORT).show();
                }
                else {
                    makePostRequest();
                }
            }
        });
        // Call makePostRequest after client has been initialized
        txtForgot = findViewById(R.id.txtForgotPwd);
        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPwd.class);
                startActivity(intent);
            }
        });
    }

    private void makePostRequest() {
        // Your existing code for making the request
        String url = "http://192.168.1.121/Android/login.php";
        String username = txtUser.getText().toString();
        String password = txtPass.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", txtUser.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        // Make sure client is not null here
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Request Failed" + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            Log.d("API_RESPONSE", "Response: " + responseData);
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String result = jsonResponse.getString("status");
                            String outcome = jsonResponse.getString("message");
                            //Toast.makeText(MainActivity.this, user + " " + pass, Toast.LENGTH_SHORT).show();
                            if (result.equals("true")) {
                                Intent intent = new Intent(MainActivity.this, HomePage.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Incorrect details!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}
