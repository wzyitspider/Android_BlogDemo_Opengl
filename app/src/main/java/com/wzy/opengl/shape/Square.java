package com.wzy.opengl.shape;
 
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
public class Square extends Shape{

    //folat缓冲区
    public FloatBuffer vertexBuffer;
    //设置每个顶点的坐标数
    public static final int COORDS_PER_VERTEX = 3;

    static float triangleCoords[] = {
            -0.5f,  0.5f, 0.0f, // top left
            0.5f,  0.5f, 0.0f,  // top right
            0.5f, -0.5f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f, // bottom left
    };

    static short index[]={
            0,3,2,0,2,1
    };

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Square() {
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
    public void destroy() {

    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }
 
    public void setVertexBuffer(FloatBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}