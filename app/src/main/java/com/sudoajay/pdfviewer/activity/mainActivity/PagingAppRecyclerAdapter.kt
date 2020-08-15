package com.sudoajay.pdfviewer.activity.mainActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.Pdf
import com.sudoajay.pdfviewer.helper.FileSize.convertIt
import kotlinx.android.synthetic.main.layout_scan_sdcard.view.*
import kotlinx.android.synthetic.main.recycler_view_pdf_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class PagingAppRecyclerAdapter(var mainActivity: MainActivity) :
    PagedListAdapter<Pdf, PagingAppRecyclerAdapter.MyViewHolder>(DIFF_CALLBACK) {

    var totalSize = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val recyclerAdapter = if (viewType == VIEW_TYPE_CELL) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_pdf_item, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_scan_sdcard, parent, false)
        }
        return MyViewHolder(recyclerAdapter)
    }
    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        var pdfName: TextView? = view.pdfName_TextView
        var pdfInfo: TextView? = view.pdfInfo_TextView
        var moreOption: ImageView? = view.moreOption_imageView
        var pdfImageView: ImageView? = view.pdf_ImageView
        var textContainer: LinearLayout? = view.textContainer_linearLayout
        var scanSdCardButton: Button? =view.scanSdCard_Button
    }

    @SuppressLint("SetTextI18n")

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position < totalSize ) {
            val sdf = SimpleDateFormat(" , h:mm a , d MMM yyyy ", Locale.getDefault())

            val pdf = getItem(position)
            holder.pdfName!!.text = pdf!!.name
            holder.pdfInfo!!.text = convertIt(pdf.size) + sdf.format(pdf.date)

            holder.pdfImageView!!.setOnClickListener {
                openPdfFile(pdf.path)
            }
            holder.textContainer!!.setOnClickListener {
                openPdfFile(pdf.path)
            }

            holder.moreOption!!.setOnClickListener {
                mainActivity.showPopupMenu(holder.moreOption!!, pdf.path)
            }
        } else {
            holder.scanSdCardButton?.setOnClickListener { mainActivity.callSdCardPermission()}
        }
    }

    override fun getItemCount(): Int {
        //Header item, plus the extra row
        return totalSize + if (mainActivity.isSdCardPresent()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == totalSize) VIEW_TYPE_FOOTER else VIEW_TYPE_CELL
    }
    private fun openPdfFile(pdfPath:String) = mainActivity.copyingPdfFile(pdfPath, null, true)

    companion object {

        const val VIEW_TYPE_FOOTER = 1
        const val VIEW_TYPE_CELL = 0
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

}