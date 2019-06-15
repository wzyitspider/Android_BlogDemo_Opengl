package edu.wzy.opengl.player;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import edu.wzy.opengl.R;
import edu.wzy.opengl.image.filter.ColorFilter;
import edu.wzy.opengl.player.GLVideoRenderer;


public class PlayerActivity extends Activity implements View.OnClickListener {
    GLSurfaceView surfaceView;
    GLVideoRenderer renderer;
    Button btn_none, btn_gray, btn_blur, btn_cold, btn_hot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        surfaceView = (GLSurfaceView) findViewById(R.id.surface);
        surfaceView.setEGLContextClientVersion(2);
        renderer = new GLVideoRenderer(this, "sdcard/test.mp4");
        surfaceView.setRenderer(renderer);
        btn_none = (Button) findViewById(R.id.Bt_None);
        btn_blur = (Button) findViewById(R.id.Bt_blur);
        btn_cold = (Button) findViewById(R.id.Bt_cold);
        btn_hot = (Button) findViewById(R.id.Bt_hot);
        btn_gray = (Button) findViewById(R.id.Bt_gray);
        btn_none.setOnClickListener(this);
        btn_blur.setOnClickListener(this);
        btn_cold.setOnClickListener(this);
        btn_hot.setOnClickListener(this);
        btn_gray.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        renderer.destory();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Bt_None:
                renderer.setFilter(ColorFilter.Filter.NONE);
                break;
            case R.id.Bt_blur:
                renderer.setFilter(ColorFilter.Filter.BLUR);
                break;
            case R.id.Bt_cold:
                renderer.setFilter(ColorFilter.Filter.COOL);
                break;
            case R.id.Bt_hot:
                renderer.setFilter(ColorFilter.Filter.WARM);
                break;
            case R.id.Bt_gray:
                renderer.setFilter(ColorFilter.Filter.GRAY);
                break;
        }
        surfaceView.requestRender();
    }
}
