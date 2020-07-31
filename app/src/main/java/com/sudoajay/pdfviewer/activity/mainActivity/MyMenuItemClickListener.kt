package com.sudoajay.pdfviewer.activity.mainActivity

import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.helper.DeleteFile


class MyMenuItemClickListener
/**
 * @param position
 */(var mainActivity: MainActivity, var path: String) :
    PopupMenu.OnMenuItemClickListener {

    /**
     * Click listener for popup menu items
     */
    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.open_menuOption -> {
                mainActivity.copyingPdfFile(path, null, true)                  // ...
                return true
            }
            R.id.shareFile_menuOption -> {
                mainActivity.copyingPdfFile(path, null, false)
                return true
            }
            R.id.deleteFile_menuOption -> {
                if (path.isNotEmpty()) {
                    if (path.startsWith("content:"))
                        DeleteFile.deleteUri(mainActivity.applicationContext, path)
                    else DeleteFile.delete(mainActivity.applicationContext, path)
                    mainActivity.callDataBaseConfig()
                }
                return true
            }
            R.id.shortcut_menuOption ->                    // ...
                return true
        }
        return false
    }

}