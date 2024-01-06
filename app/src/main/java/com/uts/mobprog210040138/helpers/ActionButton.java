package com.uts.mobprog210040138.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uts.mobprog210040138.R;

public class ActionButton {
    private Context ctx;
    private Dialog dialog;
    private LinearLayout returnLayout, editLayout, deleteLayout;
    private ImageView cancelButton;

    public interface ActionButtonClickListener {
        void onReturnClick();
        void onEditClick();
        void onDeleteClick();
    }

    private ActionButtonClickListener buttonClickListener;

    public ActionButton (Context context, ActionButtonClickListener listener) {
        ctx = context;

        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_action);

        returnLayout = dialog.findViewById(R.id.layoutReturn);
        editLayout = dialog.findViewById(R.id.layoutEdit);
        deleteLayout = dialog.findViewById(R.id.layoutDelete);
        cancelButton = dialog.findViewById(R.id.cancelButton);
    }

    public void show() {
        if(returnLayout.getVisibility() != View.GONE) {
            returnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    buttonClickListener.onReturnClick();
                }
            });
        }

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                buttonClickListener.onEditClick();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                buttonClickListener.onDeleteClick();
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

    public void setReturnButton (boolean isHide) {
        if (isHide) {
            returnLayout.setVisibility(View.GONE);
        }
    }




}
