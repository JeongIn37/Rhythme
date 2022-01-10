package com.cs496.rhythm;
import android.app.ListActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class VideoChoiceActivity extends ListActivity {

    // 인덱스 데이터
    Data[] INDEX_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        INDEX_DATA = new Data[]{
                new Data("list1", ContextCompat.getDrawable(this, R.drawable.rasputin_list)),
                new Data("list2", ContextCompat.getDrawable(this, R.drawable.rasputin_list)),
                new Data("list3", ContextCompat.getDrawable(this, R.drawable.rasputin_list))
        };

        // 인덱스 데이터를 리스트에 추가
        List<Data> list = new ArrayList<Data>();
        for (int i = 0; i < INDEX_DATA.length; i++) {
            list.add(INDEX_DATA[i]);
        }

        // 인덱스 표시 어댑터 설정
        ListAdapter adapter = new ListAdapter(this, 0, list);

        // 어댑터를 설정
        setListAdapter(adapter);
    }

    // 리스트뷰 이벤트
    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        Toast.makeText(this, "position: " + position + "content: " + INDEX_DATA[position].getText(), Toast.LENGTH_SHORT).show();
    }
}