# NagramXF Project Context

> Technical memory of the current repository state. This file documents verified source state, not conversation history. Last updated: 2026-09-19.

# 1. Project Overview

- **Project:** Nagram XF
- **Repository:** `mtpali/NagramXF`
- **Local source root:** repository root containing `TMessagesProj/`
- **Branch:** `main`
- **Current HEAD:** `6f3721315e1eab886454faca368a31a6710673ee`
- **HEAD subject:** `fix: complete advanced forward flow and slim settings`
- **Working tree:** uncommitted changes from the current development cycle
- **Project type:** Android Telegram client fork based on Nagram X/Telegram Android
- **Version:** `APP_VERSION_NAME=12.10.1`, official `APP_VERSION_CODE=7038`; module build code is currently `1250`
- **Application ID:** `fork.risin42.nagramxf`
- **Android namespace/source namespace:** `org.telegram.messenger`
- **Primary technologies:** Java 21, Kotlin, Android SDK 36, Gradle 9.4, Android Gradle Plugin from `buildSrc`, Android NDK `27.2.12479018`, CMake `3.31.6+`, native C/C++, Firebase, Room, OkHttp, Coroutines, optional plugin runtime/Chaquopy Python 3.11
- **Supported ABI:** `armeabi-v7a` only

## Architecture overview

- `TMessagesProj` is the Android application module.
- Telegram upstream UI/core code lives under `org.telegram.*`.
- Neko/Nagram settings and extensions live mainly under `tw.nekomimi.nekogram.*` and `xyz.nextalone.nagram.*`.
- AyuGram forwarding/custom message behavior lives under `com.radolyn.ayugram.*`.
- ExteraGram AI Chat remains under `com.exteragram.messenger.ai.*`.
- Settings use `ConfigItem` objects backed by Neko shared preferences.
- Drawer composition is registry/layout driven through `DrawerMenuHelper` and `DrawerLayoutAdapter`.
- Normal and plugin product flavors share the same ARMv7-only application module; plugin flavor adds the Chaquopy runtime.

## Purpose

Provide a customized Telegram client while preserving existing NagramXF behavior, Telegram UI conventions, low-risk source changes, and ARMv7 compatibility.

# 2. Current Project Status

- **Development stage:** implementation and source-level validation completed; compilation and device validation pending.
- **Latest work:** Drawer Proxy item, moved download-icon setting, N-Settings icon update, forum-aware Advanced Forward, Saved Text emoji-panel tab, image cache deletion, Add-only proxy action, removal of speed boosts and AI Translator, default-theme reduction, and application-ID migration.
- **Build status:** **blocked/unverified for the current working tree**. `./gradlew TMessagesProj:assembleNormalStaging` cannot download Gradle 9.4 because the execution environment has no access to `services.gradle.org`. The environment also exposes JDK 17, while the project requires JDK 21.
- **Last known successful baseline build:** GitHub Actions run `34826865683` for the committed baseline; it does not include the current uncommitted cycle.
- **Deployment/release:** none for the current changes.
- **Testing:** XML/JSON parsing, removed-reference scans, resource checks, package-ID scan, theme-asset check, ABI configuration check, and `git diff --check` pass. No APK, instrumentation, UI automation, or physical-device test exists for this cycle.
- **Stability:** provisional until a clean ARMv7 staging build and manual Android tests pass.

# 3. Implemented Features

## Proxy Item in Drawer Elements

- **Purpose:** make Proxy configurable like other sidebar items.
- **Behavior:** `N-Settings → Drawer Elements → Sidebar items` contains Proxy. It can be reordered, shown, hidden, and opened from the drawer. It is hidden by default for existing/default layouts.
- **Implementation:** registered existing drawer ID `13`, added proxy label/icon construction, routing to `ProxyListActivity`, and legacy-layout default state.
- **Files:** `DrawerMenuHelper.kt`, `DrawerLayoutAdapter.java`, `MainMenuActions.kt`, `LaunchActivity.java`.
- **Dependencies:** existing `SharedConfig` proxy state and `ProxyListActivity`.
- **Limitations:** dynamic proxy on/off icon refresh follows the drawer adapter refresh lifecycle.

