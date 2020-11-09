package com.example.scan.fileView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.scan.BuildConfig;
import  com.example.scan.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.scan.persistance.Document;
import com.example.scan.persistance.DocumentViewModel;
import com.example.scan.utils.DialogUtil;
import com.example.scan.utils.DialogUtilCallback;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FLAdapter extends RecyclerView.Adapter<FLViewHolder> {

    final Context context;
    protected ActionMode mActionMode;

    public boolean multiSelect = false;
    private List<Document> documentList = new ArrayList<>();
    public List<Document> selectedItems = new ArrayList<>();

    private DocumentViewModel viewModel;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            if( selectedItems.size() == 0 || selectedItems.size() == 1 ){
                MenuInflater inflater = mode.getMenuInflater();
                menu.clear();
                inflater.inflate(R.menu.single_select_menu, menu);
                mode.setTitle( "1 Selected" );
                return true;

            } else {
                MenuInflater inflater = mode.getMenuInflater();
                menu.clear();
                inflater.inflate(R.menu.multi_select_menu, menu);
                mode.setTitle( selectedItems.size() + " Selected" );
                return true;
            }

        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_delete:

                    for (Document documentItem  : selectedItems) {

                        final String baseDirectory =  context.getString(R.string.base_storage_path);
                        final File sd = Environment.getExternalStorageDirectory();

                        File toDelete = new File( sd, baseDirectory + documentItem.getPath() );
                        toDelete.delete();
                        viewModel.deleteDocument(documentItem);
                    }

                    mode.finish();
                    return true;

                case R.id.menu_edit:

                    final Document docToRename = selectedItems.get(0);
                    DialogUtil.askUserFilaname(context, docToRename.getName(), docToRename.getCategory(), new DialogUtilCallback() {

                        @Override
                        public void onSave(String textValue, String category) {

                            docToRename.setName( textValue );
                            docToRename.setCategory( category );
                            viewModel.updateDocument(docToRename);

                            Toast toast = Toast.makeText(context, "Renamed to " + textValue, Toast.LENGTH_SHORT);
                            toast.show();

                            notifyDataSetChanged();

                        }
                    });

                    mode.finish();
                    return true;




              /*  case R.id.menu_ocr:
                    final Document docToOcr = selectedItems.get(0);
                    final String baseDirectory =  context.getString(R.string.base_storage_path);
                    final File sd = Environment.getExternalStorageDirectory();
                    File toOcr = new File( sd, baseDirectory + docToOcr.getPath() );
                    Intent intent = new Intent( context, OCRActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString( OCRActivity.FILE_PATH, docToOcr.getPath()); //Your id
                    intent.putExtras(bundle); //Put your id to your next Intent
                    context.startActivity(intent);
                    mode.finish();
                    return true;*/

                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    public FLAdapter( DocumentViewModel viewModel, Context context ){
        this.viewModel = viewModel;
        this.context = context;
    }

    public void setData(List<Document> documents){
        this.documentList.clear();
        this.documentList.addAll( documents );
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FLViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate( R.layout.file_item_view, viewGroup, false );
        FLViewHolder viewHolder = new FLViewHolder(listItem, actionModeCallbacks, this );

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FLViewHolder viewHolder, int i) {
        viewHolder.setDocument( this.documentList.get(i) );
    }

    @Override
    public int getItemCount() {
        return this.documentList.size();
    }

    public void shareFile(File file) {
        //--did this bcoz of usinG file provider but got error java.lang.IllegalArgumentException: Missing android.support.FILE_PROVIDER_PATHS meta-data
       /* Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(), AUTHORITY, file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.setType("application.pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);*/
        File iconsStoragePath = Environment.getExternalStorageDirectory();
        final String selpath = iconsStoragePath.getAbsolutePath() + "/PDF Scanner/data/";
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri selectedUri = Uri.parse(selpath + "/" + file.getName());
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, selectedUri);
        context.startActivity(Intent.createChooser(intent, "Share File"));



//android.os.FileUriExposedException: file:///storage/emulated/0/PDF Scanner/data/SCANNED_01-11-2020_01-33-00.pdf exposed beyond app through ClipData.Item.getUri()
      /*  Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://" + file.getAbsolutePath()));

        context.startActivity(intentShareFile);*/
    }
   /* public void getRecipeFile(File toOpen) {

        PDFView pdfView = null;
        if (BuildConfig.DEBUG) {
            throw new AssertionError("Assertion failed");
        }
        pdfView.findViewById(R.id.pdfView);
        pdfView.fromFile(toOpen).load();
        //  return toOpen;
    }*/

   /* public void tempMethod(){
        Toast.makeText(context, "Share Method", Toast.LENGTH_SHORT).show();
    }*/

}