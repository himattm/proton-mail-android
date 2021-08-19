/*
 * Copyright (c) 2020 Proton Technologies AG
 * 
 * This file is part of ProtonMail.
 * 
 * ProtonMail is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ProtonMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ProtonMail. If not, see https://www.gnu.org/licenses/.
 */
package ch.protonmail.android.api.segments.label

import ch.protonmail.android.api.models.contacts.receive.ContactGroupsResponse
import ch.protonmail.android.api.models.messages.receive.LabelRequestBody
import ch.protonmail.android.api.models.messages.receive.LabelResponse
import ch.protonmail.android.api.models.messages.receive.LabelsResponse
import ch.protonmail.android.api.segments.RetrofitConstants.ACCEPT_HEADER_V1
import ch.protonmail.android.api.segments.RetrofitConstants.CONTENT_TYPE
import ch.protonmail.android.api.utils.Fields
import ch.protonmail.android.core.Constants
import me.proton.core.network.data.protonApi.BaseRetrofitApi
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

private const val PATH_LABEL_ID = "label_id"

interface LabelService : BaseRetrofitApi {

    @GET("v4/labels?" + Fields.Label.TYPE + "=" + Constants.LABEL_TYPE_MESSAGE_LABEL)
    @Headers(CONTENT_TYPE, ACCEPT_HEADER_V1)
    suspend fun fetchLabels(): LabelsResponse

    // this is coded here and not passed as a param because there is no point when it is a constant always
    @GET("v4/labels?" + Fields.Label.TYPE + "=" + Constants.LABEL_TYPE_CONTACT_GROUPS)
    suspend fun fetchContactGroups(): ContactGroupsResponse

    @GET("v4/labels?" + Fields.Label.TYPE + "=" + Constants.LABEL_TYPE_MESSAGE_FOLDERS)
    @Headers(CONTENT_TYPE, ACCEPT_HEADER_V1)
    suspend fun fetchFolders(): LabelsResponse

    @POST("v4/labels")
    @Headers(CONTENT_TYPE, ACCEPT_HEADER_V1)
    suspend fun createLabel(@Body label: LabelRequestBody): LabelResponse

    @PUT("v4/labels/{$PATH_LABEL_ID}")
    @Headers(CONTENT_TYPE, ACCEPT_HEADER_V1)
    suspend fun updateLabel(@Path(PATH_LABEL_ID) labelId: String, @Body label: LabelRequestBody): LabelResponse

    @DELETE("v4/labels/{$PATH_LABEL_ID}")
    @Headers(CONTENT_TYPE, ACCEPT_HEADER_V1)
    suspend fun deleteLabel(@Path(PATH_LABEL_ID) labelId: String)
}
