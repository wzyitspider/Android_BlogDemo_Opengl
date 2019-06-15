/*
 *
 * GrayFilter.java
 * 
 * Created by wzy on 2016/12/14
 * Copyright © 2016年 . All rights reserved.
 */
package edu.wzy.opengl.filter;

import android.content.res.Resources;

/**
 * Description:
 */
public class GrayFilter extends AFilter {

    public GrayFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh",
            "shader/color/gray_fragment.frag");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
