package com.example.mukfinalproject.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mukfinalproject.model.MukDateConverter
import com.example.mukfinalproject.model.MukProfile

@Database(entities = [MukProfile::class], version = 1)
@TypeConverters(MukDateConverter::class)
abstract class MukFinalProjectDatabase: RoomDatabase() {
    abstract fun mukProfileDao(): MukProfileDao

    companion object {
        private var mukInstance: MukFinalProjectDatabase? = null

        fun mukGetInstance(context: Context): MukFinalProjectDatabase {
            if (mukInstance == null) {
                mukInstance = Room.databaseBuilder(context.applicationContext,
                    MukFinalProjectDatabase::class.java,
                    "MukFinalProject"
                        )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return mukInstance as MukFinalProjectDatabase
        }
    }
}