package com.lsh2017.dontgetsick;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by 이소희 on 2017-08-04.
 */

public class MedicineAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<MedicineItem> items=new ArrayList<>();

    public MedicineAdapter(Context context, ArrayList<MedicineItem> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //뷰 inflate
        View view= LayoutInflater.from(context).inflate(R.layout.medicine_item_cardview,parent,false);
        Holder holder=new Holder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //값 넣기
        ((Holder)holder).medicineDate.setText(items.get(position).date);
        ((Holder)holder).medicineMemo.setText(items.get(position).memo);
        Glide.with(context).load(items.get(position).imgMedicine).into(((Holder) holder).imgMedicine);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    class Holder extends RecyclerView.ViewHolder{

        ImageView imgMedicine;
        TextView medicineMemo;
        TextView medicineDate;

        public Holder(View itemView) {

            super(itemView);

            imgMedicine=(ImageView)itemView.findViewById(R.id.img_medicine);
            medicineMemo=(TextView)itemView.findViewById(R.id.medicine_memo);
            medicineDate=(TextView)itemView.findViewById(R.id.medicine_date);


            //카드뷰 길게눌렀을때 삭제 가능하도록하기
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return true;
                }
            });



        }
    }
}
