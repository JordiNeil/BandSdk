package com.wakeup.bandsdk.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wakeup.bandsdk.R;

import java.util.ArrayList;
import java.util.List;

public class SendPicActivity extends AppCompatActivity {
    private static final String TAG = SendPicActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_pic);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inPurgeable = true;     //用于存储Pixel的内存空间在系统内存不足时可以被回收
        opts.inInputShareable = true;  //允许可清除， 以上options的两个属性必须联合使用才会有效果
        opts.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.kobe, opts);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Log.i(TAG, "size:" + getBitmapSize(bitmap) + "\nwidth:" + width + "\nheight:" + height);


        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        ArrayList<String> hexPixels = getHexPixels(pixels, pixels.length);
        Log.i(TAG, "length -->" + hexPixels.size() + "\n-->" + hexPixels.toString());

        List<String> hexPixel_8 = hexString_16_to_8(hexPixels);

        Log.i(TAG,"length -->" + hexPixel_8.size() + "\n-->" + hexPixel_8.toString());
    }

    /**
     * 16位转成8位颜色集合
     * @param hexPixels
     * @return
     */
    @NonNull
    private List<String> hexString_16_to_8(ArrayList<String> hexPixels) {
        List<String> hexPixel_8= new ArrayList<>();
        for (String hexPixel : hexPixels) {
            int colorValue = Integer.parseInt(hexPixel, 16);
            int i = colorValue / 256;
            int j = colorValue % 256;
            hexPixel_8.add(Integer.toHexString(i));
            hexPixel_8.add(Integer.toHexString(j));

        }
        return hexPixel_8;
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 把像素数组转化成565的像素集合
     *
     * @param pixels
     * @param size
     * @return
     */
    public static ArrayList<String> getHexPixels(int[] pixels, int size) {
        ArrayList<String> totalPixels = new ArrayList<String>(size);
        for (int i = 0; i < pixels.length; i++) {
            int clr = pixels[i];
            int red = (clr & 0x00ff0000) >> 16; // 取高两位
            int green = (clr & 0x0000ff00) >> 8; // 取中两位
            int blue = clr & 0x000000ff; // 取低两位
            red = red >> 3;
            green = green >> 2;
            blue = blue >> 3;
            red = red << 11;
            green = green << 5;
            int color = red + green + blue;
            String coh = Integer.toHexString(color);
            if (coh.length() < 4) {
                for (int j = 0; coh.length() < 4; j++) {
                    coh = "0" + coh;
                }
            }
            //转成16进制颜色数据
            //coh="0x"+coh;
            totalPixels.add(coh);
        }

        return totalPixels;
    }

}
