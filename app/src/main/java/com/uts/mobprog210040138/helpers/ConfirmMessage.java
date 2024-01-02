package com.uts.mobprog210040138.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.uts.mobprog210040138.R;

public class ConfirmMessage {

    private Context ctx;
    private TextView message;
    private Button buttonYes;
    private Button buttonCancle;
    private Dialog dialog;

    public interface ConfirmationCallback {
        void onConfirmation(boolean isConfirmed);
    }

    private ConfirmationCallback confirmationCallback;


    public ConfirmMessage (Context context) {
        this.ctx = context;

        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_bottom_confirm);

        message = dialog.findViewById(R.id.txtConfirm);
        buttonYes = dialog.findViewById(R.id.btnConfirmYes);
        buttonCancle = dialog.findViewById(R.id.btnConfirmCancle);
    }

    public void show () {
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmationCallback != null) {
                    confirmationCallback.onConfirmation(true);
                }
                dialog.dismiss();
            }
        });

        buttonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmationCallback != null) {
                    confirmationCallback.onConfirmation(false);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    public void setMessage(String newMessage) {
        this.message.setText(newMessage);
    }

    public void setTextButtonYes(String newText) {
        this.buttonYes.setText(newText);
    }

    public void setTextButtonCancle(String newText) {
        this.buttonCancle.setText(newText);
    }

    public void setConfirmationCallback(ConfirmationCallback callback) {
        this.confirmationCallback = callback;
    }
}
