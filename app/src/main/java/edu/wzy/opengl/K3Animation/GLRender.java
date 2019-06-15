package edu.wzy.opengl.K3Animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wzy.opengl.R;


/**
 * 开奖动画
 * Created by aaa on 2017/4/5.
 */

public class GLRender implements GLSurfaceView.Renderer {

    private GLSurfaceView mGLSurfaceView;
    private Context mContext;
    private String mVertexShader; //顶点着色器
    private String mFragmentShader; //片元着色器
    private int mProgram; //自定义渲染管线程序id
    private int mVertexLocation; //顶点位置属性引用id
    private int mTextureLocation; //纹理位置属性引用id
    private int mMvpUniform; //总变换矩阵引用id
    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mTextureBuffer;//纹理坐标数据缓冲
    private float mRatio = 1.0f;//坐标宽高比
    private final int mMaxTexture = 220;//产生的纹理id的数量
    private int[] mTextureID;//纹理id的数组

    private Bitmap mBitmap_Background;//背景
    private Bitmap mBitmap_Machine;//摇奖机
    private Bitmap[] mBitmap_Start; //摇奖机1开奖倒计时
    private Bitmap[] mBitmap_Loop; //摇奖机1开奖循环状态
    private Bitmap[] mBitmap_Start2; //摇奖机2开奖倒计时
    private Bitmap[] mBitmap_Loop2; //摇奖机2开奖循环状态
    private Bitmap[] mBitmap_Start3; //摇奖机3开奖倒计时
    private Bitmap[] mBitmap_Loop3; //摇奖机3开奖循环状态
    private Bitmap[] mBitmap_Over; //开奖停止状态
    private Bitmap[] mBitmap_Win;//开奖
    private Bitmap[] mBitmap_WinNum;//开奖号码
    private Bitmap mBitmap_WinNotice;//开奖公告
    private Bitmap[] mBitmap_NoticeNum;//开奖公告号码
    private Bitmap[] mBitmap_NoticeSum;//开奖公告号码和值
    private Bitmap[] mBitmap_Countdown;//开奖倒计时

    private int mStart_FirstLocation = 0; //开始状态位置
    private int mStart_FirstCount = 0; //开始状态计数
    private boolean mStart_FirstState = false;//摇奖机启动状态
    private boolean mStart_Firstloop = false;//摇奖机循环状态
    private boolean mStart_Firstover = false;//开奖停止
    private boolean mStart_FirstWin = false;//开奖

    private int mStart_SecondLocation = 0; //开始状态位置
    private int mStart_SecondCount = 0; //开始状态计数
    private boolean mStart_SecondState = false;//摇奖机启动状态
    private boolean mStart_Secondloop = false;//摇奖机循环状态
    private boolean mStart_Secondover = false;//开奖停止
    private boolean mStart_SecondWin = false;//开奖

    private int mStart_ThirdLocation = 0; //开始状态位置
    private int mStart_ThirdCount = 0; //开始状态计数
    private boolean mStart_ThirdState = false;//摇奖机启动状态
    private boolean mStart_Thirdloop = false;//摇奖机循环状态
    private boolean mStart_Thirdover = false;//开奖停止
    private boolean mStart_ThirdWin = false;//开奖


    private int mStart_OverCount = 0;//结束次数
    private int mStart_NoticeCount = 0;//结束次数
    private boolean mStart_NoticeState = false;

    private int mFirstNum = -1;
    private int mSecondNum = -1;
    private int mThirdNum = -1;
    private int mSum = -1;
    private float mFirstSpeed = 0;
    private float mSecondSpeed = 0;
    private float mThirdSpeed = 0;

    private float mFirstSpeed_low = 0;
    private float mSecondSpeed_low = 0;
    private float mThirdSpeed_low = 0;

    private final float STANDARD_COOED = 1.0f;//整个界面坐标
    private final float ZORE_COOED = 0.0f;//整个界面坐标
    private final float MACHINE_COOED_X = 0.27f;//摇奖器X坐标
    private final float MACHINE_COOED_Y = 0.35f;//摇奖器Y坐标
    private final float BALL_COOED = 0.22f;//移动号码坐标
    private final float WINNUM_COOED = 0.035f;//开奖号码坐标
    private final float NOTICE_COOED_X = 0.5f;//开奖公告背景X坐标
    private final float NOTICE_COOED_Y = 0.3f;//开奖公告Y坐标
    private final float NOTICE_WINNUM_COOED = 0.09f;//开奖公告开奖号码
    private final float NOTICE_SUM_COOED_X = 0.025f;//开奖公告和值x坐标
    private final float NOTICE_SUM_COOED_Y = 0.045f;//开奖公告和值y坐标

    private boolean mIsDownCount = false;//标记是否为倒计时状态
    private int mDownCountSite = 0;//当前倒计时位置
    private float mDownCountStep = 0;//倒计时放大倍数

    private boolean mIsMachine = false;

    private SoundPlayer mSoundPlayer = null;//播放声音
    private int mCount_Machine = 0;
    private int mFinish_time = 0;

