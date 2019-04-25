package com.wzy.opengl.shape;
 
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Time:2019/4/25
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class Triangle extends Shape {

    private int mProgramId;

    private int mPositionId,mColorId;

    //设置每个顶点的坐标数
    private static final int COORDS_PER_VERTEX = 3;
    //每个float类型变量为4字节
    private final int VERTEX_STRID = COORDS_PER_VERTEX * 4;

    //顶点个数
    private final int VERTEX_COUNT = triangleCoords.length / COORDS_PER_VERTEX;

    //folat缓冲区
    public FloatBuffer vertexBuffer;

    //设置三角形顶点数组，默认按逆时针方向绘制
    public  static float[] triangleCoords = {
            0.0f, 1.0f, 0.0f, // 顶点
            -1.0f, -0.0f, 0.0f, // 左下角
            1.0f, -0.0f, 0.0f  // 右下角
    };

    //顶点着色器
    public static final String VERTEX_SHADER =
                    "//根据所设置的顶点数据，插值在光栅化阶段进行\n" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  //设置最终坐标\n" +
                    "  gl_Position = vPosition;" +
                    "}";
    //片元着色器
    public static final String FRAGMENT_SHADER =
                    "//设置float类型默认精度，顶点着色器默认highp，片元着色器需要用户声明\n" +
                    "precision mediump float;" +
                    "//颜色值，vec4代表四维向量，此处由用户传入，数据格式为{r,g,b,a}\n" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "//该片元最终颜色值\n" +
                    "gl_FragColor = vColor;" +
                    "}";

    // 设置三角形颜色和透明度（r,g,b,a），绿色不透明
    public static float[] color = {0.0f, 1.0f, 0f, 1.0f};

    public Triangle() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgramId = loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        //通过OpenGL程序句柄查找获取顶点着色器中的位置句柄
        mPositionId = GLES20.glGetAttribLocation(mProgramId, "vPosition");
        //通过OpenGL程序句柄查找获取片元着色器中的颜色句柄
        mColorId = GLES20.glGetUniformLocation(mProgramId, "vColor");
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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
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
        //绑定颜色数据
        GLES20.glUniform4fv(mColorId, 1, Triangle.color, 0);
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