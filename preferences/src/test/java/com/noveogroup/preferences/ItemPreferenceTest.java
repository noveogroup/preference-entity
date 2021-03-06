package com.noveogroup.preferences;

import com.noveogroup.preferences.api.Preference;
import com.noveogroup.preferences.guava.Optional;
import com.noveogroup.preferences.lambda.Consumer;
import com.noveogroup.preferences.mock.TestSharedPreferences;
import com.noveogroup.preferences.param.Constants;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("PMD.AvoidFinalLocalVariable")
public class ItemPreferenceTest {

    private NoveoPreferences noveoPreferencesWrapper;
    private TestSharedPreferences preferences;

    @Before
    public void before() {
        preferences = new TestSharedPreferences();
        preferences.edit()
                .putBoolean("boolean", Constants.VALUE_BOOL)
                .putLong("long", Constants.VALUE_LONG)
                .putString("string", Constants.VALUE_STRING)
                .putInt("int", Constants.VALUE_INT)
                .putFloat("float", Constants.VALUE_FLOAT)
                .apply();

        noveoPreferencesWrapper = new NoveoPreferences(preferences);
    }

    @Test
    public void constructor() {
        final Preference<String> stringEntity = new ItemPreference<>(Constants.KEY_STRING, PreferenceStrategy.STRING, preferences);
        final Preference<String> stringEntity2 = noveoPreferencesWrapper.getString(Constants.KEY_STRING, null);

        assertEquals("constructor and static method", stringEntity.read().get(), stringEntity2.read().get());
    }

    @Test
    public void read() throws IOException {
        assertEquals("read boolean",
                noveoPreferencesWrapper.getBoolean(Constants.KEY_BOOL).read().get(),
                preferences.getBoolean("boolean", false));
        assertEquals("read long",
                noveoPreferencesWrapper.getLong(Constants.KEY_LONG).read().get().longValue(),
                preferences.getLong("long", 0L));
        assertEquals("read string",
                noveoPreferencesWrapper.getString(Constants.KEY_STRING).read().get(),
                preferences.getString("string", "none"));
        assertEquals("read int",
                noveoPreferencesWrapper.getInt(Constants.KEY_INT).read().get().intValue(),
                preferences.getInt("int", 0));
        assertEquals("read float",
                noveoPreferencesWrapper.getFloat(Constants.KEY_FLOAT).read().get(),
                preferences.getFloat("float", 0f), 1f);
    }

    @Test
    public void remove() throws IOException {
        noveoPreferencesWrapper.getBoolean(Constants.KEY_BOOL).remove();
        noveoPreferencesWrapper.getLong(Constants.KEY_LONG).remove();
        noveoPreferencesWrapper.getString(Constants.KEY_STRING).remove();
        noveoPreferencesWrapper.getInt(Constants.KEY_INT).remove();
        noveoPreferencesWrapper.getFloat(Constants.KEY_FLOAT).remove();

        for (final String key : preferences.getAll().keySet()) {
            assertFalse("remove(" + key + ")", preferences.contains(key));
        }
    }

    @Test
    public void save() throws IOException {
        final long valueLong = 10234L;
        final String valueString = "local string";
        final int valueInt = 563;
        final float valueFloat = 2345.235f;
        final boolean valueBool = true;

        final float defaultFloat = 2222f;
        final boolean defaultBoolean = false;
        final long defaultLong = 0L;
        final int defaultInt = 0;
        final float delta = 1f;

        noveoPreferencesWrapper.getBoolean(Constants.KEY_BOOL).save(valueBool);
        noveoPreferencesWrapper.getLong(Constants.KEY_LONG).save(valueLong);
        noveoPreferencesWrapper.getString(Constants.KEY_STRING).save(valueString);
        noveoPreferencesWrapper.getInt(Constants.KEY_INT).save(valueInt);
        noveoPreferencesWrapper.getFloat(Constants.KEY_FLOAT).save(valueFloat);

        assertEquals("save boolean",
                valueBool, preferences.getBoolean("boolean", defaultBoolean));
        assertEquals("save long",
                valueLong, preferences.getLong("long", defaultLong));
        final String defaultString = "none";
        assertEquals("save string",
                valueString, preferences.getString("string", defaultString));
        assertEquals("save int",
                valueInt, preferences.getInt("int", defaultInt));
        assertEquals("save float",
                valueFloat, preferences.getFloat("float", defaultFloat), delta);
    }

    @Test
    public void provider() throws IOException {
        final long firstValue = 1L;
        final long secondValue = 2L;
        final long defaultValue = 2341234L;
        final Preference<Long> simple = noveoPreferencesWrapper.getLong(Constants.KEY_LONG, defaultValue);
        assertNotNull("simple not null", simple);

        final Consumer<Optional<Long>> firstValueListener = longOptional ->
                assertEquals("provider got first value", longOptional.get().longValue(), firstValue);
        final Consumer<Optional<Long>> secondValueListener = longOptional ->
                assertEquals("provider got second value", longOptional.get().longValue(), secondValue);
        final Consumer<Optional<Long>> defaultValueListener = longOptional ->
                assertEquals("provider got default value", longOptional.get().longValue(), defaultValue);

        simple.provider().addListener(firstValueListener);
        simple.save(firstValue);
        simple.provider().removeListener(firstValueListener);

        simple.provider().addListener(defaultValueListener);
        simple.remove();
        simple.provider().removeListener(defaultValueListener);

        simple.provider().addListener(secondValueListener);
        simple.save(secondValue);
        simple.provider().removeListener(secondValueListener);
    }

    @Test(expected = IOException.class)
    public void saveBrokenFs() throws IOException {
        final long newValue = 10L;
        preferences.setBadFileSystem();
        noveoPreferencesWrapper.getLong(Constants.KEY_LONG).save(newValue);
    }

    @Test(expected = IOException.class)
    public void removeBrokenFs() throws IOException {
        preferences.setBadFileSystem();
        noveoPreferencesWrapper.getLong(Constants.KEY_LONG).remove();
    }

    @Test
    public void readBrokenFs() throws IOException {
        preferences.setBadFileSystem();
        assertEquals("broken fs not affects reading",
                noveoPreferencesWrapper.getLong(Constants.KEY_LONG).read().get().longValue(),
                Constants.VALUE_LONG);
    }

}
