/*
 *  Twitter
 *
 *  Copyright (C) 2021-2022 Living Solutions <living.solutions.vn@gmail.com>
 * 
 *  This file is part of Twitter.
 * 
 *  Twitter is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twitter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twitter. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.twitter.model

import com.twidere.services.microblog.model.ISearchResponse
import com.twidere.services.microblog.model.IStatus
import kotlinx.serialization.Serializable

@Serializable
data class TwitterSearchResponseV2(
    val meta: Meta? = null,
    val data: List<StatusV2>? = null,
    val errors: List<TwitterErrorV2>? = null,
    val includes: IncludesV2? = null
) : ISearchResponse {
    override val nextPage: String?
        get() = meta?.nextToken
    override val status: List<IStatus>
        get() = data ?: emptyList()
}
