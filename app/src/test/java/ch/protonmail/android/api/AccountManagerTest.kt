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

package ch.protonmail.android.api

import androidx.core.content.edit
import assert4k.Null
import assert4k.`is`
import assert4k.assert
import assert4k.contains
import assert4k.equals
import assert4k.invoke
import assert4k.that
import assert4k.times
import assert4k.unaryPlus
import ch.protonmail.android.domain.entity.Id
import ch.protonmail.android.prefs.SecureSharedPreferences
import ch.protonmail.android.utils.getStringList
import ch.protonmail.android.utils.putStringList
import io.mockk.coEvery
import io.mockk.mockk
import me.proton.core.test.android.mocks.newMockSharedPreferences
import me.proton.core.test.kotlin.CoroutinesTest
import kotlin.test.Test

class AccountManagerTest {

    class UsernameToIdMigrationTest : CoroutinesTest {

        private val user1 = "username1" to Id("id1")
        private val user2 = "username2" to Id("id2")
        private val user3 = "username3" to Id("id3")
        private val user4 = "username4" to Id("id4")

        private val secureSharedPreferencesMigration: SecureSharedPreferences.UsernameToIdMigration = mockk {
            coEvery { this@mockk(any()) } returns mapOf(user1, user2, user3, user4)
        }
        private val defaultPreferences = newMockSharedPreferences
        private val migration = AccountManager.UsernameToIdMigration(
            dispatchers,
            mockk(),
            secureSharedPreferencesMigration,
            defaultPreferences.apply {
                edit {
                    putStringList(PREF_USERNAMES_LOGGED_IN, listOf(user1, user2).map { it.first })
                    putStringList(PREF_USERNAMES_LOGGED_OUT, listOf(user3, user4).map { it.first })
                }
            }
        )

        @Test
        fun `does remove old preferences`() = coroutinesTest {

            migration()

            assert that defaultPreferences.getStringList(PREF_USERNAMES_LOGGED_IN, null) `is`  Null
            assert that defaultPreferences.getStringList(PREF_USERNAMES_LOGGED_OUT, null) `is`  Null
        }

        @Test
        fun `new preferences have all the values`() = coroutinesTest {

            migration()

            assert that defaultPreferences.getStringSet(PREF_ALL_LOGGED_IN, emptySet())!! * {
                +size() equals 2
                it contains user1.second.s
                it contains user2.second.s
            }
            assert that defaultPreferences.getStringSet(PREF_ALL_SAVED, emptySet())!! * {
                +size() equals 2
                it contains user3.second.s
                it contains user4.second.s
            }
        }

        @Test
        fun `correctly skips preferences without an user id`() = coroutinesTest {
            coEvery { secureSharedPreferencesMigration(any()) } returns mapOf(user1, user3)

            migration()

            assert that defaultPreferences.getStringSet(PREF_ALL_LOGGED_IN, emptySet()) equals
                setOf(user1.second.s)

            assert that defaultPreferences.getStringSet(PREF_ALL_SAVED, emptySet()) equals
                setOf(user3.second.s)
        }
    }
}
