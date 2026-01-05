package com.example.studylink;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Size;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;

    private androidx.camera.view.PreviewView previewView;
    private ImageView btnCapture;
    private ImageView imgGallery;
    private ImageView btnBack;
    private Uri lastPhotoUri;

    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);
        btnBack = findViewById(R.id.btnBack);
        imgGallery = findViewById(R.id.imgGallery);
        loadLastImageFromGallery();
        imgGallery.setOnClickListener(v -> {
            if (lastPhotoUri != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(lastPhotoUri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Belum ada foto", Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(v -> {
            finish(); // kembali ke Dashboard
        });

        // cek permission kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }

        btnCapture.setOnClickListener(v -> takePhoto());
    }

    private void loadLastImageFromGallery() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media._ID
        };

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (android.database.Cursor cursor = getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortOrder
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                );
                lastPhotoUri = Uri.withAppendedPath(uri, String.valueOf(id));
                imgGallery.setImageURI(lastPhotoUri);
            }
        }
    }



    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        String filename = "photo_" + System.currentTimeMillis() + ".jpg";

        // 1. Buat file di folder internal app
        File file = new File(getExternalFilesDir(null), filename);
        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivity.this, "Foto berhasil disimpan sementara", Toast.LENGTH_SHORT).show();

                        // 2. Pindahkan ke Gallery
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/StudyLink");
                        try{
                            lastPhotoUri = getContentResolver().insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    contentValues
                            );

                            if (lastPhotoUri != null) {
                                InputStream in = new FileInputStream(file);
                                OutputStream out = getContentResolver().openOutputStream(lastPhotoUri);

                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                                in.close();
                                out.close();
                                Toast.makeText(CameraActivity.this, "Foto berhasil disimpan di Gallery", Toast.LENGTH_SHORT).show();
                                imgGallery.setImageURI(lastPhotoUri);
                                file.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CameraActivity.this, "Gagal pindahkan ke Gallery", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraActivity.this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show();
                        exception.printStackTrace();
                    }
                });
    }

    // callback permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permission kamera dibutuhkan!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
