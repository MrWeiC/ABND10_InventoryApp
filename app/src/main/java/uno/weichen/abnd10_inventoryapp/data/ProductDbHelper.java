package uno.weichen.abnd10_inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static uno.weichen.abnd10_inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by weichen on 10/16/16.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABSE_NAME = "inventory.db";


    public ProductDbHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
// Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
            + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_CONTACT + " TEXT, "
            + ProductEntry.COLUMN_PRODUCT_PHOTO + " TEXT, "
            + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_RESTOCK_QUANTITY + " INTEGER NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
