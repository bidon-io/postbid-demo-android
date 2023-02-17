package io.bidon.mediation.example

import io.bidon.mediation.adapter.admob.AdMobLineItem

object Params {

    object AdMob {
        /**
         * Each ad unit is configured in the [AdMob dashboard](https://apps.admob.com).
         * For each ad unit, you need to set up an eCPM floor and switch off auto refresh.
         * [AdMobLineItem] stores ad unit compliance and eCPM floor.
         */
        val ADMOB_BANNER_LINE_ITEMS = setOf(AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_1", 1.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_2", 2.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_3", 3.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_4", 4.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_5", 5.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_6", 6.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_7", 7.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_8", 8.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_9", 9.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_10", 10.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_11", 11.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_12", 12.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_13", 13.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_14", 14.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_15", 15.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_16", 16.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_17", 17.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_18", 18.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_19", 19.0),
                                            AdMobLineItem("ADMOB_BANNER_AD_UNIT_ID_20", 20.0))

        val ADMOB_INTERSTITIAL_LINE_ITEMS = setOf(AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_1", 1.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_2", 2.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_3", 3.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_4", 4.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_5", 5.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_6", 6.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_7", 7.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_8", 8.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_9", 9.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_10", 10.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_11", 11.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_12", 12.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_13", 13.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_14", 14.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_15", 15.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_16", 16.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_17", 17.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_18", 18.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_19", 19.0),
                                                  AdMobLineItem("ADMOB_INTERSTITIAL_AD_UNIT_ID_20", 20.0))

        val ADMOB_REWARDED_LINE_ITEMS = setOf(AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_1", 1.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_2", 2.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_3", 3.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_4", 4.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_5", 5.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_6", 6.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_7", 7.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_8", 8.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_9", 9.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_10", 10.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_11", 11.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_12", 12.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_13", 13.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_14", 14.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_15", 15.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_16", 16.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_17", 17.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_18", 18.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_19", 19.0),
                                              AdMobLineItem("ADMOB_REWARDED_AD_UNIT_ID_20", 20.0))
    }

}