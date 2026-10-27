package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import com.example.data.model.WasteCategory

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole?): String? = value?.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole? = value?.let { enumValueOf<UserRole>(it) }

    @TypeConverter
    fun fromWasteCategory(value: WasteCategory?): String? = value?.name

    @TypeConverter
    fun toWasteCategory(value: String?): WasteCategory? = value?.let { enumValueOf<WasteCategory>(it) }

    @TypeConverter
    fun fromRequestUrgency(value: RequestUrgency?): String? = value?.name

    @TypeConverter
    fun toRequestUrgency(value: String?): RequestUrgency? = value?.let { enumValueOf<RequestUrgency>(it) }

    @TypeConverter
    fun fromCollectionStatus(value: CollectionStatus?): String? = value?.name

    @TypeConverter
    fun toCollectionStatus(value: String?): CollectionStatus? = value?.let { enumValueOf<CollectionStatus>(it) }

    @TypeConverter
    fun fromDriverAppStatus(value: DriverApplicationStatus?): String? = value?.name

    @TypeConverter
    fun toDriverAppStatus(value: String?): DriverApplicationStatus? = value?.let { enumValueOf<DriverApplicationStatus>(it) }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? = value?.let { enumValueOf<PaymentMethod>(it) }

    @TypeConverter
    fun fromBusinessVerificationStatus(value: com.example.data.model.BusinessVerificationStatus?): String? = value?.name

    @TypeConverter
    fun toBusinessVerificationStatus(value: String?): com.example.data.model.BusinessVerificationStatus? =
        value?.let { enumValueOf<com.example.data.model.BusinessVerificationStatus>(it) }

    @TypeConverter
    fun fromAssignmentMethod(value: com.example.data.model.AssignmentMethod?): String? = value?.name

    @TypeConverter
    fun toAssignmentMethod(value: String?): com.example.data.model.AssignmentMethod? =
        value?.let { enumValueOf<com.example.data.model.AssignmentMethod>(it) }
}
