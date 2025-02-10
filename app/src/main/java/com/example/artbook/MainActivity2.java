package com.example.artbook;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.artbook.databinding.ActivityMain2Binding;
import com.example.artbook.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;  //Galeriye gitmek için
    ActivityResultLauncher<String> permissionLauncher;      //İzin istemek için
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLauncher();  //Launcherları register ediyoruz ki kullanabilelim

    }


    public void save(View view) {
        String name = binding.nameText.getText().toString();
        String artistName = binding.artistText.getText().toString();
        String tear = binding.yearText.getText().toString();
        Bitmap smallImage=makeSmallerImage(selectedImage,300);


        //Resmi SQLe kaydetmek için byte arraye çeviriyoruz
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();

    }


    //Resmi küçültmek için bir metod oluşturduk
    public Bitmap makeSmallerImage(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) (width / height);
        if (bitmapRatio > 1) {
            //Resim yatay
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            //Resim dikey
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return image.createScaledBitmap(image, width, height, true);
    }


    public void selectImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Android 33+ -->READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                //İzin verilmedi

                //İzin isteme mantığı kullanıcıya açıklansın mı?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    //Snackbar ile kullanıcıya açıklama yap
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //İzin iste
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                        }
                    }).show();
                } else {
                    //İzin iste
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                }
            } else {
                //İzin verildi galeriye git
                //Galeriye git görsel al geri gel
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        } else {
            //Android 32- -->READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //İzin verilmedi

                //İzin isteme mantığı kullanıcıya açıklansın mı?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //Snackbar ile kullanıcıya açıklama yap
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //İzin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                        }
                    }).show();
                } else {
                    //İzin iste
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }
            } else {
                //İzin verildi galeriye git
                //Galeriye git görsel al geri gel
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }


        }

    }

    private void registerLauncher() {
        //Galeriye gitmek için
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                //Uri yı bitmapa çevirmek
                                ImageDecoder.Source source = ImageDecoder.createSource(MainActivity2.this.getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(MainActivity2.this.getContentResolver(), imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //İzin istemek için
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    //İzin verildi
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    //İzin verilmedi
                    Toast.makeText(MainActivity2.this, "Permission needed!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


}
