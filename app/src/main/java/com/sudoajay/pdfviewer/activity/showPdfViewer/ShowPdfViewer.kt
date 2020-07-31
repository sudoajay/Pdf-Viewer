package com.sudoajay.pdfviewer.activity.showPdfViewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.databinding.ActivityShowPdfViewerBinding
import com.sudoajay.pdfviewer.helper.CustomToast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowPdfViewer : AppCompatActivity() {

    private lateinit var binding: ActivityShowPdfViewerBinding
    private lateinit var url: String
    private var uploadFile: ValueCallback<Uri>? = null
    private var uploadFileArray: ValueCallback<Array<Uri>>? = null
    private var mOnScrollChangedListener: ViewTreeObserver.OnScrollChangedListener? = null
    private val actionGetContentCode = 100
    private val actionOpenDocumentCode = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_pdf_viewer)
        val intent = intent
        var getPath: String? = null
        if (intent != null) {
            getPath = intent.action
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_pdf_viewer)

        url = "file:///android_asset/web/viewer.html?file=$getPath"

        setUpWebView()
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

        var type = arrayListOf<String>("*/*", "application/pdf")

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

}