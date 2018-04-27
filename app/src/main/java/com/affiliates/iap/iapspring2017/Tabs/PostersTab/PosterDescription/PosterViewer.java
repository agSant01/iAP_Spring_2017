package com.affiliates.iap.iapspring2017.Tabs.PostersTab.PosterDescription;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private File posterFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_viewer);
        bind();
        setToolBar();
        loadFile();
    }

    private void bind(){
        progressBar = (ProgressBar) findViewById((R.id.loading_progress));
        pdfView = (PDFView) findViewById(R.id.pdfView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster);
        download = (Button) findViewById(R.id.download_button);
        String posterName = getIntent().getStringExtra("name");
        mToolbar.setTitle(posterName);
        ((TextView) mToolbar.findViewById(R.id.title)).setText(posterName == null ? "Poster" : posterName);
        progressBar.setEnabled(true);
        progressBar.setVisibility(ProgressBar.VISIBLE);


    }

    private void loadFile(){
        DownloadFile df = null;
        final String url = getIntent().getStringExtra("url");
        String posterName = getIntent().getStringExtra("name")+".pdf";
        try {
            df = new DownloadFile(new URL(url), posterName);
            df.execute();
        } catch (MalformedURLException e) {
            Log.v("PDF", e.getMessage());
        }
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
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
        posterFile = new File(getFilesDir(), name);
        try {
            FileUtils.copyURLToFile(url, posterFile);
        } catch (IOException e) {
            Log.v("PDF", e.getMessage());
        }
        Log.v("PDF", name + " Downloaded in " + posterFile.getPath());
        return posterFile;
    }

    private class DownloadFile extends AsyncTask<Void, Void, Void>{
        URL url;
        File f;
        String name;
        DownloadFile(URL url, String name){
            super();
            this.url = url;
            this.name = name;
        }

        public File getFile(){
            return f;
        }

        @Override
        protected void onPostExecute(Void s) {
            displayFromFile(posterFile);
        }

        @Override
        protected Void doInBackground(Void... params) {
            download(url, name);
            return null;
        }
    }


}