## Always Show Download Icon in Drawer Elements

- **Purpose:** move the setting out of top-level Appearance.
- **Behavior:** option now appears in `N-Settings → Drawer Elements`; toggling it preserves the original config key and displays the restart-required notice.
- **Implementation:** removed the Appearance row and added a `TextCheckCell` row to `SidebarMenuActivity` using the existing `NaConfig.alwaysShowDownloadIcon` item.
- **Files:** `NekoAppearanceSettingsActivity.java`, `SidebarMenuActivity.java`.
- **Compatibility:** existing user value is preserved because the configuration key was not renamed.

## N-Settings Icon

- **Purpose:** align N-Settings with Telegram's standard Settings visual language.
- **Behavior:** N-Settings uses `msg_settings` instead of the Telegram logo.
- **Files:** `DrawerMenuHelper.kt`, `DrawerLayoutAdapter.java`.

## Forum-Compatible Advanced Forward

- **Purpose:** allow edited Advanced Forward messages to send into forum/topic destinations.
- **Behavior preserved:** editable text/captions, multiple destinations, no original sender attribution, media/albums/documents/audio/GIF/video handling, comments before/after forwarding, notification/schedule/payment parameters.
- **Implementation:** every selected `MessagesStorage.TopicKey` is resolved into destination context:
  - regular forum: creates a topic-main `MessageObject` from `topicStartMessage` and passes it as reply/top-message context;
  - monoforum: passes the destination topic ID as `monoForumPeerId`;
  - simple chat: passes no topic context.
- **Files:** `ChatActivity.java`; existing `AyuForward.java` consumes the supplied context without structural refactoring.
- **Limitations:** target topic data must already be available from `TopicsController`; missing topic metadata falls back to a non-topic send. Physical-device verification is required.

## Saved Text in Emoji Panel

- **Purpose:** store and quickly insert frequently used text.
- **Behavior:** a `Text` tab appears beside Emoji/GIF/Stickers. Users can add text, tap to insert at the current selection/cursor, long-press to edit/delete, and view an empty state.
- **Implementation:** `SavedTextPanel` uses a Telegram-style `RecyclerListView` and dialogs. Ordered snippets are stored as a JSON array in global emoji preferences under `saved_text_snippets_v1`. `EmojiView` now resolves tabs by type instead of assuming fixed numeric positions.
- **Files:** `SavedTextPanel.java`, `EmojiView.java`, `ChatActivityEnterView.java`, English/Persian `strings_naxf.xml`.
- **Dependencies:** existing Telegram UI components and `MessagesController.getGlobalEmojiSettings()`; no new dependency.
- **Limitations:** snippets are device-local, unsynced, and stored as plain preference text. No explicit item reorder UI or duplicate prevention exists.

## Delete Downloaded Images

- **Purpose:** expose the existing delete-downloaded-file action for cached photos.
- **Behavior:** a downloaded image with local media can use the same delete action as supported documents/media.
- **Implementation:** photo messages without a document are eligible when `isPhoto()` and `mediaExists` are true; existing `clearMessageFiles()` removes resolved message paths.
- **Files:** `MessageHelper.java`.
- **Limitations:** Android scoped-storage and alternate cache-path behavior require device testing.

## Add Proxy Without Connecting

- **Purpose:** save a proxy for later use without changing current connectivity.
- **Behavior:** proxy alert shows side-by-side `Add`/`افزودن` and existing `Connect Proxy` buttons. Add writes to the proxy list and closes the sheet without setting `proxy_enabled`, `currentProxy`, or `ConnectionsManager` proxy state.
- **Implementation:** constructs the existing `SharedConfig.ProxyInfo`, calls `SharedConfig.addProxy()`, posts `proxySettingsChanged`, and reuses the proxy-added bulletin.
- **Files:** `AndroidUtilities.java`, English/Persian `strings_naxf.xml`.

## Optimized Default Themes

- **Purpose:** reduce bundled theme resources based on `setting backup(1).json`.
- **Available bundled themes:** Blue, Dark Blue, Arctic Blue, Day, Night.
- **Removed bundled themes:** AMOLED, Monet Light, Monet Dark, Monet AMOLED.
- **Implementation:** removed registrations from `Theme.java` and deleted four `.attheme` assets. Loading of user/imported themes from `themes2` remains unchanged.
- **Limitations:** a legacy selection pointing specifically to a removed built-in theme falls back through existing default-theme behavior; exact removed-theme appearance is not retained.

