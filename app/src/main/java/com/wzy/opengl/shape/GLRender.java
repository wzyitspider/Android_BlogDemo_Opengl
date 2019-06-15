package com.wzy.opengl.activity.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.wzy.opengl.R;
import com.wzy.opengl.activity.Myapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Time:2019/6/10
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class GLRender extends Shape {
    float [] mProjectMatrix = new float[16],
            mViewMatrix = new float[16] ,
            mMVPMatrix =new float[16];
    int mProgramId;
    int mPositionId;
    int mColorId;
    int mMatrixHandler,glHCoordinate,glVTexture;
    float [] vbo = {
            -1f,1f,0f,
             1f,1f,0f,
            -1f,-1f,0f,
            -1f,1f,0f,
    };

    private final float[] sCoord={
            0.0f,1.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,0.0f,
    };

    short [] index = {
            1,3,2,
            2,3,4
    };

    FloatBuffer vertexBuffer,textureBuffer ;
    ShortBuffer indexBuffer ;

    private final String VERTEX_SHADER =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "attribute vec2 vCoordinate;"+
                    "varying vec2 aCoordinate;"+
                    "void main() {" +
                    "  aCoordinate=vCoordinate;"+
                    "  gl_Position = vMatrix*vPosition;" +
                    "}";

    private final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D glVTexture;"+
                    "varying vec2 aCoordinate;"+
                    "void main() {" +
                    "gl_FragColor=texture2D(glVTexture,aCoordinate);"+
                    "}";

    private Bitmap mBitmap;

    public GLRender(GLSurfaceView glSurfaceView, Context context) {
        // 初始化顶点字节缓冲区，用于存放形状的坐标，每个浮点数占用4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(vbo.length * 4);
        //设置使用设备硬件的原生字节序
        bb.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        vertexBuffer.put(vbo);
        //设置buffer从第一个坐标开始读
        vertexBuffer.position(0);

        //short 占两个字节
        ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer=cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);


        ByteBuffer tt = ByteBuffer.allocateDirect(sCoord.length * 4);
        tt.order(ByteOrder.nativeOrder());
        textureBuffer = tt.asFloatBuffer();
        textureBuffer.put(sCoord);
        textureBuffer.position(0);

        mBitmap =BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_k3_bj);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mProgramId = loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        //通过OpenGL程序句柄查找获取顶点着色器中的位置句柄
        mPositionId = GLES20.glGetAttribLocation(mProgramId, "vPosition");
        //通过OpenGL程序句柄查找获取片元着色器中的颜色句柄
        mColorId = GLES20.glGetUniformLocation(mProgramId, "vColor");
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler= GLES20.glGetUniformLocation(mProgramId,"vMatrix");
        //通过OpenGL程序句柄查找获取纹理句柄
        glHCoordinate = GLES20.glGetAttribLocation(mProgramId, "vCoordinate");
        //获取纹理取样器
        glVTexture = GLES20.glGetUniformLocation(mProgramId,"glVTexture");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);;

        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        //eyex,eyey,eyez 代表相机在坐标轴的位置,centerX,centerY,centerZ代表物体的中心坐标，upX,upY,upZ代表相机往哪边看
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //告知OpenGL所要使用的Program
        GLES20.glUseProgram(mProgramId);
        //启用指向三角形顶点数据的句柄
        GLES20.glEnableVertexAttribArray(mPositionId);

        //绑定三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionId, 3,
                GLES20.GL_FLOAT, false,
                12, vertexBuffer);



        createTexture();

        //1): index  ， Attribute的索引
        //2): size, Attribute 变量数据是由几个元素组成的， x,y,z,w ; 最多四个。
        //3): normalized, 是否归一化， 编程1.0以内的数，这样做的目的是减少向gpu传递数据的带宽。
        //4): stride,  元素间隔,， 通常是0
        GLES20.glVertexAttribPointer(glHCoordinate,2,GLES20.GL_FLOAT,false,2*4,textureBuffer);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);
       // gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,indexBuffer);

        //禁用指向三角形的顶点数据
        GLES20.glDisableVertexAttribArray(mPositionId);
//        //索引法绘制正方形
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vbo.length/3);
    }

    @Override
    public void destroy() {

    }

    private int createTexture(){
        int[] texture=new int[1];
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
