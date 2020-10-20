/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.viewmodel.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.UserRepository

class UserViewModel @ViewModelInject constructor(
    private val accountRepository: AccountRepository,
    private val factory: UserRepository.AssistedFactory,
) : ViewModel() {

    private val repository =
        accountRepository.getCurrentAccount().let { accountDetails ->
            accountDetails.service.let {
                factory.create(it as LookupService, it as RelationshipService)
            }
        }

    val loaded = MutableLiveData(false)
    val refreshing = MutableLiveData(false)
    val user = MutableLiveData<UiUser>()
    val relationship = MutableLiveData<IRelationship>()
    val isMe = MutableLiveData(false)

    suspend fun init(data: UiUser) {
        if (loaded.value == true) {
            return
        }
        refresh(data)
        loaded.postValue(true)
    }

    suspend fun refresh(data: UiUser) {
        refreshing.postValue(true)
        user.postValue(data)
        val name = data.screenName
        val key = UserKey(name, "twitter.com")
        val isme = accountRepository.getCurrentAccount().key == key
        isMe.postValue(isme)
        val dbUser = repository.lookupUser(data.id)
        dbUser?.toUi()?.let {
            user.postValue(it)
        }
        if (isme) {
            accountRepository.findByAccountKey(key)?.let {
                accountRepository.getAccountDetails(it)
            }?.let { details ->
                dbUser?.let {
                    details.user = it
                    accountRepository.updateAccount(details)
                }
            }
        }
        if (!isme) {
            repository.showRelationship(data.id).let {
                relationship.postValue(it)
            }
        }
        refreshing.postValue(false)
    }
}
