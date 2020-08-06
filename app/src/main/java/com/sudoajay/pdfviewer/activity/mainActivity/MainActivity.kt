package com.sudoajay.pdfviewer.activity.mainActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.BaseActivity
import com.sudoajay.pdfviewer.activity.settingActivity.SettingsActivity
import com.sudoajay.pdfviewer.activity.showPdfViewer.ShowPdfViewer
import com.sudoajay.pdfviewer.databinding.ActivityMainBinding
import com.sudoajay.pdfviewer.helper.CopyFile
import com.sudoajay.pdfviewer.helper.CustomToast
import com.sudoajay.pdfviewer.helper.DarkModeBottomSheet
import com.sudoajay.pdfviewer.helper.InsetDivider
import com.sudoajay.pdfviewer.helper.storagePermission.AndroidExternalStoragePermission
import com.sudoajay.pdfviewer.helper.storagePermission.AndroidSdCardPermission
import com.sudoajay.pdfviewer.helper.storagePermission.SdCardPath
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class MainActivity : BaseActivity(), SelectOptionBottomSheet.IsSelectedBottomSheetFragment {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var androidExternalStoragePermission: AndroidExternalStoragePermission
    private var isDarkTheme: Boolean = false
    private val requestCode = 100
    private var TAG = "MainActivityClass"



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        isDarkTheme = isDarkMode(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        changeStatusBarColor()

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        if (!intent.action.isNullOrEmpty() && intent.action.toString() == settingShortcutId) {
            openMoreSetting()
        }


        setReference()

        androidExternalStoragePermission =
            AndroidExternalStoragePermission(applicationContext, this)
        //        Take Permission

        if (!androidExternalStoragePermission.isExternalStorageWritable
            && SelectOptionBottomSheet.getValue(applicationContext) == getString(R.string.select_file_text)
        ) {
            showSelectOption()
            Log.e(TAG, "No isExternalStorageWritable")
        } else {
            androidExternalStoragePermission.callPermission()
            Log.e(TAG, "Yes isExternalStorageWritable")

        }
        Log.e("MainActivityViewModel" , "calling from  onCreate")
        callDataBaseConfig()
    }

    override fun onStart() {
        Log.e("MainActivityViewModel", " Activity - onStart ")
        super.onStart()
    }

    override fun onResume() {
        Log.e("MainActivityViewModel", " Activity - onResume ")

        super.onResume()
    }

    override fun onPause() {
        Log.e("MainActivityViewModel", " Activity - onPause ")

        super.onPause()
    }


    override fun onStop() {
        Log.e("MainActivityViewModel", " Activity - onStop ")

        super.onStop()
    }
    override fun onRestart() {
        Log.e("MainActivityViewModel", " Activity - onRestart ")

        super.onRestart()
    }


    override fun onDestroy() {
        Log.e("MainActivityViewModel", " Activity - onDestroy ")

        super.onDestroy()
    }
    private fun setReference() {

        //      Setup Swipe RecyclerView
        binding.swipeRefresh.setColorSchemeResources(
            if (isDarkTheme) R.color.swipeSchemeDarkColor else R.color.swipeSchemeColor
        )
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                applicationContext,
                if (isDarkTheme) R.color.swipeBgDarkColor else R.color.swipeBgColor

            )
        )


