package com.wzy.opengl.shape;
 
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Time:2019/4/26
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class IsoTriangleColorFull extends Shape {

    private int mProgramId;

    private int mPositionId,mMatrixHandler, mColorId;

    //设置每个顶点的坐标数
    private static final int COORDS_PER_VERTEX = 3;
    //每个float类型变量为4字节
    private final int VERTEX_STRID = COORDS_PER_VERTEX * 4;

    //顶点个数
    private final int VERTEX_COUNT = triangleCoords.length / COORDS_PER_VERTEX;

    //folat缓冲区
    public FloatBuffer vertexBuffer,colorBuffer;

    //设置三角形顶点数组，默认按逆时针方向绘制
    public  static float[] triangleCoords = {
            0.0f, 0.5f, 0.0f, // 顶点
            0.0f, -0.5f, 0.0f, // 左下角
            -1.0f, -0.5f, 0.0f  // 右下角
    };

    //顶点着色器
    public static final String VERTEX_SHADER =
                    "//根据所设置的顶点数据，插值在光栅化阶段进行\n" +
                    "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "varying  vec4 vColor;" +
                    "attribute vec4 aColor;"+
                    "void main() {" +
                    "  //设置最终坐标\n" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "vColor=aColor;"+
                    "}";


    //片元着色器
    public static final String FRAGMENT_SHADER =
                    "//设置float类型默认精度，顶点着色器默认highp，片元着色器需要用户声明\n" +
                    "precision mediump float;" +
                   "//颜色值，varying是从顶点着色器传递过来的\n" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "//该片元最终颜色值\n" +
                    "  gl_FragColor = vColor;" +
                    "}";

    //设置颜色
    public float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f ,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    private float[] mProjectMatrix= new float[16];
    private float[] mViewMatrix= new float[16];
    private float[] mMVPMatrix= new float[16];

    public IsoTriangleColorFull() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgramId = loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        //通过OpenGL程序句柄查找获取顶点着色器中的位置句柄
        mPositionId = GLES20.glGetAttribLocation(mProgramId, "vPosition");
        //获取片元着色器的vColor成员的句柄
        mColorId = GLES20.glGetAttribLocation(mProgramId, "aColor");
        //通过OpenGL程序句柄查找获取片元着色器中的颜色句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgramId, "vMatrix");
        // 初始化顶点字节缓冲区，用于存放形状的坐标，每个浮点数占用4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        //设置使用设备硬件的原生字节序
        bb.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        vertexBuffer.put(triangleCoords);
        //设置buffer从第一个坐标开始读
        vertexBuffer.position(0);

        // 给颜色缓冲区赋值
        ByteBuffer cb = ByteBuffer.allocateDirect(color.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // 计算变换矩阵
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
        GLES20.glVertexAttribPointer(mPositionId, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRID, vertexBuffer);

        //启用指向颜色数据的句柄
        GLES20.glEnableVertexAttribArray(mColorId);
        //绑定三角形的颜色数据
        GLES20.glVertexAttribPointer(mColorId, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT);
        //禁用指向三角形的顶点数据
        GLES20.glDisableVertexAttribArray(mPositionId);
    }

    @Override
    public void destroy() {
        GLES20.glDeleteProgram(mProgramId);
    }
}