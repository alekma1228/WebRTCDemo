package com.denystsaruk.webrtcdemo.utility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.denystsaruk.webrtcdemo.R;

public class MessageUtil {
    public static final int TYPE_ERROR = 6020;
    public static final int TYPE_WARNING = 6021;
    public static final int TYPE_SUCCESS = 6022;

    public static void showError(Context context, int messageId) {
        showAlertDialog(context, TYPE_ERROR, messageId);
    }

    public static void showError(Context context, String message) {
        showAlertDialog(context, TYPE_ERROR, message);
    }

    public static void showAlertDialog(Context context, int type, int messageId) {
        if (messageId == 0)
            showAlertDialog(context, type, null);
        else
            showAlertDialog(context, type, context.getString(messageId));
    }

    public static void showAlertDialog(Context context, int type, String message) {
        showAlertDialog(context, type, message, null);
    }

    public static void showAlertDialog(Context context, int type, int messageId, DialogInterface.OnClickListener listener) {
        if (messageId == 0)
            showAlertDialog(context, type, null, listener);
        else
            showAlertDialog(context, type, context.getString(messageId), listener);
    }

    public static void showAlertDialog(Context context, int type, String message, final DialogInterface.OnClickListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) dialog.findViewById(R.id.txt_title);
        TextView messageview = (TextView) dialog.findViewById(R.id.txt_message);
        TextView left = (TextView) dialog.findViewById(R.id.btn_left);
        TextView right = (TextView) dialog.findViewById(R.id.btn_right);
        switch (type) {
            case TYPE_ERROR:
                title.setText("Error");
                break;
            case TYPE_WARNING:
                title.setText("Warning");
                break;
            case TYPE_SUCCESS:
                title.setText("Success");
                break;
        }
        messageview.setText(message);
        left.setVisibility(View.GONE);

        right.setText("OK");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener!= null)
                    listener.onClick(dialog, -1);
            }
        });
        dialog.show();
    }
    public static void showAlertDialog(Context context, String titleString, String message, final DialogInterface.OnClickListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) dialog.findViewById(R.id.txt_title);
        TextView messageview = (TextView) dialog.findViewById(R.id.txt_message);
        TextView left = (TextView) dialog.findViewById(R.id.btn_left);
        TextView right = (TextView) dialog.findViewById(R.id.btn_right);
        title.setText(titleString);
        messageview.setText(message);
        left.setVisibility(View.GONE);

        right.setText("OK");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener!= null)
                    listener.onClick(dialog, -1);
            }
        });
        dialog.show();
    }
    public static void showAlertDialog(Context context, String titleString, int messageId, final DialogInterface.OnClickListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) dialog.findViewById(R.id.txt_title);
        TextView messageview = (TextView) dialog.findViewById(R.id.txt_message);
        TextView left = (TextView) dialog.findViewById(R.id.btn_left);
        TextView right = (TextView) dialog.findViewById(R.id.btn_right);
        title.setText(titleString);
        messageview.setText(context.getString(messageId));
        left.setVisibility(View.GONE);

        right.setText("OK");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener!= null)
                    listener.onClick(dialog, -1);
            }
        });
        dialog.show();
    }
    public static void showAlertDialogForLocationPermission(final Context context, final DialogInterface.OnClickListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) dialog.findViewById(R.id.txt_title);
        TextView messageview = (TextView) dialog.findViewById(R.id.txt_message);
        TextView left = (TextView) dialog.findViewById(R.id.btn_left);
        TextView right = (TextView) dialog.findViewById(R.id.btn_right);
        title.setText("Warning");
        messageview.setText("Please turn On your location for WebRTCDemo.");

        right.setText("settings");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null){
                    listener.onClick(dialog, -1);
                }

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        left.setText("cancel");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null){
                    listener.onClick(dialog, -1);
                }
            }
        });
        dialog.show();
    }

    /*
     * Toast
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int messageId) {
        showToast(context, context.getString(messageId));
    }

    public static void showToast(Context context, String message, boolean isLong) {
        if (isLong) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            showToast(context, message);
        }
    }

    public static void showToast(Context context, int messageId, boolean isLong) {
        showToast(context, context.getString(messageId), isLong);
    }
}
