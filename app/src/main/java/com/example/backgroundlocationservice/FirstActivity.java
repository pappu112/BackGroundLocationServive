package com.example.backgroundlocationservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {
    EditText f_name,pass,mobile;
    Button go_btn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        f_name = findViewById(R.id.first_name);
        pass= findViewById(R.id.last_name);
        mobile = findViewById(R.id.mobile);
        go_btn = findViewById(R.id.change);
        preferences = getSharedPreferences("my_pref",MODE_PRIVATE);
        String name = preferences.getString("name","null");
        if(!name.equals("null")){
            //gotoMain();
        }




        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first = f_name.getText().toString();
                String last = pass.getText().toString();
                String mobile_str  =  mobile.getText().toString();
                if(first.isEmpty() || last.isEmpty() || mobile_str.isEmpty()){
                    Toast.makeText(FirstActivity.this,"Enter first and last name for first time",Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name",first);
                    editor.putString("pass",last);
                    editor.putString("mobile",mobile_str);

                    editor.commit();
                    gotoMain();

                }

            }
        });

    }

    void gotoMain(){
        startActivity(new Intent(FirstActivity.this,MainActivity.class));
        finish();

    }
}
