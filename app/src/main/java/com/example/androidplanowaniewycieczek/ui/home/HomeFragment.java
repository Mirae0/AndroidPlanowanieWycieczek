package com.example.androidplanowaniewycieczek.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.databinding.FragmentHomeBinding;
import com.example.androidplanowaniewycieczek.ui.firstpage.MapActivity;
import com.example.androidplanowaniewycieczek.ui.firstpage.MapsActivity;
import com.example.androidplanowaniewycieczek.ui.firstpage.PlannedTripsActivity;
import com.example.androidplanowaniewycieczek.ui.firstpage.RankingActivity;
import com.example.androidplanowaniewycieczek.ui.firstpage.SynchronizedActivity;

import org.jetbrains.annotations.Nullable;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView planTripButton = view.findViewById(R.id.textView2);
        planTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        });
        TextView plannedTripsButton = view.findViewById(R.id.textView6);
        plannedTripsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlannedTripsActivity.class);
            startActivity(intent);
        });
        TextView rankingButton = view.findViewById(R.id.textView7);
        rankingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RankingActivity.class);
            startActivity(intent);
        });
        TextView synchronizeTripButton = view.findViewById(R.id.textView9);
        synchronizeTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SynchronizedActivity.class);
            startActivity(intent);
        });




        return view;
    }
}

//
//    private FragmentHomeBinding binding;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
//
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}