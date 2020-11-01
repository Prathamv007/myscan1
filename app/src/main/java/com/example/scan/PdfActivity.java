package com.example.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.scan.fileView.FLAdapter;
import com.example.scan.fileView.FLViewHolder;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfActivity extends AppCompatActivity {

    PDFView pdfView;
    Integer pagenumber=0;
    String Pdffilename;
    int position= -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        
        init();
    }

    private void init() {
        pdfView=(PDFView)findViewById(R.id.pdfView);
position=getIntent().getIntExtra("position",-1);
displayFromsdcard();
    }
    private void displayFromsdcard(){
//pdfView.fromFile(new FLAdapter()){

        }
    }
