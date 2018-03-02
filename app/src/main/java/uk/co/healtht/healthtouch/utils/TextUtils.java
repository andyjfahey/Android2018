package uk.co.healtht.healthtouch.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Patterns;

import uk.co.healtht.healthtouch.R;

public class TextUtils {
    public static boolean isEmpty(CharSequence str) {
        return android.text.TextUtils.isEmpty(str) || android.text.TextUtils.getTrimmedLength(str) == 0;
    }

    public static int getTrimmedLength(CharSequence str) {
        return android.text.TextUtils.isEmpty(str) ? 0 : android.text.TextUtils.getTrimmedLength(str);
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String join(String sep, String... args) {
        StringBuilder res = new StringBuilder();
        for (String str : args) {
            if (!isEmpty(str)) {
                if (res.length() > 0) {
                    res.append(sep);
                }
                res.append(str);
            }
        }

        return res.toString();
    }

    public static void showDateAlertDialog(Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(ctx.getString(R.string.date_not_in_past))
                .setCancelable(false)
                .setNegativeButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {dialog.cancel(); }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showStartDateAlertDialog(Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage(ctx.getString(R.string.specify_start_date))
                .setCancelable(false)
                .setNegativeButton(ctx.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showStartBiggerThanEnd(Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage(ctx.getString(R.string.start_bigger_end_date_error))
                .setCancelable(false)
                .setNegativeButton(ctx.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showFillFieldsMessage(Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage(ctx.getString(R.string.fields_empty))
                .setCancelable(false)
                .setNegativeButton(ctx.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showMoreThanWeekMessage(Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage(ctx.getString(R.string.less_than_week))
                .setCancelable(false)
                .setNegativeButton(ctx.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showMessage(String s, Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage(s)
                .setCancelable(false)
                .setNegativeButton(ctx.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showErrorMessage(Context ctx, int message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage(ctx.getString(message))
                .setCancelable(false)
                .setNegativeButton(ctx.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void showFabryWorkflow(Context ctx, String trackerName, DialogInterface.OnClickListener NoSymptomsClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage("Do you have any " + trackerName +  " symptoms?")
                //.setCancelable(false)
                .setPositiveButton("No", NoSymptomsClick)
                .setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public static void showDoseTakenWorkflow(Context ctx, String medicationName, DialogInterface.OnClickListener NoDoseTaken, DialogInterface.OnClickListener YesDoseTaken){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder
                .setMessage("Have you taken your dose of " + medicationName +  " ?")
                //.setCancelable(false)
                .setPositiveButton("No", NoDoseTaken)
                .setNegativeButton("Yes",YesDoseTaken);
        AlertDialog alert = builder.create();
        alert.show();

    }
}
