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
package ch.protonmail.android.mailbox.data.remote

import ch.protonmail.android.api.interceptors.UserIdTag
import ch.protonmail.android.api.segments.BaseApi
import ch.protonmail.android.mailbox.domain.model.GetConversationsParameters

class ConversationApi(private val service: ConversationService) : BaseApi(), ConversationApiSpec {

    override suspend fun fetchConversations(params: GetConversationsParameters) =
        service.fetchConversations(
            params.oldestConversationTimestamp,
            params.pageSize,
            params.labelId,
            userIdTag = UserIdTag(params.userId)
        )
}
