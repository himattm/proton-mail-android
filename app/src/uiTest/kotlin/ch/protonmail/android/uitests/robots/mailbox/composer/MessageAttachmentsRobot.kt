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
package ch.protonmail.android.uitests.robots.mailbox.composer

import androidx.annotation.IdRes
import ch.protonmail.android.R
import ch.protonmail.android.uitests.testsHelper.MockAddAttachmentIntent
import me.proton.fusion.Fusion

/**
 * Class represents Message Attachments.
 */
open class MessageAttachmentsRobot : Fusion {

    fun addImageCaptureAttachment(@IdRes drawable: Int): ComposerRobot =
        mockCameraImageCapture(drawable)

    fun addTwoImageCaptureAttachments(
        @IdRes firstDrawable: Int,
        @IdRes secondDrawable: Int
    ): ComposerRobot =
        mockCameraImageCapture(firstDrawable)
            .attachments()
            .mockCameraImageCapture(secondDrawable)

    fun addFileAttachment(@IdRes drawable: Int): MessageAttachmentsRobot {
        mockFileAttachment(drawable)
        return this
    }

    fun removeLastAttachment(): MessageAttachmentsRobot {
        return this
    }

    fun goBackToComposer(): ComposerRobot {
        return ComposerRobot()
    }

    private fun mockCameraImageCapture(@IdRes drawableId: Int): ComposerRobot {
        view.withId(takePhotoIconId).checkIsDisplayed()
        MockAddAttachmentIntent.mockCameraImageCapture(takePhotoIconId, drawableId)
        return ComposerRobot()
    }

    private fun mockFileAttachment(@IdRes drawable: Int): ComposerRobot {
        view.withId(addAttachmentIconId).checkIsDisplayed()
        MockAddAttachmentIntent.mockChooseAttachment(addAttachmentIconId, drawable)
        return ComposerRobot()
    }

    companion object {
        private const val takePhotoIconId = R.id.take_photo
        private const val addAttachmentIconId = R.id.attach_file
    }
}
