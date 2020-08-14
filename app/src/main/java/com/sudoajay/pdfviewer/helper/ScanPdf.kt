package com.sudoajay.pdfviewer.helper

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.Pdf
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.PdfRepository
import com.sudoajay.pdfviewer.helper.storagePermission.AndroidExternalStoragePermission
import com.sudoajay.pdfviewer.helper.storagePermission.AndroidSdCardPermission
import java.io.File

class ScanPdf(private var activity: Activity, private var pdfRepository: PdfRepository) {

    private var isAndroidDir: Boolean? = null
    private var parentName: String? = null
    private var androidExternalStoragePermission =
        AndroidExternalStoragePermission(activity.applicationContext, activity)
    private var androidSdCardPermission =
        AndroidSdCardPermission(activity.applicationContext, activity)

    suspend fun scanUsingFile() {

        val externalDir =
            AndroidExternalStoragePermission.getExternalPath(activity.applicationContext)
        val sdCardDir = AndroidSdCardPermission.getSdCardPath(activity.applicationContext)

        //        Here we Just add default value of install Pdf File
        pdfRepository.setDefaultValueInstall()

        // Its supports till android 9 & api 28
        if (androidExternalStoragePermission.isExternalStorageWritable) {
            getAllPathFile(File(externalDir))
        }
        if (Build.VERSION.SDK_INT >= 21 && androidSdCardPermission.isSdStorageWritable) {
            getAllPathFile(File(sdCardDir))
        }
//
        //        Here we remove Uninstall Pdf File from Data base
        pdfRepository.removeUninstallAppFromDB()

    }

    private suspend fun getAllPathFile(directory: File) {
        val extension = ".pdf"
        var getName: String
        try {
            for (child in directory.listFiles()!!)
                if (child.isDirectory) {
                    if (!child.path.contains("/Android/data"))
                        getAllPathFile(child)
                } else {
                    getName = child.name
                    if (getName.endsWith(extension))
                        insertDataToDataBase(
                            child.name,
                            child.absolutePath,
                            child.lastModified(),
                            child.length()
                        )
                }
        } catch (ignored: Exception) {
        }
    }


    suspend fun scanUsingDoc() {

        var documentFile: DocumentFile
        val externalUri =
            AndroidExternalStoragePermission.getExternalUri(activity.applicationContext)
        val sdCardUri = AndroidSdCardPermission.getSdCardUri(activity.applicationContext)

        if (androidExternalStoragePermission.isExternalStorageWritable) {
            isAndroidDir = false
            documentFile =
                DocumentFile.fromTreeUri(activity.applicationContext, Uri.parse(externalUri))!!
            parentName = documentFile.name
            getAllPathDocumentFile(documentFile)
        }

        if (androidSdCardPermission.isSdStorageWritable) {
            isAndroidDir = false
            documentFile =
                DocumentFile.fromTreeUri(activity.applicationContext, Uri.parse(sdCardUri))!!
            parentName = documentFile.name
            getAllPathDocumentFile(documentFile)
        }

    }

    private suspend fun getAllPathDocumentFile(directory: DocumentFile) {
        val extension = ".pdf"
        var getName: String
        try {
            for (child in directory.listFiles())
                if (child.isDirectory) {
                    if (!isAndroidDir!!) {
                        if (child.name!! == "Android" && child.parentFile!!.name.equals(parentName)) {
                            isAndroidDir = true
                            continue
                        }
                    }
                    getAllPathDocumentFile(child)
                } else {
                    getName = child.name.toString()
                    if (getName.endsWith(extension)) {
                        insertDataToDataBase(child.name.toString(), child.uri.toString(), child.lastModified(), child.length())
                    }
                }
        } catch (ignored: Exception) {
        }
    }


    private suspend fun insertDataToDataBase(
        name: String,
        path: String,
        date: Long,
        size: Long
    ) {

        if (pdfRepository.isPresent(path) == 0)
            pdfRepository.insert(
                Pdf(
                    null,
                    name,
                    path,
                    date,
                    size,
                    true
                )
            )
        else
            pdfRepository.setInstallValue(
                path
            )


    }

}