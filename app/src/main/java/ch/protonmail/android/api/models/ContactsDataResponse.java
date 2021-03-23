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
package ch.protonmail.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ch.protonmail.android.api.utils.Fields;
import ch.protonmail.android.data.local.model.ContactData;

/**
 * Created by dkadrikj on 8/22/16.
 */

public class ContactsDataResponse extends ResponseBody {

    private int Total;

    @SerializedName(Fields.Contact.CONTACTS)
    private List<ContactData> contacts;

    public List<ContactData> getContacts() {
        return contacts;
    }

    public int getTotal() {
        return Total;
    }
}
