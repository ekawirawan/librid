package com.uts.mobprog210040138;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelMember;
import com.uts.mobprog210040138.models.SharedDataViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMemberFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //

    Context ctx;

    APIInterfaceMembers apiService = APIClient.getClient().create(APIInterfaceMembers.class);
    SearchView searchViewMember;

    View view;

    ModelAPIResMember result;
    List<ModelMember> dataMember, dataResSearch;

    RecyclerView recyclerViewMember;


    RecyclerViewCustomAdapterMembers adapterMember;

    ImageButton btnBack;
    private SharedDataViewModel sharedDataViewModel;

    public SearchMemberFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchMemberFragment newInstance(String param1, String param2) {
        SearchMemberFragment fragment = new SearchMemberFragment();
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

        ctx = getActivity();
        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_search_member, container, false);
        searchViewMember = view.findViewById(R.id.searchViewBookChoose);

        recyclerViewMember = view.findViewById(R.id.recyclerViewBookChoose);
        btnBack = view.findViewById(R.id.btnBackChooseMember);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerViewMember.setLayoutManager(manager);
        recyclerViewMember.setHasFixedSize(true);

        loadDataMember();
        searchMember();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getParentFragmentManager().popBackStack(); }
        });

        return view;
    }


    public void loadDataMember() {
        Call<ModelAPIResMember> getAllMember = apiService.getAllMember();
        getAllMember.enqueue(new Callback<ModelAPIResMember>() {
            @Override
            public void onResponse(Call<ModelAPIResMember> call, Response<ModelAPIResMember> response) {
                if (response.code() !=200){
                    Toast.makeText(getContext(), "code" + response.code(),
                            Toast.LENGTH_LONG).show();
                }else{
                    if (response.body() == null){

                    }else {
                        result = response.body();
                        dataMember = result.getData();
                        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataMember);

                        adapterMember.setOnItemCLickListener(new RecyclerViewCustomAdapterMembers.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendBundle(dataMember.get(position).getMemberId(), dataMember.get(position).getUsername());
                            }
                        });
                        recyclerViewMember.setAdapter(adapterMember);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResMember> call, Throwable t) {

            }
        });
    }

    public void sendBundle(String memberId, String username) {
        sharedDataViewModel.setMemberId(memberId);
        sharedDataViewModel.setUsername(username);

        AddLoansFragment addLoansFragment = new AddLoansFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, addLoansFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();

    }



    public void searchMember() {
        searchViewMember.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.trim().toLowerCase();

                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    resetSearch();
                }

                return true;
            }
        });

    }

    public void performSearch(String query) {
        Log.d("Query", query);
        Call<ModelAPIResMember> getAllLoanByUsername = apiService.getAllMemberByUsername(query.trim().toLowerCase());
        getAllLoanByUsername.enqueue(new Callback<ModelAPIResMember>() {
            @Override
            public void onResponse(Call<ModelAPIResMember> call, Response<ModelAPIResMember> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataResSearch = result.getData();
                        Log.d("Search Results", dataResSearch.toString());
                        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataResSearch);
                        recyclerViewMember.setAdapter(adapterMember);

                        adapterMember.setOnItemCLickListener(new RecyclerViewCustomAdapterMembers.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendBundle(dataResSearch.get(position).getMemberId(), dataResSearch.get(position).getUsername());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResMember> call, Throwable t) {

            }
        });
    }

    private void resetSearch() {
        // Kembalikan ke data awal atau tampilkan semua data
        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataMember);
        recyclerViewMember.setAdapter(adapterMember);
    }

}