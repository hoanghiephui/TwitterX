package com.twidere.twiderex.fragment

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.TabConstants.defaultTabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.compose.ui.zIndex
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.*
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : JetFragment() {
    private val args by navArgs<UserFragmentArgs>()

    @OptIn(ExperimentalLazyDsl::class, IncomingComposeUpdate::class)
    @Composable
    @IncomingComposeUpdate
    override fun onCompose() {
        val viewModel = viewModel<UserViewModel>()
        val user by viewModel.user.observeAsState(initial = args.user)
        val relationship by viewModel.relationship.observeAsState()
        val loaded by viewModel.loaded.observeAsState(initial = false)
        val tabs = listOf(
            Icons.Default.List,
            Icons.Default.Image,
            Icons.Default.Favorite,
        )
        val (selectedItem, setSelectedItem) = savedInstanceState { 0 }


        val timelineViewModel = viewModel<UserTimelineViewModel>()
        val timeline by timelineViewModel.timeline.observeAsState(initial = emptyList())
        val timelineLoadingMore by timelineViewModel.loadingMore.observeAsState(initial = false)

        val coroutineScope = rememberCoroutineScope()

        launchInComposition {
            viewModel.init(args.user)
            timelineViewModel.loadTimeline(args.user)
        }
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(asset = Icons.Default.Reply)
                }
            }
        ) {
            Box {
                AppBar(
                    modifier = Modifier.zIndex(1f),
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Mail)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.MoreVert)
                        }
                    }
                )

                val listState = rememberLazyListState()
                //TODO: background color
                //TODO: header paddings
                LazyColumn(
                    state = listState
                ) {
                    item {
                        Box {
                            //TODO: parallax effect
                            user.profileBackgroundImage?.let {
                                NetworkImage(
                                    url = it,
                                    modifier = Modifier
                                        .aspectRatio(320f / 160f)
                                )
                            }
                            Column {
                                WithConstraints {
                                    Spacer(modifier = Modifier.height(maxWidth * 160f / 320f - 72.dp / 2))
                                }

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    alignment = Alignment.Center
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .clipToBounds()
                                            .background(Color.White)
                                    )
                                    UserAvatar(
                                        user = user,
                                        size = 72.dp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                                Row(
                                    modifier = Modifier.padding(horizontal = standardPadding * 2)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            text = user.name,
                                            style = MaterialTheme.typography.h6,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            text = "@${user.screenName}",
                                        )
                                    }
                                    relationship?.let {
                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = if (it.followedBy) "Following" else "Follow",
                                                style = MaterialTheme.typography.h6,
                                                color = MaterialTheme.colors.primary,
                                            )
                                            if (it.following) {
                                                Text(
                                                    text = "Follows you",
                                                    style = MaterialTheme.typography.caption,
                                                )
                                            }
                                        }
                                    } ?: run {
                                        if (!loaded) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                                ListItem(
                                    text = {
                                        Text(text = user.desc)
                                    }
                                )
                                user.website?.let {
                                    ListItem(
                                        icon = {
                                            Icon(asset = Icons.Default.Link)
                                        },
                                        text = {
                                            Text(
                                                text = it,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    )
                                }
                                user.location?.let {
                                    ListItem(
                                        icon = {
                                            Icon(asset = Icons.Default.MyLocation)
                                        },
                                        text = {
                                            Text(
                                                text = it,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                                Row {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(text = user.friendsCount.toString())
                                        Text(text = "Following")
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(text = user.followersCount.toString())
                                        Text(text = "Followers")
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(text = user.listedCount.toString())
                                        Text(text = "Listed")
                                    }
                                }
                                Spacer(modifier = Modifier.height(standardPadding * 2))
                            }
                        }
                    }

                    item {
                        UserTabs(
                            items = tabs,
                            selectedItem = selectedItem,
                            onItemSelected = {
                                setSelectedItem(it)
                            },
                        )
                    }

                    if (loaded) {
                        when (selectedItem) {
                            0 -> {
                                itemsIndexed(timeline) { index, item ->
                                    Column {
                                        if (!timelineLoadingMore && index == timeline.size - 1) {
                                            coroutineScope.launch {
                                                timelineViewModel.loadTimeline(user)
                                            }
                                        }
                                        TimelineStatusComponent(item)
                                        if (index != timeline.size - 1) {
                                            Divider(
                                                modifier = Modifier.padding(
                                                    start = profileImageSize + standardPadding,
                                                    end = standardPadding
                                                )
                                            )
                                        }
                                        if (timelineLoadingMore && index == timeline.size - 1) {
                                            LoadingProgress()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserTabs(
    items: List<VectorAsset>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedItem,
        backgroundColor = MaterialTheme.colors.background,
        indicator = { tabPositions ->
            TabConstants.DefaultIndicator(
                modifier = Modifier.defaultTabIndicatorOffset(tabPositions[selectedItem]),
                color = MaterialTheme.colors.primary,
            )
        }
    ) {
        for (i in 0 until items.count()) {
            Tab(
                selected = selectedItem == i,
                onClick = {
                    onItemSelected(i)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = EmphasisAmbient.current.medium.applyEmphasis(
                    contentColor()
                ),
            ) {
                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(asset = items[i])
                }
            }
        }
    }
}