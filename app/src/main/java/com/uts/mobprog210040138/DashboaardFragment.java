package com.uts.mobprog210040138;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uts.mobprog210040138.models.ModelAPIResBook;
import com.uts.mobprog210040138.models.ModelAPIResLoans;
import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelAPIResSingleBook;
import com.uts.mobprog210040138.models.ModelBook;
import com.uts.mobprog210040138.RecyclerViewCustomeAdapterBooks;
import com.uts.mobprog210040138.models.ModelLoans;
import com.uts.mobprog210040138.models.ModelMember;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboaardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DashboaardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Variabel buku
    private List<ModelBook> data1;
    private List<ModelLoans> data5;
    private List<ModelMember> data6;
    private ModelBook data8;
    private RecyclerView recyclerBook;
    private RecyclerViewCustomeAdapterBooks customAdapter;

    private ModelAPIResSingleBook result4;
    private ModelAPIResBook result;
    private ModelAPIResLoans result2;
    private ModelAPIResMember result3;
    private Context ctx;
    private View view;

    private TextView txtTotalBook, txtTotalLoan, txtTotalMember;
    private AlertDialog alertDialog;
    private ProgressBar progressBar1;





    public DashboaardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboaardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboaardFragment newInstance(String param1, String param2) {
        DashboaardFragment fragment = new DashboaardFragment();
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
        view = inflater.inflate(R.layout.fragment_dashboaard, container, false);

        //ini buat pindah ke search

        // Temukan ImageButton dari layout
        imageButton = view.findViewById(R.id.imageButton3);
        progressBar1 = view.findViewById(R.id.progressBar1);

        customAdapter = new RecyclerViewCustomeAdapterBooks(ctx, data1);

        //Inisialisasi id recyclerView dashboard
        recyclerBook = view.findViewById(R.id.recyclerBook);
        txtTotalBook = view.findViewById(R.id.txtTotalBook);
        txtTotalLoan = view.findViewById(R.id.txtTotalLoan);
        txtTotalMember = view.findViewById(R.id.txtTotalMember);

        //Mengatur data yang akan ditampilkan
        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerBook.setLayoutManager(manager);
        recyclerBook.setHasFixedSize(true);

        //


        LoadData();
        loadDataMembers();
        loadDataLoan();

        // Tambahkan event listener untuk ImageButton
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil metode atau tindakan untuk membuka SearchListFragment
                openSearchListFragment();
            }

        });



        return view;
    }

    // Metode untuk membuka SearchListFragment
    private void openSearchListFragment() {
        SearchListFragment searchListFragment = new SearchListFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.dashboard_fragment, searchListFragment)
                .addToBackStack(null)
                .commit();
    }

    ApiInterfaceBook apiServices = APIClient.getClient().create(ApiInterfaceBook.class);
    public void LoadData(){
        onDataStart();
        Call<ModelAPIResBook> getDataBooks = apiServices.getAllBook();

        getDataBooks.enqueue(new Callback<ModelAPIResBook>() {
            @Override
            public void onResponse(Call<ModelAPIResBook> call, Response<ModelAPIResBook> response) {
                if(response.code() !=200){
                    Toast.makeText(getContext(), "Code " + response.code(), Toast.LENGTH_LONG).show();
                }else {
                    if (response.body() == null){

                    }else{
                        result = response.body();
                        data1 = result.getData();
                        customAdapter = new RecyclerViewCustomeAdapterBooks(ctx, data1);


                        customAdapter.setOnItemCLickListener(new RecyclerViewCustomeAdapterBooks.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                loadDialogView(position);
                            }
                        });

                        recyclerBook.setAdapter(customAdapter);
                        customAdapter.notifyDataSetChanged();
                        setTotalBook();
                        onDataComplete();
                    }
                }
            }


            @Override
            public void onFailure(Call<ModelAPIResBook> call, Throwable t) {

            }
        });

    }

    APIInterfaceLoans apiServiceLoan = APIClient.getClient().create(APIInterfaceLoans.class);

    public void loadDataLoan() {
        Call<ModelAPIResLoans> getAllLoan = apiServiceLoan.getAllLoan();

        getAllLoan.enqueue(new Callback<ModelAPIResLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResLoans> call, Response<ModelAPIResLoans> response) {
                if(response.code() !=200){
                    Toast.makeText(getContext(), "Code " + response.code(), Toast.LENGTH_LONG).show();
                }else {
                    if (response.body() == null){

                    }else{
                        result2 = response.body();
                        data5 = result2.getData();
                        setTotalLoan();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResLoans> call, Throwable t) {

            }
        });
    }

    APIInterfaceMembers apiServiceMember = APIClient.getClient().create(APIInterfaceMembers.class);

    public void loadDataMembers() {
        Call<ModelAPIResMember> getAllMember = apiServiceMember.getAllMember();

        getAllMember.enqueue(new Callback<ModelAPIResMember>() {
            @Override
            public void onResponse(Call<ModelAPIResMember> call, Response<ModelAPIResMember> response) {
                if(response.code() !=200){
                    Toast.makeText(getContext(), "Code " + response.code(), Toast.LENGTH_LONG).show();
                }else {
                    if(response.body() == null){

                    }else {
                        result3 = response.body();
                        data6 = result3.getData();
                        SetTotalMembers();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResMember> call, Throwable t) {

            }
        });
    }

    //menampilkan jumlah data buku melalui jumlah data yang di return
    public void setTotalBook() {
        Integer totalDataReturn = data1.size();
        txtTotalBook.setText(totalDataReturn.toString());

    }
    //menampilkan jumlah data loan melalui jumlah data yang di return
    public void setTotalLoan() {
        Integer totalDataReturn = data5.size();
        txtTotalLoan.setText(totalDataReturn.toString());
    }
    //menampilkan jumlah data member melalui jumlah data yang di return
    public void SetTotalMembers() {
        Integer totalDataReturn = data6.size();
        txtTotalMember.setText(totalDataReturn.toString());
    }



    public void loadDialogView(int position) {
        Call<ModelAPIResSingleBook> getBookById = apiServices.getBookById(data1.get(position).getBookId());
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
                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyDialogAnimation;
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


    public void onDataStart() {
        // Called when data loading starts
        if (progressBar1 != null) {
            progressBar1.setVisibility(View.VISIBLE);
        }
    }

    public void onDataComplete() {
        // Called when data loading is complete
        if (progressBar1 != null) {
            progressBar1.setVisibility(View.GONE);
        }
    }



}