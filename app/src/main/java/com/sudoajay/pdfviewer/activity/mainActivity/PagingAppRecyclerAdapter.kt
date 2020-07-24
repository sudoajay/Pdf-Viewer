package com.sudoajay.pdfviewer.activity.mainActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.Pdf
import kotlinx.android.synthetic.main.recycler_view_pdf_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PagingAppRecyclerAdapter(context: Context, private var appFilter: AppFilter) :
    PagedListAdapter<Pdf, PagingAppRecyclerAdapter.MyViewHolder>(DIFF_CALLBACK) {


    private var packageManager = context.packageManager


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val recyclerAdapter = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_pdf_item, parent, false)

        return MyViewHolder(recyclerAdapter)

    }

    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val pdfName: TextView = view.pdfName_TextView
        val pdfInfo: TextView = view.pdfInfo_TextView
        val moreOption: ImageView = view.moreOption_imageView
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val app = getItem(position)


    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Pdf>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(
                oldConcert: Pdf,
                newConcert: Pdf
            ) = oldConcert.id == newConcert.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldConcert: Pdf,
                newConcert: Pdf
            ): Boolean = oldConcert == newConcert

        }
    }

    private fun getApplicationsIcon(applicationInfo: String): Drawable {
        return try {
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            defaultApplicationIcon
        }
    }

    private val defaultApplicationIcon: Drawable
        get() = packageManager.defaultActivityIcon


}