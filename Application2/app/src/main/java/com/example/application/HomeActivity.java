package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView mTextViewLogout = (TextView) findViewById(R.id.textview_logout);
        mTextViewLogout.setOnClickListener (view -> {
            Intent LogoutIntent = new Intent (HomeActivity.this, LoginActivity.class);
            startActivity(LogoutIntent);
        });
    }

    @SuppressLint("SetTextI18n")
    public void SendMessage(View view) {
        TextView mShowCount = (TextView) findViewById(R.id.msg_count_n);
        Toast toast = Toast.makeText(this, R.string.sent_message,
                Toast.LENGTH_SHORT);
        toast.show();
        mCount++;
        if (mShowCount != null)
            mShowCount.setText(Integer.toString(mCount));

    }


}