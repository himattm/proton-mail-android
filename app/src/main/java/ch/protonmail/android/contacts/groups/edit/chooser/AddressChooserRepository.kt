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
package ch.protonmail.android.contacts.groups.edit.chooser

import ch.protonmail.android.api.models.DatabaseProvider
import ch.protonmail.android.core.UserManager
import ch.protonmail.android.data.local.model.ContactEmail
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressChooserRepository @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val userManager: UserManager
) {

    private val contactDao by lazy {
        Timber.v("Instantiating contactDao in AddressChooserRepository")
        databaseProvider.provideContactDao(userManager.requireCurrentUserId())
    }

    fun getContactGroupEmails(): Observable<List<ContactEmail>> {
        return contactDao.findAllContactsEmailsAsyncObservable()
                .toObservable()
    }

    fun filterContactGroupEmails(filter: String): Observable<List<ContactEmail>> {
        val filterString = "%$filter%"
        return contactDao.findAllContactsEmailsAsyncObservable(filterString)
                .toObservable()
    }
}
