package com.quchat.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static String ImagePath=Environment.getExternalStorageDirectory()+"/quchat/";

    public static File saveBitmapFile(Bitmap bitmap,String imageName) {
        isExist(ImagePath);
        File file = new File(ImagePath+imageName+".jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

    }
}
