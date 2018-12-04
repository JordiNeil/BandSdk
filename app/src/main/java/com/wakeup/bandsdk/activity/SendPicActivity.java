package com.wakeup.bandsdk.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.wakeup.bandsdk.R;
import com.wakeup.mylibrary.command.CommandManager;
import com.wakeup.mylibrary.service.BluetoothService;
import com.wakeup.mylibrary.utils.CommonUtils;
import com.wakeup.mylibrary.utils.DataHandUtils;

import java.util.ArrayList;
import java.util.List;

public class SendPicActivity extends AppCompatActivity {
    private static final String TAG = SendPicActivity.class.getSimpleName();
    private CommandManager commandManager;
    private List<List<String>> sourceList;
    private int req;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_pic);
        commandManager = CommandManager.getInstance(this);


    }


    /**
     * 根据sourList 和 req 得到当前应该发送的数据
     *
     * @param split
     * @param req
     * @return
     */
    private byte[] getByteArray(List<List<String>> split, int req) {
        //根据固件发送过来的h_req 获取1024个字节 byte 数组
        List<String> list = split.get(req);
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = (byte) (Integer.parseInt(list.get(i), 16));
        }
        return bytes;
    }


    /**
     * 16位转成8位颜色集合
     *
     * @param hexPixels
     * @return
     */
    @NonNull
    private List<String> hexString_16_to_8(ArrayList<String> hexPixels) {
        List<String> hexPixel_8 = new ArrayList<>();
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

    /**
     * 发送图片
     *
     * @param view
     */
    public void sendPic(View view) {
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

        Log.i(TAG, "length -->" + hexPixel_8.size() + "\n-->" + hexPixel_8.toString());

        sourceList = CommonUtils.split(hexPixel_8, 1024);
        Log.i(TAG, sourceList.size() + "");//112个1024 1个512
        Log.i(TAG, sourceList.get(sourceList.size() - 1).size() + "");//512


        handleSendPic();


    }

    private void handleSendPic() {
//        try {
//            Thread.sleep(30);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //根据sourList 和 req 得到当前应该发送的数据1024个
        byte[] byteArray = getByteArray(sourceList, req);

        //获取当前数据的长度
        int dataLength = byteArray.length;

        //计算当前数据CRC
        int crc = CommonUtils.crcTable(byteArray);


        //每1024字节64个包，其中第一个包的id为 req*64
        int firstId = req * (1024 / 16);

        if (byteArray.length==1024){
            //发送开始的指令
            commandManager.startSendPic(dataLength, req, crc, 0);
            byte[] data;
            for (int i = 0; i < 64; i++) {
                data = new byte[16];
                System.arraycopy(byteArray, 16 * i, data, 0, 16);
                commandManager.sendImageContent(firstId, data);
                firstId++;
            }
        }else {
            //最后一个包 512个字节
            //发送开始的指令
            commandManager.startSendPic(dataLength, req, crc, 1);
            Log.i(TAG,"最后一个包的长度："+byteArray.length);
            byte[] data;
            for (int i = 0; i < 32; i++) {
                data = new byte[16];
                System.arraycopy(byteArray, 16 * i, data, 0, 16);
                commandManager.sendImageContent(firstId, data);
                firstId++;
            }

        }

    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        /**拼接包的长度**/
        private int combineSize;
        /**开始拼接包**/
        private boolean combine;
        /**临时包**/
        private byte[] templeBytes = new byte[0];

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_CONNECTED");

            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");



            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");


            } else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                Log.d(TAG, "接收的数据：" + DataHandUtils.bytesToHexStr(txValue));
                List<Integer> datas = DataHandUtils.bytesToArrayList(txValue);
                if (datas.size() == 0) {
                    return;
                }
                //处理屏保返回的数据
                if (datas.get(0) == 0xAC) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(datas.get(2));
                    buffer.append(datas.get(3));
                    req = Integer.parseInt(buffer.toString());
                    Log.i(TAG,"req:"+req);
                    if (datas.get(4) == 0) {
                        //继续请求数据
                        Log.i(TAG,"继续请求数据");
                        handleSendPic();

                    } else if (datas.get(4) == 1) {
                        //结束发送数据
                        Log.i(TAG,"结束发送数据");
                    }


                }


            }
        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

}
