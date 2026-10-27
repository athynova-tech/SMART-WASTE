package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :identifier OR email = :identifier OR phone = :identifier OR licenseId = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("UPDATE users SET fcmToken = :token WHERE id = :userId")
    suspend fun updateFcmToken(userId: String, token: String)
}

@Dao
interface DriverApplicationDao {
    @Query("SELECT * FROM driver_applications ORDER BY appliedAt DESC")
    fun getAllApplications(): Flow<List<DriverApplicationEntity>>

    @Query("SELECT * FROM driver_applications WHERE status = :status ORDER BY appliedAt DESC")
    fun getApplicationsByStatus(status: DriverApplicationStatus): Flow<List<DriverApplicationEntity>>

    @Query("SELECT * FROM driver_applications WHERE id = :id LIMIT 1")
    suspend fun getApplicationById(id: String): DriverApplicationEntity?

    @Query("SELECT * FROM driver_applications WHERE assignedDriverId = :driverId LIMIT 1")
    suspend fun getApplicationByDriverId(driverId: String): DriverApplicationEntity?

    @Query("SELECT * FROM driver_applications WHERE status = 'APPROVED'")
    fun getApprovedDrivers(): Flow<List<DriverApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: DriverApplicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(apps: List<DriverApplicationEntity>)

    @Update
    suspend fun updateApplication(app: DriverApplicationEntity)
}

