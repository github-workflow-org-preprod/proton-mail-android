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

package ch.protonmail.android.settings.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import ch.protonmail.android.domain.entity.Id
import ch.protonmail.android.settings.domain.HandleChangesToViewMode
import ch.protonmail.android.usecase.delete.ClearUserMessagesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.mailsettings.domain.entity.SwipeAction
import me.proton.core.mailsettings.domain.entity.ViewMode
import javax.inject.Inject

@HiltViewModel
class AccountSettingsActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountManager: AccountManager,
    private var clearUserMessagesData: ClearUserMessagesData,
    private var handleChangesToViewMode: HandleChangesToViewMode
) : ViewModel() {

    var currentAction: SwipeAction =
        savedStateHandle.get<SwipeAction>(EXTRA_CURRENT_ACTION) ?: SwipeAction.Trash
    val swipeId: SwipeType = savedStateHandle.get<SwipeType>(EXTRA_SWIPE_ID) ?: SwipeType.RIGHT

    fun changeViewMode(viewMode: ViewMode) {
        viewModelScope.launch {
            accountManager.getPrimaryUserId().first()?.let { userId ->
                    clearUserMessagesData.invoke(Id(userId.id))
                    handleChangesToViewMode.invoke(
                        userId, viewMode
                    )
            }
        }
    }
}