## Application ID Migration

- **Purpose:** migrate the installed application identity from `fork.risin42.nagramx` to `fork.risin42.nagramxf`.
- **Implementation:** updated `APP_PACKAGE`, Google services package entry, manifest contact MIME types, authenticator account type, sync adapter account type, and package-targeted XML intent. Repository scan finds no remaining exact old application ID.
- **Files:** `gradle.properties`, `google-services.json`, `AndroidManifest.xml`, `res/xml/auth.xml`, `auth_menu.xml`, `contacts.xml`, `sync_contacts.xml`.
- **Architecture decision:** Java/Kotlin declarations and Android namespace remain `org.telegram.messenger`; application ID and source namespace are intentionally separate.
- **Limitations:** Android treats the new application ID as a separate app. Existing installed-app data, accounts, permissions, shortcuts, and signatures are not automatically migrated. A genuine Firebase config registered for the new package is still required.

# 4. Bug Fix History

## Advanced Forward Failed in Forum/Topic Destinations

**Problem:** edited Advanced Forward messages worked in simple chats but were not sent into forum/topic destinations.

**Root Cause:** destination selection returned `MessagesStorage.TopicKey`, but the edited-message path reduced it to only `dialogId`. `AyuForward` therefore received source-chat thread/monoforum context instead of the selected destination topic context.

**Solution:** preserve each target `TopicKey`, resolve regular forum top-message or monoforum peer context, and pass that context to Advanced Forward, normal forwarding, and optional before/after comments.

**Files Changed:** `ChatActivity.java`.

**Testing:** source path and API usage inspected; compilation/device verification pending.

## Downloaded Photos Missing Delete Action

**Problem:** `Delete downloaded files` appeared for documents and selected media types but not normal photos.

**Root Cause:** `messageObjectIsFile()` returned `false` whenever media type `4` had no document, which is the normal representation for Telegram photos.

**Solution:** treat locally available photos as deletable while retaining the existing deletion implementation.

**Files Changed:** `MessageHelper.java`.

**Testing:** condition and deletion path inspected; scoped-storage/device verification pending.

## Removed AI Double-Tap Action Could Leave Invalid Value

**Problem:** users who had AI Translate stored as double-tap action ID `9` would retain an action no longer present in the action registry.

**Root Cause:** UI/code removal alone does not rewrite existing shared preferences.

**Solution:** `NaConfig.fixConfig()` migrates incoming and outgoing action value `9` to normal Translate action `3`.

**Files Changed:** `NaConfig.kt`, `DoubleTap.kt`, `NekoChatSettingsActivity.java`, `ChatActivity.java`.

**Testing:** source/config migration inspection; runtime preference migration pending.

# 5. Code Change Summary

## Drawer configuration and icon

- **Files:** `DrawerMenuHelper.kt`, `DrawerLayoutAdapter.java`, `MainMenuActions.kt`, `LaunchActivity.java`, `SidebarMenuActivity.java`, `NekoAppearanceSettingsActivity.java`
- **Changes:** registered Proxy, routed actions, moved download-icon setting, replaced N-Settings icon.
- **Impact:** user-configurable drawer with unchanged underlying setting values.

## Advanced Forward topic fix

- **Files:** `ChatActivity.java`
- **Changes:** added `ForwardTargetContext`, target-topic resolution, and per-destination context propagation.
- **Impact:** forum/monoforum compatibility without changing media/caption forwarding internals.

## Saved Text

- **Files:** `SavedTextPanel.java`, `EmojiView.java`, `ChatActivityEnterView.java`, localized strings
- **Changes:** new persistent panel/tab and cursor-aware insertion callback.
- **Impact:** new local productivity feature; emoji/GIF/sticker positions are now type-resolved.

## Proxy Add-only

- **Files:** `AndroidUtilities.java`, localized strings
- **Changes:** added non-connecting save action and shared success-bulletin helper.
- **Impact:** proxy can be stored without changing network state.

