package de.yanos.islam

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.util.AppSettings
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettings: AppSettings,
    @ApplicationContext private val context: Context,
    private val db: IslamDatabase
) : ViewModel() {
    init {
        if (!appSettings.isDBInitialized) {
            initDB()
        }
    }

    private fun initDB() {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.islam)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        do {
            var section: String? = null
            line = bufferedReader.readLine()
            if (line.isEmpty())
                section
            els
        } while (line != null)
        while (line != null) {
            // `the words in the file are separated by space`, so to get each words
            val words = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            line = bufferedReader.readLine()
        }
        appSettings.isDBInitialized = true
    }
}