package ir.farhangi.core.network.demo

import ir.farhangi.core.common.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoAuthGatewayTest {

    private val gateway = DemoAuthGateway()

    @Test
    fun sendOtp_rejectsShortPhone() = runTest {
        val result = gateway.sendOtp("123")
        assertTrue(result is Result.Error)
    }

    @Test
    fun verifyOtp_acceptsDemoCode() = runTest {
        gateway.sendOtp("+989121234567")
        val result = gateway.verifyOtp("+989121234567", DemoAuthGateway.DEMO_OTP_CODE)
        assertTrue(result is Result.Success)
        assertEquals("+989121234567", (result as Result.Success).data.phone)
        assertEquals("+989121234567", gateway.observeSession().first()?.phone)
    }

    @Test
    fun signOut_clearsSession() = runTest {
        gateway.sendOtp("+989121234567")
        gateway.verifyOtp("+989121234567", DemoAuthGateway.DEMO_OTP_CODE)
        gateway.signOut()
        assertNull(gateway.observeSession().first())
    }
}
