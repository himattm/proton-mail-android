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
package ch.protonmail.android.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.protonmail.android.data.local.model.COLUMN_PENDING_DRAFT_MESSAGE_ID
import ch.protonmail.android.data.local.model.COLUMN_PENDING_SEND_LOCAL_DB_ID
import ch.protonmail.android.data.local.model.COLUMN_PENDING_SEND_MESSAGE_ID
import ch.protonmail.android.data.local.model.COLUMN_PENDING_SEND_OFFLINE_MESSAGE_ID
import ch.protonmail.android.data.local.model.COLUMN_PENDING_UPLOAD_MESSAGE_ID
import ch.protonmail.android.data.local.model.PendingDraft
import ch.protonmail.android.data.local.model.PendingSend
import ch.protonmail.android.data.local.model.PendingUpload
import ch.protonmail.android.data.local.model.TABLE_PENDING_DRAFT
import ch.protonmail.android.data.local.model.TABLE_PENDING_SEND
import ch.protonmail.android.data.local.model.TABLE_PENDING_UPLOADS

@Dao
interface PendingActionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPendingForSend(pendingSend: PendingSend)

    @Query("SELECT * FROM $TABLE_PENDING_SEND")
    fun findAllPendingSendsAsync(): LiveData<List<PendingSend>>

    @Query("SELECT * FROM $TABLE_PENDING_SEND WHERE $COLUMN_PENDING_SEND_MESSAGE_ID = :messageId")
    fun findPendingSendByMessageId(messageId: String): PendingSend?

    @Query("DELETE FROM $TABLE_PENDING_SEND WHERE $COLUMN_PENDING_SEND_MESSAGE_ID = :messageId")
    fun deletePendingSendByMessageId(messageId: String)

    @Query("DELETE FROM $TABLE_PENDING_SEND WHERE $COLUMN_PENDING_SEND_LOCAL_DB_ID = :messageDbId")
    fun deletePendingSendByDbId(messageDbId: Long)

    @Query("SELECT * FROM $TABLE_PENDING_SEND WHERE $COLUMN_PENDING_SEND_OFFLINE_MESSAGE_ID = :offlineMessageId")
    fun findPendingSendByOfflineMessageId(offlineMessageId: String): PendingSend?

    @Query("SELECT * FROM $TABLE_PENDING_SEND WHERE $COLUMN_PENDING_SEND_OFFLINE_MESSAGE_ID = :offlineMessageId")
    fun findPendingSendByOfflineMessageIdAsync(offlineMessageId: String): LiveData<PendingSend?>

    @Query("SELECT * FROM $TABLE_PENDING_SEND WHERE $COLUMN_PENDING_SEND_LOCAL_DB_ID = :dbId")
    fun findPendingSendByDbId(dbId: Long): PendingSend?

    @Query("DELETE FROM $TABLE_PENDING_SEND")
    fun clearPendingSendCache()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPendingForUpload(pendingUpload: PendingUpload)

    @Query("SELECT * FROM $TABLE_PENDING_UPLOADS")
    fun findAllPendingUploadsAsync(): LiveData<List<PendingUpload>>

    @Query("SELECT * FROM $TABLE_PENDING_UPLOADS WHERE $COLUMN_PENDING_UPLOAD_MESSAGE_ID = :messageId")
    fun findPendingUploadByMessageId(messageId: String): PendingUpload?

    @Query("DELETE FROM $TABLE_PENDING_UPLOADS WHERE $COLUMN_PENDING_UPLOAD_MESSAGE_ID IN (:messageId)")
    fun deletePendingUploadByMessageId(vararg messageId: String)

    @Query("DELETE FROM $TABLE_PENDING_UPLOADS")
    fun clearPendingUploadCache()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPendingDraft(pendingDraft: PendingDraft)

    @Query("DELETE FROM $TABLE_PENDING_DRAFT WHERE $COLUMN_PENDING_DRAFT_MESSAGE_ID = :messageDbId")
    fun deletePendingDraftById(messageDbId: Long)

    @Query("SELECT * FROM $TABLE_PENDING_DRAFT WHERE $COLUMN_PENDING_DRAFT_MESSAGE_ID = :messageDbId")
    fun findPendingDraftByDbId(messageDbId: Long): PendingDraft?
}
