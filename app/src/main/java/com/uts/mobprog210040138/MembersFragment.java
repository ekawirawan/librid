package com.uts.mobprog210040138;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.Transliterator;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.uts.mobprog210040138.helpers.ConfirmMessage;
import com.uts.mobprog210040138.helpers.DateFormatterHelpers;
import com.uts.mobprog210040138.helpers.TextViewStyle;
import com.uts.mobprog210040138.models.ModelAPIResLoans;
import com.uts.mobprog210040138.models.ModelAPIResMember;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelAPIResSingleMember;
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
    ModelAPIResSingleMember resultMemberSingle;
    List<ModelMember> dataMember, dataResSearchMember;
    ModelMember dataMemberSingle;
    TextView txtJumlahMember;
    SearchView searchViewMember;
    RecyclerViewCustomAdapterMembers adapterMember;
    Button btnAddMember;
    private View view;
    private ProgressBar progressBarMember;


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
        progressBarMember = view.findViewById(R.id.progressBarMember);
        btnAddMember = view.findViewById(R.id.btnAddMember);

        searchViewMember = view.findViewById(R.id.searchViewMember);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setHasFixedSize(true);

        loadDataMember();
        searchMember();

        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMemberFragment fragment = new AddMemberFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
        //return inflater.inflate(R.layout.fragment_members, container, false);
    }


    public void loadDataMember() {
        onDataStart();
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
                                showDetailMember(position);
                            }
                        });
                        adapterMember.setOnMoreButtonClickListener(new RecyclerViewCustomAdapterMembers.OnMoreButtonClickListener() {
                            @Override
                            public void onMoreButtonClick(int position) {
                                showBottomSheetMember(dataMember.get(position).getMemberId());
                            }
                        });
                        recyclerView1.setAdapter(adapterMember);
                        setTotalMember(dataMember);
                        onDataComplete();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResMember> call, Throwable t) {

            }
        });
    }

    public void searchMember() {
        try {
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
        } catch (Exception e){
            Log.d("search",e.toString());
        }
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
                        dataResSearchMember = result.getData();
                        Log.d("Search Results", dataResSearchMember.toString());
                        adapterMember = new RecyclerViewCustomAdapterMembers(ctx, dataResSearchMember);
                        recyclerView1.setAdapter(adapterMember);
                        setTotalMember(dataResSearchMember);

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
        recyclerView1.setAdapter(adapterMember);
        setTotalMember(dataMember);
    }

    public void setTotalMember(List<ModelMember> data1){
        String wordMember = "Member";
        txtJumlahMember = view.findViewById(R.id.txtJumlahMember);
        Integer totalDataReturn = data1.size();
        if(totalDataReturn > 1) {wordMember = "Member";}
        txtJumlahMember.setText(totalDataReturn.toString() + " " + wordMember);
    }

    public void deleteMember(String memberId){
        Call<ModelAPIResSingleMember> deleteMember = apiService.deleteMember(memberId);
        deleteMember.enqueue(new Callback<ModelAPIResSingleMember>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleMember> call, Response<ModelAPIResSingleMember> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        loadDataMember();
                        adapterMember.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleMember> call, Throwable t) {

            }
        });
    }

    public void showDetailMember (int position) {
        Call<ModelAPIResSingleMember> getMemberById = apiService.getMemberById(dataMember.get(position).getMemberId());
        getMemberById.enqueue(new Callback<ModelAPIResSingleMember>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleMember> call, Response<ModelAPIResSingleMember> response) {
                if (response.code() != 200) {

                } else {
                    if(response.body() == null) {

                    } else {
                        resultMemberSingle = response.body();
                        dataMemberSingle = resultMemberSingle.getData();

                        ViewGroup viewGroup = view.findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.detail_data_list_member, viewGroup, false);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Details");
                        builder.setIcon(android.R.drawable.ic_dialog_info);
                        builder.setCancelable(false);

                        TextView txtUsernameMemberDetail = dialogView.findViewById(R.id.txtUsernameMemberDetail);
                        TextView txtFullNameMemberDetail = dialogView.findViewById(R.id.txtFullNameMemberDetail);
                        TextView txtAddressMemberDetail = dialogView.findViewById(R.id.txtAddressMemberDetail);
                        TextView txtEmailMemberDetail = dialogView.findViewById(R.id.txtEmailMemberDetail);
                        TextView txtPhoneNumberMemberDetail = dialogView.findViewById(R.id.txtPhoneNumberMemberDetail);
                        TextView txtMembershipStartMemberDetail = dialogView.findViewById(R.id.txtMembershipStartMemberDetail);
                        TextView txtMembershipExpiryMemberDetail = dialogView.findViewById(R.id.txtMembershipExpiryMemberDetail);

                        builder.setView(dialogView);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        txtUsernameMemberDetail.setText(dataMemberSingle.getUsername());
                        txtFullNameMemberDetail.setText(dataMemberSingle.getFullName());
                        txtAddressMemberDetail.setText(dataMemberSingle.getAddress());
                        txtEmailMemberDetail.setText(dataMemberSingle.getEmail());
                        txtPhoneNumberMemberDetail.setText(dataMemberSingle.getPhoneNumber());
                        txtMembershipStartMemberDetail.setText(dataMemberSingle.getMembershipStartDate());
                        txtMembershipExpiryMemberDetail.setText(dataMemberSingle.getMembershipExpiryDate());

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleMember> call, Throwable t) {

            }
        });
    }

    public void showBottomSheetMember(String memberId) {
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
                bundle.putString("memberId",memberId);


                AddMemberFragment fragment = new AddMemberFragment();
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
                confirmMessage.setMessage("Are you sure to delete this member?");
                confirmMessage.setTextButtonYes("Yes");
                confirmMessage.setTextButtonCancle("Cancel");
                confirmMessage.show();

                confirmMessage.setConfirmationCallback(new ConfirmMessage.ConfirmationCallback() {
                    @Override
                    public void onConfirmation(boolean isConfirmed) {
                        if (isConfirmed) {
                            Log.d("onConfirm", "Dikonfirmasi Deleted" + memberId.toString());
                            deleteMember(memberId);
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
        if (progressBarMember != null) {
            progressBarMember.setVisibility(View.VISIBLE);
        }
    }

    public void onDataComplete() {
        // Called when data loading is complete
        if (progressBarMember != null) {
            progressBarMember.setVisibility(View.GONE);
        }
    }

}