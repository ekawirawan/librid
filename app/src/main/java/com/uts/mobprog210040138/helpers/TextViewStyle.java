package com.uts.mobprog210040138.helpers;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.uts.mobprog210040138.R;

public class TextViewStyle {

    public enum TypeStyle {
        WARNING,
        DANGER,
        SUCCESS
    }


    public static void textStatusReturnedStyle (String text, TextView textView, TypeStyle typeStyle, Context ctx) {
        switch (typeStyle) {
            case SUCCESS:
                textView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.rounded_corner_success));
                textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.drawable.baseline_check_circle_24), null, null, null);
                break;
            case WARNING:
                textView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.rounded_corner_warning));
                textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.drawable.baseline_error_24), null, null, null);
                break;
            case DANGER:
                textView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.rounded_corner_danger));
                textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.drawable.baseline_report_problem_24), null, null, null);
                break;
        }
        textView.setText(text);

    }
}
