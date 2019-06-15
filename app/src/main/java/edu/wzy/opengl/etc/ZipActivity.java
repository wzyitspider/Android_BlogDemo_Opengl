/*
 *
 * ZipActivity.java
 * 
 * Created by wzy on 2016/12/8
 * Copyright © 2016年 . All rights reserved.
 */
package edu.wzy.opengl.etc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.wzy.opengl.R;
import edu.wzy.opengl.utils.Gl2Utils;

/**
 * Description:
 */
public class ZipActivity extends AppCompatActivity {

    private ZipAniView mAniView;
    private String nowMenu="assets/etczip/cc.zip";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);
        mAniView= (ZipAniView)findViewById(R.id.mAni);
        mAniView.setScaleType(Gl2Utils.TYPE_CENTERINSIDE);
        mAniView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mAniView.isPlay()){
                    mAniView.setAnimation(nowMenu,50);
                    mAniView.start();
                }
            }
        });
        mAniView.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChanged(int lastState, int nowState) {
                if(nowState==STOP){
                    if(!mAniView.isPlay()){
                        mAniView.setAnimation(nowMenu,50);
                        mAniView.start();
                    }
                }
            }
        });
    }

}
