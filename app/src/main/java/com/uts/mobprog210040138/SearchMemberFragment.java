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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.uts.mobprog210040138.helpers.NotificationHelpers;
import com.uts.mobprog210040138.helpers.ProgressBarHelpers;
import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelMember;
import com.uts.mobprog210040138.models.SharedDataViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMemberFragment extends Fragment {
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

    ProgressBar progressBarSearchMember;

    ProgressBarHelpers progressBarHelpers;

    public SearchMemberFragment() {

    }

    public static SearchMemberFragment newInstance() {
        SearchMemberFragment fragment = new SearchMemberFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = getActivity();
        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_search_member, container, false);
        searchViewMember = view.findViewById(R.id.searchViewBookChoose);

        recyclerViewMember = view.findViewById(R.id.recyclerViewBookChoose);
        btnBack = view.findViewById(R.id.btnBackChooseMember);
        progressBarSearchMember = view.findViewById(R.id.progressBarSearchMember);

        progressBarHelpers = new ProgressBarHelpers(progressBarSearchMember);

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
        progressBarHelpers.show();
        Call<ModelAPIResMember> getAllMember = apiService.getAllMember();
        getAllMember.enqueue(new Callback<ModelAPIResMember>() {
            @Override
            public void onResponse(Call<ModelAPIResMember> call, Response<ModelAPIResMember> response) {
                if (response.code() !=200){
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load member data", NotificationHelpers.Status.DANGER);
                    notification.show();
                    progressBarHelpers.hide();
                }else{
                    if (response.body() == null){
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load member data", NotificationHelpers.Status.DANGER);
                        notification.show();
                        progressBarHelpers.hide();
                    }else {
                        result = response.body();
                        dataMember = result.getData();
                        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataMember);

                        adapterMember.setOnItemCLickListener(new RecyclerViewCustomAdapterMembers.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendSharedDataViewModel(dataMember.get(position).getMemberId(), dataMember.get(position).getUsername());
                            }
                        });
                        recyclerViewMember.setAdapter(adapterMember);
                        progressBarHelpers.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResMember> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load member data", NotificationHelpers.Status.DANGER);
                notification.show();
                progressBarHelpers.hide();
            }
        });
    }

    public void sendSharedDataViewModel(String memberId, String username) {
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
        progressBarHelpers.show();
        Call<ModelAPIResMember> getAllLoanByUsername = apiService.getAllMemberByUsername(query.trim().toLowerCase());
        getAllLoanByUsername.enqueue(new Callback<ModelAPIResMember>() {
            @Override
            public void onResponse(Call<ModelAPIResMember> call, Response<ModelAPIResMember> response) {
                if (response.code() != 200) {
                    progressBarHelpers.hide();
                } else {
                    if (response.body() == null) {
                        progressBarHelpers.hide();
                    } else {
                        result = response.body();
                        dataResSearch = result.getData();
                        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataResSearch);

                        adapterMember.setOnItemCLickListener(new RecyclerViewCustomAdapterMembers.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendSharedDataViewModel(dataResSearch.get(position).getMemberId(), dataResSearch.get(position).getUsername());
                            }
                        });

                        recyclerViewMember.setAdapter(adapterMember);
                        progressBarHelpers.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResMember> call, Throwable t) {
                progressBarHelpers.hide();
            }
        });
    }

    private void resetSearch() {
        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataMember);
        recyclerViewMember.setAdapter(adapterMember);
    }

}