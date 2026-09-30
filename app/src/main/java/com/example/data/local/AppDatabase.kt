package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EmailLogEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        LeaveRequestEntity::class,
        NotificationEntity::class,
        EmailLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun leaveRequestDao(): LeaveRequestDao
    abstract fun notificationDao(): NotificationDao
    abstract fun emailLogDao(): EmailLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leave_management_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val requestDao = database.leaveRequestDao()
            val notifDao = database.notificationDao()
            val emailDao = database.emailLogDao()

            val nadir = UserEntity(
                id = 1,
                firstName = "Nadir",
                lastName = "(Admin)",
                username = "nadir",
                email = "Labo.bouzenoune.stage@gmail.com",
                password = "nadir@1990",
                role = UserRole.ADMIN,
                balanceAnnual = 30,
                balanceSick = 15,
                balanceRtt = 5
            )
            val adminManager = UserEntity(
                id = 2,
                firstName = "Admin",
                lastName = "(Manager)",
                username = "admin",
                email = "admin@company.com",
                password = "admin@123",
                role = UserRole.MANAGER,
                balanceAnnual = 30,
                balanceSick = 15,
                balanceRtt = 5
            )
            val ahmed = UserEntity(
                id = 3,
                firstName = "أحمد",
                lastName = "الموظف",
                username = "ahmed",
                email = "ahmed@company.com",
                password = "123",
                role = UserRole.EMPLOYEE,
                balanceAnnual = 25,
                balanceSick = 10,
                balanceRtt = 3
            )
            val fatima = UserEntity(
                id = 4,
                firstName = "فاطمة",
                lastName = "بن علي",
                username = "fatima",
                email = "fatima@company.com",
                password = "123",
                role = UserRole.EMPLOYEE,
                balanceAnnual = 4, // Low balance to trigger year-end email reminder!
                balanceSick = 8,
                balanceRtt = 2
            )
            val karim = UserEntity(
                id = 5,
                firstName = "كريم",
                lastName = "سالم",
                username = "karim",
                email = "karim@company.com",
                password = "123",
                role = UserRole.EMPLOYEE,
                balanceAnnual = 28,
                balanceSick = 15,
                balanceRtt = 5
            )

            userDao.insertUsers(listOf(nadir, adminManager, ahmed, fatima, karim))

            // Sample Request 1: Karim requests leave, assigned Ahmed as substitute (PendingSubstitute)
            val req1 = LeaveRequestEntity(
                id = 101,
                userId = karim.id,
                userName = karim.fullName,
                userEmail = karim.email,
                type = LeaveType.ANNUAL,
                startDate = "2026-10-15",
                endDate = "2026-10-18",
                days = 4,
                reason = "عطلة عائلية قصيرة / Court séjour familial",
                substituteId = ahmed.id,
                substituteName = ahmed.fullName,
                status = LeaveStatus.PENDING_SUBSTITUTE,
                createdAt = "2026-10-01 09:30"
            )

            // Sample Request 2: Fatima requests sick leave, substitute already accepted -> PendingManager
            val req2 = LeaveRequestEntity(
                id = 102,
                userId = fatima.id,
                userName = fatima.fullName,
                userEmail = fatima.email,
                type = LeaveType.SICK,
                startDate = "2026-10-05",
                endDate = "2026-10-07",
                days = 3,
                reason = "موعد طبي واستشفاء / Rendez-vous médical",
                substituteId = karim.id,
                substituteName = karim.fullName,
                status = LeaveStatus.PENDING_MANAGER,
                createdAt = "2026-10-02 11:15"
            )

            // Sample Request 3: Ahmed previously approved annual leave
            val req3 = LeaveRequestEntity(
                id = 103,
                userId = ahmed.id,
                userName = ahmed.fullName,
                userEmail = ahmed.email,
                type = LeaveType.ANNUAL,
                startDate = "2026-08-10",
                endDate = "2026-08-14",
                days = 5,
                reason = "عطلة صيفية / Congé d'été",
                substituteId = karim.id,
                substituteName = karim.fullName,
                status = LeaveStatus.APPROVED,
                createdAt = "2026-08-01 14:00"
            )

            // Additional multi-month records for trend and leave-type charts
            val req4 = LeaveRequestEntity(
                id = 104,
                userId = karim.id,
                userName = karim.fullName,
                userEmail = karim.email,
                type = LeaveType.ANNUAL,
                startDate = "2026-05-12",
                endDate = "2026-05-16",
                days = 5,
                reason = "عطلة ربيعية / Congé de printemps",
                substituteId = ahmed.id,
                substituteName = ahmed.fullName,
                status = LeaveStatus.APPROVED,
                createdAt = "2026-05-01 10:00"
            )

            val req5 = LeaveRequestEntity(
                id = 105,
                userId = ahmed.id,
                userName = ahmed.fullName,
                userEmail = ahmed.email,
                type = LeaveType.RTT,
                startDate = "2026-06-18",
                endDate = "2026-06-20",
                days = 3,
                reason = "استرجاع ساعات عمل إضافية / Récupération RTT",
                substituteId = karim.id,
                substituteName = karim.fullName,
                status = LeaveStatus.APPROVED,
                createdAt = "2026-06-10 11:30"
            )

            val req6 = LeaveRequestEntity(
                id = 106,
                userId = fatima.id,
                userName = fatima.fullName,
                userEmail = fatima.email,
                type = LeaveType.ANNUAL,
                startDate = "2026-07-05",
                endDate = "2026-07-14",
                days = 10,
                reason = "عطلة الصيف السنوية / Vacances annuelles",
                substituteId = ahmed.id,
                substituteName = ahmed.fullName,
                status = LeaveStatus.APPROVED,
                createdAt = "2026-06-20 09:00"
            )

            val req7 = LeaveRequestEntity(
                id = 107,
                userId = ahmed.id,
                userName = ahmed.fullName,
                userEmail = ahmed.email,
                type = LeaveType.SICK,
                startDate = "2026-09-08",
                endDate = "2026-09-09",
                days = 2,
                reason = "وعكة صحية / Maladie",
                substituteId = karim.id,
                substituteName = karim.fullName,
                status = LeaveStatus.APPROVED,
                createdAt = "2026-09-08 08:30"
            )

            val req8 = LeaveRequestEntity(
                id = 108,
                userId = karim.id,
                userName = karim.fullName,
                userEmail = karim.email,
                type = LeaveType.RTT,
                startDate = "2026-11-04",
                endDate = "2026-11-05",
                days = 2,
                reason = "طلب استرجاع عاجل / RTT urgent",
                substituteId = ahmed.id,
                substituteName = ahmed.fullName,
                status = LeaveStatus.REJECTED,
                createdAt = "2026-10-25 15:00"
            )

            val req9 = LeaveRequestEntity(
                id = 109,
                userId = fatima.id,
                userName = fatima.fullName,
                userEmail = fatima.email,
                type = LeaveType.SICK,
                startDate = "2026-03-15",
                endDate = "2026-03-17",
                days = 3,
                reason = "راحة طبية / Repos médical",
                substituteId = karim.id,
                substituteName = karim.fullName,
                status = LeaveStatus.APPROVED,
                createdAt = "2026-03-14 12:00"
            )

            val req10 = LeaveRequestEntity(
                id = 110,
                userId = ahmed.id,
                userName = ahmed.fullName,
                userEmail = ahmed.email,
                type = LeaveType.ANNUAL,
                startDate = "2026-01-20",
                endDate = "2026-01-23",
                days = 4,
                reason = "عطلة شتوية / Vacances d'hiver",
                substituteId = fatima.id,
                substituteName = fatima.fullName,
                status = LeaveStatus.APPROVED,
                createdAt = "2026-01-10 16:00"
            )

            requestDao.insertRequests(listOf(req1, req2, req3, req4, req5, req6, req7, req8, req9, req10))

            // Sample notifications
            notifDao.insertNotification(
                NotificationEntity(
                    targetUserId = ahmed.id,
                    messageAr = "طلب تعويض جديد من كريم سالم، يرجى مراجعة الطلب وقبوله أو رفضه.",
                    messageFr = "Demande de remplacement de Karim Salem. Veuillez accepter ou refuser.",
                    read = false
                )
            )
            notifDao.insertNotification(
                NotificationEntity(
                    targetRole = "MANAGER",
                    messageAr = "طلب عطلة جديد قيد انتظار موافقة الإدارة من الموظفة فاطمة بن علي.",
                    messageFr = "Nouvelle demande de congé en attente de validation de Fatima Ben Ali.",
                    read = false
                )
            )

            // Sample Initial Sent Email Log
            emailDao.insertEmailLog(
                EmailLogEntity(
                    toEmail = ahmed.email,
                    recipientName = ahmed.fullName,
                    subject = "تمت الموافقة على طلب العطلة الخاص بك / Demande approuvée",
                    body = "مرحباً ${ahmed.fullName}، نحيطك علماً بأنه تمت الموافقة على طلب العطلة السنوية (5 أيام) من 2026-08-10 إلى 2026-08-14.",
                    triggerType = "REQUEST_APPROVED",
                    status = "SENT"
                )
            )
        }
    }
}
