package co.kr.myfitnote.core.networks;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Map;

import co.kr.myfitnote.BoardItem;
import co.kr.myfitnote.ClassItem;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.LoginResult;
import co.kr.myfitnote.client.ui.analysis.data.AnalysisData;
import co.kr.myfitnote.cm.data.ClientCreationResult;
import co.kr.myfitnote.cm.data.CommonResult;
import co.kr.myfitnote.measurement.data.ClientFinalResultData;
import co.kr.myfitnote.measurement.data.ClientMeasurementData;
import co.kr.myfitnote.measurement.data.ClientMeasurementResultData;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.TestLog;
import co.kr.myfitnote.model.User;
import co.kr.myfitnote.prescription.data.PrescriptionDetail;
import co.kr.myfitnote.views.fragments.result.ExerciseSeriesData;
import co.kr.myfitnote.views.rom.data.ClientRomResultData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitService {
    @POST("user/")
    Call<RetrofitStatus> createUser(@Body User user);

    @GET("user/")
    Call<User> getUser(@Body String token);

    @POST("user/login")
    Call<LoginResult> login(@Body co.kr.myfitnote.account.data.model.User user);

    @POST("user/question")
    Call<RetrofitStatus> createQuestionResult(
            @Body String json);

    @POST("user/walk")
    Call<RetrofitStatus> createExerciseResult(
            @Header("token") String token,
            @Body String json);

    @GET("user/exercise")
    Call<ArrayList<TestLog>> getUserTest(
            @Header("token") String token
    );

    @GET("user/exercise/format/series")
    Call<Map<String, ArrayList<ExerciseSeriesData>>> getExerciseSeriesData(
            @Query("client_id") String client_id);

    @POST("user/client")
    Call<ClientCreationResult> createClient(@Body Client client);

    @GET("user/client")
    Call<ArrayList<Client>> getClientList(
            @Header("Authorization") String token,
            @Query("name") String name
    );

    @POST("client/measurement")
    Call<ArrayList<Client>> createClientMeasurement(
            @Header("Authorization") String token,
            @Body ClientMeasurementResultData result
            );

    @GET("client/measurement")
    Call<ArrayList<ClientMeasurementData>> getClientMeasurements(
            @Header("Authorization") String token,
            @Query("name") String name,
            @Query("phone") String phone
    );

    @DELETE("client/measurement")
    Call<RetrofitStatus> deleteClientMeasurements(
            @Header("Authorization") String token,
            @Query("checkedResultIds") ArrayList<Integer> checkedResultIds
    );

    @Multipart
    @POST("client/rom")
    Call<ArrayList<Client>> createClientRom(
            @Header("Authorization") String token,
            @Part MultipartBody.Part resultImage,
            @Part("result") RequestBody result
    );


    // 최종 결과 산출 요청
    @GET("client/measurement/final/result")
    Call<ClientFinalResultData> getClientFinalResult(
            @Query("checkedResultIds") ArrayList<Integer> checkedResultIds
    );

    @GET("/prescription/today/")
    Call<ArrayList<PrescriptionDetail>> getClientTodayPrescription(
            @Header("Authorization") String token
    );

    @POST("/prescription/make/")
    Call<CommonResult> makePrescriptionWithAI(
            @Header("Authorization") String token,
            @Query("checkedResultIds") ArrayList<Integer> checkedResultIds,
            @Query("type") int type
    );

    /**
     * 오늘의 운동 종료 시 운동 이력 저장
     */
    @POST("/prescription/history/")
    Call<CommonResult> didTodayPrescription(
            @Query("client_id") String phone,
            @Query("prescription_id") int prescription_id
    );

    /**
     * 운동 통계
     * 전체 운동 진행 여부 (달력 스키마 처리 위함)과 운동 일차 제공
     */
    @GET("/prescription/analysis/")
    Call<AnalysisData> getPrescriptionAnalysis(
            @Query("client_id") String phone
    );

    /**
     * 게시판 목록 조회
     */
    @GET("/client/board/")
    Call<ArrayList<BoardItem>> getBoardList(
            @Query("page") int page
    );

    /**
     * 게시판 글 작성
     */
    @POST("/client/board/")
    Call<CommonResult> writeBoard(
            @Header("Authorization") String token,
            @Body BoardItem boardItem
    );

    /**
     * 클래스 조회
     */
    @GET("/client/class/")
    Call<ArrayList<ClassItem>> getClassList(
            @Query("page") int page
    );

}
