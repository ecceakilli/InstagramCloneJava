package com.eceakilli.instagramclonejava.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.eceakilli.instagramclonejava.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ActivityUploadBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;
    ProgressDialog progressDialog;
   //Bitmap selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUploadBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncher();


        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();//firebaseStorage den referans al
    }


    public void uploadClicked(View view){
        if (imageData !=null){//kullanıcı resim secti mi secmedi mi kontrol et sectiyse


            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Görsel paylaşılıyor...");
            progressDialog.show();
            //Universial Unique id ile yüklenen her resmin yeni-farklı adı olmalı javanın kendi sınıfı var bunun için onu kullanacağız
            UUID uuid=UUID.randomUUID();
            String imageName="images/"+uuid+".jpg";

            //referans ile direk kullanmaya başlayabiliriz
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//nerye kaydedecegını ayarlıyorsun
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //uploadClickeda basıldıgında firebase resmi attık urli db ye kaydı yapılması gerekiyor
                    StorageReference newReference=firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //görseli alalım
                            String downloadUrl=uri.toString();

                            //kullanıcının yazdığı yorumu cekelim
                            String comment=binding.commentText.getText().toString();
                            String commentDetail=binding.commentDetailText.getText().toString();

                            //Kullanıcı bilgilerini alalım
                            FirebaseUser user=auth.getCurrentUser();
                            String email=user.getEmail();

                            //alınan verilerin hepsini hashMapa koyup oradan firestora ekleycegiz
                            HashMap<String, Object> postData=new HashMap<>();
                            postData.put("useremail",email);
                            postData.put("downloadurl",downloadUrl);
                            postData.put("comment",comment);
                            postData.put("commentDetail",commentDetail);
                            postData.put("date", FieldValue.serverTimestamp());



                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //zaten kayır işlemleri yapıldu aktivityi kapamak içinü
                                    Intent intent=new Intent(UploadActivity.this, FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void selectImage(View view){
        //manifestten iizin verildi burda izin var mı kontrol et
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Gelariye erişim için izin vermelisiniz",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                //ask permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else{
            //zaten izin vermiş
            Intent intentToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGalery);
        }

    }

    private void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){
                   Intent intentFromResult= result.getData();
                   if (intentFromResult!=null){
                       //gerçekten veri döndü mü
                       imageData=intentFromResult.getData();
                       binding.imageView.setImageURI(imageData);//URI yeterli olmazsa bitmap ile de yapılabilir yorum satırında var


                       /*
                       try {
                           if (Build.VERSION.SDK_INT>=28){
                               ImageDecoder.Source source=ImageDecoder.createSource(UploadActivity.this.getContentResolver(),imageData);
                              selectedImage=ImageDecoder.decodeBitmap(source);
                               binding.imageView.setImageBitmap(selectedImage);
                           }else{
                             selectedImage= MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(),imageData);
                             binding.imageView.setImageBitmap(selectedImage);
                           }
                       }catch (Exception e){
                           e.printStackTrace();
                       }*/
                   }
                }
            }
        });
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent intentToGalery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGalery);
                }else{
                    Toast.makeText(UploadActivity.this,"İzin Gerekli!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}