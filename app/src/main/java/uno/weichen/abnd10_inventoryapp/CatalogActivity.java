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


import uno.weichen.abnd10_inventoryapp.data.ProductContract.ProductEntry;


/**
 * Displays list of products that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open DetailActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView productListView = (ListView) findViewById(R.id.list_view_product);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        /**
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        mProductAdapter = new ProductCursorAdapter(this, null);
        // Attach cursor adapter to the ListView
        productListView.setAdapter(mProductAdapter);

        // Setup item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
                intent.setData(withAppendedId(ProductEntry.CONTENT_URI,id));
                startActivity(intent);
            }
        });
    }

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
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {
        //Define needed parameters;
        String title = "Google Pixel";
        int price = 649;
        String photo = "";
        int sold = 20;
        int restock = 30;
        String contact = "info@weichen.uno";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, title);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_PHOTO, photo);
        values.put(ProductEntry.COLUMN_PRODUCT_RESTOCK_QUANTITY, restock);
        values.put(ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY, sold);
        values.put(ProductEntry.COLUMN_PRODUCT_CONTACT, contact);

        Uri mProductUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        Log.v("catalogActivity", "New row ID" + parseId(mProductUri));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_RESTOCK_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_PRICE
        };

        return new CursorLoader(this, ProductEntry.CONTENT_URI,
            projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductAdapter.swapCursor(null);

    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteAllProduct() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from product database");
    }


}