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
public class Ball extends Shape{

    //folat缓冲区
    public FloatBuffer vertexBuffer,vertexBuffer2;
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
    float step =1;
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    float[] shapePos;

       private final String VERTEX_SHADER = "uniform mat4 vMatrix;\n" +
               "varying vec4 vColor;\n" +
               "attribute vec4 vPosition;\n" +
               "\n" +
               "void main(){\n" +
               "    gl_Position=vMatrix*vPosition;\n" +
               "    float color;\n" +
               "    if(vPosition.z>0.0){\n" +
               "        color=vPosition.z;\n" +
               "    }else{\n" +
               "        color=-vPosition.z;\n" +
               "    }\n" +
               "    vColor=vec4(color,color,color,1.0);\n" +
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
        shapePos = createPositions();

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
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
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
        //绑定三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionId, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRID, vertexBuffer);

        //绑定颜色数据
        GLES20.glUniform4fv(mColorId, 1,color , 0);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //索引法绘制正方形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, shapePos.length/3);

        //禁用指向三角形的顶点数据
        GLES20.glDisableVertexAttribArray(mPositionId);
    }


    @Override
    public void destroy() {
        GLES20.glDeleteProgram(mProgramId);
    }

    private float[]  createPositions(){
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0);
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0);
            h1 = (float)Math.sin(i * Math.PI / 180.0);
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0);
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                data.add(r2 * cos);
                data.add(h2);
                data.add(r2 * sin);
                data.add(r1 * cos);
                data.add(h1);
                data.add(r1 * sin);
            }
        }
        float[] f=new float[data.size()];
        for(int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }


}