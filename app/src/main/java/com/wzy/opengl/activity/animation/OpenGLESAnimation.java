package com.wzy.opengl.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;

import com.wzy.opengl.R;

public class OpenGLESAnimation extends AppCompatActivity {
    SurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_glesanimation);
        surfaceView  = findViewById(R.id.surface);
    }
}
