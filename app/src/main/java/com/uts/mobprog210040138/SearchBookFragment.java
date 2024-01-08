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
import android.widget.TextView;
import android.widget.Toast;

import com.uts.mobprog210040138.helpers.NotificationHelpers;
import com.uts.mobprog210040138.helpers.ProgressBarHelpers;
import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelBook;
import com.uts.mobprog210040138.models.ModelMember;
import com.uts.mobprog210040138.models.SharedDataViewModel;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBookFragment extends Fragment {
    Context ctx;

    ApiInterfaceBook apiService = APIClient.getClient().create(ApiInterfaceBook.class);
    SearchView searchViewBook;

    View view;

    ModelAPIResBook result;
    List<ModelBook> dataBook, dataResSearch;

    RecyclerView recyclerViewBook;


    RecyclerViewCustomeAdapterBooks adapterBook;

    ImageButton btnBack;

    ProgressBar progressBarSearchBook;
    private SharedDataViewModel sharedDataViewModel;

    ProgressBarHelpers progressBarHelpers;

    TextView txtInfoBook;

    public SearchBookFragment() {

    }

    public static SearchBookFragment newInstance() {
        SearchBookFragment fragment = new SearchBookFragment();

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
        view = inflater.inflate(R.layout.fragment_search_book, container, false);
        searchViewBook = view.findViewById(R.id.searchViewBookChoose);

        recyclerViewBook = view.findViewById(R.id.recyclerViewBookChoose);
        btnBack = view.findViewById(R.id.btnBackChooseMember);
        progressBarSearchBook = view.findViewById(R.id.progressBarSearchBook);
        txtInfoBook = view.findViewById(R.id.txtInfoChooseBook);

        progressBarHelpers = new ProgressBarHelpers(progressBarSearchBook);

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
        progressBarHelpers.show();
        Call<ModelAPIResBook> getAllBook = apiService.getAllBook();
        getAllBook.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if (response.code() != 200){
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load book data", NotificationHelpers.Status.DANGER);
                    notification.show();
                    progressBarHelpers.hide();
                }else{
                    if (response.body().getData().size() == 0){
                        txtInfoBook.setText("Opss..Book data is empty");
                        txtInfoBook.setVisibility(View.VISIBLE);
                        progressBarHelpers.hide();
                    }else {
                        txtInfoBook.setVisibility(View.INVISIBLE);
                        result = response.body();
                        dataBook = result.getData();
                        adapterBook = new RecyclerViewCustomeAdapterBooks(ctx, dataBook);

                        adapterBook.setOnItemCLickListener(new RecyclerViewCustomeAdapterBooks.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                setSharedDataViewModel(dataBook.get(position).getBookId(), dataBook.get(position).getTitle());
                            }
                        });

                        recyclerViewBook.setAdapter(adapterBook);
                        progressBarHelpers.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load book data", NotificationHelpers.Status.DANGER);
                notification.show();
                progressBarHelpers.hide();
            }
        });
    }

    public void setSharedDataViewModel(String bookId, String title) {
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
        progressBarHelpers.show();
        Call<ModelAPIResBook> getAllBookByTitle = apiService.getBookByTitle(query.trim().toLowerCase());
        getAllBookByTitle.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if (response.code() != 200) {
                    progressBarHelpers.hide();
                } else {
                    if (response.body().getData().size() == 0) {
                        txtInfoBook.setText("Opss..Book data not found");
                        txtInfoBook.setVisibility(View.VISIBLE);
                        progressBarHelpers.hide();
                        adapterBook = new RecyclerViewCustomeAdapterBooks(ctx, Collections.emptyList());
                        recyclerViewBook.setAdapter(adapterBook);
                    } else {
                        txtInfoBook.setVisibility(View.INVISIBLE);
                        result = response.body();
                        dataResSearch = result.getData();
                        adapterBook = new RecyclerViewCustomeAdapterBooks(ctx, dataResSearch);

                        adapterBook.setOnItemCLickListener(new RecyclerViewCustomeAdapterBooks.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                setSharedDataViewModel(dataResSearch.get(position).getBookId(), dataResSearch.get(position).getTitle());
                            }
                        });

                        recyclerViewBook.setAdapter(adapterBook);
                        progressBarHelpers.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {
                progressBarHelpers.hide();
            }
        });
    }

    private void resetSearch() {
        txtInfoBook.setVisibility(View.INVISIBLE);
        adapterBook = new RecyclerViewCustomeAdapterBooks(ctx, dataBook);
        recyclerViewBook.setAdapter(adapterBook);
    }
}