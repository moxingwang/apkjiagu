package com.chinaredstar.testapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv_content = new TextView(this);
        tv_content.setText("Lasa队测试APP，Lasa队测试APP，Lasa队测试APP，Lasa队测试APP，Lasa队测试APP，Lasa队测试APP，Lasa队测试APP，Lasa队测试APP，Lasa队测试APP，Lasa队测试APP");
        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
            }
        });
        setContentView(tv_content);
        Log.i(TAG, "onCreate：app:" + getApplicationContext());
    }
}
