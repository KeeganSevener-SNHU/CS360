package com.zybooks.weighttracker;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import static com.zybooks.weighttracker.MainActivity.loggedIn;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.telephony.TelephonyManager;

public class Settings extends Fragment {

    Button goalBtn;
    EditText mGoal;
    TextView goalTxt;
    RadioButton rdBtnLbs, rdBtnKgs, rdBtnY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_settings, container,
                false);
        // Setup fragment if user is logged in.
        if (loggedIn) {

            WeightDB db = new WeightDB(requireContext());

            // Setup buttons for radio selection.
            rdBtnLbs = parentView.findViewById(R.id.radio_lbs);
            rdBtnKgs = parentView.findViewById(R.id.radio_kilo);
            rdBtnY = parentView.findViewById(R.id.yes);

            // Setup click Listeners for radio buttons
            rdBtnLbs.setOnClickListener(this::weightClick);
            rdBtnKgs.setOnClickListener(this::weightClick);
            rdBtnY.setOnClickListener(this::checkSMS);

            // Setup goal button and edit text.
            mGoal = parentView.findViewById(R.id.enter_weight);
            goalBtn = parentView.findViewById(R.id.create_button);
            goalBtn.setOnClickListener(this::updateGoal);
            // Setup goal text.
            goalTxt = parentView.findViewById(R.id.display_weight);
            goalTxt.setText(db.getGoal());
        }

        return parentView;
    }

    // Updating the weight type for the database.
    public void weightClick(View view) {
        String ran = "";
        WeightDB db = new WeightDB(requireContext());

        // Update the weight logs to kilograms if kg option is selected.
        if (rdBtnKgs.isChecked()) {
            ran = db.updateWeightType("kg");
            Toast.makeText(requireContext(), ran,
                    Toast.LENGTH_SHORT).show();
        }
        // Update the weight logs to pounds if kg option is selected.
        else if (rdBtnLbs.isChecked()) {
            ran = db.updateWeightType("lbs");
            Toast.makeText(requireContext(), ran,
                    Toast.LENGTH_SHORT).show();
        }
        // Update goal with new weight.
        goalTxt.setText(db.getGoal());
    }

    // Updating the goal for the database
    private void updateGoal(View view) {

        WeightDB db = new WeightDB(requireContext());

        // Update the goal if the user entered a number.
        if (mGoal.getText().toString().length() > 1) {
            double d = Double.parseDouble(mGoal.getText().toString());
            float weight = (float) d;
            String returnsStr = db.updateGoal(weight);
            Toast.makeText(requireContext(), returnsStr, Toast.LENGTH_SHORT).show();
            mGoal.getText().clear();
        }
    }


    public void checkSMS(View view) {

        if (rdBtnY.isChecked()) {
            // Enable SMS messaging.
            TelephonyManager telephonyManager = (TelephonyManager)
                    requireContext().getSystemService(Context.TELEPHONY_SERVICE);
            // Check if access is granted.
            if (
                    ActivityCompat.checkSelfPermission(requireContext(), SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED
                            ||
                    ActivityCompat.checkSelfPermission(requireContext(), READ_PHONE_NUMBERS)
                            != PackageManager.PERMISSION_GRANTED
                )
            {
                // Request permission if not granted.
                requestPermissions(new String[]{READ_PHONE_NUMBERS,
                        SEND_SMS}, 100);
            }
        }
    }
}



