package co.kr.myfitnote.webview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import co.kr.myfitnote.R;

public class WebviewActivity extends AppCompatActivity {

    private static final String TAG = "WebviewActivity";

    private WebView webview;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        intent = getIntent();

        String url = intent.getStringExtra("url");

        webview = findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                /**
                 *  파일을 다운로드 할 때 해당 콜백이 호출
                 *  s : 다운로드 URL
                 *  s1 : User-Agent
                 *  s2 : Content-Disposition
                 *  s3 : MIME Type
                 *  l : Content-Length
                 */

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "report.jpg");

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);

            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
    }

}