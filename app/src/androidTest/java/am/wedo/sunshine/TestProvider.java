package am.wedo.sunshine;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    public TestProvider() {
        super(Application.class);
    }

    public void testDeleteDb() throws Throwable{
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testGetType(){
        // content://am.wedo.sunshine/weather
        String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/am.wedo.sunshine/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://am.wedo.sunshine/weather/94074
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/am.wedo.sunshine/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://am.wedo.sunshine/weather/94074/20140612
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
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

    public void testInsertReadDb(){
        // test data insert to db
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create a new map of values
        ContentValues values = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);

        // verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d("test", "New row id: " + locationRowId);

        // A cursor is your primary interface to the query results
        Cursor cursor = db.query(
                    WeatherContract.LocationEntry.TABLE_NAME,
                    null, // columns
                    null, // WHERE
                    null, // values for WHERE
                    null, // group by
                    null, // filter by
                    null  // sort order
                );

        if(cursor.moveToFirst()){
           validateCursor(values, cursor);

            ContentValues weatherValues = getWeatherContentValues(locationRowId);

            long weatherRowId;
            weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
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
                validateCursor(weatherValues, weatherCursor);
            }else{
                fail("No weather data returned!");
            }

        }else{
            fail("No values returned :(");
        }

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

}