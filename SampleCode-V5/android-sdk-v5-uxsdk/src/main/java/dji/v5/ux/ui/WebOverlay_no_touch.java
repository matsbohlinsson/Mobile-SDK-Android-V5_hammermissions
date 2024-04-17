package dji.v5.ux.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebOverlay_no_touch extends WebView {
    public static WebOverlay_no_touch instance = null;

    public WebOverlay_no_touch(Context context) {
        super(context);
        initView(context);
        instance = this;
    }

    static public WebOverlay_no_touch getInstance() {
        return instance;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Send touch to widget below
        return false;
    }

    public WebOverlay_no_touch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        //example();
        instance = this;
    }

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
        example();
        /*
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        // zoom if you want
        webView.getSettings().setSupportZoom(true);
        // extra settings
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollContainer(true);


 */

    }


    public void example() {
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
                "    <span class=\"title\">No touch>\n" +
                "    <span class=\"author\">Noooo touch</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">No touch>\n" +
                "    <span class=\"author\">Noooo touch</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">No touch>\n" +
                "    <span class=\"author\">Noooo touch</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">No touch>\n" +
                "    <span class=\"author\">Noooo touch</span>\n" +
                "    </p>\n" +
                "    <p>\n" +
                "    <span class=\"title\">No touch>\n" +
                "    <span class=\"author\">Noooo touch</span>\n" +
                "    </p>\n" +
                "</body>\n" +
                "</html>";
        this.loadData(transparent, "text/html", null);
    }

}
