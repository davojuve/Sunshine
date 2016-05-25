package am.wedo.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Davit on 25-May-16. functions
 */
public class Utility {

    public static String getPreferredLocation(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                                    context.getString(R.string.pref_location_key),
                                    context.getString(R.string.pref_location_default)
                            );
    }
}
