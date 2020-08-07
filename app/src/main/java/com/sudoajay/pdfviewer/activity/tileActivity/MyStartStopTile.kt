package com.sudoajay.pdfviewer.activity.tileActivity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.showPdfViewer.ShowPdfViewer
import com.sudoajay.pdfviewer.helper.CustomToast


@RequiresApi(Build.VERSION_CODES.N)
class MyStartStopTile : TileService() {


    override fun onClick() {


        // Called when the user click the tile
        super.onClick()
        val tile = qsTile

        val pdfPath =
            getSharedPreferences(
                "state",
                Context.MODE_PRIVATE
            ).getString(getString(R.string.pdf_path_text), "").toString()
        val isActive =
            tile.state == Tile.STATE_ACTIVE
        if (pdfPath.isBlank()) {
            CustomToast.toastIt(
                applicationContext,
                getString(R.string.error_no_recent_pdf_file))
        } else {
            if (isActive) {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.open_pdf_file_text)
                tile.icon = Icon.createWithResource(
                    this,
                    R.drawable.ic_pdf_black
                )
                closeApp()
            } else {
                tile.state = Tile.STATE_ACTIVE
                tile.label = getString(R.string.close_pdf_file_text)
                tile.icon = Icon.createWithResource(
                    this, R.drawable.ic_close_pdf
                )
                launchPdfViewer(pdfPath)
            }
        }
        tile.updateTile()

        // Called when the user click the tile
    }

    private fun launchPdfViewer(path: String) {
        val intent = Intent(applicationContext, ShowPdfViewer::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = path
        startActivity(intent)
    }
    private fun closeApp() {

        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(
                getString(R.string.is_pdf_active_text), false
            ).apply()
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(homeIntent)
    }


    override fun onTileAdded() {
        super.onTileAdded()
        CustomToast.toastIt(
            applicationContext,
            getString(R.string.open_close_pdf_text) + " " + getString(R.string.added_text)
        )

        // Do something when the user add the Tile
    }


    override fun onTileRemoved() {
        super.onTileRemoved()
        CustomToast.toastIt(
            applicationContext,
            getString(R.string.open_close_pdf_text) + " " + getString(R.string.removed_text)
        )

        // Do something when the user removes the Tile
    }


    override fun onStartListening() {
        super.onStartListening()
        // Called when the Tile becomes visible

        val tile = qsTile
        val isPdfActive =
            getSharedPreferences("state", Context.MODE_PRIVATE).getBoolean(getString(R.string.is_pdf_active_text), false)

        if (isPdfActive) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = getString(R.string.close_pdf_file_text)
            tile.icon = Icon.createWithResource(this, R.drawable.ic_close_pdf)

        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.open_pdf_file_text)
            tile.icon = Icon.createWithResource(this, R.drawable.ic_pdf_black)
        }

        tile.updateTile()
    }
}