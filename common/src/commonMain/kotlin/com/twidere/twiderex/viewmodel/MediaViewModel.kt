/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.viewmodel

import com.twidere.twiderex.action.MediaAction
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MediaViewModel(
    private val repository: StatusRepository,
    private val accountRepository: AccountRepository,
    private val mediaAction: MediaAction,
    private val statusKey: MicroBlogKey,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null).mapNotNull { it }
    }

    suspend fun saveFile(currentMedia: UiMedia, target: String) {
        val account = account.firstOrNull() ?: return
        currentMedia.mediaUrl?.let {
            mediaAction.download(
                accountKey = account.accountKey,
                source = it,
                target = target
            )
        }
    }

    suspend fun shareMedia(currentMedia: UiMedia) {
        val account = account.firstOrNull() ?: return
        currentMedia.mediaUrl?.let {
            mediaAction.share(
                source = it,
                accountKey = account.accountKey
            )
        }
    }

    val loading = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val status by lazy {
        account.flatMapLatest {
            repository.loadStatus(
                statusKey = statusKey,
                accountKey = it.accountKey,
            )
        }
    }

    // init {
    //     viewModelScope.launch {
    //         try {
    //             repository.loadTweetFromNetwork(
    //                 statusKey.id,
    //                 account.accountKey,
    //                 account.service as LookupService
    //             )
    //         } catch (e: Throwable) {
    //             e.notify(inAppNotification)
    //         }
    //     }
    // }
}
