package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        BusinessEntity::class,
        VehicleEntity::class,
        DriverApplicationEntity::class,
        CollectionRequestEntity::class,
        CollectionAssignmentEntity::class,
        DisposalRecordEntity::class,
        ComplaintEntity::class,
        AuditLogEntity::class,
        DisposalFacilityEntity::class,
        ChatMessageEntity::class,
        ScheduledCollectionPlanEntity::class,
        PendingRegistrationEntity::class,
        UserSessionEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun businessDao(): BusinessDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun driverApplicationDao(): DriverApplicationDao
    abstract fun collectionRequestDao(): CollectionRequestDao
    abstract fun collectionAssignmentDao(): CollectionAssignmentDao
    abstract fun disposalRecordDao(): DisposalRecordDao
    abstract fun complaintDao(): ComplaintDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun disposalFacilityDao(): DisposalFacilityDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun scheduledCollectionPlanDao(): ScheduledCollectionPlanDao
    abstract fun pendingRegistrationDao(): PendingRegistrationDao
    abstract fun userSessionDao(): UserSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_waste_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
