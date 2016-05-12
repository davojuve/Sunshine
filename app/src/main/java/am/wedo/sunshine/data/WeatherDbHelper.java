package am.wedo.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Davit: DB helper for Sqlite database
 */
public class WeatherDbHelper extends SQLiteOpenHelper{

    // If we change the database schema, we must increment database version
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TODO: 12-May-16 create location table

        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + "(" +
                        WeatherContract.WeatherEntry._ID + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        WeatherContract.WeatherEntry.COLUMN_LOC_KEY + "INTEGER NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_DATETEXT + "TEXT NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + "TEXT NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + "INTEGER NOT NULL, " +

                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + "REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + "REAL NOT NULL, " +

                        WeatherContract.WeatherEntry.COLUMN_HUMIDITY + "REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_PRESSURE + "REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + "REAL NOT NULL, " +
                        WeatherContract.WeatherEntry.COLUMN_DEGREES + "REAL NOT NULL, " +

                        // Set up the location column as a foreign key to location table
                        " FOREIGN KEY (" + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                        WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), " +
                        // To assure the application has just one weather entry per day
                        // per location, it's created a UNIQUE constraint with REPLACE strategy
                        " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATETEXT + ", " +
                        WeatherContract.WeatherEntry.COLUMN_DATETEXT +") ON CONFLICT REPLACE);";

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
