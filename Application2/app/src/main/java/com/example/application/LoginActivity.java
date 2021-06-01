package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.DTO.LogResponse;
import com.example.application.DTO.LogData;

public class LoginActivity extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    String Tag ="loginActivity";
    APIInterface apiInterface;
    Encryption mEncryptor=new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mTextUsername = (EditText)findViewById(R.id.edittext_username);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mButtonLogin = (Button)findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mTextUsername.getText().toString();
                String password = mTextPassword.getText().toString();
                /*if(!validateLogin(username, password)){
                    //do login
                   // doLogin(username, password);
                }*/



                Intent homeIntent = new Intent (LoginActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                Log.d(Tag,"zalogowano");

            }
        });
        mTextViewRegister = (TextView)findViewById(R.id.textview_register);
        mTextViewRegister.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent (LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(final String username,final String password){
        LogData log=new LogData(mEncryptor.encrypt(username),mEncryptor.encrypt(password));

        Call call = apiInterface.logUser(log);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    String resObj = (String) response.body();


                    if(resObj=="Nie ma takiego użytownika!"){

                        Toast.makeText(LoginActivity.this, "The username or password is incorrect", Toast.LENGTH_SHORT).show();

                    } else {

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("ID", resObj);
                        intent.putExtra("username",log.Nazwa);
                        intent.putExtra("password",log.Haslo);
                        startActivity(intent);

                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}