    public GLRender(GLSurfaceView glSurfaceView, Context context) {
        mGLSurfaceView = glSurfaceView;
        mContext = context;
        mSoundPlayer = new SoundPlayer(context);
        initData();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化着色器
        initShader();
        //设置屏幕背景色RGBA
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);//打开深度检测
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);//关闭深度检测
        //开启混合
        GLES20.glEnable(GLES20.GL_BLEND);

        //png图片不用预乘alpha,混淆因子
        //GL_ONE：      表示使用1.0作为因子，实际上相当于完全的使用了这种颜色参与混合运算。
        //GL_ONE_MINUS_SRC_ALPHA：表示用1.0减去源颜色的alpha值来作为因子。
        //GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //关闭背面剪裁
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        // GLES20.glEnable(GLES20.GL_CULL_FACE);
        //初始化纹理ID
        initTexture(mBitmap_Background, 0);
        initTexture(mBitmap_Machine, 1);
        for (int i = 0; i < mBitmap_Start.length; i++) {
            initTexture(mBitmap_Start[i], 2 + i);
        }
        for (int i = 0; i < mBitmap_Loop.length; i++) {
            initTexture(mBitmap_Loop[i], 32 + i);
        }
        for (int i = 0; i < mBitmap_Start2.length; i++) {
            initTexture(mBitmap_Start2[i], 97 + i);
        }
        for (int i = 0; i < mBitmap_Loop2.length; i++) {
            initTexture(mBitmap_Loop2[i], 127 + i);
        }
        for (int i = 0; i < mBitmap_Start3.length; i++) {
            initTexture(mBitmap_Start3[i], 157 + i);
        }
        for (int i = 0; i < mBitmap_Loop3.length; i++) {
            initTexture(mBitmap_Loop3[i], 187 + i);
        }

        for (int i = 0; i < mBitmap_Over.length; i++) {
            initTexture(mBitmap_Over[i], 62 + i);
        }
        for (int i = 0; i < mBitmap_Win.length; i++) {
            initTexture(mBitmap_Win[i], 66 + i);
        }
        for (int i = 0; i < mBitmap_WinNum.length; i++) {
            initTexture(mBitmap_WinNum[i], 72 + i);
        }
        initTexture(mBitmap_WinNotice, 78);
        for (int i = 0; i < mBitmap_NoticeNum.length; i++) {
            initTexture(mBitmap_NoticeNum[i], 79 + i);
        }
        for (int i = 0; i < mBitmap_NoticeSum.length; i++) {
            initTexture(mBitmap_NoticeSum[i], 85 + i);
        }

//        initTexture(mBitmap_Countdown[mDownCountSite], 96);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视窗大小及位置
        GLES20.glViewport(0, 0, width, height);
        //计算GLSurfaceView的宽高比
        mRatio = (float) width / height;
        //初始化顶点及片元坐标
        initVertexData();
        //调用此方法计算产生正交投影矩阵
        MatrixState.setProjectOrtho(-mRatio, mRatio, -1, 1, 1, 10);
        //调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除深度缓冲与颜色缓冲
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //画背景
        drawBackGround();

        //倒计时
        drawCountDown();

        //画摇奖机
        if (mIsMachine) {
            drawMachineGround();
        }


        //******第一台摇奖机效果******/
        //摇奖机启动
        if (mStart_FirstState)
            drawStar_FirstMachine();
        //摇奖机循转
        if (mStart_Firstloop)
            drawLoop_FirstMachine();
        //摇奖机停止
        if (mStart_Firstover) {
            drawWinNum_FirstMachine(mFirstNum);
            drawEnd_FirstMachine();
        }
        if (mStart_FirstWin) {
            drawWinNum_Machine(0.0f, 0.6f, mFirstNum);
            drawWin_Machine(0.0f, 0.355f, mFirstNum);
        }

        //******第二台摇奖机效果******/
        //摇奖机启动
        if (mStart_SecondState)
            drawStar_SecondMachine();
        //摇奖机循转
        if (mStart_Secondloop)
            drawLoop_SecondMachine();
        //摇奖机停止
        if (mStart_Secondover) {
            drawWinNum_SecondMachine(mSecondNum);
            drawEnd_SecondMachine();
        }
        if (mStart_SecondWin) {
            drawWinNum_Machine(-0.33f, -0.1f, mSecondNum);
            drawWin_Machine(-0.33f, -0.345f, mSecondNum);
        }

        //******第三台摇奖机效果******/
        //摇奖机启动
        if (mStart_ThirdState)
            drawStar_ThirdMachine();
        //摇奖机循转
        if (mStart_Thirdloop)
            drawLoop_ThirdMachine();
        //摇奖机停止
        if (mStart_Thirdover) {
            drawWinNum_ThirdMachine(mThirdNum);
            drawEnd_ThirdMachine();
        }
        if (mStart_ThirdWin) {
            drawWinNum_Machine(0.33f, -0.1f, mThirdNum);
            drawWin_Machine(0.33f, -0.345f, mThirdNum);
        }

        //******开奖公告效果******/
        if (mStart_NoticeState)
            drawWinNoticeGround(mFirstNum, mSecondNum, mThirdNum);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        mTextureID = new int[mMaxTexture];
        //初始化图片
        mBitmap_Background = BitmapUtils.readBitmap(mContext, R.mipmap.ic_k3_bj);
        mBitmap_Machine = BitmapUtils.readBitmap(mContext, R.mipmap.ic_k3_machine);
