package edu.wzy.opengl.vr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wzy.opengl.BaseActivity;
import edu.wzy.opengl.R;

/**
 * Created by aiya on 2017/5/19.
 */

public class GlobeActivity extends BaseActivity implements GLSurfaceView.Renderer,SensorEventListener {

    private GLSurfaceView mGLView;
    //private SensorManager mSensorManager;
    //private Sensor mRotation;
    //private SkySphere mSkySphere;
    BallGlobe ball;
    private float[] matrix=new float[16];
    
    //线程循环的标志位
    boolean flag=true;
    float x;
    float y;
    static float direction=0;//视线方向
    static float cx=0;//摄像机x坐标
    static float cz=20;//摄像机z坐标
    static float tx=0;//观察目标点x坐标
    static float tz=0;//观察目标点z坐标
    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度
    //屏幕对应的宽度和高度
    public static float WIDTH;
    public static float HEIGHT;
    float Offset=20;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glview);

//        mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        List<Sensor> sensors=mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        //todo 判断是否存在rotation vector sensor
//        mRotation=mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mGLView=(GLSurfaceView) findViewById(R.id.mGLView);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(this);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mGLView.setOnTouchListener(onTouchListener);
        ball=new BallGlobe(this.getApplicationContext(),"vr/logo.png");
       // ball=new BallGlobe(this.getApplicationContext(),"vr/vr4.jpg");
        //获得系统的宽度以及高度
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels>dm.heightPixels)
        {
            WIDTH=dm.widthPixels;
            HEIGHT=dm.heightPixels;
        }
        else
        {
            WIDTH=dm.heightPixels;
            HEIGHT=dm.widthPixels;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
      //  mSensorManager.registerListener(this,mRotation,SensorManager.SENSOR_DELAY_GAME);
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mSensorManager.unregisterListener(this);
        mGLView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ball.create(gl);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        ball.setSize(width, height);
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(1,1,1,1);
        ball.draw();
//        //设置新的摄像机位置
//        MatrixState.setCamera(cx,5,cz,tx,2,tz,0,1,0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        SensorManager.getRotationMatrixFromVector(matrix,event.values);
//        ball.setMatrix(matrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float lastX, lastY;

        private int mode = 0; // 触控点的个数

        float oldDist = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = 1;
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    mode += 1;

                    oldDist = caluDist(event);

                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode -= 1;
                    break;

                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (mode >= 2) {
                        float newDist = caluDist(event);
                        if (Math.abs(newDist - oldDist) > 2f) {
                            zoom(newDist, oldDist);
                        }
                    } else {
                        float dx = event.getRawX() - lastX;
                        float dy = event.getRawY() - lastY;
                        float a = 180.0f / 320;
                        ball.mAngleX += dx * 0.2;
                        ball.mAngleY += dy * 0.2;
                    }
                    break;
            }

            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
            return true;
        }
    };


    public float caluDist(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void zoom(float newDist, float oldDist) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float px = displayMetrics.widthPixels;
        float py = displayMetrics.heightPixels;

        ball.zoom += (newDist - oldDist) * (ball.maxZoom - ball.minZoom) / Math.sqrt(px * px + py * py) / 4;

        if (ball.zoom > ball.maxZoom) {
            ball.zoom = ball.maxZoom;
        } else if (ball.zoom < ball.minZoom) {
            ball.zoom = ball.minZoom;
        }
    }

}
