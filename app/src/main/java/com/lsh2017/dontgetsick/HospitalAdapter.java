package com.lsh2017.dontgetsick;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by 이소희 on 2017-08-16.
 */

public class HospitalAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<HospitalInfo> items = new ArrayList<>();
    SQLiteDatabase db;

    public HospitalAdapter(Context context, ArrayList<HospitalInfo> items, SQLiteDatabase db) {
        this.context = context;
        this.items = items;
        this.db=db;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.hospital_item_cardview,parent,false);
        Holder holder=new Holder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Holder holder1=(Holder) holder;

        holder1.name.setText(items.get(position).name);
        holder1.addr.setText(items.get(position).addr);
        holder1.open.setText(items.get(position).openHours?"이용가능":" ");
        holder1.rating.setText(items.get(position).rating+"");
        Glide.with(context).load(items.get(position).iconUrl).into(holder1.icon);



        if(items.get(position).star){
            holder1.star.setImageResource(android.R.drawable.btn_star_big_on);
        }else{
            holder1.star.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder1.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref=context.getSharedPreferences("hosfavorite",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                if(items.get(position).star){
                    items.get(position).star=!items.get(position).star;
                    editor.putBoolean(items.get(position).addr, items.get(position).star);
                    db.execSQL("DELETE FROM "+"hospital"+" WHERE addr=?", new String[]{items.get(position).addr});
                }else{
                    items.get(position).star=!items.get(position).star;
                    editor.putBoolean(items.get(position).addr, items.get(position).star);
                    db.execSQL("INSERT INTO "+"hospital"+"(name, addr, open, rating, icon) values('"+items.get(position).name+"', '"+items.get(position).addr+"', 1"+","+items.get(position).rating+",'"+items.get(position).iconUrl+"')");


                }

                editor.commit();
                notifyDataSetChanged();




            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView icon;
        TextView addr;
        TextView rating;
        TextView open;
        ImageView star;



        public Holder(View itemView) {
            super(itemView);

            name=(TextView) itemView.findViewById(R.id.hos_name);
            icon=(ImageView) itemView.findViewById(R.id.img_hos);
            addr=(TextView)itemView.findViewById(R.id.addr);
            rating=(TextView)itemView.findViewById(R.id.rating);
            open=(TextView)itemView.findViewById(R.id.opening);
            star=(ImageView)itemView.findViewById(R.id.hos_star);

        }
    }
}

