
package com.didatv.app

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {

    private lateinit var root: FrameLayout
    private var webView: WebView? = null
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    private val website = "https://larozatv.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()
        showHome()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
    }

    private fun showHome() {
        root = FrameLayout(this)

        root.background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(8, 15, 45),
                Color.rgb(30, 12, 45),
                Color.rgb(5, 25, 55)
            )
        )

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 30, 40, 30)
        }

        val logo = TextView(this).apply {
            text = "DIDA TV"
            textSize = 50f
            setTextColor(Color.WHITE)
            typeface = Typeface.create(
                "sans-serif-black",
                Typeface.BOLD
            )
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "MOVIES • SERIES • ENTERTAINMENT"
            textSize = 18f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 35)
        }

        val enterButton = Button(this).apply {
            text = "دخول إلى DIDA TV"
            textSize = 22f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                cornerRadius = 24f
                setColor(Color.rgb(190, 35, 55))
            }
            setPadding(45, 18, 45, 18)
            isFocusable = true
            isFocusableInTouchMode = true
            setOnClickListener {
                openWebsite()
            }
        }

        layout.addView(logo)
        layout.addView(subtitle)
        layout.addView(enterButton)

        root.addView(
            layout,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)
        enterButton.requestFocus()
    }

    private fun openWebsite() {
        webView?.destroy()

        val browser = WebView(this)
        webView = browser

        browser.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadsImagesAutomatically = true
            mixedContentMode =
                WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(false)
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        browser.isFocusable = true
        browser.isFocusableInTouchMode = true

        browser.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }

        browser.webChromeClient = object : WebChromeClient() {

            override fun onShowCustomView(
                view: View?,
                callback: CustomViewCallback?
            ) {
                if (view == null || customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                customView = view
                customViewCallback = callback

                root.removeAllViews()
                root.addView(
                    view,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )

                hideSystemUI()
            }

            override fun onHideCustomView() {
                exitFullscreen()
            }
        }

        root.removeAllViews()
        root.addView(
            browser,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        browser.loadUrl(website)
        browser.requestFocus()
    }

    private fun exitFullscreen() {
        if (customView == null) return

        root.removeAllViews()

        customView = null

        val callback = customViewCallback
        customViewCallback = null
        callback?.onCustomViewHidden()

        webView?.let {
            root.addView(
                it,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }

        hideSystemUI()
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?
    ): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (customView != null) {
                exitFullscreen()
                return true
            }

            val browser = webView

            if (browser != null) {
                if (browser.canGoBack()) {
                    browser.goBack()
                } else {
                    browser.stopLoading()
                    browser.destroy()
                    webView = null
                    showHome()
                }
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onPause() {
        webView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}
