package com.uts.mobprog210040138;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class AddLoansFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //
    View view;

    ConstraintLayout CLChooseBook, CLChooseMember;
    ImageButton btnBack;
    Button btnAdd;


    public AddLoansFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddLoansFragment newInstance(String param1, String param2) {
        AddLoansFragment fragment = new AddLoansFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_loans, container, false);
        CLChooseBook = view.findViewById(R.id.CLChooseBook);
        CLChooseMember = view.findViewById(R.id.CLChooseMember);
        btnAdd = view.findViewById(R.id.btnAddLoans);
        btnBack = view.findViewById(R.id.btnBackAdd);

        CLChooseBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchListFragment fragment = new SearchListFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getParentFragmentManager().popBackStack(); }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLoans();
            }
        });


        return view;
    }

    public void addLoans() {

    }

}