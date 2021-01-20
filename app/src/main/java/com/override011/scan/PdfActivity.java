package com.override011.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class PdfActivity extends AppCompatActivity {

    PDFView pdfView;
    Integer pagenumber=0;
    String Pdffilename;
    int position= -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        pdfView= findViewById(R.id.pdfView);
       // File file=FLAdapter.class;
      //  pdfView.fromFile(file)

     //  pdfView.fromFile(fileList(F))
         //      load();
         /*   File iconsStoragePath = Environment.getExternalStorageDirectory();
            final String selpath = iconsStoragePath.getAbsolutePath() + "/PDF Scanner/data/";
            Uri selectedUri = Uri.parse(selpath + "/" + file.getName());
        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromUri(file).load();}*/
/*
        public void openit(file)
        File iconsStoragePath = Environment.getExternalStorageDirectory();
        final String selpath = iconsStoragePath.getAbsolutePath() + "/PDF Scanner/data/";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri selectedUri = Uri.parse(selpath + "/" + file.getName());
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, selectedUri);
        //context.startActivity(Intent.createChooser(intent, "Share File"));
        pdfView.fromFile(file)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)*/
                // allows to draw something on the current page, usually visible in the middle of the screen
              /*  .onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                .onDrawAll(onDrawListener)
                .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
                .onPageChange(onPageChangeListener)
                .onPageScroll(onPageScrollListener)
                .onError(onErrorListener)
                .onPageError(onPageErrorListener)
                .onRender(onRenderListener) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                .onTap(onTapListener)
                .onLongPress(onLongPressListener)*/
                //.enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
            /*    .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)*/
                /*autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
                .linkHandler(DefaultLinkHandler)
                .pageFitPolicy(FitPolicy.WIDTH) */// mode to fit pages in the view
               // .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
               // .pageSnap(false) // snap pages to screen boundaries
                //.pageFling(false) // make a fling change only a single page like ViewPager
                //.nightMode(false) // toggle night mode
                //.load();
        //init();


    /*private void init() {
        pdfView=(PDFView)findViewById(R.id.pdfView);
position=getIntent().getIntExtra("position",-1);
displayFromsdcard();
    }
    private void displayFromsdcard(){
//pdfView.fromFile(new FLAdapter()){

        }*/
    }}
