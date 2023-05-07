package com.swipecare.payments.network.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swipecare.payments.fund.*
import com.swipecare.payments.model.*
import com.swipecare.payments.model.commision.CommissionDataJson
import com.swipecare.payments.model.billlverify.Gas
import com.swipecare.payments.model.bodypara.CahwithdrawBody
import com.swipecare.payments.model.bodypara.EnquiryBody
import com.swipecare.payments.model.dth.Dthinfoores
import com.swipecare.payments.model.forget.Otpforget
import com.swipecare.payments.model.forget.Otpfrsubmit
import com.swipecare.payments.model.menuReports.aepsReport.AepsReportResponse
import com.swipecare.payments.model.menuReports.payoutReport.AccountStatementResponse
import com.swipecare.payments.model.menuReports.payoutReport.PayoutReportResponse
import com.swipecare.payments.model.payout.OtpSentStatus
import com.swipecare.payments.model.payout.WalletTransferOtpSentStatus
import com.swipecare.payments.model.payout.WalletTransferStatus
import com.swipecare.payments.profit.Profitresponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

const val baseUrl = "https://rechargepay.site/service/"
const val baseUrlForOperatorAndCircle = "https://planapi.in/"

var okHttpClientOneMinuteTimeOut = OkHttpClient().newBuilder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

val retrofitForSwipeApi = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(okHttpClientOneMinuteTimeOut)
    .baseUrl(baseUrl)
    .build()

val retrofitForPlanApi = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(baseUrlForOperatorAndCircle)
    .build()

interface SwipeApiService {
    @GET("api/android/recharge/pay?")
    suspend fun doMobileRecharge(
        @Query("apptoken") appToken: String,
        @Query("user_id") userId: String,
        @Query("provider_id") provider: String,
        @Query("amount") amount: String,
        @Query("number") mobileNumber: String,
    ): MobileRechargeResponse

    @POST("api/android/aeps/BE")
    suspend fun balanceEnquiry(
        @Body
        enquiryBody: EnquiryBody
    ): AEPSBalanceEnquiryResponse

//    @POST("api/android/aeps/MS")
//    suspend fun miniStatement(
//        @Body
//        ministBody: MinistBody
//    ): AEPSMiniStatementResponse

    @POST("api/android/aeps/CW")
    suspend fun cashWithdraw(
        @Body
        cahwithdrawBody: CahwithdrawBody
    ): AEPSCashWithdrawResponse

    @POST("api/android/checkmerchantonboard")
    suspend fun getUserOnboardingStatus(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
    ): OnboardingStatusResponse

    @POST("api/android/user_onboard_details")
    suspend fun getUserOnboardingDetails(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
    ): UserOnboardingDetailsResponse

    @POST("api/android/payout/IMPS")
    suspend fun impsMoneyTransfer(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("name") name: String,
        @Query("mobile") mobileNumber: String,
        @Query("account_number") accountNumber: String,
        @Query("ifsc") ifscCode: String,
        @Query("transactionAmount") transactionAmount: String,
    ): OtpSentStatus

    @POST("api/android/payout/IMPS")
    suspend fun impsMoneyTransferSendOtp(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("name") name: String,
        @Query("mobile") mobileNumber: String,
        @Query("account_number") accountNumber: String,
        @Query("ifsc") ifscCode: String,
        @Query("transactionAmount") transactionAmount: String,
        @Query("otp") otp: String,
    ): PayoutResponse

    @POST("api/android/payout/RTGS")
    suspend fun rtgsMoneyTransfer(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("name") name: String,
        @Query("mobile") mobileNumber: String,
        @Query("account_number") accountNumber: String,
        @Query("ifsc") ifscCode: String,
        @Query("transactionAmount") transactionAmount: String,
    ): OtpSentStatus

    @POST("api/android/payout/RTGS")
    suspend fun rtgsMoneyTransferSendOtp(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("name") name: String,
        @Query("mobile") mobileNumber: String,
        @Query("account_number") accountNumber: String,
        @Query("ifsc") ifscCode: String,
        @Query("transactionAmount") transactionAmount: String,
        @Query("otp") otp: String,
    ): PayoutResponse

    @POST("api/android/payout/NEFT")
    suspend fun neftMoneyTransfer(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("name") name: String,
        @Query("mobile") mobileNumber: String,
        @Query("account_number") accountNumber: String,
        @Query("ifsc") ifscCode: String,
        @Query("transactionAmount") transactionAmount: String,
    ): OtpSentStatus

    @POST("api/android/payout/NEFT")
    suspend fun neftMoneyTransferSendOtp(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("transactionAmount") transactionAmount: String,
        @Query("name") name: String,
        @Query("mobile") mobileNumber: String,
        @Query("ifsc") ifscCode: String,
        @Query("account_number") accountNumber: String,
        @Query("otp") otp: String,
    ): PayoutResponse

    // Menu Reports
    @POST("api/android/transactions/payoutreport")
    suspend fun getPayoutReport(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("start") start: Int
    ): PayoutReportResponse

