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

    @Query("Select * From PdfTable Order By Size Asc ")
    fun getSortBySize(): DataSource.Factory<Int, Pdf>

    @Query("SELECT * FROM PdfTable WHERE Name LIKE :search")
    fun searchItem(search: String?): DataSource.Factory<Int, Pdf>


    @Query("UPDATE PdfTable SET Installed = '0'")
    suspend fun setDefaultValueInstall()

    @Query("SELECT Count(id) FROM PdfTable WHERE Path = :path ")
    suspend fun isPresent(path: String): Int

    @Query("UPDATE  PdfTable  SET Installed = '1'  WHERE id IN (SELECT id FROM ( select id from PdfTable where Path = :path  limit 0,1)l)")
    suspend fun updateInstalledByPath(path: String)

    @Query("SELECT id FROM PdfTable WHERE Installed = '0' ")
    suspend fun getUninstallList(): List<Int>


    @Query("Select Count(*) FROM PdfTable ")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pdf: Pdf)

    @Query("DELETE FROM PdfTable Where path = :path")
    suspend fun deleteRowFromPath(path:String)


    @Query("DELETE FROM PdfTable")
    suspend fun deleteAll()

    @Query("DELETE FROM PdfTable Where id = :ID")
    fun deleteRow(ID: Int)

}