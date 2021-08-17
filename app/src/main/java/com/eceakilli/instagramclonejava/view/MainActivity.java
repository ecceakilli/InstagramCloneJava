package com.eceakilli.instagramclonejava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eceakilli.instagramclonejava.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //bu objeyi kullanarak artık bütn giriş çıkıs işlemlerin hepsini kullanabilirm
        auth=FirebaseAuth.getInstance();

        //uygulamada daha önceden giriş yapan kullanıcı varsa direk uygulama anasayfaya gidyor
        FirebaseUser user=auth.getCurrentUser();
        if (user!=null){
            Intent intent=new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }



    }
    public void singInClicked(View view){
        String email=binding.editText.getText().toString();
        String password=binding.paswordText.getText().toString();

        if (email.equals("")||password.equals("")){
            Toast.makeText(this,"Enter e-mail and password",Toast.LENGTH_LONG).show();
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void singUpClicked(View view){

        String email=binding.editText.getText().toString();
        String password=binding.paswordText.getText().toString();

        if (email.equals("")|| password.equals("")){
            Toast.makeText(this,"Enter e-mail and password",Toast.LENGTH_LONG).show();
        }else{//kullanıcı serverdan cevap alınıp basarılı sonuc aldıgında çalışır.
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();//kullanıcı bir kere giriş yaptıktan sonra bu aktivite kapansın ki hafizada yer kaplamasın


                }
                //başarısız olduğunda da bu alan calısır
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }



    }
}