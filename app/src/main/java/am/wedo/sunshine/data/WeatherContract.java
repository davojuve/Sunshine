package am.wedo.sunshine.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Davit: Store Database columns
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "am.wedo.sunshine";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // possible paths
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    private static final String DATE_FORMAT = "yyyyMMdd";

    /** Inner class that defines the table content of the location table */
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // table name
        public static final String TABLE_NAME = "location";

        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // Human readable location string, provided by the API. Because for styling,
        // "Mountain View" is more recognizable than 94043
        public static final String COLUMN_CITY_NAME = "city_name";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(Long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    /** Inner class that defines the table content of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        /*
         Short description and long description of the weather, as provided by API
         e.g "clear" vs "sky is clear"
         */
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and Max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Pressure is stored as a float
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south). Stored as a float
        public static final String COLUMN_DEGREES = "degrees";



        public static Uri buildWeatherUri(Long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(String locationSetting, String startDate){
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, String date){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri){
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }


    }

    public static String getDbDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

}
