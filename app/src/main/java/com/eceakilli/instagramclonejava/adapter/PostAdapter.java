package com.eceakilli.instagramclonejava.adapter;

import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eceakilli.instagramclonejava.databinding.RecyclerRowBinding;
import com.eceakilli.instagramclonejava.model.Post;
import com.eceakilli.instagramclonejava.view.FeedActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    ProgressDialog progressDialog;
    //kullanıcıya göstermek için post arraylisti tanımla
    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.recyclerViewEmailText.setText(postArrayList.get(position).email);
        holder.recyclerRowBinding.recyclerViewCommentDetailText.setText(postArrayList.get(position).commentDetail);
        holder.recyclerRowBinding.recyclerViewCommentText.setText(postArrayList.get(position).comment);

        holder.recyclerRowBinding.spinKit.setVisibility(View.VISIBLE);
        //görüntüleri gostermek için picasso kütphanesini kullanıyoruz

        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.recyclerViewImageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.recyclerRowBinding.spinKit.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.recyclerRowBinding.spinKit.setVisibility(View.GONE);

            }
        });




    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
          this.recyclerRowBinding=recyclerRowBinding;
        }
    }
}
