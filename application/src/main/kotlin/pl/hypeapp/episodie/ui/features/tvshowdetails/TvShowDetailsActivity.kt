package pl.hypeapp.episodie.ui.features.tvshowdetails

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import butterknife.OnClick
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_tv_show_details.*
import pl.hypeapp.domain.model.TvShowModel
import pl.hypeapp.domain.model.WatchState
import pl.hypeapp.episodie.App
import pl.hypeapp.episodie.R
import pl.hypeapp.episodie.di.components.ActivityComponent
import pl.hypeapp.episodie.di.components.DaggerActivityComponent
import pl.hypeapp.episodie.extensions.*
import pl.hypeapp.episodie.glide.GlideApp
import pl.hypeapp.episodie.glide.transformation.BlurTransformation
import pl.hypeapp.episodie.navigation.EXTRA_INTENT_TV_SHOW_MODEL
import pl.hypeapp.episodie.navigation.STATE_CHANGED
import pl.hypeapp.episodie.navigation.STATE_NOT_CHANGED
import pl.hypeapp.episodie.ui.animation.pagetransformer.DepthPageTransformer
import pl.hypeapp.episodie.ui.base.BaseActivity
import pl.hypeapp.episodie.ui.features.tvshowdetails.adapter.TvShowDetailsPagerAdapter
import pl.hypeapp.episodie.ui.viewmodel.TvShowModelParcelable
import pl.hypeapp.presentation.tvshowdetails.TvShowDetailsPresenter
import pl.hypeapp.presentation.tvshowdetails.TvShowDetailsView
import javax.inject.Inject

class TvShowDetailsActivity : BaseActivity(), TvShowDetailsView, TvShowDetailsPagerAdapter.OnChangePageListener {

    override fun getLayoutRes(): Int = R.layout.activity_tv_show_details

    @Inject lateinit var presenter: TvShowDetailsPresenter

    private val component: ActivityComponent
        get() = DaggerActivityComponent.builder()
                .appComponent((application as App).component)
                .build()

    private var isWatchStateChanged: Boolean = false

    override lateinit var model: TvShowModel

    private lateinit var tvShowModelParcelable: TvShowModelParcelable

    val watchStateChangedNotifySubject: PublishSubject<String> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        tvShowModelParcelable = intent.getParcelableExtra(EXTRA_INTENT_TV_SHOW_MODEL)
        model = tvShowModelParcelable.mapToTvShowModel()
        presenter.onAttachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetachView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(WATCH_STATE_CHANGED, isWatchStateChanged)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        isWatchStateChanged = savedInstanceState?.getBoolean(WATCH_STATE_CHANGED)!!
    }

    override fun initPagerAdapter(tvShowModel: TvShowModel?) = with(view_pager_tv_show_details) {
        val tvShowDetailsPagerAdapter = TvShowDetailsPagerAdapter(supportFragmentManager, tvShowModelParcelable)
        adapter = tvShowDetailsPagerAdapter
        setPageTransformer(true, DepthPageTransformer())
        addOnPageChangeListener(this@TvShowDetailsActivity)
        tab_layout_tv_show_details.setupWithViewPager(this)
        presenter.onPagerAdapterInit()
    }

    override fun startFabButtonAnimation() {
        YoYo.with(Techniques.ZoomIn)
                .duration(700)
                .delay(200)
                .playOn(fab_button_tv_show_details_add_to_watched)
    }

    override fun setNavigationBarOptions() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        setNavigationBarColor(ContextCompat.getColor(this, R.color.tv_show_details_navigation_bar))
        if (isLandscapeOrientation() and !isNavigationBarLandscape()) {
            setLandscapeNavBarPaddingEnd(image_view_tv_show_details_ic_share, fab_button_tv_show_details_add_to_watched,
                    coordinator_layout_tv_show_details)
        }
    }

    override fun setFullRuntime(fullRuntime: Long?) {
        if (fullRuntime!! > 0)
            text_view_tv_show_details_full_runtime.setFullRuntime(fullRuntime)
        else
            card_view_tv_show_details_full_runtime.visibility = View.GONE
    }

    override fun setTitle(title: String?) {
        text_view_tv_show_details_title.text = title
    }

    override fun setPremiered(premiered: String?) {
        text_view_tv_show_details_premiered.text = String.format(getString(R.string.item_all_format_premiered), premiered)
    }

    override fun setStatus(status: String?) {
        text_view_tv_show_details_status.text = String.format(getString(R.string.tv_show_details_status), status)
    }

    override fun onPageSelected(position: Int) = presenter.onPageSelected(position)

    override fun setCover(url: String?) {
        image_view_tv_show_details_cover.loadImage(url)
    }

    override fun setBackdrop(backdropUrl: String?, placeholderUrl: String?) {
        GlideApp.with(this)
                .load(backdropUrl)
                .override(340, 560)
                .thumbnail(GlideApp.with(this).load(placeholderUrl).transform(BlurTransformation(this, 20)))
                .transform(MultiTransformation<Bitmap>(BlurTransformation(this, 20), CenterCrop()))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(noise_view_tv_show_details)
    }

    override fun hideFabButton() = fab_button_tv_show_details_add_to_watched.viewGone()

    override fun expandAppBar() = app_bar_tv_show_details.setExpanded(true, true)

    @OnClick(R.id.dummy_view_tv_show_details_back_arrow)
    override fun onBackArrowPressed() = onBackPressed()

    override fun onBackPressed() {
        when (isWatchStateChanged) {
            true -> setResult(STATE_CHANGED)
            else -> setResult(STATE_NOT_CHANGED)
        }
        super.onBackPressed()
    }

    @OnClick(R.id.dummy_view_tv_show_details_share)
    override fun onSharePressed() {
        // TODO
    }

    override fun updateWatchState(watchState: Int) {
        fab_button_tv_show_details_add_to_watched.manageWatchStateIcon(watchState)
    }

    @OnClick(R.id.fab_button_tv_show_details_add_to_watched)
    override fun onChangeWatchTvShowState() = with(fab_button_tv_show_details_add_to_watched) {
        isWatchStateChanged = true
        // Workaround for better animation flow.
        hide()
        show()
        smallBangAnimator.bang(this)
        val watchState = model.watchState
        postDelayed({ manageWatchStateIcon(WatchState.manageWatchState(watchState)) }, 200)
        presenter.onChangeWatchedTvShowState()
    }

    override fun onChangeWatchStateError() {
        Toast.makeText(applicationContext, getString(R.string.all_toast_error_message), Toast.LENGTH_LONG).show()
    }

    override fun onChangedWatchState() {
        watchStateChangedNotifySubject.onNext(WATCH_STATE_CHANGED)
    }

    fun onChangedChildFragmentWatchState() {
        isWatchStateChanged = true
        presenter.updateWatchState()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setNavigationBarColor(@ColorInt color: Int) {
        window.navigationBarColor = color
    }

    private fun setLandscapeNavBarPaddingEnd(vararg views: View) = views.forEach {
        with(it) {
            setPadding(paddingStart, paddingTop, (paddingEnd + getNavigationBarSize().x), paddingBottom)
        }
    }

    companion object {
        const val WATCH_STATE_CHANGED = "WATCH_STATE_CHANGED"
    }

}
