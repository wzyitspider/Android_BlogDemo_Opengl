package com.wzy.opengl.shape;

import android.opengl.GLES20;
import android.util.Log;

import com.wzy.opengl.activity.LoaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Time:2019/5/16
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class Normal {
    Cube cube ;

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

    public Normal(){

    }

    private float[] matrix;
    public void setMatrix(float[] matrix){
        this.matrix=matrix;
    }

    private void initData(){
        ByteBuffer a=ByteBuffer.allocateDirect(cubePositions.length*4);
        a.order(ByteOrder.nativeOrder());
        vertexBuffer=a.asFloatBuffer();
        vertexBuffer.put(cubePositions);
        vertexBuffer.position(0);
        ByteBuffer b=ByteBuffer.allocateDirect(color.length*4);
        b.order(ByteOrder.nativeOrder());
        colorBuffer=b.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);
        ByteBuffer c=ByteBuffer.allocateDirect(index.length*2);
        c.order(ByteOrder.nativeOrder());
        indexBuffer=c.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);
    }

    public void create(){
        mProgramId= LoaderUtil.loadProgram("vary/vertex.sh","vary/fragment.sh");
        mPositionId=GLES20.glGetAttribLocation(mProgramId,"vPosition");
        mColorId=GLES20.glGetAttribLocation(mProgramId,"aColor");
        mMatrixHandler=GLES20.glGetUniformLocation(mProgramId,"vMatrix");
    }

    public void drawSelf(){

        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgramId);
        //指定vMatrix的值
        if(matrix!=null){
            GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,matrix,0);
        }
        //启用句柄
        GLES20.glEnableVertexAttribArray(mMatrixHandler);
        GLES20.glEnableVertexAttribArray(mColorId);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionId, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        //设置绘制三角形的颜色
        GLES20.glVertexAttribPointer(mColorId,4,
                GLES20.GL_FLOAT,false,
                0,colorBuffer);
        //索引法绘制正方体
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionId);
        GLES20.glDisableVertexAttribArray(mColorId);
    }

}
