package com.noveogroup.preferences.sample.presentation.support;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by avaytsekhovskiy on 23/11/2017.
 */
public class SupportWatcher implements TextWatcher {
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
