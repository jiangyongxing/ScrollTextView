package cn.yongxing.customerview.scrolltextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import cn.yongxing.customerview.scrolltextview.view.ScrollTextView;

public class MainActivity extends AppCompatActivity {

    private ScrollTextView scrollTextView;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        scrollTextView = (ScrollTextView) findViewById(R.id.scrolltextview);
        scrollTextView.setTextList(dataList);
        scrollTextView.start();
    }

    private void initData() {
        dataList = new ArrayList<>();
        for(int i = 0;i<10;i++){
            dataList.add("这是一个滚动的Text View----"+i);
        }
    }
}
