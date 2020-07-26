package com.sudoajay.pdfviewer.activity.mainActivity

import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.sudoajay.pdfviewer.R


class MyMenuItemClickListener
/**
 * @param position
 */(var position: Int) :
    PopupMenu.OnMenuItemClickListener {

    /**
     * Click listener for popup menu items
     */
    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.open_menuOption ->                    // ...
                return true
            R.id.shareFile_menuOption ->                     // ...
                return true
            R.id.deleteFile_menuOption ->                     // ...
                return true
            R.id.shortcut_menuOption ->                    // ...
                return true
        }
        return false
    }

}