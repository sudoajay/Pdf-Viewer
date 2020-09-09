package com.sudoajay.pdfviewer.activity.mainActivity

import android.os.Build
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.sudoajay.pdfviewer.R


class MyMenuItemClickListener
/**
 */(var mainActivity: MainActivity, var path: String) :
    PopupMenu.OnMenuItemClickListener {

    /**
     * Click listener for popup menu items
     */
    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.open_menuOption -> {
                mainActivity.copyingPdfFile(path, null, true)
                return true
            }
            R.id.shareFile_menuOption -> {
                mainActivity.copyingPdfFile(path, null, false)
                return true
            }
            R.id.deleteFile_menuOption -> {
                mainActivity.alertDelete(path)
                return true
            }
            R.id.shortcut_menuOption -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mainActivity.addShortcut(path)
                else mainActivity.addShortcutBelowOreo(path)
                return true
            }
        }
        return false
    }


}