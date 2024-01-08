package com.uts.mobprog210040138;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.uts.mobprog210040138.helpers.ConfirmMessage;
import com.uts.mobprog210040138.helpers.NotificationHelpers;
import com.uts.mobprog210040138.helpers.ProgressBarHelpers;
import com.uts.mobprog210040138.models.ModelAPIResLoans;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelLoans;
import com.uts.mobprog210040138.models.SharedDataViewModel;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoansFragment extends Fragment {
    enum ReturnStatus {
        NOT_YET_RETURNED,
        RETURNED,
        RETURNED_LATE
    }

    Context ctx;
    APIInterfaceLoans apiService = APIClient.getClient().create(APIInterfaceLoans.class);
    ModelAPIResLoans result;
    ModelAPIResSingleLoans resultLoanSingle;
    List<ModelLoans> dataLoan, dataResSearch;

    ModelLoans dataLoanSingle;

    TextView txtTotalLoans, txtInfoLoans;
    SearchView searchViewLoan;

    public RecyclerView recyclerView1;

    RecyclerViewCustomeAdapterLoans adapterLoans;
    ProgressBar progressBarLoans;

    private View view;

    Button btnAdd;
    SharedDataViewModel sharedDataViewModel;

    ProgressBarHelpers progressBarHelpers;

    public LoansFragment() {

    }

    public static LoansFragment newInstance() {
        LoansFragment fragment = new LoansFragment();
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
        view = inflater.inflate(R.layout.fragment_loans, container, false);
        recyclerView1 = view.findViewById(R.id.recyclerViewLoans);

        searchViewLoan = view.findViewById(R.id.searchViewLoan);
        btnAdd = view.findViewById(R.id.btnAddDataLoan);
        progressBarLoans = view.findViewById(R.id.progressBarLoans);
        txtInfoLoans = view.findViewById(R.id.txtInfoLoans);

        progressBarHelpers = new ProgressBarHelpers(progressBarLoans);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setHasFixedSize(true);

        loadDataLoan();
        searchLoan();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLoansFragment fragment = new AddLoansFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    public void loadDataLoan () {
        if (result == null) {
            progressBarHelpers.show();
        }

        Call<ModelAPIResLoans> getAllLoan = apiService.getAllLoan();
        getAllLoan.enqueue(new Callback<ModelAPIResLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResLoans> call, Response<ModelAPIResLoans> response) {
                if (response.code() != 200) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load loans data", NotificationHelpers.Status.DANGER);
                    notification.show();
                    progressBarHelpers.hide();
                } else {
                    if (response.body().getData().size() == 0) {
                        txtInfoLoans.setText("Opss..Loans data is empty");
                        txtInfoLoans.setVisibility(View.VISIBLE);
                        progressBarHelpers.hide();
                    } else {
                        txtInfoLoans.setVisibility(View.INVISIBLE);
                        result = response.body();
                        dataLoan = result.getData();

                        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataLoan);
                        adapterLoans.setOnMoreButtonClickListener(new RecyclerViewCustomeAdapterLoans.OnMoreButtonClickListener() {
                            @Override
                            public void onMoreButtonClick(int position) {
                                showBottomSheetLoan(dataLoan.get(position).getLoanId(), dataLoan.get(position).getReturnStatus(), dataLoan.get(position));
                            }
                        });

                        adapterLoans.setOnItemCLickListener(new RecyclerViewCustomeAdapterLoans.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendIdToDetailLoan(dataLoan.get(position).getLoanId());
                            }
                        });

                        recyclerView1.setAdapter(adapterLoans);
                        setTotalLoan(dataLoan);
                        progressBarHelpers.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResLoans> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load loans data", NotificationHelpers.Status.DANGER);
                notification.show();
                progressBarHelpers.hide();
            }
        });
    }

    public void sendIdToDetailLoan(String loanId) {
        DetailLoanFragment fragment = new DetailLoanFragment().newInstance(loanId);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
    }

    public void searchLoan() {
        searchViewLoan.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        Call<ModelAPIResLoans> getAllLoanByUsername = apiService.getAllLoanByUsername(query.trim().toLowerCase());
        getAllLoanByUsername.enqueue(new Callback<ModelAPIResLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResLoans> call, Response<ModelAPIResLoans> response) {
                if (response.code() != 200) {
                    progressBarHelpers.hide();
                } else {
                    if (response.body().getData().size() == 0) {
                        txtInfoLoans.setText("Opss..Loans data not found");
                        txtInfoLoans.setVisibility(View.VISIBLE);
                        progressBarHelpers.hide();
                        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, Collections.emptyList());
                        recyclerView1.setAdapter(adapterLoans);
                        txtTotalLoans.setVisibility(View.INVISIBLE);
                    } else {
                        txtInfoLoans.setVisibility(View.INVISIBLE);
                        txtTotalLoans.setVisibility(View.VISIBLE);
                        result = response.body();
                        dataResSearch = result.getData();
                        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataResSearch);

                        adapterLoans.setOnMoreButtonClickListener(new RecyclerViewCustomeAdapterLoans.OnMoreButtonClickListener() {
                            @Override
                            public void onMoreButtonClick(int position) {
                                showBottomSheetLoan(dataLoan.get(position).getLoanId(), dataLoan.get(position).getReturnStatus(), dataLoan.get(position));
                            }
                        });

                        adapterLoans.setOnItemCLickListener(new RecyclerViewCustomeAdapterLoans.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendIdToDetailLoan(dataLoan.get(position).getLoanId());
                            }
                        });

                        recyclerView1.setAdapter(adapterLoans);
                        setTotalLoan(dataResSearch);
                        progressBarHelpers.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResLoans> call, Throwable t) {
                progressBarHelpers.hide();
            }
        });
    }

    private void resetSearch() {
        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataLoan);
        recyclerView1.setAdapter(adapterLoans);
        setTotalLoan(dataLoan);
        txtInfoLoans.setVisibility(View.INVISIBLE);
        txtTotalLoans.setVisibility(View.VISIBLE);
    }

    public void setTotalLoan(List<ModelLoans> data) {
        String wordLoan = "Loan";
        txtTotalLoans = view.findViewById(R.id.txtTotalLoans);
        Integer totalDataReturn = data.size();
        if(totalDataReturn > 1) { wordLoan = "Loans"; }
        txtTotalLoans.setText(totalDataReturn.toString() + " " + wordLoan);
    }

    public void updateReturnStatusLoans (String loanId) {
        Call<ModelAPIResSingleLoans> updateReturnStatus = apiService.updateReturnStatusLoan(loanId);
        updateReturnStatus.enqueue(new Callback<ModelAPIResSingleLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
                if (response.code() != 200) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to update return status", NotificationHelpers.Status.DANGER);
                    notification.show();
                } else {
                    if (response.body() == null) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to update return status", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        loadDataLoan();
                        adapterLoans.notifyDataSetChanged();
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Return status updated successfully", NotificationHelpers.Status.SUCCESS);
                        notification.show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to update return status", NotificationHelpers.Status.DANGER);
                notification.show();
            }
        });
    }

    public void deleteLoans(String loanId) {
        Call<ModelAPIResSingleLoans> deleteLoans = apiService.deleteLoan(loanId);
        deleteLoans.enqueue(new Callback<ModelAPIResSingleLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
                if (response.code() != 200) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to delete loans", NotificationHelpers.Status.DANGER);
                    notification.show();
                } else {
                    if (response.body() == null) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to delete loans", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        loadDataLoan();
                        adapterLoans.notifyDataSetChanged();
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Loans deleted successfully", NotificationHelpers.Status.SUCCESS);
                        notification.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to delete loans", NotificationHelpers.Status.DANGER);
                notification.show();
            }
        });
    }

