package gelecegiyazanlar.com.gykfirebaseauthentication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import gelecegiyazanlar.com.gykfirebaseauthentication.R;

public class AddPhotoActivity extends AppCompatActivity {
    ImageView userPhoto;
    Button selectPhotoButton, savePhotoButton;
    private static final int IMAGE_REQUEST = 111;
    Uri filePath;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        userPhoto = findViewById(R.id.add_photo);
        selectPhotoButton = findViewById(R.id.choise_photo);
        savePhotoButton = findViewById(R.id.save_photo);
        firebaseStorage = FirebaseStorage.getInstance();


        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();

            }
        });
        savePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
            }
        });
        showPhoto();
    }

    private void selectPhoto() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Resim Seçiniz"), IMAGE_REQUEST);

    }

    private void showPhoto() {
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(AddPhotoActivity.this).load(uri).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>(200,200) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        userPhoto.setImageBitmap(resource);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPhotoActivity.this, "Fotoğraf yükleme başarısız", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void savePhoto() {
        if (filePath != null) {
            StorageReference sRef = firebaseStorage.getReference();
            sRef.child("userprofilphoto").putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddPhotoActivity.this, "Başarılı", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddPhotoActivity.this, "Başarısız", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && requestCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //centerCrop resmin ortasını alıyor geri kalanını kesiyor.
                Picasso.with(AddPhotoActivity.this).load(filePath).fit().centerCrop();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
