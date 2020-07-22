package com.sudoajay.pdfviewer.activity.mainActivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.databinding.LayoutFilterPdfBottomSheetBinding


class FilterPdfBottomSheet : BottomSheetDialogFragment() {

    private var isSelectedBottomSheetFragment: SelectOptionBottomSheet.IsSelectedBottomSheetFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_filter_pdf_bottom_sheet, null)
        val binding = LayoutFilterPdfBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this
        isSelectedBottomSheetFragment = activity as SelectOptionBottomSheet.IsSelectedBottomSheetFragment?

        return binding.root
    }


    fun isValue(value: String): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getString(getString(R.string.title_menu_order_by), getString(R.string.menu_alphabetical_order)).toString() == value
    }

    fun setValue( value: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(getString(R.string.title_menu_order_by), value).apply()
        isSelectedBottomSheetFragment!!.handleDialogClose(getString(R.string.title_menu_order_by))
        dismiss()
    }

}

