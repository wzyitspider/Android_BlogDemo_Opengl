package edu.wzy.opengl.player;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wzy.opengl.R;

/**
 * Created by wzy on 2017/3/19.
 */

public class GLVideoRenderer implements GLSurfaceView.Renderer
        , SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnVideoSizeChangedListener  {


    private static final String TAG = "GLRenderer";
    private Context context;
    private int aPositionLocation;
    private int programId;
    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            1f,-1f,0f,
            -1f,-1f,0f,
            1f,1f,0f,
            -1f,1f,0f
    };

    private final float[] projectionMatrix=new float[16];
    private int uMatrixLocation;

    private final float[] textureVertexData = {
            1f,0f,
            0f,0f,
            1f,1f,
            0f,1f
    };
    private FloatBuffer textureVertexBuffer;
    private int uTextureSamplerLocation;
    private int aTextureCoordLocation;
    private int textureId;

    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;
    private float[] mSTMatrix = new float[16];
    private int uSTMMatrixHandle;

    private boolean updateSurface;
    private boolean playerPrepared;
    private int screenWidth,screenHeight;
    private int hChangeType;
    private int hChangeColor;
    private int hIsHalf;



    edu.wzy.opengl.image.filter.ColorFilter.Filter  filter ;
    private int glHUxy;
    private float uXY;


    public GLVideoRenderer(Context context, String videoPath) {
        this.context = context;
        playerPrepared=false;
        synchronized(this) {
            updateSurface = false;
        }
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);

        mediaPlayer=new MediaPlayer();
        try{
            mediaPlayer.setDataSource(context, Uri.parse(videoPath));
        }catch (IOException e){
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);

        mediaPlayer.setOnVideoSizeChangedListener(this);
        filter = edu.wzy.opengl.image.filter.ColorFilter.Filter.WARM;
    }

    public edu.wzy.opengl.image.filter.ColorFilter.Filter getFilter() {
        return filter;
    }

    public void setFilter(edu.wzy.opengl.image.filter.ColorFilter.Filter filter) {
        this.filter = filter;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.simple_vertex_shader);
        String fragmentShader= ShaderUtils.readRawTextFile(context, R.raw.simple_fragment_shader);
        programId=ShaderUtils.loadProgram(vertexShader,fragmentShader);


       // programId= edu.wzy.opengl.utils.ShaderUtils.createProgram(context.getResources(),"filter/half_color_vertex.sh","filter/half_color_fragment.sh");

        aPositionLocation= GLES20.glGetAttribLocation(programId,"aPosition");
        uMatrixLocation=GLES20.glGetUniformLocation(programId,"uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        uTextureSamplerLocation=GLES20.glGetUniformLocation(programId,"sTexture");
        aTextureCoordLocation=GLES20.glGetAttribLocation(programId,"aTexCoord");

        hChangeType=GLES20.glGetUniformLocation(programId,"vChangeType");
        hChangeColor=GLES20.glGetUniformLocation(programId,"vChangeColor");
        hIsHalf=GLES20.glGetUniformLocation(programId,"vIsHalf");
        glHUxy=GLES20.glGetUniformLocation(programId,"uXY");
        
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
       // ShaderUtils.checkGlError("glBindTexture mTextureID");
   /*GLES11Ext.GL_TEXTURE_EXTERNAL_OES的用处？
      之前提到视频解码的输出格式是YUV的（YUV420p，应该是），那么这个扩展纹理的作用就是实现YUV格式到RGB的自动转化，
      我们就不需要再为此写YUV转RGB的代码了*/
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);//监听是否有新的一帧数据到来

        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);
        surface.release();

        if (!playerPrepared){
            try {
                mediaPlayer.prepare();
                playerPrepared=true;
            } catch (IOException t) {
                Log.e(TAG, "media player prepare failed");
            }
            mediaPlayer.start();
            playerPrepared=true;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: "+width+" "+height);
        screenWidth=width; screenHeight=height;
        float sWidthHeight=width/(float)height;
        uXY=sWidthHeight;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this){
            if (updateSurface){
                surfaceTexture.updateTexImage();//获取新数据
                surfaceTexture.getTransformMatrix(mSTMatrix);//让新的纹理和纹理坐标系能够正确的对应,mSTMatrix的定义是和projectionMatrix完全一样的。
                updateSurface = false;
            }
        }
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);

        GLES20.glUniform1i(hChangeType,filter.getType());
        GLES20.glUniform3fv(hChangeColor,1,filter.data(),0);
        GLES20.glUniform1i(hIsHalf,0);
        GLES20.glUniform1f(glHUxy,uXY);
        
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);

        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation,2,GLES20.GL_FLOAT,false,8,textureVertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);

        GLES20.glUniform1i(uTextureSamplerLocation,0);
        GLES20.glViewport(0,0,screenWidth,screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.d(TAG, "onVideoSizeChanged: "+width+" "+height);
        updateProjection(width,height);
    }

    private void updateProjection(int videoWidth, int videoHeight){
        float screenRatio=(float)screenWidth/screenHeight;
        float videoRatio=(float)videoWidth/videoHeight;
        if (videoRatio>screenRatio){
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-videoRatio/screenRatio,videoRatio/screenRatio,-1f,1f);
        }else Matrix.orthoM(projectionMatrix,0,-screenRatio/videoRatio,screenRatio/videoRatio,-1f,1f,-1f,1f);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    public void destory(){
        mediaPlayer.stop();
        mediaPlayer.setLooping(false);
    }


}

