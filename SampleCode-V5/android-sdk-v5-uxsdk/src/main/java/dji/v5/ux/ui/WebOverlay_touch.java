package dji.v5.ux.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;

public class WebOverlay_touch extends WebView {
    public WebOverlay_touch(Context context) {
        super(context);
        initView(context);
    }

    public WebOverlay_touch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";

    private void initView(Context context){
        // i am not sure with these inflater lines
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // you should not use a new instance of MyWebView here
        // MyWebView view = (MyWebView) inflater.inflate(R.layout.custom_webview, this);
        this.getSettings().setJavaScriptEnabled(true) ;
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setDomStorageEnabled(true);
        this.setBackgroundColor(0x00000000);
        //this.getSettings().setTextZoom(70);
        //example();
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setBuiltInZoomControls(false);
        // zoom if you want
        //this.getSettings().setSupportZoom(true);
        // extra settings
        /*
        this.setScrollContainer(true);
        this.setWebViewClient(new WebViewClient());
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setSupportZoom(true);
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setDisplayZoomControls(false);

        String ua = this.getSettings().getUserAgentString();
        //String androidOSString = this.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
        //String newUserAgent = this.getSettings().getUserAgentString().replace(androidOSString, "(X11; Linux x86_64)");
        this.getSettings().setUserAgentString(DESKTOP_USER_AGENT);


        //this.clearCache(true);
        //this.setInitialScale(10);
        //this.setScaleX(10);
        //this.setScaleY(10);
*/

    }


    private void example() {
        String transparent = "<html>\n" +
                "<head>\n" +
                "    <style type=\"text/css\">\n" +
                "    .title {\n" +
                "        color: blue;\n" +
                "        text-decoration: bold;\n" +
                "        text-size: 1em;\n" +
                "    }\n" +
                "\n" +
                "    .author {\n" +
                "        color: rgba(255, 255, 255, 0.2);\n" +
                "        font-family: Garamond, serif;\n" +
                "        font-size: 48px;\n" +
                "\t\t\n" +
                "    }\n" +
                "\t.text\n" +
                "    {\n" +
                "        font-family: Garamond, serif;\n" +
                "        font-size: 12px;\n" +
                "        color: rgba(0, 0, 0, 0.5);\n" +
                "    }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p>\n" +
                "    <span class=\"title\">La super bonne</span>\n" +
                "    <span class=\"author\">proposée par Jérém</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">La super bonne</span>\n" +
                "    <span class=\"author\">proposée par Jérém</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">La super bonne</span>\n" +
                "    <span class=\"author\">proposée par Jérém</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">La super bonne</span>\n" +
                "    <span class=\"author\">proposée par Jérém</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">La super bonne</span>\n" +
                "    <span class=\"author\">proposée par Jérém</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">La super bonne</span>\n" +
                "    <span class=\"author\">proposée par Jérém</span>\n" +
                "    </p>\n" +
                "</body>\n" +
                "</html>";
        this.loadData(transparent, "text/html", null);
    }

}
