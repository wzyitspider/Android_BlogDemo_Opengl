package com.wzy.opengl.activity;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Time:2019/5/16
 * Author:Jimmy Wang
 * Email:wzy901213@163.com
 * Blog:https://blog.csdn.net/wzy901213
 * Description:
 */
public class LoaderUtil {
    public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        //获取编译后的顶点着色器句柄
        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        //获取编译后的片元着色器句柄
        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }
        //创建一个Program
        iProgId = GLES20.glCreateProgram();
        //添加顶点着色器与片元着色器到Program中
        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);
        //链接生成可执行的Program
        GLES20.glLinkProgram(iProgId);
        //获取Program句柄，并存在在link数组容器中
        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        //容错
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        //删除已链接后的着色器
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }


    public static int loadShader( final String strSource,final int iType) {
        int[] compiled = new int[1];
        //创建指定类型的着色器
        int iShader = GLES20.glCreateShader(iType);
        //将源码添加到iShader并编译它
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        //获取编译后着色器句柄存在在compiled数组容器中
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        //容错判断
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }
}
