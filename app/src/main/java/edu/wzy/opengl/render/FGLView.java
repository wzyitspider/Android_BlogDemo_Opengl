/*
 *
 * FGLView.java
 * 
 * Created by wzy on 2016/9/29
 */
package edu.wzy.opengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import edu.wzy.opengl.render.FGLRender;
import edu.wzy.opengl.render.Shape;

/**
 * Description:
 */
public class FGLView extends GLSurfaceView {

    private FGLRender renderer;

    public FGLView(Context context) {
        this(context,null);
    }

    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        setRenderer(renderer=new FGLRender(this));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setShape(Class<? extends Shape> clazz){
        try {
            renderer.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
