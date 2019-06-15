/*
 *
 * NoFilter.java
 * 
 * Created by wzy on 2016/11/19
 * Copyright © 2016年 . All rights reserved.
 */
package edu.wzy.opengl.filter;

import android.content.res.Resources;

/**
 * Description:
 */
public class NoFilter extends AFilter {

    public NoFilter(Resources res) {
        super(res);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh",
            "shader/base_fragment.sh");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
