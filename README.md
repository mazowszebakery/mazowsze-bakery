# MAZOWSZE BAKERY — Android starter

This is an initial Android UI prototype. It displays a small sample product list, lets the user adjust quantities locally, and calculates an indicative GBP total.

## Build
GitHub Actions runs `gradle assembleDebug` and uploads the debug APK as an artifact.

## Not implemented yet
This is not a production ordering system. Authentication for up to 50 staff, administrator permissions, cloud database/synchronisation, editable product catalogue and limits, persistent order submission, 16:00 UK-time lock, order archive, and scheduled email reports still require backend implementation and configuration.
