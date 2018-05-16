package org.brohede.marcus.sqliteapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean ascending = false;
    private boolean descending = false;
    private List<Mountain> mountains = new ArrayList<>();
    private ArrayAdapter adapter;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;
    MountainReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new FetchData().execute();

        adapter = new ArrayAdapter(MainActivity.this, R.layout.view_items, R.id.my_text, mountains);
        ListView listView = (ListView) findViewById(R.id.my_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mountain m = mountains.get(position);
                Toast.makeText(MainActivity.this,m.info(),Toast.LENGTH_SHORT).show();
            }});

        mDbHelper = new MountainReaderDbHelper(this);
        readDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    public void readDB(){
        adapter.clear();

        dbR = mDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                MountainReaderContract.MountainEntry.COLUMN_NAME_NAME,
                MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION,
                MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME_NAME  + " DESC";
        if (descending) {
            sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT + " DESC";
        }
        else if (ascending) {
            sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME_NAME+ " ASC";
        }


        Cursor cursor = dbR.query(
                MountainReaderContract.MountainEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()) {
            String bergNamn = cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME));
            String bergPlats = cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION));
            int bergHojd = cursor.getInt(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT));
            //String bergImg = cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_IMGURL));
            //String bergInfo = cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_INFOURL));

            Mountain mq = new Mountain(bergNamn,bergPlats,bergHojd);
            adapter.add(mq);
        }
        cursor.close();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem checkable = menu.findItem(R.id.desc_order);
        //checkable.setChecked(isChecked);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.order_name:
                descending = false;
                ascending = true;
                readDB();
                return true;
            case R.id.order_height:
                descending = true;
                ascending = false;
                readDB();
                return true;
            case R.id.refresh:
                readDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //kod för at hämta json datta och spara i SQLlite
    private class FetchData extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            // These two variables need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a Java string.
            String jsonStr = null;

            try {
                // Construct the URL for the Internet service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);

            try {
                JSONArray json1 = new JSONArray(o);
                // Gets the data repository in write mode
                dbW = mDbHelper.getWritableDatabase();

                for(int i=0; i<json1.length();i++){
                    JSONObject berg = json1.getJSONObject(i);
                    String bergNamn = berg.getString("name");
                    //String bergTyp = berg.getString("type");
                    String bergPlats = berg.getString("location");
                    //String bergComp = berg.getString("company");
                    //int bergId = berg.getInt("ID");
                    //String bergCategory = berg.getString("category");
                    int bergSize = berg.getInt("size");
                    //int bergCost = berg.getInt("cost");
                    //String bergAux = berg.getString("auxdata");

                    ContentValues values = new ContentValues();
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME,bergNamn);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION,bergPlats);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT,bergSize);

                    dbW.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);
                }
            } catch (JSONException e) {
                Log.e("brom","E:"+e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}
