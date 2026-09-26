# Changelog

Short, human "what's new" notes per release -- the same text shown in the
in-app update dialog when a new version is detected. Add a new `## X.Y.Z`
section at the top before bumping `versionName` in `app/build.gradle.kts`.

Entries here are user-facing only: fixes, bugs, improvements, and features.
No internal/process notes (CI changes, release cleanup, repo housekeeping,
etc.) -- if it wouldn't mean anything to someone who just installed the app,
it doesn't belong here.

## 3.11.0
- Checking for updates when the app opens is now optional and off by default -- turn it on in Settings, or tap "Check for updates" any time
- The update dialog always shows what's new, with a link to the full release notes on GitHub
- Updates are checked against GitHub's published checksum before installing, and aren't installed if it doesn't match
- In local mode, audio never goes to the cloud -- if no local model is ready, you're told why instead
- Fixed a crash when the on-device speech engine couldn't be loaded
- Dictated text placed on the clipboard is marked as sensitive, so Android 13+ hides its preview
- Text from the field you're dictating into is no longer written to the system log

## 3.10.0
- Refreshed the app's look with Material 3 Expressive: a richer color palette and more expressive buttons, switches, and tabs
- Smoother, springier animations across the settings screen

## 3.9.0
- Help walkthrough for Android 13+'s "Restricted settings" block, shown before opening Accessibility settings if it's not enabled yet
- The update dialog now shows what's new in the new version, not just the version number