## Image deletion

- **Files:** `MessageHelper.java`
- **Changes:** photos with local media qualify for existing deletion flow.
- **Impact:** consistent cache management across media types.

## Speed-boost removal

- **Files:** `NekoExperimentalSettingsActivity.java`, `NekoConfig.java`, `FileLoadOperation.java`, `FileUploadOperation.java`, English/Persian strings
- **Changes:** removed settings/config/resources; download/upload logic uses standard upstream conditions/chunk size.
- **Impact:** less experimental code and predictable transfer behavior.

## AI Translator removal

- **Files:** translator settings/menu/UI/controller call sites, `NaConfig.kt`, `AiConfig.java`, `AiController.java`, AI preferences/response UI, translated-message helpers, localized resources
- **Deleted:** `LlmConfig.kt`, `LlmPresetRegistry.kt`, `LlmEditTextFactory.java`, `LLMTranslator.kt`
- **Changes:** removed provider, configuration, context/preset/test UI, double-tap action, AI Chat coupling, and translator-specific resources.
- **Impact:** normal providers remain; independent AI Chat and Gemini transcription remain. Shared `llm` network/model utilities required by those independent features are retained.

## Theme/package optimization

- **Files:** `Theme.java`, four removed theme assets, Gradle/manifest/Google-services/account XML files
- **Changes:** reduced bundled themes and changed application ID.
- **Impact:** smaller resources and new Android application identity.

# 6. Architecture Decisions

- Preserve Telegram/NagramXF class boundaries; do not refactor unrelated upstream code.
- Keep `org.telegram.messenger` as namespace/source package. `APP_PACKAGE` alone defines installed application identity.
- Keep `armeabi-v7a` as the only packaged ABI; never add universal, ARM64, or x86 outputs without an explicit product decision.
- Keep Advanced Forward routed through `AyuForward`; destination context belongs at the call boundary rather than duplicating media-send logic.
- Resolve emoji-panel pages by tab type, not hard-coded page positions, because Text is now a fourth optional page.
- Store Saved Text locally with existing emoji preferences; do not add a database or dependency for this small feature.
- Preserve existing config keys when moving settings UI so user preferences survive upgrades.
- Removing AI Translator must not remove independent AI Chat/transcription utilities.
- Imported/custom theme loading must remain intact even when bundled defaults are reduced.
- Preserve visible branding as `Telegram`, English/Persian language scope, `/storage/emulated/0/Telegram` default path, and existing fresh-install defaults.
- Do not reintroduce previously removed Nagram About, N-Settings Passcode, Force Snowfall, or Recent Chats Sidebar features.

# 7. Configuration and Environment Changes

- `APP_PACKAGE=fork.risin42.nagramxf`.
- Compile/target SDK: 36; minimum SDK: 27.
- Java/Kotlin JVM target: 21.
- Gradle wrapper: 9.4.0.
- NDK: `27.2.12479018`; CMake: `3.31.6+`.
- Default `NATIVE_TARGET`: `armeabi-v7a`.
- Normal/plugin ABI lists: `armeabi-v7a` only; universal APK disabled.
- Build command: `NATIVE_TARGET=armeabi-v7a ./gradlew TMessagesProj:assembleNormalStaging`.
- Signing values may come from `local.properties`/`LOCAL_PROPERTIES` or `KEYSTORE_PASS`, `ALIAS_NAME`, `ALIAS_PASS`. Never commit a private production keystore.
- Telegram API credentials may come from `local.properties` or environment variables `TELEGRAM_APP_ID`, `TELEGRAM_APP_HASH`.
- Saved Text preference key: `saved_text_snippets_v1` in global emoji settings.
- Removed AI double-tap action ID `9` migrates to action ID `3`.
- No database schema change and no new runtime permission.

# 8. Removed or Deprecated Features

## Upload Speed Boost

- **Reason:** explicitly removed from Experimental settings/source.
- **Removed:** UI row, `uploadBoost` config, boosted 512 KiB minimum upload chunk, localized string.
- **Migration:** stale preference values are ignored because the key is no longer registered.

## Download Speed Boost

