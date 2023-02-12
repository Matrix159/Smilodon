package com.matrix159.mastadonclone.shared.mvitests.screens.homefeed

import app.cash.turbine.test
import com.matrix159.mastadonclone.shared.data.Repository
import com.matrix159.mastadonclone.shared.data.models.mastadonapi.common.Account
import com.matrix159.mastadonclone.shared.data.models.mastadonapi.timelines.Status
import com.matrix159.mastadonclone.shared.fakes.FakeRepository
import com.matrix159.mastadonclone.shared.mvi.screens.homefeed.HomeFeedActions
import com.matrix159.mastadonclone.shared.mvi.screens.homefeed.HomeFeedEffects
import com.matrix159.mastadonclone.shared.mvi.screens.homefeed.HomeFeedPost
import com.matrix159.mastadonclone.shared.mvi.screens.homefeed.HomeFeedState
import com.matrix159.mastadonclone.shared.mvi.screens.homefeed.HomeFeedStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeFeedStoreTests : KoinTest {

  private lateinit var homeFeedStore: HomeFeedStore
  private lateinit var repository: Repository

  @BeforeTest
  fun before() {
    startKoin {
      //modules
      modules(
        module {
          single<Repository> { FakeRepository() }
          single { HomeFeedStore(repository = get()) }
        }
      )
    }

    homeFeedStore = get()
  }

  @AfterTest
  fun after() {
    stopKoin()
  }

  // ------ ACTIONS ------
  @Test
  fun testInitialState() = runTest {
    assertEquals(HomeFeedState(isLoading = true), homeFeedStore.state.value)
  }

  @Test
  fun testLoadingActions() = runTest {
    homeFeedStore.dispatchAction(HomeFeedActions.StartLoading)
    assertEquals(HomeFeedState(isLoading = true), homeFeedStore.state.value)

    homeFeedStore.dispatchAction(HomeFeedActions.StopLoading)
    assertEquals(HomeFeedState(isLoading = false), homeFeedStore.state.value)
  }

  @Test
  fun testShowErrorAction() = runTest {
    val errorMessage = "This is an error"
    homeFeedStore.dispatchAction(HomeFeedActions.ShowError(errorMessage))
    assertEquals(HomeFeedState(isLoading = false, error = errorMessage), homeFeedStore.state.value)
  }

  @Test
  fun testUpdatePostsAction() = runTest {
    val posts = listOf(
      HomeFeedPost(status = Status(account = Account(), content = "asdads", createdAt = "timehere")),
      HomeFeedPost(status = Status(account = Account(), content = "zxcags", createdAt = "anothertimehere"))
    )
    homeFeedStore.dispatchAction(HomeFeedActions.UpdatePosts(homeFeedPosts = posts))
    assertEquals(
      HomeFeedState(isLoading = false, error = null, homeFeedPosts = posts),
      homeFeedStore.state.value
    )
  }

  // ------ EFFECTS ------

  @Test
  fun testInitEffect() = runTest {
    val posts = listOf(
      HomeFeedPost(status = Status(account = Account(), content = "asdads", createdAt = "timehere")),
      HomeFeedPost(status = Status(account = Account(), content = "zxczxc", createdAt = "anothertimehere")),
    )
    homeFeedStore.state.test {
      assertEquals(HomeFeedState(isLoading = true), awaitItem())
      homeFeedStore.dispatchEffect(HomeFeedEffects.Init)
      assertEquals(
        HomeFeedState(isLoading = false, error = null, homeFeedPosts = posts),
        awaitItem()
      )
    }
  }
}
