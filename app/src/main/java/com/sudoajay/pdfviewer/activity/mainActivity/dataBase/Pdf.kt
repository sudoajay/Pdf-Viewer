package com.sudoajay.pdfviewer.activity.mainActivity.dataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "PdfTable")
class Pdf(
    @PrimaryKey(autoGenerate = true ) var id: Long?,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "Path") val path: String,
    @ColumnInfo(name = "Date") val date: Long,
    @ColumnInfo(name = "Size") val size: Long,
    @ColumnInfo(name = "Installed") val isInstalled: Boolean


)