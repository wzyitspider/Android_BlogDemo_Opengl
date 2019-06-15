/*
 *
 * Cone.java
 * 
 * Created by wzy on 2016/10/14
 */
package edu.wzy.opengl.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wzy.opengl.utils.ShaderUtils;

/**
 * Description:圆锥
 */
public class Cone extends Shape {

    private int mProgram;

    private Oval oval;
    private FloatBuffer vertexBuffer;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    private int n=360;  //切割份数
    private float height=2.0f;  //圆锥高度
    private float radius=1.0f;  //圆锥底面半径
    private float[] colors={1.0f,1.0f,1.0f,1.0f};

    private int vSize;

    public Cone(View mView){
        super(mView);
        oval=new Oval(mView);
        ArrayList<Float> pos=new ArrayList<>();
        pos.add(0.0f);
        pos.add(0.0f);
        pos.add(height);
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            pos.add((float) (radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(radius*Math.cos(i*Math.PI/180f)));
            pos.add(0.0f);
        }
        float[] d=new float[pos.size()];
        for (int i=0;i<d.length;i++){
            d[i]=pos.get(i);
        }
        vSize=d.length/3;
        ByteBuffer buffer=ByteBuffer.allocateDirect(d.length*4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer=buffer.asFloatBuffer();
        vertexBuffer.put(d);
        vertexBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mProgram=ShaderUtils.createProgram(mView.getResources(),"vshader/Cone.sh","fshader/Cone.sh");
        oval.onSurfaceCreated(gl,config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(mProgram);
        Log.e("wzy","mProgram:"+mProgram);
        int mMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrix,1,false,mMVPMatrix,0);
        int mPositionHandle=GLES20.glGetAttribLocation(mProgram,"vPosition");
        Log.e("wzy","Get Position:"+mPositionHandle);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,vertexBuffer);
//        int mColorHandle=GLES20.glGetUniformLocation(mProgram,"vColor");
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glUniform4fv(mColorHandle,1,colors,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,vSize);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        oval.setMatrix(mMVPMatrix);
        oval.onDrawFrame(gl);
    }
}
