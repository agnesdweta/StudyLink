package com.example.studylink;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUtils {

    public static MultipartBody.Part prepareFilePart(
            Context context,
            String partName,
            Uri fileUri
    ) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);

            String fileName = getFileName(context, fileUri);
            File file = new File(context.getCacheDir(), fileName);

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            RequestBody requestFile =
                    RequestBody.create(file, MediaType.parse("image/*"));

            return MultipartBody.Part.createFormData(
                    partName,
                    file.getName(),
                    requestFile
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String result = "image.jpg";

        if ("content".equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        return result;
    }
}
