package com.kawabanga.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;


public class PostSql {
    public static final String POST_TABLE = "posts";
    public static final String POST_ID = "id";
    public static final String POST_DESCRIPTION="description";
    public static final String POST_OWNER_ID = "ownerID";
    public static final String POST_LIKES = "likes";
    public static final String POST_LIKERS = "likers";
    public static final String POST_IS_REMOVED = "isRemoved";
    public static final String POST_IMAGE_URL = "imageURL";
    public static final String POST_LAST_UPDATE_DATE = "lastUpdateDate";


    static List<Post> getAllPostsByOwnerID(SQLiteDatabase db, String ownerID)
    {
        String args[] = { ownerID} ;
        Cursor cursor = db.query(POST_TABLE, null, POST_OWNER_ID + "=?",args , null, null, null);
        return getListOfPostsFromCursor(cursor);

    }


    static List<Post> getAllPosts(SQLiteDatabase db) {
        Log.d("TAG", "post sql getallposts");
        Cursor cursor = db.query(POST_TABLE, null, null, null, null, null, null);
        return getListOfPostsFromCursor(cursor);
    }

    static void addPost(SQLiteDatabase db, Post post) {
        ContentValues values = new ContentValues();
        values.put(POST_ID, post.id);
        values.put(POST_DESCRIPTION, post.description);
        values.put(POST_OWNER_ID, post.ownerID);
        values.put(POST_LIKES, post.likes);
        values.put(POST_LIKERS, post.likers);
        values.put(POST_IS_REMOVED, post.isRemoved);
        values.put(POST_LAST_UPDATE_DATE, post.lastUpdateDate);
        values.put(POST_IMAGE_URL, post.imageURL);
        db.insert(POST_TABLE, POST_ID, values);
    }

    static Post getPost(SQLiteDatabase db, String post_id) {

        if(post_id != null) {
            Cursor cursor = db.query(POST_TABLE, null, POST_ID + "=?", new String[]{post_id}, null, null, null);
            if (cursor.getCount() != 0) {


                cursor.moveToFirst();

                int idIndex = cursor.getColumnIndex(POST_ID);
                int descriptionIndex =cursor.getColumnIndex(POST_DESCRIPTION);
                int OwnerIDIndex = cursor.getColumnIndex(POST_OWNER_ID);
                int likesIndex = cursor.getColumnIndex(POST_LIKES);
                int likersIndex = cursor.getColumnIndex(POST_LIKERS);
                int isRemovedIndex = cursor.getColumnIndex(POST_IS_REMOVED);
                int imageUrlIndex = cursor.getColumnIndex(POST_IMAGE_URL);
                int lastUpdateIndex = cursor.getColumnIndex(POST_LAST_UPDATE_DATE);
                Post post = new Post();
                post.id = cursor.getString(idIndex);
                post.description = cursor.getString(descriptionIndex);
                post.ownerID=cursor.getString(OwnerIDIndex);
                post.likes = cursor.getString(likesIndex);
                post.likers = cursor.getString(likersIndex);
                post.isRemoved=cursor.getInt(isRemovedIndex);
                post.imageURL = cursor.getString(imageUrlIndex);
                post.lastUpdateDate = cursor.getDouble(lastUpdateIndex);
                return post;
            }

        }
        return null;

    }

    static public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + POST_TABLE +
                " (" +
                POST_ID + " TEXT PRIMARY KEY, " +
                POST_DESCRIPTION + " TEXT, " +
                POST_OWNER_ID + " TEXT, " +
                POST_LIKES + " NUMBER, " +
                POST_LIKERS + " TEXT, " +
                POST_IS_REMOVED + " NUMBER, " +
                POST_IMAGE_URL + " TEXT, " +
                POST_LAST_UPDATE_DATE + " DOUBLE); ");
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + POST_TABLE + ";");
        onCreate(db);
    }
    static public void deletePost(SQLiteDatabase db, Post post)
    {
        db.delete(POST_TABLE,POST_ID+"=?",new String[]{post.id});
    }

    static public void updatePost(SQLiteDatabase db, Post post)
    {
        ContentValues values = new ContentValues();
        values.put(POST_ID, post.id);
        values.put(POST_DESCRIPTION, post.description);
        values.put(POST_OWNER_ID, post.ownerID);
        values.put(POST_LIKES, post.likes);
        values.put(POST_LIKERS, post.likers);
        values.put(POST_IS_REMOVED, post.isRemoved);
        values.put(POST_LAST_UPDATE_DATE, post.lastUpdateDate);
        values.put(POST_IMAGE_URL, post.imageURL);
        db.update(POST_TABLE, values, POST_ID+"=?", new String[]{post.id});

    }
    public static List<Post> getListOfPostsFromCursor(Cursor cursor)
    {
        Log.d("TAG", "post sql getlistofposts");
        List<Post> list = new LinkedList<Post>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(POST_ID);
            int descriptionIndex =cursor.getColumnIndex(POST_DESCRIPTION);
            int OwnerIDIndex = cursor.getColumnIndex(POST_OWNER_ID);
            int likesIndex = cursor.getColumnIndex(POST_LIKES);
            int likersIndex = cursor.getColumnIndex(POST_LIKERS);
            int isRemovedIndex = cursor.getColumnIndex(POST_IS_REMOVED);
            int imageUrlIndex = cursor.getColumnIndex(POST_IMAGE_URL);
            int lastUpdateIndex = cursor.getColumnIndex(POST_LAST_UPDATE_DATE);

            do {
                Post post = new Post();
                post.id = cursor.getString(idIndex);
                post.description = cursor.getString(descriptionIndex);
                post.ownerID=cursor.getString(OwnerIDIndex);
                post.likes = cursor.getString(likesIndex);
                post.likers = cursor.getString(likersIndex);
                post.isRemoved=cursor.getInt(isRemovedIndex);
                post.imageURL = cursor.getString(imageUrlIndex);
                post.lastUpdateDate = cursor.getDouble(lastUpdateIndex);
                if(post.isRemoved != 1)
                    list.add(post);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
