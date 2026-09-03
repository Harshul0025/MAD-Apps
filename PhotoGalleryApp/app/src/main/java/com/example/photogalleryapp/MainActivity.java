package com.example.photogalleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> folderLauncher;

    // ✅ Store selected folder
    Uri selectedFolderUri;

    // ✅ Store image URI
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ CAMERA RESULT
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();


                    }
                }
        );

        // ✅ FOLDER PICKER
        folderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        selectedFolderUri = result.getData().getData();

                        // 🔥 VERY IMPORTANT (persist permission)
                        getContentResolver().takePersistableUriPermission(
                                selectedFolderUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );

                        Toast.makeText(this, "Folder Selected!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // ✅ TAKE PHOTO BUTTON
    public void takePhoto(View view) {

        if (selectedFolderUri == null) {
            Toast.makeText(this, "Please select folder first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        openCamera();
    }

    // ✅ OPEN CAMERA
    private void openCamera() {

        if (selectedFolderUri == null) {
            Toast.makeText(this, "Select folder first", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {

            try {
                // Create file inside selected folder
                androidx.documentfile.provider.DocumentFile folder =
                        androidx.documentfile.provider.DocumentFile.fromTreeUri(this, selectedFolderUri);

                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";

                androidx.documentfile.provider.DocumentFile newFile =
                        folder.createFile("image/jpeg", fileName);

                if (newFile == null) {
                    Toast.makeText(this, "File creation failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                photoUri = newFile.getUri();

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                cameraLauncher.launch(intent);
                grantUriPermission(
                        "com.android.camera",
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No Camera App Found", Toast.LENGTH_SHORT).show();
        }
    }



    // ✅ PERMISSION RESULT
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ✅ CHOOSE FOLDER BUTTON
    public void chooseFolder(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        folderLauncher.launch(intent);
    }

    // ✅ VIEW GALLERY BUTTON
    public void viewGallery(View view) {
        if (selectedFolderUri == null) {
            Toast.makeText(this, "Please select a folder first!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("folderUri", selectedFolderUri.toString());
        startActivity(intent);
    }
}