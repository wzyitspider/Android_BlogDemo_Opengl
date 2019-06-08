package com.wzy.opengl.activity;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wzy.opengl.R;
import com.wzy.opengl.shape.Square;
import com.wzy.opengl.shape.Shape;
import com.wzy.opengl.shape.Triangle;
import com.wzy.opengl.view.MyGLSurfaceView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Time:2019/4/25
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class ShapeActivity extends AppCompatActivity {
    MyGLSurfaceView mGLView;
    Shape shape ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Class clazz = (Class) getIntent().getExtras().getSerializable("name");

        mGLView = findViewById(R.id.mGLView);
        mGLView.setEGLContextClientVersion(2);
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            shape= (Shape) constructor.newInstance();
            mGLView.setRenderer(shape);
        } catch (Exception e) {
            e.printStackTrace();
            shape= new Triangle();
            mGLView.setRenderer(shape);
        }
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void onClick(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shape.destroy();
    }
}
