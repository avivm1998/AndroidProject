package com.kawabanga;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kawabanga.model.ModelPost;
import com.kawabanga.model.Post;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsListFragment extends Fragment {
    ListView list;
    List<Post> data;
    ResListAdapter adapter;
    private OnFragmentInteractionListener mListener;

    public PostsListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsListFragment newInstance() {
        PostsListFragment fragment = new PostsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        data= ModelPost.instance.getAllRestaurants();
        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);
        list = (ListView) view.findViewById(R.id.posts_list);
        adapter = new ResListAdapter();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mListener = null;
    }

    //update the restaurants list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ModelPost.UpdatePostEvent event) {
        //check if relevant for owner
        /*if(username != null)
            if(!event.res.ownerID.equals(user_id))
                return ;*/

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //the adapter class
    class ResListAdapter extends BaseAdapter {
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

            if(convertView == null){
                // Inflate the layout for this fragment
                convertView = inflater.inflate(R.layout.fragment_post,null);
            }

            final Post post = data.get(position);

            TextView name  = (TextView) convertView.findViewById(R.id.post_username);
          //  TextView likes  = (TextView) convertView.findViewById(R.id.post_likes);
            TextView description  = (TextView) convertView.findViewById(R.id.post_description);
            final ImageView image = (ImageView) convertView.findViewById(R.id.post_image);

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.post_progressbar);

            name.setText(post.name);
           // likes.setText(post.likes);
            description.setText(post.description);
            image.setTag(post.imageURL);
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_smiley_black_24dp, null));

            //if there is a logo
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
