package com.uts.mobprog210040138;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uts.mobprog210040138.helpers.ConfirmMessage;
import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelAPIResSingleBook;
import com.uts.mobprog210040138.models.ModelAPIResSingleMember;
import com.uts.mobprog210040138.models.ModelBook;
import com.uts.mobprog210040138.models.ModelLoans;
import com.uts.mobprog210040138.models.ModelMember;

import java.util.ArrayList;
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
    private Context ctx;
    private ApiInterfaceBook apiService = APIClient.getClient().create(ApiInterfaceBook.class);
    private ModelAPIResBook result;
    private AlertDialog alertDialog;
    private ModelAPIResSingleBook result4;
    private ModelBook data8;
    private List<ModelBook> dataBook, dataSearch = new ArrayList<>();
    private List<ModelBook> filteredBooks = new ArrayList<>();

    private TextView txtTotalBooks;

    public RecyclerView recyclerView1;

    private SearchView searchViewBook;

    private RecyclerViewCustomeAdapterBooks adapterBooks;
    ProgressBar progressBarBook;

    private View view;
    Button btnAddBook;

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

        ctx = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_books, container, false);
        recyclerView1 = view.findViewById(R.id.recycleBook);
        progressBarBook = view.findViewById(R.id.progressBarBook);

        searchViewBook = view.findViewById(R.id.searchViewBook);
        btnAddBook = view.findViewById(R.id.buttonAdd);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setHasFixedSize(true);

        loadDataBook();
        SearchBook();

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBooksFragment fragment = new AddBooksFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }


    public void loadDataBook () {
        onDataStart();

        Call<ModelAPIResBook> getAllBook = apiService.getAllBook();
        getAllBook.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if (response.code() != 200) {
                    Toast.makeText(getContext(), "code" + response.code(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataBook = result.getData();
                        adapterBooks = new RecyclerViewCustomeAdapterBooks(ctx, dataBook, true);
                        adapterBooks.setOnItemCLickListener(new RecyclerViewCustomeAdapterBooks.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                loadDialogView(position);
                            }
                        });

                        adapterBooks.setOnMoreButtonClickListener(new RecyclerViewCustomeAdapterBooks.OnMoreButtonClickListener() {
                            @Override
                            public void onMoreButtonClick(int position) {
                                showBottomSheetBook(dataBook.get(position).getBookId());
                            }
                        });

                        recyclerView1.setAdapter(adapterBooks);
                        adapterBooks.notifyDataSetChanged();
                        setTotalBook(dataBook);
                        onDataComplete();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {

            }
        });
    }

    public void SearchBook(){
        searchViewBook.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (dataBook != null) {
                    // Lakukan pencarian dan perbarui RecyclerView
                    filterBook(newText);
                } else {
                    // Jika teks pencarian kosong, kembalikan ke data awal
                    resetSearch();
                }
                return true;
            }
        });

    }

    private void filterBook(String text) {

        // Bersihkan list filteredBooks untuk memulai proses filter dari awal
        filteredBooks.clear();

        // Jika teks pencarian kosong, tampilkan semua data pada filteredBooks
        if (TextUtils.isEmpty(text)) {
            filteredBooks.addAll(dataBook);
        } else {
            text = text.toLowerCase().trim();
            // Jika judul buku mengandung teks sesui pencarian, maka akan ditambahkan buku tersebut ke filteredBooks
            for (ModelBook book : dataBook) {
                if (book.getTitle().toLowerCase().contains(text)) {
                    filteredBooks.add(book);
                }
            }
        }
        // update list buku sesuai yang dicari
        adapterBooks.setFilteredBooksAdpters(filteredBooks);
    }

