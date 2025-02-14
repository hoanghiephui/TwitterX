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

import com.twidere.services.microblog.model.IUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserV2(
    val id: String? = null,
    val protected: Boolean? = null,

    @SerialName("profile_image_url")
    val profileImageURL: String? = null,

    val verified: Boolean? = null,
    val name: String? = null,
    val entities: EntitiesV2? = null,

    @SerialName("public_metrics")
    val publicMetrics: PublicMetricsV2? = null,

    val description: String? = null,
    val location: String? = null,
    val url: String? = null,

    @SerialName("pinned_tweet_id")
    val pinnedTweetID: String? = null,

    val username: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    var profileBanner: ProfileBanner? = null,
) : IUser
