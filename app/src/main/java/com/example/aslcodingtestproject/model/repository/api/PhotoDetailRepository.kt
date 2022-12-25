package com.example.aslcodingtestproject.model.repository.api

import com.example.aslcodingtestproject.constant.IConstants
import com.example.aslcodingtestproject.model.remote.Resource
import com.example.aslcodingtestproject.model.remote.performNonTokenNormalGetOperation
import com.example.aslcodingtestproject.model.remote.responseobj.GetPhotoDetailRespItem
import com.example.aslcodingtestproject.model.remote.service.NonTokenService
import com.example.aslcodingtestproject.model.repository.BasePhotoDetailRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// get data
class PhotoDetailRepository @Inject constructor(
    private val apiService: NonTokenService,
): BasePhotoDetailRepository {

    override suspend fun getPhotoDetailFromApi(id: String): Resource<ArrayList<GetPhotoDetailRespItem>> {
        return performNonTokenNormalGetOperation(
            networkCall = {
                val nowDateTime = DateTimeFormatter.ofPattern(
                    IConstants.BASIC.ASYMMETRIC_KEY_DATE_TIME_FORMAT
                ).format(ZonedDateTime.now())
                apiService.getImgDetail(nowDateTime, id = id)
            },
        )
    }

}