- **Reason:** explicitly removed from Experimental settings/source.
- **Removed:** UI row, `enhancedFileLoader` config, forced high-request download condition, localized string.
- **Migration:** stale preference values are ignored because the key is no longer registered.

## AI Translator

- **Reason:** explicitly removed from N-Settings Translator.
- **Removed:** provider selection, API URL/key/model/preset/system/user prompts, temperature/context/test UI, config keys, menu actions, automatic-context paths, double-tap action, AI Chat provider bridge, translator implementation, and dedicated strings.
- **Preserved:** Google/Yandex/Lingo/Microsoft/DeepL/Telegram/TranSmart translation, AI Chat service configuration, and Gemini transcription.
- **Migration:** stored double-tap ID `9` becomes normal Translate ID `3`; unknown obsolete AI preference keys are not loaded.

## AMOLED and Monet Bundled Themes

- **Reason:** not part of the default themes evidenced by the supplied settings backup; remove unused assets.
- **Removed:** registrations plus `amoled.attheme`, `monet_light.attheme`, `monet_dark.attheme`, `monet_amoled.attheme`.
- **Migration:** imported/custom themes remain; legacy removed built-in selections use existing fallback behavior.

# 9. Important Files / Modules Map

| Component | Purpose | Location |
|---|---|---|
| App build | Flavors, ABI, signing, native/Chaquopy setup, dependencies | `TMessagesProj/build.gradle` |
| Global build | SDK/JDK/NDK targets | `build.gradle` |
| App identity | Version and application ID | `gradle.properties` |
| CI ARMv7 verification | Builds and validates single-ABI APK | `.github/workflows/verify.yml` |
| Manifest | Android components, providers, contact MIME routes | `TMessagesProj/src/main/AndroidManifest.xml` |
| Nagram settings | Config definitions/migrations | `TMessagesProj/src/main/kotlin/xyz/nextalone/nagram/NaConfig.kt` |
| Neko settings | Shared config definitions | `TMessagesProj/src/main/java/tw/nekomimi/nekogram/NekoConfig.java` |
| Drawer registry | Available/visible/hidden/order model | `.../xyz/nextalone/nagram/helper/DrawerMenuHelper.kt` |
| Drawer rendering | Maps IDs to labels/icons | `.../org/telegram/ui/Adapters/DrawerLayoutAdapter.java` |
| Drawer settings UI | Reorder/show/hide and download-icon setting | `.../tw/nekomimi/nekogram/settings/SidebarMenuActivity.java` |
| Drawer routing | Opens drawer destinations | `LaunchActivity.java`, `MainMenuActions.kt` |
| Advanced Forward entry | Destination processing and topic context | `.../org/telegram/ui/ChatActivity.java` |
| Advanced Forward engine | New-copy media/text send behavior | `.../com/radolyn/ayugram/AyuForward.java` |
| Emoji panel | Emoji/GIF/Stickers/Text pager | `.../org/telegram/ui/Components/EmojiView.java` |
| Saved Text UI/storage | Add/edit/delete/list snippets | `.../org/telegram/ui/Components/SavedTextPanel.java` |
| Composer insertion | Inserts selected saved text | `.../org/telegram/ui/Components/ChatActivityEnterView.java` |
| Proxy alert | Add-only and Connect actions | `.../org/telegram/messenger/AndroidUtilities.java` |
| Media deletion | Eligibility and file cleanup | `.../tw/nekomimi/nekogram/helpers/MessageHelper.java` |
| Theme registry | Bundled/custom theme loading | `.../org/telegram/ui/ActionBar/Theme.java` |
| Translator settings | Non-AI translation providers/options | `.../tw/nekomimi/nekogram/settings/NekoTranslatorSettingsActivity.java` |
| English custom strings | New labels/resources | `TMessagesProj/src/main/res/values/strings_naxf.xml` |
| Persian custom strings | Persian labels/resources | `TMessagesProj/src/main/res/values-fa/strings_naxf.xml` |

# 10. Testing Report

## Build

