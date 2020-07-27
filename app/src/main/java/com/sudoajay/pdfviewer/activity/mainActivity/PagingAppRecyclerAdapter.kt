package com.sudoajay.pdfviewer.activity.mainActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.mainActivity.dataBase.Pdf
import com.sudoajay.pdfviewer.helper.FileSize.convertIt
import kotlinx.android.synthetic.main.recycler_view_pdf_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class PagingAppRecyclerAdapter(var mainActivity: MainActivity) :
    PagedListAdapter<Pdf, PagingAppRecyclerAdapter.MyViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val recyclerAdapter = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_pdf_item, parent, false)
        return MyViewHolder(recyclerAdapter)
    }
    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var pdfName: TextView = view.pdfName_TextView
        var pdfInfo: TextView = view.pdfInfo_TextView
        var moreOption: ImageView = view.moreOption_imageView
        var pdf_ImageView: ImageView = view.pdf_ImageView
        var textContainer: LinearLayout = view.textContainer_linearLayout
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sdf = SimpleDateFormat(" , h:mm a , d MMM yyyy ", Locale.getDefault())

        val pdf = getItem(position)
        holder.pdfName.text = pdf!!.name
        holder.pdfInfo.text = convertIt(pdf.size) + sdf.format(pdf.date)

        holder.pdf_ImageView.setOnClickListener {
            mainActivity.copyingPdfFile(pdf.path, null)
        }
        holder.textContainer.setOnClickListener {
            mainActivity.copyingPdfFile(pdf.path, null)
        }

        holder.moreOption.setOnClickListener {
            mainActivity.showPopupMenu(holder.moreOption, pdf.path)
        }
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



}