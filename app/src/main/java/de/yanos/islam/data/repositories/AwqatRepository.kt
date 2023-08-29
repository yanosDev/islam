package de.yanos.islam.data.repositories

import de.yanos.islam.data.repositories.source.LocalAwqatSource
import de.yanos.islam.data.repositories.source.RemoteAwqatSource
import javax.inject.Inject

interface AwqatRepository {
}

class AwqatRepositoryImpl @Inject constructor(
    private val localSource: LocalAwqatSource,
    private val remoteSource: RemoteAwqatSource
) : AwqatRepository {

}