- **Command:** `NATIVE_TARGET=armeabi-v7a ./gradlew TMessagesProj:assembleNormalStaging --stacktrace`
- **Result:** failed before Gradle startup; wrapper could not download `https://services.gradle.org/distributions/gradle-9.4.0-bin.zip` (`java.net.SocketException: Network is unreachable`).
- **Environment:** Linux x86_64 container; OpenJDK 17.0.20 available; required JDK 21 and Gradle 9.4 distribution unavailable.
- **Compilation result:** Unknown.
- **APK/ABI inspection:** not possible for current working tree.

## Passed source-level checks

- All Android resource XML files parse successfully.
- `google-services.json` parses successfully.
- No exact references to old application ID `fork.risin42.nagramx` remain.
- No references remain to deleted AI Translator classes/provider/resources.
- No references remain to removed speed-boost config items.
- Removed string resources have no remaining code references.
- Added string references exist in resources; English and Persian Saved Text/Proxy labels are present.
- No duplicate named resources were found within a values qualifier.
- Bundled theme assets are exactly Arctic, Blue, Dark Blue, Day, Night.
- ARMv7 default and `universalApk=false` are present.
- `git diff --check` passes.

## Not tested

- Java/Kotlin/native compilation and R8/resource shrinking.
- Fresh install and update behavior under the new application ID.
- Firebase Messaging/Crashlytics with the new package.
- Advanced Forward into regular topics, General topic, closed topics, monoforums, and multiple mixed destinations.
- Saved Text add/edit/delete/insert, RTL layout, rotation, process restart, and keyboard selection replacement.
- Proxy Add versus Connect network-state behavior.
- Downloaded-photo deletion under scoped storage and alternate cache locations.
- Drawer reorder/migration and dynamic proxy icon refresh.
- Theme migration from removed built-in themes.
- Real ARMv7 device startup and media operations.

# 11. Known Issues and Technical Debt

## Current tree has no successful build

- **Impact:** compile/runtime correctness is not release-verified.
- **Cause:** unavailable Gradle distribution/network and missing JDK 21 in the current environment.
- **Next action:** run the CI workflow or a local JDK 21/Android SDK 36 environment, fix compile failures, then repeat static checks.

## New application ID lacks verified backend configuration

- **Impact:** Firebase services may fail even if the project compiles.
- **Cause:** the existing `google-services.json` package field was migrated, but a newly registered Firebase Android app configuration was not supplied.
- **Next action:** register `fork.risin42.nagramxf` in the intended Firebase project and replace the config with the downloaded authoritative file.

## Application data cannot migrate automatically across package IDs

- **Impact:** Android installs the new package separately; old app data/accounts are not inherited.
- **Cause:** application sandbox identity changes with `applicationId`.
- **Next action:** decide whether side-by-side installation is intended; if not, design an explicit export/import migration outside private app sandbox assumptions.

## Advanced Forward topic metadata fallback

- **Impact:** if `TopicsController` lacks the selected topic, send falls back without topic context.
- **Possible cause:** topic metadata not loaded or deleted topic.
- **Next action:** test and, only if reproduced, add a targeted topic fetch/error path rather than broad forwarding refactoring.

## Saved Text storage is plain local preferences

- **Impact:** no cloud sync, encryption, reorder, or cross-device availability.
- **Cause:** intentionally minimal implementation using existing emoji preferences.
- **Next action:** add sync/encryption only under a separate explicit requirement and migration design.

## Removed built-in theme selection

- **Impact:** users who selected AMOLED/Monet lose exact built-in appearance and fall back.
- **Cause:** assets were removed to reduce source/package size.
- **Next action:** verify fallback UX; preserve imported/custom themes and avoid restoring removed assets unless product requirements change.

## Signing

- **Impact:** release/update continuity requires the same secure production key.
- **Cause:** CI uses a temporary key; permanent release signing is not documented in repository state.
- **Next action:** configure secure external secrets; never commit private keys.

# 12. Security, Performance, and Compatibility Notes

