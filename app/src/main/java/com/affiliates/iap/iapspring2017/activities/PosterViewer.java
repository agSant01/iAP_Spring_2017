package com.affiliates.iap.iapspring2017.activities;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PosterViewer extends AppCompatActivity implements OnLoadCompleteListener, OnPageChangeListener{
    private PDFView pdfView;
    private Button download;
    private Toolbar mToolbar;
    private ProgressBar progressBar;
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE = 42042;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_viewer);
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{READ_EXTERNAL_STORAGE},
                    PERMISSION_CODE
            );

        }
        bind();
        setToolBar();
    }

    private void bind(){
        progressBar = (ProgressBar) findViewById((R.id.loading_progress));
        pdfView = (PDFView) findViewById(R.id.pdfView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster);
        download = (Button) findViewById(R.id.download_button);
        DownloadFile df = null;
        String url = getIntent().getStringExtra("url");
        String posterName = getIntent().getStringExtra("name");
        mToolbar.setTitle(posterName);
        ((TextView) mToolbar.findViewById(R.id.title)).setText("Poster");
        File f = new File(Environment.getExternalStoragePublicDirectory("pdf"), posterName);
        progressBar.setEnabled(true);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if(f.exists()) {
            displayFromFile(f);
            download.setEnabled(false);
            download.setBackgroundResource(R.drawable.button_oval_shape_grey);
        }
        else {
            try {
                df = new DownloadFile(new URL(url), "iapLast");
                df.execute();
            } catch (MalformedURLException e) {
                Log.v("PDF", e.getMessage());
            }
        }
        final String name = posterName;
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DownloadFile df = new DownloadFile(new URL(getIntent().getStringExtra("url")), name);
                    df.execute();
                    Toast.makeText(getBaseContext(), "Downloading", Toast.LENGTH_SHORT).show();
                    ((Button) findViewById(R.id.download_button)).setEnabled(false);
                    download.setBackgroundResource(R.drawable.button_oval_shape_grey);
                } catch (MalformedURLException e) {
                    Log.v("PDF", e.getMessage());
                }
            }
        });

    }

    private void setToolBar(){
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void displayFromFile(File file){
        pdfView.fromFile(file).scrollHandle(new DefaultScrollHandle(this)).load();
        pdfView.useBestQuality(true);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }
    @Override
    public void loadComplete(int nbPages) {
        Log.v("PDF", "Loaded");

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    public File download(URL url, String name){
        File f = new File(Environment.getExternalStoragePublicDirectory("pdf"), name);
        try {
            FileUtils.copyURLToFile(url, f);
        } catch (IOException e) {
            Log.v("PDF", e.getMessage());
        }
        Log.v("PDF", name + " Downloaded in " + f.getPath());
        return f;
    }

    private class DownloadFile extends AsyncTask<String, String, String>{
        URL url;
        File f;
        String name;
        DownloadFile(URL url, String name){
            super();
            this.url = url;
            this.name = name;
        }

        @Override
        protected String doInBackground(String... params) {
           f = download(url, name);
            return "Downloaded";

        }

        public File getFile(){
            return f;
        }

        @Override
        protected void onPostExecute(String s) {
            if(!name.contains("iapLast")){
                Toast.makeText(getBaseContext(), "Downloaded", Toast.LENGTH_SHORT).show();
            }
            else{
                displayFromFile(f);
            }
        }
    }


}
