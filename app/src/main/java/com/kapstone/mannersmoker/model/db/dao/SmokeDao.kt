package com.kapstone.mannersmoker.model.db.dao

import com.kapstone.mannersmoker.model.data.post.PostDataClass
import com.kapstone.mannersmoker.model.data.post.PostModifyModel
import com.kapstone.mannersmoker.model.data.post.PostGetModel
import com.kapstone.mannersmoker.model.data.post.PostSendModel
import com.kapstone.mannersmoker.model.data.reply.ReplyDataClass
import com.kapstone.mannersmoker.model.data.reply.ReplyGetModel
import com.kapstone.mannersmoker.model.data.reply.ReplySendModel
import com.kapstone.mannersmoker.model.data.smoke.SendSmokeDataClass
import com.kapstone.mannersmoker.model.data.smoke.SmokeDataClass
import com.kapstone.mannersmoker.model.data.smoke.SmokeAreaDataClass
import com.kapstone.mannersmoker.model.data.user.Token
import com.kapstone.mannersmoker.model.data.user.User
import com.kapstone.mannersmoker.model.data.user.UserId
import retrofit2.Call
import retrofit2.http.*

// TODO : 추후 기능별로 분리 필요
interface SmokeDao {

    /* 1. 흡연 구역 & 흡연량 관련 메서드 */
    @POST("api/smoke-amount")
    fun sendSmokeData(@Body userId : UserId) : Call<SendSmokeDataClass>

    @GET("api/smoke-amount/{userId}")
    fun getDaySmokeData(@Path("userId") userId : Int, @Query ("day") day : Int, @Query ("month") month : Int, @Query ("year") year : Int) : Call<SmokeDataClass>

    @GET("api/smoke-amount/all/{userId}")
    fun getAllSmokeData(@Path("userId") userId : Int) : Call<SmokeDataClass>

    @GET("api/smoke-amount/month/{userId}")
    fun getMonthSmokeData(@Path("userId") userId : Int, @Query ("month") month : Int, @Query ("year") year : Int) : Call<SmokeDataClass>

    @GET("area/{area}")
    fun getSmokeArea(@Path("area") areaCode : Int) : Call<SmokeAreaDataClass>


    /* 2. 커뮤니티 게시글 관련 메서드 */
    @GET("api/post")
    fun getAllPost() : Call<PostDataClass>

    @POST("api/post")
    fun sendPost(@Body post : PostSendModel) : Call<PostGetModel>

    @POST("api/post/{postId}")
    fun getOnePost(@Path("postId") postId : Int) : Call<PostGetModel>

    @PUT("api/post/{postId}")
    fun modifyPost(@Path("postId") postId : Int, @Body post : PostModifyModel) : Call<PostGetModel>

    @DELETE("api/post/{postId}")
    fun deletePost(@Path("postId") postId : Int) : Call<PostGetModel>


    /* 3. 커뮤니티 댓글 관련 메서드 */
    @POST("api/reply")
    fun sendReply(@Body reply : ReplySendModel) : Call<ReplyGetModel>

    @GET("api/reply/{postId}")
    fun getReplys(@Path("postId") postId : Int) : Call<ReplyDataClass>

    @DELETE("api/reply/{replyId}")
    fun deleteReply(@Path("replyId") replyId : Int) : Call<ReplyGetModel>


    /* 4. 로그인 관련 메서드 */
    @GET("api/login/{token}")
    fun login(@Path("token") token : String) : Call<Token>

    @GET("user/kakao/oauth")
    fun getCode(@Query ("code") code : String) : Call<String>

    @GET("api/userinfo")
    fun getUserInfo(@Header ("Authorization") token : String) : Call<User> // 유저 이메일, 아이디(Long), 닉네임

    @GET("refresh")
    fun refreshToken(@Header("Authorization") token : String) : Call<Token>
}