//    public void performSearch(String query) {
//        Log.d("Query", query);
//        Call<ModelAPIResBook> getAllBookByTitle = apiService.getAllBookByTitle(query.trim().toLowerCase());
//        getAllBookByTitle.enqueue(new Callback<ModelAPIResBook>() {
//            @Override
//            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
//                if (response.code() != 200) {
//                    // Handle error
//                } else {
//                    if (response.body() == null) {
//                        // Handle null response
//                    } else {
//                        if (result != null && result.getData() != null) {
//                            dataSearch = result.getData();
//                            result = response.body();
//                            Log.d("Search Results", dataSearch.toString());
//                            adapterBooks = new RecycleViewSearchAdapter(ctx, dataSearch);
//                            recyclerView1.setAdapter(adapterBooks);
//
//                            // Gunakan data pencarian saat memanggil setTotalBook
//                            setTotalBook(dataSearch);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }

    private void resetSearch() {
        // Kembalikan ke data awal atau tampilkan semua data
        adapterBooks = new RecyclerViewCustomeAdapterBooks(ctx, dataBook, true);
        recyclerView1.setAdapter(adapterBooks);
        setTotalBook(dataBook);
    }

    public void setTotalBook(List<ModelBook> data) {
        String wordBook = "Book";
        txtTotalBooks = view.findViewById(R.id.txtTotalBook2);
        Integer totalDataReturn = data.size();
        if(totalDataReturn > 1) { wordBook = "Book"; }
        txtTotalBooks.setText(totalDataReturn.toString() + " " + wordBook);
    }

    public void loadDialogView(int position) {
        Call<ModelAPIResSingleBook> getBookById = apiService.getBookById(dataBook.get(position).getBookId());
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

                    }else{
                        result4 = response.body();
                        data8 = result4.getData();

                        ViewGroup viewGroup = view.findViewById(android.R.id.content);
                        View dialogView =
                                LayoutInflater.from(ctx).
                                        inflate(R.layout.detail_data_buku, viewGroup,false);
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
                                alertDialog.dismiss();
                            }
                        });

                        ImageView imageView = imageViewBoook;
                        Glide.with(ctx)
                                .load(data8.getImageUrl())
                                .placeholder(R.drawable.none)
                                .into(imageViewBoook);
                        textViewTitle.setText(data8.getTitle());
                        textViewAuthor.setText(data8.getAuthor());
                        textViewPublisher.setText(data8.getPublisher());
                        textViewPublication.setText(data8.getPublicationYear());
                        textViewISBN.setText(data8.getIsbn());
                        textViewStock.setText(data8.getStock().toString());
                        textViewLocation.setText(data8.getBookRackLocation());

                        alertDialog = builder.create();
                        //untuk menamabahkan animasi
                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {

            }
        });
    }

    public void deleteBook(String bookId){
        Call<ModelAPIResSingleBook> deleteBook = apiService.deleteBook(bookId);
        deleteBook.enqueue(new Callback<ModelAPIResSingleBook>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleBook> call, Response<ModelAPIResSingleBook> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        loadDataBook();
                        adapterBooks.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {

            }
        });
    }

    public void showBottomSheetBook(String bookId) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_action);

        LinearLayout returnLayout = dialog.findViewById(R.id.layoutReturn);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        returnLayout.setVisibility(View.GONE);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("bookId",bookId);


                AddBooksFragment fragment = new AddBooksFragment();
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                //  Toast.makeText(ctx,"Create a short is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                ConfirmMessage confirmMessage = new ConfirmMessage(ctx);
                confirmMessage.setMessage("Are you sure to delete this Book?");
                confirmMessage.setTextButtonYes("Yes");
                confirmMessage.setTextButtonCancle("Cancel");
                confirmMessage.show();

                confirmMessage.setConfirmationCallback(new ConfirmMessage.ConfirmationCallback() {
                    @Override
                    public void onConfirmation(boolean isConfirmed) {
                        if (isConfirmed) {
                            Log.d("onConfirm", "Dikonfirmasi Deleted" + bookId.toString());
                            deleteBook(bookId);
                        } else {
                            Log.d("onConfirm", "Dicancle");
                        }
                    }
                });

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void onDataStart() {
        // Called when data loading starts
        if (progressBarBook != null) {
            progressBarBook.setVisibility(View.VISIBLE);
        }
    }

    public void onDataComplete() {
        // Called when data loading is complete
        if (progressBarBook != null) {
            progressBarBook.setVisibility(View.GONE);
        }
    }
}
