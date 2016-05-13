package am.wedo.sunshine;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.util.Log;

import am.wedo.sunshine.data.WeatherContract;
import am.wedo.sunshine.data.WeatherDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testCreateDb() throws Throwable{
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb(){
        // test data insert to db
        String testName = "North Pole";
        String testLocationSetting = "997545";
        double testLatitude = 64.752;
        double testLongitude = -145.256;

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create a new map of values
        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, testName);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);



        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);

        // verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d("test", "New row id: " + locationRowId);

        // Specify which columns you want
        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };

        // A cursor is your primary interface to the query results
        Cursor cursor = db.query(
                    WeatherContract.LocationEntry.TABLE_NAME,
                    columns,
                    null, // WHERE
                    null, // values for WHERE
                    null, // group by
                    null, // filter by
                    null  // sort order
                );

        if(cursor.moveToFirst()){
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
            Double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
            Double longitude = cursor.getDouble(longIndex);

            assertEquals(testName, name);
            assertEquals(testLocationSetting, location);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);


            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

            long weatherRowId;
            weatherRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);

            Cursor weatherCursor = db.query(
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    null, // just returns all columns
                    null, // WHERE
                    null, // values for WHERE
                    null, // group by
                    null, // filter by
                    null  // sort order
            );

            if(weatherCursor.moveToFirst()) {
                // Get the value in each column by finding the appropriate column index.
                int weatherIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
                String location = cursor.getString(locationIndex);

                int nameIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
                String name = cursor.getString(nameIndex);

                int latIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
                Double latitude = cursor.getDouble(latIndex);
            }

        }else{
            fail("No values returned :(");
        }


    }


}