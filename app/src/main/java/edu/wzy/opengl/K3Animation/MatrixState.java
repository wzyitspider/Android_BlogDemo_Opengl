package edu.wzy.opengl.K3Animation;

import android.opengl.Matrix;

import java.nio.ByteBuffer;

//存储系统矩阵状态的类
public class MatrixState {
    private static float[] mProjMatrix = new float[16];//4x4矩阵 投影用矩阵
    private static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵   
    private static float[] currMatrix;//当前变换矩阵

    //保护变换矩阵的栈
    static float[][] mStack = new float[10][16];
    static int stackTop = -1;

    public static void setInitStack()//获取不变换初始矩阵
    {
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    public static void pushMatrix()//保护变换矩阵
    {
        stackTop++;
        for (int i = 0; i < 16; i++) {
            mStack[stackTop][i] = currMatrix[i];
        }
    }

    public static void popMatrix()//恢复变换矩阵
    {
        for (int i = 0; i < 16; i++) {
            currMatrix[i] = mStack[stackTop][i];
        }
        stackTop--;
    }

    public static void translate(float x, float y, float z)//设置沿xyz轴移动
    {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    public static void scale(float x, float y, float z)//设置沿xyz轴缩放
    {
        Matrix.scaleM(currMatrix,0, x, y, z);
    }

    /**
     * //按角度旋转矩阵M（度）
     * @param mOffset 旋转速度
     * @param x x旋转角度
     * @param y y旋转角度
     * @param z z旋转角度
     */
    public static void rotate(float mOffset,float x,  float y, float z)
    {
        Matrix.rotateM(currMatrix, 0, mOffset, 0, 0, 1);
    }

    //设置摄像机
    static ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
    static float[] cameraLocation = new float[3];//摄像机位置

    /**
     * 设置设摄像机方法
     * @param cx  //摄像机位置x坐标
     * @param cy  //摄像机位置y坐标
     * @param cz  //摄像机位置z坐标
     * @param tx  //摄像机观察目标点x坐标
     * @param ty  //摄像机观察目标点y坐标
     * @param tz  //摄像机观察目标点z坐标
     * @param upx //摄像机UP向量X分量
     * @param upy //摄像机UP向量Y分量
     * @param upz //摄像机UP向量Z分量
     */
    public static void setCamera(float cx, float cy, float cz, float tx, float ty, float tz, float upx, float upy, float upz) {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
    }

    //设置透视投影参数
    public static void setProjectFrustum
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距离
            float far       //far面距离
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //设置正交投影参数
    public static void setProjectOrtho
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距离
            float far       //far面距离
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //创建用来存放最终变换矩阵的数组
    static float[] mMVPMatrix = new float[16];

    /**
     * 获取最终矩阵
     * @return
     */
    public static float[] getFinalMatrix() {
        mMVPMatrix=new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);//将摄影矩阵乘以变换举证
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);//将投影矩阵乘以上一部的结果矩阵得到最终矩阵
        return mMVPMatrix;
    }

    //获取具体物体的变换矩阵
    public static float[] getMMatrix() {
        return currMatrix;
    }
}
