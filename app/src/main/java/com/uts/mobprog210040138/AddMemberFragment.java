package com.uts.mobprog210040138;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.uts.mobprog210040138.helpers.ConfirmMessage;
import com.uts.mobprog210040138.helpers.NotificationHelpers;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelAPIResSingleMember;
import com.uts.mobprog210040138.models.ModelMember;
import com.uts.mobprog210040138.models.ModelMemberReq;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMemberFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context ctx;
    ModelAPIResSingleMember result;
    ModelMember dataMember;
    View view;
    ImageButton btnBack;
    Button btnAdd;
    TextInputLayout txtInputUsername, txtInputFullName, txtInputAddress, txtInputEmail, txtInputPhoneNumber;
    EditText txtUsername2, txtFullName2, txtAddress2, txtEmail2, txtPhoneNumber2;
    TextView txtTitlePage;
    APIInterfaceMembers apiService = APIClient.getClient().create(APIInterfaceMembers.class);

    public AddMemberFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddMemberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMemberFragment newInstance(String param1, String param2) {
        AddMemberFragment fragment = new AddMemberFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_member, container, false);
        txtInputUsername = view.findViewById(R.id.txtInputUsername);
        txtInputFullName = view.findViewById(R.id.txtInputFullName);
        txtInputAddress = view.findViewById(R.id.txtInputAddress);
        txtInputEmail = view.findViewById(R.id.txtInputEmail);
        txtUsername2 = view.findViewById(R.id.txtUsername2);
        txtFullName2 = view.findViewById(R.id.txtFullName2);
        txtAddress2 = view.findViewById(R.id.txtAddress2);
        txtEmail2 = view.findViewById(R.id.txtEmail2);
        txtPhoneNumber2 = view.findViewById(R.id.txtPhoneNumber2);
        txtInputPhoneNumber = view.findViewById(R.id.txtInputPhoneNumber);
        btnAdd = view.findViewById(R.id.btnUpdateMember);
        btnBack = view.findViewById(R.id.btnBackAdd);
        txtTitlePage = view.findViewById(R.id.txtTitlePage);
        String memberId;
        if(getArguments() != null){
            memberId = getArguments().getString("memberId");
            txtTitlePage.setText("Edit a Members");
            txtInputUsername.getEditText().setText("");
            txtInputFullName.getEditText().setText("");
            txtInputAddress.getEditText().setText("");
            txtInputEmail.getEditText().setText("");
            txtInputPhoneNumber.getEditText().setText("");

            loadMember(memberId);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateMember(memberId);
                }
            });
        } else {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createMember();
                }
            });
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { backMenu(); }
        });

        return view;
    }

    public void backMenu() {
        if(txtInputUsername.getEditText() !=null || txtInputFullName.getEditText() !=null || txtInputAddress.getEditText() !=null || txtInputEmail.getEditText() !=null || txtInputPhoneNumber.getEditText() !=null) {
            ConfirmMessage confirmMessage = new ConfirmMessage(ctx);
            confirmMessage.setMessage("Do you want discard your changes?");
            confirmMessage.show();

            confirmMessage.setConfirmationCallback(new ConfirmMessage.ConfirmationCallback() {
                @Override
                public void onConfirmation(boolean isConfirmed) {
                    if (isConfirmed) {
                        getParentFragmentManager().popBackStack();
                    } else {

                    }
                }
            });
        } else {
            getParentFragmentManager().popBackStack();
        }

    }

    public void createMember(){
        if(txtUsername2.getText() !=null && txtFullName2.getText() !=null && txtAddress2.getText() !=null && txtEmail2.getText() !=null && txtPhoneNumber2.getText() !=null) {
            ModelMemberReq memberReq = new ModelMemberReq(txtUsername2.getText().toString(), txtFullName2.getText().toString(), txtAddress2.getText().toString(), txtEmail2.getText().toString(), txtPhoneNumber2.getText().toString());

            Call<ModelAPIResSingleMember> createMember = apiService.createMember(memberReq);
            createMember.enqueue(new Callback<ModelAPIResSingleMember>(){
                @Override
                public void onResponse(Call<ModelAPIResSingleMember> call, Response<ModelAPIResSingleMember> response) {
                    if (response.code() != 201) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        if (response.body() == null){
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                            notification.show();
                        } else {
                            result = response.body();
                            dataMember = result.getData();
                            MembersFragment loansFragment = new MembersFragment();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, loansFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null)
                                    .commit();
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Loans added successfully", NotificationHelpers.Status.SUCCESS);
                            notification.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ModelAPIResSingleMember> call, Throwable t) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                    notification.show();
                }
            });
        }
    }

    public void updateMember(String memberId){
        if(txtUsername2.getText() !=null && txtFullName2.getText() !=null && txtAddress2.getText() !=null && txtEmail2.getText() !=null && txtPhoneNumber2.getText() !=null) {
            ModelMemberReq memberReq = new ModelMemberReq(txtUsername2.getText().toString(), txtFullName2.getText().toString(), txtAddress2.getText().toString(), txtEmail2.getText().toString(), txtPhoneNumber2.getText().toString());

            Call<ModelAPIResSingleMember> updateMember = apiService.updateMember(memberId, memberReq);
            updateMember.enqueue(new Callback<ModelAPIResSingleMember>(){
                @Override
                public void onResponse(Call<ModelAPIResSingleMember> call, Response<ModelAPIResSingleMember> response) {
                    if (response.code() != 201) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        if (response.body() == null){
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                            notification.show();
                        } else {
                            result = response.body();
                            dataMember = result.getData();
                            MembersFragment loansFragment = new MembersFragment();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, loansFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null)
                                    .commit();
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Loans added successfully", NotificationHelpers.Status.SUCCESS);
                            notification.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ModelAPIResSingleMember> call, Throwable t) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                    notification.show();
                }
            });
        }
    }

    public void loadMember(String memberId){
        Call<ModelAPIResSingleMember> getMemberById = apiService.getMemberById(memberId);
        getMemberById.enqueue(new Callback<ModelAPIResSingleMember>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleMember> call, Response<ModelAPIResSingleMember> response) {
                if (response.code() != 200) {

                } else {
                    if(response.body() == null) {

                    } else {
                        result = response.body();
                        dataMember = result.getData();

                        txtInputUsername.getEditText().setText(dataMember.getUsername());
                        txtInputFullName.getEditText().setText(dataMember.getFullName());
                        txtInputAddress.getEditText().setText(dataMember.getAddress());
                        txtInputEmail.getEditText().setText(dataMember.getEmail());
                        txtInputPhoneNumber.getEditText().setText(dataMember.getPhoneNumber());

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleMember> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                notification.show();
            }
        });
    }
}