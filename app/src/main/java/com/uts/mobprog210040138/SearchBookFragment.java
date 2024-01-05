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

import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelBook;
import com.uts.mobprog210040138.models.ModelMember;
import com.uts.mobprog210040138.models.SharedDataViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBookFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ///

    Context ctx;

    ApiInterfaceBook apiService = APIClient.getClient().create(ApiInterfaceBook.class);
    SearchView searchViewBook;

    View view;

    ModelAPIResBook result;
    List<ModelBook> dataBook, dataResSearch;

    RecyclerView recyclerViewBook;


    RecyclerViewCustomeAdapterBooks adapterBook;

    ImageButton btnBack;
    private SharedDataViewModel sharedDataViewModel;

    public SearchBookFragment() {
        // Required empty public constructor
    }

    public static SearchBookFragment newInstance(String param1, String param2) {
        SearchBookFragment fragment = new SearchBookFragment();
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
        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);
        ctx = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_book, container, false);
        searchViewBook = view.findViewById(R.id.searchViewBookChoose);

        recyclerViewBook = view.findViewById(R.id.recyclerViewBookChoose);
        btnBack = view.findViewById(R.id.btnBackChooseMember);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerViewBook.setLayoutManager(manager);
        recyclerViewBook.setHasFixedSize(true);

        loadDataBook();
        searchBook();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getParentFragmentManager().popBackStack(); }
        });

        return view;
    }


    public void loadDataBook() {
        Call<ModelAPIResBook> getAllBook = apiService.getAllBook();
        getAllBook.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if (response.code() !=200){

                }else{
                    if (response.body() == null){

                    }else {
                        result = response.body();
                        dataBook = result.getData();
                        adapterBook = new RecyclerViewCustomeAdapterBooks(ctx, dataBook);

                        adapterBook.setOnItemCLickListener(new RecyclerViewCustomeAdapterBooks.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendBundle(dataBook.get(position).getBookId(), dataBook.get(position).getTitle());
                            }
                        });

                        recyclerViewBook.setAdapter(adapterBook);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {

            }
        });
    }

    public void sendBundle(String bookId, String title) {
        sharedDataViewModel.setBookId(bookId);
        sharedDataViewModel.setTitle(title);

        AddLoansFragment addLoansFragment = new AddLoansFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, addLoansFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }



    public void searchBook() {
        searchViewBook.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        Call<ModelAPIResBook> getAllBookByTitle = apiService.getBookByTitle(query.trim().toLowerCase());
        getAllBookByTitle.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataResSearch = result.getData();
                        Log.d("Search Results", dataResSearch.toString());
                        adapterBook = new RecyclerViewCustomeAdapterBooks(ctx, dataResSearch);
                        recyclerViewBook.setAdapter(adapterBook);

                        adapterBook.setOnItemCLickListener(new RecyclerViewCustomeAdapterBooks.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendBundle(dataResSearch.get(position).getBookId(), dataResSearch.get(position).getTitle());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {

            }
        });
    }

    private void resetSearch() {
        // Kembalikan ke data awal atau tampilkan semua data
        adapterBook = new RecyclerViewCustomeAdapterBooks(ctx, dataBook);
        recyclerViewBook.setAdapter(adapterBook);
    }
}