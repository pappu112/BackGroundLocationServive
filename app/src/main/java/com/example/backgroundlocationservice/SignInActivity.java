package com.example.backgroundlocationservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    SharedPreferences preferences;
    EditText name_et,pass;
    Button signIn,signUp;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading.");
        progressDialog.setCancelable(false);
        name_et = findViewById(R.id.user_name);
        pass = findViewById(R.id.pass);
        signIn = findViewById(R.id.sign_id);
        signUp = findViewById(R.id.sign_up);

        preferences = getSharedPreferences("my_pref",MODE_PRIVATE);
        final String name = preferences.getString("name","null");
        if(!name.equals("null")){
            gotoMain();
        }

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = name_et.getText().toString();
                String last = pass.getText().toString();
                if(first.isEmpty() || last.isEmpty()){
                    Toast.makeText(SignInActivity.this,"Enter first and last name for first time",Toast.LENGTH_SHORT).show();
                }else {
                    signin(first,last);
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,FirstActivity.class));
            }
        });
    }

    void gotoMain(){
        startActivity(new Intent(SignInActivity.this,MainActivity.class));
        finish();

    }

    void signin(String name,String pass){
        progressDialog.show();
        Api service = NetworkClient.getRetrofitClient().create(Api.class);
        Call<ArrayList<Model>> call;
        call = service.signIn(name,pass);
        call.enqueue(new Callback<ArrayList<Model>>() {
            @Override
            public void onResponse(Call<ArrayList<Model>> call, Response<ArrayList<Model>> response) {
                Log.e("response",String.valueOf(response.body().size()));
                ArrayList<Model> arrayList = response.body();
                if (arrayList.size()==0){
                    Toast.makeText(SignInActivity.this,"User name and password is not correct",Toast.LENGTH_LONG).show();
                }else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name",arrayList.get(0).getDevice());
                    editor.putString("pass",arrayList.get(0).getPass());
                    editor.putString("mobile",arrayList.get(0).getMobile());
                    editor.commit();
                    gotoMain();


                }
                progressDialog.cancel();
            }

            @Override
            public void onFailure(Call<ArrayList<Model>> call, Throwable t) {
                Log.e("response",t.toString());
                progressDialog.cancel();

            }
        });
    }
}
