package pl.hypeapp.episodie.ui.features.top.adapter

import pl.hypeapp.episodie.ui.base.adapter.ViewTypeDelegateAdapter

interface TopListOnViewSelectedListener : ViewTypeDelegateAdapter.OnViewSelectedListener {

    fun onAddToWatched(tvShowId: String)

}
