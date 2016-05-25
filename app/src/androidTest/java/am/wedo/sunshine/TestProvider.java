package am.wedo.sunshine;

import android.app.Application;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import am.wedo.sunshine.data.WeatherContract;
import am.wedo.sunshine.data.WeatherDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class TestProvider extends ApplicationTestCase<Application> {

    static public String TEST_CITY_NAME = "North Pole";
    static public String TEST_LOCATION = "997545";
    static public String TEST_DATE = "20141205";

    public TestProvider() {
        super(Application.class);
    }

    // brings our database back to an empty state
    public void testDeleteAllRecords(){
        mContext.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(WeatherContract.LocationEntry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(cursor.getCount(), 0);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(cursor.getCount(), 0);
        cursor.close();
    }

    public void testGetType(){
        // content://am.wedo.sunshine/weather
        String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/am.wedo.sunshine/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        // content://am.wedo.sunshine/weather/94074
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(TEST_LOCATION));
        // vnd.android.cursor.dir/am.wedo.sunshine/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        // content://am.wedo.sunshine/weather/94074/20140612
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE));
        // vnd.android.cursor.dir/am.wedo.sunshine/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://am.wedo.sunshine/location
        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/am.wedo.sunshine/location
        assertEquals(WeatherContract.LocationEntry.CONTENT_TYPE, type);

        // content://am.wedo.sunshine/location/1
        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.buildLocationUri(1L));
        assertEquals(WeatherContract.LocationEntry.CONTENT_ITEM_TYPE, type);
    }


    public void testInsertReadProvider(){
        // test data insert to db
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create a new map of values
        ContentValues values = getLocationContentValues();

//        long locationRowId;
//        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
        // verify we got a row back.
//        assertTrue(locationRowId != -1);

        Uri insertUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(insertUri);

        // A cursor is your primary interface to the query results
        Cursor cursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.buildLocationUri(locationRowId),
                                                                    null,
                                                                    null, // WHERE
                                                                    null, // values for WHERE
                                                                    null  // sort order
                                                            );

        if(cursor.moveToFirst()){
           validateCursor(values, cursor);

            ContentValues weatherValues = getWeatherContentValues(locationRowId);
//            long weatherRowId;
//            weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
//            assertTrue(weatherRowId != -1);

            insertUri = mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);
            long weatherRowId = ContentUris.parseId(insertUri);

            Cursor weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI,
                                                                            null, // columns
                                                                            null, // WHERE
                                                                            null, // values for WHERE
                                                                            null  // sort order
                                                                        );

            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            }else{
                fail("No weather data returned!");
            }

            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().
                                        query(WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                                                null, // columns
                                                null, // WHERE
                                                null, // values for WHERE
                                                null  // sort order
                                        );

            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            }else{
                fail("data mismatched!");
            }

            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().
                    query(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                            null, // columns
                            null, // WHERE
                            null, // values for WHERE
                            null  // sort order
                    );

            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            }else{
                fail("data mismatched!");
            }

        }else{
            fail("No values returned :(");
        }

        testDeleteAllRecords();
    }

    private ContentValues getWeatherContentValues(long locationRowId) {
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
        return weatherValues;
    }

    public static void validateCursor(ContentValues expectedValues, Cursor valueCursor){
        Set< Map.Entry<String,Object> > valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);

            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }

    private ContentValues getLocationContentValues() {
        ContentValues values = new ContentValues();
        String testLocationSetting = "997545";
        double testLatitude = 64.752;
        double testLongitude = -145.256;
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);

        return values;
    }

    public void testUpdateLocation(){
        testDeleteAllRecords();

        // Create a new map of values, where column names are the keys
        ContentValues values = getLocationContentValues();

        Uri locationUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // verify we got row back
        assertTrue(locationRowId != -1);
        Log.d("test", "locationRowId: " + locationRowId);

        ContentValues values2 = new ContentValues(values);
        values2.put(WeatherContract.LocationEntry._ID, locationRowId);
        values2.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Yerevan");

        int count = mContext.getContentResolver().update(WeatherContract.LocationEntry.CONTENT_URI,
                                                            values2,
                                                            WeatherContract.LocationEntry._ID + " = ? ",
                                                            new String[]{String.valueOf(locationRowId)}
        );

        assertEquals(count, 1);

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            if(cursor.moveToFirst()){
                validateCursor(values2, cursor);
            }
            cursor.close();
        }

    }
}