@Dao
interface CollectionRequestDao {
    @Query("SELECT * FROM collection_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<CollectionRequestEntity>>

    @Query("SELECT * FROM collection_requests WHERE residentId = :residentId ORDER BY createdAt DESC")
    fun getRequestsForResident(residentId: String): Flow<List<CollectionRequestEntity>>

    @Query("SELECT * FROM collection_requests WHERE assignedDriverId = :driverId ORDER BY createdAt DESC")
    fun getRequestsForDriver(driverId: String): Flow<List<CollectionRequestEntity>>

    @Query("SELECT * FROM collection_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestById(id: String): CollectionRequestEntity?

    @Query("SELECT * FROM collection_requests WHERE trackingId = :trackingId LIMIT 1")
    suspend fun getRequestByTrackingId(trackingId: String): CollectionRequestEntity?

    @Query("SELECT * FROM collection_requests WHERE collectionStatus IN ('REQUESTED', 'PAID', 'WAITING_ASSIGNMENT') ORDER BY createdAt ASC")
    fun getPendingRequestsForDispatch(): Flow<List<CollectionRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: CollectionRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<CollectionRequestEntity>)

    @Update
    suspend fun updateRequest(request: CollectionRequestEntity)
}

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE residentId = :residentId ORDER BY createdAt DESC")
    fun getComplaintsForResident(residentId: String): Flow<List<ComplaintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaints(complaints: List<ComplaintEntity>)

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface DisposalFacilityDao {
    @Query("SELECT * FROM disposal_facilities")
    fun getAllFacilities(): Flow<List<DisposalFacilityEntity>>

    @Query("SELECT * FROM disposal_facilities WHERE id = :facilityId LIMIT 1")
    suspend fun getFacilityById(facilityId: String): DisposalFacilityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacilities(facilities: List<DisposalFacilityEntity>)

    @Update
    suspend fun updateFacility(facility: DisposalFacilityEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE requestId = :requestId ORDER BY timestamp ASC")
    fun getMessagesForRequest(requestId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT COUNT(*) FROM chat_messages WHERE requestId = :requestId AND senderRole != :myRole AND isRead = 0")
    fun getUnreadCount(requestId: String, myRole: UserRole): Flow<Int>

    @Query("SELECT * FROM chat_messages WHERE requestId = :requestId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMessage(requestId: String): Flow<ChatMessageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE requestId = :requestId AND senderRole != :myRole")
    suspend fun markMessagesAsRead(requestId: String, myRole: UserRole)
}

@Dao
interface ScheduledCollectionPlanDao {
    @Query("SELECT * FROM scheduled_collection_plans ORDER BY createdAt DESC")
    fun getAllPlans(): Flow<List<ScheduledCollectionPlanEntity>>

    @Query("SELECT * FROM scheduled_collection_plans WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getPlansForBusiness(businessId: String): Flow<List<ScheduledCollectionPlanEntity>>

    @Query("SELECT * FROM scheduled_collection_plans WHERE id = :id LIMIT 1")
    suspend fun getPlanById(id: String): ScheduledCollectionPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: ScheduledCollectionPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<ScheduledCollectionPlanEntity>)

    @Update
    suspend fun updatePlan(plan: ScheduledCollectionPlanEntity)

    @Query("DELETE FROM scheduled_collection_plans WHERE id = :id")
    suspend fun deletePlan(id: String)
}

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses ORDER BY createdAt DESC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE verificationStatus = :status ORDER BY createdAt DESC")
    fun getBusinessesByStatus(status: com.example.data.model.BusinessVerificationStatus): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE businessId = :businessId LIMIT 1")
    suspend fun getBusinessById(businessId: String): BusinessEntity?

    @Query("SELECT * FROM businesses WHERE userId = :userId LIMIT 1")
    suspend fun getBusinessByUserId(userId: String): BusinessEntity?

    @Query("SELECT * FROM businesses WHERE tin = :tin LIMIT 1")
    suspend fun getBusinessByTin(tin: String): BusinessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinesses(businesses: List<BusinessEntity>)

    @Update
    suspend fun updateBusiness(business: BusinessEntity)
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE vehicleId = :vehicleId LIMIT 1")
    suspend fun getVehicleById(vehicleId: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE assignedDriverId = :driverId LIMIT 1")
    suspend fun getVehicleByDriverId(driverId: String): VehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)
}

@Dao
interface CollectionAssignmentDao {
    @Query("SELECT * FROM collection_assignments ORDER BY assignedAt DESC")
    fun getAllAssignments(): Flow<List<CollectionAssignmentEntity>>

    @Query("SELECT * FROM collection_assignments WHERE requestId = :requestId LIMIT 1")
    suspend fun getAssignmentForRequest(requestId: String): CollectionAssignmentEntity?

    @Query("SELECT * FROM collection_assignments WHERE driverId = :driverId AND status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    fun getActiveAssignmentsForDriver(driverId: String): Flow<List<CollectionAssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: CollectionAssignmentEntity)

    @Update
    suspend fun updateAssignment(assignment: CollectionAssignmentEntity)
}

@Dao
interface DisposalRecordDao {
    @Query("SELECT * FROM disposal_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<DisposalRecordEntity>>

    @Query("SELECT * FROM disposal_records WHERE facilityId = :facilityId ORDER BY timestamp DESC")
    fun getRecordsForFacility(facilityId: String): Flow<List<DisposalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DisposalRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<DisposalRecordEntity>)
}

@Dao
interface PendingRegistrationDao {
    @Query("SELECT * FROM pending_registrations WHERE email = :email LIMIT 1")
    suspend fun getPendingRegistration(email: String): PendingRegistrationEntity?

    @Query("SELECT * FROM pending_registrations ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestPendingRegistration(): PendingRegistrationEntity?

    @Query("SELECT * FROM pending_registrations")
    fun getAllPendingRegistrations(): Flow<List<PendingRegistrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingRegistration(pending: PendingRegistrationEntity)

    @Query("DELETE FROM pending_registrations WHERE email = :email")
    suspend fun deletePendingRegistration(email: String)

    @Query("DELETE FROM pending_registrations")
    suspend fun clearAllPendingRegistrations()
}

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_sessions ORDER BY lastActiveTimestamp DESC LIMIT 1")
    suspend fun getActiveSession(): UserSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: UserSessionEntity)

    @Query("DELETE FROM user_sessions")
    suspend fun clearSession()
}


