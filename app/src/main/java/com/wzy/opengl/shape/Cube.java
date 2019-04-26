package com.wzy.opengl.shape;
 
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Time:2019/4/26
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class Cube extends Shape {

    private int mProgramId;

    private int mPositionId,mMatrixHandler, mColorId;

    //设置每个顶点的坐标数
    private static final int COORDS_PER_VERTEX = 3;
    //每个float类型变量为4字节
    private final int VERTEX_STRID = COORDS_PER_VERTEX * 4;

    //folat缓冲区
    public FloatBuffer vertexBuffer,colorBuffer;
    public ShortBuffer indexBuffer;

    final float cubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };

    //顶点个数
    private final int VERTEX_COUNT = cubePositions.length / COORDS_PER_VERTEX;

    final short index[]={
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2     //下面
    };

    //八个顶点的颜色，与顶点坐标一一对应
    float color[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
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


    private float[] mProjectMatrix= new float[16];
    private float[] mViewMatrix= new float[16];
    private float[] mMVPMatrix= new float[16];

    public Cube() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mProgramId = loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        //通过OpenGL程序句柄查找获取顶点着色器中的位置句柄
        mPositionId = GLES20.glGetAttribLocation(mProgramId, "vPosition");
        //获取片元着色器的vColor成员的句柄
        mColorId = GLES20.glGetAttribLocation(mProgramId, "aColor");
        //通过OpenGL程序句柄查找获取片元着色器中的颜色句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgramId, "vMatrix");
        // 初始化顶点字节缓冲区，用于存放形状的坐标，每个浮点数占用4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(cubePositions.length * 4);
        //设置使用设备硬件的原生字节序
        bb.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        vertexBuffer.put(cubePositions);
        //设置buffer从第一个坐标开始读
        vertexBuffer.position(0);


        // 给颜色缓冲区赋值
        ByteBuffer cb = ByteBuffer.allocateDirect(color.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        // 初始化顶点字节缓冲区，用于存放形状的坐标，每个浮点数占用4个字节
        ByteBuffer ib = ByteBuffer.allocateDirect(index.length * 4);
        //设置使用设备硬件的原生字节序
        ib.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        indexBuffer = ib.asShortBuffer();
        // 把坐标都添加到FloatBuffer中
        indexBuffer.put(index);
        //设置buffer从第一个坐标开始读
        indexBuffer.position(0);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 3, 3, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //设置相机位置
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // 计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //告知OpenGL所要使用的Program
        GLES20.glUseProgram(mProgramId);
        //启用指向顶点数据的句柄
        GLES20.glEnableVertexAttribArray(mPositionId);
        //绑定坐标数据
        GLES20.glVertexAttribPointer(mPositionId, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRID, vertexBuffer);

        //启用指向颜色数据的句柄
        GLES20.glEnableVertexAttribArray(mColorId);
        //绑定颜色数据
        GLES20.glVertexAttribPointer(mColorId, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //索引法绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        //禁用顶点数据
        GLES20.glDisableVertexAttribArray(mPositionId);
    }

    @Override
    public void destroy() {
        GLES20.glDeleteProgram(mProgramId);
    }
}
