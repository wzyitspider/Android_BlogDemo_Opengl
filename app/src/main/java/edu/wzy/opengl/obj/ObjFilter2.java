package edu.wzy.opengl.obj;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;

import edu.wzy.opengl.filter.AFilter;

/**
 * Created by wzy on 2017/1/8
 */

public class ObjFilter2 extends AFilter {

    private int vertCount;

    private int mHNormal;
    private int mHKa;
    private int mHKd;
    private int mHKs;
    private Obj3D obj;

    private int textureId;

    public ObjFilter2(Resources mRes) {
        super(mRes);
    }

    public void setObj3D(Obj3D obj){
        this.obj=obj;
    }

    @Override
    protected void initBuffer() {
        super.initBuffer();
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("3dres/obj2.vert","3dres/obj2.frag");
        mHNormal=GLES20.glGetAttribLocation(mProgram,"vNormal");
        mHKa=GLES20.glGetUniformLocation(mProgram,"vKa");
        mHKd=GLES20.glGetUniformLocation(mProgram,"vKd");
        mHKs=GLES20.glGetUniformLocation(mProgram,"vKs");
        //打开深度检测
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        if(obj!=null&&obj.mtl!=null){
            try {
                Log.e("obj","texture-->"+"3dres/"+obj.mtl.map_Kd);
                textureId=createTexture(BitmapFactory.decodeStream(mRes.getAssets().open("3dres/"+obj.mtl.map_Kd)));
                setTextureId(textureId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onClear() {
//        super.onClear();
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        if(obj!=null&&obj.mtl!=null){
            GLES20.glUniform3fv(mHKa,1,obj.mtl.Ka,0);
            GLES20.glUniform3fv(mHKd,1,obj.mtl.Kd,0);
            GLES20.glUniform3fv(mHKs,1,obj.mtl.Ks,0);
        }
    }

    @Override
    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,3, GLES20.GL_FLOAT, false,0,obj.vert);
        GLES20.glEnableVertexAttribArray(mHNormal);
        GLES20.glVertexAttribPointer(mHNormal,3, GLES20.GL_FLOAT, false, 0,obj.vertNorl);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord,2,GLES20.GL_FLOAT,false,0,obj.vertTexture);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,obj.vertCount);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHNormal);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        GLES20.glViewport(0,0,width,height);
    }

    private int createTexture(Bitmap bitmap){
        int[] texture=new int[1];
        if(bitmap!=null&&!bitmap.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }

}
