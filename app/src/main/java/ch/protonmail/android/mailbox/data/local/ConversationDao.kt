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

package ch.protonmail.android.mailbox.data.local

import androidx.room.Dao
import androidx.room.Query
import ch.protonmail.android.mailbox.data.local.model.ConversationEntity
import ch.protonmail.android.mailbox.data.local.model.ConversationEntity.Companion.COLUMN_ORDER
import ch.protonmail.android.mailbox.data.local.model.ConversationEntity.Companion.COLUMN_USER_ID
import ch.protonmail.android.mailbox.data.local.model.ConversationEntity.Companion.TABLE_CONVERSATIONS
import kotlinx.coroutines.flow.Flow
import me.proton.core.data.db.BaseDao

@Dao
abstract class ConversationDao : BaseDao<ConversationEntity>() {

    @Query(
        """
        SELECT * FROM $TABLE_CONVERSATIONS 
        WHERE $COLUMN_USER_ID = :userId
        ORDER BY `$COLUMN_ORDER` DESC
        """
    )
    abstract fun getConversations(userId: String): Flow<List<ConversationEntity>>

    @Query("DELETE FROM $TABLE_CONVERSATIONS")
    abstract fun clear()
}

