/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uno.weichen.abnd10_inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

import uno.weichen.abnd10_inventoryapp.data.ProductContract.ProductEntry;

import static uno.weichen.abnd10_inventoryapp.data.ProductProvider.LOG_TAG;


/**
 * Allows user to create a new product or edit an existing one.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * EditText field to enter the product's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the product's price
     */
    private EditText mPriceEditText;

    /**
     * TextView field to enter the product's quantity
     */
    private TextView mQuantityTextView;

    /**
     * TextView field to enter the product's sold quantity
     */
    private TextView mSoldQuantityTextView;

    /**
     * TextView field to enter the product's restock quantity
     */
    private TextView mRestockQuantityTextView;

    /**
     * EditText field to enter the product's supplier email
     */
    private EditText mContactEditText;

    /**
     * Button for Sale
     */
    private Button mSaleButton;

    /**
     * Button for Restock
     */
    private Button mRestockButton;

    /**
     * Button for Order
     */

    private Button mOrderButton;
    /**
     * Button for Delete
     */
    private Button mDeleteButton;

    /**
     * CONSTANT for PRODUCTION_ITEM_LOADER
     */
    private static final int PRODUCTION_ITEM_LOADER = 1;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentProductUri;

    /**
     * Boolean to check if user has change the default value
     */
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    /**
     * CONSTANT for PICK_IMAGE_REQUEST
     */
    private static final int PICK_IMAGE_REQUEST = 2;

    /**
     * Content URI for picking photo from gallery
     */
    private Uri mPhotoUri;

    /**
     * ImageView field to enter the product's photo
     */
    private ImageView mProductPhotoImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityTextView = (TextView) findViewById(R.id.textview_product_quantity);
        mContactEditText = (EditText) findViewById(R.id.edit_product_contact);
        mSoldQuantityTextView = (TextView) findViewById(R.id.textview_product_sold_quantity);
        mRestockQuantityTextView = (TextView) findViewById(R.id.textview_product_restock_quantity);
        mSaleButton = (Button) findViewById(R.id.button_sale_product);
        mRestockButton = (Button) findViewById(R.id.button_restock_product);
        mDeleteButton = (Button) findViewById(R.id.button_delete_product);
        mOrderButton = (Button) findViewById(R.id.button_order);

        mProductPhotoImageView = (ImageView) findViewById(R.id.image);

        // Use getIntent and getData to get the associated URI
        mCurrentProductUri = getIntent().getData();
        //Set title of DetailActivity on which situation we have
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
            mDeleteButton.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            /**
             * Initializes the CursorLoader. The URL_LOADER value is eventually passed
             * to onCreateLoader().
             */
            getSupportLoaderManager().initLoader(PRODUCTION_ITEM_LOADER, null, this);
        }

        // Setup onTouchListener for editors components.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mContactEditText.setOnTouchListener(mTouchListener);

        //Onclick Listener for Sale
        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String soldQuantityString = mSoldQuantityTextView.getText().toString().trim();
                String quantityString = mQuantityTextView.getText().toString().trim();
                int soldQuantityInt = Integer.parseInt(soldQuantityString);
                int quantityInt = Integer.parseInt(quantityString);
                if (quantityInt > 0) {
                    //if button clicked and quantity > 0
                    soldQuantityInt++;
                    quantityInt--;
                    soldQuantityString = Integer.toString(soldQuantityInt);
                    quantityString = Integer.toString(quantityInt);
                    mQuantityTextView.setText(quantityString);
                    mSoldQuantityTextView.setText(soldQuantityString);
                    //inform the user the product detail has been changed.
                    mProductHasChanged = true;
                }
            }
        });

        //Onclick Listener for Restock
        mRestockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restockQuantityString = mRestockQuantityTextView.getText().toString().trim();
                String quantityString = mQuantityTextView.getText().toString().trim();
                int restockQuantityInt = Integer.parseInt(restockQuantityString);
                int quantityInt = Integer.parseInt(quantityString);
                //if button clicked
                restockQuantityInt++;
                quantityInt++;
                restockQuantityString = Integer.toString(restockQuantityInt);
                quantityString = Integer.toString(quantityInt);
                mQuantityTextView.setText(quantityString);
                mRestockQuantityTextView.setText(restockQuantityString);
                //inform the user the product detail has been changed.
                mProductHasChanged = true;
            }
        });

        //Onclick Listener for Delete
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        //Onclick Listener for Order
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = "Restocking Request";
                String body = "Dear Supplier, \n"
                    + "We need more " + mNameEditText.getText().toString().trim() + ".\n"
                    + "Please contact us! \n";

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mContactEditText.getText().toString().trim()});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_detail.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save product to the database
                saveProduct();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Show delete Confirmation dialog
                showDeleteConfirmationDialog();

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpFromSameTask(DetailActivity.this);
                        }
                    };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String soldQuantityString = mSoldQuantityTextView.getText().toString().trim();
        String restockQuantityString = mRestockQuantityTextView.getText().toString().trim();
        String contactString = mContactEditText.getText().toString().trim();
        String photoString = "";

        // if there is a picture associated with the app.
        if (mPhotoUri != null) {
            photoString = mPhotoUri.toString();
        }

        // Nothing was input, we directly return and won't save the data into database.
        if (mCurrentProductUri == null &&
            TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(contactString)) {
            return;
        }

        //Check name, contact, price cannot be empty;
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(contactString)) {
            Toast.makeText(this, getString(R.string.editor_required_field_empty),
                Toast.LENGTH_SHORT).show();
            return;
        }

        //Check email address pattern
        if (!isEmailValid(contactString)) {
            Toast.makeText(this, getString(R.string.editor_required_valid_email),
                Toast.LENGTH_SHORT).show();
            return;
        }

        int priceInt = Integer.parseInt(priceString);
        int soldQuantityInt = Integer.parseInt(soldQuantityString);
        int restockQuantityInt = Integer.parseInt(restockQuantityString);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceInt);
        values.put(ProductEntry.COLUMN_PRODUCT_PHOTO, photoString);
        values.put(ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY, soldQuantityInt);
        values.put(ProductEntry.COLUMN_PRODUCT_RESTOCK_QUANTITY, restockQuantityInt);
        values.put(ProductEntry.COLUMN_PRODUCT_CONTACT, contactString);

        Uri mProductUri;
        //if new product mode
        if (mCurrentProductUri == null) {

            mProductUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (mProductUri != null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                    Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                    Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_RESTOCK_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_PHOTO,
            ProductEntry.COLUMN_PRODUCT_CONTACT
        };

        return new CursorLoader(this, mCurrentProductUri,
            projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String nameString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            String priceString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            String soldQuantityString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD_QUANTITY));
            String restockQuantityString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_RESTOCK_QUANTITY));
            String contactString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CONTACT));
            String photoUriString;

            // Calculate current quantity
            int productQuantityInt = Integer.parseInt(restockQuantityString) - Integer.parseInt(soldQuantityString);
            String productQuantityString = Integer.toString(productQuantityInt);

            mNameEditText.setText(nameString);
            mPriceEditText.setText(priceString);
            mQuantityTextView.setText(productQuantityString);
            mSoldQuantityTextView.setText(soldQuantityString);
            mRestockQuantityTextView.setText(restockQuantityString);
            mContactEditText.setText(contactString);
            photoUriString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PHOTO));
            if (!TextUtils.isEmpty(photoUriString)) {
                mProductPhotoImageView.setImageURI(Uri.parse(photoUriString));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityTextView.setText("0");
        mRestockQuantityTextView.setText("0");
        mSoldQuantityTextView.setText("0");
        mContactEditText.setText("");
        mProductPhotoImageView.setImageResource(0);
    }


    private void showUnsavedChangesDialog(
        DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // User clicked "Discard" button, close the current activity.
                    finish();
                }
            };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        int mRowsDeleted = 0;
        if (mCurrentProductUri != null) {

            mRowsDeleted = getContentResolver().delete(
                mCurrentProductUri,   // the user dictionary content URI
                null,                    // the column to select on
                null                      // the value to compare to
            );
        }
        if (mRowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, getString(R.string.get_bitmap_from_uri_exception), e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, getString(R.string.get_bitmap_from_uri_error));
            }
        }
    }

    public void openImageSelector(View view) {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mPhotoUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mPhotoUri.toString());
                mProductPhotoImageView.setImageBitmap(getBitmapFromUri(mPhotoUri));
            }
        }
    }

    /**
     * Validate Email address
     */
    public static boolean isEmailValid(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}