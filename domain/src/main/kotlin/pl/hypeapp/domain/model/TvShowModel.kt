package pl.hypeapp.domain.model

import java.io.Serializable

data class TvShowModel(val id: String?,
                       val imdbId: String?,
                       val name: String?,
                       val network: String?,
                       val status: String?,
                       val summary: String?,
                       val episodeRuntime: Long?,
                       val genre: String?,
                       val officialSite: String?,
                       val fullRuntime: Long?,
                       val premiered: String?,
                       val imageMedium: String?,
                       val imageOriginal: String?) : Serializable
