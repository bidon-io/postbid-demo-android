package io.bidon.mediation.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAdFormat
import com.google.android.gms.ads.AdSize
import io.bidmachine.banner.BannerSize
import io.bidon.mediation.adapter.admob.AdMobNetworkAdUnit
import io.bidon.mediation.adapter.bidmachine.BidMachineNetworkAdUnit
import io.bidon.mediation.adapter.max.MaxNetworkAdUnit
import io.bidon.mediation.example.databinding.ActivityBannerBinding
import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.mediationad.BannerMediationAdView
import io.bidon.mediation.sdk.mediationad.BannerMediationAdViewListener

class BannerActivity : AppCompatActivity() {

    companion object {
        private const val REFRESH_TIME_MS = 15000L

        fun createIntent(context: Context): Intent {
            return Intent(context, BannerActivity::class.java)
        }
    }

    private lateinit var binding: ActivityBannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBannerBinding.inflate(layoutInflater).apply {
            setContentView(root)

            bLoad.setOnClickListener {
                bShow.isEnabled = false
                bannerAdView.loadAd()
            }
            bShow.setOnClickListener {
                bannerAdView.visibility = View.VISIBLE
            }
            bHide.setOnClickListener {
                bannerAdView.visibility = View.GONE
            }
            bStopRefresh.setOnClickListener {
                bannerAdView.stopAutoRefresh()
            }
            bStartRefresh.setOnClickListener {
                bannerAdView.startAutoRefresh()
            }
            bDestroy.setOnClickListener {
                bannerAdView.destroy()
            }
            bannerAdView.apply {
                listener = BannerMediationListener()
                setAutoRefreshTime(REFRESH_TIME_MS)
                setPreBidNetworkAdUnit(listOf(MaxNetworkAdUnit(getString(R.string.max_banner_ad_unit_id))
                                                      .setMaxAdFormat(MaxAdFormat.BANNER)))
                setPostBidNetworkAdUnit(listOf(AdMobNetworkAdUnit(Params.AdMob.ADMOB_BANNER_LINE_ITEMS)
                                                       .setAdSize(AdSize.BANNER),
                                               BidMachineNetworkAdUnit()
                                                       .setBannerSize(BannerSize.Size_320x50)))
            }
        }
    }


    private inner class BannerMediationListener : BannerMediationAdViewListener {

        override fun onAdLoaded(mediationAd: BannerMediationAdView) {
            binding.bShow.isEnabled = true

            Toast.makeText(this@BannerActivity, "Banner loaded", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToLoad(mediationAd: BannerMediationAdView) {
            Toast.makeText(this@BannerActivity, "Banner failed to load", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToShow(mediationAd: BannerMediationAdView, adError: MediationError) {
            Toast.makeText(this@BannerActivity, "Banner failed to show", Toast.LENGTH_SHORT).show()
        }

        override fun onAdShown(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@BannerActivity, "Banner shown", Toast.LENGTH_SHORT).show()
        }

        override fun onAdClicked(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@BannerActivity, "Banner clicked", Toast.LENGTH_SHORT).show()
        }

        override fun onAdExpired(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@BannerActivity, "Banner expired", Toast.LENGTH_SHORT).show()
        }

        override fun onAdExpanded(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@BannerActivity, "Banner expanded", Toast.LENGTH_SHORT).show()
        }

        override fun onAdCollapsed(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@BannerActivity, "Banner collapsed", Toast.LENGTH_SHORT).show()
        }

    }

}