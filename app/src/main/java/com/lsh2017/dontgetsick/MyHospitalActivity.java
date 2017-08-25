package com.lsh2017.dontgetsick;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

public class MyHospitalActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<HospitalItem> items=new ArrayList<>();

    MyHosAdapter adapter;

    //내가 즐겨찾는 병원 카드뷰로 나타냄
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_hospital);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SQLiteDatabase db=openOrCreateDatabase("hos.db", Context.MODE_PRIVATE,null);
        Cursor cursor= db.rawQuery("SELECT * FROM "+"hospital",null);
        if(cursor==null) return;

        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext()){
            String name1= cursor.getString(cursor.getColumnIndex("name"));
            String addr=cursor.getString(cursor.getColumnIndex("addr"));
            boolean open=cursor.getInt(cursor.getColumnIndex("open"))==0?false:true;
            double rating=cursor.getDouble(cursor.getColumnIndex("rating"));
            String icon=cursor.getString(cursor.getColumnIndex("icon"));
            Log.i("값다들어왔니",name1+"  "+addr+"  "+open+"  "+rating+"  "+icon+"!");

            HospitalItem item =new HospitalItem(name1,addr,icon,open,rating);
            items.add(item);

        }

        recyclerView=(RecyclerView)findViewById(R.id.recyclerveiw_my_hospital);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter=new MyHosAdapter(this,items);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