//    public void showDetailLoans (int position) {
//        Call<ModelAPIResSingleLoans> getLoanById = apiService.getLoanById(dataLoan.get(position).getLoanId());
//        getLoanById.enqueue(new Callback<ModelAPIResSingleLoans>() {
//            @Override
//            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
//                if (response.code() != 200) {
//
//                } else {
//                    if(response.body() == null) {
//
//                    } else {
//                        resultLoanSingle = response.body();
//                        dataLoanSingle = resultLoanSingle.getData();
//
//                        ViewGroup viewGroup = view.findViewById(android.R.id.content);
//                        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.detail_data_loans, viewGroup, false);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//                        builder.setTitle("Informasi");
//                        builder.setIcon(android.R.drawable.ic_dialog_info);
//                        builder.setCancelable(false);
//
//                        TextView txtTitleBookLoan = dialogView.findViewById(R.id.txtTitleBookLoan);
//                        TextView txtUsernameLoans = dialogView.findViewById(R.id.txtUsernameLoans);
//                        TextView txtStatusLoans = dialogView.findViewById(R.id.txtStatusLoans);
//                        TextView txtBorrowedAtLoans = dialogView.findViewById(R.id.txtBorrowedAtLoans);
//                        TextView txtDueDateLoans = dialogView.findViewById(R.id.txtDueDateLoans);
//                        TextView txtReturnedAtLoans = dialogView.findViewById(R.id.txtReturnedAtLoans);
//
//                        builder.setView(dialogView);
//
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//
//                        txtTitleBookLoan.setText(dataLoanSingle.getBook().getTitle());
//                        txtUsernameLoans.setText(dataLoanSingle.getBorrower().getUsername());
//
//                        if (ReturnStatus.NOT_YET_RETURNED.name().equals(dataLoanSingle.getReturnStatus())) {
//                            TextViewStyle.textStatusReturnedStyle("NOT YET RETURNED", txtStatusLoans, TextViewStyle.TypeStyle.WARNING, ctx);
//                        } else if (ReturnStatus.RETURNED.name().equals(dataLoanSingle.getReturnStatus())) {
//                            TextViewStyle.textStatusReturnedStyle("RETURNED", txtStatusLoans, TextViewStyle.TypeStyle.SUCCESS, ctx);
//                        } else if (ReturnStatus.RETURNED_LATE.name().equals(dataLoanSingle.getReturnStatus())) {
//                            TextViewStyle.textStatusReturnedStyle("RETURNED LATE", txtStatusLoans, TextViewStyle.TypeStyle.DANGER, ctx);
//                        } else {
//                            TextViewStyle.textStatusReturnedStyle("INVALID", txtStatusLoans, TextViewStyle.TypeStyle.DANGER, ctx);
//                        }
//
//
//                        txtBorrowedAtLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getBorrowedAt()));
//                        txtDueDateLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getDueDate()));
//                        if (dataLoanSingle.getReturnedAt() == null)
//                            txtReturnedAtLoans.setText("-");
//                        else {
//                            txtReturnedAtLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getReturnedAt()));
//                        }
//
//                        AlertDialog alertDialog = builder.create();
//                        alertDialog.show();
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

    public void showBottomSheetLoan(String loanId, String returnStatus, ModelLoans loanData) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_action);

        LinearLayout returnLayout = dialog.findViewById(R.id.layoutReturn);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        if(!("NOT_YET_RETURNED".equals(returnStatus))) {
            returnLayout.setVisibility(View.GONE);
        }

        returnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmMessage confirmMessage = new ConfirmMessage(ctx);
                confirmMessage.setMessage("Are you sure to return this loans?");
                confirmMessage.setTextButtonYes("Yes");
                confirmMessage.setTextButtonCancle("Cancel");
                confirmMessage.show();

                confirmMessage.setConfirmationCallback(new ConfirmMessage.ConfirmationCallback() {
                    @Override
                    public void onConfirmation(boolean isConfirmed) {
                        if (isConfirmed) {
                            updateReturnStatusLoans(loanId);
                        }
                    }
                });

            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sharedDataViewModel.setIdLoan(loanData.getLoanId());
                sharedDataViewModel.setBookId(loanData.getBook().getBookId());
                sharedDataViewModel.setTitle(loanData.getBook().getTitle());
                sharedDataViewModel.setMemberId(loanData.getBorrower().getMemberId());
                sharedDataViewModel.setUsername(loanData.getBorrower().getUsername());

                AddLoansFragment fragment = new AddLoansFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                ConfirmMessage confirmMessage = new ConfirmMessage(ctx);
                confirmMessage.setMessage("Are you sure to delete this loans?");
                confirmMessage.setTextButtonYes("Yes");
                confirmMessage.setTextButtonCancle("Cancel");
                confirmMessage.show();

                confirmMessage.setConfirmationCallback(new ConfirmMessage.ConfirmationCallback() {
                    @Override
                    public void onConfirmation(boolean isConfirmed) {
                        if (isConfirmed) {
                            deleteLoans(loanId);
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
    
}