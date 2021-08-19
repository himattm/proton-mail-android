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
import me.proton.core.domain.entity.UserId
import me.proton.core.network.data.ApiProvider
import me.proton.core.network.domain.ApiResult

class LabelApi(private val apiProvider: ApiProvider) : LabelApiSpec {

    override suspend fun fetchLabels(userId: UserId): ApiResult<LabelsResponse> =
        apiProvider.get<LabelService>(userId).invoke {
            fetchLabels()
        }

    override suspend fun fetchContactGroups(userId: UserId): ApiResult<ContactGroupsResponse> =
        apiProvider.get<LabelService>(userId).invoke {
            fetchContactGroups()
        }

    override suspend fun fetchFolders(userId: UserId): ApiResult<ContactGroupsResponse> =
        apiProvider.get<LabelService>(userId).invoke {
            fetchFolders()
        }

    override suspend fun createLabel(userId: UserId, label: LabelRequestBody): ApiResult<LabelResponse> =
        apiProvider.get<LabelService>(userId).invoke {
            createLabel(label)
        }

    override suspend fun updateLabel(userId: UserId, labelId: String, label: LabelRequestBody):
        ApiResult<LabelResponse> = apiProvider.get<LabelService>(userId).invoke {
        updateLabel(labelId, label)
    }

    override suspend fun deleteLabel(userId: UserId, labelId: String): ApiResult<Unit> =
        apiProvider.get<LabelService>(userId).invoke {
            deleteLabel(labelId)
        }

}
