package com.zybooks.weighttracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import static com.zybooks.weighttracker.MainActivity.loggedIn;

public class Login extends Fragment {


    // Test user login is
    // Username : User
    // Password : 123
    Button createBtn, loginBtn;
    EditText mUser, mPass;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        WeightDB db = new WeightDB(requireContext());

        // Create buttons
        loginBtn = rootView.findViewById(R.id.login_button);
        createBtn = rootView.findViewById(R.id.create_button);
        // Assign text fields
        mUser = rootView.findViewById(R.id.edit_username);
        mPass = rootView.findViewById(R.id.edit_password);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new account.
                if (mUser.getText().toString().length() > 1
                        && mPass.getText().toString().length() > 1) {
                    db.addLogin
                            (mUser.getText().toString(),
                            mPass.getText().toString());
                    // Welcome the new user!
                    Toast.makeText(requireContext(), "Welcome, " +
                            mUser.getText().toString() + "!", Toast.LENGTH_SHORT).show();

                    mUser.getText().clear();
                    mPass.getText().clear();
                    loggedIn = true;
                } else {
                    Toast.makeText(requireContext(), "Please enter\n" +
                            "name and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Onclick listener fo the add button.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check user login
                if (mUser.getText().toString().length() > 1
                        && mPass.getText().toString().length() > 1) {
                    boolean yes = db.getUser(
                            mUser.getText().toString(),
                            mPass.getText().toString()
                    );
                    // Welcome user if login exists.
                    if (yes) {

                        Toast.makeText(requireContext(), "Hello," +
                                mUser.getText().toString(),
                                Toast.LENGTH_SHORT).show();

                        loggedIn = true;
                        mUser.getText().clear();
                        mPass.getText().clear();
                    }
                    // Inform user if login does not exist.
                    else { Toast.makeText(requireContext(),
                            "Sorry, user not found",
                            Toast.LENGTH_SHORT).show();}
                }
                // Please provide a valid login.
                else { Toast.makeText(requireContext(), "Please enter\n" +
                            "name and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }
}

