package com.sudoajay.pdfviewer.activity.mainActivity.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pdf::class], version = 1 , exportSchema = false)
abstract class PdfRoomDatabase : RoomDatabase() {

    abstract fun appDao(): PdfDao


//  If You want to set default value then use that method

//    private class WordDatabaseCallback(
//        private val scope: CoroutineScope
//    ) : RoomDatabase.Callback() {
//
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    val wordDao = database.wordDao()
//
//                    // Delete all content here.
//                    wordDao.deleteAll()
//                }
//            }
//        }
//    }

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
                    "app_database"
                )

//                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}