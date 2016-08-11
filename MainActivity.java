package com.nqm.nqmenviro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button authorise = (Button) findViewById(R.id.auth);
        final Button create = (Button) findViewById(R.id.createAsset);
        final EditText tokenName   = (EditText)findViewById(R.id.name);
        final EditText tokenSecret   = (EditText)findViewById(R.id.secret);
        final EditText assetName   = (EditText)findViewById(R.id.assetName);
        final EditText newAsset = (EditText)findViewById(R.id.newAssetName);

        SharedPreferences prefs = getSharedPreferences("UserData", 0);
        String currentName = prefs.getString("tokenName","");
        String currentAsset = prefs.getString("assetName", "");
        if(!currentName.equals("")) tokenName.setText(currentName);
        if(!currentAsset.equals("")) assetName.setText(currentAsset);

        create.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
              SharedPreferences prefs = getSharedPreferences("UserData", 0);
              SharedPreferences.Editor editor = prefs.edit();
              editor.putString("tokenName", tokenName.getText().toString());
              editor.commit();
              Intent enviroService = new Intent(MainActivity.this, EnviroService.class);
              enviroService.setClassName("com.nqm.nqmenviro", "com.nqm.nqmenviro.EnviroService");
              enviroService.putExtra("name", tokenName.getText().toString());
              enviroService.putExtra("secret", tokenSecret.getText().toString());
              enviroService.putExtra("asset", newAsset.getText().toString());
              enviroService.putExtra("create", (Boolean)true);
              startService(enviroService);
          }
        });



        authorise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                // Save token name
                SharedPreferences prefs = getSharedPreferences("UserData", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("tokenName", tokenName.getText().toString());
                editor.putString("assetName", assetName.getText().toString());
                editor.commit();

                // Start GPS service
                Intent enviroService = new Intent(MainActivity.this, EnviroService.class);
                enviroService.setClassName("com.nqm.nqmenviro", "com.nqm.nqmenviro.EnviroService");
                enviroService.putExtra("name", tokenName.getText().toString());
                enviroService.putExtra("secret", tokenSecret.getText().toString());
                enviroService.putExtra("asset", assetName.getText().toString());
                enviroService.putExtra("create", false);
                startService(enviroService);
            }
        });
    }
}
