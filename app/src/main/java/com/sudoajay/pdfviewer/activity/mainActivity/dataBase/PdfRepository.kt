package com.sudoajay.pdfviewer.activity.mainActivity.dataBase

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.sudoajay.pdfviewer.R


class PdfRepository(private val context: Context, private val pdfDao: PdfDao) {

    lateinit var app: DataSource.Factory<Int, Pdf>

    fun handleFilterChanges(filter: String): LiveData<PagedList<Pdf>> {

        if (filter == context.getString(R.string.filter_changes_text)) {
            //         Sorting Data in Alpha or Install date


            val getOrderBy =
                context.getSharedPreferences("state", Context.MODE_PRIVATE).getString(
                    context.getString(R.string.title_menu_order_by),
                    context.getString(R.string.filter_by_name_order)
                )
           app = when (getOrderBy){
                context.getString(R.string.filter_by_name_order) -> pdfDao.getSortByName()
               context.getString(R.string.filter_by_date_order) ->pdfDao.getSortByDate()
               context.getString(R.string.filter_by_most_used_order) ->pdfDao.getSortByMostUsed()
               context.getString(R.string.filter_by_size_order) ->pdfDao.getSortBySize()
               else -> pdfDao.getSortByName()
           }


            return app.toLiveData(
                PagedList.Config.Builder()
                    .setPageSize(20) //
                    .setInitialLoadSizeHint(20) //
                    .setEnablePlaceholders(false) //
                    .build()
            )
        } else {

            val value = "%$filter%"


            return pdfDao.searchItem(value).toLiveData(
                PagedList.Config.Builder()
                    .setPageSize(20) //
                    .setInitialLoadSizeHint(20) //
                    .setEnablePlaceholders(false) //
                    .build()
            )

        }

    }

    suspend fun insert(pdf: Pdf) {
        pdfDao.insert(pdf)
    }



}