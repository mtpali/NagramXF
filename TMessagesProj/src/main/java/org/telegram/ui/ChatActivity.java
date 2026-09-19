Warning: truncated output (original token count: 720857)
... 1834851 bytes omitted ...

/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.AndroidUtilities.lerp;
import static org.telegram.messenger.LocaleController.formatPluralStringComma;
import static org.telegram.messenger.LocaleController.formatString;
import static org.telegram.messenger.LocaleController.getString;
import static org.telegram.ui.bots.AffiliateProgramFragment.percents;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.ui.recyclerview.ChatListItemAnimator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.LinearLayoutManager;
import org.telegram.ui.recyclerview.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.zxing.common.detector.MathUtils;

import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.hooks.MenuItemRecord;
import com.exteragram.messenger.plugins.ui.components.PluginsMenuWrapper;
import com.exteragram.messenger.plugins.utils.MenuContextBuilder;
import com.exteragram.messenger.feed.FeedChatIntegration;
import com.exteragram.messenger.feed.FeedChannelActions;
import com.exteragram.messenger.feed.FeedController;
import com.exteragram.messenger.feed.FeedMessageUtils;
import com.radolyn.ayugram.AyuConstants;
import com.radolyn.ayugram.AyuUtils;
import com.radolyn.ayugram.messages.AyuMessagesController;
import com.radolyn.ayugram.messages.AyuSavePreferences;
import com.radolyn.ayugram.proprietary.AyuHistoryHook;
import com.radolyn.ayugram.utils.AyuMessageUtils;
import com.radolyn.ayugram.ui.AyuMessageHistory;
import com.radolyn.ayugram.ui.AyuViewDeleted;
import com.radolyn.ayugram.ui.DummyView;
import com.radolyn.ayugram.utils.AyuGhostPreferences;
import com.radolyn.ayugram.utils.AyuGhostUtils;
import com.radolyn.ayugram.utils.AyuState;
import com.radolyn.ayugram.utils.LastSeenHelper;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotForumHelper;
import org.telegram.messenger.BotInlineKeyboard;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatMessageSharedResources;
import org.telegram.messenger.ChatMessagesMetadataController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FactCheckController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.HashtagSearchController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.SendMessageChatArguments;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagePreviewParams;
import org.telegram.messenger.MessageSuggestionParams;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Timer;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.utils.FBool;
import org.telegram.messenger.utils.OnPostDrawView;
import org.telegram.messenger.utils.PhotoUtilities;
import org.telegram.messenger.utils.RectFMergeBounding;
import org.telegram.messenger.utils.tlutils.AmountUtils;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.messenger.utils.tlutils.TLKeyboardHelper;
import org.telegram.messenger.utils.tlutils.TlUtils;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_keyboard;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.EdgeToEdgeSupportMode;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.MessageDrawable;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.theme.ThemeKey;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MessagesSearchAdapter;
import org.telegram.ui.Business.BusinessBotButton;
import org.telegram.ui.Business.BusinessLinksActivity;
import org.telegram.ui.Business.BusinessLinksController;
import org.telegram.ui.Business.BusinessLinksEmptyView;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Business.QuickRepliesEmptyView;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Cells.BotAskCell;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageUnsupportedCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.IMessageCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.Cells.ProfileChannelCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.UserInfoCell;
import org.telegram.ui.Components.*;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.boosts.BoostDialogs;
import org.telegram.ui.Components.Premium.boosts.GiftInfoBottomSheet;
import org.telegram.ui.Components.Premium.boosts.PremiumPreviewGiftLinkBottomSheet;
import org.telegram.ui.Components.Reactions.ChatSelectionReactionMenuOverlay;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSource;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceWrapped;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.Components.chat.ChatActivityBottomViewsVisibilityController;
import org.telegram.ui.Components.chat.ChatActivityDraftMessageMeasureController;
import org.telegram.ui.Components.chat.ChatActivityMessageMetricsView;
import org.telegram.ui.Components.chat.ChatActivitySearchContainer;
import org.telegram.ui.Components.chat.layouts.ChatActivityActionsButtonsLayout;
import org.telegram.ui.Components.chat.layouts.ChatActivityChannelButtonsLayout;
import org.telegram.ui.Components.chat.ChatInputViewsContainer;
import org.telegram.ui.Components.chat.ChatListViewPaddingsAnimator;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.chat.WallpaperBitmapProvider;
import org.telegram.ui.Components.blur3.BlurredBackgroundWithFadeDrawable;
import org.telegram.ui.Components.chat.layouts.ChatActivityFadeView;
import org.telegram.ui.Components.chat.layouts.ChatActivitySideControlsButtonsLayout;
import org.telegram.ui.Components.inset.WindowInsetsStateHolder;
import org.telegram.ui.Components.poll.FileState;
import org.telegram.ui.Components.poll.PollAddOptionFieldLayout;
import org.telegram.ui.Components.poll.PollAttachedMediaPack;
import org.telegram.ui.Components.poll.PollSendParams;
import org.telegram.ui.Components.poll.PollUtils;
import org.telegram.ui.Components.poll.sheets.PollStatisticsBottomSheet;
import org.telegram.ui.Components.quickforward.QuickShareSelectorOverlayLayout;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.MessageSuggestionOfferSheet;
import org.telegram.ui.Stars.StarReactionsOverlay;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.Stories.PublicStoriesList;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoriesUtilities;
import org.telegram.ui.Stories.PublicStoriesList;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.PreviewView;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TON.TONIntroActivity;
import org.telegram.ui.bots.BotAdView;
import org.telegram.ui.bots.BotCommandsMenuContainer;
import org.telegram.ui.bots.BotCommandsMenuView;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.WebViewRequestProps;
import org.telegram.ui.community.CommunitySheet;
import org.telegram.ui.iv.BlockRow;
import org.telegram.ui.iv.ChatAttachAlertRichLayout;
import org.telegram.ui.iv.RichEditor;
import org.telegram.ui.iv.RichEditorListView;
import org.telegram.ui.iv.RichHtml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import kotlin.Unit;

import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.core.BitwiseUtils;
import me.vkryl.core.reference.ReferenceList;

import tw.nekomimi.nekogram.RecentDialogsStore;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.helpers.MainTabsHelper;
import tw.nekomimi.nekogram.filters.AyuFilter;
import tw.nekomimi.nekogram.filters.RegexChatFiltersListActivity;
import tw.nekomimi.nekogram.filters.RegexFiltersSettingActivity;
import tw.nekomimi.nekogram.filters.RegexFilterEditActivity;
import tw.nekomimi.nekogram.helpers.ChatsHelper;
import com.radolyn.ayugram.AyuForward;
import tw.nekomimi.nekogram.helpers.MessageHelper;
import tw.nekomimi.nekogram.helpers.TranscribeHelper;
import tw.nekomimi.nekogram.helpers.remote.EmojiHelper;
import tw.nekomimi.nekogram.helpers.remote.PagePreviewRulesHelper;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.network.Client;
import com.exteragram.messenger.ai.ui.GenerateFromMessageBottomSheet;
import com.exteragram.messenger.ai.ui.ResponseAlert;
import tw.nekomimi.nekogram.menu.copy.CopyPopupWrapper;
import tw.nekomimi.nekogram.menu.ayugram.AyuGramMenuPopupWrapper;
import tw.nekomimi.nekogram.menu.forward.ForwardPopupWrapper;
import tw.nekomimi.nekogram.menu.reply.ReplyPopupWrapper;
import tw.nekomimi.nekogram.menu.translate.TranslatePopupWrapper;
import tw.nekomimi.nekogram.parts.DialogTransKt;
import tw.nekomimi.nekogram.parts.MessageTransKt;
import tw.nekomimi.nekogram.parts.PollTransUpdatesKt;
import tw.nekomimi.nekogram.parts.RichMessageTransHelper;
import tw.nekomimi.nekogram.helpers.SettingsBackupHelper;
import tw.nekomimi.nekogram.translate.Translator;
import tw.nekomimi.nekogram.translate.TranslatorKt;
import tw.nekomimi.nekogram.ui.BookmarksActivity;
import tw.nekomimi.nekogram.ui.BottomBuilder;
import tw.nekomimi.nekogram.ui.AdvancedForwardActivity;
import tw.nekomimi.nekogram.ui.MessageDetailsActivity;
import tw.nekomimi.nekogram.ui.components.GroupedIconsView;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.AndroidUtil;
import tw.nekomimi.nekogram.utils.ProxyUtil;
import com.radolyn.ayugram.controllers.AyuGhostController;
import xyz.nextalone.nagram.NaConfig;
import xyz.nextalone.nagram.ToggleResult;
import xyz.nextalone.nagram.helper.BookmarksHelper;
import xyz.nextalone.nagram.helper.DoubleTap;
import xyz.nextalone.nagram.helper.SystemAiServiceHelper;

@SuppressWarnings("unchecked")
public class ChatActivity extends BaseFragment implements
        NotificationCenter.NotificationCenterDelegate,
        DialogsActivity.DialogsActivityDelegate,
        LocationActivity.LocationActivityDelegate,
        ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate,
        ChatActivityInterface,
        FloatingDebugProvider,
        InstantCameraView.Delegate,
        FactorAnimator.Target
{

    private MessageMenuStatus lastMessageMenuStatus = new MessageMenuStatus(false, false, false, false, false, false, false, false, false);
    private static final int PLUGIN_MESSAGE_MENU_OPTION_BASE = 0x70000000;
    private static final int PLUGIN_CHAT_ACTION_MENU_OPTION_BASE = 0x71000000;
    private static final int PLUGIN_CHAT_ACTION_MENU_GAP_ID = 0x71fffffe;
    private final SparseArray<MenuItemRecord> pluginMessageMenuItemsByOption = new SparseArray<>();
    private final SparseArray<MenuItemRecord> pluginChatActionMenuItemsByOption = new SparseArray<>();
    private int nextPluginChatActionMenuOptionId = PLUGIN_CHAT_ACTION_MENU_OPTION_BASE;
    private final static boolean PULL_DOWN_BACK_FRAGMENT = false;
    private final static boolean DISABLE_PROGRESS_VIEW = true;
    private final static int SKELETON_DISAPPEAR_MS = 200;
    public static final int ACTION_BAR_BLUR_ALPHA = 178;

    private static int SKELETON_LIGHT_OVERLAY_ALPHA = 22;
    private static float SKELETON_SATURATION = 1.4f;

    public final static int DEBUG_SHARE_ALERT_MODE_NORMAL = 0,
            DEBUG_SHARE_ALERT_MODE_LESS = 1,
            DEBUG_SHARE_ALERT_MODE_MORE = 2;

    // buttons by Neko
    private final static int nkactionbarbtn_reply = 2000;
    private final static int nkactionbarbtn_selectBetween = 2001;
    private final static int nkactionbarbtn_action_mode_other = 2002;

    private final static int nkheaderbtn_show_pinned = 2003;
    private final static int nkheaderbtn_zibi = 2004;
    private final static int nkheaderbtn_linked_chat = 2005;
    private final static int nkheaderbtn_upgrade = 2007;

    private final static int nkheaderbtn_hide_title = 2029;

    // shared with actionbar
    private final static int nkbtn_translate = 2008;
    private final static int nkbtn_hide = 2009;
    private final static int nkbtn_savemessage = 2010;
    private final static int nkbtn_forward_noquote = 2011;
    private final static int nkbtn_sharemessage = 2030;

    // chat click menu buttons
    private final static int nkbtn_detail = 2012;
    private final static int nkbtn_deldlcache = 2013;
    private final static int nkbtn_view_history = 2014;
    private final static int nkbtn_repeat = 2015;
    private final static int nkbtn_stickerdl = 2016;
    private final static int nkbtn_unpin = 2017;
    private final static int nkbtn_view_in_chat = 2018;
    private final static int nkbtn_editAdmin = 2019;
    private final static int nkbtn_editPermission = 2020;
    private final static int nkbtn_copy_link_in_pm = 2025;
    private final static int nkbtn_repeatascopy = 2028;
    private final static int nkbtn_setReminder = 2029;
    private final static int nkbtn_reply_private = 2033;
    private final static int nkbtn_forward_nocaption = 2035;
    private final static int nkbtn_translateVoice = 2037;
    private final static int nkbtn_transcriptionRetry = 2038;
    private final static int nkbtn_bookmark = 2039;
    private final static int nkbtn_bookmarks_manager = 2040;
    private final static int nkbtn_report = 2041;
    private final static int nkbtn_ai_chat = 2042;
    private final static int nkbtn_clearDeleted = 2100;
    private final static int nkbtn_viewDeleted = 2101;

    public int shareAlertDebugMode = DEBUG_SHARE_ALERT_MODE_NORMAL;
    public boolean shareAlertDebugTopicsSlowMotion;

    public boolean justCreatedTopic = false;
    public boolean justCreatedChat = false;
    protected TLRPC.Chat currentChat;
    protected TLRPC.User currentUser;
    protected TLRPC.EncryptedChat currentEncryptedChat;
    private boolean userBlocked;

    private long chatInviterId;

    //private static final LongSparseArray<ArrayList<ChatMessageCell>> chatMessageCellsCache = new LongSparseArray<ArrayList<ChatMessageCell>>();

    private HashMap<MessageObject, Boolean> alreadyPlayedStickers = new HashMap<>();

    private final WindowInsetsStateHolder windowInsetsStateHolder = new WindowInsetsStateHolder(this::checkInsets);

    public BlurredBackgroundColorProviderThemed blurredBackgroundColorProvider;
    private BlurredBackgroundColorProviderThemed blurredBackgroundColorProviderWhite;

    private final ReferenceList<View> glassAttachedViews = new ReferenceList<>();
    private final ReferenceList<BlurredBackgroundDrawable> glassAttachedDrawables = new ReferenceList<>();
    private final @Nullable DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private final int recommendedAdditionalSizeY;

    private final @Nullable BlurredBackgroundSourceRenderNode glassBackgroundSourceRenderNode;
    private final @Nullable BlurredBackgroundSourceRenderNode glassBackgroundSourceFrostedRenderNode;
    public final @NonNull BlurredBackgroundDrawableViewFactory glassBackgroundDrawableFactory;
    private final @NonNull BlurredBackgroundDrawableViewFactory glassBackgroundDrawableFactoryFrosted;

    private final @NonNull BlurredBackgroundSourceWrapped navbarContentSourceWallpaper;
    private final @NonNull BlurredBackgroundDrawableViewFactory navbarContentDrawableFactory;

    private Dialog closeChatDialog;
    private boolean showCloseChatDialogLater;
    private FrameLayout progressView;
    private View progressView2;
    private FrameLayout bottomOverlay;
    private BlurredBackgroundWithFadeDrawable fadeDrawable;
    public ChatInputViewsContainer chatInputViewsContainer;
    private View roundVideoRecordBackground;

    private FrameLayout chatInputBubbleContainer;
    private FrameLayout chatInputInAppContainer;
    private WallpaperBitmapProvider wallpaperBitmapProvider = new WallpaperBitmapProvider();

    private ChatActivityFadeView chatActivityFadeView;
    protected ChatActivityEnterView chatActivityEnterView;
    private ChatActivityEnterTopView chatActivityEnterTopView;
    private boolean isActionBarTooNarrow;
    private ChatReplyContainer replyLayout;
    private int chatActivityEnterViewAnimateFromTop;
    private boolean chatActivityEnterViewAnimateBeforeSending;
    private ActionBarMenuItem.Item timeItem2;
    private ComposeDrawable otherIcon;
    private ActionBarMenu.LazyItem attachItem;
    private ActionBarMenuItem.Item pluginChatActionMenuGapItem;
    private ActionBarMenuItem.Item pluginChatActionMenuItem;
    private ActionBarMenuItem.Item savedChatsItem, savedChatsGap;
    private ActionBarMenuItem headerItem;
    private PluginsMenuWrapper pluginChatActionMenuWrapper;
    private ActionBarMenu.LazyItem editTextItem;
    protected ActionBarMenuItem searchItem;
    protected ActionBarMenuItem topicCreateItem;
    private ActionBarMenuItem.Item translateItem;
    private ActionBarMenuItem searchIconItem;
    private ActionBarMenuItem viewInChatItem;
    private ActionBarMenu.LazyItem audioCallIconItem;
    private boolean searchItemVisible;
    private RadialProgressView progressBar;
    private ActionBarMenuItem.Item addContactItem;
    private ActionBarMenuItem.Item clearHistoryItem;
    private ActionBarMenuItem.Item viewAsTopics;
    private ActionBarMenuItem.Item closeTopicItem;
    private ActionBarMenuItem.Item openForumItem;
    private ActionBarMenuItem.Item toTheBeginning;
    private ActionBarMenuItem.Item toTheMessage;
    private ActionBarMenuItem.Item hideTitleItem;
    private ActionBarMenuItem.Item bookmarksItem;
    private ClippingImageView animatingImageView;
    private ThanosEffect chatListThanosEffect;
    private ChatListViewPaddingsAnimator chatListViewPaddingsAnimator;
    public ChatListRecyclerView chatListView;
    private ChatListItemAnimator chatListItemAnimator;
    private GridLayoutManagerFixed chatLayoutManager;
    public ChatActivityAdapter chatAdapter;
    private UnreadCounterTextView bottomOverlayChatText;
    private boolean bottomOverlayLinks;
    private LinkSpanDrawable.LinksTextView bottomOverlayLinksText;
    private TextView bottomOverlayText;
    private TextView bottomOverlayStartButton;
    private RadialProgressView bottomOverlayProgress;
    private AnimatorSet bottomOverlayAnimation;
    private boolean bottomOverlayChatWaitsReply;
    private HintView2 bottomGiftHintView;
    private HintView2 guestBotHintView;
    private HintView2 bottomSuggestHintView;
    private ChatActivityTopPanelLayout topPanelLayout;

    private boolean ignoreItemAnimation;
    private ChatActivityChannelButtonsLayout bottomChannelButtonsLayout;
    private ChatActivityActionsButtonsLayout actionsButtonsLayout;
    @Nullable
    private FrameLayout emptyViewContainer;
    private LinearLayout emptyViewContent;
    private ChatGreetingsView greetingsViewContainer;
    private ChatActionCell greetingsInfo;
    private QuickRepliesEmptyView quickRepliesEmptyView;
    private BusinessLinksEmptyView businessLinksEmptyView;
    private ViewPositionWatcher viewPositionWatcher;
    public ChatActivityFragmentView contentView;
    private ChatBigEmptyView bigEmptyView;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    public ChatAvatarContainer avatarContainer;
    private AnimatedTextView selectedMessagesCountTextView;
    private RecyclerListView.OnItemClickListener mentionsOnItemClickListener;
    private SuggestEmojiView suggestEmojiPanel;
    private ActionBarMenuItem.Item muteItem;
    private ActionBarMenuItem.Item muteItemGap;
    private ActionBarMenuItem.Item muteItemTopGap;
    private ActionBarMenuItem.Item adminItemsGap;
    private ActionBarMenuItem.Item adminItemsRow;
    private ActionBarMenuItem.Item feeItemGap;
    private ActionBarMenuItem.Item feeItemText;
    private ChatNotificationsPopupWrapper chatNotificationsPopupWrapper;
    // private ChatActivitySideControlsButtonsLayout topButtonsLayout;
    private ChatActivitySideControlsButtonsLayout sideControlsButtonsLayout;
    private boolean pagedownButtonShowedByScroll;
    private int reactionsMentionCount;
    private int pollVotesMentionCount;
    public Bulletin messageSeenPrivacyBulletin;
    TextView webBotTitle;
    public SearchTagsList actionBarSearchTags;
    public ChatSearchTabs hashtagSearchTabs;

    private FrameLayout searchGoToBeginningButton;
    private ImageView searchGoToBeginningButtonArrow;

    private ViewPagerFixed searchViewPager;
    private int defaultSearchPage;
    private boolean requestClearSearchPages;
    private HashtagHistoryView hashtagHistoryView;
    private AlertDialog scheduleNowDialog;

    private HintView2 timeHint;
    private HintView2 savedMessagesHint;
	private HintView2 savedMessagesSearchHint;
    private HintView2 savedMessagesTagHint;
    private HintView2 groupEmojiPackHint;
    private HintView2 botMessageHint;
    private HintView2 factCheckHint;
    private HintView2 videoConversionTimeHint;
    private float videoConversionTimeHintY;

    private TL_stories.TL_premium_boostsStatus boostsStatus;
    private ChannelBoostsController.CanApplyBoost canApplyBoosts;

    private boolean showTapForForwardingOptionsHit;
    private Runnable tapForForwardingOptionsHitRunnable;
    private ImageView replyCloseImageView;
    private MentionsContainerView mentionContainer;
    private AnimatorSet mentionListAnimation;
    public ChatAttachAlert chatAttachAlert;
    @Nullable
    private FrameLayout topChatPanelView;
    @Nullable
    private TextView addToContactsButton;
    private boolean addToContactsButtonArchive;
    @Nullable
    private TextView reportSpamButton;
    @Nullable
    private TextView restartTopicButton;
    @Nullable
    private ImageView closeRestartTopicButton;
    @Nullable
    private TranslateButton translateButton;
    private TextView addProfilePictureButton;
    public TopicsTabsView topicsTabs;
    @Nullable
    private BusinessBotButton bizBotButton;
    @Nullable
    private LinkSpanDrawable.LinksTextView emojiStatusSpamHint;
    @Nullable
    private ImageView closeReportSpam;
    private BotAdView botAdView;
    private TextView chatWithAdminTextView;
    private FragmentContextView fragmentContextView;
    private FrameLayout fragmentContextViewWrapper;
    private FragmentContextView fragmentLocationContextView;
    private FrameLayout fragmentLocationContextViewWrapper;
    private TextView emptyView;
    private FlickerLoadingView hashtagLoadingView;
    private StickerEmptyView hashtagSearchEmptyView;
    private HintView gifHintTextView;
    private HintView emojiHintTextView;
    private HintView mediaBanTooltip;
    private HintView scheduledOrNoSoundHint;
    private boolean scheduledOrNoSoundHintShown;
    private HintView scheduledHint;
    private boolean scheduledHintShown;
    private boolean searchAsListHintShown;
    private HintView fwdRestrictedTopHint;
    private HintView fwdRestrictedBottomHint;
    private HintView slowModeHint;
    private HintView pollHintView;
    private HintView timerHintView;
    private ChatMessageCell pollHintCell;
    private int pollHintX;
    private int pollHintY;
    private HintView voiceHintTextView;
    private HintView noSoundHintView;
    private HintView forwardHintView;
    private ChecksHintView checksHintView;
    private View emojiButtonRed;
    private FrameLayout pinnedMessageView;
    private BluredView blurredView;
    private PinnedLineView pinnedLineView;
    private boolean setPinnedTextTranslationX;
    private BackupImageView[] pinnedMessageImageView = new BackupImageView[2];
    private TrackingWidthSimpleTextView[] pinnedNameTextView = new TrackingWidthSimpleTextView[2];
    private SimpleTextView[] pinnedMessageTextView = new SimpleTextView[2];
    private PinnedMessageButton[] pinnedMessageButton = new PinnedMessageButton[2];
    private NumberTextView pinnedCounterTextView;
    private int pinnedCounterTextViewX;
    private AnimatorSet[] pinnedNextAnimation = new AnimatorSet[2];
    private boolean pinnedMessageButtonShown = false;
    private ImageView closePinned;
    private RadialProgressView pinnedProgress;
    private ImageView pinnedListButton;
    private AnimatorSet pinnedListAnimator;
    @Nullable
    private FrameLayout alertView;
    private Runnable hideAlertViewRunnable;
    private TextView alertNameTextView;
    private TextView alertTextView;
    private final int searchContainerHeight = 44;
    private FrameLayout searchContainer;
    private ImageView searchCalendarButton;
    private ImageView searchUserButton;
    private AnimatedTextView searchCountText;
    private AnimatedTextView searchExpandList;
    private AnimatedTextView searchOtherButton;
    private ChatActionCell floatingDateView;
    private TopicSeparator.Cell floatingTopicSeparator;
    private float intoTopViewTop;
    private ChatActionCell infoTopView;
    private int hideDateDelay = 500;
    public InstantCameraView instantCameraView;
    private View overlayView;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopicOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private AnimatorSet floatingDateAnimation;
    private ValueAnimator floatingTopicAnimation;
    private float floatingTopicViewAlpha;
    private boolean scrollingFloatingDate;
    private boolean scrollingFloatingTopic;
    private boolean scrollingChatListView;
    private boolean checkTextureViewPosition;
    private boolean searchingForUser;
    private TLRPC.User searchingUserMessages;
    private TLRPC.Chat searchingChatMessages;
    public static boolean scrolling;
    public ReactionsLayoutInBubble.VisibleReaction searchingReaction;
    public ReactionsLayoutInBubble.VisibleReaction getFilterTag() {
        return chatAdapter != null && chatAdapter.isFiltered ? searchingReaction : null;
    }
    public String getFilterQuery() {
        return chatAdapter != null && chatAdapter.isFiltered ? searchingQuery : null;
    }
    public boolean isFiltered() {
        return chatAdapter != null && chatAdapter.isFiltered;
    }
    public ArrayList<MessageObject> getFilteredMessages() {
        return chatAdapter != null ? chatAdapter.filteredMessages : null;
    }
    private boolean searchingFiltered;
    private boolean searching;
    private String searchingQuery;

    private TLRPC.MessagesFilter searchingType;

    private String searchingHashtag;
    private int hashtagSearchSelectedIndex;
    private int searchLastCount;
    private int searchLastIndex;
    private UndoView undoView;
    private UndoView topUndoView;
    private Bulletin pinBulletin;
    private boolean showPinBulletin;
    private int pinBullerinTag;
    protected boolean openKeyboardOnAttachMenuClose;
    private FlagSecureReason flagSecure;
    public boolean isFullyVisible;

    private MessageObject hintMessageObject;
    private int hintMessageType;
    private MessageObject hint2MessageObject;
    private MessageObject hint3MessageObject;

    private ChatActivitySearchContainer messagesSearchListContainer;
    public RecyclerListView messagesSearchListView;
    private MessagesSearchAdapter messagesSearchAdapter;
    private ChatActivityMessageMetricsView messageMetricsView;

    public static final int MODE_DEFAULT = 0;
    public static final int MODE_SCHEDULED = 1;
    public static final int MODE_PINNED = 2;
    public static final int MODE_SAVED = 3;
    public static final int MODE_QUICK_REPLIES = 5;
    public static final int MODE_EDIT_BUSINESS_LINK = 6;
    public static final int MODE_SEARCH = 7;
    public static final int MODE_SUGGESTIONS = 8;
    public static final int MODE_WELCOME_MESSAGES = 9;

    public static final int SEARCH_THIS_CHAT = 0;
    public static final int SEARCH_MY_MESSAGES = 1;
    public static final int SEARCH_PUBLIC_POSTS = 2;
    public static final int SEARCH_CHANNEL_POSTS = 3;
    private int searchType;

    public TL_account.TL_businessChatLink businessLink = null;

    public String quickReplyShortcut;
    private int chatMode;
    private int scheduledMessagesCount = -1;
    public boolean isSubscriberSuggestions;

    private String reportTitle;
    private byte[] reportOption;
    private String reportMessage;
    public boolean isReport() {
        return !TextUtils.isEmpty(reportTitle);
    }

    @Nullable
    private MessageObject threadMessageObject;
    private MessageObject topicStarterMessageObject;
    private boolean threadMessageVisible = true;
    private ArrayList<MessageObject> threadMessageObjects;
    private MessageObject replyMessageHeaderObject;
    private TLRPC.TL_forumTopic forumTopic;
    private long threadMessageId;
    private int replyOriginalMessageId;
    public TLRPC.Chat replyOriginalChat;
    public boolean isComments;
    public boolean isTopic;
    private boolean threadMessageAdded;
    private boolean scrollToThreadMessage;
    private int threadMaxInboxReadId;
    private int threadMaxOutboxReadId;
    private int replyMaxReadId;
    private Runnable delayedReadRunnable;
    private final SparseArray<MessageObject> pendingSendMessagesDict = new SparseArray<>();
    private final ArrayList<MessageObject> pendingSendMessages = new ArrayList<>();
    private int threadUnreadMessagesCount;
    private boolean convertingToast, convertingToastShown;
    private int convertingToastMessageId;

    public ArrayList<MessageObject> animatingMessageObjects = new ArrayList<>();
    private final HashMap<TLRPC.Document, Integer> animatingDocuments = new HashMap<>();
    private MessageObject needAnimateToMessage;

    private int scrollToPositionOnRecreate = -1;
    private int scrollToOffsetOnRecreate = 0;

    private final ArrayList<MessageObject> pollsToCheck = new ArrayList<>(10);

    private int editTextStart;
    private int editTextEnd;

    private Runnable checkPaddingsRunnable;

    private boolean wasManualScroll;
    private boolean fixPaddingsInLayout;
    private boolean globalIgnoreLayout;

    private int topViewWasVisible;

    private ArrayList<Integer> pinnedMessageIds = new ArrayList<>();
    private int maxPinnedMessageId;
    private HashMap<Integer, MessageObject> pinnedMessageObjects = new HashMap<>();
    private SparseArray<Boolean> loadingPinnedMessages = new SparseArray<>();
    private int currentPinnedMessageId;
    private int[] currentPinnedMessageIndex = new int[1];
    private int forceNextPinnedMessageId;
    private boolean forceScrollToFirst;
    private int loadedPinnedMessagesCount;
    private int totalPinnedMessagesCount;
    public boolean loadingPinnedMessagesList;
    private boolean pinnedEndReached;

    public void reloadPinnedMessages() {
        pinnedMessageIds.clear();
        pinnedMessageObjects.clear();
        currentPinnedMessageId = 0;
        loadedPinnedMessagesCount = 0;
        totalPinnedMessagesCount = 0;
        updatePinnedMessageView(true);
        getMediaDataController().loadPinnedMessages(getDialogId(), 0, chatInfo == null ? 0 : chatInfo.pinned_msg_id);
        loadingPinnedMessagesList = true;
        updatePinnedTopicStarterMessage();
    }

    private AnimatorSet forwardButtonAnimation;

    SparseIntArray dateObjectsStableIds = new SparseIntArray();
    SparseIntArray conversionObjectsStableIds = new SparseIntArray();
    public static int lastStableId = 10;

    private boolean openSearchKeyboard;

    private boolean waitingForReplyMessageLoad;

    private boolean ignoreAttachOnPause;

    private boolean allowStickersPanel = true;
    private boolean allowContextBotPanel;
    private boolean allowContextBotPanelSecond = true;
    private AnimatorSet runningAnimation;
    private int runningAnimationIndex = -1;

    private MessageObject selectedObjectToEditCaption;
    private MessageObject selectedObject;
    public MessageObject.GroupedMessages selectedObjectGroup;
    private boolean forbidForwardingWithDismiss;
    public MessagePreviewParams messagePreviewParams;
    public MessageSuggestionParams messageSuggestionParams;
    private CharSequence formwardingNameText;
    public MessageObject forwardingMessage;
    public MessageObject.GroupedMessages forwardingMessageGroup;
    private AyuForward ayuForwardHandler;
    private HashMap<String, String> advancedForwardTexts;
    private MessageObject.GroupedMessages replyingQuoteGroup;
    public MessageObject replyingTopMessage;
    private ReplyQuote replyingQuote;
    private boolean ignoreDraft;
    private MessageObject replyingMessageObject;
    private int editingMessageObjectReqId;
    public MessageObject editingMessageObject;
    private boolean paused = true;
    private boolean pausedOnLastMessage;
    private boolean wasPaused;
    boolean firstOpen = true;
    private int replyImageSize;
    private int replyImageCacheType;
    private TLRPC.PhotoSize replyImageLocation;
    private TLRPC.PhotoSize replyImageThumbLocation;
    private TLObject replyImageLocationObject;
    private int pinnedImageSize;
    private int pinnedImageCacheType;
    private boolean pinnedImageHasBlur;
    private TLRPC.PhotoSize pinnedImageLocation;
    private TLRPC.PhotoSize pinnedImageThumbLocation;
    private TLObject pinnedImageLocationObject;
    private int linkSearchRequestId;
    public TLRPC.WebPage foundWebPage;
    private ArrayList<CharSequence> foundUrls;
    private String pendingLinkSearchString;
    private Runnable pendingWebPageTimeoutRunnable;
    private Runnable waitingForCharaterEnterRunnable;
    private Runnable onChatMessagesLoaded;

    private TLRPC.ChatInvite chatInvite;
    private Runnable chatInviteRunnable;

    private LongSparseIntArray clearingHistoryArr = new LongSparseIntArray();
    boolean isClearingHistory() {
        return clearingHistoryArr.get(getThreadId(), 0) != 0;
    }

    void setClearingHistory(long threadId, boolean isClearingHistory) {
        clearingHistoryArr.put(threadId, isClearingHistory ? 1 : 0);
    }

    public boolean openAnimationEnded;
    public boolean fragmentOpened;
    private long openAnimationStartTime;

    private boolean scrollToTopOnResume;
    private boolean forceScrollToTop;
    private boolean scrollToTopUnReadOnResume;
    private long dialog_id;
    private Long dialog_id_Long;
    private int lastLoadIndex = 1;
    public SparseArray<MessageObject>[] selectedMessagesIds = new SparseArray[]{new SparseArray<>(), new SparseArray<>()};
    public SparseArray<MessageObject>[] selectedMessagesCanCopyIds = new SparseArray[]{new SparseArray<>(), new SparseArray<>()};
    public SparseArray<MessageObject>[] selectedMessagesCanStarIds = new SparseArray[]{new SparseArray<>(), new SparseArray<>()};
    private boolean hasUnfavedSelected;
    private int cantDeleteMessagesCount;
    private int cantForwardMessagesCount;
    private int canForwardMessagesCount;
    private int canEditMessagesCount;
    private int cantSaveMessagesCount;
    private int canSaveMusicCount;
    private int canSaveDocumentsCount;
    private ArrayList<Integer> waitingForLoad = new ArrayList<>();
    private boolean needRemovePreviousSameChatActivity = true;
    private boolean hasMainTabs;

    private FeedChatIntegration feedIntegration;
    private int feedLoadRetryCount;
    private final Runnable loadNextNewerFeedPage = new Runnable() {
        @Override
        public void run() {
            if (isFinished) {
                return;
            }
            loadNewerFeed(true);
        }
    };
    private final Runnable retryFailedFeedLoad = new Runnable() {
        @Override
        public void run() {
            if (isFinished || paused || !isFeedSearch()) {
                return;
            }
            FeedController feedController = FeedController.getInstance(currentAccount);
            if (feedController.isLoading()) {
                return;
            }
            waitingForLoad.clear();
            if (feedController.getMessages().isEmpty()) {
                reloadFeed();
            } else {
                loadNewerFeed(true);
                checkScrollForLoad(false);
            }
        }
    };

    private int newUnreadMessageCount;
    private int prevSetUnreadCount = Integer.MIN_VALUE;
    private int newMentionsCount;
    private boolean hasAllMentionsLocal;

    private ArrayList<ChatMessageCell> animateSendingViews = new ArrayList<>();

    public SparseArray<MessageObject>[] messagesDict = new SparseArray[]{new SparseArray<>(), new SparseArray<>()};
    private SparseArray<MessageObject> repliesMessagesDict = new SparseArray<>();
    private SparseArray<ArrayList<Integer>> replyMessageOwners = new SparseArray<>();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>();
    private SparseArray<ArrayList<MessageObject>> messagesByDaysSorted = new SparseArray<>();
    private LongSparseArray<MessageObject> conversionMessages = new LongSparseArray<>();
    public ArrayList<MessageObject> messages = new ArrayList<>();
    private SparseArray<MessageObject> waitingForReplies = new SparseArray<>();
    private LongSparseArray<ArrayList<MessageObject>> polls = new LongSparseArray<>();
    private LongSparseArray<MessageObject.GroupedMessages> groupedMessagesMap = new LongSparseArray<>();
    private int[] maxMessageId = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
    private int[] minMessageId = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private int[] maxDate = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private int[] minDate = new int[2];
    private boolean[] endReached = new boolean[2];
    private boolean[] cacheEndReached = new boolean[2];
    private boolean[] forwardEndReached = new boolean[]{true, true};
    private boolean hideForwardEndReached;
    private boolean loading = true;
    private boolean firstLoading = true;
    private boolean chatWasReset;
    private boolean firstUnreadSent;
    private int loadsCount;
    private int last_message_id = 0;
    private long mergeDialogId;
    private boolean sentBotStart;

    private long startMessageAppearTransitionMs;
    private List<MessageSkeleton> messageSkeletons = new ArrayList<>();
    private int lastSkeletonCount;
    private int lastSkeletonMessageCount;
    private Paint skeletonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint skeletonServicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ColorMatrix skeletonColorMatrix = new ColorMatrix();
    private MessageDrawable.PathDrawParams skeletonBackgroundCacheParams = new MessageDrawable.PathDrawParams();
    private MessageDrawable skeletonBackgroundDrawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, false, false, new Theme.ResourcesProvider() {
        @Override
        public int getColor(int key) {
            return getThemedColor(key);
        }
    });
    private long skeletonLastUpdateTime;
    private int skeletonGradientWidth;
    private int skeletonTotalTranslation;
    private Matrix skeletonMatrix = new Matrix();
    private LinearGradient skeletonGradient;
    private int skeletonColor0;
    private int skeletonColor1;

    private Paint skeletonOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix skeletonOutlineMatrix = new Matrix();
    private LinearGradient skeletonOutlineGradient;
    public boolean forceDisallowApplyWallpeper;
    public boolean forceDisallowRedrawThemeDescriptions;
    private boolean waitingForGetDifference;
    private int initialMessagesSize;
    private boolean loadInfo;
    private boolean historyPreloaded;
    private int migrated_to;
    private boolean firstMessagesLoaded;
    private boolean clearOnLoad;
    private boolean clearOnLoadButIsNewTopic;
    private int clearOnLoadAndScrollMessageId = -1, clearOnLoadAndScrollOffset;
    private boolean topicChangedFromMessage;
    private Runnable closeInstantCameraAnimation;

    {
        skeletonOutlinePaint.setStyle(Paint.Style.STROKE);
        skeletonOutlinePaint.setStrokeWidth(AndroidUtilities.dp(1));
    }

    private String textToSet;
    private boolean premiumInvoiceBot;
    private boolean showScrollToMessageError;
    private int startLoadFromMessageId;
    private int startReplyTo;
    private int startLoadFromDate;
    private int startLoadFromMessageIdSaved;
    private int startLoadFromMessageOffset = Integer.MAX_VALUE;
    private int startFromVideoTimestamp = -1;
    private int startFromVideoMessageId;
    private boolean needSelectFromMessageId;
    private int returnToMessageId;
    private final Stack<Integer> returnToMessageIdsStack = new Stack<>();
    private int returnToLoadIndex;
    private int createUnreadMessageAfterId;
    private boolean createUnreadMessageAfterIdLoading;
    private boolean loadingFromOldPosition;

    private boolean first = true;
    private int first_unread_id;
    private boolean loadingForward;
    private MessageObject unreadMessageObject;
    private MessageObject scrollToMessage;
    public int highlightMessageId = Integer.MAX_VALUE;
    public boolean showNoQuoteAlert;
    public boolean highlightMessageQuoteFirst;
    private long highlightMessageQuoteFirstTime;
    public String highlightMessageQuote;
    public Integer highlightTaskId;
    public byte[] highlightPollOptionId;
    public int highlightMessageQuoteOffset = -1;
    private int scrollToMessagePosition = -10000;
    private Runnable unselectRunnable;

    private String currentPicturePath;

    private ChatObject.Call groupCall;
    private boolean lastCallCheckFromServer;
    private boolean createGroupCall;
    protected TLRPC.ChatFull chatInfo;
    protected TLRPC.UserFull userInfo;

    public ProfileChannelCell.ChannelMessageFetcher profileChannelMessageFetcher;
    public ProfileBirthdayEffect.BirthdayEffectFetcher birthdayAssetsFetcher;

    public final LongSparseArray<TL_bots.BotInfo> botInfo = new LongSparseArray<>();
    private String botUser;
    private long inlineReturn;
    private String voiceChatHash;
    private boolean openVideoChat;
    private boolean livestream;
    private String attachMenuBotToOpen;
    private String attachMenuBotStartCommand;
    private MessageObject botButtons;
    private MessageObject botReplyButtons;
    private int botsCount;
    private boolean hasBotsCommands;
    private boolean hasQuickReplies;
    private boolean hasBotWebView;
    private long chatEnterTime;
    private long chatLeaveTime;

    private boolean locationAlertShown;

    private String startVideoEdit;

    private FrameLayout videoPlayerContainer;
    private ChatMessageCell drawLaterRoundProgressCell;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private TextureView videoTextureView;
    private boolean scrollToVideo;
    private Path aspectPath;
    private Paint aspectPaint;
    private Runnable destroyTextureViewRunnable = () -> {
        destroyTextureView();
    };

    public static boolean noForwardQuote;
    public static boolean noForwardCaption;
    private TLRPC.ChatParticipant selectedParticipant;
    private final BlurredBackgroundSourceBitmap scrimBlur3SourceBitmap = new BlurredBackgroundSourceBitmap();
    private final BlurredBackgroundDrawableViewFactory scrimBlur3Factory = new BlurredBackgroundDrawableViewFactory(scrimBlur3SourceBitmap);
    private Bitmap scrimBlurBitmap;
    private BitmapShader scrimBlurBitmapShader;
    private Paint scrimBlurBitmapPaint;
    private Matrix scrimBlurMatrix;

    private Paint scrimPaint;
    private Paint actionBarBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float scrimPaintAlpha = 0f;
    private boolean scrimProgressDirection;
    private View scrimView;
    private float scrimViewAlpha = 1f;
    private float scrimViewProgress = 0f;
    private Integer scrimViewReaction;
    private Integer scrimViewTask;
    private int scrimViewReactionOffset;
    private boolean scrimViewReactionAnimated;
    private int popupAnimationIndex = -1;
    private AnimatorSet scrimAnimatorSet;
    public ActionBarPopupWindow scrimPopupWindow;
    private boolean scrimPopupWindowHideDimOnDismiss = true;
    private int scrimPopupX, scrimPopupY;
    public ActionBarMenuSubItem[] scrimPopupWindowItems;
    private ActionBarMenuSubItem menuDeleteItem;
    private final Runnable updateDeleteItemRunnable = new Runnable() {
        @Override
        public void run() {
            if (selectedObject == null || menuDeleteItem == null) {
                return;
            }
            int remaining = Math.max(0, selectedObject.messageOwner.ttl_period - (getConnectionsManager().getCurrentTime() - selectedObject.messageOwner.date));
            String remainingStr;
            if (remaining < 24 * 60 * 60) {
                remainingStr = AndroidUtilities.formatDuration(remaining, false, true);
            } else {
                remainingStr = LocaleController.formatPluralString("Days", Math.round(remaining / (24 * 60 * 60.0f)));
            }
            menuDeleteItem.setSubtext(LocaleController.formatString(R.string.AutoDeleteIn, remainingStr));
            AndroidUtilities.runOnUIThread(updateDeleteItemRunnable, 1000);
        }
    };

    private ChatActivityDelegate chatActivityDelegate;
    private RecyclerAnimationScrollHelper chatScrollHelper;

    private int postponedScrollMinMessageId;
    private int postponedScrollToLastMessageQueryIndex;
    private int postponedScrollMessageId;
    private boolean fakePostponedScroll;
    private boolean postponedScrollIsCanceled;
    private ChatActivityTextSelectionHelper textSelectionHelper;
    private View slidingView;

    private final float[] tmpOverlayPos = new float[2];
    private float[] computeArticleOverlayPos(View overlay) {
        tmpOverlayPos[0] = 0;
        tmpOverlayPos[1] = 0;
        View v = overlay;
        while (v != null && v != contentView) {
            tmpOverlayPos[0] += v.getX();
            tmpOverlayPos[1] += v.getY();
            v = v.getParent() instanceof View ? (View) v.getParent() : null;
        }
        return tmpOverlayPos;
    }

    public boolean hasTextSelection() {
        return textSelectionHelper != null && textSelectionHelper.isInSelectionMode();
    }

    public boolean isTryingTextSelection() {
        return textSelectionHelper != null && textSelectionHelper.isTryingSelect();
    }

    public void clearTextSelection() {
        if (textSelectionHelper != null && textSelectionHelper.isInSelectionMode()) textSelectionHelper.clear();
    }

    private MessageObject getSlidingMessageObject() {
        if (slidingView instanceof ChatMessageCell) return ((ChatMessageCell) slidingView).getMessageObject();
        return null;
    }

    private float getSlidingNonAnimationTranslationX(boolean update) {
        if (slidingView instanceof ChatMessageCell) return ((ChatMessageCell) slidingView).getNonAnimationTranslationX(update);
        return 0;
    }

    private void slidingViewSetOffset(float offset) {
        if (slidingView instanceof ChatMessageCell) ((ChatMessageCell) slidingView).setSlidingOffset(offset);
    }

    private float slidingViewGetOffsetX() {
        if (slidingView instanceof ChatMessageCell) return ((ChatMessageCell) slidingView).getSlidingOffsetX();
        return 0;
    }
    private boolean maybeStartTrackingSlidingView;
    private boolean startedTrackingSlidingView;

    private boolean canShowPagedownButton;
    private TextSelectionHint textSelectionHint;
    private boolean textSelectionHintWasShowed;
    private float lastTouchY;
    ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate;

    private ChatMessageCell dummyMessageCell;
    protected FireworksOverlay fireworksOverlay;

    private boolean swipeBackEnabled = true;

    public static Pattern publicMsgUrlPattern;
    public static Pattern voiceChatUrlPattern;
    public static Pattern privateMsgUrlPattern;
    public boolean waitingForSendingMessageLoad;
    private ValueAnimator changeBoundAnimator;
    private Animator messageEditTextAnimator;

    private boolean openImport;

    public float chatListViewPaddingTop;
    public float paddingTopHeight;
    public int chatListViewPaddingVisibleOffset;

    private int contentPaddingTop;
    private float contentPanTranslation;
    private float contentPanTranslationT;
    private float floatingDateViewOffset;
    private float floatingTopicViewOffset;
    private float topViewOffset;
    private TLRPC.Document preloadedGreetingsSticker;
    private boolean forceHistoryEmpty;
    private boolean invalidateChatListViewTopPadding;
    private long activityResumeTime;

    private int transitionAnimationIndex;
    private int transitionAnimationGlobalIndex;
    private int scrollAnimationIndex;
    private int scrollCallbackAnimationIndex;

    public boolean allowExpandPreviewByClick;
    private boolean showSearchAsIcon;
    private boolean showAudioCallAsIcon;
    public MessageEnterTransitionContainer messageEnterTransitionContainer;
    private float pullingDownOffset, pullingBottomOffset;
    private ChatPullingDownDrawable pullingDownDrawable;
    private Animator pullingDownBackAnimator;
    private boolean fromPullingDownTransition;
    private boolean toPullingDownTransition;
    private ChatActivity pullingDownAnimateToActivity;
    private float pullingDownAnimateProgress;
    private AnimatorSet fragmentTransition;
    private ChatActivity backToPreviousFragment;
    private Runnable fragmentTransitionRunnable = new Runnable() {
        @Override
        public void run() {
            if (fragmentTransition != null && !fragmentTransition.isRunning()) {
                fragmentTransition.start();
            }
        }
    };

    private QuickShareSelectorOverlayLayout quickShareSelectorOverlay;
    private ChatSelectionReactionMenuOverlay selectionReactionsOverlay;
    private SecretVoicePlayer secretVoicePlayer;

    private boolean isPauseOnThemePreview;
    private ChatThemeBottomSheet chatThemeBottomSheet;
    private ThemeDelegate parentThemeDelegate;
    private ChatActivity parentChatActivity;
    public ThemeDelegate themeDelegate;
    private ChatActivityMemberRequestsDelegate pendingRequestsDelegate;
    private final ChatMessagesMetadataController chatMessagesMetadataController = new ChatMessagesMetadataController(this);
    private TLRPC.TL_channels_sendAsPeers sendAsPeersObj;

    private TL_account.resolvedBusinessChatLinks resolvedChatLink;

    private boolean switchFromTopics;
    private boolean switchingFromTopics;
    private float switchingFromTopicsProgress;

    public final static int OPTION_RETRY = 0;
    public final static int OPTION_DELETE = 1;
    public final static int OPTION_FORWARD = 2;
    public final static int OPTION_COPY = 3;
    public final static int OPTION_SAVE_TO_GALLERY = 4;
    public final static int OPTION_APPLY_LOCALIZATION_OR_THEME = 5;
    public final static int OPTION_SHARE = 6;
    public final static int OPTION_SAVE_TO_GALLERY2 = 7;
    public final static int OPTION_REPLY = 8;
    public final static int OPTION_ADD_TO_STICKERS_OR_MASKS = 9;
    public final static int OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC = 10;
    public final static int OPTION_ADD_TO_GIFS = 11;
    public final static int OPTION_EDIT = 12;
    public final static int OPTION_PIN = 13;
    public final static int OPTION_UNPIN = 14;
    public final static int OPTION_ADD_CONTACT = 15;
    public final static int OPTION_COPY_PHONE_NUMBER = 16;
    public final static int OPTION_CALL = 17;
    public final static int OPTION_CALL_AGAIN = 18;
    public final static int OPTION_RATE_CALL = 19;
    public final static int OPTION_ADD_STICKER_TO_FAVORITES = 20;
    public final static int OPTION_DELETE_STICKER_FROM_FAVORITES = 21;
    public final static int OPTION_COPY_LINK = 22;
    public final static int OPTION_REPORT_CHAT = 23;
    public final static int OPTION_CANCEL_SENDING = 24;
    public final static int OPTION_UNVOTE = 25;
    public final static int OPTION_STOP_POLL_OR_QUIZ = 26;
    public final static int OPTION_VIEW_REPLIES_OR_THREAD = 27;
    public final static int OPTION_STATISTICS = 28;
    public final static int OPTION_TRANSLATE = 29;
    public final static int OPTION_TRANSCRIBE = 30;
    public final static int OPTION_HIDE_SPONSORED_MESSAGE = 31;
    public final static int OPTION_VIEW_IN_TOPIC = 32;
    public final static int OPTION_ABOUT_REVENUE_SHARING_ADS = 33;
    public final static int OPTION_REPORT_AD = 34;
    public final static int OPTION_REMOVE_ADS = 35;
    public final static int OPTION_SEND_NOW = 100;
    public final static int OPTION_EDIT_SCHEDULE_TIME = 102;
    public final static int OPTION_SPEED_PROMO = 103;
    public final static int OPTION_OPEN_PROFILE = 104;
    public final static int OPTION_FACT_CHECK = 106;
    public final static int OPTION_EDIT_PRICE = 107;
    public final static int OPTION_GIFT = 108;
    public final static int OPTION_EDIT_TODO = 109;
    public final static int OPTION_ADD_TO_TODO = 110;

    public final static int OPTION_SUGGESTION_EDIT_PRICE = 111;
    public final static int OPTION_SUGGESTION_EDIT_TIME = 112;
    public final static int OPTION_SUGGESTION_EDIT_MESSAGE = 113;
    public final static int OPTION_SUGGESTION_ADD_OFFER = 114;

    private final static int OPTION_COPY_PHOTO = 150;
    private final static int OPTION_COPY_PHOTO_AS_STICKER = 151;
    private final static int OPTION_COPY_FRAME = 152;
    public final static int OPTION_VIEW_STATISTICS = 115;
    public final static int OPTION_WELCOME_REVERT = 116;

    private final static int[] allowedNotificationsDuringChatListAnimations = new int[]{
            AyuConstants.MESSAGES_DELETED_NOTIFICATION,
            AyuConstants.DELETED_MEDIA_LOADED_NOTIFICATION,
            NotificationCenter.messagesRead,
            NotificationCenter.threadMessagesRead,
            NotificationCenter.monoForumMessagesRead,
            NotificationCenter.commentsRead,
            NotificationCenter.messagesReadEncrypted,
            NotificationCenter.messagesReadContent,
            NotificationCenter.didLoadPinnedMessages,
            NotificationCenter.newDraftReceived,
            NotificationCenter.updateMentionsCount,
            NotificationCenter.didUpdateConnectionState,
            //NotificationCenter.updateInterfaces,
            NotificationCenter.updateDefaultSendAsPeer,
            NotificationCenter.closeChats,
            NotificationCenter.chatInfoCantLoad,
            NotificationCenter.userInfoDidLoad,
            NotificationCenter.pinnedInfoDidLoad,
            NotificationCenter.didSetNewWallpapper,
            NotificationCenter.savedMessagesDialogsUpdate,
            NotificationCenter.didApplyNewTheme,
            NotificationCenter.messageReceivedByServer2
    };

    private final DialogInterface.OnCancelListener postponedScrollCancelListener = dialog -> {
        postponedScrollIsCanceled = true;
        postponedScrollMessageId = 0;
        nextScrollToMessageId = 0;
        forceNextPinnedMessageId = 0;
        invalidateMessagesVisiblePart();
        showPinnedProgress(false);
    };

    private NotificationCenter.PostponeNotificationCallback postponeNotificationsWhileLoadingCallback = new NotificationCenter.PostponeNotificationCallback() {
        @Override
        public boolean needPostpone(int id, int currentAccount, Object[] args) {
            if (id == NotificationCenter.didReceiveNewMessages) {
                long did = (Long) args[0];
                if (firstLoading && did == dialog_id) {
                    return true;
                }
            }
            return false;
        }
    };
    private int chatEmojiViewPadding;
    private int fixedKeyboardHeight = -1;
    private Runnable cancelFixedPositionRunnable;
    private boolean invalidateMessagesVisiblePart;
    private boolean scrollByTouch;
    private long welcomeMessagesChatId;
    int dialogFolderId;
    int dialogFilterId;
    boolean pulled = false;
    private static boolean replacingChatActivity = false;

    private PinchToZoomHelper pinchToZoomHelper;
    public EmojiAnimationsOverlay emojiAnimationsOverlay;
    public float drawingChatListViewYoffset;
    public int blurredViewTopOffset;
    public int blurredViewBottomOffset;
    public ChatMessageSharedResources sharedResources;

    private ValueAnimator searchExpandAnimator;
    private float searchExpandProgress;

    public static ChatActivity of(long dialogId) {
        Bundle bundle = new Bundle();
        if (dialogId >= 0) {
            bundle.putLong("user_id", dialogId);
        } else {
            bundle.putLong("chat_id", -dialogId);
        }
        return new ChatActivity(bundle);
    }

    public static ChatActivity of(long dialogId, int messageId) {
        Bundle bundle = new Bundle();
        if (dialogId >= 0) {
            bundle.putLong("user_id", dialogId);
        } else {
            bundle.putLong("chat_id", -dialogId);
        }
        bundle.putInt("message_id", messageId);
        return new ChatActivity(bundle);
    }

    public void deleteHistory(int dateSelectedStart, int dateSelectedEnd, boolean forAll) {
        chatAdapter.frozenMessages.clear();
        for (int i = 0; i < messages.size(); i++) {
            MessageObject messageObject = messages.get(i);
            if (messageObject.messageOwner.date <= dateSelectedStart || messageObject.messageOwner.date >= dateSelectedEnd) {
                chatAdapter.frozenMessages.add(messageObject);
            }
        }
        if (chatListView != null) {
            chatListView.setEmptyView(null);
        }
        if (chatAdapter.frozenMessages.isEmpty()) {
            showProgressView(true);
        }
        chatAdapter.isFrozen = true;
        chatAdapter.notifyDataSetChanged(true);
        UndoView undoView = getUndoView();
        if (undoView == null) {
            return;
        }

        undoView.showWithAction(dialog_id, UndoView.ACTION_CLEAR_DATES, () -> {
            getMessagesController().deleteMessagesRange(dialog_id, ChatObject.isChannel(currentChat) ? dialog_id : 0, dateSelectedStart, dateSelectedEnd, forAll, () -> {
                chatAdapter.frozenMessages.clear();
                chatAdapter.isFrozen = false;
                chatAdapter.notifyDataSetChanged(true);
                showProgressView(false);
            });
        }, () -> {
            chatAdapter.frozenMessages.clear();
            chatAdapter.isFrozen = false;
            chatAdapter.notifyDataSetChanged(true);
            showProgressView(false);
        });
    }

    public void showHeaderItem(boolean show) {
        if (show) {
            if (chatActivityEnterView.hasText() && TextUtils.isEmpty(chatActivityEnterView.getSlowModeTimer())) {
                if (attachItem != null) {
                    attachItem.setVisibility(isTitleCentered() ? View.GONE : View.VISIBLE);
                }
                if (headerItem != null) {
                    headerItem.setVisibility(View.GONE);
                }
                if (otherIcon != null) {
                    otherIcon.setIconVisible(!isTitleCentered());
                }
            } else {
                if (attachItem != null) {
                    attachItem.setVisibility(View.GONE);
                }
                if (headerItem != null) {
                    headerItem.setVisibility(View.VISIBLE);
                }
                if (otherIcon != null) {
                    otherIcon.setIconVisible(false);
                }
            }
        } else {
            if (attachItem != null) {
                attachItem.setVisibility(View.GONE);
            }
            if (headerItem != null) {
                headerItem.setVisibility(View.GONE);
            }
            if (otherIcon != null) {
                otherIcon.setIconVisible(false);
            }
        }
        if (avatarContainer != null) {
            avatarContainer.ignoreTouches = !show;
        }
    }

    public long getTopicId() {
        return isTopic || chatMode == MODE_SAVED || chatMode == MODE_QUICK_REPLIES || chatMode == MODE_SUGGESTIONS ? threadMessageId : 0L;
    }

    public SendMessageChatArguments getMessageChatSendParams() {
        final SendMessageChatArguments.Builder builder = new SendMessageChatArguments.Builder();
        if (chatMode == MODE_WELCOME_MESSAGES) {
            builder.setWelcomeMessageChatId(welcomeMessagesChatId);
        }
        if (chatMode == MODE_QUICK_REPLIES) {
            builder.setQuickReplyShortcut(quickReplyShortcut, getQuickReplyId());
        }

        return builder.build();
    }

    public int getQuickReplyId() {
        return chatMode == MODE_QUICK_REPLIES ? (int) threadMessageId : 0;
    }

    public long getSavedDialogId() {
        return chatMode == MODE_SAVED ? threadMessageId : 0L;
    }

    public boolean isForumInViewAsMessagesMode() {
        return ChatObject.isForum(currentChat) && !isTopic || ChatObject.isMonoForum(currentChat) && getTopicId() == 0L && !isSubscriberSuggestions;
    }

    @Override
    public List<FloatingDebugController.DebugItem> onGetDebugItems() {
        List<FloatingDebugController.DebugItem> items = new ArrayList<>();
        if (ChatObject.isChannel(currentChat)) {
            items.add(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugShareAlert)));
            String mode;
            switch (shareAlertDebugMode) {
                default:
                    mode = LocaleController.getString(R.string.DebugShareAlertDialogsModeNormal);
                    break;
                case DEBUG_SHARE_ALERT_MODE_LESS:
                    mode = LocaleController.getString(R.string.DebugShareAlertDialogsModeLess);
                    break;
                case DEBUG_SHARE_ALERT_MODE_MORE:
                    mode = LocaleController.getString(R.string.DebugShareAlertDialogsModeMore);
                    break;
            }
            items.add(new FloatingDebugController.DebugItem(LocaleController.formatString(R.string.DebugShareAlertSwitchDialogsMode, mode), () -> {
                shareAlertDebugMode++;
                shareAlertDebugMode %= 3;
            }));

            items.add(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugShareAlertTopicsSlowMotion), ()-> shareAlertDebugTopicsSlowMotion = !shareAlertDebugTopicsSlowMotion));
        }
        if (currentUser == null) {
            items.add(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugMessageSkeletons)));
            items.add(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugMessageSkeletonsLightOverlayAlpha), 0, 255, new AnimationProperties.FloatProperty("") {
                @Override
                public void setValue(Object object, float value) {
                    SKELETON_LIGHT_OVERLAY_ALPHA = (int) value;
                }

                @Override
                public Object get(Object object) {
                    return (float) SKELETON_LIGHT_OVERLAY_ALPHA;
                }
            }));
            items.add(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugMessageSkeletonsSaturation), 1f, 10f, new AnimationProperties.FloatProperty("") {
                @Override
                public void setValue(Object object, float value) {
                    SKELETON_SATURATION = value;
                    skeletonColorMatrix.setSaturation(value);
                    skeletonServicePaint.setColorFilter(new ColorMatrixColorFilter(skeletonColorMatrix));
                }

                @Override
                public Object get(Object object) {
                    return SKELETON_SATURATION;
                }
            }));
        }
        return items;
    }

    public boolean allowSendPhotos() {
        if (currentChat != null && !ChatObject.canSendPhoto(currentChat)) {
            return false;
        } else {
            return true;
        }
    }

    public ThemeDelegate createThemeDelegate() {
        return new ThemeDelegate();
    }

    public void updateMessages(ArrayList<MessageObject> messageObjects, boolean replace) {
        for (int i = 0; i < messageObjects.size(); i++) {
            chatAdapter.updateRowWithMessageObject(messageObjects.get(i), false, replace);
        }
    }

    public TextView getOrCreateWebBotTitleView() {
        if (webBotTitle == null) {
            webBotTitle = new TextView(getContext());
            webBotTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            webBotTitle.setTypeface(AndroidUtilities.bold());
            webBotTitle.setGravity(Gravity.CENTER_VERTICAL);
            webBotTitle.setSingleLine(true);
            webBotTitle.setEllipsize(TextUtils.TruncateAt.END);
            actionBar.addView(webBotTitle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0,  72, 0, 72 + 34, 0));
        }
        return webBotTitle;
    }

    private interface ChatActivityDelegate {
        default void openReplyMessage(int mid) {

        }

        default void openHashtagSearch(String hashtag) {


        }

        default void onUnpin(boolean all, boolean hide) {

        }

        default void onReport() {

        }
    }

    MessagePreviewView forwardingPreviewView;

    private PhotoViewer.PhotoViewerProvider photoViewerProvider = new PhotoViewer.EmptyPhotoViewerProvider() {

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview, boolean closing) {
            return ChatActivity.this.getPlaceForPhoto(messageObject, fileLocation, index, needPreview, false);
        }

        @Override
        public boolean validateGroupId(long groupId) {
            MessageObject.GroupedMessages groupedMessages = groupedMessagesMap.get(groupId);
            return groupedMessages != null && groupedMessages.messages.size() > 1;
        }
    };
    private PhotoViewer.PhotoViewerProvider photoViewerPaidMediaProvider = new PhotoViewer.EmptyPhotoViewerProvider() {

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview, boolean closing) {
            return ChatActivity.this.getPlaceForPhoto(messageObject, fileLocation, index, needPreview, false);
        }

        @Override
        public boolean validateGroupId(long groupId) {
            MessageObject.GroupedMessages groupedMessages = groupedMessagesMap.get(groupId);
            return groupedMessages != null && groupedMessages.messages.size() > 1;
        }

        @Override
        public boolean forceAllInGroup() {
            return true;
        }
    };

    private ArrayList<Object> botContextResults;
    private PhotoViewer.PhotoViewerProvider botContextProvider = new PhotoViewer.EmptyPhotoViewerProvider() {

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview, boolean closing) {
            if (index < 0 || index >= botContextResults.size() || mentionContainer == null || mentionContainer.getListView() == null) {
                return null;
            }
            int count = mentionContainer.getListView().getChildCount();
            Object result = botContextResults.get(index);

            for (int a = 0; a < count; a++) {
                ImageReceiver imageReceiver = null;
                View view = mentionContainer.getListView().getChildAt(a);
                if (view instanceof ContextLinkCell) {
                    ContextLinkCell cell = (ContextLinkCell) view;
                    if (cell.getResult() == result) {
                        imageReceiver = cell.getPhotoImage();
                    }
                }

                if (imageReceiver != null) {
                    int[] coords = new int[2];
                    view.getLocationInWindow(coords);
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    object.viewX = coords[0];
                    object.viewY = coords[1];
//                    object.clipTopAddition = (int) (chatListViewPaddingTop - chatListViewPaddingVisibleOffset - AndroidUtilities.dp(4));
                    object.parentView = mentionContainer.getListView();
                    object.imageReceiver = imageReceiver;
                    object.thumb = imageReceiver.getBitmapSafe();
                    object.radius = imageReceiver.getRoundRadius(true);
                    return object;
                }
            }
            return null;
        }

        @Override
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, int scheduleRepeatPeriod, boolean forceDocument) {
            if (index < 0 || index >= botContextResults.size()) {
                return;
            }
            sendBotInlineResult((TLRPC.BotInlineResult) botContextResults.get(index), notify, scheduleDate, 0);
        }
    };

    private final static int copy = 10;
    private final static int forward = 11;
    private final static int delete = 12;
    private final static int chat_enc_timer = 13;
    private final static int chat_menu_attach = 14;
    private final static int chat_menu_search = -1;
    private final static int chat_menu_options = -2;
    private final static int chat_menu_edit_text_options = -3;
    private final static int clear_history = 15;
    private final static int delete_chat = 16;
    private final static int share_contact = 17;
    private final static int mute = 18;
    private final static int report = 21;
    private final static int star = 22;
    private final static int edit = 23;
    private final static int add_shortcut = 24;
    private final static int save_to = 25;
    private final static int auto_delete_timer = 26;
    private final static int change_colors = 27;
    private final static int tag_message = 28;
    private final static int boost_group = 29;

    private final static int bot_help = 30;
    private final static int bot_settings = 31;
    private final static int call = 32;
    private final static int video_call = 33;

    private final static int attach_photo = 0;
    private final static int attach_gallery = 1;
    private final static int attach_video = 2;

    private final static int text_bold = 50;
    private final static int text_italic = 51;
    private final static int text_mono = 52;
    private final static int text_link = 53;
    private final static int text_regular = 54;
    private final static int text_strike = 55;
    private final static int text_underline = 56;
    private final static int text_spoiler = 57;
    private final static int text_quote = 58;
    private final static int text_date = 74;
    private final static int text_mention = 157;
    private final static int text_transalte = 158;
    private final static int text_code = 159;

    private final static int view_as_topics = 59;

    private final static int search = 40;

    private final static int topic_close = 60;
    private final static int open_forum = 61;
    private final static int combine_message = 6200;

    private final static int translate = 62;
    private final static int scheduled = 63;
    private final static int edit_quick_reply = 64;

    private ActionBarMenuItem actionModeOtherItem; // NekoX

    private final static int copy_business_link = 65;
    private final static int share_business_link = 66;
    private final static int rename_business_link = 67;
    private final static int delete_business_link = 68;

    private final static int share = 69;
    private final static int open_direct = 70;
    private final static int remove_fee = 71;
    private final static int charge_fee = 72;

    private final static int chat_menu_topic_create = 73;

    private final static int id_chat_compose_panel = 1000;
    private final static int to_the_beginning = 200;
    private final static int to_the_message = 201;
    private final static int shortcuts_administrators = 300;
    private final static int shortcuts_recent_actions = 301;
    private final static int shortcuts_statistics = 302;
    private final static int shortcuts_permissions = 303;
    private final static int shortcuts_members = 304;

    RecyclerListView.OnItemLongClickListenerExtended onItemLongClickListener = new RecyclerListView.OnItemLongClickListenerExtended() {
        @Override
        public boolean onItemClick(View view, int position, float x, float y) {
            if (isTryingTextSelection() || hasTextSelection() || inPreviewMode || isInsideContainer) {
                return false;
            }
            if((scrimPopupWindow != null && NaConfig.INSTANCE.getDoubleTapAction().Int() == DoubleTap.DOUBLE_TAP_ACTION_SHOW_REACTIONS))
                return false;
            wasManualScroll = true;
            boolean result = true;
            boolean showMenu = true;
            if (view instanceof ChatActionCell) {
                ChatActionCell actionCell = (ChatActionCell) view;
                MessageObject messageObject = actionCell.getMessageObject();
                if (messageObject == null) return false;
                showMenu = messageObject.messageOwner.action instanceof TLRPC.TL_messageActionSetMessagesTTL || actionCell.getMessageObject().type == MessageObject.TYPE_SUGGEST_PHOTO || actionCell.getMessageObject().isWallpaperAction() || actionCell.getMessageObject().type == MessageObject.TYPE_GIFT_STARS;
            }
            if (!actionBar.isActionModeShowed() && (!isReport() || showMenu)) {
                result = createMenu(view, false, true, x, y, true);
            } else {
                boolean outside = false;
                if (view instanceof ChatMessageCell) {
                    outside = !((ChatMessageCell) view).isInsideBackground(x, y);
                }
                processRowSelect(view, outside, x, y);
            }
            if (view instanceof ChatMessageCell && (((ChatMessageCell) view).getMessageObject() != null && ((ChatMessageCell) view).getMessageObject().type != MessageObject.TYPE_JOINED_CHANNEL)) {
                startMultiselect(position);
                result = true;
            }
            return result;
        }
    };

    public RecyclerListView getChatListView() {
        return chatListView;
    }

    private void startMultiselect(int position) {
        if (isInsideContainer) {
            return;
        }
        int indexOfMessage = position - chatAdapter.messagesStartRow;
        if (indexOfMessage < 0 || indexOfMessage >= messages.size()) {
            return;
        }
        MessageObject messageObject = messages.get(indexOfMessage);
        final boolean unselect = selectedMessagesIds[0].get(messageObject.getId(), null) == null && selectedMessagesIds[1].get(messageObject.getId(), null) == null;
        SparseArray<MessageObject> alreadySelectedMessagesIds = new SparseArray<>();
        for (int i = 0; i < selectedMessagesIds[0].size(); i++) {
            alreadySelectedMessagesIds.put(selectedMessagesIds[0].keyAt(i), selectedMessagesIds[0].valueAt(i));
        }
        for (int i = 0; i < selectedMessagesIds[1].size(); i++) {
            alreadySelectedMessagesIds.put(selectedMessagesIds[1].keyAt(i), selectedMessagesIds[1].valueAt(i));
        }
        chatListView.startMultiselect(position, false, new RecyclerListView.onMultiSelectionChanged() {
            boolean limitReached;
            @Override
            public void onSelectionChanged(int position, boolean selected, float x, float y) {
                int i = position - chatAdapter.messagesStartRow;
                if (unselect) {
                    selected = !selected;
                }
                if (i >= 0 && i < messages.size()) {
                    MessageObject messageObject = messages.get(i);
                    if (selected && (selectedMessagesIds[0].indexOfKey(messageObject.getId()) >= 0 || selectedMessagesIds[1].indexOfKey(messageObject.getId()) >= 0)) {
                        return;
                    }
                    if (!selected && selectedMessagesIds[0].indexOfKey(messageObject.getId()) < 0 && selectedMessagesIds[1].indexOfKey(messageObject.getId()) < 0) {
                        return;
                    }
                    if (messageObject.contentType == 0) {
                        if (selected && selectedMessagesIds[0].size() + selectedMessagesIds[1].size() >= 1000) {
                            limitReached = true;
                        } else {
                            limitReached = false;
                        }
                        RecyclerView.ViewHolder holder = chatListView.findViewHolderForAdapterPosition(position);
                        if (holder != null && holder.itemView instanceof ChatMessageCell) {
                            processRowSelect(holder.itemView, false, x, y);
                        } else {
                            addToSelectedMessages(messageObject, false);
                            updateActionModeTitle();
                            updateVisibleRows();
                        }
                    }
                }
            }

            @Override
            public boolean canSelect(int position) {
                int i = position - chatAdapter.messagesStartRow;
                if (i >= 0 && i < messages.size()) {
                    MessageObject messageObject = messages.get(i);
                    if (messageObject.contentType == 0) {
                        if (!unselect && alreadySelectedMessagesIds.get(messageObject.getId(), null) == null) {
                            return true;
                        }
                        if (unselect && alreadySelectedMessagesIds.get(messageObject.getId(), null) != null) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public int checkPosition(int position, boolean selectionTop) {
                int i = position - chatAdapter.messagesStartRow;
                if (i >= 0 && i < messages.size()) {
                    MessageObject messageObject = messages.get(i);
                    if (messageObject.contentType == 0 && messageObject.hasValidGroupId()) {
                        MessageObject.GroupedMessages groupedMessages = groupedMessagesMap.get(messageObject.getGroupId());
                        if (groupedMessages != null) {
                            MessageObject messageObject1 = groupedMessages.messages.get(selectionTop ? 0 : groupedMessages.messages.size() - 1);
                            return chatAdapter.messagesStartRow + messages.indexOf(messageObject1);
                        }
                    }
                }
                return position;
            }

            @Override
            public boolean limitReached() {
                return limitReached;
            }

            @Override
            public void getPaddings(int[] paddings) {
                paddings[0] = (int) chatListViewPaddingTop;
                paddings[1] = blurredViewBottomOffset;
            }

            @Override
            public void scrollBy(int dy) {
                chatListView.scrollBy(0, dy);
            }
        });
    }

    RecyclerListView.OnItemClickListenerExtended onItemClickListener = new RecyclerListView.OnItemClickListenerExtended() {
        @Override
        public void onItemClick(View view, int position, float x, float y) {
            if (inPreviewMode) {
                return;
            }
            wasManualScroll = true;
            if (view instanceof ChatActionCell && ((ChatActionCell) view).getMessageObject().isDateObject) {
                if (isInsideContainer || isFeedSearch()) {
                    return;
                }
                Bundle bundle = new Bundle();
                int date = ((ChatActionCell) view).getMessageObject().messageOwner.date;
                bundle.putLong("dialog_id", dialog_id);
                bundle.putLong("topic_id", getTopicId());
                bundle.putInt("type", CalendarActivity.TYPE_CHAT_ACTIVITY);
                CalendarActivity calendarActivity = new CalendarActivity(bundle, SharedMediaLayout.FILTER_PHOTOS_AND_VIDEOS, date);
                calendarActivity.setChatActivity(ChatActivity.this);
                presentFragment(calendarActivity);
                return;
            }
            if (view instanceof ChatActionCell && ((ChatActionCell) view).getMessageObject() != null && ((ChatActionCell) view).getMessageObject().messageOwner.action instanceof TLRPC.TL_messageActionBoostApply) {
                getNotificationCenter().postNotificationName(NotificationCenter.openBoostForUsersDialog, dialog_id);
                return;
            }
            if (view instanceof ChatActionCell && ((ChatActionCell) view).getMessageObject() != null && ((ChatActionCell) view).getMessageObject().messageOwner.action instanceof TLRPC.TL_messageActionSetSameChatWallPaper) {
                int messageId = ((ChatActionCell) view).getMessageObject().getReplyMsgId();
                AndroidUtilities.runOnUIThread(() -> {
                    scrollToMessageId(messageId, 0, true, 0, true, 0);
                }, 16);
                return;
            }
            if (actionBar.isActionModeShowed() || isReport()) {
                boolean outside = false;
                if (view instanceof ChatMessageCell) {
                    if (textSelectionHelper.isSelected(((ChatMessageCell) view).getMessageObject())) {
                        return;
                    }
                    outside = !((ChatMessageCell) view).isInsideBackground(x, y);
                }
                processRowSelect(view, outside, x, y);
                return;
            }
            if (view instanceof ChatMessageCell) {
                MessageObject msg = ((ChatMessageCell) view).getMessageObject();
                if (msg != null && msg.type == MessageObject.TYPE_JOINED_CHANNEL) {
                    msg.toggleChannelRecommendations();
                    msg.forceUpdate = true;
                    ((ChatMessageCell) view).forceResetMessageObject();
                    view.requestLayout();
                    if (position >= 0) {
                        chatAdapter.notifyItemChanged(position);
                    }
                    return;
                }
            }
            createMenu(view, true, false, x, y, false);
        }

        @Override
        public boolean hasDoubleTap(View view, int position) {
            if (isQuickRepliesOrWelcomeMessagesMode()) return false;
            MessageObject message;
            if (view instanceof ChatMessageCell) {
                message = ((ChatMessageCell) view).getPrimaryMessageObject();
            } else if (view instanceof ChatActionCell) {
                message = ((ChatActionCell) view).getMessageObject();
            } else {
                return false;
            }
            var doubleTapAction = message.isOutOwner() ? NaConfig.INSTANCE.getDoubleTapActionOut().Int() : NaConfig.INSTANCE.getDoubleTapAction().Int();
            if (doubleTapAction == DoubleTap.DOUBLE_TAP_ACTION_NONE) {
                return false;
            }
            if (doubleTapAction == DoubleTap.DOUBLE_TAP_ACTION_SEND_REACTIONS || doubleTapAction == DoubleTap.DOUBLE_TAP_ACTION_SHOW_REACTIONS) {
                String reactionStringSetting = getMediaDataController().getDoubleTapReaction();
                TLRPC.TL_availableReaction reaction = getMediaDataController().getReactionsMap().get(reactionStringSetting);
                if (reaction == null && (reactionStringSetting == null || !reactionStringSetting.startsWith("animated_"))) {
                    return false;
                }
                boolean available = dialog_id >= 0;
                if (!available && chatInfo != null) {
                    available = ChatObject.reactionIsAvailable(chatInfo, reaction == null ? reactionStringSetting : reaction.reaction);
                }
                if (!available) {
                    return false;
                }
                return message != null && !message.isDateObject && !message.isSending() && message.canSetReaction() && !message.isEditing() && !actionBar.isActionModeShowed() && !isSecretChat() && !isInScheduleMode() && !message.isSponsored() && !message.isAyuDeleted();
            } else {
                if (!(view instanceof ChatMessageCell)) {
                    return false;
                }
                selectedObjectGroup = getValidGroupedMessage(selectedObject = ((ChatMessageCell) view).getMessageObject());
                var noforwards = getMessagesController().isChatNoForwards(currentChat) || message.messageOwner.noforwards;
                var isAyuDeleted = message.isAyuDeleted();
                boolean allowChatActions = chatMode != MODE_SCHEDULED && (threadMessageObjects == null || !threadMessageObjects.contains(message)) &&
                        !message.isSponsored() && (getMessageType(message) != MESSAGE_TYPE_SERVICE || message.getDialogId() != mergeDialogId) &&
                        !(message.messageOwner.action instanceof TLRPC.TL_messageActionSecureValuesSent) &&
                        (currentEncryptedChat != null || message.getId() >= 0) &&
                        // (bottomOverlayChat == null || bottomOverlayChat.getVisibility() != View.VISIBLE) &&
                        (currentChat == null || ((!ChatObject.isNotInChat(currentChat) || isThreadChat()) && (!ChatObject.isChannel(currentChat) || ChatObject.canPost(currentChat) || currentChat.megagroup) && ChatObject.canSendMessages(currentChat)));
                boolean allowEdit = message.canEditMessage(currentChat) && !chatActivityEnterView.hasAudioToSend() && message.getDialogId() != mergeDialogId;
                if (allowEdit && selectedObjectGroup != null) {
                    int captionsCount = 0;
                    for (int a = 0, N = selectedObjectGroup.messages.size(); a < N; a++) {
                        MessageObject messageObject = selectedObjectGroup.messages.get(a);
                        if (a == 0 || !TextUtils.isEmpty(messageObject.caption)) {
                            selectedObjectToEditCaption = messageObject;
                            if (!TextUtils.isEmpty(messageObject.caption)) {
                                captionsCount++;
                            }
                        }
                    }
                    allowEdit = captionsCount < 2;
                }
                boolean allowDelete = message.canDeleteMessage(chatMode == MODE_SCHEDULED, currentChat);
                boolean allowRepeat;
                switch (doubleTapAction) {
                    case DoubleTap.DOUBLE_TAP_ACTION_TRANSLATE:
                        MessageObject messageObject = getMessageForTranslate();
                        if (messageObject != null) {
                            return true;
                        }
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_REPLY:
                        return message.getId() > 0 && allowChatActions && !isAyuDeleted;
                    case DoubleTap.DOUBLE_TAP_ACTION_SAVE:
                        return !message.isSponsored() && chatMode != MODE_SCHEDULED && !message.needDrawBluredPreview() && !message.isLiveLocation() && message.type != 16 && !noforwards && !UserObject.isUserSelf(currentUser) && !isAyuDeleted;
                    case DoubleTap.DOUBLE_TAP_ACTION_REPEAT:
                        allowRepeat = allowChatActions && (currentChat == null || ((!ChatObject.isNotInChat(currentChat) || isThreadChat()) && (!ChatObject.isChannel(currentChat) || currentChat.megagroup) && ChatObject.canSendMessages(currentChat))) && !isAyuDeleted &&
                                (!isThreadChat() && !noforwards || getMessageHelper().getMessageForRepeat(message, selectedObjectGroup) != null);
                        return allowRepeat && !message.isSponsored() && chatMode != MODE_SCHEDULED && !message.needDrawBluredPreview() && !message.isLiveLocation() && message.type != 16;
                    case DoubleTap.DOUBLE_TAP_ACTION_REPEAT_AS_COPY:
                        allowRepeat = allowChatActions && (currentChat == null || ((!ChatObject.isNotInChat(currentChat) || isThreadChat()) && (!ChatObject.isChannel(currentChat) || currentChat.megagroup) && ChatObject.canSendMessages(currentChat))) && !isAyuDeleted &&
                                (!isThreadChat() || getMessageHelper().getMessageForRepeat(message, selectedObjectGroup) != null);
                        return allowRepeat && !message.isSponsored() && chatMode != MODE_SCHEDULED && !message.needDrawBluredPreview() && !message.isLiveLocation() && message.type != 16;
                    case DoubleTap.DOUBLE_TAP_ACTION_EDIT:
                        return allowEdit && !isAyuDeleted;
                    case DoubleTap.DOUBLE_TAP_ACTION_DELETE:
                        return allowDelete;
                }
            }
            return false;
        }

        @Override
        public void onDoubleTap(View view, int position, float x, float y) {
            if (getParentActivity() == null || isSecretChat() || isInScheduleMode() || isInPreviewMode() || isQuickRepliesOrWelcomeMessagesMode()) {
                return;
            }
            MessageObject messageObject;
            if (view instanceof ChatMessageCell) {
                messageObject = ((ChatMessageCell) view).getPrimaryMessageObject();
            } else if (view instanceof ChatActionCell) {
                messageObject = ((ChatActionCell) view).getMessageObject();
                if (messageObject.isDateObject) {
                    return;
                }
            } else {
                return;
            }
            var doubleTapAction = messageObject.isOutOwner() ? NaConfig.INSTANCE.getDoubleTapActionOut().Int() : NaConfig.INSTANCE.getDoubleTapAction().Int();
            if (doubleTapAction == DoubleTap.DOUBLE_TAP_ACTION_NONE) {
                return;
            }
            if (doubleTapAction == DoubleTap.DOUBLE_TAP_ACTION_SEND_REACTIONS) {
                if (!(currentChat == null || ChatObject.isChannelAndNotMegaGroup(currentChat) || ChatObject.canUserDoAction(currentChat, ChatObject.ACTION_SEND_REACTIONS))) {
                    return;
                }

                ReactionsEffectOverlay.removeCurrent(false);
                String reactionString = getMediaDataController().getDoubleTapReaction();
                if (reactionString.startsWith("animated_")) {
                    boolean available = dialog_id >= 0;
                    if (!available && chatInfo != null) {
                        available = ChatObject.reactionIsAvailable(chatInfo, reactionString);
                    }
                    if (!available) {
                        return;
                    }
                    selectReaction(view, messageObject, null, null, x, y, ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(reactionString), true, false, false, false);
                } else {
                    TLRPC.TL_availableReaction reaction = getMediaDataController().getReactionsMap().get(reactionString);
                    if (reaction == null || messageObject.isSponsored()) {
                        return;
                    }
                    boolean available = dialog_id >= 0;
                    if (!available && chatInfo != null) {
                        available = ChatObject.reactionIsAvailable(chatInfo, reaction.reaction);
                    }
                    if (!available) {
                        return;
                    }
                    selectReaction(view, messageObject, null, null, x, y, ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(reaction), true, false, false, false);
                }
            } else if (doubleTapAction == DoubleTap.DOUBLE_TAP_ACTION_SHOW_REACTIONS) {
                createMenu(view, true, false, x, y, true, false, false, true);
            } else {
                if (!(view instanceof ChatMessageCell)) {
                    return;
                }
                selectedObjectGroup = getValidGroupedMessage(selectedObject = ((ChatMessageCell) view).getMessageObject());
                switch (doubleTapAction) {
                    case DoubleTap.DOUBLE_TAP_ACTION_TRANSLATE:
                        MessageTransKt.translateMessages(ChatActivity.this);
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_REPLY:
                        processSelectedOption(OPTION_REPLY);
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_SAVE:
                        processSelectedOption(nkbtn_savemessage);
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_REPEAT:
                        processSelectedOption(nkbtn_repeat);
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_REPEAT_AS_COPY:
                        processSelectedOption(nkbtn_repeatascopy);
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_EDIT:
                        if (messageObject.isTodo()) {
                            processSelectedOption(OPTION_EDIT_TODO);
                        } else {
                            processSelectedOption(OPTION_EDIT);
                        }
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_DELETE:
                        processSelectedOption(OPTION_DELETE);
                        break;
                    case DoubleTap.DOUBLE_TAP_ACTION_READ:
                        AyuGhostUtils.markReadOnServer(selectedObject, false);
                        BotWebViewVibrationEffect.SELECTION_CHANGE.vibrate();
                        break;
                }
            }
        }
    };

    private class ChatActivityEnterViewDelegate implements ChatActivityEnterView.ChatActivityEnterViewDelegate {

        int lastSize;
        boolean isEditTextItemVisibilitySuppressed;

        @Override
        public int getContentViewHeight() {
            return contentView.getHeight();
        }

        @Override
        public int measureKeyboardHeight() {
            return contentView.measureKeyboardHeight();
        }

        @Override
        public TLRPC.TL_channels_sendAsPeers getSendAsPeers() {
            return sendAsPeersObj;
        }

        @Override
        public void onMessageSend(CharSequence message, boolean notify, int scheduleDate, int scheduleRepeatPeriod, long payStars) {
            if (chatListItemAnimator != null) {
                chatActivityEnterViewAnimateFromTop = chatActivityEnterView.getBackgroundTop();
                if (chatActivityEnterViewAnimateFromTop != 0) {
                    chatActivityEnterViewAnimateBeforeSending = true;
                }
            }
            if (mentionContainer != null && mentionContainer.getAdapter() != null) {
                mentionContainer.getAdapter().addHashtagsFromMessage(message);
            }
            if (scheduleDate != 0) {
                if (scheduledMessagesCount == -1) {
                    scheduledMessagesCount = 0;
                }
                if (message != null) {
                    scheduledMessagesCount++;
                }
                if (messagePreviewParams != null && messagePreviewParams.forwardMessages != null && !messagePreviewParams.forwardMessages.messages.isEmpty()) {
                    scheduledMessagesCount += messagePreviewParams.forwardMessages.messages.size();
                }
                updateScheduledInterface(false);
            }
            if (!TextUtils.isEmpty(message) && messagePreviewParams != null && messagePreviewParams.forwardMessages != null && !messagePreviewParams.forwardMessages.messages.isEmpty() && messagePreviewParams.quote == null && payStars <= 0) {
                final ArrayList<MessageObject> messagesToForward = new ArrayList<>();
                messagePreviewParams.forwardMessages.getSelectedMessages(messagesToForward);
                boolean showReplyHint = messagesToForward.size() > 0;
                TLRPC.Peer toPeer = getMessagesController().getPeer(dialog_id);
                for (int i = 0; i < messagesToForward.size(); ++i) {
                    MessageObject msg = messagesToForward.get(i);
                    if (msg != null && msg.messageOwner != null && !MessageObject.peersEqual(msg.messageOwner.peer_id, toPeer)) {
                        showReplyHint = false;
                        break;
                    }
                }

                if (showReplyHint) {
                    Bulletin bulletin = BulletinFactory.of(ChatActivity.this)
                        .createSimpleBulletin(
                            R.raw.hint_swipe_reply,
                            LocaleController.getString(R.string.SwipeToReplyHint),
                            LocaleController.getString(R.string.SwipeToReplyHintMessage)
                        );
                    RLottieImageView imageView = ((Bulletin.TwoLineLottieLayout) bulletin.getLayout()).imageView;
                    imageView.setScaleX(1.8f);
                    imageView.setScaleY(1.8f);
                    bulletin.show(true);
                }
            }
            if (ChatObject.isForum(currentChat) && !isTopic && replyingMessageObject != null) {
                long topicId = replyingMessageObject.replyToForumTopic != null ? replyingMessageObject.replyToForumTopic.id : MessageObject.getTopicId(currentAccount, replyingMessageObject.messageOwner, true);
                if (topicId != 0) {
                    getMediaDataController().cleanDraft(dialog_id, topicId, false);
                }
            }

            hideFieldPanel(notify, scheduleDate, payStars, true);
            if (chatActivityEnterView != null && chatActivityEnterView.getEmojiView() != null) {
                chatActivityEnterView.getEmojiView().onMessageSend();
            }

            if (!getMessagesController().premiumFeaturesBlocked() && getMessagesController().transcribeAudioTrialWeeklyNumber <= 0 && !getMessagesController().didPressTranscribeButtonEnough() && !getUserConfig().isPremium() && !TextUtils.isEmpty(message) && messages != null) {
                for (int i = 1; i < Math.min(5, messages.size()); ++i) {
                    MessageObject msg = messages.get(i);
                    if (msg != null && !msg.isOutOwner() && (msg.isVoice() || msg.isRoundVideo()) && msg.isContentUnread()) {
                        TranscribeButton.showOffTranscribe(msg);
                    }
                }
            }
        }

        // NekoX
        @Override
        public void beforeMessageSend(CharSequence message, boolean notify, int scheduleDate, long payStars) {
            ChatActivity.this.beforeMessageSend(notify, scheduleDate, true, payStars);
        }

        @Override
        public int getDisableLinkPreviewStatus() {
            return disableLinkPreview ? 2 : 1;
        }

        @Override
        public void toggleDisableLinkPreview() {
            disableLinkPreview = !disableLinkPreview;
        }

        @Override
        public void didPressStreamingStop() {
            BotForumHelper.getInstance(currentAccount).stopStreaming(dialog_id, (int) getTopicId());
            checkSendButtonBlockedByTyping(true);
        }

        @Override
        public void onEditTextScroll() {
            if (suggestEmojiPanel != null) {
                suggestEmojiPanel.forceClose();
            }
        }

        @Override
        public void onContextMenuOpen() {
            if (suggestEmojiPanel != null) {
                suggestEmojiPanel.forceClose();
            }
        }

        @Override
        public void onContextMenuClose() {
            if (suggestEmojiPanel != null) {
                suggestEmojiPanel.fireUpdate();
            }
        }

        @Override
        public void onSwitchRecordMode(boolean video) {
            showVoiceHint(false, video);
        }

        @Override
        public void onPreAudioVideoRecord() {
            showVoiceHint(true, false);
        }

        @Override
        public void onUpdateSlowModeButton(View button, boolean show, CharSequence time) {
            showSlowModeHint(button, show, time);
            if (headerItem != null && headerItem.getVisibility() != View.VISIBLE) {
                headerItem.setVisibility(View.VISIBLE);
                if (attachItem != null) {
                    attachItem.setVisibility(View.GONE);
                }
                if (otherIcon != null) {
                    otherIcon.setIconVisible(false);
                }
            }
        }

        @Override
        public boolean checkCanRemoveRestrictionsByBoosts() {
            return ChatActivity.this.checkCanRemoveRestrictionsByBoosts();
        }

        @Override
        public void onTextSelectionChanged(int start, int end) {
            if (editTextItem == null) {
                return;
            }
            ActionBarMenu menu = actionBar.createMenu();
            if (suggestEmojiPanel != null) {
                suggestEmojiPanel.onTextSelectionChanged(start, end);
            }
            if (end - start > 0) {
                if (editTextItem.getTag() == null) {
                    editTextItem.setTag(1);

                    if (editTextItem.getVisibility() != View.VISIBLE) {
                        if (chatMode == MODE_SAVED && getSavedDialogId() == getUserConfig().getClientUserId() || chatMode == 0 && (threadMessageId == 0 || isTopic) && !UserObject.isReplyUser(currentUser) && !isReport()) {
                            editTextItem.setVisibility(View.VISIBLE);
                            checkEditTextItemMenu();
                            if (headerItem != null) {
                                headerItem.setVisibility(View.GONE);
                            }
                            if (attachItem != null) {
                                attachItem.setVisibility(View.GONE);
                            }
                            if (otherIcon != null) {
                                otherIcon.setIconVisible(false);
                            }
                        } else {
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(AndroidUtilities.dp(48), 0);
                            valueAnimator.setDuration(220);
                            valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            valueAnimator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    actionBar.setMenuOffsetSuppressed(true);
                                    checkEditTextItemMenu();
                                    editTextItem.setVisibility(View.VISIBLE);
                                    menu.translateXItems(AndroidUtilities.dp(48));
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    actionBar.setMenuOffsetSuppressed(false);
                                }
                            });
                            valueAnimator.addUpdateListener(animation -> menu.translateXItems((float) animation.getAnimatedValue()));
                            valueAnimator.start();
                        }
                    }
                }
                editTextStart = start;
                editTextEnd = end;
            } else {
                if (editTextItem.getTag() != null) {
                    editTextItem.setTag(null);
                    if (editTextItem.getVisibility() != View.GONE) {
                        if (chatMode == MODE_SAVED && getSavedDialogId() == getUserConfig().getClientUserId() || chatMode == 0 && (threadMessageId == 0 || isTopic) && !UserObject.isReplyUser(currentUser) && !isReport()) {
                            editTextItem.setVisibility(View.GONE);

                            if (chatActivityEnterView.hasText() && TextUtils.isEmpty(chatActivityEnterView.getSlowModeTimer())) {
                                if (headerItem != null) {
                                    headerItem.setVisibility(View.GONE);
                                }
                                if (attachItem != null) {
                                    attachItem.setVisibility(isTitleCentered() ? View.GONE : View.VISIBLE);
                                }
                                if (otherIcon != null) {
                                    otherIcon.setIconVisible(!isTitleCentered());
                                }
                            } else {
                                if (headerItem != null) {
                                    headerItem.setVisibility(View.VISIBLE);
                                }
                                if (attachItem != null) {
                                    attachItem.setVisibility(View.GONE);
                                }
                                if (otherIcon != null) {
                                    otherIcon.setIconVisible(false);
                                }
                            }
                        } else {
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, AndroidUtilities.dp(48));
                            valueAnimator.setDuration(220);
                            valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            valueAnimator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    actionBar.setMenuOffsetSuppressed(true);
                                    isEditTextItemVisibilitySuppressed = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    editTextItem.setVisibility(View.GONE);
                                    menu.translateXItems(0);

                                    actionBar.setMenuOffsetSuppressed(false);
                                    isEditTextItemVisibilitySuppressed = false;
                                }
                            });
                            valueAnimator.addUpdateListener(animation -> menu.translateXItems((float) animation.getAnimatedValue()));
                            valueAnimator.start();
                        }
                    }
                }
            }
        }

        @Override
        public void onTextChanged(final CharSequence text, boolean bigChange, boolean fromDraft) {
            MediaController.getInstance().setInputFieldHasText(!TextUtils.isEmpty(text) || chatActivityEnterView.isEditingMessage());
            if (mentionContainer != null && mentionContainer.getAdapter() != null) {
                mentionContainer.getAdapter().searchUsernameOrHashtag(text, chatActivityEnterView.getCursorPosition(), messages, false, false);
            }
            if (waitingForCharaterEnterRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(waitingForCharaterEnterRunnable);
                waitingForCharaterEnterRunnable = null;
            }
            if ((currentChat == null || ChatObject.canSendEmbed(currentChat)) && chatActivityEnterView.isMessageWebPageSearchEnabled() && (!chatActivityEnterView.isEditingMessage() || !chatActivityEnterView.isEditingCaption())) {
                if (bigChange) {
                    searchLinks(text, true);
                } else {
                    checkEditLinkRemoved(text);
                    waitingForCharaterEnterRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (this == waitingForCharaterEnterRunnable) {
                                searchLinks(text, false);
                                waitingForCharaterEnterRunnable = null;
                            }
                        }
                    };
                    AndroidUtilities.runOnUIThread(waitingForCharaterEnterRunnable, AndroidUtilities.WEB_URL == null ? 3000 : 1000);
                }
            }
            if (emojiAnimationsOverlay != null) {
                emojiAnimationsOverlay.cancelAllAnimations();
            }
            ReactionsEffectOverlay.dismissAll();
            if (!fromDraft) {
                if ((scheduledOrNoSoundHint != null && scheduledOrNoSoundHint.getVisibility() == View.VISIBLE)
                        || (scheduledHint != null && scheduledHint.getVisibility() == View.VISIBLE)) {
                    hideSendButtonHints();
                } else {
                    showScheduledHint();
                }
            }
        }

        @Override
        public void onTextSpansChanged(CharSequence text) {
            searchLinks(text, true);
        }

        @Override
        public void needSendTyping() {
            if (isQuickRepliesOrWelcomeMessagesMode() || chatMode == MODE_EDIT_BUSINESS_LINK || chatMode == MODE_SUGGESTIONS) return;
            getMessagesController().sendTyping(dialog_id, threadMessageId, 0, classGuid);
        }

        @Override
        public void onAttachButtonHidden() {
            if (actionBar.isSearchFieldVisible()) {
                return;
            }
            if (editTextItem != null && !isEditTextItemVisibilitySuppressed) {
                editTextItem.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(chatActivityEnterView.getSlowModeTimer())) {
                if (headerItem != null) {
                    headerItem.setVisibility(View.GONE);
                }
                if (attachItem != null) {
                    attachItem.setVisibility(isTitleCentered() ? View.GONE : View.VISIBLE);
                }
                if (otherIcon != null) {
                    otherIcon.setIconVisible(!isTitleCentered());
                }
            }
        }

        @Override
        public void onAttachButtonShow() {
            if (actionBar.isSearchFieldVisible()) {
                return;
            }
            if (headerItem != null) {
                headerItem.setVisibility(View.VISIBLE);
            }
            if (editTextItem != null && !isEditTextItemVisibilitySuppressed) {
                editTextItem.setVisibility(View.GONE);
            }
            if (attachItem != null) {
                attachItem.setVisibility(View.GONE);
            }
            if (otherIcon != null) {
                otherIcon.setIconVisible(false);
            }
        }

        @Override
        public void onMessageEditEnd(boolean loading) {
            if (chatListItemAnimator != null) {
                chatActivityEnterViewAnimateFromTop = chatActivityEnterView.getBackgroundTop();
                if (chatActivityEnterViewAnimateFromTop != 0) {
                    chatActivityEnterViewAnimateBeforeSending = true;
                }
            }
            if (!loading) {
                if (mentionContainer != null) {
                    mentionContainer.getAdapter().setNeedBotContext(true);
                }
                if (editingMessageObject != null) {
                    AndroidUtilities.runOnUIThread(() -> hideFieldPanel(true), 30);
                }
                boolean waitingForKeyboard = false;
                if (chatActivityEnterView.isPopupShowing()) {
                    chatActivityEnterView.setFieldFocused();
                    waitingForKeyboard = true;
                }
                chatActivityEnterView.setAllowStickersAndGifs(true, true, true, waitingForKeyboard);
                if (editingMessageObjectReqId != 0) {
                    getConnectionsManager().cancelRequest(editingMessageObjectReqId, true);
                    editingMessageObjectReqId = 0;
                }
                updatePinnedMessageView(true);
                updateBottomOverlay();
                updateVisibleRows();
            }
        }

        @Override
        public void onWindowSizeChanged(int size) {
            if (size < AndroidUtilities.dp(72) + ActionBar.getCurrentActionBarHeight()) {
                allowStickersPanel = false;
                if (suggestEmojiPanel.getVisibility() == View.VISIBLE) {
                    suggestEmojiPanel.setVisibility(View.INVISIBLE);
                }
            } else {
                allowStickersPanel = true;
                if (suggestEmojiPanel.getVisibility() == View.INVISIBLE && !isInPreviewMode()) {
                    suggestEmojiPanel.setVisibility(View.VISIBLE);
                }
            }

            allowContextBotPanel = !chatActivityEnterView.isPopupShowing();
//                checkContextBotPanel();
            int size2 = size + (chatActivityEnterView.isPopupShowing() ? 1 << 16 : 0);
            if (lastSize != size2) {
                chatActivityEnterViewAnimateFromTop = 0;
                chatActivityEnterViewAnimateBeforeSending = false;
            }
            lastSize = size2;
        }

        @Override
        public void onStickersTab(boolean opened) {
            if (emojiButtonRed != null) {
                emojiButtonRed.setVisibility(View.GONE);
            }
            allowContextBotPanelSecond = !opened;
//                checkContextBotPanel();
        }

        @Override
        public void didPressAttachButton() {
            if (chatAttachAlert != null) {
                chatAttachAlert.setEditingMessageObject(0, null);
            }
            openAttachMenu();
        }

        @Override
        public void didPressSuggestionButton() {
            new MessageSuggestionOfferSheet(getContext(), currentAccount, dialog_id, messageSuggestionParams != null ? messageSuggestionParams: MessageSuggestionParams.empty(), ChatActivity.this, getResourceProvider(), MessageSuggestionOfferSheet.MODE_INPUT, ChatActivity.this::showFieldPanelForSuggestionParams).show();
        }

        @Override
        public void toggleVideoRecordingPause() {
            if (instantCameraView != null) {
                instantCameraView.togglePause();
            }
        }

        @Override
        public boolean isVideoRecordingPaused() {
            return instantCameraView != null && instantCameraView.isPaused();
        }

        @Override
        public void needStartRecordVideo(int state, boolean notify, int scheduleDate, int scheduleRepeatPeriod, int ttl, long effectId, long stars) {
            checkInstantCameraView();
            if (instantCameraView != null) {
                if (state == 0) {
                    instantCameraView.showCamera(false);
                    chatListView.stopScroll();
                    chatAdapter.updateRowsSafe();
                } else if (state == 1 || state == 3 || state == 4) {
                    instantCameraView.send(state, notify, scheduleDate, 0, ttl, effectId, stars);
                } else if (state == 2 || state == 5) {
                    instantCameraView.cancel(state == 2);
                }
            }
        }

        @Override
        public void setVideoRecordingCameraFront(boolean front) {
            checkInstantCameraView();
            if (instantCameraView != null) {
                instantCameraView.setUseFrontCamera(front);
            }
        }

        @Override
        public void needChangeVideoPreviewState(int state, float seekProgress) {
            if (instantCameraView != null) {
                instantCameraView.changeVideoPreviewState(state, seekProgress);
            }
        }

        @Override
        public void needStartRecordAudio(int state) {
            int visibility = state == 0 ? View.GONE : View.VISIBLE;
            if (overlayView.getVisibility() != visibility) {
                overlayView.setVisibility(visibility);
            }
        }

        @Override
        public void needShowMediaBanHint() {
            showMediaBannedHint();
        }

        @Override
        public void onEmojiViewTabChanged() {
            final boolean isExpanded = chatActivityEnterView.isStickersExpanded();
            final boolean isEmoji = chatActivityEnterView.isCurrentPageEmoji();
            animatorHideTopPanelByEmojiKeyboardExpanded.setValue(isExpanded && !isEmoji, true);
        }

        @Override
        public void onStickersExpandedChange() {
            checkRaiseSensors();

            final boolean isExpanded = chatActivityEnterView.isStickersExpanded();
            final boolean isEmoji = chatActivityEnterView.isCurrentPageEmoji();
            animatorHideTopPanelByEmojiKeyboardExpanded.setValue(isExpanded && !isEmoji, true);

            if (isExpanded) {
                AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), classGuid);
                if (Bulletin.getVisibleBulletin() != null && Bulletin.getVisibleBulletin().isShowing()) {
                    Bulletin.getVisibleBulletin().hide();
                }
            } else {
                AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
            }
            if (mentionContainer != null) {
                mentionContainer.animate().alpha(isExpanded || isInPreviewMode() ? 0 : 1f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
            if (suggestEmojiPanel != null) {
                suggestEmojiPanel.setVisibility(View.VISIBLE);
                suggestEmojiPanel.animate().alpha(isExpanded || isInPreviewMode() ? 0 : 1f).setInterpolator(CubicBezierInterpolator.DEFAULT).withEndAction(() -> {
                    if (suggestEmojiPanel != null && isExpanded) {
                        suggestEmojiPanel.setVisibility(View.GONE);
                    }
                }).start();
            }
        }

        @Override
        public void scrollToSendingMessage() {
            int id = getSendMessagesHelper().getSendingMessageId(dialog_id);
            if (id != 0) {
                scrollToMessageId(id, 0, true, 0, true, 0);
            }
        }

        @Override
        public boolean hasScheduledMessages() {
            if (getMessagesController().isForum(getDialogId()) && !isTopic || chatMode == MODE_WELCOME_MESSAGES) {
                return false;
            }
            return scheduledMessagesCount > 0 && (chatMode == 0 || chatMode == MODE_SAVED && getSavedDialogId() == getUserConfig().getClientUserId());
        }

        @Override
        public void onSendLongClick() {
            if (scheduledOrNoSoundHint != null) {
                scheduledOrNoSoundHint.hide();
            }
            if (scheduledHint != null) {
                scheduledHint.hide();
            }
        }

        @Override
        public void openScheduledMessages() {
            ChatActivity.this.openScheduledMessages();
        }

        @Override
        public void onAudioVideoInterfaceUpdated() {
            updatePagedownButtonVisibility(true);
        }

        @Override
        public void bottomPanelTranslationYChanged(float translation) {
            if (translation != 0) {
                wasManualScroll = true;
            }

            invalidateChatListViewTopPadding();
            invalidateMessagesVisiblePart();
            updateTextureViewPosition(false, false);
            contentView.invalidate();
            updateBulletinLayout();
        }

        @Override
        public void prepareMessageSending() {
            waitingForSendingMessageLoad = true;
            if (chatAdapter != null) {
                chatAdapter.checkRemoveBotForumRowsStartThreadRow(true);
            }
        }

        @Override
        public void onTrendingStickersShowed(boolean show) {
            if (show) {
                AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), classGuid);
                fragmentView.requestLayout();
            } else {
                AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
            }
        }

        @Override
        public boolean hasForwardingMessages() {
            return messagePreviewParams != null && messagePreviewParams.forwardMessages != null && !messagePreviewParams.forwardMessages.messages.isEmpty();
        }

        @Override
        public void onKeyboardRequested() {
            checkAdjustResize();
        }

        @Override
        public boolean onceVoiceAvailable() {
            return currentUser != null && !UserObject.isUserSelf(currentUser) && !currentUser.bot && currentEncryptedChat == null && chatMode == 0;
        }

        @Override
        public ReplyQuote getReplyQuote() {
            return replyingQuote;
        }
    }

    private final ChatScrollCallback chatScrollHelperCallback = new ChatScrollCallback();

    private final Runnable showScheduledOrNoSoundRunnable = () -> {
        if (getParentActivity() == null || fragmentView == null || chatActivityEnterView == null) {
            return;
        }
        View anchor = chatActivityEnterView.getSendButton();
        if (anchor == null || chatActivityEnterView.getEditField() == null || chatActivityEnterView.getEditField().getText().length() < 5) {
            return;
        }
        SharedConfig.increaseScheduledOrNoSoundHintShowed();
        if (scheduledOrNoSoundHint == null) {
            scheduledOrNoSoundHint = new HintView(getParentActivity(), 4, themeDelegate) {
                @Override
                protected int offsetCx() {
                    return dp(100 - 44) / 2;
                }
            };
            scheduledOrNoSoundHint.createCloseButton();
            scheduledOrNoSoundHint.setAlpha(0);
            scheduledOrNoSoundHint.setVisibility(View.INVISIBLE);
            scheduledOrNoSoundHint.setText(getString(R.string.ScheduledOrNoSoundHint));
            contentView.addView(scheduledOrNoSoundHint, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 10, 0, 10, 0));
        }
        scheduledOrNoSoundHint.showForView(anchor, true);
        scheduledOrNoSoundHintShown = true;
    };

    private final Runnable showScheduledHintRunnable = () -> {
        if (getParentActivity() == null || fragmentView == null || chatActivityEnterView == null || forwardingPreviewView != null || getMessagesController().getSendPaidMessagesStars(getDialogId()) > 0) {
            return;
        }
        View anchor = chatActivityEnterView.getSendButton();
        if (anchor == null || chatActivityEnterView.getEditField() == null || chatActivityEnterView.getEditField().getText().length() == 0) {
            return;
        }
        SharedConfig.increaseScheduledHintShowed();
        if (scheduledHint == null) {
            scheduledHint = new HintView(getParentActivity(), 4, themeDelegate);
            scheduledHint.createCloseButton();
            scheduledHint.setAlpha(0);
            scheduledHint.setVisibility(View.INVISIBLE);
            scheduledHint.setText(LocaleController.getString(R.string.ScheduledHint));
            contentView.addView(scheduledHint, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 10, 0, 10, 0));
        }
        scheduledHint.showForView(anchor, true);
        scheduledHintShown = true;
    };

    public boolean isInsideContainer;
    public boolean reversed;
    private long wallpaperRandomSeed;

    public ChatActivity(Bundle args) {
        super(args);

        navbarContentSourceWallpaper = new BlurredBackgroundSourceWrapped();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && SharedConfig.chatBlurEnabled()) {
            scrollableViewNoiseSuppressor = new DownscaleScrollableNoiseSuppressor();

            recommendedAdditionalSizeY = Math.max(0, dp(48) - Math.min(AndroidUtilities.navigationBarHeight, AndroidUtilities.statusBarHeight));

            glassBackgroundSourceFrostedRenderNode = new BlurredBackgroundSourceRenderNode(navbarContentSourceWallpaper);
            glassBackgroundSourceFrostedRenderNode.setOnDrawablesRelativePositionChangeListener(this::invalidateMergedVisibleBlurredPositionsAndSourcesPositions);
            glassBackgroundSourceFrostedRenderNode.setScrollableNoiseSuppressor(scrollableViewNoiseSuppressor, DownscaleScrollableNoiseSuppressor.DRAW_FROSTED_GLASS);
            glassBackgroundSourceFrostedRenderNode.setUnderSource(navbarContentSourceWallpaper);

            glassBackgroundDrawableFactoryFrosted = new BlurredBackgroundDrawableViewFactory(glassBackgroundSourceFrostedRenderNode);
            glassBackgroundDrawableFactoryFrosted.setLiquidGlassEffectAllowed(LiteMode.isEnabled(LiteMode.FLAG_LIQUID_GLASS));

            if (LiteMode.isEnabled(LiteMode.FLAG_LIQUID_GLASS)) {
                glassBackgroundSourceRenderNode = new BlurredBackgroundSourceRenderNode(navbarContentSourceWallpaper);
                glassBackgroundSourceRenderNode.setOnDrawablesRelativePositionChangeListener(this::invalidateMergedVisibleBlurredPositionsAndSourcesPositions);
                glassBackgroundSourceRenderNode.setScrollableNoiseSuppressor(scrollableViewNoiseSuppressor, DownscaleScrollableNoiseSuppressor.DRAW_GLASS);
                glassBackgroundSourceRenderNode.setUnderSource(navbarContentSourceWallpaper);
                glassBackgroundDrawableFactory = new BlurredBackgroundDrawableViewFactory(glassBackgroundSourceRenderNode);
                glassBackgroundDrawableFactory.setLiquidGlassEffectAllowed(LiteMode.isEnabled(LiteMode.FLAG_LIQUID_GLASS));
            } else {
                glassBackgroundSourceRenderNode = null;
                glassBackgroundDrawableFactory = glassBackgroundDrawableFactoryFrosted;
            }
        } else {
            scrollableViewNoiseSuppressor = null;
            recommendedAdditionalSizeY = 0;

            glassBackgroundSourceRenderNode = null;
            glassBackgroundSourceFrostedRenderNode = null;

            glassBackgroundDrawableFactory = new BlurredBackgroundDrawableViewFactory(navbarContentSourceWallpaper);
            glassBackgroundDrawableFactoryFrosted = new BlurredBackgroundDrawableViewFactory(navbarContentSourceWallpaper);
        }
        navbarContentDrawableFactory = new BlurredBackgroundDrawableViewFactory(navbarContentSourceWallpaper);
        navbarContentDrawableFactory.setLinkedViewsRef(glassAttachedViews);
        glassBackgroundDrawableFactory.setLinkedViewsRef(glassAttachedViews);
        glassBackgroundDrawableFactoryFrosted.setLinkedViewsRef(glassAttachedViews);
        scrimBlur3Factory.setLinkedViewsRef(new ReferenceList<>());

        navbarContentDrawableFactory.setLinkedDrawablesRef(glassAttachedDrawables);
        glassBackgroundDrawableFactory.setLinkedDrawablesRef(glassAttachedDrawables);
        glassBackgroundDrawableFactoryFrosted.setLinkedDrawablesRef(glassAttachedDrawables);
        scrimBlur3Factory.setLinkedDrawablesRef(glassAttachedDrawables);
    }

    private NotificationCenter.ObserversGroup observersGroup;
    private NotificationCenter.ObserversGroup globalObserversGroup;

    @Override
    public boolean onFragmentCreate() {
        final long chatId = arguments.getLong("chat_id", 0);
        final long userId = arguments.getLong("user_id", 0);
        final int encId = arguments.getInt("enc_id", 0);
        dialogFolderId = arguments.getInt("dialog_folder_id", 0);
        dialogFilterId = arguments.getInt("dialog_filter_id", 0);
        chatMode = arguments.getInt("chatMode", 0);
        hasMainTabs = arguments.getBoolean("hasMainTabs", false);
        quickReplyShortcut = arguments.getString("quick_reply", null);
        welcomeMessagesChatId = arguments.getLong("welcome_messages_chat_id", 0);
        voiceChatHash = arguments.getString("voicechat", null);
        openVideoChat = arguments.getBoolean("videochat", false);
        livestream = !TextUtils.isEmpty(arguments.getString("livestream", null));
        attachMenuBotToOpen = arguments.getString("attach_bot", null);
        attachMenuBotStartCommand = arguments.getString("attach_bot_start_command", null);
        inlineReturn = arguments.getLong("inline_return", 0);
        final String inlineQuery = arguments.getString("inline_query");
        textToSet = arguments.getString("start_text");
        premiumInvoiceBot = arguments.getBoolean("premium_bot", false);
        startLoadFromMessageId = arguments.getInt("message_id", 0);
        if (highlightTaskId == null) {
            highlightTaskId = arguments.containsKey("task_id") ? arguments.getInt("task_id", 0) : null;
        }
        if (highlightPollOptionId == null) {
            highlightPollOptionId = arguments.containsKey("poll_option_id") ? arguments.getByteArray("poll_option_id") : null;
        }
        startReplyTo = arguments.getInt("reply_to", 0);
        startLoadFromDate = arguments.getInt("start_from_date", 0);
        startFromVideoTimestamp = arguments.getInt("video_timestamp", -1);
        threadUnreadMessagesCount = arguments.getInt("unread_count", 0);
        convertingToast = arguments.getBoolean("converting_toast", false);
        convertingToastMessageId = arguments.getInt("converting_toast_from", 0);
        isSubscriberSuggestions = arguments.getBoolean("isSubscriberSuggestions", false);
        if (startFromVideoTimestamp >= 0) {
            startFromVideoMessageId = startLoadFromMessageId;
        }
        reportTitle = arguments.getString("reportTitle", null);
        reportOption = arguments.getByteArray("reportOption");
        reportMessage = arguments.getString("reportMessage", null);
        pulled = arguments.getBoolean("pulled", false);
        historyPreloaded = arguments.getBoolean("historyPreloaded", false);
        if (highlightMessageId != 0 && highlightMessageId != Integer.MAX_VALUE) {
            startLoadFromMessageId = highlightMessageId;
        }
        migrated_to = arguments.getInt("migrated_to", 0);
        scrollToTopOnResume = arguments.getBoolean("scrollToTopOnResume", false);
        needRemovePreviousSameChatActivity = arguments.getBoolean("need_remove_previous_same_chat_activity", true);
        noForwardQuote = arguments.getBoolean("forward_noquote", false);
        noForwardCaption = arguments.getBoolean("forward_nocaption", false);
        justCreatedChat = arguments.getBoolean("just_created_chat", false);
        wallpaperRandomSeed = Utilities.random.nextLong();
        if (quickReplyShortcut != null) {
            QuickRepliesController.QuickReply quickReply = QuickRepliesController.getInstance(currentAccount).findReply(quickReplyShortcut);
            if (quickReply != null) {
                setQuickReplyId(quickReply.id);
            }
        }

        if (chatId != 0) {
            currentChat = getMessagesController().getChat(chatId);
            if (currentChat == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                final MessagesStorage messagesStorage = getMessagesStorage();
                messagesStorage.getStorageQueue().postRunnable(() -> {
                    currentChat = messagesStorage.getChat(chatId);
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (currentChat != null) {
                    getMessagesController().putChat(currentChat, true);
                } else {
                    return false;
                }
            }
            if (ChatObject.isMonoForum(currentChat)) {
                chatMode = MODE_SUGGESTIONS;
                isSubscriberSuggestions = !ChatObject.canManageMonoForum(currentAccount, currentChat);
            }
            dialog_id = -chatId;
            if (ChatObject.isChannel(currentChat)) {
                if (ChatObject.isNotInChat(currentChat) && !ChatObject.isMonoForum(currentChat) && !isThreadChat() && !isInScheduleMode()) {
                    waitingForGetDifference = true;
                    getMessagesController().startShortPoll(currentChat, classGuid, false, isGettingDifference -> {
                        waitingForGetDifference = isGettingDifference;
                        if (!waitingForGetDifference) {
                            firstLoadMessages();
                        }
                    });
                } else {
                    getMessagesController().startShortPoll(currentChat, classGuid, false);
                }
            }
        } else if (userId != 0) {
            currentUser = getMessagesController().getUser(userId);
            if (currentUser == null) {
                final MessagesStorage messagesStorage = getMessagesStorage();
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                messagesStorage.getStorageQueue().postRunnable(() -> {
                    currentUser = messagesStorage.getUser(userId);
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (currentUser != null) {
                    getMessagesController().putUser(currentUser, true);
                } else {
                    return false;
                }
            }
            dialog_id = userId;
            botUser = arguments.getString("botUser");
            if (inlineQuery != null) {
                getMessagesController().sendBotStart(currentUser, inlineQuery);
            } else if (premiumInvoiceBot && !TextUtils.isEmpty(botUser)) {
                getMessagesController().sendBotStart(currentUser, botUser);

                botUser = null;
                premiumInvoiceBot = false;
            }
            hasQuickReplies = false;
            if (currentUser != null && chatMode == 0 && !currentUser.bot) {
                QuickRepliesController.getInstance(currentAccount).load();
                if (NaConfig.INSTANCE.getShowQuickReplyInBotCommands().Bool()) {
                    hasQuickReplies = QuickRepliesController.getInstance(currentAccount).hasReplies();
                }
            }
        } else if (encId != 0) {
            currentEncryptedChat = getMessagesController().getEncryptedChat(encId);
            final MessagesStorage messagesStorage = getMessagesStorage();
            if (currentEncryptedChat == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                messagesStorage.getStorageQueue().postRunnable(() -> {
                    currentEncryptedChat = messagesStorage.getEncryptedChat(encId);
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (currentEncryptedChat != null) {
                    getMessagesController().putEncryptedChat(currentEncryptedChat, true);
                } else {
                    return false;
                }
            }
            currentUser = getMessagesController().getUser(currentEncryptedChat.user_id);
            if (currentUser == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                messagesStorage.getStorageQueue().postRunnable(() -> {
                    currentUser = messagesStorage.getUser(currentEncryptedChat.user_id);
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (currentUser != null) {
                    getMessagesController().putUser(currentUser, true);
                } else {
                    return false;
                }
            }
            dialog_id = DialogObject.makeEncryptedDialogId(encId);
            maxMessageId[0] = maxMessageId[1] = Integer.MIN_VALUE;
            minMessageId[0] = minMessageId[1] = Integer.MAX_VALUE;
        } else if (chatMode == MODE_EDIT_BUSINESS_LINK) {
            String businessLinkArgument = arguments.getString("business_link");
            if (businessLinkArgument == null) {
                return false;
            }
            businessLink = BusinessLinksController.getInstance(currentAccount).findLink(businessLinkArgument);
            if (businessLink == null) {
                return false;
            }
            forceEmptyHistory();
        } else if (chatMode == MODE_SEARCH) {
            searchType = arguments.getInt("searchType", 0);
            searchingHashtag = arguments.getString("searchHashtag", null);
            searchingQuery = searchingHashtag;
            if (searchType == 0 || (searchingHashtag == null && searchType != 4)) {
                return false;
            }
        } else {
            return false;
        }

        dialog_id_Long = dialog_id;

        transitionAnimationGlobalIndex = NotificationCenter.getGlobalInstance().setAnimationInProgress(transitionAnimationGlobalIndex, new int[0]);

        if (currentUser != null && Build.VERSION.SDK_INT < 23) {
            MediaController.getInstance().startMediaObserver();
        }

        observersGroup = getNotificationCenter().createObserversGroup(this);
        globalObserversGroup = NotificationCenter.getGlobalInstance().createObserversGroup(this);

        getNotificationCenter().addPostponeNotificationsCallback(postponeNotificationsWhileLoadingCallback);
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);

        // na: unread count
        getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);

        if (chatMode != MODE_SCHEDULED) {
            if (threadMessageId == 0) {
                observersGroup
                    .add(NotificationCenter.screenshotTook)
                    .add(NotificationCenter.encryptedChatUpdated)
                    .add(NotificationCenter.messagesReadEncrypted)
                    .add(NotificationCenter.updateMentionsCount)
                    .add(NotificationCenter.newDraftReceived)
                    .add(NotificationCenter.chatOnlineCountDidLoad)
                    .add(NotificationCenter.peerSettingsDidLoad)
                    .add(NotificationCenter.didLoadPinnedMessages)
                    .add(NotificationCenter.commentsRead)
                    .add(NotificationCenter.changeRepliesCounter)
                    .add(NotificationCenter.messagesRead)
                    .add(NotificationCenter.didLoadChatInviter)
                    .add(NotificationCenter.groupCallUpdated);
            } else {
                observersGroup.add(NotificationCenter.threadMessagesRead);
                if (isTopic) {
                    observersGroup.add(NotificationCenter.updateMentionsCount);
                    observersGroup.add(NotificationCenter.didLoadPinnedMessages);
                }
            }
            observersGroup
                .add(NotificationCenter.monoForumMessagesRead)
                .add(NotificationCenter.botKeyboardDidLoad)
                .add(NotificationCenter.removeAllMessagesFromDialog)
                .add(NotificationCenter.messagesReadContent)
                .add(NotificationCenter.chatSearchResultsAvailable)
                .add(NotificationCenter.chatSearchResultsLoading)
                .add(NotificationCenter.didUpdateMessagesViews)
                .add(NotificationCenter.didUpdatePollResults)
                .add(NotificationCenter.availableEffectsUpdate)
                .add(NotificationCenter.starReactionAnonymousUpdate);
            if (currentEncryptedChat != null) {
                observersGroup.add(NotificationCenter.didVerifyMessagesStickers);
            }
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pluginMenuItemsUpdated);
        if (chatMode != MODE_PINNED) {
            observersGroup.add(NotificationCenter.didReceiveNewMessages);
        }
        if (chatMode == 0) {
            observersGroup.add(NotificationCenter.didLoadSponsoredMessages);
        }
        observersGroup
            .add(NotificationCenter.updatedChatRanks)
            .add(NotificationCenter.premiumFloodWaitReceived)
            .add(NotificationCenter.messagesDidLoad)
            .add(NotificationCenter.loadingMessagesFailed)
            .add(NotificationCenter.didUpdateConnectionState)
            .add(NotificationCenter.updateInterfaces)
            .add(NotificationCenter.updateDefaultSendAsPeer)
            .add(NotificationCenter.userIsPremiumBlockedUpadted)
            .add(NotificationCenter.didLoadSendAsPeers)
            .add(NotificationCenter.closeChatActivity)
            .add(NotificationCenter.messagesDeleted)
            .add(NotificationCenter.historyCleared)
            .add(NotificationCenter.messageReceivedByServer)
            .add(NotificationCenter.messageReceivedByAck)
            .add(NotificationCenter.messageSendError)
            .add(NotificationCenter.chatInfoDidLoad)
            .add(NotificationCenter.groupRestrictionsUnlockedByBoosts)
            .add(NotificationCenter.customStickerCreated)
            .add(NotificationCenter.contactsDidLoad)
            .add(NotificationCenter.messagePlayingProgressDidChanged)
            .add(NotificationCenter.messagePlayingDidReset)
            .add(NotificationCenter.messagePlayingGoingToStop)
            .add(NotificationCenter.messagePlayingPlayStateChanged)
            .add(NotificationCenter.blockedUsersDidLoad)
            .add(NotificationCenter.fileNewChunkAvailable)
            .add(NotificationCenter.didCreatedNewDeleteTask)
            .add(NotificationCenter.messagePlayingDidStart)
            .add(NotificationCenter.updateMessageMedia)
            .add(NotificationCenter.voiceTranscriptionUpdate)
            .add(NotificationCenter.animatedEmojiDocumentLoaded)
            .add(NotificationCenter.replaceMessagesObjects)
            .add(NotificationCenter.notificationsSettingsUpdated)
            .add(NotificationCenter.replyMessagesDidLoad)
            .add(NotificationCenter.didReceivedWebpages)
            .add(NotificationCenter.didReceivedWebpagesInUpdates)
            .add(NotificationCenter.botInfoDidLoad)
            .add(NotificationCenter.chatInfoCantLoad)
            .add(NotificationCenter.userInfoDidLoad)
            .add(NotificationCenter.pinnedInfoDidLoad)
            .add(NotificationCenter.topicsDidLoaded)
            .add(NotificationCenter.chatWasBoostedByUser)
            .add(NotificationCenter.channelRightsUpdated)
            .add(NotificationCenter.audioRecordTooShort)
            .add(NotificationCenter.didUpdateReactions)
            .add(NotificationCenter.savedReactionTagsUpdate)
            .add(NotificationCenter.updateAllMessages)
            .add(NotificationCenter.didUpdateExtendedMedia)
            .add(NotificationCenter.videoLoadingStateChanged)
            .add(NotificationCenter.scheduledMessagesUpdated)
            .add(NotificationCenter.diceStickersDidLoad)
            .add(NotificationCenter.dialogDeleted)
            .add(NotificationCenter.chatAvailableReactionsUpdated)
            .add(NotificationCenter.dialogsUnreadReactionsCounterChanged)
            .add(NotificationCenter.dialogsUnreadPollVotesCounterChanged)
            .add(NotificationCenter.groupStickersDidLoad)
            .add(NotificationCenter.dialogTranslate)
            .add(NotificationCenter.dialogIsTranslatable)
            .add(NotificationCenter.messageTranslated)
            .add(NotificationCenter.messageTranslating)
            .add(NotificationCenter.onReceivedChannelDifference)
            .add(NotificationCenter.storiesUpdated)
            .add(NotificationCenter.channelRecommendationsLoaded)
            .add(NotificationCenter.updateTranscriptionLock)
            .add(NotificationCenter.savedMessagesDialogsUpdate)
            .add(NotificationCenter.quickRepliesDeleted)
            .add(NotificationCenter.quickRepliesUpdated)
            .add(NotificationCenter.factCheckLoaded)
            .add(NotificationCenter.messagesFeeUpdated)
            .add(NotificationCenter.starBalanceUpdated)
            .add(NotificationCenter.botForumTopicDidCreate)
            .add(NotificationCenter.botForumDraftUpdate)
            .add(NotificationCenter.botForumDraftDelete)
            .add(NotificationCenter.joinedGroup)
            .add(NotificationCenter.regexFiltersUpdated)
            .add(AyuConstants.MESSAGES_DELETED_NOTIFICATION)
            .add(AyuConstants.DELETED_MEDIA_LOADED_NOTIFICATION);

        globalObserversGroup
            .add(NotificationCenter.emojiLoaded)
            .add(NotificationCenter.invalidateMotionBackground)
            .add(NotificationCenter.didSetNewWallpapper)
            .add(NotificationCenter.didApplyNewTheme)
            .add(NotificationCenter.goingToPreviewTheme);

        if (chatMode == MODE_EDIT_BUSINESS_LINK) {
            observersGroup.add(NotificationCenter.businessLinksUpdated);
        }
        if (chatMode == MODE_SEARCH) {
            observersGroup.add(NotificationCenter.hashtagSearchUpdated);
        }

        super.onFragmentCreate();

        if (chatMode == MODE_PINNED) {
            ArrayList<MessageObject> messageObjects = new ArrayList<>();
            for (int a = 0, N = pinnedMessageIds.size(); a < N; a++) {
                Integer id = pinnedMessageIds.get(a);
                MessageObject object = pinnedMessageObjects.get(id);
                if (object != null) {
                    MessageObject o = new MessageObject(object.currentAccount, object.messageOwner, true, false);
                    o.replyMessageObject = object.replyMessageObject;
                    o.mediaExists = object.mediaExists;
                    o.attachPathExists = object.attachPathExists;
                    messageObjects.add(o);
                }
            }
            int loadIndex = lastLoadIndex++;
            waitingForLoad.add(loadIndex);
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, dialog_id, messageObjects.size(), messageObjects, false, 0, last_message_id, 0, 0, 2, true, classGuid, loadIndex, pinnedMessageIds.get(0), 0, MODE_PINNED);
        } else if (!forceHistoryEmpty) {
            loading = true;
        }
        if (isThreadChat() && !isTopic) {
            if (highlightMessageId == startLoadFromMessageId) {
                needSelectFromMessageId = true;
            }
        } else {
            getMessagesController().setLastCreatedDialogId(dialog_id, chatMode == MODE_SCHEDULED, true);
            if (chatMode == 0 || chatMode == MODE_SAVED) {
                if (currentEncryptedChat == null) {
                    getMediaDataController().loadBotKeyboard(MessagesStorage.TopicKey.of(dialog_id, getTopicId()));
                }
                getMessagesController().loadPeerSettings(currentUser, currentChat);

                if (startLoadFromMessageId == 0) {
                    SharedPreferences sharedPreferences = MessagesController.getNotificationsSettings(currentAccount);
                    int messageId = sharedPreferences.getInt("diditem" + NotificationsController.getSharedPrefKey(dialog_id, getTopicId()), 0);
                    if (messageId != 0) {
                        wasManualScroll = true;
                        loadingFromOldPosition = true;
                        startLoadFromMessageOffset = sharedPreferences.getInt("diditemo" + NotificationsController.getSharedPrefKey(dialog_id, getTopicId()), 0);
                        startLoadFromMessageId = messageId;
                    }
                } else {
                    showScrollToMessageError = true;
                    needSelectFromMessageId = true;
                }
            }
        }

        loadInfo = false;
        if (currentChat != null) {
            chatInfo = getMessagesController().getChatFull(currentChat.id);
            groupCall = getMessagesController().getGroupCall(currentChat.id, true);
            if (ChatObject.isChannel(currentChat) && !getMessagesController().isChannelAdminsLoaded(currentChat.id) && !ChatObject.isMonoForum(currentChat)) {
                getMessagesController().loadChannelAdmins(currentChat.id, true);
            }
            fillInviterId(false);
            if (chatMode != MODE_PINNED) {
                getMessagesStorage().loadChatInfo(currentChat.id, ChatObject.isChannel(currentChat), null, true, false, startLoadFromMessageId);
            }
            if (chatMode == 0 && chatInfo != null && ChatObject.isChannel(currentChat) && chatInfo.migrated_from_chat_id != 0 && !isThreadChat()) {
                mergeDialogId = -chatInfo.migrated_from_chat_id;
                maxMessageId[1] = chatInfo.migrated_from_max_id;
            }
            loadInfo = chatInfo == null;
            checkGroupCallJoin(false);
            gotChatInfo();
        } else if (currentUser != null) {
            if (chatMode != MODE_PINNED) {
                getMessagesController().loadUserInfo(currentUser, true, classGuid, startLoadFromMessageId);
            }
            loadInfo = userInfo == null;
        }

        if (forceHistoryEmpty) {
            endReached[0] = endReached[1] = true;
            forwardEndReached[0] = forwardEndReached[1] = true;
            firstLoading = false;
            loading = false;
            checkDispatchHideSkeletons(false);
        }
        if (chatMode != MODE_PINNED && !forceHistoryEmpty) {
            if (SharedConfig.deviceIsHigh()) {
                initialMessagesSize = (isThreadChat() && !isTopic) ? 30 : 25;
            } else {
                initialMessagesSize = (isThreadChat() && !isTopic) ? 20 : 15;
            }
            if (!waitingForGetDifference) {
                firstLoadMessages();
            }
        }

        if (chatMode == 0) {
            if (userId != 0 && currentUser.bot) {
                AndroidUtilities.runOnUIThread(()-> getMediaDataController().loadBotInfo(userId, userId, true, classGuid));
            } else if (chatInfo instanceof TLRPC.TL_chatFull) {
                for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                    TLRPC.ChatParticipant participant = chatInfo.participants.participants.get(a);
                    TLRPC.User user = getMessagesController().getUser(participant.user_id);
                    if (user != null && user.bot) {
                        getMediaDataController().loadBotInfo(user.id, -chatInfo.id, true, classGuid);
                    }
                }
            }
            if (AndroidUtilities.isTablet() && !isComments) {
                getNotificationCenter().postNotificationName(NotificationCenter.openedChatChanged, dialog_id, getTopicId(), false);
            }

            if (currentUser != null && !UserObject.isReplyUser(currentUser)) {
                userBlocked = getMessagesController().blockePeers.indexOfKey(currentUser.id) >= 0;
            }

            if (currentEncryptedChat != null && AndroidUtilities.getMyLayerVersion(currentEncryptedChat.layer) != SecretChatHelper.CURRENT_SECRET_CHAT_LAYER) {
                getSecretChatHelper().sendNotifyLayerMessage(currentEncryptedChat, null);
            }
        }
        if (chatInfo != null && chatInfo.linked_chat_id != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(chatInfo.linked_chat_id);
            if (chat != null && chat.megagroup) {
                getMessagesController().startShortPoll(chat, classGuid, false, null);
            }
        }

        if (currentUser != null) {
            TLRPC.UserFull userFull = getMessagesController().getUserFull(currentUser.id);
            if (userFull != null && userFull.theme != null) {
                ChatThemeController.getInstance(currentAccount).putThemeIfNeeded(userFull.theme);
            }
        }


        themeDelegate = parentThemeDelegate != null ? parentThemeDelegate : new ThemeDelegate();
        if (themeDelegate.isThemeChangeAvailable(false)) {
            globalObserversGroup.add(NotificationCenter.needSetDayNightTheme);
        }

        if (chatInvite != null) {
            int timeout = chatInvite.expires - getConnectionsManager().getCurrentTime();
            if (timeout < 0) {
                timeout = 10;
            }
            AndroidUtilities.runOnUIThread(chatInviteRunnable = () -> {
                chatInviteRunnable = null;
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), themeDelegate);
                if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                    builder.setMessage(getString(R.string.JoinByPeekChannelText));
                    builder.setTitle(getString(R.string.JoinByPeekChannelTitle));
                } else {
                    builder.setMessage(getString(R.string.JoinByPeekGroupText));
                    builder.setTitle(getString(R.string.JoinByPeekGroupTitle));
                }
                builder.setPositiveButton(getString(R.string.JoinByPeekJoin), (dialogInterface, i) -> {
                    if (bottomOverlayChatText != null) {
                        bottomOverlayChatText.callOnClick();
                    }
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), (dialogInterface, i) -> finishFragment());
                showDialog(builder.create());
            }, timeout * 1000L);
        }

        if (ChatObject.isMonoForum(currentChat)) {
            // reload balance if needed
            StarsController.getTonInstance(currentAccount).canUseTon();
        }

        if (isTopic || getMessagesController().isMonoForumWithManageRights(dialog_id) && getTopicId() != 0) {
            getMessagesController().getTopicsController().getTopicRepliesCount(dialog_id, getTopicId());
        }
        if (chatMode != MODE_EDIT_BUSINESS_LINK) {
            getMessagesController().getSavedMessagesController().preloadDialogs(false);
        }
        if (chatMode == MODE_SAVED) {
            getMessagesController().getSavedMessagesController().checkSavedDialogCount(getTopicId());
        }

        return true;
    }

    protected void updateSearchingHashtag(String hashtag) {
        if (chatMode != MODE_SEARCH || isFeedSearch()) {
            return;
        }
        if (!TextUtils.equals(searchingHashtag, hashtag)) {
            createSearchHashtagViewsIfNeeded();
            showMessagesSearchListView(true);
            searchingHashtag = hashtag;
            searchingQuery = searchingHashtag;
            checkHashtagStories(false);
            clearChatData(true);
            startMessageAppearTransitionMs = 0;
            firstMessagesLoaded = false;
            HashtagSearchController.getInstance(currentAccount).clearSearchResults(searchType);
            messagesSearchAdapter.notifyDataSetChanged();
            messagesSearchListView.requestLayout();
            if (messagesSearchListView.getLayoutManager() != null) {
                messagesSearchListView.getLayoutManager().scrollToPosition(0);
            }
            updateSearchListEmptyView();
            hashtagSearchEmptyView.showProgress(true);
            firstLoadMessages();
        }
    }

    public void resetForReload() {
        getConnectionsManager().cancelRequestsForGuid(classGuid);
        getMessagesStorage().cancelTasksForGuid(classGuid);
        classGuid = ConnectionsManager.generateClassGuid();

        startLoadFromMessageId = 0;
        firstMessagesLoaded = false;
        clearOnLoad = true;
        waitingForLoad.clear();
    }

    public void savePositionForTopicChange(long intoTopic) {
        if (chatListView == null || chatLayoutManager == null || chatLayoutManager.hasPendingScrollPosition()) {
            clearOnLoadAndScrollMessageId = -1;
            return;
        }
        int centerY = 0;//(chatListView.getHeight() / 2) - chatListView.getPaddingBottom() - chatListView.getPaddingTop();
        int top = 0;
        int messageId = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int i = chatListView.getChildCount() - 1; i >= 0; i--) {
            final View v = chatListView.getChildAt(i);
            final int vposition = chatListView.getChildAdapterPosition(v);
            if (vposition < 0) continue;
            if (v instanceof ChatMessageCell) {
                final MessageObject messageObject = ((ChatMessageCell) v).getMessageObject();
                if (messageObject == null || messageObject.getTopicId() != intoTopic)
                    continue;
                final int thisTop = getScrollingOffsetForView(v);
                final int distance = Math.abs(thisTop + centerY);
                if (distance < bestDistance) {
                    messageId = messageObject.getId();
                    top = thisTop;
                    bestDistance = distance;
                }
            }
        }
        clearOnLoadAndScrollMessageId = messageId;
        clearOnLoadAndScrollOffset = top;
    }

    public void firstLoadMessages() {
        if (firstMessagesLoaded) {
            return;
        }
        firstMessagesLoaded = true;
        final Runnable load = () -> {
            waitingForLoad.add(lastLoadIndex);
            if (chatMode == MODE_SEARCH) {
                if (isFeedSearch()) {
                    loadMoreFeedSearchResults();
                } else {
                    HashtagSearchController.getInstance(currentAccount).searchHashtag(searchingHashtag, classGuid, searchType, lastLoadIndex++);
                }
            } else if (startLoadFromDate != 0) {
                getMessagesController().loadMessages(dialog_id, mergeDialogId, false, 30, 0, startLoadFromDate, true, 0, classGuid, 4, 0, chatMode, threadMessageId, replyMaxReadId, lastLoadIndex++, isTopic);
            } else if (startLoadFromMessageId != 0 && (!isThreadChat() || startLoadFromMessageId == highlightMessageId || isTopic)) {
                startLoadFromMessageIdSaved = startLoadFromMessageId;
                if (migrated_to != 0) {
                    mergeDialogId = migrated_to;
                    getMessagesController().loadMessages(mergeDialogId, 0, loadInfo, initialMessagesSize, startLoadFromMessageId, 0, true, 0, classGuid, MessagesController.LOAD_AROUND_MESSAGE, 0, chatMode, threadMessageId, replyMaxReadId, lastLoadIndex++, isTopic);
                } else {
                    getMessagesController().loadMessages(dialog_id, mergeDialogId, loadInfo, initialMessagesSize, startLoadFromMessageId, 0, true, 0, classGuid, MessagesController.LOAD_AROUND_MESSAGE, 0, chatMode, threadMessageId, replyMaxReadId, lastLoadIndex++, isTopic);
                }
            } else {
                if (historyPreloaded) {
                    lastLoadIndex++;
                } else {
                    getMessagesController().loadMessages(dialog_id, mergeDialogId, loadInfo, initialMessagesSize, startLoadFromMessageId, 0, true, 0, classGuid, 2, 0, chatMode, threadMessageId, replyMaxReadId, lastLoadIndex++, isTopic);
                }
            }
            if ((chatMode == 0 || chatMode == MODE_SAVED && getSavedDialogId() == getUserConfig().getClientUserId()) && (!isThreadChat() || isTopic)) {
                waitingForLoad.add(lastLoadIndex);
                getMessagesController().loadMessages(dialog_id, mergeDialogId, false, 1, 0, 0, true, 0, classGuid, 2, 0, MODE_SCHEDULED, chatMode == MODE_SAVED ? 0 : threadMessageId, replyMaxReadId, lastLoadIndex++, isTopic);
            }
        };
        getMessagesController().checkSensitive(this, dialog_id, load, this::finishFragment);
    }

    private void fillInviterId(boolean load) {
        if (currentChat == null || chatInfo == null || ChatObject.isNotInChat(currentChat) || currentChat.creator) {
            return;
        }
        if (chatInfo.inviterId != 0) {
            chatInviterId = chatInfo.inviterId;
            return;
        }
        if (chatInfo.participants != null) {
            if (chatInfo.participants.self_participant != null) {
                chatInviterId = chatInfo.participants.self_participant.inviter_id;
                return;
            }
            long selfId = getUserConfig().getClientUserId();
            for (int a = 0, N = chatInfo.participants.participants.size(); a < N; a++) {
                TLRPC.ChatParticipant participant = chatInfo.participants.participants.get(a);
                if (participant.user_id == selfId) {
                    chatInviterId = participant.inviter_id;
                    return;
                }
            }
        }
        if (load && chatInviterId == 0) {
            getMessagesController().checkChatInviter(currentChat.id, false);
        }
    }

    private void hideUndoViews() {
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        if (pinBulletin != null) {
            pinBulletin.hide(false, 0);
        }
        if (topUndoView != null) {
            topUndoView.hide(true, 0);
        }
    }

    public int getOtherSameChatsDiff() {
        if (parentLayout == null || parentLayout.getFragmentStack() == null) {
            return 0;
        }
        int cur = parentLayout.getFragmentStack().indexOf(this);
        if (cur == -1) {
            cur = parentLayout.getFragmentStack().size();
        }
        int i = cur;
        for (int a = 0; a < parentLayout.getFragmentStack().size(); a++) {
            BaseFragment fragment = parentLayout.getFragmentStack().get(a);
            if (fragment != this && fragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) fragment;
                if (chatActivity.dialog_id == dialog_id) {
                    i = a;
                    break;
                }
            }
        }
        return i - cur;
    }

    @Override
    public void onBeginSlide() {
        super.onBeginSlide();

        if (selectionReactionsOverlay != null && selectionReactionsOverlay.isVisible()) {
            selectionReactionsOverlay.setHiddenByScroll(true);
        }
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(loadNextNewerFeedPage);
        AndroidUtilities.cancelRunOnUIThread(retryFailedFeedLoad);
        if (feedIntegration != null) {
            feedIntegration.destroy();
        }
        if (ayuForwardHandler != null) {
            if (ayuForwardHandler.isForwarding()) {
                ayuForwardHandler.detachFromFragment();
            } else {
                ayuForwardHandler.dispose();
            }
            ayuForwardHandler = null;
        }
        if (messageMetricsView != null) {
            messageMetricsView.finish();
        }
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        if (avatarContainer != null) {
            avatarContainer.onDestroy();
        }
        if (mentionContainer != null && mentionContainer.getAdapter() != null) {
            mentionContainer.getAdapter().onDestroy();
        }
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
        }
        ContentPreviewViewer.getInstance().clearDelegate(contentPreviewViewerDelegate);
        getNotificationCenter().onAnimationFinish(transitionAnimationIndex);
        NotificationCenter.getGlobalInstance().onAnimationFinish(transitionAnimationGlobalIndex);
        getNotificationCenter().onAnimationFinish(scrollAnimationIndex);
        getNotificationCenter().onAnimationFinish(scrollCallbackAnimationIndex);
        hideUndoViews();
        if (chatInviteRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(chatInviteRunnable);
            chatInviteRunnable = null;
        }

        // na: unread count
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);

        getNotificationCenter().removePostponeNotificationsCallback(postponeNotificationsWhileLoadingCallback);
        getMessagesController().setLastCreatedDialogId(dialog_id, chatMode == MODE_SCHEDULED, false);

        if (observersGroup != null) {
            observersGroup.removeAllObservers();
            observersGroup = null;
        }
        if (globalObserversGroup != null) {
            globalObserversGroup.removeAllObservers();
            globalObserversGroup = null;
        }

        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pluginMenuItemsUpdated);

        if (chatMode == 0 && AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.openedChatChanged, dialog_id, getTopicId(), true);
        }
        if (currentUser != null) {
            MediaController.getInstance().stopMediaObserver();
        }

        if (flagSecure != null) {
            flagSecure.detach();
        }
        if (currentUser != null) {
            getMessagesController().cancelLoadFullUser(currentUser.id);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), classGuid);
        if (chatAttachAlert != null) {
            chatAttachAlert.onDestroy();
        }
        AndroidUtilities.unlockOrientation(getParentActivity());
        if (ChatObject.isChannel(currentChat)) {
            getMessagesController().startShortPoll(currentChat, classGuid, true);
            if (chatInfo != null && chatInfo.linked_chat_id != 0) {
                TLRPC.Chat chat = getMessagesController().getChat(chatInfo.linked_chat_id);
                getMessagesController().startShortPoll(chat, classGuid, true);
            }
        }
        if (textSelectionHelper != null) {
            textSelectionHelper.clear();
        }
        if (chatListItemAnimator != null) {
            chatListItemAnimator.onDestroy();
        }
        if (pinchToZoomHelper != null) {
            pinchToZoomHelper.clear();
        }
        chatThemeBottomSheet = null;

        INavigationLayout parentLayout = getParentLayout();
        if (parentLayout != null && parentLayout.getFragmentStack() != null) {
            BackButtonMenu.clearPulledDialogs(this, parentLayout.getFragmentStack().indexOf(this) - (replacingChatActivity ? 0 : 1));
        }
        replacingChatActivity = false;

        if (progressDialogCurrent != null) {
            progressDialogCurrent.cancel();
            progressDialogCurrent = null;
        }
        chatMessagesMetadataController.onFragmentDestroy();
        if (birthdayAssetsFetcher != null) {
            birthdayAssetsFetcher.detach(true);
            birthdayAssetsFetcher = null;
        }
        if (starReactionsOverlay != null) {
            starReactionsOverlay.setMessageCell(null);
            AndroidUtilities.removeFromParent(starReactionsOverlay);
            starReactionsOverlay = null;
        }
    }

    private ArrayList<MessageObject> getSelectedMessages() {
        ArrayList<MessageObject> fmessages = new ArrayList<>();
        for (int a = 1; a >= 0; a--) {
            ArrayList<Integer> ids = new ArrayList<>();
            for (int b = 0; b < selectedMessagesIds[a].size(); b++) {
                ids.add(selectedMessagesIds[a].keyAt(b));
            }
            Collections.sort(ids);
            for (int b = 0; b < ids.size(); b++) {
                Integer id = ids.get(b);
                MessageObject messageObject = selectedMessagesIds[a].get(id);
                if (messageObject != null) {
                    fmessages.add(messageObject);
                }
            }
            selectedMessagesCanCopyIds[a].clear();
            selectedMessagesCanStarIds[a].clear();
            selectedMessagesIds[a].clear();
        }
        hideActionMode();
        updatePinnedMessageView(true);
        updateVisibleRows();
        return fmessages;
    }

    private ArrayList<MessageObject> getSelectedMessages1() {
        ArrayList<MessageObject> fmessages = new ArrayList<>();
        for (int a = 1; a >= 0; a--) {
            for (int b = 0; b < selectedMessagesIds[a].size(); b++) {
                MessageObject messageObject = selectedMessagesIds[a].get(selectedMessagesIds[a].keyAt(b));
                if (messageObject != null) {
                    fmessages.add(messageObject);
                }
            }
        }
        return fmessages;
    }

    private static class ChatActivityTextSelectionHelper extends TextSelectionHelper.ChatListTextSelectionHelper {
        ChatActivity chatActivity;
        public void setChatActivity(ChatActivity chatActivity) {
            cancelAllAnimators();
            clear();
            textSelectionOverlay = null;
            this.chatActivity = chatActivity;
        }

        @Override
        public int getParentTopPadding() {
            return chatActivity == null ? 0 : (int) chatActivity.chatListViewPaddingTop;
        }

        @Override
        public int getParentBottomPadding() {
            return chatActivity == null ? 0 : chatActivity.blurredViewBottomOffset;
        }

        @Override
        protected int getThemedColor(int key) {
            return Theme.getColor(key, chatActivity.themeDelegate);
        }

        @Override
        protected Theme.ResourcesProvider getResourcesProvider() {
            if (chatActivity != null) {
                return chatActivity.themeDelegate;
            }
            return null;
        }

        @Override
        protected boolean canShowQuote() {
            if (chatActivity != null && chatActivity.getDialogId() == UserObject.VERIFY) {
                return false;
            }
            if (selectedView != null && selectedView.getMessageObject() != null && selectedView.getMessageObject().isAyuDeleted()) return false;
            final boolean noforwards = (
                chatActivity != null && chatActivity.isPeerNoForwards() ||
                selectedView != null && selectedView.getMessageObject() != null && selectedView.getMessageObject().messageOwner != null && selectedView.getMessageObject().messageOwner.noforwards
            );
            return !isFactCheck && (
                chatActivity != null && chatActivity.getCurrentEncryptedChat() == null &&
                (selectedView == null ||
                    selectedView.getMessageObject() != null && selectedView.getMessageObject().type != MessageObject.TYPE_STORY &&
                    !selectedView.getMessageObject().isVoiceTranscriptionOpen() && !selectedView.getMessageObject().isInvoice() &&
                    selectedView.getMessageObject().richLayout == null &&
                    !chatActivity.textSelectionHelper.isDescription
                ) &&
                (!chatActivity.getMessagesController().getTranslateController().isTranslatingDialog(chatActivity.dialog_id) || (selectedView != null && selectedView.getMessageObject() != null && selectedView.getMessageObject().isOutOwner())) &&
                !UserObject.isService(chatActivity.dialog_id) &&
                (!noforwards || (chatActivity.getCurrentChat() == null || ChatObject.canWriteToChat(chatActivity.getCurrentChat())))
            );
        }

        @Override
        protected boolean canCopy() {
            if (chatActivity != null && chatActivity.getDialogId() == UserObject.VERIFY) {
                return true;
            }
            return chatActivity == null || !(selectedView != null && selectedView.getMessageObject() != null && selectedView.getMessageObject().messageOwner != null);
            /*return chatActivity == null || !(
                chatActivity.getDialogId() < 0 && chatActivity.getMessagesController().isPeerNoForwards(chatActivity.getDialogId()) ||
                selectedView != null && selectedView.getMessageObject() != null && (selectedView.getMessageObject().messageOwner != null && selectedView.getMessageObject().messageOwner.noforwards)
            );*/
        }

        @Override
        protected void onQuoteClick(MessageObject messageObject, int start, int end, CharSequence text) {
            if (messageObject == null) {
                return;
            }
            if (chatActivity != null) {
                end = Math.min(end, start + chatActivity.getMessagesController().quoteLengthMax);
                if (messageObject.getGroupId() != 0) {
                    MessageObject.GroupedMessages group = chatActivity.getGroup(messageObject.getGroupId());
                    if (group != null && !group.isDocuments) {
                        messageObject = group.captionMessage;
                    }
                }
                if (messageObject == null) {
                    return;
                }
                ReplyQuote quote = ReplyQuote.from(messageObject, start, end);
                if (quote.getText() == null) {
                    return;
                }
                if (chatActivity.chatActivityEnterView == null || chatActivity.chatActivityEnterView.getVisibility() != View.VISIBLE) {
                    chatActivity.replyingQuote = quote;
                    chatActivity.replyingMessageObject = messageObject;
                    chatActivity.forbidForwardingWithDismiss = false;
                    chatActivity.messagePreviewParams = new MessagePreviewParams(chatActivity.currentEncryptedChat != null, chatActivity.isPeerNoForwards(), ChatObject.isMonoForum(chatActivity.currentChat), noForwardQuote, noForwardCaption);
                    chatActivity.messagePreviewParams.updateReply(chatActivity.replyingMessageObject, chatActivity.getGroup(messageObject.getGroupId()), chatActivity.getDialogId(), chatActivity.replyingQuote);
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", DialogsActivity.DIALOGS_TYPE_FORWARD);
                    args.putBoolean("quote", true);
                    args.putInt("messagesCount", 1);
                    args.putBoolean("canSelectTopics", true);
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(chatActivity);
                    chatActivity.presentFragment(fragment);
                } else {
                    if (chatActivity.actionBar != null && chatActivity.actionBar.isActionModeShowed()) {
                        chatActivity.clearSelectionMode();
                    }
                    chatActivity.showFieldPanelForReplyQuote(messageObject, quote);
                    if (chatActivity.chatActivityEnterView != null) {
                        chatActivity.chatActivityEnterView.openKeyboard();
                    }
                }
            }
        }

        @Override
        protected boolean canShowAddToFilter() {
            if (chatActivity == null || !NaConfig.INSTANCE.getRegexFiltersEnabled().Bool()) return false;
            return !(chatActivity.getDialogId() == chatActivity.getUserConfig().getClientUserId());
        }

        @Override
        protected void onAddToFilterClick(String text) {
            if (chatActivity != null) {
                chatActivity.presentFragment(new RegexFilterEditActivity(chatActivity.getDialogId(), text));
            }
        }

        @Override
        protected void searchText(CharSequence text) {
            if (chatActivity != null) {
                chatActivity.hideActionMode();
                chatActivity.openSearchWithText(text == null ? "" : text.toString());
            }
        }
    }

    private Runnable justForTest;

    @Override
    public View createView(Context context) {
        Timer t = Timer.create("ChatActivity.createView");

        blurredBackgroundColorProvider = new BlurredBackgroundColorProviderThemed(themeDelegate, Theme.key_chat_messagePanelBackground) {
            @Override
            public int getBackgroundColor() {
                if (!BlurredBackgroundProviderImpl.checkBlurEnabled(currentAccount, themeDelegate)) {
                    return ColorUtils.setAlphaComponent(getThemedColor(Theme.key_chat_messagePanelBackground), 255);
                }

                final boolean isThemeLight = themeDelegate != null && !themeDelegate.isDark();
                if (isThemeLight) {
                    return ColorUtils.setAlphaComponent(super.getBackgroundColor(), 216);
                }
                return super.getBackgroundColor();
            }
        };
        blurredBackgroundColorProviderWhite = new BlurredBackgroundColorProviderThemed(themeDelegate, Theme.key_windowBackgroundWhite) {
            @Override
            public int getBackgroundColor() {
                if (!BlurredBackgroundProviderImpl.checkBlurEnabled(currentAccount, themeDelegate)) {
                    return ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundWhite), 255);
                }

                final boolean isThemeLight = themeDelegate != null && !themeDelegate.isDark();
                if (isThemeLight) {
                    return ColorUtils.setAlphaComponent(super.getBackgroundColor(), 216);
                }
                return super.getBackgroundColor();
            }
        };

        if (textSelectionHelper == null) {
            Timer.Task t1 = Timer.start(t, "new ChatActivityTextSelectionHelper");
            textSelectionHelper = new ChatActivityTextSelectionHelper();
            textSelectionHelper.setChatActivity(this);
            Timer.done(t1);
        }

        if (isReport()) {
            actionBar.setBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefault));
            actionBar.setItemsColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), false);
            actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), false);
            actionBar.setTitleColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon));
            actionBar.setSubtitleColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon));
        }
        if (isInsideContainer) {
            actionBar.setVisibility(View.GONE);
        }
        actionBarBackgroundPaint.setColor(getThemedColor(Theme.key_actionBarDefault));
        sharedResources = new ChatMessageSharedResources(context);

        //ArrayList<ChatMessageCell> chatMessagesCache = chatMessageCellsCache.get(currentAccount);
        //if (chatMessagesCache == null) {
        //    chatMessageCellsCache.put(currentAccount, chatMessagesCache = new ArrayList<>());
        //}
        //if (chatMessagesCache.size() < 10) {
        //    int n = 15 - chatMessagesCache.size();
        //    Timer.Task t2 = Timer.start(t, "create ChatMessageCell n=" + n);
        //    for (int a = 0; a < n; a++) {
        //        chatMessagesCache.add(new ChatMessageCell(context, currentAccount,true, sharedResources, themeDelegate));
        //    }
        //    Timer.done(t2);
        //}
        for (int a = 1; a >= 0; a--) {
            selectedMessagesIds[a].clear();
            selectedMessagesCanCopyIds[a].clear();
            selectedMessagesCanStarIds[a].clear();
        }
        scheduledOrNoSoundHint = null;
        scheduledHint = null;
        infoTopView = null;
        aspectRatioFrameLayout = null;
        videoTextureView = null;
        mediaBanTooltip = null;
        noSoundHintView = null;
        forwardHintView = null;
        checksHintView = null;
        textSelectionHint = null;
        emojiButtonRed = null;
        gifHintTextView = null;
        emojiHintTextView = null;
        pollHintView = null;
        timerHintView = null;
        videoPlayerContainer = null;
        voiceHintTextView = null;
        blurredView = null;
        dummyMessageCell = null;
        cantDeleteMessagesCount = 0;
        canEditMessagesCount = 0;
        cantForwardMessagesCount = 0;
        canForwardMessagesCount = 0;
        cantSaveMessagesCount = 0;
        canSaveMusicCount = 0;
        canSaveDocumentsCount = 0;

        hasOwnBackground = true;
        if (chatAttachAlert != null) {
            try {
                if (chatAttachAlert.isShowing()) {
                    chatAttachAlert.dismiss();
                }
            } catch (Exception ignore) {

            }
            chatAttachAlert.onDestroy();
            chatAttachAlert = null;
        }

        Theme.createChatResources(context, false);

        actionBar.setAddToContainer(false);
        actionBar.setCastShadows(false);
        actionBar.setBackground(null);
        // actionBar.setOccupyStatusBar(false);
        if (inPreviewMode) {
            actionBar.setBackButtonDrawable(null);
        } else {
            actionBar.setBackButtonDrawable(new BackDrawable(isReport()));
        }

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(final int id) {
                if (id == -1) {
                    if (isInPollAddOptionMode()) {
                        pollAddOptionModeClose();
                    } else if (actionBar.isActionModeShowed()) {
                        clearSelectionMode();
                    } else {
                        if (chatMode == MODE_QUICK_REPLIES && (messages.isEmpty() || threadMessageId == 0)) {
                            showQuickRepliesRemoveAlert();
                            return;
                        }
                        if (chatMode == MODE_EDIT_BUSINESS_LINK && chatActivityEnterView.businessLinkHasChanges()) {
                            showBusinessLinksDiscardAlert(() -> {
                                finishFragment();
                            });
                            return;
                        }
                        if (!checkRecordLocked(true, true)) {
                            finishFragment();
                        }
                    }
                } else if (handlePluginChatActionMenuOption(id)) {
                    return;
                } else if (id == view_as_topics) {
                    if (getUserConfig().getClientUserId() == dialog_id) {
                        getMessagesController().setSavedViewAs(true);
                        avatarContainer.openProfile(false, true, true);
                    } else {
                        getMessagesController().getTopicsController().toggleViewForumAsMessages(-dialog_id, false);
                        TopicsFragment.prepareToSwitchAnimation(ChatActivity.this);
                    }
                } else if (id == copy) {
                    SpannableStringBuilder str = new SpannableStringBuilder();
                    long previousUid = 0;
                    for (int a = 1; a >= 0; a--) {
                        ArrayList<Integer> ids = new ArrayList<>();
                        for (int b = 0; b < selectedMessagesCanCopyIds[a].size(); b++) {
                            ids.add(selectedMessagesCanCopyIds[a].keyAt(b));
                        }
                        if (currentEncryptedChat == null) {
                            Collections.sort(ids);
                        } else {
                            Collections.sort(ids, Collections.reverseOrder());
                        }
                        for (int b = 0; b < ids.size(); b++) {
                            Integer messageId = ids.get(b);
                            MessageObject messageObject = selectedMessagesCanCopyIds[a].get(messageId);
                            if (str.length() != 0) {
                                str.append("\n\n");
                            }
                            str.append(getMessageContent(messageObject, previousUid, ids.size() != 1 && (currentUser == null || !currentUser.self)));
                            previousUid = messageObject.getFromChatId();
                        }
                    }
                    if (str.length() != 0) {
                        AndroidUtilities.addToClipboard(str);
                        createUndoView();
                        undoView.showWithAction(0, UndoView.ACTION_TEXT_COPIED, null);
                    }
                    clearSelectionMode();
                } else if (id == combine_message) {
                    StringBuilder str = new StringBuilder();
                    ArrayList<Integer> toDeleteMessagesIds = new ArrayList<>();
                    MessageObject replyTo = getThreadMessage();
                    ArrayList<Character> suffice_en = new ArrayList(Arrays.asList(',', '.', '!', '?', ':', ';', '(', ')'));
                    ArrayList<Character> suffice_zh = new ArrayList(Arrays.asList('，', '。', '！', '？', '：', '；', '（', '）'));
                    for (int a = 1; a >= 0; a--) {
                        ArrayList<Integer> ids = new ArrayList<>();
                        for (int b = 0; b < selectedMessagesCanCopyIds[a].size(); b++) {
                            ids.add(selectedMessagesCanCopyIds[a].keyAt(b));
                        }
                        if (currentEncryptedChat == null) {
                            Collections.sort(ids);
                        } else {
                            Collections.sort(ids, Collections.reverseOrder());
                        }
                        for (int b = 0; b < ids.size(); b++) {
                            Integer messageId = ids.get(b);
                            MessageObject messageObject = selectedMessagesCanCopyIds[a].get(messageId);
                            if (b == 0 && NaConfig.INSTANCE.getCombineMessage().Int() == 0) {
                                replyTo = messageObject.replyMessageObject;
                            }
                            if (str.length() != 0) {
                                if (!suffice_en.contains(str.charAt(str.length() - 1)) && !suffice_zh.contains(str.charAt(str.length() - 1))) {
                                    // add comma refer to language
                                    if (LocaleController.getInstance().getCurrentLocale().getLanguage().equals("zh")) {
                                        str.append('，');
                                    } else {
                                        str.append(',');
                                    }
                                }
                            }
                            str.append(messageObject.messageText);
                            if (messageObject.getSenderId() == UserConfig.getInstance(currentAccount).getClientUserId()) {
                                toDeleteMessagesIds.add(messageId);
                            }
                        }
                    }
                    if (str.length() != 0) {
                        SendMessagesHelper.getInstance(currentAccount)
                                .sendMessage(str.toString(), dialog_id, replyTo, getThreadMessage(), null, false, null, null, null, !AyuGhostController.getInstance(UserConfig.selectedAccount).isSendWithoutSound(), 0, 0, null, false);
                        MessagesController.getInstance(currentAccount).deleteMessages(toDeleteMessagesIds, null, null, dialog_id, 0, true, MODE_DEFAULT);
                    }
                    clearSelectionMode();
                } else if (id == delete) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    createDeleteMessagesAlert(null, null);
                } else if (id == forward) {
                    advancedForwardTexts = null;
                    noForwardQuote = id == nkbtn_forward_noquote;
                    noForwardCaption = id == nkbtn_forward_nocaption;
                    if (messagePreviewParams != null) {
                        messagePreviewParams.setHideForwardSendersName(noForwardQuote);
                        messagePreviewParams.hideCaption = noForwardCaption;
                    }
                    openForward(true);
                } else if (id == share) {
                    share();
                } else if (id == open_direct) {
                    if (currentChat == null) return;
                    presentFragment(ChatActivity.of(-currentChat.linked_monoforum_id));
                } else if (id == charge_fee ) {
                    long user_id = dialog_id;
                    long parent_id = 0;
                    if (ChatObject.isMonoForum(currentChat) && ChatObject.canManageMonoForum(currentAccount, currentChat)) {
                        user_id = getThreadId();
                        parent_id = dialog_id;
                    }
                    StarsController.getInstance(currentAccount).stopPaidMessages(user_id, parent_id, false, false);
                } else if (id == remove_fee) {
                    long _user_id = dialog_id;
                    long _parent_id = 0;
                    if (ChatObject.isMonoForum(currentChat) && ChatObject.canManageMonoForum(currentAccount, currentChat)) {
                        _user_id = getThreadId();
                        _parent_id = dialog_id;
                    }
                    final long user_id = _user_id;
                    final long parent_id = _parent_id;
                    StarsController.getInstance(currentAccount).getPaidRevenue(user_id, parent_id, revenue -> {
                        if (getContext() == null) return;
                        AlertsCreator.showAlertWithCheckboxWithBalance(
                            getContext(),
                            getString(R.string.RemoveMessageFeeTitle),
                            AndroidUtilities.replaceTags(formatString(ChatObject.isMonoForum(currentChat) ? R.string.RemoveMessageFeeMessageChannel : R.string.RemoveMessageFeeMessage, DialogObject.getShortName(user_id))),
                            revenue > 0 ? formatPluralStringComma("RemoveMessageFeeRefund", (int) (long) revenue) : null,
                            getString(R.string.Confirm),
                            refund -> StarsController.getInstance(currentAccount).stopPaidMessages(user_id, parent_id, revenue > 0 && refund, true),
                            resourceProvider
                        );
                    });
                } else if (id == tag_message) {
                    if (tagSelector == null) {
                        showTagSelector();
                    } else {
                        hideTagSelector();
                    }
                } else if (id == save_to) {
                    ArrayList<MessageObject> messageObjects = new ArrayList<>();
                    for (int a = 1; a >= 0; a--) {
                        for (int b = 0; b < selectedMessagesIds[a].size(); b++) {
                            messageObjects.add(selectedMessagesIds[a].valueAt(b));
                        }
                        selectedMessagesIds[a].clear();
                        selectedMessagesCanCopyIds[a].clear();
                        selectedMessagesCanStarIds[a].clear();
                    }
                    boolean isMusic = canSaveMusicCount > 0;
                    hideActionMode();
                    updatePinnedMessageView(true);
                    updateVisibleRows();
                    MediaController.saveFilesFromMessages(getParentActivity(), getAccountInstance(), messageObjects, (count) -> {
                        if (count > 0) {
                            if (getParentActivity() == null) {
                                return;
                            }
                            BulletinFactory.of(ChatActivity.this).createDownloadBulletin(isMusic ? BulletinFactory.FileType.AUDIOS : BulletinFactory.FileType.UNKNOWNS, count, themeDelegate).show();
                        }
                    });
                } else if (id == chat_enc_timer) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    showDialog(AlertsCreator.createTTLAlert(getParentActivity(), currentEncryptedChat, themeDelegate).create());
                } else if (id == clear_history || id == delete_chat || id == auto_delete_timer) {
                    if (getParentActivity() == null) {
                        return;
                    }

                    if (id == clear_history && ChatObject.isMonoForum(currentChat)) {
                        if (getThreadId() != 0) {
                            final TLRPC.User user = getMessagesController().getUser(getThreadId());
                            if (user != null) {
                                AlertsCreator.createClearDaysDialogAlert(ChatActivity.this, -1, user, currentChat, true, revoke -> {
                                    if (user.id != getThreadId()) {
                                        return;
                                    }
                                    performHistoryClear(false, true);
                                }, getResourceProvider());
                            }
                        }
                        return;
                    }

                    boolean canDeleteHistory = chatInfo != null && chatInfo.can_delete_channel;
                    if (id == auto_delete_timer || id == clear_history && currentEncryptedChat == null && ((currentUser != null && !UserObject.isUserSelf(currentUser) && !UserObject.isDeleted(currentUser)) || (chatInfo != null && chatInfo.can_delete_channel))) {
                        AlertsCreator.createClearDaysDialogAlert(ChatActivity.this, -1, currentUser, currentChat, canDeleteHistory, new MessagesStorage.BooleanCallback() {
                            @Override
                            public void run(boolean revoke) {
                                if (revoke && (currentUser != null || canDeleteHistory)) {
                                    getMessagesStorage().getMessagesCount(dialog_id, (count) -> {
                                        if (count >= 50) {
                                            AlertsCreator.createClearOrDeleteDialogAlert(ChatActivity.this, true, currentChat, currentUser, false, false, false, canDeleteHistory, (param) -> performHistoryClear(true, canDeleteHistory));
                                        } else {
                                            performHistoryClear(true, canDeleteHistory);
                                        }
                                    });
                                } else {
                                    performHistoryClear(revoke, canDeleteHistory);
                                }
                            }
                        }, getResourceProvider());
                        return;
                    }
                    AlertsCreator.createClearOrDeleteDialogAlert(ChatActivity.this, id == clear_history, currentChat, currentUser, currentEncryptedChat != null, true, false, canDeleteHistory, (param) -> {
                        if (id == clear_history && ChatObject.isChannel(currentChat) && (!currentChat.megagroup || ChatObject.isPublic(currentChat))) {
                            getMessagesController().deleteDialog(dialog_id, 2, param);
                        } else {
                            if (id != clear_history) {
                                getNotificationCenter().removeObserver(ChatActivity.this, NotificationCenter.closeChats);
                                getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                                finishFragment();
                                getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, dialog_id, currentUser, currentChat, param);
                            } else {
                                performHistoryClear(param, canDeleteHistory);
                            }
                        }
                    });
                } else if (id == share_contact) {
                    if (currentUser == null || getParentActivity() == null) {
                        return;
                    }
                    if (addToContactsButton != null && addToContactsButton.getTag() != null) {
                        shareMyContact((Integer) addToContactsButton.getTag(), null);
                    } else {
                        Bundle args = new Bundle();
                        args.putLong("user_id", currentUser.id);
                        args.putBoolean("addContact", true);
                        presentFragment(new ContactAddActivity(args));
                    }
                } else if (id == mute) {
                    toggleMute(false);
                } else if (id == add_shortcut) {
                    try {
                        getMediaDataController().installShortcut(currentUser.id, MediaDataController.SHORTCUT_TYPE_USER_OR_CHAT);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (id == to_the_beginning) {
                    scrollToMessageId(1, 0, false, 0, true, 0);
                } else if (id == to_the_message){
                    setScrollToMessage();
                } else if (id == boost_group) {
                    if (ChatObject.hasAdminRights(currentChat)) {
                        BoostsActivity boostsActivity = new BoostsActivity(dialog_id);
                        boostsActivity.setBoostsStatus(boostsStatus);
                        presentFragment(boostsActivity);
                    } else {
                        getNotificationCenter().postNotificationName(NotificationCenter.openBoostForUsersDialog, dialog_id);
                    }
                } else if (id == report) {
                    ReportBottomSheet.openChat(ChatActivity.this);
                } else if (id == star) {
                    for (int a = 0; a < 2; a++) {
                        for (int b = 0; b < selectedMessagesCanStarIds[a].size(); b++) {
                            MessageObject msg = selectedMessagesCanStarIds[a].valueAt(b);
                            getMediaDataController().addRecentSticker(MediaDataController.TYPE_FAVE, msg, msg.getDocument(), (int) (System.currentTimeMillis() / 1000), !hasUnfavedSelected);
                        }
                    }
                    clearSelectionMode();
                } else if (id == edit) {
                    MessageObject messageObject = null;
                    for (int a = 1; a >= 0; a--) {
                        if (messageObject == null && selectedMessagesIds[a].size() == 1) {
                            ArrayList<Integer> ids = new ArrayList<>();
                            for (int b = 0; b < selectedMessagesIds[a].size(); b++) {
                                ids.add(selectedMessagesIds[a].keyAt(b));
                            }
                            messageObject = messagesDict[a].get(ids.get(0));
                        }
                        selectedMessagesIds[a].clear();
                        selectedMessagesCanCopyIds[a].clear();
                        selectedMessagesCanStarIds[a].clear();
                    }
                    if (messageObject != null && messageObject.isTodo()) {
                        selectedObject = messageObject;
                        processSelectedOption(OPTION_EDIT_TODO);
                    } else {
                        startEditingMessageObject(messageObject);
                    }
                    hideActionMode();
                    updatePinnedMessageView(true);
                    updateVisibleRows();
                } else if (id == edit_quick_reply) {
                    QuickRepliesController.QuickReply currentQuickReply = QuickRepliesController.getInstance(currentAccount).findReply(getQuickReplyId());
                    QuickRepliesActivity.openRenameReplyAlert(getContext(), currentAccount, quickReplyShortcut, currentQuickReply, getResourceProvider(), false, name -> {
                        if (currentQuickReply != null) {
                            QuickRepliesController.getInstance(currentAccount).renameReply(currentQuickReply.id, name);
                        }
                        quickReplyShortcut = name;
                        avatarContainer.setTitle(name);
                    });
                } else if (id == chat_menu_attach) {
                    ActionBarMenuSubItem attach = new ActionBarMenuSubItem(context, false, true, true, getResourceProvider());
                    attach.setTextAndIcon(LocaleController.getString(R.string.AttachMenu), R.drawable.input_attach);
                    attach.setOnClickListener(view -> {
                        headerItem.closeSubMenu();
                        if (chatAttachAlert != null) {
                            chatAttachAlert.setEditingMessageObject(0, null);
                        }
                        openAttachMenu();
                    });
                    headerItem.toggleSubMenu(attach, attachItem.createView());
                } else if (id == bot_help) {
                    getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of("/help", dialog_id, null, null, null, false, null, null, null, true, 0, 0, null, false));
                } else if (id == bot_settings) {
                    getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of("/settings", dialog_id, null, null, null, false, null, null, null, true, 0, 0, null, false));
                } else if (id == search) {
                    openSearchWithText(isSupportedTags() ? "" : null);
                } else if (id == translate) {
                    getMessagesController().getTranslateController().setHideTranslateDialog(getDialogId(), false, true);
                    if (!getMessagesController().getTranslateController().toggleTranslatingDialog(getDialogId(), true)) {
                        updateTopPanel(true);
                    }
                } else if (id == call || id == video_call) {
                    if (currentUser != null && getParentActivity() != null) {
                        VoIPHelper.startCall(currentUser, id == video_call, userInfo != null && userInfo.video_calls_available, getParentActivity(), getMessagesController().getUserFull(currentUser.id), getAccountInstance());
                    }
                } else if (id == text_bold) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedBold();
                    }
                } else if (id == text_italic) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedItalic();
                    }
                } else if (id == text_spoiler) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedSpoiler();
                    }
                } else if (id == text_quote) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedQuote();
                    }
                } else if (id == text_mono) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedMono();
                    }
                } else if (id == text_code) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedCode();
                    }
                } else if (id == text_strike) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedStrike();
                    }
                } else if (id == text_underline) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedUnderline();
                    }
                } else if (id == text_date) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedDate();
                    }
                } else if (id == text_link) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedUrl();
                    }
                } else if (id == text_regular) {
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedRegular();
                    }
                } else if (id == text_mention) {
                    if (chatActivityEnterView != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedMention();
                    }
                } else if (id == text_transalte) {
                    if (chatActivityEnterView != null) {
                        chatActivityEnterView.getEditField().setSelectionOverride(editTextStart, editTextEnd);
                        chatActivityEnterView.getEditField().makeSelectedTranslate();
                    }
                } else if (id == change_colors) {
                    showChatThemeBottomSheet();
                } else if (id == topic_close) {
                    if (forumTopic == null)
                        return;
                    getMessagesController().getTopicsController().toggleCloseTopic(currentChat.id, forumTopic.id, forumTopic.closed = true);
                    updateTopicButtons();
                    updateBottomOverlay();
                    updateTopPanel(true);
                } else if (id == open_forum) {
                    TopicsFragment.prepareToSwitchAnimation(ChatActivity.this);
//                    Bundle bundle = new Bundle();
//                    bundle.putLong("chat_id", -dialog_id);
//                    presentFragment(new TopicsFragment(bundle));
                } else if (id == copy_business_link) {
                    AndroidUtilities.addToClipboard(businessLink.link);
                    BulletinFactory.of(LaunchActivity.getLastFragment()).createCopyLinkBulletin().show();
                } else if (id == share_business_link) {
                    Runnable shareTask = () -> {
                        Intent intent = new Intent(getContext(), LaunchActivity.class);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, businessLink.link);
                        startActivityForResult(intent, 500);
                    };
                    if (chatActivityEnterView.businessLinkHasChanges()) {
                        showBusinessLinksDiscardAlert(shareTask);
                    } else {
                        shareTask.run();
                    }
                } else if (id == rename_business_link) {
                    BusinessLinksActivity.openRenameAlert(getContext(), currentAccount, businessLink, resourceProvider, false);
                } else if (id == delete_business_link) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext(), getResourceProvider())
                            .setTitle(getString(R.string.BusinessLinksDeleteTitle))
                            .setMessage(getString(R.string.BusinessLinksDeleteMessage))
                            .setPositiveButton(getString(R.string.Remove), (di, w) -> {
                                finishFragment();
                                getNotificationCenter().postNotificationName(NotificationCenter.needDeleteBusinessLink, businessLink);
                            })
                            .setNegativeButton(getString(R.string.Cancel), null)
                            .create();
                    showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (button != null) {
                        button.setTextColor(getThemedColor(Theme.key_text_RedBold));
                    }
                } else if (id == chat_menu_topic_create) {
                    presentFragment(TopicCreateFragment.create(-dialog_id, 0).setOpenInChatActivity(ChatActivity.this));
                } else if (id == 888) {
                    dumpCanvas();
                } else if (id == 889) {
                    sendDebugRichMessage();
                } else {
                    nkbtn_onclick_actionbar(id);
                }
            }
        });
        View backButton = actionBar.getBackButton();
        backButton.setOnTouchListener(new LongPressListenerWithMovingGesture() {
            @Override
            public void onLongPress() {
                scrimPopupWindow = BackButtonMenu.show(ChatActivity.this, backButton, dialog_id, getTopicId(), themeDelegate);
                if (scrimPopupWindow != null) {
                    setSubmenu(scrimPopupWindow);
                    scrimPopupWindow.setOnDismissListener(() -> {
                        setSubmenu(null);
                        scrimPopupWindow = null;
                        menuDeleteItem = null;
                        scrimPopupWindowItems = null;
                        chatLayoutManager.setCanScrollVertically(true);
                        if (scrimPopupWindowHideDimOnDismiss) {
                            dimBehindView(false);
                        } else {
                            scrimPopupWindowHideDimOnDismiss = true;
                        }
                        if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                            chatActivityEnterView.getEditField().setAllowDrawCursor(true);
                        }
                    });
                    chatListView.stopScroll();
                    chatLayoutManager.setCanScrollVertically(false);
                    dimBehindView(backButton, 0.3f);
                    hideHints(false);
                    if (topUndoView != null) {
                        topUndoView.hide(true, 1);
                    }
                    if (undoView != null) {
                        undoView.hide(true, 1);
                    }
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setAllowDrawCursor(false);
                    }
                }
            }
        });
        actionBar.setInterceptTouchEventListener((view, motionEvent) -> {
            if (chatThemeBottomSheet != null) {
                chatThemeBottomSheet.close();
                return true;
            }
            return false;
        });

        topPanelLayout = new ChatActivityTopPanelLayout(context);
        topPanelLayout.setOnAnimatedHeightChangedListener(() -> {
            invalidateChatListViewTopPadding();
            invalidateMessagesVisiblePart();
            checkUi_messagesSearchListPadding();
            checkUi_topFade();
        });
        if (avatarContainer != null) {
            avatarContainer.onDestroy();
        }
        avatarContainer = new ChatAvatarContainer(context, this, currentEncryptedChat != null, themeDelegate) {
            @Override
            protected boolean onAvatarClick() {
                if (isTitleCentered()) {
                    if (editTextItem != null && editTextItem.getTag() != null) {
                        checkEditTextItemMenu();
                        editTextItem.createView().performClick();
                        return true;
                    }
                    if (headerItem != null) {
                        if (attachItem != null && chatActivityEnterView.hasText() && TextUtils.isEmpty(chatActivityEnterView.getSlowModeTimer()) && (currentChat == null || ChatObject.canSendPlain(currentChat))) {
                            attachItem.createView().performClick();
                            return true;
                        }
                        headerItem.performClick();
                        return true;
                    }
                } else if (currentUser != null && currentUser.linked_community_id != 0) {
                    showDialog(new CommunitySheet(ChatActivity.this, currentUser.linked_community_id));
                    return true;
                } else if (currentChat != null && currentChat.linked_community_id != 0) {
                    showDialog(new CommunitySheet(ChatActivity.this, currentChat.linked_community_id));
                    return true;
                }
                return super.onAvatarClick();
            }

            @Override
            protected boolean useAnimatedSubtitle() {
                return chatMode == MODE_SAVED;
            }

            @Override
            protected boolean canSearch() {
                return !isInsideContainer && !isInPreviewMode() && !inBubbleMode && searchItem != null && !searching && (!isThreadChat() || isTopic);
            }

            @Override
            protected void openSearch() {
                openSearchWithText(isSupportedTags() ? "" : null);
            }

            @Override
            protected boolean isCentered() {
                return isTitleCentered();
            }

            @Override
            protected boolean isPreviewMode() {
                return isInPreviewMode();
            }
        };
        avatarContainer.setGlassMode();
        avatarContainer.allowShorterStatus = true;
        avatarContainer.premiumIconHiddable = true;
        avatarContainer.allowDrawStories = dialog_id < 0 && !isTopic;
        avatarContainer.setClipChildren(false);
        updateTopicTitleIcon();
        if (inPreviewMode || inBubbleMode || isInsideContainer) {
            avatarContainer.setOccupyStatusBar(false);
        }
        if (isReport()) {
            actionBar.setTitle(reportTitle);
            actionBar.setSubtitle(getString(R.string.ReportSelectMessages));
        } else if (startLoadFromDate != 0) {
            final int date = startLoadFromDate;
            actionBar.setOnClickListener((v) -> {
                jumpToDate(date);
            });
            actionBar.setTitle(LocaleController.formatDateChat(startLoadFromDate, false));
            actionBar.setSubtitle(getString(R.string.Loading));

            TLRPC.TL_messages_getHistory gh1 = new TLRPC.TL_messages_getHistory();
            gh1.peer = getMessagesController().getInputPeer(dialog_id);
            gh1.offset_date = startLoadFromDate;
            gh1.limit = 1;
            gh1.add_offset = -1;

            int req = getConnectionsManager().sendRequest(gh1, (response, error) -> {
                if (response instanceof TLRPC.messages_Messages) {
                    List<TLRPC.Message> l = ((TLRPC.messages_Messages) response).messages;
                    if (!l.isEmpty()) {

                        TLRPC.TL_messages_getHistory gh2 = new TLRPC.TL_messages_getHistory();
                        gh2.peer = getMessagesController().getInputPeer(dialog_id);
                        gh2.offset_date = startLoadFromDate + 60 * 60 * 24;
                        gh2.limit = 1;

                        getConnectionsManager().sendRequest(gh2, (response1, error1) -> {
                            if (response1 instanceof TLRPC.messages_Messages) {
                                List<TLRPC.Message> l2 = ((TLRPC.messages_Messages) response1).messages;
                                int count = 0;
                                if (!l2.isEmpty()) {
                                    count = ((TLRPC.messages_Messages) response).offset_id_offset - ((TLRPC.messages_Messages) response1).offset_id_offset;
                                } else {
                                    count = ((TLRPC.messages_Messages) response).offset_id_offset;
                                }
                                int finalCount = count;
                                AndroidUtilities.runOnUIThread(() -> {
                                    if (finalCount != 0) {
                                        AndroidUtilities.runOnUIThread(() -> actionBar.setSubtitle(LocaleController.formatPluralString("messages", finalCount)));
                                    } else {
                                        actionBar.setSubtitle(getString(R.string.NoMessagesForThisDay));
                                    }
                                });
                            }
                        });
                    } else {
                        actionBar.setSubtitle(getString(R.string.NoMessagesForThisDay));
                    }
                }
            });
            getConnectionsManager().bindRequestToGuid(req, classGuid);
        } else {
            actionBar.addView(avatarContainer, 0, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, !inPreviewMode ? 52 : 0, 0, 52, 0));
            actionBar.createMenu().bringToFront();
        }
        actionBar.setOnActionModeFactorChangeListener(() -> {
            checkUi_avatarContainerVisibility();
        });

        detachPluginChatActionMenuWrapper();
        ActionBarMenu menu = actionBar.createMenu();
        pluginChatActionMenuItemsByOption.clear();
        menu.setCenteredTitle(isTitleCentered());

        if (isThreadChat() && threadMessageId != 0 && !isTopic) {
            viewInChatItem = menu.addItem(nkbtn_view_in_chat, R.drawable.msg_viewreplies);
        }
        if (chatMode == MODE_QUICK_REPLIES && !QuickRepliesController.isSpecial(quickReplyShortcut)) {
            menu.addItem(edit_quick_reply, R.drawable.group_edit).setContentDescription(LocaleController.getString(R.string.Edit));
        }

        if (UserObject.isBotForumWithEditableTopics(currentUser) && chatMode == 0) {
            topicCreateItem = menu.addItem(chat_menu_topic_create, R.drawable.menu_topic_add_30);
        }

        if (currentEncryptedChat == null && (chatMode == 0 || chatMode == MODE_SAVED || chatMode == MODE_SUGGESTIONS) && !isReport()) {
            searchIconItem = menu.addItem(search, isSupportedTags() ? R.drawable.navbar_search_tag : R.drawable.outline_header_search);
            searchIconItem.setContentDescription(LocaleController.getString(R.string.Search));
            searchItem = menu.addItem(chat_menu_search, R.drawable.outline_header_search, themeDelegate);
            searchItem.setSearchPaddingStart(7);
            searchItem.setIsSearchField(true);
            searchItem.setActionBarMenuItemSearchListener(getSearchItemListener());
            searchItem.setSearchFieldHint(isSupportedTags() ? LocaleController.getString(R.string.SavedTagSearchHint) : LocaleController.getString(R.string.Search));
            if (chatMode == MODE_SAVED || chatMode == MODE_SUGGESTIONS || threadMessageId == 0 && !UserObject.isReplyUser(currentUser) || threadMessageObject != null && threadMessageObject.getRepliesCount() < 10) {
                searchItem.setVisibility(View.GONE);
            } else {
                searchItem.setVisibility(View.VISIBLE);
            }
            searchItemVisible = false;
        }

        if (chatMode == 0 && (threadMessageId == 0 || isTopic) && !UserObject.isReplyUser(currentUser) && !isReport() && !isTitleCentered()) {
            TLRPC.UserFull userFull = null;
            if (currentUser != null) {
                audioCallIconItem = menu.lazilyAddItem(call, R.drawable.call, themeDelegate);
                audioCallIconItem.setContentDescription(LocaleController.getString(R.string.Call));
                userFull = getMessagesController().getUserFull(currentUser.id);
                if (userFull != null && userFull.phone_calls_available) {
                    showAudioCallAsIcon = !inPreviewMode;
                    audioCallIconItem.setVisibility(View.VISIBLE);
                } else {
                    showAudioCallAsIcon = false;
                    audioCallIconItem.setVisibility(View.GONE);
                }
            }
        }
        /*
        Choreographer60FpsContent.getInstance().addFrameCallback(justForTest = () -> {
            if (audioCallIconItem != null) {
                showAudioCallAsIcon = !showAudioCallAsIcon;
                audioCallIconItem.setVisibility(!showAudioCallAsIcon ? View.GONE : View.VISIBLE);
            }
        }, 1);
        */

        editTextItem = menu.lazilyAddItem(chat_menu_edit_text_options, R.drawable.ic_ab_other, themeDelegate);
        editTextItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
        editTextItem.setTag(null);
        editTextItem.setVisibility(View.GONE);

        otherIcon = new ComposeDrawable(
            context.getResources().getDrawable(R.drawable.ic_ab_other).mutate(),
            context.getResources().getDrawable(R.drawable.mini_attach).mutate()
        );
        otherIcon.setIconTranslate(-dp(6), dp(6.66f));

        if (((chatMode == 0 && (threadMessageId == 0 || isTopic)) || chatMode == MODE_SUGGESTIONS) && !UserObject.isReplyUser(currentUser) && !isReport()) {
            TLRPC.UserFull userFull = null;
            if (currentUser != null) {
                userFull = getMessagesController().getUserFull(currentUser.id);
            }
            headerItem = menu.addItem(chat_menu_options, otherIcon);
            headerItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() {
                @Override
                public void onShowSubMenu() {
                    updateScrimSourceBitmap();
                }

                @Override
                public void onHideSubMenu() {

                }
            });
            otherIcon.addView(headerItem.getIconView());
            headerItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            if (avatarContainer != null) {
                avatarContainer.setAvatarOptionsMenuItem(headerItem);
            }
            headerItem.setForceHidden(isTitleCentered());

            if (currentUser != null && currentUser.self && chatMode != MODE_SAVED) {
                savedChatsItem = headerItem.lazilyAddSubItem(view_as_topics, R.drawable.msg_topics, LocaleController.getString(R.string.SavedViewAsChats));
                savedChatsGap = headerItem.lazilyAddColoredGap();
                savedChatsItem.setVisibility(getMessagesController().getSavedMessagesController().hasDialogs() ? View.VISIBLE : View.GONE);
                savedChatsGap.setVisibility(getMessagesController().getSavedMessagesController().hasDialogs() ? View.VISIBLE : View.GONE);
            } else if (chatMode != MODE_SAVED && (currentUser == null || !currentUser.self)) {
                chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, currentAccount, headerItem.getPopupLayout().getSwipeBack(), false, false, new ChatNotificationsPopupWrapper.Callback() {
                    @Override
                    public void dismiss() {
                        headerItem.toggleSubMenu();
                    }

                    @Override
                    public void toggleSound() {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                        boolean enabled = !preferences.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(dialog_id, getTopicId()), true);
                        preferences.edit().putBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(dialog_id, getTopicId()), enabled).apply();
                        if (BulletinFactory.canShowBulletin(ChatActivity.this)) {
                            BulletinFactory.createSoundEnabledBulletin(ChatActivity.this, enabled ? NotificationsController.SETTING_SOUND_ON : NotificationsController.SETTING_SOUND_OFF, getResourceProvider()).show();
                        }
                        updateTitleIcons();
                    }

                    @Override
                    public void muteFor(int timeInSeconds) {
                        if (timeInSeconds == 0) {
                            if (getMessagesController().isDialogMuted(dialog_id, getTopicId())) {
                                ChatActivity.this.toggleMute(true);
                            }
                            if (BulletinFactory.canShowBulletin(ChatActivity.this)) {
                                BulletinFactory.createMuteBulletin(ChatActivity.this, NotificationsController.SETTING_MUTE_UNMUTE, timeInSeconds, getResourceProvider()).show();
                            }
                        } else {
                            getNotificationsController().muteUntil(dialog_id, getTopicId(), timeInSeconds);
                            if (BulletinFactory.canShowBulletin(ChatActivity.this)) {
                                BulletinFactory.createMuteBulletin(ChatActivity.this, NotificationsController.SETTING_MUTE_CUSTOM, timeInSeconds, getResourceProvider()).show();
                            }
                        }
                    }

                    @Override
                    public void showCustomize() {
                        if (dialog_id != 0 && chatMode != MODE_SAVED) {
                            if (currentUser != null) {
                                getMessagesController().putUser(currentUser, true);
                            }
                            Bundle args = new Bundle();
                            args.putLong("dialog_id", dialog_id);
                            if (getTopicId() != 0) {
                                args.putLong("topic_id", getTopicId());
                            }
                            presentFragment(new ProfileNotificationsActivity(args, themeDelegate));
                        }
                    }

                    @Override
                    public void toggleMute() {
                        ChatActivity.this.toggleMute(true);
                        BulletinFactory.createMuteBulletin(ChatActivity.this, getMessagesController().isDialogMuted(dialog_id, getTopicId()), themeDelegate).show();
                    }
                }, getResourceProvider());
                muteItem = headerItem.lazilyAddSwipeBackItem(R.drawable.msg_mute, null, null, chatNotificationsPopupWrapper.windowLayout);
                muteItem.setOnClickListener(view -> {
                    boolean muted = MessagesController.getInstance(currentAccount).isDialogMuted(dialog_id, getTopicId());
                    if (muted) {
                        updateTitleIcons(true);
                        AndroidUtilities.runOnUIThread(() -> {
                            ChatActivity.this.toggleMute(true);
                        }, 150);
                        headerItem.toggleSubMenu();
                        if (ChatActivity.this.getParentActivity() != null) {
                            BulletinFactory.createMuteBulletin(ChatActivity.this, false, themeDelegate).show();
                        }
                    } else {
                        muteItem.openSwipeBack();
                    }
                });
                muteItemTopGap = headerItem.lazilyAddColoredGap();
                createAyuGramMenuItem();
                muteItemGap = headerItem.lazilyAddColoredGap();
            }

            if (currentChat != null) {
                headerItem.lazilyAddSubItem(open_direct, R.drawable.msg_markunread, getString(R.string.ChannelOpenDirect));
                headerItem.setSubItemShown(open_direct, ChatObject.isChannel(currentChat) && !ChatObject.isMonoForum(currentChat) && currentChat.linked_monoforum_id != 0 && (NaConfig.INSTANCE.getDisableChannelMuteButton().Bool() || ChatObject.canManageMonoForum(currentAccount, -currentChat.linked_monoforum_id)));
            }
            if (currentUser != null && chatMode != MODE_SAVED) {
                headerItem.lazilyAddSubItem(call, R.drawable.msg_callback, LocaleController.getString(R.string.Call));
                headerItem.lazilyAddSubItem(video_call, R.drawable.msg_videocall, LocaleController.getString(R.string.VideoCall));
                if (userFull != null && userFull.phone_calls_available) {
                    headerItem.showSubItem(call);
                    if (userFull.video_calls_available) {
                        headerItem.showSubItem(video_call);
                    } else {
                        headerItem.hideSubItem(video_call);
                    }
                } else {
                    headerItem.hideSubItem(call);
                    headerItem.hideSubItem(video_call);
                }
            }

            if (searchItem != null) {
                headerItem.lazilyAddSubItem(search, R.drawable.msg_search, LocaleController.getString(R.string.Search));
            }
            boolean allowShowPinned;
            if (currentChat != null) {
                allowShowPinned = ChatObject.canUserDoAction(currentChat, ChatObject.ACTION_PIN) || ChatObject.isChannel(currentChat);
            } else if (currentUser != null && currentUser.self) {
                allowShowPinned = true;
            } else if (userInfo != null) {
                allowShowPinned = userInfo.can_pin_message;
            } else {
                allowShowPinned = false;
            }
            if (allowShowPinned) {
                headerItem.lazilyAddSubItem(nkheaderbtn_show_pinned, R.drawable.msg_pin, LocaleController.getString("PinnedMessage", R.string.PinnedMessage));
            }
            if (ChatObject.isBoostSupported(currentChat) && (getUserConfig().isPremium() || ChatObject.isBoosted(chatInfo) || ChatObject.hasAdminRights(currentChat))) {
                RLottieDrawable drawable = new RLottieDrawable(R.raw.boosts, "" + R.raw.boosts, dp(24), dp(24));
                if (NaConfig.INSTANCE.getChatMenuItemBoostGroup().Bool()) headerItem.lazilyAddSubItem(boost_group, drawable, LocaleController.getString(ChatObject.isChannelAndNotMegaGroup(currentChat) ? R.string.BoostingBoostChannelMenu : R.string.BoostingBoostGroupMenu));
            }
            translateItem = headerItem.lazilyAddSubItem(translate, R.drawable.msg_translate, LocaleController.getString(R.string.TranslateMessage));
            updateTranslateItemVisibility();
            /*if (currentChat != null && !currentChat.creator && !ChatObject.hasAdminRights(currentChat)) {
                headerItem.lazilyAddSubItem(report, R.drawable.msg_report, LocaleController.getString(R.string.ReportChat));
            }*/

            if (currentChat != null && (currentChat.has_link || (chatInfo != null && chatInfo.linked_chat_id != 0))) {
                String text;
                int draw;
                if (!currentChat.megagroup) {
                    text = LocaleController.getString(R.string.LinkedGroupChat);
                    draw = R.drawable.msg_groups;
                } else {
                    text = LocaleController.getString(R.string.LinkedChannelChat);
                    draw = R.drawable.msg_channel;
                }
                if (NaConfig.INSTANCE.getChatMenuItemLinkedChat().Bool()) headerItem.lazilyAddSubItem(nkheaderbtn_linked_chat, draw, text);
            }

            if (currentUser != null && currentUser.id != UserObject.VERIFY && currentUser.id != UserObject.REPLY_BOT) {
                addContactItem = headerItem.lazilyAddSubItem(share_contact, R.drawable.msg_addcontact, LocaleController.getString(R.string.AddToContacts));
            }
            if (currentEncryptedChat != null) {
                timeItem2 = headerItem.lazilyAddSubItem(chat_enc_timer, R.drawable.msg_autodelete, LocaleController.getString(R.string.SetTimer));
            }
            if (currentChat != null && !isTopic) {
                viewAsTopics = headerItem.lazilyAddSubItem(view_as_topics, R.drawable.msg_topics, LocaleController.getString("TopicViewAsTopics", R.string.TopicViewAsTopics));
            }
            if (themeDelegate.isThemeChangeAvailable(true)) {
                headerItem.lazilyAddSubItem(change_colors, R.drawable.msg_background, LocaleController.getString(R.string.SetWallpapers));
            }
            if (currentUser != null && currentUser.self && getDialogId() != UserObject.VERIFY) {
                headerItem.lazilyAddSubItem(add_shortcut, R.drawable.msg_home, LocaleController.getString(R.string.AddShortcut));
            }
            if (!isTopic && !ChatObject.isMonoForum(currentChat)) {
                clearHistoryItem = headerItem.lazilyAddSubItem(clear_history, R.drawable.msg_clear,
                    LocaleController.getString(UserObject.isBotForum(currentUser) ? R.string.ClearAllHistory : R.string.ClearHistory));
            }
            boolean addedSettings = false;
            if (NaConfig.INSTANCE.getChatMenuItemToBeginning().Bool()) headerItem.lazilyAddSubItem(to_the_beginning, R.drawable.ic_upward, getString(R.string.ToTheBeginning));
            if (NaConfig.INSTANCE.getChatMenuItemGoToMessage().Bool()) headerItem.lazilyAddSubItem(to_the_message, R.drawable.msg_go_up, getString(R.string.ToTheMessage));
            if (NaConfig.INSTANCE.getShowAddToBookmark().Bool()) {
                bookmarksItem = headerItem.lazilyAddSubItem(nkbtn_bookmarks_manager, R.drawable.msg_fave, getString(R.string.BookmarksManager));
                headerItem.setSubItemShown(nkbtn_bookmarks_manager, BookmarksHelper.getBookmarkedMessageIds(currentAccount, dialog_id).length > 0);
            }
            hideTitleItem = NaConfig.INSTANCE.getChatMenuItemHideTitle().Bool() ? headerItem.lazilyAddSubItem(nkheaderbtn_hide_title, R.drawable.hide_title, getString(R.string.HideTitle)) : null;
            if (muteItem == null) {
                headerItem.lazilyAddColoredGap();
                createAyuGramMenuItem();
                headerItem.lazilyAddColoredGap();
            }
            if (!isTopic) {
                if (NaConfig.INSTANCE.getChatMenuItemDeleteOwnMessages().Bool() && (ChatObject.isMegagroup(currentChat) || currentChat != null && !ChatObject.isChannel(currentChat))) {
                    headerItem.lazilyAddSubItem(nkheaderbtn_zibi, R.drawable.msg_delete, LocaleController.getString(R.string.DeleteAllFromSelf));
                }
                if (ChatObject.isChannel(currentChat) && !currentChat.creator) {
                    if (!ChatObject.isNotInChat(currentChat)) {
                        if (currentChat.monoforum) {
                            headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_leave, LocaleController.getString(R.string.LeaveConversationMenu));
                        } else if (currentChat.megagroup) {
                            headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_leave, LocaleController.getString(R.string.LeaveMegaMenu));
                        } else {
                            headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_leave, LocaleController.getString(R.string.LeaveChannelMenu));
                        }
                    }
                } else if (!ChatObject.isChannel(currentChat) && getDialogId() != UserObject.VERIFY) {
                    if (currentChat != null) {
                        headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_leave, LocaleController.getString(R.string.DeleteAndExit));
                    } else if (currentUser != null && currentUser.bot) {
                        headerItem.lazilyAddSubItem(bot_settings, R.drawable.msg_settings_old, LocaleController.getString(R.string.BotSettings));
                        addedSettings = true;
                        headerItem.lazilyAddSubItem(bot_help, R.drawable.msg_help, LocaleController.getString(R.string.BotHelp));
                        if (!MessagesController.isSupportUser(currentUser)) {
                            headerItem.lazilyAddSubItem(report, R.drawable.msg_report, LocaleController.getString(R.string.ReportBot)).setColors(getThemedColor(Theme.key_text_RedRegular), getThemedColor(Theme.key_text_RedRegular));
                        }
                        headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_block2, LocaleController.getString(R.string.DeleteAndBlock)).setColors(getThemedColor(Theme.key_text_RedRegular), getThemedColor(Theme.key_text_RedRegular));
                        updateBotButtons();
                    } else {
                        headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_delete, LocaleController.getString(R.string.DeleteChatUser));
                    }
                }
            }
            if (ChatObject.isMonoForum(currentChat) && ChatObject.canManageMonoForum(currentAccount, currentChat)) {
                headerItem.lazilyAddSubItem(remove_fee, R.drawable.menu_paid_off, getString(R.string.DirectRemoveFee));
                headerItem.lazilyAddSubItem(charge_fee, R.drawable.menu_feature_paid, getString(R.string.DirectChargeFee));
                headerItem.setSubItemShown(remove_fee, false);
                headerItem.setSubItemShown(charge_fee, false);

                feeItemGap = headerItem.lazilyAddColoredGap();
                feeItemText = headerItem.lazilyAddText("", 13);
                feeItemGap.setVisibility(View.GONE);
                feeItemText.setVisibility(View.GONE);
            }
            if (currentChat != null && (currentChat.creator || currentChat.admin_rights != null)
                    && !ChatObject.isMonoForum(currentChat)
                    && (ChatObject.isChannel(currentChat) || currentChat.megagroup || currentChat.gigagroup)) {
                adminItemsGap = headerItem.lazilyAddColoredGap();
                int permissionsIcon = (ChatObject.isChannel(currentChat) && !currentChat.megagroup) || currentChat.gigagroup
                        ? R.drawable.msg_user_remove
                        : R.drawable.msg_permissions;
                List<com.exteragram.messenger.components.ActionRow.ActionItem> adminActions = Arrays.asList(
                        new com.exteragram.messenger.components.ActionRow.ActionItem(permissionsIcon, true, v -> onAdminShortcutsClick(shortcuts_permissions)),
                        new com.exteragram.messenger.components.ActionRow.ActionItem(R.drawable.msg_admins, true, v -> onAdminShortcutsClick(shortcuts_administrators)),
                        new com.exteragram.messenger.components.ActionRow.ActionItem(R.drawable.msg_groups, true, v -> onAdminShortcutsClick(shortcuts_members)),
                        new com.exteragram.messenger.components.ActionRow.ActionItem(R.drawable.msg_log, true, v -> onAdminShortcutsClick(shortcuts_recent_actions))
                );
                com.exteragram.messenger.components.ActionRow adminActionRow = new com.exteragram.messenger.components.ActionRow(getContext(), themeDelegate, adminActions);
                adminItemsRow = headerItem.lazilyAddView(adminActionRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 56));
            }
        } else if (chatMode == MODE_EDIT_BUSINESS_LINK) {
            headerItem = menu.addItem(chat_menu_options, otherIcon);
            otherIcon.addView(headerItem.getIconView());
            headerItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));

            headerItem.lazilyAddSubItem(copy_business_link, R.drawable.msg_copy, getString(R.string.Copy));
            headerItem.lazilyAddSubItem(share_business_link, R.drawable.msg_share, getString(R.string.LinkActionShare));
            headerItem.lazilyAddSubItem(rename_business_link, R.drawable.msg_edit, getString(R.string.Rename));
            headerItem.lazilyAddSubItem(delete_business_link, R.drawable.msg_delete, getString(R.string.Delete)).setColors(Theme.getColor(Theme.key_text_RedRegular), Theme.getColor(Theme.key_text_RedRegular));
        }
        if (ChatObject.isForum(currentChat) && isTopic && getParentLayout() != null && getParentLayout().getFragmentStack() != null && chatMode == MODE_DEFAULT) {
            boolean hasMyForum = false;
            for (int i = 0; i < getParentLayout().getFragmentStack().size(); ++i) {
                BaseFragment fragment = getParentLayout().getFragmentStack().get(i);
                if (fragment instanceof TopicsFragment && ((TopicsFragment) fragment).getDialogId() == dialog_id) {
                    hasMyForum = true;
                    break;
                }
            }

            if (!hasMyForum) {
                openForumItem = headerItem.lazilyAddSubItem(open_forum, R.drawable.msg_discussion, LocaleController.getString(R.string.OpenAllTopics));
            }
        }
        if (currentChat != null && forumTopic != null && chatMode == 0) {
            closeTopicItem = headerItem.lazilyAddSubItem(topic_close, R.drawable.msg_topic_close, LocaleController.getString(R.string.CloseTopic));
            closeTopicItem.setVisibility(currentChat != null && ChatObject.canManageTopic(currentAccount, currentChat, forumTopic) && forumTopic != null && !forumTopic.closed ? View.VISIBLE : View.GONE);
        }
        appendPluginChatActionMenuItems();
        menu.setVisibility(inMenuMode ? View.GONE : View.VISIBLE);

        updateTitle(false);
        avatarContainer.updateOnlineCount();
        avatarContainer.updateSubtitle();
        updateTitleIcons();

        if (chatMode == 0 && (!isThreadChat() || isTopic) && !isReport()) {
            attachItem = menu.lazilyAddItem(chat_menu_attach, otherIcon, themeDelegate);
            attachItem.onView(cell -> otherIcon.addView(cell.getIconView()));
            attachItem.setOverrideMenuClick(true);
            attachItem.setAllowCloseAnimation(false);
            attachItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            attachItem.setVisibility(View.GONE);
        }

        if (inPreviewMode) {
            if (headerItem != null) {
                headerItem.setAlpha(0.0f);
            }
            if (attachItem != null) {
                attachItem.setAlpha(0.0f);
            }
        }

        if (BuildConfig.DEBUG_PRIVATE_VERSION && headerItem != null) {
            headerItem.lazilyAddSubItem(888, R.drawable.menu_download_round, "Dump Canvas");        }

        actionModeViews.clear();
        selectedMessagesCountTextView = null;
        checkActionBarMenu(false);

        scrimPaint = new Paint();

        if (chatListThanosEffect != null) {
            AndroidUtilities.removeFromParent(chatListThanosEffect);
            chatListThanosEffect = null;
        }
        removingFromParent = false;
        fragmentView = contentView = new ChatActivityFragmentView(context, parentLayout);
        invalidateBlurredSourcesView = new OnPostDrawView(context, true, this::invalidateMergedVisibleBlurredPositionsAndSourcesImpl);
        contentView.addView(invalidateBlurredSourcesView);

        viewPositionWatcher = new ViewPositionWatcher(contentView);

        final ViewGroup parentView = parentChatActivity != null ? parentChatActivity.contentView : contentView;
        glassBackgroundDrawableFactory.setSourceRootView(viewPositionWatcher, parentView);
        glassBackgroundDrawableFactoryFrosted.setSourceRootView(viewPositionWatcher, parentView);
        navbarContentDrawableFactory.setSourceRootView(viewPositionWatcher, parentView);
        scrimBlur3Factory.setSourceRootView(viewPositionWatcher, parentView);

        if (headerItem != null) {
            headerItem.setBlurredBackgroundFactory(scrimBlur3Factory, BlurredBackgroundProviderImpl.messageMenuBackground(resourceProvider));
        }

        contentView.setOccupyStatusBar(!inBubbleMode && !isInsideContainer && !inPreviewMode);

        actionBar.setupGlass(
            glassBackgroundDrawableFactory,
            BlurredBackgroundProviderImpl.topPanelChatActivity(themeDelegate),
            ChatObject.isForum(currentChat));
        actionBar.setChatAvatarContainer(avatarContainer);

        if (chatMode == MODE_PINNED) {
            avatarContainer.setActionBar(actionBar);
        } else if (chatMode == MODE_WELCOME_MESSAGES) {
            actionBar.setForcedMenuWidth(dp(46));
            actionBar.doNotDrawGlassMenu = true;
            avatarContainer.setActionBar(actionBar);
        } else if (isComments) {
            actionBar.setForcedMenuMinWidth(dp(46));
            avatarContainer.setActionBar(actionBar);
        }

        fadeDrawable = new BlurredBackgroundWithFadeDrawable(
                navbarContentDrawableFactory.create(chatInputViewsContainer, null));
        if (!SharedConfig.chatBlurEnabled() || LiteMode.isEnabled(LiteMode.FLAG_LIQUID_GLASS) || true) {
            fadeDrawable.setFadeHeight(dp(72), true);
        }

        chatInputViewsContainer = new ChatInputViewsContainer(context);
        chatInputViewsContainer.setClipChildren(false);
        chatInputViewsContainer.setWindowInsetsProvider(windowInsetsStateHolder);
        chatInputViewsContainer.setInputIslandBubbleDrawable(
            glassBackgroundDrawableFactory.create(chatInputViewsContainer, blurredBackgroundColorProvider));
        chatInputViewsContainer.setUnderKeyboardBackgroundDrawable(
            glassBackgroundDrawableFactoryFrosted.create(chatInputViewsContainer, blurredBackgroundColorProvider));

        if (NekoConfig.iOSMessageInputField.Bool()) {
            chatInputViewsContainer.setLeftBubbleDrawable(
                glassBackgroundDrawableFactory.create(chatInputViewsContainer, blurredBackgroundColorProvider));
            chatInputViewsContainer.setRightBubbleDrawable(
                glassBackgroundDrawableFactory.create(chatInputViewsContainer, blurredBackgroundColorProvider));
        }

        chatInputBubbleContainer = chatInputViewsContainer.getInputIslandBubbleContainer();
        chatInputBubbleContainer.setClipChildren(false);

        chatInputInAppContainer = chatInputViewsContainer.getInAppKeyboardBubbleContainer();

        updateBackground();

        emptyViewContainer = null;

        CharSequence oldMessage;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
            if (!chatActivityEnterView.isEditingMessage()) {
                oldMessage = chatActivityEnterView.getFieldText();
            } else {
                oldMessage = null;
            }
        } else {
            oldMessage = null;
        }
        if (mentionContainer != null && mentionContainer.getAdapter() != null) {
            mentionContainer.getAdapter().onDestroy();
        }

        chatListView = new ChatListRecyclerView(context, themeDelegate) {
            private int lastWidth;

            private final ArrayList<ChatMessageCell> drawTimeAfter = new ArrayList<>();
            private final ArrayList<ChatMessageCell> drawNamesAfter = new ArrayList<>();
            private final ArrayList<ChatMessageCell> drawCaptionAfter = new ArrayList<>();
            private final ArrayList<ChatMessageCell> drawReactionsAfter = new ArrayList<>();
            private final ArrayList<MessageObject.GroupedMessages> drawingGroups = new ArrayList<>(10);

            private int startedTrackingX;
            private int startedTrackingY;
            private int startedTrackingPointerId;
            private long lastTrackingAnimationTime;
            private float trackAnimationProgress;
            private float endTrackingX;
            private boolean wasTrackingVibrate;

            private float springMultiplier = 2000f;

            private Paint outlineActionBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            private Paint outlineActionBackgroundDarkenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            private FloatValueHolder slidingDrawableVisibilityProgress = new FloatValueHolder(0);
            private SpringAnimation slidingDrawableVisibilitySpring = new SpringAnimation(slidingDrawableVisibilityProgress)
                    .setMinValue(0f)
                    .setMaxValue(springMultiplier)
                    .setSpring(new SpringForce(0)
                            .setStiffness(SpringForce.STIFFNESS_MEDIUM)
                            .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY))
                    .addUpdateListener((animation, value, velocity) -> invalidate());
            private FloatValueHolder slidingFillProgress = new FloatValueHolder(0);
            private SpringAnimation slidingFillProgressSpring = new SpringAnimation(slidingFillProgress)
                    .setMinValue(0f)
                    .setSpring(new SpringForce(0)
                            .setStiffness(400f)
                            .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY))
                    .addUpdateListener((animation, value, velocity) -> invalidate());
            private FloatValueHolder slidingOuterRingProgress = new FloatValueHolder(0);
            private SpringAnimation slidingOuterRingSpring = new SpringAnimation(slidingOuterRingProgress)
                    .setMinValue(0f)
                    .setSpring(new SpringForce(0)
                            .setStiffness(200f)
                            .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY))
                    .addUpdateListener((animation, value, velocity) -> invalidate());
            private boolean slidingBeyondMax;
            private Path path = new Path();

            private boolean ignoreLayout;
            private boolean invalidated;

            int lastH = 0;

            {
                outlineActionBackgroundPaint.setStyle(Paint.Style.STROKE);
                outlineActionBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
                outlineActionBackgroundPaint.setStrokeWidth(AndroidUtilities.dp(2));
                outlineActionBackgroundDarkenPaint.setStyle(Paint.Style.STROKE);
                outlineActionBackgroundDarkenPaint.setStrokeCap(Paint.Cap.ROUND);
                outlineActionBackgroundDarkenPaint.setStrokeWidth(AndroidUtilities.dp(2));
            }

            @Override
            public void requestLayout() {
                if (ignoreLayout) {
                    return;
                }
                botDraftHeightController.onRequestLayout();
                super.requestLayout();
            }

            @Override
            public void setTranslationY(float translationY) {
                if (translationY != getTranslationY()) {
                    super.setTranslationY(translationY);
                    invalidateChatListViewTopPadding();
                    invalidateMessagesVisiblePart();
                }
            }

            @Override
            protected boolean allowSelectChildAtPosition(View child) {
                if (child != null && (child.getVisibility() == View.INVISIBLE || child.getVisibility() == View.GONE)) return false;
                return super.allowSelectChildAtPosition(child);
            }

            @Override
            protected void onMeasure(int widthSpec, int heightSpec) {
//                saveScrollPosition();
                super.onMeasure(widthSpec, heightSpec);
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);

                if (lastWidth != r - l) {
                    if (lastWidth != 0) {
                        hideHints(false);
                    }
                    lastWidth = r - l;
                }

                int height = getMeasuredHeight();
                if (lastH != height) {
                    ignoreLayout = true;
                    if (chatListItemAnimator != null) {
                        chatListItemAnimator.endAnimations();
                    }
                    chatScrollHelper.cancel();
                    ignoreLayout = false;
                    lastH = height;
                }

                forceScrollToTop = false;
                if (textSelectionHelper != null && textSelectionHelper.isInSelectionMode()) {
                    textSelectionHelper.invalidate();
                }
                invalidateClipRectForBackgroundAndChatList();
                isSkeletonVisible();
            }

            private void setGroupTranslationX(ChatMessageCell view, float dx) {
                MessageObject.GroupedMessages group = view.getCurrentMessagesGroup();
                if (group == null) {
                    return;
                }
                int count = getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = getChildAt(a);
                    if (child == view || !(child instanceof ChatMessageCell)) {
                        continue;
                    }
                    ChatMessageCell cell = (ChatMessageCell) child;
                    if (cell.getCurrentMessagesGroup() == group) {
                        cell.setSlidingOffset(dx);
                        cell.invalidate();
                    }
                }
                invalidate();
            }

            @Override
            public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
                if (scrimPopupWindow != null) {
                    return false;
                }
                return super.requestChildRectangleOnScreen(child, rect, immediate);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent e) {
                textSelectionHelper.checkSelectionCancel(e);
                if (isFastScrollAnimationRunning()) {
                    return false;
                }
                if (quickShareSelectorOverlay != null && quickShareSelectorOverlay.isActive()) {
                    return false;
                }
                boolean result = super.onInterceptTouchEvent(e);
                if (actionBar.isActionModeShowed() || isReport()) {
                    return result;
                }
                processTouchEvent(e);
                return result;
            }

            @Override
            public void setItemAnimator(ItemAnimator animator) {
                if (isFastScrollAnimationRunning()) {
                    return;
                }
                super.setItemAnimator(animator);
            }

            private void drawReplyButton(Canvas canvas) {
                if (slidingView == null || Thread.currentThread() != Looper.getMainLooper().getThread()) {
                    return;
                }
                Paint chatActionBackgroundPaint = getThemedPaint(Theme.key_paint_chatActionBackground);
                Paint chatActionBackgroundDarkenPaint = Theme.chat_actionBackgroundGradientDarkenPaint;
                if (outlineActionBackgroundPaint.getColor() != chatActionBackgroundPaint.getColor()) {
                    outlineActionBackgroundPaint.setColor(chatActionBackgroundPaint.getColor());
                }
                if (outlineActionBackgroundDarkenPaint.getColor() != chatActionBackgroundDarkenPaint.getColor()) {
                    outlineActionBackgroundDarkenPaint.setColor(chatActionBackgroundDarkenPaint.getColor());
                }
                if (outlineActionBackgroundPaint.getShader() != chatActionBackgroundPaint.getShader()) {
                    outlineActionBackgroundPaint.setShader(chatActionBackgroundPaint.getShader());
                }
                if (outlineActionBackgroundDarkenPaint.getShader() != chatActionBackgroundDarkenPaint.getShader()) {
                    outlineActionBackgroundDarkenPaint.setShader(chatActionBackgroundDarkenPaint.getShader());
                }

                float fillProgress = slidingFillProgress.getValue() / springMultiplier;
                int wasDarkenColor = outlineActionBackgroundDarkenPaint.getColor();

                if (fillProgress > 1) {
                    slidingBeyondMax = true;
                }

                float translationX = getSlidingNonAnimationTranslationX(false);
                if (slidingDrawableVisibilityProgress.getValue() == 0) {
                    slidingFillProgressSpring.cancel();
                    slidingFillProgressSpring.getSpring().setFinalPosition(0);
                    slidingFillProgress.setValue(0f);
                    slidingOuterRingSpring.cancel();
                    slidingOuterRingSpring.getSpring().setFinalPosition(0);
                    slidingOuterRingProgress.setValue(0f);
                    slidingBeyondMax = false;
                }
                float progress;
                if (slidingFillProgressSpring.getSpring().getFinalPosition() != springMultiplier) {
                    progress = androidx.core.math.MathUtils.clamp((-translationX - AndroidUtilities.dp(20)) / AndroidUtilities.dp(30), 0, 1);
                } else {
                    progress = 1f;
                }

                if (progress == 1f && slidingFillProgressSpring.getSpring().getFinalPosition() != springMultiplier) {
                    slidingFillProgressSpring.getSpring().setFinalPosition(springMultiplier);
                    slidingFillProgressSpring.start();

                    slidingOuterRingSpring.getSpring().setFinalPosition(springMultiplier);
                    slidingOuterRingSpring.start();
                }

                boolean visible = translationX <= -AndroidUtilities.dp(20);
                float endVisibleValue = visible ? springMultiplier : 0;
                if (endVisibleValue != slidingDrawableVisibilitySpring.getSpring().getFinalPosition()) {
                    slidingDrawableVisibilitySpring.getSpring().setFinalPosition(endVisibleValue);
                    if (!slidingDrawableVisibilitySpring.isRunning()) {
                        slidingDrawableVisibilitySpring.start();
                    }
                }

                float iconProgress = slidingDrawableVisibilityProgress.getValue() / springMultiplier;
                MessageObject slidingMsg = getSlidingMessageObject();
                float x = getMeasuredWidth() + translationX * (slidingMsg != null && slidingMsg.isOut() ? 0.5f : 1f);
                float y = slidingView.getTop() + slidingView.getMeasuredHeight() / 2f;
                float scale = slidingBeyondMax ? fillProgress : iconProgress;

                float clearScale = slidingBeyondMax ? 0f : 1f - fillProgress;

                boolean isDark = ColorUtils.calculateLuminance(getThemedColor(Theme.key_windowBackgroundWhite)) <= 0.5f;
                if (iconProgress != 0) {
                    AndroidUtilities.rectTmp.set((int) (x - AndroidUtilities.dp(16) * scale + outlineActionBackgroundPaint.getStrokeWidth() / 2f), (int) (y - AndroidUtilities.dp(16) * scale + outlineActionBackgroundPaint.getStrokeWidth() / 2f), (int) (x + AndroidUtilities.dp(16) * scale - outlineActionBackgroundPaint.getStrokeWidth() / 2f), (int) (y + AndroidUtilities.dp(16) * scale - outlineActionBackgroundPaint.getStrokeWidth() / 2f));
                    Theme.applyServiceShaderMatrix(getMeasuredWidth(), AndroidUtilities.displaySize.y, 0, getY() + AndroidUtilities.rectTmp.top);
                    if (fillProgress == 0) {
                        int outlineAlpha = outlineActionBackgroundPaint.getAlpha();
                        outlineActionBackgroundPaint.setAlpha((int) (outlineAlpha * iconProgress));
                        canvas.drawArc(AndroidUtilities.rectTmp, -90, 360 * progress, false, outlineActionBackgroundPaint);
                        outlineActionBackgroundPaint.setAlpha(outlineAlpha);

                        if (themeDelegate.hasGradientService()) {
                            outlineAlpha = outlineActionBackgroundDarkenPaint.getAlpha();
                            if (isDark) {
                                outlineActionBackgroundDarkenPaint.setColor(Color.WHITE);
                            }
                            outlineActionBackgroundDarkenPaint.setAlpha((int) (outlineAlpha * iconProgress));
                            canvas.drawArc(AndroidUtilities.rectTmp, -90, 360 * progress, false, outlineActionBackgroundDarkenPaint);
                        }
                    }
                }
                AndroidUtilities.rectTmp.set((int) (x - AndroidUtilities.dp(16) * scale), (int) (y - AndroidUtilities.dp(16) * scale), (int) (x + AndroidUtilities.dp(16) * scale), (int) (y + AndroidUtilities.dp(16) * scale));
                Theme.applyServiceShaderMatrix(getMeasuredWidth(), AndroidUtilities.displaySize.y, 0, getY() + AndroidUtilities.rectTmp.top);
                path.rewind();
                path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(16) * scale, AndroidUtilities.dp(16) * scale, Path.Direction.CW);

                int wasAlpha = chatActionBackgroundPaint.getAlpha();
                chatActionBackgroundPaint.setAlpha((int) (iconProgress * 0.6f * progress * wasAlpha));
                canvas.drawPath(path, chatActionBackgroundPaint);
                chatActionBackgroundPaint.setAlpha(wasAlpha);

                if (themeDelegate.hasGradientService()) {
                    wasAlpha = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                    if (isDark) {
                        Theme.chat_actionBackgroundGradientDarkenPaint.setColor(Color.WHITE);
                    }
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (iconProgress * 0.6f * progress * wasAlpha));
                    canvas.drawPath(path, Theme.chat_actionBackgroundGradientDarkenPaint);
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(wasAlpha);
                }

                if (clearScale != 0f) {
                    AndroidUtilities.rectTmp.set((int) (x - AndroidUtilities.dp(16) * clearScale), (int) (y - AndroidUtilities.dp(16) * clearScale), (int) (x + AndroidUtilities.dp(16) * clearScale), (int) (y + AndroidUtilities.dp(16) * clearScale));
                    path.rewind();
                    path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(16), AndroidUtilities.dp(16), Path.Direction.CW);

                    canvas.save();
                    canvas.clipPath(path, Region.Op.DIFFERENCE);
                }

                AndroidUtilities.rectTmp.set((int) (x - AndroidUtilities.dp(16) * scale), (int) (y - AndroidUtilities.dp(16) * scale), (int) (x + AndroidUtilities.dp(16) * scale), (int) (y + AndroidUtilities.dp(16) * scale));
                Theme.applyServiceShaderMatrix(getMeasuredWidth(), AndroidUtilities.displaySize.y, 0, getY() + AndroidUtilities.rectTmp.top);
                path.rewind();
                path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(16) * scale, AndroidUtilities.dp(16) * scale, Path.Direction.CW);

                wasAlpha = chatActionBackgroundPaint.getAlpha();
                chatActionBackgroundPaint.setAlpha((int) (iconProgress * 0.4f * wasAlpha));
                canvas.drawPath(path, chatActionBackgroundPaint);
                chatActionBackgroundPaint.setAlpha(wasAlpha);

                if (themeDelegate.hasGradientService()) {
                    wasAlpha = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                    if (isDark) {
                        Theme.chat_actionBackgroundGradientDarkenPaint.setColor(Color.WHITE);
                    }
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (iconProgress * 0.4f * wasAlpha));
                    canvas.drawPath(path, Theme.chat_actionBackgroundGradientDarkenPaint);
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(wasAlpha);
                }
                if (clearScale != 0f) {
                    canvas.restore();
                }

                float outerRingProgress = slidingOuterRingProgress.getValue() / springMultiplier;
                if (outerRingProgress != 0 && outerRingProgress != 1) {
                    float outScale = 1f + outerRingProgress;

                    float wasWidth = outlineActionBackgroundPaint.getStrokeWidth();
                    float width = (1f - outerRingProgress) * wasWidth;
                    if (width != 0f) {
                        AndroidUtilities.rectTmp.set((int) (x - AndroidUtilities.dp(16) * outScale + width), (int) (y - AndroidUtilities.dp(16) * outScale + width), (int) (x + AndroidUtilities.dp(16) * outScale - width), (int) (y + AndroidUtilities.dp(16) * outScale - width));
                        Theme.applyServiceShaderMatrix(getMeasuredWidth(), AndroidUtilities.displaySize.y, 0, getY() + AndroidUtilities.rectTmp.top);

                        wasAlpha = outlineActionBackgroundPaint.getAlpha();
                        outlineActionBackgroundPaint.setAlpha((int) (wasAlpha * iconProgress));

                        outlineActionBackgroundPaint.setStrokeWidth(width);
                        canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(16) * outScale, AndroidUtilities.dp(16) * outScale, outlineActionBackgroundPaint);
                        outlineActionBackgroundPaint.setStrokeWidth(wasWidth);

                        outlineActionBackgroundPaint.setAlpha(wasAlpha);

                        if (themeDelegate.hasGradientService()) {
                            wasAlpha = outlineActionBackgroundDarkenPaint.getAlpha();
                            if (isDark) {
                                outlineActionBackgroundDarkenPaint.setColor(Color.WHITE);
                            }
                            outlineActionBackgroundDarkenPaint.setAlpha((int) (wasAlpha * iconProgress));

                            outlineActionBackgroundDarkenPaint.setStrokeWidth(width);
                            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(16) * outScale, AndroidUtilities.dp(16) * outScale, outlineActionBackgroundDarkenPaint);
                            outlineActionBackgroundDarkenPaint.setStrokeWidth(wasWidth);
                        }
                    }
                }

                int alpha = (int) (iconProgress * 0xFF);
                Drawable replyIconDrawable = getThemedDrawable(Theme.key_drawable_replyIcon);
                replyIconDrawable.setAlpha(alpha);
                replyIconDrawable.setBounds((int) (x - replyIconDrawable.getIntrinsicWidth() / 2 * scale), (int) (y - replyIconDrawable.getIntrinsicHeight() / 2 * scale), (int) (x + replyIconDrawable.getIntrinsicWidth() / 2 * scale), (int) (y + replyIconDrawable.getIntrinsicHeight() / 2 * scale));
                replyIconDrawable.draw(canvas);
                replyIconDrawable.setAlpha(255);

                outlineActionBackgroundDarkenPaint.setColor(wasDarkenColor);
                chatActionBackgroundDarkenPaint.setColor(wasDarkenColor);
            }

            private void processTouchEvent(MotionEvent e) {
                if (e != null) {
                    wasManualScroll = true;
                }
                if (e != null && e.getAction() == MotionEvent.ACTION_DOWN && !startedTrackingSlidingView && !maybeStartTrackingSlidingView && slidingView == null && !inPreviewMode) {
                    View view = getPressedChildView();
                    if (view instanceof ChatMessageCell) {
                        if (slidingView != null) {
                            slidingViewSetOffset(0);
                        }
                        slidingView = view;
                        MessageObject message = getSlidingMessageObject();
                        boolean allowReplyOnOpenTopic = canSendMessageToTopic(message);
                        if (message != null && message.isAyuDeleted()) {
                            slidingViewSetOffset(0);
                            slidingView = null;
                            return;
                        }
                        if (
                            chatMode != 0 && chatMode != MODE_QUICK_REPLIES && chatMode != MODE_SUGGESTIONS && (chatMode != MODE_SAVED || threadMessageId != getUserConfig().getClientUserId()) ||
                            threadMessageObjects != null && threadMessageObjects.contains(message) ||
                            getMessageType(message) == 1 && (message.getDialogId() == mergeDialogId || message.needDrawBluredPreview()) ||
                            currentEncryptedChat == null && message.getId() < 0 ||
                            currentChat != null && ChatObject.isForum(currentChat) && !allowReplyOnOpenTopic ||
                            hasTextSelection() ||
                            message.isEphemeral() && message.isOut()
                        ) {
                            slidingViewSetOffset(0);
                            slidingView = null;
                            return;
                        }
                        startedTrackingPointerId = e.getPointerId(0);
                        maybeStartTrackingSlidingView = true;
                        startedTrackingX = (int) e.getX();
                        startedTrackingY = (int) e.getY();
                    }
                } else if (slidingView != null && e != null && e.getAction() == MotionEvent.ACTION_MOVE && e.getPointerId(0) == startedTrackingPointerId) {
                    int dx = Math.max(AndroidUtilities.dp(-80), Math.min(0, (int) (e.getX() - startedTrackingX)));
                    int dy = Math.abs((int) e.getY() - startedTrackingY);
                    if (getScrollState() == SCROLL_STATE_IDLE && maybeStartTrackingSlidingView && !startedTrackingSlidingView && dx <= -AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                        slidingView.onTouchEvent(event);
                        super.onInterceptTouchEvent(event);
                        event.recycle();
                        chatLayoutManager.setCanScrollVertically(false);
                        maybeStartTrackingSlidingView = false;
                        startedTrackingSlidingView = true;
                        startedTrackingX = (int) e.getX();
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    } else if (startedTrackingSlidingView) {
                        if (Math.abs(dx) >= AndroidUtilities.dp(50)) {
                            if (!wasTrackingVibrate) {
                                try {
                                    if (!NekoConfig.disableVibration.Bool()) performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                                } catch (Exception ignore) {}
                                wasTrackingVibrate = true;
                            }
                        } else {
                            wasTrackingVibrate = false;
                        }
                        slidingViewSetOffset(dx);
                        MessageObject messageObject = getSlidingMessageObject();
                        if (messageObject != null && (messageObject.isRoundVideo() || messageObject.isVideo())) {
                            updateTextureViewPosition(false, false);
                        }
                        if (slidingView instanceof ChatMessageCell) {
                            setGroupTranslationX((ChatMessageCell) slidingView, dx);
                        }
                        invalidate();
                    }
                } else if (slidingView != null && (e == null || e.getPointerId(0) == startedTrackingPointerId && (e.getAction() == MotionEvent.ACTION_CANCEL || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_POINTER_UP))) {
                    if (e != null && e.getAction() != MotionEvent.ACTION_CANCEL && Math.abs(getSlidingNonAnimationTranslationX(false)) >= AndroidUtilities.dp(50)) {
                        MessageObject message = getSlidingMessageObject();
                        final boolean allowReplyOnOpenTopic = canSendMessageToTopic(message);
                        if (
                            bottomChannelButtonsLayout != null && bottomChannelButtonsLayout.getVisibility() == View.VISIBLE && !(bottomOverlayChatWaitsReply && allowReplyOnOpenTopic || message.wasJustSent) ||
                            currentChat != null && (
                                ChatObject.isNotInChat(currentChat) && !isThreadChat() ||
                                ChatObject.isChannel(currentChat) && !ChatObject.canPost(currentChat) && !currentChat.megagroup ||
                                !ChatObject.canSendMessages(currentChat)
                            )
                        ) {
                            if (message.getGroupId() != 0) {
                                MessageObject.GroupedMessages group = getGroup(message.getGroupId());
                                if (group != null && group.captionMessage != null) {
                                    message = group.captionMessage;
                                }
                            }
                            replyingMessageObject = message;
                            Bundle args = new Bundle();
                            args.putBoolean("onlySelect", true);
                            args.putInt("dialogsType", DialogsActivity.DIALOGS_TYPE_FORWARD);
                            args.putBoolean("quote", true);
                            args.putBoolean("reply_to", true);
                            final long author = DialogObject.getPeerDialogId(message.getFromPeer());
                            if (author != 0 && author != getDialogId() && author != getUserConfig().getClientUserId() && author > 0) {
                                args.putLong("reply_to_author", author);
                            }
                            args.putInt("messagesCount", 1);
                            args.putBoolean("canSelectTopics", true);
                            final DialogsActivity fragment = new DialogsActivity(args);
                            fragment.setDelegate(ChatActivity.this);
                            presentFragment(fragment);
                        } else {
                            showFieldPanelForReply(getSlidingMessageObject());
                        }
                    }
                    endTrackingX = slidingViewGetOffsetX();
                    if (endTrackingX == 0) {
                        slidingView = null;
                    }
                    lastTrackingAnimationTime = System.currentTimeMillis();
                    trackAnimationProgress = 0.0f;
                    invalidate();
                    maybeStartTrackingSlidingView = false;
                    startedTrackingSlidingView = false;
                    chatLayoutManager.setCanScrollVertically(true);
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent e) {
                textSelectionHelper.checkSelectionCancel(e);
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    scrollByTouch = true;
                }
                if (!NekoConfig.disableSwipeToNext.Bool() && pullingDownOffset != 0 && (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL)) {
                    float progress = Math.min(1f, pullingDownOffset / AndroidUtilities.dp(110));
                    if (e.getAction() == MotionEvent.ACTION_UP && progress == 1 && pullingDownDrawable != null && !pullingDownDrawable.emptyStub) {
                        if (pullingDownDrawable.animationIsRunning()) {
                            ValueAnimator animator = ValueAnimator.ofFloat(pullingDownOffset, pullingDownOffset + AndroidUtilities.dp(8));
                            pullingDownBackAnimator = animator;
                            animator.addUpdateListener(valueAnimator -> {
                                pullingDownOffset = (float) valueAnimator.getAnimatedValue();
                                chatListView.invalidate();
                            });
                            animator.setDuration(200);
                            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            animator.start();
                            pullingDownDrawable.runOnAnimationFinish(() -> {
                                animateToNextChat();
                            });
                        } else {
                            animateToNextChat();
                        }
                    } else {
                        if (pullingDownDrawable != null && pullingDownDrawable.emptyStub && (System.currentTimeMillis() - pullingDownDrawable.lastShowingReleaseTime) < 500 && pullingDownDrawable.animateSwipeToRelease) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            pullingDownBackAnimator = animatorSet;
                            if (pullingDownDrawable != null) {
                                animatorPullingDownContainerVisibility.setValue(false, true);
                            }
                            ValueAnimator animator = ValueAnimator.ofFloat(pullingDownOffset, AndroidUtilities.dp(111));
                            animator.addUpdateListener(valueAnimator -> {
                                pullingDownOffset = (float) valueAnimator.getAnimatedValue();
                                chatListView.invalidate();
                            });
                            animator.setDuration(400);
                            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);

                            ValueAnimator animator2 = ValueAnimator.ofFloat(AndroidUtilities.dp(111), 0);
                            animator2.addUpdateListener(valueAnimator -> {
                                pullingDownOffset = (float) valueAnimator.getAnimatedValue();
                                chatListView.invalidate();
                            });
                            animator2.setStartDelay(600);
                            animator2.setDuration(ChatListItemAnimator.DEFAULT_DURATION);
                            animator2.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);

                            animatorSet.playSequentially(animator, animator2);
                            animatorSet.start();
                        } else {
                            ValueAnimator animator = ValueAnimator.ofFloat(pullingDownOffset, 0);
                            pullingDownBackAnimator = animator;
                            if (pullingDownDrawable != null) {
                                animatorPullingDownContainerVisibility.setValue(false, true);
                            }
                            animator.addUpdateListener(valueAnimator -> {
                                pullingDownOffset = (float) valueAnimator.getAnimatedValue();
                                chatListView.invalidate();
                            });
                            animator.setDuration(ChatListItemAnimator.DEFAULT_DURATION);
                            animator.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                            animator.start();
                        }
                    }
                }
                if (isFastScrollAnimationRunning()) {
                    return false;
                }
                boolean result = super.onTouchEvent(e);
                if (actionBar.isActionModeShowed() || isReport()) {
                    return result;
                }
                processTouchEvent(e);
                return startedTrackingSlidingView || result;
            }

            @Override
            public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                super.requestDisallowInterceptTouchEvent(disallowIntercept);
                if (slidingView != null) {
                    processTouchEvent(null);
                }
            }

            @Override
            protected void onChildPressed(View child, float x, float y, boolean pressed) {
                super.onChildPressed(child, x, y, pressed);
                if (child instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) child;
                    MessageObject object = chatMessageCell.getMessageObject();
                    if (object.isMusic() || object.isDocument()) {
                        return;
                    }
                    MessageObject.GroupedMessages groupedMessages = chatMessageCell.getCurrentMessagesGroup();
                    if (groupedMessages != null) {
                        int count = getChildCount();
                        for (int a = 0; a < count; a++) {
                            View item = getChildAt(a);
                            if (item == child || !(item instanceof ChatMessageCell)) {
                                continue;
                            }
                            ChatMessageCell cell = (ChatMessageCell) item;
                            if (cell.getCurrentMessagesGroup() == groupedMessages) {
                                cell.setPressed(pressed);
                            }
                        }
                    }
                }
            }

            @Override
            public void onDraw(Canvas c) {
                super.onDraw(c);
                if (slidingView != null) {
                    float translationX = slidingViewGetOffsetX();
                    if (!maybeStartTrackingSlidingView && !startedTrackingSlidingView && endTrackingX != 0 && translationX != 0) {
                        long newTime = System.currentTimeMillis();
                        long dt = newTime - lastTrackingAnimationTime;
                        trackAnimationProgress += dt / 180.0f;
                        if (trackAnimationProgress > 1.0f) {
                            trackAnimationProgress = 1.0f;
                        }
                        lastTrackingAnimationTime = newTime;
                        translationX = endTrackingX * (1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(trackAnimationProgress));
                        if (translationX == 0) {
                            endTrackingX = 0;
                        }
                        if (slidingView instanceof ChatMessageCell) {
                            setGroupTranslationX((ChatMessageCell) slidingView, translationX);
                        }
                        slidingViewSetOffset(translationX);
                        MessageObject messageObject = getSlidingMessageObject();
                        if (messageObject != null && (messageObject.isRoundVideo() || messageObject.isVideo())) {
                            updateTextureViewPosition(false, false);
                        }

                        if (trackAnimationProgress == 1f || trackAnimationProgress == 0f) {
                            slidingViewSetOffset(0);
                            slidingView = null;
                        }
                        invalidate();
                    }
                    drawReplyButton(c);
                }

                if (!NekoConfig.disableSwipeToNext.Bool() && pullingDownOffset != 0 && !isInPreviewMode() && !isInsideContainer && chatMode != MODE_SAVED && chatMode != MODE_SCHEDULED) {
                    c.save();
                    float transitionOffset = 0;
                    if (pullingDownAnimateProgress != 0) {
                        transitionOffset = (
                            chatListView.getMeasuredHeight()
                                - pullingDownOffset
                                + (pullingDownAnimateToActivity == null ? 0 : pullingDownAnimateToActivity.pullingBottomOffset)
                        ) * pullingDownAnimateProgress;
                    }

                    c.translate(0, getMeasuredHeight() - blurredViewBottomOffset - transitionOffset);
                    if (pullingDownDrawable == null) {
                        pullingDownDrawable = new ChatPullingDownDrawable(currentAccount, fragmentView, dialog_id, dialogFolderId, dialogFilterId, getTopicId(), themeDelegate);
                        pullingDownDrawable.progressToBottomPanel = animatorPullingDownContainerVisibility.getFloatValue();
                        if (nextChannels != null && !nextChannels.isEmpty()) {
                            pullingDownDrawable.updateDialog(nextChannels.get(0));
                        } else if (isTopic) {
                            pullingDownDrawable.updateTopic();
                        } else {
                            pullingDownDrawable.updateDialog();
                        }
                        pullingDownDrawable.onAttach();
                    }
                    pullingDownDrawable.setWidth(getMeasuredWidth() - (isSideMenued() ? dp(SIDE_MENU_WIDTH) : 0));
                    float progress = Math.min(1f, pullingDownOffset / AndroidUtilities.dp(110));
                    c.translate(isSideMenued() ? lerp(dp(32), dp(SIDE_MENU_WIDTH), getSideMenuAlpha()) : 0,
                        -(windowInsetsStateHolder.getAnimatedMaxBottomInset() + dp(10) +
                        chatInputViewsContainer.getInputBubbleHeight() + getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM)));
                    pullingDownDrawable.draw(c, chatListView, progress, 1f - pullingDownAnimateProgress);

                    c.restore();

                    if (pullingDownAnimateToActivity != null) {
                        c.saveLayerAlpha(0, 0, pullingDownAnimateToActivity.chatListView.getMeasuredWidth(), pullingDownAnimateToActivity.chatListView.getMeasuredHeight(), (int) (255 * pullingDownAnimateProgress), Canvas.ALL_SAVE_FLAG);
                        c.translate(0, getMeasuredHeight() - pullingDownOffset - transitionOffset);
                        pullingDownAnimateToActivity.chatListView.draw(c);
                        c.restore();
                    }
                } else if (pullingDownDrawable != null) {
                    pullingDownDrawable.reset();
                }
            }

            @Override
            public void draw(Canvas canvas) {
                if ((startMessageAppearTransitionMs == 0 || System.currentTimeMillis() - startMessageAppearTransitionMs <= SKELETON_DISAPPEAR_MS) && !AndroidUtilities.isTablet() && !isComments && currentUser == null) {
                    boolean noAvatar = (currentChat == null || ChatObject.isChannelAndNotMegaGroup(currentChat)) && chatMode != MODE_SEARCH;
                    if (pullingDownOffset != 0) {
                        canvas.save();
                        canvas.translate(0, -pullingDownOffset);
                    }
                    updateSkeletonColors();
                    updateSkeletonGradient();

                    int lastTop = getHeight() - blurredViewBottomOffset - (int) (windowInsetsStateHolder.getAnimatedMaxBottomInset() + getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM)) - dp(44 + 7 + 9 - 3);
                    int j = 0;

                    int childMaxTop = Integer.MAX_VALUE;
                    for (int i = 0; i < getChildCount(); i++) {
                        int top = getChildAt(i).getTop();
                        if (top < childMaxTop) {
                            childMaxTop = top;
                        }
                    }
                    if (startMessageAppearTransitionMs == 0 && childMaxTop <= 0) {
                        checkDispatchHideSkeletons(fragmentBeginToShow);
                    }

                    Paint servicePaint = getThemedPaint(Theme.key_paint_chatActionBackground);
                    if (skeletonServicePaint.getColor() != servicePaint.getColor()) {
                        skeletonServicePaint.setColor(servicePaint.getColor());
                    }
                    if (skeletonServicePaint.getShader() != servicePaint.getShader()) {
                        skeletonServicePaint.setShader(servicePaint.getShader());
                        skeletonColorMatrix.setSaturation(SKELETON_SATURATION);
                        skeletonServicePaint.setColorFilter(new ColorMatrixColorFilter(skeletonColorMatrix));
                    }

                    for (int i = 0; i < getChildCount(); i++) {
                        View v = getChildAt(i);
//                        if (v instanceof ChatMessageCell) {
//                            ChatMessageCell cell = (ChatMessageCell) v;
//                            if ((cell.getCurrentMessagesGroup() == null || cell.getCurrentMessagesGroup().findPrimaryMessageObject() == cell.getMessageObject())) {
//                                if (cell.shouldDrawAlphaLayer() || System.currentTimeMillis() - startMessageAppearTransitionMs >= SKELETON_DISAPPEAR_MS) {
//                                    float progress = cell.getAlpha();
//
//                                    MessageSkeleton skeleton;
//                                    if (j >= messageSkeletons.size()) {
//                                        skeleton = getNewSkeleton(noAvatar);
//                                        messageSkeletons.add(skeleton);
//                                    } else {
//                                        skeleton = messageSkeletons.get(j);
//                                    }
//
//                                    Rect bounds = cell.getCurrentBackgroundDrawable(true).getBounds();
//                                    MessageObject.GroupedMessages group = cell.getCurrentMessagesGroup();
//
//                                    int alpha = skeletonPaint.getAlpha();
//                                    int wasServiceAlpha = servicePaint.getAlpha();
//                                    servicePaint.setAlpha((int) (wasServiceAlpha * 0.4f * (1f - progress)));
//                                    skeletonPaint.setAlpha((int) (alpha * (1f - progress)));
//                                    int bottom = (int) AndroidUtilities.lerp(Math.min(skeleton.lastBottom, lastTop - AndroidUtilities.dp(3f)), v.getBottom() + (group != null ? group.transitionParams.top + group.transitionParams.offsetTop : 0), progress);
//                                    int left = noAvatar ? AndroidUtilities.dp(3f) : AndroidUtilities.dp(51);
//                                    int top = (int) AndroidUtilities.lerp(bottom - skeleton.height, bounds.top + v.getTop() + (group != null ? group.transitionParams.top + group.transitionParams.offsetTop : 0), progress);
//                                    int right = skeleton.width;
//
//                                    boolean lerp = cell.getMessageObject() == null || !cell.getMessageObject().isOut();
//                                    skeletonBackgroundDrawable.setBounds(lerp ? AndroidUtilities.lerp(left, cell.getBackgroundDrawableLeft(), progress) : left, top,
//                                            lerp ? AndroidUtilities.lerp(right, cell.getBackgroundDrawableRight(), progress) : right, bottom);
//                                    Theme.applyServiceShaderMatrix(getMeasuredWidth(), AndroidUtilities.displaySize.y, 0, getY() + skeletonBackgroundDrawable.getBounds().top);
//                                    skeletonBackgroundDrawable.drawCached(canvas, skeletonBackgroundCacheParams, servicePaint);
//                                    skeletonBackgroundDrawable.drawCached(canvas, skeletonBackgroundCacheParams, skeletonPaint);
//                                    if (!noAvatar) {
//                                        Theme.applyServiceShaderMatrix(getMeasuredWidth(), AndroidUtilities.displaySize.y, 0, getY() + bottom - AndroidUtilities.dp(42));
//                                        canvas.drawCircle(AndroidUtilities.dp(48 - 21), bottom - AndroidUtilities.dp(21), AndroidUtilities.dp(21), servicePaint);
//                                        canvas.drawCircle(AndroidUtilities.dp(48 - 21), bottom - AndroidUtilities.dp(21), AndroidUtilities.dp(21), skeletonPaint);
//                                    }
//                                    servicePaint.setAlpha(wasServiceAlpha);
//                                    skeletonPaint.setAlpha(alpha);
//                                    j++;
//
//                                    if (top < lastTop) {
//                                        lastTop = top;
//                                    }
//
//                                    continue;
//                                }
//                                j++;
//                            }
//                        }
                        if (v instanceof ChatMessageCell) {
                            MessageObject.GroupedMessages group = ((ChatMessageCell) v).getCurrentMessagesGroup();
                            Rect bounds = ((ChatMessageCell) v).getCurrentBackgroundDrawable(true).getBounds();
                            int newTop = (int) (v.getTop() + bounds.top + (group != null ? group.transitionParams.top + group.transitionParams.offsetTop : 0));
                            int top = startMessageAppearTransitionMs == 0 && isSkeletonVisible() ? lerp(lastTop, newTop, v.getAlpha()) : v.getAlpha() == 1f ? newTop : lastTop;
                            if (top < lastTop) {
                                lastTop = top;
                            }
                        } else if (v instanceof ChatActionCell) {
                            int top = startMessageAppearTransitionMs == 0 && isSkeletonVisible() ? lerp(lastTop, v.getTop(), v.getAlpha()) : v.getAlpha() == 1f ? v.getTop() : lastTop;
                            if (top < lastTop) {
                                lastTop = top;
                            }
                        }
                    }

                    if (isSkeletonVisible()) {
                        boolean drawService = SharedConfig.getDevicePerformanceClass() != SharedConfig.PERFORMANCE_CLASS_LOW && Theme.hasGradientService();
                        boolean darkOverlay = ColorUtils.calculateLuminance(getThemedColor(Theme.key_windowBackgroundWhite)) <= 0.7f && Theme.hasGradientService();
                        boolean blackOverlay = ColorUtils.calculateLuminance(getThemedColor(Theme.key_windowBackgroundWhite)) <= 0.01f && Theme.hasGradientService();
                        if (drawService) {
                            Theme.applyServiceShaderMatrix(getMeasuredWidth(), AndroidUtilities.displaySize.y, 0, getY() - contentPanTranslation);
                        }
                        int wasDarkenAlpha = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                        if (blackOverlay) {
                            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (wasDarkenAlpha * 4f));
                        }

                        float topSkeletonAlpha = startMessageAppearTransitionMs != 0 ? 1f - (System.currentTimeMillis() - startMessageAppearTransitionMs) / (float) SKELETON_DISAPPEAR_MS : 1f;
                        int alpha = skeletonPaint.getAlpha();
                        int wasServiceAlpha = skeletonServicePaint.getAlpha();
                        int wasOutlineAlpha = skeletonOutlinePaint.getAlpha();
                        float adaptDark = 1f;
                        if (themeDelegate != null && themeDelegate.isDark && skeletonServicePaint.getShader() != null) {
                            adaptDark *= .3f;
                        }
                        skeletonServicePaint.setAlpha((int) (0xFF * topSkeletonAlpha * adaptDark));
                        skeletonPaint.setAlpha((int) (topSkeletonAlpha * adaptDark * alpha));
                        skeletonOutlinePaint.setAlpha((int) (topSkeletonAlpha * alpha));
                        while (lastTop > blurredViewTopOffset) {
                            lastTop -= AndroidUtilities.dp(3f);

                            MessageSkeleton skeleton;
                            if (j >= messageSkeletons.size()) {
                                skeleton = getNewSkeleton(noAvatar);
                                messageSkeletons.add(skeleton);
                            } else {
                                skeleton = messageSkeletons.get(j);
                            }
                            skeleton.lastBottom = startMessageAppearTransitionMs != 0 ? messages.size() <= 2 ? Math.min(skeleton.lastBottom, lastTop) : skeleton.lastBottom : lastTop;

                            lastTop -= skeleton.height;

                            j++;
                        }

                        lastTop = messageSkeletons.isEmpty() ? getHeight() - blurredViewBottomOffset : messageSkeletons.get(0).lastBottom + AndroidUtilities.dp(3f);
                        int left = dp(noAvatar ? 3 : 51);
                        if (isSideMenued()) {
                            left = lerp(left, dp(SIDE_MENU_WIDTH), getSideMenuAlpha());
                        }
                        for (int i = 0; i < messageSkeletons.size() && lastTop > blurredViewTopOffset; i++) {
                            lastTop -= dp(3f);

                            MessageSkeleton skeleton = messageSkeletons.get(i);

                            int bottom = skeleton.lastBottom;
                            skeletonBackgroundDrawable.setBounds(left, bottom - skeleton.height, skeleton.width, bottom);
                            if (drawService) {
                                skeletonBackgroundDrawable.drawCached(canvas, skeletonBackgroundCacheParams, skeletonServicePaint);
                            }
                            skeletonBackgroundDrawable.drawCached(canvas, skeletonBackgroundCacheParams, skeletonPaint);
                            if (darkOverlay) {
                                skeletonBackgroundDrawable.drawCached(canvas, skeletonBackgroundCacheParams, Theme.chat_actionBackgroundGradientDarkenPaint);
                            }
                            skeletonBackgroundDrawable.drawCached(canvas, skeletonBackgroundCacheParams, skeletonOutlinePaint);

                            if (!noAvatar) {
                                if (drawService) {
                                    canvas.drawCircle(dp(48 - 21), bottom - dp(21), dp(21), skeletonServicePaint);
                                }
                                canvas.drawCircle(dp(48 - 21), bottom - dp(21), dp(21), skeletonPaint);
                                if (darkOverlay) {
                                    canvas.drawCircle(dp(48 - 21), bottom - dp(21), dp(21), Theme.chat_actionBackgroundGradientDarkenPaint);
                                }
                                canvas.drawCircle(dp(48 - 21), bottom - dp(21), dp(21), skeletonOutlinePaint);
                            }

                            lastTop -= skeleton.height;
                        }

                        skeletonServicePaint.setAlpha(wasServiceAlpha);
                        skeletonPaint.setAlpha(alpha);
                        skeletonOutlinePaint.setAlpha(wasOutlineAlpha);
                        Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(wasDarkenAlpha);
                        invalidated = false;
                        invalidate();
                    } else if (System.currentTimeMillis() - startMessageAppearTransitionMs > SKELETON_DISAPPEAR_MS) {
                        messageSkeletons.clear();
                    }
                    lastSkeletonCount = messageSkeletons.size();
                    lastSkeletonMessageCount = messages.size();
                    if (pullingDownOffset != 0) {
                        canvas.restore();
                    }
                }
                super.draw(canvas);
            }

            private void updateSkeletonColors() {
                boolean dark = ColorUtils.calculateLuminance(getThemedColor(Theme.key_windowBackgroundWhite)) <= 0.7f;
                int color0 = ColorUtils.blendARGB(getThemedColor(Theme.key_listSelector), Color.argb(dark ? 0x21 : 0x03, 0xFF, 0xFF, 0xFF), dark ? 0.9f : 0.5f);
                int color1 = ColorUtils.setAlphaComponent(getThemedColor(Theme.key_listSelector), dark ? 24 : SKELETON_LIGHT_OVERLAY_ALPHA);
                if (skeletonColor1 != color1 || skeletonColor0 != color0) {
                    skeletonColor0 = color0;
                    skeletonColor1 = color1;
                    skeletonGradient = new LinearGradient(0, 0, skeletonGradientWidth = AndroidUtilities.dp(200), 0, new int[]{color1, color0, color0, color1}, new float[]{0.0f, 0.4f, 0.6f, 1f}, Shader.TileMode.CLAMP);
                    skeletonTotalTranslation = -skeletonGradientWidth * 2;
                    skeletonPaint.setShader(skeletonGradient);

                    int outlineColor = Color.argb(dark ? 0x2B : 0x60, 0xFF, 0xFF, 0xFF);
                    skeletonOutlineGradient = new LinearGradient(0, 0, skeletonGradientWidth, 0, new int[]{Color.TRANSPARENT, outlineColor, outlineColor, Color.TRANSPARENT}, new float[]{0.0f, 0.4f, 0.6f, 1f}, Shader.TileMode.CLAMP);
                    skeletonOutlinePaint.setShader(skeletonOutlineGradient);
                }
            }

            private void updateSkeletonGradient() {
                long newUpdateTime = SystemClock.elapsedRealtime();
                long dt = Math.abs(skeletonLastUpdateTime - newUpdateTime);
                if (dt > 17) {
                    dt = 16;
                }
                if (dt < 4) {
                    dt = 0;
                }
                int width = getWidth();
                skeletonLastUpdateTime = newUpdateTime;
                skeletonTotalTranslation += dt * width / 400.0f;
                if (skeletonTotalTranslation >= width * 2) {
                    skeletonTotalTranslation = -skeletonGradientWidth * 2;
                }
                skeletonMatrix.setTranslate(skeletonTotalTranslation, 0);
                if (skeletonGradient != null) {
                    skeletonGradient.setLocalMatrix(skeletonMatrix);
                }
                skeletonOutlineMatrix.setTranslate(skeletonTotalTranslation, 0);
                if (skeletonOutlineGradient != null) {
                    skeletonOutlineGradient.setLocalMatrix(skeletonOutlineMatrix);
                }
            }


            @Override
            protected void dispatchDraw(Canvas canvas) {
                drawLaterRoundProgressCell = null;
                invalidated = false;

                canvas.save();
                if ((fragmentTransition == null || (fromPullingDownTransition && !toPullingDownTransition)) && !isInsideContainer) {
                    // canvas.clipRect(0, chatListViewPaddingTop - chatListViewPaddingVisibleOffset - AndroidUtilities.dp(4), getMeasuredWidth(), getMeasuredHeight() - blurredViewBottomOffset);
                }
                selectorRect.setEmpty();
                if (pullingDownOffset != 0) {
                    int restoreToCount = canvas.save();
                    float transitionOffset = 0;
                    if (pullingDownAnimateProgress != 0) {
                        transitionOffset = (chatListView.getMeasuredHeight() - pullingDownOffset) * pullingDownAnimateProgress;
                    }
                    canvas.translate(0, drawingChatListViewYoffset = -pullingDownOffset - transitionOffset);
                    drawChatBackgroundElements(canvas);
                    super.dispatchDraw(canvas);
                    drawChatForegroundElements(canvas);
                    canvas.restoreToCount(restoreToCount);
                } else {
                    drawChatBackgroundElements(canvas);
                    super.dispatchDraw(canvas);
                    drawChatForegroundElements(canvas);
                }
                canvas.restore();
            }

            protected void drawChatForegroundElements(Canvas canvas, RectF position) {
                int size = drawTimeAfter.size();
                if (size > 0) {
                    for (int a = 0; a < size; a++) {
                        ChatMessageCell cell = drawTimeAfter.get(a);
                        if (quickRejectChild(cell, position)) {
                            continue;
                        }
                        canvas.save();
                        canvas.translate(cell.getLeft() + cell.getNonAnimationTranslationX(false), cell.getY() + cell.getPaddingTop());
                        cell.drawTime(canvas, cell.shouldDrawAlphaLayer() ? cell.getAlpha() : 1f, true);
                        canvas.restore();
                    }
                    drawTimeAfter.clear();
                }
                size = drawNamesAfter.size();
                if (size > 0) {
                    for (int a = 0; a < size; a++) {
                        ChatMessageCell cell = drawNamesAfter.get(a);
                        if (quickRejectChild(cell, position)) {
                            continue;
                        }
                        float canvasOffsetX = cell.getLeft() + cell.getNonAnimationTranslationX(false);
                        float canvasOffsetY = cell.getY() + cell.getPaddingTop();
                        float alpha = cell.shouldDrawAlphaLayer() ? cell.getAlpha() : 1f;

                        canvas.save();
                        canvas.translate(canvasOffsetX, canvasOffsetY);
                        cell.setInvalidatesParent(true);
                        cell.drawNamesLayout(canvas, alpha);
                        cell.setInvalidatesParent(false);
                        canvas.restore();
                    }
                    drawNamesAfter.clear();
                }
                size = drawCaptionAfter.size();
                if (size > 0) {
                    for (int a = 0; a < size; a++) {
                        ChatMessageCell cell = drawCaptionAfter.get(a);
                        if (quickRejectChild(cell, position)) {
                            continue;
                        }
                        boolean selectionOnly = false;
                        if (cell.getCurrentPosition() != null) {
                            selectionOnly = (cell.getCurrentPosition().flags & MessageObject.POSITION_FLAG_LEFT) == 0;
                        }
                        float alpha = cell.shouldDrawAlphaLayer() ? cell.getAlpha() : 1f;
                        float canvasOffsetX = cell.getLeft() + cell.getNonAnimationTranslationX(false);
                        float canvasOffsetY = cell.getY() + cell.getPaddingTop();
                        canvas.save();
                        MessageObject.GroupedMessages groupedMessages = cell.getCurrentMessagesGroup();
                        if (groupedMessages != null && groupedMessages.transitionParams.backgroundChangeBounds) {
                            float x = cell.getNonAnimationTranslationX(true);
                            float l = (groupedMessages.transitionParams.left + x + groupedMessages.transitionParams.offsetLeft);
                            float t = (groupedMessages.transitionParams.top + groupedMessages.transitionParams.offsetTop);
                            float r = (groupedMessages.transitionParams.right + x + groupedMessages.transitionParams.offsetRight);
                            float b = (groupedMessages.transitionParams.bottom + groupedMessages.transitionParams.offsetBottom);

                            if (!groupedMessages.transitionParams.backgroundChangeBounds) {
                                t += cell.getTranslationY();
                                b += cell.getTranslationY();
                            }
                            canvas.clipRect(
                                    l + AndroidUtilities.dp(8), t + AndroidUtilities.dp(8),
                                    r - AndroidUtilities.dp(8), b - AndroidUtilities.dp(8)
                            );
                        }
                        if (cell.getTransitionParams().wasDraw) {
                            canvas.translate(canvasOffsetX, canvasOffsetY);
                            cell.setInvalidatesParent(true);
                            cell.drawCaptionLayout(canvas, selectionOnly, alpha);
                            cell.setInvalidatesParent(false);
                        }
                        canvas.restore();
                    }
                    drawCaptionAfter.clear();
                }
                size = drawReactionsAfter.size();
                if (size > 0) {
                    for (int a = 0; a < size; a++) {
                        ChatMessageCell cell = drawReactionsAfter.get(a);
                        if (quickRejectChild(cell, position)) {
                            continue;
                        }
                        boolean selectionOnly = false;
                        if (cell.getCurrentPosition() != null) {
                            selectionOnly = (cell.getCurrentPosition().flags & MessageObject.POSITION_FLAG_LEFT) == 0;
                        }
                        float alpha = cell.shouldDrawAlphaLayer() ? cell.getAlpha() : 1f;
                        float canvasOffsetX = cell.getLeft() + cell.getNonAnimationTranslationX(false);
                        float canvasOffsetY = cell.getY() + cell.getPaddingTop();
                        canvas.save();
                        MessageObject.GroupedMessages groupedMessages = cell.getCurrentMessagesGroup();
                        if (groupedMessages != null && groupedMessages.transitionParams.backgroundChangeBounds) {
                            float x = cell.getNonAnimationTranslationX(true);
                            float l = (groupedMessages.transitionParams.left + x + groupedMessages.transitionParams.offsetLeft);
                            float t = (groupedMessages.transitionParams.top + groupedMessages.transitionParams.offsetTop);
                            float r = (groupedMessages.transitionParams.right + x + groupedMessages.transitionParams.offsetRight);
                            float b = (groupedMessages.transitionParams.bottom + groupedMessages.transitionParams.offsetBottom);

                            if (!groupedMessages.transitionParams.backgroundChangeBounds) {
                                t += cell.getTranslationY();
                                b += cell.getTranslationY();
                            }
                            canvas.clipRect(
                                    l + AndroidUtilities.dp(8), t + AndroidUtilities.dp(8),
                                    r - AndroidUtilities.dp(8), b - AndroidUtilities.dp(8)
                            );
                        }
                        if (!selectionOnly && cell.getTransitionParams().wasDraw) {
                            canvas.translate(canvasOffsetX, canvasOffsetY);
                            cell.setInvalidatesParent(true);
                            cell.drawReactionsLayout(canvas, alpha, null);
                            cell.drawCommentLayout(canvas, alpha);
                            cell.setInvalidatesParent(false);
                        }
                        canvas.restore();
                    }
                    drawReactionsAfter.clear();
                }
            }

            protected void drawChatBackgroundElements(Canvas canvas, RectF positionF) {
                final int count = getChildCount();
                MessageObject.GroupedMessages lastDrawnGroup = null;

                for (int a = 0; a < count; a++) {
                    View child = getChildAt(a);
                    if (child.getVisibility() == View.INVISIBLE || child.getVisibility() == View.GONE || quickRejectChild(child, positionF)) {
                        continue;
                    }
                    if (child instanceof ChatMessageUnsupportedCell) {
                        ChatMessageUnsupportedCell unsupportedCell = (ChatMessageUnsupportedCell) child;
                        canvas.save();
                        canvas.translate(child.getX(), child.getY());
                        unsupportedCell.drawBackground(canvas);
                        canvas.restore();
                    } else if (chatAdapter.isBot && child instanceof BotHelpCell) {
                        BotHelpCell botCell = (BotHelpCell) child;
                        float top = (getMeasuredHeight() - chatListViewPaddingTop - blurredViewBottomOffset) / 2 - child.getMeasuredHeight() / 2 + chatListViewPaddingTop;
                        if (!botCell.animating() && !chatListView.fastScrollAnimationRunning) {
                            if (child.getTop() > top) {
                                child.setTranslationY(top - child.getTop());
                            } else {
                                child.setTranslationY(0);
                            }
                        }
                        break;
                    } else if (child instanceof UserInfoCell) {
                        UserInfoCell cell = (UserInfoCell) child;
                        float top = (getMeasuredHeight() - chatListViewPaddingTop - blurredViewBottomOffset) / 2 - child.getMeasuredHeight() / 2 + chatListViewPaddingTop;
                        if (!cell.animating() && !chatListView.fastScrollAnimationRunning) {
                            if (child.getTop() > top) {
                                child.setTranslationY(top - child.getTop());
                            } else {
                                child.setTranslationY(0);
                            }
                        }
                    } else if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) child;
                        MessageObject.GroupedMessages group = cell.getCurrentMessagesGroup();
                        if (group == null || group != lastDrawnGroup) {
                            lastDrawnGroup = group;
                            MessageObject.GroupedMessagePosition position = cell.getCurrentPosition();
                            MessageBackgroundDrawable backgroundDrawable = cell.getBackgroundDrawable();
                            if ((backgroundDrawable.isAnimationInProgress() || cell.isDrawingSelectionBackground()) && (position == null || (position.flags & MessageObject.POSITION_FLAG_RIGHT) != 0)) {
                                if (cell.isHighlighted() || cell.isHighlightedAnimated()) {
                                    if (position == null) {
                                        Paint backgroundPaint = getThemedPaint(Theme.key_paint_chatMessageBackgroundSelected);
                                        if (themeDelegate != null && themeDelegate.isDark || backgroundPaint == null) {
                                            backgroundPaint = Theme.chat_replyLinePaint;
                                            backgroundPaint.setColor(getThemedColor(Theme.key_chat_selectedBackground));
                                        } else {
                                            float viewTop = (isKeyboardVisible() ? chatListView.getTop() : actionBar.getMeasuredHeight()) - contentView.getBackgroundTranslationY();
                                            int backgroundHeight = contentView.getBackgroundSizeY();
                                            if (themeDelegate != null) {
                                                themeDelegate.applyServiceShaderMatrix(getMeasuredWidth(), backgroundHeight, cell.getX(), viewTop);
                                            } else {
                                                Theme.applyServiceShaderMatrix(getMeasuredWidth(), backgroundHeight, cell.getX(), viewTop);
                                            }
                                        }
                                        canvas.save();
                                        canvas.translate(0, cell.getTranslationY());
                                        int wasAlpha = backgroundPaint.getAlpha();
                                        backgroundPaint.setAlpha((int) (wasAlpha * cell.getHighlightAlpha() * cell.getAlpha()));
                                        canvas.drawRect(0, cell.getTop(), getMeasuredWidth(), cell.getBottom(), backgroundPaint);
                                        backgroundPaint.setAlpha(wasAlpha);
                                        canvas.restore();
                                    }
                                } else {
                                    int y = (int) cell.getY();
                                    int height;
                                    canvas.save();
                                    if (position == null) {
                                        height = cell.getMeasuredHeight();
                                    } else {
                                        height = y + cell.getMeasuredHeight();
                                        long time = 0;
                                        float touchX = 0;
                                        float touchY = 0;
                                        for (int i = 0; i < count; i++) {
                                            View inner = getChildAt(i);
                                            if (inner instanceof ChatMessageCell) {
                                                ChatMessageCell innerCell = (ChatMessageCell) inner;
                                                MessageObject.GroupedMessages innerGroup = innerCell.getCurrentMessagesGroup();
                                                if (innerGroup == group) {
                                                    MessageBackgroundDrawable drawable = innerCell.getBackgroundDrawable();
                                                    y = Math.min(y, (int) innerCell.getY());
                                                    height = Math.max(height, (int) innerCell.getY() + innerCell.getMeasuredHeight());
                                                    long touchTime = drawable.getLastTouchTime();
                                                    if (touchTime > time) {
                                                        touchX = drawable.getTouchX() + innerCell.getX();
                                                        touchY = drawable.getTouchY() + innerCell.getY();
                                                        time = touchTime;
                                                    }
                                                }
                                            }
                                        }
                                        backgroundDrawable.setTouchCoordsOverride(touchX, touchY - y);
                                        height -= y;
                                    }
                                    canvas.clipRect(0, y, getMeasuredWidth(), y + height);
                                    Paint selectedBackgroundPaint = getThemedPaint(Theme.key_paint_chatMessageBackgroundSelected);
                                    if (themeDelegate != null && !themeDelegate.isDark && selectedBackgroundPaint != null) {
                                        backgroundDrawable.setCustomPaint(selectedBackgroundPaint);
                                        float viewTop = (isKeyboardVisible() ? chatListView.getTop() : actionBar.getMeasuredHeight()) - contentView.getBackgroundTranslationY();
                                        int backgroundHeight = contentView.getBackgroundSizeY();
                                        if (themeDelegate != null) {
                                            themeDelegate.applyServiceShaderMatrix(getMeasuredWidth(), backgroundHeight, cell.getX(), viewTop);
                                        } else {
                                            Theme.applyServiceShaderMatrix(getMeasuredWidth(), backgroundHeight, cell.getX(), viewTop);
                                        }
                                    } else {
                                        backgroundDrawable.setCustomPaint(null);
                                        backgroundDrawable.setColor(getThemedColor(Theme.key_chat_selectedBackground));
                                    }
                                    backgroundDrawable.setBounds(0, y, getMeasuredWidth(), y + height);
                                    backgroundDrawable.draw(canvas);
                                    canvas.restore();
                                }
                            }
                        }
                        if ((scrimView != cell || scrimViewTask != null) && group == null && cell.drawBackgroundInParent()) {
                            canvas.save();
                            canvas.translate(cell.getX(), cell.getY() + cell.getPaddingTop());
                            if (cell.getScaleX() != 1f) {
                                canvas.scale(
                                    cell.getScaleX(), cell.getScaleY(),
                                    cell.getPivotX(), (cell.getHeight() >> 1)
                                );
                            }
                            cell.drawBackgroundInternal(canvas, true);
                            canvas.restore();
                        }
                    } else if (child instanceof ChatActionCell) {
                        ChatActionCell cell = (ChatActionCell) child;
                        if (cell.hasGradientService()) {
                            canvas.save();
                            canvas.translate(cell.getX(), cell.getY() + cell.getPaddingTop());
                            canvas.scale(cell.getScaleX(), cell.getScaleY(), cell.getMeasuredWidth() / 2f, cell.getMeasuredHeight() / 2f);
                            canvas.translate(getSideMenuWidth() / 2f, 0);
                            cell.drawBackground(canvas, true);
                            cell.drawReactions(canvas, true, null);
                            canvas.restore();
                        }
                    }
                }
                MessageObject.GroupedMessages scrimGroup = null;
                if (scrimView instanceof ChatMessageCell) {
                    scrimGroup = ((ChatMessageCell) scrimView).getCurrentMessagesGroup();
                }
                for (int k = 0; k < 3; k++) {
                    drawingGroups.clear();
                    if (k == 2 && !chatListView.isFastScrollAnimationRunning()) {
                        continue;
                    }
                    for (int i = 0; i < count; i++) {
                        View child = chatListView.getChildAt(i);
                        if (child instanceof ChatMessageCell) {
                            ChatMessageCell cell = (ChatMessageCell) child;
                            if (child.getY() > chatListView.getHeight() || child.getY() + child.getHeight() < 0 || cell.getVisibility() == View.GONE) {
                                continue;
                            }
                            MessageObject.GroupedMessages group = cell.getCurrentMessagesGroup();
                            if (group == null || (k == 0 && group.messages.size() == 1) || (k == 1 && !group.transitionParams.drawBackgroundForDeletedItems)) {
                                continue;
                            }
                            if ((k == 0 && cell.getMessageObject().deleted) || (k == 1 && !cell.getMessageObject().deleted)) {
                                continue;
                            }
                            if ((k == 2 && !cell.willRemovedAfterAnimation()) || (k != 2 && cell.willRemovedAfterAnimation())) {
                                continue;
                            }

                            if (!drawingGroups.contains(group)) {
                                group.transitionParams.left = 0;
                                group.transitionParams.top = 0;
                                group.transitionParams.right = 0;
                                group.transitionParams.bottom = 0;

                                group.transitionParams.pinnedBotton = false;
                                group.transitionParams.pinnedTop = false;
                                group.transitionParams.cell = cell;
                                drawingGroups.add(group);
                            }

                            group.transitionParams.pinnedTop = cell.isPinnedTop();
                            group.transitionParams.pinnedBotton = cell.isPinnedBottom();

                            int left = (cell.getLeft() + cell.getBackgroundDrawableLeft());
                            int right = (cell.getLeft() + cell.getBackgroundDrawableRight());
                            int top = (cell.getTop() + cell.getPaddingTop() + cell.getBackgroundDrawableTop());
                            int bottom = (cell.getTop() + cell.getPaddingTop() + cell.getBackgroundDrawableBottom());

                            if ((cell.getCurrentPosition().flags & MessageObject.POSITION_FLAG_TOP) == 0) {
                                top -= AndroidUtilities.dp(10);
                            }

                            if ((cell.getCurrentPosition().flags & MessageObject.POSITION_FLAG_BOTTOM) == 0) {
                                bottom += AndroidUtilities.dp(10);
                            }

                            if (cell.willRemovedAfterAnimation()) {
                                group.transitionParams.cell = cell;
                            }

                            if (group.transitionParams.top == 0 || top < group.transitionParams.top) {
                                group.transitionParams.top = top;
                            }
                            if (group.transitionParams.bottom == 0 || bottom > group.transitionParams.bottom) {
                                group.transitionParams.bottom = bottom;
                            }
                            if (group.transitionParams.left == 0 || left < group.transitionParams.left) {
                                group.transitionParams.left = left;
                            }
                            if (group.transitionParams.right == 0 || right > group.transitionParams.right) {
                                group.transitionParams.right = right;
                            }
                        }
                    }

                    for (int i = 0; i < drawingGroups.size(); i++) {
                        final MessageObject.GroupedMessages group = drawingGroups.get(i);
                        if (group == scrimGroup) {
                             // continue;
                        }
                        float x = group.transitionParams.cell.getNonAnimationTranslationX(true);
                        float l = (group.transitionParams.left + x + group.transitionParams.offsetLeft);
                        float t = (group.transitionParams.top + group.transitionParams.offsetTop);
                        float r = (group.transitionParams.right + x + group.transitionParams.offsetRight);
                        float b = (group.transitionParams.bottom + group.transitionParams.offsetBottom);

                        if (!group.transitionParams.backgroundChangeBounds) {
                            t += group.transitionParams.cell.getTranslationY();
                            b += group.transitionParams.cell.getTranslationY();
                        }

                        /*
                        if (t < chatListViewPaddingTop - chatListViewPaddingVisibleOffset - dp(20)) {
                            t = chatListViewPaddingTop - chatListViewPaddingVisibleOffset - dp(20);
                        }
                        */

                        if (b > chatListView.getMeasuredHeight() + dp(20)) {
                            b = chatListView.getMeasuredHeight() + dp(20);
                        }

                        boolean useScale = group.transitionParams.cell.getScaleX() != 1f || group.transitionParams.cell.getScaleY() != 1f;
                        if (useScale) {
                            canvas.save();
                            canvas.scale(group.transitionParams.cell.getScaleX(), group.transitionParams.cell.getScaleY(), l + (r - l) / 2, t + (b - t) / 2);
                        }
                        boolean selected = true;
                        for (int a = 0, N = group.messages.size(); a < N; a++) {
                            MessageObject object = group.messages.get(a);
                            int index = object.getDialogId() == dialog_id ? 0 : 1;
                            if (selectedMessagesIds[index].indexOfKey(object.getId()) < 0) {
                                selected = false;
                                break;
                            }
                        }
                        group.transitionParams.cell.drawBackground(canvas, (int) l, (int) t, (int) r, (int) b, group.transitionParams.pinnedTop, group.transitionParams.pinnedBotton, selected, 0);
                        if (group != scrimGroup) {
                            group.transitionParams.cell = null;
                        }
                        group.transitionParams.drawCaptionLayout = group.hasCaption;
                        if (useScale) {
                            canvas.restore();
                            for (int ii = 0; ii < count; ii++) {
                                View child = chatListView.getChildAt(ii);
                                if (child instanceof ChatMessageCell && ((ChatMessageCell) child).getCurrentMessagesGroup() == group) {
                                    ChatMessageCell cell = ((ChatMessageCell) child);
                                    int left = cell.getLeft();
                                    int top = cell.getTop();
                                    child.setPivotX(l - left + (r - l) / 2);
                                    child.setPivotY(t - top + (b - t) / 2);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (isSkeletonVisible()) {
                    invalidated = false;
                    invalidate();
                }

                int clipLeft = 0;
                int clipBottom = 0;
                boolean skipDraw = child == scrimView && scrimViewTask == null;
                IMessageCell mcell = null;
                ChatMessageCell cell;
                ChatActionCell actionCell = null;
                float cilpTop = 0;
                boolean isAnimatingBounds = false;
                if (child instanceof ChatMessageCell) {
                    cell = (ChatMessageCell) child;
                    mcell = cell;
                    isAnimatingBounds = cell.transitionParams.animateBackgroundBoundsInner;
                }
                if (!SizeNotifierFrameLayout.drawingBlur && (child.getY() > getMeasuredHeight() || child.getY() + child.getMeasuredHeight() < cilpTop) && !isAnimatingBounds || child.getVisibility() == View.INVISIBLE || child.getVisibility() == View.GONE) {
                    skipDraw = true;
                }

                MessageObject.GroupedMessages group = null;

                if (child instanceof ChatMessageCell) {
                    cell = (ChatMessageCell) child;
                    if (animateSendingViews.contains(cell)) {
                        skipDraw = true;
                    }
                    MessageObject.GroupedMessagePosition position = cell.getCurrentPosition();
                    group = cell.getCurrentMessagesGroup();
                    if (position != null) {
                        if (position.pw != position.spanSize && position.spanSize == 1000 && position.siblingHeights == null && group.hasSibling) {
                            clipLeft = cell.getBackgroundDrawableLeft();
                        } else if (position.siblingHeights != null) {
                            clipBottom = child.getBottom() - AndroidUtilities.dp(1 + (cell.isPinnedBottom() ? 1 : 0));
                        }
                    }
                    if (cell.needDelayRoundProgressDraw()) {
                        drawLaterRoundProgressCell = cell;
                    }
                    if (!skipDraw && scrimView instanceof ChatMessageCell && scrimViewTask == null) {
                        ChatMessageCell cell2 = (ChatMessageCell) scrimView;
                        if (cell2.getCurrentMessagesGroup() != null && cell2.getCurrentMessagesGroup() == group) {
                            skipDraw = true;
                        }
                    }
                    if (skipDraw) {
                        cell.getPhotoImage().skipDraw();
                    }
                } else if (child instanceof ChatActionCell) {
                    actionCell = (ChatActionCell) child;
                    cell = null;
                } else {
                    cell = null;
                }
                if (clipLeft != 0) {
                    canvas.save();
                } else if (clipBottom != 0) {
                    canvas.save();
                }

                if (skipDraw) {
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (DownscaleScrollableNoiseSuppressor.isRecordingCanvas(canvas)) {
                            skipDraw = false;
                        }
                    }*/
                    skipDraw = false;
                }

                boolean result;
                if (!skipDraw) {
                    boolean clipToGroupBounds = (cell != null && !cell.transitionParams.needsStopClipping) && (group != null && group.transitionParams.backgroundChangeBounds);
                    if (clipToGroupBounds) {
                        canvas.save();
                        float x = cell.getNonAnimationTranslationX(true);
                        float l = (group.transitionParams.left + x + group.transitionParams.offsetLeft);
                        float t = (group.transitionParams.top + group.transitionParams.offsetTop);
                        float r = (group.transitionParams.right + x + group.transitionParams.offsetRight);
                        float b = (group.transitionParams.bottom + group.transitionParams.offsetBottom);

                        canvas.clipRect(
                                l + AndroidUtilities.dp(4),
                                t + AndroidUtilities.dp(4),
                                r - AndroidUtilities.dp(4),
                                b - AndroidUtilities.dp(4)
                        );
                    }
                    if (cell != null && cell.transitionParams.needsStopClipping) {
                        canvas.save();
                        canvas.translate(cell.getX(), cell.getY());
                        cell.drawInternal(canvas);
                        canvas.restore();
                        result = cell.transitionParams.animateChange;
                    } else if (cell != null && clipToGroupBounds) {
                        cell.clipToGroupBounds = true;
                        result = super.drawChild(canvas, child, drawingTime);
                        cell.clipToGroupBounds = false;
                    } else {
                        result = super.drawChild(canvas, child, drawingTime);
                    }
                    if (clipToGroupBounds) {
                        canvas.restore();
                    }
                    if (cell != null && cell.hasOutboundsContent()) {
                        canvas.save();
                        canvas.translate(cell.getX(), cell.getY() + cell.getPaddingTopAnimated());
                        cell.drawOutboundsContent(canvas);
                        canvas.restore();
                    } else if (actionCell != null) {
                        canvas.save();
                        canvas.translate(actionCell.getX(), actionCell.getY());
                        actionCell.drawOutboundsContent(canvas);
                        canvas.restore();
                    }
                } else {
                    result = false;
                }
                if (clipLeft != 0 || clipBottom != 0) {
                    canvas.restore();
                }

                if (child.getTranslationY() != 0) {
                    canvas.save();
                    canvas.translate(0, child.getTranslationY());
                }

                if (cell != null) {
                    cell.drawCheckBox(canvas);
                }

                if (child.getTranslationY() != 0) {
                    canvas.restore();
                }

                if (child.getTranslationY() != 0) {
                    canvas.save();
                    canvas.translate(0, child.getTranslationY());
                }

                if (cell != null) {
                    final MessageObject message = cell.getMessageObject();
                    final MessageObject.GroupedMessagePosition position = cell.getCurrentPosition();
                    if (!skipDraw) {
                        if (position != null || cell.getTransitionParams().animateBackgroundBoundsInner) {
                            if (position == null || (position.last || position.minX == 0 && position.minY == 0)) {
                                if (position == null || position.last) {
                                    drawTimeAfter.add(cell);
                                }
                                if ((position == null || (position.minX == 0 && position.minY == 0)) && cell.hasNameLayout()) {
                                    drawNamesAfter.add(cell);
                                }
                            }
                            if (position != null || cell.getTransitionParams().transformGroupToSingleMessage || cell.getTransitionParams().animateBackgroundBoundsInner) {
                                if (position == null || (position.flags & cell.captionFlag()) != 0) {
                                    drawCaptionAfter.add(cell);
                                }
                                if (position == null || (position.flags & MessageObject.POSITION_FLAG_BOTTOM) != 0 && (position.flags & MessageObject.POSITION_FLAG_LEFT) != 0) {
                                    drawReactionsAfter.add(cell);
                                }
                            }
                        }

                        if (videoPlayerContainer != null && (message.isRoundVideo() || message.isVideo()) && !message.isVoiceTranscriptionOpen() && MediaController.getInstance().isPlayingMessage(message)) {
                            ImageReceiver imageReceiver = cell.getPhotoImage();
                            float newX = imageReceiver.getImageX() + cell.getX();
                            float newY = cell.getY() + cell.getPaddingTop() + imageReceiver.getImageY() + chatListView.getY() - videoPlayerContainer.getTop();
                            if (videoPlayerContainer.getTranslationX() != newX || videoPlayerContainer.getTranslationY() != newY) {
                                videoPlayerContainer.setTranslationX(newX);
                                videoPlayerContainer.setTranslationY(newY);
                                fragmentView.invalidate();
                                videoPlayerContainer.invalidate();
                            }
                        }
                    }
                }
                if (mcell != null) {
                    final MessageObject message = mcell.getMessageObject();
                    final MessageObject.GroupedMessagePosition position = mcell.getCurrentPosition();
                    final ImageReceiver imageReceiver = mcell.getAvatarImage();
                    if (imageReceiver != null && getSideMenuAlpha() < 1.f) {
                        final MessageObject.GroupedMessages groupedMessages = getValidGroupedMessage(message);
                        boolean updateVisibility = !mcell.getMessageObject().deleted && chatListView.getChildAdapterPosition(child) != RecyclerView.NO_POSITION;

                        boolean replaceAnimation = chatListView.isFastScrollAnimationRunning() || (groupedMessages != null && groupedMessages.transitionParams.backgroundChangeBounds);
                        int top = (replaceAnimation ? child.getTop() : (int) child.getY()) + child.getPaddingTop();
                        if (mcell.drawPinnedBottom()) {
                            int p;
                            if (mcell.willRemovedAfterAnimation()) {
                                p = chatScrollHelper.positionToOldView.indexOfValue(child);
                                if (p >= 0) {
                                    p = chatScrollHelper.positionToOldView.keyAt(p);
                                }
                            } else {
                                ViewHolder holder = chatListView.getChildViewHolder(child);
                                p = holder.getAdapterPosition();
                            }

                            if (p >= 0) {
                                int nextPosition;
                                if (groupedMessages != null && position != null) {
                                    int idx = groupedMessages.posArray.indexOf(position);
                                    int size = groupedMessages.posArray.size();
                                    if ((position.flags & MessageObject.POSITION_FLAG_BOTTOM) != 0) {
                                        nextPosition = p - size + idx;
                                    } else {
                                        nextPosition = p - 1;
                                        for (int a = idx + 1; a < size; a++) {
                                            if (groupedMessages.posArray.get(a).minY > position.maxY) {
                                                break;
                                            } else {
                                                nextPosition--;
                                            }
                                        }
                                    }
                                } else {
                                    nextPosition = p - 1;
                                }
                                if (mcell.willRemovedAfterAnimation()) {
                                     View view = chatScrollHelper.positionToOldView.get(nextPosition);
                                     if (view != null) {
                                         if (child.getTranslationY() != 0) {
                                             canvas.restore();
                                         }
                                         imageReceiver.setVisible(false, false);
                                         return result;
                                     }
                                } else {
                                    ViewHolder holder = chatListView.findViewHolderForAdapterPosition(nextPosition);
                                    if (holder != null) {
                                        if (child.getTranslationY() != 0) {
                                            canvas.restore();
                                        }
                                        imageReceiver.setVisible(false, false);
                                        return result;
                                    }
                                }
                            }
                        }
                        float tx = mcell.getSlidingOffsetX() + mcell.getCheckBoxTranslation();
                        int y = 0;
                        y += replaceAnimation ? child.getTop() : child.getY();
                        if (mcell instanceof ChatMessageCell) {
                            y += ((ChatMessageCell) mcell).getPaddingTopAnimated();
                        } else {
                            y += child.getPaddingTop();
                        }
                        if (mcell instanceof ChatMessageCell) {
                            y += cell.getLayoutHeight() + cell.getTransitionParams().deltaBottom;
                        } else {
                            y += child.getHeight();
                        }
                        int maxY = chatListView.getMeasuredHeight() - chatListView.getPaddingBottom();
                        boolean canUpdateTx = false;
                        if (mcell instanceof ChatMessageCell) {
                            final ChatMessageCell cmcell = (ChatMessageCell) mcell;
                            canUpdateTx = cmcell.isCheckBoxVisible() && tx == 0;
                            if (cmcell.isPlayingRound() || cmcell.getTransitionParams().animatePlayingRound) {
                                if (cmcell.getTransitionParams().animatePlayingRound) {
                                    float progressLocal = cmcell.getTransitionParams().animateChangeProgress;
                                    if (!cmcell.isPlayingRound()) {
                                        progressLocal = 1f - progressLocal;
                                    }
                                    int fromY = y;
                                    int toY = Math.min(y, maxY);
                                    y = (int) (fromY * progressLocal + toY * (1f - progressLocal));
                                }
                            } else {
                                if (y > maxY) {
                                    y = maxY;
                                }
                            }
                        } else {
                            if (y > maxY) {
                                y = maxY;
                            }
                        }

                        if (!replaceAnimation && child.getTranslationY() != 0) {
                            canvas.restore();
                        }
                        if (mcell.drawPinnedTop()) {
                            int p;
                            if (mcell.willRemovedAfterAnimation()) {
                                p = chatScrollHelper.positionToOldView.indexOfValue(child);
                                if (p >= 0) {
                                    p = chatScrollHelper.positionToOldView.keyAt(p);
                                }
                            } else {
                                ViewHolder holder = chatListView.getChildViewHolder(child);
                                p = holder.getAdapterPosition();
                            }
                            if (p >= 0) {
                                int tries = 0;
                                while (true) {
                                    if (tries >= 20) {
                                        break;
                                    }
                                    tries++;

                                    int prevPosition;
                                    if (groupedMessages != null && position != null) {
                                        int idx = groupedMessages.posArray.indexOf(position);
                                        if (idx < 0) {
                                            break;
                                        }
                                        int size = groupedMessages.posArray.size();
                                        if ((position.flags & MessageObject.POSITION_FLAG_TOP) != 0) {
                                            prevPosition = p + idx + 1;
                                        } else {
                                            prevPosition = p + 1;
                                            for (int a = idx - 1; a >= 0; a--) {
                                                if (groupedMessages.posArray.get(a).maxY < position.minY) {
                                                    break;
                                                } else {
                                                    prevPosition++;
                                                }
                                            }
                                        }
                                    } else {
                                        prevPosition = p + 1;
                                    }
                                    if (mcell.willRemovedAfterAnimation()) {
                                        final View view = chatScrollHelper.positionToOldView.get(prevPosition);
                                        if (view != null) {
                                            top = view.getTop() + view.getPaddingTop();
                                            if (view instanceof IMessageCell) {
                                                mcell = (IMessageCell) view;
                                                float newTx = mcell.getSlidingOffsetX() + mcell.getCheckBoxTranslation();
                                                if (canUpdateTx && newTx > 0) {
                                                    tx = newTx;
                                                }
                                                if (!mcell.drawPinnedTop()) {
                                                    break;
                                                } else {
                                                    p = prevPosition;
                                                }
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    } else {
                                        final ViewHolder holder = chatListView.findViewHolderForAdapterPosition(prevPosition);
                                        if (holder != null) {
                                            top = holder.itemView.getTop() + holder.itemView.getPaddingTop();
                                            if (holder.itemView instanceof ChatMessageCell) {
                                                mcell = (IMessageCell) holder.itemView;
                                                float newTx = mcell.getSlidingOffsetX() + mcell.getCheckBoxTranslation();
                                                if (canUpdateTx && newTx > 0) {
                                                    tx = newTx;
                                                }
                                                if (!mcell.drawPinnedTop()) {
                                                    break;
                                                } else {
                                                    p = prevPosition;
                                                }
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (y - dp(48) < top) {
                            y = top + dp(48);
                        }
                        if (!mcell.drawPinnedBottom()) {
                            int cellBottom;
                            if (replaceAnimation) {
                                cellBottom = child.getBottom();
                            } else {
                                cellBottom = (int) (mcell.getY() + mcell.getMeasuredHeight() + mcell.getDeltaBottom());
                            }
                            if (y > cellBottom) {
                                y = cellBottom;
                            }
                        }
                        canvas.save();
                        if (tx != 0) {
                            canvas.translate(tx, 0);
                        }
                        if (mcell instanceof ChatMessageCell) {
                            final ChatMessageCell chatMessageCell = (ChatMessageCell) mcell;
                            if (chatMessageCell.getCurrentMessagesGroup() != null) {
                                if (chatMessageCell.getCurrentMessagesGroup().transitionParams.backgroundChangeBounds) {
                                    y -= chatMessageCell.getTranslationY();
                                }
                            }
                        }
                        if (updateVisibility) {
                            imageReceiver.setImageY(y - dp(44));
                        }
                        if (mcell.shouldDrawAlphaLayer()) {
                            imageReceiver.setAlpha((1f - getSideMenuAlpha()) * mcell.getAlpha());
                            canvas.scale(
                                mcell.getScaleX(), mcell.getScaleY(),
                                mcell.getX() + mcell.getPivotX(),
                                mcell.getY() + (mcell.getHeight() >> 1)
                            );
                        } else {
                            imageReceiver.setAlpha(1f - getSideMenuAlpha());
                        }
                        if (updateVisibility) {
                            imageReceiver.setVisible(true, false);
                        }
                        if (getSideMenuAlpha() > 0f) {
                            canvas.scale(1f - getSideMenuAlpha(), 1f - getSideMenuAlpha(), imageReceiver.getImageX2(), imageReceiver.getImageY2());
                            canvas.translate(dp(24) * getSideMenuAlpha(), 0f);
                        }
                        // imageReceiver.draw(canvas);
                        cell.drawStatusWithImage(canvas, imageReceiver, AndroidUtilities.dp(7));
                        canvas.restore();

                        if (!replaceAnimation && child.getTranslationY() != 0) {
                            canvas.save();
                        }
                    }
                }

                if (child.getTranslationY() != 0) {
                    canvas.restore();
                }
                return result;
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                if (currentEncryptedChat != null) {
                    return;
                }
                super.onInitializeAccessibilityNodeInfo(info);
                AccessibilityNodeInfo.CollectionInfo collection = info.getCollectionInfo();
                if (collection != null) {
                    info.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(collection.getRowCount(), 1, false));
                }
            }

            @Override
            public AccessibilityNodeInfo createAccessibilityNodeInfo() {
                if (currentEncryptedChat != null) {
                    return null;
                }
                return super.createAccessibilityNodeInfo();
            }
        };
        chatListView.addEdgeEffectListener(() -> invalidateMergedVisibleBlurredPositionsAndSources(BLUR_INVALIDATE_FLAG_SCROLL | BLUR_INVALIDATE_FLAG_CLIP));
        if (currentEncryptedChat != null) {
            chatListView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        }
        chatListView.setHideIfEmpty(false);
        chatListView.setAccessibilityEnabled(false);
        chatListView.setNestedScrollingEnabled(false);
        chatListView.setInstantClick(true);
        chatListView.setDisableHighlightState(true);
        chatListView.setTag(1);
        chatListView.setVerticalScrollBarEnabled(!SharedConfig.chatBlurEnabled());
        chatListView.setAdapter(chatAdapter = new ChatActivityAdapter(context));
        chatListView.setClipToPadding(false);
        if (ChatObject.isMonoForum(currentChat) || ChatObject.areTabsEnabled(currentChat)) {
            chatListView.setClipChildren(false);
        }
        chatListView.setAnimateEmptyView(true, RecyclerListView.EMPTY_VIEW_ANIMATION_TYPE_ALPHA_SCALE);
        chatListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        chatListViewPaddingsAnimator = new ChatListViewPaddingsAnimator(chatListView);
        chatListViewPaddingTop = 0;
        paddingTopHeight = 0;
        botDraftHeightController.setRecyclerView(chatListView);
        invalidateChatListViewTopPadding();
        if (MessagesController.getGlobalMainSettings().getBoolean("view_animations", false)) {
            chatListItemAnimator = new ChatListItemAnimator(this, chatListView, themeDelegate) {

                Runnable finishRunnable;

                @Override
                public void checkIsRunning() {
                    if (scrollAnimationIndex == -1) {
                        scrollAnimationIndex = getNotificationCenter().setAnimationInProgress(scrollAnimationIndex, allowedNotificationsDuringChatListAnimations, false);
                    }
                }

                @Override
                public void onAnimationStart() {
                    scrollAnimationIndex = getNotificationCenter().setAnimationInProgress(scrollAnimationIndex, allowedNotificationsDuringChatListAnimations, false);
                    if (finishRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(finishRunnable);
                        finishRunnable = null;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("chatItemAnimator disable notifications");
                    }
                    chatActivityEnterView.getAdjustPanLayoutHelper().runDelayedAnimation();
                    chatActivityEnterView.runEmojiPanelAnimation();
                }

                @Override
                protected void onAllAnimationsDone() {
                    super.onAllAnimationsDone();
                    if (finishRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(finishRunnable);
                        finishRunnable = null;
                    }
                    AndroidUtilities.runOnUIThread(finishRunnable = () -> {
                        finishRunnable = null;
                        if (scrollAnimationIndex != -1) {
                            getNotificationCenter().onAnimationFinish(scrollAnimationIndex);
                            scrollAnimationIndex = -1;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("chatItemAnimator enable notifications");
                        }
                    });
                }


                @Override
                public void endAnimations() {
                    super.endAnimations();
                    if (finishRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(finishRunnable);
                    }
                    AndroidUtilities.runOnUIThread(finishRunnable = () -> {
                        finishRunnable = null;
                        if (scrollAnimationIndex != -1) {
                            getNotificationCenter().onAnimationFinish(scrollAnimationIndex);
                            scrollAnimationIndex = -1;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("chatItemAnimator enable notifications");
                        }
                    });
                }
            };
            chatListItemAnimator.setOnSnapMessage(this::supportsThanosEffect, this::getChatThanosEffect);
        }

        chatLayoutManager = new GridLayoutManagerFixed(context, 1000, LinearLayoutManager.VERTICAL, !reversed) {

            boolean computingScroll;

            @Override
            public int getStartForFixGap() {
                int padding = (int) chatListViewPaddingTop;
                return padding;
            }

            @Override
            protected int getParentStart() {
                if (computingScroll) {
                    return (int) chatListViewPaddingTop;
                }
                return 0;
            }

            @Override
            public int getStartAfterPadding() {
                if (computingScroll) {
                    return (int) chatListViewPaddingTop;
                }
                return super.getStartAfterPadding();
            }

            @Override
            public int getTotalSpace() {
                if (computingScroll) {
                    return (int) (getHeight() - chatListViewPaddingTop - getPaddingBottom());
                }
                return super.getTotalSpace();
            }

            @Override
            public int computeVerticalScrollExtent(RecyclerView.State state) {
                computingScroll = true;
                int r = super.computeVerticalScrollExtent(state);
                computingScroll = false;
                return r;
            }

            @Override
            public int computeVerticalScrollOffset(RecyclerView.State state) {
                computingScroll = true;
                int r = super.computeVerticalScrollOffset(state);
                computingScroll = false;
                return r;
            }

            @Override
            public int computeVerticalScrollRange(RecyclerView.State state) {
                computingScroll = true;
                int r = super.computeVerticalScrollRange(state);
                computingScroll = false;
                return r;
            }

            @Override
            public void scrollToPositionWithOffset(int position, int offset, boolean bottom) {
                if (!bottom) {
                    offset = (int) (offset - getPaddingTop() + chatListViewPaddingTop);
                }
                super.scrollToPositionWithOffset(position, offset, bottom);
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                scrollByTouch = false;
                LinearSmoothScrollerCustom linearSmoothScroller = new LinearSmoothScrollerCustom(recyclerView.getContext(), LinearSmoothScrollerCustom.POSITION_MIDDLE);
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }

            @Override
            public boolean shouldLayoutChildFromOpositeSide(View child) {
                if (child instanceof ChatMessageCell) {
                    return !((ChatMessageCell) child).getMessageObject().isOutOwner();
                }
                return false;
            }


            @Override
            protected boolean hasSiblingChild(int position) {
                if (position >= chatAdapter.messagesStartRow && position < chatAdapter.messagesEndRow) {
                    int index = position - chatAdapter.messagesStartRow;
                    if (index >= 0 && index < chatAdapter.getMessages().size()) {
                        MessageObject message = chatAdapter.getMessages().get(index);
                        MessageObject.GroupedMessages group = getValidGroupedMessage(message);
                        if (group != null) {
                            MessageObject.GroupedMessagePosition pos = group.getPosition(message);
                            if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == 0) {
                                return false;
                            }
                            int count = group.posArray.size();
                            for (int a = 0; a < count; a++) {
                                MessageObject.GroupedMessagePosition p = group.posArray.get(a);
                                if (p == pos) {
                                    continue;
                                }
                                if (p.minY <= pos.minY && p.maxY >= pos.minY) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    super.onLayoutChildren(recycler, state);
                } else {
                    try {
                        super.onLayoutChildren(recycler, state);
                    } catch (Exception e) {
                        FileLog.e(e);
                        AndroidUtilities.runOnUIThread(() -> chatAdapter.notifyDataSetChanged(false));
                    }
                }
            }

            /*
            @Override
            public boolean canScrollVertically() {
                return !isInPollAddOptionMode() && super.canScrollVertically();
            }
            */

            @Override
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (!NekoConfig.disableSwipeToNext.Bool() && dy < 0 && pullingDownOffset != 0) {
                    pullingDownOffset += dy;
                    if (pullingDownOffset < 0) {
                        dy = (int) pullingDownOffset;
                        pullingDownOffset = 0;
                        chatListView.invalidate();
                    } else {
                        dy = 0;
                    }
                }

                int n = chatListView.getChildCount();
                int scrolled = 0;
                boolean foundTopView = false;
                for (int i = 0; i < n; i++) {
                    View child = chatListView.getChildAt(i);
                    float padding = chatListViewPaddingTop;
                    if (chatListView.getChildAdapterPosition(child) == (reversed ? 0 : chatAdapter.getItemCount() - 1)) {
                        int dyLocal = dy;
                        if (child.getTop() - dy > padding) {
                            dyLocal = (int) (child.getTop() - padding);
                        }
                        scrolled = super.scrollVerticallyBy(dyLocal, recycler, state);
                        foundTopView = true;
                        break;
                    }
                }
                if (!foundTopView) {
                    scrolled = super.scrollVerticallyBy(dy, recycler, state);
                }
                final boolean allowPullingDownScroll = !NekoConfig.disableSwipeToNext.Bool() && !isInPollAddOptionMode() && !hasSelectedMessages();
                if (allowPullingDownScroll && dy > 0 && scrolled == 0 && (ChatObject.isChannel(currentChat) && !currentChat.megagroup || isTopic && !UserObject.isBotForum(currentUser)) && chatMode != MODE_SAVED && chatMode != MODE_WELCOME_MESSAGES && chatMode != MODE_SCHEDULED && chatListView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && !chatListView.isFastScrollAnimationRunning() && !chatListView.isMultiselect() && !isReport()) {
                    if (pullingDownOffset == 0 && pullingDownDrawable != null) {
                        if (nextChannels != null && !nextChannels.isEmpty()) {
                            pullingDownDrawable.updateDialog(nextChannels.get(0));
                        } else if (isTopic) {
                            pullingDownDrawable.updateTopic();
                        } else {
                            pullingDownDrawable.updateDialog();
                        }
                    }
                    if (pullingDownBackAnimator != null) {
                        pullingDownBackAnimator.removeAllListeners();
                        pullingDownBackAnimator.cancel();
                    }

                    float k;
                    if (pullingDownOffset < AndroidUtilities.dp(110)) {
                        float progress = pullingDownOffset / AndroidUtilities.dp(110);
                        k = 0.65f * (1f - progress) + 0.45f * progress;
                    } else if (pullingDownOffset < AndroidUtilities.dp(160)) {
                        float progress = (pullingDownOffset - AndroidUtilities.dp(110)) / AndroidUtilities.dp(50);
                        k = 0.45f * (1f - progress) + 0.05f * progress;
                    } else {
                        k = 0.05f;
                    }

                    pullingDownOffset += dy * k;
                    ReactionsEffectOverlay.onScrolled((int) (dy * k));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && scrollableViewNoiseSuppressor != null) {
                        scrollableViewNoiseSuppressor.onScrolled(0, (dy * k));
                    }
                    chatListView.invalidate();
                }
                if (pullingDownOffset == 0) {
                    chatListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                } else {
                    chatListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                }
                if (pullingDownDrawable != null) {
                    animatorPullingDownContainerVisibility.setValue(pullingDownOffset > 0 && chatListView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING, true);
                }
                return scrolled;
            }
        };
        chatLayoutManager.setSpanSizeLookup(new GridLayoutManagerFixed.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position >= chatAdapter.messagesStartRow && position < chatAdapter.messagesEndRow) {
                    int idx = position - chatAdapter.messagesStartRow;
                    if (idx >= 0 && idx < chatAdapter.getMessages().size()) {
                        MessageObject message = chatAdapter.getMessages().get(idx);
                        MessageObject.GroupedMessages groupedMessages = getValidGroupedMessage(message);
                        if (groupedMessages != null) {
                            return groupedMessages.getPosition(message).spanSize;
                        }
                    }
                }
                return 1000;
            }
        });
        chatListView.setLayoutManager(chatLayoutManager);
        chatListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 0;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    MessageObject.GroupedMessages group = cell.getCurrentMessagesGroup();
                    if (group != null) {
                        MessageObject.GroupedMessagePosition position = cell.getCurrentPosition();
                        if (position != null && position.siblingHeights != null) {
                            float maxHeight = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
                            int h = cell.getExtraInsetHeight();
                            for (int a = 0; a < position.siblingHeights.length; a++) {
                                h += (int) Math.ceil(maxHeight * position.siblingHeights[a]);
                            }
                            h += (position.maxY - position.minY) * Math.round(7 * AndroidUtilities.density);
                            int count = group.posArray.size();
                            for (int a = 0; a < count; a++) {
                                MessageObject.GroupedMessagePosition pos = group.posArray.get(a);
                                if (pos.minY != position.minY || pos.minX == position.minX && pos.maxX == position.maxX && pos.minY == position.minY && pos.maxY == position.maxY) {
                                    continue;
                                }
                                if (pos.minY == position.minY) {
                                    h -= (int) Math.ceil(maxHeight * pos.ph) - AndroidUtilities.dp(4);
                                    break;
                                }
                            }
                            outRect.bottom = -h;
                        }
                    }
                }
            }
        });
        chatListView.setOnItemLongClickListener(onItemLongClickListener);
        chatListView.setOnItemClickListener(onItemClickListener);
        chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            private float totalDy = 0;
            private boolean scrollUp;
            private final int scrollValue = AndroidUtilities.dp(100);

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (pollHintCell != null) {
                        pollHintView.showForMessageCell(pollHintCell, -1, pollHintX, pollHintY, true);
                        pollHintCell = null;
                    }
                    scrollingFloatingDate = false;
                    scrollingFloatingTopic = false;
                    scrollingChatListView = false;
                    checkTextureViewPosition = false;
                    hideFloatingDateView(true);
                    hideFloatingTopicView(true);
                    if (SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_LOW) {
                        scrolling = true;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startSpoilers);
                    chatListView.setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);
                    textSelectionHelper.stopScrolling();
                    updateVisibleRows();
                    invalidateMergedVisibleBlurredPositionsAndSources(BLUR_INVALIDATE_FLAG_SCROLL);
                    scrollByTouch = false;
                } else {
                    if (groupEmojiPackHint != null && groupEmojiPackHint.shown()) {
                        groupEmojiPackHint.hide();
                    }
                    if (searchOtherButton != null && searchOtherButton.getVisibility() == View.VISIBLE && isKeyboardVisible()) {
                        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                    }
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        wasManualScroll = true;
                        scrollingChatListView = true;
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        if (NekoConfig.hideKeyboardOnChatScroll.Bool()) {
                            if (isKeyboardVisible()) {
                                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                            } else if (chatActivityEnterView != null) {
                                chatActivityEnterView.hidePopup(true);
                            }
                        }
                        pollHintCell = null;
                        wasManualScroll = true;
                        scrollingFloatingDate = true;
                        scrollingFloatingTopic = true;
                        checkTextureViewPosition = true;
                        scrollingChatListView = true;
                    }
                    if (SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_LOW) {
                        scrolling = false;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopSpoilers);

                    if (selectionReactionsOverlay != null && selectionReactionsOverlay.isVisible()) {
                        selectionReactionsOverlay.setHiddenByScroll(true);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final ChatActivity chatToUpdate = parentChatActivity != null ? parentChatActivity : ChatActivity.this;

                if (isFeedSearch()) {
                    feedIntegration().onScrolled(dy);
                }

                chatListView.invalidate();
                if (contentView != null) {
                    contentView.updateBlurContent();
                }
                if (chatListThanosEffect != null) {
                    chatListThanosEffect.scroll(dx, dy);
                }
                scrollUp = dy < 0;
                int firstVisibleItem = chatLayoutManager.findFirstVisibleItemPosition();
                if (dy != 0 && (scrollByTouch && recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) || recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (forceNextPinnedMessageId != 0) {
                        if ((!scrollUp || forceScrollToFirst)) {
                            forceNextPinnedMessageId = 0;
                        } else if (!chatListView.isFastScrollAnimationRunning() && firstVisibleItem != RecyclerView.NO_POSITION) {
                            int lastVisibleItem = chatLayoutManager.findLastVisibleItemPosition();
                            MessageObject messageObject = null;
                            boolean foundForceNextPinnedView = false;
                            for (int i = lastVisibleItem; i >= firstVisibleItem; i--) {
                                View view = chatLayoutManager.findViewByPosition(i);
                                if (view instanceof ChatMessageCell) {
                                    messageObject = ((ChatMessageCell) view).getMessageObject();
                                } else if (view instanceof ChatActionCell) {
                                    messageObject = ((ChatActionCell) view).getMessageObject();
                                }
                                if (messageObject != null) {
                                    if (forceNextPinnedMessageId == messageObject.getId()) {
                                        foundForceNextPinnedView = true;
                                        break;
                                    }
                                }
                            }
                            if (!foundForceNextPinnedView && messageObject != null && messageObject.getId() < forceNextPinnedMessageId) {
                                forceNextPinnedMessageId = 0;
                            }
                        }
                    }
                }
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
                    forceScrollToFirst = false;
                    if (!wasManualScroll && dy != 0) {
                        wasManualScroll = true;
                    }
                }
                if (dy != 0) {
                    invalidateMergedVisibleBlurredPositionsAndSources(BLUR_INVALIDATE_FLAG_SCROLL);
                    contentView.invalidateBlur();
                    hideHints(true);
                }
                if (dy != 0 && scrollingFloatingDate && !currentFloatingTopIsNotMessage) {
                    if (highlightMessageId != Integer.MAX_VALUE) {
                        removeSelectedMessageHighlight();
                        updateVisibleRows();
                    }
                    showFloatingDateView(true);
                }
                if (isAllChats() && dy != 0 && scrollingFloatingTopic && !currentFloatingTopIsNotMessage) {
                    if (highlightMessageId != Integer.MAX_VALUE) {
                        removeSelectedMessageHighlight();
                        updateVisibleRows();
                    }
                    showFloatingTopicView(true);
                }
                checkScrollForLoad(true);
                if (firstVisibleItem != RecyclerView.NO_POSITION) {
                    int totalItemCount = chatAdapter.getItemCount();
                    if (firstVisibleItem == 0 && forwardEndReached[0]) {
                        if (dy >= 0) {
                            canShowPagedownButton = false;
                            updatePagedownButtonVisibility(true);
                        }
                    } else {
                        final boolean isPageDownButtonVisible = sideControlsButtonsLayout.isButtonVisible(
                            ChatActivitySideControlsButtonsLayout.BUTTON_PAGE_DOWN);

                        if (dy > 0) {
                            if (!isPageDownButtonVisible) {
                                totalDy += dy;
                                if (totalDy > scrollValue) {
                                    totalDy = 0;
                                    canShowPagedownButton = true;
                                    updatePagedownButtonVisibility(true);
                                    pagedownButtonShowedByScroll = true;
                                }
                            }
                        } else {
                            if (pagedownButtonShowedByScroll && isPageDownButtonVisible) {
                                totalDy += dy;
                                if (totalDy < -scrollValue) {
                                    canShowPagedownButton = false;
                                    updatePagedownButtonVisibility(true);
                                    totalDy = 0;
                                }
                            }
                        }
                    }
                }
                invalidateMessagesVisiblePart();
                textSelectionHelper.onParentScrolled();
                emojiAnimationsOverlay.onScrolled(dy);
                ReactionsEffectOverlay.onScrolled(dy);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && chatToUpdate.scrollableViewNoiseSuppressor != null) {
                    chatToUpdate.scrollableViewNoiseSuppressor.onScrolled(dx, dy);
                }

                checkTranslation(false);

                if (savedMessagesTagHint != null) {
                    if (savedMessagesTagHint.shown()) {
                        savedMessagesTagHint.hide();
                    } else if (!savedMessagesTagHintShown) {
                        lastScrollTime = System.currentTimeMillis();
                        AndroidUtilities.cancelRunOnUIThread(ChatActivity.this::checkSavedMessagesTagHint);
                        AndroidUtilities.runOnUIThread(ChatActivity.this::checkSavedMessagesTagHint, 2000);
                    }
                }
                if (videoConversionTimeHint != null && videoConversionTimeHint.shown()) {
                    videoConversionTimeHint.hide();
                }
                if (botMessageHint != null && botMessageHint.shown()) {
                    botMessageHint.hide();
                } else {
                    AndroidUtilities.cancelRunOnUIThread(ChatActivity.this::checkBotMessageHint);
                    AndroidUtilities.runOnUIThread(ChatActivity.this::checkBotMessageHint, 2000);
                }
                if (timeHint != null && timeHint.shown()) {
                    timeHint.hide();
                }
                if (factCheckHint != null) {
                    factCheckHint.hide();
                }
                if (chatActivityEnterView != null) {
                    chatActivityEnterView.hideHints();
                }
                if (starReactionsOverlay != null) {
                    starReactionsOverlay.invalidate();
                }
                if (botDraftHeightController != null) {
                    botDraftHeightController.onScroll();
                }
            }
        });

        contentView.addView(chatListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        chatActivityFadeView = new ChatActivityFadeView(context);
        chatActivityFadeView.setup(navbarContentDrawableFactory);
        chatActivityFadeView.setFadeHeightTop(dp(48));
        chatActivityFadeView.setFadeHeightBottom(dp(48));
        contentView.addView(chatActivityFadeView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        if (false/* && getDialogId() != getUserConfig().getClientUserId()*/) {
            selectionReactionsOverlay = new ChatSelectionReactionMenuOverlay(this, context);
            contentView.addView(selectionReactionsOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        animatingImageView = new ClippingImageView(context);
        animatingImageView.setVisibility(View.GONE);
        contentView.addView(animatingImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        progressView = new FrameLayout(context);
        progressView.setVisibility(View.INVISIBLE);
        contentView.addView(progressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        progressView2 = new View(context) {
            private final RectF rect = new RectF();
         …62152 tokens truncated…null, null, Theme.key_avatar_nameInMessageCyan));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageBlue));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessagePink));

        MessageDrawable msgInDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgIn);
        MessageDrawable msgInMediaDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgInMedia);
        MessageDrawable msgInSelectedDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgInSelected);
        MessageDrawable msgInMediaSelectedDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgInMediaSelected);
        MessageDrawable msgOutDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOut);
        MessageDrawable msgOutMediaDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOutMedia);
        MessageDrawable msgOutSelectedDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOutSelected);
        MessageDrawable msgOutMediaSelectedDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOutMediaSelected);
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, new Drawable[]{msgInDrawable, msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{msgInSelectedDrawable, msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));

        if (msgInDrawable != null) {
            themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
            themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
            themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, msgOutDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
            themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, msgOutMediaDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
        }

        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{msgOutDrawable, msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        if (!themeDelegate.isThemeChangeAvailable(false)) {
            themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{msgOutDrawable, msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
            themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{msgOutDrawable, msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
            themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{msgOutDrawable, msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        }
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{msgOutSelectedDrawable, msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{msgOutSelectedDrawable, msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleGradientSelectedOverlay));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, getThemedPaint(Theme.key_paint_chatActionText), null, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, getThemedPaint(Theme.key_paint_chatActionText), null, null, Theme.key_chat_serviceLink));

        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_botCardDrawable, getThemedDrawable(Theme.key_drawable_shareIcon), getThemedDrawable(Theme.key_drawable_replyIcon), getThemedDrawable(Theme.key_drawable_botInline), getThemedDrawable(Theme.key_drawable_botLink), getThemedDrawable(Theme.key_drawable_botLock), getThemedDrawable(Theme.key_drawable_botInvite), getThemedDrawable(Theme.key_drawable_goIcon), getThemedDrawable(Theme.key_drawable_commentSticker)}, null, Theme.key_chat_serviceIcon));

        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackgroundSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, null, null, Theme.key_chat_messageTextIn));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, null, null, Theme.key_chat_messageLinkIn, null));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageLinkOut, null));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgNoSoundDrawable}, null, Theme.key_chat_mediaTimeText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutCheck)}, null, Theme.key_chat_outSentCheck));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutCheckSelected)}, null, Theme.key_chat_outSentCheckSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutCheckRead), getThemedDrawable(Theme.key_drawable_msgOutHalfCheck)}, null, Theme.key_chat_outSentCheckRead));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutCheckReadSelected), getThemedDrawable(Theme.key_drawable_msgOutHalfCheckSelected)}, null, Theme.key_chat_outSentCheckReadSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSentClock));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSentClockSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSentClock));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSentClockSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgStickerHalfCheck), getThemedDrawable(Theme.key_drawable_msgStickerCheck), getThemedDrawable(Theme.key_drawable_msgStickerClock), getThemedDrawable(Theme.key_drawable_msgStickerViews), getThemedDrawable(Theme.key_drawable_msgStickerReplies), getThemedDrawable(Theme.key_drawable_msgStickerPinned)}, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaSentClock));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutViews), getThemedDrawable(Theme.key_drawable_msgOutReplies), getThemedDrawable(Theme.key_drawable_msgOutPinned)}, null, Theme.key_chat_outViews));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutViewsSelected), getThemedDrawable(Theme.key_drawable_msgOutRepliesSelected), getThemedDrawable(Theme.key_drawable_msgOutPinnedSelected)}, null, Theme.key_chat_outViewsSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable, Theme.chat_msgInRepliesDrawable, Theme.chat_msgInPinnedDrawable}, null, Theme.key_chat_inViews));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable, Theme.chat_msgInRepliesSelectedDrawable, Theme.chat_msgInPinnedSelectedDrawable}, null, Theme.key_chat_inViewsSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable, Theme.chat_msgMediaRepliesDrawable, Theme.chat_msgMediaPinnedDrawable}, null, Theme.key_chat_mediaViews));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutMenu)}, null, Theme.key_chat_outMenu));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutMenuSelected)}, null, Theme.key_chat_outMenuSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, Theme.key_chat_inMenu));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, Theme.key_chat_inMenuSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, Theme.key_chat_mediaMenu));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutInstant)}, null, Theme.key_chat_outInstant));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_commentDrawable, Theme.chat_commentArrowDrawable}, null, Theme.key_chat_inInstant));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutCallAudio), getThemedDrawable(Theme.key_drawable_msgOutCallVideo)}, null, Theme.key_chat_outInstant));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable(Theme.key_drawable_msgOutCallAudioSelected), getThemedDrawable(Theme.key_drawable_msgOutCallVideoSelected)}, null, Theme.key_chat_outInstant));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallDrawable, null, Theme.key_chat_inInstant));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallSelectedDrawable, null, Theme.key_chat_inInstantSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, null, Theme.key_chat_outGreenCall));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, null, Theme.key_fill_RedNormal));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, null, Theme.key_chat_inGreenCall));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, Theme.key_chat_sentError));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, Theme.key_chat_sentErrorIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, selectedBackgroundDelegate, Theme.key_chat_selectedBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, Theme.key_chat_previewDurationText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, Theme.key_chat_previewGameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, Theme.key_chat_secretTimeText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, getThemedPaint(Theme.key_paint_chatBotButton), null, null, Theme.key_chat_botButtonText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, getThemedPaint(Theme.key_paint_chatTimeBackground), null, null, Theme.key_chat_mediaTimeBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inForwardedNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outForwardedNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPsaNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPsaNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inViaBotNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outViaBotNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerViaBotNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyLine));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyMessageText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewLine));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewLine));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSiteNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSiteNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaProgress));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioProgress));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioProgress));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSelectedProgress));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaTimeText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAdminText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAdminSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAdminText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAdminSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerformerText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerformerSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerformerText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerformerSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioTitleText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioTitleText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbar));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbar));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarFill));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioCacheSeekbar));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarFill));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioCacheSeekbar));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbar));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbar));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarFill));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarFill));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgress));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgress));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgressSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgressSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileNameText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackgroundSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackgroundSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaInfoText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, Theme.key_chat_linkSelectBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_outUrlPaint, null, null, Theme.key_chat_outReplyLine));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, Theme.key_chat_inReplyLine));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outLoader));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outMediaIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outLoaderSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outMediaIconSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLoader));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inMediaIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLoaderSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inMediaIconSelected));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLocationBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPollCorrectAnswer));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPollCorrectAnswer));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPollWrongAnswer));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPollWrongAnswer));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_pollHintDrawable[0]}, null, Theme.key_chat_inPreviewInstantText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_pollHintDrawable[1]}, null, Theme.key_chat_outPreviewInstantText));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_psaHelpDrawable[0]}, null, Theme.key_chat_inViews));
        themeDescriptions.add(new ThemeDescription(chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_psaHelpDrawable[1]}, null, Theme.key_chat_outViews));

        if (!themeDelegate.isThemeChangeAvailable(false)) {
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, null, null, Theme.key_chats_name));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, null, null, Theme.key_chats_secretName));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable}, null, Theme.key_chats_draft));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], null, null, Theme.key_chats_message_threeLines));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, null, null, Theme.key_chats_nameMessage_threeLines));
            themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chats_nameMessage));
            themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chats_attachMessage));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, Theme.dialogs_messagePrintingPaint, null, null, Theme.key_chats_actionMessage));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable}, null, Theme.key_chats_sentCheck));
            themeDescriptions.add(new ThemeDescription(messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentReadCheck));
        }

        themeDescriptions.add(new ThemeDescription(mentionContainer, 0, null, getThemedPaint(Theme.key_paint_chatComposeBackground), null, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(mentionContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
        themeDescriptions.add(new ThemeDescription(mentionContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowRoundDrawable}, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(searchContainer, 0, null, getThemedPaint(Theme.key_paint_chatComposeBackground), null, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(searchContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
        themeDescriptions.add(new ThemeDescription(bottomOverlay, 0, null, getThemedPaint(Theme.key_paint_chatComposeBackground), null, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(bottomOverlay, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));

        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, null, getThemedPaint(Theme.key_paint_chatComposeBackground), null, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelText));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_CURSORCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelCursor));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_glass_defaultText));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, null, null, null, Theme.key_chat_messagePanelSend));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, null, null, 24, null, Theme.key_chat_messagePanelSend));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"botButton"}, null, null, null, Theme.key_glass_defaultIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"botButton"}, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"notifyButton"}, null, null, null, Theme.key_glass_defaultIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatActivityEnterView.class}, new String[]{"scheduledButton"}, null, null, null, Theme.key_glass_defaultIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"scheduledButton"}, null, null, null, Theme.key_chat_recordedVoiceDot));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"scheduledButton"}, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"attachButton"}, null, null, null, Theme.key_glass_defaultIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"attachButton"}, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"suggestButton"}, null, null, null, Theme.key_glass_defaultIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"suggestButton"}, null, null, null, Theme.key_listSelector));
//        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"audioSendButton"}, null, null, null, Theme.key_glass_defaultIcon));
//        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"videoSendButton"}, null, null, null, Theme.key_glass_defaultIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"notifyButton"}, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"videoTimelineView"}, null, null, null, Theme.key_chat_messagePanelSend));
        //themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonImage"}, null, null, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"micDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"cameraDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"sendDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, null, null, null, null, Theme.key_chat_messagePanelVoiceLock));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, null, null, null, Theme.key_chat_messagePanelVoiceLockBackground));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockShadowDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLockShadow));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"recordDeleteImageView"}, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioBackground"}, null, null, null, Theme.key_chat_recordedVoiceBackground));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, null, null, null, null, Theme.key_chat_recordTime));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, null, null, null, null, Theme.key_chat_recordVoiceCancel));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, null, null, null, null, Theme.key_chat_recordVoiceCancel));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"cancelBotButton"}, null, null, null, Theme.key_chat_messagePanelCancelInlineBot));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"cancelBotButton"}, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"redDotPaint"}, null, null, null, Theme.key_chat_recordedVoiceDot));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paint"}, null, null, null, Theme.key_chat_messagePanelVoiceBackground));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"dotPaint"}, null, null, null, Theme.key_chat_emojiPanelNewTrending));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_recordedVoicePlayPause));

        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelBackground));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelShadowLine));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelEmptyText));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelIconSelected));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelStickerPackSelector));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelBackspace));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelTrendingTitle));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelTrendingDescription));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiBottomPanelIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiSearchIcon));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelStickerSetNameHighlight));
        themeDescriptions.add(new ThemeDescription(chatActivityEnterView != null ? chatActivityEnterView.getEmojiView() : null, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelStickerPackSelectorLine));

        if (chatActivityEnterView != null) {
            final TrendingStickersAlert trendingStickersAlert = chatActivityEnterView.getTrendingStickersAlert();
            if (trendingStickersAlert != null) {
                themeDescriptions.addAll(trendingStickersAlert.getThemeDescriptions());
            }
            themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, new Drawable[]{chatActivityEnterView.getStickersArrowDrawable()}, null, Theme.key_glass_defaultIcon));
        }

        for (int a = 0; a < 2; a++) {
            UndoView v = a == 0 ? undoView : topUndoView;
            themeDescriptions.add(new ThemeDescription(v, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
            themeDescriptions.add(new ThemeDescription(v, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, Theme.key_undo_cancelColor));
            themeDescriptions.add(new ThemeDescription(v, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, Theme.key_undo_cancelColor));
            themeDescriptions.add(new ThemeDescription(v, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(v, 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, null, null, null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(v, ThemeDescription.FLAG_LINKCOLOR, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, null, null, null, Theme.key_undo_cancelColor));
            themeDescriptions.add(new ThemeDescription(v, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(v, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, Theme.key_undo_infoColor));
            themeDescriptions.add(new ThemeDescription(v, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, null, null, null, Theme.key_undo_infoColor));
        }

        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackgroundPressed));

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerPerformer));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_returnToCallText));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose));

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_returnToCallBackground));

        themeDescriptions.add(new ThemeDescription(pinnedLineView, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_topPanelLine));
        themeDescriptions.add(new ThemeDescription(pinnedLineView, 0, null, null, null, selectedBackgroundDelegate, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(pinnedCounterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
        for (int a = 0; a < 2; a++) {
            themeDescriptions.add(new ThemeDescription(pinnedNameTextView[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
            themeDescriptions.add(new ThemeDescription(pinnedMessageTextView[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage));
        }
        themeDescriptions.add(new ThemeDescription(alertNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
        themeDescriptions.add(new ThemeDescription(alertTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage));
        themeDescriptions.add(new ThemeDescription(closePinned, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose));
        themeDescriptions.add(new ThemeDescription(pinnedListButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose));
        themeDescriptions.add(new ThemeDescription(closeReportSpam, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose));
        themeDescriptions.add(new ThemeDescription(addToContactsButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_addContact));
        themeDescriptions.add(new ThemeDescription(reportSpamButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_text_RedBold));
        themeDescriptions.add(new ThemeDescription(reportSpamButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_chat_addContact));

        themeDescriptions.add(new ThemeDescription(replyCloseImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null,
            chatActivityEnterView != null && chatActivityEnterView.isIOSInputStyle() ? Theme.key_actionBarActionModeDefaultIcon : Theme.key_glass_defaultIcon));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_replyPanelName));

        themeDescriptions.add(new ThemeDescription(searchCalendarButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons));
        themeDescriptions.add(new ThemeDescription(searchCalendarButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        themeDescriptions.add(new ThemeDescription(searchUserButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons));
        themeDescriptions.add(new ThemeDescription(searchUserButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));

        themeDescriptions.add(new ThemeDescription(bottomOverlayText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_secretChatStatusText));
        themeDescriptions.add(new ThemeDescription(bottomOverlayChatText, 0, null, null, null, null, Theme.key_glass_defaultText));
        themeDescriptions.add(new ThemeDescription(bottomOverlayChatText, 0, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground));
        themeDescriptions.add(new ThemeDescription(bottomOverlayChatText, 0, null, null, null, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(bottomOverlayProgress, 0, null, null, null, null, Theme.key_featuredStickers_buttonText));

        themeDescriptions.add(new ThemeDescription(bigEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText));

        themeDescriptions.add(new ThemeDescription(progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_chat_serviceText));

        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, null, null, null, Theme.key_chat_unreadMessagesStartBackground));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_chat_unreadMessagesStartArrowIcon));
        themeDescriptions.add(new ThemeDescription(chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_unreadMessagesStartText));

        themeDescriptions.add(new ThemeDescription(progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground));
        themeDescriptions.add(new ThemeDescription(emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground));
        themeDescriptions.add(new ThemeDescription(bigEmptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground));

        if (mentionContainer != null) {
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotSwitchCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_botSwitchToInlineText));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"usernameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, new Drawable[]{Theme.chat_inlineResultFile, Theme.chat_inlineResultAudio, Theme.chat_inlineResultLocation}, null, Theme.key_chat_inlineResultIcon));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioProgress));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress));
            themeDescriptions.add(new ThemeDescription(mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_divider));
        }
        themeDescriptions.add(new ThemeDescription(gifHintTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_gifSaveHintBackground));
        themeDescriptions.add(new ThemeDescription(gifHintTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_gifSaveHintText));


        themeDescriptions.add(new ThemeDescription(noSoundHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_gifSaveHintText));
        themeDescriptions.add(new ThemeDescription(noSoundHintView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{HintView.class}, new String[]{"imageView"}, null, null, null, Theme.key_chat_gifSaveHintText));
        themeDescriptions.add(new ThemeDescription(noSoundHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"arrowImageView"}, null, null, null, Theme.key_chat_gifSaveHintBackground));

        themeDescriptions.add(new ThemeDescription(forwardHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_gifSaveHintText));
        themeDescriptions.add(new ThemeDescription(forwardHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"arrowImageView"}, null, null, null, Theme.key_chat_gifSaveHintBackground));

        themeDescriptions.add(new ThemeDescription(floatingDateView, 0, null, null, null, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(floatingDateView, 0, null, null, null, null, Theme.key_chat_serviceBackground));
        themeDescriptions.add(new ThemeDescription(infoTopView, 0, null, null, null, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(infoTopView, 0, null, null, null, null, Theme.key_chat_serviceBackground));

        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachIcon));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachGalleryBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachIcon));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachAudioBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachIcon));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachIcon));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachContactBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachContactText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachIcon));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachLocationBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachIcon));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachPollBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachEmptyDrawable}, null, Theme.key_chat_attachEmptyImage));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_attachPhotoBackground));

        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_dialogBackgroundGray));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_dialogTextGray2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_dialogScrollGlow));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_dialogGrayLine));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_dialogButtonSelector));

        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));

        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_outTextSelectionHighlight));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inTextSelectionHighlight));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_TextSelectionCursor));

        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayGreen1));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayGreen2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayBlue1));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayBlue2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGreen1));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGreen2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelBlue1));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelBlue2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGray));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientMuted));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientMuted2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientUnmuted));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientUnmuted2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient3));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertMutedByAdmin));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertMutedByAdmin2));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_outReactionButtonBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inReactionButtonBackground));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inReactionButtonText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_outReactionButtonText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inReactionButtonTextSelected));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inReactionButtonTextSelected));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, selectedBackgroundDelegate, Theme.key_chat_BlurAlpha));

        if (chatActivityEnterView != null && chatActivityEnterView.botCommandsMenuContainer != null) {
            themeDescriptions.add(new ThemeDescription(chatActivityEnterView.botCommandsMenuContainer.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotCommandsMenuView.BotCommandView.class}, new String[]{"description"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            themeDescriptions.add(new ThemeDescription(chatActivityEnterView.botCommandsMenuContainer.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotCommandsMenuView.BotCommandView.class}, new String[]{"command"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        }
        if (pendingRequestsDelegate != null) {
            pendingRequestsDelegate.fillThemeDescriptions(themeDescriptions);
        }

        for (ThemeDescription description : themeDescriptions) {
            description.resourcesProvider = themeDelegate;
        }

        return themeDescriptions;
    }

    private void openRightsEdit(int action, TLRPC.User user, TLRPC.ChatParticipant participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean editingAdmin) {
        boolean[] needShowBulletin = new boolean[1];
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user.id, currentChat.id, adminRights, currentChat.default_banned_rights, bannedRights, rank, action, true, false, null, participant) {
            @Override
            public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                if (!isOpen && backward && needShowBulletin[0] && BulletinFactory.canShowBulletin(ChatActivity.this)) {
                    BulletinFactory.createPromoteToAdminBulletin(ChatActivity.this, user.first_name).show();
                }
            }
        };
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            @Override
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                if (action == 0) {
                    if (participant instanceof TLRPC.TL_chatChannelParticipant channelParticipant1) {
                        if (rights == 1) {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                            channelParticipant1.channelParticipant.flags |= 4;
                        } else {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        channelParticipant1.channelParticipant.inviter_id = getUserConfig().getClientUserId();
                        channelParticipant1.channelParticipant.peer = getMessagesController().getPeer(participant.user_id);
                        channelParticipant1.channelParticipant.date = participant.date;
                        channelParticipant1.channelParticipant.banned_rights = rightsBanned;
                        channelParticipant1.channelParticipant.admin_rights = rightsAdmin;
                        channelParticipant1.channelParticipant.rank = rank;
                    } else if (participant != null) {
                        TLRPC.ChatParticipant newParticipant;
                        if (rights == 1) {
                            newParticipant = new TLRPC.TL_chatParticipantAdmin();
                        } else {
                            newParticipant = new TLRPC.TL_chatParticipant();
                        }
                        newParticipant.user_id = participant.user_id;
                        newParticipant.date = participant.date;
                        newParticipant.inviter_id = participant.inviter_id;
                        int index = chatInfo.participants.participants.indexOf(participant);
                        if (index >= 0) {
                            chatInfo.participants.participants.set(index, newParticipant);
                        }
                    }
                    if (rights == 1 && !editingAdmin) {
                        needShowBulletin[0] = true;
                    }
                } else if (action == 1) {
                    if (rights == 0) {
                        if (currentChat.megagroup && chatInfo != null && chatInfo.participants != null) {
                            for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                                TLRPC.ChannelParticipant p = ((TLRPC.TL_chatChannelParticipant) chatInfo.participants.participants.get(a)).channelParticipant;
                                if (MessageObject.getPeerId(p.peer) == participant.user_id) {
                                    chatInfo.participants_count--;
                                    chatInfo.participants.participants.remove(a);
                                    break;
                                }
                            }
                            if (chatInfo != null && chatInfo.participants != null) {
                                for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                                    TLRPC.ChatParticipant p = chatInfo.participants.participants.get(a);
                                    if (p.user_id == participant.user_id) {
                                        chatInfo.participants.participants.remove(a);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void didChangeOwner(TLRPC.User user) {
                undoView.showWithAction(-currentChat.id, UndoView.ACTION_OWNER_TRANSFERED_GROUP, user);
            }
        });
        presentFragment(fragment);
    }

    private void doAdminActions(int action) {
        if (selectedParticipant == null) {
            return;
        }
        TLRPC.ChatParticipant participant = selectedParticipant;

        final TLRPC.ChannelParticipant channelParticipant;
        TLRPC.User user = getMessagesController().getUser(participant.user_id);
        boolean editingAdmin;
        if (ChatObject.isChannel(currentChat)) {
            channelParticipant = ((TLRPC.TL_chatChannelParticipant) participant).channelParticipant;
            editingAdmin = channelParticipant instanceof TLRPC.TL_channelParticipantAdmin;
        } else {
            channelParticipant = null;
            editingAdmin = participant instanceof TLRPC.TL_chatParticipantAdmin;
        }
        if (action == 1 && (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin || participant instanceof TLRPC.TL_chatParticipantAdmin)) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setTitle(getString(R.string.NagramX));
            builder2.setMessage(formatString(R.string.AdminWillBeRemoved, ContactsController.formatName(user.first_name, user.last_name)));
            builder2.setPositiveButton(getString(R.string.OK), (dialog, which) -> {
                if (channelParticipant != null) {
                    openRightsEdit(action, user, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, editingAdmin);
                } else {
                    openRightsEdit(action, user, participant, null, null, "", editingAdmin);
                }
            });
            builder2.setNegativeButton(getString(R.string.Cancel), null);
            showDialog(builder2.create());
        } else {
            if (channelParticipant != null) {
                openRightsEdit(action, user, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, editingAdmin);
            } else {
                openRightsEdit(action, user, participant, null, null, "", editingAdmin);
            }
        }
    }

    public ChatAvatarContainer getAvatarContainer() {
        return avatarContainer;
    }

    public void openedInstantly() {
        fragmentOpened = true;
        fragmentBeginToShow = true;
        fragmentTransition = null;
        contentView.invalidate();
        contentView.setSkipBackgroundDrawing(false);
        toPullingDownTransition = false;
        fragmentView.setAlpha(1f);
        avatarContainer.setTranslationY(0);
        avatarContainer.getAvatarImageView().setScaleX(1f);
        avatarContainer.getAvatarImageView().setScaleY(1f);
        avatarContainer.getAvatarImageView().setAlpha(1f);
    }

    @Override
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        if (isOpen && fromPullingDownTransition && getParentLayout() != null && getParentLayout().getFragmentStack().size() > 1) {
            BaseFragment previousFragment = getParentLayout().getFragmentStack().get(getParentLayout().getFragmentStack().size() - 2);
            if (previousFragment instanceof ChatActivity) {
                wasManualScroll = true;
                ChatActivity previousChat = (ChatActivity) previousFragment;
                previousChat.setTransitionToChatActivity(this);
                fragmentView.setAlpha(0);
                contentView.setSkipBackgroundDrawing(true);
                avatarContainer.setTranslationY(AndroidUtilities.dp(8));
                avatarContainer.getAvatarImageView().setAlpha(0);
                avatarContainer.getAvatarImageView().setTranslationY(-AndroidUtilities.dp(8));
                toPullingDownTransition = true;
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);

                if (chatActivityEnterView != null) {
                    chatActivityEnterView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(999999, View.MeasureSpec.AT_MOST));
                }
                if (bottomOverlay != null) {
                    bottomOverlay.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(999999, View.MeasureSpec.AT_MOST));
                }
                int currentBottom = Math.max(chatActivityEnterView == null ? 0 : chatActivityEnterView.getMeasuredHeight(), bottomOverlay == null ? 0 : bottomOverlay.getMeasuredHeight());
                int prevBottom = Math.max(previousChat.chatActivityEnterView == null ? 0 : previousChat.chatActivityEnterView.getMeasuredHeight(), bottomOverlay == null ? 0 : bottomOverlay.getMeasuredHeight());

                pullingBottomOffset = -(prevBottom - currentBottom);

                valueAnimator.addUpdateListener(valueAnimator1 -> {
                    float progress = (float) valueAnimator1.getAnimatedValue();
                    previousChat.setTransitionToChatProgress(progress);
                    float y = AndroidUtilities.dp(8) * (1f - progress);
                    avatarContainer.setTranslationY(y);
                    avatarContainer.getAvatarImageView().setTranslationY(-y);
                    y = -AndroidUtilities.dp(8) * progress;
                    previousChat.avatarContainer.setTranslationY(y);
                    previousChat.avatarContainer.getAvatarImageView().setTranslationY(-y);
                    avatarContainer.getAvatarImageView().setScaleX(0.8f + 0.2f * progress);
                    avatarContainer.getAvatarImageView().setScaleY(0.8f + 0.2f * progress);
                    avatarContainer.getAvatarImageView().setAlpha(progress);
                    previousChat.avatarContainer.getAvatarImageView().setScaleX(0.8f + 0.2f * (1f - progress));
                    previousChat.avatarContainer.getAvatarImageView().setScaleY(0.8f + 0.2f * (1f - progress));
                    previousChat.avatarContainer.getAvatarImageView().setAlpha(1f - progress);
                    if (previousChat.chatActivityEnterView != null) {
                        // previousChat.chatActivityEnterView.setTranslationY(-pullingBottomOffset * progress);
                    }
                    if (previousChat.bottomOverlay != null) {
                        // previousChat.bottomOverlay.setTranslationY(-pullingBottomOffset * progress);
                    }

                    if (previousChat.topPanelLayout != null) {
                        previousChat.topPanelLayout.setAlpha(1f - progress);
                    }
                });

                updateChatListViewTopPadding();
                fragmentTransition = new AnimatorSet();
                fragmentTransition.addListener(new AnimatorListenerAdapter() {

                    int index;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        index = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(index, null);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fragmentOpened = true;
                        fragmentBeginToShow = true;
                        fragmentTransition = null;
                        AndroidUtilities.runOnUIThread(() -> {
                            NotificationCenter.getInstance(currentAccount).onAnimationFinish(index);
                        }, 32);

                        super.onAnimationEnd(animation);
                        contentView.invalidate();
                        contentView.setSkipBackgroundDrawing(false);
                        toPullingDownTransition = false;
                        previousChat.setTransitionToChatProgress(0);
                        previousChat.setTransitionToChatActivity(null);
                        fragmentView.setAlpha(1f);
                        callback.run();
                        avatarContainer.setTranslationY(0);
                        previousChat.avatarContainer.setTranslationY(0);
                        previousChat.avatarContainer.getAvatarImageView().setTranslationY(0);
                        avatarContainer.getAvatarImageView().setScaleX(1f);
                        avatarContainer.getAvatarImageView().setScaleY(1f);
                        avatarContainer.getAvatarImageView().setAlpha(1f);
                        previousChat.avatarContainer.getAvatarImageView().setScaleX(1f);
                        previousChat.avatarContainer.getAvatarImageView().setScaleY(1f);
                        previousChat.avatarContainer.getAvatarImageView().setAlpha(1f);

                        if (previousChat.topPanelLayout != null) {
                            previousChat.topPanelLayout.setAlpha(1f);
                        }
                    }
                });
                fragmentTransition.setDuration(300);
                fragmentTransition.setInterpolator(CubicBezierInterpolator.DEFAULT);
                fragmentTransition.playTogether(valueAnimator);
                AndroidUtilities.runOnUIThread(fragmentTransitionRunnable, 200);
                return fragmentTransition;
            }
        }
        if (switchFromTopics && getParentLayout() != null && getParentLayout().getFragmentStack().size() > 1) {
            BaseFragment previousFragment = getParentLayout().getFragmentStack().get(getParentLayout().getFragmentStack().size() - 2);
            if (!(previousFragment instanceof TopicsFragment)) {
                return null;
            }
            ValueAnimator valueAnimator = isOpen ? ValueAnimator.ofFloat(0f, 1f) : ValueAnimator.ofFloat(1f, 0f);
            int width = previousFragment.getFragmentView().getWidth();

            switchingFromTopicsProgress = isOpen ? 0f : 1f;
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    switchingFromTopicsProgress = (float) animation.getAnimatedValue();
                    contentView.invalidate();
                }
            });

            switchingFromTopics = true;
            if (actionBar != null) {
                actionBar.invalidate();
            }
            if (contentView != null) {
                contentView.invalidate();
            }

            fragmentTransition = new AnimatorSet();
            fragmentTransition.addListener(new AnimatorListenerAdapter() {

                int index;

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    index = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(index, null);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    fragmentOpened = true;
                    fragmentBeginToShow = true;
                    fragmentTransition = null;
                    if (isOpen) {
                        switchingFromTopics = false;
                    }
                    actionBar.invalidate();
                    contentView.invalidate();
                    AndroidUtilities.runOnUIThread(() -> {
                        NotificationCenter.getInstance(currentAccount).onAnimationFinish(index);
                    }, 32);
                    callback.run();
                }
            });
            fragmentTransition.setDuration(150);
            fragmentTransition.playTogether(valueAnimator);
            if (isOpen) {
                AndroidUtilities.runOnUIThread(fragmentTransitionRunnable, 200);
            } else {
                fragmentTransition.start();
            }
            return fragmentTransition;
        }
        return null;
    }

    private void setTransitionToChatActivity(ChatActivity chatActivity) {
        pullingDownAnimateToActivity = chatActivity;
    }

    private void setTransitionToChatProgress(float p) {
        pullingDownAnimateProgress = p;
        fragmentView.invalidate();
        chatListView.invalidate();
    }

    private void showChatThemeBottomSheet() {
        if (currentChat != null) {
            if (ChatObject.isMegagroup(currentChat)) {
                if (ChatObject.hasAdminRights(currentChat)) {
                    presentFragment(new GroupColorActivity(getDialogId()).setOnApplied(ChatActivity.this));
                }
            } else {
                if (ChatObject.canChangeChatInfo(currentChat)) {
                    presentFragment(new ChannelColorActivity(getDialogId()).setOnApplied(ChatActivity.this));
                }
            }
            return;
        }
        chatThemeBottomSheet = new ChatThemeBottomSheet(ChatActivity.this, themeDelegate);
        chatListView.setOnInterceptTouchListener(event -> true);
        setChildrenEnabled(contentView, false);
        showDialog(chatThemeBottomSheet, dialogInterface -> {
            chatThemeBottomSheet = null;
            chatListView.setOnInterceptTouchListener(null);
            setChildrenEnabled(contentView, true);
            ChatThemeController.getInstance(currentAccount).clearWallpaperThumbImages();
        });
    }

    private void setChildrenEnabled(View view, boolean isEnabled) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                setChildrenEnabled(viewGroup.getChildAt(i), isEnabled);
            }
        }
        if (view != chatListView && view != contentView) {
            view.setEnabled(isEnabled);
        }
    }

    private void checkThemeEmoticonOrWallpaper() {
        getNotificationCenter().doOnIdle(() -> {
            setChatThemeEmoticon(userInfo != null ? userInfo.theme : null);
//            if (emoticon == null && chatInfo != null) {
//                emoticon = chatInfo.theme_emoticon;
//            }
        });
    }

    private void setChatThemeEmoticon(final TLRPC.ChatTheme theme) {
        if (themeDelegate == null || parentThemeDelegate != null) {
            return;
        }
        ThemeKey key = ThemeKey.of(theme);
        ChatThemeController chatThemeController = ChatThemeController.getInstance(currentAccount);
        chatThemeController.setDialogTheme(dialog_id, theme, false);

        if (theme instanceof TLRPC.TL_chatThemeUniqueGift) {
            chatThemeController.putThemeIfNeeded(theme);
            EmojiThemes theme1 = chatThemeController.getTheme(key);
            if (theme1 == null) {
                // unreachable code?
                theme1 = new EmojiThemes(currentAccount, (TLRPC.TL_chatThemeUniqueGift) theme);
                theme1.initColors();
                theme1.loadPreviewColors(currentAccount);
            }

            themeDelegate.setCurrentTheme(theme1, themeDelegate.wallpaper, openAnimationStartTime != 0, null);
            return;
        }

        if (key != null && !key.isEmpty()) {
            chatThemeController.requestChatTheme(key, result -> {
                themeDelegate.setCurrentTheme(result, themeDelegate.wallpaper, openAnimationStartTime != 0, null);
            });
        }
        TLRPC.WallPaper wallPaper = chatThemeController.getDialogWallpaper(dialog_id);
        themeDelegate.setCurrentTheme(themeDelegate.chatTheme, wallPaper, openAnimationStartTime != 0, null);
    }

    @Override
    public Drawable getThemedDrawable(String drawableKey) {
        Drawable drawable = themeDelegate.getDrawable(drawableKey);
        return drawable != null ? drawable : super.getThemedDrawable(drawableKey);
    }

    @Override
    public Paint getThemedPaint(String paintKey) {
        Paint paint = themeDelegate.getPaint(paintKey);
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    public float getChatListViewPadding() {
        return chatListViewPaddingTop;
    }

    public FragmentContextView getFragmentContextView() {
        return fragmentContextView;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return themeDelegate;
    }

    public Runnable onThemeChange;

    public class ThemeDelegate implements Theme.ResourcesProvider, ChatActionCell.ThemeDelegate, MessagePreviewView.ResourcesDelegate {

        private final HashMap<String, Drawable> currentDrawables = new HashMap<>();
        private final HashMap<String, Paint> currentPaints = new HashMap<>();
        private final Matrix actionMatrix = new Matrix();

        private SparseIntArray currentColors = new SparseIntArray();
        private SparseIntArray animatingColors;
        private EmojiThemes chatTheme;
        private TLRPC.WallPaper wallpaper;
        private Drawable backgroundDrawable;
        private ValueAnimator patternIntensityAnimator;
        private Bitmap serviceBitmap;
        private Bitmap serviceBitmapSource;
        private Paint paint = new Paint();
        private Canvas serviceCanvas;
        private BitmapShader serviceShader;
        private BitmapShader serviceShaderSource;
        private boolean useSourceShader;
        private int currentColor;
        private boolean isDark;
        private AnimatorSet patternAlphaAnimator;

        MessageDrawable animatingMessageDrawable;
        MessageDrawable animatingMessageMediaDrawable;

        ThemeDelegate() {
            isDark = Theme.getActiveTheme().isDark();
            boolean setup = false;
            if (isThemeChangeAvailable(false)) {
                chatTheme = ChatThemeController.getInstance(currentAccount).getDialogTheme(dialog_id);
                wallpaper = ChatThemeController.getInstance(currentAccount).getDialogWallpaper(dialog_id);
                if (chatTheme != null || wallpaper != null) {
                    setup = true;
                    setupChatTheme(chatTheme, wallpaper, false, true);
                }
            }
            if (!setup && ThemeEditorView.getInstance() == null) {
                Theme.refreshThemeColors(true, true);
            } else {
                AndroidUtilities.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false, true, true));
            }
        }

        @Override
        public int getColor(int key) {
            if (animatingColors != null) {
                int index = animatingColors.indexOfKey(key);
                if (index >= 0) {
                    return animatingColors.valueAt(index);
                }
            }
            if (chatTheme == null) {
                return Theme.getColor(key);
            }
            int index = currentColors.indexOfKey(key);
            if (index >= 0) {
                return currentColors.valueAt(index);
            }

            int fallbackKey = Theme.getFallbackKey(key);
            if (fallbackKey >= 0) {
                index = currentColors.indexOfKey(fallbackKey);
                if (index >= 0) {
                    return currentColors.valueAt(index);
                }
            }
            return Theme.getColor(key);
        }

        @Override
        public int getCurrentColor(int key) {
            return getCurrentColor(key, false);
        }

        public int getCurrentColor(int key, boolean ignoreAnimation) {
            if (chatTheme == null && backgroundDrawable == null) {
                return Theme.getColor(key);
            }

            if (!ignoreAnimation && animatingColors != null) {
                int index = animatingColors.indexOfKey(key);
                if (index >= 0) {
                    return animatingColors.valueAt(index);
                }
            }
            if (currentColors != null) {
                int index = currentColors.indexOfKey(key);
                if (index >= 0) {
                    return currentColors.valueAt(index);
                }
            }
            return Theme.getColor(key);
        }

        @Override
        public void setAnimatedColor(int key, int color) {
            if (animatingColors != null) {
                animatingColors.put(key, color);
            }
        }

        @Override
        public void applyServiceShaderMatrix(int w, int h, float translationX, float translationY) {
            if (backgroundDrawable == null || serviceBitmap == null || serviceShader == null) {
                ChatActionCell.ThemeDelegate.super.applyServiceShaderMatrix(w, h, translationX, translationY);
            } else {
                if (useSourceShader) {
                    Theme.applyServiceShaderMatrix(serviceBitmapSource, serviceShaderSource, actionMatrix, w, h, translationX, translationY);
                } else {
                    Theme.applyServiceShaderMatrix(serviceBitmap, serviceShader, actionMatrix, w, h, translationX, translationY);
                }
            }
        }

        @Override
        public int getCurrentColor() {
            return backgroundDrawable != null ? currentColor : Theme.currentColor;
        }

        public boolean isGiftTheme() {
            EmojiThemes themes = getCurrentTheme();
            return themes != null && themes.getThemeGift() != null;
        }

        @Override
        public boolean hasGradientService() {
            return backgroundDrawable != null ? serviceShader != null : Theme.hasGradientService();
        }

        @Override
        public Drawable getDrawable(String drawableKey) {
            return !currentDrawables.isEmpty() ? currentDrawables.get(drawableKey) : null;
        }

        @Override
        public Paint getPaint(String paintKey) {
            return chatTheme != null || backgroundDrawable != null ? currentPaints.get(paintKey) : null;
        }

        public boolean isThemeChangeAvailable(boolean canEdit) {
            return currentEncryptedChat == null && (
                (!canEdit /*|| currentChat != null && ChatObject.isChannelAndNotMegaGroup(currentChat) && ChatObject.canChangeChatInfo(currentChat)*/) ||
                currentChat == null && currentUser != null && !currentUser.bot
            );
        }

        public EmojiThemes getCurrentTheme() {
            return chatTheme;
        }

        @Override
        public Drawable getWallpaperDrawable() {
            return backgroundDrawable != null ? backgroundDrawable : Theme.getCachedWallpaperNonBlocking();
        }

        @Override
        public boolean isWallpaperMotion() {
            return chatTheme != null ? false : Theme.isWallpaperMotion();
        }

        public void setCurrentTheme(final EmojiThemes chatTheme, TLRPC.WallPaper newWallpaper, boolean animated, Boolean forceDark) {
            setCurrentTheme(chatTheme, newWallpaper, animated, forceDark, false);
        }
        public void setCurrentTheme(final EmojiThemes chatTheme, TLRPC.WallPaper newWallpaper, boolean animated, Boolean forceDark, boolean force) {
            if (parentLayout == null || parentThemeDelegate != null) {
                return;
            }
            final EmojiThemes prevTheme = this.chatTheme;
            boolean newIsDark = forceDark != null ? forceDark : this.isDark;//Theme.getActiveTheme().isDark();
            ThemeKey newEmoticon = chatTheme != null ? chatTheme.getThemeKey() : null;
            ThemeKey oldEmoticon = this.chatTheme != null ? this.chatTheme.getThemeKey() : null;
            TLRPC.WallPaper oldWallpaper = this.wallpaper;
            if (!force && (!isThemeChangeAvailable(false) || (ThemeKey.equals(oldEmoticon, newEmoticon) && this.isDark == newIsDark && ChatThemeController.equals(newWallpaper, oldWallpaper)))) {
                return;
            }

            this.isDark = newIsDark;

            Theme.ThemeInfo currentTheme = newIsDark ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            ActionBarLayout.ThemeAnimationSettings animationSettings = new ActionBarLayout.ThemeAnimationSettings(currentTheme, currentTheme.currentAccentId, currentTheme.isDark(), !animated);

            if (this.chatTheme == null && wallpaper == null) {
                Drawable background = Theme.getCachedWallpaperNonBlocking();
                drawServiceGradient = background instanceof MotionBackgroundDrawable;
                initServiceMessageColors(background);
                startServiceTextColor = drawServiceGradient ? 0xffffffff : Theme.getColor(Theme.key_chat_serviceText);
                startServiceLinkColor = drawServiceGradient ? 0xffffffff : Theme.getColor(Theme.key_chat_serviceLink);
                startServiceButtonColor = drawServiceGradient ? 0xffffffff : Theme.getColor(Theme.key_chat_serviceLink);
                startServiceIconColor = drawServiceGradient ? 0xffffffff : Theme.getColor(Theme.key_chat_serviceIcon);
            } else if (drawServiceGradient && backgroundDrawable instanceof MotionBackgroundDrawable) {
                startServiceBitmap = ((MotionBackgroundDrawable) backgroundDrawable).getBitmap();
                final boolean forceRecolorServiceMessages = isGiftTheme() && isDark;
                if (forceRecolorServiceMessages) {
                    startServiceBitmap = Bitmap.createBitmap(startServiceBitmap);
                    Canvas tmpC = new Canvas(startServiceBitmap);
                    tmpC.drawColor(0xCC222222);
                }
            } else if (backgroundDrawable != null) {
                initServiceMessageColors(backgroundDrawable);
            }
            startServiceColor = currentServiceColor;
            startServiceTextColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceText, true);
            startServiceLinkColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceLink, true);
            startServiceButtonColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceLink, true);
            startServiceIconColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceIcon, true);

            if (chatTheme != null || newWallpaper != null) {
                int[] colors = AndroidUtilities.calcDrawableColor(backgroundDrawable);
                currentColor = colors[0];
                initDrawables();
                initPaints();
            }

            animationSettings.applyTheme = false;
            if (dialog_id < 0)
                animationSettings.applyTrulyTheme = false;
            animationSettings.afterStartDescriptionsAddedRunnable = () -> {
                setupChatTheme(chatTheme, newWallpaper, animated, true);
                initServiceMessageColors(backgroundDrawable);
                //updateBackground();
                if (contentView != null) {
                    contentView.invalidateBackground();
                }
            };
            if (animated) {
                animationSettings.animationProgress = new ActionBarLayout.ThemeAnimationSettings.onAnimationProgress() {
                    @Override
                    public void setProgress(float p) {
                        chatListView.invalidate();
                        animatingMessageDrawable.crossfadeProgress = p;
                        animatingMessageMediaDrawable.crossfadeProgress = p;
                        updateServiceMessageColor(p);
                    }
                };
                animationSettings.beforeAnimationRunnable = () -> {
                    animatingColors = new SparseIntArray();
                    animatingMessageDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOut);
                    animatingMessageDrawable.crossfadeFromDrawable = parentLayout.getMessageDrawableOutStart();
                    animatingMessageMediaDrawable = (MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOutMedia);
                    animatingMessageMediaDrawable.crossfadeFromDrawable = parentLayout.getMessageDrawableOutMediaStart();
                    animatingMessageDrawable.crossfadeProgress = 0f;
                    animatingMessageMediaDrawable.crossfadeProgress = 0f;
                    updateMessagesVisiblePart(false);
                    updateServiceMessageColor(0);
                };
                animationSettings.afterAnimationRunnable = () -> {
                    animatingMessageDrawable.crossfadeFromDrawable = null;
                    animatingMessageMediaDrawable.crossfadeFromDrawable = null;
                    animatingColors = null;
                    updateServiceMessageColor(1f);
                };
            } else {
                if (contentView != null) {
                    updateBackground();
                }
                animationSettings.afterStartDescriptionsAddedRunnable.run();
            }
            animationSettings.onlyTopFragment = true;
            animationSettings.resourcesProvider = this;
            animationSettings.duration = 250;
            parentLayout.animateThemedValues(animationSettings, null);
            if (onThemeChange != null) {
                onThemeChange.run();
            }
        }

        private void setupChatTheme(EmojiThemes chatTheme, TLRPC.WallPaper wallPaper, boolean withAnimation, boolean createNewResources) {
            if (parentThemeDelegate != null) return;

            this.chatTheme = chatTheme;
            this.wallpaper = wallPaper;

            Drawable prevDrawable = null;
            if (fragmentView != null) {
                prevDrawable = (contentView).getBackgroundImage();
            }
            final MotionBackgroundDrawable prevMotionDrawable = (prevDrawable instanceof MotionBackgroundDrawable) ? (MotionBackgroundDrawable) prevDrawable : null;
            final int prevPhase = prevMotionDrawable != null ? prevMotionDrawable.getPhase() : 0;

            if ((chatTheme == null || chatTheme.showAsDefaultStub) && wallPaper == null) {
                currentColor = Theme.getServiceMessageColor();
            }
            if (chatTheme == null && wallPaper == null) {
                currentColors = new SparseIntArray();
                currentPaints.clear();
                currentDrawables.clear();
                Drawable wallpaperDrawable = Theme.getCachedWallpaperNonBlocking();
                if (wallpaperDrawable instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) wallpaperDrawable).setPhase(prevPhase);
                }
                backgroundDrawable = null;//wallpaperDrawable;

                Theme.ThemeInfo activeTheme;
                if (Theme.getActiveTheme().isDark() == isDark) {
                    activeTheme = Theme.getActiveTheme();
                } else {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
                    String dayThemeName = preferences.getString("lastDayTheme", "Blue");
                    if (Theme.getTheme(dayThemeName) == null || Theme.getTheme(dayThemeName).isDark()) {
                        dayThemeName = "Blue";
                    }
                    String nightThemeName = preferences.getString("lastDarkTheme", "Dark Blue");
                    if (Theme.getTheme(nightThemeName) == null || !Theme.getTheme(nightThemeName).isDark()) {
                        nightThemeName = "Dark Blue";
                    }
                    activeTheme = isDark ? Theme.getTheme(nightThemeName) : Theme.getTheme(dayThemeName);
                }

                Theme.applyTheme(activeTheme, false, isDark);
                initServiceMessageColors(backgroundDrawable);
            } else {
                if (ApplicationLoader.applicationContext != null) {
                    Theme.createChatResources(ApplicationLoader.applicationContext, false);
                }
                if (chatTheme == null) {
                    currentColors = new SparseIntArray();
                } else {
                    currentColors = chatTheme.createColors(currentAccount, isDark ? 1 : 0);
                }
                if (!TextUtils.isEmpty(ChatThemeController.getWallpaperEmoticon(wallpaper))) {
                    backgroundDrawable = PreviewView.getBackgroundDrawable(backgroundDrawable, currentAccount, wallpaper, isDark);
                } else if (wallPaper != null) {
                    backgroundDrawable = ChatBackgroundDrawable.getOrCreate(backgroundDrawable, wallPaper, isDark);
                } else {
                    backgroundDrawable = getBackgroundDrawableFromTheme(chatTheme, prevPhase);
                }

                if (patternAlphaAnimator != null) {
                    patternAlphaAnimator.cancel();
                }
                if (withAnimation) {
                    patternAlphaAnimator = new AnimatorSet();
                    if (prevMotionDrawable != null) {
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
                        valueAnimator.addUpdateListener(animator -> prevMotionDrawable.setPatternAlpha((float) animator.getAnimatedValue()));
                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                prevMotionDrawable.setPatternAlpha(1f);
                            }
                        });
                        valueAnimator.setDuration(200);
                        patternAlphaAnimator.playTogether(valueAnimator);
                    }
                    if (backgroundDrawable instanceof MotionBackgroundDrawable) {
                        final MotionBackgroundDrawable currentBackgroundDrawable = (MotionBackgroundDrawable) backgroundDrawable;
                        currentBackgroundDrawable.setPatternAlpha(0f);
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                        valueAnimator.addUpdateListener(animator -> currentBackgroundDrawable.setPatternAlpha((float) animator.getAnimatedValue()));
                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                currentBackgroundDrawable.setPatternAlpha(1f);
                            }
                        });
                        valueAnimator.setDuration(250);
                        patternAlphaAnimator.playTogether(valueAnimator);
                    }
                    patternAlphaAnimator.start();
                }

                if (chatTheme == null && dialog_id >= 0) {
                    Theme.ThemeInfo activeTheme;
                    if (Theme.getActiveTheme().isDark() == isDark) {
                        activeTheme = Theme.getActiveTheme();
                    } else {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
                        String dayThemeName = preferences.getString("lastDayTheme", "Blue");
                        if (Theme.getTheme(dayThemeName) == null || Theme.getTheme(dayThemeName).isDark()) {
                            dayThemeName = "Blue";
                        }
                        String nightThemeName = preferences.getString("lastDarkTheme", "Dark Blue");
                        if (Theme.getTheme(nightThemeName) == null || !Theme.getTheme(nightThemeName).isDark()) {
                            nightThemeName = "Dark Blue";
                        }
                        activeTheme = isDark ? Theme.getTheme(nightThemeName) : Theme.getTheme(dayThemeName);
                    }

                    Theme.applyTheme(activeTheme, false, isDark);
                }
                if (createNewResources) {
                    int[] colors = AndroidUtilities.calcDrawableColor(backgroundDrawable);
                    currentColor = colors[0];
                    initDrawables();
                    initPaints();
                    initServiceMessageColors(backgroundDrawable);
                    updateServiceMessageColor(1f);
                }
            }
        }

        private void initDrawables() {
            Map<String, Drawable> chatDrawablesMap = Theme.getThemeDrawablesMap();
            for (Map.Entry<String, Drawable> entry : chatDrawablesMap.entrySet()) {
                Drawable drawable;
                switch (entry.getKey()) {
                    case Theme.key_drawable_msgIn:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, false, false, this);
                        break;
                    case Theme.key_drawable_msgInSelected:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, false, true, this);
                        break;
                    case Theme.key_drawable_msgInMedia:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, false, false, this);
                        break;
                    case Theme.key_drawable_msgInMediaSelected:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, false, true, this);
                        break;
                    case Theme.key_drawable_msgOut:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, true, false, this);
                        break;
                    case Theme.key_drawable_msgOutSelected:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, true, true, this);
                        break;
                    case Theme.key_drawable_msgOutMedia:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, true, false, this);
                        break;
                    case Theme.key_drawable_msgOutMediaSelected:
                        drawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, true, true, this);
                        break;
                    default:
                        drawable = entry.getValue();
                        Drawable.ConstantState constantState = drawable.getConstantState();
                        if (constantState != null) {
                            drawable = constantState.newDrawable().mutate();
                        } else {
                            drawable = null;
                        }
                        if (drawable != null) {
                            int colorKey = Theme.getThemeDrawableColorKey(entry.getKey());
                            if (colorKey >= 0) {
                                Theme.setDrawableColor(drawable, getColor(colorKey));
                            }
                        }
                }
                if (drawable != null) {
                    currentDrawables.put(entry.getKey(), drawable);
                }
            }
        }

        private void initPaints() {
            Map<String, Paint> chatPaintsMap = Theme.getThemePaintsMap();
            for (Map.Entry<String, Paint> entry : chatPaintsMap.entrySet()) {
                Paint oldPaint = entry.getValue();
                Paint newPaint;
                if (oldPaint instanceof TextPaint) {
                    newPaint = new TextPaint();
                    newPaint.setTextSize(oldPaint.getTextSize());
                    newPaint.setTypeface(oldPaint.getTypeface());
                } else {
                    newPaint = new Paint();
                }
                if ((oldPaint.getFlags() & Paint.ANTI_ALIAS_FLAG) != 0) {
                    newPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                }

                int colorKey = Theme.getThemePaintColorKey(entry.getKey());
                if (colorKey >= 0 && !Theme.key_paint_chatActionBackgroundDarken.equals(entry.getKey())) {
                    newPaint.setColor(getColor(colorKey));
                }
                currentPaints.put(entry.getKey(), newPaint);
            }
        }

        int startServiceTextColor;
        int startServiceLinkColor;
        int startServiceButtonColor;
        int startServiceIconColor;
        int startServiceColor;
        int startSelectedBackgroundColor;
        Bitmap startServiceBitmap;

        int currentServiceColor;
        boolean drawServiceGradient;
        boolean drawSelectedGradient;

        final Rect src = new Rect(), dst = new Rect();

        private void initServiceMessageColors(Drawable backgroundDrawable) {
            if (parentThemeDelegate != null) return;

            int[] result = AndroidUtilities.calcDrawableColor(backgroundDrawable);
            int currentServiceMessageColor = result[0];

            int serviceColor = getCurrentColor(Theme.key_chat_serviceBackground);
            int selectedBackgroundColor = getCurrentColor(Theme.key_chat_selectedBackground);
            int serviceColor2 = serviceColor;
            if (serviceColor == 0 || wallpaper != null) {
                serviceColor = currentServiceMessageColor;
            }
            currentServiceColor = serviceColor;

            float dimAmount = 0;
            if (backgroundDrawable instanceof ChatBackgroundDrawable) {
                dimAmount = ((ChatBackgroundDrawable) backgroundDrawable).getDimAmount();
                backgroundDrawable = ((ChatBackgroundDrawable) backgroundDrawable).getDrawable(false);
            }
            drawServiceGradient = (backgroundDrawable instanceof MotionBackgroundDrawable || backgroundDrawable instanceof BitmapDrawable) && SharedConfig.getDevicePerformanceClass() != SharedConfig.PERFORMANCE_CLASS_LOW;
            final boolean forceRecolorServiceMessages = isGiftTheme() && isDark;

            drawSelectedGradient = drawServiceGradient;

            if (drawServiceGradient) {
                if (backgroundDrawable instanceof BitmapDrawable) {
                    Bitmap source = ((BitmapDrawable) backgroundDrawable).getBitmap();
                    int w, h;
                    if (source.getWidth() > source.getHeight()) {
                        w = 40;
                        h = (int) ((float) w / source.getWidth() * source.getHeight());
                    } else {
                        h = 40;
                        w = (int) ((float) h / source.getHeight() * source.getWidth());
                    }
                    serviceBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    serviceCanvas = new Canvas(serviceBitmap);
                    src.set(0, 0, source.getWidth(), source.getHeight());
                    dst.set(0, 0, serviceBitmap.getWidth(), serviceBitmap.getHeight());
                    serviceCanvas.drawBitmap(source, src, dst, null);
                    Utilities.blurBitmap(serviceBitmap, 3);
                    serviceCanvas.drawColor(ColorUtils.setAlphaComponent(0xff000000, (int) (0xFF * dimAmount)));
                    serviceShader = new BitmapShader(serviceBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    serviceBitmapSource = Bitmap.createBitmap(serviceBitmap);
                    serviceShaderSource = new BitmapShader(serviceBitmapSource, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        serviceShader.setFilterMode(BitmapShader.FILTER_MODE_LINEAR);
                        serviceShaderSource.setFilterMode(BitmapShader.FILTER_MODE_LINEAR);
                    }
                    useSourceShader = true;
                } else {
                    serviceBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
                    serviceBitmapSource = ((MotionBackgroundDrawable) backgroundDrawable).getBitmap();

                    if (forceRecolorServiceMessages) {
                        serviceBitmapSource = Bitmap.createBitmap(serviceBitmapSource);
                        Canvas tmpC = new Canvas(serviceBitmapSource);
                        tmpC.drawColor(0xCC222222);
                    }

                    serviceCanvas = new Canvas(serviceBitmap);
                    src.set(0, 0, serviceBitmapSource.getWidth(), serviceBitmapSource.getHeight());
                    dst.set(0, 0, serviceBitmap.getWidth(), serviceBitmap.getHeight());
                    serviceCanvas.drawBitmap(serviceBitmapSource, src, dst, null);
                    serviceCanvas.drawColor(ColorUtils.setAlphaComponent(0xff000000, (int) (0xFF * dimAmount)));
                    if (forceRecolorServiceMessages) {
                        serviceCanvas.drawColor(0xCC222222);
                    }

                    serviceShader = new BitmapShader(serviceBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    serviceShaderSource = new BitmapShader(serviceBitmapSource, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        serviceShader.setFilterMode(BitmapShader.FILTER_MODE_LINEAR);
                        serviceShaderSource.setFilterMode(BitmapShader.FILTER_MODE_LINEAR);
                    }
                    useSourceShader = true;
                }
            } else {
                serviceBitmap = null;
                serviceShader = null;
                serviceBitmapSource = null;
                serviceCanvas = null;
                useSourceShader = false;
            }

            Paint actionBackgroundPaint = getPaint(Theme.key_paint_chatActionBackground);
            Paint actionBackgroundSelectedPaint = getPaint(Theme.key_paint_chatActionBackgroundSelected);
            Paint msgBackgroundSelectedPaint = getPaint(Theme.key_paint_chatMessageBackgroundSelected);

            if (actionBackgroundPaint != null) {
                Paint darkenPaint = currentPaints.get(Theme.key_paint_chatActionBackgroundDarken);
                if (darkenPaint == null) {
                    currentPaints.put(Theme.key_paint_chatActionBackgroundDarken, darkenPaint = new Paint(Paint.ANTI_ALIAS_FLAG));
                    darkenPaint.setColor(0);
                }
                if (drawServiceGradient) {
                    ColorMatrix colorMatrix = new ColorMatrix();
                    if (backgroundDrawable instanceof MotionBackgroundDrawable) {
                        float intensity = ((MotionBackgroundDrawable) backgroundDrawable).getIntensity();
                        if (intensity >= 0) {
                            colorMatrix.setSaturation(1.6f);
                            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isDark ? .97f : .92f);
                            AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, isDark ? +.12f : -.06f);
                        } else {
                            colorMatrix.setSaturation(1.1f);
                            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isDark ? .4f : .8f);
                            AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, isDark ? +.08f : -.06f);
                        }
                    } else {
                        colorMatrix.setSaturation(1.6f);
                        AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isDark ? .9f : .84f);
                        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, isDark ? +.04f : +.06f);
                    }

                    actionBackgroundPaint.setAlpha(0xff);
                    actionBackgroundPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                    actionBackgroundPaint.setShader(serviceShaderSource);
                    actionBackgroundPaint.setFilterBitmap(true);

                    actionBackgroundSelectedPaint.setAlpha(0xFF);
                    colorMatrix = new ColorMatrix(colorMatrix);
                    AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, +.26f);
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, .92f);
                    actionBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                    actionBackgroundSelectedPaint.setShader(serviceShaderSource);
                    actionBackgroundSelectedPaint.setFilterBitmap(true);
                    darkenPaint.setAlpha(0);
                } else {
                    actionBackgroundPaint.setColorFilter(null);
                    actionBackgroundPaint.setShader(null);
                    actionBackgroundSelectedPaint.setColorFilter(null);
                    actionBackgroundSelectedPaint.setShader(null);
                    darkenPaint.setAlpha(0x15);
                }
            }

            if (msgBackgroundSelectedPaint == null) {
                msgBackgroundSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                currentPaints.put(Theme.key_paint_chatMessageBackgroundSelected, msgBackgroundSelectedPaint);
            }
            if (drawSelectedGradient) {
                ColorMatrix colorMatrix2 = new ColorMatrix();
                AndroidUtilities.adjustSaturationColorMatrix(colorMatrix2, 2.5f);
                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix2, .75f);
                msgBackgroundSelectedPaint.setAlpha(64);
                msgBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix2));
                msgBackgroundSelectedPaint.setShader(serviceShaderSource);
                msgBackgroundSelectedPaint.setFilterBitmap(true);
            } else {
                if (selectedBackgroundColor == 0) {
                    selectedBackgroundColor = getColor(Theme.key_chat_selectedBackground);
                }
                msgBackgroundSelectedPaint.setColor(selectedBackgroundColor);
                msgBackgroundSelectedPaint.setColorFilter(null);
                msgBackgroundSelectedPaint.setShader(null);
            }
        }

        private void updateServiceMessageColor(float progress) {
            if (currentPaints.isEmpty()) {
                return;
            }
            Paint actionBackgroundPaint = getPaint(Theme.key_paint_chatActionBackground);
            Paint actionBackgroundSelectedPaint = getPaint(Theme.key_paint_chatActionBackgroundSelected);
            Paint msgBackgroundSelectedPaint = getPaint(Theme.key_paint_chatMessageBackgroundSelected);

            int serviceColor = currentServiceColor;
            int serviceTextColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceText, true);
            int serviceLinkColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceLink, true);
            int serviceButtonColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceLink, true);
            int serviceIconColor = drawServiceGradient ? 0xffffffff : getCurrentColor(Theme.key_chat_serviceIcon, true);
            if (progress != 1f) {
                serviceColor = ColorUtils.blendARGB(startServiceColor, serviceColor, progress);
                serviceTextColor = ColorUtils.blendARGB(startServiceTextColor, serviceTextColor, progress);
                serviceLinkColor = ColorUtils.blendARGB(startServiceLinkColor, serviceLinkColor, progress);
                serviceButtonColor = ColorUtils.blendARGB(startServiceButtonColor, serviceButtonColor, progress);
                serviceIconColor = ColorUtils.blendARGB(startServiceIconColor, serviceIconColor, progress);
            }
            if (actionBackgroundPaint != null && !drawServiceGradient) {
                actionBackgroundPaint.setColor(serviceColor);
                actionBackgroundSelectedPaint.setColor(serviceColor);
            }

            currentColor = serviceColor;

            Paint linkPaint = getPaint(Theme.key_paint_chatActionText);
            if (linkPaint != null) {
                ((TextPaint) linkPaint).linkColor = serviceLinkColor;
                getPaint(Theme.key_paint_chatActionText).setColor(serviceTextColor);
                getPaint(Theme.key_paint_chatBotButton).setColor(serviceButtonColor);
            }

            Theme.setDrawableColor(getDrawable(Theme.key_drawable_msgStickerCheck), serviceTextColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_msgStickerClock), serviceTextColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_msgStickerHalfCheck), serviceTextColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_msgStickerPinned), serviceTextColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_msgStickerReplies), serviceTextColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_msgStickerViews), serviceTextColor);

            Theme.setDrawableColor(getDrawable(Theme.key_drawable_botInline), serviceIconColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_botLink), serviceIconColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_botLock), serviceIconColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_botInvite), serviceIconColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_commentSticker), serviceIconColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_goIcon), serviceIconColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_replyIcon), serviceIconColor);
            Theme.setDrawableColor(getDrawable(Theme.key_drawable_shareIcon), serviceIconColor);

            if (serviceCanvas != null && serviceBitmapSource != null) {
                if (progress != 1f && startServiceBitmap != null) {
                    useSourceShader = false;
                    src.set(0, 0, startServiceBitmap.getWidth(), startServiceBitmap.getHeight());
                    dst.set(0, 0, serviceBitmap.getWidth(), serviceBitmap.getHeight());
                    serviceCanvas.drawBitmap(startServiceBitmap, src, dst, null);
                    paint.setAlpha((int) (255 * progress));
                    src.set(0, 0, serviceBitmapSource.getWidth(), serviceBitmapSource.getHeight());
                    dst.set(0, 0, serviceBitmap.getWidth(), serviceBitmap.getHeight());
                    serviceCanvas.drawBitmap(serviceBitmapSource, src, dst, paint);
                    if (actionBackgroundPaint != null) {
                        actionBackgroundPaint.setShader(serviceShader);
                        actionBackgroundSelectedPaint.setShader(serviceShader);
                    }
                    if (msgBackgroundSelectedPaint != null) {
                        msgBackgroundSelectedPaint.setShader(serviceShader);
                    }
                } else {
                    useSourceShader = true;
                    src.set(0, 0, serviceBitmapSource.getWidth(), serviceBitmapSource.getHeight());
                    dst.set(0, 0, serviceBitmap.getWidth(), serviceBitmap.getHeight());
                    serviceCanvas.drawBitmap(serviceBitmapSource, src, dst, null);
                    if (actionBackgroundPaint != null) {
                        actionBackgroundPaint.setShader(serviceShaderSource);
                        actionBackgroundSelectedPaint.setShader(serviceShaderSource);
                    }
                    if (msgBackgroundSelectedPaint != null) {
                        msgBackgroundSelectedPaint.setShader(serviceShaderSource);
                    }
                }
            }
        }

        private Drawable getBackgroundDrawableFromTheme(EmojiThemes chatTheme, int prevPhase) {
            Drawable drawable;
            if (chatTheme.showAsDefaultStub) {
                Theme.ThemeInfo themeInfo = EmojiThemes.getDefaultThemeInfo(isDark);
                SparseIntArray currentColors = chatTheme.getPreviewColors(currentAccount, isDark ? 1 : 0);
                String wallpaperLink = chatTheme.getWallpaperLink(isDark ? 1 : 0);
                Theme.BackgroundDrawableSettings settings = Theme.createBackgroundDrawable(themeInfo, currentColors, wallpaperLink, prevPhase, false);
                drawable = settings.wallpaper;
                drawable = new ColorDrawable(Color.BLACK);
            } else {
                int backgroundColor = getColor(Theme.key_chat_wallpaper);
                int gradientColor1 = getColor(Theme.key_chat_wallpaper_gradient_to1);
                int gradientColor2 = getColor(Theme.key_chat_wallpaper_gradient_to2);
                int gradientColor3 = getColor(Theme.key_chat_wallpaper_gradient_to3);

                MotionBackgroundDrawable motionDrawable = new MotionBackgroundDrawable();
                motionDrawable.setPatternBitmap(chatTheme.getWallpaper(isDark ? 1 : 0).settings.intensity);
                motionDrawable.setGiftDrawable(chatTheme.getEmojiAnimatedSticker());
                motionDrawable.setColors(backgroundColor, gradientColor1, gradientColor2, gradientColor3, 0,true);
                motionDrawable.setPhase(prevPhase);
                int patternColor = motionDrawable.getPatternColor();
                final boolean isDarkTheme = isDark;

                chatTheme.loadWallpaperGiftPattern(isDark ? 1 : 0, pair -> {
                    if (pair == null) {
                        return;
                    }
                    long themeId = pair.first;
                    Bitmap bitmap = pair.second;
                    if (this.chatTheme != null && themeId == this.chatTheme.getThemeId(isDark ? 1 : 0) && bitmap != null) {
                        motionDrawable.setGiftPatternBitmap(bitmap);
                    }
                });

                chatTheme.loadWallpaper(isDark ? 1 : 0, pair -> {
                    if (pair == null) {
                        return;
                    }
                    long themeId = pair.first;
                    Bitmap bitmap = pair.second.bitmap;
                    if (this.chatTheme != null && themeId == this.chatTheme.getThemeId(isDark ? 1 : 0) && bitmap != null) {
                        if (patternIntensityAnimator != null) {
                            patternIntensityAnimator.cancel();
                        }
                        int intensity = chatTheme.getWallpaper(isDarkTheme ? 1 : 0).settings.intensity;
                        motionDrawable.setPatternGiftPositions(pair.second.giftPatternPositions);
                        motionDrawable.setGiftPatternRandomSeed(wallpaperRandomSeed);
                        motionDrawable.setPatternBitmap(intensity, bitmap);
                        motionDrawable.setPatternColorFilter(patternColor);
                        patternIntensityAnimator = ValueAnimator.ofFloat(0, 1f);
                        patternIntensityAnimator.addUpdateListener(animator -> {
                            float value = (float) animator.getAnimatedValue();
                            motionDrawable.setPatternAlpha(value);
                        });
                        patternIntensityAnimator.setDuration(250);
                        patternIntensityAnimator.start();
                    }
                });
                drawable = motionDrawable;
            }
            return drawable;
        }

        public TLRPC.WallPaper getCurrentWallpaper() {
            return wallpaper;
        }
    }

    private void updateBackground() {
        if (contentView == null || parentThemeDelegate != null) {
            return;
        }
        if (themeDelegate.backgroundDrawable != null && contentView.getBackgroundImage() != null) {
            return;
        }
        if (contentView.getBackgroundImage() == null || AndroidUtilities.isTablet()) {
            final Drawable drawable = Theme.getCachedWallpaper();
            contentView.setBackgroundImage(drawable, Theme.isWallpaperMotion());
        }
    }

    private void updateBotHelpCellClick(BotHelpCell cell) {
        final CharSequence text = cell.getText();
        if (!TextUtils.isEmpty(text)) {
            String toLang = NekoConfig.translateToLang.String();
            cell.setOnClickListener(e -> {
                ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                Drawable shadowDrawable2 = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert4).mutate();
                shadowDrawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
                layout.setBackground(shadowDrawable2);

                final Runnable[] dismiss = new Runnable[3];
                ActionBarMenuSubItem copyButton = new ActionBarMenuSubItem(getContext(), true, false);
                copyButton.setTextAndIcon(getString(R.string.Copy), R.drawable.msg_copy);
                copyButton.setOnClickListener(e2 -> {
                    AndroidUtilities.addToClipboard(text);
                    createUndoView();
                    if (undoView == null) return;
                    undoView.showWithAction(0, UndoView.ACTION_MESSAGE_COPIED, null);
                    if (dismiss[0] != null) dismiss[0].run();
                });
                layout.addView(copyButton);

                ActionBarMenuSubItem translateButton = new ActionBarMenuSubItem(getContext(), false, false);
                translateButton.setTextAndIcon(LocaleController.getString(R.string.TranslateMessage), R.drawable.msg_translate);
                translateButton.setOnClickListener(e3 -> {
                    DialogTransKt.startTrans(getParentActivity(), text.toString());
                    if (dismiss[1] != null) dismiss[1].run();
                });
                translateButton.setOnLongClickListener(view -> {
                    Translator.showTargetLangSelect(view, (locale) -> {
                        if (scrimPopupWindow != null) {
                            scrimPopupWindow.dismiss();
                            scrimPopupWindow = null;
                            scrimPopupWindowItems = null;
                        }
                        DialogTransKt.startTrans(getParentActivity(), text.toString(), locale.getLanguage());
                        if (dismiss[1] != null) dismiss[1].run();
                        return Unit.INSTANCE;
                    });
                    return true;
                });
                layout.addView(translateButton);

                ActionBarPopupWindow window = new ActionBarPopupWindow(layout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
                dismiss[0] = window::dismiss;
                dismiss[1] = window::dismiss;
                dismiss[2] = window::dismiss;
                window.setPauseNotifications(true);
                window.setDismissAnimationDuration(220);
                window.setOutsideTouchable(true);
                window.setClippingEnabled(true);
                window.setAnimationStyle(R.style.PopupContextAnimation);
                window.setFocusable(true);
                window.showAsDropDown(cell, cell.getWidth() / 2 - AndroidUtilities.dp(90), AndroidUtilities.dp(-16), Gravity.BOTTOM | Gravity.LEFT);
            });
        } else {
            cell.setClickable(false);
        }
    }

    @Override
    protected boolean allowPresentFragment() {
        return !inPreviewMode;
    }

    @Override
    protected boolean hideKeyboardOnShow() {
        if (threadMessageObject != null && threadMessageObject.getRepliesCount() == 0 && ChatObject.canSendMessages(currentChat)) {
            return false;
        }
        return super.hideKeyboardOnShow();
    }

    public boolean isFeedSearch() {
        return chatMode == MODE_SEARCH && searchType == 4;
    }

    public int getBulletinTopOffset() {
        Bulletin.Delegate delegate = bulletinDelegate;
        return delegate != null ? delegate.getTopOffset(0) : AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight();
    }

    public int getBulletinBottomOffset() {
        Bulletin.Delegate delegate = bulletinDelegate;
        if (delegate != null) {
            return delegate.getBottomOffset(0);
        }
        return 0;
    }

    public org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode getGlassSource() {
        return LiteMode.isEnabled(LiteMode.FLAG_LIQUID_GLASS) ? glassBackgroundSourceRenderNode : glassBackgroundSourceFrostedRenderNode;
    }

    private Runnable glassSourceInvalidationCallback;

    public void setGlassSourceInvalidationCallback(Runnable callback) {
        glassSourceInvalidationCallback = callback;
    }

    private void invalidateGlassSource() {
        Runnable callback = glassSourceInvalidationCallback;
        if (callback != null) {
            callback.run();
        }
    }

    private FeedChatIntegration feedIntegration() {
        if (feedIntegration == null) {
            feedIntegration = new FeedChatIntegration(currentAccount, new FeedChatIntegration.Host() {
                @Override
                public ArrayList<MessageObject> getMessages() {
                    return ChatActivity.this.messages;
                }

                @Override
                public boolean isListReady() {
                    return ChatActivity.this.isFeedSearch() && ChatActivity.this.chatAdapter != null;
                }

                @Override
                public void notifyMessageRemoved(int index) {
                    if (ChatActivity.this.chatAdapter != null) {
                        ChatActivity.this.chatAdapter.notifyItemRemoved(ChatActivity.this.chatAdapter.messagesStartRow + index);
                    }
                }

                @Override
                public void notifyMessageInserted(int index) {
                    if (ChatActivity.this.chatAdapter != null) {
                        ChatActivity.this.chatAdapter.notifyItemInserted(ChatActivity.this.chatAdapter.messagesStartRow + index);
                    }
                }

                @Override
                public void notifyAllMessagesChanged() {
                    if (ChatActivity.this.chatAdapter != null) {
                        ChatActivity.this.chatAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void scrollToMessage(int index, int offset) {
                    if (ChatActivity.this.chatLayoutManager == null || ChatActivity.this.chatAdapter == null) {
                        return;
                    }
                    ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(ChatActivity.this.chatAdapter.messagesStartRow + index, offset, false);
                }

                @Override
                public void scrollToMessageAnimated(int index, int offset) {
                    if (ChatActivity.this.chatLayoutManager == null || ChatActivity.this.chatAdapter == null || ChatActivity.this.chatListView == null || ChatActivity.this.chatScrollHelper == null || ChatActivity.this.chatListView.isFastScrollAnimationRunning() || index < 0 || index >= ChatActivity.this.messages.size()) {
                        return;
                    }
                    ChatActivity.this.chatAdapter.updateRowsSafe();
                    ChatActivity.this.chatScrollHelper.setScrollDirection(0);
                    ChatActivity.this.chatScrollHelperCallback.scrollTo = ChatActivity.this.messages.get(index);
                    ChatActivity.this.chatScrollHelperCallback.lastBottom = false;
                    ChatActivity.this.chatScrollHelperCallback.lastItemOffset = offset;
                    ChatActivity.this.chatScrollHelperCallback.lastPadding = (int) ChatActivity.this.chatListViewPaddingTop;
                    int position = ChatActivity.this.chatAdapter.messagesStartRow + index;
                    ChatActivity.this.chatScrollHelperCallback.position = position;
                    ChatActivity.this.chatScrollHelperCallback.offset = offset;
                    ChatActivity.this.chatScrollHelperCallback.bottom = false;
                    ChatActivity.this.chatScrollHelper.scrollToPosition(position, offset, false, true);
                }

                @Override
                public int getLastVisibleMessageIndex() {
                    int lastVisiblePosition;
                    if (ChatActivity.this.chatLayoutManager == null || ChatActivity.this.chatAdapter == null || (lastVisiblePosition = ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition()) == -1) {
                        return Integer.MIN_VALUE;
                    }
                    return lastVisiblePosition - ChatActivity.this.chatAdapter.messagesStartRow;
                }

                @Override
                public int getNewestVisibleMessageIndex() {
                    if (ChatActivity.this.chatLayoutManager != null && ChatActivity.this.chatAdapter != null) {
                        int firstVisiblePosition = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                        int lastVisiblePosition = ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                        if (firstVisiblePosition != -1 && lastVisiblePosition != -1) {
                            return Math.min(firstVisiblePosition, lastVisiblePosition) - ChatActivity.this.chatAdapter.messagesStartRow;
                        }
                    }
                    return Integer.MIN_VALUE;
                }

                @Override
                public boolean canScrollToNewer() {
                    return ChatActivity.this.chatListView != null && ChatActivity.this.chatListView.canScrollVertically(1);
                }

                @Override
                public int getDistanceToNewerPx() {
                    if (ChatActivity.this.chatListView == null) {
                        return Integer.MAX_VALUE;
                    }
                    if (ChatActivity.this.chatListView.canScrollVertically(1)) {
                        return (ChatActivity.this.chatListView.computeVerticalScrollRange() - ChatActivity.this.chatListView.computeVerticalScrollExtent()) - ChatActivity.this.chatListView.computeVerticalScrollOffset();
                    }
                    return 0;
                }

                @Override
                public boolean isListScrollIdle() {
                    return ChatActivity.this.chatListView != null && ChatActivity.this.chatListView.getScrollState() == 0;
                }

                @Override
                public boolean isScrollAnimationRunning() {
                    return ChatActivity.this.chatListView != null && ChatActivity.this.chatListView.isFastScrollAnimationRunning();
                }

                @Override
                public void setPagedownCount(int count) {
                    if (ChatActivity.this.sideControlsButtonsLayout != null) {
                        ChatActivity.this.sideControlsButtonsLayout.setButtonCount(1, count, true);
                    }
                }

                @Override
                public void setPagedownButtonVisible(boolean visible) {
                    if (visible != ChatActivity.this.canShowPagedownButton) {
                        ChatActivity.this.canShowPagedownButton = visible;
                        ChatActivity.this.updatePagedownButtonVisibility(true);
                    }
                }

                @Override
                public boolean isPagedownButtonVisible() {
                    return ChatActivity.this.sideControlsButtonsLayout != null && ChatActivity.this.sideControlsButtonsLayout.isButtonVisible(1);
                }

                @Override
                public void invalidateVisiblePart() {
                    ChatActivity.this.invalidateMessagesVisiblePart();
                }

                @Override
                public int nextStableId() {
                    int id = ChatActivity.lastStableId;
                    ChatActivity.lastStableId = id + 1;
                    return id;
                }

                @Override
                public boolean isFirstLoadComplete() {
                    return ChatActivity.this.firstMessagesLoaded;
                }

                @Override
                public void reloadFeed() {
                    ChatActivity.this.reloadFeed();
                }

                @Override
                public void requestOlderFeedPage() {
                    FeedController feedController = FeedController.getInstance(currentAccount);
                    if (ChatActivity.this.loading || feedController.getStore().isEndReached()) {
                        return;
                    }
                    int loadIndex = ChatActivity.this.lastLoadIndex;
                    ChatActivity.this.loading = true;
                    ChatActivity.this.waitingForLoad.add(loadIndex);
                    boolean started = feedController.loadMore(ChatActivity.this.classGuid, loadIndex);
                    if (started) {
                        ChatActivity.this.lastLoadIndex++;
                    } else {
                        ChatActivity.this.waitingForLoad.remove(Integer.valueOf(loadIndex));
                        ChatActivity.this.loading = false;
                    }
                }

                @Override
                public FeedChatIntegration.ScrollAnchor captureScrollAnchor() {
                    MessageObject message;
                    if (ChatActivity.this.chatListView != null && ChatActivity.this.chatAdapter != null) {
                        for (int i = 0; i < ChatActivity.this.chatListView.getChildCount(); i++) {
                            View child = ChatActivity.this.chatListView.getChildAt(i);
                            if (child instanceof ChatMessageCell) {
                                message = ((ChatMessageCell) child).getMessageObject();
                            } else {
                                message = child instanceof ChatActionCell ? ((ChatActionCell) child).getMessageObject() : null;
                            }
                            if (FeedMessageUtils.isPostRow(message)) {
                                return new FeedChatIntegration.ScrollAnchor(message, ChatActivity.this.getScrollingOffsetForView(child));
                            }
                        }
                    }
                    return null;
                }

                @Override
                public void restoreScrollAnchor(FeedChatIntegration.ScrollAnchor anchor) {
                    if (anchor == null || ChatActivity.this.chatLayoutManager == null || ChatActivity.this.chatAdapter == null) {
                        return;
                    }
                    int index = ChatActivity.this.messages.indexOf(anchor.row);
                    if (index < 0) {
                        return;
                    }
                    ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(ChatActivity.this.chatAdapter.messagesStartRow + index, anchor.offsetTop);
                }

                @Override
                public void materializeRow(MessageObject message) {
                    if (ChatActivity.this.messagesDict[0].indexOfKey(message.getId()) >= 0) {
                        return;
                    }
                    if (message.stableId == 0) {
                        int id = ChatActivity.lastStableId;
                        ChatActivity.lastStableId = id + 1;
                        message.stableId = id;
                    }
                    ChatActivity.this.messagesDict[0].put(message.getId(), message);
                    ArrayList<MessageObject> dayMessages = ChatActivity.this.messagesByDays.get(message.dateKey);
                    if (dayMessages == null) {
                        dayMessages = new ArrayList<>();
                        ChatActivity.this.messagesByDays.put(message.dateKey, dayMessages);
                        ChatActivity.this.messagesByDaysSorted.put(message.dateKeyInt, dayMessages);
                    }
                    dayMessages.add(message);
                    if (message.hasValidGroupId()) {
                        MessageObject.GroupedMessages groupedMessages = ChatActivity.this.groupedMessagesMap.get(message.getGroupId());
                        if (groupedMessages == null) {
                            groupedMessages = new MessageObject.GroupedMessages();
                            groupedMessages.groupId = message.getGroupId();
                            ChatActivity.this.groupedMessagesMap.put(groupedMessages.groupId, groupedMessages);
                        }
                        if (!groupedMessages.messages.contains(message)) {
                            groupedMessages.messages.add(0, message);
                            groupedMessages.calculate();
                        }
                    }
                    ChatActivity.this.getMessagesController().getTranslateController().checkTranslation(message, false);
                }

                @Override
                public void deleteRows(ArrayList<Integer> rowIds) {
                    ChatActivity.this.processFeedDeletedMessages(rowIds, 0L, false, false);
                }

                @Override
                public int stableIdForDateHeader(int dateKeyInt) {
                    return ChatActivity.this.getStableIdForDateObject(dateKeyInt);
                }

                @Override
                public void onFeedListChanged() {
                    if (ChatActivity.this.chatAdapter != null) {
                        ChatActivity.this.chatAdapter.updateRowsSafe();
                    }
                    if (ChatActivity.this.messagesSearchAdapter != null) {
                        ChatActivity.this.messagesSearchAdapter.notifyDataSetChanged();
                    }
                    ChatActivity.this.updateSearchListEmptyView();
                }

                @Override
                public void showEmptyFeedState() {
                    ChatActivity.this.showMessagesSearchListView(true);
                }

                @Override
                public void showEmptyFeedProgress() {
                    if (ChatActivity.this.hashtagSearchEmptyView != null) {
                        ChatActivity.this.hashtagSearchEmptyView.showProgress(true);
                    }
                }

                @Override
                public BaseFragment getFragment() {
                    return ChatActivity.this;
                }
            }, !ChatActivity.this.hasMainTabs);
        }
        return feedIntegration;
    }

    public void reattachCurrentFeedVideoTexture() {
        View view;
        MessageObject playingMessage;
        TextureView textureView;
        if (!isFeedSearch() || (view = fragmentView) == null || view.getParent() == null || chatListView == null || (playingMessage = MediaController.getInstance().getPlayingMessageObject()) == null) {
            return;
        }
        if ((playingMessage.isRoundVideo() || playingMessage.isVideo()) && playingMessage.eventId == 0 && FeedController.getInstance(currentAccount).getMessage(playingMessage.getDialogId(), playingMessage.getRealId()) != null && (textureView = createTextureView(false)) != null) {
            MediaController.getInstance().setTextureView(textureView, aspectRatioFrameLayout, videoPlayerContainer, true);
            updateTextureViewPosition(true, false);
        }
    }

    public void setFeedChannelsChangedCallback(Runnable callback) {
        if (callback == null && feedIntegration == null) {
            return;
        }
        feedIntegration().setChannelsChangedCallback(callback);
    }

    public void setFeedViewportActive(boolean active) {
        feedIntegration().setViewportActive(active);
    }

    public void saveFeedScrollPosition() {
        FeedChatIntegration integration = feedIntegration;
        if (!isFeedSearch() || hasMainTabs || integration == null) {
            return;
        }
        integration.saveDrawerScrollPosition();
    }

    public void reloadFeed() {
        if (isFeedSearch()) {
            FeedChatIntegration integration = feedIntegration;
            if (integration != null) {
                integration.resetUiState();
            }
            if (messagesSearchAdapter == null) {
                FeedController.getInstance(currentAccount).clear();
                firstMessagesLoaded = false;
                return;
            }
            showMessagesSearchListView(false);
            clearChatData(true);
            startMessageAppearTransitionMs = 0L;
            firstMessagesLoaded = false;
            FeedController.getInstance(currentAccount).clear();
            messagesSearchAdapter.notifyDataSetChanged();
            messagesSearchListView.requestLayout();
            if (messagesSearchListView.getLayoutManager() != null) {
                messagesSearchListView.getLayoutManager().scrollToPosition(0);
            }
            updateSearchListEmptyView();
            hashtagSearchEmptyView.showProgress(true);
            firstLoadMessages();
        }
    }

    public void loadNewerFeed(boolean preserveScroll) {
        if (isFeedSearch()) {
            FeedController feedController = FeedController.getInstance(currentAccount);
            if (feedController.getMessages().isEmpty()) {
                if (feedController.isLoading()) {
                    return;
                }
                reloadFeed();
                return;
            }
            int loadIndex = lastLoadIndex;
            waitingForLoad.add(loadIndex);
            if (feedController.loadNewer(classGuid, loadIndex)) {
                if (preserveScroll) {
                    feedIntegration().onPreserveScrollLoadStarted(loadIndex);
                }
                lastLoadIndex++;
                return;
            }
            waitingForLoad.remove(Integer.valueOf(loadIndex));
        }
    }

    private void handleFeedLoadResult(boolean failed) {
        if (isFeedSearch()) {
            AndroidUtilities.cancelRunOnUIThread(retryFailedFeedLoad);
            if (!failed) {
                feedLoadRetryCount = 0;
                return;
            }
            int retries = feedLoadRetryCount;
            if (retries >= 3) {
                return;
            }
            feedLoadRetryCount = retries + 1;
            AndroidUtilities.runOnUIThread(retryFailedFeedLoad, (retries + 1) * 750L);
        }
    }

    public void markFeedAsRead() {
        if (isFeedSearch()) {
            feedIntegration().markAllRead();
        }
    }

    public void refreshFeedUnreadDivider() {
        if (isFeedSearch()) {
            FeedController.getInstance(currentAccount).refreshReadState(() -> {
                if (isFeedSearch()) {
                    feedIntegration().onReadStateRefreshed();
                }
            });
        }
    }

    public void onFeedChannelsChanged(boolean truncated) {
        if (isFeedSearch()) {
            if (truncated) {
                reloadFeed();
            } else {
                reconcileFeedList();
            }
        }
    }

    public void applyFeedConfigChange() {
        if (isFeedSearch()) {
            FeedController.getInstance(currentAccount).applyConfigChange(value -> {
                if (isFinished || !isFeedSearch()) {
                    return;
                }
                if (Boolean.TRUE.equals(value)) {
                    reloadFeed();
                } else {
                    reconcileFeedList();
                    loadNextNewerFeedPage.run();
                }
            });
        }
    }

    public void reconcileFeedList() {
        if (!isFeedSearch() || chatAdapter == null) {
            return;
        }
        feedIntegration().reconcileWithStore();
    }

    public void hideFeedChannelWithUndo(long dialogId, CharSequence title) {
        feedIntegration().hideChannelWithUndo(dialogId, title);
    }

    private void loadMoreFeedSearchResults() {
        int account = currentAccount;
        FeedController feedController = FeedController.getInstance(account);
        boolean emptyMessages = messages.isEmpty();
        int guid = classGuid;
        if (emptyMessages) {
            int loadIndex = lastLoadIndex;
            lastLoadIndex = loadIndex + 1;
            if (feedController.loadInitial(guid, loadIndex)) {
                AndroidUtilities.cancelRunOnUIThread(loadNextNewerFeedPage);
                AndroidUtilities.runOnUIThread(loadNextNewerFeedPage);
            }
            return;
        }
        if (feedController.loadMore(guid, lastLoadIndex)) {
            lastLoadIndex++;
        } else {
            waitingForLoad.remove(Integer.valueOf(lastLoadIndex));
            loading = false;
        }
    }

    private void addForwardingMessageObject(ArrayList<MessageObject> messageObjects, MessageObject messageObject) {
        MessageObject forwardingMessageObject = FeedMessageUtils.getForwardingMessageObject(currentAccount, isFeedSearch(), messageObject);
        if (forwardingMessageObject != null) {
            messageObjects.add(forwardingMessageObject);
        }
    }

    private TLRPC.InputPeer getInputPeerForMessageRequest(MessageObject messageObject) {
        return FeedMessageUtils.getInputPeerForMessageRequest(getMessagesController(), dialog_id, isFeedSearch(), messageObject);
    }

    private void updateFeedTabBackButton() {
        ActionBar actionBarLocal;
        ImageView imageView;
        if (!hasMainTabs || !isFeedSearch() || (actionBarLocal = actionBar) == null || (imageView = actionBarLocal.backButtonImageView) == null) {
            return;
        }
        imageView.setVisibility(actionBarLocal.isActionModeShowed() ? View.VISIBLE : View.GONE);
    }

    private void updateFeedRows(ArrayList<MessageObject> messageObjects, boolean reactionsChanged) {
        if (messageObjects == null || messageObjects.isEmpty()) {
            return;
        }
        for (int i = 0; i < messageObjects.size(); i++) {
            updateFeedRow(messageObjects.get(i), reactionsChanged);
        }
        if (messagesSearchAdapter != null) {
            messagesSearchAdapter.notifyDataSetChanged();
        }
    }

    private void updateFeedRow(MessageObject messageObject, boolean reactionsChanged) {
        if (messageObject == null) {
            return;
        }
        messageObject.forceUpdate = true;
        if (reactionsChanged) {
            messageObject.reactionsChanged = true;
        }
        ChatActivityAdapter adapter = chatAdapter;
        if (adapter != null) {
            adapter.updateRowWithMessageObject(messageObject, false, false);
        }
    }

    private MessageObject getMessageObjectForUpdate(long dialogId, int messageId) {
        if (isFeedSearch()) {
            return FeedController.getInstance(currentAccount).getMessage(dialogId, messageId);
        }
        return messagesDict[dialogId == dialog_id ? 0 : 1].get(messageId);
    }

    private void updateChangedMessageObject(MessageObject messageObject, boolean reactionsChanged) {
        if (!isFeedSearch()) {
            if (messageObject != null) {
                updateMessageAnimated(messageObject, reactionsChanged);
            }
        } else {
            updateFeedRow(messageObject, reactionsChanged);
            if (messageObject == null || messagesSearchAdapter == null) {
                return;
            }
            messagesSearchAdapter.notifyDataSetChanged();
        }
    }

    private void processFeedDeletedMessages(ArrayList<Integer> messageIds, long dialogId, boolean isBroadcast, boolean forceUpdate) {
        if (messageIds.isEmpty()) {
            return;
        }
        processDeletedMessages(messageIds, dialogId, isBroadcast, forceUpdate);
        cleanupInvisibleFeedDeletedMessages(messageIds);
        feedIntegration().onMessagesDeleted();
        feedIntegration().refreshRows();
        if (messagesSearchAdapter != null) {
            messagesSearchAdapter.notifyDataSetChanged();
        }
    }

    private void cleanupInvisibleFeedDeletedMessages(ArrayList<Integer> messageIds) {
        MessageObject.GroupedMessages groupedMessages;
        for (int i = 0; i < messageIds.size(); i++) {
            int messageId = messageIds.get(i);
            MessageObject messageObject = messagesDict[0].get(messageId);
            if (messageObject != null && messages.indexOf(messageObject) < 0) {
                messagesDict[0].remove(messageId);
                repliesMessagesDict.remove(messageId);
                updateReplyMessageOwners(messageId, null);
                ArrayList<MessageObject> dayMessages = messagesByDays.get(messageObject.dateKey);
                if (dayMessages != null) {
                    dayMessages.remove(messageObject);
                    if (dayMessages.isEmpty()) {
                        messagesByDays.remove(messageObject.dateKey);
                        messagesByDaysSorted.remove(messageObject.dateKeyInt);
                    }
                }
                if (messageObject.hasValidGroupId() && (groupedMessages = groupedMessagesMap.get(messageObject.getGroupId())) != null) {
                    groupedMessages.messages.remove(messageObject);
                    if (groupedMessages.messages.isEmpty()) {
                        groupedMessagesMap.remove(groupedMessages.groupId);
                    } else {
                        groupedMessages.calculate();
                    }
                }
            }
        }
    }

    @Override
    public boolean isLightStatusBar() {
        if (isReport()) {
            Theme.ResourcesProvider resourcesProvider = getResourceProvider();
            int color;
            if (resourcesProvider != null) {
                color = resourcesProvider.getColorOrDefault(Theme.key_actionBarActionModeDefault);
            } else {
                color = Theme.getColor(Theme.key_actionBarActionModeDefault, null, true);
            }
            return ColorUtils.calculateLuminance(color) > 0.7f;
        }
        if (actionBar == null) {
            return !Theme.isCurrentThemeDark();
        }
        return !shouldHaveLightStatusBarIcons;
    }

    public MessageObject.GroupedMessages getGroup(long id) {
        return groupedMessagesMap.get(id);
    }

    private MessageSkeleton getNewSkeleton(boolean noAvatar) {
        MessageSkeleton skeleton = new MessageSkeleton();
        if (currentChat != null && ChatObject.isChannelAndNotMegaGroup(currentChat)) {
            skeleton.height = AndroidUtilities.dp(128) + Utilities.fastRandom.nextInt(AndroidUtilities.dp(64));
        } else {
            skeleton.height = AndroidUtilities.dp(64) + Utilities.fastRandom.nextInt(AndroidUtilities.dp(64));
        }
        skeleton.width = (int) Math.min(chatListView.getWidth() * 0.8f - (noAvatar ? 0 : AndroidUtilities.dp(42)), AndroidUtilities.dp(42) + (0.4f + Utilities.fastRandom.nextFloat() * 0.35f) * chatListView.getWidth());
        return skeleton;
    }

    @Override
    public SizeNotifierFrameLayout getContentView() {
        return contentView;
    }

    private final static class MessageSkeleton {
        int width;
        int height;
        int lastBottom;
    }

    private void nkbtn_onclick_actionbar(int id) {
        createUndoView();
        // from ActionBar & Header ( without text_* )
        // should hide shit action bar after done
        if (id == nkbtn_forward_noquote) {
            openAdvancedForwardEditor(true);
        } else if (id == nkbtn_forward_nocaption) {
            advancedForwardTexts = null;
            noForwardQuote = id == nkbtn_forward_noquote;
            noForwardCaption = id == nkbtn_forward_nocaption;
            if (messagePreviewParams != null) {
                messagePreviewParams.setHideForwardSendersName(noForwardQuote);
                messagePreviewParams.hideCaption = noForwardCaption;
            }
            openForward(true);
        } else if (id == nkactionbarbtn_reply) {
            MessageObject messageObject = null;
            for (int a = 1; a >= 0; a--) {
                if (messageObject == null && selectedMessagesIds[a].size() != 0) {
                    messageObject = messagesDict[a].get(selectedMessagesIds[a].keyAt(0));
                }
                selectedMessagesIds[a].clear();
                selectedMessagesCanCopyIds[a].clear();
                selectedMessagesCanStarIds[a].clear();
            }
            clearSelectionMode();
            if (messageObject != null && (messageObject.messageOwner.id > 0 || messageObject.messageOwner.id < 0 && currentEncryptedChat != null)) {
                showFieldPanelForReply(messageObject);
            }
        } else if (id == nkbtn_translate) {
            MessageTransKt.translateMessages(ChatActivity.this, getSelectedMessages());
        } else if (id == nkbtn_unpin) {
            for (MessageObject selectedMessage : getSelectedMessages()) {
                if (selectedMessage.messageOwner.pinned) {
                    unpinMessage(selectedMessage);
                }
            }
        } else if (id == nkbtn_savemessage) {
            ArrayList<MessageObject> messages = getSelectedMessages();
            forwardMessages(messages, false, false, true, 0, UserConfig.getInstance(currentAccount).getClientUserId(), 0);
            undoView.showWithAction(getUserConfig().getClientUserId(), UndoView.ACTION_FWD_MESSAGES, messages.size());
        } else if (id == nkbtn_hide) {
            ArrayList<MessageObject> messages = getSelectedMessages();
            for (MessageObject message : messages) {
                message.messageOwner.hide = true;
            }
            getMessageHelper().resetMessageContent(dialog_id, messages);
            clearSelectionMode();
        } else if (id == nkactionbarbtn_selectBetween) {
            ArrayList<Integer> ids = new ArrayList<>();
            for (int a = 1; a >= 0; a--) {
                for (int b = 0; b < selectedMessagesIds[a].size(); b++) {
                    ids.add(selectedMessagesIds[a].keyAt(b));
                }
            }
            Collections.sort(ids);
            Integer begin = ids.get(0);
            Integer end = ids.get(ids.size() - 1);
            for (int i = 0; i < messages.size(); i++) {
                int msgId = messages.get(i).getId();
                if (AyuFilter.shouldHideIgnoredBlockedMessages() && AyuFilter.isIgnoredBlockedMessage(messages.get(i))) {
                    continue;
                }
                {
                    var selMsg = messages.get(i);
                    var selGroup = getGroup(selMsg.getGroupId());
                    if (selGroup == null) {
                        selGroup = getValidGroupedMessage(selMsg);
                    }
                    var selFilterMsg = selGroup != null ? selGroup.findPrimaryMessageObject() : null;
                    if (selFilterMsg == null) {
                        selFilterMsg = selMsg;
                    }
                    if (AyuFilter.shouldHideFilteredMessage(selFilterMsg, selGroup)) {
                        continue;
                    }
                }
                if (msgId > begin && msgId < end && selectedMessagesIds[0].indexOfKey(msgId) < 0) {
                    MessageObject message = messages.get(i);
                    int type = getMessageType(message);

                    if (type < MESSAGE_TYPE_MEDIA || type == MESSAGE_TYPE_SEND_ERROR_TEXT) {
                        continue;
                    }

                    if (selectedMessagesIds[0].size() + selectedMessagesIds[1].size() >= 1000) {
                        if (message.getId() != begin) {
                            for (int x = 0; x < messages.size(); x++) {
                                MessageObject msg = messages.get(x);
                                if (msg.getId() == begin) {
                                    addToSelectedMessages(msg, false, false);
                                    addToSelectedMessages(message, true);
                                    break;
                                }
                            }
                        }
                        break;
                    }

                    addToSelectedMessages(message, true);
                }
            }
            updateActionModeTitle();
            updateVisibleRows();
        } else if (id == nkheaderbtn_zibi) {
            getMessageHelper().createDeleteHistoryAlert(ChatActivity.this, currentChat, forumTopic, mergeDialogId, themeDelegate);
        } else if (id == nkbtn_clearDeleted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.ClearDeleted));
            builder.setMessage(LocaleController.getString(R.string.ClearDeletedAlertMessage));
            builder.setPositiveButton(LocaleController.getString(R.string.Clear), (dialogInterface, i) -> {
                AyuMessagesController.getInstance().deleteCurrent(dialog_id, mergeDialogId, () -> {
                    AndroidUtilities.runOnUIThread(() -> {
                        getNotificationCenter().removeObserver(ChatActivity.this, NotificationCenter.closeChats);
                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                        finishFragment();
                    });
                    if (!NekoConfig.disableVibration.Bool()) LaunchActivity.getLastFragment().getFragmentView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                });
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
            }
        } else if (id == nkbtn_viewDeleted) {
            presentFragment(new AyuViewDeleted(dialog_id));
        } else if (id == nkbtn_bookmarks_manager) {
            presentFragment(new BookmarksActivity(dialog_id));
        } else if (id == nkheaderbtn_upgrade) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("ConvertGroupAlert", R.string.ConvertGroupAlert));
            builder.setTitle(LocaleController.getString("ConvertGroupAlertWarning", R.string.ConvertGroupAlertWarning));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialogInterface, i) -> getMessagesController().convertToMegaGroup(getParentActivity(), currentChat.id, ChatActivity.this, chatNew -> {
                if (chatNew != 0) {
                    getMessagesController().toggleChannelInvitesHistory(chatNew, false);
                }
            }));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else if (id == nkheaderbtn_show_pinned) {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
            preferences.edit().remove("pin_" + dialog_id).apply();
            updatePinnedMessageView(true);
        } else if (id == nkheaderbtn_linked_chat) {
            if (chatInfo == null) {
                return;
            }
            Bundle args = new Bundle();
            args.putLong("chat_id", chatInfo.linked_chat_id);
            if (!getMessagesController().checkCanOpenChat(args, ChatActivity.this)) {
                return;
            }
            presentFragment(new ChatActivity(args));
        } else if (id == nkbtn_view_in_chat) {
            if (chatInfo == null || threadMessageId == 0)
                return;
            Bundle args = new Bundle();
            args.putLong("chat_id", chatInfo.id);
            args.putLong("message_id", threadMessageId);
            if (!getMessagesController().checkCanOpenChat(args, ChatActivity.this))
                return;
            presentFragment(new ChatActivity(args), true);
        } else if (id == nkbtn_repeat) {
            repeatMessage(false, false);
            clearSelectionMode();
        } else if (id == nkbtn_repeatascopy) {
            repeatMessage(false, true);
            clearSelectionMode();
        } else if (id == nkheaderbtn_hide_title) {
            if (avatarContainer != null) {
                avatarContainer.setTitle("");
                BackupImageView avatarImageView = avatarContainer.getAvatarImageView();
                if (avatarImageView != null) {
                    avatarImageView.setVisibility(android.view.View.GONE);
                }
            }
            if (hideTitleItem != null) hideTitleItem.setVisibility(android.view.View.GONE);
        } else if (id == nkbtn_detail) {
            ArrayList<MessageObject> messageObjects = getSelectedMessages();
            if (!messageObjects.isEmpty()) {
                MessageObject.GroupedMessages messageGroup = getValidGroupedMessage(messageObjects.get(0));
                presentFragment(new MessageDetailsActivity(messageObjects.get(0), messageGroup));
            }
        } else if (id == nkbtn_sharemessage) {
            var selected = getSelectedMessages();
            if (selected.isEmpty()) return;
            var builder = new StringBuilder();
            for (int i = 0; i < selected.size(); i++) {
                builder.append(selected.get(i).messageOwner.message);
                if (i != selected.size() - 1)
                    builder.append("\n\n");
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
            try {
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                AlertUtil.showToast(e);
            }
        } else if (id == shortcuts_administrators || id == shortcuts_permissions || id == shortcuts_members || id == shortcuts_recent_actions || id == shortcuts_statistics) {
            onAdminShortcutsClick(id);
        } else if (id == nkbtn_report) {
            getSelectedMessages1().stream().findFirst().ifPresent(obj -> {
                selectedObject = obj;
                processSelectedOption(OPTION_REPORT_CHAT);
                clearSelectionMode();
            });
        }
    }

    private void onAdminShortcutsClick(int id) {
        if (headerItem != null) {
            headerItem.closeSubMenu();
        }
        if (currentChat == null) {
            return;
        }
        if (id == shortcuts_recent_actions) {
            presentFragment(new ChannelAdminLogActivity(currentChat));
            return;
        }
        if (id == shortcuts_statistics) {
            presentFragment(StatisticActivity.create(currentChat, false));
            return;
        }
        Bundle args = new Bundle();
        args.putLong("chat_id", currentChat.id);
        if (id == shortcuts_permissions) {
            args.putInt("type", (!ChatObject.isChannel(currentChat) || currentChat.megagroup) && !currentChat.gigagroup
                    ? ChatUsersActivity.TYPE_KICKED
                    : ChatUsersActivity.TYPE_BANNED);
        } else if (id == shortcuts_administrators) {
            args.putInt("type", ChatUsersActivity.TYPE_ADMIN);
        } else if (id == shortcuts_members) {
            args.putInt("type", ChatUsersActivity.TYPE_USERS);
        } else {
            return;
        }
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(chatInfo != null ? chatInfo : getMessagesController().getChatFull(currentChat.id));
        presentFragment(fragment);
    }

    private void nkbtn_onclick(int id) {
        // from "items"
        createUndoView();
        switch (id) {
            case nkbtn_repeat: {
                repeatMessage(false,false);
                break;
            }
            case nkbtn_repeatascopy: {
                repeatMessage(false, true);
                break;
            }
            case nkbtn_forward_nocaption:
            case nkbtn_forward_noquote: {
                if (id == nkbtn_forward_noquote) {
                    forwardingMessage = selectedObject;
                    forwardingMessageGroup = selectedObjectGroup;
                    openAdvancedForwardEditor(false);
                    break;
                }
                advancedForwardTexts = null;
                noForwardQuote = id == nkbtn_forward_noquote;
                noForwardCaption = id == nkbtn_forward_nocaption;
                if (messagePreviewParams != null) {
                    messagePreviewParams.setHideForwardSendersName(noForwardQuote);
                    messagePreviewParams.hideCaption = noForwardCaption;
                }
                forwardingMessage = selectedObject;
                forwardingMessageGroup = selectedObjectGroup;
                openForward(false);
                break;
            }
            case nkbtn_deldlcache: {
                if (Build.VERSION.SDK_INT >= 23 && (Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    getParentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                    selectedObject = null;
                    selectedObjectGroup = null;
                    selectedObjectToEditCaption = null;
                    return;
                }
                var object = selectedObject;
                getMessageHelper().clearMessageFiles(object, () -> {
                    if (object != null) {
                        var messageCell = (ChatMessageCell) findMessageCell(object.getId(), false);
                        if (messageCell != null) {
                            messageCell.updateButtonState(false, true, false);
                        }
                    }
                });
                break;
            }
            case nkbtn_savemessage: {
                ArrayList<MessageObject> messages = new ArrayList<>();
                if (selectedObjectGroup != null) {
                    messages.addAll(selectedObjectGroup.messages);
                } else {
                    messages.add(selectedObject);
                }
                forwardMessages(messages, false, false, true, 0, getUserConfig().getClientUserId(), 0);
                undoView.showWithAction(getUserConfig().getClientUserId(), UndoView.ACTION_FWD_MESSAGES, messages.size());
                break;
            }
            case nkbtn_bookmark: {
                if (selectedObject == null) {
                    return;
                }
                int[] messageIds;
                if (selectedObjectGroup != null && selectedObjectGroup.messages != null && !selectedObjectGroup.messages.isEmpty()) {
                    ArrayList<MessageObject> groupMessages = selectedObjectGroup.messages;
                    messageIds = new int[groupMessages.size()];
                    for (int i = 0; i < groupMessages.size(); i++) {
                        messageIds[i] = groupMessages.get(i).getId();
                    }
                } else {
                    messageIds = new int[]{selectedObject.getId()};
                }
                ToggleResult result = BookmarksHelper.toggleBookmarks(currentAccount, selectedObject.getDialogId(), messageIds);
                if (result == ToggleResult.LIMIT_REACHED) {
                    BulletinFactory.of(this).createSimpleBulletin(R.raw.error, formatString(R.string.BookmarksLimitReached, BookmarksHelper.MAX_PER_CHAT)).show();
                } else {
                    boolean added = result == ToggleResult.ADDED;
                    Drawable drawable = ContextCompat.getDrawable(getParentActivity(), added ? R.drawable.msg_fave : R.drawable.msg_unfave);
                    if (drawable != null) {
                        drawable = drawable.mutate();
                        drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_undo_infoColor), PorterDuff.Mode.SRC_IN));
                    }
                    long dialogIdForBookmarks = selectedObject.getDialogId();
                    CharSequence text = AndroidUtilities.replaceSingleTag(
                            getString(result == ToggleResult.ADDED ? R.string.BookmarkAdded : R.string.BookmarkRemoved),
                            () -> presentFragment(new BookmarksActivity(dialogIdForBookmarks))
                    );
                    BulletinFactory.of(this).createSimpleBulletin(drawable, text).show();
                    updateVisibleRows();
                    if (headerItem != null && bookmarksItem != null) {
                        headerItem.setSubItemShown(nkbtn_bookmarks_manager, BookmarksHelper.getBookmarkedMessageIds(currentAccount, dialog_id).length > 0);
                    }
                }
                break;
            }
            case nkbtn_sharemessage: {
                MessageObject messageObject = null;
                if (selectedObjectGroup != null) {
                    if (!TextUtils.isEmpty(selectedObjectGroup.messages.get(0).messageOwner.message)) {
                        messageObject = selectedObjectGroup.messages.get(0);
                    }
                } else if (!TextUtils.isEmpty(selectedObject.messageOwner.message) || selectedObject.type == MessageObject.TYPE_POLL) {
                    messageObject = selectedObject;
                }
                if (messageObject == null) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = messageObject.messageOwner.message;
                intent.putExtra(Intent.EXTRA_TEXT,body);
                try {
                    getParentActivity().startActivity(intent);
                } catch (Exception e) {
                    AlertUtil.showToast(e);
                }
                break;
            }
            case nkbtn_ai_chat: {
                handleAiChat(selectedObject, selectedObjectGroup);
                break;
            }
            case nkbtn_stickerdl: {
                if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    getParentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                    selectedObject = null;
                    selectedObjectGroup = null;
                    selectedObjectToEditCaption = null;
                    return;
                }
                getMessageHelper().saveStickerToGallery(getParentActivity(), selectedObject, uri -> {
                    if (BulletinFactory.canShowBulletin(ChatActivity.this)) {
                        BulletinFactory.of(ChatActivity.this).createSimpleBulletin(R.raw.ic_save_to_gallery, getString(R.string.StickerSavedHint)).show();
                    }
                });
                break;
            }
            case nkbtn_translate:
                if (handleTranslateDuringAutoTrans(null)) {
                    return;
                }
                MessageTransKt.translateMessages(this, 0);
                break;
            case nkbtn_translateVoice:
                TranscribeButton.retryOrTranslateVoiceTranscription(selectedObject, false, null);
                break;
            case nkbtn_transcriptionRetry:
                TranscribeButton.retryOrTranslateVoiceTranscription(selectedObject, true, null);
                break;
            case nkbtn_detail: {
                presentFragment(new MessageDetailsActivity(selectedObject, selectedObjectGroup));
                break;
            }
            case nkbtn_view_history: {
                // same as "search_from_user_id"
                TLRPC.Peer peer = selectedObject.messageOwner.from_id;
                if ((threadMessageId == 0 || isTopic) && !UserObject.isReplyUser(currentUser)) {
                    openSearchWithText("");
                } else {
                    searchItem.openSearch(false);
                }
                if (peer.user_id!=0) {
                    TLRPC.User user = getMessagesController().getUser(peer.user_id);
                    searchUserMessages(user,null);
                } else if (peer.chat_id!=0) {
                    TLRPC.Chat chat = getMessagesController().getChat(peer.chat_id);
                    searchUserMessages(null, chat);
                } else if (peer.channel_id!=0) {
                    // thanks to Nekogram
                    TLRPC.Chat chat = getMessagesController().getChat(peer.channel_id);
                    searchUserMessages(null, chat);
                }
                showMessagesSearchListView(true);
                break;
            }
            case nkbtn_editAdmin: {
                if (selectedParticipant == null) {
                    break;
                }
                doAdminActions(0);
                break;
            }
            case nkbtn_editPermission: {
                if (selectedParticipant == null) {
                    break;
                }
                doAdminActions(1);
                break;
            }
            case nkbtn_hide: {
                if (selectedObjectGroup != null) {
                    for (MessageObject object : selectedObjectGroup.messages) {
                        object.messageOwner.hide = true;
                    }
                    getMessageHelper().resetMessageContent(dialog_id, selectedObjectGroup.messages);
                } else {
                    selectedObject.messageOwner.hide = true;
                    getMessageHelper().resetMessageContent(dialog_id, selectedObject);
                }
                break;

            }
            case nkbtn_view_in_chat: {
                if (selectedObject == null)
                    return;
                Bundle args = new Bundle();
                args.putLong("chat_id", chatInfo.id);
                args.putInt("message_id", selectedObject.messageOwner.id);
                if (!getMessagesController().checkCanOpenChat(args, ChatActivity.this))
                    return;
                presentFragment(new ChatActivity(args), true);
                break;

            }
            case nkbtn_copy_link_in_pm: {
                try {
                    String link_message = "tg://openmessage?user_id=" + currentUser.id + "&message_id=" + selectedObject.messageOwner.id;
                    ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", link_message);
                    clipboard.setPrimaryClip(clip);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                break;

            }
            case nkbtn_setReminder: {
                ArrayList<MessageObject> messages =  new ArrayList<>();
                if (selectedObjectGroup != null) {
                    messages.addAll(selectedObjectGroup.messages);
                } else {
                    messages.add(selectedObject);
                }
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), getUserConfig().getClientUserId(), (notify, scheduleDate, scheduleRepeatPeriod) -> {
                    forwardMessages(messages, false, false, notify, scheduleDate, getUserConfig().getClientUserId(), 0);
                    undoView.showWithAction(getUserConfig().getClientUserId(), UndoView.ACTION_FWD_MESSAGES, messages.size());
                }, themeDelegate);
                break;
            }
            case nkbtn_reply_private: {
                Bundle args = new Bundle();
                args.putLong("user_id", selectedObject.messageOwner.from_id.user_id);

                ChatActivity chatActivity = new ChatActivity(args);
                presentFragment(chatActivity);

                if (chatActivityEnterView != null && chatActivity.chatActivityEnterView != null) {
                    chatActivity.chatActivityEnterView.setFieldText(
                            chatActivityEnterView.getFieldText()
                    );
                }
                chatActivity.replyingQuoteGroup = getGroup(selectedObject.getGroupId());
                chatActivity.replyingTopMessage = selectedObject;
                chatActivity.showFieldPanelForReplyQuote(selectedObject, null);
                break;
            }
        }
    }

    private void handleAiChat(MessageObject selectedObject, MessageObject.GroupedMessages selectedObjectGroup) {
        if (selectedObject == null) return;

        String messageText = "";
        if (selectedObjectGroup != null) {
            for (MessageObject msg : selectedObjectGroup.messages) {
                if (msg.caption != null && msg.caption.length() > 0) {
                    messageText = msg.caption.toString();
                    break;
                } else if (msg.messageOwner != null && !TextUtils.isEmpty(msg.messageOwner.message)) {
                    messageText = msg.messageOwner.message;
                    break;
                }
            }
        } else {
            if (selectedObject.caption != null && selectedObject.caption.length() > 0) {
                messageText = selectedObject.caption.toString();
            } else if (selectedObject.messageOwner != null && !TextUtils.isEmpty(selectedObject.messageOwner.message)) {
                messageText = selectedObject.messageOwner.message;
            }
        }

        String imagePath = AiController.getPathToMessage(selectedObject);
        new GenerateFromMessageBottomSheet(messageText, imagePath, ChatActivity.this, getParentActivity(), data -> {
            Client aiClient = new Client.Builder().build();
            boolean noForwards = getMessagesController().isPeerNoForwards(getDialogId())
                    || (selectedObject.messageOwner != null && selectedObject.messageOwner.noforwards);
            ResponseAlert.showAlert(ChatActivity.this, aiClient, data.prompt(), data.imagePath(), data.useHistory(), noForwards,
                    urlSpan -> {
                        didPressMessageUrl(urlSpan, false, selectedObject, null);
                        return Boolean.TRUE;
                    },
                    null,
                    chatActivityEnterView != null && currentChat != null && ChatObject.canSendMessages(currentChat) ? (prompt, response) -> {
                        if (TextUtils.isEmpty(response)) return;
                        if (AiConfig.insertAsQuote) {
                            chatActivityEnterView.getEditField().setText(response);
                            chatActivityEnterView.getEditField().append("\n");
                            int start = AiConfig.showResponseOnly ? 0 : prompt.length() + 4;
                            int end = chatActivityEnterView.getEditText().length() - 1;
                            org.telegram.ui.Components.QuoteSpan.putQuoteToEditable(chatActivityEnterView.getEditText(), start, end, true);
                        } else {
                            chatActivityEnterView.getEditField().setText(response);
                        }
                        chatActivityEnterView.getEditField().setSelection(chatActivityEnterView.getEditField().length());
                        chatActivityEnterView.openKeyboard();
                    } : null);
        }).show();
    }

    private void repeatMessage(boolean isLongClick, boolean isRepeatasCopy) {
        if (checkSlowMode(chatActivityEnterView.getSendButton())) {
            return;
        }
        final ArrayList<MessageObject> messages = new ArrayList<>();
        if (selectedObject != null) {
            messages.add(selectedObject);
        } else {
            for (int k = 0; k < selectedMessagesIds[0].size(); k++) {
                if (selectedMessagesIds[0].get(selectedMessagesIds[0].keyAt(k)) != null) {
                    messages.add(selectedMessagesIds[0].get(selectedMessagesIds[0].keyAt(k)));
                }
            }
        }
        if (!NekoConfig.repeatConfirm.Bool()) {
            doRepeatMessage(isLongClick, messages, isRepeatasCopy);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("Repeat", R.string.Repeat));
        builder.setMessage(LocaleController.getString("repeatConfirmText", R.string.repeatConfirmText));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialogInterface, i) -> {
            doRepeatMessage(isLongClick, messages, isRepeatasCopy);
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void doRepeatMessage(boolean isLongClick, ArrayList<MessageObject> messages, boolean isRepeatAsCopy) {
        boolean noforwards = getMessagesController().isChatNoForwards(currentChat);
        if (selectedObject == null && noforwards && !messages.isEmpty()) {
            selectedObject = messages.get(0);
        }
        if (selectedObject != null && selectedObject.messageOwner != null && (isLongClick || (isThreadChat() && !isTopic) || noforwards)) {
            // If selected message contains `replyTo`:
            // When longClick it will reply to the `replyMessage` of selectedMessage
            // When not LongClick but in a threadchat: reply to the Thread
            MessageObject replyTo = selectedObject.replyMessageObject != null ? isLongClick ? selectedObject.replyMessageObject : getThreadMessage() : getThreadMessage();
            if (replyTo != null || noforwards) {
                if (selectedObject.type == 0 || selectedObject.isAnimatedEmoji() || getMessageCaption(selectedObject, selectedObjectGroup) != null) {
                    CharSequence caption = getMessageCaption(selectedObject, selectedObjectGroup);
                    if (caption == null) {
                        caption = getMessageContent(selectedObject, 0, false);
                    }
                    if (!TextUtils.isEmpty(caption)) {
                        SendMessagesHelper.getInstance(currentAccount)
                                .sendMessage(caption.toString(), dialog_id, replyTo,
                                        getThreadMessage(), null,
                                        false, selectedObject.messageOwner.entities, null, null,
                                        true, 0, 0, null, false);
                    }
                } else if ((selectedObject.isSticker() || selectedObject.isAnimatedSticker()) && selectedObject.getDocument() != null) {
                    SendMessagesHelper.getInstance(currentAccount)
                            .sendSticker(selectedObject.getDocument(), null, dialog_id, null, null, replyTo, getThreadMessage(), null, replyingQuote, null, true, 0, 0, false, null, getMessageChatSendParams(), 0, 0, null);
                }
                return;
            }
        }

        forwardMessages(messages, isLongClick || isRepeatAsCopy, false, true, 0, 0);
    }

    public void setScrollToMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("InputMessageId", R.string.InputMessageId));
        final EditTextBoldCursor editText = new EditTextBoldCursor(getParentActivity());
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        editText.setSingleLine(true);
        editText.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        editText.setFocusable(true);
        editText.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField),
                getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated),
                getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setBackgroundDrawable(null);
        editText.requestFocus();
        editText.setPadding(0, 0, 0, 0);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK),
                (dialogInterface, i) -> {
                    try {
                        if (Integer.parseInt(editText.getText().toString()) > 0) {
                            scrollToMessageId(Integer.parseInt(editText.getText().toString()), 0, false, 0, true, 0);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setOnDismissListener(dialog -> {
            chatActivityEnterView.getEditField().clearFocus();
        });
        builder.show().setOnShowListener(dialog -> {
            editText.requestFocus();
            AndroidUtilities.showKeyboard(editText);
        });
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
        if (layoutParams != null) {
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams).gravity = Gravity.CENTER_HORIZONTAL;
            }
            layoutParams.rightMargin = layoutParams.leftMargin = AndroidUtilities.dp(24);
            layoutParams.height = AndroidUtilities.dp(36);
            editText.setLayoutParams(layoutParams);
        }
    }

    private class RecyclerListViewInternal extends RecyclerListView implements StoriesListPlaceProvider.ClippedView {
        public RecyclerListViewInternal(Context context, ThemeDelegate themeDelegate) {
            super(context, themeDelegate);
        }

        @Override
        public void updateClip(int[] clip) {
            clip[0] = (int) chatListViewPaddingTop - AndroidUtilities.dp(4);
            clip[1] = chatListView.getMeasuredHeight() - (chatListView.getPaddingBottom() - AndroidUtilities.dp(3));
        }
    }

    private void updateVisibleWallpaperActions() {
        if (chatListView != null && chatAdapter != null) {
            for (int i = 0; i < chatListView.getChildCount(); ++i) {
                View child = chatListView.getChildAt(i);
                int position = chatListView.getChildAdapterPosition(child) - chatAdapter.messagesStartRow;
                if (child instanceof ChatActionCell && position >= 0 && position < messages.size()) {
                    MessageObject msg = messages.get(position);
                    if (msg != null && msg.isWallpaperForBoth()) {
                        ((ChatActionCell) child).setMessageObject(msg, true);
                    }
                }
            }
        }
    }

    private void checkLeaveChannelButton() {
        if (headerItem == null || chatMode == MODE_SAVED) return;
        if (!headerItem.hasSubItem(delete_chat)) {
            if (!isTopic) {
                if (ChatObject.isChannel(currentChat) && !currentChat.creator) {
                    if (!ChatObject.isNotInChat(currentChat)) {
                        if (currentChat.megagroup) {
                            headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_leave, LocaleController.getString(R.string.LeaveMegaMenu));
                        } else {
                            headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_leave, LocaleController.getString(R.string.LeaveChannelMenu));
                        }
                    }
                } else if (!ChatObject.isChannel(currentChat)) {
                    if (currentChat != null) {
                        headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_leave, LocaleController.getString(R.string.DeleteAndExit));
                    } else if (currentUser != null && currentUser.bot) {
                        headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_block2, LocaleController.getString(R.string.DeleteAndBlock)).setColors(getThemedColor(Theme.key_text_RedRegular), getThemedColor(Theme.key_text_RedRegular));
                    } else {
                        headerItem.lazilyAddSubItem(delete_chat, R.drawable.msg_delete, LocaleController.getString(R.string.DeleteChatUser));
                    }
                }
            }
        }
    }

    public boolean supportsThanosEffect() {
        return ThanosEffect.supports() && LiteMode.isEnabled(LiteMode.FLAG_CHAT_THANOS);
    }

    public ThanosEffect getChatThanosEffect() {
        if (!LiteMode.isEnabled(LiteMode.FLAG_CHAT_THANOS) || !ThanosEffect.supports()) {
            return null;
        }
        if (chatListThanosEffect == null || chatListThanosEffect.destroyed) {
            if (getContext() == null || !ThanosEffect.supports() || chatListView == null || contentView == null) {
                return null;
            }
            if (chatListThanosEffect != null) {
                AndroidUtilities.removeFromParent(chatListThanosEffect);
            }
            final ThanosEffect[] thisThanosEffect = new ThanosEffect[1];
            final ThanosEffect thanosEffect = new ThanosEffect(getContext(), () -> {
                if (removingFromParent || thisThanosEffect[0] == null) {
                    return;
                }
                ThanosEffect effect = thisThanosEffect[0];
                thisThanosEffect[0] = null;
                if (chatListThanosEffect == effect) {
                    chatListThanosEffect = null;
                }
                AndroidUtilities.removeFromParent(effect);
            });
            thisThanosEffect[0] = chatListThanosEffect = thanosEffect;
            contentView.addView(thanosEffect, 1 + contentView.indexOfChild(chatListView), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }
        return chatListThanosEffect;
    }

    private StarReactionsOverlay starReactionsOverlay;
    public StarReactionsOverlay getStarReactionsOverlay() {
        if (starReactionsOverlay == null) {
            starReactionsOverlay = new StarReactionsOverlay(ChatActivity.this);
        }
        FrameLayout starReactionsOverlayParent = getLayoutContainer();
//        if (LaunchActivity.instance != null) {
//            starReactionsOverlayParent = LaunchActivity.instance.frameLayout;
//        }
        if (starReactionsOverlayParent == null) {
            return null;
        }
        if (starReactionsOverlay.getParent() != starReactionsOverlayParent) {
            AndroidUtilities.removeFromParent(starReactionsOverlay);
            starReactionsOverlayParent.addView(starReactionsOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        } else if (starReactionsOverlayParent.indexOfChild(starReactionsOverlay) < starReactionsOverlayParent.indexOfChild(fragmentView)) {
            starReactionsOverlay.bringToFront();
        }
        return starReactionsOverlay;
    }

    private void checkGroupMessagesOrder() {
        if (!reversed) return;
        int groupStart = -1;
        long thisGroupId = 0;
        for (int i = 0; i < messages.size(); ++i) {
            MessageObject msg = messages.get(i);
            long msgGroupId = msg.getGroupIdForUse();
            if (thisGroupId != msgGroupId) {
                if (groupStart >= 0 && thisGroupId != 0 && i - groupStart > 1) {
                    // thisGroup from groupStart to (i - 1)
                    int count = i - groupStart;
                    ArrayList<MessageObject> groupMessages = new ArrayList<>();
                    for (int a = 0; a < count; ++a) {
                        groupMessages.add(messages.remove(groupStart));
                    }
                    Collections.sort(groupMessages, (a, b) -> b.getId() - a.getId());
                    messages.addAll(groupStart, groupMessages);
                }
                groupStart = i;
                thisGroupId = msgGroupId;
            }
        }
        if (groupStart >= 0 && thisGroupId != 0 && messages.size() - groupStart > 1) {
            // thisGroup from groupStart to (messages.size() - 1)
            int count = messages.size() - groupStart;
            ArrayList<MessageObject> groupMessages = new ArrayList<>();
            for (int a = 0; a < count; ++a) {
                groupMessages.add(messages.remove(groupStart));
            }
            messages.addAll(groupStart, groupMessages);
        }
    }

    private void invalidatePremiumBlocked() {
        if (getDialogId() == getUserConfig().getClientUserId())
            return;
        if (getUserConfig().isPremium())
            return;
        if (currentUser == null || !currentUser.contact_require_premium)
            return;
        if (messages.isEmpty() == (getMessagesController().isUserContactBlocked(getDialogId()) != null))
            return;
        getMessagesController().invalidateUserPremiumBlocked(getDialogId(), classGuid);
    }

    public boolean checkCanRemoveRestrictionsByBoosts() {
        boolean result = ChatObject.isPossibleRemoveChatRestrictionsByBoosts(chatInfo);
        if (result) {
            AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
            LimitReachedBottomSheet.openBoostsForRemoveRestrictions(ChatActivity.this, boostsStatus, canApplyBoosts, dialog_id, false);
        }
        return result;
    }

    public void showPremiumFloodWaitBulletin(final boolean isUpload) {
        final long now = System.currentTimeMillis();
        if (now - ConnectionsManager.lastPremiumFloodWaitShown < 1000L * MessagesController.getInstance(currentAccount).uploadPremiumSpeedupNotifyPeriod) {
            return;
        }
        ConnectionsManager.lastPremiumFloodWaitShown = now;
        if (UserConfig.getInstance(currentAccount).isPremium() || MessagesController.getInstance(currentAccount).premiumFeaturesBlocked()) {
            return;
        }

        final float n;
        if (isUpload) {
            n = MessagesController.getInstance(currentAccount).uploadPremiumSpeedupUpload;
        } else {
            n = MessagesController.getInstance(currentAccount).uploadPremiumSpeedupDownload;
        }
        SpannableString boldN = new SpannableString(Double.toString(Math.round(n * 10) / 10.0).replaceAll("\\.0$", ""));
        boldN.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, boldN.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (hasStoryViewer()) return;
        BulletinFactory.of(this).createSimpleBulletin(
            R.raw.speed_limit,
            LocaleController.getString(isUpload ? R.string.UploadSpeedLimited : R.string.DownloadSpeedLimited),
            AndroidUtilities.replaceCharSequence("%d", AndroidUtilities.premiumText(LocaleController.getString(isUpload ? R.string.UploadSpeedLimitedMessage : R.string.DownloadSpeedLimitedMessage), () -> {
                presentFragment(new PremiumPreviewFragment(isUpload ? "upload_speed" : "download_speed"));
            }), boldN)
        ).setDuration(8000).show(true);
    }

    interface NagramCopyMesage {
        void run(int action);
    }

    public void didLongPressLink(ChatMessageCell cell, MessageObject messageObject, CharacterStyle span, String str) {
        final ItemOptions options = ItemOptions.makeOptions(ChatActivity.this, cell, true);
        final ScrimOptions dialog = new ScrimOptions(getContext(), themeDelegate);
        options.setOnDismiss(dialog::dismissFast);

        final boolean allowCustomTabs = !str.startsWith("video?") && !Browser.isInternalUri(Uri.parse(str), null);
        final boolean inAppBrowser = getMessagesController().isWebBrowserOpenInApp(str);
        final boolean customTabs = inAppBrowser && allowCustomTabs;
        final boolean isHashtag = str.startsWith("#") || str.startsWith("$");
        final boolean isMail = str.startsWith("mailto:");

        if (!isMail) {
            options.add(customTabs && !isHashtag ? R.drawable.menu_website : R.drawable.msg_openin, getString(customTabs && !isHashtag ? R.string.OpenInTelegramBrowser2 : R.string.Open), () -> {
                if (str.startsWith("video?")) {
                    didPressMessageUrl(span, false, messageObject, cell);
                } else if (customTabs && !isHashtag) {
                    Browser.openInTelegramBrowser(getParentActivity(), str, null);
                } else {
                    logSponsoredClicked(messageObject, false, false);
                    openClickableLink(span, str, false, cell, messageObject, false);
                }
            });
        }

        if (customTabs && !isHashtag || isMail) {
            options.add(R.drawable.msg_openin, getString(R.string.OpenInSystemBrowser2), () -> {
                if (MessagesController.getInstance(currentAccount).isWebBrowserExceptionsLimitReached(true)) {
                    Browser.openInExternalBrowser(getParentActivity(), str, false);
                } else {
                    AlertsCreator.showOpenExternalBrowserAlert(getParentActivity(), themeDelegate, str, true, true, (confirmed, checked) -> {
                        if (confirmed) {
                            if (checked) {
                                getMessagesController().addWebBrowserException(str, true);
                            }
                            Browser.openInExternalBrowser(getParentActivity(), str, false);
                        }
                    });
                }
            });
        } else if (!isMail && !isHashtag && !customTabs && allowCustomTabs && !inAppBrowser) {
            options.add(R.drawable.menu_website, getString(R.string.OpenInTelegramBrowser2), () -> {
                if (MessagesController.getInstance(currentAccount).isWebBrowserExceptionsLimitReached(false)) {
                    Browser.openInTelegramBrowser(getParentActivity(), str, null);
                } else {
                    AlertsCreator.showOpenExternalBrowserAlert(getParentActivity(), themeDelegate, str, false, true, (confirmed, checked) -> {
                        if (confirmed) {
                            if (checked) {
                                getMessagesController().addWebBrowserException(str, false);
                            }
                            Browser.openInTelegramBrowser(getParentActivity(), str, null);
                        }
                    });
                }
            });
        }

        TLRPC.MessageMedia media = MessageObject.getMedia(messageObject);
        if (media instanceof TLRPC.TL_messageMediaWebPage && media.webpage != null && media.webpage.cached_page != null && TextUtils.equals(media.webpage.url, str)) {
            options.add(R.drawable.menu_instant_view, getString(R.string.OpenInstantView), () -> {
                if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                    if (LaunchActivity.instance != null && LaunchActivity.instance.getBottomSheetTabs() != null && LaunchActivity.instance.getBottomSheetTabs().tryReopenTab(messageObject) != null) {
                        return;
                    }
                    ChatActivity.this.createArticleViewer(false).open(messageObject);
                }
            });
        }

        final int ACTION_SHARE = 0;
        final int ACTION_COPY = 1;
        final int ACTION_FORWARD = 2;

        NagramCopyMesage run1 = (int action) -> {
            String urlFinal = str;
            if (str.startsWith("video?") && messageObject != null && !messageObject.scheduled) {
                MessageObject messageObject1 = messageObject;
                boolean isMedia = messageObject.isVideo() || messageObject.isRoundVideo() || messageObject.isVoice() || messageObject.isMusic();
                if (!isMedia && messageObject.replyMessageObject != null) {
                    messageObject1 = messageObject.replyMessageObject;
                }
                long dialogId = messageObject1.getDialogId();
                int messageId = messageObject1.getId();
                String link = null;

                if (messageObject1.messageOwner.fwd_from != null) {
                    if (messageObject1.messageOwner.fwd_from.saved_from_peer != null) {
                        dialogId = MessageObject.getPeerId(messageObject1.messageOwner.fwd_from.saved_from_peer);
                        messageId = messageObject1.messageOwner.fwd_from.saved_from_msg_id;
                    } else if (messageObject1.messageOwner.fwd_from.from_id != null) {
                        dialogId = MessageObject.getPeerId(messageObject1.messageOwner.fwd_from.from_id);
                        messageId = messageObject1.messageOwner.fwd_from.channel_post;
                    }
                }
                int timestamp = -1;
                if (str.startsWith("video?")) {
                    timestamp = Utilities.parseInt(str);
                }
                if (DialogObject.isChatDialog(dialogId)) {
                    TLRPC.Chat currentChat = MessagesController.getInstance(currentAccount).getChat(-dialogId);
                    String username = ChatObject.getPublicUsername(currentChat);
                    if (currentChat != null && username != null) {
                        link = "https://t.me/" + username + "/" + messageId + "?t=" + AndroidUtilities.formatTimestamp(timestamp);
                    }
                } else {
                    TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(dialogId);
                    String username = UserObject.getPublicUsername(user);
                    if (user != null && username != null) {
                        link = "https://t.me/" + username + "/" + messageId + "?t=" + AndroidUtilities.formatTimestamp(timestamp);
                    }
                }
                if (link == null) {
                    return;
                }
                urlFinal = link;
                // AndroidUtilities.addToClipboard(link);
            } else {
                // AndroidUtilities.addToClipboard(str);
            }
            if (action == ACTION_COPY) {
                if (isMail) {
                    urlFinal = urlFinal.substring("mailto:".length());
                }
                AndroidUtilities.addToClipboard(urlFinal);
                createUndoView();
                if (undoView == null) {
                    return;
                }
                if (str.startsWith("@")) {
                    undoView.showWithAction(0, UndoView.ACTION_USERNAME_COPIED, null);
                } else if (str.startsWith("#") || str.startsWith("$")) {
                    undoView.showWithAction(0, UndoView.ACTION_HASHTAG_COPIED, null);
                } else {
                    undoView.showWithAction(0, UndoView.ACTION_LINK_COPIED, null);
                }
            } else {
                // ShareMessage
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, urlFinal);
                if (action == ACTION_FORWARD) {
                    shareIntent.setPackage(ApplicationLoader.applicationContext.getPackageName());
                }
                Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString(R.string.ShareFile));
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationLoader.applicationContext.startActivity(chooserIntent);
            }
        };
        options.add(R.drawable.msg_copy, getString(isHashtag ? R.string.CopyHashtag : isMail ? R.string.CopyMail : R.string.CopyLink), () -> {
            run1.run(ACTION_COPY);
        });
        options.add(R.drawable.msg_qrcode, getString(R.string.ShareQRCode), () -> {
            // QRCode
            ProxyUtil.showQrDialog(getParentActivity(), str);
        });
        options.add(R.drawable.msg_shareout, getString(R.string.ShareMessages), () -> {
            // ShareMessage
            run1.run(ACTION_SHARE);
        });
        options.add(R.drawable.msg_forward_noquote, getString(R.string.Forward), () -> {
            run1.run(ACTION_FORWARD);
        });

        if (inAppBrowser && !isHashtag && !isMail && !str.startsWith("tg:")) {
            options.add(R.drawable.outline_saved_24, getString(R.string.WebBookmarkAdd), () -> {
                ArticleViewer.addBookmark(str, currentAccount, contentView, null, themeDelegate);
            });
        }

        dialog.setItemOptions(options);
        if (str != null && str.startsWith("mailto:")) {
            SpannableString s = new SpannableString(str.substring(7));
            s.setSpan(span, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dialog.setScrim(cell, span, s);
        } else if (span instanceof URLSpanReplacement) {
            String formattedUrl = ((URLSpanReplacement) span).getURL();
            try {
                try {
                    Uri uri = Uri.parse(formattedUrl);
                    formattedUrl = Browser.replaceHostname(uri, Browser.IDN_toUnicode(uri.getHost()), null);
                } catch (Exception e) {
                    FileLog.e(e, false);
                }
                formattedUrl = URLDecoder.decode(formattedUrl.replaceAll("\\+", "%2b"), "UTF-8");
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (formattedUrl.length() > 204) {
                formattedUrl = formattedUrl.substring(0, 204) + "…";
            }
            SpannableString s = new SpannableString(formattedUrl);
            s.setSpan(span, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dialog.setScrim(cell, span, s);
        } else {
            dialog.setScrim(cell, span, null);
        }
        showDialog(dialog);
    }

    public void didLongPressFormattedDate(ChatMessageCell cell, CharacterStyle span, String originalText, TLRPC.TL_messageEntityFormattedDate entity) {
        if (entity == null) {
            return;
        }

        final MessageObject messageObject = cell.getMessageObject();
        if (messageObject == null) {
            return;
        }

        ArrayList<MessageObject> arrayList = null;
        if (messageObject.getGroupId() != 0) {
            MessageObject.GroupedMessages groupedMessages = groupedMessagesMap.get(messageObject.getGroupId());
            if (groupedMessages != null) {
                arrayList = groupedMessages.messages;
            }
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            arrayList.add(messageObject);
        }
        final ArrayList<MessageObject> finalArrayList = arrayList;

        final long selfDialogId = getUserConfig().getClientUserId();
        final String formattedDate = LocaleController.formatEntityFormattedDate(entity, true);

        final ItemOptions options = ItemOptions.makeOptions(ChatActivity.this, cell, true);
        final ScrimOptions dialog = new ScrimOptions(getContext(), themeDelegate);
        options.setOnDismiss(dialog::dismissFast);
        options.add(R.drawable.msg_copy, getString(R.string.RelativeDateMenuCopy), () -> {
            dialog.dismiss();
            AndroidUtilities.addToClipboard(formattedDate);
            BulletinFactory.of(ChatActivity.this).createCopyBulletin(getString(R.string.RelativeDateCopied)).show();
        });
        options.add(R.drawable.msg_calendar2, getString(R.string.RelativeDateMenuAddToACalendar), () -> {
            options.dontDismiss();

            final String description;
            if (!TextUtils.isEmpty(messageObject.caption)) {
                description = cell.getMessageObject().caption.toString();
            } else if (!TextUtils.isEmpty(messageObject.messageText)) {
                description = cell.getMessageObject().messageText.toString();
            } else {
                description = formattedDate;
            }

            final String title = (description.length() > 21) ?
                (description.substring(0, 21) + "...") : description;

            AndroidUtilities.createCalendarEvent(getParentActivity(), entity.date * 1000L, title, description, !entity.long_time && !entity.short_time);
            dialog.dismiss();
        });
        options.add(R.drawable.msg_notifications, getString(R.string.RelativeDateMenuSetAReminder), () -> {
            options.dontDismiss();

            AlertsCreator.createScheduleDatePickerDialog(getContext(), null, selfDialogId, entity.date, true, (notify, scheduleDate, scheduleRepeatPeriod) -> {
                if (notify) {
                    SendMessagesHelper.getInstance(currentAccount).sendMessage(finalArrayList, selfDialogId, false, false, true, scheduleDate, 0, null, -1, 0, 0, null);
                    AndroidUtilities.runOnUIThread(() -> {
                        final Bulletin bulletin = BulletinFactory.createForwardedBulletin(getContext(),
                                ChatActivity.this, null, 1, selfDialogId, 1,
                                getThemedColor(Theme.key_undo_background),
                                getThemedColor(Theme.key_undo_infoColor),
                                Bulletin.DURATION_PROLONG, true, null, null
                        );
                        bulletin.allowBlur().show(true);
                    }, 400);
                    dialog.dismiss();
                }
            }, dialog::dismiss);
        });

        SpannableString s = new SpannableString(formattedDate);
        s.setSpan(span, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        dialog.setItemOptions(options);
        dialog.setScrim(cell, span, s);
        showDialog(dialog);
    }


    public void didLongPressCard(ChatMessageCell cell, CharacterStyle link, String card) {
        final Browser.Progress progress = makeProgressForLink(cell, link);
        TLRPC.TL_payments_getBankCardData req = new TLRPC.TL_payments_getBankCardData();
        req.number = card;
        int reqId = getConnectionsManager().sendRequest(req, (res, err) -> AndroidUtilities.runOnUIThread(() -> {
            progress.end();

            final ItemOptions options = ItemOptions.makeOptions(ChatActivity.this, cell, true);
            final ScrimOptions dialog = new ScrimOptions(getContext(), themeDelegate);
            options.setOnDismiss(dialog::dismissFast);
            options.add(R.drawable.msg_copy, getString(R.string.CopyCardNumber), () -> {
                dialog.dismiss();
                AndroidUtilities.addToClipboard(card);
                BulletinFactory.of(ChatActivity.this).createCopyBulletin(getString(R.string.CardNumberCopied)).show();
            });
            if (res instanceof TLRPC.TL_payments_bankCardData) {
                TLRPC.TL_payments_bankCardData i = (TLRPC.TL_payments_bankCardData) res;
                for (TLRPC.TL_bankCardOpenUrl d : i.open_urls) {
                    options.add(R.drawable.msg_payment_card, d.name, () -> {
                        Browser.openUrl(getContext(), d.url, inlineReturn == 0, false);
                    });
                }
                if (!TextUtils.isEmpty(i.title)) {
                    options.addGap();
                    options.addText(i.title, 13, dp(200));
                }
            }

            dialog.setItemOptions(options);
            dialog.setScrim(cell, link, null);
            showDialog(dialog);
        }), null, null, 0, getMessagesController().webFileDatacenterId, ConnectionsManager.ConnectionTypeGeneric, true);
        progress.onCancel(() -> getConnectionsManager().cancelRequest(reqId, true));
        progress.init();
    }

    public void didLongPressUsername(ChatMessageCell cell, CharacterStyle link, String username) {
        final Browser.Progress progress = makeProgressForLink(cell, link);
        TLObject cachedObject = getMessagesController().getUserOrChat(username);
        Utilities.Callback2<TLObject, Boolean> open = (obj, selling) -> {
            progress.end();

            boolean isUser = false, isGroup = false, isChannel = false;
            final long did;
            if (obj instanceof TLRPC.User) {
                did = ((TLRPC.User) obj).id;
                isUser = true;
            } else if (obj instanceof TLRPC.Chat) {
                did = -((TLRPC.Chat) obj).id;
                isChannel = ChatObject.isChannelAndNotMegaGroup((TLRPC.Chat) obj);
                isGroup = !isChannel;
            } else did = 0;

            final ItemOptions options = ItemOptions.makeOptions(ChatActivity.this, cell, true);
            final ScrimOptions dialog = new ScrimOptions(getContext(), themeDelegate);
            options.setOnDismiss(dialog::dismissFast);

            if (did != 0) {
                options.add(isChannel ? R.drawable.msg_channel : R.drawable.msg_discussion, getString(isChannel ? R.string.ViewChannel : R.string.SendMessage), () -> {
                    presentFragment(ChatActivity.of(did));
                });
            }
            options.add(R.drawable.msg_copy, getString(R.string.ProfileCopyUsername), () -> {
                dialog.dismiss();
                AndroidUtilities.addToClipboard("@" + username);
                BulletinFactory.of(ChatActivity.this).createCopyBulletin(getString(R.string.UsernameCopied)).show();
            });
            if (selling) {
                options.add(R.drawable.outline_gram_24, getString(R.string.BuyUsernameOnFragment), () -> {
                    Browser.openUrl(getContext(), "https://fragment.com/username/" + username);
                });
            }
            options.addGap();
            if (did != 0) {
                options.addProfile(obj, getString(isUser ? R.string.ViewProfile : (isChannel ? R.string.ViewChannelProfile : R.string.ViewGroupProfile)), () -> {
                    presentFragment(ProfileActivity.of(did));
                });
            } else {
                options.addText(getString(R.string.NoUsernameFound2), 13, dp(200));
            }

            dialog.setItemOptions(options);
            dialog.setScrim(cell, link, null);
            showDialog(dialog);
        };
        if (true || BuildVars.DEBUG_PRIVATE_VERSION) {
            TL_account.checkUsername req2 = new TL_account.checkUsername();
            req2.username = username;
            int reqId2 = getConnectionsManager().sendRequest(req2, (res2, err2) -> AndroidUtilities.runOnUIThread(() -> {
                final boolean selling = err2 != null && "USERNAME_PURCHASE_AVAILABLE".equals(err2.text);
                if (cachedObject != null || err2 == null && res2 instanceof TLRPC.TL_boolTrue) {
                    open.run(cachedObject, selling);
                } else {
                    TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                    req.username = username;
                    int reqId = getConnectionsManager().sendRequest(req, (res, err) -> AndroidUtilities.runOnUIThread(() -> {
                        progress.end();

                        TLObject obj = null;
                        if (res instanceof TLRPC.TL_contacts_resolvedPeer) {
                            TLRPC.TL_contacts_resolvedPeer r = (TLRPC.TL_contacts_resolvedPeer) res;
                            getMessagesController().putUsers(r.users, false);
                            getMessagesController().putChats(r.chats, false);

                            long did = DialogObject.getPeerDialogId(r.peer);
                            if (did >= 0) {
                                obj = getMessagesController().getUser(did);
                            } else if (did < 0) {
                                obj = getMessagesController().getChat(-did);
                            }
                        }
                        open.run(obj, selling);
                    }));
                    progress.onCancel(() -> getConnectionsManager().cancelRequest(reqId, true));
                    progress.init();
                }
            }));
            progress.onCancel(() -> getConnectionsManager().cancelRequest(reqId2, true));
            progress.init();
        } else {
            if (cachedObject != null) {
                open.run(cachedObject, false);
            } else {
                TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                req.username = username;
                int reqId = getConnectionsManager().sendRequest(req, (res, err) -> AndroidUtilities.runOnUIThread(() -> {
                    progress.end();

                    TLObject obj = null;
                    if (res instanceof TLRPC.TL_contacts_resolvedPeer) {
                        TLRPC.TL_contacts_resolvedPeer r = (TLRPC.TL_contacts_resolvedPeer) res;
                        getMessagesController().putUsers(r.users, false);
                        getMessagesController().putChats(r.chats, false);

                        long did = DialogObject.getPeerDialogId(r.peer);
                        if (did >= 0) {
                            obj = getMessagesController().getUser(did);
                        } else if (did < 0) {
                            obj = getMessagesController().getChat(-did);
                        }
                    }
                    open.run(obj, false);
                }));
                progress.onCancel(() -> getConnectionsManager().cancelRequest(reqId, true));
                progress.init();
            }
        }
    }

    public void didLongPressCopyButton(String text) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity(), false, themeDelegate);
        builder.setTitle(text);
        builder.setTitleMultipleLines(true);
        builder.setItems(new CharSequence[] { getString(R.string.Copy) }, (dialog, which) -> {
            AndroidUtilities.addToClipboard(text);
            BulletinFactory.of(ChatActivity.this).createCopyBulletin(formatString(R.string.ExactTextCopied, text)).show();
        });
        showDialog(builder.create());
    }

    public void didPressPhoneNumber(ChatMessageCell cell, CharacterStyle link, String phone) {
        final Browser.Progress progress = makeProgressForLink(cell, link);
        final TLRPC.TL_contact contact = getContactsController().contactsByPhone.get(PhoneFormat.stripExceptNumbers(phone));
        Utilities.Callback<TLRPC.User> open = user -> {
            TLRPC.UserFull userInfo = user != null ? getMessagesController().getUserFull(user.id) : null;

            final ItemOptions options = ItemOptions.makeOptions(ChatActivity.this, cell, true);
            final ScrimOptions dialog = new ScrimOptions(getContext(), themeDelegate);
            options.setOnDismiss(dialog::dismiss);

            Utilities.Callback<Boolean> addToContacts = asNew -> {
                if (getParentActivity() == null) return;
                Intent intent;
                if (asNew) {
                    intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                } else {
                    intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                }
                if (user != null) {
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, ContactsController.formatName(user.first_name, user.last_name));
                }
                ArrayList<ContentValues> data = new ArrayList<>();
                ContentValues row = new ContentValues();
                row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                String thisPhone = phone;
                if (!thisPhone.startsWith("+")) {
                    TLRPC.User myself = getUserConfig().getCurrentUser();
                    HashMap<String, List<CountrySelectActivity.Country>> codesMap = new HashMap<>();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt")));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] args = line.split(";");
                            CountrySelectActivity.Country countryWithCode = new CountrySelectActivity.Country();
                            countryWithCode.name = args[2];
                            countryWithCode.code = args[0];
                            countryWithCode.shortname = args[1];
                            List<CountrySelectActivity.Country> countryList = codesMap.get(args[0]);
                            if (countryList == null) {
                                codesMap.put(args[0], countryList = new ArrayList<>());
                            }
                            countryList.add(countryWithCode);
                        }
                        reader.close();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    boolean foundCountry = false;
                    String myphone = myself.phone;
                    for (int a = 4; a >= 1; a--) {
                        String sub = myphone.substring(0, a);
                        List<CountrySelectActivity.Country> country = codesMap.get(sub);
                        if (country != null && country.size() > 0) {
                            final String regionCode = country.get(0).code;
                            if (regionCode.endsWith("0") && thisPhone.startsWith("0")) {
                                thisPhone = thisPhone.substring(1);
                            }
                            thisPhone = "+" + regionCode + thisPhone;
                            foundCountry = true;
                            break;
                        }
                    }
                    if (!foundCountry && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        final Context ctx = ApplicationLoader.applicationContext;
                        final String regionCode = (ctx != null) ? ctx.getSystemService(TelephonyManager.class).
                                getSimCountryIso().toUpperCase(Locale.US) : Locale.getDefault().getCountry();
                        if (regionCode.endsWith("0") && thisPhone.startsWith("0")) {
                            thisPhone = thisPhone.substring(1);
                        }
                        thisPhone = "+" + regionCode + thisPhone;
                    }
                }
                row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, thisPhone);
                row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                data.add(row);
                intent.putExtra("finishActivityOnSaveCompleted", true);
                intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
                getParentActivity().startActivity(intent);
            };
            ItemOptions subOptions = options.makeSwipeback();
            subOptions.add(R.drawable.ic_ab_back, getString(R.string.Back), options::closeSwipeback);
            subOptions.addGap();
            subOptions.add(R.drawable.msg_addbot, getString(R.string.CreateNewContact), () -> {
                options.dismiss();
                NewContactBottomSheet sheet = new NewContactBottomSheet(this, getContext()).setInitialPhoneNumber(phone, false);
                sheet.show();
            });
            subOptions.add(R.drawable.menu_contact_existing, getString(R.string.AddToExistingContact), () -> addToContacts.run(false));

            if (contact == null && (user == null || !getContactsController().contactsDict.containsKey(user.id))) {
                options.add(R.drawable.msg_contact_add, getString(R.string.AddToContacts), () -> options.openSwipeback(subOptions));
                options.addGap();
            }
            if (user == null) {
                options.add(R.drawable.menu_invit_telegram, getString(R.string.InviteToTelegramShort), () -> {
                    if (getParentActivity() == null) return;
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null));
                        intent.putExtra("sms_body", ContactsController.getInstance(currentAccount).getInviteText(1));
                        getParentActivity().startActivityForResult(intent, 500);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                });
                options.add(R.drawable.msg_calls_regular, getString(R.string.VoiceCallViaCarrier), () -> {
                    Browser.openUrl(getContext(), "tel:" + phone);
                });
                options.add(R.drawable.msg_copy, getString(R.string.CopyNumber), () -> {
                    AndroidUtilities.addToClipboard(phone);
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.PhoneCopied)).show();
                });
                options.addGap();
                options.addText(getString(R.string.NumberNotOnTelegram), 13);
            } else {
                options.add(R.drawable.msg_discussion, getString(R.string.SendMessage), () -> presentFragment(ChatActivity.of(user.id)));
                if (!UserObject.isUserSelf(user)) {
                    options.add(R.drawable.msg_calls, getString(R.string.VoiceCallViaTelegram), () -> {
                        VoIPHelper.startCall(user, false, userInfo != null && userInfo.video_calls_available, getParentActivity(), userInfo, getAccountInstance());
                    });
                    options.add(R.drawable.msg_videocall, getString(R.string.VideoCallViaTelegram), () -> {
                        VoIPHelper.startCall(user, true, userInfo != null && userInfo.video_calls_available, getParentActivity(), userInfo, getAccountInstance());
                    });
                }
                options.add(R.drawable.msg_calls_regular, getString(R.string.VoiceCallViaCarrier), () -> {
                    Browser.openUrl(getContext(), "tel:" + phone);
                });
                options.add(R.drawable.msg_copy, getString(R.string.CopyNumber), () -> {
                    AndroidUtilities.addToClipboard(phone);
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.PhoneCopied)).show();
                });
                options.addGap();
                options.addProfile(user, getString(R.string.ViewProfile), () -> {
                    dialog.dismiss();
                    presentFragment(ProfileActivity.of(user.id));
                });
            }

            dialog.setItemOptions(options);
            if (link instanceof URLSpanReplacement) {
                String formattedUrl = ((URLSpanReplacement) link).getURL();
                if (formattedUrl == null) formattedUrl = "";
                formattedUrl = formattedUrl.trim();
                if (formattedUrl.startsWith("tel:")) {
                    formattedUrl = formattedUrl.substring(4);
                }
                if (formattedUrl.length() > 204) {
                    formattedUrl = formattedUrl.substring(0, 204) + "…";
                }
                final SpannableString s = new SpannableString(formattedUrl);
                s.setSpan(link, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dialog.setScrim(cell, link, s);
            } else {
                dialog.setScrim(cell, link, null);
            }
            showDialog(dialog);
        };
        if (contact != null) {
            TLRPC.User user = getMessagesController().getUser(contact.user_id);
            if (user != null) {
                open.run(user);
            } else {
                getMessagesStorage().getStorageQueue().postRunnable(() -> {
                    TLRPC.User user2 = getMessagesStorage().getUser(contact.user_id);
                    AndroidUtilities.runOnUIThread(() -> open.run(user2));
                });
            }
        } else {
            TLRPC.TL_contacts_resolvePhone req = new TLRPC.TL_contacts_resolvePhone();
            req.phone = PhoneFormat.stripExceptNumbers(phone);
            int reqId = getConnectionsManager().sendRequest(req, (res, err) -> AndroidUtilities.runOnUIThread(() -> {
                progress.end();

                TLRPC.User user = null;
                if (res instanceof TLRPC.TL_contacts_resolvedPeer) {
                    TLRPC.TL_contacts_resolvedPeer r = (TLRPC.TL_contacts_resolvedPeer) res;
                    getMessagesController().putUsers(r.users, false);
                    getMessagesController().putChats(r.chats, false);

                    long did = DialogObject.getPeerDialogId(r.peer);
                    if (did >= 0) {
                        user = getMessagesController().getUser(did);
                    }
                }
                open.run(user);
            }));
            progress.onCancel(() -> getConnectionsManager().cancelRequest(reqId, true));
            progress.init();
        }
    }

    private boolean restoringFirstViewPageVisibility;

    public class FirstViewPage extends View {

        private final ArrayList<View> views = new ArrayList<>();
        private final ArrayList<View> viewsToMakeVisible = new ArrayList<>();

        public FirstViewPage(Context context) {
            super(context);
        }

        private void updateViews() {
            views.clear();
            views.add(topChatPanelView);
            views.add(chatListView);
            views.add(chatActivityFadeView);
            views.add(messagesSearchListContainer);
            views.add(mentionContainer);
            views.add(floatingDateView);
            views.add(chatActivityEnterView);
            views.add(sideControlsButtonsLayout);
            views.add(chatInputViewsContainer);
            if (chatInputViewsContainer != null) {
                views.add(chatInputViewsContainer.getFadeView());
            }
            views.add(topicsTabs);

            views.removeAll(Collections.singleton(null));
        }

        @Override
        public void setTranslationX(float translationX) {
            super.setTranslationX(translationX);
            updateViews();
            for (View view : views) {
                if (view != null) {
                    view.setTranslationX(translationX);
                }
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            restoringFirstViewPageVisibility = true;
            for (View view : viewsToMakeVisible) {
                view.setVisibility(View.VISIBLE);
            }
            viewsToMakeVisible.clear();
            restoringFirstViewPageVisibility = false;
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            updateViews();
            restoringFirstViewPageVisibility = true;
            for (View view : views) {
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                    viewsToMakeVisible.add(view);
                }
            }
            restoringFirstViewPageVisibility = false;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            return false;
        }

    }

    private void checkHashtagStories(boolean instant) {
        if (isFeedSearch()) return;
        if (searchType != SEARCH_PUBLIC_POSTS) return;
        if (messagesSearchAdapter == null) return;
        messagesSearchAdapter.searchStories(searchingHashtag, instant);
    }

    public float getClipTop() {
        return chatListView.getY() + ((fragmentTransition == null || (fromPullingDownTransition && !toPullingDownTransition)) && !isInsideContainer ? chatListViewPaddingTop - chatListViewPaddingVisibleOffset - AndroidUtilities.dp(4) : 0);
    }

    public float getClipBottom() {
        return fragmentView.getBottom() - chatListView.getBottom() + ((fragmentTransition == null || (fromPullingDownTransition && !toPullingDownTransition)) && !isInsideContainer ? blurredViewBottomOffset : 0);
    }

    private void gotChatInfo() {
        if (chatInfo != null && chatInfo.paid_reactions_available) {
            getMessagesController().getPaidReactionsDialogId();
            if (!StarsController.getInstance(currentAccount).balanceAvailable()) {
                StarsController.getInstance(currentAccount).getBalance();
            }
        }
        if (chatInfo != null && chatInfo.bot_verification != null) {
            updateTopPanel(true);
        }

        if (chatInfo != null) {
            final boolean giftUpdate = (chatInfo.stargifts_available)
                != (bottomChannelButtonsLayout != null && bottomChannelButtonsLayout.isButtonVisible(ChatActivityChannelButtonsLayout.BUTTON_GIFT));

            final boolean suggestUpdate = (currentChat != null && currentChat.broadcast_messages_allowed && currentChat.linked_monoforum_id != 0)
                != (bottomChannelButtonsLayout != null && bottomChannelButtonsLayout.isButtonVisible(ChatActivityChannelButtonsLayout.BUTTON_DIRECT));

            if (giftUpdate || suggestUpdate) {
                updateBottomOverlay(true);
            }
        }
    }

    @Override
    public boolean allowFinishFragmentInsteadOfRemoveFromStack() {
        return !inPreviewMode;
    }

    private float getHashtagTabsShownT() {
        ChatActivity chatActivity = parentChatActivity != null ? parentChatActivity : this;
        if (chatActivity.hashtagSearchTabs == null) return 0;
        return chatActivity.hashtagSearchTabs.shownT;
    }

    public void didPressReaction(View cell, TLRPC.ReactionCount reaction, boolean longpress, float x, float y) {
        if (getParentActivity() == null || getContext() == null) {
            return;
        }
        if (savedMessagesTagHint != null && savedMessagesTagHint.shown()) {
            savedMessagesTagHint.hide();
        }
        if (videoConversionTimeHint != null && videoConversionTimeHint.shown()) {
            videoConversionTimeHint.hide();
        }
        MessageObject messageObject;
        if (cell instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) cell).getPrimaryMessageObject();
        } else if (cell instanceof ChatActionCell) {
            messageObject = ((ChatActionCell) cell).getMessageObject();
        } else {
            return;
        }
        if (messageObject == null) return;
        if (getUserConfig().getClientUserId() == getDialogId() && messageObject.areTags() && !getUserConfig().isPremium()) {
            if (longpress) return;
            new PremiumFeatureBottomSheet(ChatActivity.this, PremiumPreviewFragment.PREMIUM_FEATURE_SAVED_TAGS, true).show();
            return;
        }
        if (longpress && reaction.reaction instanceof TLRPC.TL_reactionPaid) {
            if (!NekoConfig.disableVibration.Bool()) cell.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            ArrayList<TLRPC.MessageReactor> reactors = null;
            if (messageObject.messageOwner != null && messageObject.messageOwner.reactions != null) {
                reactors = messageObject.messageOwner.reactions.top_reactors;
            }
            StarsController.getInstance(currentAccount).commitPaidReaction();
            TLRPC.ChatFull chatFull = getMessagesController().getChatFull(-StarsController.MessageId.from(messageObject).did);
            final StarsReactionsSheet sheet = new StarsReactionsSheet(getContext(), currentAccount, dialog_id, ChatActivity.this, messageObject, reactors, chatFull == null || chatFull.paid_reactions_available, false, 0, themeDelegate);
            sheet.setMessageCell(ChatActivity.this, messageObject.getId(), findMessageCell(messageObject.getId(), true));
            sheet.show();
            return;
        }
        if (longpress || messageObject.areTags() && (isInsideContainer || searchingReaction != null && searchingReaction.isSame(reaction.reaction))) {
            if (!NekoConfig.disableVibration.Bool()) cell.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            FrameLayout scrimPopupContainerLayout = new FrameLayout(getParentActivity()) {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        closeMenu();
                    }
                    return super.dispatchKeyEvent(event);
                }

                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int h = Math.min(MeasureSpec.getSize(heightMeasureSpec), AndroidUtilities.dp(ReactedUsersListView.VISIBLE_ITEMS * ReactedUsersListView.ITEM_HEIGHT_DP));
                    if (h == 0) {
                        h = AndroidUtilities.dp(ReactedUsersListView.VISIBLE_ITEMS * ReactedUsersListView.ITEM_HEIGHT_DP);
                    }
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.AT_MOST));
                }
            };
            scrimPopupContainerLayout.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

            final ReactionsLayoutInBubble.VisibleReaction reactionObj = ReactionsLayoutInBubble.VisibleReaction.fromTL(reaction.reaction);
            ReactionsLayoutInBubble.ReactionButton button = null;
            ReactionsLayoutInBubble reactionsLayoutInBubble;
            if (cell instanceof ChatMessageCell) {
                reactionsLayoutInBubble = ((ChatMessageCell) cell).reactionsLayoutInBubble;
                button = reactionsLayoutInBubble.getReactionButton(reactionObj);
            } else if (cell instanceof ChatActionCell) {
                reactionsLayoutInBubble = ((ChatActionCell) cell).reactionsLayoutInBubble;
                button = reactionsLayoutInBubble.getReactionButton(reactionObj);
            } else return;
            if (button == null) {
                return;
            }

            scrimPopupContainerLayout.setClipToOutline(true);
            scrimPopupContainerLayout.setOutlineProvider(ViewOutlineProviderImpl.boundsWithPaddingRoundRect(dp(8), dp(12)));
            scrimPopupContainerLayout.setBackground(scrimBlur3Factory.create(scrimPopupContainerLayout, true)
                .setColorProvider(BlurredBackgroundProviderImpl.scrimMenuBackground(resourceProvider))
                .setRadius(dp(12))
                .setPadding(dp(8))
                .setHasPadding(true));

            float bottom = reactionsLayoutInBubble.y + button.y + AndroidUtilities.dp(28);
            float left = reactionsLayoutInBubble.x + button.x;
            int[] loc = new int[2];
            cell.getLocationInWindow(loc);
            boolean forceBottom = false;
            final boolean tags = getUserConfig().getClientUserId() == getDialogId() && !getMessagesController().getSavedMessagesController().unsupported;
            if (tags) {
                MessagesController.getGlobalMainSettings().edit().putInt("savedsearchtaghint", 1).apply();

                ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity(), 0, getResourceProvider(), 0);
                if (getUserConfig().isPremium()) {
                    ActionBarMenuSubItem editTag = new ActionBarMenuSubItem(getParentActivity(), false, false);
                    editTag.setTextAndIcon(LocaleController.getString(TextUtils.isEmpty(getMessagesController().getSavedTagName(reaction.reaction)) ? R.string.SavedTagLabelTag : R.string.SavedTagRenameTag), R.drawable.menu_tag_rename);
                    editTag.setMinimumWidth(160);
                    editTag.setOnClickListener(view -> {
                        closeMenu();
                        SearchTagsList.openRenameTagAlert(getContext(), currentAccount, reaction.reaction, themeDelegate, false);
                    });
                    popupLayout.addView(editTag);
                }
                if (!isInsideContainer && (searchingReaction == null || !searchingReaction.isSame(reaction.reaction)) && (chatMode == 0 || chatMode == MODE_SAVED)) {
                    ActionBarMenuSubItem filterByTag = new ActionBarMenuSubItem(getParentActivity(), false, false);
                    filterByTag.setTextAndIcon(LocaleController.getString(R.string.SavedTagFilterByTag), R.drawable.menu_tag_filter);
                    filterByTag.setMinimumWidth(160);
                    filterByTag.setOnClickListener(view -> {
                        closeMenu();
                        openSearchWithText("");
                        if (actionBarSearchTags != null) {
                            actionBarSearchTags.setChosen(ReactionsLayoutInBubble.VisibleReaction.fromTL(reaction.reaction), true);
                        }
                    });
                    popupLayout.addView(filterByTag);
                }
                ActionBarMenuSubItem removeTag = new ActionBarMenuSubItem(getParentActivity(), false, false);
                removeTag.setTextAndIcon(LocaleController.getString(R.string.SavedTagRemoveTag), R.drawable.menu_tag_delete);
                removeTag.setMinimumWidth(160);
                removeTag.setOnClickListener(view -> {
                    pressedReaction(cell, reaction, 0, 0);
                });
                removeTag.setColors(Theme.getColor(Theme.key_color_red), Theme.getColor(Theme.key_color_red));
                popupLayout.addView(removeTag);
                scrimPopupContainerLayout.addView(popupLayout);
            } else if (messageObject != null && messageObject.messageOwner != null && messageObject.messageOwner.reactions != null && messageObject.messageOwner.reactions.can_see_list || dialog_id >= 0) {
                final boolean canDeleteReactions = ChatObject.canUserDoAdminAction(currentChat, ChatObject.ACTION_DELETE_MESSAGES)
                    && (reaction.count > 1 || !reaction.chosen);

                if (reaction.reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                    button.stopAnimation();
                }

                final LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(new ReactedUsersListView(getParentActivity(), themeDelegate, currentAccount, messageObject, reaction, false, false)
                    .setOnCustomEmojiSelectedListener((reactedUsersListView1, customEmojiStickerSets) -> {
                        if (getParentActivity() == null || getContext() == null) return;
                        EmojiPacksAlert alert = new EmojiPacksAlert(ChatActivity.this, getParentActivity(), themeDelegate, customEmojiStickerSets) {
                            @Override
                            public void dismiss() {
                                super.dismiss();
                                dimBehindView(false);
                            }
                        };
                        alert.setCalcMandatoryInsets(isKeyboardVisible());
                        alert.setDimBehind(false);
                        closeMenu(false);
                        showDialog(alert);
                    })
                    .setOnProfileSelectedListener((view1, userId, messagePeerReaction) -> {
                        Bundle args = new Bundle();
                        if (userId > 0) {
                            args.putLong("user_id", userId);
                        } else {
                            args.putLong("chat_id", -userId);
                        }
                        if (!(messagePeerReaction == null || messagePeerReaction.reaction == null)) {
                            args.putInt("report_reaction_message_id", messageObject.getId());
                            args.putLong("report_reaction_from_dialog_id", dialog_id);
                        }
                        ProfileActivity fragment = new ProfileActivity(args);
                        presentFragment(fragment);
                        closeMenu();
                    }).setOnProfileLongSelectedListener((view, userId, messagePeerReaction) -> {
                        if (messagePeerReaction == null || messagePeerReaction.reaction == null || userId == getUserConfig().getClientUserId() || !canDeleteReactions) {
                            return;
                        }

                        final ArrayList<MessageObject> arrMessages = new ArrayList<>(1);
                        arrMessages.add(messageObject);

                        final TLObject userOrChat = getMessagesController().getUserOrChat(userId);
                        final ArrayList<TLObject> actionParticipants = new ArrayList<>(1);
                        actionParticipants.add(userOrChat);

                        final TLRPC.ChannelParticipant[] participants = new TLRPC.ChannelParticipant[1];
                        final TLRPC.TL_channels_getParticipant req = new TLRPC.TL_channels_getParticipant();
                        req.channel = MessagesController.getInputChannel(currentChat);
                        req.participant = MessagesController.getInputPeer(userOrChat);
                        getConnectionsManager().sendRequestTyped(req, AndroidUtilities::runOnUIThread, (res, err) -> {
                            if (res != null) {
                                getMessagesController().putUsers(res.users, false);
                                getMessagesController().putChats(res.chats, false);
                                participants[0] = res.participant;
                            }
                            new DeleteMessagesBottomSheet(this, currentChat,
                                arrMessages, actionParticipants, participants,
                                mergeDialogId, (int) getTopicId(),
                                chatMode, true, () -> {}).show();
                        });
                        closeMenu();
                    }), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

                if (canDeleteReactions) {
                    final int bgColor = Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, themeDelegate), 0.06f);
                    final ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(contentView.getContext(), themeDelegate);
                    gapView.setColor(bgColor);

                    linearLayout.addView(gapView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 8));
                    View tapAndHoldView = createMenuTextOption(getContext(), themeDelegate, getString(R.string.TapAndHoldToDeleteReaction));
                    tapAndHoldView.setMinimumHeight(dp(32));
                    linearLayout.addView(tapAndHoldView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                }

                scrimPopupContainerLayout.addView(linearLayout, LayoutHelper.createFrame(240, LayoutHelper.WRAP_CONTENT));
            } else if (reaction.reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                TLRPC.TL_reactionCustomEmoji customEmoji = (TLRPC.TL_reactionCustomEmoji) reaction.reaction;
                TLRPC.InputStickerSet inputStickerSet = AnimatedEmojiDrawable.getDocumentFetcher(currentAccount).findStickerSet(customEmoji.document_id);
                if (inputStickerSet != null) {
                    button.stopAnimation();
                    ArrayList<TLRPC.InputStickerSet> arr = new ArrayList<TLRPC.InputStickerSet>();
                    arr.add(inputStickerSet);
                    MessageContainsEmojiButton setButton = new MessageContainsEmojiButton(currentAccount, getContext(), themeDelegate, arr, MessageContainsEmojiButton.SINGLE_REACTION_TYPE);
                    setButton.setOnClickListener(v -> {
                        if (getParentActivity() == null || getContext() == null) return;
                        new EmojiPacksAlert(ChatActivity.this, getContext(), themeDelegate, arr).show();
                        closeMenu();
                    });
                    scrimPopupContainerLayout.addView(setButton, LayoutHelper.createFrame(240, LayoutHelper.WRAP_CONTENT));
                    forceBottom = true;
                } else {
                    scrimPopupContainerLayout.setVisibility(View.GONE);
                }
            } else {
                scrimPopupContainerLayout.setVisibility(View.GONE);
            }

            scrimPopupWindow = new ActionBarPopupWindow(scrimPopupContainerLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT) {
                @Override
                public void dismiss() {
                    super.dismiss();
                    if (scrimPopupWindow != this) {
                        return;
                    }
                    scrimPopupWindow = null;
                    menuDeleteItem = null;
                    scrimPopupWindowItems = null;
                    chatLayoutManager.setCanScrollVertically(true);
                    if (scrimPopupWindowHideDimOnDismiss) {
                        dimBehindView(false);
                    } else {
                        scrimPopupWindowHideDimOnDismiss = true;
                    }
                    if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                        chatActivityEnterView.getEditField().setAllowDrawCursor(true);
                    }
                }
            };
            scrimPopupWindow.setPauseNotifications(true);
            scrimPopupWindow.setDismissAnimationDuration(220);
            scrimPopupWindow.setOutsideTouchable(true);
            scrimPopupWindow.setClippingEnabled(true);
            scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            scrimPopupWindow.setFocusable(true);
            scrimPopupContainerLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST));
            scrimPopupWindow.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
            scrimPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            scrimPopupWindow.getContentView().setFocusableInTouchMode(true);

            int totalHeight = contentView.getHeight();
            int height = scrimPopupContainerLayout.getMeasuredHeight();
            int keyboardHeight = contentView.measureKeyboardHeight();
            if (keyboardHeight > AndroidUtilities.dp(20)) {
                totalHeight += keyboardHeight;
            }

            int popupX = (int) (left - AndroidUtilities.dp(28));
            popupX = Math.max(AndroidUtilities.dp(6), Math.min(chatListView.getMeasuredWidth() - AndroidUtilities.dp(6) - scrimPopupContainerLayout.getMeasuredWidth(), popupX));
            if (AndroidUtilities.isTablet()) {
                int[] location = new int[2];
                fragmentView.getLocationInWindow(location);
                popupX += location[0];
            }
            int popupY;
            if (height < totalHeight) {
                float cellY = chatListView.getY() + cell.getY();
                if (isInsideContainer) {
                    int[] location = new int[2];
                    cell.getLocationInWindow(location);
                    cellY = location[1];
                }
                if (height < totalHeight / 2f && cellY + reactionsLayoutInBubble.y + button.y > totalHeight / 2f && !forceBottom) {
                    scrimViewReactionOffset = -(height - dp(12));
                    popupY = (int) (cellY + reactionsLayoutInBubble.y + button.y - height);
                } else {
                    scrimViewReactionOffset = 0;
                    popupY = (int) (cellY + reactionsLayoutInBubble.y + button.y + button.height);
                }
            } else {
                scrimViewReactionOffset = 0;
                popupY = inBubbleMode ? 0 : AndroidUtilities.statusBarHeight;
            }
            if (scrimPopupContainerLayout.getVisibility() != View.VISIBLE) {
                scrimViewReactionOffset = 0;
            }
            scrimPopupWindow.showAtLocation(chatListView, Gravity.LEFT | Gravity.TOP, scrimPopupX = popupX, scrimPopupY = popupY);

            chatListView.stopScroll();
            chatLayoutManager.setCanScrollVertically(false);
            scrimViewTask = null;
            scrimViewReaction = reaction.reaction.hashCode();
            scrimViewReactionAnimated = reaction.reaction instanceof TLRPC.TL_reactionCustomEmoji && LiteMode.isEnabled(LiteMode.FLAG_ANIMATED_EMOJI_KEYBOARD);
            dimBehindView(cell, true, true);
            hideHints(false);
            if (topUndoView != null) {
                topUndoView.hide(true, 1);
            }
            if (undoView != null) {
                undoView.hide(true, 1);
            }
            if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                chatActivityEnterView.getEditField().setAllowDrawCursor(false);
            }
        } else if (messageObject.areTags() && (chatMode == 0 || chatMode == MODE_SAVED)) {
            closeMenu();
            openSearchWithText("");
            if (actionBarSearchTags != null) {
                actionBarSearchTags.setChosen(ReactionsLayoutInBubble.VisibleReaction.fromTL(reaction.reaction), true);
            }
        } else if (reaction != null) {
            pressedReaction(cell, reaction, x, y);
        }
    }

    private void pressedReaction(View cell, TLRPC.ReactionCount reaction, float x, float y) {
        ReactionsLayoutInBubble.VisibleReaction visibleReaction = ReactionsLayoutInBubble.VisibleReaction.fromTL(reaction.reaction);
        MessageObject messageObject;
        if (cell instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) cell).getPrimaryMessageObject();
        } else if (cell instanceof ChatActionCell) {
            messageObject = ((ChatActionCell) cell).getMessageObject();
        } else return;
        selectReaction(cell, messageObject, null, null, x, y, visibleReaction,false, false, false, false);
        if (messageObject != null && messageObject.messageOwner != null) {
            if (chatAdapter.isFiltered) {
                MessageObject realMessage = messagesDict[0].get(messageObject.getId());
                if (realMessage != null && realMessage.messageOwner != null) {
                    realMessage.messageOwner.reactions = messageObject.messageOwner.reactions;
                }
            } else if (!chatAdapter.isFiltered && searchingReaction != null) {
                updateFilteredMessages(false);
            }
        }
        if (chatAdapter.isFiltered && !messageObject.hasReaction(searchingReaction)) {
            final MessageObject msg = messageObject;
            final MessageObject.GroupedMessages groupedMessages = getValidGroupedMessage(msg);
            if (groupedMessages != null) {
                for (int i = 0; i < groupedMessages.messages.size(); ++i) {
                    MessageObject gmsg = groupedMessages.messages.get(i);
                    getMediaDataController().removeMessageFromResults(gmsg.getId());
                }
            } else {
                getMediaDataController().removeMessageFromResults(msg.getId());
            }
            if (messagesSearchAdapter != null) {
                messagesSearchAdapter.notifyDataSetChanged();
            }
            updateFilteredMessages(true);
        }
    }

    private int findAdPlace() {
        final ArrayList<Integer> cachedApproximateHeight = new ArrayList<>(messages.size());
        final int unreadRow = messages.indexOf(unreadMessageObject);
        int unreadY = -1;
        int unreadOffset = 0;
        final ArrayList<Integer> adsY = new ArrayList<>();
        final ArrayList<Integer> adsIndices = new ArrayList<>();
        final ArrayList<Integer> adsIndexOffset = new ArrayList<>();
        final HashSet<Long> groupIds = new HashSet<>();
        int y = 0, o = 0;
        for (int i = 0; i < messages.size(); ++i) {
            final MessageObject msg = messages.get(i);
            final int h = msg.getApproximateHeightCached();
            cachedApproximateHeight.add(h);
            if (msg.isSponsored() || i == unreadRow) {
                if (i == unreadRow) {
                    unreadY = y;
                }
                adsIndices.add(i);
                adsY.add(y);
                adsIndexOffset.add(o);
            }
            if (!msg.hasValidGroupIdFast()) {
                o++;
                y += h;
            } else if (!groupIds.contains(msg.getGroupId())) {
                groupIds.add(msg.getGroupId());
                o++;
                y += h;
            }
        }
        if (adsIndices.size() - (unreadRow >= 0 ? 1 : 0) <= 0) {
            return 0;
        }
        y = unreadY;
        o = unreadOffset;
        groupIds.clear();
        for (int i = unreadRow; i >= 0; --i) {
            final MessageObject msg = messages.get(i);
            final int h = cachedApproximateHeight.get(i);
            if (!msg.hasValidGroupIdFast()) {
                o--;
                y -= h;
            } else if (!groupIds.contains(msg.getGroupId())) {
                groupIds.add(msg.getGroupId());
                o--;
                y -= h;
            }
            if (msg.isSponsored() || msg.hasValidGroupIdFast() || msg == unreadMessageObject) continue;

            int t = -1, ty = -1, to = -1;
            int b = -1, by = -1, bo = -1;
            for (int j = 0; j < adsIndices.size(); ++j) {
                int idx = adsIndices.get(j);
                if (idx >= i && (t == -1 || t > idx)) {
                    t = idx;
                    ty = adsY.get(j);
                    to = adsIndexOffset.get(j);
                }
                if (idx <= i && (b == -1 || b < idx)) {
                    b = idx;
                    by = adsY.get(j);
                    bo = adsIndexOffset.get(j);
                }
            }
            if (
                (t == -1 || Math.abs(t - i) > sponsoredMessagesPostsBetween + 1 && Math.abs(to - o) > sponsoredMessagesPostsBetween + 1 && Math.abs(ty - (y + h)) > AndroidUtilities.displaySize.y) &&
                (b == -1 || Math.abs(b - i) > sponsoredMessagesPostsBetween + 1 && Math.abs(bo - o) > sponsoredMessagesPostsBetween + 1 && Math.abs(by - y) > AndroidUtilities.displaySize.y)
            ) {
                return i;
            }
        }
        y = 0;
        o = 0;
        groupIds.clear();
        for (int i = 0; i < messages.size(); ++i) {
            MessageObject msg = messages.get(i);
            final int h = cachedApproximateHeight.get(i);
            if (!msg.hasValidGroupIdFast()) {
                o++;
                y += h;
            } else if (!groupIds.contains(msg.getGroupId())) {
                groupIds.add(msg.getGroupId());
                o++;
            }
            if (msg.isSponsored() || msg.hasValidGroupIdFast() || msg == unreadMessageObject) continue;

            int t = -1, ty = -1, to = -1;
            int b = -1, by = -1, bo = -1;
            for (int j = 0; j < adsIndices.size(); ++j) {
                int idx = adsIndices.get(j);
                if (idx >= i && (t == -1 || t > idx)) {
                    t = idx;
                    ty = adsY.get(j);
                    to = adsIndexOffset.get(j);
                }
                if (idx <= i && (b == -1 || b < idx)) {
                    b = idx;
                    by = adsY.get(j);
                    bo = adsIndexOffset.get(j);
                }
            }
            if (
                (t == -1 || Math.abs(t - i) >= sponsoredMessagesPostsBetween + 1 && Math.abs(to - o) >= sponsoredMessagesPostsBetween + 1 && Math.abs(ty - (y + h)) > AndroidUtilities.displaySize.y) &&
                (b == -1 || Math.abs(b - i) >= sponsoredMessagesPostsBetween + 1 && Math.abs(bo - o) >= sponsoredMessagesPostsBetween + 1 && Math.abs(by - y) > AndroidUtilities.displaySize.y)
            ) {
                 return i;
            }
        }
        return -1;
    }

    public void fillMessageMenu(
        MessageObject primaryMessage,

        ArrayList<Integer> icons,
        ArrayList<CharSequence> items,
        ArrayList<Integer> options
    ) {
        final MessageObject message = selectedObject;
        final MessageObject.GroupedMessages groupedMessages = selectedObjectGroup;
        final int type = getMessageType(message);
        final boolean isEphemeral = message.isEphemeral();
        final boolean isEphemeralFromBot = isEphemeral && !message.isOut();


        boolean allowChatActions = true;
        boolean allowPin;
        if (chatMode == MODE_SAVED || isQuickRepliesOrWelcomeMessagesMode() || isEphemeral) {
            allowPin = false;
        } else if (chatMode == MODE_SCHEDULED || (isThreadChat() && !isTopic)) {
            allowPin = false;
        } else if (currentChat != null) {
            allowPin = message.getDialogId() != mergeDialogId && ChatObject.canPinMessages(currentChat) && (!currentChat.monoforum /*|| ChatObject.canManageMonoForum(currentAccount, currentChat)*/);
        } else if (currentEncryptedChat == null) {
            if (UserObject.isDeleted(currentUser)) {
                allowPin = false;
            } else if (userInfo != null) {
                allowPin = userInfo.can_pin_message;
            } else {
                allowPin = false;
            }
        } else {
            allowPin = false;
        }
        if (UserObject.isReplyUser(dialog_id) || dialog_id == UserObject.VERIFY) {
            allowPin = false;
        }
        allowPin = allowPin && message.getId() > 0 && (message.messageOwner.action == null || message.messageOwner.action instanceof TLRPC.TL_messageActionEmpty) && !message.isExpiredStory() && message.type != MessageObject.TYPE_STORY_MENTION;
        boolean noforwards = isEphemeral || isPeerNoForwards() || message.messageOwner.noforwards || getDialogId() == UserObject.VERIFY;
        boolean noforwardsOverride = false;
        boolean noforwardsOrPaidMedia = noforwardsOverride || message.type == MessageObject.TYPE_PAID_MEDIA;
        boolean allowUnpin = !isEphemeral && message.getDialogId() != mergeDialogId && allowPin && (pinnedMessageObjects.containsKey(message.getId()) || groupedMessages != null && !groupedMessages.messages.isEmpty() && pinnedMessageObjects.containsKey(groupedMessages.messages.get(0).getId())) && !message.isExpiredStory();
        boolean allowEdit = !isEphemeral && message.canEditMessage(currentChat) && !chatActivityEnterView.hasAudioToSend() && message.getDialogId() != mergeDialogId && message.type != MessageObject.TYPE_STORY && message.type != MessageObject.TYPE_POLL;

        boolean isAyuDeleted = message.isAyuDeleted();

        if (isAyuDeleted) {
            // 已删除消息仍可回复：服务端已无此消息，发送时会转成引用文本形式的伪回复
            allowPin = false;
            allowUnpin = false;
            allowEdit = false;
            noforwards = true;
        }

        boolean allowCopy = false;
        boolean allowCopyPhoto = false;
        boolean allowCopyLink = false;
        boolean allowCopyLinkPm = false;
        boolean allowDelete = false;
        boolean allowReply = false;
        boolean allowReplyPm = false;
        boolean allowForward = false;

        boolean isMessageTextEmpty = selectedObject.messageOwner == null || TextUtils.isEmpty(selectedObject.messageOwner.message);
        boolean isStaticSticker = type == MESSAGE_TYPE_STICKER_PACK_NOT_INSTALLED && !selectedObject.isAnimatedSticker() && !selectedObject.isVideoSticker();
        boolean hasCaption = !TextUtils.isEmpty(getMessageCaption(selectedObject, selectedObjectGroup));
        boolean canDeleteMessage = selectedObject.canDeleteMessage(chatMode == MODE_SCHEDULED, currentChat);
        TLRPC.Peer selectedFromPeer = selectedObject.messageOwner != null ? selectedObject.messageOwner.from_id : null;
        TLRPC.Peer selectedDialogPeer = selectedObject.messageOwner != null ? selectedObject.messageOwner.peer_id : null;
        long selectedFromUserId = selectedFromPeer != null ? selectedFromPeer.user_id : 0;
        long selectedDialogUserId = selectedDialogPeer != null ? selectedDialogPeer.user_id : 0;
        if (allowEdit && groupedMessages != null) {
            int captionsCount = 0;
            for (int a = 0, N = groupedMessages.messages.size(); a < N; a++) {
                MessageObject messageObject = groupedMessages.messages.get(a);
                if (a == 0 || !TextUtils.isEmpty(messageObject.caption)) {
                    selectedObjectToEditCaption = messageObject;
                    if (!TextUtils.isEmpty(messageObject.caption)) {
                        captionsCount++;
                    }
                }
            }
            allowEdit = captionsCount < 2;
        }
        if (message.isExpiredStory() || chatMode == MODE_SCHEDULED || threadMessageObjects != null && threadMessageObjects.contains(message) ||
            message.isSponsored() || type == MESSAGE_TYPE_SERVICE && message.getDialogId() == mergeDialogId ||
            message.messageOwner.action instanceof TLRPC.TL_messageActionSecureValuesSent ||
            isEphemeral ||
            currentEncryptedChat == null && message.getId() < 0 ||
            bottomChannelButtonsLayout != null && bottomChannelButtonsLayout.getVisibility() == View.VISIBLE && !(bottomOverlayChatWaitsReply && selectedObject != null && (MessageObject.getTopicId(currentAccount, selectedObject.messageOwner, ChatObject.isForum(currentChat)) != 0 || selectedObject.wasJustSent))
        ) {
            allowChatActions = false;
        }

        if (currentChat != null && (ChatObject.isNotInChat(currentChat) && !ChatObject.isMonoForum(currentChat) && !isThreadChat())) {
            allowChatActions = false;
        }

        if (currentChat != null && (ChatObject.isChannel(currentChat) && !ChatObject.canPost(currentChat) && !currentChat.megagroup)) {
            allowChatActions = false;
        }

        if (currentChat != null && (!ChatObject.canSendMessages(currentChat))) {
            allowChatActions = false;
        }

        if (message.isSponsored() && !getUserConfig().isPremium() && !getMessagesController().premiumFeaturesBlocked() && !message.sponsoredCanReport) {
            items.add(LocaleController.getString(R.string.HideAd));
            options.add(OPTION_HIDE_SPONSORED_MESSAGE);
            icons.add(R.drawable.msg_block2);
        }

        if (message.isSponsored() && message.sponsoredCanReport) {
            items.add(LocaleController.getString(R.string.AboutRevenueSharingAds));
            options.add(OPTION_ABOUT_REVENUE_SHARING_ADS);
            icons.add(R.drawable.msg_report);

            items.add(LocaleController.getString(R.string.ReportAd));
            options.add(OPTION_REPORT_AD);
            icons.add(R.drawable.msg_block2);

            if (!getMessagesController().premiumFeaturesBlocked()) {
                items.add(getString(R.string.RemoveAds));
                options.add(OPTION_REMOVE_ADS);
                icons.add(R.drawable.msg_cancel);
            }
        }

        final @DrawableRes int deleteIconRes;
        if (selectedObject.isPaidSuggestedPostProtected()) {
            deleteIconRes = R.drawable.menu_delete_paid;
        } else if (selectedObject.messageOwner.ttl_period != 0) {
            deleteIconRes = R.drawable.msg_delete_auto;
        } else {
            deleteIconRes = R.drawable.msg_delete;
        }

        if (type == -1) {
            if ((selectedObject.type == MessageObject.TYPE_TEXT || selectedObject.type == MessageObject.TYPE_ARTICLE || selectedObject.isAnimatedEmoji() || selectedObject.isAnimatedEmojiStickers() || getMessageCaption(selectedObject, selectedObjectGroup) != null) && (!noforwardsOrPaidMedia || isEphemeral) && !message.isExpiredStory()) {
                allowCopy = true;
                if (!GroupedIconsView.useGroupedIcons()) {
                    items.add(LocaleController.getString(R.string.Copy));
                    options.add(OPTION_COPY);
                    icons.add(R.drawable.msg_copy);
                }
            }
            items.add(LocaleController.getString(R.string.CancelSending));
            options.add(OPTION_CANCEL_SENDING);
            icons.add(R.drawable.msg_delete);
        } else if (type == MESSAGE_TYPE_SEND_ERROR_MEDIA) {
            items.add(LocaleController.getString(R.string.Retry));
            options.add(OPTION_RETRY);
            icons.add(R.drawable.msg_retry);

            allowDelete = true;
            if (!GroupedIconsView.useGroupedIcons()) {
                items.add(LocaleController.getString(chatMode == MODE_SAVED && threadMessageId != getUserConfig().getClientUserId() ? R.string.Remove : R.string.Delete));
                options.add(OPTION_DELETE);
                icons.add(deleteIconRes);
            }
        } else if (type == MESSAGE_TYPE_SERVICE) {
            if (currentChat != null) {
                if ((allowChatActions || isEphemeralFromBot) && (primaryMessage == null || !primaryMessage.isWelcomeMessage()) && !isInsideContainer && chatMode != MODE_WELCOME_MESSAGES) {
                    allowReply = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(R.string.Reply));
                        options.add(OPTION_REPLY);
                        icons.add(R.drawable.menu_reply);
                    }
                }
                if (!isThreadChat() && chatMode != MODE_SCHEDULED && primaryMessage != null && primaryMessage.hasReplies() && currentChat.megagroup && primaryMessage.canViewThread()) {
                    items.add(LocaleController.formatPluralString("ViewReplies", primaryMessage.getRepliesCount()));
                    options.add(OPTION_VIEW_REPLIES_OR_THREAD);
                    icons.add(R.drawable.msg_viewreplies);
                }
                if (selectedObject != null && selectedObject.messageOwner != null && selectedObject.messageOwner.action == null && currentChat != null && currentChat.forum && !isTopic && selectedObject.messageOwner != null && selectedObject.messageOwner.reply_to != null && selectedObject.messageOwner.reply_to.forum_topic) {
                    items.add(LocaleController.getString(R.string.ViewInTopic));
                    options.add(OPTION_VIEW_IN_TOPIC);
                    icons.add(R.drawable.msg_viewintopic);
                }
                if (allowUnpin) {
                    items.add(LocaleController.getString(R.string.UnpinMessage));
                    options.add(OPTION_UNPIN);
                    icons.add(R.drawable.msg_unpin);
                } else if (allowPin) {
                    items.add(LocaleController.getString(R.string.PinMessage));
                    options.add(OPTION_PIN);
                    icons.add(R.drawable.msg_pin);
                }
                /*if (selectedObject != null && selectedObject.contentType == 0 && ((!TextUtils.isEmpty(selectedObject.getMessageTextToTranslate(groupedMessages, null)) && !selectedObject.isAnimatedEmoji() && !selectedObject.isDice()) || (selectedObject.type == MessageObject.TYPE_ARTICLE && selectedObject.messageOwner != null && selectedObject.messageOwner.rich_message != null && !selectedObject.translated))) {
                    items.add(LocaleController.getString(R.string.TranslateMessage));
                    options.add(OPTION_TRANSLATE);
                    icons.add(R.drawable.msg_translate);
                }*/
                if (message.canEditMessage(currentChat) && message.type != MessageObject.TYPE_POLL || chatMode == MODE_WELCOME_MESSAGES) {
                    allowEdit = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(R.string.Edit));
                        options.add(OPTION_EDIT);
                        icons.add(R.drawable.msg_edit);
                    }
                }
                if (ChatObject.isMonoForum(currentChat) && selectedObject.getGroupId() == 0 && selectedObjectGroup == null && message != null && message.messageOwner != null && message.messageOwner.suggested_post == null && message.messageOwner.action == null) {
                    items.add(LocaleController.getString(R.string.EditOfferAdd));
                    options.add(OPTION_SUGGESTION_ADD_OFFER);
                    icons.add(R.drawable.menu_edit_price);
                }
//                        if (message.scheduled && message.type == MessageObject.TYPE_PAID_MEDIA && message.canEditMessage(currentChat)) {
//                            items.add(LocaleController.getString(R.string.PaidMediaPriceButton));
//                            options.add(OPTION_EDIT_PRICE);
//                            icons.add(R.drawable.menu_feature_paid);
//                        }
                if (NekoConfig.showReport.Bool() && !isAyuDeleted && chatMode != MODE_WELCOME_MESSAGES && selectedObject.contentType == 0 && !selectedObject.isMediaEmptyWebpage() && selectedObject.getId() > 0 && !selectedObject.isOut() && (currentChat != null || currentUser != null && currentUser.bot)) {
                    items.add(LocaleController.getString(R.string.ReportChat));
                    options.add(OPTION_REPORT_CHAT);
                    icons.add(R.drawable.msg_report);
                }
                // currentChat != null END
            } else {
                if (selectedObject.getId() > 0 && (allowChatActions || isEphemeralFromBot) && (primaryMessage == null || !primaryMessage.isWelcomeMessage()) && !isInsideContainer && chatMode != MODE_WELCOME_MESSAGES) {
                    allowReply = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(R.string.Reply));
                        options.add(OPTION_REPLY);
                        icons.add(R.drawable.menu_reply);
                    }
                }
            }

            if (selectedObject != null && selectedObject.messageOwner != null) {
                if (selectedObject.messageOwner.action instanceof TLRPC.TL_messageActionStarGiftUnique && ((TLRPC.TL_messageActionStarGiftUnique) (selectedObject.messageOwner.action)).message != null) {
                    items.add(LocaleController.getString(R.string.Copy));
                    options.add(OPTION_COPY);
                    icons.add(R.drawable.msg_copy);
                }
                if (currentUser != null && !UserObject.isService(currentUser.id) && (selectedObject.messageOwner.action instanceof TLRPC.TL_messageActionStarGift || selectedObject.messageOwner.action instanceof TLRPC.TL_messageActionStarGiftUnique || selectedObject.messageOwner.action instanceof TLRPC.TL_messageActionGiftPremium)) {
                    items.add(selectedObject.isOutOwner() ? getString(R.string.SendAnotherGift) : formatString(R.string.SendGiftTo, UserObject.getForcedFirstName(currentUser)));
                    options.add(OPTION_GIFT);
                    icons.add(R.drawable.menu_gift);
                }
            }
            if (message.canDeleteMessage(chatMode == MODE_SCHEDULED, currentChat) && (threadMessageObjects == null || !threadMessageObjects.contains(message)) && !(message != null && message.messageOwner != null && message.messageOwner.action instanceof TLRPC.TL_messageActionTopicCreate)) {
                allowDelete = true;
                if (!GroupedIconsView.useGroupedIcons()) {
                    items.add(LocaleController.getString(chatMode == MODE_SAVED && threadMessageId != getUserConfig().getClientUserId() ? R.string.Remove : R.string.Delete));
                    options.add(OPTION_DELETE);
                    icons.add(deleteIconRes);
                }
            }
            boolean allowViewHistory = currentChat != null && chatMode == 0 && !currentChat.broadcast && !(threadMessageObjects != null && threadMessageObjects.contains(message));
            if (allowViewHistory && NekoConfig.showViewHistory.Bool()) {
                items.add(LocaleController.getString(R.string.ViewHistory));
                options.add(nkbtn_view_history);
                icons.add(R.drawable.msg_recent);
            }
            // type == MESSAGE_TYPE_SERVICE END
        } else if (type == MESSAGE_TYPE_SEND_ERROR_TEXT) {
            items.add(LocaleController.getString(R.string.Retry));
            options.add(OPTION_RETRY);
            icons.add(R.drawable.msg_retry);
            if (!noforwardsOrPaidMedia || isEphemeral) {
                allowCopy = true;
                if (!GroupedIconsView.useGroupedIcons()) {
                    items.add(LocaleController.getString(R.string.Copy));
                    options.add(OPTION_COPY);
                    icons.add(R.drawable.msg_copy);
                }
            }
            allowDelete = true;
            if (!GroupedIconsView.useGroupedIcons()) {
                items.add(LocaleController.getString(chatMode == MODE_SAVED && threadMessageId != getUserConfig().getClientUserId() ? R.string.Remove : R.string.Delete));
                options.add(OPTION_DELETE);
                icons.add(deleteIconRes);
            }
        } else {
            if (currentEncryptedChat == null) {
                if (!selectedObject.isPaidSuggestedPostProtected() && chatMode == MODE_SCHEDULED) {
                    items.add(LocaleController.getString(R.string.MessageScheduleSend));
                    options.add(OPTION_SEND_NOW);
                    icons.add(R.drawable.msg_send);
                }
                if (selectedObject.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                    TLRPC.TL_messageActionPhoneCall call = (TLRPC.TL_messageActionPhoneCall) message.messageOwner.action;
                    items.add((call.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed || call.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) && !message.isOutOwner() ? LocaleController.getString(R.string.CallBack) : LocaleController.getString(R.string.CallAgain));
                    options.add(OPTION_CALL_AGAIN);
                    icons.add(R.drawable.msg_callback);
                    if (VoIPHelper.canRateCall(call)) {
                        items.add(LocaleController.getString(R.string.CallMessageReportProblem));
                        options.add(OPTION_RATE_CALL);
                        icons.add(R.drawable.msg_fave);
                    }
                }
                if (((allowChatActions || isEphemeralFromBot) || !noforwardsOrPaidMedia && ChatObject.isChannelAndNotMegaGroup(currentChat) && !selectedObject.isSponsored() && selectedObject.contentType == 0 && chatMode == MODE_DEFAULT) && !isInsideContainer && (primaryMessage == null || !primaryMessage.isWelcomeMessage()) && chatMode != MODE_WELCOME_MESSAGES) {
                    allowReply = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(R.string.Reply));
                        options.add(OPTION_REPLY);
                        icons.add(R.drawable.menu_reply);
                    }
                }
                if (!noforwardsOrPaidMedia && !selectedObject.isSponsored() && selectedObject.contentType == 0 && chatMode == MODE_DEFAULT && !isInsideContainer && currentChat != null && currentUser == null && selectedDialogUserId == 0 && selectedFromUserId > 0 && selectedFromUserId != getUserConfig().getClientUserId() && !isAyuDeleted) {
                    allowReplyPm = true;
                    if (NaConfig.INSTANCE.getShowReplyInPrivate().Bool()) {
                        items.add(LocaleController.getString(R.string.ReplyInPrivate));
                        options.add(nkbtn_reply_private);
                        icons.add(R.drawable.menu_reply);
                    }                }
                if ((selectedObject.type == MessageObject.TYPE_TEXT || selectedObject.type == MessageObject.TYPE_ARTICLE || selectedObject.isDice() || selectedObject.isAnimatedEmoji() || selectedObject.isAnimatedEmojiStickers() || getMessageCaption(selectedObject, selectedObjectGroup) != null) && (!noforwardsOrPaidMedia || isEphemeral) && !selectedObject.sponsoredCanReport) {
                    allowCopy = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(R.string.Copy));
                        options.add(OPTION_COPY);
                        icons.add(R.drawable.msg_copy);
                    }
                }
                if (!isThreadChat() && chatMode != MODE_SCHEDULED && currentChat != null && primaryMessage != null && (currentChat.has_link || primaryMessage.hasReplies()) && currentChat.megagroup && primaryMessage.canViewThread()) {
                    if (primaryMessage.hasReplies()) {
                        items.add(LocaleController.formatPluralString("ViewReplies", primaryMessage.getRepliesCount()));
                    } else {
                        items.add(LocaleController.getString(R.string.ViewThread));
                    }
                    options.add(OPTION_VIEW_REPLIES_OR_THREAD);
                    icons.add(R.drawable.msg_viewreplies);
                } else if (isThreadChat() && chatMode != MODE_SCHEDULED && currentChat != null) {
                    options.add(nkbtn_view_in_chat);
                    icons.add(R.drawable.msg_viewreplies);
                    items.add(LocaleController.getString(R.string.ViewInChat));
                }
                if (!isEphemeral && !selectedObject.isSponsored() && chatMode != MODE_SCHEDULED && ChatObject.isChannel(currentChat) && !ChatObject.isMonoForum(currentChat) && selectedObject.getDialogId() != mergeDialogId && !selectedObject.isAyuDeleted()) {
                    allowCopyLink = true;
                    if (
                        (!GroupedIconsView.useGroupedIcons() && (NaConfig.INSTANCE.getShowCopyLink().Bool() || selectedObject.isAnyKindOfSticker() || selectedObject.isPoll()))
                        ||
                        (GroupedIconsView.useGroupedIcons() && NaConfig.INSTANCE.getShowCopyLink().Bool() && ((!isMessageTextEmpty || hasCaption) && selectedObject.isPhoto() && !selectedObject.isWebpage() || (canDeleteMessage && (!isMessageTextEmpty || selectedObject.isPhoto() || isStaticSticker))))
                    ) {
                        items.add(LocaleController.getString(R.string.CopyLink));
                        options.add(OPTION_COPY_LINK);
                        icons.add(R.drawable.msg_link);
                    }
                }
                if (!selectedObject.isSponsored() && chatMode != MODE_SCHEDULED && currentUser != null && selectedObject.getDialogId() != mergeDialogId) {
                    allowCopyLinkPm = true;
                    if (
                        (!GroupedIconsView.useGroupedIcons() && NaConfig.INSTANCE.getShowCopyLink().Bool())
                        ||
                        (GroupedIconsView.useGroupedIcons() && NaConfig.INSTANCE.getShowCopyLink().Bool() && (!isMessageTextEmpty && !selectedObject.isPhoto() || selectedObject.isPhoto() && !selectedObject.needDrawBluredPreview() || isStaticSticker))
                    ) {
                        items.add(getString(R.string.CopyLink));
                        options.add(nkbtn_copy_link_in_pm);
                        icons.add(R.drawable.msg_link);
                    }
                }
                if (selectedObject != null && selectedObject.messageOwner != null && selectedObject.messageOwner.action == null && currentChat != null && currentChat.forum && !isTopic && selectedObject.messageOwner != null && selectedObject.messageOwner.reply_to != null && selectedObject.messageOwner.reply_to.forum_topic) {
                    items.add(LocaleController.getString(R.string.ViewInTopic));
                    options.add(OPTION_VIEW_IN_TOPIC);
                    icons.add(R.drawable.msg_viewintopic);
                }
                if (type == MESSAGE_TYPE_MEDIA) {
                    if (chatMode != MODE_SCHEDULED) {

                        if (selectedObject.type == MessageObject.TYPE_POLL && !noforwardsOrPaidMedia) {
                            TLRPC.MessageMedia media = MessageObject.getMedia(selectedObject);
                            if (media instanceof TLRPC.TL_messageMediaPoll) {
                                TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) media;
                                TLRPC.MessageMedia pollMediaInDescription = PollAttachedMediaPack.getMedia(mediaPoll, PollAttachedMediaPack.INDEX_DESCRIPTION);
                                TLRPC.MessageMedia pollMediaInExplanation = PollAttachedMediaPack.getMedia(mediaPoll, PollAttachedMediaPack.INDEX_EXPLANATION);

                                final boolean canDownloadFiles = pollMediaInDescription != null && pollMediaInDescription.document != null && !MessageObject.isVideoDocument(pollMediaInDescription.document)
                                    || selectedObject.expandedExplanation && pollMediaInExplanation != null && pollMediaInExplanation.document != null && !MessageObject.isVideoDocument(pollMediaInExplanation.document);

                                if (canDownloadFiles) {
                                    items.add(LocaleController.getString(pollMediaInDescription != null && MessageObject.isMusicDocument(pollMediaInDescription.document) ? R.string.SaveToMusic : R.string.SaveToDownloads));
                                    options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                                    icons.add(R.drawable.msg_download);
                                }
                            }
                        }

                        if (selectedObject.type == MessageObject.TYPE_POLL && !message.isPollClosed()) {
                            TLRPC.MessageMedia media = MessageObject.getMedia(selectedObject);
                            if (media instanceof TLRPC.TL_messageMediaPoll) {
                                TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) media;
                                if (mediaPoll.results != null && mediaPoll.results.can_view_stats && !selectedObject.isForwarded()) {
                                    items.add(LocaleController.getString(R.string.PollV2MenuViewStats));
                                    options.add(OPTION_VIEW_STATISTICS);
                                    icons.add(R.drawable.msg_stats);
                                }

                                if (message.canUnvote()) {
                                    items.add(LocaleController.getString(R.string.Unvote));
                                    options.add(OPTION_UNVOTE);
                                    icons.add(R.drawable.msg_unvote);
                                }
                                if (!message.isForwarded() && (
                                    message.isOut() && (!ChatObject.isChannel(currentChat) || currentChat.megagroup) ||
                                        ChatObject.isChannel(currentChat) && !currentChat.megagroup && (currentChat.creator || currentChat.admin_rights != null && currentChat.admin_rights.edit_messages))) {
                                    if (message.isQuiz()) {
                                        items.add(LocaleController.getString(R.string.StopQuiz));
                                    } else {
                                        items.add(LocaleController.getString(R.string.StopPoll));
                                    }
                                    options.add(OPTION_STOP_POLL_OR_QUIZ);
                                    icons.add(R.drawable.msg_pollstop);
                                }
                            } else if (media instanceof TLRPC.TL_messageMediaToDo) {
                                if (message.canEditMessage(currentChat)) {
                                    items.add(getString(R.string.EditToDo));
                                    options.add(OPTION_EDIT_TODO);
                                    icons.add(R.drawable.msg_edit);
                                }
                                if (message.canAppendToTodo()) {
                                    items.add(getString(R.string.AddTasks));
                                    options.add(OPTION_ADD_TO_TODO);
                                    icons.add(R.drawable.msg_addbot);
                                }
                            }
                        } else if (selectedObject.isMusic() && !noforwardsOrPaidMedia && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                            items.add(LocaleController.getString(R.string.SaveToMusic));
                            options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                            icons.add(R.drawable.msg_download);
                        } else if (selectedObject.isDocument() && !noforwardsOrPaidMedia && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                            items.add(LocaleController.getString(R.string.SaveToDownloads));
                            options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                            icons.add(R.drawable.msg_download);
                        }
                        if (!TextUtils.isEmpty(selectedObject.getVoiceTranscription())) {
                            items.add(getString(R.string.Translate));
                            options.add(nkbtn_translateVoice);
                            icons.add(R.drawable.msg_translate);
                            if (TranscribeHelper.useTranscribeAI(selectedObject.currentAccount)) {
                                items.add(getString(R.string.Retry));
                                options.add(nkbtn_transcriptionRetry);
                                icons.add(R.drawable.msg_retry);
                            }
                        }
                    }
                } else if (type == MESSAGE_TYPE_MEDIA_WEB && !noforwardsOrPaidMedia) {
                    if (selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && MessageObject.isNewGifDocument(selectedObject.messageOwner.media.webpage.document)) {
                        items.add(LocaleController.getString(R.string.SaveToGIFs));
                        options.add(OPTION_ADD_TO_GIFS);
                        icons.add(R.drawable.msg_gif);
                    }
                } else if (type == MESSAGE_TYPE_MEDIA_CACHED) {
                    if (!noforwardsOrPaidMedia && !selectedObject.hasRevealedExtendedMedia()) {
                        if (selectedObject.isVideo()) {
                            if (!selectedObject.needDrawBluredPreview()) {
                                items.add(LocaleController.getString(R.string.SaveToGallery));
                                options.add(OPTION_SAVE_TO_GALLERY);
                                icons.add(R.drawable.msg_gallery);
                                items.add(LocaleController.getString(R.string.ShareFile));
                                options.add(OPTION_SHARE);
                                icons.add(R.drawable.msg_shareout);
                            }
                        } else if (selectedObject.isMusic() && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                            items.add(LocaleController.getString(R.string.SaveToMusic));
                            options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                            icons.add(R.drawable.msg_download);
                            items.add(LocaleController.getString(R.string.ShareFile));
                            options.add(OPTION_SHARE);
                            icons.add(R.drawable.msg_shareout);
                        } else if (selectedObject.getDocument() != null && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                            if (NaConfig.INSTANCE.getShowCopyFrame().Bool()) {
                                BaseCell cell = findMessageCell(selectedObject.getId(), true);
                                if (cell instanceof ChatMessageCell chatMessageCell) {
                                    AnimatedFileDrawable animation = chatMessageCell.getPhotoImage().getAnimation();
                                    if (animation != null && animation.hasBitmap()) {
                                        items.add(getString(R.string.CopyVideoFrame));
                                        options.add(OPTION_COPY_FRAME);
                                        icons.add(R.drawable.msg_copy_photo);
                                    }
                                }
                            }
                            if (MessageObject.isNewGifDocument(selectedObject.getDocument())) {
                                items.add(LocaleController.getString(R.string.SaveToGIFs));
                                options.add(OPTION_ADD_TO_GIFS);
                                icons.add(R.drawable.msg_gif);
                            }
                            items.add(LocaleController.getString(R.string.SaveToDownloads));
                            options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                            icons.add(R.drawable.msg_download);
                            items.add(LocaleController.getString(R.string.ShareFile));
                            options.add(OPTION_SHARE);
                            icons.add(R.drawable.msg_shareout);
                        } else {
                            if (!selectedObject.needDrawBluredPreview()) {
                                items.add(LocaleController.getString(R.string.SaveToGallery));
                                options.add(OPTION_SAVE_TO_GALLERY);
                                icons.add(R.drawable.msg_gallery);
                                allowCopyPhoto = true;
                                if (
                                    (!GroupedIconsView.useGroupedIcons() && NaConfig.INSTANCE.getShowCopyPhoto().Bool())
                                    ||
                                    (GroupedIconsView.useGroupedIcons() && (!isMessageTextEmpty || hasCaption) && canDeleteMessage && NaConfig.INSTANCE.getShowCopyPhoto().Bool())
                                ) {
                                    items.add(LocaleController.getString(R.string.CopyPhoto));
                                    options.add(OPTION_COPY_PHOTO);
                                    icons.add(R.drawable.msg_copy_photo);
                                }
                                if (
                                    (!GroupedIconsView.useGroupedIcons() && NaConfig.INSTANCE.getShowCopyAsSticker().Bool())
                                    ||
                                    (GroupedIconsView.useGroupedIcons() && (!isMessageTextEmpty || hasCaption) && canDeleteMessage && NaConfig.INSTANCE.getShowCopyAsSticker().Bool())
                                ) {
                                    items.add(LocaleController.getString(R.string.CopyPhotoAsSticker));
                                    options.add(OPTION_COPY_PHOTO_AS_STICKER);
                                    icons.add(R.drawable.msg_copy_photo);
                                }
                            }
                        }
                    }
                } else if (type == MESSAGE_TYPE_XML) {
                    items.add(LocaleController.getString(R.string.ApplyLocalizationFile));
                    options.add(OPTION_APPLY_LOCALIZATION_OR_THEME);
                    icons.add(R.drawable.msg_language);
                    if (!noforwardsOrPaidMedia && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                        items.add(LocaleController.getString(R.string.SaveToDownloads));
                        options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                        icons.add(R.drawable.msg_download);
                        items.add(LocaleController.getString(R.string.ShareFile));
                        options.add(OPTION_SHARE);
                        icons.add(R.drawable.msg_shareout);
                    }
                } else if (type == MESSAGE_TYPE_THEME) {
                    items.add(LocaleController.getString(R.string.ApplyThemeFile));
                    options.add(OPTION_APPLY_LOCALIZATION_OR_THEME);
                    icons.add(R.drawable.msg_theme);
                    if (!noforwardsOrPaidMedia && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                        items.add(LocaleController.getString(R.string.SaveToDownloads));
                        options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                        icons.add(R.drawable.msg_download);
                        items.add(LocaleController.getString(R.string.ShareFile));
                        options.add(OPTION_SHARE);
                        icons.add(R.drawable.msg_shareout);
                    }
                } else if (type == MESSAGE_TYPE_NEKOX_STICKERS_JSON || type == MESSAGE_TYPE_NEKOX_SETTINGS_JSON || type == MESSAGE_TYPE_FONT) {
                    options.add(OPTION_APPLY_LOCALIZATION_OR_THEME);
                    if (type == MESSAGE_TYPE_NEKOX_STICKERS_JSON) {
                        items.add(LocaleController.getString(R.string.ImportStickersList));
                        icons.add(R.drawable.msg_sticker);
                    } else if (type == MESSAGE_TYPE_NEKOX_SETTINGS_JSON) {
                        items.add(LocaleController.getString(R.string.ImportSettings));
                        icons.add(R.drawable.menu_secret);
                    } else {
                        items.add(LocaleController.getString(R.string.ApplyEmojiSet));
                        icons.add(R.drawable.smiles_tab_smiles);
                    }
                    if (!noforwardsOrPaidMedia) {
                        items.add(LocaleController.getString(R.string.SaveToDownloads));
                        options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                        icons.add(R.drawable.msg_download);
                        items.add(LocaleController.getString(R.string.ShareFile));
                        options.add(OPTION_SHARE);
                        icons.add(R.drawable.msg_shareout);
                    }
                } else if (type == MESSAGE_TYPE_IMAGE_OR_VIDEO && !noforwardsOrPaidMedia && !selectedObject.hasRevealedExtendedMedia()) {
                    if (!selectedObject.needDrawBluredPreview() && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                        if (NaConfig.INSTANCE.getShowCopyFrame().Bool()) {
                            BaseCell cell = findMessageCell(selectedObject.getId(), true);
                            if (cell instanceof ChatMessageCell chatMessageCell) {
                                AnimatedFileDrawable animation = chatMessageCell.getPhotoImage().getAnimation();
                                if (animation != null && animation.hasBitmap()) {
                                    items.add(getString(R.string.CopyVideoFrame));
                                    options.add(OPTION_COPY_FRAME);
                                    icons.add(R.drawable.msg_copy_photo);
                                }
                            }
                        }
                        items.add(LocaleController.getString(R.string.SaveToGallery));
                        options.add(OPTION_SAVE_TO_GALLERY2);
                        icons.add(R.drawable.msg_gallery);
                        items.add(LocaleController.getString(R.string.SaveToDownloads));
                        options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                        icons.add(R.drawable.msg_download);
                        items.add(LocaleController.getString(R.string.ShareFile));
                        options.add(OPTION_SHARE);
                        icons.add(R.drawable.msg_shareout);
                    }
                } else if (type == MESSAGE_TYPE_STICKER_PACK_NOT_INSTALLED) {
                    if (selectedObject.isMask()) {
                        items.add(LocaleController.getString(R.string.AddToMasks));
                        options.add(OPTION_ADD_TO_STICKERS_OR_MASKS);
                        icons.add(R.drawable.msg_sticker);
                    } else {
                        if (!selectedObject.isAnimatedSticker()) {
                            items.add(LocaleController.getString(R.string.SaveToGallery));
                            options.add(nkbtn_stickerdl);
                            icons.add(R.drawable.msg_gallery);
                            allowCopyPhoto = true;
                            if (!GroupedIconsView.useGroupedIcons()) {
                                items.add(getString(R.string.CopySticker));
                                icons.add(R.drawable.msg_copy_photo);
                                options.add(OPTION_COPY_PHOTO);
                            }
                        }
                        if (NaConfig.INSTANCE.getShowAddToStickers().Bool()) {
                            items.add(LocaleController.getString(R.string.AddToStickers));
                            options.add(OPTION_ADD_TO_STICKERS_OR_MASKS);
                            icons.add(R.drawable.msg_sticker);
                        }
                        TLRPC.Document document = selectedObject.getDocument();
                        if (!getMediaDataController().isStickerInFavorites(document)) {
                            if (NaConfig.INSTANCE.getShowAddToFavorites().Bool() && getMediaDataController().canAddStickerToFavorites()) {
                                items.add(LocaleController.getString(R.string.AddToFavorites));
                                options.add(OPTION_ADD_STICKER_TO_FAVORITES);
                                icons.add(R.drawable.msg_fave);
                            }
                        } else {
                            items.add(LocaleController.getString(R.string.DeleteFromFavorites));
                            options.add(OPTION_DELETE_STICKER_FROM_FAVORITES);
                            icons.add(R.drawable.msg_unfave);
                        }
                    }
                } else if (type == MESSAGE_TYPE_CONTACT) {
                    long uid = selectedObject.messageOwner.media.user_id;
                    TLRPC.User user = null;
                    if (uid != 0) {
                        user = MessagesController.getInstance(currentAccount).getUser(uid);
                    }
                    if (user != null && user.id != getUserConfig().getClientUserId() && getContactsController().contactsDict.get(user.id) == null) {
                        items.add(LocaleController.getString(R.string.AddContactTitle));
                        options.add(OPTION_ADD_CONTACT);
                        icons.add(R.drawable.msg_addcontact);
                    }
                    if (!TextUtils.isEmpty(selectedObject.messageOwner.media.phone_number)) {
                        if (!noforwardsOrPaidMedia) {
                            items.add(LocaleController.getString(R.string.Copy));
                            options.add(OPTION_COPY_PHONE_NUMBER);
                            icons.add(R.drawable.msg_copy);
                        }
                        items.add(LocaleController.getString(R.string.Call));
                        options.add(OPTION_CALL);
                        icons.add(R.drawable.msg_callback);
                    }
                } else if (type == MESSAGE_TYPE_STICKER_PACK_INSTALLED) {
                    if (!selectedObject.isAnimatedSticker()) {
                        items.add(LocaleController.getString(R.string.SaveToGallery));
                        options.add(nkbtn_stickerdl);
                        icons.add(R.drawable.msg_gallery);
                    }
                    TLRPC.Document document = selectedObject.getDocument();
                    if (!getMediaDataController().isStickerInFavorites(document)) {
                        if (NaConfig.INSTANCE.getShowAddToFavorites().Bool() && getMediaDataController().canAddStickerToFavorites()) {
                            items.add(LocaleController.getString(R.string.AddToFavorites));
                            options.add(OPTION_ADD_STICKER_TO_FAVORITES);
                            icons.add(R.drawable.msg_fave);
                        }
                    } else {
                        items.add(LocaleController.getString(R.string.DeleteFromFavorites));
                        options.add(OPTION_DELETE_STICKER_FROM_FAVORITES);
                        icons.add(R.drawable.msg_unfave);
                    }
                }

                final boolean canForward = !selectedObject.isSponsored()
                    && !isQuickRepliesOrWelcomeMessagesMode()
                    && chatMode != MODE_SCHEDULED
                    && (!selectedObject.needDrawBluredPreview() || selectedObject.hasExtendedMediaPreview())
                    && !selectedObject.isLiveLocation()
                    && selectedObject.type != MessageObject.TYPE_PHONE_CALL
                    && !noforwards && selectedObject.type != MessageObject.TYPE_SHARING_OFFER
                    && selectedObject.type != MessageObject.TYPE_GIFT_PREMIUM
                    && selectedObject.type != MessageObject.TYPE_GIFT_OFFER
                    && selectedObject.type != MessageObject.TYPE_COMMUNITY_CHANGED
                    && selectedObject.type != MessageObject.TYPE_GIFT_OFFER_REJECTED
                    && selectedObject.type != MessageObject.TYPE_GIFT_PREMIUM_CHANNEL
                    && selectedObject.type != MessageObject.TYPE_SUGGEST_PHOTO
                    && !selectedObject.isWallpaperAction()
                    && !message.isExpiredStory()
                    && message.type != MessageObject.TYPE_STORY_MENTION
                    && message.type != MessageObject.TYPE_GIFT_STARS;
                if (canForward) {
                    allowForward = true;
                    if (!GroupedIconsView.useGroupedIcons() || allowEdit) {
                        items.add(LocaleController.getString(R.string.Forward));
                        options.add(OPTION_FORWARD);
                        icons.add(NaConfig.INSTANCE.getShowNoQuoteForward().Bool() ? R.drawable.msg_forward : R.drawable.msg_forward_noquote);
                    }
                }
                // --- NagramX Start ---
                if (chatMode != MODE_SCHEDULED) {
                    if (!selectedObject.needDrawBluredPreview() && !selectedObject.isLiveLocation() && selectedObject.type != 16) {
                        if (NaConfig.INSTANCE.getShowNoQuoteForward().Bool()) {
                            items.add(LocaleController.getString(R.string.AdvancedForward));
                            options.add(nkbtn_forward_noquote);
                            icons.add(R.drawable.msg_forward_noquote);
                        }
                    }
                    if (NaConfig.INSTANCE.getShowSetReminder().Bool()) {
                        items.add(LocaleController.getString(R.string.SetReminder));
                        options.add(nkbtn_setReminder);
                        icons.add(R.drawable.msg_calendar2);
                    }
                    if (NekoConfig.showAddToSavedMessages.Bool() && !UserObject.isUserSelf(currentUser) && !noforwards && selectedObject.canForwardMessage()) {
                        items.add(getString(R.string.AddToSavedMessages));
                        options.add(nkbtn_savemessage);
                        icons.add(R.drawable.msg_saved);
                    }
                    if (NaConfig.INSTANCE.getShowAddToBookmark().Bool() && selectedObject != null && !selectedObject.isAnyKindOfSticker()) {
                        boolean bookmarked;
                        if (selectedObjectGroup != null && selectedObjectGroup.messages != null && !selectedObjectGroup.messages.isEmpty()) {
                            int size = selectedObjectGroup.messages.size();
                            int[] ids = new int[size];
                            for (int i = 0; i < size; i++) {
                                ids[i] = selectedObjectGroup.messages.get(i).getId();
                            }
                            bookmarked = BookmarksHelper.areAllBookmarked(currentAccount, selectedObject.getDialogId(), ids);
                        } else {
                            bookmarked = BookmarksHelper.isBookmarked(currentAccount, selectedObject.getDialogId(), selectedObject.getId());
                        }
                        items.add(getString(bookmarked ? R.string.RemoveBookmark : R.string.AddBookmark));
                        options.add(nkbtn_bookmark);
                        icons.add(bookmarked ? R.drawable.msg_unfave : R.drawable.msg_fave);
                    }
                    boolean allowRepeat = currentUser != null || (currentChat != null && ChatObject.canSendMessages(currentChat));
                    if (allowRepeat && !noforwards && selectedObject.canForwardMessage() && NekoConfig.showRepeat.Bool()) {
                        items.add(LocaleController.getString(R.string.Repeat));
                        options.add(nkbtn_repeat);
                        icons.add(R.drawable.msg_repeat);
                    }
                    if (allowRepeat && !isAyuDeleted && !selectedObject.needDrawBluredPreview() && (NaConfig.INSTANCE.getShowRepeatAsCopy().Bool() || (NekoConfig.showRepeat.Bool() && noforwards))){
                        items.add(LocaleController.getString(R.string.RepeatAsCopy));
                        options.add(nkbtn_repeatascopy);
                        icons.add(R.drawable.msg_repeat);
                    }
                    if (NekoConfig.showDeleteDownloadedFile.Bool() && getMessageHelper().messageObjectIsFile(type, selectedObject)) {
                        items.add(LocaleController.getString(R.string.DeleteDownloadedFile));
                        options.add(nkbtn_deldlcache);
                        icons.add(R.drawable.msg_clear);
                    }
                    boolean allowViewHistory = currentChat != null && chatMode == 0 && !currentChat.broadcast && !(threadMessageObjects != null && threadMessageObjects.contains(message));
                    if (allowViewHistory && NekoConfig.showViewHistory.Bool()) {
                        items.add(LocaleController.getString(R.string.ViewHistory));
                        options.add(nkbtn_view_history);
                        icons.add(R.drawable.msg_recent);
                    }
                    final MessageObject msg = getMessageForTranslate();
                    boolean showTranslate = NekoConfig.showTranslate.Bool();
                    boolean isTranslatableMessage = msg != null && !msg.isSponsored() && !msg.isAnimatedEmoji() && !msg.isDice();
                    if (showTranslate && isTranslatableMessage) {
                        boolean isRichMessage = msg != null && msg.isRich();
                        if (isRichMessage) {
                            showTranslate = true;
                        }
                        String fromLang = msg.messageOwner.originalLanguage;
                        // check if language is restricted but don't detect language here to avoid extra delay
                        if (fromLang != null && RestrictedLanguagesSelectActivity.getRestrictedLanguages().contains(fromLang)) {
                            showTranslate = false;
                        }
                        boolean isOutgoingOrNotTranslatingDialog = msg.isOutOwner() || !isTranslatingDialog(msg);
                        boolean summarizedOpen = msg.messageOwner.summarizedOpen;
                        boolean isTranslated = msg.isTranslated();
                        boolean isTranslatedSummary = msg.isTranslatedSummary();
                        boolean canUndoTranslate = (isTranslated && !summarizedOpen || isTranslatedSummary && summarizedOpen) && isOutgoingOrNotTranslatingDialog;
                        if (isOutgoingOrNotTranslatingDialog) {
                            items.add(canUndoTranslate ? getString(R.string.UndoTranslate) : getString(R.string.Translate));
                            options.add(nkbtn_translate);
                            icons.add(R.drawable.msg_translate);
                        }
                    }
                    if (NekoConfig.showShareMessages.Bool() && msg != null) {
                        items.add(LocaleController.getString(R.string.ShareMessages));
                        options.add(nkbtn_sharemessage);
                        icons.add(R.drawable.msg_shareout);
                    }
                    if (AiController.canUseAI() && selectedObject != null) {
                        items.add(LocaleController.getString(R.string.AIChatGenerateFromMessage));
                        options.add(nkbtn_ai_chat);
                        icons.add(R.drawable.ai_chat);
                    }
                }
                if (NekoConfig.showMessageHide.Bool()) {
                    items.add(LocaleController.getString(R.string.Hide));
                    options.add(nkbtn_hide);
                    icons.add(R.drawable.msg_disable);
                }
                boolean canViewStats = false;
                if (message.messageOwner.views > 0 || message.messageOwner.forwards > 0) {
                    if (message.messageOwner.fwd_from != null && message.messageOwner.fwd_from.channel_post != 0) {
                        TLRPC.Chat fwdChat = getMessagesController().getChat(message.messageOwner.fwd_from.from_id.channel_id);
                        canViewStats = ChatObject.hasAdminRights(fwdChat);
                    } else if (!message.isForwarded()) {
                        canViewStats = ChatObject.hasAdminRights(getCurrentChat());
                    }
                }
                if (canViewStats) {
                    items.add(LocaleController.getString(R.string.ViewStatistics));
                    options.add(OPTION_STATISTICS);
                    icons.add(R.drawable.msg_stats);
                }
                // --- NagramX End ---
                if (allowUnpin) {
                    items.add(LocaleController.getString(R.string.UnpinMessage));
                    options.add(OPTION_UNPIN);
                    icons.add(R.drawable.msg_unpin);
                } else if (allowPin) {
                    items.add(LocaleController.getString(R.string.PinMessage));
                    options.add(OPTION_PIN);
                    icons.add(R.drawable.msg_pin);
                }
                /*if (selectedObject != null && selectedObject.contentType == 0 && ((!TextUtils.isEmpty(selectedObject.getMessageTextToTranslate(selectedObjectGroup, null)) && !selectedObject.isAnimatedEmoji() && !selectedObject.isDice()) || (selectedObject.type == MessageObject.TYPE_ARTICLE && selectedObject.messageOwner != null && selectedObject.messageOwner.rich_message != null && !selectedObject.translated))) {
                    items.add(LocaleController.getString(R.string.TranslateMessage));
                    options.add(OPTION_TRANSLATE);
                    icons.add(R.drawable.msg_translate);
                }*/
                if ((allowEdit || chatMode == MODE_WELCOME_MESSAGES) && !GroupedIconsView.useGroupedIcons()) {
                    items.add(LocaleController.getString(R.string.Edit));
                    options.add(OPTION_EDIT);
                    icons.add(R.drawable.msg_edit);
                }
                if (ChatObject.isMonoForum(currentChat) && selectedObject.getGroupId() == 0 && selectedObjectGroup == null && message != null && message.messageOwner != null && message.messageOwner.suggested_post == null && message.messageOwner.action == null) {
                    items.add(LocaleController.getString(R.string.EditOfferAdd));
                    options.add(OPTION_SUGGESTION_ADD_OFFER);
                    icons.add(R.drawable.menu_edit_price);
                }
                if (!selectedObject.isPaidSuggestedPostProtected() && chatMode == MODE_SCHEDULED && selectedObject.canEditMessageScheduleTime(currentChat)) {
                    items.add(LocaleController.getString(R.string.MessageScheduleEditTime));
                    options.add(OPTION_EDIT_SCHEDULE_TIME);
                    icons.add(R.drawable.msg_calendar2);
                }
                MessageObject msg = selectedObjectGroup != null ? selectedObjectGroup.findPrimaryMessageObject() : selectedObject;
                if (msg != null && msg.isFactCheckable() && getMessagesController().canEditFactcheck && ChatObject.isChannelAndNotMegaGroup(currentChat) && chatMode == MODE_DEFAULT) {
                    items.add(LocaleController.getString(msg.getFactCheck() == null ? R.string.AddFactCheck : R.string.EditFactCheck));
                    options.add(OPTION_FACT_CHECK);
                    icons.add(R.drawable.menu_factcheck);
                }
                if (chatMode != MODE_WELCOME_MESSAGES && chatMode != MODE_SCHEDULED && selectedObject.contentType == 0 && selectedObject.getId() > 0 && !selectedObject.isOut() && (currentChat != null || currentUser != null && currentUser.bot)) {
                    if (UserObject.isReplyUser(currentUser)) {
                        items.add(LocaleController.getString(R.string.BlockContact));
                        options.add(OPTION_REPORT_CHAT);
                        icons.add(R.drawable.msg_block2);
                    } else if (NekoConfig.showReport.Bool() && !isAyuDeleted) {
                        items.add(LocaleController.getString(R.string.ReportChat));
                        options.add(OPTION_REPORT_CHAT);
                        icons.add(R.drawable.msg_report);
                    }
                }
                if (message.canDeleteMessage(chatMode == MODE_SCHEDULED, currentChat) && (threadMessageObjects == null || !threadMessageObjects.contains(message))) {
                    allowDelete = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(chatMode == MODE_SAVED && threadMessageId != getUserConfig().getClientUserId() ? R.string.Remove : R.string.Delete));
                        options.add(OPTION_DELETE);
                        icons.add(deleteIconRes);
                    }
                }
            } else { // currentEncryptedChat == null END
                if ((allowChatActions || isEphemeralFromBot) && (primaryMessage == null || !primaryMessage.isWelcomeMessage()) && !isInsideContainer && chatMode != MODE_WELCOME_MESSAGES) {
                    allowReply = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(R.string.Reply));
                        options.add(OPTION_REPLY);
                        icons.add(R.drawable.menu_reply);
                    }
                }
                if ((selectedObject.type == MessageObject.TYPE_TEXT || selectedObject.type == MessageObject.TYPE_ARTICLE || selectedObject.isAnimatedEmoji() || selectedObject.isAnimatedEmojiStickers() || getMessageCaption(selectedObject, selectedObjectGroup) != null) && (!noforwardsOrPaidMedia || isEphemeral)) {
                    allowCopy = true;
                    if (!GroupedIconsView.useGroupedIcons()) {
                        items.add(LocaleController.getString(R.string.Copy));
                        options.add(OPTION_COPY);
                        icons.add(R.drawable.msg_copy);
                    }
                }
                if (!isThreadChat() && chatMode != MODE_SCHEDULED && currentChat != null && primaryMessage != null && (currentChat.has_link || primaryMessage.hasReplies()) && currentChat.megagroup && primaryMessage.canViewThread()) {
                    if (primaryMessage.hasReplies()) {
                        items.add(LocaleController.formatPluralString("ViewReplies", primaryMessage.getRepliesCount()));
                    } else {
                        items.add(LocaleController.getString(R.string.ViewThread));
                    }
                    options.add(OPTION_VIEW_REPLIES_OR_THREAD);
                    icons.add(R.drawable.msg_viewreplies);
                } else if (isThreadChat() && !isTopic && chatMode != MODE_SCHEDULED && currentChat != null) {
                    options.add(nkbtn_view_in_chat);
                    icons.add(R.drawable.msg_viewreplies);
                    items.add(LocaleController.getString(R.string.ViewInChat));
                }
                if (selectedObject != null && selectedObject.messageOwner != null && selectedObject.messageOwner.action == null && currentChat != null && currentChat.forum && !isTopic && selectedObject.messageOwner != null && selectedObject.messageOwner.reply_to != null && selectedObject.messageOwner.reply_to.forum_topic) {
                    items.add(LocaleController.getString(R.string.ViewInTopic));
                    options.add(OPTION_VIEW_IN_TOPIC);
                    icons.add(R.drawable.msg_viewintopic);
                }
                if (type == MESSAGE_TYPE_MEDIA_CACHED && !noforwardsOrPaidMedia && !selectedObject.hasRevealedExtendedMedia() && !selectedObject.needDrawBluredPreview()) {
                    if (selectedObject.isVideo()) {
                        items.add(LocaleController.getString(R.string.SaveToGallery));
                        options.add(OPTION_SAVE_TO_GALLERY);
                        icons.add(R.drawable.msg_gallery);
                        items.add(LocaleController.getString(R.string.ShareFile));
                        options.add(OPTION_SHARE);
                        icons.add(R.drawable.msg_shareout);
                    } else if (selectedObject.isMusic() && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                        items.add(LocaleController.getString(R.string.SaveToMusic));
                        options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                        icons.add(R.drawable.msg_download);
                        items.add(LocaleController.getString(R.string.ShareFile));
                        options.add(OPTION_SHARE);
                        icons.add(R.drawable.msg_shareout);
                    } else if (!selectedObject.isVideo() && selectedObject.getDocument() != null && !selectedObject.isVoiceOnce() && !selectedObject.isRoundOnce()) {
                        items.add(LocaleController.getString(R.string.SaveToDownloads));
                        options.add(OPTION_SAVE_TO_DOWNLOADS_OR_MUSIC);
                        icons.add(R.drawable.msg_download);
                        items.add(LocaleController.getString(R.string.ShareFile));
                        options.add(OPTION_SHARE);
                        icons.add(R.drawable.msg_shareout);
                    } else {
                        items.add(LocaleController.getString(R.string.SaveToGallery));
                        options.add(OPTION_SAVE_TO_GALLERY);
                        icons.add(R.drawable.msg_gallery);
                        allowCopyPhoto = true;
                        if (
                            (!GroupedIconsView.useGroupedIcons() && NaConfig.INSTANCE.getShowCopyPhoto().Bool())
                            ||
                            (GroupedIconsView.useGroupedIcons() && (!isMessageTextEmpty || hasCaption) && canDeleteMessage && NaConfig.INSTANCE.getShowCopyPhoto().Bool())
                        ) {
                            items.add(LocaleController.getString(R.string.CopyPhoto));
                            options.add(OPTION_COPY_PHOTO);
                            icons.add(R.drawable.msg_copy_photo);
                        }
                        if (
                            (!GroupedIconsView.useGroupedIcons() && NaConfig.INSTANCE.getShowCopyAsSticker().Bool())
                            ||
                            (GroupedIconsView.useGroupedIcons() && (!isMessageTextEmpty || hasCaption) && canDeleteMessage && NaConfig.INSTANCE.getShowCopyAsSticker().Bool())
                        ) {
                            items.add(LocaleController.getString(R.string.CopyPhotoAsSticker));
                            options.add(OPTION_COPY_PHOTO_AS_STICKER);
                            icons.add(R.drawable.msg_copy_photo);
                        }
                    }
                } else if (type == MESSAGE_TYPE_XML) {
                    items.add(LocaleController.getString(R.string.ApplyLocalizationFile));
                    options.add(OPTION_APPLY_LOCALIZATION_OR_THEME);
                    icons.add(R.drawable.msg_language);
                } else if (type == MESSAGE_TYPE_THEME) {
                    items.add(LocaleController.getString(R.string.ApplyThemeFile));
                    options.add(OPTION_APPLY_LOCALIZATION_OR_THEME);
                    icons.add(R.drawable.msg_theme);
                } else if (type == MESSAGE_TYPE_STICKER_PACK_NOT_INSTALLED && NaConfig.INSTANCE.getShowAddToStickers().Bool()) {
                    items.add(LocaleController.getString(R.string.AddToStickers));
                    options.add(OPTION_ADD_TO_STICKERS_OR_MASKS);
                    icons.add(R.drawable.msg_sticker);
                } else if (type == MESSAGE_TYPE_CONTACT) {
                    long uid = selectedObject.messageOwner.media.user_id;
                    TLRPC.User user = null;
                    if (uid != 0) {
                        user = MessagesController.getInstance(currentAccount).getUser(uid);
                    }
                    if (user != null && user.id != getUserConfig().getClientUserId() && getContactsController().contactsDict.get(user.id) == null) {
                        items.add(LocaleController.getString(R.string.AddContactTitle));
                        options.add(OPTION_ADD_CONTACT);
                        icons.add(R.drawable.msg_addcontact);
                    }
                    if (!TextUtils.isEmpty(selectedObject.messageOwner.media.phone_number)) {
                        if (!noforwardsOrPaidMedia) {
                            items.add(LocaleController.getString(R.string.Copy));
                            options.add(OPTION_COPY_PHONE_NUMBER);
                            icons.add(R.drawable.msg_copy);
                        }
                        items.add(LocaleController.getString(R.string.Call));
                        options.add(OPTION_CALL);
                        icons.add(R.drawable.msg_callback);
                    }
                }
                final MessageObject msg = getMessageForTranslate();
                boolean showTranslate = NekoConfig.showTranslate.Bool();
                boolean isTranslatableMessage = msg != null && !msg.isSponsored() && !msg.isAnimatedEmoji() && !msg.isDice();
                if (showTranslate && isTranslatableMessage) {
                    boolean isRichMessage = msg != null && msg.isRich();
                    if (isRichMessage) {
                        showTranslate = true;
                    }
                    String fromLang = msg.messageOwner.originalLanguage;
                    if (fromLang != null && RestrictedLanguagesSelectActivity.getRestrictedLanguages().contains(fromLang)) {
                        showTranslate = false;
                    }
                    boolean isOutgoingOrNotTranslatingDialog = msg.isOutOwner() || !isTranslatingDialog(msg);
                    boolean isTranslated = msg.isTranslated();
                    boolean canUndoTranslate = isTranslated && isOutgoingOrNotTranslatingDialog;
                    if (isOutgoingOrNotTranslatingDialog) {
                        items.add(canUndoTranslate ? getString(R.string.UndoTranslate) : getString(R.string.Translate));
                        options.add(nkbtn_translate);
                        icons.add(R.drawable.msg_translate);
                    }
                }
                allowDelete = true;
                items.add(LocaleController.getString(chatMode == MODE_SAVED && threadMessageId != getUserConfig().getClientUserId() ? R.string.Remove : R.string.Delete));
                options.add(OPTION_DELETE);
                icons.add(deleteIconRes);
            }
            if (chatInfo != null && chatInfo.participants != null && chatInfo.participants.participants != null) {
                selectedParticipant = null;
                if (selectedFromUserId != 0) {
                    long user_id = selectedFromUserId;
                    for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                        TLRPC.ChatParticipant participant = chatInfo.participants.participants.get(a);
                        if (participant.user_id != user_id || participant.user_id == getUserConfig().getCurrentUser().id) {
                            continue;
                        }

                        boolean canEditAdmin;
                        boolean canRestrict;
                        boolean editingAdmin;
                        final TLRPC.ChannelParticipant channelParticipant;

                        if (ChatObject.isChannel(currentChat)) {
                            channelParticipant = ((TLRPC.TL_chatChannelParticipant) participant).channelParticipant;
                            canEditAdmin = ChatObject.canAddAdmins(currentChat);
                            if (canEditAdmin && (channelParticipant instanceof TLRPC.TL_channelParticipantCreator || channelParticipant instanceof TLRPC.TL_channelParticipantAdmin && !channelParticipant.can_edit)) {
                                canEditAdmin = false;
                            }
                            canRestrict = ChatObject.canBlockUsers(currentChat) && (!(channelParticipant instanceof TLRPC.TL_channelParticipantAdmin || channelParticipant instanceof TLRPC.TL_channelParticipantCreator) || channelParticipant.can_edit);
                            editingAdmin = channelParticipant instanceof TLRPC.TL_channelParticipantAdmin;
                        } else {
                            canEditAdmin = currentChat.creator;
                            canRestrict = currentChat.creator;
                            editingAdmin = participant instanceof TLRPC.TL_chatParticipantAdmin;
                        }

                        if (canEditAdmin && NekoConfig.showAdminActions.Bool()) {
                            items.add(editingAdmin ? LocaleController.getString(R.string.EditAdminRights) : LocaleController.getString(R.string.SetAsAdmin));
                            icons.add(R.drawable.profile_admin);
                            options.add(nkbtn_editAdmin);
                            selectedParticipant = participant;
                        }
                        if (canRestrict && NekoConfig.showChangePermissions.Bool()) {
                            items.add(LocaleController.getString(R.string.ChangePermissions));
                            icons.add(R.drawable.msg_permissions);
                            options.add(nkbtn_editPermission);
                            selectedParticipant = participant;
                        }
                    }
                }
            }
        }
        if (NekoConfig.showMessageDetails.Bool()) {
            items.add(LocaleController.getString(R.string.MessageDetails));
            options.add(nkbtn_detail);
            icons.add(R.drawable.msg_info);
        }
        pluginMessageMenuItemsByOption.clear();
        List<MenuItemRecord> pluginMenuItems = PluginsController.getInstance().getMenuItemsForLocation(
                PluginsConstants.MenuItemTypes.MESSAGE_CONTEXT_MENU,
                buildMessageMenuPluginContext(message, groupedMessages));
        for (int i = 0; i < pluginMenuItems.size(); i++) {
            MenuItemRecord pluginItem = pluginMenuItems.get(i);
            int optionId = PLUGIN_MESSAGE_MENU_OPTION_BASE + i;
            pluginMessageMenuItemsByOption.put(optionId, pluginItem);
            items.add(pluginItem.text);
            options.add(optionId);
            icons.add(pluginItem.iconResId != 0 ? pluginItem.iconResId : R.drawable.msg_plugins);
        }
        if (showWelcomeMessageRevertOption(primaryMessage)) {
            items.add(getString(R.string.WelcomeMessageRevert));
            options.add(OPTION_WELCOME_REVERT);
            icons.add(R.drawable.outline_revert_24);
        }
        this.lastMessageMenuStatus = new MessageMenuStatus(allowCopy, allowCopyPhoto, allowCopyLink, allowCopyLinkPm, allowDelete, allowEdit, allowReply, allowReplyPm, allowForward);
    }

    private boolean showWelcomeMessageRevertOption(MessageObject messageObject) {
        return chatMode == MODE_DEFAULT && messageObject != null && messageObject.isWelcomeAnchored();
    }

    private boolean isTitleCentered() {
        return canShowCenteredTitle(this);
    }

    private boolean canShowCenteredTitle(ChatActivity parentFragment) {
        if (!NaConfig.INSTANCE.getCenterActionBarTitle().Bool()) {
            return false;
        }
        if (parentFragment == null) {
            return false;
        }
        if (parentFragment.isReplyChatComment() || parentFragment.isReport()) {
            return false;
        }
        return parentFragment.getChatMode() != ChatActivity.MODE_SEARCH && parentFragment.getChatMode() != ChatActivity.MODE_SAVED;
    }

    public MessageObject getMessageForTranslate() {
        MessageObject messageObject = null;
        boolean isDocuments = selectedObjectGroup != null && selectedObjectGroup.isDocuments;
        if (selectedObjectGroup != null && !isDocuments) {
            for (MessageObject object : selectedObjectGroup.messages) {
                if (canTranslateSelectedMessage(object)) {
                    if (messageObject != null) {
                        messageObject = null;
                        break;
                    } else {
                        messageObject = object;
                    }
                }
            }
        } else if (canTranslateSelectedMessage(selectedObject)) {
            messageObject = selectedObject;
        }
        if (messageObject == null && isDocuments) {
            for (MessageObject obj : selectedObjectGroup.messages) {
                if (canTranslateSelectedMessage(obj)) {
                    messageObject = obj;
                }
            }
        }
        return messageObject;
    }

    private boolean canTranslateSelectedMessage(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null) {
            return false;
        }
        if (!TextUtils.isEmpty(messageObject.messageOwner.message) || messageObject.isPoll()) {
            return true;
        }
        return messageObject.isRich() && !RichMessageTransHelper.collectPlainTexts(messageObject.messageOwner.rich_message).isEmpty();
    }

    private boolean handleTranslateDuringAutoTrans(String toLang) {
        if (selectedObject == null || selectedObject.messageOwner == null || selectedObject.isOutOwner()) {
            return false;
        }
        if (!isTranslatingDialog(selectedObject)) {
            return false;
        }
        String text;
        if (selectedObject.messageOwner.summarizedOpen && selectedObject.messageOwner.summaryText != null && !TextUtils.isEmpty(selectedObject.messageOwner.summaryText.text)) {
            text = selectedObject.messageOwner.summaryText.text;
        } else {
            text = getMessageHelper().getMessagePlainText(selectedObject, selectedObjectGroup);
        }
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        DialogTransKt.startTrans(getParentActivity(), text, toLang != null ? toLang : NekoConfig.translateToLang.String(), 0);
        return true;
    }

    private boolean isTranslatingDialog(MessageObject messageObject) {
        return messageObject != null && getMessagesController().getTranslateController().isTranslatingDialog(messageObject.getDialogId());
    }

    private record MessageMenuStatus(boolean allowCopy, boolean allowCopyPhoto,
                                     boolean allowCopyLink, boolean allowCopyLinkPm,
                                     boolean allowDelete, boolean allowEdit,
                                     boolean allowReply, boolean allowReplyPm,
                                     boolean allowForward) {
    }

    public boolean isBlockedUser(long senderId) {
        if (!NekoConfig.ignoreBlocked.Bool()) {
            return false;
        }
        return getMessagesController().blockePeers.indexOfKey(senderId) >= 0 || AyuFilter.isCustomFilteredPeer(senderId);    }

    private void updateBotforumTabsBottomMargin() {
        if (topicsTabs == null) {
            return;
        }

        final float margin = windowInsetsStateHolder.getAnimatedMaxBottomInset() +
                (chatInputViewsContainer.getInputBubbleHeight() + dp(9) - dp(5));

        topicsTabs.setSideMenuBackgroundMarginBottom(margin);
    }

    private void checkUi_botMenuPosition() {
        final float margin = windowInsetsStateHolder.getAnimatedMaxBottomInset()
            + getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM)
            + (chatInputViewsContainer.getInputBubbleHeight() + dp(9 + 6));

        if (chatActivityEnterView != null && chatActivityEnterView.botCommandsMenuContainer != null) {
            chatActivityEnterView.botCommandsMenuContainer.setTranslationY(-margin);
        }
        if (mentionContainer != null) {
            mentionContainer.setTranslationY(mentionContainer.isReversed() ? dp(5) : -margin);
        }
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    @Override
    public EdgeToEdgeSupportMode getEdgeToEdgeSupportMode() {
        return EdgeToEdgeSupportMode.FULL;
    }

    @Override
    public boolean drawEdgeNavigationBar() {
        return false;
    }


    /* */

    private float calculateInputIslandHeight(boolean target) {
        final float enterViewIslandHeight = Math.max(
            chatActivityEnterView != null ? chatActivityEnterView.getIslandTotalHeight(target): 0, dp(44));

        final float defaultIslandHeight = dp(44);
        final float enterViewFactor;
        float visibility;
        float pollAddVisibility;

        if (target) {
            enterViewFactor = bottomViewsVisibilityController.getCurrentPriorityContainerId() == MESSAGE_INPUT_CONTAINER ? 1 : 0;
            visibility = bottomViewsVisibilityController.getCurrentPriorityContainerId() == 0 ? 0 : 1;
            pollAddVisibility = animatorPollAddAnswerVisibility.getValue() ? 1 : 0;
        } else {
            enterViewFactor = bottomViewsVisibilityController.getVisibility(MESSAGE_INPUT_CONTAINER);
            visibility = 1f - bottomViewsVisibilityController.getVisibility(0);
            pollAddVisibility = animatorPollAddAnswerVisibility.getFloatValue();
        }

        if (shouldHideBottomBar()) {
            return 0;
        }
        if (!isInsideContainer && !isInPreviewMode()) {
            return lerp(Math.max(lerp(defaultIslandHeight, enterViewIslandHeight, enterViewFactor) * visibility, dp(44)), -dp(7), pollAddVisibility);
        } else {
            return lerp(defaultIslandHeight, enterViewIslandHeight, enterViewFactor) * visibility;
        }
    }

    private float inputIslandHeightCurrent;
    private float inputIslandHeightTarget;

    public float getInputIslandHeightTarget() {
        return inputIslandHeightCurrent;
    }

    private void checkUi_inputIslandHeight() {
        if (contentView == null) {
            return;
        }

        inputIslandHeightCurrent = calculateInputIslandHeight(false);
        inputIslandHeightTarget = calculateInputIslandHeight(true);

        chatInputViewsContainer.setInputBubbleHeight(inputIslandHeightCurrent);
        updatePagedownButtonsPosition();
        updateBotforumTabsBottomMargin();
        checkUi_botMenuPosition();
        checkUi_BlurHeight();
        checkUi_emptyContainerPosition();
        checkUi_chatListViewPaddings();
    }

    private static final Rect clipBoundsRect = new Rect();
    private void checkUi_BlurHeight() {
        final boolean hideBottomBar = shouldHideBottomBar();
        final float inputHeight = windowInsetsStateHolder.getAnimatedMaxBottomInset()
            + dp(9) + (hideBottomBar ? 0 : chatInputViewsContainer.getInputBubbleHeight()) + dp(7)
            + getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM);

        chatActivityFadeView.setFadeZoneBottom((int) inputHeight);

        int clipBound = contentView.getMeasuredHeight() - (int) inputHeight + dp(36);
        clipBound = androidx.core.math.MathUtils.clamp(clipBound, 0, contentView.getMeasuredHeight());

        clipBoundsRect.set(0, 0, contentView.getMeasuredWidth(), clipBound);
        clipBoundsRect.set(0,
            blurredViewTopOffset,
            chatListView.getMeasuredWidth(),
            chatListView.getMeasuredHeight() - blurredViewBottomOffset - (int) inputHeight + dp(36)
        );
    }

    private void checkUi_emptyContainerPosition() {
        if (emptyViewContainer != null) {
            emptyViewContainer.setTranslationY(-0.5f * (dp(9)
                + windowInsetsStateHolder.getAnimatedImeBottomInset()
                + chatInputViewsContainer.getInputBubbleHeight()
            ));
        }
    }

    private void checkUi_topFade() {
        if (parentChatActivity != null) {
            parentChatActivity.checkUi_topFade();
        }

        float fadeHeight = actionBar.getMeasuredHeight();
        fadeHeight += dp(7 - 6);
        fadeHeight += getTopPanelHeightWithPadding(dp(7));
        if (topicsTabs != null) {
            fadeHeight += getTopicTabsSideSize(TopicsTabsView.Position.TOP);
        }
        if (actionBarSearchTags != null) {
            fadeHeight += dp(28 + 7) * actionBarSearchTags.shownT;
        }
        fadeHeight += dp(36 + 7) * getHashtagTabsShownT();

        chatActivityFadeView.setFadeZoneTop((int) fadeHeight);
    }

    private void checkUi_messagesSearchListPadding() {
        if (parentChatActivity != null) {
            parentChatActivity.checkUi_messagesSearchListPadding();
        }

        final int top = AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight() + dp(2)
            + ((int) getTopPanelHeightWithPadding(dp(7)))
            + (actionBarSearchTags != null ? dp((28 + 7) * actionBarSearchTags.shownT) : 0)
            + dp((36 + 7) * getHashtagTabsShownT());

        final int bottom = (int) windowInsetsStateHolder.getAnimatedMaxBottomInset() + dp(60);
        if (messagesSearchListView != null) {
            messagesSearchListView.setPadding(0, top, 0, bottom);
        }
        if (hashtagHistoryView != null) {
            hashtagHistoryView.setTopBottomPadding(top, bottom);
        }
        if (messagesSearchListContainer != null) {
            messagesSearchListContainer.setFade(top, bottom);
        }
        if (hashtagLoadingView != null) {
            hashtagLoadingView.setTranslationY(top);
        }
        if (hashtagSearchEmptyView != null) {
            hashtagSearchEmptyView.linearLayout.setTranslationY((top - bottom) / 2f + dp(32));
        }
    }

    private float getTopPanelHeightWithPadding(float padding) {
        ChatActivity chatActivity = parentChatActivity != null ? parentChatActivity : this;
        if (chatActivity.topPanelLayout == null) return 0;
        return chatActivity.topPanelLayout.getAnimatedHeightWithPadding(padding);
    }

    private void checkUi_topPanelLayoutVisibility() {
        final float factor = 1f - animatorHideTopPanelByEmojiKeyboardExpanded.getFloatValue();
        topPanelLayout.setAlpha(factor);
        topPanelLayout.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);
    }

    private void checkUi_topPanelLayoutWidth() {
        if (topPanelLayout != null) {
            float sideMenu = getSideMenuWidth()
                * (1f - animatorSearchResultAsListVisibility.getFloatValue())
                * (1f - getHashtagTabsShownT());

            topPanelLayout.setPadding(dp(7) + (int) sideMenu, dp(7), dp(7), dp(7));
        }
    }

    private void checkUi_topicTabsVisibility() {
        final float factor = 1f - animatorHideTopPanelByEmojiKeyboardExpanded.getFloatValue();
        if (topicsTabs != null) {
            topicsTabs.setAlpha(factor);
            topicsTabs.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void checkUi_sideControlsLayoutPosition() {
        final float factor = (1f - animatorPollAddAnswerVisibility.getFloatValue())
            * (1f - animatorSearchResultAsListVisibility.getFloatValue());

        sideControlsButtonsLayout.setTranslationX(dp(80) * (1f - factor));
        sideControlsButtonsLayout.setAlpha(factor);
        sideControlsButtonsLayout.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);
    }

    private void checkUi_hashtagSearchHistoryVisibility() {
        final float factor = animatorSearchHashtagHistoryVisibility.getFloatValue();
        if (hashtagHistoryView != null) {
            hashtagHistoryView.setAlpha(factor);
            hashtagHistoryView.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);
        }
        if (messagesSearchListView != null) {
            messagesSearchListView.setAlpha(1f - factor);
            messagesSearchListView.setVisibility((1f - factor) > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void checkUi_messagesSearchListContainerVisibility() {
        if (messagesSearchListContainer == null) {
            return;
        }

        final float factor = animatorSearchResultAsListVisibility.getFloatValue();
        messagesSearchListContainer.setAlpha(factor);
        messagesSearchListContainer.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);
    }

    private void checkUi_fadeViewVisible() {
        final boolean visible = animatorSearchResultAsListVisibility.getFloatValue() < 1;
        chatActivityFadeView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void checkUi_backgroundViewVisible() {
        if (parentChatActivity != null) {
            return;
        }

        final boolean visible = animatorSearchResultAsListVisibility.getFloatValue() < 1
            || searchViewPager != null && searchViewPager.getPositionAnimated() > 0;
        contentView.backgroundView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void checkUi_chatListViewVisible() {
        final boolean visible = animatorSearchResultAsListVisibility.getFloatValue() < 1;
        chatListView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void checkUi_avatarContainerVisibility() {
        if (avatarContainer != null) {
            final float factor1 = 1f - actionBar.getActionModeFactor();
            final float factor2 = 1f - animatorSearchFieldVisibility.getFloatValue();
            final float factor = factor1 * factor2;
            final float scale = lerp(0.95f, 1f, factor);
            avatarContainer.setScaleX(scale);
            avatarContainer.setScaleY(scale);
            avatarContainer.setAlpha(factor);
            avatarContainer.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);
        }
    }



    @Override
    public void onFactorChanged(int id, float factor, float fraction, FactorAnimator callee) {
        if (id == ANIMATOR_ID_PULLING_DOWN_CONTAINER_VISIBILITY) {
            onBottomItemsVisibilityChanged();
        } else if (id == ANIMATOR_ID_ROUND_MESSAGE_CAMERA_VISIBILITY) {
            if (roundVideoRecordBackground != null) {
                roundVideoRecordBackground.setAlpha(factor);
                roundVideoRecordBackground.setVisibility(factor > 0 ? View.VISIBLE : View.INVISIBLE);
            }
            // checkUi_BlurHeight();
        } else if (id == ANIMATOR_ID_POLL_ADD_ANSWER_VISIBILITY) {
            onBottomItemsVisibilityChanged();
            checkUi_sideControlsLayoutPosition();
            if (pollAddOptionFieldLayout != null) {
                pollAddOptionFieldLayout.setAnimatedVisibility(animatorPollAddAnswerVisibility.getFloatValue());
            }
        } else if (id == ANIMATOR_ID_HIDE_TOP_PANEL_BY_EMOJI_KEYBOARD_EXPANDED) {
            checkUi_topPanelLayoutVisibility();
            checkUi_topicTabsVisibility();
        } else if (id == ANIMATOR_ID_SEARCH_RESULT_LIST_VISIBILITY) {
            checkUi_messagesSearchListContainerVisibility();
            checkUi_fadeViewVisible();
            checkUi_backgroundViewVisible();
            checkUi_chatListViewVisible();
            checkUi_sideControlsLayoutPosition();
            checkUi_topPanelPositions();
            checkUi_topPanelLayoutWidth();
            invalidateMergedVisibleBlurredPositionsAndSources(BLUR_INVALIDATE_FLAG_SCROLL);
        } else if (id == ANIMATOR_ID_SEARCH_FIELD_VISIBILITY) {
            checkUi_avatarContainerVisibility();
            if (actionBar != null) {
                actionBar.setSearchFactor(factor);
            }
        } else if (id == ANIMATOR_ID_SEARCH_HASHTAG_HISTORY_VISIBILITY) {
            checkUi_hashtagSearchHistoryVisibility();
        }
    }

    @Override
    public void onFactorChangeFinished(int id, float finalFactor, FactorAnimator callee) {
        if (id == ANIMATOR_ID_POLL_ADD_ANSWER_VISIBILITY) {
            if (finalFactor == 0) {
                pollAddOptionModeDestroy();
            }
        }
    }

    private static final int ANIMATOR_ID_PULLING_DOWN_CONTAINER_VISIBILITY = 0;
    private static final int ANIMATOR_ID_ROUND_MESSAGE_CAMERA_VISIBILITY = 1;
    private static final int ANIMATOR_ID_POLL_ADD_ANSWER_VISIBILITY = 2;
    private static final int ANIMATOR_ID_HIDE_TOP_PANEL_BY_EMOJI_KEYBOARD_EXPANDED = 3;
    private static final int ANIMATOR_ID_SEARCH_RESULT_LIST_VISIBILITY = 4;
    private static final int ANIMATOR_ID_SEARCH_FIELD_VISIBILITY = 5;
    private static final int ANIMATOR_ID_SEARCH_HASHTAG_HISTORY_VISIBILITY = 6;

    private final BoolAnimator animatorPullingDownContainerVisibility = new BoolAnimator(ANIMATOR_ID_PULLING_DOWN_CONTAINER_VISIBILITY, this, CubicBezierInterpolator.EASE_OUT_QUINT, 320);
    private final BoolAnimator animatorRoundMessageCameraVisibility = new BoolAnimator(ANIMATOR_ID_ROUND_MESSAGE_CAMERA_VISIBILITY, this, CubicBezierInterpolator.EASE_OUT_QUINT, 520);
    private final BoolAnimator animatorPollAddAnswerVisibility = new BoolAnimator(ANIMATOR_ID_POLL_ADD_ANSWER_VISIBILITY, this, CubicBezierInterpolator.EASE_OUT_QUINT, 520);
    private final BoolAnimator animatorHideTopPanelByEmojiKeyboardExpanded = new BoolAnimator(ANIMATOR_ID_HIDE_TOP_PANEL_BY_EMOJI_KEYBOARD_EXPANDED, this, CubicBezierInterpolator.EASE_OUT_QUINT, 320);
    private final BoolAnimator animatorSearchResultAsListVisibility = new BoolAnimator(ANIMATOR_ID_SEARCH_RESULT_LIST_VISIBILITY, this, CubicBezierInterpolator.EASE_OUT_QUINT, 320);
    private final BoolAnimator animatorSearchFieldVisibility = new BoolAnimator(ANIMATOR_ID_SEARCH_FIELD_VISIBILITY, this, CubicBezierInterpolator.EASE_OUT_QUINT, 320);
    private final BoolAnimator animatorSearchHashtagHistoryVisibility = new BoolAnimator(ANIMATOR_ID_SEARCH_HASHTAG_HISTORY_VISIBILITY, this, CubicBezierInterpolator.EASE_OUT_QUINT, 320);

    public static final int MESSAGE_INPUT_CONTAINER = 1;
    public static final int BOTTOM_OVERLAY_TEXT_CONTAINER = 2;
    public static final int BOTTOM_OVERLAY_CHAT_CONTAINER = 3;
    public static final int MESSAGE_ACTION_CONTAINER = 5;
    public static final int MESSAGE_SEARCH_CONTAINER = 4;

    private final ChatActivityBottomViewsVisibilityController bottomViewsVisibilityController =
            new ChatActivityBottomViewsVisibilityController(this::onBottomItemsVisibilityChanged);

    private void checkBottomViewVisibility(View view, int containerId, boolean allowVisibilityChange) {
        if (view != null) {
            final float alpha = bottomViewsVisibilityController.getVisibility(containerId)
                * (1f - animatorPullingDownContainerVisibility.getFloatValue())
                * (1f - animatorPollAddAnswerVisibility.getFloatValue());

            view.setAlpha(alpha);
            if (allowVisibilityChange) {
                final int visibility = alpha > 0 ? View.VISIBLE : View.GONE;
                if (view.getVisibility() != visibility) {
                    view.setVisibility(visibility);
                }
            }

            if (containerId == MESSAGE_INPUT_CONTAINER && chatActivityEnterView != null) {
                chatActivityEnterView.setBubblesProgress(alpha);
            }
        }
    }



    private void onBottomItemsVisibilityChanged() {
        checkBottomViewVisibility(actionsButtonsLayout, MESSAGE_ACTION_CONTAINER, true);
        checkBottomViewVisibility(chatActivityEnterView, MESSAGE_INPUT_CONTAINER, false);
        checkBottomViewVisibility(searchContainer, MESSAGE_SEARCH_CONTAINER, true);
        checkBottomViewVisibility(bottomChannelButtonsLayout, BOTTOM_OVERLAY_CHAT_CONTAINER, false);
        checkBottomViewVisibility(bottomOverlay, BOTTOM_OVERLAY_TEXT_CONTAINER, false);

        final float pollAddOptionVisibility = animatorPollAddAnswerVisibility.getFloatValue();
        final float actionFactor = bottomViewsVisibilityController.getVisibility(MESSAGE_ACTION_CONTAINER);

        final float hideFactor = 1f - ((1f - actionFactor) * (1f - pollAddOptionVisibility));

        if (actionsButtonsLayout != null) {
            actionsButtonsLayout.setTotalVisibilityFactor(hideFactor);
        }
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setTranslationY(dp(54) * hideFactor);
        }
        if (searchContainer != null) {
            searchContainer.setTranslationY(dp(54) * hideFactor);
        }
        if (bottomChannelButtonsLayout != null) {
            bottomChannelButtonsLayout.setTranslationY(dp(54) * hideFactor);
        }
        if (bottomOverlay != null) {
            bottomOverlay.setTranslationY(dp(54) * hideFactor);
        }
        if (chatInputViewsContainer != null) {
            chatInputViewsContainer.setInputBubbleAlpha((int) (255 * (1f - hideFactor)));
            chatInputViewsContainer.setInputBubbleTranslationY(dp(54) * hideFactor);
        }


        if (bottomChannelButtonsLayout != null) {
            bottomChannelButtonsLayout.setTotalVisibilityFactor(
                bottomViewsVisibilityController.getVisibility(BOTTOM_OVERLAY_CHAT_CONTAINER) *
                (1f - animatorPullingDownContainerVisibility.getFloatValue())
            );
        }

        if (pullingDownDrawable != null) {
            final float factor = animatorPullingDownContainerVisibility.getFloatValue();
            if (pullingDownDrawable.progressToBottomPanel != factor) {
                pullingDownDrawable.progressToBottomPanel = factor;
                fragmentView.invalidate();
            }
        }

        checkUi_inputIslandHeight();
    }

    private void invalidateAllGlassAttachedViews() {
        contentView.invalidate();
        for (View v: glassAttachedViews) {
            v.invalidate();
        }
    }

    private void onSideControlButtonOnClick(int buttonId, View v) {
        if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_PAGE_DOWN) {
            onPageDownClicked();
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_MENTION) {
            loadLastUnreadMention();
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_REACTIONS) {
            wasManualScroll = true;
            getMessagesController().getNextReactionMention(dialog_id, getTopicId(), reactionsMentionCount, (messageId) -> {
                if (messageId == 0) {
                    reactionsMentionCount = 0;
                    updateReactionsMentionButton(true);
                    getMessagesController().markReactionsAsRead(dialog_id, getTopicId());
                } else {
                    updateReactionsMentionButton(true);
                    scrollToMessageId(messageId, 0, false, 0, true, 0);
                }
            });
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_POLL_VOTES) {
            wasManualScroll = true;
            getMessagesController().getNextPollVotesMention(dialog_id, getTopicId(), pollVotesMentionCount, (messageId) -> {
                if (messageId == 0) {
                    pollVotesMentionCount = 0;
                    updatePollVotesMentionButton(true);
                    getMessagesController().markPollVotesAsRead(dialog_id, getTopicId());
                } else {
                    pollVotesMentionCount--;
                    if (pollVotesMentionCount <= 0) {
                        getMessagesController().markPollVotesAsRead(dialog_id, getTopicId());
                    }
                    updatePollVotesMentionButton(true);
                    scrollToMessageId(messageId, 0, false, 0, true, 0);
                }
            });
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_SEARCH_UP) {
            goToNextOrPrevSearchMessage(true);
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_SEARCH_DOWN) {
            goToNextOrPrevSearchMessage(false);
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_ATTACH) {
            if (chatAttachAlert != null) {
                chatAttachAlert.setEditingMessageObject(0, null);
            }
            openAttachMenu();
        }
    }

    private void goToNextOrPrevSearchMessage(boolean searchUp) {
        if (chatMode == MODE_SEARCH) {
            if (isFeedSearch()) {
                return;
            }
            HashtagSearchController.getInstance(currentAccount).jumpToMessage(classGuid, hashtagSearchSelectedIndex + (searchUp ? 1 : -1), searchType);
        } else {
            getMediaDataController().searchMessagesInChat(null, dialog_id, mergeDialogId, classGuid, searchUp ? (reversed ? 2 : 1) : (reversed ? 1 : 2), threadMessageId, searchingUserMessages, searchingChatMessages, searchingReaction, searchingType);
            showMessagesSearchListView(false);
        }
    }

    private void loadLastUnreadMention() {
        wasManualScroll = true;
        if (hasAllMentionsLocal) {
            getMessagesStorage().getUnreadMention(dialog_id, getTopicId(), param -> {
                if (param == 0) {
                    hasAllMentionsLocal = false;
                    loadLastUnreadMention();
                } else {
                    scrollToMessageId(param, 0, false, 0, true, 0);
                }
            });
        } else {
            final MessagesStorage messagesStorage = getMessagesStorage();
            TLRPC.TL_messages_getUnreadMentions req = new TLRPC.TL_messages_getUnreadMentions();
            req.peer = getMessagesController().getInputPeer(dialog_id);
            req.limit = 1;
            if (isTopic) {
                req.top_msg_id = (int) threadMessageId;
                req.flags |= 1;
            }
            req.add_offset = newMentionsCount - 1;
            getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                if (error != null || res.messages.isEmpty()) {
                    if (res != null) {
                        newMentionsCount = res.count;
                    } else {
                        newMentionsCount = 0;
                    }
                    messagesStorage.resetMentionsCount(dialog_id, getTopicId(), newMentionsCount);
                    if (newMentionsCount == 0) {
                        hasAllMentionsLocal = true;
                        showMentionDownButton(false, true);
                    } else {
                        sideControlsButtonsLayout.setButtonCount(ChatActivitySideControlsButtonsLayout.BUTTON_MENTION, newMentionsCount, true);
                        loadLastUnreadMention();
                    }
                } else {
                    int id = res.messages.get(0).id;
                    MessageObject object = messagesDict[0].get(id);
                    messagesStorage.markMessageAsMention(dialog_id, id);
                    if (object != null) {
                        object.messageOwner.media_unread = true;
                        object.messageOwner.mentioned = true;
                    }
                    scrollToMessageId(id, 0, false, 0, true, 0);
                }
            }));
        }
    }

    private boolean onSideControlButtonOnLongClick(int buttonId, View view) {
        if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_PAGE_DOWN) {
            if (NekoConfig.rememberAllBackMessages.Bool()) {
                returnToMessageId = 0;
                returnToMessageIdsStack.clear();
                onPageDownClicked();
                if (NekoConfig.disableVibration.Bool()) {
                    AndroidUtil.disableHapticFeedback(view);
                }
                return true;
            }
            return false;
        }

        final Runnable onRead;
        final int type;
        if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_MENTION) {
            type = ReadAllMentionsMenu.TYPE_MENTIONS;
            onRead = () -> {
                for (int a = 0; a < messages.size(); a++) {
                    MessageObject messageObject = messages.get(a);
                    if (messageObject.messageOwner.mentioned && !messageObject.isContentUnread()) {
                        messageObject.setContentIsRead();
                    }
                }
                newMentionsCount = 0;
                getMessagesController().markMentionsAsRead(dialog_id, getTopicId());
                hasAllMentionsLocal = true;
                showMentionDownButton(false, true);
                if (scrimPopupWindow != null) {
                    scrimPopupWindow.dismiss();
                }
            };
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_REACTIONS) {
            type = ReadAllMentionsMenu.TYPE_REACTIONS;
            onRead = () -> {
                for (int i = 0; i < messages.size(); i++) {
                    messages.get(i).markReactionsAsRead();
                }
                reactionsMentionCount = 0;
                updateReactionsMentionButton(true);
                getMessagesController().markReactionsAsRead(dialog_id, getTopicId());
                if (scrimPopupWindow != null) {
                    scrimPopupWindow.dismiss();
                }
            };
        } else if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_POLL_VOTES) {
            type = ReadAllMentionsMenu.TYPE_POLL_VOTES;
            onRead = () -> {
                for (int i = 0; i < messages.size(); i++) {
                    messages.get(i).markPollVotesAsRead();
                }
                pollVotesMentionCount = 0;
                updatePollVotesMentionButton(true);
                getMessagesController().markPollVotesAsRead(dialog_id, getTopicId());
                if (scrimPopupWindow != null) {
                    scrimPopupWindow.dismiss();
                }
            };
        } else {
            return false;
        }

        scrimPopupWindow = ReadAllMentionsMenu.show(type,
            getParentActivity(), getParentLayout(),
            contentView, view, getResourceProvider(), onRead);

        dimBehindView(sideControlsButtonsLayout, true);
        scrimPopupWindow.setOnDismissListener(() -> {
            scrimPopupWindow = null;
            menuDeleteItem = null;
            scrimPopupWindowItems = null;
            chatLayoutManager.setCanScrollVertically(true);
            dimBehindView(false);
            if (chatActivityEnterView != null && chatActivityEnterView.getEditField() != null) {
                chatActivityEnterView.getEditField().setAllowDrawCursor(true);
            }
        });
        try {
            if (!NekoConfig.disableVibration.Bool()) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            }
        } catch (Exception ignored) {}
        return true;
    }

    private static View createMenuTextOption(Context context, Theme.ResourcesProvider resourcesProvider, CharSequence text) {
        return createMenuTextOption(context, resourcesProvider, text, 14);
    }

    private static View createMenuTextOption(Context context, Theme.ResourcesProvider resourcesProvider, CharSequence text, int textSize) {
        FrameLayout sponsoredAbout = new FrameLayout(context);
        sponsoredAbout.setMinimumHeight(dp(48));
        sponsoredAbout.setPadding(dp(14), dp(4), dp(14), dp(4));

        TextView infoText = new TextView(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST && getLayout() != null) {
                    Layout layout = getLayout();
                    int width = 0;
                    for (int i = 0; i < layout.getLineCount(); ++i) {
                        width = Math.max(width, (int) Math.ceil(layout.getLineWidth(i)));
                    }
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(getPaddingLeft() + width + getPaddingRight(), MeasureSpec.EXACTLY);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        infoText.setMaxLines(3);
        infoText.setGravity(Gravity.LEFT);
        infoText.setEllipsize(TextUtils.TruncateAt.END);
        infoText.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
        infoText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        infoText.setMaxWidth(AndroidUtilities.dp(170));
        infoText.setText(text);
        sponsoredAbout.addView(infoText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL));

        return sponsoredAbout;
    }

    public boolean isPeerNoForwards() {
        return currentChat != null ?
            getMessagesController().isChatNoForwards(currentChat) :
            getMessagesController().isUserNoForwards(userInfo);
    }

    private PollAddOptionFieldLayout pollAddOptionFieldLayout;

    private void pollAddOptionModeStart(ChatMessageCell cell) {
        if (cell == null || chatActivityEnterView == null || isInPollAddOptionMode() || cell.getMessageObject() == null) {
            return;
        }

        ViewPositionWatcher.computeRectInParent(cell, contentView, AndroidUtilities.rectTmp);
        if (!cell.getPollAddButtonBounds(AndroidUtilities.rectTmp2)) {
            return;
        }
        float cellBottom = AndroidUtilities.rectTmp.bottom;
        float cellBottomTarget = contentView.getMeasuredHeight() - chatListView.getPaddingBottom() + inputIslandHeightCurrent - dp(2);
        final int scrollBy = (int) (cellBottom - cellBottomTarget);






        final MessageObject messageObject = cell.getMessageObject();

        if (pollAddOptionFieldLayout == null) {
            pollAddOptionFieldLayout = new PollAddOptionFieldLayout(this, getContext(), resourceProvider);

            int index = contentView.indexOfChild(chatListView);
            if (index >= 0) {
                contentView.addView(pollAddOptionFieldLayout, index + 1, LayoutHelper.createFrameMatchParent());
            } else {
                contentView.addView(pollAddOptionFieldLayout, LayoutHelper.createFrameMatchParent());
            }
        }

        int color2;
        if (messageObject.isOutOwner()) {
            color2 = getThemedColor(cell.isDrawSelectionBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText);
        } else {
            color2 = getThemedColor(cell.isDrawSelectionBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText);
        }
        pollAddOptionFieldLayout.setColor(color2);
        pollAddOptionFieldLayout.setCellToWatch(cell);
        pollAddOptionFieldLayout.setEmojiKeyboardVisible(false, false);
        pollAddOptionFieldLayout.setAnimatedVisibility(animatorPollAddAnswerVisibility.getFloatValue());
        pollAddOptionFieldLayout.doOnEmojiClick(this::pollAddOptionModeToggleEmoji);
        pollAddOptionFieldLayout.doOnCancel(this::pollAddOptionModeClose);

        final EditTextBoldCursor textView = pollAddOptionFieldLayout.textView;
        textView.setOnKeyListener((v, keyCode, event) -> {
            EditTextBoldCursor field = (EditTextBoldCursor) v;
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && field.length() == 0) {
                pollAddOptionModeClose();
                return true;
            }
            return false;
        });
        textView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (pollAddOptionFieldLayout != null && pollAddOptionFieldLayout.cellToWatch != null) {
                    pollAddOptionModeComplete(pollAddOptionFieldLayout.cellToWatch);
                    return true;
                }
            }
            return false;
        });

        chatActivityEnterView.overrideEditTextView(textView);
        AndroidUtilities.runOnUIThread(() -> {
            chatListView.smoothScrollBy(0, scrollBy);
            if (!AndroidUtilities.showKeyboard(textView)) {
                textView.clearFocus();
                textView.requestFocus();
            }
            AndroidUtilities.runOnUIThread(() -> AndroidUtilities.showKeyboard(textView), 100);
        }, 100);

        animatorPollAddAnswerVisibility.setValue(true, true);
    }

    private void pollAddOptionModeToggleEmoji() {
        if (chatActivityEnterView == null || pollAddOptionFieldLayout == null) {
            return;
        }

        if (chatActivityEnterView.isPopupShowing()) {
            chatActivityEnterView.hidePopup(false);
            AndroidUtilities.showKeyboard(pollAddOptionFieldLayout.textView);
            pollAddOptionFieldLayout.setEmojiKeyboardVisible(false, true);
        } else {
            chatActivityEnterView.setAllowStickersAndGifs(true, false, false);
            chatActivityEnterView.showEmojiView();
            pollAddOptionFieldLayout.setEmojiKeyboardVisible(true, true);
        }
    }

    private void pollAddOptionModeComplete(ChatMessageCell cell) {
        if (!isInPollAddOptionMode() || pollAddOptionFieldLayout == null || pollAddOptionFieldLayout.cellToWatch != cell) {
            return;
        }

        final CharSequence pollOptionText = pollAddOptionFieldLayout.textView.getText();
        final int pollOptionLength = pollOptionText.length();
        if (pollOptionLength == 0 || pollOptionLength > getMessagesController().config.pollAnswerLengthMax.get()) {
            AndroidUtilities.shakeView(pollAddOptionFieldLayout.textView);
            return;
        }

        SendMessagesHelper.getInstance(currentAccount).addPollOption(
            cell.getMessageObject(), pollOptionText,
            pollAddOptionFieldLayout.getAttachedMedia());
        pollAddOptionModeClose();
    }

    private void pollAddOptionModeClose() {
        animatorPollAddAnswerVisibility.setValue(false, true);
        if (pollAddOptionFieldLayout != null) {
            pollAddOptionFieldLayout.updateCell();
        }
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setAllowStickersAndGifs(true, true, true, false);
            chatActivityEnterView.overrideEditTextView(null);
        }
    }

    private void pollAddOptionModeDestroy() {
        if (pollAddOptionFieldLayout != null) {
            contentView.removeView(pollAddOptionFieldLayout);
            pollAddOptionFieldLayout = null;
        }
        if (chatActivityEnterView != null) {
            chatActivityEnterView.openKeyboard();
        }
    }

    private boolean isInPollAddOptionMode() {
        return animatorPollAddAnswerVisibility.getValue();
    }


    public void startFireworks() {
        if (fireworksOverlay == null || fireworksOverlay.isStarted()) {
            return;
        }
        fireworksOverlay.start();
        try {
            if (!NekoConfig.disableVibration.Bool()) fireworksOverlay.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        } catch (Exception ignored) {};
    }

    private float getTopicTabsSideSize(TopicsTabsView.Position position) {
        return topicsTabs != null ? topicsTabs.getTabsVisibleSpaceWithPadding(position, dp(7)) : 0;
    }

    private OnPostDrawView invalidateBlurredSourcesView;

    private static final int BLUR_INVALIDATE_FLAG_SCROLL = 1;
    private static final int BLUR_INVALIDATE_FLAG_POSITIONS = 1 << 1;
    private static final int BLUR_INVALIDATE_FLAG_CLIP = 1 << 2;

    private void invalidateMergedVisibleBlurredPositionsAndSourcesPositions() {
        invalidateMergedVisibleBlurredPositionsAndSources(BLUR_INVALIDATE_FLAG_POSITIONS);
    }

    private void invalidateMergedVisibleBlurredPositionsAndSources(int flags) {
        if (parentChatActivity != null) {
            parentChatActivity.invalidateMergedVisibleBlurredPositionsAndSources(flags);
        }
        invalidateGlassSource();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || scrollableViewNoiseSuppressor == null) {
            return;
        }

        invalidateBlurredSourcesView.invalidate(flags);
    }

    private final ArrayList<RectF> glassDrawablesPositions = new ArrayList<>();
    private final ArrayList<RectF> glassDrawablesPositionsMerged = new ArrayList<>();
    private int glassDrawablesPositionsCount;

    private void invalidateMergedVisibleBlurredPositionsAndSourcesImpl(int flags) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || scrollableViewNoiseSuppressor == null) {
            return;
        }

        if (BitwiseUtils.hasFlag(flags, BLUR_INVALIDATE_FLAG_CLIP)) {
            invalidateClipRectForBackgroundAndChatList();
        }

        if (BitwiseUtils.hasFlag(flags, BLUR_INVALIDATE_FLAG_POSITIONS)) {
            glassDrawablesPositionsCount = getMergedVisibleBlurredPositions(glassDrawablesPositionsMerged);
            scrollableViewNoiseSuppressor.setupRenderNodes(glassDrawablesPositionsMerged, glassDrawablesPositionsCount);
        }

        //if (BitwiseUtils.hasFlag(flags, BLUR_INVALIDATE_FLAG_POSITIONS | BLUR_INVALIDATE_FLAG_SCROLL)) {
        final boolean hasChanges = scrollableViewNoiseSuppressor.invalidateResultRenderNodes(contentView::drawList, contentView.getWidth(), contentView.getHeight());
        if (hasChanges) {
            if (glassBackgroundSourceRenderNode != null) {
                glassBackgroundSourceRenderNode.invalidateDisplayListForDrawables();
            }
            if (glassBackgroundSourceFrostedRenderNode != null) {
                glassBackgroundSourceFrostedRenderNode.invalidateDisplayListForDrawables();
            }
            if (actionBar != null) {
                actionBar.invalidate();
            }
            invalidateAllGlassAttachedViews();
        }

        //}
    }

    private int getMergedVisibleBlurredPositions(List<RectF> positions) {
        final int positionsCount = getVisibleBlurredPositions(glassDrawablesPositions);
        final int mergedPositionsCount = RectFMergeBounding.mergeOverlapping(glassDrawablesPositions, positionsCount, positions);
        final int maxX = contentView.getMeasuredWidth();
        for (int a = 0; a < mergedPositionsCount; a++) {
            final RectF position = positions.get(a);
            position.left = androidx.core.math.MathUtils.clamp(position.left, 0, maxX);
            position.top = Math.max(chatListView.getY(), position.top);
            position.right = androidx.core.math.MathUtils.clamp(position.right, 0, maxX);
            position.bottom = Math.min(chatListView.getY() + chatListView.getMeasuredHeight(), position.bottom);
            /*if (drawDebug) {
                ((Canvas) null).drawRect(position, Theme.DEBUG_GREEN_STROKE);
            }*/
        }

        return mergedPositionsCount;
    }

    private int getVisibleBlurredPositions(List<RectF> positions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int count = 0;

            if (glassBackgroundSourceFrostedRenderNode != null) {
                final int blurAlpha = ACTION_BAR_BLUR_ALPHA;
                if (blurAlpha < 255) {
                    final RectF rectf;
                    if (positions.isEmpty()) {
                        positions.add(rectf = new RectF());
                    } else {
                        rectf = positions.get(0);
                    }

                    rectf.set(0, 0, contentView.getMeasuredWidth(), chatListView.getPaddingTop() + chatListView.getY());
                    rectf.inset(0, -dp(45));
                    count += 1;
                }

                count += glassBackgroundSourceFrostedRenderNode.getVisiblePositions(positions, count, dp(48));
            }

            if (glassBackgroundSourceRenderNode != null) {
                count += glassBackgroundSourceRenderNode.getVisiblePositions(positions, count, dp(8));
            }

            return count;
        } else {
            return 0;
        }
    }



    private final Rect clipBoundsTmp = new Rect();
    private void invalidateClipRectForBackgroundAndChatList() {
        if (contentView == null) {
            return;
        }

        final boolean hasSideInsets = insetSystemLeft > 0 || insetSystemRight > 0;

        final int topClip = 0;
        final int bottomClip = (int) Math.max(0,
            windowInsetsStateHolder.getAnimatedImeBottomInset() * windowInsetsStateHolder.getAnimatedKeyboardVisibility() - dp(29));

        if (contentView.backgroundView != null) {
            clipBoundsTmp.set(0, topClip, contentView.getMeasuredWidth(), contentView.getMeasuredHeight() - bottomClip);
            contentView.backgroundView.setClipBounds(clipBoundsTmp);
        }
        if (chatListView != null) {
            clipBoundsTmp.set(0, topClip, contentView.getMeasuredWidth(), contentView.getMeasuredHeight() - bottomClip);
            clipBoundsTmp.offset(0, -chatListView.getTop());
            chatListView.setClipBounds(chatListView.hasActiveEdgeEffects() ? null : clipBoundsTmp);
        }
        if (chatActivityFadeView != null) {
            final int bottomClip2 = (int) Math.max(0,
                Math.min(windowInsetsStateHolder.getInAppKeyboardHeight(), windowInsetsStateHolder.getAnimatedImeBottomInset() * windowInsetsStateHolder.getAnimatedKeyboardVisibility()) - dp(29));

            clipBoundsTmp.set(0, 0, contentView.getMeasuredWidth(), contentView.getMeasuredHeight() - bottomClip2);
            chatActivityFadeView.setClipBounds(hasSideInsets ? null : clipBoundsTmp);
        }
    }

    private void sendDebugRichMessage() {
        TLRPC.WebPage src = ArticleViewer.debugCopiedRichMessageWebPage;
        if (src == null || src.cached_page == null) {
            BulletinFactory.of(this).createErrorBulletin("No rich message copied").show();
            return;
        }
        SendMessagesHelper.prepareSendingArticle(
            getAccountInstance(),
            new ArrayList<>(src.cached_page.blocks),
            src.cached_page.rtl,
            dialog_id,
            replyingMessageObject,
            getThreadMessage(),
            true, 0, 0, null, 0, getSendMonoForumPeerId(), 0
        );
    }

    private abstract class ChatListRecyclerView extends RecyclerListViewInternal {

        public ChatListRecyclerView(Context context, ThemeDelegate themeDelegate) {
            super(context, themeDelegate);
        }

        void drawChatBackgroundElements(Canvas canvas) {
            drawChatBackgroundElements(canvas, null);
        }

        void drawChatForegroundElements(Canvas canvas) {
            drawChatForegroundElements(canvas, null);
        }

        abstract void drawChatBackgroundElements(Canvas canvas, @Nullable RectF position);
        abstract void drawChatForegroundElements(Canvas canvas, @Nullable RectF position);
    }
}
