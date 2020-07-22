package com.sudoajay.pdfviewer.activity.mainActivity.dataBase

import android.content.Context


class PdfRepository(private val context: Context, private val pdfDao: PdfDao) {


    suspend fun insert(pdf: Pdf) {
        pdfDao.insert(pdf)
    }



}