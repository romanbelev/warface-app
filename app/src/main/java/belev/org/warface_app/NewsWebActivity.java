package belev.org.warface_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import belev.org.warface_app.R;

public class NewsWebActivity extends AppCompatActivity {

    private static final String BUNDLE_TEXT = "bundle_text";
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String text = intent.getStringExtra("BUNDLE_TEXT");

        //Bundle bundle = getIntent().getExtras();
        //String json = bundle.getString("BUNDLE_TEXT");
        //NewsModel movieModel = new Gson().fromJson(json, NewsModel.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.menu_news));
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setBackgroundColor(0);
        //webView.loadUrl("http://belev.org/android/wardocwarface/maps/spec_vulkan.html");
        //webView.loadUrl(link);
        webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
