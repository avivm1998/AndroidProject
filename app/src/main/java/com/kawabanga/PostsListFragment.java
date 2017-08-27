package com.kawabanga;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kawabanga.model.ModelPost;
import com.kawabanga.model.ModelUser;
import com.kawabanga.model.Post;
import com.kawabanga.model.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostsListFragment.PostsListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link PostsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsListFragment extends Fragment {
    ListView list;
    List<Post> data;
    PostListAdapter adapter;

    private static final String USER_ID = "user_id";
    private String user_id;

    public PostsListFragment() {
        Log.d("TAG", "post list frag ctor");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    public static PostsListFragment newInstance(String user_id) {
        Log.d("TAG", "post list frag newinstance");
        PostsListFragment fragment = new PostsListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "post list frag oncreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_id = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //inflate the layout for this fragment
        Log.d("TAG", "post list oncreateview");
        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);

        if(user_id == null)
            data = ModelPost.instance.getAllPosts();

        else
            data = ModelPost.instance.getAllPostsByOwnerID(user_id);

        Collections.reverse(data);

        list = (ListView) view.findViewById(R.id.posts_list);
        adapter = new PostListAdapter();
        list.setAdapter(adapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        Log.d("TAG", "post list frag onattach");
        super.onAttach(context);
        /*if (context instanceof PostsListFragmentListener) {
            mListener = (PostsListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onAttach(Activity context) {
        Log.d("TAG", "post list frag onattach");
        super.onAttach(context);
        /*if (context instanceof PostsListFragmentListener) {
            mListener = (PostsListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDetach() {
        Log.d("TAG", "post list frag ondetach");
        super.onDetach();
        EventBus.getDefault().unregister(this);
        //mListener = null;
    }

    //update the posts list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ModelPost.UpdatePostEvent event) {
        Log.d("TAG", "post list frag onmessageevent");
        //check if relevant for owner
        if(user_id != null)
            if(!event.post.ownerID.equals(user_id))
                return ;

        boolean exist = false;

        for (int i = 0 ; i<data.size() ; i++){
            Post post = data.get(i) ;
            if (post.id.equals(event.post.id)){
                exist = true;
                if(event.post.isRemoved == 1) {
                    data.remove(i);
                }
                else
                    data.set(i, event.post);
                break;
            }
        }
        if (!exist && event.post.isRemoved != 1){
            data.add(event.post);
        }
        adapter.notifyDataSetChanged();
    }

    //the adapter class
    class PostListAdapter extends BaseAdapter {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("TAG", "post list adapter getview");

            if(convertView == null){
                // Inflate the layout for this fragment
                convertView = inflater.inflate(R.layout.fragment_post,null);
            }

            final Post post = data.get(position);
            TextView likes  = (TextView) convertView.findViewById(R.id.post_likes);
            TextView description  = (TextView) convertView.findViewById(R.id.post_description);
            final TextView username = (TextView) convertView.findViewById(R.id.post_username);
            final ImageView image = (ImageView) convertView.findViewById(R.id.post_image);
            Button delete_post = (Button) convertView.findViewById(R.id.post_delete_button);
            ImageButton like_post = (ImageButton) convertView.findViewById(R.id.post_like_button);

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.post_progressbar);

            likes.setText(post.likes);
            description.setText(post.description);
            image.setTag(post.imageURL);
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_smiley, null));

            delete_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelPost.instance.deletePost(post);
                }
            });

            like_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!post.likers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) { // check if not liked already
                        Integer likes = Integer.valueOf(post.likes) + 1;
                        post.likes = likes.toString();

                        if(post.likers.equals("")) {
                            post.likers = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        }

                        else {
                            post.likers += "__,__" + FirebaseAuth.getInstance().getCurrentUser().getUid();
                        }

                        ModelPost.instance.updatePost(post);
                    }

                    else {
                        Toast.makeText(getActivity(), "Post already liked!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.ownerID)) {
                delete_post.setVisibility(View.VISIBLE);
            }

            else {
                delete_post.setVisibility(View.GONE);
            }

            ModelUser.instance.getUser(post.ownerID, new ModelUser.GetUserCallback() {
                @Override
                public void onComplete(User user) {
                    username.setText("@" + user.name);
                }

                @Override
                public void onCancel() {}
            });

            if(!post.imageURL.isEmpty())
            {
                progressBar.setVisibility(View.VISIBLE);
                ModelPost.instance.getImage(post.imageURL, new ModelPost.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image_bmp) {
                        String tagUrl = image.getTag().toString();
                        if (tagUrl.equals(post.imageURL)) {
                            image.setImageBitmap(image_bmp);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
            return convertView ;
        }
}
}
