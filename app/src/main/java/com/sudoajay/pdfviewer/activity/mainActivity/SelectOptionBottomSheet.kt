package com.sudoajay.pdfviewer.activity.mainActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.databinding.LayoutDialogSelectoptionBinding
import com.sudoajay.pdfviewer.helper.CustomToast


class SelectOptionBottomSheet : BottomSheetDialogFragment() {

    private var isSelectedBottomSheetFragment: IsSelectedBottomSheetFragment? = null

    interface IsSelectedBottomSheetFragment {
        fun handleDialogClose(value: String)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_dialog_selectoption, null)
        val binding = LayoutDialogSelectoptionBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this
        isSelectedBottomSheetFragment = activity as IsSelectedBottomSheetFragment?
        isCancelable  =false
        return binding.root
    }

    fun setValue(value: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(
                getString(R.string.select_option_text), value
            ).apply()

        isSelectedBottomSheetFragment!!.handleDialogClose(getString(R.string.select_option_text))
        dismiss()
    }

    companion object{
        fun getValue(context: Context): String {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.select_option_text), context.getString(R.string.scan_file_text)
                )
                .toString()
        }
    }
}

