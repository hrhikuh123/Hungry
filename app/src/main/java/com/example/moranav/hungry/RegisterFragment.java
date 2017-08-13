package com.example.moranav.hungry;


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

import com.example.moranav.hungry.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */

/*
* this class represent the fragment which responsible of registering to the application*/
public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RegisterFragmentListener mListener;
    User u  ;

    public RegisterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button register = (Button) view.findViewById(R.id.register_register);
        final EditText mailET = (EditText) view.findViewById(R.id.mail_register);
        final EditText passET = (EditText) view.findViewById(R.id.pass_register);
        final EditText fullnameET = (EditText) view.findViewById(R.id.fullname_register);
        final EditText addressET = (EditText) view.findViewById(R.id.address_register);
        final EditText phoneET = (EditText) view.findViewById(R.id.phone_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mailET.getText().toString();
                String pass = passET.getText().toString();
                String fullname = fullnameET.getText().toString();
                String address = addressET.getText().toString();
                String phone = phoneET.getText().toString();

                //if at least one of the fields is empty
                if(mail.isEmpty() || pass.isEmpty() || fullname.isEmpty() ||address.isEmpty()||phone.isEmpty()) {
                    Toast.makeText(getActivity(), "עלייך למלא את כל הפרטים", Toast.LENGTH_SHORT).show();
                    return ;
                }

                //create a new user and add him to database
                u = new User() ;
                u.name = fullname ;
                u.address = address ;
                u.phone = phone ;
                mAuth.createUserWithEmailAndPassword(mail,pass)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
                                    u.id = mAuth.getCurrentUser().getUid();
                                    mListener.onRegisterSuccess(u);
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
        if (context instanceof SignInFragment.SinginFragmentListener) {
            mListener = (RegisterFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof SignInFragment.SinginFragmentListener) {
            mListener = (RegisterFragmentListener) context;
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





    public interface RegisterFragmentListener {
        void onRegisterSuccess(User user);
    }

}







