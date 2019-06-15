package edu.wzy.opengl.K3Animation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * 开奖动画
 * Created by aaa on 2017/4/11.
 */

public class RenderGLSurfaceView extends GLSurfaceView {
    private GLRender mGlRender;

    public RenderGLSurfaceView(Context context) {
        super(context);
    }

    public RenderGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //当使用OpenGLES 2.0时，你必须在GLSurfaceView构造器中调用另外一个函数，它说明了你将要使用2.0版的API：
        setEGLContextClientVersion(2);
        mGlRender = new GLRender(RenderGLSurfaceView.this, context);
        //设置render到GLSurfaceView
        setRenderer(mGlRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }

    public void play() {
        mGlRender.play();
    }

    public void stop() {
        mGlRender.stop();
    }

    public void playWin(int fistNum, int secondNum, int thridNum) {
        mGlRender.playWin(fistNum, secondNum, thridNum);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mGlRender.recycleBitmap();
    }
}
