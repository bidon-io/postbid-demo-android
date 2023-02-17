# BidMachine adapter

## Supported type

* Banner
* Interstitial
* Rewarded

## Integration

How to integrate the BidMachine, see [the official documentation](https://docs.bidon.io/docs/in-house-mediation).

## Network ad unit

Configure and add ```BidMachineNetworkAdUnit``` to list of ad unit for the ```BidMachine``` to participate in mediation.

| Parameter       | Required | Type                |
|-----------------|----------|---------------------|
| targetingParams | No       | TargetingParams     |
| networkList     | No       | List<NetworkConfig> |
| customParams    | No       | CustomParams        |
| placementId     | No       | String              |
| bannerSize      | No       | BannerSize          |
| adContentType   | No       | AdContentType       |