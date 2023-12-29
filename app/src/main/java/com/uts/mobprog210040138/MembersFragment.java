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
import android.widget.Toast;

import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelLoans;
import com.uts.mobprog210040138.models.ModelMember;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MembersFragment extends Fragment {

    Context ctx;
    APIInterfaceMembers apiService = APIClient.getClient().create(APIInterfaceMembers.class);
    RecyclerView recyclerView1;
    ModelAPIResMember result;
    List<ModelMember> data1, dataResSearch1;

    TextView txtJumlahMember;
    SearchView searchViewMember;
    RecyclerViewCustomAdapterMembers adapterMember;
    private View view;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MembersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MembersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MembersFragment newInstance(String param1, String param2) {
        MembersFragment fragment = new MembersFragment();
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
        ctx = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_members, container, false);
        recyclerView1 = view.findViewById(R.id.recyclerViewMember);

        searchViewMember = view.findViewById(R.id.searchViewMember);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setHasFixedSize(true);

        loadDataMember();
        searchMember();

        return view;
        //return inflater.inflate(R.layout.fragment_members, container, false);
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
                        data1 = result.getData();
                        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, data1);
                        recyclerView1.setAdapter(adapterMember);
                        setTotalMember(data1);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResMember> call, Throwable t) {

            }
        });
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
        Call<ModelAPIResMember> getAllMemberByUsername = apiService.getAllMemberByUsername(query.trim().toLowerCase());
        getAllMemberByUsername.enqueue(new Callback<ModelAPIResMember>() {
            @Override
            public void onResponse(Call<ModelAPIResMember> call, Response<ModelAPIResMember> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataResSearch1 = result.getData();
                        Log.d("Search Results", dataResSearch1.toString());
                        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataResSearch1);
                        recyclerView1.setAdapter(adapterMember);
                        setTotalMember(dataResSearch1);
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
        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, data1);
        recyclerView1.setAdapter(adapterMember);
        setTotalMember(data1);
    }

    public void setTotalMember(List<ModelMember>data1){
        String wordMember = "Member";
        txtJumlahMember = view.findViewById(R.id.txtJumlahMember);
        Integer totalDataReturn = data1.size();
        if(totalDataReturn > 1) {wordMember = "Member";}
        txtJumlahMember.setText(totalDataReturn.toString() + " " + wordMember);
    }

}