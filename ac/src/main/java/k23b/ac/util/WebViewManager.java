package k23b.ac.util;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewManager {

    private static final String headerAsset = "header.html";
    private static final String footerAsset = "footer.html";

    private static final String htmlHeaderString;
    private static final String htmlFooterString;

    private static final XmlTreeConverter xmlTreeConverter = new XmlTreeConverter();

    static {

        htmlHeaderString = AssetManager.loadAsset(headerAsset);
        htmlFooterString = AssetManager.loadAsset(footerAsset);
    }

    private final WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    public WebViewManager(WebView webView) {
        super();

        this.webView = webView;

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setInitialScale(125);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
    }

    public void displayOutput(String output) {

        String content = xmlTreeConverter.stringForXml(output);

        String htmlString = htmlHeaderString + content + htmlFooterString;

        webView.loadDataWithBaseURL("file:///android_asset/", htmlString, "text/html", "utf-8", null);
    }

    public void clearOutput() {

        webView.loadUrl("about:blank");
    }

    public void expandAll() {

        webView.loadUrl("javascript:expandTree('tree')");
    }

    public void collapseAll() {

        webView.loadUrl("javascript:collapseTree('tree')");
    }
}
