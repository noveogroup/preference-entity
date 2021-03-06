package com.noveogroup.preferences;

import android.content.SharedPreferences;

import com.noveogroup.preferences.api.Preference;
import com.noveogroup.preferences.api.PreferenceProvider;
import com.noveogroup.preferences.guava.Optional;
import com.noveogroup.preferences.lambda.Consumer;
import com.noveogroup.preferences.lambda.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by avaytsekhovskiy on 23/11/2017.
 */
class NoveoPreferenceProvider<T> implements PreferenceProvider<T> {

    private final Preference<T> entity;
    private final SharedPreferences preferences;

    private final Map<Consumer, SharedPreferences.OnSharedPreferenceChangeListener> changeListeners = new HashMap<>();
    private final Function<String, Boolean> filter;

    NoveoPreferenceProvider(final Preference<T> entity, final SharedPreferences preferences, final Function<String, Boolean> filter) {
        this.entity = entity;
        this.preferences = preferences;
        this.filter = filter;
    }

    @Override
    public Consumer<Optional<T>> addListener(final Consumer<Optional<T>> changeListener) {
        final SharedPreferences.OnSharedPreferenceChangeListener listener =
                (sharedPreferences, changedKey) -> notifyListener(changeListener, changedKey);
        changeListeners.put(changeListener, listener);
        preferences.registerOnSharedPreferenceChangeListener(listener);
        return changeListener;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void notifyListener(final Consumer<Optional<T>> changeListener, final String changedKey) {
        try {
            if (filter.apply(changedKey)) {
                changeListener.accept(entity.read());
            }
        } catch (final Exception original) {
            Utils.sneakyThrow(original);
        }
    }

    @Override
    public void removeListener(final Consumer<Optional<T>> changeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(changeListeners.remove(changeListener));
    }

}