//         Setup BottomAppBar Navigation Setup
        binding.bottomAppBar.navigationIcon?.mutate()?.let {
            it.setTint(
                ContextCompat.getColor(
                    applicationContext,
                    if (isDarkTheme) R.color.navigationIconDarkColor else R.color.navigationIconColor
                )
            )
            binding.bottomAppBar.navigationIcon = it
        }

        setSupportActionBar(binding.bottomAppBar)


        binding.filterFloatingActionButton.setOnClickListener {
            showFilterOption()
        }

        setRecyclerView()
    }

    private fun setRecyclerView() {


        val recyclerView = binding.recyclerView
        val divider = getInsetDivider()
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val pagingAppRecyclerAdapter = PagingAppRecyclerAdapter(this)


        viewModel.appList!!.observe(this, androidx.lifecycle.Observer {

            for (x in it) {
                Log.e("MainActivityViewModel", x.name)
            }
            Log.e("MainActivityViewModel", "_________________________________________________________")

            pagingAppRecyclerAdapter.submitList(it)
            recyclerView.adapter = pagingAppRecyclerAdapter
            if (binding.swipeRefresh.isRefreshing)
                binding.swipeRefresh.isRefreshing = false

            isDataEmpty(it.size)


        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onRefresh()
            isDataEmpty(pagingAppRecyclerAdapter.itemCount)

        }

    }

    private fun getInsetDivider(): RecyclerView.ItemDecoration {
        val dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)
        val dividerColor = ContextCompat.getColor(
            applicationContext,
            R.color.divider
        )
        val marginLeft = resources.getDimensionPixelSize(R.dimen.divider_inset)
        return InsetDivider.Builder(this)
            .orientation(InsetDivider.VERTICAL_LIST)
            .dividerHeight(dividerHeight)
            .color(dividerColor)
            .insets(marginLeft, 0)
            .build()
    }

    fun callDataBaseConfig() = viewModel.databaseConfiguration(this)

    private fun isDataEmpty(it:Int){
        CoroutineScope(Dispatchers.Main).launch {
            if(it == 0  && viewModel.isEmpty())
                CustomToast.toastIt(applicationContext,getString(R.string.empty_list_text))
        }
    }

    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet(homeShortcutId)
        darkModeBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            darkModeBottomSheet.tag
        )

    }

    private fun showSelectOption() {
        val selectOptionBottomSheet = SelectOptionBottomSheet()
        selectOptionBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            selectOptionBottomSheet.tag
        )

    }

    private fun showFilterOption(){
        val filterPdfBottomSheet = FilterPdfBottomSheet()
        filterPdfBottomSheet.show(supportFragmentManager, filterPdfBottomSheet.tag)
    }

    private fun showNavigationDrawer(){
        val navigationDrawerBottomSheet = NavigationDrawerBottomSheet()
        navigationDrawerBottomSheet.show(supportFragmentManager, navigationDrawerBottomSheet.tag)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> showNavigationDrawer()
            R.id.darkMode_optionMenu -> showDarkMode()
            R.id.refresh_optionMenu -> viewModel.onRefresh()
            R.id.filePicker_optionMenu -> openFilePicker()
            R.id.more_setting_optionMenu -> openMoreSetting()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_toolbar_menu, menu)
        val actionSearch = menu.findItem(R.id.bottomToolbar_search)
        manageSearch(actionSearch)
        return super.onCreateOptionsMenu(menu)
    }

    private fun manageSearch(searchItem: MenuItem) {
        val searchView =
            searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        manageFabOnSearchItemStatus(searchItem)
        manageInputTextInSearchView(searchView)
    }

    private fun manageFabOnSearchItemStatus(searchItem: MenuItem) {
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                binding.filterFloatingActionButton.hide()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                binding.filterFloatingActionButton.show()
                return true
            }
        })
    }

    private fun manageInputTextInSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val query: String = newText.toLowerCase(Locale.ROOT).trim { it <= ' ' }
                viewModel.filterChanges(query)
                return true
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == 1) { // If request is cancelled, the result arrays are empty.
            if (!(grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) { // permission denied, boo! Disable the
// functionality that depends on this permission.
                CustomToast.toastIt(applicationContext, getString(R.string.giveUsPermission))
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    showSelectOption()
                }
            } else {
                AndroidExternalStoragePermission.setExternalPath(
                    applicationContext,
                    AndroidExternalStoragePermission.getExternalPathFromCacheDir(applicationContext)
                        .toString()
                )
                Log.e("MainActivityViewModel" , "calling onRequestPermissionsResult")

                callDataBaseConfig()

            }

        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { // local variable
        super.onActivityResult(requestCode, resultCode, data)
        val sdCardPathURL: String?
        val stringURI: String
        val spiltPart: String?
        if (resultCode != Activity.RESULT_OK) return

//        Here I get OpenManage File
        if (this.requestCode == requestCode && data != null) {
            copyingPdfFile(null, data.data, true)
            Log.e(TAG, data.data.toString() + " Get File Uri - ")
            return
        } else if (requestCode == 42 || requestCode == 58) {
            val sdCardURL: Uri? = data!!.data
            grantUriPermission(
                this@MainActivity.packageName,
                sdCardURL,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            this@MainActivity.contentResolver.takePersistableUriPermission(
                sdCardURL!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            sdCardPathURL = SdCardPath.getFullPathFromTreeUri(sdCardURL, this@MainActivity)
            stringURI = sdCardURL.toString()

            // Its supports till android 9 & api 28
            if (requestCode == 42) {
                spiltPart = "%3A"
                AndroidSdCardPermission.setSdCardPath(
                    applicationContext,
                    spiltThePath(stringURI, sdCardPathURL.toString())
                )
                AndroidSdCardPermission.setSdCardUri(
                    applicationContext,
                    spiltUri(stringURI, spiltPart)
                )
                val androidSdCardPermission = AndroidSdCardPermission(applicationContext, this)
                if (!androidSdCardPermission.isSdStorageWritable) {
                    CustomToast.toastIt(
                        applicationContext,
                        resources.getString(R.string.wrongDirectorySelected)
                    )
                    return
                }

            } else {
                val realExternalPath =
                    AndroidExternalStoragePermission.getExternalPath(applicationContext)
                if (realExternalPath in sdCardPathURL.toString() + "/") {
                    spiltPart = "primary%3A"
                    AndroidExternalStoragePermission.setExternalPath(
                        applicationContext,
                        realExternalPath
                    )
                    AndroidExternalStoragePermission.setExternalUri(
                        applicationContext,
                        spiltUri(stringURI, spiltPart)
                    )
                } else {
                    CustomToast.toastIt(
                        applicationContext,
                        getString(R.string.wrongDirectorySelected)
                    )

                    return
                }


            }

        } else {
            CustomToast.toastIt(applicationContext, getString(R.string.reportIt))
        }
    }

    private fun spiltUri(uri: String, spiltPart: String): String {
        return uri.split(spiltPart)[0] + spiltPart

    }

    private fun spiltThePath(url: String, path: String): String {
        val spilt = url.split("%3A").toTypedArray()
        val getPaths = spilt[0].split("/").toTypedArray()
        val paths = path.split(getPaths[getPaths.size - 1]).toTypedArray()
        return paths[0] + getPaths[getPaths.size - 1] + "/"

    }

    fun copyingPdfFile(filePath: String?, fileUri: Uri?, launchIt: Boolean) {

        CoroutineScope(Dispatchers.IO).launch {
            var uri: Uri? = fileUri
            var dst = File("")
            withContext(Dispatchers.IO) {
                if (!filePath.isNullOrEmpty() && filePath.startsWith("content:")) {
                    uri = Uri.parse(filePath)
                }
                if (uri != null) {
                    val cache: String = cacheDir.absolutePath
                    val fileName: String = queryName(contentResolver, fileUri)
                    dst = File(
                        """$cache/$fileName"""
                    )
                    Log.e(TAG, "File Dest " + dst.absolutePath.toString())

                    if (dst.exists()) dst.delete()


                    CopyFile.copyUri(applicationContext, uri!!, dst)
                } else {
                    val src = File(filePath.toString())
                    dst = File(cacheDir.toString() + "/" + src.name)
                    //                If file exist with same size
                    if (dst.exists()) dst.delete()
                    CopyFile.copy(src, dst)
                }
            }
            if (launchIt) launchPdfViewer(dst)
            else shareTheFile(dst)

        }

    }

    private fun launchPdfViewer(dst: File) {
        val intent = Intent(applicationContext, ShowPdfViewer::class.java)
        intent.action = dst.absolutePath
        startActivity(intent)
    }

    private fun shareTheFile(file: File) {
        try {
            val filename = file.name
            val fileLocation = File(cacheDir, filename)
            Log.e(TAG , fileLocation.absolutePath)
            val path = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                fileLocation
            )

            val intentShareFile = Intent(Intent.ACTION_SEND)
            if (fileLocation.exists()) {
                intentShareFile.type = "application/pdf"
                intentShareFile.putExtra(
                    Intent.EXTRA_STREAM,
                   path)
                intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.share_pdf_file_loading_text)
                )
                intentShareFile.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_pdf_file_loading_text))
                startActivity(Intent.createChooser(intentShareFile, getString(R.string.share_pdf_file_text)))
            }
        } catch (e: Exception) {
            CustomToast.toastIt(applicationContext, getString(R.string.submit_error_text))
        }
    }

    override fun handleDialogClose(value: String) {
        if (value == getString(R.string.select_option_text)) {
            if (SelectOptionBottomSheet.getValue(applicationContext) == getString(R.string.select_file_text)) {
                Log.e(TAG, "select_file_text Option Click")
                openFilePicker()
            } else {
                Log.e(TAG, "scan_file_text Option Click")
                androidExternalStoragePermission.callPermission()
            }
        } else {
            viewModel.filterChanges()
        }
    }

    private fun openFilePicker() {
        val intent = Intent()
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Set your required file type
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.select_pdf_file_text)
            ), requestCode
        )
    }

    private fun openMoreSetting() {
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }


    @SuppressLint("Recycle")
    private fun queryName(resolver: ContentResolver, uri: Uri?): String {
        val returnCursor = resolver.query(uri!!, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }


    /**
     * Showing popup menu when tapping on 3 dots
     */
    fun showPopupMenu(view: View, path: String) {
        val popup = PopupMenu(this, view, Gravity.END)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.more_option, popup.menu)

        //set menu item click listener here
        popup.setOnMenuItemClickListener(MyMenuItemClickListener(this, path))
        popup.show()
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
        const val settingShortcutId = "settingShortcut"

        //  Shortcut Info Id
        const val homeShortcutId = "homeShortcut"
    }


}
