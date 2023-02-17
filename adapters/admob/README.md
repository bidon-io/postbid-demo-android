# AdMob adapter

> **Warning** The price that will be used as the price of the loaded ad is the ```price``` from the ```AdMobLineItem```.

## Supported type

* Banner
* Interstitial
* Rewarded

## Integration

How to integrate the AdMob, see [the official documentation](https://developers.google.com/admob/android/quick-start).

## Network ad unit

Configure and add ```AdMobNetworkAdUnit``` to list of ad unit for the ```AdMob``` to participate in mediation.

| Parameter | Required | Type                      |
|-----------|----------|---------------------------|
| lineItems | Yes      | Collection<AdMobLineItem> |
| adRequest | No       | AdRequest                 |
| adSize    | No       | AdSize                    |

```AdMobLineItem``` contains information about the ad unit id from [AdMob dashboard](https://apps.admob.com) and the
price corresponding to it.

> **Warning** Switch off auto refresh for the banner ad unit in the [AdMob dashboard](https://apps.admob.com) before load it.