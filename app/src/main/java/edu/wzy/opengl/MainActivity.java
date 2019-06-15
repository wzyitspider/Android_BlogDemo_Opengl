package edu.wzy.opengl;

import edu.wzy.opengl.blend.BlendActivity;
import edu.wzy.opengl.camera.Camera3Activity;
import edu.wzy.opengl.light.LightActivity;
import edu.wzy.opengl.player.PlayerActivity;
import edu.wzy.opengl.vr.GlobeActivity;
import edu.wzy.opengl.vr.VrContextActivity;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.wzy.opengl.camera.Camera2Activity;
import edu.wzy.opengl.camera.CameraActivity;
import edu.wzy.opengl.egl.EGLBackEnvActivity;
import edu.wzy.opengl.etc.ZipActivity;
import edu.wzy.opengl.fbo.FBOActivity;
import edu.wzy.opengl.image.SGLViewActivity;
import edu.wzy.opengl.obj.ObjLoadActivity;
import edu.wzy.opengl.obj.ObjLoadActivity2;
import edu.wzy.opengl.render.FGLViewActivity;
import edu.wzy.opengl.vary.VaryActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mList;
    private ArrayList<MenuBean> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList= (RecyclerView)findViewById(R.id.mList);
        mList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        data=new ArrayList<>();
        add("开奖动画", edu.wzy.opengl.K3Animation.K3MainActivity.class);
        add("播放小视频", PlayerActivity.class);
        add("绘制形体",FGLViewActivity.class);
        add("相机3 美颜",Camera3Activity.class);
        add("地球仪(随手势旋转缩放)",GlobeActivity.class);
        add("VR效果",VrContextActivity.class);
        add("光照",LightActivity.class);
        add("图片处理",SGLViewActivity.class);
        add("图形变换",VaryActivity.class);
        add("相机",CameraActivity.class);
        add("相机2 动画",Camera2Activity.class);
        add("压缩纹理动画",ZipActivity.class);
        add("FBO使用",FBOActivity.class);
        add("EGL后台处理",EGLBackEnvActivity.class);
        add("3D obj模型",ObjLoadActivity.class);
        add("obj+mtl模型",ObjLoadActivity2.class);
        add("颜色混合",BlendActivity.class);

        mList.setAdapter(new MenuAdapter());
    }

    private void add(String name,Class<?> clazz){
        MenuBean bean=new MenuBean();
        bean.name=name;
        bean.clazz=clazz;
        data.add(bean);
    }

    private class MenuBean{

        String name;
        Class<?> clazz;

    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder>{


        @Override
        public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MenuHolder(getLayoutInflater().inflate(R.layout.item_button,parent,false));
        }

        @Override
        public void onBindViewHolder(MenuHolder holder, int position) {
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MenuHolder extends RecyclerView.ViewHolder{

            private Button mBtn;

            MenuHolder(View itemView) {
                super(itemView);
                mBtn= (Button)itemView.findViewById(R.id.mBtn);
                mBtn.setOnClickListener(MainActivity.this);
            }

            public void setPosition(int position){
                MenuBean bean=data.get(position);
                mBtn.setText(bean.name);
                mBtn.setTag(position);
            }
        }

    }

    @Override
    public void onClick(View view){
        int position= (int)view.getTag();
        MenuBean bean=data.get(position);
        startActivity(new Intent(this,bean.clazz));
    }
}
