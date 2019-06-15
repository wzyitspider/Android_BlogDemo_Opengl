package edu.wzy.opengl.K3Animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import edu.wzy.opengl.R;

public class K3MainActivity extends Activity {

    private Button mBt_start;
    private Button mBt_stop;
    private Button mBt_win;

    private BtOnClick mBtOnClick;
    private RenderGLSurfaceView mGl_Surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k3_main);
        mBtOnClick = new BtOnClick();
        mBt_start = (Button) findViewById(R.id.Bt_start);
        mBt_stop = (Button) findViewById(R.id.Bt_stop);
        mGl_Surface = (RenderGLSurfaceView) findViewById(R.id.gl_surface);
        mBt_win = (Button) findViewById(R.id.Bt_win);
        mBt_start.setOnClickListener(mBtOnClick);
        mBt_stop.setOnClickListener(mBtOnClick);
        mBt_win.setOnClickListener(mBtOnClick);
    }

    public class BtOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.Bt_start:  //开启
                    mGl_Surface.play();
                    break;
                case R.id.Bt_stop:  //停用
                    mGl_Surface.stop();
                    break;
                case R.id.Bt_win:  //开奖
                    mGl_Surface.playWin(3,4,5);
                    break;
            }
        }
    };
}
