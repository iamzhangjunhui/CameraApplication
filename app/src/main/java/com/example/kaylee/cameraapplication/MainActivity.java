package com.example.kaylee.cameraapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 拍照相关
 * 1.判断改手机是否有拍照app
 * 2.获取图片的相关信息。
 * ExifInterface.TAG_ORIENTATION //旋转角度，整形表示，在ExifInterface中有常量对应表示
 * ExifInterface.TAG_DATETIME //拍摄时间，取决于设备设置的时间
 * ExifInterface.TAG_MAKE //设备品牌
 * ExifInterface.TAG_MODEL //设备型号，整形表示，在ExifInterface中有常量对应表示
 * ExifInterface.TAG_FLASH //闪光灯
 * ExifInterface.TAG_IMAGE_LENGTH //图片高度
 * ExifInterface.TAG_IMAGE_WIDTH //图片宽度
 * ExifInterface.TAG_GPS_LATITUDE //纬度
 * ExifInterface.TAG_GPS_LONGITUDE //经度
 * ExifInterface.TAG_GPS_LATITUDE_REF //纬度名（N or S）
 * ExifInterface.TAG_GPS_LONGITUDE_REF //经度名（E or W）
 * ExifInterface.TAG_EXPOSURE_TIME //曝光时间
 * ExifInterface.TAG_APERTURE //光圈值
 * ExifInterface.TAG_ISO //ISO感光度
 * ExifInterface.TAG_DATETIME_DIGITIZED //数字化时间
 * ExifInterface.TAG_SUBSEC_TIME //
 * ExifInterface.TAG_SUBSEC_TIME_ORIG //
 * ExifInterface.TAG_SUBSEC_TIME_DIG //
 * ExifInterface.TAG_GPS_ALTITUDE //海拔高度
 * ExifInterface.TAG_GPS_ALTITUDE_REF //海拔高度
 * ExifInterface.TAG_GPS_TIMESTAMP //时间戳
 * ExifInterface.TAG_GPS_DATESTAMP //日期戳
 * ExifInterface.TAG_WHITE_BALANCE //白平衡
 * ExifInterface.TAG_FOCAL_LENGTH //焦距
 * ExifInterface.TAG_GPS_PROCESSING_METHOD //用于定位查找的全球定位系统处理方法。
 * 3.旋转图片
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        1.判断改手机是否有拍照app
        if (hasCamera()) {
            Toast.makeText(this, "有拍照app", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "无拍照app", Toast.LENGTH_SHORT).show();
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots/S80307-192115.JPG";
        ImageView img = (ImageView) findViewById(R.id.img);
        img.setImageBitmap(BitmapFactory.decodeFile(path));

//         2.获取图片的相关信息。
        Toast.makeText(this, "图片旋转角度：" + getBitmapDegree(path) + " " + path, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "图片生成时间：" + getTime(path) + " " + path, Toast.LENGTH_SHORT).show();

//        3.旋转图片
        Bitmap bitmap = rotateBitmap(BitmapFactory.decodeFile(path), 90);
        img.setImageBitmap(bitmap);

    }

    //判断手机是否有照相机
    private boolean hasCamera() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 获取图片的旋转角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取图片生成时间
     *
     * @param path 图片路径
     * @return 如果没有就返回null
     */
    private String getTime(String path) {
        String time = "";
        try {
            ExifInterface e = new ExifInterface(path);
            e.setAttribute(ExifInterface.TAG_DATETIME, "2018");//设置值
            e.saveAttributes();//保存值，设置多个值得时候可以多个值设置完了统一保存。
            time = e.getAttribute(ExifInterface.TAG_DATETIME);//获取时间
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return time;
    }

    /**
     * 旋转图片
     *
     * @param bitmap 要旋转的图片
     * @param degree 旋转的角度  沿顺时针旋转
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        //根据旋转矩阵，创建一个新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //回收旋转前的图片
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }
}
