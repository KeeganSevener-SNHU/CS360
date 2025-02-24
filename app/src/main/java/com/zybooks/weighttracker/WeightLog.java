package com.zybooks.weighttracker;

import static com.zybooks.weighttracker.MainActivity.loggedIn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import android.telephony.TelephonyManager;

public class WeightLog extends Fragment {

    // Declare variables for the WeightLog fragment code.
    static long holderPos = -1;
    Dialog addDialog, deleteDialog, updateDialog;
    FloatingActionButton fabBtn;
    Button cancelBtn, addBtn, upBtn, upCancel, updateBtn;
    Button deleteBtn, canclBtn, delBtn;
    EditText mDate, mWeight, upDate, upWeight;
    TextView txtV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_weight_log,
                container, false);

        // Get database.
        WeightDB db = new WeightDB(requireContext());

        // Load functionality if the user is logged in,
        if (loggedIn) {

            // FAB button find view.
            fabBtn = parentView.findViewById(R.id.addFAB);

            // Create dialog box for the add log option.
            addDialog = new Dialog(requireContext());
            addDialog.setContentView(R.layout.add_dialog);
            addDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // Setup buttons and EditText fields.
            addBtn = addDialog.findViewById(R.id.addButton);
            cancelBtn = addDialog.findViewById(R.id.cancelDialog);
            mWeight = addDialog.findViewById(R.id.editWeight);
            mDate = addDialog.findViewById(R.id.editDate);

            // Create onClick listeners for the buttons.
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDialog.dismiss();
                }
            });

            // Onclick listener fo the add button.
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Check that input is correct
                    if (mWeight.getText().toString().length() > 1
                            && mDate.getText().toString().length() > 9) {
                        // Get input from user.
                        double d = Double.parseDouble(mWeight.getText().toString());
                        float weight = (float) d;
                        String date = mDate.getText().toString();

                        // Add to database.
                        db.addLog(weight, date);
                        // Confirm it was added to the user.
                        Toast.makeText(requireContext(), "Log Added.", Toast.LENGTH_SHORT).show();
                        smsCongrats(weight, db);
                    } else {
                        Toast.makeText(requireContext(), "Please input weight \n and date"
                                , Toast.LENGTH_SHORT).show();
                    }
                    // Close the dialog.

                    // Update the view with the new list.
                    ArrayList<LogDocs> stringList = db.getLogs();
                    RecyclerView recyclerView = parentView.findViewById(R.id.log_list);
                    LogAdapter adapter = new LogAdapter(requireContext(), stringList);
                    recyclerView.setAdapter(adapter);

                    addDialog.dismiss();
                }
            });
            // Click listener for FAB button.
            fabBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDialog.show();
                }
            });




            // Create Update dialog option.
            upBtn = parentView.findViewById(R.id.update_button);
            updateDialog = new Dialog(requireContext());
            updateDialog.setContentView(R.layout.update_dialog);
            updateDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            //Setup edit text boxes
            upWeight = updateDialog.findViewById(R.id.upWeight);
            upDate = updateDialog.findViewById(R.id.upDate);
            upCancel = updateDialog.findViewById(R.id.cancelUpdate);
            updateBtn = updateDialog.findViewById(R.id.subUpdate);

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check that input is correct
                    if (upWeight.getText().toString().length() > 1
                            && upDate.getText().toString().length() > 9) {
                        // Get input from user.
                        double d = Double.parseDouble(upWeight.getText().toString());
                        float weight = (float) d;
                        String date = upDate.getText().toString();

                        // Update log in database.
                        String retStr = db.updateLog(date, weight, holderPos);
                        // Confirm log was updated to the user.
                        Toast.makeText(requireContext(), retStr, Toast.LENGTH_SHORT).show();
                        smsCongrats(weight, db);
                        holderPos = -1;
                    } else {
                        Toast.makeText(requireContext(), "Please input weight \n and date"
                                , Toast.LENGTH_SHORT).show();
                    }
                    // Update the view with the new list.
                    ArrayList<LogDocs> stringList = db.getLogs();
                    RecyclerView recyclerView = parentView.findViewById(R.id.log_list);
                    LogAdapter adapter = new LogAdapter(requireContext(), stringList);
                    recyclerView.setAdapter(adapter);
                    // Close the dialog.
                    updateDialog.dismiss();
                }
            });


            // Create onClick listeners for the buttons.
            upCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateDialog.dismiss();
                }
            });

            upBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (holderPos > 0) {
                        LogDocs log = db.getALog(holderPos);

                        upWeight.setText(log.getWeight() + "");
                        upDate.setText(log.getDate());
                        updateDialog.show();
                    }
                    else {
                        Toast.makeText(requireContext(), "Please select a log.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });



            //Setup delete button
            deleteBtn = parentView.findViewById(R.id.delete_button);
            deleteDialog = new Dialog(requireContext());
            deleteDialog.setContentView(R.layout.delete_dialog);
            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            txtV = deleteDialog.findViewById(R.id.textV);
            delBtn = deleteDialog.findViewById(R.id.delButton);
            canclBtn = deleteDialog.findViewById(R.id.delete_cancel_dialog);

            // Create onClick listeners for the buttons.
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holderPos > 0) {
                        String foundDate = db.getDateById(holderPos);
                        txtV.setText("Are you sure you want to delete this log? - " + foundDate
                                + " - \n This cannot be undone.");
                        deleteDialog.show();
                    }
                    else {
                        Toast.makeText(requireContext(), "Please select a log.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            canclBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDialog.dismiss();
                }
            });
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.deleteId(holderPos);
                    Toast.makeText(requireContext(), "Log deleted",
                            Toast.LENGTH_SHORT).show();
                    holderPos = -1;
                    // Update the view with the new list.
                    ArrayList<LogDocs> stringList = db.getLogs();
                    RecyclerView recyclerView = parentView.findViewById(R.id.log_list);
                    LogAdapter adapter = new LogAdapter(requireContext(), stringList);
                    recyclerView.setAdapter(adapter);

                    deleteDialog.dismiss();
                }
            });

            // Setup the recycler for the view and create ArrayList for logs.
            ArrayList<LogDocs> stringList = db.getLogs();
            RecyclerView recyclerView = parentView.findViewById(R.id.log_list);
            LogAdapter adapter = new LogAdapter(requireContext(), stringList);
            recyclerView.setAdapter(adapter);
        }

        return parentView;
    }

    private class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogHolder> {
        // Context and ArrayList fo the LogAdapter to use.
        Context context;
        ArrayList<LogDocs> stringList;

        // Assigns the context and ArrayList passed in by WeightLog onCreateView()
        public LogAdapter(Context context, ArrayList<LogDocs> stringList) {
            this.context = context;
            this.stringList = stringList;
        }

        @NonNull
        @Override
        public LogAdapter.LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the layout and give the look to our rows.
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.weight_logs, parent, false);
            return new LogAdapter.LogHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LogAdapter.LogHolder holder, int position) {
            // This method assigns the values to the rows as they enter the screen.
            // Get variables from LogDocs object
            long id = stringList.get(position).getId();
            float weight = stringList.get(position).getWeight();
            String measure = stringList.get(position).getMeasure();
            String date = stringList.get(position).getDate();

            // Create string
            String textToSet = weight + " " + measure + " \n" + date;

            // Setup textView
            holder.textView.setText(textToSet);
            holder.textView.setTextColor(Color.parseColor("#FFFFFF"));
            holder.textView.setTextAlignment(holder.textView.getTextAlignment() + 3);
            holder.textView.setTextSize(20);
            // Add id to Tag for other operations.
            holder.textView.setTag(id);

            //Setup onClick listener to find currently selected item.
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Assigns to global variable to be deleted on command.
                    holderPos = (long) v.getTag();
                    Toast.makeText(
                            requireContext(), "\nSelected " +
                                    holder.textView.getText().toString()
                            , Toast.LENGTH_SHORT).show();
                    /*holder.textView.setBackground(
                            Drawable.createFromPath("@color/orange_100"));*/
                }
            });

        }

        @Override
        public int getItemCount() {
            // Get total number of items for onBindViewHolder
            return stringList.size();
        }

        public static class LogHolder extends RecyclerView.ViewHolder {
            // This class is grabs the views from recycler view layout.
            TextView textView;
            public LogHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.recView);
            }
        }
    }

    public void smsCongrats(float weight, WeightDB db) {

        // Send user a notification congratulating them.
        if (String.valueOf(weight).equals(db.getGoal())) {
            TelephonyManager telephonyManager = (TelephonyManager)
                    requireContext().getSystemService(Context.TELEPHONY_SERVICE);
            try {
                // Permission is requested in settings.
                @SuppressLint("MissingPermission")
                String phoneNumber = telephonyManager.getLine1Number();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null,
                        "Congrats on meeting your goal weight!!",
                        null, null);
            } catch (Exception e) {
                Log.d("Exception SMS", "Something went wrong.");
            }
        }
    }
}