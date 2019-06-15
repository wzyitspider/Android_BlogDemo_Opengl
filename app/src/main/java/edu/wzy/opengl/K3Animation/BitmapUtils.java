package edu.wzy.opengl.K3Animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 工具类
 * Created by Administrator on 2016/10/9.
 */
public class BitmapUtils {

    public static Bitmap readBitmap(Context context, int id)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(id);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
        try
        {
            is.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }
}
