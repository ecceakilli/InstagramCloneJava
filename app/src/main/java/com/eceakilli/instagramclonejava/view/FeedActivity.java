package com.eceakilli.instagramclonejava.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eceakilli.instagramclonejava.R;
import com.eceakilli.instagramclonejava.adapter.PostAdapter;
import com.eceakilli.instagramclonejava.databinding.ActivityFeedBinding;
import com.eceakilli.instagramclonejava.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

        private FirebaseAuth auth;
        private FirebaseFirestore firebaseFirestore;
        ArrayList<Post> postArrayList;  //gelen postları tutuyorum
        private ActivityFeedBinding binding;
        PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityFeedBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);




        postArrayList=new ArrayList<>(); //boş arraylisti

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        getDataFromFirestore();

        binding.recylerViewFeed.setLayoutManager(new LinearLayoutManager(this));
        postAdapter=new PostAdapter(postArrayList);
        binding.recylerViewFeed.setAdapter(postAdapter);










    }

    private void getDataFromFirestore(){
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if (value !=null){
                    //tek tek documanlara ulaşmak için for döngüsüne alıyoruz
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String, Object> data=snapshot.getData();

                        String userEmail=(String) data.get("useremail");
                        String comment=(String) data.get("comment");
                        String commentDetail=(String) data.get("commentDetail");
                        String downloadUrl=(String) data.get("downloadurl");

                        Post post=new Post(userEmail,comment,commentDetail,downloadUrl);
                        postArrayList.add(post);

                    }
                    postAdapter.notifyDataSetChanged(); //adaptere yeni veri geldiğinde göster dersen recyleview dolar
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menuyu activitye bagladık
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.add_post){
            Intent intentToUpload=new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        }else if (item.getItemId()==R.id.signout){

            auth.signOut();
            Intent intentToMain=new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}