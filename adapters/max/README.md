# MAX adapter

> **Warning** MAX adapter does not support price floor setting, so using it in a post bid will not work correctly.

## Supported type

* Banner
* Interstitial
* Rewarded

## Integration

How to integrate the Applovin MAX,
see [the official documentation](https://dash.applovin.com/documentation/mediation/android/getting-started/integration).

## Network ad unit

Configure and add ```MaxNetworkAdUnit``` to list of ad unit for the ```MAX``` to participate in mediation.

| Parameter            | Required | Type                |
|----------------------|----------|---------------------|
| adUnitId             | Yes      | String              |
| maxAdFormat          | No       | MaxAdFormat         |
| extraParameters      | No       | Map<String, String> |
| localExtraParameters | No       | Map<String, Any>    |
| customData           | No       | String              |
| placement            | No       | String              |