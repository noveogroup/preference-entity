package com.noveogroup.preferences.sample.ui;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        //do nothing
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        //do nothing
    }

    @Override
    public void afterTextChanged(final Editable s) {
        //do nothing
    }
}
