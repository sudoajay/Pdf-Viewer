package com.sudoajay.pdfviewer.activity.mainActivity

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.Pdf
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.PdfDao
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.PdfRepository
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.PdfRoomDatabase
import com.sudoajay.pdfviewer.helper.CustomToast
import com.sudoajay.pdfviewer.helper.ScanPdf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel (application: Application) : AndroidViewModel(application) {


    var appList: LiveData<PagedList<Pdf>>? = null
    var TAG = "MainActivityViewModel"
    private var _application = application
    private var pdfRepository: PdfRepository

    private val filterChanges: MutableLiveData<String> = MutableLiveData()
    private var hideProgress: MutableLiveData<Boolean>? = null


    private var pdfDao: PdfDao =
        PdfRoomDatabase.getDatabase(_application.applicationContext).appDao()
    init {

//        Creating Object and Initialization
        pdfRepository = PdfRepository(_application.applicationContext, pdfDao)


        appList = Transformations.switchMap(filterChanges) {
            pdfRepository.handleFilterChanges(it)
        }

    }
    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        filterChanges.value = filter
    }
    fun databaseConfiguration(activity: MainActivity) {
        filterChanges()
        getHideProgress()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                if (pdfRepository.getCount() != 0) {
                    Log.e(TAG, "Is not Empty ")
                    pdfRepository.deleteAll()
                } else {
                    Log.e(TAG, "Is  Empty ")
                }
                pdfDatabaseConfiguration(activity)
                hideProgress!!.postValue(false)
                filterChanges.postValue(_application.getString(R.string.filter_changes_text))
            }
            checkForEmpty()
        }

    }

    private suspend fun pdfDatabaseConfiguration(activity: MainActivity){
        val scanPdf = ScanPdf(activity, pdfRepository)
        //             Its supports till android 9 & api 28
        if (Build.VERSION.SDK_INT <= 28) {
            scanPdf.scanUsingFile()
        } else {
            scanPdf.scanUsingDoc()
        }
    }

    fun onRefresh() {
            appList!!.value!!.dataSource.invalidate()
        CoroutineScope(Dispatchers.Main).launch {
            checkForEmpty()
        }
    }

    private suspend fun checkForEmpty(){
        if (pdfRepository.getCount() == 0) {
            CustomToast.toastIt(_application, _application.getString(R.string.empty_list_text))
        }
    }

    fun getHideProgress(): LiveData<Boolean> {
        if (hideProgress == null) {
            hideProgress = MutableLiveData()
            loadHideProgress()
        }
        return hideProgress as MutableLiveData<Boolean>
    }

    private fun loadHideProgress() {
        hideProgress!!.value = true
    }
}