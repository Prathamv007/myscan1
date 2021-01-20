package com.override011.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;

public class Nav_ocr extends AppCompatActivity {
    ImageView Img;
    TextView tv;
    Button Choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_ocr);

        Img = findViewById(R.id.Img_view);
        tv = findViewById(R.id.orc_text);
        Choose = findViewById(R.id.gal_btn);

        Choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(getIntent().createChooser(i, "select images"), 0);
            }
        });


        // from GALLERY
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null && bundle.containsKey("mFrom") && bundle.getString("mFrom","").equalsIgnoreCase("Gallery")){
            try {
                Uri myUri = Uri.parse(bundle.getString("GalleryData"));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri);
                Img.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        // from CAMERA
        if (bundle!=null && bundle.containsKey("mFrom") && bundle.getString("mFrom","").equalsIgnoreCase("Camera"))
            setImage();
    }

    private void setImage(){
        final String dir = android.os.Environment.getExternalStorageDirectory() + "/picFolder/";
        String file = dir + "temp.jpg";
        File newfile = new File(file);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(newfile.getAbsolutePath(),bmOptions);
        Img.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            Img.setImageURI(data.getData());

            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());

                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();


                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                // Task completed successfully
                                // ...
                                tv.setText(result.getText());
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}