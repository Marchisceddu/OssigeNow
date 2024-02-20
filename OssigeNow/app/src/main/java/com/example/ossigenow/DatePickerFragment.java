package com.example.ossigenow;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private Calendar calendar;
    private DatePickerDialog datePicker;

    public static DatePickerFragment newInstance() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();

        datePickerFragment.setArguments(args);

        return datePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        datePicker = new DatePickerDialog(getActivity(), R.style.CustomDatePickerDialogTheme);
        calendar = Calendar.getInstance();

        return new AlertDialog.Builder(getActivity(),R.style.CustomDatePickerDialogTheme).setView(datePicker.getDatePicker())
                .setPositiveButton(R.string.conferma,
                        (DialogInterface.OnClickListener) (dialog, whichButton) -> {
                            calendar.set(Calendar.YEAR, datePicker.getDatePicker().getYear());
                            calendar.set(Calendar.MONTH, datePicker.getDatePicker().getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDatePicker().getDayOfMonth());
                            ((RegisterActivity) getActivity()).doPositiveClick(calendar);
                        }
                ).setNegativeButton(R.string.annulla,
                        (DialogInterface.OnClickListener) (dialog, whichButton) ->
                                ((RegisterActivity) getActivity()).doNegativeClick()
                )
                .create();
    }
}