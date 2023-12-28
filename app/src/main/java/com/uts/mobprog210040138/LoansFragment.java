package com.uts.mobprog210040138;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.uts.mobprog210040138.models.ModelAPIResLoans;
import com.uts.mobprog210040138.models.ModelLoans;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoansFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoansFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //start
    Context ctx;
    APIInterfaceLoans apiService = APIClient.getClient().create(APIInterfaceLoans.class);
    ModelAPIResLoans result;
    List<ModelLoans> dataLoan, dataResSearch;

    TextView txtTotalLoans;
    SearchView searchViewLoan;

    public RecyclerView recyclerView1;

    RecyclerViewCustomeAdapterLoans adapterLoans;

    private View view;

    public LoansFragment() {
        // Required empty public constructor
    }

    public static LoansFragment newInstance(String param1, String param2) {
        LoansFragment fragment = new LoansFragment();
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

        //start
        //ctx = this;

        ctx = getActivity();


//        LinearLayoutManager manager = new LinearLayoutManager(ctx);
//        recyclerView1.setLayoutManager(manager);
//        recyclerView1.setHasFixedSize(true);
//
//        if (adapterLoans != null) {
//            adapterLoans = null;
//            data.clear();
//        }
//
//        loadDataLoan();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //tess kalii
        view = inflater.inflate(R.layout.fragment_loans, container, false);
        recyclerView1 = view.findViewById(R.id.recyclerViewLoans);

        searchViewLoan = view.findViewById(R.id.searchViewLoan);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setHasFixedSize(true);

        loadDataLoan();
        searchLoan();

        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_loans, container, false);
    }



    public void loadDataLoan () {
        Call<ModelAPIResLoans> getAllLoan = apiService.getAllLoan();
        getAllLoan.enqueue(new Callback<ModelAPIResLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResLoans> call, Response<ModelAPIResLoans> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataLoan = result.getData();
                        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataLoan);
                        recyclerView1.setAdapter(adapterLoans);
                        setTotalLoan(dataLoan);

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResLoans> call, Throwable t) {

            }
        });
    }

    public void searchLoan() {
        searchViewLoan.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.trim().toLowerCase();

                // Cek apakah teks pencarian tidak kosong
                if (!query.isEmpty()) {
                    // Lakukan pencarian dan perbarui RecyclerView
                    performSearch(query);
                } else {
                    // Jika teks pencarian kosong, kembalikan ke data awal
                    resetSearch();
                }

                return true;
            }
        });

    }

    public void performSearch(String query) {
        Log.d("Query", query);
        Call<ModelAPIResLoans> getAllLoanByUsername = apiService.getAllLoanByUsername(query.trim().toLowerCase());
        getAllLoanByUsername.enqueue(new Callback<ModelAPIResLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResLoans> call, Response<ModelAPIResLoans> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataResSearch = result.getData();
                        Log.d("Search Results", dataResSearch.toString());
                        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataResSearch);
                        recyclerView1.setAdapter(adapterLoans);
                        setTotalLoan(dataResSearch);

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResLoans> call, Throwable t) {

            }
        });
    }


    private void resetSearch() {
        // Kembalikan ke data awal atau tampilkan semua data
        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataLoan);
        recyclerView1.setAdapter(adapterLoans);
        setTotalLoan(dataLoan);
    }

    public void setTotalLoan(List<ModelLoans> data) {
        String wordLoan = "Loan";
        txtTotalLoans = view.findViewById(R.id.txtTotalLoans);
        Integer totalDataReturn = data.size();
        if(totalDataReturn > 1) { wordLoan = "Loans"; }
        txtTotalLoans.setText(totalDataReturn.toString() + " " + wordLoan);
    }
    
}