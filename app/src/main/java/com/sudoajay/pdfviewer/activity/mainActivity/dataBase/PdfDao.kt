package com.sudoajay.pdfviewer.activity.mainActivity.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PdfDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pdf: Pdf)


    @Query("DELETE FROM PdfTable")
    suspend fun deleteAll()


}