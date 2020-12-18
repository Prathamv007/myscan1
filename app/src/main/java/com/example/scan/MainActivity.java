package com.example.scan;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scan.fileView.FLAdapter;
import com.example.scan.ocr.MainActivityOcr;
import com.example.scan.persistance.Document;
import com.example.scan.persistance.DocumentViewModel;
import com.example.scan.utils.DialogUtil;
import com.example.scan.utils.DialogUtilCallback;
import com.example.scan.utils.FileIOUtils;
import com.example.scan.utils.FileWritingCallback;
import com.example.scan.utils.PDFWriterUtil;
import com.example.scan.utils.PermissionUtil;
import com.example.scan.utils.UIUtil;
import com.google.android.material.navigation.NavigationView;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private FLAdapter fileAdapter;
    private final Context c = this;
    private List<Uri> scannedBitmaps = new ArrayList<>();

    private DocumentViewModel viewModel;
    private LinearLayout emptyLayout;

    private String searchText = "";
    LiveData<List<Document>> liveData;
    private int TAKE_PHOTO_CODE = 0;
    private static int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       dl = (DrawerLayout)findViewById(R.id.dl);
        t = new ActionBarDrawerToggle(this, dl,R.string.open, R.string.close);
t.setDrawerIndicatorEnabled(true);
       dl.addDrawerListener(t);
        t.syncState();
        NavigationView nav_view=(NavigationView)findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.Ocr:
                        Toast.makeText(MainActivity.this, "My Account",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.show_prefrences:
                        Toast.makeText(MainActivity.this, "My Cart",Toast.LENGTH_SHORT).show();break;
                    default:
                        return true;
                }


                return true;

            }
        });

 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.rw);

        UIUtil.setLightNavigationBar( recyclerView, this );
        PermissionUtil.ask(this);

        final String baseStorageDirectory =  getApplicationContext().getString( R.string.base_storage_path);
        FileIOUtils.mkdir( baseStorageDirectory );

        final String baseStagingDirectory =  getApplicationContext().getString( R.string.base_staging_path);
        FileIOUtils.mkdir( baseStagingDirectory );

        final String scanningTmpDirectory =  getApplicationContext().getString( R.string.base_scantmp_path);
        FileIOUtils.mkdir( scanningTmpDirectory );

        this.emptyLayout = findViewById(R.id.empty_list);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);

        fileAdapter = new FLAdapter(viewModel, this);
        recyclerView.setAdapter( fileAdapter );

        liveData = viewModel.getAllDocuments();
        liveData.observe(this, new Observer<List<Document>>() {
            @Override
            public void onChanged(@Nullable List<Document> documents) {

                if( documents.size() > 0 ){
                    emptyLayout.setVisibility(View.GONE);

                } else {
                    emptyLayout.setVisibility(View.VISIBLE);
                }

                fileAdapter.setData(documents);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return t.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    public void qrScan(MenuItem mi) {
        Toast.makeText(getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();
    }

    public void Ocr(MenuItem mi) {
        Toast.makeText(getApplicationContext(),"Working", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(this, MainActivityOcr.class);
    startActivity(intent);
        //dialog();
    }

    public void goToSearch(MenuItem mi) {
        Intent intent = new Intent(this, SearchableActivity.class);
        startActivityForResult(intent, 0);
    }


    public void goToPreferences(MenuItem mi) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 0);
    }

    public void dialog()
    {
        final CharSequence[] options = {"Take Photo", "choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(options[item].equals("Take Photo"))
                {
                    final String dir = android.os.Environment.getExternalStorageDirectory() + "/picFolder/";
                    File newdir = new File(dir);
                    newdir.mkdirs();

                    String file = dir + "temp.jpg";
                    File newfile = new File(file);
                    try {
                        newfile.createNewFile();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Uri outputFileUri = Uri.fromFile(newfile);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                }
                else if (options[item].equals("choose from Gallery"))
                {// from gallary
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),3);

                }
            else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void openCamera(View v){
        scannedBitmaps.clear();

        String stagingDirPath = getApplicationContext().getString( R.string.base_staging_path );
        String scanningTmpDirectory =  getApplicationContext().getString( R.string.base_scantmp_path);

        FileIOUtils.clearDirectory( stagingDirPath );
        FileIOUtils.clearDirectory( scanningTmpDirectory );


        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);

        //startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle());
    }

//    public void openGallery(View v){
//        scannedBitmaps.clear();
//
//        String stagingDirPath = getApplicationContext().getString( R.string.base_staging_path );
//        FileIOUtils.clearDirectory( stagingDirPath );
//
//        Intent intent = new Intent(this, ScanActivity.class);
//        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA);
//        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
//    }

    private void saveBitmap( final Bitmap bitmap, final boolean addMore ){

        final String baseDirectory =  getApplicationContext().getString( addMore ? R.string.base_staging_path : R.string.base_storage_path);
        final File sd = Environment.getExternalStorageDirectory();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        final String timestamp = simpleDateFormat.format( new Date() );

        if( addMore ){

            try {

                String filename = "SCANNED_STG_" + timestamp + ".png";

                FileIOUtils.writeFile(baseDirectory, filename, new FileWritingCallback() {
                    @Override
                    public void write(FileOutputStream out) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                });

                bitmap.recycle();
                System.gc();

            }catch(IOException ioe){
                ioe.printStackTrace();
            }

        } else {

            DialogUtil.askUserFilaname( c, null, null, new DialogUtilCallback() {

                @Override
                public void onSave(String textValue, String category) {

                    try {

                        final PDFWriterUtil pdfWriter = new PDFWriterUtil();

                        String stagingDirPath = getApplicationContext().getString( R.string.base_staging_path );

                        List<File> stagingFiles = FileIOUtils.getAllFiles( stagingDirPath );
                        for ( File stagedFile : stagingFiles ) {
                            pdfWriter.addFile( stagedFile );
                        }

                        pdfWriter.addBitmap(bitmap);

                        String filename = "SCANNED_" + timestamp + ".pdf";
                        FileIOUtils.writeFile( baseDirectory, filename, new FileWritingCallback() {
                            @Override
                            public void write(FileOutputStream out) {
                                try {
                                    pdfWriter.write(out);

                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        });


                        fileAdapter.notifyDataSetChanged();

                        FileIOUtils.clearDirectory( stagingDirPath );

                        SimpleDateFormat simpleDateFormatView = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                        final String timestampView = simpleDateFormatView.format(new Date());

                        Document newDocument = new Document();
                        newDocument.setName( textValue );
                        newDocument.setCategory( category );
                        newDocument.setPath( filename );
                        newDocument.setScanned( timestampView );
                        newDocument.setPageCount( pdfWriter.getPageCount() );
                        viewModel.saveDocument(newDocument);

                        pdfWriter.close();

                        bitmap.recycle();
                        System.gc();

                    }catch(IOException ioe){
                        ioe.printStackTrace();

                    }

                }
            });

        }
    }

    private void savePdf() {

        final String baseDirectory = getApplicationContext().getString(R.string.base_storage_path);
        final File sd = Environment.getExternalStorageDirectory();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        final String timestamp = simpleDateFormat.format(new Date());


        DialogUtil.askUserFilaname(c, null, null, new DialogUtilCallback() {

            @Override
            public void onSave(String textValue, String category) {
                try {

                    final PDFWriterUtil pdfWriter = new PDFWriterUtil();

                    String stagingDirPath = getApplicationContext().getString(R.string.base_staging_path);

                    List<File> stagingFiles = FileIOUtils.getAllFiles(stagingDirPath);
                    for (File stagedFile : stagingFiles) {
                        pdfWriter.addFile(stagedFile);
                    }

                    String filename = "mScan_" + timestamp + ".pdf";
                    FileIOUtils.writeFile(baseDirectory, filename, new FileWritingCallback() {
                        @Override
                        public void write(FileOutputStream out) {
                            try {
                                pdfWriter.write(out);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    fileAdapter.notifyDataSetChanged();

                    FileIOUtils.clearDirectory(stagingDirPath);

                    SimpleDateFormat simpleDateFormatView = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                    final String timestampView = simpleDateFormatView.format(new Date());

                    Document newDocument = new Document();
                    newDocument.setName(textValue);
                    newDocument.setCategory(category);
                    newDocument.setPath(filename);
                    newDocument.setScanned(timestampView);
                    newDocument.setPageCount(pdfWriter.getPageCount());
                    viewModel.saveDocument(newDocument);

                    pdfWriter.close();

                    System.gc();

                } catch (IOException ioe) {
                    ioe.printStackTrace();

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, MainActivityOcr.class);
            intent.putExtra("mFrom", "Camera");
            startActivity(intent);
        }

        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                       Intent intent = new Intent(this, MainActivityOcr.class);
                        intent.putExtra("GalleryData", data.getData().toString());
                        intent.putExtra("mFrom", "Gallery");
                        //intent.putExtra("GalleryData", byteArray);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if ( ( requestCode == ScanConstants.PICKFILE_REQUEST_CODE || requestCode == ScanConstants.START_CAMERA_REQUEST_CODE ) &&
                resultCode == Activity.RESULT_OK) {


            boolean saveMode = data.getExtras().containsKey(ScanConstants.SAVE_PDF) ? data.getExtras().getBoolean( ScanConstants.SAVE_PDF ) : Boolean.FALSE;
            if(saveMode){
                savePdf();

            } else {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                boolean doScanMore = data.getExtras().getBoolean(ScanConstants.SCAN_MORE);

                final File sd = Environment.getExternalStorageDirectory();
                File src = new File(sd, uri.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(src.getAbsolutePath());

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                saveBitmap(bitmap, doScanMore);

                if (doScanMore) {
                    scannedBitmaps.add(uri);
                    Intent intent = new Intent(this, MultiPageActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);

                    startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE);
                }

                //getContentResolver().delete(uri, null, null);
            }
        }
    }
}