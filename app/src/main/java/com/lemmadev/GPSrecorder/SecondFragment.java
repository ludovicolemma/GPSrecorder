package com.lemmadev.GPSrecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lemmadev.GPSrecorder.databinding.FragmentSecondBinding;

import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {
    
    private FragmentSecondBinding binding;

    public final MutableLiveData<Boolean> switchChecked = new MutableLiveData<>(false);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCollector collector = new DataCollector();
                Map<String, Object> collectedData = collector.getData(view, getActivity());
                binding.dateTime.setText((CharSequence) collectedData.get("datetime"));
                binding.device.setText((CharSequence) collectedData.get("device"));
                binding.IpAddress.setText((CharSequence) collectedData.get("ip"));
                binding.latitude.setText((CharSequence) collectedData.get("latitude"));
                binding.longitude.setText((CharSequence) collectedData.get("longitude"));
                };
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore fsDb;
                fsDb = FirebaseFirestore.getInstance();

                Map<String, Object> data = new HashMap<>();
                data.put("device", binding.device.getText().toString());
                data.put("ip", binding.IpAddress.getText().toString());
                data.put("active_vpn", Boolean.toString(binding.switch1.isChecked()));
                data.put("datetime", binding.dateTime.getText().toString());
                data.put("latitude", binding.latitude.getText().toString());
                data.put("longitude", binding.longitude.getText().toString());
                data.put("description", binding.description.getText().toString());
                fsDb.collection("info").add(data);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}