package com.sudoajay.pdfviewer.activity.mainActivity

import android.app.Activity
import android.app.Application
import android.os.Build
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
import com.sudoajay.pdfviewer.helper.ScanPdf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel (application: Application) : AndroidViewModel(application) {


    var appList: LiveData<PagedList<Pdf>>? = null

    private var _application = application
    private var pdfRepository: PdfRepository

    private val filterChanges: MutableLiveData<String> = MutableLiveData()
    private var hideProgress: MutableLiveData<Boolean>? = null


    private var pdfDao: PdfDao =
        PdfRoomDatabase.getDatabase(_application.applicationContext).appDao()
    init {

//        Creating Object and Initialization
        pdfRepository = PdfRepository(_application.applicationContext, pdfDao)

        filterChanges()

        appList = Transformations.switchMap(filterChanges) {
            pdfRepository.handleFilterChanges(it)
        }

    }
    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        filterChanges.value = filter
    }
    fun databaseConfiguration(activity: MainActivity) {
        getHideProgress()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                if (pdfRepository.getCount() == 0)
                    pdfDatabaseConfiguration(activity)
            }
            hideProgress!!.postValue(  false)
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