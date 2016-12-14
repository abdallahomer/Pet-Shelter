package com.abdallahomer12.pets;


import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // private PetDbHelper mDbHelper;
    PetCursorAdapter adapter;
    private static final int EXISTING_PET_LOADER = 1;
    private ListView l =null ;
    private View emptyView = null;

    private void showAllDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_All_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteAllPet();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        l = (ListView) findViewById(R.id.listview);
        emptyView = findViewById(R.id.empty_view);
        l.setEmptyView(emptyView);
        adapter = new PetCursorAdapter(this, null);
        l.setAdapter(adapter);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(CategoryActivity.this, EditorActivity.class);
                Uri petUri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
                i.setData(petUri);
                startActivity(i);

            }
        });

        getSupportLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {PetContract.PetEntry._ID
                , PetContract.PetEntry.COLUMN_PET_NAME
                , PetContract.PetEntry.COLUMN_PET_BREED};
        return new CursorLoader(this
                , PetContract.PetEntry.CONTENT_URI
                , projection
                , null
                , null
                , null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu_catalog) {
        super.onPrepareOptionsMenu(menu_catalog);
        if(l.getAdapter()==null){
            MenuItem menuItem = menu_catalog.findItem(R.id.action_delete_all_entries);
            menuItem.setVisible(false);
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                showAllDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPet() {
        int rowsDeleted = getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, null, null);
        Log.v("CategoryActivity", rowsDeleted + " rows deleted from pet database");
    }

}

