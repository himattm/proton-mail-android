/*
 * Copyright (c) 2022 Proton AG
 *
 * This file is part of Proton Mail.
 *
 * Proton Mail is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Mail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Mail. If not, see https://www.gnu.org/licenses/.
 */
package ch.protonmail.android.activities.messageDetails

import android.os.AsyncTask
import ch.protonmail.android.core.ProtonMailApplication
import ch.protonmail.android.data.local.MessageDatabase
import ch.protonmail.android.data.local.model.Message
import ch.protonmail.android.jobs.FetchMessageDetailJob
import ch.protonmail.android.labels.domain.LabelRepository

internal class RegisterReloadTask(
    private val message: Message,
    private val labelRepository: LabelRepository
) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg voids: Void): Void? {
        val app = ProtonMailApplication.getApplication()
        val messagesDatabase = MessageDatabase.getInstance(app, app.userManager.requireCurrentUserId()).getDao()
        val jobManager = app.jobManager
        if (message.checkIfAttHeadersArePresent(messagesDatabase)) {
            jobManager.addJobInBackground(FetchMessageDetailJob(message.messageId, labelRepository))
        }
        return null
    }
}
