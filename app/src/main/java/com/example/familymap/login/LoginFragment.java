package com.example.familymap.login;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.familymap.R;
import com.example.familymap.ServerProxy;

import Models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private EditText serverHostEditText;
    private EditText serverPortEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private String gender;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHostEditText = view.findViewById(R.id.editServerHost);
        serverPortEditText = view.findViewById(R.id.editServerPort);
        userNameEditText = view.findViewById(R.id.editUserName);
        passwordEditText = view.findViewById(R.id.editPassword);
        firstNameEditText = view.findViewById(R.id.editFirstName);
        lastNameEditText = view.findViewById(R.id.editLastName);
        emailEditText = view.findViewById(R.id.editEmail);

        RadioGroup genderRadioEdit = view.findViewById(R.id.editRadioGender);
        genderRadioEdit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadio) {
                    gender = "m";
                }
                else {
                    gender = "f";
                }
            }
        });

        Button signInButton = view.findViewById(R.id.btnSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverHost = serverHostEditText.getText().toString();
                String serverPort = serverPortEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (validInputs(serverHost, serverPort, userName, password, "login", view)) {
                    LoginThread login = new LoginThread(serverHost, serverPort, userName, password);
                    login.start();
                }
            }
        });

        Button registerButton = view.findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverHost = serverHostEditText.getText().toString();
                String serverPort = serverPortEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                if (validInputs(serverHost, serverPort, userName, password, "register", view)) {
                    register(serverHost, serverPort, userName, password, firstName, lastName, email, gender);
                }
            }
        });

        return view;
    }

    private boolean validInputs(String serverHost, String serverPort, String userName,
                                String password, String selection, View v) {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        Context context = getContext();
        if (serverHost.length() == 0) {
            Toast.makeText(context, "Invalid Server Host", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (serverPort.length() == 0) {
            Toast.makeText(context, "Invalid Server Port", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (userName.length() == 0) {
            Toast.makeText(context, "Invalid User Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() == 0) {
            Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selection != "login") {
            if (firstName.length() == 0) {
                Toast.makeText(context, "Invalid First Name", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (lastName.length() == 0) {
                Toast.makeText(context, "Invalid Last Name", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (email.length() == 0) {
                Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (gender.length() == 0) {
                Toast.makeText(context, "Invalid Gender Selection", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    //change this from a thread to other background task (watch lecture)
    class LoginThread extends Thread {
        String serverHost;
        String serverPort;
        String userName;
        String password;
        public LoginThread(String serverHost, String serverPort, String userName, String password) {
            this.serverHost=serverHost;
            this.serverPort=serverPort;
            this.userName=userName;
            this.password=password;
        }

        public void run() {
            Context context = getContext();
            ServerProxy.login(serverHost, serverPort, userName, password, context);
        }
    }


    private void register(String serverHost, String serverPort, String userName, String password,
                          String firstName, String lastName, String email, String gender) {

    }


}