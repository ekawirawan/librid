package com.uts.mobprog210040138;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelBook;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //start
    Context ctx;
    ApiInterfaceBook apiService = APIClient.getClient().create(ApiInterfaceBook.class);
    ModelAPIResBook result;
    List<ModelBook> data;

    TextView txtTotalBooks;

    public RecyclerView recyclerView1;

    RecyclerViewCustomeAdapterBooks adapterBooks;

    private View view;

    public BooksFragment() {
        // Required empty public constructor
    }

    public static BooksFragment newInstance(String param1, String param2) {
        BooksFragment fragment = new BooksFragment();
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

        ctx = getContext();


//        LinearLayoutManager manager = new LinearLayoutManager(ctx);
//        recyclerView1.setLayoutManager(manager);
//        recyclerView1.setHasFixedSize(true);
//
//        if (adapterLoans != null) {
//            adapterLoans = null;
//            data.clear();
//        }
        //loadDataBook();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_books, container, false);
        recyclerView1 = view.findViewById(R.id.recycleBook);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setHasFixedSize(true);

        loadDataBook();

        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_loans, container, false);
    }



    public void loadDataBook () {
        Call<ModelAPIResBook> getAllBook = apiService.getAllBook();
        getAllBook.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        data = result.getData();
                        adapterBooks = new RecyclerViewCustomeAdapterBooks(ctx, data);
                        recyclerView1.setAdapter(adapterBooks);
                        setTotalBook();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {

            }
        });
    }

    public void setTotalBook() {
        String wordBook = "Book";
        txtTotalBooks = view.findViewById(R.id.txtTotalBooks);
        Integer totalDataReturn = data.size();
        if(totalDataReturn > 1) { wordBook = "Book"; }
        txtTotalBooks.setText(totalDataReturn.toString() + " " + wordBook);
    }
}
