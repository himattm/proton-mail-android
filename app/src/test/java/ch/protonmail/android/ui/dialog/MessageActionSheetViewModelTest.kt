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

package ch.protonmail.android.ui.dialog

import androidx.lifecycle.SavedStateHandle
import ch.protonmail.android.core.Constants
import ch.protonmail.android.data.local.model.Message
import ch.protonmail.android.labels.domain.usecase.MoveMessagesToFolder
import ch.protonmail.android.labels.presentation.ui.LabelsActionSheet
import ch.protonmail.android.mailbox.domain.ChangeConversationsReadStatus
import ch.protonmail.android.mailbox.domain.ChangeConversationsStarredStatus
import ch.protonmail.android.mailbox.domain.DeleteConversations
import ch.protonmail.android.mailbox.domain.MoveConversationsToFolder
import ch.protonmail.android.mailbox.domain.model.ConversationsActionResult
import ch.protonmail.android.mailbox.presentation.ConversationModeEnabled
import ch.protonmail.android.repository.MessageRepository
import ch.protonmail.android.ui.actionsheet.MessageActionSheetAction
import ch.protonmail.android.ui.actionsheet.MessageActionSheetState
import ch.protonmail.android.ui.actionsheet.MessageActionSheetViewModel
import ch.protonmail.android.ui.actionsheet.model.ActionSheetTarget
import ch.protonmail.android.usecase.delete.DeleteMessage
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.domain.entity.UserId
import me.proton.core.test.android.ArchTest
import me.proton.core.test.kotlin.CoroutinesTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageActionSheetViewModelTest : ArchTest, CoroutinesTest {

    @MockK
    private lateinit var deleteMessage: DeleteMessage

    @MockK
    private lateinit var moveMessagesToFolder: MoveMessagesToFolder

    @MockK
    private lateinit var messageRepository: MessageRepository

    @MockK
    private lateinit var changeConversationsReadStatus: ChangeConversationsReadStatus

    @MockK
    private lateinit var changeConversationsStarredStatus: ChangeConversationsStarredStatus

    @MockK
    private lateinit var conversationModeEnabled: ConversationModeEnabled

    @MockK
    private lateinit var moveConversationsToFolder: MoveConversationsToFolder

    @MockK
    private lateinit var deleteConversations: DeleteConversations

    @MockK
    private lateinit var accountManager: AccountManager

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: MessageActionSheetViewModel

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = MessageActionSheetViewModel(
            savedStateHandle,
            deleteMessage,
            deleteConversations,
            moveMessagesToFolder,
            moveConversationsToFolder,
            messageRepository,
            changeConversationsReadStatus,
            changeConversationsStarredStatus,
            conversationModeEnabled,
            accountManager
        )
    }

    @Test
    fun verifyShowLabelsManagerActionIsExecutedForLabels() = runBlockingTest {
        // given
        val messageId1 = "messageId1"
        val labelId1 = "labelId1"
        val messageId2 = "messageId2"
        val labelId2 = "labelId2"
        val messageIds = listOf(messageId1, messageId2)
        val currentLocation = Constants.MessageLocationType.INBOX
        val labelsSheetType = LabelsActionSheet.Type.LABEL
        val expected = MessageActionSheetAction.ShowLabelsManager(
            messageIds,
            currentLocation.messageLocationTypeValue,
            labelsSheetType,
            ActionSheetTarget.MAILBOX_ITEM_IN_DETAIL_SCREEN
        )
        val message1 = mockk<Message> {
            every { messageId } returns messageId1
            every { labelIDsNotIncludingLocations } returns listOf(labelId1)
        }
        val message2 = mockk<Message> {
            every { messageId } returns messageId2
            every { labelIDsNotIncludingLocations } returns listOf(labelId2)
        }
        coEvery { messageRepository.findMessageById(messageId1) } returns message1
        coEvery { messageRepository.findMessageById(messageId2) } returns message2
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MAILBOX_ITEM_IN_DETAIL_SCREEN

        // when
        viewModel.showLabelsManager(messageIds, currentLocation)

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
    }

    @Test
    fun verifyShowLabelsManagerActionIsExecutedForFolders() = runBlockingTest {
        // given
        val messageId1 = "messageId1"
        val labelId1 = "labelId1"
        val messageId2 = "messageId2"
        val labelId2 = "labelId2"
        val messageIds = listOf(messageId1, messageId2)
        val currentLocation = Constants.MessageLocationType.INBOX
        val labelsSheetType = LabelsActionSheet.Type.FOLDER
        val expected = MessageActionSheetAction.ShowLabelsManager(
            messageIds,
            currentLocation.messageLocationTypeValue,
            labelsSheetType,
            ActionSheetTarget.MAILBOX_ITEMS_IN_MAILBOX_SCREEN
        )
        val message1 = mockk<Message> {
            every { messageId } returns messageId1
            every { labelIDsNotIncludingLocations } returns listOf(labelId1)
        }
        val message2 = mockk<Message> {
            every { messageId } returns messageId2
            every { labelIDsNotIncludingLocations } returns listOf(labelId2)
        }
        coEvery { messageRepository.findMessageById(messageId1) } returns message1
        coEvery { messageRepository.findMessageById(messageId2) } returns message2
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MAILBOX_ITEMS_IN_MAILBOX_SCREEN

        // when
        viewModel.showLabelsManager(messageIds, currentLocation, LabelsActionSheet.Type.FOLDER)

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
    }

    @Test
    fun verifyShowMessageHeadersActionProcessing() {

        // given
        val messageId1 = "messageId1"
        val messageHeader = "testMessageHeader"
        val message1 = mockk<Message> {
            every { messageId } returns messageId1
            every { header } returns messageHeader
        }
        coEvery { messageRepository.findMessageById(messageId1) } returns message1
        val expected = MessageActionSheetAction.ShowMessageHeaders(messageHeader)

        // when
        viewModel.showMessageHeaders(messageId1)

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
    }

    @Test
    fun verifyMoveToInboxEmitsShouldDismissActionThatDoesNotDismissBackingActivityWhenBottomActionSheetTargetIsConversationDetails() {
        // given
        val messageId = "messageId1"
        val expected = MessageActionSheetAction.DismissActionSheet(false)
        every { conversationModeEnabled(any()) } returns true
        every { moveMessagesToFolder.invoke(any(), any(), any()) } just Runs
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        // when
        viewModel.moveToInbox(
            listOf(messageId),
            Constants.MessageLocationType.ARCHIVE
        )

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
        verify { moveMessagesToFolder.invoke(listOf(messageId), "0", "6") }
    }

    @Test
    fun verifyMoveToInboxEmitsShouldDismissActionThatDismissesBackingActivityWhenBottomActionSheetTargetIsMessageDetails() {
        // given
        val messageId = "messageId2"
        val expected = MessageActionSheetAction.DismissActionSheet(true)
        every { conversationModeEnabled(any()) } returns false
        every { moveMessagesToFolder.invoke(any(), any(), any()) } just Runs
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MAILBOX_ITEM_IN_DETAIL_SCREEN

        // when
        viewModel.moveToInbox(
            listOf(messageId),
            Constants.MessageLocationType.SPAM
        )

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
        verify { moveMessagesToFolder.invoke(listOf(messageId), "0", "4") }
    }

    @Test
    fun verifyUnStarMessageCallsChangeConversationStarredStatusWhenConversationModeIsEnabledAndAConversationIsBeingUnStarred() {
        // given
        val conversationId = "conversationId01"
        val expected = MessageActionSheetAction.ChangeStarredStatus(starredStatus = false, isSuccessful = true)
        val userId = UserId("userId")
        val unstarAction = ChangeConversationsStarredStatus.Action.ACTION_UNSTAR
        every { conversationModeEnabled(any()) } returns true
        every { accountManager.getPrimaryUserId() } returns flowOf(userId)
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MAILBOX_ITEM_IN_DETAIL_SCREEN
        coEvery { changeConversationsStarredStatus.invoke(listOf(conversationId), userId, unstarAction) } returns ConversationsActionResult.Success

        // when
        viewModel.unStarMessage(
            listOf(conversationId),
            Constants.MessageLocationType.LABEL_FOLDER
        )

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
        coVerify { changeConversationsStarredStatus.invoke(listOf(conversationId), userId, unstarAction) }
        verify { messageRepository wasNot Called }
    }

    @Test
    fun verifyMarkReadCallsChangeConversationReadStatusWhenConversationModeIsEnabledAndAConversationIsBeingMarkedAsRead() {
        // given
        val conversationId = "conversationId02"
        val expected = MessageActionSheetAction.DismissActionSheet(true)
        val userId = UserId("userId02")
        val markReadAction = ChangeConversationsReadStatus.Action.ACTION_MARK_READ
        val location = Constants.MessageLocationType.ALL_MAIL
        every { conversationModeEnabled(any()) } returns true
        every { accountManager.getPrimaryUserId() } returns flowOf(userId)
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MAILBOX_ITEM_IN_DETAIL_SCREEN
        coEvery {
            changeConversationsReadStatus.invoke(
                listOf(conversationId),
                markReadAction,
                userId,
                location,
                location.messageLocationTypeValue.toString()
            )
        } returns ConversationsActionResult.Success

        // when
        viewModel.markRead(
            listOf(conversationId),
            location,
            location.messageLocationTypeValue.toString()
        )

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
        coVerify { changeConversationsReadStatus.invoke(listOf(conversationId), markReadAction, userId, location, location.messageLocationTypeValue.toString()) }
        verify { messageRepository wasNot Called }
    }

    @Test
    fun verifyStarMessageCallsMessageRepositoryToStarMessageWhenConversationIsEnabledAndActionIsBeingAppliedToASpecificMessageInAConversation() {
        // given
        val messageId = "messageId3"
        val expected = MessageActionSheetAction.ChangeStarredStatus(true, isSuccessful = true)
        every { conversationModeEnabled(any()) } returns true
        every { messageRepository.starMessages(listOf(messageId)) } just Runs
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN
        val userId = UserId("userId")
        every { accountManager.getPrimaryUserId() } returns flowOf(userId)
        coEvery { changeConversationsStarredStatus(any(), any(), any()) } returns ConversationsActionResult.Success

        // when
        viewModel.starMessage(
            listOf(messageId),
            Constants.MessageLocationType.INBOX
        )

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
        verify { messageRepository.starMessages(listOf(messageId)) }
        verify { accountManager wasNot Called }
    }

    @Test
    fun verifyMarkUnreadCallsMessageRepositoryToMarkUnreadWhenConversationIsEnabledAndActionIsBeingAppliedToASpecificMessageInAConversation() {
        // given
        val messageId = "messageId4"
        val expected = MessageActionSheetAction.DismissActionSheet(false)
        every { conversationModeEnabled(any()) } returns true
        every { messageRepository.markUnRead(listOf(messageId)) } just Runs
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        // when
        viewModel.markUnread(
            listOf(messageId),
            Constants.MessageLocationType.INBOX,
            Constants.MessageLocationType.INBOX.messageLocationTypeValue.toString()
        )

        // then
        assertEquals(expected, viewModel.actionsFlow.value)
        verify { messageRepository.markUnRead(listOf(messageId)) }
        verify { accountManager wasNot Called }
    }

    @Test
    fun verifyDeleteCallsDeleteMessageWhenCalledForAMessageInsideAConversation() {
        val messageIds = listOf("messageId5")
        val currentFolder = Constants.MessageLocationType.TRASH
        every { conversationModeEnabled(any()) } returns true
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.delete(
            messageIds,
            currentFolder,
            true
        )

        coVerify { deleteMessage(messageIds, currentFolder.messageLocationTypeValue.toString()) }
    }

    @Test
    fun verifyDeleteCallsDeleteConversationWhenCalledForAConversation() {
        val messageIds = listOf("messageId6")
        val currentFolder = Constants.MessageLocationType.SPAM
        val userId = UserId("userId6")
        every { accountManager.getPrimaryUserId() } returns flowOf(userId)
        every { conversationModeEnabled(any()) } returns true
        every {
            savedStateHandle.get<ActionSheetTarget>("extra_arg_action_sheet_actions_target")
        } returns ActionSheetTarget.CONVERSATION_ITEM_IN_DETAIL_SCREEN

        viewModel.delete(
            messageIds,
            currentFolder,
            true
        )

        coVerify {
            deleteConversations(
                messageIds,
                userId,
                currentFolder.messageLocationTypeValue.toString()
            )
        }
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToInboxActionVisibleWhenActionTargetIsMessageAndMessageLocationIsTrash() {
        val messageIds = listOf("messageId7")
        val currentFolder = Constants.MessageLocationType.TRASH
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = true,
            isMoveToTrashVisible = false,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = false,
            isDeleteActionVisible = true
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToInboxActionNotVisibleWhenActionTargetIsMessageAndMessageLocationIsNotArchiveSpamOrTrash() {
        val messageIds = listOf("messageId7")
        val currentFolder = Constants.MessageLocationType.SENT
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = false,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = false,
            isDeleteActionVisible = true
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToInboxActionVisibleWhenActionTargetIsConversation() {
        val messageIds = listOf("messageId8")
        val currentFolder = Constants.MessageLocationType.INBOX
        val actionsTarget = ActionSheetTarget.CONVERSATION_ITEM_IN_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = true,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = true,
            isDeleteActionVisible = false
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToTrashActionVisibleWhenMessageLocationIsNotTrash() {
        val messageIds = listOf("messageId8")
        val currentFolder = Constants.MessageLocationType.ARCHIVE
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = true,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = false,
            isMoveToSpamVisible = true,
            isDeleteActionVisible = false
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToTrashActionNotVisibleWhenMessageLocationIsTrash() {
        val messageIds = listOf("messageId10")
        val currentFolder = Constants.MessageLocationType.TRASH
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = true,
            isMoveToTrashVisible = false,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = false,
            isDeleteActionVisible = true
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToArchiveActionVisibleWhenActionTargetIsMessageAndMessageLocationIsNotArchiveOrSpam() {
        val messageIds = listOf("messageId11")
        val currentFolder = Constants.MessageLocationType.LABEL
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = false,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = true,
            isDeleteActionVisible = false
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToArchiveActionNotVisibleWhenActionTargetIsMessageAndMessageLocationIsSpam() {
        val messageIds = listOf("messageId12")
        val currentFolder = Constants.MessageLocationType.SPAM
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = true,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = false,
            isMoveToSpamVisible = false,
            isDeleteActionVisible = true
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToSpamActionVisibleWhenActionTargetIsMessageAndMessageLocationIsNotSpamDraftSentOrTrash() {
        val messageIds = listOf("messageId13")
        val currentFolder = Constants.MessageLocationType.INBOX
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = false,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = true,
            isDeleteActionVisible = false
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithMoveToSpamActionNotVisibleWhenActionTargetIsMessageAndMessageLocationIsDraft() {
        val messageIds = listOf("messageId14")
        val currentFolder = Constants.MessageLocationType.DRAFT
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = false,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = false,
            isDeleteActionVisible = true
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithDeleteActionVisibleWhenMessageLocationIsEitherOfSpamDraftSentOrTrash() {
        val messageIds = listOf("messageId15")
        val currentFolder = Constants.MessageLocationType.DRAFT
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = false,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = true,
            isMoveToSpamVisible = false,
            isDeleteActionVisible = true
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

    @Test
    fun verifySetupViewStateReturnsMoveSectionStateWithDeleteActionNotVisibleWhenMessageLocationIsArchive() {
        val messageIds = listOf("messageId16")
        val currentFolder = Constants.MessageLocationType.ARCHIVE
        val actionsTarget = ActionSheetTarget.MESSAGE_ITEM_WITHIN_CONVERSATION_DETAIL_SCREEN

        viewModel.setupViewState(messageIds, currentFolder, actionsTarget)

        val expected = MessageActionSheetState.MoveSectionState(
            messageIds,
            currentFolder,
            actionsTarget = actionsTarget,
            isMoveToInboxVisible = true,
            isMoveToTrashVisible = true,
            isMoveToArchiveVisible = false,
            isMoveToSpamVisible = true,
            isDeleteActionVisible = false
        )
        assertEquals(expected, viewModel.stateFlow.value)
    }

}