//        int start[] = {R.mipmap.qiu_start_00000, R.mipmap.qiu_start_00002, R.mipmap.qiu_start_00004, R.mipmap.qiu_start_00006, R.mipmap.qiu_start_00008
//                , R.mipmap.qiu_start_00010, R.mipmap.qiu_start_00012, R.mipmap.qiu_start_00014, R.mipmap.qiu_start_00016, R.mipmap.qiu_start_00018
//                , R.mipmap.qiu_start_00020, R.mipmap.qiu_start_00022, R.mipmap.qiu_start_00024, R.mipmap.qiu_start_00026, R.mipmap.qiu_start_00028};
        int start[] = {R.mipmap.qiu_start_00000, R.mipmap.qiu_start_00001, R.mipmap.qiu_start_00002, R.mipmap.qiu_start_00003, R.mipmap.qiu_start_00004
                , R.mipmap.qiu_start_00005, R.mipmap.qiu_start_00006, R.mipmap.qiu_start_00007, R.mipmap.qiu_start_00008, R.mipmap.qiu_start_00009
                , R.mipmap.qiu_start_00010, R.mipmap.qiu_start_00011, R.mipmap.qiu_start_00012, R.mipmap.qiu_start_00013, R.mipmap.qiu_start_00014
                , R.mipmap.qiu_start_00015, R.mipmap.qiu_start_00016, R.mipmap.qiu_start_00017, R.mipmap.qiu_start_00018, R.mipmap.qiu_start_00019
                , R.mipmap.qiu_start_00020, R.mipmap.qiu_start_00021, R.mipmap.qiu_start_00022, R.mipmap.qiu_start_00023, R.mipmap.qiu_start_00024
                , R.mipmap.qiu_start_00025, R.mipmap.qiu_start_00026, R.mipmap.qiu_start_00027, R.mipmap.qiu_start_00028, R.mipmap.qiu_start_00029};
        mBitmap_Start = new Bitmap[30];
        for (int i = 0; i < start.length; i++) {
            mBitmap_Start[i] = BitmapUtils.readBitmap(mContext, start[i]);
        }
        int loop[] = {R.mipmap.qiu_loop_00030, R.mipmap.qiu_loop_00031, R.mipmap.qiu_loop_00032, R.mipmap.qiu_loop_00033, R.mipmap.qiu_loop_00034
                , R.mipmap.qiu_loop_00035, R.mipmap.qiu_loop_00036, R.mipmap.qiu_loop_00037, R.mipmap.qiu_loop_00038, R.mipmap.qiu_loop_00039
                , R.mipmap.qiu_loop_00040, R.mipmap.qiu_loop_00041, R.mipmap.qiu_loop_00042, R.mipmap.qiu_loop_00043, R.mipmap.qiu_loop_00044
                , R.mipmap.qiu_loop_00045, R.mipmap.qiu_loop_00046, R.mipmap.qiu_loop_00047, R.mipmap.qiu_loop_00048, R.mipmap.qiu_loop_00049
                , R.mipmap.qiu_loop_00050, R.mipmap.qiu_loop_00051, R.mipmap.qiu_loop_00052, R.mipmap.qiu_loop_00053, R.mipmap.qiu_loop_00054
                , R.mipmap.qiu_loop_00055, R.mipmap.qiu_loop_00056, R.mipmap.qiu_loop_00057, R.mipmap.qiu_loop_00058, R.mipmap.qiu_loop_00059};
        mBitmap_Loop = new Bitmap[30];
        for (int i = 0; i < loop.length; i++) {
            mBitmap_Loop[i] = BitmapUtils.readBitmap(mContext, loop[i]);
        }
        int over[] = {R.mipmap.qiu_end_00091, R.mipmap.qiu_end_00092, R.mipmap.qiu_end_00093, R.mipmap.qiu_end_00094};
        mBitmap_Over = new Bitmap[4];
        for (int i = 0; i < over.length; i++) {
            mBitmap_Over[i] = BitmapUtils.readBitmap(mContext, over[i]);
        }
        int win[] = {R.mipmap.qiu_1, R.mipmap.qiu_2, R.mipmap.qiu_3, R.mipmap.qiu_4, R.mipmap.qiu_5, R.mipmap.qiu_6};
        mBitmap_Win = new Bitmap[6];
        for (int i = 0; i < win.length; i++) {
            mBitmap_Win[i] = BitmapUtils.readBitmap(mContext, win[i]);
        }
        int winNum[] = {R.mipmap.k3_s_ball_01_02, R.mipmap.k3_s_ball_02_02, R.mipmap.k3_s_ball_03_02, R.mipmap.k3_s_ball_04_01, R.mipmap.k3_s_ball_05_02, R.mipmap.k3_s_ball_06_02};
        mBitmap_WinNum = new Bitmap[6];
        for (int i = 0; i < winNum.length; i++) {
            mBitmap_WinNum[i] = BitmapUtils.readBitmap(mContext, winNum[i]);
        }
        mBitmap_WinNotice = BitmapUtils.readBitmap(mContext, R.mipmap.k3_s_kjtc_db);
        int NoticeWinNum[] = {R.mipmap.k3_s_kjtc_1, R.mipmap.k3_s_kjtc_2, R.mipmap.k3_s_kjtc_3, R.mipmap.k3_s_kjtc_4, R.mipmap.k3_s_kjtc_5, R.mipmap.k3_s_kjtc_6};
        mBitmap_NoticeNum = new Bitmap[6];
        for (int i = 0; i < NoticeWinNum.length; i++) {
            mBitmap_NoticeNum[i] = BitmapUtils.readBitmap(mContext, NoticeWinNum[i]);
        }
        int NoticeSum[] = {R.mipmap.k3_s_qh_0, R.mipmap.k3_s_qh_1, R.mipmap.k3_s_qh_2, R.mipmap.k3_s_qh_3, R.mipmap.k3_s_qh_4, R.mipmap.k3_s_qh_5
                , R.mipmap.k3_s_qh_6, R.mipmap.k3_s_qh_7, R.mipmap.k3_s_qh_8, R.mipmap.k3_s_qh_9};
        mBitmap_NoticeSum = new Bitmap[10];
        for (int i = 0; i < NoticeSum.length; i++) {
            mBitmap_NoticeSum[i] = BitmapUtils.readBitmap(mContext, NoticeSum[i]);
        }
        int CountDownSum[] = {R.mipmap.n10, R.mipmap.n9, R.mipmap.n8, R.mipmap.n7, R.mipmap.n6, R.mipmap.n5
                , R.mipmap.n4, R.mipmap.n3, R.mipmap.n2, R.mipmap.n1};
        mBitmap_Countdown = new Bitmap[10];
        for (int i = 0; i < CountDownSum.length; i++) {
            mBitmap_Countdown[i] = BitmapUtils.readBitmap(mContext, CountDownSum[i]);
        }
        int start2[] = {R.mipmap.qiu2_start_00000, R.mipmap.qiu2_start_00001, R.mipmap.qiu2_start_00002, R.mipmap.qiu2_start_00003, R.mipmap.qiu2_start_00004
                , R.mipmap.qiu2_start_00005, R.mipmap.qiu2_start_00006, R.mipmap.qiu2_start_00007, R.mipmap.qiu2_start_00008, R.mipmap.qiu2_start_00009
                , R.mipmap.qiu2_start_00010, R.mipmap.qiu2_start_00011, R.mipmap.qiu2_start_00012, R.mipmap.qiu2_start_00013, R.mipmap.qiu2_start_00014
                , R.mipmap.qiu2_start_00015, R.mipmap.qiu2_start_00016, R.mipmap.qiu2_start_00017, R.mipmap.qiu2_start_00018, R.mipmap.qiu2_start_00019
                , R.mipmap.qiu2_start_00020, R.mipmap.qiu2_start_00021, R.mipmap.qiu2_start_00022, R.mipmap.qiu2_start_00023, R.mipmap.qiu2_start_00024
                , R.mipmap.qiu2_start_00025, R.mipmap.qiu2_start_00026, R.mipmap.qiu2_start_00027, R.mipmap.qiu2_start_00028, R.mipmap.qiu2_start_00029};
        mBitmap_Start2 = new Bitmap[30];
        for (int i = 0; i < start2.length; i++) {
            mBitmap_Start2[i] = BitmapUtils.readBitmap(mContext, start2[i]);
        }
        int loop2[] = {R.mipmap.qiu2_loop_00030, R.mipmap.qiu2_loop_00031, R.mipmap.qiu2_loop_00032, R.mipmap.qiu2_loop_00033, R.mipmap.qiu2_loop_00034
                , R.mipmap.qiu2_loop_00035, R.mipmap.qiu2_loop_00036, R.mipmap.qiu2_loop_00037, R.mipmap.qiu2_loop_00038, R.mipmap.qiu2_loop_00039
                , R.mipmap.qiu2_loop_00040, R.mipmap.qiu2_loop_00041, R.mipmap.qiu2_loop_00042, R.mipmap.qiu2_loop_00043, R.mipmap.qiu2_loop_00044
                , R.mipmap.qiu2_loop_00045, R.mipmap.qiu2_loop_00046, R.mipmap.qiu2_loop_00047, R.mipmap.qiu2_loop_00048, R.mipmap.qiu2_loop_00049
                , R.mipmap.qiu2_loop_00050, R.mipmap.qiu2_loop_00051, R.mipmap.qiu2_loop_00052, R.mipmap.qiu2_loop_00053, R.mipmap.qiu2_loop_00054
                , R.mipmap.qiu2_loop_00055, R.mipmap.qiu2_loop_00056, R.mipmap.qiu2_loop_00057, R.mipmap.qiu2_loop_00058, R.mipmap.qiu2_loop_00059};
        mBitmap_Loop2 = new Bitmap[30];
        for (int i = 0; i < loop2.length; i++) {
            mBitmap_Loop2[i] = BitmapUtils.readBitmap(mContext, loop2[i]);
        }
        int star3[] = {R.mipmap.qiu3_star_00000, R.mipmap.qiu3_star_00001, R.mipmap.qiu3_star_00002, R.mipmap.qiu3_star_00003, R.mipmap.qiu3_star_00004
                , R.mipmap.qiu3_star_00005, R.mipmap.qiu3_star_00006, R.mipmap.qiu3_star_00007, R.mipmap.qiu3_star_00008, R.mipmap.qiu3_star_00009
                , R.mipmap.qiu3_star_00010, R.mipmap.qiu3_star_00011, R.mipmap.qiu3_star_00012, R.mipmap.qiu3_star_00013, R.mipmap.qiu3_star_00014
                , R.mipmap.qiu3_star_00015, R.mipmap.qiu3_star_00016, R.mipmap.qiu3_star_00017, R.mipmap.qiu3_star_00018, R.mipmap.qiu3_star_00019
                , R.mipmap.qiu3_star_00020, R.mipmap.qiu3_star_00021, R.mipmap.qiu3_star_00022, R.mipmap.qiu3_star_00023, R.mipmap.qiu3_star_00024
                , R.mipmap.qiu3_star_00025, R.mipmap.qiu3_star_00026, R.mipmap.qiu3_star_00027, R.mipmap.qiu3_star_00028, R.mipmap.qiu3_star_00029};
        mBitmap_Start3 = new Bitmap[30];
        for (int i = 0; i < star3.length; i++) {
            mBitmap_Start3[i] = BitmapUtils.readBitmap(mContext, star3[i]);
        }
        int loop3[] = {R.mipmap.qiu3_loop_00030, R.mipmap.qiu3_loop_00031, R.mipmap.qiu3_loop_00032, R.mipmap.qiu3_loop_00033, R.mipmap.qiu3_loop_00034
                , R.mipmap.qiu3_loop_00035, R.mipmap.qiu3_loop_00036, R.mipmap.qiu3_loop_00037, R.mipmap.qiu3_loop_00038, R.mipmap.qiu3_loop_00039
                , R.mipmap.qiu3_loop_00040, R.mipmap.qiu3_loop_00041, R.mipmap.qiu3_loop_00042, R.mipmap.qiu3_loop_00043, R.mipmap.qiu3_loop_00044
                , R.mipmap.qiu3_loop_00045, R.mipmap.qiu3_loop_00046, R.mipmap.qiu3_loop_00047, R.mipmap.qiu3_loop_00048, R.mipmap.qiu3_loop_00049
                , R.mipmap.qiu3_loop_00050, R.mipmap.qiu3_loop_00051, R.mipmap.qiu3_loop_00052, R.mipmap.qiu3_loop_00053, R.mipmap.qiu3_loop_00054
                , R.mipmap.qiu3_loop_00055, R.mipmap.qiu3_loop_00056, R.mipmap.qiu3_loop_00057, R.mipmap.qiu3_loop_00058, R.mipmap.qiu3_loop_00059};
        mBitmap_Loop3 = new Bitmap[30];
        for (int i = 0; i < loop3.length; i++) {
            mBitmap_Loop3[i] = BitmapUtils.readBitmap(mContext, loop3[i]);
        }
    }

    /**
     * 初始化顶点数据
     */
    private void initVertexData() {
        float vertexArray[] = {   //顶点坐标
                -STANDARD_COOED, -STANDARD_COOED, ZORE_COOED,    //背景
                STANDARD_COOED, -STANDARD_COOED, ZORE_COOED,
                -STANDARD_COOED, STANDARD_COOED, ZORE_COOED,
                STANDARD_COOED, STANDARD_COOED, ZORE_COOED,

                -MACHINE_COOED_X, -MACHINE_COOED_Y, ZORE_COOED,    //摇奖器
                MACHINE_COOED_X, -MACHINE_COOED_Y, ZORE_COOED,
                -MACHINE_COOED_X, MACHINE_COOED_Y, ZORE_COOED,
                MACHINE_COOED_X, MACHINE_COOED_Y, ZORE_COOED,

                -BALL_COOED, -BALL_COOED, ZORE_COOED,    //球体轨迹
                BALL_COOED, -BALL_COOED, ZORE_COOED,
                -BALL_COOED, BALL_COOED, ZORE_COOED,
                BALL_COOED, BALL_COOED, ZORE_COOED,

                -WINNUM_COOED, -WINNUM_COOED, ZORE_COOED,    //开奖号码
                WINNUM_COOED, -WINNUM_COOED, ZORE_COOED,
                -WINNUM_COOED, WINNUM_COOED, ZORE_COOED,
                WINNUM_COOED, WINNUM_COOED, ZORE_COOED,

                -NOTICE_COOED_X, -NOTICE_COOED_Y, ZORE_COOED,    //开奖公告
                NOTICE_COOED_X, -NOTICE_COOED_Y, ZORE_COOED,
                -NOTICE_COOED_X, NOTICE_COOED_Y, ZORE_COOED,
                NOTICE_COOED_X, NOTICE_COOED_Y, ZORE_COOED,

                -NOTICE_WINNUM_COOED, -NOTICE_WINNUM_COOED, ZORE_COOED,    //开奖公告开奖号码
                NOTICE_WINNUM_COOED, -NOTICE_WINNUM_COOED, ZORE_COOED,
                -NOTICE_WINNUM_COOED, NOTICE_WINNUM_COOED, ZORE_COOED,
                NOTICE_WINNUM_COOED, NOTICE_WINNUM_COOED, ZORE_COOED,

                -NOTICE_SUM_COOED_X, -NOTICE_SUM_COOED_Y, ZORE_COOED,    //开奖公告和值
                NOTICE_SUM_COOED_X, -NOTICE_SUM_COOED_Y, ZORE_COOED,
                -NOTICE_SUM_COOED_X, NOTICE_SUM_COOED_Y, ZORE_COOED,
                NOTICE_SUM_COOED_X, NOTICE_SUM_COOED_Y, ZORE_COOED,

                -0.5f, -0.5f, 0.0f,    //倒计时
                0.5f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
        };
        float textureArray[] = {   //纹理坐标
                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,

                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,

                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,

                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,

                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,

                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,

                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,

                ZORE_COOED, STANDARD_COOED,
                STANDARD_COOED, STANDARD_COOED,
                ZORE_COOED, ZORE_COOED,
                STANDARD_COOED, ZORE_COOED,
        };
//
//        vertexArray[0] = -mRatio;
//        vertexArray[3] = mRatio;
//        vertexArray[6] = -mRatio;
//        vertexArray[9] = mRatio;

        //创建顶点坐标数据缓冲
        //vertexArray.length*4是因为一个整数四个字节
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = byteBuffer.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertexArray);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        byteBuffer.clear();

        //创建纹理坐标数据缓冲
        //textureArray.length*4是因为一个整数四个字节
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(textureArray.length * 4);
        byteBuffer2.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer = byteBuffer2.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textureArray);//向缓冲区中放入纹理坐标数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        byteBuffer2.clear();

        //为画笔指定顶点位置数据，将顶点坐标位置传递进渲染管线，以便管线经过基本处理后将对应的值传递给顶点着色器中的属性（attribute）变量
        GLES20.glVertexAttribPointer
                (
                        mVertexLocation, //顶点位置属性引用
                        3,               //每个顶点的数据个数（这是是x,y,z坐标，所以为3）
                        GLES20.GL_FLOAT, //数据类型
                        false,           //是否格式化
                        3 * 4,           //每个数据的尺寸，这里每组3个浮点数组(X,Y,Z轴坐标)，每个浮点数4个字节，共3*4个字节
                        mVertexBuffer    //存放了数据的缓存
                );
        //为画笔指定顶点纹理坐标数据，将纹理坐标位置传递进渲染管线，，以便管线经过基本处理后将对应的值传递给顶点着色器中的属性（attribute）变量
        GLES20.glVertexAttribPointer
                (
                        mTextureLocation,  //纹理位置属性引用
                        2,                 //每个顶点的数据个数（这是是x,y坐标，所以为2）
                        GLES20.GL_FLOAT,   //数据类型
                        false,             //是否格式化
                        2 * 4,             //每个数据的尺寸，这里每组2个浮点数组(X,Y轴坐标)，每个浮点数4个字节，共2*4个字节
                        mTextureBuffer     //存放了数据的缓存
                );

        //允许用到的属性数据数组
        GLES20.glEnableVertexAttribArray(mVertexLocation);//启用顶点位置数据
        GLES20.glEnableVertexAttribArray(mTextureLocation);//启用纹理位置坐标数据
    }

    /**
     * 初始化着色器
     */
    private void initShader() {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mGLSurfaceView.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("fragment.sh", mGLSurfaceView.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取mProgram程序中顶点位置属性引用id
        mVertexLocation = GLES20.glGetAttribLocation(mProgram, "vertexPosition");
        //获取mProgram程序中纹理位置属性引用id
        mTextureLocation = GLES20.glGetAttribLocation(mProgram, "textureCoor");
        //获取程序中总变换矩阵引用id
        mMvpUniform = GLES20.glGetUniformLocation(mProgram, "mvpMatrix");
//        //获取程序一致变量(纹理采样器)引用id
//        mTextureUniform = GLES20.glGetUniformLocation(mProgram, "textureUniform");
    }

//    private int createTexture(){
//        int[] texture=new int[1];
//        if(mBitmap!=null&&!mBitmap.isRecycled()){
//            //生成纹理
//            GLES20.glGenTextures(1,texture,0);
//            //生成纹理
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
//            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
//            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
//            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
//            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
//            //根据以上指定的参数，生成一个2D纹理
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
//            return texture[0];
//        }
//        return 0;
//    }
//

    /**
     * 初始化纹理,根据ID获取对应的纹理背景glBindTexture
     */
    private void initTexture(Bitmap bitmap, int ID) {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1,     //产生的纹理id的数量
                textures,   //纹理id的数组
                0             //偏移量
        );
        mTextureID[ID] = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[ID]);//绑定对应的textureID
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);//设置mix时为线性采样
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);//设置MAG时为线性采样
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);//设置S轴的拉伸方式为截取
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);//设置T轴的拉伸方式为截取
//        Log.v("data", "此时的bitmap_Background==" + bitmap);
        //实际加载纹理进显存
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmap,                 //纹理图像
                        0                       //纹理边框尺寸
                );
        bitmap.recycle();          //纹理加载成功后释放图片
    }

    /**
     * 初始化纹理,根据ID获取对应的纹理背景（暂不释放图片资源）
     */
    private void initTextureNODestory(Bitmap bitmap, int ID) {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,     //产生的纹理id的数量
                        textures,   //纹理id的数组
                        0             //偏移量
                );
        mTextureID[ID] = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[ID]);//绑定对应的textureID
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);//设置mix时为线性采样
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);//设置MAG时为线性采样
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);//设置S轴的拉伸方式为截取
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);//设置T轴的拉伸方式为截取
//        Log.v("data", "此时的bitmap_Background==" + bitmap);
        //实际加载纹理进显存
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmap,                 //纹理图像
                        0                       //纹理边框尺寸
                );
    }

    /**
     * 画背景
     */
    private void drawBackGround() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
