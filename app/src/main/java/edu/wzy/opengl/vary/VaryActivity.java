package edu.wzy.opengl.vary;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import edu.wzy.opengl.BaseActivity;
import edu.wzy.opengl.R;

/**
 * Created by wzy on 2016/10/30
 */

public class VaryActivity extends BaseActivity {

    private GLSurfaceView mGLView;
    private VaryRender render;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl);
        initGL();
    }

    public void initGL(){
        mGLView= (GLSurfaceView) findViewById(R.id.mGLView);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(render=new VaryRender(getResources()));
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

}
