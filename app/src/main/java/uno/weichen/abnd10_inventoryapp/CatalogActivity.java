package uno.weichen.abnd10_inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;

package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PET_LOADER = 0;
    PetCursorAdapter mPetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list_view_pet);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        /**
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(PET_LOADER, null, this);
        mPetAdapter = new PetCursorAdapter(this, null);
        // Attach cursor adapter to the ListView
        petListView.setAdapter(mPetAdapter);

        // Setup item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(withAppendedId(PetEntry.CONTENT_URI,id));
                startActivity(intent);
            }
        });
    }

/*    *//**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     *//*
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PET_BREED,
            ProductEntry.COLUMN_PET_GENDER,
            ProductEntry.COLUMN_PET_WEIGHT
        };

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        // Filter results WHERE "title" = 'My Title'
        //String selection = ProductEntry.COLUMN_PRODUCT_NAME + " = ?";
        //String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        //String sortOrder =
        //    ProductEntry.COLUMN_PRODUCT_NAME + " DESC";
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null, null, null);

        //Attaching the adapter to a ListView
        // Find ListView to populate
        ListView petItems = (ListView) findViewById(R.id.list_view_pet);
        // Setup cursor adapter using cursor from last step
        mPetAdapter = new PetCursorAdapter(this, cursor);
        // Attach cursor adapter to the ListView
        petItems.setAdapter(mPetAdapter);

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                //displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet() {
        //Define needed parameters;
        String title = "Toto";
        String breed = "Terrier";
        String photo = "";
        int gender = PetEntry.GENDER_FEMALE;
        int weight = 7;
        Uri mPetUri;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, title);
        values.put(PetEntry.COLUMN_PET_BREED, breed);
        values.put(PetEntry.COLUMN_PET_GENDER, gender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);
        values.put(PetEntry.COLUMN_PET_PHOTO, photo);

        mPetUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
        Log.v("catalogActivity", "New row ID" + parseId(mPetUri));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // displayDatabaseInfo();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
            PetEntry._ID,
            PetEntry.COLUMN_PET_NAME,
            PetEntry.COLUMN_PET_BREED,
            PetEntry.COLUMN_PET_PHOTO
        };

        return new CursorLoader(this, PetEntry.CONTENT_URI,
            projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPetAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPetAdapter.swapCursor(null);

    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteAllPet() {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }
}