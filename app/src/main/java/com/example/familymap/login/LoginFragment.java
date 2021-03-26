package com.example.familymap.login;

import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.familymap.DataCache;
import com.example.familymap.R;
import com.example.familymap.ServerProxy;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Models.Person;
import Results.LoginResult;
import Results.PersonResult;
import Results.RegisterResult;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private static final String PERSONID_KEY = "LoginKey";
    private static final String AUTHTOKEN_KEY = "AuthtokenKey";
    private static final String USERNAME_KEY = "UsernameKey";
    private static final String MESSAGE_KEY = "MessageKey";
    private static final String FIRST_NAME_KEY = "FirstNameKey";

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
    private Button signInButton;
    private Button registerButton;
    private String gender;
    private boolean success;
    private String authToken;
    private String personID;



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
        signInButton = view.findViewById(R.id.btnSignIn);
        registerButton = view.findViewById(R.id.btnRegister);

        signInButton.setEnabled(false);
        registerButton.setEnabled(false);



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
                String serverHost = serverHostEditText.getText().toString();
                String serverPort = serverPortEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (validInputs(serverHost, serverPort, userName, password, "login")) {
                    signInButton.setEnabled(true);
                }
                else {
                    signInButton.setEnabled(false);
                }


                if (validInputs(serverHost, serverPort, userName, password, "register")) {
                    registerButton.setEnabled(true);
                }
                else {
                    registerButton.setEnabled(false);
                }
            }
        });




        serverHostEditText.addTextChangedListener(loginTextWatcher);
        serverPortEditText.addTextChangedListener(loginTextWatcher);
        userNameEditText.addTextChangedListener(loginTextWatcher);
        passwordEditText.addTextChangedListener(loginTextWatcher);
        firstNameEditText.addTextChangedListener(loginTextWatcher);
        lastNameEditText.addTextChangedListener(loginTextWatcher);
        emailEditText.addTextChangedListener(loginTextWatcher);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverHost = serverHostEditText.getText().toString();
                String serverPort = serverPortEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Handler loginHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        if (!bundle.getString(MESSAGE_KEY, "").equals("")) {
                            Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            success = false;
                        }
                        else {
                            success = true;
                            Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                        personID = bundle.getString(PERSONID_KEY, "");
                        authToken = bundle.getString(AUTHTOKEN_KEY, "");
                    }
                };

                LoginTask task = new LoginTask(loginHandler, serverHost, serverPort, userName, password);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);

            }
        });


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
                if (validInputs(serverHost, serverPort, userName, password, "register")) {
                    Handler registerHandler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            if (!bundle.getString(MESSAGE_KEY, "").equals("")) {
                                Toast.makeText(getContext(), "Register Failed", Toast.LENGTH_SHORT).show();
                                success = false;
                            }
                            else {
                                success = true;
                                Toast.makeText(getContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                            }
                            personID = bundle.getString(PERSONID_KEY, "");
                            authToken = bundle.getString(AUTHTOKEN_KEY, "");
                            String user = bundle.getString(USERNAME_KEY, "");
                        }
                    };
                    RegisterTask task = new RegisterTask(registerHandler, serverHost, serverPort, userName, password, firstName, lastName, email, gender);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(task);
                }
            }
        });

        return view;
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String serverHost = serverHostEditText.getText().toString();
            String serverPort = serverPortEditText.getText().toString();
            String userName = userNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (validInputs(serverHost, serverPort, userName, password, "login")) {
                signInButton.setEnabled(true);
            }
            else {
                signInButton.setEnabled(false);
            }


            if (validInputs(serverHost, serverPort, userName, password, "register")) {
                registerButton.setEnabled(true);
            }
            else {
                registerButton.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean validInputs(String serverHost, String serverPort, String userName,
                                String password, String selection) {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        if (serverHost.length() == 0) {
            return false;
        }
        if (serverPort.length() == 0) {
            return false;
        }
        if (userName.length() == 0) {
            return false;
        }
        if (password.length() == 0) {
            return false;
        }

        if (selection != "login") {
            if (firstName.length() == 0) {
                return false;
            }
            if (lastName.length() == 0) {
                return false;
            }
            if (email.length() == 0) {
                return false;
            }
            if (gender == null) {
                return false;
            }
        }

        return true;
    }


    private class DataRetrievalTask implements Runnable {

        String serverHost;
        String serverPort;
        String authToken;

        private final Handler dataHandler;

        public DataRetrievalTask(Handler dataHandler, String serverHost, String serverPort, String authToken) {
            this.dataHandler=dataHandler;
            this.serverHost=serverHost;
            this.serverPort=serverPort;
            this.authToken=authToken;
        }

        @Override
        public void run() {
            PersonResult personResult = ServerProxy.retrieveData(serverHost, serverPort, authToken);
            DataCache cache = DataCache.getInstance();
            sendMessage(personResult);
        }

        private void sendMessage(PersonResult result) {
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            if (result.isSuccessful()) {

            }
            else {
                messageBundle.putString(MESSAGE_KEY, result.getMessage());
            }
            message.setData(messageBundle);

            dataHandler.sendMessage(message);
        }
    }

    Handler dataHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            if (!bundle.getString(MESSAGE_KEY, "").equals("")) {
                Toast.makeText(getContext(), "Data Retrieval Failed", Toast.LENGTH_SHORT).show();
                success = false;
            }
            else {
                success = true;

                DataCache cache = DataCache.getInstance();
                Person p = cache.findPerson(personID);
                String out = p.getFirstName() + " " + p.getLastName();
                Toast.makeText(getContext(), out, Toast.LENGTH_SHORT).show();

            }
        }
    };

    private class LoginTask implements Runnable {
        String serverHost;
        String serverPort;
        String userName;
        String password;


        private final Handler loginHandler;


        public LoginTask(Handler loginHandler, String serverHost, String serverPort, String userName, String password) {
            this.loginHandler=loginHandler;
            this.serverHost=serverHost;
            this.serverPort=serverPort;
            this.userName=userName;
            this.password=password;
        }

        @Override
        public void run() {
            LoginResult result = ServerProxy.login(serverHost, serverPort, userName, password);
            sendMessage(result);
        }

        private void sendMessage(LoginResult result) {
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            if (result.isSuccessful()) {
                messageBundle.putString(AUTHTOKEN_KEY, result.getAuthtoken());
                messageBundle.putString(PERSONID_KEY, result.getPersonID());


                DataRetrievalTask dataTask = new DataRetrievalTask(dataHandler, serverHost, serverPort, result.getAuthtoken());
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(dataTask);
            }
            else {
                messageBundle.putString(MESSAGE_KEY, result.getMessage());
            }
            message.setData(messageBundle);

            loginHandler.sendMessage(message);
        }
    }


    private class RegisterTask implements Runnable {

        String serverHost;
        String serverPort;
        String userName;
        String password;
        String firstName;
        String lastName;
        String email;
        String gender;


        private final Handler registerHandler;


        public RegisterTask(Handler registerHandler, String serverHost, String serverPort, String userName, String password,
                            String firstName, String lastName, String email, String gender) {
            this.registerHandler=registerHandler;
            this.serverHost=serverHost;
            this.serverPort=serverPort;
            this.userName=userName;
            this.password=password;
            this.firstName=firstName;
            this.lastName=lastName;
            this.email=email;
            this.gender=gender;
        }

        @Override
        public void run() {
            RegisterResult result = ServerProxy.register(serverHost, serverPort, userName, password,
                    firstName, lastName, email, gender);
            sendMessage(result);
        }

        private void sendMessage(RegisterResult result) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            if (result.isSuccessful()) {
                messageBundle.putString(AUTHTOKEN_KEY, result.getAuthtoken());
                messageBundle.putString(USERNAME_KEY, result.getUsername());
                messageBundle.putString(PERSONID_KEY, result.getPersonID());

                DataRetrievalTask dataTask = new DataRetrievalTask(dataHandler, serverHost, serverPort, result.getAuthtoken());
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(dataTask);
            }
            else {
                messageBundle.putString(MESSAGE_KEY, result.getMessage());
            }
            message.setData(messageBundle);

            registerHandler.sendMessage(message);
        }


    }


}