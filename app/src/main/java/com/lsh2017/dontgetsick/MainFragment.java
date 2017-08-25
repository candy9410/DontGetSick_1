package com.lsh2017.dontgetsick;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by 이소희 on 2017-08-02.
 */

public class MainFragment extends Fragment{

    ImageView imgHead, imgLeftArm, imgRightArm, imgBody, imgLeftLeg, imgRightLeg;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_main,container,false);
        view.findViewById(R.id.frag_main);

        imgBody=(ImageView)view.findViewById(R.id.img_body);
        imgHead=(ImageView)view.findViewById(R.id.img_head);
        imgLeftArm=(ImageView)view.findViewById(R.id.img_left_arm00);
        imgRightArm=(ImageView) view.findViewById(R.id.img_right_arm00);
//        imgLeftLeg=(ImageView)view.findViewById(R.id.img_leg);
//        imgRightLeg=(ImageView)view.findViewById(R.id.img_leg2);

        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    //이곳에 사람 신체 부위 이미지버튼 누르면 넘어가기
}

