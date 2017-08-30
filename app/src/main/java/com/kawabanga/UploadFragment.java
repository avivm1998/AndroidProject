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
 * {@link UploadFragment.UploadFragmentListener} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {
    // the fragment initialization parameters
    private static final String USER_ID = "user_id";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String user_id;
    private Post post;
    private Bitmap imageBitmap;
    ImageView image;
    private Lock l = new ReentrantLock();

    private UploadFragmentListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user_id the post's owner.
     * @return A new instance of fragment UploadFragment.
     */
    public static UploadFragment newInstance(String user_id) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_id = getArguments().getString(USER_ID);
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
                post.ownerID = user_id;
                post.id = post.ownerID + getTimeValue(); //random id

                //update the fields
                UploadFragment.this.post.description = description.getText().toString();

                //implementation of the interface
                ModelPost.instance.saveImage(imageBitmap, "image_" + post.id + ".jpeg", new ModelPost.SaveImageListener() {
                    @Override
                    public void complete(String url) {
                        l.lock();

                        UploadFragment.this.post.imageURL = url;
                        ModelPost.instance.addPost(UploadFragment.this.post);
                        progressBar.setVisibility(GONE);
                        mListener.onUpload();

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
        if (context instanceof UploadFragmentListener) {
            mListener = (UploadFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof UploadFragmentListener) {
            mListener = (UploadFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface UploadFragmentListener {
        void onUpload();
    }
}
