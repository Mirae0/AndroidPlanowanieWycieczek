package com.example.androidplanowaniewycieczek.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.androidplanowaniewycieczek.R;
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

        ConstraintLayout planTripButton = view.findViewById(R.id.planTripBtn);
        planTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        });
        ConstraintLayout plannedTripsButton = view.findViewById(R.id.plannedTripsBtn);
        plannedTripsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlannedTripsActivity.class);
            startActivity(intent);
        });
        ConstraintLayout rankingButton = view.findViewById(R.id.rankingBtn);
        rankingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RankingActivity.class);
            startActivity(intent);
        });
        ConstraintLayout synchronizeTripButton = view.findViewById(R.id.synchronizeTripsBtn);
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