- Saved Text may contain sensitive content and is stored locally as plain JSON in preferences; avoid treating it as secure storage.
- Proxy credentials already use existing Telegram proxy persistence; Add-only must never enable or connect implicitly.
- Package migration changes Android sandbox, account type, contact MIME types, intents, Firebase identity, and update compatibility.
- Current `google-services.json` must not be considered backend-verified for the new package.
- Removing speed boosts restores standard chunk/request logic and avoids aggressive transfer behavior.
- Theme asset removal reduces packaged resources; R8/resource shrink result remains unmeasured.
- Minimum Android version is API 27; target/compile API is 36.
- Java/Kotlin target is 21; building with JDK 17 is unsupported.
- Only `armeabi-v7a` is allowed. Plugin/native changes must be tested against 32-bit ABI limits.
- Preserve Telegram server-media reuse and avoid downloading/re-uploading media in Advanced Forward unless unavoidable.
- Do not log proxy credentials, API keys, saved snippets, Telegram tokens, or signing secrets.

# 13. Version Control Summary

- **Branch:** `main`
- **Base commit:** `6f3721315e1eab886454faca368a31a6710673ee`
- **Base milestone:** completed earlier Advanced Forward flow/slim-settings cycle.
- **Current cycle:** uncommitted working-tree changes; no commit, tag, pull request, or release created.
- **Last verified CI baseline:** Actions run `34826865683`, ARMv7-only; current changes are not covered.
- **Required before commit:** successful JDK 21 ARMv7 build, review generated diff, device checklist, and authoritative Firebase decision.

# 14. Future Development Roadmap

| Priority | Task | Reason | Dependencies |
|---|---|---|---|
| P0 | Build `assembleNormalStaging` with JDK 21 | Establish compile correctness | Gradle 9.4, SDK 36, NDK 27.2, CMake, dependencies |
| P0 | Fix any compile/R8 failures without broad refactoring | Current build is unknown | Successful build environment |
| P0 | Obtain proper Firebase config for `fork.risin42.nagramxf` | Preserve push/crash services | Firebase project access |
| P0 | Test Advanced Forward in forum/monoforum destinations | Core bug fix is runtime-sensitive | ARMv7 Android device, topic chats |
| P1 | Test Saved Text UI and persistence in English/Persian/RTL | New pager/UI state | Built APK/device |
| P1 | Test Proxy Add-only and drawer state | Must not alter active network proxy | Built APK/device, test proxies |
| P1 | Test photo deletion on Android storage variants | Scoped-storage risk | Built APK/device, downloaded photos |
| P1 | Validate app-ID migration/install strategy | Package changes affect user data | Signed old/new APKs, migration decision |
| P2 | Measure APK/resource-size change | Confirm theme/removal goal | Successful staging APK |
| P2 | Consider optional Saved Text reorder/export | Usability enhancement, not current scope | Product decision |

# 15. AI Development Rules

1. Read this file and inspect the current diff before modifying code.
2. Treat uncommitted user changes as authoritative; never reset or overwrite unrelated work.
3. Explain root cause, affected classes/files, and minimal implementation plan before coding.
4. Modify only files required by the requested behavior; avoid unrelated formatting and refactoring.
5. Preserve Telegram UI conventions and existing NagramXF/AyuGram customizations.
6. Preserve `armeabi-v7a`-only output and API 27 compatibility; do not add ARM64/x86/universal builds.
7. Do not add dependencies unless the feature cannot reasonably use existing components.
8. Keep installed application ID and source namespace distinct; never bulk-rename `org.telegram.messenger` or internal `nagram` identifiers for an application-ID change.
9. Preserve Advanced Forward caption editing, multiple destinations, no attribution, album/media behavior, and server-media reuse.
10. When touching destination sending, test simple chats, regular forums, monoforums, comments, schedules, and multiple destinations.
11. Do not restore AI Translator or speed-boost code/resources. Do not remove independent AI Chat/transcription utilities merely because they use shared LLM helpers.
12. Keep custom/imported theme compatibility when changing bundled defaults.
13. Preserve existing config keys when moving UI; add narrow migrations when removing stored enum/action values.
14. Never commit API credentials, proxy credentials, user snippets, signing keys, or generated secrets.
15. Run `git diff --check`, resource/reference scans, XML/JSON parsing, and `NATIVE_TARGET=armeabi-v7a ./gradlew TMessagesProj:assembleNormalStaging` before declaring completion.
16. Do not claim build, deployment, device testing, or release success without direct evidence.
17. Update this document after each verified development cycle; record current state and decisions, not chat history.
