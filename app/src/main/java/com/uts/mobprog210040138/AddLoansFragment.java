package com.uts.mobprog210040138;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uts.mobprog210040138.helpers.ConfirmMessage;
import com.uts.mobprog210040138.helpers.NotificationHelpers;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelLoanReq;
import com.uts.mobprog210040138.models.ModelLoans;
import com.uts.mobprog210040138.models.SharedDataViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLoansFragment extends Fragment {
    Context ctx;
    ModelAPIResSingleLoans result;
    ModelLoans dataLoan;

    View view;

    ConstraintLayout CLChooseBook, CLChooseMember;
    ImageButton btnBack;
    Button btnAdd;

    APIInterfaceLoans apiService = APIClient.getClient().create(APIInterfaceLoans.class);
    TextView txtUsernameChoose, txtIdMemberChoose, txtTitleBook, txtIdBook, txtPageTitle;

    private SharedDataViewModel sharedDataViewModel;


    public AddLoansFragment() {

    }

    public static AddLoansFragment newInstance() {
        AddLoansFragment fragment = new AddLoansFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_loans, container, false);
        CLChooseBook = view.findViewById(R.id.CLChooseBook);
        CLChooseMember = view.findViewById(R.id.CLChooseMember);
        btnAdd = view.findViewById(R.id.btnAddLoans);
        btnBack = view.findViewById(R.id.btnBackAdd);
        txtUsernameChoose = view.findViewById(R.id.txtUsernameChoose);
        txtIdMemberChoose = view.findViewById(R.id.txtIdMemberChoose);
        txtIdBook = view.findViewById(R.id.txtIdBookChoose);
        txtTitleBook = view.findViewById(R.id.txtTitleBookChoose);
        txtPageTitle = view.findViewById(R.id.txtPageTitle);

        //fungsi edit
        if (sharedDataViewModel.getIdLoan() != null) {
            String loanId = sharedDataViewModel.getIdLoan();
            txtPageTitle.setText("Edit a loans");
            txtUsernameChoose.setText("");
            txtTitleBook.setText("");
            //loadDataToForm(loanId);

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLoan(loanId);
                }
            });
        } else {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLoans();
                }
            });
        }

        //retrive choose member
        if (sharedDataViewModel.getMemberId() != null && sharedDataViewModel.getUsername() != null) {
            String memberId = sharedDataViewModel.getMemberId();
            String username = sharedDataViewModel.getUsername();

            txtUsernameChoose.setText(username);
            txtIdMemberChoose.setText(memberId);
        }

        if (sharedDataViewModel.getBookId() != null && sharedDataViewModel.getTitle() != null) {
            String bookId = sharedDataViewModel.getBookId();
            String title = sharedDataViewModel.getTitle();

            txtIdBook.setText(bookId);
            txtTitleBook.setText(title);
        }

        CLChooseBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBookFragment fragment = new SearchBookFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        CLChooseMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchMemberFragment fragment = new SearchMemberFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { backMenu(); }
        });


        return view;
    }

    public void backMenu() {
        if(txtIdBook.getText() != "" || txtIdMemberChoose.getText() != "") {
            ConfirmMessage confirmMessage = new ConfirmMessage(ctx);
            confirmMessage.setMessage("Do you want discard your changes?");
            confirmMessage.show();

            confirmMessage.setConfirmationCallback(new ConfirmMessage.ConfirmationCallback() {
                @Override
                public void onConfirmation(boolean isConfirmed) {
                    if (isConfirmed) {
                        LoansFragment fragment = new LoansFragment();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null)
                                .commit();
                        resetSharedDataViewModel();
                    }
                }
            });
        } else {
            getParentFragmentManager().popBackStack();
        }
    }

    public void resetSharedDataViewModel() {
        sharedDataViewModel.setBookId(null);
        sharedDataViewModel.setTitle(null);
        sharedDataViewModel.setMemberId(null);
        sharedDataViewModel.setUsername(null);
        sharedDataViewModel.setIdLoan(null);
    }

    public void addLoans() {
        if(txtIdBook.getText() != null && txtIdMemberChoose.getText() != null) {
            ModelLoanReq loanReq = new ModelLoanReq(txtIdMemberChoose.getText().toString(), txtIdBook.getText().toString());

            Call<ModelAPIResSingleLoans> addLoan = apiService.addLoan(loanReq);
            addLoan.enqueue(new Callback<ModelAPIResSingleLoans>() {
                @Override
                public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
                    if (response.code() != 201) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        if (response.body() == null){
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                            notification.show();
                        } else {
                            result = response.body();
                            dataLoan = result.getData();
                            resetSharedDataViewModel();
                            LoansFragment loansFragment = new LoansFragment();
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
                public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                    notification.show();
                }
            });



        }

    }


//    public void loadDataToForm(String loanId) {
//        Call<ModelAPIResSingleLoans> getDataLoanById = apiService.getLoanById(loanId);
//        getDataLoanById.enqueue(new Callback<ModelAPIResSingleLoans>() {
//            @Override
//            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
//                if(response.code() != 200) {
//
//                } else {
//                    if (response.body() == null) {
//
//                    } else {
//                        result = response.body();
//                        dataLoan = result.getData();
//
//                        txtIdBook.setText(dataLoan.getBook().getBookId());
//                        txtTitleBook.setText(dataLoan.getBook().getTitle());
//                        txtIdMemberChoose.setText(dataLoan.getBorrower().getMemberId());
//                        txtUsernameChoose.setText(dataLoan.getBorrower().getUsername());
//
//                        sharedDataViewModel.setBookId(dataLoan.getBook().getBookId());
//                        sharedDataViewModel.setTitle(dataLoan.getBook().getTitle());
//                        sharedDataViewModel.setMemberId(dataLoan.getBorrower().getMemberId());
//                        sharedDataViewModel.setUsername(dataLoan.getBorrower().getUsername());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {
//
//            }
//        });
//    }

    public void updateLoan(String loanId) {
        if(txtIdBook.getText() != null && txtIdMemberChoose.getText() != null) {
            ModelLoanReq loanReq = new ModelLoanReq(txtIdMemberChoose.getText().toString(), txtIdBook.getText().toString());

            Call<ModelAPIResSingleLoans> updateLoan = apiService.updateLoan(loanId, loanReq);
            updateLoan.enqueue(new Callback<ModelAPIResSingleLoans>() {
                @Override
                public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
                    if (response.code() != 200) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        if (response.body() == null){
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                            notification.show();
                        } else {
                            result = response.body();
                            dataLoan = result.getData();
                            resetSharedDataViewModel();
                            LoansFragment loansFragment = new LoansFragment();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, loansFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null)
                                    .commit();
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Loans updated successfully", NotificationHelpers.Status.SUCCESS);
                            notification.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                    notification.show();
                }
            });

        }
    }


}

