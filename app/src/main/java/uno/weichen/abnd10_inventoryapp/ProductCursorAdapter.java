package uno.weichen.abnd10_inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static uno.weichen.abnd10_inventoryapp.data.ProductContract.ProductEntry;
import static uno.weichen.abnd10_inventoryapp.data.ProductProvider.LOG_TAG;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view that uses a {@link Cursor} of
 * pet data as its data source. This adapter knows how to create list items for each row of pet data
 * in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given list
     * item layout. For example, the name for the current pet can be set on the name TextView in the
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        /**
         * Define views
         */
        final TextView productNameTextView;
        final TextView productPriceTextView;
        final TextView productQuantityTextView;
        final TextView productSoldQuantityTextview;
        final TextView saleTextView;

        final int mRowId;

        final String PRICE = "Price: ";
        final String SOLD = "Sold: ";
        final String QUANTITY = "Quantity: ";

        // Find fields to populate in inflated template
        productNameTextView = (TextView) view.findViewById(R.id.item_name);
        productPriceTextView = (TextView) view.findViewById(R.id.item_price);
        productQuantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        productSoldQuantityTextview = (TextView) view.findViewById(R.id.item_sold_quantity);
        saleTextView = (TextView) view.findViewById(R.id.item_sale);

        // Extract properties from cursor
        String productNameString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        String productPriceString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        String productSoldQuantityString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY));
        String productRestockQuantityString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_RESTOCK_QUANTITY));

        // Calculate current quantity
        int productQuantityInt = Integer.parseInt(productRestockQuantityString) - Integer.parseInt(productSoldQuantityString);
        String productQuantityString = Integer.toString(productQuantityInt);

        // Populate fields with extracted properties
        productNameTextView.setText(productNameString);
        productPriceTextView.setText(PRICE+productPriceString);
        productQuantityTextView.setText(QUANTITY+productQuantityString);
        productSoldQuantityTextview.setText(SOLD+productSoldQuantityString);

        // Get the ID for the product row
        mRowId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

        //if quantity > 0
        //saleButton.setText("SALE");

        saleTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "sale TextView was pressed");
                int soldQuantity = Integer.parseInt(productSoldQuantityTextview.getText().toString().substring(SOLD.length()));
                int quantity = Integer.parseInt(productQuantityTextView.getText().toString().substring(QUANTITY.length()));
                int mRowsAffected = 0;
                if (quantity > 0) {
                    soldQuantity++;

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY, soldQuantity);
                    Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,
                        mRowId);
                    mRowsAffected = context.getContentResolver().update(currentProductUri, values,
                        null, null);
                }


                if (mRowsAffected != 0) {
                    productQuantityTextView.setText(Integer.toString(quantity));
                    productSoldQuantityTextview.setText(Integer.toString(soldQuantity));
                }
            }
        });
    }

}