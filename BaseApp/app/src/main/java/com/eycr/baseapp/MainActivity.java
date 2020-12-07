package com.eycr.baseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ma_Button = (Button) findViewById(R.id.buttonPrincipal);
        ma_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ma_Text = (TextView) findViewById(R.id.textoPrincipal);
                ma_Text.setText("He sido presionado");
            }
        });

    }
}