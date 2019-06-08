package com.wzy.opengl.activity;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wzy.opengl.R;
import com.wzy.opengl.shape.VaryShap;
import com.wzy.opengl.view.MyGLSurfaceView;

public class VaryActivity extends AppCompatActivity {
    MyGLSurfaceView mGLView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vary);
        mGLView = findViewById(R.id.surface);
        mGLView.setEGLContextClientVersion(2);
        VaryShap shape = new VaryShap();
         mGLView.setRenderer(shape);
        //Normal shape1 = new Normal();
        //mGLView.setRenderer(shape1);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
