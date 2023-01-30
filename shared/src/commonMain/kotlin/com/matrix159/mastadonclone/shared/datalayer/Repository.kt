package com.matrix159.mastadonclone.shared.datalayer

import com.matrix159.mastadonclone.shared.datalayer.models.MastadonApiApplication
import com.matrix159.mastadonclone.shared.datalayer.models.mastadonapi.instance.InstanceResponseJson
import com.matrix159.mastadonclone.shared.datalayer.sources.localsettings.MastadonSettings
import com.matrix159.mastadonclone.shared.datalayer.sources.runtimecache.CacheObjects
import com.matrix159.mastadonclone.shared.datalayer.sources.webservices.MastadonApiRemoteDataSource
import com.matrix159.mastadonclone.shared.datalayer.sources.webservices.MastadonRemoteDataSource
import com.russhwolf.settings.Settings
import kotlinx.coroutines.*

class Repository(/*val sqlDriver: SqlDriver*,*/
  val settings: Settings = Settings(),
  val useDefaultDispatcher: Boolean = true
) {

  internal val mastadonSettings: MastadonSettings by lazy { MastadonSettings(settings) }
  internal val mastadonApi: MastadonRemoteDataSource by lazy {
    MastadonApiRemoteDataSource(
      mastadonSettings
    )
  }

  //internal val localDb by lazy { LocalDb(sqlDriver) }
  internal val runtimeCache get() = CacheObjects

  // we run each repository function on a Dispatchers.Default coroutine
  // we pass useDefaultDispatcher=false just for the TestRepository instance
  suspend fun <T> withRepoContext(block: suspend () -> T): T {
    return if (useDefaultDispatcher) {
      withContext(Dispatchers.Default) {
        block()
      }
    } else {
      block()
    }
  }

  // TODO: Create domain layer data models so we aren't using the JSON models directly for below functions
  suspend fun getClientApplication(): MastadonApiApplication = withContext(Dispatchers.Default) {
    if (
      mastadonSettings.authState.clientId != null &&
      mastadonSettings.authState.clientSecret != null &&
      mastadonSettings.authState.redirectUri != null
    ) {
      MastadonApiApplication(
        clientId = mastadonSettings.authState.clientId!!,
        clientSecret = mastadonSettings.authState.clientSecret!!,
        redirectUri = mastadonSettings.authState.redirectUri!!,
      )
    } else {
      // If values aren't stored locally, retrieve and store them
      val apiResponse = mastadonApi.createClientApplication()
      mastadonSettings.authState = mastadonSettings.authState.copy(
        clientId = apiResponse.clientId,
        clientSecret = apiResponse.clientSecret,
        redirectUri = apiResponse.redirectUris
      )
      MastadonApiApplication(
        clientId = apiResponse.clientId,
        clientSecret = apiResponse.clientSecret,
        redirectUri = apiResponse.redirectUris,
      )
    }
  }

  suspend fun getInstance(serverUrl: String): InstanceResponseJson? {
    return mastadonApi.getInstance(serverUrl)
  }

}