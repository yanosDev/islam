package de.yanos.islam.data.repositories.source

import de.yanos.islam.data.database.dao.AwqatDao

interface LocalAwqatSource {
}

class LocalAwqatSourceImpl(
    private val dao: AwqatDao
) : LocalAwqatSource {

}