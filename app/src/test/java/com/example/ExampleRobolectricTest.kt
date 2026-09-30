package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.email.EmailService
import com.example.data.local.AppDatabase
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.LeaveRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: LeaveRepository
    private lateinit var emailService: EmailService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        emailService = EmailService(db.emailLogDao())
        repository = LeaveRepository(
            userDao = db.userDao(),
            leaveRequestDao = db.leaveRequestDao(),
            notificationDao = db.notificationDao(),
            emailLogDao = db.emailLogDao(),
            emailService = emailService
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Leave Manager", appName)
    }

    @Test
    fun testUserRolesAndAuthentication() = runTest {
        val admin = UserEntity(
            id = 1,
            firstName = "Nadir",
            lastName = "Admin",
            username = "nadir",
            email = "nadir@company.com",
            password = "nadir@1990",
            role = UserRole.ADMIN,
            balanceAnnual = 30
        )
        val manager = UserEntity(
            id = 2,
            firstName = "Admin",
            lastName = "Manager",
            username = "admin",
            email = "admin@company.com",
            password = "admin@123",
            role = UserRole.MANAGER,
            balanceAnnual = 30
        )
        val employee = UserEntity(
            id = 3,
            firstName = "Ahmed",
            lastName = "Employee",
            username = "ahmed",
            email = "ahmed@company.com",
            password = "123",
            role = UserRole.EMPLOYEE,
            balanceAnnual = 25
        )

        db.userDao().insertUsers(listOf(admin, manager, employee))

        // Test login
        val loggedInAdmin = repository.login("nadir", "nadir@1990")
        assertNotNull(loggedInAdmin)
        assertEquals(UserRole.ADMIN, loggedInAdmin?.role)

        val loggedInManager = repository.login("admin@company.com", "admin@123")
        assertNotNull(loggedInManager)
        assertEquals(UserRole.MANAGER, loggedInManager?.role)

        val loggedInEmployee = repository.login("ahmed", "123")
        assertNotNull(loggedInEmployee)
        assertEquals(UserRole.EMPLOYEE, loggedInEmployee?.role)
    }

    @Test
    fun testSubstituteWorkflowAndManagerApprovalWithEmail() = runTest {
        val requester = UserEntity(
            id = 10,
            firstName = "Karim",
            lastName = "Salem",
            username = "karim",
            email = "karim@company.com",
            password = "123",
            role = UserRole.EMPLOYEE,
            balanceAnnual = 20
        )
        val substitute = UserEntity(
            id = 20,
            firstName = "Ahmed",
            lastName = "Ali",
            username = "ahmed",
            email = "ahmed@company.com",
            password = "123",
            role = UserRole.EMPLOYEE,
            balanceAnnual = 25
        )
        val manager = UserEntity(
            id = 30,
            firstName = "Manager",
            lastName = "Boss",
            username = "manager",
            email = "manager@company.com",
            password = "123",
            role = UserRole.MANAGER,
            balanceAnnual = 30
        )

        db.userDao().insertUsers(listOf(requester, substitute, manager))

        // 1. Submit leave request
        val result = repository.submitLeaveRequest(
            requester = requester,
            type = LeaveType.ANNUAL,
            startDate = "2026-11-01",
            endDate = "2026-11-05",
            days = 5,
            reason = "Vacation",
            substituteId = substitute.id
        )
        assertTrue(result.isSuccess)
        val reqId = result.getOrThrow()

        // Verify request is in PENDING_SUBSTITUTE
        val reqAfterSubmit = db.leaveRequestDao().getRequestById(reqId)
        assertEquals(LeaveStatus.PENDING_SUBSTITUTE, reqAfterSubmit?.status)

        // Verify email notification logged for managers
        val emailLogsAfterSubmit = db.emailLogDao().getAllEmailLogs().first()
        assertTrue(emailLogsAfterSubmit.any { it.triggerType == "REQUEST_SUBMITTED" })

        // 2. Substitute accepts
        repository.processSubstituteDecision(reqId, accepted = true, substituteUser = substitute)
        val reqAfterSubAccept = db.leaveRequestDao().getRequestById(reqId)
        assertEquals(LeaveStatus.PENDING_MANAGER, reqAfterSubAccept?.status)

        // 3. Manager approves
        repository.processManagerDecision(reqId, approved = true, managerUser = manager)
        val reqAfterManagerApprove = db.leaveRequestDao().getRequestById(reqId)
        assertEquals(LeaveStatus.APPROVED, reqAfterManagerApprove?.status)

        // Verify requester's balance was deducted (20 - 5 = 15)
        val updatedRequester = db.userDao().getUserById(requester.id)
        assertEquals(15, updatedRequester?.balanceAnnual)

        // Verify approval email was logged
        val allEmailLogs = db.emailLogDao().getAllEmailLogs().first()
        assertTrue(allEmailLogs.any { it.toEmail == requester.email && it.triggerType == "REQUEST_APPROVED" })
    }

    @Test
    fun testLowBalanceYearEndReminderEmail() = runTest {
        val lowBalanceEmp = UserEntity(
            id = 50,
            firstName = "Fatima",
            lastName = "Zahra",
            username = "fatima",
            email = "fatima@company.com",
            password = "123",
            role = UserRole.EMPLOYEE,
            balanceAnnual = 3 // Below 5 days threshold!
        )
        val normalEmp = UserEntity(
            id = 51,
            firstName = "Omar",
            lastName = "Tarek",
            username = "omar",
            email = "omar@company.com",
            password = "123",
            role = UserRole.EMPLOYEE,
            balanceAnnual = 22
        )
        db.userDao().insertUsers(listOf(lowBalanceEmp, normalEmp))

        val sentCount = repository.sendLowBalanceReminders(thresholdDays = 5)
        assertEquals(1, sentCount)

        val emailLogs = db.emailLogDao().getAllEmailLogs().first()
        val reminderLog = emailLogs.firstOrNull { it.triggerType == "LOW_BALANCE_REMINDER" }
        assertNotNull(reminderLog)
        assertEquals("fatima@company.com", reminderLog?.toEmail)
    }

    @Test
    fun testDefaultHREmailNotification() = runTest {
        val requester = UserEntity(
            id = 70,
            firstName = "Sami",
            lastName = "Bennani",
            username = "sami",
            email = "sami@company.com",
            password = "123",
            role = UserRole.EMPLOYEE,
            balanceAnnual = 15
        )
        val substitute = UserEntity(
            id = 71,
            firstName = "Zineb",
            lastName = "Amrani",
            username = "zineb",
            email = "zineb@company.com",
            password = "123",
            role = UserRole.EMPLOYEE,
            balanceAnnual = 15
        )
        db.userDao().insertUsers(listOf(requester, substitute))

        repository.submitLeaveRequest(
            requester = requester,
            type = LeaveType.ANNUAL,
            startDate = "2026-12-01",
            endDate = "2026-12-03",
            days = 3,
            reason = "Mission",
            substituteId = substitute.id
        )

        val logs = db.emailLogDao().getAllEmailLogs().first()
        val hrLog = logs.firstOrNull { it.toEmail.equals("Labo.bouzenoune.stage@gmail.com", ignoreCase = true) }
        assertNotNull(hrLog)
        assertEquals("REQUEST_SUBMITTED", hrLog?.triggerType)
    }
}
