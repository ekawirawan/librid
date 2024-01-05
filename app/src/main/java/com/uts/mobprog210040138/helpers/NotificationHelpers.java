package com.uts.mobprog210040138.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.uts.mobprog210040138.R;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationHelpers {
    Context ctx;
    String msg;

    public enum Status {
        SUCCESS,  WARNING, DANGER
    }

    Status sts;
    Dialog dialog;

    TextView txtMessage;
    ImageButton btnClose;


    public NotificationHelpers (Context context, String message, Status status) {
        this.ctx = context;
        this.msg = message;
        this.sts = status;

        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notification);

        txtMessage = dialog.findViewById(R.id.txtMessageNotif);
        btnClose = dialog.findViewById(R.id.btnCloseNotif);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        txtMessage.setText(msg);

        switch (status) {
            case SUCCESS:
                txtMessage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.drawable.baseline_check_circle_24), null, null, null);
                break;
            case WARNING:
                txtMessage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.drawable.baseline_error_24), null, null, null);
                break;
            case DANGER:
                txtMessage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.drawable.baseline_report_problem_24), null, null, null);
                break;
        }

    }

    public void show() {
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000L);
    }

}
