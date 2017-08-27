package com.kawabanga;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SinginFragmentListener} interface
 * to handle interaction events.
 */

/*
* this class represent the fragment which responsible of signIn to the application*/
public class SignInFragment extends Fragment {

    private SinginFragmentListener mListener;
    private FirebaseAuth mAuth;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        final EditText mailET = (EditText) view.findViewById(R.id.signin_email);
        final EditText passwordET = (EditText) view.findViewById(R.id.signin_password);

        Button reisterB = (Button) view.findViewById(R.id.signin_register_button);
        Button signinB = (Button) view.findViewById(R.id.signin_login_button);

        reisterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRegisterClick();
            }
        });

        signinB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mailET.getText().toString();
                String pass = passwordET.getText().toString();

                //if at least one of the fields is empty
                if(mail.isEmpty() | pass.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please fill all of the details.",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(mail,pass)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                    mListener.onSignin();

                                }
                                else {
                                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view ;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SinginFragmentListener) {
            mListener = (SinginFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof SinginFragmentListener) {
            mListener = (SinginFragmentListener) context;
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
    public interface SinginFragmentListener {
        void onRegisterClick();
        void onSignin();
    }
}
