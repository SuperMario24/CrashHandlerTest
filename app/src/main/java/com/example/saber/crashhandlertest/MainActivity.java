package com.example.saber.crashhandlertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCrashHandler = (Button) findViewById(R.id.btn);
        btnCrashHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里模拟异常抛出情况，人为抛出一个运行时异常
                throw new RuntimeException("自定义异常：这是自己抛出的异常");
            }
        });


    }
}
