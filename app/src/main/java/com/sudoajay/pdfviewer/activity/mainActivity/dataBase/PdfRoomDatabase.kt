package com.sudoajay.pdfviewer.activity.mainActivity.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pdf::class], version = 1 , exportSchema = false)
abstract class PdfRoomDatabase : RoomDatabase() {

    abstract fun pdfDao(): PdfDao


    companion object {
        @Volatile
        private var INSTANCE: PdfRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): PdfRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PdfRoomDatabase::class.java,
                    "pdf_database"
                )

                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}