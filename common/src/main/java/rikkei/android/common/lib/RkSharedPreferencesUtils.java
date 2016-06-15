package rikkei.android.common.lib;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by datpt2 on 5/5/2016.
 */
public class RkSharedPreferencesUtils {

    /**
     * Application SharedPreferences
     */
    private static SharedPreferences sAppSharedPreferences;

    /**
     * Instance of RkSharedPreferencesUtils Object
     */
    private static RkSharedPreferencesUtils sInstance;

    /**
     * Initialize SharedPreferences
     *
     * @param context
     */
    public static void initialize(Context context, int mode) {
        if (sAppSharedPreferences == null) {
            String name = context.getApplicationContext().getPackageName();
            sAppSharedPreferences = context.getApplicationContext()
                    .getSharedPreferences(name, mode);
        }
    }

    /**
     * Get instance of RkSharedPreferencesUtils Object
     *
     * @return Return an instance of RkSharedPreferencesUtils Object
     */
    public static RkSharedPreferencesUtils getInstance() {
        if (sInstance == null) {
            sInstance = new RkSharedPreferencesUtils();
        }
        return sInstance;
    }

    /**
     * Retrieve a string value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * Throws ClassCastException if there is a preference with this name
     * that is not a String.
     */
    public String getString(String key, String defValue) {
        return sAppSharedPreferences.getString(key, defValue);
    }

    /**
     * Retrieve a integer value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * Throws ClassCastException if there is a preference with this name
     * that is not an int.
     */
    public int getInt(String key, int defValue) {
        return sAppSharedPreferences.getInt(key, defValue);
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * Throws ClassCastException if there is a preference with this name
     * that is not a float.
     */
    public float getFloat(String key, float defValue) {
        return sAppSharedPreferences.getFloat(key, defValue);
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * Throws ClassCastException if there is a preference with this name
     * that is not a long.
     */
    public long getLong(String key, long defValue) {
        return sAppSharedPreferences.getLong(key, defValue);
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * Throws ClassCastException if there is a preference with this name
     * that is not a boolean.
     */
    public boolean getBoolean(String key, boolean defValue) {
        return sAppSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * Retrieve a set of String values from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference values if they exist, or defValues.
     * Throws ClassCastException if there is a preference with this name
     * that is not a Set.
     */
    public Set<String> getStringSet(String key, Set<String> defValue) {
        return sAppSharedPreferences.getStringSet(key, defValue);
    }

    /**
     * Set a String value in the preferences editor, to be written back once commit() or apply() are called.
     *
     * @param key   String: The name of the preference to modify.
     * @param value String: The new value for the preference.
     * @return Returns true if the new values were successfully written to persistent storage.
     */
    public boolean saveString(String key, String value) {
        return sAppSharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * Set an int value in the preferences editor, to be written back once commit() or apply() are called.
     *
     * @param key   String: The name of the preference to modify.
     * @param value int: The new value for the preference.
     * @return Returns true if the new values were successfully written to persistent storage.
     */
    public boolean saveInt(String key, int value) {
        return sAppSharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * Set a float value in the preferences editor, to be written back once commit() or apply() are called.
     *
     * @param key   String: The name of the preference to modify.
     * @param value float: The new value for the preference.
     * @return Returns true if the new values were successfully written to persistent storage.
     */
    public boolean saveFloat(String key, float value) {
        return sAppSharedPreferences.edit().putFloat(key, value).commit();
    }

    /**
     * Set a long value in the preferences editor, to be written back once commit() or apply() are called.
     *
     * @param key   String: The name of the preference to modify.
     * @param value long: The new value for the preference.
     * @return Returns true if the new values were successfully written to persistent storage.
     */
    public boolean saveLong(String key, long value) {
        return sAppSharedPreferences.edit().putLong(key, value).commit();
    }

    /**
     * Set a boolean value in the preferences editor, to be written back once commit() or apply() are called.
     *
     * @param key   String: The name of the preference to modify.
     * @param value boolean: The new value for the preference.
     * @return Returns true if the new values were successfully written to persistent storage.
     */
    public boolean saveBoolean(String key, boolean value) {
        return sAppSharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * Set a set of String values in the preferences editor, to be written back once commit() or apply() is called.
     *
     * @param key   String: The name of the preference to modify.
     * @param value Set: The set of new values for the preference. Passing null for this argument is equivalent to
     *              calling remove(String) with this key.
     * @return Returns true if the new values were successfully written to persistent storage.
     */
    public boolean saveStringSet(String key, Set<String> value) {
        return sAppSharedPreferences.edit().putStringSet(key, value).commit();
    }

    /**
     * Clear all data in app sharedPreferences
     *
     * @return Return true if clear data successfully
     */
    public boolean clear() {
        return sAppSharedPreferences.edit().clear().commit();
    }

    /**
     * Mark in the editor that a preference value should be removed, which
     * will be done in the actual preferences once {@link #commit} is
     * called.
     * <p/>
     * <p>Note that when committing back to the preferences, all removals
     * are done first, regardless of whether you called remove before
     * or after put methods on this editor.
     *
     * @param key The name of the preference to remove.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    public boolean remove(String key) {
        return sAppSharedPreferences.edit().remove(key).commit();
    }
}
