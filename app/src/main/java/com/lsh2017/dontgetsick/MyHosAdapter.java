package com.lsh2017.dontgetsick;

import android.content.Context;
import android.database.Cursor;
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
 * Created by 이소희 on 2017-08-24.
 */

public class MyHosAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<HospitalItem> items=new ArrayList<>();

    public MyHosAdapter(Context context,ArrayList<HospitalItem> items) {
        this.context = context;
        this.items=items;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.hospital_item_cardview_nostar,parent,false);
        Holder holder=new Holder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Holder holder1=(Holder)holder;

        holder1.name.setText(items.get(position).name);
        holder1.addr.setText(items.get(position).addr);
        holder1.open.setText(items.get(position).openHours?"이용가능":" ");
        holder1.rating.setText(items.get(position).rating+"");
        Glide.with(context).load(items.get(position).iconUrl).into(holder1.icon);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    class Holder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView icon;
        TextView addr;
        TextView rating;
        TextView open;

        public Holder(View itemView) {
            super(itemView);

            name=(TextView) itemView.findViewById(R.id.hos_name);
            icon=(ImageView) itemView.findViewById(R.id.img_hos);
            addr=(TextView)itemView.findViewById(R.id.addr);
            rating=(TextView)itemView.findViewById(R.id.rating);
            open=(TextView)itemView.findViewById(R.id.opening);
        }
    }

}
