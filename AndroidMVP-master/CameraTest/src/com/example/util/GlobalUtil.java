package com.example.util;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class GlobalUtil {
    static String externPath;

    public static String getExternalAbsolutePath(Context context) {
        if (externPath == null) {
            externPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        }
        return externPath;
    }

    public static String getId() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            UUID uuid = UUID.randomUUID();
            String guidStr = uuid.toString();
            md.update(guidStr.getBytes(), 0, guidStr.length());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static Bitmap preHandleImage(InputStream is) {

        BitmapFactory.Options opts = new Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        int bitmapheight = opts.outHeight;
        int bitmapwidht = opts.outWidth;
        Log.i("sjl", "bitmapheight" + bitmapheight);
        Log.i("sjl", "bitmapwidht" + bitmapwidht);

        if (bitmapheight < 500) {
//       	 do nothing 
//          把数据库自带的小图片过滤掉  不做缩放处理
        } else {
            int scaleX = 4;
            opts.inSampleSize = scaleX;
        }
        opts.inJustDecodeBounds = false;//真是的解析位图
        bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    /**
     * 图片预处理 避免OOM错误
     *
     * @param imageView
     * @param path
     * @return
     */
    public static Bitmap preHandleImage(ImageView imageView, String path) {

        BitmapFactory.Options opts = new Options();
        // 这里并不是真正的加载一个位图对象而是把位图的边框信息给获取出来
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        // 获取当前位图的边框信息
        int bitmapheight = opts.outHeight;
        int bitmapwidht = opts.outWidth;
        Log.i("sjl", "bitmapheight" + bitmapheight);
        Log.i("sjl", "bitmapwidht" + bitmapwidht);

        if (bitmapheight < 500) {
//        	 do nothing 
//           把数据库自带的小图片过滤掉  不做缩放处理
        } else {
            int scaleX = 4;
            opts.inSampleSize = scaleX;
        }

        // 获取当前窗体的边框信息
//         int windowWidht = imageView.getWidth();  
//         int windowHidht = imageView.getHeight();  


//         int scalaY =2;  
        //此时以X轴的比例缩放
//         if(scaleX>scalaY&&scalaY>1){  
//             opts.inSampleSize=scaleX;  
//         }  
        //此时以Y轴的比例缩放
//         if(scalaY>scaleX&&scaleX>1)  
//         {  
//             opts.inSampleSize=scalaY;  
//         }  

        opts.inJustDecodeBounds = false;//真是的解析位图
        //以缩放比例的形式来显式位图
        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;

    }


    public static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.051f, 0.051f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

}
