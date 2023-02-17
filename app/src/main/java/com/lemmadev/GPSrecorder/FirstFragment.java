package com.lemmadev.GPSrecorder;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.lemmadev.GPSrecorder.databinding.FragmentFirstBinding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FirstFragment extends Fragment {

    private static final String SERVICE_RUNNING_KEY = "15399";
    private FragmentFirstBinding binding;

    private String csvLocation;
    private FirebaseFirestore db;

    private final Map<Integer, String> fieldDict = new HashMap<Integer, String>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        fieldDict.put(0, "datetime");
        fieldDict.put(1, "device");
        fieldDict.put(2, "ip");
        fieldDict.put(3, "latitude");
        fieldDict.put(4, "longitude");

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity().getApplicationContext();
        csvLocation = context.getFilesDir()+"/locations.csv";

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        binding.buttonRows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int nLocations = getNumberLocationsStored();
                binding.rowsCount.setText(String.valueOf(nLocations));

            }
        });
        binding.buttonUploadLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();

                // Get a reference to the collection to be updated
                CollectionReference collectionRef = db.collection("batch_collection");

                int counter = 0;
                int iteration = 0;
                while (counter < getNumberLocationsStored()) {
                    iteration++;
                    // Create a new batch
                    WriteBatch batch = db.batch();
                    counter = updateBatch(counter, iteration, collectionRef, batch);
                    System.out.println(counter+" documents sent.");
                    // Commit the batch
                    batch.commit()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Batch writing succeeded.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error performing batch write.", e);
                                }
                            });
            }

                Snackbar.make(view, "All locations stored were sent!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                try {
                    FileWriter overwrite = new FileWriter(csvLocation);
                    overwrite.close();
                } catch (Exception ignore) { }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of any views in the Fragment
        outState.putBoolean("switch_state", binding.switch2.isChecked());
        outState.putString("counter_state", binding.rowsCount.getText().toString());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // Restore the state of any views in the Fragment
        if (savedInstanceState != null) {
            boolean myCheckboxState = savedInstanceState.getBoolean("switch_state");
            String myTextViewText = savedInstanceState.getString("counter_state");

            binding.switch2.setChecked(myCheckboxState);
            binding.rowsCount.setText(myTextViewText);
        }
    }
    public int getNumberLocationsStored() {
        int count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvLocation));
            while (reader.readLine() != null) {
                count++;
            }
            reader.close();
        } catch (IOException ignore) {
        }
        return count;
    }
    public int updateBatch(int externalCounter, int iteration, CollectionReference collectionRef, WriteBatch batch) {

        int maxBatchSize = 500;
        int internalCounter = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvLocation));
            String line;

            while (((line = reader.readLine()) != null) && externalCounter < maxBatchSize*iteration) {
                internalCounter++;

                if (internalCounter > externalCounter) {

                    externalCounter++;
                    String[] fields = line.split(", ");
                    Map<String, Object> updateRow = new HashMap<>();

                    for (int i = 0; i < fields.length; i++) {
                        updateRow.put(fieldDict.get(i), fields[i]);
                    }

                    DocumentReference newDoc = collectionRef.document();
                    batch.set(newDoc, updateRow);

                }
            }
            reader.close();
        } catch (IOException ignore) { }

        return externalCounter;
    }
}