    @POST("api/android/transactions/aepsstatement")
    suspend fun getAepsReport(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
    ): AepsReportResponse

    @POST("api/android/transactions/accountstatement")
    suspend fun getAccountStatementReport(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
    ): AccountStatementResponse

    @POST("api/android/statement/fetch")
    suspend fun getUserStatementReport(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
    ): Userlistres

    @POST("api/android/wallettransfer")
    suspend fun walletTransfer(
        @Query("apptoken") appToken: String,
        @Query("user_id") userId: String,
        @Query("amount") amount: Double,
    ): WalletTransferOtpSentStatus

    @POST("api/android/wallettransfer")
    suspend fun walletTransferEnterOtp(
        @Query("apptoken") appToken: String,
        @Query("user_id") userId: String,
        @Query("amount") amount: Double,
        @Query("otp") otp: String,
    ): WalletTransferStatus

    //fetch bill for gas
    @POST("api/android/paysprintgetbill/gass")
    suspend fun getgasbill(
        @Query("apptoken") appToken: String,
        @Query("user_id") userId: String,
        @Query("provider_id") provider_id: String,
        @Query("number") number: String,

    ): Gas
    //paybill
    @POST("api/android/paysprintpaybill/gass")
    suspend fun paygasbill(
        @Query("apptoken") appToken: String,
        @Query("user_id") userId: String,
        @Query("provider_id") provider_id: String,
        @Query("number") number: String,
        @Query("biller") biller: String,
        @Query("amount") amount: String,
        @Query("number") duedate: String,
        @Query("mobile") mobile: String,
        ): Gas
    @POST("api/android/addfundbharatt")
    suspend fun addfundbharat(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("amount") amount: String,
        ): Profitresponse

    @POST("api/android/bharatverify")
    suspend fun bharatverify(
        @Body
        bharatverify : Bharatverify
    ): Bharatresponse

    @POST("api/android/addfundupi")
    suspend fun addfundupi(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("amount") amount: String,
    ): Upiresponse

    @POST("api/android/upiverify")
    suspend fun upiverifyr(
        @Body
        upiverify: Upiverify
    ): Upiverifyres
    //status
    @POST("api/android/recharge/status")
    suspend fun status(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("txnid") txnid: String
    ): Statusres

    //dth info
    @POST("api/android/recharge/dthinfo")
    suspend fun dthinfo(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("provider_id") provider_id: String,
        @Query("mobileno") mobileno: String
    ): Dthinfoores
    //dth plan
    @POST("api/android/recharge/dthplan")
    suspend fun dthplan(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("provider_id") provider_id: String,
        @Query("mobileno") mobileno: String
    ): Dthinfoores
    // Commission
    @GET("api/android/commission")
    suspend fun getCommissionData(
        @Query("apptoken") appToken: String,
        @Query("user_id") userId: String,
    ): CommissionDataJson
    //forget passwrd
//    @GET("api/android/passwordreset")
//    suspend fun otpsentforget(
//        @Query("mobile") mobile: String,
//    ): Otpforget
    //forget passwrd otp submit
    @GET("api/android/passwordreset")
    suspend fun otpsentforgetsubmit(
        @Query("mobile") mobile: String,
        @Query("otp") otp: String
    ): Otpfrsubmit
    //news
    @POST("api/android/newmember")
    suspend fun useradd(
        @Query("user_id") userId: String,
        @Query("apptoken") appToken: String,
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("address") address: String,
        @Query("city") city: String,
        @Query("state") state: String,
        @Query("pincode") pincode: String,
        @Query("pancard") pancard: String,
        @Query("aadharcard") aadharcard: String,
        @Query("role_id") role_id: String,
        @Query("name") name: String
    ): Useradd

}

interface PlanApiService {
    @GET("api/Mobile/OperatorFetchNew")
    suspend fun getOperatorAndCircle(
        @Query("ApiUserID") apiUserId: String,
        @Query("ApiPassword") apiPassword: String,
        @Query("Mobileno") mobileNumber: String,
    ): OperatorAndCircle

    @GET("api/Mobile/Operatorplan")
    suspend fun getOperatorPlans(
        @Query("apimember_id") apiUserId: String,
        @Query("api_password") apiPassword: String,
        @Query("cricle") circle: String,
        @Query("operatorcode") operatorCode: String,
    ): OperatorPlan


    @GET("api/Mobile/RofferCheck")
    suspend fun getRechargeOffers(
        @Query("apimember_id") apiUserId: String,
        @Query("api_password") apiPassword: String,
        @Query("mobile_no") mobileNumber: String,
        @Query("operator_code") operatorCode: String,
    ): RoffersResponse

    //commision


}

object SwipeApi {
    val retrofitService: SwipeApiService by lazy {
        retrofitForSwipeApi.create(SwipeApiService::class.java)
    }
}

object PlanApi {
    val planRetrofitService: PlanApiService by lazy {
        retrofitForPlanApi.create(PlanApiService::class.java)
    }
}