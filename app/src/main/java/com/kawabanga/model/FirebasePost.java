package com.kawabanga.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bugsec on 20/08/2017.
 */

public class FirebasePost {
    public void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PostSql.POST_TABLE);
        Map<String, Object> value = new HashMap<>();
        value.put(PostSql.POST_ID, post.id);
        value.put(PostSql.POST_NAME, post.name);
        value.put(PostSql.POST_DESCRIPTION, post.description);
        value.put(PostSql.POST_OWNER_ID, post.ownerID);
        value.put(PostSql.POST_IS_REMOVED, post.isRemoved);
        value.put(PostSql.POST_LAST_UPDATE_DATE, ServerValue.TIMESTAMP);
        value.put(PostSql.POST_IMAGE_URL, post.imageURL);

        myRef.child(post.id).setValue(value);
    }

    interface GetPostCallback {
        void onComplete(Post post);

        void onCancel();
    }

    public void getPost(String stId, final GetPostCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PostSql.POST_TABLE);
        myRef.child(stId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                callback.onComplete(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    interface RegisterPostUpdatesCallback {
        void onPostUpdate(Post post);
    }
    public void registerPostsUpdates(double lastUpdateDate,
                                           final RegisterPostUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PostSql.POST_TABLE);
         myRef.orderByChild(PostSql.POST_LAST_UPDATE_DATE).startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("TAG","onChildAdded called");
                        Post post = dataSnapshot.getValue(Post.class);
                        callback.onPostUpdate(post);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        callback.onPostUpdate(post);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        callback.onPostUpdate(post);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        callback.onPostUpdate(post);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void saveImage(Bitmap imageBmp, String name, final ModelPost.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });
    }

    public void getImage(String url, final ModelPost.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                listener.onFail();
            }
        });
    }

    public void unregisterPostsUpdates()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PostSql.POST_TABLE);
    }
}
