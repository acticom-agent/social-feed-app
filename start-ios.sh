#!/bin/bash
# Build and run Social Feed on iOS Simulator
set -e

# Find an available iPhone simulator
DEVICE_ID=$(xcrun simctl list devices available -j | python3 -c "
import json, sys
data = json.load(sys.stdin)
for runtime, devices in data['devices'].items():
    if 'iOS' in runtime:
        for d in devices:
            if 'iPhone' in d['name'] and d['state'] != 'Booted':
                print(d['udid']); sys.exit(0)
            elif 'iPhone' in d['name'] and d['state'] == 'Booted':
                print(d['udid']); sys.exit(0)
" 2>/dev/null)

if [ -z "$DEVICE_ID" ]; then
    echo "Error: No iPhone simulator found."
    exit 1
fi

DEVICE_NAME=$(xcrun simctl list devices | grep "$DEVICE_ID" | sed 's/ (.*//' | xargs)
echo "=== Using iOS Simulator: $DEVICE_NAME ==="

echo "Booting simulator..."
xcrun simctl boot "$DEVICE_ID" 2>/dev/null || true
open -a Simulator

echo "=== Building Social Feed iOS app ==="
cd "$(dirname "$0")/ios"
xcodebuild -project SocialFeed.xcodeproj -scheme SocialFeed \
    -destination "platform=iOS Simulator,id=$DEVICE_ID" \
    -derivedDataPath build build 2>&1 | tail -3

echo "=== Installing and launching ==="
xcrun simctl install "$DEVICE_ID" build/Build/Products/Debug-iphonesimulator/SocialFeed.app
xcrun simctl launch "$DEVICE_ID" com.example.socialfeed
echo "Social Feed is running on iOS Simulator!"
