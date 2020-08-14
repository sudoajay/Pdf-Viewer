package com.sudoajay.pdfviewer.activity.showPdfViewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.BaseActivity
import com.sudoajay.pdfviewer.activity.mainActivity.MainActivity
import com.sudoajay.pdfviewer.databinding.ActivityShowPdfViewerBinding
import com.sudoajay.pdfviewer.helper.CustomToast
import java.io.File

class ShowPdfViewer : AppCompatActivity() {

    private lateinit var binding: ActivityShowPdfViewerBinding
    private lateinit var url: String
    private lateinit var getPath: String
    private var uploadFile: ValueCallback<Uri>? = null
    private var uploadFileArray: ValueCallback<Array<Uri>>? = null
    private val actionGetContentCode = 100
    private val actionOpenDocumentCode = 101
    private  var isDarkTheme: Boolean =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkTheme = BaseActivity.isDarkMode(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme )
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_show_pdf_viewer)

        changeStatusBarColor()
        val intent = intent
        if (intent != null) {
            getPath = intent.action.toString()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_pdf_viewer)

        url = "file:///android_asset/web/viewer.html?file=$getPath"

        setUpWebView()

    }

    override fun onResume() {
        super.onResume()

        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(
                getString(R.string.is_pdf_active_text), true
            ).apply()

        setValueInSharedPreference()
    }

    private fun setValueInSharedPreference() {


        var pdfArray = getPdfPathFromShared(applicationContext).asReversed()

        for (i in pdfArray.indices.reversed()) if (getPath == pdfArray[i] || pdfArray[i] == "none") pdfArray.removeAt(i)


        pdfArray.add(getPath)

        pdfArray = pdfArray.distinct().toMutableList().asReversed()

//        set value and initialize
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(
                getString(R.string.pdf_path_1_text), if (pdfArray.size >= 1) pdfArray[0] else "none"
            ).apply()
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(
                getString(R.string.pdf_path_2_text), if (pdfArray.size >= 2) pdfArray[1] else "none"
            ).apply()
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(
                getString(R.string.pdf_path_3_text), if (pdfArray.size >= 3) pdfArray[2] else "none"
            ).apply()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            shortcutManager()
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "WrongConstant")
    private fun setUpWebView() {

        binding.myWebView.setPadding(0, 0, 0, 0)
        binding.myWebView.setInitialScale(1)
        binding.myWebView.scrollBarStyle = 33554432
        binding.myWebView.isScrollbarFadingEnabled = false

        val settings = binding.myWebView.settings
        settings.javaScriptEnabled = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.domStorageEnabled = true
        settings.domStorageEnabled = true
        val appCachePath = applicationContext.cacheDir.absolutePath
        settings.setAppCachePath(appCachePath)
        settings.allowFileAccess = true
        settings.setAppCacheEnabled(true)
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        binding.myWebView.webViewClient = CustomWebViewClient()
        binding.myWebView.webChromeClient = CustomWebChromeClient()
        binding.myWebView.loadUrl(url)


        // download listener
        binding.myWebView.setDownloadListener { _, _, _, _, _ ->

            CustomToast.toastIt(
                applicationContext,
                getString(R.string.downloading_not_suppoted_text)
            )
        }

    }
    internal inner class CustomWebViewClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            CustomToast.toastIt(applicationContext, getString(R.string.submit_error_text))
        }

        override fun onPageFinished(view: WebView, url: String) {
            binding.frameLayout.visibility = View.GONE
        }

    }

    internal inner class CustomWebChromeClient : WebChromeClient() {

        var type = arrayListOf("*/*", "application/pdf")

        // For 3.0+ Devices (Start)
        // onActivityResult attached before constructor
        private fun openFileChooser(
            uploadMsg: ValueCallback<Uri>,
            acceptType: String?
        ) {
            uploadFile = uploadMsg
            try {
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = type[0]
                startActivityForResult(
                    Intent.createChooser(i, getString(R.string.select_pdf_file_text)),
                    actionGetContentCode
                )
            } catch (e: ActivityNotFoundException) {
                uploadFile = null
                CustomToast.toastIt(
                    applicationContext,
                    getString(R.string.cannot_open_file_chooser_text)
                )

            }
        }

        //For Android 4.1 only
        private fun openFileChooser(
            uploadMsg: ValueCallback<Uri>,
            acceptType: String?,
            capture: String?
        ) {
            uploadFile = uploadMsg
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = type[0]
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.select_pdf_file_text)),
                    actionGetContentCode
                )
            } catch (e: ActivityNotFoundException) {
                uploadFile = null
                CustomToast.toastIt(
                    applicationContext,
                    getString(R.string.cannot_open_file_chooser_text)
                )

            }
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (uploadFileArray != null) {
                    uploadFileArray!!.onReceiveValue(null)
                    uploadFileArray = null
                }
                uploadFileArray = filePathCallback
                try {
                    val intent = Intent()
                    intent.type = type[1]
                    intent.action = Intent.ACTION_OPEN_DOCUMENT
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_pdf_file_text)),
                        actionOpenDocumentCode
                    )
                } catch (e: ActivityNotFoundException) {
                    uploadFileArray = null
                    CustomToast.toastIt(
                        applicationContext,
                        getString(R.string.cannot_open_file_chooser_text)
                    )
                    CustomToast.toastIt(
                        applicationContext,
                        getString(R.string.cannot_open_file_chooser_text)
                    )
                    return false
                }
                return true
            }
            return false

        }


        private fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
            uploadFile = uploadMsg
            try {
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = type[0]
                startActivityForResult(
                    Intent.createChooser(i, getString(R.string.select_pdf_file_text)),
                    actionGetContentCode
                )
            } catch (e: ActivityNotFoundException) {
                uploadFile = null
                CustomToast.toastIt(
                    applicationContext,
                    getString(R.string.cannot_open_file_chooser_text)
                )

            }
        }

        //Getting webview rendering progress
        override fun onProgressChanged(view: WebView?, p: Int) {
            binding.progressBar.progress = p
            if (p == 100) {
                binding.progressBar.progress = 0
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == actionOpenDocumentCode) {
                uploadFileArray = null
                return
            } else {
                uploadFile = null
                return
            }

        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == actionOpenDocumentCode) {
                if (uploadFileArray == null) return
                uploadFileArray!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        intent
                    )
                )
                uploadFileArray = null

            } else if (requestCode == actionGetContentCode) {
                if (null == uploadFile) return
                // Use MainActivity.RESULT_OK if you're implementing WebViewFragment inside Fragment
                // Use RESULT_OK only if you're implementing WebViewFragment inside an Activity
                val result =
                    if (intent == null || resultCode != RESULT_OK) null else intent.data
                uploadFile!!.onReceiveValue(result)
                uploadFile = null

            }
            binding.frameLayout.visibility = View.VISIBLE
        } else CustomToast.toastIt(
            applicationContext,
            getString(R.string.failed_to_open_file_text)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(
                getString(R.string.is_pdf_active_text), false
            ).apply()
    }


    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun shortcutManager() {

        val shortcutManager = getSystemService<ShortcutManager>(ShortcutManager::class.java)

        val pdfList = getPdfPathFromShared(applicationContext)
        val listShortcutInfo: MutableList<ShortcutInfo> = mutableListOf()
        val pdfShortcut1: ShortcutInfo?
        val pdfShortcut2: ShortcutInfo?
        val pdfShortcut3: ShortcutInfo?
        if (pdfList[0] != "none" && File(pdfList[0]).exists()) {
            pdfShortcut1 = ShortcutInfo.Builder(
                applicationContext,
                pdfPathShortcutId1
            )
                .setShortLabel(File(pdfList[0]).name)
                .setLongLabel(File(pdfList[0]).name)
                .setIcon(
                    Icon.createWithResource(
                        applicationContext,
                        R.drawable.ic_pdf
                    )
                )
                .setIntent(
                    Intent(
                        applicationContext,
                        ShowPdfViewer::class.java
                    ).setAction(pdfList[0])
                )
                .build()
            listShortcutInfo.add(pdfShortcut1)
        }
        if (pdfList[1] != "none" && File(pdfList[1]).exists()) {
            pdfShortcut2 =
                ShortcutInfo.Builder(
                    applicationContext,
                    pdfPathShortcutId2
                )
                    .setLongLabel(File(pdfList[1]).name)
                    .setShortLabel(File(pdfList[1]).name)
                    .setIcon(
                        Icon.createWithResource(
                            applicationContext,
                            R.drawable.ic_pdf
                        )
                    )
                    .setIntent(
                        Intent(
                            applicationContext,
                            ShowPdfViewer::class.java
                        ).setAction(pdfList[1])
                    )
                    .build()
            listShortcutInfo.add(pdfShortcut2)
        }
        if (pdfList[2] != "none" && File(pdfList[2]).exists()) {
            pdfShortcut3 =
                ShortcutInfo.Builder(
                    applicationContext,
                    pdfPathShortcutId3
                )
                    .setLongLabel(File(pdfList[2]).name)
                    .setShortLabel(File(pdfList[2]).name)
                    .setIcon(
                        Icon.createWithResource(
                            applicationContext,
                            R.drawable.ic_pdf
                        )
                    )
                    .setIntent(
                        Intent(
                            applicationContext,
                            ShowPdfViewer::class.java
                        ).setAction(pdfList[2])
                    )
                    .build()
            listShortcutInfo.add(pdfShortcut3)
        }


        shortcutManager!!.dynamicShortcuts = listShortcutInfo
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }


    companion object {
        const val pdfPathShortcutId1 = "pdfPath1"
        const val pdfPathShortcutId2 = "pdfPath2"
        const val pdfPathShortcutId3 = "pdfPath3"


        fun getPdfPathFromShared(context: Context): MutableList<String> {
            val pdfPath1 = context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.pdf_path_1_text), "none"
                )
            val pdfPath2 = context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.pdf_path_2_text), "none"
                )
            val pdfPath3 = context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.pdf_path_3_text), "none"
                )
            Log.e(
                "ShowPdfViewer",
                pdfPath1.toString() + " _----" + pdfPath2.toString() + " --- " + pdfPath3.toString()
            )
            return mutableListOf(pdfPath1.orEmpty(), pdfPath2.toString(), pdfPath3.toString())
        }


    }
}