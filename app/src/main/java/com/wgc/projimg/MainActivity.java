package com.wgc.projimg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private Button btnBuscar;
    private ImageView ivFoto;
    private ProgressBar pbFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnBuscar = (Button)findViewById(R.id.btnBuscar);
        ivFoto = (ImageView)findViewById(R.id.ivFoto);
        pbFoto = (ProgressBar) findViewById(R.id.pbFoto);
        pbFoto.setMax(100);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selecionarFoto();
                //downloadFoto();
                deleteFoto();
            }
        });
    }

    private void selecionarFoto() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"SelecionarFoto"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1){
            Uri uri = data.getData();
            ivFoto.setImageURI(uri);
            enviarFoto();
        }
    }

    private void enviarFoto() {
        Bitmap bitmap = ((BitmapDrawable)ivFoto.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imagem = byteArrayOutputStream.toByteArray();

        StorageReference imgRef = mStorageRef.child("img-005.jpeg");
        UploadTask uploadTask = imgRef.putBytes(imagem);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                msfToast("Upload realizado com Sucesso");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int pg = (int) progress;
                pbFoto.setProgress(pg);
            }
        });
    }

    private void msfToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void downloadFoto(){
        StorageReference imgRef = mStorageRef.child("img-001.jpeg");
        final long  MEGABYTE = 1024 * 1024;
        imgRef.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                ivFoto.setImageBitmap(bitmap);
            }
        });
    }

    private void deleteFoto(){
        StorageReference imgRef = mStorageRef.child("img-002.jpeg");
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                msfToast("Imagem deletada com sucesso");
            }
        });
    }
}
