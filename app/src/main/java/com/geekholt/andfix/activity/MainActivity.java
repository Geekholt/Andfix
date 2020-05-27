package com.geekholt.andfix.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.geekholt.andfix.R;
import com.geekholt.andfix.fix.AndFixService;
import com.geekholt.andfix.util.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //启动bug修复服务
        startPatchService();
    }

    private void startPatchService() {
        Intent intent = new Intent(this, AndFixService.class);
        startService(intent);
    }

    //按钮点击事件
    public void createBug(View view) {
        Utils.printLog();
    }
}

