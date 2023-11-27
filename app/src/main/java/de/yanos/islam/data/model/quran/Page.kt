package de.yanos.islam.data.model.quran

data class Page(val page: Int, val pageSurahName: String, val pageSurahId: Int, val ayahs: List<Ayah>)