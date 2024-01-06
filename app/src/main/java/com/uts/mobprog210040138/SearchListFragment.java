package com.uts.mobprog210040138;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelAPIResSingleBook;
import com.uts.mobprog210040138.models.ModelBook;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Variabel buku
    private List<ModelBook> data2;
    private RecyclerView recyclerSearch;
    private RecycleViewSearchAdapter searchAdapter;
    private ModelAPIResBook result;
    private Context ctx;
    private View view;
    private SearchView searchView;
    private ModelBook data9;
    private ModelAPIResSingleBook result7;
    private List<ModelBook> allBooks = new ArrayList<>();
    private List<ModelBook> filteredBooks = new ArrayList<>();
    private ProgressBar progressBar2;
    private TextView txtNotfound, statusTextview2;
    private AlertDialog alertDialog1;
    private ImageButton imageButtonRetry2;
    private ImageView imageNoInternet2;


    public SearchListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchListFragment newInstance(String param1, String param2) {
        SearchListFragment fragment = new SearchListFragment();
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

    }



    ImageButton imageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_list, container, false);

        //inisialisasi txtnotfound di search xml
        txtNotfound= view.findViewById(R.id.txtNotFound);
        //inisialisasi imagebutton id
        imageButton = view.findViewById(R.id.imageButton4);

        statusTextview2 = view.findViewById(R.id.statusTextView2);
        imageButtonRetry2 = view.findViewById(R.id.imageButtonRetry2);
        imageNoInternet2 = view.findViewById(R.id.imageNoInternet2);

        //inisialisasi id recyclerView dashboard
        recyclerSearch = view.findViewById(R.id.recyclerSearch);
        searchView = view.findViewById(R.id.SeacrhDashboard);
        //Mengatur data yang akan ditampilkan
        progressBar2 = view.findViewById(R.id.progressBar2);
        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerSearch.setLayoutManager(manager);
        recyclerSearch.setHasFixedSize(true);

        loadSemua();

        // Tambahkan event listener untuk ImageButton
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kode yang akan dijalankan ketika ImageButton diklik
                // Panggil metode atau tindakan untuk membuka SearchListFragment
                openDashboardFragment();
            }
        });

        imageButtonRetry2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger data reload when the retry button is clicked
                loadSemua();
            }
        });



        return view;
    }

    // Metode untuk membuka SearchListFragment
    private void openDashboardFragment() {
        // Buat instance dari SearchListFragment
        DashboaardFragment dashboaardFragment = new DashboaardFragment();

        // Ganti fragment di dalam container (contoh: menggunakan R.id.fragment_container)
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_search, dashboaardFragment)
                .addToBackStack(null)
                .commit();
    }


    private void searchBook() {

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    onDataStart1();
                    if(data2 !=null){
                        filterBook(s);
                    }
                    if(data2 != filteredBooks){
                        txtNotfound.setText("no book named " + (s));
                    }
                    onDataComplete1();
                    return true;
                }
            });
        }


    }

    //untuk load data buku di serach
    ApiInterfaceBook apiServices = APIClient.getClient().create(ApiInterfaceBook.class);

    public void searchData() {
        onDataStart1();
        Call<ModelAPIResBook> getDataBooks = apiServices.getAllBook();

        getDataBooks.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if(response.code() != 200) {
                    Toast.makeText(getContext(), "code" + response.code(), Toast.LENGTH_SHORT).show();
                }else{
                    if (response.body() == null) {

                    }else {
                        result = response.body();
                        data2 = result.getData();
                        searchAdapter = new RecycleViewSearchAdapter(ctx, data2);


                        searchAdapter.setOnItemCLickListener(new RecycleViewSearchAdapter.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                loadDialogSearch(position);
                            }
                        });

                        recyclerSearch.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                        onDataComplete1();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {

            }
        });
    }

    //untuk filter buku sesuai text yang diketik
    private void filterBook(String text) {

        // Bersihkan list filteredBooks untuk memulai proses filter dari awal
        filteredBooks.clear();

        // Jika teks pencarian kosong, tampilkan semua data pada filteredBooks
        if (TextUtils.isEmpty(text)) {
            filteredBooks.addAll(data2);
        } else {
            text = text.toLowerCase().trim();

            // Jika judul buku mengandung teks sesui pencarian, maka akan ditambahkan buku tersebut ke filteredBooks
            for (ModelBook book : data2) {
                if (book.getTitle().toLowerCase().contains(text)) {
                    filteredBooks.add(book);
                }
            }
        }

        // update list buku sesuai yang dicari
        searchAdapter.setFilteredBooks(filteredBooks);
    }

    public void onDataStart1() {
        // Called when data loading starts
        if (progressBar2 != null) {
            progressBar2.setVisibility(View.VISIBLE);
        }
    }

    public void onDataComplete1() {
        // Called when data loading is complete
        if (progressBar2 != null) {
            progressBar2.setVisibility(View.GONE);
        }
    }

    public void loadDialogSearch(int position) {

        Call<ModelAPIResSingleBook> getBookById = apiServices.getBookById(data2.get(position).getBookId());
        getBookById.enqueue(new Callback<ModelAPIResSingleBook>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleBook> call, Response<ModelAPIResSingleBook> response) {
                if (view == null || getContext() == null) {
                    return;
                }
                if(response.code() !=200){
                    Toast.makeText(getContext(), "Code " + response.code(), Toast.LENGTH_LONG).show();
                }else {
                    if (response.body() == null){

                    }else {
                        result7 = response.body();
                        data9 = result7.getData();

                        ViewGroup viewGroup = view.findViewById(android.R.id.content);
                        View dialogView =
                                LayoutInflater.from(ctx).
                                        inflate(R.layout.detail_data_buku, viewGroup, false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setCancelable(false);

                        //inisialisasi komponen
                        ImageView imageViewBoook = dialogView.findViewById(R.id.imageViewBook);
                        TextView textViewTitle = dialogView.findViewById(R.id.textViewTitle);
                        TextView textViewAuthor = dialogView.findViewById(R.id.textViewAuthor);
                        TextView textViewPublisher = dialogView.findViewById(R.id.textViewPublisher);
                        TextView textViewPublication = dialogView.findViewById(R.id.textViewPublication);
                        TextView textViewISBN = dialogView.findViewById(R.id.textViewISBN);
                        TextView textViewStock = dialogView.findViewById(R.id.textViewStock);
                        TextView textViewLocation = dialogView.findViewById(R.id.textViewLocation);
                        ImageButton imageButtonClose = dialogView.findViewById(R.id.imageButtonClose);

                        builder.setView(dialogView);
                        imageButtonClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog1.dismiss();
                            }
                        });

                        ImageView imageView = imageViewBoook;
                        Glide.with(ctx)
                                .load(data9.getImageUrl())
                                .placeholder(R.drawable.none)
                                .into(imageViewBoook);
                        textViewTitle.setText(data9.getTitle());
                        textViewAuthor.setText(data9.getAuthor());
                        textViewPublisher.setText(data9.getPublisher());
                        textViewPublication.setText(data9.getPublicationYear());
                        textViewISBN.setText(data9.getIsbn());
                        textViewStock.setText(data9.getStock().toString());
                        textViewLocation.setText(data9.getBookRackLocation());

                        alertDialog1 = builder.create();
                        //untuk menamabahkan animasi
                        alertDialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog1.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {

            }
        });
    }

    public void checkInternetConnectionSearch () {

        if (!isAdded() || requireActivity() == null) {

            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Mendapatkan info koneksi saat ini
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Memeriksa apakah ada koneksi internet
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            // Jika ada koneksi, sembunyikan pesan kesalahan dan tampilkan konten
            statusTextview2.setVisibility(View.GONE);
            imageNoInternet2.setVisibility(View.GONE);
            imageButtonRetry2.setVisibility(View.GONE);
            txtNotfound.setVisibility(View.GONE);

        } else {

            onDataStart1();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isAdded() && requireActivity() != null) {

                        statusTextview2.setVisibility(View.VISIBLE);
                        statusTextview2.setText("No internet connection");
                        imageNoInternet2.setVisibility(View.VISIBLE);
                        imageButtonRetry2.setVisibility(View.VISIBLE);
                        onDataComplete1();
                    }
                }
            }, 5000);

        }

    }
    private void loadSemua () {
        searchBook();
        searchData();
        checkInternetConnectionSearch();
        statusTextview2.setVisibility(View.GONE);
        imageNoInternet2.setVisibility(View.GONE);
        imageButtonRetry2.setVisibility(View.GONE);
        txtNotfound.setVisibility(View.GONE);
    }
}