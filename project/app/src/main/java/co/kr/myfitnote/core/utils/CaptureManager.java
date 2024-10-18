package co.kr.myfitnote.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CaptureManager {
    static public byte[] convertImageFileToBytes(File imageFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static public byte[] convertImageFileToCompressedBytes(File imageFile) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            // Set the desired width and height for the compressed image
            int targetWidth = 800; // Adjust as needed
            int targetHeight = (int) ((float) targetWidth / bitmap.getWidth() * bitmap.getHeight());

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Use JPEG format with compression quality (0-100), adjust as needed
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
            byteArrayOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
