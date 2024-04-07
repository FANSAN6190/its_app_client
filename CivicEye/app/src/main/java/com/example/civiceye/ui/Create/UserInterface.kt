import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @POST("/api/whistle/login")
    suspend fun login(@Body userId: String): Response<LoginResponse>

    @POST("/api/whistle/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>
}

data class LoginRequest(val userId: String)
data class SignupRequest(val fullName: String, val userId: String)

data class LoginResponse(val success: Boolean, val userId: String)
data class SignupResponse(val success: Boolean, val userId: String)