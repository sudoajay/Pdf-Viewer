package com.sudoajay.pdfviewer.activity.mainActivity.dataBase

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PdfDao {


    @Query("Select * From PdfTable Order By Name Asc ")
    fun getSortByName(): DataSource.Factory<Int, Pdf>

    @Query("Select * From PdfTable Order By Date Desc ")
    fun getSortByDate(): DataSource.Factory<Int, Pdf>

    @Query("Select * From PdfTable Order By Most_Used Desc ")
    fun getSortByMostUsed(): DataSource.Factory<Int, Pdf>

    @Query("Select * From PdfTable Order By Size Asc ")
    fun getSortBySize(): DataSource.Factory<Int, Pdf>



    @Query("SELECT * FROM PdfTable WHERE Name LIKE :search")
    fun searchItem(search: String?): DataSource.Factory<Int, Pdf>




    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pdf: Pdf)


    @Query("DELETE FROM PdfTable")
    suspend fun deleteAll()


}