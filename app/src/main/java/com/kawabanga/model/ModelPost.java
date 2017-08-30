package com.kawabanga.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.kawabanga.model.ModelFiles.saveImageToFile;

public class ModelPost
{
    public static ModelPost instance = new ModelPost();
        public ModelSql modelSql;
        private FirebasePost firebasePost;

        //constructor
        private ModelPost() {
            firebasePost = new FirebasePost();
            modelSql = new ModelSql(Kawabanga.getMyContext());
        }

        private void syncPostsDatabaseAndRegisterPostsUpdates() {
            //1. get local lastUpdateDate
            SharedPreferences pref = Kawabanga.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
            final double lastUpdateDate = pref.getFloat("PostsLastUpdateDate", 0);
            Log.d("TAG", "lastUpdateDate: " + lastUpdateDate);

            firebasePost.registerPostsUpdates(lastUpdateDate, new FirebasePost.RegisterPostUpdatesCallback() {
                @Override
                public void onPostUpdate(Post post) {
                    boolean isDeleted = false ;
                    //3. update the local db
                    if(PostSql.getPost(modelSql.getReadableDatabase(), post.id) == null)
                        PostSql.addPost(modelSql.getWritableDatabase(), post);
                    else {
                        if(post.isRemoved == 1) {
                            PostSql.deletePost(modelSql.getWritableDatabase(), post);
                            isDeleted = true ;
                        }
                        else
                            PostSql.updatePost(modelSql.getWritableDatabase(), post);
                    }
                    //4. update the lastUpdateDate
                    SharedPreferences pref = Kawabanga.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                    final double lastUpdateDate = pref.getFloat("PostsLastUpdateDate", 0);
                    if (lastUpdateDate < post.lastUpdateDate) {
                        SharedPreferences.Editor prefEd = Kawabanga.getMyContext().getSharedPreferences("TAG",
                                Context.MODE_PRIVATE).edit();
                        prefEd.putFloat("PostsLastUpdateDate", (float) post.lastUpdateDate);
                        prefEd.commit();
                        Log.d("TAG", "PostsLastUpdateDate: " + post.lastUpdateDate);
                    }

                    EventBus.getDefault().post(new UpdatePostEvent(post, isDeleted));
                }
            });
        }


        public List<Post> getAllPostsByOwnerID(String id) {
            return PostSql.getAllPostsByOwnerID(modelSql.getReadableDatabase(),id);
        }

        public List<Post> getAllPosts() {
            return PostSql.getAllPosts(modelSql.getReadableDatabase());
        }

        public void addPost(Post post) {
            firebasePost.addPost(post);
        }

        public void deletePost(Post post) {
            post.isRemoved  = 1 ;
            firebasePost.addPost(post);
        }

        public void updatePost(Post post)
        {
            firebasePost.addPost(post);
        }

public class UpdatePostEvent {
    public final Post post ;
    public final boolean isDeleted;
    public UpdatePostEvent(Post post, boolean isDeleted){
        this.post = post;
        this.isDeleted = isDeleted;
    }
}

public interface SaveImageListener {
    void complete(String url);
    void fail();
}

    public void RegisterUpdates()
    {
        syncPostsDatabaseAndRegisterPostsUpdates();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        firebasePost.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });


    }

public interface GetImageListener{
    void onSuccess(Bitmap image);
    void onFail();
}
    public void getImage(final String url, final GetImageListener listener) {
        //check if the image exists locally
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelFiles.loadImageFromFileAsync(fileName, new ModelFiles.LoadImageFromFileAsync() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }

                else {
                    firebasePost.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG","getImage from FB success " + fileName);
                            saveImageToFile(image,fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG","getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });
    }

    public void unRegisterUpdates()
    {
        firebasePost.unregisterPostsUpdates();
    }
}



