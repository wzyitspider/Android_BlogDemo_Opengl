package com.wzy.opengl.shape;
 
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Time:2019/4/25
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class Cylinder extends Shape{

    //folat缓冲区
    public FloatBuffer vertexBuffer,vertexBufferUp,vertexBufferDown;
    //folat缓冲区
    public ShortBuffer indexBuffer;
    //设置每个顶点的坐标数
    public static final int COORDS_PER_VERTEX = 3;
    //每个float类型变量为4字节,顶点之间的偏移量
    private final int VERTEX_STRID = COORDS_PER_VERTEX * 4;

//    static float triangleCoords[] = {
//            -0.5f,  0.5f, 0.0f, // top left
//            0.5f,  0.5f, 0.0f,  // top right
//            0.5f, -0.5f, 0.0f, // bottom right
//            -0.5f, -0.5f, 0.0f, // bottom left
//    };

    static short index[]={
            0,3,2,0,2,1
    };

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    int mProgramId;
    int mPositionId;
    int mColorId;
    int mMatrixHandler;

    //n=360 的时候为圆形  n = 4 的时候为正四边形
    int n = 360;

    int radius =1;
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    float[] shapePos,upCirclePos,downCirclePos;

    private final String VERTEX_SHADER =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "varying vec4 vColor;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "   if((vPosition.z==0.0)){\n" +
                    "        vColor=vec4(1,1,1,1.0);\n" +
                    "    }else {\n" +
                    "        vColor=vec4(0.1,0.1,0.1,1.0);\n" +
                    "    }"+
                    "}";

    private final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }
 
    public void setVertexBuffer(FloatBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
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

        //绘制圆锥侧面
        shapePos = createZhutiPositions(2.0f);
        //绘制圆锥底面
        upCirclePos = createPositions(0.0f,0.0f);
        //绘制圆锥底面
        downCirclePos = createPositions(2.0f,2.0f);

        // 初始化顶点字节缓冲区，用于存放形状的坐标，每个浮点数占用4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(shapePos.length * 4);
        //设置使用设备硬件的原生字节序
        bb.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        vertexBuffer.put(shapePos);
        //设置buffer从第一个坐标开始读
        vertexBuffer.position(0);

        // 初始化顶点字节缓冲区，用于存放形状的坐标，每个浮点数占用4个字节
        ByteBuffer upb = ByteBuffer.allocateDirect(upCirclePos.length * 4);
        //设置使用设备硬件的原生字节序
        upb.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        vertexBufferUp = upb.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        vertexBufferUp.put(upCirclePos);
        //设置buffer从第一个坐标开始读
        vertexBufferUp.position(0);


        // 初始化顶点字节缓冲区，用于存放形状的坐标，每个浮点数占用4个字节
        ByteBuffer downb = ByteBuffer.allocateDirect(downCirclePos.length * 4);
        //设置使用设备硬件的原生字节序
        downb.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        vertexBufferDown = downb.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        vertexBufferDown.put(downCirclePos);
        //设置buffer从第一个坐标开始读
        vertexBufferDown.position(0);

        //short 占两个字节
        ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer=cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        //eyex,eyey,eyez 代表相机在坐标轴的位置,centerX,centerY,centerZ代表物体的中心坐标，upX,upY,upZ代表相机往哪边看
        //Matrix.setLookAtM(mViewMatrix, 0, 3, 5, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, -1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //告知OpenGL所要使用的Program
        GLES20.glUseProgram(mProgramId);
        //启用指向三角形顶点数据的句柄
        GLES20.glEnableVertexAttribArray(mPositionId);
        drawStep1();
        drawStep2();
        drawStep3();
        //禁用指向三角形的顶点数据
        GLES20.glDisableVertexAttribArray(mPositionId);
    }


    public void  drawStep1(){
        //绑定三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionId, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRID, vertexBuffer);

        //绑定颜色数据
        GLES20.glUniform4fv(mColorId, 1,color , 0);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, shapePos.length/3);

    }


    public void  drawStep2(){
        //绑定三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionId, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRID, vertexBufferUp);

        //绑定颜色数据
        GLES20.glUniform4fv(mColorId, 1,color , 0);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //索引法绘制正方形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, upCirclePos.length/3);
    }

    public void  drawStep3(){
        //绑定三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionId, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRID, vertexBufferDown);

        //绑定颜色数据
        GLES20.glUniform4fv(mColorId, 1,color , 0);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //索引法绘制正方形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, downCirclePos.length/3);
    }

    @Override
    public void destroy() {
        GLES20.glDeleteProgram(mProgramId);
    }

    private float[]  createPositions(float height,float z){
        ArrayList<Float> data=new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);
        data.add(height);
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            data.add((float) (radius*Math.sin(i*Math.PI/180f)));
            data.add((float)(radius*Math.cos(i*Math.PI/180f)));
            data.add(z);
        }
        float[] f=new float[data.size()];
        for (int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }

    //获取圆柱柱体顶点
    private float[] createZhutiPositions(float height){
        ArrayList<Float> pos=new ArrayList<>();
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            pos.add((float) (radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(radius*Math.cos(i*Math.PI/180f)));
            pos.add(height);
            pos.add((float) (radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(radius*Math.cos(i*Math.PI/180f)));
            pos.add(0.0f);
        }
        float[] d=new float[pos.size()];
        for (int i=0;i<d.length;i++){
            d[i]=pos.get(i);
        }
        return d;
    }



}