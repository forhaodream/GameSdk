package com.nezha.gamelib.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by CH
 * on 2021/9/9 10:53
 * desc
 */
public class ImageUtil {
    /**
     * 添加文本到图片的方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap drawTextToBitmap(Bitmap bitmap, String name,String psd) {
        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(12);
        paint.setTextSize(40);

        canvas.drawText(name, 100, 100, paint);
        canvas.drawText(psd, 100, 200, paint);

        return bitmap;
    }

    /**
     * 将bitmap存在SDCard中
     *
     * @param fingerprint_bitmap
     * @return 返回所存路径
     */
    public static void saveBitmapToSDCard(Bitmap fingerprint_bitmap, String filePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fingerprint_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
