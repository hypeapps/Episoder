package pl.hypeapp.presentation.tvshowdetails

import pl.hypeapp.domain.model.TvShowModel
import pl.hypeapp.presentation.base.View

interface TvShowDetailsView : View {

    fun setNavigationBarOptions()

    fun onChangeWatchTvShowState()

    var model: TvShowModel

    fun setCover(url: String?)

    fun setBackdrop(backdropUrl: String?, placeholderUrl: String?)

    fun initPagerAdapter(tvShowModel: TvShowModel?)

    fun setTitle(title: String?)

    fun setPremiered(premiered: String?)

    fun setStatus(status: String?)

    fun setFullRuntime(fullRuntime: Long?)

    fun updateWatchState(watchState: Int)

    fun expandAppBar()

    fun startFabButtonAnimation()

    fun onSharePressed()

    fun onBackArrowPressed()

    fun onWatchStateChangeError()

    fun hideFabButton()

    fun onWatchStateChanged()

    fun showRuntimeNotification(oldUserRuntime: Long, newRuntime: Long)
}
