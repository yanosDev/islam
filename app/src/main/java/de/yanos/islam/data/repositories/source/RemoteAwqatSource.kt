package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.api.AwqatApi
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

interface RemoteAwqatSource {
}

class RemoteAwqatSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val api: AwqatApi,
) : RemoteAwqatSource {

}