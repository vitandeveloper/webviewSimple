package protector.com.cmim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, AdvancedWebView.Listener {
    private FloatingActionButton fab;
    private AdvancedWebView mWebView;
    private ProgressBar progressBar;
    private ConstraintLayout ivError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        addView();
        creatWebView();
    }

    @SuppressLint("RestrictedApi")
    private void addView(){
        progressBar= findViewById(R.id.prg);

        mWebView = findViewById(R.id.webView);

        ivError = findViewById(R.id.ivError);

        fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.reload();
            }
        });
    }

    private void creatWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setInitialScale(1);

        mWebView.setListener(this, this);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onProgressChanged(WebView view, int progress) {
                fab.setVisibility(View.GONE);
            }
        });


        this.mWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(view.GONE);
                fab.setVisibility(View.GONE);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(view.GONE);
                fab.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handleURL(url);
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mWebView.loadUrl(getString(R.string.url_home));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);


        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    OnEverySecond();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();
    }

    public void handleURL(String url) {
        if (url.startsWith("tel:") || url.startsWith("geo:") || url.startsWith("mailto:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return;
        } else{
            mWebView.loadUrl(url);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_terms) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.url_terms)));
            startActivity(i);
        } else if (id == R.id.nav_privacy) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.url_privacy)));
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //region VALIDAR INTERNET
    public boolean IsNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.getState() == NetworkInfo.State.CONNECTED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void OnEverySecond() {
        runOnUiThread(new Runnable() {
            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                if (IsNetworkAvailable()) {
                    ivError.setVisibility(View.GONE);
                } else {
                    ivError.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    //endregion

    //region LISTENER WEBVIEW
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();

    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {
        mWebView.loadUrl(url);
    }
    //endregion
}
