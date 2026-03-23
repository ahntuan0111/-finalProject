package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.api.ReminderApiService
import com.example.finalproject.data.remote.model.ReminderSettingRequest
import com.example.finalproject.data.remote.model.ReminderSettingResponse
import com.example.finalproject.domain.repository.ReminderRepository
import com.example.finalproject.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val api: ReminderApiService
) : ReminderRepository {

    override fun getMySettings(): Flow<Resource<ReminderSettingResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMySettings()
            if(response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Dữ liệu trả về rỗng"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    override fun updateMySettings(request: ReminderSettingRequest): Flow<Resource<ReminderSettingResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.updateMySettings(request)
            if(response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Chưa thể lưu dữ liệu"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi lưu dữ liệu"))
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối Internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }
}
