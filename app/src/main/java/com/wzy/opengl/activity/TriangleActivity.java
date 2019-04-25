package com.wzy.opengl.activity;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wzy.opengl.R;
import com.wzy.opengl.shape.Triangle;

/**
 * Time:2019/4/25
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class TriangleActivity extends AppCompatActivity {
    GLSurfaceView mGLView;
    Triangle render ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLView = findViewById(R.id.mGLView);
        mGLView.setEGLContextClientVersion(2);
        render = new Triangle();
        mGLView.setRenderer(render);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        render.destroy();
    }
}