//        //将最终变换矩阵（一致性变量）传入渲染管线
//        GLES20.glUniformMatrix4fv(mMvpUniform, 1, false, MatrixState.getFinalMatrix(), 0);
//        //绑定纹理
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//设置使用的纹理编号
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[0]);//绑定指定的纹理ID
//        //绘制纹理矩形
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        drawArrays(mMvpUniform, 0, 0, 4);
    }

    /**
     * 画摇奖机
     */
    private void drawMachineGround() {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(0, 0.3f, 0);
        drawArrays(mMvpUniform, 1, 4, 4);

        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(-0.33f, -0.4f, 0);
        drawArrays(mMvpUniform, 1, 4, 4);

        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(0.33f, -0.4f, 0);
        drawArrays(mMvpUniform, 1, 4, 4);
    }

    /**
     * 开启摇奖机
     */
    private void drawStar_FirstMachine() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
        MatrixState.translate(0, 0.355f, 0);
        drawArrays(mMvpUniform, 2 + mStart_FirstLocation, 8, 4);
        mStart_FirstCount++;
        if (mStart_FirstCount > 5) {
            mStart_FirstCount = 0;
            mStart_FirstLocation++;
            mSoundPlayer.playSound(SoundPlayer.TYPE_DRAW, 0);
        }
        if (mStart_FirstLocation >= mBitmap_Start.length) {
            mStart_FirstLocation = 0;
            mStart_FirstCount = 0;
            mStart_FirstState = false;
            mStart_Firstloop = true;
            mSoundPlayer.playSound(SoundPlayer.TYPE_DRAW, 0);
        }
    }

    /**
     * 开启摇奖机
     */
    private void drawStar_SecondMachine() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
        MatrixState.translate(-0.33f, -0.345f, 0);
        drawArrays(mMvpUniform, 97 + mStart_SecondLocation, 8, 4);
        mStart_SecondCount++;
        if (mStart_SecondCount > 5) {
            mStart_SecondCount = 0;
            mStart_SecondLocation++;
        }
        if (mStart_SecondLocation >= mBitmap_Start2.length) {
            mStart_SecondLocation = 0;
            mStart_SecondCount = 0;
            mStart_SecondState = false;
            mStart_Secondloop = true;
            mSoundPlayer.playSound(SoundPlayer.TYPE_DRAW, 0);
        }
    }

    /**
     * 开启摇奖机
     */
    private void drawStar_ThirdMachine() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
        MatrixState.translate(0.33f, -0.345f, 0);
        drawArrays(mMvpUniform, 157 + mStart_ThirdLocation, 8, 4);
        mStart_ThirdCount++;
        if (mStart_ThirdCount > 5) {
            mStart_ThirdCount = 0;
            mStart_ThirdLocation++;
        }
        if (mStart_ThirdLocation >= mBitmap_Start3.length) {
            mStart_ThirdLocation = 0;
            mStart_ThirdCount = 0;
            mStart_ThirdState = false;
            mStart_Thirdloop = true;
        }
    }

    /**
     * 循环摇奖机
     */
    private void drawLoop_FirstMachine() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
        MatrixState.translate(0, 0.355f, 0);
        drawArrays(mMvpUniform, 32 + mStart_FirstLocation, 8, 4);
        mStart_FirstCount++;
        if (mStart_FirstCount > 3) {
            mStart_FirstCount = 0;
            mStart_FirstLocation++;

        }
        if (mStart_FirstLocation >= mBitmap_Loop.length) {
            mStart_FirstLocation = 0;
        }
    }

    /**
     * 循环摇奖机
     */
    private void drawLoop_SecondMachine() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
        MatrixState.translate(-0.33f, -0.345f, 0);
        drawArrays(mMvpUniform, 127 + mStart_SecondLocation, 8, 4);
        mStart_SecondCount++;
        if (mStart_SecondCount > 3) {
            mStart_SecondCount = 0;
            mStart_SecondLocation++;
        }
        if (mStart_SecondLocation >= mBitmap_Loop.length) {
            mStart_SecondLocation = 0;
        }

        if (mStart_FirstWin) {  //一号摇奖机已开奖
            mStart_OverCount++;
            if (mStart_OverCount > 50) {
                mStart_OverCount = 0;
                mStart_SecondLocation = 0;
                mStart_SecondCount = 0;
                mSecondSpeed = 0;
                mStart_Secondloop = false;
                mStart_Secondover = true;
            }
        } else {
            if (mCount_Machine % 50 == 0) {
                mSoundPlayer.playSound(SoundPlayer.TYPE_DRAW, 0);
                mCount_Machine = 0;
            }
            mCount_Machine++;
        }
    }

    /**
     * 循环摇奖机
     */
    private void drawLoop_ThirdMachine() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
        MatrixState.translate(0.33f, -0.345f, 0);
        drawArrays(mMvpUniform, 187 + mStart_ThirdLocation, 8, 4);
        mStart_ThirdCount++;
        if (mStart_ThirdCount > 3) {
            mStart_ThirdCount = 0;
            mStart_ThirdLocation++;
        }
        if (mStart_ThirdLocation >= mBitmap_Loop3.length) {
            mStart_ThirdLocation = 0;
        }


        if (mStart_SecondWin) {  //二号摇奖机已开奖
            mStart_OverCount++;
            if (mStart_OverCount > 50) {
                mStart_OverCount = 0;
                mStart_ThirdLocation = 0;
                mStart_ThirdCount = 0;
                mThirdSpeed = 0;
                mStart_Thirdloop = false;
                mStart_Thirdover = true;
            }
        }
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawEnd_FirstMachine() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //获取不变换初始矩阵
        MatrixState.setInitStack();
        MatrixState.translate(0, 0.355f, 0);
        drawArrays(mMvpUniform, 62 + mStart_FirstLocation, 8, 4);
        mStart_FirstCount++;
        if (mStart_FirstCount > 4) {
            mStart_FirstCount = 0;
            mStart_FirstLocation++;
        }
        if (mStart_FirstLocation >= mBitmap_Over.length) {
            mStart_FirstLocation = 0;
            mStart_Firstover = false;
            mStart_FirstWin = true;
        }
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawEnd_SecondMachine() {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(-0.33f, -0.345f, 0);
        drawArrays(mMvpUniform, 62 + mStart_SecondLocation, 8, 4);
        mStart_SecondCount++;
        if (mStart_SecondCount > 3) {
            mStart_SecondCount = 0;
            mStart_SecondLocation++;
        }
        if (mStart_SecondLocation >= mBitmap_Over.length) {
            mStart_SecondLocation = 0;
            mStart_Secondover = false;
            mStart_SecondWin = true;
        }
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawEnd_ThirdMachine() {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(0.33f, -0.345f, 0);
        drawArrays(mMvpUniform, 62 + mStart_ThirdLocation, 8, 4);
        mStart_ThirdCount++;
        if (mStart_ThirdCount > 3) {
            mStart_ThirdCount = 0;
            mStart_ThirdLocation++;
        }
        if (mStart_ThirdLocation >= mBitmap_Over.length) {
            mStart_ThirdLocation = 0;
            mStart_Thirdover = false;
            mStart_ThirdWin = true;
        }
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawWinNum_Machine(float x, float y, int num) {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(x, y, 0);
        drawArrays(mMvpUniform, 71 + num, 12, 4);

        if (mStart_ThirdWin && !mStart_NoticeState) {  //开奖已结束，准备显示开奖公告
            mStart_NoticeCount++;
            if (mStart_NoticeCount >= 200) {
                mStart_NoticeState = true;
            }
        }
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawWin_Machine(float x, float y, int num) {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(x, y, 0);
        drawArrays(mMvpUniform, 65 + num, 8, 4);
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawWinNum_FirstMachine(int num) {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        if (mFirstSpeed < 0.06f) {
            mFirstSpeed = mFirstSpeed + 0.005f;
            mFirstSpeed_low = mFirstSpeed;
        } else {
            if (mFirstSpeed_low > 0.04f) {
                mFirstSpeed_low = mFirstSpeed_low - 0.005f;
            }
        }
        MatrixState.translate(0, 0.56f + mFirstSpeed_low, 0);
        drawArrays(mMvpUniform, 71 + num, 12, 4);
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawWinNum_SecondMachine(int num) {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        if (mSecondSpeed < 0.06f) {
            mSecondSpeed = mSecondSpeed + 0.005f;
            mSecondSpeed_low = mSecondSpeed;
        } else {
            if (mSecondSpeed_low > 0.04f) {
                mSecondSpeed_low = mSecondSpeed_low - 0.005f;
            }
        }
        MatrixState.translate(-0.33f, -0.14f + mSecondSpeed_low, 0);
        drawArrays(mMvpUniform, 71 + num, 12, 4);
    }

    /**
     * 摇奖机停止并开奖
     */
    private void drawWinNum_ThirdMachine(int num) {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        if (mThirdSpeed < 0.06f) {
            mThirdSpeed = mThirdSpeed + 0.005f;
            mThirdSpeed_low = mThirdSpeed;
        } else {
            if (mThirdSpeed_low > 0.04f) {
                mThirdSpeed_low = mThirdSpeed_low - 0.005f;
            }
        }
        MatrixState.translate(0.33f, -0.14f + mThirdSpeed_low, 0);
        drawArrays(mMvpUniform, 71 + num, 12, 4);
    }

    /**
     * 画开奖公告
     */
    private void drawWinNoticeGround(int firstNum, int secondNum, int ThirdNum) {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(0, 0.2f, 0);
        drawArrays(mMvpUniform, 78, 16, 4);

        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(-0.1f, 0.265f, 0);
        drawArrays(mMvpUniform, 78 + firstNum, 20, 4);

        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(0.12f, 0.265f, 0);
        drawArrays(mMvpUniform, 78 + secondNum, 20, 4);

        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(0.345f, 0.265f, 0);
        drawArrays(mMvpUniform, 78 + ThirdNum, 20, 4);

        if (mSum < 10) {
            GLES20.glUseProgram(mProgram);
            MatrixState.setInitStack();
            MatrixState.translate(0.12f, 0.095f, 0);
            drawArrays(mMvpUniform, 85 + mSum, 24, 4);
        } else {
            GLES20.glUseProgram(mProgram);
            MatrixState.setInitStack();
            MatrixState.translate(0.1f, 0.095f, 0);
            drawArrays(mMvpUniform, 86, 24, 4);
            MatrixState.translate(0.04f, 0, 0);
            drawArrays(mMvpUniform, 85 + (mSum - 10), 24, 4);
        }

        if (mFinish_time == 20) {
            mSoundPlayer.playSound(SoundPlayer.TYPE_SAY, 0);
        }
        if (mFinish_time == 140) {
            mSoundPlayer.playSound(firstNum + 4, 0);
        }
        if (mFinish_time == 190) {
            mSoundPlayer.playSound(secondNum + 4, 0);
        }
        if (mFinish_time == 250) {
            mSoundPlayer.playSound(ThirdNum + 4, 0);
        }
        if (mFinish_time == 300) {
            mSoundPlayer.playSound(SoundPlayer.TYPE_GOODLUCK, 0);
        }
        mFinish_time++;
    }

    /**
     * 开奖倒计时
     */
    private void drawCountDown() {
        if (mIsDownCount) {
            //制定使用某套shader程序
            GLES20.glUseProgram(mProgram);
            if (mDownCountSite == 0 && mDownCountStep == 0) {  //第一次加载倒计时声音
                initTextureNODestory(mBitmap_Countdown[mDownCountSite], 96);
                mSoundPlayer.playSound(SoundPlayer.TYPE_COUNTDOWN, 0);
            }
            //获取不变换初始矩阵
            MatrixState.setInitStack();
            if (mDownCountStep > 0.3f) {
                mDownCountSite++;
                if (mDownCountSite < mBitmap_Countdown.length) {
                    mDownCountStep = 0;
                    initTextureNODestory(mBitmap_Countdown[mDownCountSite], 96);
                    mSoundPlayer.playSound(SoundPlayer.TYPE_COUNTDOWN, 0);
                } else {
                    mIsDownCount = false;
                    mIsMachine = true; //开启开奖动画
                    mStart_FirstState = true;
                    mStart_SecondState = true;
                    mStart_ThirdState = true;
                    mSoundPlayer.playSound(SoundPlayer.TYPE_DRAW, 0);
                }
            } else {
                mDownCountStep = mDownCountStep + 0.005f;
            }
            MatrixState.scale(0.2f + mDownCountStep, 0.2f + mDownCountStep, 0);
            //将最终变换矩阵（一致性变量）传入渲染管线
            GLES20.glUniformMatrix4fv(mMvpUniform, 1, false, MatrixState.getFinalMatrix(), 0);
            //绑定纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//设置使用的纹理编号
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[96]);//绑定指定的纹理ID

            //绘制纹理矩形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 28, 4);
        } else {
            return;
        }
    }


    /**
     * 绘制纹理
     */
    private void drawArrays(int location, int texture_id, int first, int count) {
        //将最终变换矩阵（一致性变量）传入渲染管线
        GLES20.glUniformMatrix4fv(location, 1, false, MatrixState.getFinalMatrix(), 0);
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[texture_id]);//绑定指定的纹理ID
        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, first, count);
    }

    /**
     * 开启摇奖器
     */
    public void play() {
        cleanState();
        mIsDownCount = true;
    }

    /**
     * 停止
     */
    public void stop() {
        cleanState();
    }

    /**
     * 开奖
     *
     * @param fistNum   开奖号码1
     * @param secondNum 开奖号码2
     * @param thridNum  开奖号码3
     */
    public void playWin(int fistNum, int secondNum, int thridNum) {
        if (fistNum > 6 || fistNum < 1) {
            return;
        }
        if (secondNum > 6 || secondNum < 1) {
            return;
        }
        if (thridNum > 6 || thridNum < 1) {
            return;
        }
        mSum = fistNum + secondNum + thridNum;
        if (mSum > 18 || mSum < 3) {
            return;
        }
        cleanState();
        mFirstNum = fistNum;
        mSecondNum = secondNum;
        mThirdNum = thridNum;
        mStart_Firstover = true;
        mStart_Secondloop = true;
        mStart_Thirdloop = true;
        mIsMachine = true;

    }

    private void cleanState() {
        mStart_FirstLocation = 0;
        mStart_FirstCount = 0;
        mStart_SecondLocation = 0;
        mStart_SecondCount = 0;
        mStart_ThirdLocation = 0;
        mStart_ThirdCount = 0;
        mFirstSpeed = 0;
        mSecondSpeed = 0;
        mThirdSpeed = 0;
        mStart_OverCount = 0;

        mFirstNum = -1;
        mSecondNum = -1;
        mThirdNum = -1;

        mStart_NoticeCount = 0;

        mStart_FirstState = false;
        mStart_Firstloop = false;
        mStart_Firstover = false;
        mStart_FirstWin = false;

        mStart_SecondState = false;
        mStart_Secondloop = false;
        mStart_Secondover = false;
        mStart_SecondWin = false;

        mStart_ThirdState = false;
        mStart_Thirdloop = false;
        mStart_Thirdover = false;
        mStart_ThirdWin = false;

        mStart_NoticeState = false;

        mIsDownCount = false;
        mDownCountSite = 0;
        mDownCountStep = 0;
        mIsDownCount = false;
        mIsMachine = false;

        mCount_Machine = 0;
        mFinish_time = 0;
    }


    /**
     * 释放图片资源
     */
    public void recycleBitmap() {

        if (mBitmap_Background != null) {
            if (mBitmap_Background.isRecycled()) {
                mBitmap_Background.recycle();
            }
        }

        if (mBitmap_Countdown != null) {
            for (int i = 0; i < mBitmap_Countdown.length; i++) {
                if (mBitmap_Countdown[i].isRecycled()) {
                    mBitmap_Countdown[i].recycle();
                }
            }
        }

        mSoundPlayer.clearSoundPlayer();
//        System.gc();
    }
}
