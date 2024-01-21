package com.uts.mobprog210040138;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.net.Uri;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uts.mobprog210040138.helpers.ConfirmMessage;
import com.uts.mobprog210040138.helpers.NotificationHelpers;
import com.uts.mobprog210040138.models.ModelAPIResSingleBook;
import com.uts.mobprog210040138.models.ModelBook;
import com.uts.mobprog210040138.models.ModelBookReq;
import com.uts.mobprog210040138.models.SharedDataViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBooksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context ctx;
    ModelAPIResSingleBook result;
    ModelBook dataBook;
    View view;
    Button btnSave, btnCancel;
    ImageView imageAdd;

    String imageUrl;
    Boolean isUpdate = false;

    TextInputLayout inputTitle, inputAuthor, inputPublisher, inputYear, inputISBN, inputStock, inputRack ;
    EditText txtTitle, txtAuthor, txtPublisher, txtYear, txtIsbn, txtStock, txtRack;
    TextView txtTitlePage;

    private SharedDataViewModel sharedDataViewModel;

    ApiInterfaceBook apiService = APIClient.getClient().create(ApiInterfaceBook.class);

    public AddBooksFragment() {
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
    public static AddBooksFragment newInstance(String param1, String param2) {
        AddBooksFragment fragment = new AddBooksFragment();
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
        view =  inflater.inflate(R.layout.fragment_add_book, container, false);
        btnSave = view.findViewById(R.id.buttonSave);
        btnCancel = view.findViewById(R.id.buttonCancel);
        imageAdd = view.findViewById(R.id.imageAddBook);
        inputTitle = view.findViewById(R.id.inputTitle);
        inputAuthor = view.findViewById(R.id.inputAuthor);
        inputISBN = view.findViewById(R.id.inputIsbn);
        inputPublisher = view.findViewById(R.id.inputPublisher);
        inputStock = view.findViewById(R.id.inputStock);
        inputYear = view.findViewById(R.id.inputYear);
        inputRack = view.findViewById(R.id.inputRack);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtAuthor = view.findViewById(R.id.txtAuthor);
        txtPublisher = view.findViewById(R.id.txtPublisher);
        txtYear = view.findViewById(R.id.txtYear);
        txtIsbn = view.findViewById(R.id.txtIsbn);
        txtStock = view.findViewById(R.id.txtStock);
        txtRack = view.findViewById(R.id.txtRack);
        txtTitlePage = view.findViewById(R.id.textview01);
        String bookId;
        if(getArguments() != null){
            bookId = getArguments().getString("bookId");
            txtTitlePage.setText("Edit a Book");
            inputTitle.getEditText().setText("");
            inputAuthor.getEditText().setText("");
            inputPublisher.getEditText().setText("");
            inputISBN.getEditText().setText("");
            inputYear.getEditText().setText("");
            inputStock.getEditText().setText("");
            inputRack.getEditText().setText("");
            loadBook(bookId);
            Log.d("argument", "ngedit cuy");
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadImage();
                }
            });
        } else {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadImage();
                }
            });
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelMenu();
            }
        });

        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectImage();
            }
        });
        return view;
    }

    public void cancelMenu() {
        if(txtTitle.getText() !=null || txtAuthor.getText() !=null || txtIsbn.getText() !=null || txtPublisher.getText() !=null || txtStock.getText() !=null || txtYear.getText() !=null || txtRack.getText() !=null) {
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


//    public void addBook(){
//        Log.d("arguments", "Add cuy");
//        if(txtTitle.getText() !=null && txtAuthor.getText() !=null && txtPublisher.getText() !=null && txtYear.getText() !=null && txtIsbn.getText() !=null && txtStock.getText() !=null && txtRack.getText() !=null) {
//
//            ModelBookReq bookReq = new ModelBookReq (txtTitle.getText().toString(), txtAuthor.getText().toString(), txtPublisher.getText().toString(), Integer.valueOf(txtYear.getText().toString()), txtIsbn.getText().toString(), Integer.valueOf(txtStock.getText().toString()), imageUrl, txtRack.getText().toString());
//
//            Call<ModelAPIResSingleBook> createBook = apiService.createBook(bookReq);
//            createBook.enqueue(new Callback<ModelAPIResSingleBook>(){
//                @Override
//                public void onResponse(Call<ModelAPIResSingleBook> call, Response<ModelAPIResSingleBook> response) {
//                    if (response.code() != 201) {
//                        Log.d("images", "onResponse: cek" + imageUrl);
//                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong a", NotificationHelpers.Status.DANGER);
//                        notification.show();
//                    } else {
//                        if (response.body() == null){
//                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong b", NotificationHelpers.Status.DANGER);
//                            notification.show();
//                        } else {
//                            result = response.body();
//                            dataBook = result.getData();
//                            uploadImage();
//                            BooksFragment booksFragment = new BooksFragment();
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.frame_layout, booksFragment)
//                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                                    .addToBackStack(null)
//                                    .commit();
//                            NotificationHelpers notification = new NotificationHelpers(ctx, "Book added successfully", NotificationHelpers.Status.SUCCESS);
//                            notification.show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {
//                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong c", NotificationHelpers.Status.DANGER);
//                    notification.show();
//                }
//            });
//        }
//    }

    private void selectImage() {
        final CharSequence[] items = {"Choose from library", "Cancel"};
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Choose from library")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Image"),20);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            final Uri path = data.getData();
            Thread thread = new Thread(() -> {
                try {
                    InputStream inputStream = requireContext().getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageAdd.post(() -> {
                        imageAdd.setImageBitmap(bitmap);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }


    private void uploadImage() {
        imageAdd.setDrawingCacheEnabled(true);
        imageAdd.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageAdd.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Generate a unique filename
        String imageName = "BOOK" + new Date().getTime() + ".jpeg";

        // Upload the image
        StorageReference imageRef = storageRef.child("book/" + imageName);
        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL
                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            if (downloadUrl != null) {
                                // Now you have the download URL, you can use it as needed
                                imageUrl = downloadUrl.toString();

                                if (getArguments() != null && getArguments().containsKey("bookId")){
                                    String bookId = getArguments().getString("bookId");
                                    updateBook(bookId);
                                }else {
                                    addBook();
                                }
                                // Use imageUrl as needed (e.g., include it in your ModelBookReq)
                                // Save imageUrl to API
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void addBook() {
        if (txtTitle.getText() != null && txtAuthor.getText() != null && txtPublisher.getText() != null &&
                txtIsbn.getText() != null && txtStock.getText() != null && txtYear.getText() != null &&
                txtRack.getText() != null) {

            ModelBookReq bookReq = new ModelBookReq(
                    txtTitle.getText().toString(),
                    txtAuthor.getText().toString(),
                    txtPublisher.getText().toString(),
                    Integer.parseInt(txtYear.getText().toString()),
                    txtIsbn.getText().toString(),
                    Integer.parseInt(txtStock.getText().toString()),
                    imageUrl != null ? imageUrl : dataBook.getImageUrl(),
                    txtRack.getText().toString()
            );

            Call<ModelAPIResSingleBook> createBook = apiService.createBook(bookReq);
            createBook.enqueue(new Callback<ModelAPIResSingleBook>() {
                @Override
                public void onResponse(Call<ModelAPIResSingleBook> call, Response<ModelAPIResSingleBook> response) {
                    if (response.code() != 201) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong a", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        if (response.body() == null) {
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong b", NotificationHelpers.Status.DANGER);
                            notification.show();
                        } else {
                            result = response.body();
                            dataBook = result.getData();
                            BooksFragment booksFragment = new BooksFragment();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, booksFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null)
                                    .commit();
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Book added successfully", NotificationHelpers.Status.SUCCESS);
                            notification.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong c", NotificationHelpers.Status.DANGER);
                    notification.show();
                }
            });
        }
    }

    private void updateBook(String bookId) {
        if (txtTitle.getText() != null && txtAuthor.getText() != null && txtPublisher.getText() != null &&
                txtIsbn.getText() != null && txtStock.getText() != null && txtYear.getText() != null &&
                txtRack.getText() != null) {
            ModelBookReq bookReq = new ModelBookReq(
                    txtTitle.getText().toString(),
                    txtAuthor.getText().toString(),
                    txtPublisher.getText().toString(),
                    Integer.parseInt(txtYear.getText().toString()),
                    txtIsbn.getText().toString(),
                    Integer.parseInt(txtStock.getText().toString()),
                    imageUrl != null ? imageUrl : dataBook.getImageUrl(),
                    txtRack.getText().toString()
            );

            Call<ModelAPIResSingleBook> updateBook = apiService.updateBook(bookId, bookReq);
            updateBook.enqueue(new Callback<ModelAPIResSingleBook>() {
                @Override
                public void onResponse(Call<ModelAPIResSingleBook> call, Response<ModelAPIResSingleBook> response) {
                    if (response.code() != 200) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        if (response.body() == null) {
                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                            notification.show();
                        } else {
                            result = response.body();
                            dataBook = result.getData();
                            BooksFragment booksFragment = new BooksFragment();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, booksFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null)
                                    .commit();
                            NotificationHelpers notification = new NotificationHelpers(ctx, "This Book updated successfully", NotificationHelpers.Status.SUCCESS);
                            notification.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                    notification.show();
                }
            });
        }
    }


//    private void saveImageUrlToAPI(String bookId) {
//        Log.d("saveImageUrlToAPI", "imageUrl: " + imageUrl);
//        if(txtTitle.getText() !=null && txtAuthor.getText() !=null && txtPublisher.getText() !=null && txtIsbn.getText() !=null && txtStock.getText() !=null && txtYear.getText()  !=null && imageAdd.getDrawable() != null && txtRack.getText() !=null){
//            // Include imageUrl in your ModelBookReq and call the API to save/update the book with the image URL
//            ModelBookReq bookReq = new ModelBookReq(
//                    txtTitle.getText().toString(),
//                    txtAuthor.getText().toString(),
//                    txtPublisher.getText().toString(),
//                    Integer.parseInt(txtYear.getText().toString()),
//                    txtIsbn.getText().toString(),
//                    Integer.parseInt(txtStock.getText().toString()),
//                    imageUrl != null ? imageUrl : dataBook.getImageUrl(),
//                    txtRack.getText().toString()
//            );
//
//            // Determine whether to call createBook or updateBook based on the isUpdate parameter
//            Call<ModelAPIResSingleBook> apiCall;
//            if (isUpdate) {
//                apiCall = apiService.updateBook(bookId, bookReq); // Assuming you have the bookId for updating
//            } else {
//                apiCall = apiService.createBook(bookReq);
//            }
//
//            // Call the API to save/update the book with the image URL
//            apiCall.enqueue(new Callback<ModelAPIResSingleBook>() {
//                @Override
//                public void onResponse(Call<ModelAPIResSingleBook> call, Response<ModelAPIResSingleBook> response) {
//                    // Handle the response
//                    if (response.code() != 200 && response.code() != 201) {
//                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong a", NotificationHelpers.Status.DANGER);
//                        notification.show();
//                    } else {
//                        if (response.body() == null){
//                            NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong b", NotificationHelpers.Status.DANGER);
//                            notification.show();
//                        } else {
//                            result = response.body();
//                            dataBook = result.getData();
//                            BooksFragment booksFragment = new BooksFragment();
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.frame_layout, booksFragment)
//                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                                    .addToBackStack(null)
//                                    .commit();
//                            NotificationHelpers notification = new NotificationHelpers(ctx, "Book " + (isUpdate ? "updated" : "added") + " successfully", NotificationHelpers.Status.SUCCESS);
//                            notification.show();
//                        }
//                    }
//                }
//                @Override
//                public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {
//                    // Handle the failure
//                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
//                    notification.show();
//                }
//            });
//        }
//
//    }

//    private void showNotification(String message, NotificationHelpers.Status status) {
//        NotificationHelpers notification = new NotificationHelpers(ctx, message, status);
//        notification.show();
//    }



    public void loadBook(String bookId){
        Call<ModelAPIResSingleBook> getBookById = apiService.getBookById(bookId);
        getBookById.enqueue(new Callback<ModelAPIResSingleBook>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleBook> call, Response<ModelAPIResSingleBook> response) {
                if (response.code() != 200) {

                } else {
                    if(response.body() == null) {

                    } else {
                        result = response.body();
                        dataBook = result.getData();

                        inputTitle.getEditText().setText(dataBook.getTitle());
                        inputAuthor.getEditText().setText(dataBook.getAuthor());
                        inputPublisher.getEditText().setText(dataBook.getPublisher());
                        inputYear.getEditText().setText(String.valueOf(dataBook.getPublicationYear()));
                        inputISBN.getEditText().setText(dataBook.getIsbn());
                        inputStock.getEditText().setText(String.valueOf(dataBook.getStock()));
                        Glide.with(ctx)
                                .load(dataBook.getImageUrl())
                                .placeholder(R.drawable.none)
                                .into(imageAdd);
                        inputRack.getEditText().setText(dataBook.getBookRackLocation());
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleBook> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Something went wrong", NotificationHelpers.Status.DANGER);
                notification.show();
            }
        });
    }

}