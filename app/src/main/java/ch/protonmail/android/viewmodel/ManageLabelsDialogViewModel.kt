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

package ch.protonmail.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.protonmail.android.adapters.LabelsAdapter
import ch.protonmail.android.api.models.room.messages.MessagesDao
import ch.protonmail.android.core.UserManager
import ch.protonmail.android.viewmodel.ManageLabelsDialogViewModel.ViewState.ShowMissingColorError
import ch.protonmail.android.views.ThreeStateButton
import javax.inject.Inject
import javax.inject.Named

class ManageLabelsDialogViewModel @Inject constructor(
    @Named("messages") private val messagesDao: MessagesDao,
    private val userManager: UserManager
) : ViewModel() {

    sealed class ViewState {
        object ShowMissingColorError : ViewState()
        object ShowMissingNameError : ViewState()
        object ShowLabelNameDuplicatedError : ViewState()
        class ShowLabelCreatedEvent(val labelName: String) : ViewState()
    }

    val viewState: MutableLiveData<ViewState> = MutableLiveData()

    private var creationMode: Boolean = false

    fun onDoneClicked(
        isCreationMode: Boolean,
        labelColor: String?,
        checkedLabelIds: List<String>,
        isShowCheckboxes: Boolean,
        mArchiveCheckboxState: Int,
        labelName: String,
        labelItemsList: List<LabelsAdapter.LabelItem>
    ) {

        if (isCreationMode) {
            creationMode = false
            createLabel(labelName, labelColor, labelItemsList)
        } else {
//            val maxLabelsAllowed = UserUtils.getMaxAllowedLabels(userManager)
//            if (checkedLabelIds.size > maxLabelsAllowed) {

//                if (isAdded()) {
//                    getActivity().showToast(String.format(getString(R.string.max_labels_selected), maxLabelsAllowed), Toast.LENGTH_SHORT)
//                }
//                return
//            }
            if (isShowCheckboxes) {
                if (mArchiveCheckboxState == ThreeStateButton.STATE_CHECKED ||
                    mArchiveCheckboxState == ThreeStateButton.STATE_PRESSED) {
                    // also archive
//                    mLabelStateChangeListener.onLabelsChecked(getCheckedLabels(), if (mMessageIds == null) null else getUnchangedLabels(), mMessageIds, mMessageIds)
                } else {
//                    mLabelStateChangeListener.onLabelsChecked(getCheckedLabels(), if (mMessageIds == null) null else getUnchangedLabels(), mMessageIds)
                }
            } else if (!isShowCheckboxes) {
//                mLabelCreationListener.onLabelsDeleted(getCheckedLabels())
            }
//            dismissAllowingStateLoss()
        }
    }

    private fun createLabel(
        labelName: String,
        labelColor: String?,
        labelItemsList: List<LabelsAdapter.LabelItem>
    ) {
        if (labelColor.isNullOrEmpty()) {
            viewState.value = ShowMissingColorError
            return
        }

        if (labelName.isEmpty()) {
            viewState.value = ViewState.ShowMissingNameError
            return
        }

        labelItemsList
            .find { it.name == labelName }
            ?.let {
                viewState.value = ViewState.ShowLabelNameDuplicatedError
                return
            }

        viewState.value = ViewState.ShowLabelCreatedEvent(labelName)
    }


}
