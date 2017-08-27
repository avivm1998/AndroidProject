package com.kawabanga;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kawabanga.model.ModelPost;
import com.kawabanga.model.Post;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {
    // the fragment initialization parameters
    private static final String USERNAME = "username";
    private static final String POST_ID = "post_id";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String username;
    private String post_id;
    private Post post;
    private Bitmap imageBitmap;
    ImageView image;
    private Lock l = new ReentrantLock();

    //private OnFragmentInteractionListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username the post's owner.
     * @param post_id the post's id.
     * @return A new instance of fragment UploadFragment.
     */
    public static UploadFragment newInstance(String username, String post_id) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(POST_ID, post_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
            post_id = getArguments().getString(POST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        final EditText description = (EditText) view.findViewById(R.id.upload_description);
        image = (ImageView) view.findViewById(R.id.upload_image);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.upload_progressbar);
        Button share = (Button) view.findViewById(R.id.upload_button);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                post = new Post();
                post.ownerID = MainActivity.user.id;
                post.id = post.ownerID + getTimeValue(); //random id

                //update the fields
                UploadFragment.this.post.description = description.getText().toString();

                //implementation of the interface
                ModelPost.instance.saveImage(imageBitmap, "image_" + UploadFragment.this.post_id + ".jpeg", new ModelPost.SaveImageListener() {
                    @Override
                    public void complete(String url) {
                        l.lock();

                        UploadFragment.this.post.imageURL = url;
                        ModelPost.instance.addPost(UploadFragment.this.post);
                        progressBar.setVisibility(GONE);
                        //UploadFragment.this.getActivity().getFragmentManager().popBackStack();

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        PostsListFragment postsListFragment = PostsListFragment.newInstance(null);
                        fragmentTransaction.replace(R.id.content, postsListFragment);
                        fragmentTransaction.commit();

                        l.unlock();
                    }

                    @Override
                    public void fail() {

                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public static String getTimeValue(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return  df.format(Calendar.getInstance().getTime());
    }
}
