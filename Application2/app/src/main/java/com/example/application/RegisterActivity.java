package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.DTO.LogResponse;
import com.example.application.DTO.LogData;
import com.example.application.DTO.RegData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;
    APIInterface apiInterface;
    String Tag ="registerctivity";
    Encryption mEncryptor=new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        mTextUsername = (EditText) findViewById(R.id.edittext_username);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mTextCnfPassword = (EditText) findViewById(R.id.edittext_cnf_password);
        mButtonRegister = (Button) findViewById(R.id.button_register);
        mTextViewLogin = (TextView) findViewById(R.id.textview_login);
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mTextUsername.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();

                if(validateRegister(user, pwd, cnf_pwd)){

                 doRegister(user, pwd);
                 }
            }
        });
    }

    private boolean validateRegister(String username, String password, String cnfpassword) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cnfpassword == null || cnfpassword.trim().length() == 0) {
            Toast.makeText(this, "Confirmed password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!cnfpassword.trim().equals(password.trim())) {
            Toast.makeText(this, "Confirmed password and password are not the same", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doRegister(final String username, final String password) {

        String address = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        RegData register = new RegData(mEncryptor.encrypt(username),mEncryptor.encrypt(password), mEncryptor.encrypt(address));

        Call call = apiInterface.registerUser(register);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d(Tag,"wysłano i okej"+response.code());

                if (response.isSuccessful()) {


                    LogResponse resObj = (LogResponse) response.body();

                    switch (resObj.getResp()) {
                        case ("Udało się zarejestrować!"):
                            Toast.makeText(RegisterActivity.this, "U have registered correctly", Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            //startActivity(intent);
                            break;
                        case ("Użytkownik tego urządzenia posiada już konto!"):
                            Toast.makeText(RegisterActivity.this, "That device is already registered", Toast.LENGTH_SHORT).show();
                            break;
                        case ("Taka nazwa użytkownika już istnieje!"):
                            Toast.makeText(RegisterActivity.this, "That username is already taken", Toast.LENGTH_SHORT).show();
                            break;
                    }

                } else {
                    Log.d(Tag,"wyjebało sie");
                    Toast.makeText(RegisterActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}