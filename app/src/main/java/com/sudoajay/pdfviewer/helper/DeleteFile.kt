package com.sudoajay.pdfviewer.helper

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.helper.storagePermission.AndroidExternalStoragePermission
import com.sudoajay.pdfviewer.helper.storagePermission.AndroidSdCardPermission
import java.io.File

object DeleteFile {
    fun delete(context: Context, path: String) {
        try {
            val filePath = File(path)
            if (filePath.exists())
                if (filePath.delete())
                    CustomToast.toastIt(
                        context,
                        context.getString(R.string.successfully_file_deleted_text)
                    )

        } catch (ignored: Exception) {

        }
    }


    fun deleteUri(context: Context, path: String) {
        var documentFile: DocumentFile
        val externalPath = AndroidExternalStoragePermission.getExternalPathFromCacheDir(context)
        try {
            val spilt: String


            if (path.contains(externalPath!!)) {
                val externalUri = AndroidExternalStoragePermission.getExternalUri(context)
                spilt = externalPath
                documentFile = DocumentFile.fromTreeUri(context, Uri.parse(externalUri))!!
            } else {

                val sdCardPath = AndroidSdCardPermission.getSdCardPath(context)
                val sdCardUri = AndroidSdCardPermission.getSdCardUri(context)
                spilt = sdCardPath
                documentFile = DocumentFile.fromTreeUri(context, Uri.parse(sdCardUri))!!

            }
            val list = path.split(spilt)[1].split("/")
            for (file in list) {
                documentFile = documentFile.findFile(file)!!

            }
            if (documentFile.delete())
                CustomToast.toastIt(
                    context,
                    context.getString(R.string.successfully_file_deleted_text)
                )
        } catch (ignored: Exception) {

        }
    }

}