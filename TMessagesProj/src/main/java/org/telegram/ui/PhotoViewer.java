Warning: truncated output (original token count: 308982)
... 187349 bytes omitted ...

/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.AndroidUtilities.dpf2;
import static org.telegram.messenger.AndroidUtilities.lerp;
import static org.telegram.messenger.LocaleController.getString;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.FloatProperty;
import android.util.Pair;
import android.util.Property;
import android.util.Range;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import org.telegram.ui.recyclerview.LinearSmoothScrollerEnd;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.jetbrains.annotations.NotNull;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileStreamLoadOperation;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageSuggestionParams;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.utils.PhotoUtilities;
import org.telegram.messenger.camera.Size;
import org.telegram.messenger.chromecast.ChromecastController;
import org.telegram.messenger.chromecast.ChromecastMedia;
import org.telegram.messenger.chromecast.ChromecastMediaVariations;
import org.telegram.messenger.pip.source.IPipSourceDelegate;
import org.telegram.messenger.pip.utils.PipPermissions;
import org.telegram.messenger.pip.PipSource;
import org.telegram.messenger.pip.utils.PipUtils;
import org.telegram.messenger.utils.WindowVisibilityManager;
import org.telegram.messenger.video.OldVideoPlayerRewinder;
import org.telegram.messenger.video.VideoAds;
import org.telegram.messenger.video.VideoFramesRewinder;
import org.telegram.messenger.video.VideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSlider;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedFileNative;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CaptionPhotoViewer;
import org.telegram.ui.Components.CastMediaRouteButton;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.Crop.CropView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditCoverButton;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.IntSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.GestureDetector2;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LivePhotoButton;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.MuteDrawable;
import org.telegram.ui.Components.OtherDocumentPlaceholderDrawable;
import org.telegram.ui.Components.Paint.Views.LPhotoPaintView;
import org.telegram.ui.Components.Paint.Views.MaskPaintView;
import org.telegram.ui.Components.Paint.Views.StickerCutOutBtn;
import org.telegram.ui.Components.Paint.Views.StickerMakerView;
import org.telegram.ui.Components.Paint.Views.StickerMakerBackgroundView;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoViewerCoverEditor;
import org.telegram.ui.Components.PhotoViewerPollAttachButtons;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.PipVideoOverlay;
import org.telegram.ui.Components.PlayPauseDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RectOld;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SeekSpeedDrawable;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.SpeedIconDrawable;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.Components.Tooltip;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.VideoCompressButton;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayerSeekBar;
import org.telegram.ui.Components.VideoSeekPreviewImage;
import org.telegram.ui.Components.VideoTimelinePlayView;
import org.telegram.ui.Components.ViewHelper;
import org.telegram.ui.Components.blur3.Blur3HashImpl;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawableSource;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSource;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.Components.voip.AnimatedFileInfo;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.recorder.CaptionContainerView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.KeyboardNotifier;
import org.telegram.ui.Stories.recorder.StoryEntry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import kotlin.Unit;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.helpers.ChatsHelper;
import com.radolyn.ayugram.AyuForward;
import tw.nekomimi.nekogram.translate.Translator;
import tw.nekomimi.nekogram.translate.TranslatorKt;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.AndroidUtil;
import tw.nekomimi.nekogram.utils.ProxyUtil;
import com.radolyn.ayugram.controllers.AyuGhostController;
import xyz.nextalone.nagram.NaConfig;
import tw.nekomimi.nekogram.helpers.MessageHelper;
import tw.nekomimi.nekogram.streaming.MediaStreamingProvider;

import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.core.reference.ReferenceList;

@SuppressLint("WrongConstant")
@SuppressWarnings("unchecked")
public class PhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector2.OnGestureListener, GestureDetector2.OnDoubleTapListener, IPipSourceDelegate, FactorAnimator.Target {

    private static final boolean centerTitle = NaConfig.INSTANCE.getCenterActionBarTitle().Bool();
    private static final int ANIMATOR_ID_POLL_ATTACH_BUTTONS_VISIBLE = 0;
    private final BoolAnimator animatorPollAttachButtonsVisibility = new BoolAnimator(ANIMATOR_ID_POLL_ATTACH_BUTTONS_VISIBLE, this, CubicBezierInterpolator.EASE_OUT_QUINT, 380);
    private final static float ZOOM_SCALE = 0.1f;
    private final static int MARK_DEFERRED_IMAGE_LOADING = 1;

    private boolean ALLOW_USE_SURFACE = Build.VERSION.SDK_INT >= 30;

    private int classGuid;
    private PhotoViewerProvider placeProvider;
    private boolean isVisible;
    private boolean isVisibleOrAnimating;
    private int maxSelectedPhotos = -1;
    private boolean allowOrder = true;

    private boolean muteVideo;

    private boolean isUnalivePhoto() {
        if (sendPhotoType == SELECT_TYPE_STICKER) return true;
        if (currentIndex < 0 || currentIndex >= imagesArrLocals.size()) return false;
        if (placeProvider != null && !placeProvider.allowLivePhotos()) return true;
        Object obj = imagesArrLocals.get(currentIndex);
        if (obj instanceof MediaController.PhotoEntry)
            return ((MediaController.PhotoEntry) obj).isUnalivePhoto();
        return false;
    }
    private void setUnalivePhoto(boolean unalive) {
        if (currentIndex < 0 || currentIndex >= imagesArrLocals.size()) return;
        Object obj = imagesArrLocals.get(currentIndex);
        if (obj instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) obj).discardLivePhoto = unalive;

            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)
                .edit().putBoolean("photoLiveDefault", SharedConfig.photoLiveDefault = !unalive).apply();
            if (placeProvider != null) {
                placeProvider.updatedLivePhotos();
            }
        }
    }

    private boolean inBubbleMode;

    private int slideshowMessageId;
    private String nameOverride;
    private int dateOverride;

    private AnimatorSet miniProgressAnimator;
    private final Runnable miniProgressShowRunnable = () -> toggleMiniProgressInternal(true);

    private Activity parentActivity;
    private Context activityContext;

    private ActionBar actionBar;
    private ImageView actionBarBackButton;
    private Drawable actionBarBackButtonDrawableDeafult;
    private Drawable actionBarBackButtonDrawableGlass;
    private boolean isActionBarVisible = true;
    private boolean isPhotosListViewVisible;
    private AnimatorSet actionBarAnimator;
    private PhotoViewerActionBarContainer actionBarContainer;
    private PhotoCountView countView;
    public boolean closePhotoAfterSelect = true;
    public boolean closePhotoAfterSelectWithAnimation = false;
    private TextSelectionHelper.SimpleTextSelectionHelper textSelectionHelper;
    private boolean firstFrameRendered;
    private Paint surfaceBlackoutPaint;

    public TextureView getVideoTextureView() {
        return videoTextureView;
    }

    public boolean isVisibleOrAnimating() {
        return isVisibleOrAnimating;
    }

    public SurfaceView getVideoSurfaceView() {
        return videoSurfaceView;
    }

    private static class PhotoViewerActionBarContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

        private FrameLayout container;
        private FrameLayout titleLayout;
        SimpleTextView[] titleTextView;
        AnimatedTextView subtitleTextView;

        public PhotoViewerActionBarContainer(Context context) {
            super(context);

            container = new FrameLayout(context);
            container.setPadding(centerTitle ? 0 : dp((AndroidUtilities.isTablet() ? 80 : 72) - 16), 0, 0, 0);
            addView(container, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));

            titleLayout = new FrameLayout(context) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    setPivotY(getMeasuredHeight());
                }
            };
            titleLayout.setPivotX(dp(16));
            titleLayout.setPadding(centerTitle ? 0 : dp(16), centerTitle ? AndroidUtilities.dp(17) : 0, 0, 0);
            titleLayout.setClipToPadding(false);
            container.addView(titleLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));

            titleTextView = new SimpleTextView[2];
            for (int i = 0; i < 2; ++i) {
                titleTextView[i] = new SimpleTextView(context);
                titleTextView[i].setGravity(centerTitle ? Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL : Gravity.LEFT | Gravity.CENTER_VERTICAL);
                titleTextView[i].setTextColor(0xffffffff);
                titleTextView[i].setTextSize(centerTitle ? 18 : 20);
                titleTextView[i].setTypeface(AndroidUtilities.bold());
                titleTextView[i].setDrawablePadding(dp(4));
                titleTextView[i].setScrollNonFitText(true);
                titleLayout.addView(titleTextView[i], LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, centerTitle ? Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL : Gravity.LEFT | Gravity.CENTER_VERTICAL, centerTitle ? 96 : 0, 0, centerTitle ? 96 : 0, 0));
            }

            subtitleTextView = new AnimatedTextView(context, true, false, false);
            subtitleTextView.setAnimationProperties(.4f, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
            subtitleTextView.setTextSize(dp(14));
            subtitleTextView.setGravity(centerTitle ? Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL : Gravity.LEFT | Gravity.CENTER_VERTICAL);
            subtitleTextView.setTextColor(0xffffffff);
            subtitleTextView.setEllipsizeByGradient(true);
            subtitleTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            subtitleTextView.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
            container.addView(subtitleTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, centerTitle ? Gravity.CENTER_HORIZONTAL | Gravity.TOP : Gravity.LEFT | Gravity.TOP, centerTitle ? 0 : 16, 0, 0, 0));
        }

        public void setTextShadows(boolean applyShadows) {
            titleTextView[0].getPaint().setShadowLayer(dpf2(0.66f), 0, 1, applyShadows ? 0x72000000 : 0);
            titleTextView[1].getPaint().setShadowLayer(dpf2(0.66f), 0, 1, applyShadows ? 0x72000000 : 0);
            subtitleTextView.getDrawable().setShadowLayer(dpf2(0.66f), 0, 1, applyShadows ? 0x72000000 : 0);
        }

        public void setTitle(CharSequence title) {
            titleTextView[1].setAlpha(0);
            titleTextView[1].setVisibility(View.GONE);

            if (!areStringsEqual(titleTextView[0].getText(), title)) {
                titleTextView[0].resetScrolling();
            }

            titleTextView[0].setText(title);
            titleTextView[0].setAlpha(1);
            titleTextView[0].setTranslationX(0);
            titleTextView[0].setTranslationY(0);
        }

        public CharSequence getTitle() {
            return titleTextView[0].getText();
        }

        private AnimatorSet titleAnimator;

        private boolean areStringsEqual(CharSequence a, CharSequence b) {
            if (a == null && b == null) {
                return true;
            }
            if ((a == null) != (b == null)) {
                return false;
            }
            return TextUtils.equals(a.toString(), b.toString());
        }

        // axis: true = top-bottom, false = left-right
        // direction: true = from top/from left, false = from bottom/from right
        public void setTitleAnimated(CharSequence title, boolean axis, boolean direction) {
            if (areStringsEqual(titleTextView[0].getText(), title)) {
                return;
            }

            if (titleAnimator != null) {
                titleAnimator.cancel();
                titleAnimator = null;
            }

            titleTextView[1].copyScrolling(titleTextView[0]);
            titleTextView[1].setText(titleTextView[0].getText());
            titleTextView[1].setRightPadding((int) rightPadding);
            titleTextView[0].resetScrolling();
            titleTextView[0].setText(title);

            float amplitude = dp(8) * (direction ? 1 : -1);

            titleTextView[1].setTranslationX(0);
            titleTextView[1].setTranslationY(0);
            if (axis) {
                titleTextView[0].setTranslationX(0);
                titleTextView[0].setTranslationY(-amplitude);
            } else {
                titleTextView[0].setTranslationX(-amplitude);
                titleTextView[0].setTranslationY(0);
            }

            titleTextView[0].setAlpha(0);
            titleTextView[1].setAlpha(1);
            titleTextView[0].setVisibility(View.VISIBLE);
            titleTextView[1].setVisibility(View.VISIBLE);

            ArrayList<Animator> arrayList = new ArrayList<>();
            arrayList.add(ObjectAnimator.ofFloat(titleTextView[1], View.ALPHA, 0));
            arrayList.add(ObjectAnimator.ofFloat(titleTextView[0], View.ALPHA, 1));
            arrayList.add(ObjectAnimator.ofFloat(titleTextView[1], axis ? View.TRANSLATION_Y : View.TRANSLATION_X, amplitude));
            arrayList.add(ObjectAnimator.ofFloat(titleTextView[0], axis ? View.TRANSLATION_Y : View.TRANSLATION_X, 0));
            titleAnimator = new AnimatorSet();
            titleAnimator.playTogether(arrayList);
            titleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (titleAnimator == animation) {
                        titleTextView[1].setVisibility(View.GONE);
                        titleAnimator = null;
                    }
                }
            });
            titleAnimator.setDuration(320);
            titleAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            titleAnimator.start();
        }

        private AnimatorSet subtitleAnimator;
        private boolean hasSubtitle;
        public void setSubtitle(CharSequence subtitle) {
            setSubtitle(subtitle, true);
        }
        public void setSubtitle(CharSequence subtitle, boolean animated) {
            final boolean haveSubtitle = !TextUtils.isEmpty(subtitle);
            if (haveSubtitle != hasSubtitle) {
                hasSubtitle = haveSubtitle;

                if (subtitleAnimator != null) {
                    subtitleAnimator.cancel();
                }

                final boolean isLandscape = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
                final int subtitleTranslation = dp((haveSubtitle ? 30 : 33) - (isLandscape ? (AndroidUtilities.isTablet() ? -4 : 6) : 0) + (centerTitle ? 3 : 0));

                if (animated) {
                    ArrayList<Animator> arrayList = new ArrayList<>();
                    arrayList.add(ObjectAnimator.ofFloat(subtitleTextView, View.ALPHA, haveSubtitle ? 1 : 0));
                    arrayList.add(ObjectAnimator.ofFloat(subtitleTextView, View.TRANSLATION_Y, subtitleTranslation));
                    arrayList.add(ObjectAnimator.ofFloat(titleLayout, View.TRANSLATION_Y, haveSubtitle ? dp(-12 - (centerTitle ? 3 : 0)) : 0));
                    arrayList.add(ObjectAnimator.ofFloat(titleLayout, View.SCALE_X, haveSubtitle && !centerTitle ? .87f : 1));
                    arrayList.add(ObjectAnimator.ofFloat(titleLayout, View.SCALE_Y, haveSubtitle && !centerTitle ? .87f : 1));
                    subtitleAnimator = new AnimatorSet();
                    subtitleAnimator.playTogether(arrayList);
                    subtitleAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    subtitleAnimator.start();
                } else {
                    subtitleTextView.setAlpha(haveSubtitle ? 1 : 0);
                    subtitleTextView.setTranslationY(subtitleTranslation);
                    titleLayout.setTranslationY(haveSubtitle ? dp(-12 - (centerTitle ? 3 : 0)) : 0);
                    titleLayout.setScaleX(haveSubtitle && !centerTitle ? .87f : 1);
                    titleLayout.setScaleY(haveSubtitle && !centerTitle ? .87f : 1);
                }
            }
            subtitleTextView.setText(subtitle, animated);
            subtitleTextView.setContentDescription(TextUtils.isEmpty(subtitle) ? null : subtitle);
        }

        public void updateOrientation() {
            hasSubtitle = !hasSubtitle;
            setSubtitle(subtitleTextView.getText(), false);
        }

        public void updateRightPadding(int rightPadding) {
            updateRightPadding(rightPadding, true);
        }

        private ValueAnimator rightPaddingAnimator;
        private float rightPadding;
        public void updateRightPadding(float rightPadding, boolean animated) {
            if (centerTitle) {
                return;
            }
            if (rightPaddingAnimator != null) {
                rightPaddingAnimator.cancel();
                rightPaddingAnimator = null;
            }
            if (animated) {
                rightPaddingAnimator = ValueAnimator.ofFloat(this.rightPadding, rightPadding);
                rightPaddingAnimator.addUpdateListener(anm -> {
                    this.rightPadding = (float) anm.getAnimatedValue();
                    titleTextView[0].setRightPadding((int) rightPadding);
                    titleTextView[1].setRightPadding((int) rightPadding);
                    subtitleTextView.setRightPadding(rightPadding);
                });
                rightPaddingAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        updateRightPadding(rightPadding, false);
                    }
                });
                rightPaddingAnimator.setDuration(320);
                rightPaddingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                rightPaddingAnimator.start();
            } else {
                this.rightPadding = rightPadding;
                titleTextView[0].setRightPadding((int) rightPadding);
                subtitleTextView.setRightPadding(rightPadding);
            }
        }

        int lastHeight;

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int t = AndroidUtilities.statusBarHeight;
            if (lastHeight != AndroidUtilities.displaySize.y) {
                lastHeight = AndroidUtilities.displaySize.y;
                updateOrientation();
            }
            container.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height - t, MeasureSpec.EXACTLY));
            setMeasuredDimension(width, height);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int t = AndroidUtilities.statusBarHeight;
            container.layout(0, t, right - left, bottom - top);
        }

        @Override
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.emojiLoaded) {
                titleTextView[0].invalidate();
                titleTextView[1].invalidate();
                subtitleTextView.invalidate();
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        }
    }

    private static class PhotoCountView extends View {

        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        AnimatedTextView.AnimatedTextDrawable left;
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        StaticLayout center;
        float centerWidth, centerTop;
        AnimatedTextView.AnimatedTextDrawable right;
        private String lng;

        public PhotoCountView(Context context) {
            super(context);

            backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);

            left = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
            left.setAnimationProperties(.3f, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
            left.setTextColor(0xffffffff);
            left.setTextSize(dp(14));
            left.setTypeface(AndroidUtilities.bold());
            left.setCallback(this);
            left.setText("0");
            left.setOverrideFullWidth(AndroidUtilities.displaySize.x);

            paint.setColor(0xffffffff);
            paint.setTextSize(dp(14));
            paint.setTypeface(AndroidUtilities.bold());
            setCenterText();

            right = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
            right.setAnimationProperties(.3f, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
            right.setTextColor(0xffffffff);
            right.setTextSize(dp(14));
            right.setTypeface(AndroidUtilities.bold());
            right.setCallback(this);
            right.setText("0");
            right.setOverrideFullWidth(AndroidUtilities.displaySize.x);
        }

        private void setCenterText() {
            center = new StaticLayout(getOf(), paint, dp(200), Layout.Alignment.ALIGN_CENTER, 1, 0, false);
            if (center.getLineCount() >= 1) {
                centerWidth = center.getLineWidth(0);
                centerTop = center.getLineDescent(0);
            } else {
                centerWidth = 0;
                centerTop = 0;
            }
        }

        private String getOf() {
            lng = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
            String text = getString(R.string.Of); // %1$d of %2$d
            text = text.replace("%1$d", "");
            text = text.replace("%2$d", "");
            return text;
        }

        public void set(int left, int right) {
            set(left, right, true);
        }

        public void set(int left, int right, boolean animated) {
            left = Math.max(0, left);
            right = Math.max(left, right);
            if (
                LocaleController.getInstance().getCurrentLocaleInfo() != null &&
                !TextUtils.equals(lng, LocaleController.getInstance().getCurrentLocaleInfo().shortName)
            ) {
                setCenterText();
            }
            this.left.setText(String.format("%d", (LocaleController.isRTL ? right : left)), animated && !nextNotAnimate && !LocaleController.isRTL);
            this.right.setText(String.format("%d", (LocaleController.isRTL ? left : right)), animated && !nextNotAnimate && !LocaleController.isRTL);
            nextNotAnimate = !animated;
        }

        @Override
        protected boolean verifyDrawable(@NonNull Drawable who) {
            return left == who || right == who || super.verifyDrawable(who);
        }

        private boolean shown = false;
        private AnimatedFloat showT = new AnimatedFloat(this, 0, 350, CubicBezierInterpolator.EASE_OUT_QUINT);
        private boolean nextNotAnimate;

        public void updateShow(boolean show, boolean animated) {
            if (NaConfig.INSTANCE.getHidePhotoCounter().Bool()) {
                show = false;
            }
            if (shown != show) {
                shown = show;
                if (!show) {
                    nextNotAnimate = true;
                }
                if (!animated) {
                    showT.set(show ? 1 : 0, true);
                }
                invalidate();
            }
        }

        public boolean isShown() {
            return shown;
        }

        private int marginTop;

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float show = this.showT.set(shown ? 1 : 0);

            if (show <= 0) {
                return;
            }

            float width = left.getCurrentWidth() + centerWidth + right.getCurrentWidth() + dp(9 + 9);
            float marginTop = this.marginTop + (1f - show) * -dp(8);

            AndroidUtilities.rectTmp.set(
                (getWidth() - width) / 2f,
                marginTop + dpf2(10),
                (getWidth() + width) / 2f,
                marginTop + dpf2(10 + 23)
            );
            int wasAlpha = backgroundPaint.getAlpha();
            backgroundPaint.setAlpha((int) (wasAlpha * show));
            canvas.drawRoundRect(
                AndroidUtilities.rectTmp,
                dpf2(12),
                dpf2(12),
                backgroundPaint
            );
            backgroundPaint.setAlpha(wasAlpha);

            canvas.save();
            canvas.translate((getWidth() - width) / 2f + dp(9), marginTop + dp(9.5f));
            left.setBounds(0, 0, (int) left.getCurrentWidth(), dp(23));
            left.setAlpha((int) (0xFF * show));
            left.draw(canvas);

            canvas.translate(left.getCurrentWidth(), 0);
            canvas.save();
            canvas.translate(-(center.getWidth() - centerWidth) / 2f, (dp(23) - center.getHeight() + centerTop / 2f) / 2f);
            paint.setAlpha((int) (0xFF * show));
            center.draw(canvas);
            canvas.restore();

            canvas.translate(centerWidth, 0);
            right.setBounds(0, 0, (int) right.getCurrentWidth(), dp(23));
            right.setAlpha((int) (0xFF * show));
            right.draw(canvas);
            canvas.restore();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            marginTop = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
            left.setOverrideFullWidth(width);
            right.setOverrideFullWidth(width);
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(marginTop + dp(10 + 23 + 10), MeasureSpec.EXACTLY)
            );
        }
    }

    private int currentAccount;

    private static final int PROGRESS_NONE = -1;
    private static final int PROGRESS_EMPTY = 0;
    private static final int PROGRESS_CANCEL = 1;
    private static final int PROGRESS_LOAD = 2;
    private static final int PROGRESS_PLAY = 3;
    private static final int PROGRESS_PAUSE = 4;
    private static Drawable[] progressDrawables;

    public static final int EDIT_MODE_NONE = 0;
    public static final int EDIT_MODE_CROP = 1;
    public static final int EDIT_MODE_FILTER = 2;
    public static final int EDIT_MODE_PAINT = 3;
    public static final int EDIT_MODE_STICKER_MASK = 4;
    public static final int EDIT_MODE_COVER = 5;

    private int videoWidth, videoHeight;
    private float inlineOutAnimationProgress;

    private BlurredBackgroundSource blurredBackgroundSource;
    private BlurredBackgroundDrawableViewFactory iBlur3FactoryFrostedLiquidGlass;
    private ReferenceList<BlurredBackgroundDrawableRenderNode> iBlur3BlurredDrawables = new ReferenceList<>();
    private ReferenceList<View> glassAttachedViews;

    public BlurringShader.BlurManager blurManager;
    private BlurringShader.StoryBlurDrawer shadowBlurer;
    private WindowManager.LayoutParams windowLayoutParams;
    private boolean windowColorModeHdr;
    private Boolean windowDisplayHdrCapable;
    private FrameLayoutDrawer containerView;
    private PhotoViewerWebView photoViewerWebView;
    public FrameLayout windowView;
    private ClippingImageView animatingImageView;
    private FrameLayout bottomLayout;
    private View navigationBar;
    private int navigationBarHeight;
    private TextView docNameTextView;
    private TextView docInfoTextView;
    private TextView doneButtonFullWidth;
    private ActionBarMenuItem menuItem;
    private ActionBarMenuItem videoItem;
    private ActionBarMenuSubItem allMediaItem;
    private ActionBarMenuSlider.SpeedSlider speedItem;
    private ActionBarMenuSubItem loopItem;
    private ActionBarMenuSubItem galleryButton;
    private ActionBarPopupWindow.GapView galleryGap;
    private ActionBarMenuSubItem pipItem;
    private ChooseQualityLayout.QualityIcon videoItemIcon;
    private LinearLayout videoQualityLayout;
    private final ArrayList<ActionBarMenuSubItem> videoQualityItems = new ArrayList<>();
    private ActionBarPopupWindow.GapView speedGap;
    private ActionBarMenu menu;
    private ActionBarMenuItem sendItem;
    private ActionBarMenuItem editItem;
    private ActionBarMenuItem masksItem;
    private ActionBarMenuItem deleteItem;
    private ActionBarMenuSubItem castItem;
    private CastMediaRouteButton castItemButton;
    private LinearLayout itemsLayout;
    private SpeedButtonsLayout chooseSpeedLayout;
    private ChooseDownloadQualityLayout chooseDownloadQualityLayout;
    private Map<View, Boolean> actionBarItemsVisibility = new HashMap<>(3);
    private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(0xff000000);
    private Paint blackPaint = new Paint();
    private CheckBox checkImageView;
    private CounterView photosCounterView;
    private FrameLayout pickerView;
    private FrameLayout topBulletinUnderCaption;
    private FrameLayout bottomBulletinUnderCaption;
    private ChatActivityEnterView.SendButton pickerViewSendButton;
    private PhotoViewerPollAttachButtons pollAttachButtons;
    private PickerBottomLayoutViewer editorDoneLayout;
    private TextView resetButton;
    private PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
    private RadialProgressView miniProgressView;
    private ImageView paintItem;
    private ImageView cropItem;
    private ImageView mirrorItem;
    private ImageView rotateItem;
    private ImageView tuneItem;
    private MuteDrawable muteDrawable;
    private ImageView muteButton;
    private LivePhotoButton livePhotoButton;
    private EditCoverButton editCoverButton;
    private ArrayList<HintView2> muteHints;
    private ArrayList<HintView2> livePhotoHints;
    private VideoCompressButton compressItem;
    private HintView2 compressPhotoHint;
    private GroupedPhotosListView groupedPhotosListView;
    private Tooltip tooltip;
    private UndoView hintView;
    private SelectedPhotosListView selectedPhotosListView;
    private ListAdapter selectedPhotosAdapter;
    private AnimatorSet compressItemAnimation;
    private ImageReceiver sideImage;
    private boolean isCurrentVideo;

    private Runnable onUserLeaveHintListener = this::onUserLeaveHint;

    private float currentVideoSpeed;

    private long lastPhotoSetTime;

    private GradientDrawable[] pressedDrawable = new GradientDrawable[2];
    private boolean[] drawPressedDrawable = new boolean[2];
    private float[] pressedDrawableAlpha = new float[2];
    private int touchSlop;

    private VideoForwardDrawable videoForwardDrawable;
    private SeekSpeedDrawable seekSpeedDrawable;

    private AnimatorSet currentListViewAnimation;
    private PhotoCropView photoCropView;
    private CropTransform cropTransform = new CropTransform();
    private CropTransform leftCropTransform = new CropTransform();
    private CropTransform rightCropTransform = new CropTransform();
    private MediaController.CropState leftCropState;
    private MediaController.CropState rightCropState;
    private PhotoFilterView photoFilterView;
    private AnimatorSet paintKeyboardAnimator;
    private KeyboardNotifier paintKeyboardNotifier;
    private LPhotoPaintView photoPaintView;
    private boolean maskPaintViewEraser;
    private MaskPaintView maskPaintView;
    private boolean maskPaintViewShuttingDown;
    private AlertDialog visibleDialog;
    private CaptionTextViewSwitcher captionTextViewSwitcher;
    private FrameLayout adButtonView;
    private TextView adButtonTextView;
    private CaptionScrollView captionScrollView;
    private CaptionPhotoViewer captionEdit;
    private CaptionPhotoViewer topCaptionEdit;
    private float shiftDp = -8;
    private FrameLayout captionEditContainer;
    private FrameLayout topCaptionEditContainer;
    private FrameLayout captionContainer;
    private ChatAttachAlert parentAlert;
    private WindowVisibilityManager.Controller parentAlertWindowVisibilityController;
//    private PhotoViewerCaptionEnterView captionEditText;
    private int sendPhotoType;
    private boolean sendPhotoTypeIsGif;
    private boolean sendPhotoTypeIsPollMedia;
    private boolean sendPhotoTypeIsPollMediaEdit;
    private boolean cropInitied;
    private boolean isDocumentsPicker;
    private boolean needCaptionLayout;
    private AnimatedFileDrawable currentAnimation;
    private boolean allowShare;
    private boolean openedFullScreenVideo;
    private boolean dontChangeCaptionPosition;
    private boolean captionHwLayerEnabled;
    private ImageUpdater.AvatarFor setAvatarFor;
    private boolean lastCaptionTranslating;
    private MessagesController.DialogPhotos dialogPhotos;

    public static Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private boolean pipAvailable;

    private final Rect insets = new Rect();
    private boolean padImageForHorizontalInsets;

    public boolean doneButtonPressed;
    boolean keyboardAnimationEnabled;
    private Theme.ResourcesProvider resourcesProvider;

    private boolean pausedOnPause = false;

    private Runnable setLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentMessageObject == null) {
                return;
            }
            FileLoader.getInstance(currentMessageObject.currentAccount).setLoadingVideo(currentMessageObject.getDocument(), true, false);
        }
    };

    private Runnable hideActionBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoPlayerControlVisible && isPlaying && !ApplicationLoader.mainInterfacePaused) {
                if (menuItem != null && menuItem.isSubMenuShowing() || videoItem != null && videoItem.isSubMenuShowing()) {
                    return;
                }
                if (captionScrollView != null && captionScrollView.getScrollY() != 0) {
                    return;
                }
                if (miniProgressView != null && miniProgressView.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (PipInstance == PhotoViewer.this) {
                    return;
                }
                toggleActionBar(false, true);
            }
        }
    };

    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private View flashView;
    private AnimatorSet flashAnimator;
    private TextureView videoTextureView;
    private SurfaceView videoSurfaceView;
    private boolean usedSurfaceView;
    private FirstFrameView firstFrameView;
    private VideoPlayer videoPlayer;
    private PipSource pipSource;
    private boolean manuallyPaused;
    private Runnable videoPlayRunnable;
    private boolean previousHasTransform;
    private float previousCropPx;
    private float previousCropPy;
    private float previousCropPw;
    private float previousCropPh;
    private float previousCropScale;
    private float previousCropRotation;
    private boolean previousCropMirrored;
    private int previousCropOrientation;
    private VideoPlayer injectingVideoPlayer;
    private SurfaceTexture injectingVideoPlayerSurface;
    private boolean playerInjected;
    private boolean skipFirstBufferingProgress;
    private boolean playerWasReady;
    private boolean playerWasPlaying;
    private boolean playerAutoStarted;
    private boolean playerLooping;
    private float seekToProgressPending;
    private String shouldSavePositionForCurrentVideo;
    private String shouldSavePositionForCurrentVideoShortTerm;
    private static final HashMap<String, SavedVideoPosition> savedVideoPositions = new HashMap<>();
    private long lastSaveTime;
    private float seekToProgressPending2;
    private boolean streamingAlertShown;
    private long startedPlayTime;
    private boolean keepScreenOnFlagSet;
    private VideoPlayerControlFrameLayout videoPlayerControlFrameLayout;
    private String lastControlFrameDuration;
    private Animator videoPlayerControlAnimator;
    private boolean videoPlayerControlVisible = true;
    private int[] videoPlayerCurrentTime = new int[2];
    private int[] videoPlayerTotalTime = new int[2];
    private SimpleTextView videoPlayerTime;
    private ImageView exitFullscreenButton;
    private VideoPlayerSeekBar videoPlayerSeekbar;
    private View videoPlayerSeekbarView;
    private VideoSeekPreviewImage videoPreviewFrame;
    private AnimatorSet videoPreviewFrameAnimation;
    private boolean needShowOnReady;
    private int waitingForDraw;
    public TextureView changedTextureView;
    private ImageView textureImageView;
    private ImageView[] fullscreenButton = new ImageView[3];
    private boolean allowShowFullscreenButton;
    private int[] pipPosition = new int[2];
    private boolean pipAnimationInProgress;
    private Bitmap currentBitmap;
    private Bitmap lastFrameBitmap;
    private ImageView lastFrameImageView;
    private boolean changingTextureView;
    private int waitingForFirstTextureUpload;
    private boolean textureUploaded;
    private boolean videoSizeSet;
    private boolean isInline;
    private boolean pipVideoOverlayAnimateFlag = true;
    private boolean switchingInlineMode;
    private boolean videoCrossfadeStarted;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean isPlaying;
    private boolean isStreaming;
    private boolean firstAnimationDelay;
    private long lastBufferedPositionCheck;
    private View playButtonAccessibilityOverlay;
    private StickersAlert masksAlert;
    private int lastImageId = -1;

    private OrientationEventListener orientationEventListener;
    private int prevOrientation = -10;
    private int fullscreenedByButton;
    private boolean wasRotated;

    private int keyboardSize;

    private float currentPanTranslationY;

    public final static int SELECT_TYPE_NO_SELECT = -1;
    public final static int SELECT_TYPE_AVATAR = 1;
    public final static int SELECT_TYPE_WALLPAPER = 3;
    public final static int SELECT_TYPE_QR = 10;
    public final static int SELECT_TYPE_STICKER = 11;
    public final static int SELECT_TYPE_GIF = 12;
    public final static int SELECT_TYPE_POLL_MEDIA = 13;
    public final static int SELECT_TYPE_POLL_MEDIA_EDIT = 14;

    OldVideoPlayerRewinder longVideoPlayerRewinder = new OldVideoPlayerRewinder() {
        @Override
        protected void onRewindCanceled() {
            onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0));
            videoForwardDrawable.setShowing(false);

            PipVideoOverlay.onRewindCanceled();
        }

        @Override
        protected void updateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek) {
            videoForwardDrawable.setTime(Math.abs(timeDiff));
            if (rewindByBackSeek) {
                videoPlayerSeekbar.setProgress(progress);
                videoPlayerSeekbarView.invalidate();
            }

            PipVideoOverlay.onUpdateRewindProgressUi(timeDiff, progress, rewindByBackSeek);
        }

        @Override
        protected void onRewindStart(boolean rewindForward) {
            videoForwardDrawable.setOneShootAnimation(false);
            videoForwardDrawable.setLeftSide(!rewindForward);
            videoForwardDrawable.setShowing(true);
            containerView.invalidate();

            PipVideoOverlay.onRewindStart(rewindForward);
        }
    };
    public final VideoFramesRewinder framesRewinder = new VideoFramesRewinder();
    private final VideoPlayerRewinder videoPlayerRewinder = new VideoPlayerRewinder(framesRewinder) {
        @Override
        protected void onRewindCanceled() {
            onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0));
            videoForwardDrawable.setShowing(false);

            PipVideoOverlay.onRewindCanceled();
        }

        @Override
        protected void updateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek) {
            videoForwardDrawable.setTime(Math.abs(timeDiff));
            if (rewindByBackSeek) {
                videoPlayerSeekbar.setProgress(progress);
                videoPlayerSeekbarView.invalidate();
            }

            PipVideoOverlay.onUpdateRewindProgressUi(timeDiff, progress, rewindByBackSeek);
        }

        @Override
        protected void onRewindStart(boolean rewindForward) {
            videoForwardDrawable.setOneShootAnimation(false);
            videoForwardDrawable.setLeftSide(!rewindForward);
            videoForwardDrawable.setShowing(true);
            containerView.invalidate();

            PipVideoOverlay.onRewindStart(rewindForward);
        }
    };

    public final Property<View, Float> FLASH_VIEW_VALUE = new AnimationProperties.FloatProperty<View>("flashViewAlpha") {
        @Override
        public void setValue(View object, float value) {
            object.setAlpha(value);
            if (photoCropView != null) {
                photoCropView.setVideoThumbFlashAlpha(value);
            }
        }

        @Override
        public Float get(View object) {
            return object.getAlpha();
        }
    };

    private Drawable pickerViewSendDrawable;
    private CharSequence customTitle;
    private boolean disableSelection;
    public boolean skipLastFrameDraw;

    public void setSelectionDisabled(boolean disabled) {
        disableSelection = disabled;
        if (checkImageView != null) {
            checkImageView.setVisibility(disabled ? View.GONE : checkImageView.getVisibility());
        }
        if (photosCounterView != null && disabled) {
            photosCounterView.setVisibility(View.GONE);
        }
    }

    public void addPhoto(MessageObject message, int classGuid) {
        if (classGuid != this.classGuid) {
            return;
        }

        if (imagesByIds[0].indexOfKey(message.getId()) < 0) {
            if (opennedFromMedia) {
                imagesArr.add(message);
            } else {
                imagesArr.add(0, message);
            }
            imagesByIds[0].put(message.getId(), message);
        }
        endReached[0] = imagesArr.size() == totalImagesCount;
        setImages();
    }

    public int getClassGuid() {
        return classGuid;
    }

    public void setCaption(CharSequence caption) {
        hasCaptionForAllMedia = true;
        captionForAllMedia = caption;
        setCurrentCaption(null, caption, false, false);
        updateCaptionTextForCurrentPhoto(null);
    }

    public void setAvatarFor(ImageUpdater.AvatarFor avatarFor) {
        setAvatarFor = avatarFor;
        if (sendPhotoType == SELECT_TYPE_AVATAR) {
            if (useFullWidthSendButton()) {
                doneButtonFullWidth.setVisibility(View.VISIBLE);
                pickerViewSendButton.setVisibility(View.GONE);
            } else {
                pickerViewSendButton.setVisibility(View.VISIBLE);
                doneButtonFullWidth.setVisibility(View.GONE);
            }
            if (avatarFor != null && avatarFor.fromObject != null && avatarFor.type == ImageUpdater.TYPE_SET_PHOTO_FOR_USER && setAvatarFor.self) {
                if (avatarFor.isVideo) {
                    photoCropView.setSubtitle(LocaleController.formatString(R.string.SetSuggestedVideoTooltip, avatarFor.fromObject.first_name));
                } else {
                    photoCropView.setSubtitle(LocaleController.formatString(R.string.SetSuggestedPhotoTooltip, avatarFor.fromObject.first_name));
                }
            } else {
                photoCropView.setSubtitle(null);
            }
        }
        if (avatarFor != null) {
            if (avatarFor.type == ImageUpdater.TYPE_SUGGEST_PHOTO_FOR_USER) {
                setTitle(avatarFor.isVideo ? getString("SuggestVideo", R.string.SuggestVideo) : getString("SuggestPhoto", R.string.SuggestPhoto));
            }
            if (avatarFor.isVideo) {
                videoAvatarTooltip.setText(getString("SetCover", R.string.SetCover));
            }
            actionBar.setBackground(null);
            actionBar.setElevation(2f);
        }
    }

    private boolean useFullWidthSendButton() {
        return setAvatarFor != null && setAvatarFor.self && !setAvatarFor.isVideo;
    }

    private static class SavedVideoPosition {

        public final float position;
        public final long timestamp;

        public SavedVideoPosition(float position, long timestamp) {
            this.position = position;
            this.timestamp = timestamp;
        }
    }

    private void onLinkClick(ClickableSpan link, TextView widget) {
        if (widget != null && link instanceof URLSpan) {
            String url = ((URLSpan) link).getURL();
            if (url.startsWith("video")) {
                if (videoPlayer != null && currentMessageObject != null) {
                    int seconds = Utilities.parseInt(url);
                    if (videoPlayer.getDuration() == C.TIME_UNSET) {
                        seekToProgressPending = seconds / (float) currentMessageObject.getDuration();
                    } else {
                        videoPlayer.seekTo(seconds * 1000L);
                        videoPlayerSeekbar.setProgress(seconds * 1000L / (float) videoPlayer.getDuration(), true);
                        videoPlayerSeekbarView.invalidate();
                    }
                }
            } else if (url.startsWith("#")) {
                if (parentActivity instanceof LaunchActivity) {
                    DialogsActivity fragment = new DialogsActivity(null);
                    fragment.setSearchString(url);
                    ((LaunchActivity) parentActivity).presentFragment(fragment, false, true);
                    closePhoto(false, false);
                }
            } else if (parentChatActivity != null && (link instanceof URLSpanReplacement || AndroidUtilities.shouldShowUrlInAlert(url))) {
                AlertsCreator.showOpenUrlAlert(parentChatActivity, url, true, true);
            } else {
                link.onClick(widget);
            }
        } else {
            link.onClick(widget);
        }
    }

    private void onLinkLongPress(ClickableSpan link, TextView widget, Runnable onDismiss) {
        if (!(link instanceof URLSpan)) {
            if (onDismiss != null) {
                onDismiss.run();
            }
            return;
        }
        final String url = ((URLSpan) link).getURL();
        int timestamp = -1;
        BottomSheet.Builder builder = new BottomSheet.Builder(parentActivity, false, resourcesProvider, 0xff1C2229);
        if (url.startsWith("video?")) {
            try {
                String timestampStr = url.substring(url.indexOf('?') + 1);
                timestamp = Integer.parseInt(timestampStr);
            } catch (Throwable ignore) {}
        }
        String url1 = url;
        boolean tel = false;
        if (url1.startsWith("mailto:")) {
            url1 = url1.substring(7);
        } else if (url1.startsWith("tel:")) {
            url1 = url1.substring(4);
            tel = true;
        } else if (timestamp >= 0) {
            if (currentMessageObject != null && !currentMessageObject.scheduled) {
                MessageObject messageObject1 = currentMessageObject;
                boolean isMedia = currentMessageObject.isVideo() || currentMessageObject.isRoundVideo() || currentMessageObject.isVoice() || currentMessageObject.isMusic();
                if (!isMedia && currentMessageObject.replyMessageObject != null) {
                    messageObject1 = currentMessageObject.replyMessageObject;
                }
                long dialogId = messageObject1.getDialogId();
                int messageId = messageObject1.getId();

                if (messageObject1.messageOwner.fwd_from != null) {
                    if (messageObject1.messageOwner.fwd_from.saved_from_peer != null) {
                        dialogId = MessageObject.getPeerId(messageObject1.messageOwner.fwd_from.saved_from_peer);
                        messageId = messageObject1.messageOwner.fwd_from.saved_from_msg_id;
                    } else if (messageObject1.messageOwner.fwd_from.from_id != null) {
                        dialogId = MessageObject.getPeerId(messageObject1.messageOwner.fwd_from.from_id);
                        messageId = messageObject1.messageOwner.fwd_from.channel_post;
                    }
                }

                if (DialogObject.isChatDialog(dialogId)) {
                    TLRPC.Chat currentChat = MessagesController.getInstance(currentAccount).getChat(-dialogId);
                    String username = ChatObject.getPublicUsername(currentChat);
                    if (username != null) {
                        url1 = "https://t.me/" + username + "/" + messageId + "?t=" + AndroidUtilities.formatTimestamp(timestamp);
                    }
                } else {
                    TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(dialogId);
                    String username = UserObject.getPublicUsername(user);
                    if (user != null && username != null) {
                        url1 = "https://t.me/" + username + "/" + messageId + "?t=" + AndroidUtilities.formatTimestamp(timestamp);
                    }
                }
            }
        }
        builder.setTitle(url1);
        final boolean finalTel = tel;
        final String finalUrl1 = url1;
        builder.setItems(new CharSequence[]{getString(R.string.Open), getString(R.string.CopyLink)}, (dialog, which) -> {
            if (which == 0) {
                onLinkClick(link, widget);
            } else if (which == 1) {
                AndroidUtilities.addToClipboard(finalUrl1);
                String bulletinMessage;
                if (finalTel) {
                    bulletinMessage = getString("PhoneCopied", R.string.PhoneCopied);
                } else if (finalUrl1.startsWith("#")) {
                    bulletinMessage = getString("HashtagCopied", R.string.HashtagCopied);
                } else if (finalUrl1.startsWith("@")) {
                    bulletinMessage = getString("UsernameCopied", R.string.UsernameCopied);
                } else {
                    bulletinMessage = getString("LinkCopied", R.string.LinkCopied);
                }
                if (AndroidUtilities.shouldShowClipboardToast()) {
                    BulletinFactory.of(containerView, resourcesProvider).createSimpleBulletin(R.raw.voip_invite, bulletinMessage).show();
                }
            }
        });
        builder.setOnPreDismissListener(di -> onDismiss.run());
        BottomSheet bottomSheet = builder.create();
        bottomSheet.scrollNavBar = true;
        bottomSheet.show();
        try {
            if (!NekoConfig.disableVibration.Bool()) containerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        } catch (Exception ignore) {}
        bottomSheet.setItemColor(0,0xffffffff, 0xffffffff);
        bottomSheet.setItemColor(1,0xffffffff, 0xffffffff);
        bottomSheet.setBackgroundColor(0xff1C2229);
        bottomSheet.setTitleColor(0xff8A8A8A);
        bottomSheet.setCalcMandatoryInsets(true);
        AndroidUtilities.setNavigationBarColor(bottomSheet, 0xff1C2229, false);
        AndroidUtilities.setLightNavigationBar(bottomSheet, false);
        bottomSheet.scrollNavBar = true;
    }

    private void cancelFlashAnimations() {
        if (flashView != null) {
            flashView.animate().setListener(null).cancel();
            flashView.setAlpha(0.0f);
        }
        if (flashAnimator != null) {
            flashAnimator.cancel();
            flashAnimator = null;
        }
        if (photoCropView != null) {
            photoCropView.cancelThumbAnimation();
        }
    }

    private void cancelVideoPlayRunnable() {
        if (videoPlayRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(videoPlayRunnable);
            videoPlayRunnable = null;
        }
    }

    private long getCurrentVideoPosition() {
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return photoViewerWebView.getCurrentPosition();
        } else {
            if (videoPlayer == null) {
                return 0;
            }
            return videoPlayer.getCurrentPosition();
        }
    }

    private long getVideoDuration() {
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return photoViewerWebView.getVideoDuration();
        } else {
            if (videoPlayer == null) {
                return 0;
            }
            return videoPlayer.getDuration();
        }
    }

    private void seekVideoOrWebTo(long position) {
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            photoViewerWebView.seekTo(position);
        } else if (videoPlayer != null) {
            videoPlayer.seekTo(position);
        }
        updateVideoPlayerTime();
    }

    private boolean isVideoPlaying() {
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return photoViewerWebView.isPlaying();
        } else {
            return videoPlayer != null && videoPlayer.isPlaying();
        }
    }

    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoPlayer != null || photoViewerWebView != null && photoViewerWebView.isControllable()) {
                if (isCurrentVideo) {
                    if (!videoTimelineView.isDragging()) {
                        float progress = getCurrentVideoPosition() / (float) getVideoDuration();
                        if (shownControlsByEnd && !actionBarWasShownBeforeByEnd) {
                            progress = 0;
                        }
                        if (!inPreview && (currentEditMode != EDIT_MODE_NONE || videoTimelineViewContainer.getVisibility() == View.VISIBLE)) {
                            if (progress >= videoTimelineView.getRightProgress()) {
                                videoTimelineView.setProgress(videoTimelineView.getLeftProgress());
                                videoPlayer.seekTo((int) (videoTimelineView.getLeftProgress() * getVideoDuration()));
                                manuallyPaused = false;
                                cancelVideoPlayRunnable();
                                if (muteVideo || sendPhotoType == SELECT_TYPE_AVATAR || currentEditMode != EDIT_MODE_NONE || switchingToMode > 0) {
                                    playVideoOrWeb();
                                } else {
                                    pauseVideoOrWeb();
                                }
                                containerView.invalidate();
                            } else {
                                videoTimelineView.setProgress(progress);
                            }
                        } else if (sendPhotoType != SELECT_TYPE_AVATAR) {
                            videoTimelineView.setProgress(progress);
                        }
                        updateVideoPlayerTime();
                    }
                } else {
                    float progress = getCurrentVideoPosition() / (float) getVideoDuration();
                    if (shownControlsByEnd && !actionBarWasShownBeforeByEnd) {
                        progress = 0;
                    }
                    float bufferedProgress;
                    if (currentVideoFinishedLoading) {
                        bufferedProgress = 1.0f;
                    } else {
                        long newTime = SystemClock.elapsedRealtime();
                        if (Math.abs(newTime - lastBufferedPositionCheck) >= 500) {
                            if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
                                bufferedProgress = photoViewerWebView.getBufferedPosition();
                            } else {
                                bufferedProgress = isStreaming ? FileLoader.getInstance(currentAccount).getBufferedProgressFromPosition(seekToProgressPending != 0 ? seekToProgressPending : progress, currentFileNames[0]) : 1.0f;
                            }
                            lastBufferedPositionCheck = newTime;
                        } else {
                            bufferedProgress = -1;
                        }
                    }
                    if (!inPreview && videoTimelineViewContainer.getVisibility() == View.VISIBLE) {
                        if (progress >= videoTimelineView.getRightProgress()) {
                            manuallyPaused = false;
                            pauseVideoOrWeb();
                            videoPlayerSeekbar.setProgress(0);
                            seekVideoOrWebTo((int) (videoTimelineView.getLeftProgress() * getVideoDuration()));
                            containerView.invalidate();
                        } else {
                            progress -= videoTimelineView.getLeftProgress();
                            if (progress < 0) {
                                progress = 0;
                            }
                            progress /= (videoTimelineView.getRightProgress() - videoTimelineView.getLeftProgress());
                            if (progress > 1) {
                                progress = 1;
                            }
                            videoPlayerSeekbar.setProgress(progress);
                        }
                    } else {
                        if (seekToProgressPending == 0 && (longVideoPlayerRewinder.rewindCount == 0 || !longVideoPlayerRewinder.rewindByBackSeek && !videoPlayerRewinder.rewindByBackSeek)) {
                            videoPlayerSeekbar.setProgress(progress, false);
                        }
                        if (bufferedProgress != -1) {
                            videoPlayerSeekbar.setBufferedProgress(bufferedProgress);
                            PipVideoOverlay.setBufferedProgress(bufferedProgress);
                        }
                    }
                    videoPlayerSeekbarView.invalidate();
                    if (shouldSavePositionForCurrentVideo != null) {
                        float value = progress;
                        if (value >= 0 && SystemClock.elapsedRealtime() - lastSaveTime >= 1000) {
                            final String saveFor = shouldSavePositionForCurrentVideo;
                            lastSaveTime = SystemClock.elapsedRealtime();
                            if (currentMessageObject != null) {
                                currentMessageObject.cachedSavedTimestamp = value;
                            }
                            Utilities.globalQueue.postRunnable(() -> {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", Activity.MODE_PRIVATE).edit();
                                editor.putFloat(saveFor, value).apply();
                            });
                        }
                    }
                    updateVideoPlayerTime();
                }
//                    if (!videoPlayer.isLooping() && videoPlayer.getDuration() != C.TIME_UNSET) {
//                        if (videoPlayer.getCurrentPosition() > videoPlayer.getDuration() - FirstFrameView.fadeDuration) {
//                            if (!shownControlsByEnd) {
//                                actionBarWasShownBeforeByEnd = isActionBarVisible;
//                                shownControlsByEnd = true;
//                                toggleActionBar(true, true);
//                                checkProgress(0, false, false);
//                            } else {
//                                shownControlsByEnd = false;
//                                actionBarWasShownBeforeByEnd = false;
//                            }
//                        }
//                    }
//                }
            }
            if (firstFrameView != null) {
                firstFrameView.updateAlpha();
            }
            if (isPlaying) {
                AndroidUtilities.runOnUIThread(updateProgressRunnable, 17);
            }
        }
    };

    private final Runnable switchToInlineRunnable = new Runnable() {
        @Override
        public void run() {
            if (PipVideoOverlay.isVisible()) {
                PipVideoOverlay.dismiss();
                AndroidUtilities.runOnUIThread(this, 250);
                return;
            }

            switchingInlineMode = false;
            if (currentBitmap != null) {
                currentBitmap.recycle();
                currentBitmap = null;
            }

            changingTextureView = true;

            TextureViewContainer textureViewContainer = new TextureViewContainer(parentActivity);

            try {
                if (usedSurfaceView) {
                    Drawable drawable = textureImageView.getDrawable();
                    if (drawable instanceof BitmapDrawable) {
                        currentBitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (currentBitmap != null) {
                            if (textureImageView != null) {
                                textureImageView.setVisibility(View.VISIBLE);
                                textureImageView.setImageBitmap(currentBitmap);
                            }
                            textureViewContainer.imageReceiver.setImageBitmap(currentBitmap);
                        }
                    } else {
                        currentBitmap = Bitmaps.createBitmap(videoSurfaceView.getWidth(), videoSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
                        AndroidUtilities.getBitmapFromSurface(videoSurfaceView, currentBitmap, () -> {
                            if (currentBitmap != null) {
                                if (textureImageView != null) {
                                    textureImageView.setVisibility(View.VISIBLE);
                                    textureImageView.setImageBitmap(currentBitmap);
                                }
                                textureViewContainer.imageReceiver.setImageBitmap(currentBitmap);
                            }
                        });
                    }
                } else {
                    currentBitmap = Bitmaps.createBitmap(videoTextureView.getWidth(), videoTextureView.getHeight(), Bitmap.Config.ARGB_8888);
                    videoTextureView.getBitmap(currentBitmap);

                    if (currentBitmap != null) {
                        if (textureImageView != null) {
                            textureImageView.setVisibility(View.VISIBLE);
                            textureImageView.setImageBitmap(currentBitmap);
                        }
                        textureViewContainer.imageReceiver.setImageBitmap(currentBitmap);
                    }
                }
            } catch (Throwable e) {
                if (currentBitmap != null) {
                    currentBitmap.recycle();
                    currentBitmap = null;
                }
                FileLog.e(e);
            }

            isInline = true;
            changedTextureView = textureViewContainer.textureView;
            if (PipVideoOverlay.show(false, parentActivity, textureViewContainer, videoWidth, videoHeight, pipVideoOverlayAnimateFlag)) {
                PipVideoOverlay.setPhotoViewer(PhotoViewer.this);
            }
            pipVideoOverlayAnimateFlag = true;

            if (usedSurfaceView) {
                if (aspectRatioFrameLayout != null) {
                    aspectRatioFrameLayout.removeView(videoTextureView);
                    aspectRatioFrameLayout.removeView(videoSurfaceView);
                }
                videoPlayer.setSurfaceView(null);
                videoPlayer.setTextureView(null);
                videoPlayer.play();
                videoPlayer.setTextureView(changedTextureView);
                checkChangedTextureView(true);
                changedTextureView.setVisibility(View.VISIBLE);
            } else {
                changedTextureView.setVisibility(View.INVISIBLE);
                if (aspectRatioFrameLayout != null) {
                    aspectRatioFrameLayout.removeView(videoTextureView);
                    aspectRatioFrameLayout.removeView(videoSurfaceView);
                }
            }

        }
    };

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (videoTextureView == null) {
                return true;
            }

            if (PipVideoOverlay.isVisible() && PipVideoOverlay.getPipSource() != null) {
                if (PipVideoOverlay.getPipSource().state2.isAttachedToPip()) {
                    PipVideoOverlay.getPipTextureView().setSurfaceTexture(surface);
                    PipVideoOverlay.getPipTextureView().setVisibility(View.VISIBLE);

                    return false;
                }
            }

            if (changingTextureView) {
                if (switchingInlineMode) {
                    waitingForFirstTextureUpload = 2;
                }
                videoTextureView.setSurfaceTexture(surface);
                videoTextureView.setVisibility(View.VISIBLE);
                changingTextureView = false;
                containerView.invalidate();
                return false;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (waitingForFirstTextureUpload == 1) {
                checkChangedTextureView(true);
            }
        }
    };

    private void checkChangedTextureView(boolean enter) {
        if (enter) {
            if (changedTextureView == null) {
                return;
            }
            changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (textureImageView != null) {
                        if (isInline) {
                            AndroidUtilities.runOnUIThread(() -> {
                                textureImageView.setVisibility(View.INVISIBLE);
                                textureImageView.setImageDrawable(null);
                                if (currentBitmap != null) {
                                    currentBitmap.recycle();
                                    currentBitmap = null;
                                }
                            }, 300);
                        } else {
                            textureImageView.setVisibility(View.INVISIBLE);
                            textureImageView.setImageDrawable(null);
                            if (currentBitmap != null) {
                                currentBitmap.recycle();
                                currentBitmap = null;
                            }
                        }
                    }
                    AndroidUtilities.runOnUIThread(() -> {
                        if (isInline) {
                            dismissInternal();
                        }
                    });
                    waitingForFirstTextureUpload = 0;
                    return true;
                }
            });
            changedTextureView.invalidate();
        } else {
            if (waitingForFirstTextureUpload == 2) {
                if (textureImageView != null) {
//                    if (usedSurfaceView) {
//                        if (currentBitmap != null) {
//                            currentBitmap.recycle();
//                            currentBitmap = null;
//                        }
//                        textureImageView.setVisibility(View.VISIBLE);
//                        textureImageView.setImageBitmap(currentBitmap = changedTextureView.getBitmap());
//                    } else {
                        textureImageView.setVisibility(View.INVISIBLE);
                        textureImageView.setImageDrawable(null);
                        if (currentBitmap != null) {
                            currentBitmap.recycle();
                            currentBitmap = null;
                        }
                   // }

                }
                switchingInlineMode = false;

                View textureView = usedSurfaceView ? videoSurfaceView : videoTextureView;
                if (aspectRatioFrameLayout == null) {
                    return;
                }
                aspectRatioFrameLayout.getLocationInWindow(pipPosition);
                //pipPosition[0] -= getLeftInset();
                pipPosition[1] -= containerView.getTranslationY();
                if (textureImageView != null) {
                    textureImageView.setTranslationX(textureImageView.getTranslationX() + getLeftInset());
                }
                if (textureView != null) {
                    textureView.setTranslationX(textureView.getTranslationX() + getLeftInset() - aspectRatioFrameLayout.getX());
                }
                if (firstFrameView != null) {
                    firstFrameView.setTranslationX(textureView.getTranslationX());
                }

                ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);
                progressAnimator.addUpdateListener(animation -> clippingImageProgress = 1f - (float) animation.getAnimatedValue());

                float toX = usedSurfaceView ? 0 : pipPosition[0] - aspectRatioFrameLayout.getX();
                float toY = usedSurfaceView ? 0 : pipPosition[1] - aspectRatioFrameLayout.getY();
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(progressAnimator);
                animators.add(ObjectAnimator.ofFloat(textureImageView, View.SCALE_X, 1.0f));
                animators.add(ObjectAnimator.ofFloat(textureImageView, View.SCALE_Y, 1.0f));
                animators.add(ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_X, usedSurfaceView ? 0 : pipPosition[0]));
                animators.add(ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_Y, usedSurfaceView ? 0 : pipPosition[1]));
                animators.add(ObjectAnimator.ofFloat(textureView, View.SCALE_X, 1.0f));
                animators.add(ObjectAnimator.ofFloat(textureView, View.SCALE_Y, 1.0f));
                animators.add(ObjectAnimator.ofFloat(textureView, View.TRANSLATION_X, toX));
                animators.add(ObjectAnimator.ofFloat(textureView, View.TRANSLATION_Y, toY));
                animators.add(ObjectAnimator.ofInt(backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 255));
                if (firstFrameView != null) {
                    animators.add(ObjectAnimator.ofFloat(firstFrameView, View.SCALE_X, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(firstFrameView, View.SCALE_Y, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(firstFrameView, View.TRANSLATION_X, toX));
                    animators.add(ObjectAnimator.ofFloat(firstFrameView, View.TRANSLATION_Y, toY));
                }

                RectOld pipRect = PipVideoOverlay.getPipRect(false, aspectRatioFrameLayout.getAspectRatio());
                float scale = pipRect.width / textureView.getWidth();
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.addUpdateListener(animation -> {
                    inlineOutAnimationProgress = (float) animation.getAnimatedValue();
                    textureView.invalidateOutline();
                    if (textureImageView != null) {
                        textureImageView.invalidateOutline();
                    }
                    if (firstFrameView != null) {
                        firstFrameView.invalidateOutline();
                    }
                });
                animators.add(valueAnimator);

                animatorSet.playTogether(animators);
                final DecelerateInterpolator interpolator = new DecelerateInterpolator();
                animatorSet.setInterpolator(interpolator);
                animatorSet.setDuration(250);
                if (videoSurfaceView != null) {
                    videoSurfaceView.setVisibility(View.VISIBLE);
                }
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pipAnimationInProgress = false;
                        textureView.setOutlineProvider(null);
                        if (textureImageView != null) {
                            textureImageView.setOutlineProvider(null);
                        }
                        if (firstFrameView != null) {
                            firstFrameView.setOutlineProvider(null);
                        }
                        if (videoSurfaceView != null) {
                            videoSurfaceView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                animatorSet.start();
                toggleActionBar(true, true, new ActionBarToggleParams().enableStatusBarAnimation(false).enableTranslationAnimation(false).animationDuration(250).animationInterpolator(interpolator));

                waitingForFirstTextureUpload = 0;
            }
        }
    }

    private float[][] animationValues = new float[2][13];

    private ChatActivity parentChatActivity;
    private BaseFragment parentFragment;
//    private MentionsAdapter mentionsAdapter;
//    private RecyclerListView mentionListView;
//    private LinearLayoutManager mentionLayoutManager;
//    private SpringAnimation mentionListAnimation;
//    private boolean mentionListViewVisible;
//    private boolean allowMentions;

    private int animationInProgress;
    private boolean openAnimationInProgress;
    private long transitionAnimationStartTime;
    private Runnable animationEndRunnable;
    private PlaceProviderObject showAfterAnimation;
    private PlaceProviderObject hideAfterAnimation;
    private boolean disableShowCheck;

    private CharSequence lastTitle;

    private boolean isEmbedVideo;

    private int currentEditMode;

    private final Runnable updateContainerFlagsRunnable = () -> {
        if (isVisible && animationInProgress == 0) {
            updateContainerFlags(isActionBarVisible);
        }
    };

    public static class EditState {
        public String paintPath;
        public String croppedPaintPath;
        public MediaController.CropState cropState;
        public MediaController.SavedFilterState savedFilterState;
        public ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
        public ArrayList<VideoEditedInfo.MediaEntity> croppedMediaEntities;
        public long averageDuration;

        public void reset() {
            paintPath = null;
            cropState = null;
            savedFilterState = null;
            mediaEntities = null;
            croppedPaintPath = null;
            croppedMediaEntities = null;
            averageDuration = 0;
        }
    }

    private class SavedState {

        private int index;
        private ArrayList<MessageObject> messages;
        private PhotoViewerProvider provider;

        public SavedState(int index, ArrayList<MessageObject> messages, PhotoViewerProvider provider) {
            this.messages = messages;
            this.index = index;
            this.provider = provider;
        }

        public void restore() {
            placeProvider = provider;

            windowLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            windowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
            windowView.setFocusable(false);
            containerView.setFocusable(false);
            backgroundDrawable.setAlpha(255);
            containerView.setAlpha(1.0f);

            onPhotoShow(null, null, null, null, messages, null, null, index, provider.getPlaceForPhoto(messages.get(index), null, index, true, false));
        }
    }

    private int currentImageHasFace;
    private String currentImageFaceKey;
    private PaintingOverlay paintingOverlay;
    private PaintingOverlay leftPaintingOverlay;
    private PaintingOverlay rightPaintingOverlay;
    private ImageReceiver leftImage = new ImageReceiver();
    public ImageReceiver centerImage = new ImageReceiver() {
        @Override
        protected boolean setImageBitmapByKey(Drawable drawable, String key, int type, boolean memCache, int guid) {
            boolean result = super.setImageBitmapByKey(drawable, key, type, memCache, guid);
            prepareSegmentImage();
            return result;
        }
    };
    private ImageReceiver rightImage = new ImageReceiver();
    private BlurringShader.ThumbBlurer leftBlur = new BlurringShader.ThumbBlurer(1, this::invalidateBlur);
    private BlurringShader.ThumbBlurer centerBlur = new BlurringShader.ThumbBlurer(1, this::invalidateBlur);
    private BlurringShader.ThumbBlurer rightBlur = new BlurringShader.ThumbBlurer(1, this::invalidateBlur);
    private boolean leftImageIsVideo;
    private boolean centerImageIsVideo;
    private boolean centerImageIsLivePhoto;
    private boolean rightImageIsVideo;
    private boolean centerImageTransformLocked = false;
    private Matrix centerImageTransform = new Matrix();
    private Paint videoFrameBitmapPaint = new Paint();
    private Bitmap videoFrameBitmap = null;
    private int currentIndex;
    private int switchingToIndex;
    private boolean editing;
    private boolean fancyShadows;
    private MessageObject currentMessageObject;
    private VideoAds ads;
    private ArrayList<VideoPlayer.Quality> currentPlayingVideoQualityFiles;
    private Uri currentPlayingVideoFile;
    private EditState editState = new EditState();
    private TLRPC.BotInlineResult currentBotInlineResult;
    private ImageLocation currentFileLocation;
    private ImageLocation currentFileLocationVideo;
    private SecureDocument currentSecureDocument;
    private String[] currentFileNames = new String[3];
    private PlaceProviderObject currentPlaceObject;
    private String currentPathObject;
    private long currentPathVideoOffset;
    private String currentImagePath;
    private boolean currentVideoFinishedLoading;
    private TL_iv.PageBlock currentPageBlock;
    private ImageReceiver.BitmapHolder currentThumb;
    private boolean ignoreDidSetImage;
    private boolean dontAutoPlay;
    boolean fromCamera;
    private boolean captionTranslated;
    private String captionDetectedLanguage;
    public StickerMakerView stickerMakerView;
    public PhotoViewerCoverEditor coverEditor;
    private ArrayList<String> selectedEmojis;
    private StickerMakerBackgroundView stickerMakerBackgroundView;
    private BlurButton cutOutBtn;
    private LinearLayout btnLayout;
    private BlurButton eraseBtn, restoreBtn, undoBtn, outlineBtn;

    private long avatarsDialogId;
    private boolean canEditAvatar;
    private boolean isEvent;
    private int sharedMediaType;
    private long topicId;
    private long currentDialogId;
    private ReactionsLayoutInBubble.VisibleReaction currentFilterTag;
    private String currentFilterQuery;
    private boolean currentFiltered;
    private long mergeDialogId;
    private int totalImagesCount;
    private int startOffset;
    private int totalImagesCountMerge;
    private boolean isFirstLoading;
    private boolean needSearchImageInArr;
    private boolean loadingMoreImages;
    private boolean[] endReached = new boolean[]{false, true};
    private boolean startReached = false;
    private boolean opennedFromMedia;
    private boolean openedFromProfile;

    private boolean attachedToWindow;

    private boolean wasLayout;
    private boolean dontResetZoomOnFirstLayout;

    private boolean draggingDown;
    private float dragY;
    private float translationX;
    private float translationY;
    private float translateY;
    private float scale = 1;
    private float currentCropScale = 1.0f;
    private float currentCropX = 0.0f, currentCropY = 0.0f;
    private float rotate = 0;
    private float mirror = 0;
    private float animateToX;
    private float animateToY;
    private float animateToScale;
    private float animateToRotate;
    private float animateToMirror;
    private float savedTx, savedTy, savedScale, savedRotation;
    private float animationValue;
    private float clippingImageProgress;
    private boolean applying;
    private long animationStartTime;
    private int switchingToMode = -1;
    private AnimatorSet imageMoveAnimation;
    private AnimatorSet changeModeAnimation;
    private GestureDetector2 gestureDetector;
    private boolean doubleTapEnabled;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private float pinchStartDistance;
    private float pinchStartAngle;
    private float pinchStartScale = 1;
    private float pinchStartRotate = 0;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartX;
    private float pinchStartY;
    private float moveStartX;
    private float moveStartY;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private boolean canZoom = true;
    private boolean changingPage;
    private boolean zooming;
    private boolean moving;
    private int paintViewTouched;
    private int maskPaintViewTouched;
    private boolean doubleTap;
    private boolean invalidCoords;
    private boolean canDragDown = true;
    private boolean zoomAnimation;
    private boolean discardTap;
    private int switchImageAfterAnimation;
    private VelocityTracker velocityTracker;
    private Scroller scroller;
    private boolean shownControlsByEnd = false;
    private boolean actionBarWasShownBeforeByEnd = false;

    private final ArrayList<MessageObject> imagesArrTemp = new ArrayList<>();
    private final SparseArray<MessageObject>[] imagesByIdsTemp = new SparseArray[] {new SparseArray<>(), new SparseArray<>()};
    private final ArrayList<MessageObject> imagesArr = new ArrayList<>();
    private final SparseArray<MessageObject>[] imagesByIds = new SparseArray[] {new SparseArray<>(), new SparseArray<>()};
    private final ArrayList<ImageLocation> imagesArrLocations = new ArrayList<>();
    private final ArrayList<ImageLocation> imagesArrLocationsVideo = new ArrayList<>();
    private final ArrayList<Long> imagesArrLocationsSizes = new ArrayList<>();
    private final ArrayList<TLRPC.Message> imagesArrMessages = new ArrayList<>();
    private final ArrayList<SecureDocument> secureDocuments = new ArrayList<>();
    private final ArrayList<TLRPC.Photo> avatarsArr = new ArrayList<>();
    private final ArrayList<Object> imagesArrLocals = new ArrayList<>();
    private ImageLocation currentAvatarLocation = null;
    private SavedState savedState = null;

    private PageBlocksAdapter pageBlocksAdapter;

    public interface PageBlocksAdapter {
        int getItemsCount();
        TL_iv.PageBlock get(int index);
        List<TL_iv.PageBlock> getAll();
        boolean isVideo(int index);
        TLObject getMedia(int index);
        File getFile(int index);
        String getFileName(int index);
        CharSequence getCaption(int index);
        TLRPC.PhotoSize getFileLocation(TLObject media, int[] size);
        void updateSlideshowCell(TL_iv.PageBlock currentPageBlock);
        Object getParentObject();
        boolean isHardwarePlayer(int index);
    }

    private Rect hitRect = new Rect();

    private final static int gallery_menu_quality = 1;
    private final static int gallery_menu_save = 2;
    private final static int gallery_menu_showall = 3;
    private final static int gallery_menu_send = 4;
    private final static int gallery_menu_showinchat = 5;
    private final static int gallery_menu_pip = 6;
    private final static int gallery_menu_delete = 7;
    private final static int gallery_menu_cancel_loading = 8;
    private final static int gallery_menu_share = 9;
    private final static int gallery_menu_openin = 10;
    private final static int gallery_menu_masks = 11;
    private final static int gallery_menu_savegif = 12;
    private final static int gallery_menu_masks2 = 13;
    private final static int gallery_menu_set_as_main = 14;
    private final static int gallery_menu_edit_avatar = 15;
    private final static int gallery_menu_share2 = 16;
    private final static int gallery_menu_speed = 17;
    private final static int gallery_menu_paint = 18;
    private final static int gallery_menu_translate = 19;
    private final static int gallery_menu_hide_translation = 20;
    private final static int gallery_menu_reply = 21;
    private final static int gallery_menu_loop = 22;
    private final static int gallery_menu_report = 23;
    private final static int gallery_menu_chromecast = 24;
    private final static int gallery_menu_create_sticker = 25;
    private final static int gallery_menu_delete2 = 26;

    private final static int gallery_menu_paint2 = 1001;

    private final static int ads_sponsor_info = 101;
    private final static int ads_about = 102;
    private final static int ads_report = 103;
    private final static int ads_separator = 104;
    private final static int ads_remove = 105;

    private final static int gallery_menu_scan = 200;
    private final static int gallery_menu_send_forward = 201;
    private final static int gallery_menu_copy = 202;
    private final static int gallery_menu_set_photo = 203;
    private final static int gallery_menu_send_noquote = 204;
    private final static int gallery_menu_copy_frame = 205;

    private static DecelerateInterpolator decelerateInterpolator;
    private static Paint progressPaint;

    private AnimationNotificationsLocker transitionNotificationLocker = new AnimationNotificationsLocker(new int[]{
            NotificationCenter.dialogsNeedReload,
            NotificationCenter.closeChats,
            NotificationCenter.mediaCountDidLoad,
            NotificationCenter.mediaDidLoad,
            NotificationCenter.dialogPhotosUpdate
    });

    private class BackgroundDrawable extends ColorDrawable {

        private final RectF rect = new RectF();
        private final RectF visibleRect = new RectF();

        private final Paint paint;

        private Runnable drawRunnable;
        private boolean allowDrawContent;

        public BackgroundDrawable(int color) {
            super(color);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
        }

        private void checkAllowDrawContent() {
            if (activityVisibilityController != null) {
                activityVisibilityController.setHidden(!allowDrawContent);
            }
            if (parentAlertWindowVisibilityController != null) {
                parentAlertWindowVisibilityController.setHidden(!allowDrawContent);
            }
            if (parentAlert != null) {
                parentAlert.setAllowDrawContent(allowDrawContent);
            }
        }

        @Keep
        @Override
        public void setAlpha(int alpha) {
            if (parentActivity instanceof LaunchActivity) {
                allowDrawContent = !isVisible || alpha != 255;
                if (allowDrawContent) {
                    checkAllowDrawContent();
                } else {
                    AndroidUtilities.runOnUIThread(this::checkAllowDrawContent, 70);
                }
            }
            super.setAlpha(alpha);
            paint.setAlpha(alpha);
        }

        @Override
        public void draw(Canvas canvas) {
            if (textureViewSkipRender) {
                return;
            }

            if (animationInProgress != 0 && !AndroidUtilities.isTablet() && currentPlaceObject != null && currentPlaceObject.animatingImageView != null) {
                animatingImageView.getClippedVisibleRect(visibleRect);
                if (!visibleRect.isEmpty()) {
                    visibleRect.inset(dp(1f), dp(1f));

                    final Rect boundsRect = getBounds();
                    final float width = boundsRect.right;
                    final float height = boundsRect.bottom;

                    for (int i = 0; i < 4; i++) {
                        switch (i) {
                            case 0: // left rect
                                rect.set(0, visibleRect.top, visibleRect.left, visibleRect.bottom);
                                break;
                            case 1: // top rect
                                rect.set(0, 0, width, visibleRect.top);
                                break;
                            case 2: // right rect
                                rect.set(visibleRect.right, visibleRect.top, width, visibleRect.bottom);
                                break;
                            case 3: // bottom rect
                                rect.set(0, visibleRect.bottom, width, height);
                                break;
                        }
                        canvas.drawRect(rect, paint);
                    }
                }
            } else {
                super.draw(canvas);
            }
            if (getAlpha() != 0) {
                if (drawRunnable != null) {
                    AndroidUtilities.runOnUIThread(drawRunnable);
                    drawRunnable = null;
                }
            }
        }
    }

    private static class SelectedPhotosListView extends RecyclerListView {

        private Drawable arrowDrawable;
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private RectF rect = new RectF();

        public SelectedPhotosListView(Context context) {
            super(context);
            setWillNotDraw(false);

            setClipToPadding(false);
            setTranslationY(-dp(10));
            DefaultItemAnimator defaultItemAnimator;
            setItemAnimator(defaultItemAnimator = new DefaultItemAnimator() {
                @Override
                protected void onMoveAnimationUpdate(ViewHolder holder) {
                    invalidate();
                }
            });
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            setPadding(dp(12), dp(12), dp(12), dp(6));
            paint.setColor(0x7f000000);

            arrowDrawable = context.getResources().getDrawable(R.drawable.photo_tooltip2).mutate();
        }

        @Override
        public void onDraw(Canvas c) {
            super.onDraw(c);

            int count = getChildCount();
            if (count > 0) {
                int x = getMeasuredWidth() - dp(87);
                arrowDrawable.setBounds(x, 0, x + arrowDrawable.getIntrinsicWidth(), dp(6));
                arrowDrawable.draw(c);

                int minX = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                for (int a = 0; a < count; a++) {
                    View v = getChildAt(a);
                    minX = (int) Math.min(minX, Math.floor(v.getX()));
                    maxX = (int) Math.max(maxX, Math.ceil(v.getX() + v.getMeasuredWidth()));
                }
                if (minX != Integer.MAX_VALUE && maxX != Integer.MIN_VALUE) {
                    rect.set(minX - dp(6), dp(6), maxX + dp(6), dp(6 + 85 + 12));
                    c.drawRoundRect(rect, dp(8), dp(8), paint);
                }
            }
        }
    }

    private static class CounterView extends View {

        private StaticLayout staticLayout;
        private TextPaint textPaint;
        private Paint paint;
        private int width;
        private int height;
        private RectF rect;
        private int currentCount = 0;
        private float rotation;

        public CounterView(Context context) {
            super(context);
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(dp(15));
            textPaint.setTypeface(AndroidUtilities.bold());
            textPaint.setColor(0xffffffff);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0xffffffff);
            paint.setStrokeWidth(dp(2));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);

            rect = new RectF();

            setCount(0);
        }

        @Keep
        @Override
        public void setScaleX(float scaleX) {
            super.setScaleX(scaleX);
            invalidate();
        }

        @Keep
        @Override
        public void setRotationX(float rotationX) {
            rotation = rotationX;
            invalidate();
        }

        @Override
        public float getRotationX() {
            return rotation;
        }

        public void setCount(int value) {
            staticLayout = new StaticLayout("" + Math.max(1, value), textPaint, dp(100), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            width = (int) Math.ceil(staticLayout.getLineWidth(0));
            height = staticLayout.getLineBottom(0);
            AnimatorSet animatorSet = new AnimatorSet();
            if (value == 0) {
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(this, View.SCALE_X, 0.0f),
                        ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.0f),
                        ObjectAnimator.ofInt(paint, AnimationProperties.PAINT_ALPHA, 0),
                        ObjectAnimator.ofInt(textPaint, AnimationProperties.PAINT_ALPHA, 0));
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else if (currentCount == 0) {
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(this, View.SCALE_X, 0.0f, 1.0f),
                        ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.0f, 1.0f),
                        ObjectAnimator.ofInt(paint, AnimationProperties.PAINT_ALPHA, 0, 255),
                        ObjectAnimator.ofInt(textPaint, AnimationProperties.PAINT_ALPHA, 0, 255));
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else if (value < currentCount) {
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(this, View.SCALE_X, 1.1f, 1.0f),
                        ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.1f, 1.0f));
                animatorSet.setInterpolator(new OvershootInterpolator());
            } else {
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(this, View.SCALE_X, 0.9f, 1.0f),
                        ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.9f, 1.0f));
                animatorSet.setInterpolator(new OvershootInterpolator());
            }

            animatorSet.setDuration(180);
            animatorSet.start();
            requestLayout();
            currentCount = value;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(Math.max(width + dp(20), dp(30)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dp(40), MeasureSpec.EXACTLY));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int cy = getMeasuredHeight() / 2;
            paint.setAlpha(255);
            rect.set(dp(1), cy - dp(14), getMeasuredWidth() - dp(1), cy + dp(14));
            canvas.drawRoundRect(rect, dp(15), dp(15), paint);
            if (staticLayout != null) {
                textPaint.setAlpha((int) ((1.0f - rotation) * 255));
                canvas.save();
                canvas.translate((getMeasuredWidth() - width) / 2, (getMeasuredHeight() - height) / 2 + dpf2(0.2f) + rotation * dp(5));
                staticLayout.draw(canvas);
                canvas.restore();
                paint.setAlpha((int) (rotation * 255));
                int cx = (int) rect.centerX();
                cy = (int) rect.centerY();
                cy -= dp(5) * (1.0f - rotation);
                canvas.drawLine(cx + dp(5), cy - dp(5), cx - dp(5), cy + dp(5), paint);
                canvas.drawLine(cx - dp(5), cy - dp(5), cx + dp(5), cy + dp(5), paint);
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName("android.widget.Button");
            if (currentCount > 0) {
                info.setContentDescription(LocaleController.formatPluralString("PhotosSelected", currentCount));
            }
        }
    }

    private class PhotoProgressView {

        private long lastUpdateTime = 0;
        private float radOffset = 0;
        private float currentProgress = 0;
        private float animationProgressStart = 0;
        private long currentProgressTime = 0;
        private float animatedProgressValue = 0;
        private RectF progressRect = new RectF();
        private int backgroundState = -1;
        private View parent;
        private int size = dp(64);
        private int previousBackgroundState = -2;
        private float animatedAlphaValue = 1.0f;
        private float[] animAlphas = new float[3];
        private float[] alphas = new float[3];
        private float scale = 1.0f;
        private boolean visible;

        private final CombinedDrawable playDrawable;
        private final PlayPauseDrawable playPauseDrawable;

        public PhotoProgressView(View parentView) {
            if (decelerateInterpolator == null) {
                decelerateInterpolator = new DecelerateInterpolator(1.5f);
                progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                progressPaint.setStyle(Paint.Style.STROKE);
                progressPaint.setStrokeCap(Paint.Cap.ROUND);
                progressPaint.setStrokeWidth(dp(3));
                progressPaint.setColor(0xffffffff);
            }
            parent = parentView;
            resetAlphas();

            playPauseDrawable = new PlayPauseDrawable(28);
            playPauseDrawable.setDuration(200);

            Drawable circleDrawable = ContextCompat.getDrawable(parentActivity, R.drawable.circle_big);
            playDrawable = new CombinedDrawable(circleDrawable.mutate(), playPauseDrawable);
        }

        private void updateAnimation(boolean withProgressAnimation) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - lastUpdateTime;
            if (dt > 18) {
                dt = 18;
            }
            lastUpdateTime = newTime;

            boolean postInvalidate = false;

            if (withProgressAnimation) {
                if (animatedProgressValue != 1 || currentProgress != 1) {
                    radOffset += 360 * dt / 3000.0f;
                    float progressDiff = currentProgress - animationProgressStart;
                    if (Math.abs(progressDiff) > 0) {
                        currentProgressTime += dt;
                        if (currentProgressTime >= 300) {
                            animatedProgressValue = currentProgress;
                            animationProgressStart = currentProgress;
                            currentProgressTime = 0;
                        } else {
                            animatedProgressValue = animationProgressStart + progressDiff * decelerateInterpolator.getInterpolation(currentProgressTime / 300.0f);
                        }
                    }
                    postInvalidate = true;
                }

                if (animatedAlphaValue > 0 && previousBackgroundState != -2) {
                    animatedAlphaValue -= dt / 200.0f;
                    if (animatedAlphaValue <= 0) {
                        animatedAlphaValue = 0.0f;
                        previousBackgroundState = -2;
                    }
                    postInvalidate = true;
                }
            }

            for (int i = 0; i < alphas.length; i++) {
                if (alphas[i] > animAlphas[i]) {
                    animAlphas[i] = Math.min(1f, animAlphas[i] + dt / 200f);
                    postInvalidate = true;
                } else if (alphas[i] < animAlphas[i]) {
                    animAlphas[i] = Math.max(0f, animAlphas[i] - dt / 200f);
                    postInvalidate = true;
                }
            }

            if (postInvalidate) {
                parent.postInvalidateOnAnimation();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (!animated) {
                animatedProgressValue = value;
                animationProgressStart = value;
            } else {
                animationProgressStart = animatedProgressValue;
            }
            currentProgress = value;
            currentProgressTime = 0;
            parent.invalidate();
        }

        public void setBackgroundState(int state, boolean animated, boolean animateIcon) {
            if (backgroundState == state) {
                return;
            }
            if (playPauseDrawable != null) {
                boolean animatePlayPause = animateIcon && (backgroundState == PROGRESS_PLAY || backgroundState == PROGRESS_PAUSE);
                if (state == PROGRESS_PLAY) {
                    playPauseDrawable.setPause(false, animatePlayPause);
                } else if (state == PROGRESS_PAUSE) {
                    playPauseDrawable.setPause(true, animatePlayPause);
                }
                playPauseDrawable.setParent(parent);
                playPauseDrawable.invalidateSelf();
            }
            lastUpdateTime = System.currentTimeMillis();
            if (animated && backgroundState != state) {
                previousBackgroundState = backgroundState;
                animatedAlphaValue = 1.0f;
            } else {
                previousBackgroundState = -2;
            }
            onBackgroundStateUpdated(backgroundState = state);
            parent.invalidate();
        }

        protected void onBackgroundStateUpdated(int state) {
        }

        public void setAlpha(float value) {
            setIndexedAlpha(0, value, false);
        }

        public void setScale(float value) {
            scale = value;
        }

        public void setIndexedAlpha(int index, float alpha, boolean animated) {
            if (alphas[index] != alpha) {
                alphas[index] = alpha;
                if (!animated) {
                    animAlphas[index] = alpha;
                }
                checkVisibility();
                parent.invalidate();
            }
        }

        public void resetAlphas() {
            for (int i = 0; i < alphas.length; i++) {
                alphas[i] = animAlphas[i] = 1.0f;
            }
            checkVisibility();
        }

        private float calculateAlpha() {
            float alpha = 1.0f;
            for (int i = 0; i < animAlphas.length; i++) {
                if (i == 2) {
                    alpha *= AndroidUtilities.accelerateInterpolator.getInterpolation(animAlphas[i]);
                } else {
                    alpha *= animAlphas[i];
                }
            }
            return alpha;
        }

        private void checkVisibility() {
            boolean newVisible = true;
            for (int i = 0; i < alphas.length; i++) {
                if (alphas[i] != 1.0f) {
                    newVisible = false;
                    break;
                }
            }
            if (newVisible != visible) {
                visible = newVisible;
                onVisibilityChanged(visible);
            }
        }

        protected void onVisibilityChanged(boolean visible) {
        }

        public boolean isVisible() {
            return visible;
        }

        public int getX() {
            return (containerView.getWidth() - (int) (size * scale)) / 2;
        }

        public int getY() {
            int y = ((AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - (int) (size * scale)) / 2;
            y += currentPanTranslationY;
            if (sendPhotoType == SELECT_TYPE_AVATAR) {
                y -= dp(38);
            }
            return y;
        }

        public void onDraw(Canvas canvas) {
            int sizeScaled = (int) (size * scale);
            int x = getX();
            int y = getY();

            final float alpha = calculateAlpha();

            if (previousBackgroundState >= 0 && previousBackgroundState < progressDrawables.length + 2) {
                Drawable drawable;
                if (previousBackgroundState < progressDrawables.length) {
                    drawable = progressDrawables[previousBackgroundState];
                } else {
                    drawable = playDrawable;
                }
                if (drawable != null) {
                    drawable.setAlpha((int) (255 * animatedAlphaValue * alpha));
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }

            if (backgroundState >= 0 && backgroundState < progressDrawables.length + 2) {
                Drawable drawable;
                if (backgroundState < progressDrawables.length) {
                    drawable = progressDrawables[backgroundState];
                } else {
                    drawable = playDrawable;
                }
                if (drawable != null) {
                    if (previousBackgroundState != -2) {
                        drawable.setAlpha((int) (255 * (1.0f - animatedAlphaValue) * alpha));
                    } else {
                        drawable.setAlpha((int) (255 * alpha));
                    }
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }

            if (backgroundState == PROGRESS_EMPTY || backgroundState == PROGRESS_CANCEL || previousBackgroundState == PROGRESS_EMPTY || previousBackgroundState == PROGRESS_CANCEL) {
                int diff = dp(4);
                if (previousBackgroundState != -2) {
                    progressPaint.setAlpha((int) (255 * animatedAlphaValue * alpha));
                } else {
                    progressPaint.setAlpha((int) (255 * alpha));
                }
                progressRect.set(x + diff, y + diff, x + sizeScaled - diff, y + sizeScaled - diff);
                canvas.drawArc(progressRect, -90 + radOffset, Math.max(4, 360 * animatedProgressValue), false, progressPaint);
                updateAnimation(true);
            } else {
                updateAnimation(false);
            }
        }
    }

    public static class PlaceProviderObject {
        public ImageReceiver imageReceiver;
        public int viewX;
        public int viewY;
        public int viewY2;
        public View parentView;
        public ImageReceiver.BitmapHolder thumb;

        public long dialogId;
        public int index;
        public long size;
        public int[] radius;
        public int clipBottomAddition;
        public int clipTopAddition;
        public float scale = 1.0f;
        public boolean isEvent;
        public ClippingImageView animatingImageView;
        public int animatingImageViewYOffset;
        public boolean allowTakeAnimation = true;
        public boolean canEdit;
        public int starOffset;
        public boolean fadeIn;
        public boolean keepImageReceiverVisible;
    }

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        @Override
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview, boolean closing) {
            return null;
        }

        @Override
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            return null;
        }

        @Override
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {

        }

        @Override
        public void willHidePhotoViewer() {

        }

        @Override
        public int setPhotoUnchecked(Object photoEntry) {
            return -1;
        }

        @Override
        public boolean isPhotoChecked(int index) {
            return false;
        }

        @Override
        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        @Override
        public boolean cancelButtonPressed() {
            return true;
        }

        @Override
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, int scheduleRepeatPeriod, boolean forceDocument) {

        }

        @Override
        public void replaceButtonPressed(int index, VideoEditedInfo videoEditedInfo) {

        }

        @Override
        public boolean canEdit(int index) {
            return false;
        }

        @Override
        public boolean canReplace(int index) {
            return false;
        }

        @Override
        public int getSelectedCount() {
            return 0;
        }

        @Override
        public void updatePhotoAtIndex(int index) {

        }

        @Override
        public boolean allowSendingSubmenu() {
            return true;
        }

        @Override
        public boolean allowCaption() {
            return true;
        }

        @Override
        public boolean scaleToFill() {
            return false;
        }

        @Override
        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        @Override
        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        @Override
        public boolean canScrollAway() {
            return true;
        }

        @Override
        public void needAddMorePhotos() {

        }

        @Override
        public int getPhotoIndex(int index) {
            return -1;
        }

        @Override
        public void deleteImageAtIndex(int index) {

        }

        @Override
        public String getDeleteMessageString() {
            return null;
        }

        @Override
        public boolean canCaptureMorePhotos() {
            return true;
        }

        @Override
        public void openPhotoForEdit(String file, String thumb, boolean isVideo) {

        }

        @Override
        public int getTotalImageCount() {
            return -1;
        }

        @Override
        public boolean loadMore() {

            return false;
        }

        @Override
        public CharSequence getTitleFor(int i) {
            return null;
        }

        @Override
        public CharSequence getSubtitleFor(int i) {
            return null;
        }

        @Override
        public MessageObject getEditingMessageObject() {
            return null;
        }

        @Override
        public void onCaptionChanged(CharSequence caption) {
        }

        @Override
        public boolean closeKeyboard() {
            return false;
        }

        @Override
        public boolean validateGroupId(long groupId) {
            return true;
        }

        @Override
        public void onApplyCaption(CharSequence caption) {

        }

        @Override
        public void onOpen() {

        }

        @Override
        public void onClose() {

        }
    }

    public interface PhotoViewerProvider {
        default void spoilerPressed() { }
        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview, boolean closing);
        ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index);
        void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index);
        void willHidePhotoViewer();
        boolean isPhotoChecked(int index);
        int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo);
        int setPhotoUnchecked(Object photoEntry);
        boolean cancelButtonPressed();
        void needAddMorePhotos();
        void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, int scheduleRepeatPeriod, boolean forceDocument);
        void replaceButtonPressed(int index, VideoEditedInfo videoEditedInfo);
        boolean canEdit(int index);
        boolean canReplace(int index);
        int getSelectedCount();
        void updatePhotoAtIndex(int index);
        boolean allowSendingSubmenu();
        boolean allowCaption();
        boolean scaleToFill();
        ArrayList<Object> getSelectedPhotosOrder();
        HashMap<Object, Object> getSelectedPhotos();
        boolean canScrollAway();
        int getPhotoIndex(int index);
        void deleteImageAtIndex(int index);
        String getDeleteMessageString();
        boolean canCaptureMorePhotos();
        void openPhotoForEdit(String file, String thumb, boolean isVideo);
        int getTotalImageCount();
        boolean loadMore();
        CharSequence getTitleFor(int index);
        CharSequence getSubtitleFor(int index);
        MessageObject getEditingMessageObject();
        void onCaptionChanged(CharSequence caption);
        boolean closeKeyboard();
        boolean validateGroupId(long groupId);
        void onApplyCaption(CharSequence caption);

        void onOpen();
        void onClose();

        default void onPreOpen() {}
        default void onPreClose() {}
        default void onEditModeChanged(boolean isEditMode) {}
        default boolean onDeletePhoto(int index) {
            return true;
        }
        default boolean canLoadMoreAvatars() {
            return true;
        }
        default void onReleasePlayerBeforeClose(int currentIndex) {};
        default long getDialogId() { return 0; }
        default boolean canSchedule() { return false; }
        default boolean canSetTimer() { return false; }

        default boolean forceAllInGroup() {
            return false;
        }

        default boolean isCaptionAbove() {
            return false;
        }
        default boolean canMoveCaptionAbove() {
            return false;
        }
        default void moveCaptionAbove(boolean above) {}

        default boolean isEditingMessage() {
            return false;
        }
        default boolean isEditingSticker() {
            return false;
        }

        default boolean isEditingMessageResend() {
            return false;
        }

        default void onPollAttachReplace() {

        }

        default void onPollAttachDelete() {

        }

        default boolean allowLivePhotos() {
            return false;
        }
        default void updatedLivePhotos() {}
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {

        private Paint paint = new Paint();
        private boolean ignoreLayout;
        private boolean captionAbove;

        public FrameLayoutDrawer(Context context, Activity activity) {
            super(context, activity, false);
            setWillNotDraw(false);
            paint.setColor(0x33000000);
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }

        @Override
        public int getBottomPadding() {
            return pickerView.getHeight();
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            textSelectionHelper.getOverlayView(getContext()).checkCancelAction(ev);
            if (textSelectionHelper.isInSelectionMode()) {
                if (textSelectionHelper.getOverlayView(getContext()).onTouchEvent(ev)) {
                    return true;
                }
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            View overlay = textSelectionHelper.getOverlayView(windowView.getContext());
            overlay.draw(canvas);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (getLayoutParams().height > 0) {
                heightSize = getLayoutParams().height;
            }

            setMeasuredDimension(widthSize, heightSize);

            if (!isCurrentVideo) {
                ignoreLayout = true;
                if (needCaptionLayout) {
                    final int maxLines = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 5 : 10;
                    captionTextViewSwitcher.getCurrentView().setMaxLines(maxLines);
                    captionTextViewSwitcher.getNextView().setMaxLines(maxLines);
                } else {
                    captionTextViewSwitcher.getCurrentView().setMaxLines(Integer.MAX_VALUE);
                    captionTextViewSwitcher.getNextView().setMaxLines(Integer.MAX_VALUE);
                }
                ignoreLayout = false;
            }

            final int bottomLayoutHeight = bottomLayout.getVisibility() != GONE ? dp(48) : 0;

            final int groupedPhotosHeight;
            if (groupedPhotosListView != null && groupedPhotosListView.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) groupedPhotosListView.getLayoutParams();
                lp.bottomMargin = bottomLayoutHeight;
                measureChildWithMargins(groupedPhotosListView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                groupedPhotosHeight = groupedPhotosListView.getMeasuredHeight();

                ignoreLayout = true;
                if (!AndroidUtilities.isTablet() && heightSize < widthSize) {
                    if (groupedPhotosListView.getVisibility() != INVISIBLE) {
                        groupedPhotosListView.setVisibility(INVISIBLE);
                    }
                } else {
                    if (groupedPhotosListView.getVisibility() != VISIBLE) {
                        groupedPhotosListView.setVisibility(VISIBLE);
                    }
                }
                ignoreLayout = false;
            } else {
                groupedPhotosHeight = 0;
            }

            if (videoPlayerControlFrameLayout != null) {
                videoPlayerControlFrameLayout.parentWidth = widthSize;
                videoPlayerControlFrameLayout.parentHeight = heightSize;
            }

            widthSize -= (getPaddingRight() + getPaddingLeft());
            heightSize -= getPaddingBottom();

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE || child == groupedPhotosListView) {
                    continue;
                }
                if (child == aspectRatioFrameLayout) {
                    int heightSpec = MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0), MeasureSpec.EXACTLY);
                    child.measure(widthMeasureSpec, heightSpec);
                } else if (child == paintingOverlay) {
                    int width;
                    int height;
                    if (aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == VISIBLE) {
                        View view = usedSurfaceView ? videoSurfaceView : videoTextureView;
                        width = view.getMeasuredWidth();
                        height = view.getMeasuredHeight();
                    } else {
                        width = centerImage.getBitmapWidth();
                        height = centerImage.getBitmapHeight();
                    }
                    if (width == 0 || height == 0) {
                        width = widthSize;
                        height = heightSize;
                    }
                    paintingOverlay.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                } else if (captionEdit.editText.isPopupView(child) || topCaptionEdit.editText.isPopupView(child)) {
                    int inputFieldHeight = 0;
                    if (inBubbleMode) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize - inputFieldHeight, MeasureSpec.EXACTLY));
                    } else if (AndroidUtilities.isInMultiwindow) {
                        if (AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Math.min(dp(320), heightSize - inputFieldHeight - AndroidUtilities.statusBarHeight), MeasureSpec.EXACTLY));
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize - inputFieldHeight - AndroidUtilities.statusBarHeight, MeasureSpec.EXACTLY));
                        }
                    } else {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height + AndroidUtilities.navigationBarHeight, MeasureSpec.EXACTLY));
                    }
                } else if (child == captionScrollView) {
                    int bottomMargin = bottomLayoutHeight;
                    if (dontChangeCaptionPosition) {
                        if (captionAbove) {
                            bottomMargin += groupedPhotosHeight;
                        }
                    } else if (groupedPhotosListView.hasPhotos() && (AndroidUtilities.isTablet() || heightSize > widthSize)) {
                        bottomMargin += groupedPhotosHeight;
                        captionAbove = true;
                    } else {
                        captionAbove = false;
                    }
                    final int topMargin = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                    final int height = heightSize - topMargin - bottomMargin;
                    ((MarginLayoutParams) captionScrollView.getLayoutParams()).bottomMargin = bottomMargin;
                    child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                } else if (child == topCaptionEditContainer || child == topBulletinUnderCaption) {
                    final int topMargin = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                    int heightSpec = MeasureSpec.makeMeasureSpec(heightSize - topMargin, MeasureSpec.EXACTLY);
                    child.measure(widthMeasureSpec, heightSpec);
                } else if (child == topCaptionEdit.mentionContainer) {
                    final int topMargin = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                    int heightSpec = MeasureSpec.makeMeasureSpec(heightSize - topMargin, MeasureSpec.EXACTLY);
                    child.measure(widthMeasureSpec, heightSpec);
                } else {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                }
            }
        }

        @Override
        protected void onLayout(boolean changed, int _l, int t, int _r, int _b) {
            if (btnLayout != null && undoBtn != null) {
                int sz = _r - _l - dp(20);
                undoBtn.setTranslationY(-sz / 2f - dp(29 + 18));
                btnLayout.setTranslationY(sz / 2f + dp(29 + 18));
                cutOutBtn.setTranslationY(sz / 2f + dp(29 + 18));
                outlineBtn.setTranslationY(sz / 2f + dp(29 + 18 + 36 + 12));
            }

            final int count = getChildCount();
            int keyboardHeight = measureKeyboardHeight();
            keyboardSize = keyboardHeight;
//            int paddingBottom = keyboardHeight <= dp(20) && !AndroidUtilities.isInMultiwindow ? captionEdit.editText.getEmojiPadding() : 0;
            int paddingBottom = 0;

            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                int l, r, b;
                if (child == aspectRatioFrameLayout) {
                    l = _l;
                    r = _r;
                    b = _b;
                } else {
                    l = _l + getPaddingLeft();
                    r = _r - getPaddingRight();
                    b = _b - getPaddingBottom();
                }
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = Gravity.TOP | Gravity.LEFT;
                }

                final int horizontalGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (horizontalGravity) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = (r - l - width) / 2 + lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        childLeft = (r - l - width) - lp.rightMargin;
                        break;
                    case Gravity.LEFT:
                    default:
                        childLeft = lp.leftMargin;
                }

                switch (verticalGravity) {
                    case Gravity.CENTER_VERTICAL:
                        childTop = ((b - paddingBottom) - t - height) / 2 + lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        childTop = ((b - paddingBottom) - t) - height - (lp.bottomMargin);
                        break;
                    default:
                        childTop = lp.topMargin;
                        break;
                }

                if (child == captionEdit.mentionContainer) {
                } else if (child == topCaptionEdit.mentionContainer) {
                    childTop += actionBar.getMeasuredHeight();
                } else if (captionEdit.editText.isPopupView(child) || topCaptionEdit.editText.isPopupView(child)) {
                    childTop = (_b - t) - height + (!inBubbleMode && !AndroidUtilities.isInMultiwindow ? AndroidUtilities.navigationBarHeight : 0);
                } else if (child == selectedPhotosListView) {
                    childTop = actionBar.getMeasuredHeight() + dp(5);
                } else if (child == muteButton || child == livePhotoButton || child == editCoverButton) {
                    int top;
                    if (videoTimelineViewContainer != null && videoTimelineViewContainer.getVisibility() == VISIBLE) {
                        top = videoTimelineViewContainer.getTop();
                    } else if (!captionAbove && captionEdit != null && captionEdit.getVisibility() == VISIBLE) {
                        top = pickerView.getTop() - dp(56);
                    } else {
                        top = pickerView.getTop();
                        if (child == livePhotoButton) {
                            top -= dp(50);
                        }
                    }
                    childTop = top - dp(sendPhotoType == 4 || sendPhotoType == 5 ? 40 : 15) + dp(12) - child.getMeasuredHeight();
                } else if (livePhotoHints != null && livePhotoHints.contains(child)) {
                    int top;
                    if (videoTimelineViewContainer != null && videoTimelineViewContainer.getVisibility() == VISIBLE) {
                        top = videoTimelineViewContainer.getTop();
                    } else {
                        top = pickerView.getTop();
                        top -= dp(50);
                    }
                    childTop = top - dp(sendPhotoType == 4 || sendPhotoType == 5 ? 40 : 15) + dp(12) - dp(32 + 4) - child.getMeasuredHeight();
                } else if (muteHints != null && muteHints.contains(child)) {
                    final int top;
                    if (videoTimelineViewContainer != null && videoTimelineViewContainer.getVisibility() == VISIBLE) {
                        top = videoTimelineViewContainer.getTop();
                    } else {
                        top = pickerView.getTop();
                    }
                    childTop = top - dp(sendPhotoType == 4 || sendPhotoType == 5 ? 40 : 15) + dp(12) - dp(32 + 4) - child.getMeasuredHeight();
                } else if (child == videoTimelineViewContainer) {
                    childTop -= pickerView.getHeight();
                    if (sendPhotoType == SELECT_TYPE_AVATAR) {
                        childTop -= dp(52);
                    } else if (captionEdit.getVisibility() == View.VISIBLE) {
                        childTop -= dp(56);
                    }
                } else if (child == captionEditContainer) {
                    childTop = (b - t) - height - (lp.bottomMargin);
                    childTop -= pickerView.getHeight();
                } else if (child == topCaptionEditContainer || child == topBulletinUnderCaption) {
                    childTop = actionBar.getMeasuredHeight();
                } else if (child == bottomBulletinUnderCaption) {
                    childTop = (b - t) - height - (lp.bottomMargin);
                    childTop -= pickerView.getHeight();
                } else if (child == videoAvatarTooltip) {
                    childTop -= pickerView.getHeight() + dp(31);
                }
                child.layout(childLeft + l, childTop, childLeft + width + l, childTop + height);
            }

            notifyHeightChanged();
            updateExclusionRects();
        }

        private ArrayList<Rect> exclusionRects;

        public void updateExclusionRects() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (exclusionRects == null) {
                    exclusionRects = new ArrayList<>();
                }
                exclusionRects.clear();
                if (currentEditMode == EDIT_MODE_CROP || switchingToMode == EDIT_MODE_CROP) {
                    int h = getMeasuredHeight(), w = getMeasuredWidth();
//                    exclusionRects.add(new Rect(0, 0, AndroidUtilities.dp(50), h));
//                    exclusionRects.add(new Rect(0, 0, w, AndroidUtilities.dp(100)));
//                    exclusionRects.add(new Rect(w - AndroidUtilities.dp(50), 0, w, h));
                    exclusionRects.add(new Rect(0, (h - dp(200)) / 2, dp(100), (h + dp(200)) / 2));
                    exclusionRects.add(new Rect(w - dp(100), (h - dp(200)) / 2, w, (h + dp(200)) / 2));
                }
                setSystemGestureExclusionRects(exclusionRects);
                invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);

            if (isStatusBarVisible() && AndroidUtilities.statusBarHeight != 0 && actionBar != null) {
                paint.setAlpha((int) (255 * actionBar.getAlpha() * 0.498f));
                if (getPaddingRight() > 0) {
                    canvas.drawRect(getMeasuredWidth() - getPaddingRight(), 0, getMeasuredWidth(), getMeasuredHeight(), paint);
                }
                if (getPaddingLeft() > 0) {
                    canvas.drawRect(0, 0, getPaddingLeft(), getMeasuredHeight(), paint);
                }
                if (getPaddingBottom() > 0) {
                    float offset = dpf2(24) * (1f - actionBar.getAlpha());
                    canvas.drawRect(0, getMeasuredHeight() - getPaddingBottom() + offset, getMeasuredWidth(), getMeasuredHeight() + offset, paint);
                }
            }
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);

            if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
                int h = (int) (photoViewerWebView.getWebView().getMeasuredHeight() * (scale - 1.0f)) / 2;
                if (videoForwardDrawable != null && videoForwardDrawable.isAnimating()) {
                    videoForwardDrawable.setBounds(photoViewerWebView.getLeft(), photoViewerWebView.getWebView().getTop() - h + (int) (translationY / scale), photoViewerWebView.getRight(), photoViewerWebView.getWebView().getBottom() + h + (int) (translationY / scale));
                    videoForwardDrawable.draw(canvas);
                }
                if (seekSpeedDrawable != null && seekSpeedDrawable.isShown()) {
                    seekSpeedDrawable.setBounds(photoViewerWebView.getLeft(), (int) (AndroidUtilities.statusBarHeight + dp(90) * actionBar.getAlpha()), photoViewerWebView.getRight(), photoViewerWebView.getWebView().getBottom() + h + (int) (translationY / scale));
                    seekSpeedDrawable.draw(canvas);
                }
            }
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            View overlay = textSelectionHelper.getOverlayView(windowView.getContext());
            if (child == overlay || child == stickerMakerBackgroundView) {
                return false;
            }

            if (child == leftPaintingOverlay || child == rightPaintingOverlay) {
                return false;
            }
//            if (child != navigationBar && (captionEdit == null || !captionEdit.editText.isPopupView(child))) {
//                canvas.save();
////                canvas.clipRect(0, 0, getWidth(), getHeight());
//            }
            boolean result = this.drawChildInternal(canvas, child, drawingTime);
//            if (child != navigationBar && (captionEdit == null || !captionEdit.editText.isPopupView(child))) {
//                canvas.restore();
//            }
            return result;
        }

        protected boolean drawChildInternal(Canvas canvas, View child, long drawingTime) {
            if (child == miniProgressView) {
                return false;
            }

            if (child == videoTimelineViewContainer && videoTimelineViewContainer.getTranslationY() > 0 && pickerView.getTranslationY() == 0) {
                canvas.save();
                canvas.clipRect(videoTimelineViewContainer.getX(), videoTimelineViewContainer.getY(), videoTimelineViewContainer.getX() + videoTimelineViewContainer.getMeasuredWidth(), videoTimelineViewContainer.getBottom());
                boolean b = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return b;
            }
            try {
                return child != aspectRatioFrameLayout && child != paintingOverlay && super.drawChild(canvas, child, drawingTime);
            } catch (Throwable ignore) {
                return true;
            }
        }

        @Override
        public void requestLayout() {
            if (ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Bulletin.addDelegate(this, new Bulletin.Delegate() {
                @Override
                public int getBottomOffset(int tag) {
                    int offset = 0;
                    if (editing) {
                        if (captionEdit != null) {
                            offset += captionEdit.keyboardNotifier.getKeyboardHeight();
                            if (captionEdit.getVisibility() == VISIBLE && !(placeProvider != null && placeProvider.isCaptionAbove())) {
                                offset += captionEdit.getEditTextHeight() + dp(12);
                            }
                        }
                        if (pickerView != null && pickerView.getVisibility() == VISIBLE && (captionEdit == null || !captionEdit.keyboardNotifier.keyboardVisible())) {
                            offset += pickerView.getHeight();
                        }
                    } else {
                        if (bottomLayout != null && bottomLayout.getVisibility() == VISIBLE) {
                            offset += bottomLayout.getHeight() * bottomLayout.getAlpha();
                        }
                        if (groupedPhotosListView != null && groupedPhotosListView.hasPhotos() && (AndroidUtilities.isTablet() || containerView.getMeasuredHeight() > containerView.getMeasuredWidth())) {
                            offset += groupedPhotosListView.getHeight() * groupedPhotosListView.getAlpha();
                        }
                    }
                    return offset;
                }

                @Override
                public int getTopOffset(int tag) {
                    final int topMargin = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                    return topMargin + (int) (topCaptionEdit.getAlpha() * topCaptionEdit.getEditTextHeight());
                }
            });
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Bulletin.removeDelegate(this);
        }

        @Override
        public void notifyHeightChanged() {
            super.notifyHeightChanged();
            if (isCurrentVideo) {
                photoProgressViews[0].setIndexedAlpha(2, getKeyboardHeight() <= dp(20) ? 1.0f : 0.0f, true);
            }
        }
    }

    private static final Property<VideoPlayerControlFrameLayout, Float> VPC_PROGRESS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            VPC_PROGRESS = new FloatProperty<VideoPlayerControlFrameLayout>("progress") {
                @Override
                public void setValue(VideoPlayerControlFrameLayout object, float value) {
                    object.setProgress(value);
                }

                @Override
                public Float get(VideoPlayerControlFrameLayout object) {
                    return object.getProgress();
                }
            };
        } else {
            VPC_PROGRESS = new Property<VideoPlayerControlFrameLayout, Float>(Float.class, "progress") {
                @Override
                public void set(VideoPlayerControlFrameLayout object, Float value) {
                    object.setProgress(value);
                }

                @Override
                public Float get(VideoPlayerControlFrameLayout object) {
                    return object.getProgress();
                }
            };
        }
    }

    private class VideoPlayerControlFrameLayout extends FrameLayout {

        private float progress = 1f;
        private boolean seekBarTransitionEnabled;
        private boolean translationYAnimationEnabled = true;
        private boolean ignoreLayout;
        private int parentWidth;
        private int parentHeight;

        private int lastTimeWidth;
        private FloatValueHolder timeValue = new FloatValueHolder(0);
        private SpringAnimation timeSpring = new SpringAnimation(timeValue)
                .setSpring(new SpringForce(0)
                        .setStiffness(750f)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY))
                .addUpdateListener((animation, value, velocity) -> {
                    int extraWidth;
                    if (parentWidth > parentHeight) {
                        extraWidth = dp(48);
                    } else {
                        extraWidth = 0;
                    }

                    videoPlayerSeekbar.setSize((int) (getMeasuredWidth() - dp(2 + 14) - value - extraWidth), getMeasuredHeight());
                });

        public VideoPlayerControlFrameLayout(@NonNull Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (progress < 1f) {
                return false;
            }
            if (videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - dp(2), event.getY())) {
                getParent().requestDisallowInterceptTouchEvent(true);
                videoPlayerSeekbarView.invalidate();
                return true;
            }
            return true;
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();

            timeValue.setValue(0);
            lastTimeWidth = 0;
        }

        @Override
        public void requestLayout() {
            if (ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int extraWidth;
            ignoreLayout = true;
            LayoutParams layoutParams = (LayoutParams) videoPlayerTime.getLayoutParams();
            if (parentWidth > parentHeight) {
                if (exitFullscreenButton.getVisibility() != VISIBLE) {
                    exitFullscreenButton.setVisibility(VISIBLE);
                }
                extraWidth = dp(48);
                layoutParams.rightMargin = dp(47);
            } else {
                if (exitFullscreenButton.getVisibility() != INVISIBLE) {
                    exitFullscreenButton.setVisibility(INVISIBLE);
                }
                extraWidth = 0;
                layoutParams.rightMargin = dp(12);
            }
            ignoreLayout = false;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            long duration;
            if (videoPlayer != null) {
                duration = videoPlayer.getDuration();
                if (duration == C.TIME_UNSET) {
                    duration = 0;
                }
            } else if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
                duration = photoViewerWebView.getVideoDuration();
            } else {
                duration = 0;
            }
            duration /= 1000;

            String durationStr;
            if (duration / 60 > 60) {
                durationStr = String.format(Locale.ROOT, "%02d:%02d:%02d", (duration / 60) / 60, (duration / 60) % 60, duration % 60);
            } else {
                durationStr = String.format(Locale.ROOT, "%02d:%02d", duration / 60, duration % 60);
            }

            int size = (int) Math.ceil(videoPlayerTime.getPaint().measureText(String.format(Locale.ROOT, "%1$s / %1$s", durationStr)));
            timeSpring.cancel();
            if (lastTimeWidth != 0 && timeValue.getValue() != size) {
                timeSpring.getSpring().setFinalPosition(size);
                timeSpring.start();
            } else {
                videoPlayerSeekbar.setSize(getMeasuredWidth() - dp(2 + 14) - size - extraWidth, getMeasuredHeight());
                timeValue.setValue(size);
            }
            lastTimeWidth = size;
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            float progress = 0;
            if (videoPlayer != null) {
                progress = videoPlayer.getCurrentPosition() / (float) videoPlayer.getDuration();
            }
            if (playerWasReady) {
                videoPlayerSeekbar.setProgress(progress);
            }
            videoTimelineView.setProgress(progress);
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            if (this.progress != progress) {
                this.progress = progress;
                onProgressChanged(progress);
            }
        }

        private void onProgressChanged(float progress) {
            videoPlayerTime.setAlpha(progress);
            exitFullscreenButton.setAlpha(progress);
            if (seekBarTransitionEnabled) {
                videoPlayerTime.setPivotX(videoPlayerTime.getWidth());
                videoPlayerTime.setPivotY(videoPlayerTime.getHeight());
                videoPlayerTime.setScaleX(1f - 0.1f * (1f - progress));
                videoPlayerTime.setScaleY(1f - 0.1f * (1f - progress));
                videoPlayerSeekbar.setTransitionProgress(1f - progress);
            } else {
                if (translationYAnimationEnabled) {
                    setTranslationY(dpf2(24) * (1f - progress));
                }
                videoPlayerSeekbarView.setAlpha(progress);
            }
        }

        public boolean isSeekBarTransitionEnabled() {
            return seekBarTransitionEnabled;
        }

        public void setSeekBarTransitionEnabled(boolean seekBarTransitionEnabled) {
            if (this.seekBarTransitionEnabled != seekBarTransitionEnabled) {
                this.seekBarTransitionEnabled = seekBarTransitionEnabled;
                if (seekBarTransitionEnabled) {
                    setTranslationY(0);
                    videoPlayerSeekbarView.setAlpha(1f);
                } else {
                    videoPlayerTime.setScaleX(1f);
                    videoPlayerTime.setScaleY(1f);
                    videoPlayerSeekbar.setTransitionProgress(0f);
                }
                onProgressChanged(progress);
            }
        }

        public void setTranslationYAnimationEnabled(boolean translationYAnimationEnabled) {
            if (this.translationYAnimationEnabled != translationYAnimationEnabled) {
                this.translationYAnimationEnabled = translationYAnimationEnabled;
                if (!translationYAnimationEnabled) {
                    setTranslationY(0);
                }
                onProgressChanged(progress);
            }
        }
    }

    public static class CaptionTextViewSwitcher extends TextViewSwitcher {

        private boolean inScrollView = false;
        private float alpha = 1.0f;
        private NestedScrollView scrollView;
        private FrameLayout container;

        public CaptionTextViewSwitcher(Context context) {
            super(context);
        }

        public void setScrollView(NestedScrollView scrollView) {
            this.scrollView = scrollView;
        }

        public void setContainer(FrameLayout container) {
            this.container = container;
        }

        @Override
        public void setVisibility(int visibility) {
            setVisibility(visibility, true);
        }

        public void setVisibility(int visibility, boolean withScrollView) {
            super.setVisibility(visibility);
            if (inScrollView && withScrollView) {
                scrollView.setVisibility(visibility);
            }
        }

        @Override
        public void setAlpha(float alpha) {
            this.alpha = alpha;
            if (inScrollView) {
                scrollView.setAlpha(alpha);
            } else {
                super.setAlpha(alpha);
            }
        }

        @Override
        public float getAlpha() {
            if (inScrollView) {
                return alpha;
            } else {
                return super.getAlpha();
            }
        }

        @Override
        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            if (inScrollView) {
                scrollView.invalidate(); // invalidate background drawing
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (container != null && getParent() == container) {
                inScrollView = true;
                scrollView.setVisibility(getVisibility());
                scrollView.setAlpha(alpha);
                super.setAlpha(1.0f);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (inScrollView) {
                inScrollView = false;
                scrollView.setVisibility(View.GONE);
                super.setAlpha(alpha);
            }
        }
    }

    public static class CaptionScrollView extends NestedScrollView {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private final SpringAnimation springAnimation;

        private boolean nestedScrollStarted;
        private float overScrollY;
        private float velocitySign;
        private float velocityY;

        private Method abortAnimatedScrollMethod;
        private OverScroller scroller;

        private boolean isLandscape;
        private int textHash;
        private int prevHeight;

        public float backgroundAlpha = 1f;
        public boolean dontChangeTopMargin;
        private int pendingTopMargin = -1;

        private final CaptionTextViewSwitcher captionTextViewSwitcher;
        private final FrameLayout captionContainer;

        public CaptionScrollView(@NonNull Context context, CaptionTextViewSwitcher switcher, FrameLayout container) {
            super(context);
            this.captionTextViewSwitcher = switcher;
            this.captionContainer = container;

            setClipChildren(false);
            setOverScrollMode(View.OVER_SCROLL_NEVER);

            paint.setColor(Color.BLACK);
            setFadingEdgeLength(dp(12));
            setVerticalFadingEdgeEnabled(true);
            setWillNotDraw(false);

            springAnimation = new SpringAnimation(captionTextViewSwitcher, DynamicAnimation.TRANSLATION_Y, 0);
            springAnimation.getSpring().setStiffness(100f);
            springAnimation.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_PIXELS);
            springAnimation.addUpdateListener((animation, value, velocity) -> {
                overScrollY = value;
                velocityY = velocity;
                onScrollUpdate();
            });
            springAnimation.addEndListener((anm, c, v, a) -> {
                onScrollEnd();
            });
            springAnimation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);

            try {
                abortAnimatedScrollMethod = NestedScrollView.class.getDeclaredMethod("abortAnimatedScroll");
                abortAnimatedScrollMethod.setAccessible(true);
            } catch (Exception e) {
                abortAnimatedScrollMethod = null;
                FileLog.e(e);
            }

            try {
                final Field scrollerField = NestedScrollView.class.getDeclaredField("mScroller");
                scrollerField.setAccessible(true);
                scroller = (OverScroller) scrollerField.get(this);
            } catch (Exception e) {
                scroller = null;
                FileLog.e(e);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getY() < captionContainer.getTop() - getScrollY() + captionTextViewSwitcher.getTranslationY()) {
                return false;
            }
            return super.onTouchEvent(ev);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            updateTopMargin(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void applyPendingTopMargin() {
            dontChangeTopMargin = false;
            if (pendingTopMargin >= 0) {
                ((MarginLayoutParams) captionContainer.getLayoutParams()).topMargin = pendingTopMargin;
                pendingTopMargin = -1;
                requestLayout();
            }
        }

        public int getPendingMarginTopDiff() {
            if (pendingTopMargin >= 0) {
                return pendingTopMargin - ((MarginLayoutParams) captionContainer.getLayoutParams()).topMargin;
            } else {
                return 0;
            }
        }

        public void updateTopMargin() {
            updateTopMargin(getWidth(), getHeight());
        }

        private void updateTopMargin(int width, int height) {
            final int marginTop = calculateNewContainerMarginTop(width, height);
            if (marginTop >= 0) {
                if (dontChangeTopMargin) {
                    pendingTopMargin = marginTop;
                } else {
                    ((MarginLayoutParams) captionContainer.getLayoutParams()).topMargin = marginTop;
                    pendingTopMargin = -1;
                }
            }
        }

        public int calculateNewContainerMarginTop(int width, int height) {
            if (width == 0 || height == 0) {
                return -1;
            }

            final TextView textView = captionTextViewSwitcher.getCurrentView();
            final CharSequence text = textView.getText();

            final int textHash = text.hashCode();
            final boolean isLandscape = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;

            if (this.textHash == textHash && this.isLandscape == isLandscape && this.prevHeight == height) {
                return -1;
            }

            this.textHash = textHash;
            this.isLandscape = isLandscape;
            this.prevHeight = height;

            textView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

            final Layout layout = textView.getLayout();
            final int lineCount = layout.getLineCount();

            if (isLandscape && lineCount <= 2 || !isLandscape && lineCount <= 5) {
                return height - textView.getMeasuredHeight() - captionTextViewSwitcher.getPaddingBottom();
            }

            int i = Math.min(isLandscape ? 2 : 5, lineCount);

            cycle:
            while (i > 1) {
                for (int j = layout.getLineStart(i - 1); j < layout.getLineEnd(i - 1); j++) {
                    if (!Character.isWhitespace(text.charAt(j))) {
                        break cycle;
                    }
                }
                i--;
            }

            final int lineHeight = textView.getPaint().getFontMetricsInt(null);
            return height - lineHeight * i - dp(8);
        }

        public void reset() {
            scrollTo(0, 0);
        }

        public void stopScrolling() {
            if (abortAnimatedScrollMethod != null) {
                try {
                    abortAnimatedScrollMethod.invoke(this);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }

        @Override
        public void fling(int velocityY) {
            super.fling(velocityY);
            this.velocitySign = Math.signum(velocityY);
            this.velocityY = 0f;
        }

        @Override
        public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
            consumed[1] = 0;

            if (nestedScrollStarted && (overScrollY > 0 && dy > 0 || overScrollY < 0 && dy < 0)) {
                final float delta = overScrollY - dy;

                if (overScrollY > 0) {
                    if (delta < 0) {
                        overScrollY = 0;
                        consumed[1] += dy + delta;
                    } else {
                        overScrollY = delta;
                        consumed[1] += dy;
                    }
                } else {
                    if (delta > 0) {
                        overScrollY = 0;
                        consumed[1] += dy + delta;
                    } else {
                        overScrollY = delta;
                        consumed[1] += dy;
                    }
                }
                onScrollUpdate();

                captionTextViewSwitcher.setTranslationY(overScrollY);
                return true;
            }

            return false;
        }

        @Override
        public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
            if (dyUnconsumed != 0) {
                final int topMargin = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                final int dy = Math.round(dyUnconsumed * (1f - Math.abs((-overScrollY / (captionContainer.getTop() - topMargin)))));

                if (dy != 0) {
                    if (!nestedScrollStarted) {
                        if (!springAnimation.isRunning()) {
                            int consumedY;
                            float velocity = scroller != null ? scroller.getCurrVelocity() : Float.NaN;
                            if (!Float.isNaN(velocity)) {
                                final float clampedVelocity = Math.min(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 3000 : 5000, velocity);
                                consumedY = (int) (dy * clampedVelocity / velocity);
                                velocity = clampedVelocity * -velocitySign;
                            } else {
                                consumedY = dy;
                                velocity = 0;
                            }
                            if (consumedY != 0) {
                                overScrollY -= consumedY;
                                captionTextViewSwitcher.setTranslationY(overScrollY);
                            }
                            startSpringAnimationIfNotRunning(velocity);
                        }
                    } else {
                        overScrollY -= dy;
                        captionTextViewSwitcher.setTranslationY(overScrollY);
                    }
                }

                onScrollUpdate();
            }
        }

        private void startSpringAnimationIfNotRunning(float velocityY) {
            if (!springAnimation.isRunning()) {
                springAnimation.setStartVelocity(velocityY);
                springAnimation.start();
            }
        }

        @Override
        public boolean startNestedScroll(int axes, int type) {
            if (type == ViewCompat.TYPE_TOUCH) {
                springAnimation.cancel();
                nestedScrollStarted = true;
                overScrollY = captionTextViewSwitcher.getTranslationY();
                onScrollStart();
            }
            return true;
        }

        @Override
        public void computeScroll() {
            super.computeScroll();
            if (!nestedScrollStarted && overScrollY != 0 && scroller != null && scroller.isFinished()) {
                startSpringAnimationIfNotRunning(0);
            }
            onScrollUpdate();
        }

        @Override
        public void stopNestedScroll(int type) {
            if (nestedScrollStarted && type == ViewCompat.TYPE_TOUCH) {
                nestedScrollStarted = false;
                if (overScrollY != 0 && scroller != null && scroller.isFinished()) {
                    startSpringAnimationIfNotRunning(velocityY);
                }
                onScrollEnd();
            }
        }

        protected void onScrollStart() {}
        protected void onScrollUpdate() {}
        protected void onScrollEnd() {}

        @Override
        protected float getTopFadingEdgeStrength() {
            return 1f;
        }

        @Override
        protected float getBottomFadingEdgeStrength() {
            return 1f;
        }

        @Override
        public void draw(Canvas canvas) {
            final int width = getWidth();
            final int height = getHeight();
            final int scrollY = getScrollY();

            final int saveCount = canvas.save();
            canvas.clipRect(0, scrollY, width, height + scrollY);

            paint.setAlpha((int) (backgroundAlpha * 127));
            canvas.drawRect(0, captionContainer.getTop() + captionTextViewSwitcher.getTranslationY(), width, height + scrollY, paint);

            super.draw(canvas);
            canvas.restoreToCount(saveCount);
        }

        protected boolean isStatusBarVisible() {
            return true;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static volatile PhotoViewer Instance = null;
    private static volatile PhotoViewer PipInstance = null;
    private static volatile PhotoViewer Instance2 = null;

    public static PhotoViewer getPipInstance() {
        return PipInstance;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PhotoViewer();
                }
            }
        }
        return localInstance;
    }

    public static PhotoViewer getInstance2() {
        PhotoViewer localInstance = Instance2;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance2;
                if (localInstance == null) {
                    Instance2 = localInstance = new PhotoViewer();
                }
            }
        }
        return localInstance;
    }

    public boolean isOpenedFullScreenVideo() {
        return openedFullScreenVideo;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public PhotoViewer() {
        blackPaint.setColor(0xff000000);
        videoFrameBitmapPaint.setColor(0xffffffff);
        centerImage.setFileLoadingPriority(FileLoader.PRIORITY_HIGH);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoadFailed) {
            String location = (String) args[0];
            for (int a = 0; a < 3; a++) {
                if (currentFileNames[a] != null && currentFileNames[a].equals(location)) {
                    boolean animated = a == 0 || a == 1 && sideImage == rightImage || a == 2 && sideImage == leftImage;
                    photoProgressViews[a].setProgress(1.0f, animated);
                    checkProgress(a, false, true);
                    break;
                }
            }
        } else if (id == NotificationCenter.customStickerCreated) {
            closePhoto(false, false);
        } else if (id == NotificationCenter.fileLoaded) {
            String location = (String) args[0];
            for (int a = 0; a < 3; a++) {
                if (currentFileNames[a] != null && currentFileNames[a].equals(location)) {
                    boolean animated = a == 0 || a == 1 && sideImage == rightImage || a == 2 && sideImage == leftImage;
                    photoProgressViews[a].setProgress(1.0f, animated);
                    checkProgress(a, false, animated);
                    if (videoPlayer == null && a == 0 && (currentMessageObject != null && currentMessageObject.isVideo() || currentBotInlineResult != null && (currentBotInlineResult.type.equals("video") || MessageObject.isVideoDocument(currentBotInlineResult.document)) || pageBlocksAdapter != null && (pageBlocksAdapter.isVideo(currentIndex) || pageBlocksAdapter.isHardwarePlayer(currentIndex)))) {
                        onActionClick(false);
                    }
                    if (a == 0 && videoPlayer != null) {
                        currentVideoFinishedLoading = true;
                    }
                    break;
                }
            }
        } else if (id == NotificationCenter.fileLoadProgressChanged) {
            String location = (String) args[0];
            for (int a = 0; a < 3; a++) {
                if (currentFileNames[a] != null && currentFileNames[a].equals(location)) {
                    Long loadedSize = (Long) args[1];
                    Long totalSize = (Long) args[2];
                    float loadProgress = Math.min(1f, loadedSize / (float) totalSize);
                    boolean animated = a == 0 || a == 1 && sideImage == rightImage || a == 2 && sideImage == leftImage;
                    photoProgressViews[a].setProgress(loadProgress, animated);
                    if (a == 0 && videoPlayer != null && videoPlayerSeekbar != null) {
                        float bufferedProgress;
                        if (currentVideoFinishedLoading) {
                            bufferedProgress = 1.0f;
                        } else {
                            long newTime = SystemClock.elapsedRealtime();
                            if (Math.abs(newTime - lastBufferedPositionCheck) >= 500) {
                                float progress;
                                if (seekToProgressPending == 0) {
                                    long duration = videoPlayer.getDuration();
                                    long position = videoPlayer.getCurrentPosition();
                                    if (duration >= 0 && duration != C.TIME_UNSET && position >= 0) {
                                        progress = position / (float) duration;
                                    } else {
                                        progress = 0.0f;
                                    }
                                } else {
                                    progress = seekToProgressPending;
                                }
                                bufferedProgress = isStreaming ? FileLoader.getInstance(currentAccount).getBufferedProgressFromPosition(progress, currentFileNames[0]) : 1.0f;
                                lastBufferedPositionCheck = newTime;
                            } else {
                                bufferedProgress = -1;
                            }
                        }
                        if (bufferedProgress != -1) {
                            videoPlayerSeekbar.setBufferedProgress(bufferedProgress);
                            PipVideoOverlay.setBufferedProgress(bufferedProgress);
                            videoPlayerSeekbarView.invalidate();
                        }
                        checkBufferedProgress(loadProgress);
                    }
                }
            }
        } else if (id == NotificationCenter.dialogPhotosUpdate) {
            MessagesController.DialogPhotos dialogPhotos = (MessagesController.DialogPhotos) args[0];
            if (avatarsDialogId == dialogPhotos.dialogId) {
                this.dialogPhotos = dialogPhotos;
                int setToImage = -1;
                ArrayList<TLRPC.Photo> photos = new ArrayList<>(dialogPhotos.photos);
                if (avatarsDialogId > 0) {
                    TLRPC.UserFull fullUser = MessagesController.getInstance(currentAccount).getUserFull(avatarsDialogId);
                    TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(avatarsDialogId);
                    if (fullUser != null && fullUser.personal_photo instanceof TLRPC.TL_photo) {
                        photos.add(0, fullUser.personal_photo);
                    }
                    if (user != null && user.self && UserObject.hasFallbackPhoto(fullUser)) {
                        photos.add(fullUser.fallback_photo);
                    }
                }
                imagesArrLocations.clear();
                imagesArrLocationsSizes.clear();
                imagesArrLocationsVideo.clear();
                imagesArrMessages.clear();
                avatarsArr.clear();

                for (int a = 0; a < photos.size(); a++) {
                    TLRPC.Photo photo = photos.get(a);
                    if (photo == null || photo instanceof TLRPC.TL_photoEmpty || photo.sizes == null) {
                        imagesArrLocations.add(null);
                        imagesArrLocationsSizes.add(null);
                        imagesArrLocationsVideo.add(null);
                        imagesArrMessages.add(null);
                        avatarsArr.add(null);
                        continue;
                    }
                    TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                    TLRPC.VideoSize videoSize = photo.video_sizes.isEmpty() ? null :  FileLoader.getClosestVideoSizeWithSize(photo.video_sizes, 1000);
                    if (sizeFull != null) {
                        if (setToImage == -1 && currentFileLocation != null) {
                            for (int b = 0; b < photo.sizes.size(); b++) {
                                TLRPC.PhotoSize size = photo.sizes.get(b);
                                if (size.location != null && size.location.local_id == currentFileLocation.location.local_id && size.location.volume_id == currentFileLocation.location.volume_id) {
                                    setToImage = imagesArrLocations.size();
                                    break;
                                }
                            }
                        }
                        if (setToImage == -1 && currentFileLocation != null) {
                            for (int b = 0; b < photo.video_sizes.size(); b++) {
                                TLRPC.VideoSize size = photo.video_sizes.get(b);
                                if (size.location != null && size.location.local_id == currentFileLocation.location.local_id && size.location.volume_id == currentFileLocation.location.volume_id) {
                                    setToImage = imagesArrLocations.size();
                                    break;
                                }
                            }
                        }
                        if (photo.dc_id != 0) {
                            sizeFull.location.dc_id = photo.dc_id;
                            sizeFull.location.file_reference = photo.file_reference;
                        }
                        ImageLocation location = ImageLocation.getForPhoto(sizeFull, photo);
                        ImageLocation videoLocation = videoSize != null ? ImageLocation.getForPhoto(videoSize, photo) : location;
                        if (location != null) {
                            imagesArrLocations.add(location);
                            imagesArrLocationsSizes.add(videoLocation != null ? videoLocation.currentSize : null);
                            imagesArrLocationsVideo.add(videoLocation);
                            imagesArrMessages.add(null);
                            avatarsArr.add(photo);
                        }
                    }
                }
                if (!avatarsArr.isEmpty()) {
                    menuItem.showSubItem(gallery_menu_delete);
                } else {
                    menuItem.hideSubItem(gallery_menu_delete);
                }
                needSearchImageInArr = false;
                currentIndex = -1;
                if (setToImage != -1) {
                    setImageIndex(setToImage);
                } else {
                    TLRPC.User user = null;
                    TLRPC.Chat chat = null;
                    if (avatarsDialogId > 0) {
                        user = MessagesController.getInstance(currentAccount).getUser(avatarsDialogId);
                    } else {
                        chat = MessagesController.getInstance(currentAccount).getChat(-avatarsDialogId);
                    }
                    if (user != null || chat != null) {
                        ImageLocation location;
                        if (user != null) {
                            location = ImageLocation.getForUserOrChat(currentAccount, user, ImageLocation.TYPE_BIG);
                        } else {
                            location = ImageLocation.getForUserOrChat(currentAccount, chat, ImageLocation.TYPE_BIG);
                        }
                        if (location != null) {
                            if (!imagesArrLocations.isEmpty() && imagesArrLocations.get(0) != null && imagesArrLocations.get(0).photoId == location.photoId) {
                                imagesArrLocations.remove(0);
                                avatarsArr.remove(0);
                                imagesArrLocationsSizes.remove(0);
                                imagesArrLocationsVideo.remove(0);
                                imagesArrMessages.remove(0);
                            }
                            imagesArrLocations.add(0, location);
                            avatarsArr.add(0, new TLRPC.TL_photoEmpty());
                            imagesArrLocationsSizes.add(0, currentFileLocationVideo != null ? currentFileLocationVideo.currentSize : null);
                            imagesArrLocationsVideo.add(0, currentFileLocationVideo);
                            imagesArrMessages.add(0, null);
                            setImageIndex(0);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.mediaCountDidLoad) {
            long uid = (Long) args[0];
            long topicId = (Long) args[1];
            if (this.topicId == topicId && (uid == currentDialogId || uid == mergeDialogId)) {
                if (currentMessageObject == null || MediaDataController.getMediaType(currentMessageObject.messageOwner) == sharedMediaType) {
                    if (uid == currentDialogId) {
                        totalImagesCount = (Integer) args[2];
                    } else {
                        totalImagesCountMerge = (Integer) args[2];
                    }
                    if (needSearchImageInArr && isFirstLoading) {
                        isFirstLoading = false;
                        loadingMoreImages = true;
                        MediaDataController.getInstance(currentAccount).loadMedia(currentDialogId, 20, 0, 0, sharedMediaType, topicId, 1, classGuid, 0, currentFilterTag, null);
                    } else if (!imagesArr.isEmpty()) {
                        setIsAboutToSwitchToIndex(switchingToIndex, true, true);
                    }
                }
            }
        } else if (id == NotificationCenter.mediaDidLoad) {
            long uid = (Long) args[0];
            int guid = (Integer) args[3];
            if ((uid == currentDialogId || uid == mergeDialogId) && guid == classGuid) {
                loadingMoreImages = false;
                int loadIndex = uid == currentDialogId ? 0 : 1;
                ArrayList<MessageObject> arr = (ArrayList<MessageObject>) args[2];
                endReached[loadIndex] = (Boolean) args[5];
                boolean fromStart = (boolean) args[6];
                if (needSearchImageInArr) {
                    if (arr.isEmpty() && (loadIndex != 0 || mergeDialogId == 0) || currentIndex < 0 || currentIndex >= imagesArr.size()) {
                        needSearchImageInArr = false;
                        return;
                    }
                    int foundIndex = -1;

                    MessageObject currentMessage = imagesArr.get(currentIndex);

                    int added = 0;
                    for (int a = 0; a < arr.size(); a++) {
                        MessageObject message = arr.get(a);
                        if (message.isHiddenSensitive())
                            continue;
                        if (imagesByIdsTemp[loadIndex].indexOfKey(message.getId()) < 0) {
                            imagesByIdsTemp[loadIndex].put(message.getId(), message);
                            if (opennedFromMedia) {
                                imagesArrTemp.add(message);
                                if (message.getId() == currentMessage.getId()) {
                                    foundIndex = added;
                                }
                                added++;
                            } else {
                                added++;
                                imagesArrTemp.add(0, message);
                                if (message.getId() == currentMessage.getId()) {
                                    foundIndex = arr.size() - added;
                                }
                            }
                        }
                    }
                    if (added == 0 && (loadIndex != 0 || mergeDialogId == 0)) {
                        totalImagesCount = imagesArr.size();
                        totalImagesCountMerge = 0;
                    }

                    if (foundIndex != -1) {
                        imagesArr.clear();
                        imagesArr.addAll(imagesArrTemp);
                        for (int a = 0; a < 2; a++) {
                            imagesByIds[a] = imagesByIdsTemp[a].clone();
                            imagesByIdsTemp[a].clear();
                        }
                        imagesArrTemp.clear();
                        needSearchImageInArr = false;
                        currentIndex = -1;
                        if (foundIndex >= imagesArr.size()) {
                            foundIndex = imagesArr.size() - 1;
                        }
                        setImageIndex(foundIndex);
                    } else {
                        int loadFromMaxId;
                        if (opennedFromMedia) {
                            loadFromMaxId = imagesArrTemp.isEmpty() ? 0 : imagesArrTemp.get(imagesArrTemp.size() - 1).getId();
                            if (loadIndex == 0 && endReached[loadIndex] && mergeDialogId != 0) {
                                loadIndex = 1;
                                if (!imagesArrTemp.isEmpty() && imagesArrTemp.get(imagesArrTemp.size() - 1).getDialogId() != mergeDialogId) {
                                    loadFromMaxId = 0;
                                }
                            }
                        } else {
                            loadFromMaxId = imagesArrTemp.isEmpty() ? 0 : imagesArrTemp.get(0).getId();
                            if (loadIndex == 0 && endReached[loadIndex] && mergeDialogId != 0) {
                                loadIndex = 1;
                                if (!imagesArrTemp.isEmpty() && imagesArrTemp.get(0).getDialogId() != mergeDialogId) {
                                    loadFromMaxId = 0;
                                }
                            }
                        }

                        if (!endReached[loadIndex]) {
                            loadingMoreImages = true;
                            MediaDataController.getInstance(currentAccount).loadMedia(loadIndex == 0 ? currentDialogId : mergeDialogId, 40, loadFromMaxId, 0, sharedMediaType, topicId, 1, classGuid, 0, currentFilterTag, null);
                        }
                    }
                } else {
                    int added = 0;
                    for (int i = 0; i < arr.size(); i++) {
                        MessageObject message = arr.get(fromStart ? arr.size() - 1 - i : i);
                        if (imagesByIds[loadIndex].indexOfKey(message.getId()) < 0) {
                            added++;
                            if (opennedFromMedia) {
                                if (fromStart) {
                                    imagesArr.add(0, message);
                                    startOffset--;
                                    currentIndex++;
                                    if (startOffset < 0) {
                                        startOffset = 0;
                                    }
                                } else {
                                    imagesArr.add(message);
                                }
                            } else {
                                imagesArr.add(0, message);
                            }
                            imagesByIds[loadIndex].put(message.getId(), message);
                        }
                    }
                    if (opennedFromMedia) {
                        if (added == 0 && !fromStart) {
                            totalImagesCount = startOffset + imagesArr.size();
                            totalImagesCountMerge = 0;
                        }
                    } else {
                        if (added != 0) {
                            int index = currentIndex;
                            currentIndex = -1;
                            setImageIndex(index + added);
                        } else {
                            totalImagesCount = imagesArr.size();
                            totalImagesCountMerge = 0;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            if (captionTextViewSwitcher != null) {
                captionTextViewSwitcher.invalidateViews();
            }
        } else if (id == NotificationCenter.filePreparingFailed) {
            MessageObject messageObject = (MessageObject) args[0];
            if (loadInitialVideo) {
                loadInitialVideo = false;
                progressView.setVisibility(View.INVISIBLE);
                preparePlayer(currentPlayingVideoQualityFiles, currentPlayingVideoFile, false, false, editState.savedFilterState, false, 0);
            } else if (tryStartRequestPreviewOnFinish) {
                releasePlayer(false);
                tryStartRequestPreviewOnFinish = !MediaController.getInstance().scheduleVideoConvert(videoPreviewMessageObject, true, true, false);
            } else if (messageObject == videoPreviewMessageObject) {
                requestingPreview = false;
                progressView.setVisibility(View.INVISIBLE);
            }
        } else if (id == NotificationCenter.fileNewChunkAvailable) {
            MessageObject messageObject = (MessageObject) args[0];
            if (messageObject == videoPreviewMessageObject) {
                String finalPath = (String) args[1];
                long finalSize = (Long) args[3];
                float progress = (float) args[4];
                photoProgressViews[0].setProgress(progress, true);
                if (finalSize != 0) {
                    requestingPreview = false;
                    photoProgressViews[0].setProgress(1f, true);
                    photoProgressViews[0].setBackgroundState(PROGRESS_PLAY, true, true);
                    preparePlayer(null, Uri.fromFile(new File(finalPath)), false, true, editState.savedFilterState, false, 0);
                }
            }
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            long dialogId = (long) args[0];
            if (currentDialogId != dialogId) return;
            boolean updatedCurrent = false;
            ArrayList<MessageObject> editedMessages = (ArrayList<MessageObject>) args[1];
            for (int i = 0; i < editedMessages.size(); ++i) {
                MessageObject msg = editedMessages.get(i);
                if (msg == null) continue;
                MessageObject myMsg = null;
                int myMsgIndex = -1;
                if (imagesArr != null) {
                    for (int j = 0; j < imagesArr.size(); ++j) {
                        final MessageObject arrMsg = imagesArr.get(j);
                        if (arrMsg != null && arrMsg.getDialogId() == msg.getDialogId() && arrMsg.getId() == msg.getId()) {
                            myMsg = arrMsg;
                            myMsgIndex = j;
                            break;
                        }
                    }
                }
                if (myMsg != null) {
                    imagesArr.set(myMsgIndex, msg);
                    if (currentIndex == myMsgIndex) {
                        updatedCurrent = true;
                    }
                }
            }
            if (updatedCurrent) {
                setImageIndex(currentIndex, false, true, true);
            }
        } else if (id == NotificationCenter.dialogDeleted) {
            long dialogId = (long) args[0];
            if (currentDialogId != dialogId) return;
            closePhoto(true, false);
        } else if (id == NotificationCenter.messagesDeleted) {
            boolean scheduled = (Boolean) args[2];
            if (scheduled) {
                return;
            }

            if (NaConfig.INSTANCE.getEnableSaveDeletedMessages().Bool()) {
                return;
            }

            long channelId = (Long) args[1];
            ArrayList<Integer> markAsDeletedMessages = (ArrayList<Integer>) args[0];
            boolean reset = false;
            boolean resetCurrent = false;
            for (int x = 0; x < 2; x++) {
                ArrayList<MessageObject> arr = x == 0 ? imagesArr : imagesArrTemp;
                SparseArray<MessageObject>[] ids = x == 0 ? imagesByIds : imagesByIdsTemp;
                if (!arr.isEmpty()) {
                    for (int b = 0; b < 2; b++) {
                        if (ids[b].size() > 0) {
                            MessageObject messageObject = ids[b].valueAt(0);
                            if (messageObject.messageOwner.peer_id.channel_id == channelId) {
                                for (int a = 0, N = markAsDeletedMessages.size(); a < N; a++) {
                                    int mid = markAsDeletedMessages.get(a);
                                    MessageObject message = ids[b].get(markAsDeletedMessages.get(a));
                                    if (message != null) {
                                        ids[b].remove(mid);
                                        arr.remove(message);
                                        if (b == 0) {
                                            totalImagesCount--;
                                        } else {
                                            totalImagesCountMerge--;
                                        }
                                        if (message == currentMessageObject) {
                                            resetCurrent = true;
                                        }
                                        reset = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (reset) {
                if (resetCurrent && this == PipInstance) {
                    destroyPhotoViewer();
                } else {
                    if (!imagesArr.isEmpty()) {
                        int index = currentIndex;
                        currentIndex = -1;
                        if (index >= imagesArr.size()) {
                            index = imagesArr.size() - 1;
                        }
                        setImageIndex(index);
                    } else {
                        closePhoto(false, true);
                    }
                }
            }
        }
    }

    private void showDownloadAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, resourcesProvider);
        builder.setTitle(getString(R.string.NagramX));
        builder.setPositiveButton(getString("OK", R.string.OK), null);
        boolean alreadyDownloading = currentMessageObject != null && currentMessageObject.isVideo() && FileLoader.getInstance(currentMessageObject.currentAccount).isLoadingFile(currentFileNames[0]);
        if (alreadyDownloading) {
            builder.setMessage(getString(R.string.PleaseStreamDownload));
        } else {
            builder.setMessage(getString(R.string.PleaseDownload));
        }
        showAlertDialog(builder);
    }

    private void onSharePressed() {
        if (parentActivity == null/* || !allowShare*/) {
            return;
        }
        try {
            File f = null;
            boolean isVideo = false;

            if (currentMessageObject != null) {
                isVideo = currentMessageObject.isVideo();
                        /*if (currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                            AndroidUtilities.openUrl(parentActivity, currentMessageObject.messageOwner.media.webpage.url);
                            return;
                        }*/
                if (!TextUtils.isEmpty(currentMessageObject.messageOwner.attachPath)) {
                    f = new File(currentMessageObject.messageOwner.attachPath);
                    if (!f.exists()) {
                        f = null;
                    }
                }
                if (f == null) {
                    f = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
                }
            } else if (currentFileLocationVideo != null) {
                f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), avatarsDialogId != 0 || isEvent);
                if (f == null || !f.exists()) {
                    f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), false);
                }
            } else if (pageBlocksAdapter != null) {
                f = pageBlocksAdapter.getFile(currentIndex);
            }
            if (f != null && !f.exists()) {
                f = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), f.getName());
            }

            if (f != null && f.exists()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                if (isVideo) {
                    intent.setType("video/mp4");
                } else {
                    if (currentMessageObject != null) {
                        intent.setType(currentMessageObject.getMimeType());
                    } else {
                        intent.setType("image/jpeg");
                    }
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(parentActivity, ApplicationLoader.getApplicationId() + ".provider", f));
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignore) {
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    }
                } else {
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                }

                parentActivity.startActivityForResult(Intent.createChooser(intent, getString("ShareFile", R.string.ShareFile)), 500);
            } else {
                showDownloadAlert();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void setScaleToFill() {
        float bitmapWidth = centerImage.getBitmapWidth();
        float bitmapHeight = centerImage.getBitmapHeight();
        if (bitmapWidth == 0 || bitmapHeight == 0) {
            return;
        }
        float containerWidth = getContainerViewWidth();
        float containerHeight = getContainerViewHeight();
        float scaleFit = Math.min(containerHeight / bitmapHeight, containerWidth / bitmapWidth);
        float width = (int) (bitmapWidth * scaleFit);
        float height = (int) (bitmapHeight * scaleFit);
        scale = Math.max(containerWidth / width, containerHeight / height);
        updateMinMax(scale);
    }

    public void setParentAlert(ChatAttachAlert alert) {
        parentAlert = alert;
        if (parentAlertWindowVisibilityController != null) {
            parentAlertWindowVisibilityController.destroy();
            parentAlertWindowVisibilityController = null;
        }
        if (alert != null) {
            parentAlertWindowVisibilityController = alert.obtainWindowVisibilityController();
        }
    }

    public void setParentActivity(Activity activity) {
        setParentActivity(activity, null, null);
    }

    public void setParentActivity(Activity activity, Theme.ResourcesProvider resourcesProvider) {
        setParentActivity(activity, null, resourcesProvider);
    }

    public void setParentActivity(BaseFragment fragment) {
        setParentActivity(fragment, null);
    }

    public void setParentActivity(BaseFragment fragment, Theme.ResourcesProvider resourcesProvider) {
        setParentActivity(null, fragment, resourcesProvider);
    }

    private WindowVisibilityManager.Controller activityVisibilityController;

    public Activity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(Activity inActivity, BaseFragment fragment, Theme.ResourcesProvider resourcesProvider) {
        if (activityVisibilityController != null) {
            activityVisibilityController.destroy();
            activityVisibilityController = null;
        }
        activityVisibilityController = LaunchActivity.obtainActivityVisibilityController();

        Activity activity = inActivity != null ? inActivity : fragment.getParentActivity();
        Theme.createChatResources(activity, false);
        this.resourcesProvider = resourcesProvider;
        this.parentFragment = fragment;
        currentAccount = UserConfig.selectedAccount;
        centerImage.setCurrentAccount(currentAccount);
        leftImage.setCurrentAccount(currentAccount);
        rightImage.setCurrentAccount(currentAccount);
        if (captionEdit != null) {
            captionEdit.setAccount(currentAccount);
        }
        if (stickerMakerView != null) {
            stickerMakerView.setCurrentAccount(currentAccount);
        }
        if (parentActivity == activity || activity == null) {
            updateColors();
            return;
        }
        inBubbleMode = activity instanceof BubbleActivity;
        parentActivity = activity;
        activityContext = new ContextThemeWrapper(parentActivity, R.style.Theme_TMessages);
        touchSlop = ViewConfiguration.get(parentActivity).getScaledTouchSlop();

        if (progressDrawables == null) {
            Drawable circleDrawable = ContextCompat.getDrawable(parentActivity, R.drawable.circle_big);
            progressDrawables = new Drawable[]{
                    circleDrawable, // PROGRESS_EMPTY
                    ContextCompat.getDrawable(parentActivity, R.drawable.cancel_big), // PROGRESS_CANCEL
                    ContextCompat.getDrawable(parentActivity, R.drawable.load_big), // PROGRESS_LOAD
            };
        }

        scroller = new Scroller(activity);

        windowView = new PhotoViewerWindowView(activity);
        windowView.setBackground(backgroundDrawable);
        windowView.setFocusable(false);

        animatingImageView = new ClippingImageView(activity);
        animatingImageView.setAnimationValues(animationValues, false, false);
        windowView.addView(animatingImageView, LayoutHelper.createFrame(40, 40));

        containerView = new FrameLayoutDrawer(activity, activity);
        containerView.setFocusable(false);
        if (framesRewinder != null) {
            framesRewinder.setParentView(containerView);
        }

        containerView.setClipChildren(true);
        containerView.setClipToPadding(true);
        windowView.setClipChildren(false);
        windowView.setClipToPadding(false);

        blurManager = new BlurringShader.BlurManager(containerView);
        blurManager.padding = 1;

        blurredBackgroundSource = new BlurredBackgroundSource() {
            private final BlurringShader.StoryBlurDrawer blur = new BlurringShader.StoryBlurDrawer(blurManager, containerView, BlurringShader.StoryBlurDrawer.BLUR_TYPE_BACKGROUND);

            @Override
            public BlurredBackgroundDrawable createDrawable() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    BlurredBackgroundDrawableRenderNode d = new BlurredBackgroundDrawableRenderNode(this);
                    iBlur3BlurredDrawables.add(d);
                    return d;
                } else {
                    return new BlurredBackgroundDrawableSource(this);
                }
            }

            @Override
            public void draw(Canvas canvas, float left, float top, float right, float bottom) {
                canvas.save();
                canvas.clipRect(left, top, right, bottom);
                drawCaptionBlur(canvas, blur, 0xFF262626, 0x33000000, false, true, true);
                canvas.drawColor(0x26000000);
                canvas.restore();
            }
        };
        glassAttachedViews = new ReferenceList<>();
        iBlur3BlurredDrawables = new ReferenceList<>();
        iBlur3FactoryFrostedLiquidGlass = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSource);
        // iBlur3FactoryFrostedLiquidGlass.setLiquidGlassEffectAllowed(LiteMode.isEnabled(LiteMode.FLAG_LIQUID_GLASS));
        iBlur3FactoryFrostedLiquidGlass.setSourceRootView(new ViewPositionWatcher(containerView), containerView);
        iBlur3FactoryFrostedLiquidGlass.setLinkedViewsRef(glassAttachedViews);

        shadowBlurer = new BlurringShader.StoryBlurDrawer(blurManager, containerView, BlurringShader.StoryBlurDrawer.BLUR_TYPE_SHADOW);

        windowView.addView(containerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        ViewCompat.setOnApplyWindowInsetsListener(containerView, (v, newInsetsCompat) -> {
            final Rect oldInsets = new Rect(insets);
            final Insets r = AndroidUtilities.getDefaultWindowInsets(newInsetsCompat, false);

            insets.set(r.left, r.top, r.right, r.bottom);

            int newTopInset = insets.top;
            if (!oldInsets.equals(insets)) {
                if (animationInProgress == 1 || animationInProgress == 3) {
                    animatingImageView.setTranslationX(animatingImageView.getTranslationX() - getLeftInset());
                    animationValues[0][2] = animatingImageView.getTranslationX();
                }
                if (windowView != null) {
                    windowView.requestLayout();
                }
            }

            if (navigationBar != null) {
                navigationBarHeight = insets.bottom;
                ViewGroup.MarginLayoutParams navigationBarLayoutParams = (ViewGroup.MarginLayoutParams) navigationBar.getLayoutParams();
                navigationBarLayoutParams.height = navigationBarHeight;
                navigationBarLayoutParams.bottomMargin = -navigationBarHeight / 2;
                navigationBar.setLayoutParams(navigationBarLayoutParams);
            }
            containerView.setPadding(r.left, 0, r.right, 0);
            if (actionBar != null) {
                AndroidUtilities.cancelRunOnUIThread(updateContainerFlagsRunnable);
                if (isVisible && animationInProgress == 0) {
                    AndroidUtilities.runOnUIThread(updateContainerFlagsRunnable, 200);
                }
            }
            return WindowInsetsCompat.CONSUMED;
        });
        containerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.format = PixelFormat.TRANSLUCENT;
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowLayoutParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
        windowColorModeHdr = false;
        windowDisplayHdrCapable = null;
        if (Build.VERSION.SDK_INT >= 34) {
            windowLayoutParams.setColorMode(ActivityInfo.COLOR_MODE_DEFAULT);
        }
        AndroidUtilities.applyEdgeToEdgeLayoutParams(windowLayoutParams);
        windowLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

        paintingOverlay = new PaintingOverlay(parentActivity);
        containerView.addView(paintingOverlay, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        leftPaintingOverlay = new PaintingOverlay(parentActivity);
        containerView.addView(leftPaintingOverlay, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        rightPaintingOverlay = new PaintingOverlay(parentActivity);
        containerView.addView(rightPaintingOverlay, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        actionBar = new ActionBar(activity) {
            @Override
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                containerView.invalidate();
            }
        };
        actionBar.setOverlayTitleAnimation(true);
        actionBar.setTitleColor(0xffffffff);
        actionBar.setSubtitleColor(0xffffffff);
        actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        actionBar.setOccupyStatusBar(isStatusBarVisible());
        actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
        actionBar.setItemsColor(Color.WHITE, false);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBarBackButton = actionBar.getBackButton();
        actionBarBackButtonDrawableDeafult = actionBarBackButton.getBackground();
        actionBarBackButtonDrawableGlass = null;
        actionBarContainer = new PhotoViewerActionBarContainer(activity);
        actionBar.addView(actionBarContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));
        containerView.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        countView = new PhotoCountView(activity);
        containerView.addView(countView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.FILL_HORIZONTAL | Gravity.TOP));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (photoPaintView != null && photoPaintView.onBackPressed()) {
                        return;
                    }
                    if (isCaptionOpen()) {
                        closeCaptionEnter(false);
                        return;
                    }
                    closePhoto(true, false);
                } else if (id == gallery_menu_save) {
                    if (Build.VERSION.SDK_INT >= 23 && (Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && parentActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        parentActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                        return;
                    }

                    ArrayList<MessageObject> msgs = new ArrayList<>(1);
                    MessageObject.GroupedMessages group = parentChatActivity != null ? parentChatActivity.getGroup(currentMessageObject.getGroupId()) : null;
                    if (group != null) {
                        msgs.addAll(group.messages);
                    } else {
                        msgs.add(currentMessageObject);
                    }

                    if (msgs.size() <= 1) {
                        File f = null;
                        final boolean isVideo;
                        if (currentMessageObject != null) {
                            if (MessageObject.getMedia(currentMessageObject.messageOwner) instanceof TLRPC.TL_messageMediaWebPage && MessageObject.getMedia(currentMessageObject.messageOwner).webpage != null && MessageObject.getMedia(currentMessageObject.messageOwner).webpage.document == null) {
                                TLObject fileLocation = getFileLocation(currentIndex, null);
                                f = FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation, true);
                                if (!f.exists()) {
                                    f = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), f.getName());
                                }
                            } else {
                                f = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
                            }
                            isVideo = currentMessageObject.isVideo();
                        } else if (currentFileLocationVideo != null) {
                            String ext = getFileLocationExt(currentFileLocationVideo);
                            f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), ext, avatarsDialogId != 0 || isEvent);
                            if (f != null && !f.exists()) {
                                f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), ext, false);
                            }
                            if (ext != null) {
                                ext = ext.toLowerCase();
                            }
                            isVideo = ext != null && (ext.equals("webm") || ext.equals("mp4") || ext.equals("gif"));
                        } else if (pageBlocksAdapter != null) {
                            f = pageBlocksAdapter.getFile(currentIndex);
                            isVideo = pageBlocksAdapter.isVideo(currentIndex);
                        } else {
                            isVideo = false;
                        }
                        if (f != null && !f.exists()) {
                            f = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), f.getName());
                        }

                        final boolean isLivePhoto = currentMessageObject != null && currentMessageObject.isLivePhoto();
                        File videoFileForLivePhoto = null;
                        if (isLivePhoto) {
                            final TLRPC.Document videoDoc = MessageObject.getMedia(currentMessageObject.messageOwner) != null
                                    ? MessageObject.getMedia(currentMessageObject.messageOwner).document
                                    : null;
                            if (videoDoc != null) {
                                videoFileForLivePhoto = FileLoader.getInstance(currentAccount).getPathToAttach(videoDoc, false);
                                if (videoFileForLivePhoto == null || !videoFileForLivePhoto.exists()) {
                                    videoFileForLivePhoto = FileLoader.getInstance(currentAccount).getPathToAttach(videoDoc, true);
                                }
                            }
                        }
                        if (isLivePhoto) {
                            if (f != null && f.exists() && videoFileForLivePhoto != null && videoFileForLivePhoto.exists()) {
                                MediaController.saveFile(f.toString(), videoFileForLivePhoto.toString(), parentActivity, uri -> BulletinFactory.createSaveToGalleryBulletin(containerView, false, true, 0xf9222222, 0xffffffff).show());
                            } else {
                                showDownloadAlert();
                            }
                        } else if (f != null && f.exists()) {
                            MediaController.saveFile(f.toString(), parentActivity, isVideo ? 1 : 0, null, null, uri -> BulletinFactory.createSaveToGalleryBulletin(containerView, isVideo, 0xf9222222, 0xffffffff).show());
                        } else {
                            showDownloadAlert();
                        }
                    } else {
                        boolean hasVideo_ = false, hasPhoto_ = false, hasLivePhoto_ = false;
                        for (int i = 0; i < msgs.size(); ++i) {
                            MessageObject m = msgs.get(i);
                            if (m.isLivePhoto()) {
                                hasLivePhoto_ = true;
                            } else if (m.isVideo()) {
                                hasVideo_ = true;
                            } else {
                                hasPhoto_ = true;
                            }
                        }
                        final boolean hasVideo = hasVideo_;
                        final boolean hasPhoto = hasPhoto_;
                        final boolean hasLivePhoto = hasLivePhoto_;
                        final boolean isVideo = hasVideo && !hasPhoto && !hasLivePhoto;
                        AlertDialog dialog = new AlertDialog.Builder(parentActivity, resourcesProvider)
                                .setTitle(getString("SaveGroupMedia", R.string.SaveGroupMedia))
                                .setMessage(getString("SaveGroupMediaMessage", R.string.SaveGroupMediaMessage))
                                .setDialogButtonColorKey(Theme.key_voipgroup_listeningText)
                                .setNegativeButton(((currentMessageObject == null || !currentMessageObject.isVideo() || currentMessageObject.isLivePhoto()) ? getString("ThisPhoto", R.string.ThisPhoto) : getString("ThisMedia", R.string.ThisMedia)), (di, a) -> {
                                    if (currentMessageObject == null) {
                                        return;
                                    }

                                    File f;
                                    if (MessageObject.getMedia(currentMessageObject.messageOwner) instanceof TLRPC.TL_messageMediaWebPage && MessageObject.getMedia(currentMessageObject.messageOwner).webpage != null && MessageObject.getMedia(currentMessageObject.messageOwner).webpage.document == null) {
                                        TLObject fileLocation = getFileLocation(currentIndex, null);
                                        f = FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation, true);
                                    } else {
                                        f = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
                                    }
                                    boolean isThisVideo = currentMessageObject.isVideo();
                                    final boolean isThisLivePhoto = currentMessageObject.isLivePhoto();
                                    File videoFileForLivePhoto = null;
                                    if (isThisLivePhoto) {
                                        final TLRPC.Document videoDoc = MessageObject.getMedia(currentMessageObject.messageOwner) != null
                                                ? MessageObject.getMedia(currentMessageObject.messageOwner).document
                                                : null;
                                        if (videoDoc != null) {
                                            videoFileForLivePhoto = FileLoader.getInstance(currentAccount).getPathToAttach(videoDoc, false);
                                            if (videoFileForLivePhoto == null || !videoFileForLivePhoto.exists()) {
                                                videoFileForLivePhoto = FileLoader.getInstance(currentAccount).getPathToAttach(videoDoc, true);
                                            }
                                        }
                                    }

                                    if (isThisLivePhoto) {
                                        if (f != null && f.exists() && videoFileForLivePhoto != null && videoFileForLivePhoto.exists()) {
                                            MediaController.saveFile(f.toString(), videoFileForLivePhoto.toString(), parentActivity, uri -> BulletinFactory.createSaveToGalleryBulletin(containerView, false, true, 0xf9222222, 0xffffffff).show());
                                        } else {
                                            showDownloadAlert();
                                        }
                                    } else if (f != null && f.exists()) {
                                        MediaController.saveFile(f.toString(), parentActivity, isThisVideo ? 1 : 0, null, null, uri -> BulletinFactory.createSaveToGalleryBulletin(containerView, isThisVideo, 0xf9222222, 0xffffffff).show());
                                    } else {
                                        showDownloadAlert();
                                    }
                                })
                                .setPositiveButton((!hasVideo && !hasLivePhoto) ? LocaleController.formatPluralString("AllNPhotos", msgs.size()) : LocaleController.formatPluralString("AllNMedia", msgs.size()), (di, a) -> {
                                    final int[] count = new int[1];
                                    final int[] done = new int[1];
                                    Runnable bulletin = () -> {
                                        done[0]++;
                                        if (done[0] == count[0]) {
                                            BulletinFactory.createSaveMediaToGalleryBulletin(containerView, count[0], hasVideo, hasPhoto, hasLivePhoto, 0xf9222222, 0xffffffff).show();
                                        }
                                    };
                                    for (int i = 0; i < msgs.size(); ++i) {
                                        MessageObject msg = msgs.get(i);
                                        if (msg == null) {
                                            continue;
                                        }

                                        File f;
                                        if (MessageObject.getMedia(msg.messageOwner) instanceof TLRPC.TL_messageMediaWebPage && MessageObject.getMedia(msg.messageOwner).webpage != null && MessageObject.getMedia(msg.messageOwner).webpage.document == null) {
                                            f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentIndex, null), true);
                                        } else {
                                            f = FileLoader.getInstance(currentAccount).getPathToMessage(msg.messageOwner);
                                        }
                                        boolean isThisVideo = msg.isVideo();
                                        final boolean isThisLivePhoto = msg.isLivePhoto();
                                        File videoFileForLivePhoto = null;
                                        if (isThisLivePhoto) {
                                            final TLRPC.Document videoDoc = MessageObject.getMedia(msg.messageOwner) != null
                                                    ? MessageObject.getMedia(msg.messageOwner).document
                                                    : null;
                                            if (videoDoc != null) {
                                                videoFileForLivePhoto = FileLoader.getInstance(currentAccount).getPathToAttach(videoDoc, false);
                                                if (videoFileForLivePhoto == null || !videoFileForLivePhoto.exists()) {
                                                    videoFileForLivePhoto = FileLoader.getInstance(currentAccount).getPathToAttach(videoDoc, true);
                                                }
                                            }
                                        }

                                        if (isThisLivePhoto && f != null && f.exists()) {
                                            count[0]++;
                                            if (videoFileForLivePhoto != null && videoFileForLivePhoto.exists()) {
                                                MediaController.saveFile(f.toString(), videoFileForLivePhoto.toString(), parentActivity, uri -> AndroidUtilities.runOnUIThread(bulletin));
                                            } else {
                                                MediaController.saveFile(f.toString(), parentActivity, 0, null, null, uri -> AndroidUtilities.runOnUIThread(bulletin));
                                            }
                                        } else if (!isThisLivePhoto && f != null && f.exists()) {
                                            count[0]++;
                                            MediaController.saveFile(f.toString(), parentActivity, isThisVideo ? 1 : 0, null, null, uri -> AndroidUtilities.runOnUIThread(bulletin));
                                        }
                                    }
                                })
                                .setNeutralButton(getString("Cancel", R.string.Cancel), (di, a) -> {
                                    di.dismiss();
                                }).create();
                        dialog.setBackgroundColor(getThemedColor(Theme.key_voipgroup_dialogBackground));
                        dialog.show();
                        View neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);
                        if (neutralButton instanceof TextView) {
                            ((TextView) neutralButton).setTextColor(getThemedColor(Theme.key_text_RedBold));
                            neutralButton.setBackground(Theme.getRoundRectSelectorDrawable(getThemedColor(Theme.key_text_RedBold)));
                            if (dialog.getButtonsLayout() instanceof LinearLayout && ((LinearLayout) dialog.getButtonsLayout()).getOrientation() == LinearLayout.VERTICAL) {
                                neutralButton.bringToFront();
                            }
                        }
                        dialog.setTextColor(getThemedColor(Theme.key_voipgroup_actionBarItems));
                    }
                } else if (id == gallery_menu_chromecast) {
                    ChromecastController.getInstance().setCurrentMediaAndCastIfNeeded(getCurrentChromecastMedia());
                    castItemButton.performClick();
                } else if (id == gallery_menu_scan) {
                    File f = null;
                    final boolean isVideo;
                    if (currentMessageObject != null) {
                        if (currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && currentMessageObject.messageOwner.media.webpage != null && currentMessageObject.messageOwner.media.webpage.document == null) {
                            TLObject fileLocation = getFileLocation(currentIndex, null);
                            f = FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation, true);
                        } else {
                            f = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
                        }
                        isVideo = currentMessageObject.isVideo();
                    } else if (currentFileLocationVideo != null) {
                        f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), avatarsDialogId != 0 || isEvent);
                        isVideo = false;
                    } else if (pageBlocksAdapter != null) {
                        f = pageBlocksAdapter.getFile(currentIndex);
                        isVideo = pageBlocksAdapter.isVideo(currentIndex);
                    } else {
                        isVideo = false;
                    }
                    try {
                        Bitmap bitmap;
                        if (isVideo) {
                            bitmap = videoTextureView.getBitmap();
                        } else if (f != null && f.exists()) {
                            bitmap = ImageLoader.loadBitmap(f.getPath(), null, -1f, -1f, false);
                        } else {
                            showDownloadAlert();
                            return;
                        }
                        ProxyUtil.tryReadQR(parentActivity, bitmap);
                    } catch (Exception ignored) {
                        AlertUtil.showToast(LocaleController.getString("NoQrFound", R.string.NoQrFound));
                    }
                } else if (id == gallery_menu_showall) {
                    if (currentDialogId != 0) {
                        disableShowCheck = true;
                        Bundle args2 = new Bundle();
                        args2.putLong("dialog_id", currentDialogId);
                        MediaActivity mediaActivity = new MediaActivity(args2, null);
                        if (parentChatActivity != null) {
                            mediaActivity.setChatInfo(parentChatActivity.getCurrentChatInfo());
                        }
                        closePhoto(false, false);
                        if (parentActivity instanceof LaunchActivity) {
                            ((LaunchActivity) parentActivity).presentFragment(mediaActivity, false, true);
                        }
                    }
                } else if (id == gallery_menu_showinchat || id == gallery_menu_reply) {
                    if (currentMessageObject == null) {
                        return;
                    }
                    Bundle args = new Bundle();
                    long dialogId = currentDialogId;
                    if (currentMessageObject != null) {
                        dialogId = currentMessageObject.getDialogId();
                    }
                    if (DialogObject.isEncryptedDialog(dialogId)) {
                        args.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
                    } else if (DialogObject.isUserDialog(dialogId)) {
                        args.putLong("user_id", dialogId);
                    } else {
                        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-dialogId);
                        if (chat != null && chat.migrated_to != null) {
                            args.putLong("migrated_to", dialogId);
                            dialogId = -chat.migrated_to.channel_id;
                        }
                        args.putLong("chat_id", -dialogId);
                    }
                    args.putInt("message_id", currentMessageObject.getId());
                    if (id == gallery_menu_reply) {
                        args.putInt("reply_to", currentMessageObject.getId());
                    }
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.closeChats);
                    if (parentActivity instanceof LaunchActivity) {
                        LaunchActivity launchActivity = (LaunchActivity) parentActivity;
                        boolean remove = launchActivity.getMainFragmentsCount() > 1 || AndroidUtilities.isTablet();
                        launchActivity.presentFragment(new ChatActivity(args), remove, true);
                    }
                    closePhoto(false, false);
                    currentMessageObject = null;
                    if (ads != null) {
                        ads.stop();
                        ads = null;
                    }
                } else if (id == gallery_menu_create_sticker) {
                    if (parentFragment == null || placeProvider == null) return;
                    if (currentMessageObject == null || currentMessageObject.messageOwner == null) return;
                    String path = null;
                    if (!TextUtils.isEmpty(currentMessageObject.messageOwner.attachPath)) {
                        path = currentMessageObject.messageOwner.attachPath;
                        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                            path = null;
                        }
                    }
                    if (TextUtils.isEmpty(path)) {
                        final File file = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner, true);
                        if (file != null && file.exists()) {
                            path = file.getAbsolutePath();
                        }
                    }
                    if (TextUtils.isEmpty(path)) return;
                    final String finalPath = path;
                    final PhotoViewerProvider provider = placeProvider;
                    final BaseFragment fragment = parentFragment;
                    final ChatActivity chatActivity = parentChatActivity;

                    final ArrayList<Object> photos = new ArrayList<>();
                    final MediaController.PhotoEntry entry = new MediaController.PhotoEntry(0, 0, 0, finalPath, 0, false, 0, 0, 0);
                    photos.add(entry);
                    openPhotoForSelect(photos, 0, PhotoViewer.SELECT_TYPE_STICKER, false, new EmptyPhotoViewerProvider() {
                        @Override
                        public boolean allowCaption() {
                            return false;
                        }
                        @Override
                        public boolean isEditingSticker() {
                            return true;
                        }
                    }, chatActivity);
                    enableStickerMode(null, null, false, null);
                    prepareSegmentImage();
                    ContentPreviewViewer.getInstance().setStickerSetForCustomSticker(null);
                } else if (id == gallery_menu_send || id == gallery_menu_send_forward || id == gallery_menu_send_noquote) {
                    if (currentMessageObject == null || !(parentActivity instanceof LaunchActivity)) {
                        return;
                    }
                    boolean isChannel = false;
                    if (!currentMessageObject.scheduled) {
                        long dialogId = currentMessageObject.getDialogId();
                        if (DialogObject.isChatDialog(dialogId)) {
                            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-dialogId);
                            isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
                        }
                    }
                    ((LaunchActivity) parentActivity).switchToAccount(currentMessageObject.currentAccount, true);
                    ArrayList<MessageObject> msgs = new ArrayList<>(1);
                    MessageObject.GroupedMessages group = parentChatActivity != null ? parentChatActivity.getGroup(currentMessageObject.getGroupId()) : null;
                    if (group != null) {
                        msgs.addAll(group.messages);
                    } else {
                        msgs.add(currentMessageObject);
                    }

                    if (isChannel && msgs.size() <= 1) {
                        showForward(msgs, id == gallery_menu_send_noquote);
                    } else if (msgs.size() > 1) {
                        boolean photos = true;
                        for (int i = 0; i < msgs.size(); ++i) {
                            if (!msgs.get(i).isPhoto() || msgs.get(i).isVideo()) {
                                photos = false;
                                break;
                            }
                        }

                        AlertDialog dialog = new AlertDialog.Builder(parentActivity, resourcesProvider)
                                .setTitle(getString("ForwardGroupMedia", R.string.ForwardGroupMedia))
                                .setMessage(getString("ForwardGroupMediaMessage", R.string.ForwardGroupMediaMessage))
                                .setDialogButtonColorKey(Theme.key_voipgroup_listeningText)
                                .setNegativeButton((photos ? getString("ThisPhoto", R.string.ThisPhoto) : getString("ThisMedia", R.string.ThisMedia)), (di, a) -> {
                                    ArrayList<MessageObject> singleMessage = new ArrayList<>(1);
                                    singleMessage.add(currentMessageObject);
                                    showForward(singleMessage, id == gallery_menu_send_noquote);
                                })
                                .setPositiveButton(photos ? LocaleController.formatPluralString("AllNPhotos", msgs.size()) : LocaleController.formatPluralString("AllNMedia", msgs.size()), (di, a) -> {
                                    showForward(msgs, id == gallery_menu_send_noquote);
                                })
                                .setNeutralButton(getString("Cancel", R.string.Cancel), (di, a) -> {
                                    di.dismiss();
                                }).create();
                        dialog.setBackgroundColor(getThemedColor(Theme.key_voipgroup_dialogBackground));
                        dialog.show();
                        View neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);
                        if (neutralButton instanceof TextView) {
                            ((TextView) neutralButton).setTextColor(getThemedColor(Theme.key_text_RedBold));
                            neutralButton.setBackground(Theme.getRoundRectSelectorDrawable(getThemedColor(Theme.key_text_RedBold)));
                            if (dialog.getButtonsLayout() instanceof LinearLayout && ((LinearLayout) dialog.getButtonsLayout()).getOrientation() == LinearLayout.VERTICAL) {
                                neutralButton.bringToFront();
                            }
                        }
                        dialog.setTextColor(getThemedColor(Theme.key_voipgroup_actionBarItems));
                    } else {
                        Bundle args = new Bundle();
                        args.putBoolean("onlySelect", true);
                        args.putBoolean("canSelectTopics", true);
                        args.putInt("dialogsType", DialogsActivity.DIALOGS_TYPE_FORWARD);
                        DialogsActivity fragment = new DialogsActivity(args);
                        final ArrayList<MessageObject> fmessages = new ArrayList<>();
                        fmessages.add(currentMessageObject);
                        final ChatActivity parentChatActivityFinal = parentChatActivity;
                        fragment.setDelegate((fragment1, dids, message, param, notify, scheduleDate, scheduleRepeatPeriod, topicsFragment) -> {
                            if (dids.size() > 1 || dids.get(0).dialogId == UserConfig.getInstance(currentAccount).getClientUserId() || message != null) {
                                for (int a = 0; a < dids.size(); a++) {
                                    long did = dids.get(a).dialogId;
                                    if (message != null) {
                                        SendMessagesHelper.getInstance(currentAccount).sendMessage(SendMessagesHelper.SendMessageParams.of(message.toString(), did, null, null, null, true, null, null, null, !AyuGhostController.getInstance(UserConfig.selectedAccount).isSendWithoutSound(), 0, 0, null, false));
                                    }
                                    forwardMessagesFromViewer(fmessages, did, id == gallery_menu_send_noquote, !AyuGhostController.getInstance(UserConfig.selectedAccount).isSendWithoutSound(), 0);
                                }
                                fragment1.finishFragment();
                                if (parentChatActivityFinal != null) {
                                    UndoView undoView = parentChatActivityFinal.getUndoView();
                                    if (undoView != null) {
                                        if (dids.size() == 1) {
                                            undoView.showWithAction(dids.get(0).dialogId, UndoView.ACTION_FWD_MESSAGES, fmessages.size());
                                        } else {
                                            undoView.showWithAction(0, UndoView.ACTION_FWD_MESSAGES, fmessages.size(), dids.size(), null, null);
                                        }
                                    }
                                }
                            } else {
                                MessagesStorage.TopicKey topicKey = dids.get(0);
                                long did = topicKey.dialogId;
                                Bundle args1 = new Bundle();
                                if (id == gallery_menu_send_noquote) {
                                    args1.putBoolean("forward_noquote", true);
                                }
                                args1.putBoolean("scrollToTopOnResume", true);
                                if (DialogObject.isEncryptedDialog(did)) {
                                    args1.putInt("enc_id", DialogObject.getEncryptedChatId(did));
                                } else if (DialogObject.isUserDialog(did)) {
                                    args1.putLong("user_id", did);
                                } else {
                                    args1.putLong("chat_id", -did);
                                }
                                ChatActivity chatActivity = new ChatActivity(args1);
                                if (topicKey.topicId != 0) {
                                    ForumUtilities.applyTopic(chatActivity, topicKey);
                                }
                                if (((LaunchActivity) parentActivity).presentFragment(chatActivity, true, false)) {
                                    chatActivity.showFieldPanelForForward(true, fmessages);
                                } else {
                                    fragment1.finishFragment();
                                }
                            }
                            return true;
                        });
                        ((LaunchActivity) parentActivity).presentFragment(fragment, false, true);
                        closePhoto(false, false);
                    }
                } else if (id == gallery_menu_paint || id == gallery_menu_paint2) {
                    openCurrentPhotoInPaintModeForSelect();
                } else if (id == gallery_menu_delete2) {
                    if (parentActivity == null || placeProvider == null) {
                        return;
                    }
                    placeProvider.onPollAttachDelete();
                    closePhoto(true, false);
                } else if (id == gallery_menu_delete) {
                    if (parentActivity == null || placeProvider == null) {
                        return;
                    }
                    boolean isChannel = false;
                    if (currentMessageObject != null && !currentMessageObject.scheduled) {
                        long dialogId = currentMessageObject.getDialogId();
                        if (DialogObject.isChatDialog(dialogId)) {
                            isChannel = ChatObject.isChannel(MessagesController.getInstance(currentAccount).getChat(-dialogId));
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                    String text = placeProvider.getDeleteMessageString();
                    if (text != null) {
                        builder.setTitle(getString("AreYouSureDeletePhotoTitle", R.string.AreYouSureDeletePhotoTitle));
                        builder.setMessage(text);
                    } else if (isEmbedVideo || currentFileLocationVideo != null && currentFileLocationVideo != currentFileLocation || currentMessageObject != null && currentMessageObject.isVideo()) {
                        builder.setTitle(getString("AreYouSureDeleteVideoTitle", R.string.AreYouSureDeleteVideoTitle));
                        if (isChannel) {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideoEveryone", R.string.AreYouSureDeleteVideoEveryone));
                        } else {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", R.string.AreYouSureDeleteVideo));
                        }
                    } else if (currentMessageObject != null && currentMessageObject.isGif()) {
                        builder.setTitle(getString("AreYouSureDeleteGIFTitle", R.string.AreYouSureDeleteGIFTitle));
                        if (isChannel) {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeleteGIFEveryone", R.string.AreYouSureDeleteGIFEveryone));
                        } else {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeleteGIF", R.string.AreYouSureDeleteGIF));
                        }
                    } else {
                        builder.setTitle(getString("AreYouSureDeletePhotoTitle", R.string.AreYouSureDeletePhotoTitle));
                        if (isChannel) {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeletePhotoEveryone", R.string.AreYouSureDeletePhotoEveryone));
                        } else {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", R.string.AreYouSureDeletePhoto));
                        }
                    }

                    final boolean[] deleteForAll = new boolean[1];
                    if (currentMessageObject != null && !currentMessageObject.scheduled) {
                        long dialogId = currentMessageObject.getDialogId();
                        if (!DialogObject.isEncryptedDialog(dialogId)) {
                            TLRPC.Chat currentChat;
                            TLRPC.User currentUser;
                            if (DialogObject.isUserDialog(dialogId)) {
                                currentUser = MessagesController.getInstance(currentAccount).getUser(dialogId);
                                currentChat = null;
                            } else {
                                currentUser = null;
                                currentChat = MessagesController.getInstance(currentAccount).getChat(-dialogId);
                            }
                            if (currentUser != null || !ChatObject.isChannel(currentChat)) {
                                boolean hasOutgoing = false;
                                int currentDate = ConnectionsManager.getInstance(currentAccount).getCurrentTime();

                                int revokeTimeLimit;
                                if (currentUser != null) {
                                    revokeTimeLimit = MessagesController.getInstance(currentAccount).revokeTimePmLimit;
                                } else {
                                    revokeTimeLimit = MessagesController.getInstance(currentAccount).revokeTimeLimit;
                                }

                                if (currentUser != null && currentUser.id != UserConfig.getInstance(currentAccount).getClientUserId() || currentChat != null) {
                                    boolean canRevokeInbox = currentUser != null && MessagesController.getInstance(currentAccount).canRevokePmInbox;
                                    if ((currentMessageObject.messageOwner.action == null || currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty) && (currentMessageObject.isOut() || canRevokeInbox || ChatObject.hasAdminRights(currentChat)) && (currentDate - currentMessageObject.messageOwner.date) <= revokeTimeLimit) {
                                        FrameLayout frameLayout = new FrameLayout(parentActivity);
                                        CheckBoxCell cell = new CheckBoxCell(parentActivity, 1, resourcesProvider);
                                        cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                        if (currentChat != null) {
                                            cell.setText(getString("DeleteForAll", R.string.DeleteForAll), "", false, false);
                                        } else {
                                            cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(currentUser)), "", false, false);
                                        }
                                        cell.setPadding(LocaleController.isRTL ? dp(16) : dp(8), 0, LocaleController.isRTL ? dp(8) : dp(16), 0);
                                        frameLayout.addView(cell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.TOP | Gravity.LEFT, 0, 0, 0, 0));
                                        cell.setOnClickListener(v -> {
                                            CheckBoxCell cell1 = (CheckBoxCell) v;
                                            deleteForAll[0] = !deleteForAll[0];
                                            cell1.setChecked(deleteForAll[0], true);
                                        });
                                        builder.setView(frameLayout);
                                        builder.setCustomViewOffset(9);
                                    }
                                }
                            }
                        }
                    }
                    builder.setPositiveButton(getString("Delete", R.string.Delete), (dialogInterface, i) -> {
                        if (!placeProvider.onDeletePhoto(currentIndex)) {
                            closePhoto(false, false);
                            return;
                        }
                        if (!imagesArr.isEmpty()) {
                            if (currentIndex < 0 || currentIndex >= imagesArr.size()) {
                                return;
                            }
                            MessageObject obj = imagesArr.get(currentIndex);
                            if (obj.isSent()) {
                                closePhoto(false, false);
                                ArrayList<Integer> arr = new ArrayList<>();
                                if (slideshowMessageId != 0) {
                                    arr.add(slideshowMessageId);
                                } else {
                                    arr.add(obj.getId());
                                }

                                ArrayList<Long> random_ids = null;
                                TLRPC.EncryptedChat encryptedChat = null;
                                if (DialogObject.isEncryptedDialog(obj.getDialogId()) && obj.messageOwner.random_id != 0) {
                                    random_ids = new ArrayList<>();
                                    random_ids.add(obj.messageOwner.random_id);
                                    encryptedChat = MessagesController.getInstance(currentAccount).getEncryptedChat(DialogObject.getEncryptedChatId(obj.getDialogId()));
                                }

                                MessagesController.getInstance(currentAccount).deleteMessages(arr, random_ids, encryptedChat, obj.getDialogId(), obj.getQuickReplyId(), deleteForAll[0], obj.getChatMode());
                            }
                        } else if (!avatarsArr.isEmpty()) {
                            if (currentIndex < 0 || currentIndex >= avatarsArr.size()) {
                                return;
                            }
                            TLRPC.Message message = imagesArrMessages.get(currentIndex);
                            if (message != null) {
                                ArrayList<Integer> arr = new ArrayList<>();
                                arr.add(message.id);
                                MessagesController.getInstance(currentAccount).deleteMessages(arr, null, null, MessageObject.getDialogId(message), message.quick_reply_shortcut_id, true, 0);
                                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.reloadDialogPhotos);
                            }
                            if (isCurrentAvatarSet()) {
                                if (avatarsDialogId > 0) {
                                    MessagesController.getInstance(currentAccount).deleteUserPhoto(null);
                                } else {
                                    MessagesController.getInstance(currentAccount).changeChatAvatar(-avatarsDialogId, null, null, null, null, 0, null, null, null, null);
                                }
                                closePhoto(false, false);
                            } else {
                                TLRPC.Photo photo = avatarsArr.get(currentIndex);
                                if (photo == null) {
                                    return;
                                }
                                TLRPC.TL_inputPhoto inputPhoto = new TLRPC.TL_inputPhoto();
                                inputPhoto.id = photo.id;
                                inputPhoto.access_hash = photo.access_hash;
                                inputPhoto.file_reference = photo.file_reference;
                                if (inputPhoto.file_reference == null) {
                                    inputPhoto.file_reference = new byte[0];
                                }
                                if (avatarsDialogId > 0) {
                                    MessagesController.getInstance(currentAccount).deleteUserPhoto(inputPhoto);
                                }
                                MessagesStorage.getInstance(currentAccount).clearUserPhoto(avatarsDialogId, photo.id);
                                imagesArrLocations.remove(currentIndex);
                                imagesArrLocationsSizes.remove(currentIndex);
                                imagesArrLocationsVideo.remove(currentIndex);
                                imagesArrMessages.remove(currentIndex);
                                avatarsArr.remove(currentIndex);
                                if (imagesArrLocations.isEmpty()) {
                                    closePhoto(false, false);
                                } else {
                                    int index = currentIndex;
                                    if (index >= avatarsArr.size()) {
                                        index = avatarsArr.size() - 1;
                                    }
                                    currentIndex = -1;
                                    setImageIndex(index);
                                }
                                if (message == null) {
                                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.reloadDialogPhotos);
                                }
                            }
                        } else if (!secureDocuments.isEmpty()) {
                            if (placeProvider == null) {
                                return;
                            }
                            secureDocuments.remove(currentIndex);
                            placeProvider.deleteImageAtIndex(currentIndex);
                            if (secureDocuments.isEmpty()) {
                                closePhoto(false, false);
                            } else {
                                int index = currentIndex;
                                if (index >= secureDocuments.size()) {
                                    index = secureDocuments.size() - 1;
                                }
                                currentIndex = -1;
                                setImageIndex(index);
                            }
                        }
                    });
                    builder.setNegativeButton(getString("Cancel", R.string.Cancel), null);
                    AlertDialog alertDialog = builder.create();
                    showAlertDialog(builder);
                    TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (button != null) {
                        button.setTextColor(getThemedColor(Theme.key_text_RedBold));
                    }
                } else if (id == gallery_menu_share || id == gallery_menu_share2) {
                    onSharePressed();
                } else if (id == gallery_menu_openin) {
                    try {
                        if (isEmbedVideo) {
                            Browser.openUrl(parentActivity, MessageObject.getMedia(currentMessageObject.messageOwner).webpage.url);
                            closePhoto(false, false);
                        } else if (currentMessageObject != null) {
                            if (AndroidUtilities.openForView(currentMessageObject, parentActivity, resourcesProvider, currentMessageObject.isVideo() || currentMessageObject.isPhoto() || currentMessageObject.isSticker())) {
                                closePhoto(false, false);
                            } else if (currentMessageObject.isVideo() && MediaStreamingProvider.openForStreaming(parentActivity, currentAccount, currentMessageObject.getDocument(), currentMessageObject)) {
                                closePhoto(false, false);
                            } else {
                                showDownloadAlert();
                            }
                        } else if (pageBlocksAdapter != null) {
                            if (AndroidUtilities.openForView(pageBlocksAdapter.getMedia(currentIndex), parentActivity)) {
                                closePhoto(false, false);
                            } else if (pageBlocksAdapter.isVideo(currentIndex) && MediaStreamingProvider.openForStreaming(parentActivity, currentAccount, (TLRPC.Document) pageBlocksAdapter.getMedia(currentIndex), pageBlocksAdapter.getParentObject())) {
                                closePhoto(false, false);
                            } else {
                                showDownloadAlert();
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (id == gallery_menu_masks || id == gallery_menu_masks2) {
                    if (parentActivity == null || currentMessageObject == null) {
                        return;
                    }
                    TLObject object;
                    if (MessageObject.getMedia(currentMessageObject.messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
                        object = MessageObject.getMedia(currentMessageObject.messageOwner).photo;
                    } else if (MessageObject.getMedia(currentMessageObject.messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
                        object = MessageObject.getMedia(currentMessageObject.messageOwner).document;
                    } else {
                        return;
                    }
                    masksAlert = new StickersAlert(parentActivity, currentMessageObject, object, resourcesProvider) {
                        @Override
                        public void dismiss() {
                            super.dismiss();
                            if (masksAlert == this) {
                                masksAlert = null;
                            }
                        }
                    };
                    masksAlert.show();
                } else if (id == gallery_menu_pip) {
                    if (!menuItem.isSubItemVisible(gallery_menu_pip)) {
                        return;
                    }
                    if (isEmbedVideo) {
                        if (photoViewerWebView.openInPip()) {
                            if (PipInstance != null) {
                                PipInstance.destroyPhotoViewer();
                            }
                            isInline = true;
                            PipInstance = Instance;
                            Instance = null;
                            isVisible = false;
                            isVisibleOrAnimating = false;
                            if (currentPlaceObject != null && !currentPlaceObject.imageReceiver.getVisible()) {
                                currentPlaceObject.imageReceiver.setVisible(true, true);
                            }
                            clippingImageProgress = 1f;
                            containerView.invalidate();
                            dismissInternal();
                        }
                    } else {
                        switchToPip(false);
                    }
                } else if (id == gallery_menu_cancel_loading) {
                    if (currentMessageObject == null) {
                        return;
                    }
                    FileLoader.getInstance(currentAccount).cancelLoadFile(currentMessageObject.getDocument());
                    releasePlayer(false);
                    bottomLayout.setTag(1);
                    bottomLayout.setVisibility(View.VISIBLE);
                } else if (id == gallery_menu_savegif) {
                    if (currentMessageObject != null) {
                        TLRPC.Document document = currentMessageObject.getDocument();
                        if (parentChatActivity != null && parentChatActivity.chatActivityEnterView != null) {
                            parentChatActivity.chatActivityEnterView.addRecentGif(document);
                        } else {
                            MediaDataController.getInstance(currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000), true);
                        }
                        MessagesController.getInstance(currentAccount).saveGif(currentMessageObject, document);
                    } else if (pageBlocksAdapter != null) {
                        TLObject object = pageBlocksAdapter.getMedia(currentIndex);
                        if (object instanceof TLRPC.Document) {
                            TLRPC.Document document = (TLRPC.Document) object;
                            MediaDataController.getInstance(currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000), true);
                            MessagesController.getInstance(currentAccount).saveGif(pageBlocksAdapter.getParentObject(), document);
                        }
                    } else {
                        return;
                    }
                    if (containerView != null) {
                        BulletinFactory.of(containerView, resourcesProvider).createDownloadBulletin(BulletinFactory.FileType.GIF, resourcesProvider).show();
                    }
                } else if (id == gallery_menu_set_as_main) {
                    TLRPC.Photo photo = avatarsArr.get(currentIndex);
                    if (photo == null || photo.sizes.isEmpty()) {
                        return;
                    }
                    TLRPC.PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                    TLRPC.PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 90);
                    UserConfig userConfig = UserConfig.getInstance(currentAccount);
                    if (avatarsDialogId == userConfig.clientUserId) {
                        TLRPC.TL_photos_updateProfilePhoto req = new TLRPC.TL_photos_updateProfilePhoto();
                        req.id = new TLRPC.TL_inputPhoto();
                        req.id.id = photo.id;
                        req.id.access_hash = photo.access_hash;
                        req.id.file_reference = photo.file_reference;
                        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                            if (response instanceof TLRPC.TL_photos_photo) {
                                TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                                MessagesController.getInstance(currentAccount).putUsers(photos_photo.users, false);
                                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(userConfig.clientUserId);
                                if (photos_photo.photo instanceof TLRPC.TL_photo) {
                                    int idx = avatarsArr.indexOf(photo);
                                    if (idx >= 0) {
                                        avatarsArr.set(idx, photos_photo.photo);
                                    }
                                    if (user != null) {
                                        user.photo.photo_id = photos_photo.photo.id;
                                        userConfig.setCurrentUser(user);
                                        userConfig.saveConfig(true);
                                    }
                                }
                            }
                        }));

                        TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(userConfig.clientUserId);
                        if (user != null) {
                            user.photo.photo_id = photo.id;
                            user.photo.dc_id = photo.dc_id;
                            user.photo.photo_small = smallSize.location;
                            user.photo.photo_big = bigSize.location;
                            userConfig.setCurrentUser(user);
                            userConfig.saveConfig(true);
                            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
                        }
                    } else {
                        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-avatarsDialogId);
                        if (chat == null) {
                            return;
                        }
                        TLRPC.TL_inputChatPhoto inputChatPhoto = new TLRPC.TL_inputChatPhoto();
                        inputChatPhoto.id = new TLRPC.TL_inputPhoto();
                        inputChatPhoto.id.id = photo.id;
                        inputChatPhoto.id.access_hash = photo.access_hash;
                        inputChatPhoto.id.file_reference = photo.file_reference;
                        MessagesController.getInstance(currentAccount).changeChatAvatar(-avatarsDialogId, inputChatPhoto, null, null, null, 0, null, null, null, null);
                        chat.photo.dc_id = photo.dc_id;
                        chat.photo.photo_small = smallSize.location;
                        chat.photo.photo_big = bigSize.location;
                        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_AVATAR);
                    }
                    currentAvatarLocation = ImageLocation.getForPhoto(bigSize, photo);
                    avatarsArr.remove(currentIndex);
                    avatarsArr.add(0, photo);

                    ImageLocation location = imagesArrLocations.get(currentIndex);
                    imagesArrLocations.remove(currentIndex);
                    imagesArrLocations.add(0, location);

                    location = imagesArrLocationsVideo.get(currentIndex);
                    imagesArrLocationsVideo.remove(currentIndex);
                    imagesArrLocationsVideo.add(0, location);

                    Long size = imagesArrLocationsSizes.get(currentIndex);
                    imagesArrLocationsSizes.remove(currentIndex);
                    imagesArrLocationsSizes.add(0, size);

                    TLRPC.Message message = imagesArrMessages.get(currentIndex);
                    imagesArrMessages.remove(currentIndex);
                    imagesArrMessages.add(0, message);

                    currentIndex = -1;
                    setImageIndex(0);

                    groupedPhotosListView.clear();
                    groupedPhotosListView.fillList();
                    hintView.showWithAction(avatarsDialogId, UndoView.ACTION_PROFILE_PHOTO_CHANGED, currentFileLocationVideo == currentFileLocation ? null : 1);
                    AndroidUtilities.runOnUIThread(() -> {
                        if (menuItem == null) {
                            return;
                        }
                        menuItem.hideSubItem(gallery_menu_set_as_main);
                    }, 300);
                } else if (id == gallery_menu_edit_avatar) {
                    File f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), true);
                    boolean isVideo = currentFileLocationVideo.imageType == FileLoader.IMAGE_TYPE_ANIMATION;
                    String thumb;
                    if (isVideo) {
                        thumb = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocation), getFileLocationExt(currentFileLocation), true).getAbsolutePath();
                    } else {
                        thumb = null;
                    }
                    placeProvider.openPhotoForEdit(f.getAbsolutePath(), thumb, isVideo);
                } else if (id == gallery_menu_copy) {
                    File f = null;
                    if (currentMessageObject != null) {
                        if (currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && currentMessageObject.messageOwner.media.webpage != null && currentMessageObject.messageOwner.media.webpage.document == null) {
                            TLObject fileLocation = getFileLocation(currentIndex, null);
                            f = FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation, true);
                        } else {
                            f = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
                        }
                    } else if (currentFileLocationVideo != null) {
                        f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), avatarsDialogId != 0 || isEvent);
                    } else if (pageBlocksAdapter != null) {
                        f = pageBlocksAdapter.getFile(currentIndex);
                    }

                    if (f != null && f.exists()) {
                        MessageHelper.addFileToClipboard(f, () -> BulletinFactory.of(containerView, null).createCopyBulletin(LocaleController.getString("PhotoCopied", R.string.PhotoCopied)).show());
                    } else {
                        showDownloadAlert();
                    }
                } else if (id == gallery_menu_copy_frame) {
                    if (videoPlayer == null || currentMessageObject == null) {
                        return;
                    }
                    final long currentPosition = videoPlayer.getCurrentPosition();
                    final MessageObject messageObject = currentMessageObject;
                    final String videoPath = messageObject != null ? MessageHelper.getPathToMessage(messageObject) : null;
                    final File videoFile = !TextUtils.isEmpty(videoPath) ? new File(videoPath) : null;
                    MessageHelper.copyVideoFrameToClipboard(videoFile, currentPosition, containerView, resourcesProvider, () -> {
                        if (captureCurrentVideoFrameToClipboard()) {
                            return;
                        }
                        if (messageObject != null && currentMessageObject == messageObject) {
                            showDownloadAlert();
                        }
                    });
                } else if (id == gallery_menu_set_photo) {
                    File f = null;
                    if (currentMessageObject != null) {
                        if (currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && currentMessageObject.messageOwner.media.webpage != null && currentMessageObject.messageOwner.media.webpage.document == null) {
                            TLObject fileLocation = getFileLocation(currentIndex, null);
                            f = FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation, true);
                        } else {
                            f = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
                        }
                    } else if (currentFileLocationVideo != null) {
                        f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), avatarsDialogId != 0 || isEvent);
                    } else if (pageBlocksAdapter != null) {
                        f = pageBlocksAdapter.getFile(currentIndex);
                    }
                    if (f == null || !f.exists()) {
                        showDownloadAlert();
                    }
                    final ArrayList<Object> arrayList = new ArrayList<>();
                    MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0, f.getAbsolutePath(), 0, false, 0, 0, 0);
                    arrayList.add(photoEntry);
                    openPhotoForSelect(arrayList, 0, PhotoViewer.SELECT_TYPE_AVATAR, false, new PhotoViewer.EmptyPhotoViewerProvider() {
                        @Override
                        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, int scheduleRepeatPeriod, boolean forceDocument) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
                            if (photoEntry.imagePath != null || photoEntry.isVideo) {
                                PhotoUtilities.setImageAsAvatar(photoEntry, parentFragment, null);
                            }
                        }

                        @Override
                        public boolean allowCaption() {
                            return false;
                        }

                        @Override
                        public boolean canScrollAway() {
                            return false;
                        }
                    }, null);
                } else if (id == gallery_menu_translate) {
                    if (switchingToIndex < 0 || switchingToIndex >= imagesArr.size()) {
                        return;
                    }
                    MessageObject messageObject = imagesArr.get(switchingToIndex);
                    captionTranslated = true;
                    AndroidUtilities.runOnUIThread(() -> {
                        menuItem.hideSubItem(gallery_menu_translate);
                        menuItem.showSubItem(gallery_menu_hide_translation);
                    }, 32);
                    updateCaptionTranslated();
                    MessagesController.getInstance(currentAccount).getTranslateController().translatePhoto(messageObject, PhotoViewer.this::updateCaptionTranslated);
                } else if (id == gallery_menu_hide_translation) {
                    captionTranslated = false;
                    AndroidUtilities.runOnUIThread(() -> {
                        menuItem.showSubItem(gallery_menu_translate);
                        menuItem.hideSubItem(gallery_menu_hide_translation);
                    }, 32);
                    updateCaptionTranslated();
                } else if (id == gallery_menu_loop) {
                    playerLooping = !playerLooping;
                    VideoPlayer.saveLooping(playerLooping, currentMessageObject);
                    if (videoPlayer != null) {
                        videoPlayer.setLooping(playerLooping);
                    }
                    loopItem.setEnabledByColor(playerLooping, 0xFFFFFFFF, 0xFF73B4EC);
                    loopItem.setSelectorColor(playerLooping ? 0x0F73B4EC : 0x0fffffff);
                } else if (id == gallery_menu_report) {
                    TLRPC.Photo photo = null;
                    if (currentFileLocation != null && currentFileLocation.photo != null) {
                        photo = currentFileLocation.photo;
                    } else if (currentFileLocationVideo != null && currentFileLocationVideo.photo != null) {
                        photo = currentFileLocationVideo.photo;
                    }
                    if (photo == null) return;
                    AlertsCreator.createReportPhotoAlert(currentAccount, parentActivity, avatarsDialogId, photo, new DarkThemeResourceProvider());
                }
            }

            @Override
            public boolean canOpenMenu() {
                if (currentMessageObject != null || currentSecureDocument != null) {
                    return true;
                } else if (currentFileLocationVideo != null) {
                    File f = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), avatarsDialogId != 0 || isEvent);
                    File f2 = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), f.getName());
                    File f3 = FileLoader.getInstance(currentAccount).getPathToAttach(getFileLocation(currentFileLocationVideo), getFileLocationExt(currentFileLocationVideo), false);
                    return f.exists() || f2.exists() || f3.exists();
                } else if (pageBlocksAdapter != null) {
                    return true;
                }
                return false;
            }
        });

        menu = actionBar.createMenu();
        menu.setOnLayoutListener(this::updateActionBarTitlePadding);

        deleteItem = menu.addItem(gallery_menu_delete2, R.drawable.menu_delete_old);
        deleteItem.setContentDescription(getString(R.string.Delete));
        ScaleStateListAnimator.apply(deleteItem);
        setItemVisible(deleteItem, false, false);
        masksItem = menu.addItem(gallery_menu_masks, R.drawable.msg_mask);
        masksItem.setContentDescription(getString(R.string.Masks));
        editItem = menu.addItem(gallery_menu_paint, R.drawable.msg_header_draw);
        editItem.setContentDescription(getString(R.string.AccDescrPhotoEditor));
        sendItem = menu.addItem(gallery_menu_send, R.drawable.msg_header_share);
        sendItem.setContentDescription(getString(R.string.Forward));

        videoItem = menu.addItem(gallery_menu_quality, videoItemIcon = new ChooseQualityLayout.QualityIcon(activityContext, R.drawable.video_settings, new DarkThemeResourceProvider()));
        videoItemIcon.setCallback(videoItem.getIconView());
        videoItem.getPopupLayout().setSwipeBackForegroundColor(0xff222222);
        videoItem.getPopupLayout().swipeBackGravityRight = true;
        videoItem.getPopupLayout().setFitItems(true);
        videoItem.setMenuXOffset(dp(3));

        speedItem = new ActionBarMenuSlider.SpeedSlider(activityContext, resourcesProvider);
        speedItem.setStops(new float[]{0.5f, 1.0f, 1.5f, 2.0f, 2.5f});
        speedItem.setMinimumWidth(AndroidUtilities.dp(196));
        speedItem.setDrawShadow(false);
        speedItem.setBackgroundColor(0xff222222);
        speedItem.setTextColor(0xffffffff);
        speedItem.setLabel(LocaleController.getString(R.string.VideoPlayerSpeed));
        speedItem.setOnValueChange((value, isFinal) -> {
            final float speed = ActionBarMenuSlider.SpeedSlider.MIN_SPEED + (ActionBarMenuSlider.SpeedSlider.MAX_SPEED - ActionBarMenuSlider.SpeedSlider.MIN_SPEED) * value;
            chooseSpeed(speed, isFinal, false);
        });
        videoItem.getPopupLayout().addView(speedItem, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 44));
        speedGap = videoItem.addColoredGap();
        speedGap.setColor(0xff181818);
        videoItem.getPopupLayout().addView(chooseSpeedLayout = new SpeedButtonsLayout(activityContext, this::chooseSpeed));
        videoQualityLayout = new LinearLayout(activityContext);
        videoQualityLayout.setOrientation(LinearLayout.VERTICAL);
        videoItem.getPopupLayout().addView(videoQualityLayout);
        loopItem = videoItem.addSubItem(gallery_menu_loop, R.drawable.menu_video_loop, LocaleController.getString(R.string.VideoPlayerLoop));
        loopItem.setSelectorColor(0x0fffffff);
        castItemButton = new CastMediaRouteButton(activityContext) {
            @Override
            public void stateUpdated(boolean connected) {
                if (castItem != null) {
                    castItem.setEnabledByColor(connected, 0xFFFFFFFF, 0xFF73B4EC);
                    castItem.setSelectorColor(connected ? 0x0F73B4EC : 0x0fffffff);
                }
                if (videoPlayer != null) {
                    videoPlayer.setMute(CastSync.isActive() || muteVideo);
                }
                if (videoItemIcon != null) {
                    videoItemIcon.setCasting(CastSync.isActive(), true);
                }
            }
        };
        boolean castAvailable = true;
        try {
            castItemButton.setRouteSelector(CastContext.getSharedInstance(activityContext).getMergedSelector());
        } catch (Exception e) {
            FileLog.e(e);
            castAvailable = false;
        }
        castItemButton.setVisibility(View.INVISIBLE);
        if (castAvailable) {
            castItem = videoItem.addSubItem(gallery_menu_chromecast, R.drawable.menu_video_chromecast, getString(R.string.VideoPlayerChromecast));
            castItem.setEnabledByColor(false, 0xFFFFFFFF, 0xFF73B4EC);
            castItem.setSelectorColor(0x0fffffff);
            castItem.addView(castItemButton, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        videoItem.redrawPopup(0xf9222222);
        videoItem.setOnMenuDismiss(byClick -> checkProgress(0, false, false));

        menuItem = menu.addItem(0, R.drawable.media_more);
        menuItem.setContentDescription(getString(R.string.AccDescrMoreOptions));
        menuItem.setOnClickListener(v -> {
            if (currentMessageObject != null && currentMessageObject.isSponsored()) {
                openAdsMenu();
            } else if (actionBar.actionBarMenuOnItemClick.canOpenMenu()) {
                menuItem.toggleSubMenu();
            }
        });
        menuItem.setOnMenuDismiss(byClick -> checkProgress(0, false, false));

        menuItem.getPopupLayout().setSwipeBackForegroundColor(0xff222222);
        menuItem.getPopupLayout().swipeBackGravityRight = true;
        menuItem.getPopupLayout().setFitItems(true);

        chooseDownloadQualityLayout = new ChooseDownloadQualityLayout(activityContext, menuItem.getPopupLayout().getSwipeBack(), (messageObject, quality) -> {
            if (quality == null) return;
            TLRPC.Document document = quality.getDownloadDocument();
            if (document == null) return;
            File f = FileLoader.getInstance(currentAccount).getPathToAttach(document, null, false, true);
            if (f == null || !f.exists()) {
                f = FileLoader.getInstance(currentAccount).getPathToAttach(document, null, true, true);
            }
            if (f != null && f.exists()) {
                MediaController.saveFile(f.toString(), parentActivity, 1, null, null, uri -> BulletinFactory.createSaveToGalleryBulletin(containerView, true, 0xf9222222, 0xffffffff).show());
            } else {
                ArrayList<MessageObject> messageObjects = new ArrayList<>();
                messageObject.qualityToSave = document;
                messageObjects.add(messageObject);
                MediaController.saveFilesFromMessages(parentActivity, AccountInstance.getInstance(currentAccount), messageObjects, (count) -> {
                    if (parentActivity == null || containerView == null) {
                        return;
                    }
                    if (count > 0) {
                        BulletinFactory.createSaveToGalleryBulletin(containerView, true, 0xf9222222, 0xffffffff).show();
                    }
                });
            }
            menuItem.toggleSubMenu();
        });

        galleryButton = menuItem.addSwipeBackItem(R.drawable.msg_gallery, null, getString(R.string.SaveToGallery), chooseDownloadQualityLayout.layout).setColors(0xfffafafa, 0xfffafafa);
        galleryButton.setOnClickListener(v -> {
            if (currentMessageObject != null && currentMessageObject.hasVideoQualities() && chooseDownloadQualityLayout.update(currentMessageObject)) {
                galleryButton.openSwipeBack();
                return;
            }
            if (actionBar != null && actionBar.getActionBarMenuOnItemClick() != null) {
                actionBar.getActionBarMenuOnItemClick().onItemClick(gallery_menu_save);
                menuItem.toggleSubMenu();
            }
        });
        galleryGap = menuItem.addColoredGap();
        galleryGap.setColor(0xff181818);
        menuItem.addSubItem(gallery_menu_openin, R.drawable.msg_openin, getString(R.string.OpenInExternalApp)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_paint2, R.drawable.msg_header_draw, getString(R.string.EditPhoto)).setColors(0xfffafafa, 0xfffafafa);
        pipItem = menuItem.addSubItem(gallery_menu_pip, R.drawable.menu_video_pip, getString(R.string.PipMinimize)).setColors(0xfffafafa, 0xfffafafa);
        allMediaItem = menuItem.addSubItem(gallery_menu_showall, R.drawable.msg_media, getString(R.string.ShowAllMedia));
        allMediaItem.setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_savegif, R.drawable.msg_gif, getString(R.string.SaveToGIFs)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_showinchat, R.drawable.msg_message, getString(R.string.ShowInChat)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_create_sticker, R.drawable.msg_sticker, getString(R.string.CreateSticker)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_reply, R.drawable.menu_reply, getString(R.string.Reply)).setColors(0xfffafafa, 0xfffafafa);
        if (NaConfig.INSTANCE.getMediaViewerMenuItemForward().Bool()) menuItem.addSubItem(gallery_menu_send_forward, R.drawable.msg_forward, getString(R.string.Forward)).setColors(0xfffafafa, 0xfffafafa);
        if (NaConfig.INSTANCE.getMediaViewerMenuItemNoQuoteForward().Bool()) menuItem.addSubItem(gallery_menu_send_noquote, R.drawable.msg_forward_noquote, getString(R.string.NoQuoteForward)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_report, R.drawable.msg_report, getString(R.string.ReportProfilePhoto)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_share, R.drawable.msg_shareout, getString(R.string.ShareFile)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_masks2, R.drawable.msg_sticker, getString(R.string.ShowStickers)).setColors(0xfffafafa, 0xfffafafa);
        //menuItem.addSubItem(gallery_menu_edit_avatar, R.drawable.photo_paint, LocaleController.getString(R.string.EditPhoto)).setColors(0xfffafafa, 0xfffafafa);

        if (NaConfig.INSTANCE.getMediaViewerMenuItemCopyPhoto().Bool()) menuItem.addSubItem(gallery_menu_copy, R.drawable.msg_copy_photo, getString(R.string.CopyPhoto)).setColors(0xfffafafa, 0xfffafafa);
        if (NaConfig.INSTANCE.getMediaViewerMenuItemCopyFrame().Bool()) menuItem.addSubItem(gallery_menu_copy_frame, R.drawable.msg_copy_photo, getString(R.string.CopyVideoFrame)).setColors(0xfffafafa, 0xfffafafa);
        if (NaConfig.INSTANCE.getMediaViewerMenuItemSetProfilePhoto().Bool()) menuItem.addSubItem(gallery_menu_set_photo, R.drawable.msg_openprofile, getString(R.string.SetProfilePhoto)).setColors(0xfffafafa, 0xfffafafa);
        if (NaConfig.INSTANCE.getMediaViewerMenuItemScanQRCode().Bool()) menuItem.addSubItem(gallery_menu_scan, R.drawable.msg_qrcode, getString(R.string.ScanQRCode)).setColors(0xfffafafa, 0xfffafafa);

        menuItem.addSubItem(gallery_menu_set_as_main, R.drawable.msg_openprofile, getString(R.string.SetAsMain)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_translate, R.drawable.msg_translate, getString(R.string.TranslateMessage)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_hide_translation, R.drawable.msg_translate, getString(R.string.HideTranslation)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_delete, R.drawable.msg_delete, getString(R.string.Delete)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.addSubItem(gallery_menu_cancel_loading, R.drawable.msg_cancel, getString(R.string.StopDownload)).setColors(0xfffafafa, 0xfffafafa);
        menuItem.redrawPopup(0xf9222222);
        menuItem.hideSubItem(gallery_menu_translate);
        menuItem.hideSubItem(gallery_menu_hide_translation);
        setMenuItemIcon(false, true);
        menuItem.setPopupItemsSelectorColor(0x0fffffff);

        menuItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() {
            @Override
            public void onShowSubMenu() {
                if (videoPlayerControlVisible && isPlaying) {
                    AndroidUtilities.cancelRunOnUIThread(hideActionBarRunnable);
                }
            }

            @Override
            public void onHideSubMenu() {
                if (videoPlayerControlVisible && isPlaying) {
                    scheduleActionBarHide();
                }
            }
        });

        bottomLayout = new FrameLayout(activityContext) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
            }
        };
        bottomLayout.setBackgroundColor(0x7f000000);
        containerView.addView(bottomLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM | Gravity.LEFT));

        navigationBar = new View(activityContext);
        navigationBar.setBackgroundColor((sendPhotoType == SELECT_TYPE_STICKER ? 0xFF000000 : 0x7f000000));
        windowView.addView(navigationBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, navigationBarHeight / AndroidUtilities.density, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL));

        pressedDrawable[0] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {0x32000000, 0});
        pressedDrawable[0].setShape(GradientDrawable.RECTANGLE);
        pressedDrawable[1] = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {0x32000000, 0});
        pressedDrawable[1].setShape(GradientDrawable.RECTANGLE);

        groupedPhotosListView = new GroupedPhotosListView(activityContext, dp(10));
        containerView.addView(groupedPhotosListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 68, Gravity.BOTTOM | Gravity.LEFT));
        groupedPhotosListView.setDelegate(new GroupedPhotosListView.GroupedPhotosListViewDelegate() {
            @Override
            public int getCurrentIndex() {
                return currentIndex;
            }

            @Override
            public int getCurrentAccount() {
                return currentAccount;
            }

            @Override
            public long getAvatarsDialogId() {
                return avatarsDialogId;
            }

            @Override
            public int getSlideshowMessageId() {
                return slideshowMessageId;
            }

            @Override
            public ArrayList<ImageLocation> getImagesArrLocations() {
                return imagesArrLocations;
            }

            @Override
            public ArrayList<MessageObject> getImagesArr() {
                return imagesArr;
            }

            @Override
            public List<TL_iv.PageBlock> getPageBlockArr() {
                return pageBlocksAdapter != null ? pageBlocksAdapter.getAll() : null;
            }

            @Override
            public Object getParentObject() {
                return pageBlocksAdapter != null ? pageBlocksAdapter.getParentObject() : null;
            }

            @Override
            public void setCurrentIndex(int index) {
                currentIndex = -1;
                if (currentThumb != null) {
                    currentThumb.release();
                    currentThumb = null;
                }
                dontAutoPlay = true;
                setImageIndex(index);
                dontAutoPlay = false;
            }

            @Override
            public void onShowAnimationStart() {
                containerView.requestLayout();
            }

            @Override
            public void onStopScrolling() {
                if (shouldMessageObjectAutoPlayed(currentMessageObject)) {
                    playerAutoStarted = true;
                    onActionClick(true);
                    checkProgress(0, false, true);
                }
            }

            @Override
            public boolean validGroupId(long groupId) {
                if (placeProvider != null) {
                    return placeProvider.validateGroupId(groupId);
                }
                return true;
            }

            @Override
            public boolean forceAll() {
                return placeProvider != null && placeProvider.forceAllInGroup();
            }
        });

        for (int a = 0; a < 3; a++) {
            fullscreenButton[a] = new ImageView(parentActivity);
            fullscreenButton[a].setImageResource(R.drawable.msg_maxvideo);
            fullscreenButton[a].setContentDescription(getString("AccSwitchToFullscreen", R.string.AccSwitchToFullscreen));
            fullscreenButton[a].setScaleType(ImageView.ScaleType.CENTER);
            fullscreenButton[a].setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            fullscreenButton[a].setVisibility(View.INVISIBLE);
            fullscreenButton[a].setAlpha(1.0f);
            containerView.addView(fullscreenButton[a], LayoutHelper.createFrame(48, 48));
            fullscreenButton[a].setOnClickListener(v -> {
                if (parentActivity == null) {
                    return;
                }
                wasRotated = false;
                fullscreenedByButton = 1;
                if (prevOrientation == -10) {
                    prevOrientation = parentActivity.getRequestedOrientation();
                }
                WindowManager manager = (WindowManager) parentActivity.getSystemService(Activity.WINDOW_SERVICE);
                int displayRotation = manager.getDefaultDisplay().getRotation();
                if (displayRotation == Surface.ROTATION_270) {
                    parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                } else {
                    parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                toggleActionBar(false, false);
            });
        }

        textSelectionHelper = new TextSelectionHelper.SimpleTextSelectionHelper(null, new DarkThemeResourceProvider()) {
            @Override
            public int getParentBottomPadding() {
                return 0;//AndroidUtilities.dp(80);
            }
        };

        captionTextViewSwitcher = new CaptionTextViewSwitcher(containerView.getContext());
        captionTextViewSwitcher.setFactory(() -> new CaptionTextView(activityContext, captionScrollView, textSelectionHelper, this::onLinkClick, this::onLinkLongPress));
        captionTextViewSwitcher.setVisibility(View.INVISIBLE);
        setCaptionHwLayerEnabled(true);

        for (int a = 0; a < 3; a++) {
            photoProgressViews[a] = new PhotoProgressView(containerView) {
                @Override
                protected void onBackgroundStateUpdated(int state) {
                    if (this == photoProgressViews[0]) {
                        updateAccessibilityOverlayVisibility();
                    }
                }

                @Override
                protected void onVisibilityChanged(boolean visible) {
                    if (this == photoProgressViews[0]) {
                        updateAccessibilityOverlayVisibility();
                    }
                }
            };
            photoProgressViews[a].setBackgroundState(PROGRESS_EMPTY, false, true);
        }

        miniProgressView = new RadialProgressView(activityContext, resourcesProvider) {
            @Override
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                if (containerView != null) {
                    containerView.invalidate();
                }
            }

            @Override
            public void invalidate() {
                super.invalidate();
                if (containerView != null) {
                    containerView.invalidate();
                }
            }
        };
        miniProgressView.setUseSelfAlpha(true);
        miniProgressView.setProgressColor(0xffffffff);
        miniProgressView.setSize(dp(54));
        miniProgressView.setBackgroundResource(R.drawable.circle_big);
        miniProgressView.setVisibility(View.INVISIBLE);
        miniProgressView.setAlpha(0.0f);
        containerView.addView(miniProgressView, LayoutHelper.createFrame(64, 64, Gravity.CENTER));

        createVideoControlsInterface();

        progressView = new RadialProgressView(parentActivity, resourcesProvider);
        progressView.setProgressColor(0xffffffff);
        progressView.setBackgroundResource(R.drawable.circle_big);
        progressView.setVisibility(View.INVISIBLE);
        containerView.addView(progressView, LayoutHelper.createFrame(54, 54, Gravity.CENTER));

        qualityPicker = new PickerBottomLayoutViewer(parentActivity, true, true);
        qualityPicker.setBackgroundColor(0x7f000000);
        qualityPicker.updateSelectedCount(0, false);
        qualityPicker.setTranslationY(dp(120));
        qualityPicker.doneButton.setText(getString("Done", R.string.Done).toUpperCase());
        qualityPicker.doneButton.setTextColor(getThemedColor(Theme.key_chat_editMediaButton));
        containerView.addView(qualityPicker, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM | Gravity.LEFT));
        qualityPicker.cancelButton.setOnClickListener(view -> {
            selectedCompression = previousCompression;
            didChangedCompressionLevel(false);
            showQualityView(false);
            requestVideoPreview(2);
        });
        qualityPicker.doneButton.setOnClickListener(view -> {
            Object object = imagesArrLocals.get(currentIndex);
            if (object instanceof MediaController.MediaEditState) {
                ((MediaController.MediaEditState) object).editedInfo = getCurrentVideoEditedInfo();
            }
            if (selectedCompression != previousCompression && previousCompression == -2) {
                updateItemsState(false);
            }
            showQualityView(false);
            requestVideoPreview(2);
        });
        qualityPicker.originalButton.setOnClickListener(view -> {
            if (selectedCompression != -2) {
                selectedCompression = -2;
                muteVideo = false;
                editState.reset();
                cropTransform = new CropTransform();
                if (paintingOverlay != null) {
                    paintingOverlay.reset();
                    paintingOverlay.setVisibility(View.GONE);
                }
                updateWidthHeightBitrateForCompression();
                updateVideoInfo();
                Object object = imagesArrLocals.get(currentIndex);
                if (object instanceof MediaController.MediaEditState state) {
                    state.resetEdit();
                    state.editedInfo = getCurrentVideoEditedInfo();
                }
                updateItemsState(true);
                if ((sendPhotoType == 0 || sendPhotoType == 4) && placeProvider != null) {
                    placeProvider.updatePhotoAtIndex(currentIndex);
                }
            }
            showQualityView(false);
            requestVideoPreview(2);
        });

        videoForwardDrawable = new VideoForwardDrawable(false);
        videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() {
            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void invalidate() {
                containerView.invalidate();
            }
        });

        seekSpeedDrawable = new SeekSpeedDrawable(containerView::invalidate, false, false);

        qualityChooseView = new QualityChooseView(parentActivity);
        qualityChooseView.setTranslationY(dp(120));
        qualityChooseView.setVisibility(View.INVISIBLE);
        qualityChooseView.setBackgroundColor(0x7f000000);
        containerView.addView(qualityChooseView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 70, Gravity.LEFT | Gravity.BOTTOM, 0, 0, 0, 48));

        Paint pickerBackgroundPaint = new Paint();
        pickerBackgroundPaint.setColor(0x7f000000);
        pickerView = new FrameLayout(activityContext) {

            private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            private final LinearGradient bgGradient = new LinearGradient(0, 0, 0, 16, new int[] { 0, 0x7f000000 }, new float[] { 0, 1 }, Shader.TileMode.CLAMP);
            private final Matrix bgMatrix = new Matrix();

            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!fancyShadows) {
                    int top = 0;
                    if (doneButtonFullWidth.getVisibility() == View.VISIBLE) {
                        top = getMeasuredHeight() - dp(48);
                    }
                    if (sendPhotoType == 0 || sendPhotoType == 2 || sendPhotoType == SELECT_TYPE_NO_SELECT) {
                        bgMatrix.reset();
                        final float gradientHeight = Math.min(dp(40), getMeasuredHeight() - top);
                        bgMatrix.postTranslate(0, top);
                        bgMatrix.postScale(1, gradientHeight / 16f);
                        bgGradient.setLocalMatrix(bgMatrix);
                        bgPaint.setShader(bgGradient);
                    } else {
                        bgPaint.setShader(null);
                        bgPaint.setColor(0x7f000000);
                    }
                    canvas.drawRect(0, top, getMeasuredWidth(), getMeasuredHeight(), bgPaint);
                }
                super.dispatchDraw(canvas);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                ((LayoutParams) itemsLayout.getLayoutParams()).rightMargin = pickerViewSendButton.getVisibility() == View.VISIBLE ? dp(63) : 0;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (videoTimelineViewContainer != null && videoTimelineViewContainer.getVisibility() != GONE) {
                    videoTimelineViewContainer.setTranslationY(pickerView.getTranslationY() - Math.max(0, captionEdit.getEditTextHeight() - dp(46)) * captionEdit.getAlpha());
                }
                if (captionEditContainer != null) {
                    captionEditContainer.setTranslationY(translationY);
                }
                if (videoAvatarTooltip != null && videoAvatarTooltip.getVisibility() != GONE) {
                    videoAvatarTooltip.setTranslationY(translationY);
                }
            }

            @Override
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                if (videoTimelineViewContainer != null && videoTimelineViewContainer.getVisibility() != GONE) {
                    videoTimelineViewContainer.setAlpha(alpha);
                }
                if (captionEdit != null && captionEdit.getVisibility() != GONE) {
                    captionEdit.setAlpha(alpha * captionEditAlpha[0]);
                }
                if (topCaptionEdit != null && topCaptionEdit.getVisibility() != GONE) {
                    topCaptionEdit.setAlpha(alpha * topCaptionEditAlpha[0]);
                }
            }

            @Override
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                if (videoTimelineViewContainer != null && videoTimelineViewContainer.getVisibility() != GONE) {
                    videoTimelineViewContainer.setVisibility(visibility == VISIBLE ? VISIBLE : INVISIBLE);
                }
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (itemsLayout.getVisibility() != GONE) {
                    int rightMargin = pickerViewSendButton.getVisibility() == View.VISIBLE ? dp(63) : 0;
                    int x = (right - left - rightMargin - itemsLayout.getMeasuredWidth()) / 2;
                    itemsLayout.layout(x, itemsLayout.getTop(), x + itemsLayout.getMeasuredWidth(), itemsLayout.getTop() + itemsLayout.getMeasuredHeight());
                }
            }
        };
        containerView.addView(pickerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT));

        docNameTextView = new TextView(containerView.getContext());
        docNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        docNameTextView.setTypeface(AndroidUtilities.bold());
        docNameTextView.setSingleLine(true);
        docNameTextView.setMaxLines(1);
        docNameTextView.setEllipsize(TextUtils.TruncateAt.END);
        docNameTextView.setTextColor(0xffffffff);
        docNameTextView.setGravity(Gravity.LEFT);
        pickerView.addView(docNameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 20, 23, 84, 0));

        docInfoTextView = new TextView(containerView.getContext());
        docInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        docInfoTextView.setSingleLine(true);
        docInfoTextView.setMaxLines(1);
        docInfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        docInfoTextView.setTextColor(0xffffffff);
        docInfoTextView.setGravity(Gravity.LEFT);
        pickerView.addView(docInfoTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 20, 46, 84, 0));


        doneButtonFullWidth = new TextView(containerView.getContext());
        doneButtonFullWidth.setBackground(Theme.AdaptiveRipple.filledRect(getThemedColor(Theme.key_featuredStickers_addButton), 6));
        doneButtonFullWidth.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        doneButtonFullWidth.setEllipsize(TextUtils.TruncateAt.END);
        doneButtonFullWidth.setGravity(Gravity.CENTER);
        doneButtonFullWidth.setLines(1);
        doneButtonFullWidth.setSingleLine(true);
        doneButtonFullWidth.setText(getString("SetAsMyPhoto", R.string.SetAsMyPhoto));
        doneButtonFullWidth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        doneButtonFullWidth.setTypeface(AndroidUtilities.bold());
        doneButtonFullWidth.setOnClickListener(v -> sendPressed(false, 0, 0));
        doneButtonFullWidth.setVisibility(View.GONE);
        pickerView.addView(doneButtonFullWidth, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.TOP | Gravity.LEFT, 20, 0, 20, 48 + 16));

        videoTimelineView = new VideoTimelinePlayView(parentActivity) {
            @Override
            public void setTranslationY(float translationY) {
                if (getTranslationY() != translationY) {
                    super.setTranslationY(translationY);
                    containerView.invalidate();
                }
            }

            private final Path path = new Path();
            private final BlurringShader.StoryBlurDrawer blur = new BlurringShader.StoryBlurDrawer(blurManager, this, BlurringShader.StoryBlurDrawer.BLUR_TYPE_BACKGROUND);

            @Override
            protected boolean customBlur() {
                return true;
            }

            @Override
            protected void drawBlur(Canvas canvas, RectF rect) {
                canvas.save();
                canvas.clipRect(rect);
                canvas.translate(-getX() - videoTimelineViewContainer.getX(), -getY() - videoTimelineViewContainer.getY());
                drawCaptionBlur(canvas, blur, 0xff1e1e1e, 0x33000000, false, true, false);
                canvas.restore();
            }

            @Override
            public void invalidate() {
                if (SharedConfig.photoViewerBlur && (animationInProgress == 1 || animationInProgress == 2 || animationInProgress == 3)) {
                    return;
                }
                super.invalidate();
            }
        };
        videoTimelineView.setDelegate(new VideoTimelinePlayView.VideoTimelineViewDelegate() {

            private Runnable seekToRunnable;
            private int seekTo;
            private boolean wasPlaying;

            @Override
            public void onLeftProgressChanged(float progress) {
                if (videoPlayer == null) {
                    return;
                }
                if (videoPlayer.isPlaying()) {
                    manuallyPaused = false;
                    videoPlayer.pause();
                    containerView.invalidate();
                }
                updateAvatarStartTime(1);
                seekTo(progress);
                videoPlayerSeekbar.setProgress(0);
                videoTimelineView.setProgress(progress);
                updateVideoInfo();
            }

            @Override
            public void onRightProgressChanged(float progress) {
                if (videoPlayer == null) {
                    return;
                }
                if (videoPlayer.isPlaying()) {
                    manuallyPaused = false;
                    videoPlayer.pause();
                    containerView.invalidate();
                }
                updateAvatarStartTime(2);
                seekTo(progress);
                videoPlayerSeekbar.setProgress(1f);
                videoTimelineView.setProgress(progress);
                updateVideoInfo();
            }

            @Override
            public void onPlayProgressChanged(float progress) {
                if (videoPlayer == null) {
                    return;
                }
                if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    updateAvatarStartTime(0);
                }
                seekTo(progress);
            }

            private void seekTo(float progress) {
                seekTo = (int) (videoDuration * progress);
                if (SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_HIGH) {
                    seekVideoOrWebTo(seekTo);
                    if (sendPhotoType == SELECT_TYPE_AVATAR) {
                        needCaptureFrameReadyAtTime = seekTo;
                        if (captureFrameReadyAtTime != needCaptureFrameReadyAtTime) {
                            captureFrameReadyAtTime = -1;
                        }
                    }
                    seekToRunnable = null;
                } else if (seekToRunnable == null) {
                    AndroidUtilities.runOnUIThread(seekToRunnable = () -> {
                        seekVideoOrWebTo(seekTo);
                        if (sendPhotoType == SELECT_TYPE_AVATAR) {
                            needCaptureFrameReadyAtTime = seekTo;
                            if (captureFrameReadyAtTime != needCaptureFrameReadyAtTime) {
                                captureFrameReadyAtTime = -1;
                            }
                        }
                        seekToRunnable = null;
                    }, 100);
                }
            }

            private void updateAvatarStartTime(int fix) {
                if (sendPhotoType != SELECT_TYPE_AVATAR) {
                    return;
                }
                if (fix != 0) {
                    if (photoCropView != null && (videoTimelineView.getLeftProgress() > avatarStartProgress || videoTimelineView.getRightProgress() < avatarStartProgress)) {
                        photoCropView.setVideoThumbVisible(false);
                        if (fix == 1) {
                            avatarStartTime = (long) (videoDuration * 1000 * videoTimelineView.getLeftProgress());
                        } else {
                            avatarStartTime = (long) (videoDuration * 1000 * videoTimelineView.getRightProgress());
                        }
                        captureFrameAtTime = -1;
                    }
                } else {
                    avatarStartProgress = videoTimelineView.getProgress();
                    avatarStartTime = (long) (videoDuration * 1000 * avatarStartProgress);
                }
            }

            @Override
            public void didStartDragging(int type) {
                if (type == VideoTimelinePlayView.TYPE_PROGRESS) {
                    cancelVideoPlayRunnable();
                    if (sendPhotoType == SELECT_TYPE_AVATAR) {
                        cancelFlashAnimations();
                        captureFrameAtTime = -1;
                    }
                    if (wasPlaying = isVideoPlaying()) {
                        manuallyPaused = false;
                        pauseVideoOrWeb();
                        containerView.invalidate();
                    }
                }
            }

            @Override
            public void didStopDragging(int type) {
                if (seekToRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(seekToRunnable);
                    seekToRunnable.run();
                }
                cancelVideoPlayRunnable();
                if (sendPhotoType == SELECT_TYPE_AVATAR && flashView != null && type == VideoTimelinePlayView.TYPE_PROGRESS) {
                    cancelFlashAnimations();
                    captureFrameAtTime = avatarStartTime;
                    if (captureFrameReadyAtTime == seekTo) {
                        captureCurrentFrame();
                    }
                } else {
                    if (sendPhotoType == SELECT_TYPE_AVATAR || wasPlaying) {
                        manuallyPaused = false;
                        playVideoOrWeb();
                    }
                }
            }
        });
        videoTimelineViewContainer = new FrameLayout(parentActivity);
        videoTimelineViewContainer.setClipChildren(false);
        videoTimelineViewContainer.addView(videoTimelineView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 54, Gravity.LEFT | Gravity.BOTTOM));
        showVideoTimeline(false, false);
        containerView.addView(videoTimelineViewContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 54, Gravity.LEFT | Gravity.BOTTOM, 0, 8, 0, 0));

        if (deleteItem != null) {
            deleteItem.setBackground(Blur3Utils.wrapCenteredDrawable(
                iBlur3FactoryFrostedLiquidGlass.create(deleteItem)
                    .setColorProvider(BlurredBackgroundProviderImpl.photoViewer(resourcesProvider))
                    .setRadius(dp(20)).setPadding(dp(7)), dp(54), dp(54)));
        }

        editCoverButton = new EditCoverButton(parentActivity, LocaleController.getString(R.string.EditorSetCover), true) {
            @Override
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
            }
        };
        editCoverButton.setBlurredBackgroundDrawable(iBlur3FactoryFrostedLiquidGlass.create(editCoverButton)
            .setColorProvider(BlurredBackgroundProviderImpl.photoViewer(resourcesProvider)));
        ScaleStateListAnimator.apply(editCoverButton);

        editCoverButton.setOnClickListener(v -> {
            switchToEditMode(EDIT_MODE_COVER);
        });
        containerView.addView(editCoverButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 32, Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 60, 0, 60, 0));

        coverEditor = new PhotoViewerCoverEditor(parentActivity, resourcesProvider, this, blurManager);
        coverEditor.openGalleryButton.setBlurredBackgroundDrawable(iBlur3FactoryFrostedLiquidGlass.create(coverEditor.openGalleryButton)
            .setColorProvider(BlurredBackgroundProviderImpl.photoViewer(resourcesProvider)));
        ScaleStateListAnimator.apply(coverEditor.openGalleryButton);

        coverEditor.setVisibility(View.GONE);
        coverEditor.setAlpha(0.0f);
        coverEditor.setOnClose(() -> {
            switchToEditMode(EDIT_MODE_NONE);
        });
        containerView.addView(coverEditor, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));
        coverEditor.button.setOnClickListener(v -> {
            if (coverEditor.button.isLoading() || currentIndex < 0 || currentIndex >= imagesArrLocals.size() || !(imagesArrLocals.get(currentIndex) instanceof MediaController.PhotoEntry)) {
                return;
            }
            final MediaController.PhotoEntry entry = (MediaController.PhotoEntry) imagesArrLocals.get(currentIndex);
            final long time = coverEditor.getTime();
            final String coverPath = getTempFileAbsolutePath();
            coverEditor.button.setLoading(true);
            Utilities.globalQueue.postRunnable(() -> {

                final Utilities.Callback<Bitmap> onFrame = frame -> {
                    if (frame == null) {
                        AndroidUtilities.runOnUIThread(() -> {
                            coverEditor.button.setLoading(false);
                            switchToEditMode(EDIT_MODE_NONE);
                        });
                        return;
                    }

                    try {
                        FileOutputStream stream = new FileOutputStream(new File(coverPath));
                        frame.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        stream.close();
                    } catch (Exception e) {
                        FileLog.e(e);
                        AndroidUtilities.runOnUIThread(() -> {
                            coverEditor.button.setLoading(false);
                            switchToEditMode(EDIT_MODE_NONE);
                        });
                        return;
                    }
                    final Bitmap bitmap = Bitmap.createBitmap(dp(26), dp(26), Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(bitmap);
                    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    canvas.translate(bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
                    final float scale = Math.max((float) bitmap.getWidth() / frame.getWidth(), (float) bitmap.getHeight() / frame.getHeight());
                    canvas.scale(scale, scale);
                    canvas.drawBitmap(frame, -frame.getWidth() / 2.0f, -frame.getHeight() / 2.0f, paint);
                    AndroidUtilities.runOnUIThread(() -> {
                        if (entry.coverPath != null) {
                            try {
                                new File(entry.coverPath).delete();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                        entry.coverSavedPosition = time;
                        entry.coverPath = coverPath;
                        entry.coverPhoto = null;
                        entry.coverPhotoParentObject = null;
                        coverEditor.button.setLoading(false);
                        if (placeProvider != null) {
                            placeProvider.updatePhotoAtIndex(currentIndex);
                        }
                        if (editCoverButton != null) {
                            editCoverButton.setImage(bitmap);
                        }
                        switchToEditMode(EDIT_MODE_NONE);
                        if (!checkImageView.isChecked()) {
                            checkImageView.callOnClick();
                        }
                    });
                };

                if (usedSurfaceView) {
                    Bitmap toBitmap = Bitmap.createBitmap(videoSurfaceView.getWidth(), videoSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
                    AndroidUtilities.getBitmapFromSurface(videoSurfaceView, toBitmap, () -> {
                        onFrame.run(toBitmap);
                    });
                } else {
                    Bitmap src = videoTextureView.getBitmap(videoTextureView.getWidth(), videoTextureView.getHeight());
                    if (src == null) {
                        onFrame.run(SendMessagesHelper.createVideoThumbnailAtTime(entry.path, time, null, true));
                    } else {
                        onFrame.run(src);
                    }
                }
            });
        });
        coverEditor.setOnGalleryImage(fromEntry -> {
            if (coverEditor.button.isLoading() || currentIndex < 0 || currentIndex >= imagesArrLocals.size() || !(imagesArrLocals.get(currentIndex) instanceof MediaController.PhotoEntry)) {
                return;
            }
            coverEditor.closeGallery();
            coverEditor.button.setLoading(true);
            final MediaController.PhotoEntry entry = (MediaController.PhotoEntry) imagesArrLocals.get(currentIndex);
            final String coverPath = getTempFileAbsolutePath();
            Utilities.globalQueue.postRunnable(() -> {
                Bitmap frame = BitmapFactory.decodeFile(fromEntry.path);
                if (frame == null) {
                    AndroidUtilities.runOnUIThread(() -> {
                        coverEditor.button.setLoading(false);
                        switchToEditMode(EDIT_MODE_NONE);
                    });
                    return;
                }

                int[] params = new int[AnimatedFileInfo.PARAM_NUM_COUNT];
                AnimatedFileNative.getVideoInfo(entry.path, params, 0);

                int w = Math.max(params[AnimatedFileInfo.PARAM_NUM_WIDTH], entry.width);
                int h = Math.max(params[AnimatedFileInfo.PARAM_NUM_HEIGHT], entry.height);
                if ((params[AnimatedFileInfo.PARAM_NUM_ROTATION] / 90) % 2 == 1) {
                    int t = w;
                    w = h;
                    h = t;
                }

                float scale = Math.max((float) frame.getWidth() / w, (float) frame.getHeight() / h);
                final Bitmap croppedFrame = Bitmap.createBitmap((int) (w * scale), (int) (h * scale), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(croppedFrame);
                final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                canvas.translate(croppedFrame.getWidth() / 2, croppedFrame.getHeight() / 2);
                scale = Math.max((float) croppedFrame.getWidth() / frame.getWidth(), (float) croppedFrame.getHeight() / frame.getHeight());
                canvas.scale(scale, scale);
                canvas.drawBitmap(frame, -frame.getWidth() / 2, -frame.getHeight() / 2, paint);

                try {
                    FileOutputStream stream = new FileOutputStream(new File(coverPath));
                    croppedFrame.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    stream.close();
                } catch (Exception e) {
                    FileLog.e(e);
                    AndroidUtilities.runOnUIThread(() -> {
                        coverEditor.button.setLoading(false);
                        switchToEditMode(EDIT_MODE_NONE);
                    });
                    return;
                }

                final Bitmap bitmap = Bitmap.createBitmap(dp(26), dp(26), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                canvas.translate(bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
                scale = Math.max((float) bitmap.getWidth() / croppedFrame.getWidth(), (float) bitmap.getHeight() / croppedFrame.getHeight());
                canvas.scale(scale, scale);
                canvas.drawBitmap(croppedFrame, -croppedFrame.getWidth() / 2.0f, -croppedFrame.getHeight() / 2.0f, paint);

                AndroidUtilities.runOnUIThread(() -> {
                    if (entry.coverPath != null) {
                        try {
                            new File(entry.coverPath).delete();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    entry.coverSavedPosition = -1;
                    entry.coverPath = coverPath;
                    entry.coverPhoto = null;
                    entry.coverPhotoParentObject = null;
                    coverEditor.button.setLoading(false);
                    if (placeProvider != null) {
                        placeProvider.updatePhotoAtIndex(currentIndex);
                    }
                    if (editCoverButton != null) {
                        editCoverButton.setImage(bitmap);
                    }
                    switchToEditMode(EDIT_MODE_NONE);
                    if (!checkImageView.isChecked()) {
                        checkImageView.callOnClick();
                    }
                });
            });
        });

        muteButton = new ImageView(parentActivity) {
            @Override
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (muteHints != null) {
                    for (HintView2 hint : muteHints) {
                        hint.setTranslationY(translationY);
                    }
                }
                if (editCoverButton != null) {
                    editCoverButton.setTranslationY(translationY);
                }
                if (livePhotoButton != null) {
                    livePhotoButton.setTranslationY(translationY);
                }
            }
        };
        muteButton.setScaleType(ImageView.ScaleType.CENTER);
        muteButton.setImageDrawable(muteDrawable = new MuteDrawable(parentActivity));
        muteButton.setColorFilter(new PorterDuffColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN));
        muteButton.setBackground(iBlur3FactoryFrostedLiquidGlass.create(muteButton)
            .setColorProvider(BlurredBackgroundProviderImpl.photoViewer(null))
            .setPadding(dp(4))
            .setRadius(dp(16)));
        ScaleStateListAnimator.apply(muteButton);
        containerView.addView(muteButton, LayoutHelper.createFrame(40, 40, Gravity.LEFT | Gravity.BOTTOM, 8, 0, 0, -4));
        muteButton.setOnClickListener(v -> {
            if (isCaptionOpen()) {
                return;
            }
            muteVideo = !muteVideo;
            if (muteHints != null) {
                for (HintView2 hint : muteHints) {
                    hint.hide();
                }
            }
            if (muteVideo) {
                final HintView2 hint = new HintView2(parentActivity, HintView2.DIRECTION_BOTTOM);
                hint.setMultilineText(true);
                hint.setText(getString(R.string.EditorMuteHint));
                hint.setMaxWidthPx(HintView2.cutInFancyHalf(hint.getText(), hint.getTextPaint()));
                hint.setPadding(dp(6), 0, dp(6), 0);
                hint.setJoint(0, 12 + 16 - 6);
                hint.setOnHiddenListener(() -> {
                    if (muteHints != null) {
                        muteHints.remove(hint);
                    }
                });
                if (muteHints == null) {
                    muteHints = new ArrayList<>();
                }
                containerView.addView(hint, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 200, Gravity.LEFT | Gravity.BOTTOM));
                muteHints.add(hint);
                hint.show();
            }
            updateMuteButton();
            updateVideoInfo();
            if (muteVideo && !checkImageView.isChecked()) {
                checkImageView.callOnClick();
            } else {
                Object object = imagesArrLocals.get(currentIndex);
                if (object instanceof MediaController.MediaEditState) {
                    ((MediaController.MediaEditState) object).editedInfo = getCurrentVideoEditedInfo();
                }
            }
        });

        livePhotoButton = new LivePhotoButton(containerView.getContext());
        livePhotoButton.setOnClickListener(v -> {
            setUnalivePhoto(!isUnalivePhoto());
            livePhotoButton.setValue(!isUnalivePhoto(), true);
            videoTimelineView.animate().alpha(isUnalivePhoto() ? 0.45f : 1.0f).start();

            if (isUnalivePhoto() && videoPlayer != null) {
                videoPlayer.pause();
            }
            containerView.invalidate();

            if (muteHints == null) {
                muteHints = new ArrayList<>();
            }
            if (livePhotoHints == null) {
                livePhotoHints = new ArrayList<>();
            }
            for (HintView2 hint : muteHints) {
                hint.hide();
            }

            final HintView2 hint = new HintView2(parentActivity, HintView2.DIRECTION_BOTTOM);
            hint.setText(AndroidUtilities.replaceTags(getString(isUnalivePhoto() ? R.string.LivePhotoOff : R.string.LivePhotoOn)));
            hint.setPadding(dp(6), 0, dp(6), dp(6));
            hint.setJoint(0, 12 + 16);
            hint.setOnHiddenListener(() -> {
                if (muteHints != null) {
                    muteHints.remove(hint);
                }
                if (livePhotoHints != null) {
                    livePhotoHints.remove(hint);
                }
            });
            containerView.addView(hint, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 200, Gravity.LEFT | Gravity.BOTTOM));
            muteHints.add(hint);
            livePhotoHints.add(hint);
            hint.show();
        });
        containerView.addView(livePhotoButton, LayoutHelper.createFrame(45, 45, Gravity.LEFT | Gravity.BOTTOM, 8, 0, 0, -4));

        captionEdit = new CaptionPhotoViewer(containerView.getContext(), windowView, containerView, containerView, resourcesProvider, blurManager, this::applyCaption) {
            private final Path path = new Path();

            @Override
            protected boolean customBlur() {
                return true;
            }

            @Override
            protected boolean ignoreTouches(float x, float y) {
                return !keyboardShown && currentEditMode != EDIT_MODE_NONE;
            }

            @Override
            public void updateKeyboard(int keyboardHeight) {
                super.updateKeyboard(keyboardHeight - insets.bottom);
                final Bulletin bulletin = Bulletin.getVisibleBulletin();
                if (bulletin != null) {
                    bulletin.updatePosition();
                }
                updateMoveCaptionButton();
                if (bottomBulletinUnderCaption != null) {
                    bottomBulletinUnderCaption.animate()
                        .translationY(-Math.max(0, keyboardHeight - insets.bottom - pickerView.getHeight()))
                        .setDuration(AdjustPanLayoutHelper.keyboardDuration)
                        .setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator)
                        .start();
                }
                actionBar.animate().alpha(isActionBarVisible && (getCaptionView() != topCaptionEdit || !topCaptionEdit.keyboardNotifier.keyboardVisible()) ? 1.0f : 0.0f).start();
                if (pickerView.getVisibility() == View.VISIBLE) {
                    toggleOnlyCheckImageView(isActionBarVisible && (getCaptionView() != topCaptionEdit || !topCaptionEdit.keyboardNotifier.keyboardVisible()));
                }
            }

            @Override
            public void setText(CharSequence text) {
                super.setText(text);
                updateMoveCaptionButton();
            }

            private void updateMoveCaptionButton() {
                final boolean show = (placeProvider != null && placeProvider.canMoveCaptionAbove()) && (captionEdit.keyboardNotifier.keyboardVisible() || !isCurrentVideo && !TextUtils.isEmpty(getCaptionView().getText()));
                setShowMoveButtonVisible(show, true);
            }

            @Override
            protected void drawBlur(BlurringShader.StoryBlurDrawer blur, Canvas canvas, RectF rect, float r, boolean text, float ox, float oy, boolean thisView, float alpha) {
                canvas.save();
                path.rewind();
                path.addRoundRect(rect, r, r, Path.Direction.CW);
                canvas.clipPath(path);
                if (thisView) {
                    canvas.translate(-getX() - captionEditContainer.getX() + ox, -getY() - captionEditContainer.getY() + oy);
                } else {
                    canvas.translate(ox, oy);
                }
                drawCaptionBlur(canvas, blur, Theme.multAlpha(text ? 0xFF787878 : 0xFF262626, alpha), Theme.multAlpha(thisView ? (text ? 0 : 0x33000000) : 0x44000000, alpha), false, !text, !text && thisView);
                canvas.restore();
            }

            @Override
            protected boolean captionLimitToast() {
                if (limitBulletin != null && Bulletin.getVisibleBulletin() == limitBulletin) {
                    return false;
                }
                return showCaptionLimitBulletin(containerView);
            }

            @Override
            protected void setupMentionContainer() {
                mentionContainer.getAdapter().setAllowStickers(false);
                mentionContainer.getAdapter().setAllowBots(false);
                mentionContainer.getAdapter().setAllowChats(false);
                if (parentChatActivity != null) {
                    mentionContainer.getAdapter().setSearchInDialogs(false);
                    mentionContainer.getAdapter().setChatInfo(parentChatActivity.chatInfo);
                    mentionContainer.getAdapter().setNeedUsernames(parentChatActivity.currentChat != null);
                } else {
                    mentionContainer.getAdapter().setSearchInDialogs(true);
                    mentionContainer.getAdapter().setChatInfo(null);
                    mentionContainer.getAdapter().setNeedUsernames(false);
                }
                mentionContainer.getAdapter().setNeedBotContext(false);
            }

            @Override
            public void updateMentionsLayoutPosition() {
                if (mentionContainer != null) {
                    mentionContainer.setTranslationY(-getEditTextHeight() - dp(14) - keyboardNotifier.getKeyboardHeight());
                }
            }

            @Override
            protected void onUpdateShowKeyboard(float keyboardT) {
                super.onUpdateShowKeyboard(keyboardT);
                muteButton.setAlpha((1f - keyboardT) * (muteButton.getTag() != null ? 1 : 0));
                livePhotoButton.setAlpha((1f - keyboardT) * (livePhotoButton.getTag() != null ? 1 : 0));
                videoTimelineViewContainer.setAlpha((1f - keyboardT) * (videoTimelineViewContainer.getTag() != null ? 1 : 0));
            }

            @Override
            public void invalidate() {
                if (SharedConfig.photoViewerBlur && (animationInProgress == 1 || animationInProgress == 2 || animationInProgress == 3)) {
                    return;
                }
                super.invalidate();
            }

            @Override
            protected boolean showMoveButton() {
                return placeProvider != null && placeProvider.canMoveCaptionAbove();
            }

            @Override
            protected boolean isAtTop() {
                return false;
            }

            @Override
            protected void onMoveButtonClick() {
                toggleCaptionAbove();
            }

            @Override
            protected void openedKeyboard() {
                expandMoveButton();
                if (topCaptionEdit != null) {
                    topCaptionEdit.expandMoveButton();
                }
            }
        };
        captionEdit.setBlurredBackgroundDrawableForMentions(iBlur3FactoryFrostedLiquidGlass);
        // captionEdit.editText.getEditText().setBlurredBackgroundDrawableViewFactory(iBlur3FactoryFrostedLiquidGlass);
        captionEdit.setOnTimerChange(seconds -> {
            Object object1 = imagesArrLocals.get(currentIndex);
            if (object1 instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) object1).ttl = seconds;
            } else if (object1 instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) object1).ttl = seconds;
            }
            if (seconds != 0 && !placeProvider.isPhotoChecked(currentIndex)) {
                setPhotoChecked();
            }
            topCaptionEdit.setTimer(seconds);
        });
        captionEdit.setAccount(currentAccount);
        captionEdit.setOnHeightUpdate(height -> {
            if (videoTimelineViewContainer != null && videoTimelineViewContainer.getVisibility() != View.GONE) {
                videoTimelineViewContainer.setTranslationY(pickerView.getTranslationY() - Math.max(0, captionEdit.getEditTextHeight() - dp(46)) * captionEdit.getAlpha());
            }
            muteButton.setTranslationY(-Math.max(0, height - dp(46)) * captionEdit.getAlpha());
            livePhotoButton.setTranslationY(-Math.max(0, height - dp(46)) * captionEdit.getAlpha());
            editCoverButton.setTranslationY(-Math.max(0, height - dp(46)) * captionEdit.getAlpha());
            if (captionEdit.mentionContainer != null) {
                captionEdit.mentionContainer.setTranslationY(-height - dp(14));
            }
        });
        captionEdit.setOnAddPhotoClick(v -> {
            if (placeProvider == null || isCaptionOpen()) {
                return;
            }
            placeProvider.needAddMorePhotos();
            closePhoto(true, false);
        });

        topCaptionEdit = new CaptionPhotoViewer(activityContext, windowView, containerView, containerView, resourcesProvider, blurManager, this::applyCaption) {
            private final Path path = new Path();

            @Override
            protected boolean customBlur() {
                return true;
            }

            @Override
            protected boolean ignoreTouches(float x, float y) {
                return !keyboardShown && currentEditMode != EDIT_MODE_NONE;
            }

            @Override
            public void updateKeyboard(int keyboardHeight) {
                super.updateKeyboard(keyboardHeight - insets.bottom);
                actionBar.animate().alpha(isActionBarVisible && (getCaptionView() != topCaptionEdit || !topCaptionEdit.keyboardNotifier.keyboardVisible()) ? 1.0f : 0.0f).start();
                if (pickerView.getVisibility() == View.VISIBLE) {
                    toggleOnlyCheckImageView(isActionBarVisible && (getCaptionView() != topCaptionEdit || !topCaptionEdit.keyboardNotifier.keyboardVisible()));
                }
            }

            @Override
            protected void drawBlur(BlurringShader.StoryBlurDrawer blur, Canvas canvas, RectF rect, float r, boolean text, float ox, float oy, boolean thisView, float alpha) {
                canvas.save();
                path.rewind();
                path.addRoundRect(rect, r, r, Path.Direction.CW);
                canvas.clipPath(path);
                if (thisView) {
                    canvas.translate(-getX() - topCaptionEditContainer.getX() + ox, -getY() - topCaptionEditContainer.getY() + oy);
                } else {
                    canvas.translate(ox, oy);
                }
                drawCaptionBlur(canvas, blur, Theme.multAlpha(text ? 0xFF787878 : 0xFF262626, alpha), Theme.multAlpha(thisView ? (text ? 0 : 0x33000000) : 0x44000000, alpha), false, !text, !text && thisView);
                canvas.restore();
            }

            @Override
            protected boolean captionLimitToast() {
                if (limitBulletin != null && Bulletin.getVisibleBulletin() == limitBulletin) {
                    return false;
                }
                return showCaptionLimitBulletin(containerView);
            }

            @Override
            public void invalidate() {
                if (SharedConfig.photoViewerBlur && (animationInProgress == 1 || animationInProgress == 2 || animationInProgress == 3)) {
                    return;
                }
                super.invalidate();
            }

            @Override
            protected void setupMentionContainer() {
                mentionContainer.setReversed(true);
                mentionContainer.getAdapter().setAllowStickers(false);
                mentionContainer.getAdapter().setAllowBots(false);
                mentionContainer.getAdapter().setAllowChats(false);
                if (parentChatActivity != null) {
                    mentionContainer.getAdapter().setSearchInDialogs(false);
                    mentionContainer.getAdapter().setChatInfo(parentChatActivity.chatInfo);
                    mentionContainer.getAdapter().setNeedUsernames(parentChatActivity.currentChat != null);
                } else {
                    mentionContainer.getAdapter().setSearchInDialogs(true);
                    mentionContainer.getAdapter().setChatInfo(null);
                    mentionContainer.getAdapter().setNeedUsernames(false);
                }
                mentionContainer.getAdapter().setNeedBotContext(false);
                mentionContainer.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
            }

            @Override
            public void updateMentionsLayoutPosition() {
                if (mentionContainer != null) {
                    mentionContainer.setTranslationY(getEditTextHeight());
                }
            }

            @Override
            protected boolean showMoveButton() {
                return placeProvider != null && placeProvider.canMoveCaptionAbove();
            }

            @Override
            protected boolean isAtTop() {
                return true;
            }

            @Override
            protected void onMoveButtonClick() {
                toggleCaptionAbove();
            }

            @Override
            protected void openedKeyboard() {
                expandMoveButton();
                if (captionEdit != null) {
                    captionEdit.expandMoveButton();
                }
            }
        };
        topCaptionEdit.setBlurredBackgroundDrawableForMentions(iBlur3FactoryFrostedLiquidGlass);
        topCaptionEdit.setShowMoveButtonVisible(true, false);
        topCaptionEdit.setOnTimerChange(seconds -> {
            Object object1 = imagesArrLocals.get(currentIndex);
            if (object1 instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) object1).ttl = seconds;
            } else if (object1 instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) object1).ttl = seconds;
            }
            if (seconds != 0 && !placeProvider.isPhotoChecked(currentIndex)) {
                setPhotoChecked();
            }
            captionEdit.setTimer(seconds);
        });
        topCaptionEdit.setAccount(currentAccount);
        topCaptionEdit.setOnHeightUpdate(height -> {
            if (topCaptionEdit.mentionContainer != null) {
                topCaptionEdit.mentionContainer.setTranslationY(height);
            }
        });
        topCaptionEdit.setOnAddPhotoClick(v -> {
            if (placeProvider == null || isCaptionOpen()) {
                return;
            }
            placeProvider.needAddMorePhotos();
            closePhoto(true, false);
        });

        stickerMakerBackgroundView = new StickerMakerBackgroundView(activityContext) {
            @Override
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                windowView.invalidate();
            }
        };
        stickerMakerBackgroundView.setVisibility(View.GONE);
        containerView.addView(stickerMakerBackgroundView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, 0, 0, 0, 0));
        stickerMakerView = new StickerMakerView(activityContext, resourcesProvider);

        stickerMakerView.setCurrentAccount(currentAccount);
        containerView.addView(stickerMakerView, containerView.indexOfChild(actionBar) - 1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, 0, 0, 0, 0));
        cutOutBtn = new BlurButton();
        cutOutBtn.setRad(18);
        cutOutBtn.wrapContentDynamic();
        stickerMakerView.setStickerCutOutBtn(cutOutBtn);
        cutOutBtn.setOnClickListener(v -> {
            if (stickerEmpty) {
                return;
            }
            if (cutOutBtn.isLoading() || cutOutBtn.isUndoCutState()) {
                return;
            }
            if (currentIndex < 0 || currentIndex >= imagesArrLocals.size() || stickerMakerView.isThanosInProgress) {
                return;
            }
            MediaController.MediaEditState entry = (MediaController.MediaEditState) imagesArrLocals.get(currentIndex);
            boolean hasFilters = !TextUtils.isEmpty(entry.filterPath);
            if (cutOutBtn.isCutOutState()) {
                cutOutBtn.setCancelState(true);
                stickerMakerView.enableClippingMode(segmentedObject -> {
                    if (stickerMakerView.hasSegmentedBitmap()) {
                        ThanosEffect thanosEffect = stickerMakerView.getThanosEffect();
                        stickerMakerView.setSegmentedState(true, segmentedObject);
                        Bitmap segmentedImage = stickerMakerView.getSegmentedImage(centerImage.getBitmap(), hasFilters, centerImage.getOrientation());

                        Object object = imagesArrLocals.get(currentIndex);
                        MediaController.PhotoEntry photoEntry = ((MediaController.PhotoEntry) object);

                        if (thanosEffect == null/* || photoEntry.isCropped || centerImage.getOrientation() == 180*/) {
                            Utilities.themeQueue.postRunnable(() -> {
                                applyCurrentEditMode(segmentedImage);
                                AndroidUtilities.runOnUIThread(() -> {
                                    centerImage.setImageBitmap(segmentedImage);
                                    cutOutBtn.setUndoCutState(true);
                                    showStickerMode(true, true);
                                });
                            });
                            stickerMakerView.disableClippingMode();
                            containerView.invalidate();
                            return;
                        }

                        Bitmap bitmap = stickerMakerView.getThanosImage(photoEntry, centerImage.getOrientation());
                        if (bitmap == null) {
                            Utilities.themeQueue.postRunnable(() -> {
                                applyCurrentEditMode(segmentedImage);
                                AndroidUtilities.runOnUIThread(() -> {
                                    centerImage.setImageBitmap(segmentedImage);
                                    cutOutBtn.setUndoCutState(true);
                                    showStickerMode(true, true);
                                });
                            });
                            stickerMakerView.disableClippingMode();
                            containerView.invalidate();
                            return;
                        }
                        if (entry.cropState != null) {
                            bitmap = createCroppedBitmap(bitmap, entry.cropState, new int[]{centerImage.getOrientation(), centerImage.getInvert()}, true);
                        }
                        if (bitmap == null) {
                            Utilities.themeQueue.postRunnable(() -> {
                                applyCurrentEditMode(segmentedImage);
                                AndroidUtilities.runOnUIThread(() -> {
                                    centerImage.setImageBitmap(segmentedImage);
                                    cutOutBtn.setUndoCutState(true);
                                    showStickerMode(true, true);
                                });
                            });
                            stickerMakerView.disableClippingMode();
                            containerView.invalidate();
                            return;
                        }

                        Matrix matrix = new Matrix();
                        int BW = bitmap.getWidth(), BH = bitmap.getHeight();
                        if (!photoEntry.isCropped && centerImage.getOrientation() / 90 % 2 != 0) {
                            BW = bitmap.getHeight();
                            BH = bitmap.getWidth();
                        }
                        float scale = Math.min(
                            (float) getContainerViewWidth() / BW,
                            (float) getContainerViewHeight() / BH
                        );
                        float w = BW * scale, h = BH * scale;
                        float tx = 0, ty = 0;
                        if (centerImage.getOrientation() != 0 && !photoEntry.isCropped || rotate != 0) {
                            final float bw = bitmap.getWidth();
                            final float bh = bitmap.getHeight();
                            final float r = (float) Math.sqrt((bw / 2f) * (bw / 2f) + (bh / 2f) * (bh / 2f));
                            final float d = 2 * r;
                            Bitmap newBitmap = Bitmap.createBitmap((int) d, (int) d, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(newBitmap);
                            canvas.save();
                            canvas.rotate((photoEntry.isCropped ? 0 : centerImage.getOrientation()) + rotate, r, r);
                            canvas.drawBitmap(bitmap, (d - bw) / 2, (d - bh) / 2, null);
                            bitmap.recycle();
                            bitmap = newBitmap;

                            final float pd = 2 * (float) Math.sqrt((w / 2f) * (w / 2f) + (h / 2f) * (h / 2f));
                            tx = -(pd - w) / 2;
                            ty = -(pd - h) / 2;
                            w = pd;
                            h = pd;
                        }
                        matrix.postScale(w, h);
                        matrix.postScale(this.scale, this.scale, w / 2f, h / 2f);
                        matrix.postTranslate(
                            translationX + tx + Math.max(0, (int) ((getContainerViewWidth() - BW * scale) / 2f)),
                            translationY + ty + Math.max(0, (int) ((getContainerViewHeight() - BH * scale) / 2f))
                        );
                        stickerMakerView.isThanosInProgress = true;
                        Utilities.themeQueue.postRunnable(() -> {
                            applyCurrentEditMode(segmentedImage);
                        });
                        Runnable turnOff = () -> stickerMakerView.isThanosInProgress = false;
                        thanosEffect.animate(matrix, bitmap, () -> {
                            centerImage.setImageBitmap(segmentedImage);
                            cutOutBtn.setUndoCutState(true);
                            showStickerMode(true, true);
                            AndroidUtilities.cancelRunOnUIThread(turnOff);
                            AndroidUtilities.runOnUIThread(turnOff, 800);
                        }, () -> {});
                        AndroidUtilities.runOnUIThread(turnOff, 1200);
                    } else {
                        cutOutBtn.setCutOutState(true);
                        showEditStickerMode(false, true);
                    }
                    stickerMakerView.disableClippingMode();
                    containerView.invalidate();
                });
                containerView.invalidate();
            } else if (cutOutBtn.isCancelState()) {
                cutOutBtn.setCutOutState(true);
                showEditStickerMode(false, true);
                stickerMakerView.disableClippingMode();
                containerView.invalidate();
            } else {
                stickerMakerView.resetPaths();
                stickerMakerView.getThanosEffect();
                stickerMakerView.setSegmentedState(false, null);
                centerImage.setImageBitmap(stickerMakerView.getSourceBitmap(hasFilters));
                cutOutBtn.setCutOutState(true);
                showEditStickerMode(false, true);
                applyCurrentEditMode();
            }
        });
        cutOutBtn.setCutOutState(false);
        containerView.addView(cutOutBtn, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36, Gravity.CENTER));

        btnLayout = new LinearLayout(parentActivity);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        eraseBtn = new BlurButton();
        eraseBtn.wrapContent();
        eraseBtn.setRad(18);
        eraseBtn.setEraseState(false);
        eraseBtn.setOnClickListener(v -> {
            eraseBtn.setActive(true, true);
            restoreBtn.setActive(false, true);
            if (stickerMakerView != null) {
                stickerMakerView.setOutlineVisible(false);
            }
            maskPaintViewEraser = true;
            if (maskPaintView != null) {
                maskPaintView.setEraser(maskPaintViewEraser);
            }
            switchToEditMode(EDIT_MODE_STICKER_MASK);
        });
        btnLayout.addView(eraseBtn, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 36));

        btnLayout.addView(new Space(parentActivity), LayoutHelper.createLinear(12, LayoutHelper.MATCH_PARENT));

        restoreBtn = new BlurButton();
        restoreBtn.wrapContent();
        restoreBtn.setRad(18);
        restoreBtn.setRestoreState(false);
        restoreBtn.setOnClickListener(v -> {
            eraseBtn.setActive(false, true);
            restoreBtn.setActive(true, true);
            if (stickerMakerView != null) {
                stickerMakerView.setOutlineVisible(false);
            }
            maskPaintViewEraser = false;
            if (maskPaintView != null) {
                maskPaintView.setEraser(maskPaintViewEraser);
            }
            switchToEditMode(EDIT_MODE_STICKER_MASK);
        });
        btnLayout.addView(restoreBtn, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 36));

        containerView.addView(btnLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 36, Gravity.CENTER));

        undoBtn = new BlurButton();
        undoBtn.setUndoState(false);
        undoBtn.setRad(18);
        undoBtn.wrapContent();
        undoBtn.setOnClickListener(v -> {
            if (maskPaintView == null || !maskPaintView.undo()) {
                switchToEditMode(EDIT_MODE_NONE);
                stickerMakerView.resetPaths();
                MediaController.MediaEditState entry = (MediaController.MediaEditState) imagesArrLocals.get(currentIndex);
                boolean hasFilters = !TextUtils.isEmpty(entry.filterPath);
                if (stickerMakerView != null && !stickerMakerView.empty) {
                    stickerMakerView.setSegmentedState(false, null);
                }
                centerImage.setImageBitmap(stickerMakerView.getSourceBitmap(hasFilters));
                if (stickerMakerView == null || !stickerMakerView.empty) {
                    cutOutBtn.setCutOutState(true);
                }
                showStickerMode(true, true);
            }
        });
        containerView.addView(undoBtn, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 36, Gravity.CENTER));

        outlineBtn = new BlurButton();
        outlineBtn.setOutlineState(false);
        outlineBtn.setRad(18);
        outlineBtn.wrapContent();
        outlineBtn.setOnClickListener(v -> {
            if (stickerMakerView != null) {
                outlineBtn.setActive(!outlineBtn.isActive(), true);
                stickerMakerView.setOutlineVisible(outlineBtn.isActive() && !(eraseBtn.isActive() || restoreBtn.isActive()));
            }
        });
        containerView.addView(outlineBtn, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 36, Gravity.CENTER));

        showEditCaption(false, false);
        showStickerMode(false, false);

        captionEditContainer = new FrameLayout(parentActivity) {
            @Override
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                invalidateBlur();
            }
        };
        captionEditContainer.addView(captionEdit, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.BOTTOM));
        containerView.addView(captionEditContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.BOTTOM, 0, 8, 0, 0));

        topCaptionEditContainer = new FrameLayout(parentActivity);
        topCaptionEditContainer.addView(topCaptionEdit, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
        containerView.addView(topCaptionEditContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 0, 8, 0, 0));

        topBulletinUnderCaption = new FrameLayout(parentActivity);
        containerView.addView(topBulletinUnderCaption, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 120, Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0, 0, 0));

        bottomBulletinUnderCaption = new FrameLayout(parentActivity);
        containerView.addView(bottomBulletinUnderCaption, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 120, Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0, 0, 0));

        videoAvatarTooltip = new TextView(parentActivity);
        videoAvatarTooltip.setSingleLine(true);
        videoAvatarTooltip.setVisibility(View.GONE);
        videoAvatarTooltip.setText(getString("ChooseCover", R.string.ChooseCover));
        videoAvatarTooltip.setGravity(Gravity.CENTER_HORIZONTAL);
        videoAvatarTooltip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        videoAvatarTooltip.setTextColor(0xff8c8c8c);
        containerView.addView(videoAvatarTooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM, 0, 8, 0, 0));

        pickerViewSendButton = new ChatActivityEnterView.SendButton(parentActivity, R.drawable.send_plane_24, resourcesProvider) {
            @Override
            public boolean isOpen() {
                return true;
            }
            @Override
            public boolean isInScheduleMode() {
                return super.isInScheduleMode();
            }
            @Override
            public boolean isInactive() {
                return false;
            }
            @Override
            public boolean shouldDrawBackground() {
                return true;
            }
            @Override
            public int getFillColor() {
                return getThemedColor(Theme.key_chat_editMediaButton);
            }
        };
        pickerViewSendButton.setCircleSize(dp(52), dp(38));
        pickerViewSendButton.newCounterPos = true;
        pickerViewSendButton.setBlurredBackgroundDrawable(iBlur3FactoryFrostedLiquidGlass.create(pickerViewSendButton).setColorProvider(BlurredBackgroundProviderImpl.photoViewer(resourcesProvider)));
        containerView.addView(pickerViewSendButton, LayoutHelper.createFrame(120, 120, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 8, 2f));
        pickerViewSendButton.setContentDescription(getString("Send", R.string.Send));
        ScaleStateListAnimator.apply(pickerViewSendButton);
        pickerViewSendButton.setOnClickListener(v -> {
            if (parentChatActivity != null && parentChatActivity.editingMessageObject != null && parentChatActivity.editingMessageObject.needResendWhenEdit() && !ChatObject.canManageMonoForum(currentAccount, parentChatActivity.editingMessageObject.getDialogId())) {
                final MessageSuggestionParams params = parentFragment != null && parentChatActivity.messageSuggestionParams != null ?
                        parentChatActivity.messageSuggestionParams :
                        MessageSuggestionParams.of(parentChatActivity.editingMessageObject.messageOwner.suggested_post);

                if (!StarsController.isEnoughAmount(currentAccount, params.amount)) {
                    if (parentChatActivity != null) {
                        parentChatActivity.showSuggestionOfferForEditMessage(params);
                    }

                    return;
                }
            }

            if (captionEdit.isCaptionOverLimit()) {
                AndroidUtilities.shakeViewSpring(captionEdit.limitTextView, shiftDp = -shiftDp);
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                if (!MessagesController.getInstance(currentAccount).premiumFeaturesBlocked() && MessagesController.getInstance(currentAccount).captionLengthLimitPremium > captionEdit.getCodePointCount()) {
                    showCaptionLimitBulletin(containerView);
                }
                return;
            }
            if (parentChatActivity != null && parentChatActivity.isInScheduleMode() && !parentChatActivity.isEditingMessageMedia()) {
                showScheduleDatePickerDialog();
            } else {
                sendPressed(!AyuGhostController.getInstance(UserConfig.selectedAccount).isSendWithoutSound(), 0, 0);
            }
        });
        pickerViewSendButton.setOnLongClickListener(view -> {
            if (placeProvider != null && !placeProvider.allowSendingSubmenu()) return false;
            if (sendPhotoType == SELECT_TYPE_STICKER) return false;
            final boolean isStoryViewer = parentFragment != null && parentFragment.getLastStoryViewer() != null;
            if (parentChatActivity != null && parentChatActivity.isInScheduleMode()) return false;
            if (parentChatActivity == null && !isStoryViewer && placeProvider == null) return false;
            if (captionEdit.isCaptionOverLimit()) return false;
            final TLRPC.User user;
            final boolean canScheduleMessage;
            if (parentChatActivity != null) {
                user = parentChatActivity.getCurrentUser();
                canScheduleMessage = parentChatActivity.canScheduleMessage();
            } else if (placeProvider != null) {
                final long dialogId = placeProvider.getDialogId();
                user = dialogId != 0 ? MessagesController.getInstance(currentAccount).getUser(dialogId) : null;
                canScheduleMessage = placeProvider.canSchedule();
            } else {
                return false;
            }

            final boolean canEdit = placeProvider != null && placeProvider.canEdit(currentIndex);
            final boolean canReplace = placeProvider != null && placeProvider.canReplace(currentIndex);
            final boolean userIsSelf = UserObject.isUserSelf(user);

            boolean hasTtl = false;
            if (placeProvider != null && placeProvider.getSelectedPhotos() != null) {
                for (HashMap.Entry<Object, Object> entry : placeProvider.getSelectedPhotos().entrySet()) {
                    Object object = entry.getValue();
                    if (object instanceof MediaController.PhotoEntry) {
                        if (((MediaController.PhotoEntry) object).ttl != 0) {
                            hasTtl = true;
                            break;
                        }
                    } else if (object instanceof MediaController.SearchImage) {
                        if (((MediaController.SearchImage) object).ttl != 0) {
                            hasTtl = true;
                            break;
                        }
                    }
                }
            }

            Object currentObject = imagesArrLocals.get(currentIndex);
            boolean canSpoiler = fragment instanceof ChatActivity && !((ChatActivity) fragment).isSecretChat() && currentObject instanceof MediaController.PhotoEntry;
            boolean spoilerEnabled = false;
            if (canSpoiler) {
                MediaController.PhotoEntry entry = (MediaController.PhotoEntry) currentObject;
                spoilerEnabled = entry.hasSpoiler;
            }

            final boolean showSendAsFile = !canEdit && !isCurrentVideo && !captionEdit.hasTimer();
            final boolean showSchedule = !canEdit && canScheduleMessage && !hasTtl;
            final boolean showWithoutSound = !(canEdit && canReplace) && !userIsSelf;
            final boolean multipleSelected = placeProvider != null && placeProvider.getSelectedCount() > 1;

            boolean sendWithoutSoundNax = AyuGhostController.getInstance(UserConfig.selectedAccount).isSendWithoutSound();

            final ItemOptions options = ItemOptions.makeOptions(containerView, new DarkThemeResourceProvider(), view)
                .addIf(showSendAsFile, R.drawable.msg_sendfile, getString(multipleSelected ? R.string.SendAsFiles : R.string.SendAsFile), () -> sendPressed(!sendWithoutSoundNax, 0, 0, false, true, false))
                .addIf(canReplace, R.drawable.msg_send, getString(R.string.SendAsNewPhoto), () -> sendPressed(!sendWithoutSoundNax, 0, 0))
                .addIf(canReplace, R.drawable.msg_replace, getString(R.string.ReplacePhoto), this::replacePressed)
                .addIf(showSchedule, R.drawable.msg_calendar2, getString(userIsSelf ? R.string.SetReminder : R.string.ScheduleMessage), this::showScheduleDatePickerDialog)
                .addIf(showWithoutSound, sendWithoutSoundNax ? R.drawable.input_notify_on : R.drawable.input_notify_off, sendWithoutSoundNax ? getString(R.string.SendWithSound) : getString(R.string.SendWithoutSound), () -> sendPressed(sendWithoutSoundNax, 0, 0))
                .addIf(canSpoiler, spoilerEnabled ? R.drawable.msg_spoiler_off : R.drawable.msg_spoiler, LocaleController.getString(spoilerEnabled ? R.string.DisablePhotoSpoiler : R.string.EnablePhotoSpoiler), () -> {
                    if (placeProvider != null && !placeProvider.isPhotoChecked(currentIndex)) {
                        setPhotoChecked();
                    }
                    MediaController.PhotoEntry entry = (MediaController.PhotoEntry) currentObject;
                    entry.hasSpoiler = !entry.hasSpoiler;
                    if (placeProvider != null) placeProvider.spoilerPressed();
                });

            if (captionEdit != null && captionEdit.getText().length() > 0) {
                String languageText = Translator.getInputTranslateLangForChat(ChatsHelper.getChatId()).toUpperCase();
                options.add(R.drawable.ic_translate, getString(R.string.TranslateMessage) + ' ' + "(" + languageText + ")", () -> translateComment(Translator.getInputTranslateLangLocaleForChat(ChatsHelper.getChatId())));
            }

            if (options.getItemsCount() == 0) return false;

            options.setGravity(Gravity.RIGHT).show();
            return true;
        });

        pollAttachButtons = new PhotoViewerPollAttachButtons(parentActivity);
        pollAttachButtons.editButton.setBackground(iBlur3FactoryFrostedLiquidGlass.create(pollAttachButtons.editButton)
            .setColorProvider(BlurredBackgroundProviderImpl.photoViewer(resourcesProvider))
            .setRadius(dp(18)).setPadding(dp(7)));
        pollAttachButtons.replaceButton.setBackground(iBlur3FactoryFrostedLiquidGlass.create(pollAttachButtons.replaceButton)
                .setColorProvider(BlurredBackgroundProviderImpl.photoViewer(resourcesProvider))
                .setRadius(dp(18)).setPadding(dp(7)));
        pollAttachButtons.setVisibility(View.GONE);
        pollAttachButtons.editButton.setOnClickListener(v -> animatorPollAttachButtonsVisibility.setValue(false, true));
        pollAttachButtons.replaceButton.setOnClickListener(v -> {
            if (parentActivity == null || placeProvider == null) {
                return;
            }
            placeProvider.onPollAttachReplace();
            closePhoto(true, false);
        });
        containerView.addView(pollAttachButtons, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 56, Gravity.BOTTOM));

        itemsLayout = new LinearLayout(parentActivity) {

            boolean ignoreLayout;

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int visibleItemsCount = 0;
                int count = getChildCount();
                for (int a = 0; a < count; a++) {
                    View v = getChildAt(a);
                    if (v.getVisibility() != VISIBLE) {
                        continue;
                    }
                    visibleItemsCount++;
                }
                int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
                int height = MeasureSpec.getSize(heightMeasureSpec);

                if (visibleItemsCount != 0) {
                    int itemWidth = Math.min(dp(56), width / visibleItemsCount);
                    if (compressItem.getVisibility() == VISIBLE) {
                        ignoreLayout = true;
                        int compressIconWidth;
                        if (selectedCompression < 2) {
                            compressIconWidth = 48;
                        } else {
                            compressIconWidth = 64;
                        }
                        int padding = Math.max(0, (itemWidth - dp(compressIconWidth)) / 2);
                        compressItem.setPadding(padding, 0, padding, 0);
                        ignoreLayout = false;
                    }
                    for (int a = 0; a < count; a++) {
                        View v = getChildAt(a);
                        if (v.getVisibility() == GONE) {
                            continue;
                        }
                        v.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    }
                    setMeasuredDimension(itemWidth * visibleItemsCount + getPaddingLeft() + getPaddingRight(), height);
                } else {
                    setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height);
                }
            }

            @Override
            public void draw(@NonNull Canvas canvas) {
                for (int a = 0, N = getChildCount(); a < N; a++) {
                    if (getChildAt(a).getVisibility() == VISIBLE) {
                        super.draw(canvas);
                        return;
                    }
                }
            }
        };
        itemsLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemsLayout.setPadding(dp(2), 0, dp(2), 0);
        itemsLayout.setBackground(iBlur3FactoryFrostedLiquidGlass.create(itemsLayout)
            .setColorProvider(BlurredBackgroundProviderImpl.photoViewer(resourcesProvider))
            .setPadding(dp(2))
            .setRadius(dp(22)));

        pickerView.addView(itemsLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 48, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 3, 63, 0));

        cropItem = new ImageView(parentActivity);
        cropItem.setScaleType(ImageView.ScaleType.CENTER);
        cropItem.setImageResource(R.drawable.media_crop);
        cropItem.setBackground(Theme.createInsetRoundRectDrawable(0x10FFFFFF, dp(22), dp(4), dp(6)));
        itemsLayout.addView(cropItem, LayoutHelper.createLinear(48, 48));
        cropItem.setOnClickListener(v -> {
            cancelStickerClippingMode();
            if (isCaptionOpen()) {
                return;
            }
            if (isCurrentVideo) {
                if (!videoConvertSupported) {
                    return;
                }
                if (videoTextureView instanceof VideoEditTextureView) {
                    VideoEditTextureView textureView = (VideoEditTextureView) videoTextureView;
                    if (textureView.getVideoWidth() <= 0 || textureView.getVideoHeight() <= 0) {
                        return;
                    }
                } else {
                    return;
                }
            }
            switchToEditMode(EDIT_MODE_CROP);
        });
        cropItem.setContentDescription(getString("CropImage", R.string.CropImage));

        rotateItem = new ImageView(parentActivity);
        rotateItem.setScaleType(ImageView.ScaleType.CENTER);
        rotateItem.setImageResource(R.drawable.msg_photo_rotate);
        rotateItem.setBackground(Theme.createInsetRoundRectDrawable(0x10FFFFFF, dp(22), dp(4), dp(6)));
        itemsLayout.addView(rotateItem, LayoutHelper.createLinear(48, 48));
        rotateItem.setOnClickListener(v -> cropRotate(-90));
        rotateItem.setContentDescription(getString("AccDescrRotate", R.string.AccDescrRotate));

        mirrorItem = new ImageView(parentActivity);
        mirrorItem.setScaleType(ImageView.ScaleType.CENTER);
        mirrorItem.setImageResource(R.drawable.media_flip);
        mirrorItem.setBackground(Theme.createInsetRoundRectDrawable(0x10FFFFFF, dp(22), dp(4), dp(6)));
        itemsLayout.addView(mirrorItem, LayoutHelper.createLinear(48, 48));
        mirrorItem.setOnClickListener(v -> cropMirror());
        mirrorItem.setContentDescription(getString("AccDescrMirror", R.string.AccDescrMirror));

        paintItem = new ImageView(parentActivity);
        paintItem.setScaleType(ImageView.ScaleType.CENTER);
        paintItem.setImageResource(R.drawable.media_draw);
        paintItem.setBackground(Theme.createInsetRoundRectDrawable(0x10FFFFFF, dp(22), dp(4), dp(6)));
        itemsLayout.addView(paintItem, LayoutHelper.createLinear(48, 48));
        paintItem.setOnClickListener(v -> {
            cancelStickerClippingMode();
            if (isCaptionOpen()) {
                return;
            }
            if (isCurrentVideo) {
                if (!videoConvertSupported) {
                    return;
                }
                if (videoTextureView instanceof VideoEditTextureView) {
                    VideoEditTextureView textureView = (VideoEditTextureView) videoTextureView;
                    if (textureView.getVideoWidth() <= 0 || textureView.getVideoHeight() <= 0) {
                        return;
                    }
                } else {
                    return;
                }
            }
            switchToEditMode(EDIT_MODE_PAINT);
        });
        paintItem.setContentDescription(getString("AccDescrPhotoEditor", R.string.AccDescrPhotoEditor));

        compressItem = new VideoCompressButton(parentActivity);
        compressItem.setTag(1);
        compressItem.setBackground(Theme.createInsetRoundRectDrawable(0x10FFFFFF, dp(22), dp(4), dp(6)));

        selectedCompression = selectCompression();
        compressItem.setState(videoConvertSupported && compressionsCount > 1, muteVideo, Math.min(resultWidth, resultHeight));
        compressItem.setContentDescription(getString("AccDescrVideoQuality", R.string.AccDescrVideoQuality));
        itemsLayout.addView(compressItem, LayoutHelper.createLinear(48, 48));
        compressItem.setOnClickListener(v -> {
            if (isCaptionOpen() || muteVideo) {
                return;
            }
            if (currentIndex >= 0 && currentIndex < imagesArrLocals.size()) {
                Object object = imagesArrLocals.get(currentIndex);
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                    if (!photoEntry.isVideo || photoEntry.isLivePhoto()) {
                        photoEntry.highQuality = !photoEntry.isHighQuality();
                        compressItem.setPhotoState(photoEntry.isHighQuality());
                        showPhotoQualityHint(photoEntry.isHighQuality());

                        SharedConfig.photoHighQualityDefault = photoEntry.isHighQuality();
                        final SharedPreferences prefs = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                        prefs.edit().putBoolean("photoHighQualityDefault", SharedConfig.photoHighQualityDefault).apply();
                        return;
                    }
                }
            }
            if (compressItem.getTag() == null) {
                if (videoConvertSupported) {
                    if (tooltip == null) {
                        tooltip = new Tooltip(activity, containerView, 0xcc111111, Color.WHITE);
                    }
                    tooltip.setText(getString("VideoQualityIsTooLow", R.string.VideoQualityIsTooLow));
                    tooltip.show(compressItem);
                }
                return;
            }
            showQualityView(true);
            requestVideoPreview(1);
        });

        tuneItem = new ImageView(parentActivity);
        tuneItem.setScaleType(ImageView.ScaleType.CENTER);
        tuneItem.setImageResource(R.drawable.media_settings);
        tuneItem.setBackground(Theme.createInsetRoundRectDrawable(0x10FFFFFF, dp(22), dp(4), dp(6)));
        itemsLayout.addView(tuneItem, LayoutHelper.createLinear(48, 48));
        tuneItem.setOnClickListener(v -> {
            if (v.getAlpha() < .9f) return;
            cancelStickerClippingMode();
            if (isCaptionOpen()) {
                return;
            }
            if (isCurrentVideo) {
                if (!videoConvertSupported) {
                    return;
                }
                if (videoTextureView instanceof VideoEditTextureView) {
                    VideoEditTextureView textureView = (VideoEditTextureView) videoTextureView;
                    if (textureView.getVideoWidth() <= 0 || textureView.getVideoHeight() <= 0) {
                        return;
                    }
                } else {
                    return;
                }
            }
            switchToEditMode(EDIT_MODE_FILTER);
        });
        tuneItem.setContentDescription(getString("AccDescrPhotoAdjust", R.string.AccDescrPhotoAdjust));

        editorDoneLayout = new PickerBottomLayoutViewer(activityContext);
        editorDoneLayout.setBackgroundColor(0xcc000000);
        editorDoneLayout.updateSelectedCount(0, false);
        editorDoneLayout.setVisibility(View.GONE);
        containerView.addView(editorDoneLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.LEFT | Gravity.BOTTOM));
        editorDoneLayout.cancelButton.setOnClickListener(view -> {
            if (imageMoveAnimation != null) {
                return;
            }
            Runnable onEnd = () -> {
                cropTransform.setViewTransform(previousHasTransform, previousCropPx, previousCropPy, previousCropRotation, previousCropOrientation, previousCropScale, scale1(), scale1(), previousCropPw, previousCropPh, 0, 0, previousCropMirrored);
//                if (previousHasTransform) {
//                    editState.cropState = new MediaController.CropState();
//                    editState.cropState.cropPx = previousCropPx;
//                    editState.cropState.cropPy = previousCropPy;
//                    editState.cropState.cropRotate = previousCropRotation;
//                    editState.cropState.transformRotation = previousCropOrientation;
//                    editState.cropState.cropScale = previousCropScale;
//                    editState.cropState.cropPw = previousCropPw;
//                    editState.cropState.cropPh = previousCropPh;
//                    editState.cropState.mirrored = previousCropMirrored;
//                    editState.cropState.freeform = sendPhotoType != SELECT_TYPE_AVATAR;
//                } else {
//                    editState.cropState = null;
//                }
                switchToEditMode(EDIT_MODE_NONE);
            };
            if (!previousHasTransform) {
                float backRotate = previousCropOrientation - photoCropView.cropView.getStateOrientation();
                if (Math.abs(backRotate) > 180) {
                    backRotate = backRotate < 0 ? 360 + backRotate : -(360 - backRotate);
                }
                cropRotate(backRotate, photoCropView.cropView.getStateMirror(), onEnd);
            } else {
                onEnd.run();
            }
        });
        editorDoneLayout.doneButton.setOnClickListener(view -> {
            if (currentEditMode == EDIT_MODE_CROP && !photoCropView.isReady()) {
                return;
            }
            applyCurrentEditMode();
            switchToEditMode(EDIT_MODE_NONE);
        });

        resetButton = new TextView(activityContext);
        resetButton.setClickable(false);
        resetButton.setVisibility(View.GONE);
        resetButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        resetButton.setTextColor(0xffffffff);
        resetButton.setGravity(Gravity.CENTER);
        resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        resetButton.setPadding(dp(20), 0, dp(20), 0);
        resetButton.setText(getString(R.string.CropReset).toUpperCase());
        resetButton.setTypeface(AndroidUtilities.bold());
        editorDoneLayout.addView(resetButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.CENTER));
        resetButton.setOnClickListener(v -> {
            float backRotate = -photoCropView.cropView.getStateOrientation();
            if (Math.abs(backRotate) > 180) {
                backRotate = backRotate < 0 ? 360 + backRotate : -(360 - backRotate);
            }
            cropRotate(backRotate, photoCropView.cropView.getStateMirror(), () -> {
                photoCropView.reset(true);
            });
        });

        gestureDetector = new GestureDetector2(containerView.getContext(), this);
        gestureDetector.setIsLongpressEnabled(false);
        setDoubleTapEnabled(true);

        ImageReceiver.ImageReceiverDelegate imageReceiverDelegate = (imageReceiver, set, thumb, memCache) -> {
            if (imageReceiver == centerImage && set && !thumb) {
                if (!isCurrentVideo && (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR || sendPhotoType == SELECT_TYPE_STICKER) && photoCropView != null) {
                    Bitmap bitmap = imageReceiver.getBitmap();
                    if (bitmap != null) {
                        photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), sendPhotoType != SELECT_TYPE_AVATAR && sendPhotoType != SELECT_TYPE_STICKER, true, paintingOverlay, cropTransform, null, null);
                    }
                }
                if (paintingOverlay.getVisibility() == View.VISIBLE) {
                    containerView.requestLayout();
                }
                updateWindowHdrColorMode();
                detectFaces();
            }
            if (imageReceiver == centerImage && set && placeProvider != null && placeProvider.scaleToFill() && !ignoreDidSetImage && sendPhotoType != SELECT_TYPE_AVATAR && sendPhotoType != SELECT_TYPE_STICKER) {
                if (!wasLayout) {
                    dontResetZoomOnFirstLayout = true;
                } else {
                    setScaleToFill();
                }
            }
        };

        centerImage.setParentView(containerView);
        centerImage.setCrossfadeAlpha((byte) 2);
        centerImage.setInvalidateAll(true);
        centerImage.setDelegate(imageReceiverDelegate);
        leftImage.setParentView(containerView);
        leftImage.setCrossfadeAlpha((byte) 2);
        leftImage.setInvalidateAll(true);
        leftImage.setDelegate(imageReceiverDelegate);
        rightImage.setParentView(containerView);
        rightImage.setCrossfadeAlpha((byte) 2);
        rightImage.setInvalidateAll(true);
        rightImage.setDelegate(imageReceiverDelegate);

        WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService(Activity.WINDOW_SERVICE);
        int rotation = manager.getDefaultDisplay().getRotation();

        checkImageView = new CheckBox(containerView.getContext(), R.drawable.selectphoto_large);
        checkImageView.setDrawBackground(true);
        checkImageView.setHasBorder(true);
        checkImageView.setSize(34);
        checkImageView.setCheckOffset(dp(1));
        checkImageView.setColor(getThemedColor(Theme.key_chat_editMediaButton), 0xffffffff);
        checkImageView.setVisibility(View.GONE);
        containerView.addView(checkImageView, LayoutHelper.createFrame(34, 34, Gravity.RIGHT | Gravity.TOP, 0, rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_90 ? 61 : 71, 11, 0));
        if (isStatusBarVisible()) {
            ((FrameLayout.LayoutParams) checkImageView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
        }
        checkImageView.setOnClickListener(v -> {
            if (sendPhotoTypeIsPollMedia) {
                return;
            }
            if (isCaptionOpen()) {
                return;
            }
            setPhotoChecked();
        });

        photosCounterView = new CounterView(parentActivity);
        containerView.addView(photosCounterView, LayoutHelper.createFrame(40, 40, Gravity.RIGHT | Gravity.TOP, 0, rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_90 ? 58 : 68, 64, 0));
        if (isStatusBarVisible()) {
            ((FrameLayout.LayoutParams) photosCounterView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
        }
        photosCounterView.setOnClickListener(v -> {
            if (isCaptionOpen() || placeProvider == null || placeProvider.getSelectedPhotosOrder() == null || placeProvider.getSelectedPhotosOrder().isEmpty()) {
                return;
            }
            togglePhotosListView(!isPhotosListViewVisible, true);
        });

        selectedPhotosListView = new SelectedPhotosListView(parentActivity);
        selectedPhotosListView.setVisibility(View.GONE);
        selectedPhotosListView.setAlpha(0.0f);
        selectedPhotosListView.setLayoutManager(new LinearLayoutManager(parentActivity, LinearLayoutManager.HORIZONTAL, true) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScrollerEnd linearSmoothScroller = new LinearSmoothScrollerEnd(recyclerView.getContext()) {
                    @Override
                    protected int calculateTimeForDeceleration(int dx) {
                        return Math.max(180, super.calculateTimeForDeceleration(dx));
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        });
        selectedPhotosListView.setAdapter(selectedPhotosAdapter = new ListAdapter(parentActivity));
        containerView.addView(selectedPhotosListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 103, Gravity.LEFT | Gravity.TOP));
        selectedPhotosListView.setOnItemClickListener((view, position) -> {
            if (!imagesArrLocals.isEmpty() && currentIndex >= 0 && currentIndex < imagesArrLocals.size()) {
                Object entry = imagesArrLocals.get(currentIndex);
                if (entry instanceof MediaController.MediaEditState) {
                    ((MediaController.MediaEditState) entry).editedInfo = getCurrentVideoEditedInfo();
                }
            }
            ignoreDidSetImage = true;
            int idx = imagesArrLocals.indexOf(view.getTag());
            if (idx >= 0) {
                currentIndex = -1;
                setImageIndex(idx);
            }
            ignoreDidSetImage = false;
        });

        hintView = new UndoView(activityContext, null, false, resourcesProvider);
        hintView.setAdditionalTranslationY(dp(112));
        hintView.setColors(0xf9222222, 0xffffffff);
        containerView.addView(hintView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));

        if (AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
            playButtonAccessibilityOverlay = new View(activityContext);
            playButtonAccessibilityOverlay.setContentDescription(getString("AccActionPlay", R.string.AccActionPlay));
            playButtonAccessibilityOverlay.setFocusable(true);
            playButtonAccessibilityOverlay.setOnClickListener(v -> {
                onActionClick(true);
                checkProgress(0, false, true);
            });
            containerView.addView(playButtonAccessibilityOverlay, LayoutHelper.createFrame(64, 64, Gravity.CENTER));
        }

        doneButtonFullWidth.setBackground(Theme.AdaptiveRipple.filledRect(getThemedColor(Theme.key_featuredStickers_addButton), 6));
        doneButtonFullWidth.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));

        textSelectionHelper.allowScrollPrentRelative = true;
        textSelectionHelper.useMovingOffset = false;
        View overlay = textSelectionHelper.getOverlayView(windowView.getContext());
        if (overlay != null) {
            AndroidUtilities.removeFromParent(overlay);
            containerView.addView(overlay);
        }
        textSelectionHelper.setParentView(containerView);
        textSelectionHelper.setInvalidateParent();

        animatorPollAttachButtonsVisibility.setValue(false, false);
    }

    public void invalidateAllGlassAttachedViews() {
        if (iBlur3BlurredDrawables != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (BlurredBackgroundDrawableRenderNode d : iBlur3BlurredDrawables) {
                d.invalidateDisplayList();
            }
        }
        if (glassAttachedViews != null) {
            for (View v : glassAttachedViews) {
                v.invalidate();
            }
        }
    }

    private Bulletin limitBulletin;
    public boolean showCaptionLimitBulletin(FrameLayout view) {
        if (!(parentFragment instanceof ChatActivity) || !ChatObject.isChannelAndNotMegaGroup(((ChatActivity) parentFragment).getCurrentChat())) {
            return false;
        }
        limitBulletin = BulletinFactory.of(view, resourcesProvider).createCaptionLimitBulletin(MessagesController.getInstance(currentAccount).captionLengthLimitPremium, ()->{
            closePhoto(false, false);
            if (parentAlert != null) {
                parentAlert.dismiss(true);
            }
            if (parentFragment != null) {
                pare…62152 tokens truncated…ssage() && !placeProvider.isEditingMessageResend()) ? 0 : MessagesController.getInstance(currentAccount).getSendPaidMessagesStars(dialogId),
                Math.max(1, placeProvider == null ? 1 : placeProvider.getSelectedCount())
            );
        }
    }

    private void resetIndexForDeferredImageLoading() {
        Object mark = centerImage.getMark();
        if (mark != null && mark.equals(MARK_DEFERRED_IMAGE_LOADING)) {
            setIndexToImage(centerImage, currentIndex, null);
        }
    }

    private void setCurrentCaption(MessageObject messageObject, final CharSequence _caption, boolean translating, boolean animated) {
        final CharSequence caption = AnimatedEmojiSpan.cloneSpans(_caption, AnimatedEmojiDrawable.CACHE_TYPE_ALERT_PREVIEW);
        showEditCaption(editing, animated);
        if (editing || sendPhotoType == SELECT_TYPE_AVATAR) {
            getCaptionView().setText(caption);
            captionTextViewSwitcher.setVisibility(View.GONE);
            return;
        } else {
            captionEdit.setVisibility(View.GONE);
            topCaptionEdit.setVisibility(View.GONE);
        }
        if (needCaptionLayout) {
            if (captionTextViewSwitcher.getParent() != pickerView) {
                if (captionContainer != null) {
                    captionContainer.removeView(captionTextViewSwitcher);
                }
                captionTextViewSwitcher.setMeasureAllChildren(false);
                pickerView.addView(captionTextViewSwitcher, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 76, 48));
            }
        } else {
            if (captionScrollView == null) {
                captionContainer = new FrameLayout(containerView.getContext());
                captionTextViewSwitcher.setContainer(captionContainer);
                captionScrollView = new CaptionScrollView(containerView.getContext(), captionTextViewSwitcher, captionContainer) {
                    @Override
                    protected boolean isStatusBarVisible() {
                        return !inBubbleMode;
                    }

                    @Override
                    public void invalidate() {
                        super.invalidate();
                        if (isActionBarVisible) {
                            final int scrollY = getScrollY();
                            final float translationY = captionTextViewSwitcher.getTranslationY();

                            boolean buttonVisible = scrollY == 0 && translationY == 0;
                            boolean enalrgeIconVisible = scrollY == 0 && translationY == 0;

                            if (!buttonVisible) {
                                final int progressBottom = photoProgressViews[0].getY() + photoProgressViews[0].size;
                                final int topMargin = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                                final int captionTop = captionContainer.getTop() + (int) translationY - scrollY + topMargin - dp(12);
                                final int enlargeIconTop = (int) fullscreenButton[0].getY();
                                enalrgeIconVisible = captionTop > enlargeIconTop + dp(32);
                                buttonVisible = captionTop > progressBottom;
                            }
                            if (allowShowFullscreenButton) {
                                if (fullscreenButton[0].getTag() != null && ((Integer) fullscreenButton[0].getTag()) == 3 && enalrgeIconVisible) {
                                    fullscreenButton[0].setTag(2);
                                    fullscreenButton[0].animate().alpha(1).setDuration(150).setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            fullscreenButton[0].setTag(null);
                                        }
                                    }).start();
                                } else if (fullscreenButton[0].getTag() == null && !enalrgeIconVisible) {
                                    fullscreenButton[0].setTag(3);
                                    fullscreenButton[0].animate().alpha(0).setListener(null).setDuration(150).start();
                                }

                            }
                            photoProgressViews[0].setIndexedAlpha(2, buttonVisible ? 1f : 0f, true);
                        }
                    }
                };
                captionTextViewSwitcher.setScrollView(captionScrollView);
                captionContainer.setClipChildren(false);
                captionScrollView.addView(captionContainer, new ViewGroup.LayoutParams(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                containerView.addView(captionScrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.BOTTOM));
            }
            if (captionTextViewSwitcher.getParent() != captionContainer) {
                pickerView.removeView(captionTextViewSwitcher);
                captionTextViewSwitcher.setMeasureAllChildren(true);
                captionContainer.addView(captionTextViewSwitcher, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
                videoPreviewFrame.bringToFront();
            }
            if (messageObject != null && messageObject.isSponsored()) {
                createAdButtonView();
                AndroidUtilities.removeFromParent(adButtonView);
                adButtonTextView.setText(messageObject.sponsoredButtonText);
                captionContainer.addView(adButtonView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 44, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 16, 0, 16, 12));
                captionTextViewSwitcher.setPadding(0, 0, 0, dp(64));
                adButtonView.bringToFront();
            } else if (adButtonView != null) {
                AndroidUtilities.removeFromParent(adButtonView);
                captionTextViewSwitcher.setPadding(0, 0, 0, 0);
            }
        }

        final boolean isCaptionEmpty = TextUtils.isEmpty(caption);
        final boolean isCurrentCaptionEmpty = TextUtils.isEmpty(captionTextViewSwitcher.getCurrentView().getText());

        TextView captionTextView = animated ? captionTextViewSwitcher.getNextView() : captionTextViewSwitcher.getCurrentView();

        if (isCurrentVideo) {
            if (captionTextView.getMaxLines() != 1) {
                captionTextViewSwitcher.getCurrentView().setMaxLines(1);
                captionTextViewSwitcher.getNextView().setMaxLines(1);
                captionTextViewSwitcher.getCurrentView().setSingleLine(true);
                captionTextViewSwitcher.getNextView().setSingleLine(true);
                captionTextViewSwitcher.getCurrentView().setEllipsize(TextUtils.TruncateAt.END);
                captionTextViewSwitcher.getNextView().setEllipsize(TextUtils.TruncateAt.END);
            }
        } else {
            final int maxLines = captionTextView.getMaxLines();
            if (maxLines == 1) {
                captionTextViewSwitcher.getCurrentView().setSingleLine(false);
                captionTextViewSwitcher.getNextView().setSingleLine(false);
            }
            final int newCount;
            if (needCaptionLayout) {
                newCount = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 5 : 10;
            } else {
                newCount = Integer.MAX_VALUE;
            }
            if (maxLines != newCount) {
                captionTextViewSwitcher.getCurrentView().setMaxLines(newCount);
                captionTextViewSwitcher.getNextView().setMaxLines(newCount);
                captionTextViewSwitcher.getCurrentView().setEllipsize(null);
                captionTextViewSwitcher.getNextView().setEllipsize(null);
            }
        }

        captionTextView.setScrollX(0);
        dontChangeCaptionPosition = !needCaptionLayout && animated && isCaptionEmpty;
        boolean withTransition = false;

        if (!needCaptionLayout) {
            captionScrollView.dontChangeTopMargin = false;
        }

        if (animated) {
            withTransition = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TransitionManager.endTransitions(needCaptionLayout ? pickerView : captionScrollView);
            }
            if (needCaptionLayout) {
                final TransitionSet transitionSet = new TransitionSet();
                transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.addTransition(new Fade(Fade.OUT));
                transitionSet.addTransition(new Fade(Fade.IN));
                transitionSet.setDuration(200);
                TransitionManager.beginDelayedTransition(pickerView, transitionSet);
            } else {
                final TransitionSet transition = new TransitionSet()
                        .addTransition(new Fade(Fade.OUT) {
                            @Override
                            public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                                final Animator animator = super.onDisappear(sceneRoot, view, startValues, endValues);
                                if (!isCurrentCaptionEmpty && isCaptionEmpty && view == captionTextViewSwitcher) {
                                    animator.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            captionScrollView.setVisibility(View.INVISIBLE);
                                            captionScrollView.backgroundAlpha = 1f;
                                        }
                                    });
                                    ((ObjectAnimator) animator).addUpdateListener(animation -> {
                                        captionScrollView.backgroundAlpha = (float) animation.getAnimatedValue();
                                        captionScrollView.invalidate();
                                    });
                                }
                                return animator;
                            }
                        })
                        .addTransition(new Fade(Fade.IN) {
                            @Override
                            public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                                final Animator animator = super.onAppear(sceneRoot, view, startValues, endValues);
                                if (isCurrentCaptionEmpty && !isCaptionEmpty && view == captionTextViewSwitcher) {
                                    animator.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            captionScrollView.backgroundAlpha = 1f;
                                        }
                                    });
                                    ((ObjectAnimator) animator).addUpdateListener(animation -> {
                                        captionScrollView.backgroundAlpha = (float) animation.getAnimatedValue();
                                        captionScrollView.invalidate();
                                    });
                                }
                                return animator;
                            }
                        })
                        .setDuration(200);

                if (!isCurrentCaptionEmpty) {
                    captionScrollView.dontChangeTopMargin = true;
                    transition.addTransition(new Transition() {
                        @Override
                        public void captureStartValues(TransitionValues transitionValues) {
                            if (transitionValues.view == captionScrollView) {
                                transitionValues.values.put("scrollY", captionScrollView.getScrollY());
                            }
                        }

                        @Override
                        public void captureEndValues(TransitionValues transitionValues) {
                            if (transitionValues.view == captionTextViewSwitcher) {
                                transitionValues.values.put("translationY", captionScrollView.getPendingMarginTopDiff());
                            }
                        }

                        @Override
                        public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
                            if (startValues.view == captionScrollView) {
                                final ValueAnimator animator = ValueAnimator.ofInt((Integer) startValues.values.get("scrollY"), 0);
                                animator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        captionTextViewSwitcher.getNextView().setText(null);
                                        captionScrollView.applyPendingTopMargin();
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        captionScrollView.stopScrolling();
                                    }
                                });
                                animator.addUpdateListener(a -> captionScrollView.scrollTo(0, (Integer) a.getAnimatedValue()));
                                return animator;
                            } else if (endValues.view == captionTextViewSwitcher) {
                                final int endValue = (int) endValues.values.get("translationY");
                                if (endValue != 0) {
                                    final ObjectAnimator animator = ObjectAnimator.ofFloat(captionTextViewSwitcher, View.TRANSLATION_Y, 0, endValue);
                                    animator.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            captionTextViewSwitcher.setTranslationY(0);
                                        }
                                    });
                                    return animator;
                                }
                            }
                            return null;
                        }
                    });
                }

                if (isCurrentCaptionEmpty && !isCaptionEmpty) {
                    transition.addTarget(captionTextViewSwitcher);
                }

                TransitionManager.beginDelayedTransition(captionScrollView, transition);
            }
        } else {
            captionTextViewSwitcher.getCurrentView().setText(null);
            if (captionScrollView != null) {
                captionScrollView.scrollTo(0, 0);
            }
        }

        boolean switchedToNext = false;
        if (!isCaptionEmpty) {
            Theme.createChatResources(null, true);
            CharSequence str;
            if (messageObject != null && captionTranslated && messageObject.messageOwner != null && messageObject.messageOwner.translatedText != null && TextUtils.equals(messageObject.messageOwner.translatedToLanguage, NekoConfig.translateToLang.String()/*TranslateAlert2.getToLanguage()*/)) {
                str = caption;
            } else if (messageObject != null && !messageObject.messageOwner.entities.isEmpty()) {
                Spannable spannableString = new SpannableString(caption);
                messageObject.addEntitiesToText(spannableString, true, false);
                if (messageObject.isVideo()) {
                    MessageObject.addUrlsByPattern(messageObject.isOutOwner(), spannableString, false, 3, (int) messageObject.getDuration(), false);
                }
                str = Emoji.replaceEmoji(spannableString, captionTextView.getPaint().getFontMetricsInt(), false);
            } else {
                str = Emoji.replaceEmoji(new SpannableStringBuilder(caption), captionTextView.getPaint().getFontMetricsInt(), false);
            }
            if (messageObject != null && messageObject.isSponsored()) {
                str = sponsoredCaption(messageObject, str);
            }
            captionTextViewSwitcher.setTag(str);
            try {
                switchedToNext = captionTextViewSwitcher.setText(str, animated, lastCaptionTranslating != translating);
                if (captionScrollView != null) {
                    captionScrollView.updateTopMargin();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            captionTextView.setScrollY(0);
            captionTextView.setTextColor(0xffffffff);
            boolean visible = isActionBarVisible && (!isCurrentVideo || pickerView.getVisibility() == View.VISIBLE || pageBlocksAdapter != null);
            captionTextViewSwitcher.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        } else {
            if (needCaptionLayout) {
                captionTextViewSwitcher.setText(getString("AddCaption", R.string.AddCaption), animated);
                captionTextViewSwitcher.getCurrentView().setTextColor(0xb2ffffff);
                captionTextViewSwitcher.setTag("empty");
                captionTextViewSwitcher.setVisibility(View.VISIBLE);
            } else {
                captionTextViewSwitcher.setText(null, animated);
                captionTextViewSwitcher.getCurrentView().setTextColor(0xffffffff);
                captionTextViewSwitcher.setVisibility(View.INVISIBLE, !withTransition || isCurrentCaptionEmpty);
                captionTextViewSwitcher.setTag(null);
            }
        }
        if (captionTextViewSwitcher.getCurrentView() instanceof CaptionTextView) {
            ((CaptionTextView) captionTextViewSwitcher.getCurrentView()).setLoading(translating);
        }
        lastCaptionTranslating = !isCaptionEmpty && translating;
    }

    private void setCaptionHwLayerEnabled(boolean enabled) {
        if (captionHwLayerEnabled != enabled) {
            captionHwLayerEnabled = enabled;
            captionTextViewSwitcher.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            captionTextViewSwitcher.getCurrentView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
            captionTextViewSwitcher.getNextView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    private void checkProgress(int a, boolean scroll, boolean animated) {
        int index = currentIndex;
        if (a == 1) {
            index += 1;
        } else if (a == 2) {
            index -= 1;
        }
        if (currentFileNames[a] != null) {
            File f1 = null;
            File f2 = null;
            boolean fileExist = false;
            FileLoader.FileResolver f2Resolver = null;
            boolean isVideo = false;
            boolean canStream = false;
            boolean canAutoPlay = false;
            MessageObject messageObject = null;
            if (a == 0 && currentIndex == 0 && currentAnimation != null) {
                fileExist = currentAnimation.hasBitmap();
            }
            if (currentMessageObject != null) {
                if (index < 0 || index >= imagesArr.size()) {
                    photoProgressViews[a].setBackgroundState(PROGRESS_NONE, animated, true);
                    return;
                }
                messageObject = imagesArr.get(index);
                canAutoPlay = shouldMessageObjectAutoPlayed(messageObject);
                if (sharedMediaType == MediaDataController.MEDIA_FILE && !messageObject.canPreviewDocument()) {
                    photoProgressViews[a].setBackgroundState(PROGRESS_NONE, animated, true);
                    return;
                }
                if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                    f1 = new File(messageObject.messageOwner.attachPath);
                }
                if (MessageObject.getMedia(messageObject.messageOwner) instanceof TLRPC.TL_messageMediaWebPage && MessageObject.getMedia(messageObject.messageOwner).webpage != null && MessageObject.getMedia(messageObject.messageOwner).webpage.document == null) {
                    TLObject fileLocation = getFileLocation(index, null);
                    f2Resolver = () -> FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation, true);
                } else {
                    TLRPC.Message finalMessage = messageObject.messageOwner;
                    f2Resolver = () -> FileLoader.getInstance(currentAccount).getPathToMessage(finalMessage);
                }
                if (messageObject.isVideo()) {
                    canStream = SharedConfig.streamMedia && messageObject.canStreamVideo() && !DialogObject.isEncryptedDialog(messageObject.getDialogId()) || messageObject.hasVideoQualities();
                    isVideo = true;
                }
            } else if (currentBotInlineResult != null) {
                if (index < 0 || index >= imagesArrLocals.size()) {
                    photoProgressViews[a].setBackgroundState(PROGRESS_NONE, animated, true);
                    return;
                }
                TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) imagesArrLocals.get(index);
                if (botInlineResult.type.equals("video") || MessageObject.isVideoDocument(botInlineResult.document)) {
                    if (botInlineResult.document != null) {
                        f1 = FileLoader.getInstance(currentAccount).getPathToAttach(botInlineResult.document);
                    } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                        f1 = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.MD5(botInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content.url, "mp4"));
                    }
                    isVideo = true;
                } else if (botInlineResult.document != null) {
                    f1 = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_DOCUMENT), currentFileNames[a]);
                } else if (botInlineResult.photo != null) {
                    f1 = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_IMAGE), currentFileNames[a]);
                }
                f2 = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), currentFileNames[a]);
            } else if (currentFileLocation != null) {
                if (index < 0 || index >= imagesArrLocationsVideo.size()) {
                    photoProgressViews[a].setBackgroundState(PROGRESS_NONE, animated, true);
                    return;
                }
                ImageLocation location = imagesArrLocationsVideo.get(index);
                if (location != null) {
                    f1 = FileLoader.getInstance(currentAccount).getPathToAttach(location.location, getFileLocationExt(location), false);
                    f2 = FileLoader.getInstance(currentAccount).getPathToAttach(location.location, getFileLocationExt(location), true);
                }
            } else if (currentSecureDocument != null) {
                if (index < 0 || index >= secureDocuments.size()) {
                    photoProgressViews[a].setBackgroundState(PROGRESS_NONE, animated, true);
                    return;
                }
                SecureDocument location = secureDocuments.get(index);
                f1 = FileLoader.getInstance(currentAccount).getPathToAttach(location, true);
                f2 = FileLoader.getInstance(currentAccount).getPathToAttach(location, false);
            } else if (currentPathObject != null) {
                f1 = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_DOCUMENT), currentFileNames[a]);
                f2 = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), currentFileNames[a]);
            } else if (pageBlocksAdapter != null) {
                f1 = pageBlocksAdapter.getFile(index);
                isVideo = pageBlocksAdapter.isVideo(index) || pageBlocksAdapter.isHardwarePlayer(index);
                canStream = isVideo && SharedConfig.streamMedia && pageBlocksAdapter.getMedia(index) instanceof TLRPC.Document;
                canAutoPlay = shouldIndexAutoPlayed(index);
            }
            File f1Final = f1;
            File f2Final = f2;
            FileLoader.FileResolver finalF2Resolver = f2Resolver;
            MessageObject messageObjectFinal = messageObject;
            boolean canStreamFinal = canStream;
            boolean canAutoPlayFinal = !(a == 0 && dontAutoPlay) && canAutoPlay;
            boolean isVideoFinal = isVideo;

            boolean finalFileExist = fileExist;
            Utilities.globalQueue.postRunnable(() -> {
                boolean exists = finalFileExist;
                if (!exists && f1Final != null) {
                    exists = f1Final.exists();
                }

                File f2Local = f2Final;
                File f3Local = null;
                if (f2Local == null && finalF2Resolver != null) {
                    f2Local = finalF2Resolver.getFile();
                } else if (finalF2Resolver != null) {
                    f3Local = finalF2Resolver.getFile();
                }

                if (!exists && f2Local != null) {
                    exists = f2Local.exists();
                }

                if (!exists && f3Local != null) {
                    exists = f3Local.exists();
                }
                if (!exists && a != 0 && messageObjectFinal != null && canStreamFinal) {
                    if (DownloadController.getInstance(currentAccount).canDownloadMedia(messageObjectFinal.messageOwner) != 0) {
                        if ((parentChatActivity == null || parentChatActivity.getCurrentEncryptedChat() == null) && !messageObjectFinal.shouldEncryptPhotoOrVideo()) {
                            final TLRPC.Document document = messageObjectFinal.getDocument();
                            if (document != null) {
                                FileLoader.getInstance(currentAccount).loadFile(document, messageObjectFinal, FileLoader.PRIORITY_LOW, 10);
                            }
                        }
                    }
                }
                boolean existsFinal = exists;
                File finalF2Local = f2Local;
                AndroidUtilities.runOnUIThread(() -> {
                    if (shownControlsByEnd && !actionBarWasShownBeforeByEnd && isPlaying) {
                        photoProgressViews[a].setBackgroundState(PROGRESS_PLAY, false, false);
                        return;
                    }
                    if ((f1Final != null || finalF2Local != null) && (existsFinal || canStreamFinal)) {
                        if (a != 0 || !isPlaying) {
                            if (isVideoFinal && (!canAutoPlayFinal || a == 0 && playerWasPlaying)) {
                                photoProgressViews[a].setBackgroundState(PROGRESS_PLAY, animated, true);
                            } else {
                                photoProgressViews[a].setBackgroundState(PROGRESS_NONE, animated, true);
                            }
                        }
                        if (a == 0 && !menuItem.isSubMenuShowing()) {
                            if (!existsFinal) {
                                if (!FileLoader.getInstance(currentAccount).isLoadingFile(currentFileNames[a])) {
                                    menuItem.hideSubItem(gallery_menu_cancel_loading);
                                } else {
                                    menuItem.showSubItem(gallery_menu_cancel_loading);
                                }
                            } else {
                                menuItem.hideSubItem(gallery_menu_cancel_loading);
                            }
                        }
                    } else {
                        if (isVideoFinal) {
                            if (!FileLoader.getInstance(currentAccount).isLoadingFile(currentFileNames[a])) {
                                photoProgressViews[a].setBackgroundState(PROGRESS_LOAD, false, true);
                            } else {
                                photoProgressViews[a].setBackgroundState(PROGRESS_CANCEL, false, true);
                            }
                        } else {
                            photoProgressViews[a].setBackgroundState(PROGRESS_EMPTY, animated, true);
                        }
                        Float progress = ImageLoader.getInstance().getFileProgress(currentFileNames[a]);
                        if (progress == null) {
                            progress = 0.0f;
                        }
                        photoProgressViews[a].setProgress(progress, false);
                    }
                    if (a == 0) {
                        canZoom = !isEmbedVideo && (!imagesArrLocals.isEmpty() || (currentFileNames[0] != null && photoProgressViews[0].backgroundState != 0));
                    }
                });
            });
        } else {
            boolean isLocalVideo = false;
            if (!imagesArrLocals.isEmpty() && index >= 0 && index < imagesArrLocals.size()) {
                Object object = imagesArrLocals.get(index);
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = ((MediaController.PhotoEntry) object);
                    isLocalVideo = photoEntry.isVideo && (!photoEntry.isLivePhoto() || !photoEntry.isUnalivePhoto() && sendPhotoType != SELECT_TYPE_STICKER);
                }
            }
            if (isLocalVideo) {
                photoProgressViews[a].setBackgroundState(PROGRESS_PLAY, animated, true);
            } else {
                photoProgressViews[a].setBackgroundState(PROGRESS_NONE, animated, true);
            }
        }
    }

    public int getSelectionLength() {
        return getCaptionView().editText != null ? getCaptionView().getSelectionLength() : 0;
    }

    private void setIndexToPaintingOverlay(int index, PaintingOverlay paintingOverlay) {
        if (paintingOverlay == null) {
            return;
        }
        paintingOverlay.reset();
        paintingOverlay.setVisibility(View.GONE);
        if (!imagesArrLocals.isEmpty() && index >= 0 && index < imagesArrLocals.size()) {
            Object object = imagesArrLocals.get(index);
            boolean isVideo = false;
            String paintPath = null;
            ArrayList<VideoEditedInfo.MediaEntity> mediaEntities = null;
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                isVideo = photoEntry.isVideo;
                paintPath = photoEntry.paintPath;
                mediaEntities = photoEntry.mediaEntities;
            } else if (object instanceof MediaController.SearchImage) {
                MediaController.SearchImage photoEntry = (MediaController.SearchImage) object;
                paintPath = photoEntry.paintPath;
                mediaEntities = photoEntry.mediaEntities;
            }
            paintingOverlay.setVisibility(View.VISIBLE);
            paintingOverlay.setData(paintPath, mediaEntities, isVideo, false, sendPhotoType != SELECT_TYPE_STICKER);
        }
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index, CropTransform cropTransform) {
        imageReceiver.setOrientation(0, false);
        if (!secureDocuments.isEmpty()) {
            if (index >= 0 && index < secureDocuments.size()) {
                Object object = secureDocuments.get(index);
                int size = (int) (AndroidUtilities.getPhotoSize() / AndroidUtilities.density);
                ImageReceiver.BitmapHolder placeHolder = null;
                if (currentThumb != null && imageReceiver == centerImage) {
                    placeHolder = currentThumb;
                }
                if (placeHolder == null) {
                    placeHolder = placeProvider.getThumbForPhoto(null, null, index);
                }
                SecureDocument document = secureDocuments.get(index);
                long imageSize = document.secureFile.size;
                imageReceiver.setImage(ImageLocation.getForSecureDocument(document), "d", null, null, placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, imageSize, null, null, 0);
            }
        } else if (!imagesArrLocals.isEmpty()) {
            if (index >= 0 && index < imagesArrLocals.size()) {
                Object object = imagesArrLocals.get(index);
                int size = (int) (AndroidUtilities.getPhotoSize() / AndroidUtilities.density);
                ImageReceiver.BitmapHolder placeHolder = null;
                if (currentThumb != null && imageReceiver == centerImage) {
                    placeHolder = currentThumb;
                }
                if (placeHolder == null) {
                    placeHolder = placeProvider.getThumbForPhoto(null, null, index);
                }
                String path = null;
                TLRPC.Document document = null;
                WebFile webDocument = null;
                ImageLocation videoThumb = null;
                TLRPC.PhotoSize photo = null;
                TLObject photoObject = null;
                MediaController.CropState cropState = null;
                long imageSize = 0;
                String filter = null;
                boolean isVideo = false;
                int cacheType = 0;
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                    cropState = photoEntry.cropState;
                    isVideo = photoEntry.isVideo;
                    if (photoEntry.isVideo && !photoEntry.isLivePhoto()) {
                        if (photoEntry.thumbPath != null) {
                            if (fromCamera) {
                                Bitmap b = BitmapFactory.decodeFile(photoEntry.thumbPath);
                                if (b != null) {
                                    placeHolder = new ImageReceiver.BitmapHolder(b);
                                    photoEntry.thumbPath = null;
                                }
                            } else {
                                path = photoEntry.thumbPath;
                            }
                        } else {
                            path = "vthumb://" + photoEntry.imageId + ":" + photoEntry.path;
                        }
                    } else {
                        if (photoEntry.filterPath != null) {
                            path = photoEntry.filterPath;
                        } else {
                            imageReceiver.setOrientation(photoEntry.orientation, photoEntry.invert, false);
                            path = photoEntry.path;
                        }
                        filter = String.format(Locale.US, "%d_%d", size, size);
                    }
                } else if (object instanceof TLRPC.BotInlineResult) {
                    cacheType = 1;
                    TLRPC.BotInlineResult botInlineResult = ((TLRPC.BotInlineResult) object);
                    if (botInlineResult.type.equals("video") || MessageObject.isVideoDocument(botInlineResult.document)) {
                        if (botInlineResult.document != null) {
                            photo = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.document.thumbs, 90);
                            photoObject = botInlineResult.document;
                        } else if (botInlineResult.thumb instanceof TLRPC.TL_webDocument) {
                            webDocument = WebFile.createWithWebDocument(botInlineResult.thumb);
                        }
                    } else if (botInlineResult.type.equals("gif") && botInlineResult.document != null) {
                        document = botInlineResult.document;
                        imageSize = botInlineResult.document.size;
                        TLRPC.VideoSize videoSize = MessageObject.getDocumentVideoThumb(botInlineResult.document);
                        if (videoSize != null) {
                            videoThumb = ImageLocation.getForDocument(videoSize, document);
                        }
                        filter = "d";
                    } else if (botInlineResult.photo != null) {
                        TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
                        photo = sizeFull;
                        photoObject = botInlineResult.photo;
                        imageSize = sizeFull.size;
                        filter = String.format(Locale.US, "%d_%d", size, size);
                    } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                        if (botInlineResult.type.equals("gif")) {
                            filter = "d";
                            if (botInlineResult.thumb instanceof TLRPC.TL_webDocument && "video/mp4".equals(botInlineResult.thumb.mime_type)) {
                                videoThumb = ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult.thumb));
                            }
                        } else {
                            filter = String.format(Locale.US, "%d_%d", size, size);
                        }
                        webDocument = WebFile.createWithWebDocument(botInlineResult.content);
                    }
                } else if (object instanceof MediaController.SearchImage) {
                    cacheType = 1;
                    MediaController.SearchImage photoEntry = (MediaController.SearchImage) object;
                    if (photoEntry.photoSize != null) {
                        photo = photoEntry.photoSize;
                        photoObject = photoEntry.photo;
                        imageSize = photoEntry.photoSize.size;
                    } else if (photoEntry.filterPath != null) {
                        path = photoEntry.filterPath;
                    } else if (photoEntry.document != null) {
                        document = photoEntry.document;
                        imageSize = photoEntry.document.size;
                    } else {
                        path = photoEntry.imageUrl;
                        imageSize = photoEntry.size;
                    }
                    cropState = photoEntry.cropState;
                    filter = "d";
                }
                if (document != null) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    if (videoThumb != null) {
                        imageReceiver.setImage(ImageLocation.getForDocument(document), "d", videoThumb, null, placeHolder == null ? ImageLocation.getForDocument(thumb, document) : null, String.format(Locale.US, "%d_%d", size, size), placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, imageSize, null, object, cacheType);
                    } else {
                        imageReceiver.setImage(ImageLocation.getForDocument(document), "d", placeHolder == null ? ImageLocation.getForDocument(thumb, document) : null, String.format(Locale.US, "%d_%d", size, size), placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, imageSize, null, object, cacheType);
                    }
                } else if (photo != null) {
                    imageReceiver.setImage(ImageLocation.getForObject(photo, photoObject), filter, placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, imageSize, null, object, cacheType);
                } else if (webDocument != null) {
                    if (videoThumb != null) {
                        imageReceiver.setImage(ImageLocation.getForWebFile(webDocument), filter, videoThumb, null, (Drawable) null, object, cacheType);
                    } else {
                        imageReceiver.setImage(ImageLocation.getForWebFile(webDocument), filter, placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : (isVideo && parentActivity != null ? parentActivity.getResources().getDrawable(R.drawable.nophotos) : null), null, object, cacheType);
                    }
                } else {
                    imageReceiver.setImage(path, filter, placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : (isVideo && parentActivity != null ? parentActivity.getResources().getDrawable(R.drawable.nophotos) : null), null, imageSize);
                }

                if (cropTransform != null) {
                    if (cropState != null) {
                        cropTransform.setViewTransform(true, cropState.cropPx, cropState.cropPy, cropState.cropRotate, cropState.transformRotation, cropState.cropScale, 1.0f, 1.0f, cropState.cropPw, cropState.cropPh, 0, 0, cropState.mirrored);
                    } else {
                        cropTransform.setViewTransform(false);
                    }
                }

                if (imageReceiver == leftImage) {
                    leftCropState = cropState;
                    leftImageIsVideo = isVideo;
                } else if (imageReceiver == rightImage) {
                    rightCropState = cropState;
                    rightImageIsVideo = isVideo;
                }
            } else {
                imageReceiver.setImageBitmap((Bitmap) null);
            }
        } else if (pageBlocksAdapter != null) {
            int[] size = new int[1];
            TLObject media = pageBlocksAdapter.getMedia(index);
            TLRPC.PhotoSize fileLocation = pageBlocksAdapter.getFileLocation(media, size);
            if (fileLocation != null) {
                if (media instanceof TLRPC.Photo) {
                    TLRPC.Photo photo = (TLRPC.Photo) media;
                    ImageReceiver.BitmapHolder placeHolder = null;
                    if (currentThumb != null && imageReceiver == centerImage) {
                        placeHolder = currentThumb;
                    }
                    if (size[0] == 0) {
                        size[0] = -1;
                    }

                    boolean autoDownload = (DownloadController.getInstance(currentAccount).getAutodownloadMask() & DownloadController.AUTODOWNLOAD_TYPE_PHOTO) != 0;
                    boolean needFullImage = autoDownload || currentIndex == index || FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation, true).exists();
                    ImageLocation imageThumbLocation = placeHolder == null
                        ? ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80), photo)
                        : null;
                    BitmapDrawable thumbPlaceHolder = placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null;
                    ImageLocation imageLocation = needFullImage ? ImageLocation.getForPhoto(fileLocation, photo) : null;
                    imageReceiver.setImage(imageLocation, null, imageThumbLocation, "b", thumbPlaceHolder, size[0], null, pageBlocksAdapter.getParentObject(), 1);
                    imageReceiver.setMark(needFullImage ? null : MARK_DEFERRED_IMAGE_LOADING);
                } else if (pageBlocksAdapter.isVideo(index) || pageBlocksAdapter.isHardwarePlayer(index)) {
                    if (!(fileLocation.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                        ImageReceiver.BitmapHolder placeHolder = null;
                        if (currentThumb != null && imageReceiver == centerImage) {
                            placeHolder = currentThumb;
                        }
                        imageReceiver.setImage(null, null, placeHolder == null ? ImageLocation.getForDocument(fileLocation, (TLRPC.Document) media) : null, "b", placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, 0, null, pageBlocksAdapter.getParentObject(), 1);
                    } else {
                        imageReceiver.setImageBitmap(parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                    }
                } else if (imageReceiver == centerImage && currentAnimation != null) {
                    imageReceiver.setImageBitmap(currentAnimation);
                    currentAnimation.addSecondParentView(containerView);
                }
            } else {
                if (size[0] == 0) {
                    imageReceiver.setImageBitmap((Bitmap) null);
                } else {
                    imageReceiver.setImageBitmap(parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                }
            }
        } else {
            MessageObject messageObject;
            if (!imagesArr.isEmpty() && index >= 0 && index < imagesArr.size()) {
                messageObject = imagesArr.get(index);
                imageReceiver.setShouldGenerateQualityThumb(true);
            } else {
                messageObject = null;
            }

            if (messageObject != null) {
                String restrictionReason = MessagesController.getInstance(messageObject.currentAccount).getRestrictionReason(messageObject.messageOwner.restriction_reason);
                if (!TextUtils.isEmpty(restrictionReason)) {
                    imageReceiver.setImageBitmap(parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                    return;
                } else if (messageObject.isVideo()) {
                    if (messageObject.photoThumbs != null && !messageObject.photoThumbs.isEmpty()) {
                        ImageReceiver.BitmapHolder placeHolder = null;
                        if (currentThumb != null && imageReceiver == centerImage) {
                            placeHolder = currentThumb;
                        }
                        TLRPC.PhotoSize thumbLocation = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320);
                        if (messageObject.isLivePhoto()) {
                            imageReceiver.setNeedsQualityThumb(true);
                            imageReceiver.setImage(getImageLocation(index, null), null, placeHolder == null ? ImageLocation.getForObject(thumbLocation, messageObject.photoThumbsObject) : null, "b", placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, 0, null, messageObject, 1);
                        } else {
                            imageReceiver.setNeedsQualityThumb(thumbLocation.w < 100 && thumbLocation.h < 100);
                            imageReceiver.setImage(null, null, placeHolder == null ? ImageLocation.getForObject(thumbLocation, messageObject.photoThumbsObject) : null, "b", placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, 0, null, messageObject, 1);
                        }
                        if (currentThumb != null) {
                            imageReceiver.setOrientation(currentThumb.orientation, false);
                        }
                    } else {
                        imageReceiver.setImageBitmap(parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                    }
                    return;
                } else if (imageReceiver == centerImage && currentAnimation != null) {
                    currentAnimation.addSecondParentView(containerView);
                    imageReceiver.setImageBitmap(currentAnimation);
                    return;
                } else if (sharedMediaType == MediaDataController.MEDIA_FILE) {
                    if (messageObject.canPreviewDocument()) {
                        TLRPC.Document document = messageObject.getDocument();
                        imageReceiver.setNeedsQualityThumb(true);
                        ImageReceiver.BitmapHolder placeHolder = null;
                        if (currentThumb != null && imageReceiver == centerImage) {
                            placeHolder = currentThumb;
                        }
                        int size = (int) (2048 / AndroidUtilities.density);

                        boolean autoDownload = (DownloadController.getInstance(currentAccount).getAutodownloadMask() & DownloadController.AUTODOWNLOAD_TYPE_DOCUMENT) != 0;
                        boolean needFullImage = autoDownload || currentIndex == index || FileLoader.getInstance(currentAccount).getPathToAttach(document).exists();
                        ImageLocation imageThumbLocation = placeHolder == null ? ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100), document) : null;
                        BitmapDrawable thumbPlaceHolder = placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null;
                        ImageLocation imageLocation = needFullImage ? ImageLocation.getForDocument(document) : null;
                        imageReceiver.setImage(imageLocation, String.format(Locale.US, "%d_%d", size, size), imageThumbLocation, "b", thumbPlaceHolder, document.size, null, messageObject, 0);
                        imageReceiver.setMark(needFullImage ? null : MARK_DEFERRED_IMAGE_LOADING);
                    } else {
                        OtherDocumentPlaceholderDrawable drawable = new OtherDocumentPlaceholderDrawable(parentActivity, containerView, messageObject);
                        imageReceiver.setImageBitmap(drawable);
                    }
                    return;
                }
            }
            long[] size = new long[1];
            ImageLocation imageLocation = getImageLocation(index, size);
            TLObject fileLocation = getFileLocation(index, size);
            imageReceiver.setNeedsQualityThumb(true);

            if (imageLocation != null) {
                ImageReceiver.BitmapHolder placeHolder = null;
                if (currentThumb != null && imageReceiver == centerImage) {
                    placeHolder = currentThumb;
                }
                if (size[0] == 0) {
                    size[0] = -1;
                }
                TLRPC.PhotoSize thumbLocation;
                TLObject photoObject;
                if (messageObject != null) {
                    thumbLocation = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                    photoObject = messageObject.photoThumbsObject;
                } else {
                    thumbLocation = null;
                    photoObject = null;
                }
                if (thumbLocation != null && thumbLocation == fileLocation) {
                    thumbLocation = null;
                }
                if (thumbLocation == null && imageLocation.photo != null && imageLocation.photo.sizes != null) {
                    for (int i = 0; i < imageLocation.photo.sizes.size(); ++i) {
                        if (imageLocation.photo.sizes.get(i) instanceof TLRPC.TL_photoStrippedSize) {
                            thumbLocation = imageLocation.photo.sizes.get(i);
                            photoObject = imageLocation.photo;
                            break;
                        }
                    }
                }
                boolean cacheOnly = messageObject != null && messageObject.isWebpage() || avatarsDialogId != 0 || isEvent;
                Object parentObject;
                ImageLocation videoThumb = null;
                if (messageObject != null) {
                    parentObject = messageObject;
                    if (sharedMediaType == MediaDataController.MEDIA_GIF) {
                        TLRPC.Document document = messageObject.getDocument();
                        TLRPC.VideoSize videoSize = MessageObject.getDocumentVideoThumb(document);
                        if (videoSize != null) {
                            videoThumb = ImageLocation.getForDocument(videoSize, document);
                        }
                    }
                } else if (avatarsDialogId != 0) {
                    if (avatarsDialogId > 0) {
                        parentObject = MessagesController.getInstance(currentAccount).getUser(avatarsDialogId);
                    } else {
                        parentObject = MessagesController.getInstance(currentAccount).getChat(-avatarsDialogId);
                    }
                    if (photoObject != null) {
                        parentObject = photoObject;
                    }
                    if (placeHolder == null && thumbLocation instanceof TLRPC.TL_photoStrippedSize) {
                        placeHolder = new ImageReceiver.BitmapHolder(ImageLoader.getStrippedPhotoBitmap(thumbLocation.bytes, "b"));
                    }
                } else {
                    parentObject = null;
                }
                if (videoThumb != null) {
                    String filter = sharedMediaType == MediaDataController.MEDIA_GIF ? ImageLoader.AUTOPLAY_FILTER : null;
                    imageReceiver.setImage(imageLocation, filter, videoThumb, null, placeHolder == null ? ImageLocation.getForObject(thumbLocation, photoObject) : null, "b", placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, size[0], null, parentObject, cacheOnly ? 1 : 0);
                    imageReceiver.setAllowStartAnimation(true);
                } else {
                    String filter;
                    if (avatarsDialogId != 0) {
                        filter = imageLocation.imageType == FileLoader.IMAGE_TYPE_ANIMATION ? ImageLoader.AUTOPLAY_FILTER : null;
                    } else {
                        filter = null;
                    }
                    boolean autoDownload = (DownloadController.getInstance(currentAccount).getAutodownloadMask() & DownloadController.AUTODOWNLOAD_TYPE_PHOTO) != 0;
                    boolean needFullImage = autoDownload || currentIndex == index || FileLoader.getInstance(currentAccount).getPathToAttach(fileLocation).exists();
                    ImageLocation imageThumbLocation = placeHolder == null ? ImageLocation.getForObject(thumbLocation, photoObject) : null;
                    BitmapDrawable thumbPlaceHolder = placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null;
                    int cacheType = cacheOnly ? 1 : 0;
                    ImageLocation fullImage = needFullImage ? imageLocation : null;
                    imageReceiver.setImage(fullImage, filter, imageThumbLocation, "b", thumbPlaceHolder, size[0], null, parentObject, cacheType);
                    imageReceiver.setMark(needFullImage ? null : MARK_DEFERRED_IMAGE_LOADING);
                }
            } else {
                if (size[0] == 0) {
                    imageReceiver.setImageBitmap((Bitmap) null);
                } else {
                    imageReceiver.setImageBitmap(parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                }
            }
        }
    }

    public static boolean isShowingImage(MessageObject object) {
        boolean result = false;
        if (Instance != null) {
            if (!Instance.pipAnimationInProgress && Instance.isVisible && !Instance.disableShowCheck && object != null) {
                MessageObject currentMessageObject = Instance.currentMessageObject;
                if (currentMessageObject == null && Instance.placeProvider != null) {
                    currentMessageObject = Instance.placeProvider.getEditingMessageObject();
                }
                result = currentMessageObject != null && currentMessageObject.getId() == object.getId() && currentMessageObject.getDialogId() == object.getDialogId();
            }
        }
        if (!result && PipInstance != null) {
            result = PipInstance.isVisible && !PipInstance.disableShowCheck && object != null && PipInstance.currentMessageObject != null && PipInstance.currentMessageObject.getId() == object.getId() && PipInstance.currentMessageObject.getDialogId() == object.getDialogId();
        }
        return result;
    }

    public static boolean isPlayingMessageInPip(MessageObject object) {
        return PipInstance != null && object != null && PipInstance.currentMessageObject != null && PipInstance.currentMessageObject.getId() == object.getId() && PipInstance.currentMessageObject.getDialogId() == object.getDialogId();
    }

    public static boolean isPlayingMessage(MessageObject object) {
        return Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && object != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == object.getId() && Instance.currentMessageObject.getDialogId() == object.getDialogId();
    }

    public static boolean isShowingImage(TLRPC.FileLocation object) {
        boolean result = false;
        if (Instance != null) {
            result = Instance.isVisible && !Instance.disableShowCheck && object != null &&
                    ((Instance.currentFileLocation != null && object.local_id == Instance.currentFileLocation.location.local_id && object.volume_id == Instance.currentFileLocation.location.volume_id && object.dc_id == Instance.currentFileLocation.dc_id) ||
                     (Instance.currentFileLocationVideo != null && object.local_id == Instance.currentFileLocationVideo.location.local_id && object.volume_id == Instance.currentFileLocationVideo.location.volume_id && object.dc_id == Instance.currentFileLocationVideo.dc_id));
        }
        return result;
    }

    public static boolean isShowingImage(TLRPC.BotInlineResult object) {
        boolean result = false;
        if (Instance != null) {
            result = Instance.isVisible && !Instance.disableShowCheck && object != null && Instance.currentBotInlineResult != null && object.id == Instance.currentBotInlineResult.id;
        }
        return result;
    }

    public static boolean isShowingImage(String object) {
        boolean result = false;
        if (Instance != null) {
            result = Instance.isVisible && !Instance.disableShowCheck && object != null && object.equals(Instance.currentPathObject);
        }
        return result;
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        parentChatActivity = chatActivity;
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        maxSelectedPhotos = value;
        allowOrder = order;
    }

    public void checkCurrentImageVisibility() {
        if (currentPlaceObject != null) {
            currentPlaceObject.imageReceiver.setVisible(true, true);
        }
        currentPlaceObject = placeProvider == null ? null : placeProvider.getPlaceForPhoto(currentMessageObject, getFileLocation(currentFileLocation), currentIndex, false, false);
        if (currentPlaceObject != null && !currentPlaceObject.keepImageReceiverVisible) {
            currentPlaceObject.imageReceiver.setVisible(false, true);
        }
    }

    public boolean openPhoto(final MessageObject messageObject, ChatActivity chatActivity, long dialogId, long mergeDialogId, long topicId, final PhotoViewerProvider provider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, provider, chatActivity, dialogId, mergeDialogId, topicId, true, null, null);
    }

    public boolean openPhoto(final MessageObject messageObject, int embedSeekTime, ChatActivity chatActivity, long dialogId, long mergeDialogId, long topicId, final PhotoViewerProvider provider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, provider, chatActivity, dialogId, mergeDialogId, topicId, true, null, embedSeekTime);
    }

    public boolean openPhoto(final MessageObject messageObject, long dialogId, long mergeDialogId, long topicId, final PhotoViewerProvider provider, boolean fullScreenVideo) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, provider, null, dialogId, mergeDialogId, topicId, fullScreenVideo, null, null);
    }

    public boolean openPhoto(final TLRPC.FileLocation fileLocation, final PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, null, null, null, null, null, 0, provider, null, 0, 0, 0, true, null, null);
    }

    public boolean openPhotoWithVideo(final TLRPC.FileLocation fileLocation, ImageLocation videoLocation, final PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, null, videoLocation, null, null, null, 0, provider, null, 0, 0, 0, true, null, null);
    }

    public boolean openPhotoWithVideo(final TLRPC.FileLocation fileLocation, ImageLocation imageLocation, ImageLocation videoLocation, final PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, imageLocation, videoLocation, null, null, null, 0, provider, null, 0, 0, 0, true, null, null);
    }

    public boolean openPhoto(final TLRPC.FileLocation fileLocation, final ImageLocation imageLocation, final PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, imageLocation, null, null, null, null, 0, provider, null, 0, 0, 0, true, null, null);
    }

    public boolean openPhoto(final ArrayList<MessageObject> messages, final int index, long dialogId, long mergeDialogId, long topicId, final PhotoViewerProvider provider) {
        return openPhoto(messages.get(index), null, null, null, messages, null, null, index, provider, null, dialogId, mergeDialogId, topicId, true, null, null);
    }

    public boolean openPhoto(final ArrayList<SecureDocument> documents, final int index, final PhotoViewerProvider provider) {
        return openPhoto(null, null, null, null, null, documents, null, index, provider, null, 0, 0, 0, true, null, null);
    }

    public boolean openPhoto(int index, PageBlocksAdapter pageBlocksAdapter, PhotoViewerProvider provider) {
        return openPhoto(null, null, null, null, null, null, null, index, provider, null, 0, 0, 0, true, pageBlocksAdapter, null);
    }

    private static String attrsToString(TLRPC.Document document) {
        if (document == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < document.attributes.size(); ++i) {
            TLRPC.DocumentAttribute a = document.attributes.get(i);
            if (sb.length() > 0) sb.append(",");
            sb.append(a.getClass().getSimpleName());
            if (a instanceof TLRPC.TL_documentAttributeVideo) {
                sb.append("(w=").append(a.w).append(",h=").append(a.h).append(",dur=").append(a.duration).append(")");
            }
        }
        return sb.toString();
    }

    public boolean openPhotoForSelect(final ArrayList<Object> photos, final int index, int type, boolean documentsPicker, final PhotoViewerProvider provider, ChatActivity chatActivity) {
        return openPhotoForSelect(null, null, photos, index, type, documentsPicker, provider, chatActivity);
    }

    public boolean openPhotoForSelect(final TLRPC.FileLocation fileLocation, final ImageLocation imageLocation, final ArrayList<Object> photos, final int index, int type, boolean documentsPicker, final PhotoViewerProvider provider, ChatActivity chatActivity) {
        isDocumentsPicker = documentsPicker;
        if (pickerViewSendButton != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) pickerViewSendButton.getLayoutParams();
            if (type == 4 || type == 5) {
                pickerViewSendButton.setResourceId(R.drawable.send_plane_24);
//                pickerViewSendButton.setImageResource(R.drawable.send_plane_24);
                layoutParams2.bottomMargin = dp(1);
            } else if (type == SELECT_TYPE_POLL_MEDIA || type == SELECT_TYPE_POLL_MEDIA_EDIT || type == SELECT_TYPE_AVATAR || type == SELECT_TYPE_WALLPAPER || type == SELECT_TYPE_QR || type == SELECT_TYPE_STICKER) {
//                pickerViewSendButton.setImageResource(R.drawable.floating_check);
                pickerViewSendButton.setResourceId(R.drawable.floating_check);
                pickerViewSendButton.setPadding(0, dp(1), 0, 0);
                layoutParams2.bottomMargin = dp(1f);
            } else {
//                pickerViewSendButton.setImageResource(R.drawable.send_plane_24);
                pickerViewSendButton.setResourceId(R.drawable.send_plane_24);
                layoutParams2.bottomMargin = dp(1);
            }
            pickerViewSendButton.setLayoutParams(layoutParams2);
        }
        if (type != SELECT_TYPE_STICKER && stickerMakerView != null) {
            stickerEmpty = false;
            if (tuneItem != null) {
                tuneItem.setAlpha(1f);
            }
            if (outlineBtn != null) outlineBtn.setActive(false, false);
            stickerMakerView.clean();
            if (selectedEmojis != null) selectedEmojis.clear();
        }
        if (isVisible && sendPhotoType != type && type == SELECT_TYPE_AVATAR) {
            sendPhotoType = type;
            doneButtonPressed = false;
            actionBarContainer.setTitle("");
            actionBarContainer.setSubtitle("", false);
            placeProvider = provider;
            mergeDialogId = 0;
            currentDialogId = 0;
            selectedPhotosAdapter.notifyDataSetChanged();
            pageBlocksAdapter = null;

            if (velocityTracker == null) {
                velocityTracker = VelocityTracker.obtain();
            }

            isVisible = true;
            isVisibleOrAnimating = true;

            togglePhotosListView(false, false);

            openedFullScreenVideo = false;
            createCropView();
            toggleActionBar(false, false);
            seekToProgressPending2 = 0;
            skipFirstBufferingProgress = false;
            playerInjected = false;

            makeFocusable();

            backgroundDrawable.setAlpha(255);
            containerView.setAlpha(1.0f);
            onPhotoShow(null, fileLocation, imageLocation, null, null, null, photos, index, null);
            initCropView();
            setCropBitmap();
            return true;
        } else if (isVisible && sendPhotoType != type && type == SELECT_TYPE_STICKER) {
            sendPhotoType = type;
            doneButtonPressed = false;
            actionBarContainer.setTitle("");
            actionBarContainer.setSubtitle("", false);
            placeProvider = provider;
            mergeDialogId = 0;
            currentDialogId = 0;
            selectedPhotosAdapter.notifyDataSetChanged();
            pageBlocksAdapter = null;

            if (velocityTracker == null) {
                velocityTracker = VelocityTracker.obtain();
            }

            isVisible = true;
            isVisibleOrAnimating = true;
            togglePhotosListView(false, false);
            seekToProgressPending2 = 0;
            skipFirstBufferingProgress = false;
            playerInjected = false;

            makeFocusable();

            backgroundDrawable.setAlpha(255);
            containerView.setAlpha(1.0f);

            onPhotoShow(null, fileLocation, imageLocation, null, null, null, photos, index, null);
            return true;
        }
        sendPhotoType = type;
        if (sendPhotoType == SELECT_TYPE_GIF) {
            sendPhotoType = 0;
            sendPhotoTypeIsGif = true;
        }

        sendPhotoTypeIsPollMediaEdit = sendPhotoType == SELECT_TYPE_POLL_MEDIA_EDIT;
        if (sendPhotoType == SELECT_TYPE_POLL_MEDIA || sendPhotoTypeIsPollMediaEdit) {
            sendPhotoType = 0;
            sendPhotoTypeIsPollMedia = true;
        }
        animatorPollAttachButtonsVisibility.setValue(sendPhotoTypeIsPollMediaEdit, false);
        if (sendPhotoType == SELECT_TYPE_STICKER) {
            navigationBar.setBackgroundColor(0xFF000000);
        }

        if (actionBarContainer != null && actionBarContainer.subtitleTextView != null) {
            actionBarContainer.subtitleTextView.setVisibility(sendPhotoTypeIsPollMedia ? View.GONE : View.VISIBLE);
        }

        return openPhoto(null, fileLocation, imageLocation, null, null, null, photos, index, provider, chatActivity, 0, 0, 0, true, null, null);
    }

    private int aboutToSwitchTo;

    public void setTitle(CharSequence title) {
        actionBarContainer.setTitle(customTitle = title);
        toggleActionBar(true, false);
    }
    private void openCurrentPhotoInPaintModeForSelect() {
        if (!canSendMediaToParentChatActivity()) {
            return;
        }

        File file = null;
        boolean isVideo = false;
        boolean canEdit = false;
        boolean capReplace = false;
        MessageObject messageObject = null;

        if (currentMessageObject != null) {
            messageObject = currentMessageObject;
            canEdit = currentMessageObject.canEditMedia() && !currentMessageObject.isDocument();
            capReplace = canEdit && currentMessageObject.isOutOwner();
            isVideo = currentMessageObject.isVideo();
            if (!TextUtils.isEmpty(currentMessageObject.messageOwner.attachPath)) {
                file = new File(currentMessageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file == null) {
                file = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
            }
        }

        if (file != null && file.exists()) {
            savedState = new SavedState(currentIndex, new ArrayList<>(imagesArr), placeProvider);

            final ActionBarToggleParams toggleParams = new ActionBarToggleParams().enableStatusBarAnimation(false);
            toggleActionBar(false, true, toggleParams);

            File finalFile = file;
            boolean finalIsVideo = isVideo;
            boolean finalCanEdit = canEdit;
            boolean finalCanReplace = capReplace;
            MessageObject finalMessageObject = messageObject;
            AndroidUtilities.runOnUIThread(() -> {
                Pair<Integer, Integer> orientation = AndroidUtilities.getImageOrientation(finalFile);
                final MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, lastImageId--, 0, finalFile.getAbsolutePath(), finalIsVideo ? 0 : orientation.first, finalIsVideo, 0, 0, 0).setOrientation(orientation);

                sendPhotoType = 2;
                doneButtonPressed = false;
                final PhotoViewerProvider chatPhotoProvider = placeProvider;
                placeProvider = new EmptyPhotoViewerProvider() {

                    private final ImageReceiver.BitmapHolder thumbHolder = centerImage.getBitmapSafe();

                    @Override
                    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview, boolean closing) {
                        return chatPhotoProvider != null ? chatPhotoProvider.getPlaceForPhoto(finalMessageObject, null, 0, needPreview, false) : null;
                    }

                    @Override
                    public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
                        return thumbHolder;
                    }

                    @Override
                    public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, int scheduleRepeatPeriod, boolean forceDocument) {
                        sendMedia(videoEditedInfo, notify, scheduleDate, 0, false, forceDocument);
                    }

                    @Override
                    public void replaceButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
                        if (photoEntry.isCropped || photoEntry.isPainted || photoEntry.isFiltered || videoEditedInfo != null || !TextUtils.isEmpty(photoEntry.caption)) {
                            sendMedia(videoEditedInfo, false, 0, 0, true, false);
                        }
                    }

                    @Override
                    public boolean canEdit(int index) {
                        return chatPhotoProvider != null && finalCanEdit;
                    }

                    @Override
                    public boolean canReplace(int index) {
                        return chatPhotoProvider != null && finalCanReplace;
                    }

                    @Override
                    public MessageObject getEditingMessageObject() {
                        return finalMessageObject;
                    }

                    @Override
                    public boolean canCaptureMorePhotos() {
                        return false;
                    }

                    private void sendMedia(VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, int scheduleRepeatPeriod, boolean replace, boolean forceDocument) {
                        if (parentChatActivity != null) {
                            final MessageObject editingMessageObject = replace ? finalMessageObject : null;
                            if (editingMessageObject != null && !TextUtils.isEmpty(photoEntry.caption)) {
                                editingMessageObject.editingMessage = photoEntry.caption;
                                editingMessageObject.editingMessageEntities = photoEntry.entities;
                            }
                            final MessageObject replyToMsg;
                            final ChatActivity.ReplyQuote replyQuote;
                            if (!replace && finalMessageObject != null) {
                                replyToMsg = finalMessageObject;
                                replyQuote = null;
                            } else {
                                replyToMsg = parentChatActivity.getReplyMessage();
                                replyQuote = parentChatActivity.getReplyQuote();
                            }
                            if (photoEntry.isVideo) {
                                if (videoEditedInfo != null) {
                                    SendMessagesHelper.prepareSendingVideo(parentChatActivity.getAccountInstance(), photoEntry.path, videoEditedInfo, null, null, parentChatActivity.getDialogId(), replyToMsg, parentChatActivity.getThreadMessage(), null, replyQuote, photoEntry.entities, photoEntry.ttl, editingMessageObject, notify, scheduleDate, scheduleRepeatPeriod, forceDocument, photoEntry.hasSpoiler, photoEntry.caption, parentChatActivity.getMessageChatSendParams(), 0, 0, parentChatActivity.getSendMonoForumPeerId(), parentChatActivity.getSendMessageSuggestionParams());
                                } else {
                                    SendMessagesHelper.prepareSendingVideo(parentChatActivity.getAccountInstance(), photoEntry.path, null, null, null, parentChatActivity.getDialogId(), replyToMsg, parentChatActivity.getThreadMessage(), null, replyQuote, photoEntry.entities, photoEntry.ttl, editingMessageObject, notify, scheduleDate, scheduleRepeatPeriod, forceDocument, photoEntry.hasSpoiler, photoEntry.caption, parentChatActivity.getMessageChatSendParams(), 0, 0, parentChatActivity.getSendMonoForumPeerId(), parentChatActivity.getSendMessageSuggestionParams());
                                }
                            } else {
                                if (photoEntry.imagePath != null) {
                                    SendMessagesHelper.prepareSendingPhoto(parentChatActivity.getAccountInstance(), photoEntry.imagePath, photoEntry.thumbPath, null, parentChatActivity.getDialogId(), replyToMsg, parentChatActivity.getThreadMessage(), null, replyQuote, photoEntry.entities, photoEntry.stickers, null, photoEntry.ttl, editingMessageObject, videoEditedInfo, notify, scheduleDate, scheduleRepeatPeriod, 0, forceDocument, photoEntry.caption, parentChatActivity.getMessageChatSendParams(), 0, 0, parentChatActivity.getSendMonoForumPeerId(), parentChatActivity.getSendMessageSuggestionParams());
                                } else if (photoEntry.path != null) {
                                    SendMessagesHelper.prepareSendingPhoto(parentChatActivity.getAccountInstance(), photoEntry.path, photoEntry.thumbPath, null, parentChatActivity.getDialogId(), replyToMsg, parentChatActivity.getThreadMessage(), null, replyQuote, photoEntry.entities, photoEntry.stickers, null, photoEntry.ttl, editingMessageObject, videoEditedInfo, notify, scheduleDate, scheduleRepeatPeriod, 0, forceDocument, photoEntry.caption, parentChatActivity.getMessageChatSendParams(), 0, 0, parentChatActivity.getSendMonoForumPeerId(), parentChatActivity.getSendMessageSuggestionParams());
                                }
                            }
                        }
                    }
                };
                selectedPhotosAdapter.notifyDataSetChanged();

                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                }

                aboutToSwitchTo = EDIT_MODE_PAINT;

                togglePhotosListView(false, false);
                toggleActionBar(true, false);

                if (parentChatActivity != null && parentChatActivity.getChatActivityEnterView() != null && parentChatActivity.isKeyboardVisible()) {
                    parentChatActivity.getChatActivityEnterView().closeKeyboard();
                } else {
                    makeFocusable();
                }
                backgroundDrawable.setAlpha(255);
                containerView.setAlpha(1.0f);

                onPhotoShow(null, null, null, null, null, null, Collections.singletonList(photoEntry), 0, null);

                pickerView.setTranslationY(dp(isCurrentVideo ? 154 : 96));
                pickerViewSendButton.setTranslationY(dp(isCurrentVideo ? 154 : 96));
                actionBar.setTranslationY(-actionBar.getHeight());
                captionTextViewSwitcher.setTranslationY(dp(isCurrentVideo ? 154 : 96));

                createPaintView();
                switchToPaintMode();
                aboutToSwitchTo = EDIT_MODE_NONE;
            }, toggleParams.animationDuration);
        } else {
            showDownloadAlert();
        }
    }

    private boolean checkAnimation() {
        if (animationInProgress != 0) {
            invalidateBlur();
            if (Math.abs(transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
                if (animationEndRunnable != null) {
                    animationEndRunnable.run();
                    animationEndRunnable = null;
                }
                animationInProgress = 0;
            }
        }
        return animationInProgress != 0;
    }

    private void setCropBitmap() {
        if (cropInitied || sendPhotoType != SELECT_TYPE_AVATAR) {
            return;
        }
        if (isCurrentVideo) {
            VideoEditTextureView textureView = (VideoEditTextureView) videoTextureView;
            if (textureView == null || textureView.getVideoWidth() <= 0 || textureView.getVideoHeight() <= 0) {
                return;
            }
        }
        cropInitied = true;
        Bitmap bitmap = centerImage.getBitmap();
        int orientation = centerImage.getOrientation();
        if (bitmap == null) {
            bitmap = animatingImageView.getBitmap();
            orientation = animatingImageView.getOrientation();
        }
        if (bitmap != null || videoTextureView != null) {
            photoCropView.setBitmap(bitmap, orientation, false, false, paintingOverlay, cropTransform, isCurrentVideo ? (VideoEditTextureView) videoTextureView : null, editState.cropState);
        }
    }

    private void initCropView() {
        if (photoCropView == null) {
            return;
        }
        photoCropView.setBitmap(null, 0, false, false, null, null, null, null);
        if (sendPhotoType != SELECT_TYPE_AVATAR) {
            return;
        }
        photoCropView.onAppear();
        photoCropView.setVisibility(View.VISIBLE);
        photoCropView.setAlpha(1.0f);
        photoCropView.onAppeared();
        padImageForHorizontalInsets = true;
    }

    public boolean openPhoto(final MessageObject messageObject, final TLRPC.FileLocation fileLocation, final ImageLocation imageLocation, final ImageLocation videoLocation, final ArrayList<MessageObject> messages, final ArrayList<SecureDocument> documents, final ArrayList<Object> photos, final int index, final PhotoViewerProvider provider, ChatActivity chatActivity, long dialogId, long mDialogId, long topicId, boolean fullScreenVideo, PageBlocksAdapter pageBlocksAdapter, Integer embedSeekTime) {
        if (parentActivity == null || isVisible || provider == null && checkAnimation() || messageObject == null && fileLocation == null && messages == null && photos == null && documents == null && imageLocation == null && pageBlocksAdapter == null) {
            return false;
        }

        final PlaceProviderObject object = provider.getPlaceForPhoto(messageObject, fileLocation, index, true, false);
        WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
        if (attachedToWindow) {
            try {
                wm.removeView(windowView);
                onHideView();
            } catch (Exception e) {
                //don't promt
            }
        }

        try {
            windowLayoutParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
            windowLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            /*if (chatActivity != null && chatActivity.getCurrentEncryptedChat() != null ||
                avatarsDialogId != 0 && MessagesController.getInstance(currentAccount).isPeerNoForwards(avatarsDialogId) ||
                messageObject != null && (MessagesController.getInstance(currentAccount).isPeerNoForwards(messageObject.getDialogId()) ||
                (messageObject.messageOwner != null && messageObject.messageOwner.noforwards)) || messageObject != null && messageObject.hasRevealedExtendedMedia()
            ) {
                windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_SECURE;
                AndroidUtilities.logFlagSecure();
            } else {
                windowLayoutParams.flags &=~ WindowManager.LayoutParams.FLAG_SECURE;
                AndroidUtilities.logFlagSecure();
            }*/
            windowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
            windowView.setFocusable(false);
            containerView.setFocusable(false);
            wm.addView(windowView, windowLayoutParams);
            onShowView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                final OnBackInvokedDispatcher dispatcher = windowView.findOnBackInvokedDispatcher();
                if (dispatcher != null) {
                    dispatcher.registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                        () -> {
                            if (parentActivity instanceof LaunchActivity) {
                                ((LaunchActivity) parentActivity).onBackPressed();
                            } else {
                                if (isVisible()) {
                                    closePhoto(true, false);
                                }
                            }
                        }
                    );
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }

        hasCaptionForAllMedia = false;
        doneButtonPressed = false;
        closePhotoAfterSelect = true;
        closePhotoAfterSelectWithAnimation = false;
        allowShowFullscreenButton = true;
        usedSurfaceView = false;
        parentChatActivity = chatActivity;
        lastTitle = null;
        isEmbedVideo = embedSeekTime != null;
        if (editCoverButton != null) {
            editCoverButton.setImage((Bitmap) null);
        }

        actionBarContainer.setTitle("");
        actionBarContainer.setSubtitle("", false);
        if (countView != null) {
            countView.set(0, 0, false);
            countView.updateShow(false, false);
        }
        actionBar.setTitleScrollNonFitText(false);

        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.customStickerCreated);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.dialogPhotosUpdate);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.dialogDeleted);

        placeProvider = provider;
        mergeDialogId = mDialogId;
        currentDialogId = dialogId;
        currentFilterTag = chatActivity != null ? chatActivity.getFilterTag() : null;
        currentFilterQuery = chatActivity != null ? chatActivity.getFilterQuery() : null;
        currentFiltered = chatActivity != null && chatActivity.isFiltered();

        this.topicId = topicId;
        selectedPhotosAdapter.notifyDataSetChanged();
        this.pageBlocksAdapter = pageBlocksAdapter;
        setAvatarFor = null;

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        isVisible = true;
        isVisibleOrAnimating = true;

        togglePhotosListView(false, false);

        openedFullScreenVideo = !fullScreenVideo;
        if (openedFullScreenVideo) {
            toggleActionBar(false, false);
        } else {
            if (sendPhotoType == SELECT_TYPE_AVATAR) {
                createCropView();
                toggleActionBar(false, false);
            } else {
                toggleActionBar(true, false);
            }
        }

        windowView.setClipChildren(false);
        navigationBar.setVisibility(View.VISIBLE);

        seekToProgressPending2 = 0;
        skipFirstBufferingProgress = false;
        playerInjected = false;
        if (object != null) {
            disableShowCheck = true;
            animationInProgress = 1;
            if (messageObject != null) {
                currentAnimation = object.allowTakeAnimation ? object.imageReceiver.getAnimation() : null;
                if (currentAnimation != null) {
                    if (messageObject.isVideo()) {
                        object.imageReceiver.setAllowStartAnimation(false);
                        object.imageReceiver.stopAnimation();
                        if (MediaController.getInstance().isPlayingMessage(messageObject)) {
                            seekToProgressPending2 = messageObject.audioProgress;
                        }
                        skipFirstBufferingProgress = injectingVideoPlayer == null && !FileLoader.getInstance(messageObject.currentAccount).isLoadingVideo(messageObject.getDocument(), true) && (currentAnimation.hasBitmap() || !FileLoader.getInstance(messageObject.currentAccount).isLoadingVideo(messageObject.getDocument(), false));
                        currentAnimation = null;
                    } else if (messageObject.getWebPagePhotos(null, null).size() > 1) {
                        currentAnimation = null;
                    }
                }
            } else if (pageBlocksAdapter != null) {
                currentAnimation = object.allowTakeAnimation ? object.imageReceiver.getAnimation() : null;
                if (currentAnimation != null && pageBlocksAdapter.isVideo(index)) {
                    object.imageReceiver.setAllowStartAnimation(false);
                    object.imageReceiver.stopAnimation();
                    TLObject media = pageBlocksAdapter.getMedia(index);
                    TLRPC.Document document = media instanceof TLRPC.Document ? (TLRPC.Document) media : null;
                    skipFirstBufferingProgress = injectingVideoPlayer == null && document != null && !FileLoader.getInstance(currentAccount).isLoadingVideo(document, true) && (currentAnimation.hasBitmap() || !FileLoader.getInstance(currentAccount).isLoadingVideo(document, false));
                    currentAnimation = null;
                }
            }

            onPhotoShow(messageObject, fileLocation, imageLocation, videoLocation, messages, documents, photos, index, object);
            if (sendPhotoType == SELECT_TYPE_AVATAR) {
                photoCropView.setVisibility(View.VISIBLE);
                photoCropView.setAlpha(0.0f);
                photoCropView.setFreeform(false);
            }
            final RectF drawRegion = object.imageReceiver.getDrawRegion();
            float left = drawRegion.left;
            float top = drawRegion.top;
            int orientation = object.imageReceiver.getOrientation();
            int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
            if (animatedOrientation != 0) {
                orientation = animatedOrientation;
            }

            final ClippingImageView[] animatingImageViews = getAnimatingImageViews(object);
            clippingImageProgress = 1f;

            for (int i = 0; i < animatingImageViews.length; i++) {
                animatingImageViews[i].setAnimationValues(animationValues, true, object.fadeIn);
                animatingImageViews[i].setVisibility(View.VISIBLE);
                animatingImageViews[i].setRadius(object.radius);
                animatingImageViews[i].setOrientation(orientation,  object.imageReceiver.getInvert());
                animatingImageViews[i].setImageBitmap(object.thumb);
            }

            initCropView();
            if (sendPhotoType == SELECT_TYPE_AVATAR) {
                photoCropView.setAspectRatio(1.0f);
            }

            final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
            layoutParams.width = (int) drawRegion.width();
            layoutParams.height = (int) drawRegion.height();
            if (layoutParams.width <= 0) {
                layoutParams.width = 100;
            }
            if (layoutParams.height <= 0) {
                layoutParams.height = 100;
            }

            for (int i = 0; i < animatingImageViews.length; i++) {
                if (animatingImageViews.length > 1 || object.fadeIn) {
                    animatingImageViews[i].setAlpha(0.0f);
                } else {
                    animatingImageViews[i].setAlpha(1.0f);
                }
                animatingImageViews[i].setPivotX(0.0f);
                animatingImageViews[i].setPivotY(0.0f);
                animatingImageViews[i].setScaleX(object.scale);
                animatingImageViews[i].setScaleY(object.scale);
                animatingImageViews[i].setTranslationX(object.viewX + drawRegion.left * object.scale);
                animatingImageViews[i].setTranslationY(object.viewY + drawRegion.top * object.scale);
                animatingImageViews[i].setLayoutParams(layoutParams);
            }

            windowView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (animatingImageViews.length > 1) {
                        animatingImageViews[1].setAlpha(1.0f);
                        animatingImageViews[1].setAdditionalTranslationX(-getLeftInset());
                    }
                    animatingImageViews[0].setTranslationX(animatingImageViews[0].getTranslationX() + getLeftInset());
                    windowView.getViewTreeObserver().removeOnPreDrawListener(this);
                    float scaleX;
                    float scaleY;
                    float scale;
                    float yPos;
                    float xPos;
                    if (sendPhotoType == SELECT_TYPE_AVATAR) {
                        float statusBarHeight = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                        float measuredHeight = (float) photoCropView.getMeasuredHeight() - dp(64) - statusBarHeight;
                        float minSide = Math.min(photoCropView.getMeasuredWidth(), measuredHeight) - 2 * dp(16);
                        float centerX = photoCropView.getMeasuredWidth() / 2.0f;
                        float centerY = statusBarHeight + measuredHeight / 2.0f;

                        float left = centerX - (minSide / 2.0f);
                        float top = centerY - (minSide / 2.0f);
                        float right = centerX + (minSide / 2.0f);
                        float bottom = centerY + (minSide / 2.0f);

                        scaleX = (right - left) / layoutParams.width;
                        scaleY = (bottom - top) / layoutParams.height;
                        scale = Math.max(scaleX, scaleY);
                        yPos = top + (bottom - top - layoutParams.height * scale) / 2;
                        xPos = (windowView.getMeasuredWidth() - getLeftInset() - getRightInset() - layoutParams.width * scale) / 2.0f + getLeftInset();
                    } else {
                        scaleX = (float) (windowView.getMeasuredWidth()) / layoutParams.width;
                        scaleY = (float) (AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) / layoutParams.height;
                        scale = Math.min(scaleX, scaleY);
                        if (sendPhotoType == SELECT_TYPE_STICKER) {
                            scale *= scale1();
                        }
                        yPos = ((AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - (layoutParams.height * scale)) / 2.0f;
                        xPos = (windowView.getMeasuredWidth() - layoutParams.width * scale) / 2.0f;
                        rotate = 0;
                        animateToRotate = 0;
                    }
                    int clipHorizontal = (int) Math.abs(left - object.imageReceiver.getImageX());
                    int clipVertical = (int) Math.abs(top - object.imageReceiver.getImageY());

                    if (object.imageReceiver.isAspectFit()) {
                        clipHorizontal = 0;
                    }

                    int[] coords2 = new int[2];
                    object.parentView.getLocationInWindow(coords2);
                    int clipTop = (int) (coords2[1] - 0 - (object.viewY + top) + object.clipTopAddition);
                    if (clipTop < 0) {
                        clipTop = 0;
                    }
                    int clipBottom = (int) (object.viewY + top + layoutParams.height - (coords2[1] + object.parentView.getHeight() - 0) + object.clipBottomAddition);
                    if (clipBottom < 0) {
                        clipBottom = 0;
                    }
                    clipTop = Math.max(clipTop, clipVertical);
                    clipBottom = Math.max(clipBottom, clipVertical);

                    animationValues[0][0] = animatingImageView.getScaleX();
                    animationValues[0][1] = animatingImageView.getScaleY();
                    animationValues[0][2] = animatingImageView.getTranslationX();
                    animationValues[0][3] = animatingImageView.getTranslationY();
                    animationValues[0][4] = clipHorizontal * object.scale;
                    animationValues[0][5] = clipTop * object.scale;
                    animationValues[0][6] = clipBottom * object.scale;
                    int[] rad = animatingImageView.getRadius();
                    for (int a = 0; a < 4; a++) {
                        animationValues[0][7 + a] = rad != null ? rad[a] : 0;
                    }
                    animationValues[0][11] = clipVertical * object.scale;
                    animationValues[0][12] = clipHorizontal * object.scale;

                    animationValues[1][0] = scale;
                    animationValues[1][1] = scale;
                    animationValues[1][2] = xPos;
                    animationValues[1][3] = yPos;
                    animationValues[1][4] = 0;
                    animationValues[1][5] = 0;
                    animationValues[1][6] = 0;
                    animationValues[1][7] = 0;
                    animationValues[1][8] = 0;
                    animationValues[1][9] = 0;
                    animationValues[1][10] = 0;
                    animationValues[1][11] = 0;
                    animationValues[1][12] = 0;

                    for (int i = 0; i < animatingImageViews.length; i++) {
                        animatingImageViews[i].setAnimationProgress(0);
                    }
                    backgroundDrawable.setAlpha(0);
                    containerView.setAlpha(0);
                    navigationBar.setAlpha(0);

                    if (provider != null) {
                        provider.onPreOpen();
                    }
                    animationEndRunnable = () -> {
                        animationEndRunnable = null;
                        if (containerView == null || windowView == null) {
                            return;
                        }
                        containerView.setLayerType(View.LAYER_TYPE_NONE, null);
                        animationInProgress = 0;
                        invalidateBlur();
                        transitionAnimationStartTime = 0;
                        leftCropState = null;
                        leftCropTransform.setViewTransform(false);
                        rightCropState = null;
                        rightCropTransform.setViewTransform(false);
                        setImages();
                        setCropBitmap();
                        containerView.invalidate();
                        for (int i = 0; i < animatingImageViews.length; i++) {
                            animatingImageViews[i].setVisibility(View.GONE);
                        }
                        if (showAfterAnimation != null) {
                            showAfterAnimation.imageReceiver.setVisible(true, true);
                        }
                        if (hideAfterAnimation != null && !hideAfterAnimation.keepImageReceiverVisible) {
                            hideAfterAnimation.imageReceiver.setVisible(false, true);
                        }
                        if (photos != null && sendPhotoType != 3 && sendPhotoType != SELECT_TYPE_AVATAR) {
                            if (placeProvider == null || !placeProvider.closeKeyboard()) {
                                makeFocusable();
                            }
                        }
                        if (videoPlayer != null && videoPlayer.isPlaying() && isCurrentVideo && !imagesArrLocals.isEmpty()) {
                            seekAnimatedStickersTo(videoPlayer.getCurrentPosition());
                            playOrStopAnimatedStickers(true);
                        }
                        if (isEmbedVideo) {
                            initEmbedVideo(embedSeekTime);
                        }

                        if (provider != null) {
                            provider.onOpen();
                        }
                    };

                    if (!openedFullScreenVideo) {
                        final AnimatorSet animatorSet = new AnimatorSet();
                        ArrayList<Animator> animators = new ArrayList<>((sendPhotoType == SELECT_TYPE_AVATAR ? 3 : 2) + animatingImageViews.length + (animatingImageViews.length > 1 ? 1 : 0));
                        for (int i = 0; i < animatingImageViews.length; i++) {
                            ObjectAnimator animator = ObjectAnimator.ofFloat(animatingImageViews[i], AnimationProperties.CLIPPING_IMAGE_VIEW_PROGRESS, 0.0f, 1.0f);
                            if (i == 0) {
                                animator.addUpdateListener(animation -> {
                                    clippingImageProgress = 1f - (float) animation.getAnimatedValue();
                                    invalidateBlur();
                                });
                            }
                            animators.add(animator);
                        }
                        if (animatingImageViews.length > 1) {
                            animators.add(ObjectAnimator.ofFloat(animatingImageView, View.ALPHA, 0f, 1f));
                        }
                        animators.add(ObjectAnimator.ofInt(backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 0, 255));
                        animators.add(ObjectAnimator.ofFloat(containerView, View.ALPHA, 0.0f, 1.0f));
                        animators.add(ObjectAnimator.ofFloat(navigationBar, View.ALPHA, 0.0f, 1.0f));
                        if (sendPhotoType == SELECT_TYPE_AVATAR) {
                            animators.add(ObjectAnimator.ofFloat(photoCropView, View.ALPHA, 0, 1.0f));
                        }
                        animatorSet.playTogether(animators);
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                AndroidUtilities.runOnUIThread(() -> {
                                    transitionNotificationLocker.unlock();
                                    if (animationEndRunnable != null) {
                                        animationEndRunnable.run();
                                        animationEndRunnable = null;
                                    }
                                    setCaptionHwLayerEnabled(true);
                                });
                            }
                        });

                        containerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                        setCaptionHwLayerEnabled(false);
                        transitionAnimationStartTime = System.currentTimeMillis();
                        AndroidUtilities.runOnUIThread(() -> {
                            transitionNotificationLocker.lock();
                            animatorSet.start();
                        });
                    } else {
                        if (animationEndRunnable != null) {
                            animationEndRunnable.run();
                            animationEndRunnable = null;
                        }
                        containerView.setAlpha(1.0f);
                        backgroundDrawable.setAlpha(255);
                        for (int i = 0; i < animatingImageViews.length; i++) {
                            animatingImageViews[i].setAnimationProgress(1.0f);
                        }
                        if (sendPhotoType == SELECT_TYPE_AVATAR) {
                            photoCropView.setAlpha(1.0f);
                        }
                    }
                    backgroundDrawable.drawRunnable = () -> {
                        disableShowCheck = false;
                        if (!object.keepImageReceiverVisible) {
                            object.imageReceiver.setVisible(false, true);
                        }
                    };
                    if (parentChatActivity != null && parentChatActivity.getFragmentView() != null) {
                        UndoView undoView = parentChatActivity.getUndoView();
                        if (undoView != null) {
                            undoView.hide(false, 1);
                        }
                        parentChatActivity.getFragmentView().invalidate();
                    }
                    return true;
                }
            });
        } else {
            if (photos != null && sendPhotoType != 3) {
                if (placeProvider == null || !placeProvider.closeKeyboard()) {
                    makeFocusable();
                }
            }

            //backgroundDrawable.setAlpha(255);
            containerView.setAlpha(1.0f);
            onPhotoShow(messageObject, fileLocation, imageLocation, videoLocation, messages, documents, photos, index, object);
            initCropView();
            setCropBitmap();
            if (parentChatActivity != null) {
                UndoView undoView = parentChatActivity.getUndoView();
                if (undoView != null) {
                    undoView.hide(false, 1);
                }
                parentChatActivity.getFragmentView().invalidate();
            }
            windowView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    windowView.getViewTreeObserver().removeOnPreDrawListener(this);
                    actionBar.setTranslationY(-dp(32));
                    actionBar.animate().alpha(1).translationY(0).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();

                    checkImageView.setTranslationY(-dp(32));
                    checkImageView.animate().alpha(1).translationY(0).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();

                    photosCounterView.setTranslationY(-dp(32));
                    photosCounterView.animate().alpha(1).translationY(0).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();

                    pickerView.setTranslationY(dp(32));
                    pickerView.animate().alpha(1).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    pickerViewSendButton.setTranslationY(dp(32));
                    pickerViewSendButton.setAlpha(0f);
                    pickerViewSendButton.animate().alpha(1).translationY(0).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();

                    videoPreviewFrame.setTranslationY(dp(32));
                    videoPreviewFrame.animate().alpha(1).translationY(0).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();

                    containerView.setAlpha(0);
                    backgroundDrawable.setAlpha(0);

                    animationInProgress = 4;
                    containerView.invalidate();
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator a2 = ObjectAnimator.ofFloat(pickerView, View.TRANSLATION_Y, pickerView.getTranslationY(), 0f).setDuration(220);
                    a2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ObjectAnimator a3 = ObjectAnimator.ofFloat(pickerView, View.ALPHA, 1f).setDuration(220);
                    a3.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(containerView, View.ALPHA, 0f, 1f).setDuration(220),
                            ObjectAnimator.ofFloat(navigationBar, View.ALPHA, 0f, 1f).setDuration(220),
                            a2, a3
                    );
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            if (provider != null) {
                                provider.onPreOpen();
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animationInProgress = 0;
                            invalidateBlur();
                            backgroundDrawable.setAlpha(255);
                            containerView.invalidate();
                            pickerView.setTranslationY(0f);
                            if (isEmbedVideo) {
                                initEmbedVideo(embedSeekTime);
                            }

                            if (provider != null) {
                                provider.onOpen();
                            }
                        }
                    });
                    animatorSet.start();
                    return true;
                }
            });

        }

        AccessibilityManager am = (AccessibilityManager) parentActivity.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am.isTouchExplorationEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
            event.getText().add(getString("AccDescrPhotoViewer", R.string.AccDescrPhotoViewer));
            am.sendAccessibilityEvent(event);
        }

        return true;
    }

    public void openKeyboard() {
        final CaptionContainerView captionContainerView = getCaptionView();
        if (captionContainerView != null && captionContainerView.editText != null) {
            captionContainerView.editText.openKeyboard();
        }
    }

    private void initEmbedVideo(int embedSeekTime) {
        if (!isEmbedVideo) {
            return;
        }
        photoViewerWebView = new PhotoViewerWebView(this, parentActivity, pipItem) {

            Rect rect = new Rect();

            @Override
            protected void drawBlackBackground(Canvas canvas, int w, int h) {
                Bitmap bitmap = centerImage.getBitmap();
                if (bitmap != null) {
                    float minScale = Math.min(w / (float) bitmap.getWidth(), h / (float) bitmap.getHeight());
                    int width = (int) (bitmap.getWidth() * minScale);
                    int height = (int) (bitmap.getHeight() * minScale);
                    int top = (h - height) / 2;
                    int left = (w - width) / 2;
                    rect.set(left, top, left + width, top + height);
                    canvas.drawBitmap(bitmap, null, rect, null);
                }
            }

            @Override
            protected void processTouch(MotionEvent event) {
              //  gestureDetector.onTouchEvent(event);
            }
        };
        photoViewerWebView.init(embedSeekTime, MessageObject.getMedia(currentMessageObject.messageOwner).webpage);
        photoViewerWebView.setPlaybackSpeed(currentVideoSpeed);
        containerView.addView(photoViewerWebView, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        if (photoViewerWebView.isControllable()) {
            setVideoPlayerControlVisible(true, true);
        }
        videoPlayerSeekbar.clearTimestamps();
        updateVideoPlayerTime();

        shouldSavePositionForCurrentVideo = null;
        shouldSavePositionForCurrentVideoShortTerm = null;
        lastSaveTime = 0;
        seekToProgressPending = seekToProgressPending2;
        videoPlayerSeekbar.setProgress(0);
        videoTimelineView.setProgress(0);
        videoPlayerSeekbar.setBufferedProgress(0);
    }

    private void makeFocusable() {
        windowLayoutParams.flags =
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        windowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
        WindowManager wm1 = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
        try {
            wm1.updateViewLayout(windowView, windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
        windowView.setFocusable(true);
        containerView.setFocusable(true);
    }

    private void requestAdjustToNothing() {
        windowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        WindowManager wm1 = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
        try {
            wm1.updateViewLayout(windowView, windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void requestAdjust() {
        windowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
        WindowManager wm1 = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
        try {
            wm1.updateViewLayout(windowView, windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void injectVideoPlayerToMediaController() {
        if (videoPlayer.isPlaying()) {
            if (playerLooping) {
                videoPlayer.setLooping(false);
            }
            MediaController.getInstance().injectVideoPlayer(videoPlayer, currentMessageObject);
            videoPlayer = null;
        }
    }

    public void closePhoto(boolean animated, boolean fromEditMode) {
        if (stickerMakerView != null) {
            stickerMakerView.isThanosInProgress = false;
            if (cutOutBtn.isCancelState()) {
                cutOutBtn.setCutOutState(true);
                showEditStickerMode(false, true);
                stickerMakerView.disableClippingMode();
                containerView.invalidate();
            }
        }
        if (!fromEditMode && currentEditMode != EDIT_MODE_NONE) {
            if (currentEditMode == EDIT_MODE_PAINT && photoPaintView != null) {
                closePaintMode();
                return;
            }
            if (currentEditMode == EDIT_MODE_CROP) {
                cropTransform.setViewTransform(previousHasTransform, previousCropPx, previousCropPy, previousCropRotation, previousCropOrientation, previousCropScale, 1.0f, 1.0f, previousCropPw, previousCropPh, 0, 0, previousCropMirrored);
            }
            if (currentEditMode == EDIT_MODE_STICKER_MASK) {
                applyCurrentEditMode();
            }
            switchToEditMode(EDIT_MODE_NONE);
            return;
        }
        if (qualityChooseView != null && qualityChooseView.getTag() != null) {
            qualityPicker.cancelButton.callOnClick();
            return;
        }
        setWindowHdrColorMode(false);
        isVisibleOrAnimating = false;
        openedFullScreenVideo = false;
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (containerView != null) {
            AndroidUtilities.cancelRunOnUIThread(updateContainerFlagsRunnable);
            updateContainerFlags(true);
        }
        if (currentEditMode != EDIT_MODE_NONE) {
            if (currentEditMode == EDIT_MODE_FILTER) {
                photoFilterView.shutdown();
                containerView.removeView(photoFilterView);
                photoFilterView = null;
            } else if (currentEditMode == EDIT_MODE_CROP) {
                editorDoneLayout.setVisibility(View.GONE);
                photoCropView.setVisibility(View.GONE);
            } else if (currentEditMode == EDIT_MODE_PAINT) {
                photoPaintView.shutdown();
                containerView.removeView(photoPaintView.getView());
                photoPaintView = null;
                savedState = null;
            } else if (currentEditMode == EDIT_MODE_STICKER_MASK) {
                maskPaintViewShuttingDown = true;
                if (containerView != null) {
                    containerView.invalidate();
                    final MaskPaintView _maskPaintView = maskPaintView;
                    containerView.post(() -> {
                        _maskPaintView.shutdown();
                        containerView.removeView(maskPaintView);
                    });
                } else {
                    maskPaintView.shutdown();
                }
                maskPaintView = null;
            }
            currentEditMode = EDIT_MODE_NONE;
            getCaptionView().keyboardNotifier.ignore(false);
            if (paintKeyboardNotifier != null) {
                paintKeyboardNotifier.ignore(currentEditMode != EDIT_MODE_PAINT);
            }
        }

        if (navigationBar != null) {
            navigationBar.setVisibility(View.VISIBLE);
        }
        if (windowView != null) {
            windowView.setClipChildren(false);
        }

        if (parentActivity == null || !isInline && !isVisible || checkAnimation() || placeProvider == null) {
            return;
        }
//        if (captionEditText.hideActionMode() && !fromEditMode) {
//            return;
//        }
        if (parentActivity != null && fullscreenedByButton != 0) {
            parentActivity.setRequestedOrientation(prevOrientation);
            fullscreenedByButton = 0;
            wasRotated = false;
        }
        if (!doneButtonPressed && !imagesArrLocals.isEmpty() && currentIndex >= 0 && currentIndex < imagesArrLocals.size()) {
            Object entry = imagesArrLocals.get(currentIndex);
            if (entry instanceof MediaController.MediaEditState) {
                ((MediaController.MediaEditState) entry).editedInfo = getCurrentVideoEditedInfo();
                if (sendPhotoType == SELECT_TYPE_STICKER) {
                    ((MediaController.MediaEditState) entry).reset();
                }
            }
        }
        float wasScaleX = 1f, wasScaleY = 1f, wasScaleAlertX = 1f, wasScaleAlertY = 1f;
        if (parentFragment != null && parentFragment.getFragmentView() != null) {
            View view = parentFragment.getFragmentView();
            wasScaleX = view.getScaleX();
            wasScaleY = view.getScaleY();
            view.setScaleX(1f);
            view.setScaleY(1f);

            if (parentAlert != null) {
                view = parentAlert.getContainer();
                wasScaleAlertX = view.getScaleX();
                wasScaleAlertY = view.getScaleY();
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        }
        if (placeProvider != null) {
            placeProvider.onReleasePlayerBeforeClose(currentIndex);
        }
        final boolean[] allowStart = new boolean[] { true };
        final Runnable[] start = new Runnable[1];
        final PlaceProviderObject object = placeProvider == null ? null : placeProvider.getPlaceForPhoto(currentMessageObject, getFileLocation(currentFileLocation), currentIndex, true, true);
        if (videoPlayer != null && object != null) {
            AnimatedFileDrawable animation = object.imageReceiver.getAnimation();
            if (animation != null) {
                final long startTime = animation.getStartTime();
                final long seekTo = videoPlayer.getCurrentPosition() + (startTime > 0 ? startTime : 0);
                final TLRPC.Document document = videoPlayer.getCurrentDocument() == null ? (currentMessageObject != null ? currentMessageObject.getDocument() : null) : videoPlayer.getCurrentDocument();
                boolean doSeek = true;
                final Runnable seek = () -> {
                    if (animation != null && document != null) {
                        FileLog.d("seeking from photo viewer to animation object");
                        animation.seekTo(seekTo, !FileLoader.getInstance(currentAccount).isLoadingVideo(document, true), true);
                    }
                    if (object != null && object.imageReceiver != null) {
                        object.imageReceiver.setAllowStartAnimation(true);
                        object.imageReceiver.startAnimation();
                    }
                };
                if (textureUploaded) {
                    Bitmap bitmap = animation.getAnimatedBitmap();
                    if (bitmap != null) {
                        if (usedSurfaceView) {
                            AndroidUtilities.getBitmapFromSurface(videoSurfaceView, bitmap);
//                            doSeek = false;
//                            object.imageReceiver.stopAnimation();
//                            Bitmap toBitmap = Bitmap.createBitmap(bitmap);
//                            final boolean[] started = new boolean[1];
//                            allowStart[0] = false;
//                            final long startTime2 = System.currentTimeMillis();
//                            AndroidUtilities.getBitmapFromSurface(videoSurfaceView, toBitmap, () -> {
//                                if (animation != null) {
//                                    animation.replaceAnimatedBitmap(toBitmap);
//                                    animation.invalidateInternal();
//                                    containerView.invalidate();
//                                }
//                                if (!started[0]) {
//                                    seek.run();
//                                    started[0] = true;
//                                    if (start[0] != null) {
//                                        start[0].run();
//                                    } else {
//                                        allowStart[0] = true;
//                                    }
//                                }
//                            });
//                            AndroidUtilities.runOnUIThread(() -> {
//                                if (!started[0]) {
//                                    seek.run();
//                                    started[0] = true;
//                                    if (start[0] != null) {
//                                        start[0].run();
//                                    } else {
//                                        allowStart[0] = true;
//                                    }
//                                }
//                            }, 600);
                        } else {
                            try {
                                Bitmap src = videoTextureView.getBitmap(bitmap.getWidth(), bitmap.getHeight());
                                Canvas canvas = new Canvas(bitmap);
                                canvas.drawBitmap(src, 0, 0, null);
                                src.recycle();
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    }
                }
                if (doSeek) {
                    seek.run();
                }
            }
        }
        if (photoViewerWebView != null) {
            photoViewerWebView.release();
            containerView.removeView(photoViewerWebView);
            photoViewerWebView = null;
        }
//        captionEdit.editText.onDestroy();
        if (parentChatActivity != null && parentChatActivity.getFragmentView() != null) {
            parentChatActivity.getFragmentView().invalidate();
        }
        parentChatActivity = null;
        removeObservers();

        isActionBarVisible = false;

        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }

        if (isInline) {
            isInline = false;
            animationInProgress = 0;
            onPhotoClosed(object);
            containerView.setScaleX(1.0f);
            containerView.setScaleY(1.0f);
            if (!doneButtonPressed) {
                releasePlayer(true);
            }
        } else {
            if (animated) {
                final ClippingImageView[] animatingImageViews = getAnimatingImageViews(object);

                for (int i = 0; i < animatingImageViews.length; i++) {
                    animatingImageViews[i].setAnimationValues(animationValues, false, object == null ? false : object.fadeIn);
                    animatingImageViews[i].setVisibility(View.VISIBLE);
                }

                final AnimatorSet animatorSet = new AnimatorSet();

                final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
                RectF drawRegion = null;
                if (object != null) {
                    drawRegion = object.imageReceiver.getDrawRegion();
                    layoutParams.width = (int) drawRegion.width();
                    layoutParams.height = (int) drawRegion.height();
                    int orientation = object.imageReceiver.getOrientation();
                    int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                    if (animatedOrientation != 0) {
                        orientation = animatedOrientation;
                    }
                    for (int i = 0; i < animatingImageViews.length; i++) {
                        animatingImageViews[i].setOrientation(orientation, object.imageReceiver.getInvert());
                        animatingImageViews[i].setImageBitmap(object.thumb);
                    }
                } else {
                    layoutParams.width = (int) centerImage.getImageWidth();
                    layoutParams.height = (int) centerImage.getImageHeight();
                    for (int i = 0; i < animatingImageViews.length; i++) {
                        animatingImageViews[i].setOrientation(centerImage.getOrientation(), centerImage.getInvert());
                        animatingImageViews[i].setImageBitmap(centerImage.getBitmapSafe());
                    }
                }
                if (layoutParams.width <= 0) {
                    layoutParams.width = 100;
                }
                if (layoutParams.height <= 0) {
                    layoutParams.height = 100;
                }

                float scaleX;
                float scaleY;
                float scale2;
                if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    float statusBarHeight = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                    float measuredHeight = (float) photoCropView.getMeasuredHeight() - dp(64) - statusBarHeight;
                    float minSide = Math.min(photoCropView.getMeasuredWidth(), measuredHeight) - 2 * dp(16);
                    scaleX = minSide / layoutParams.width;
                    scaleY = minSide / layoutParams.height;
                    scale2 = Math.max(scaleX, scaleY);
                } else {
                    scaleX = (float) windowView.getMeasuredWidth() / layoutParams.width;
                    scaleY = (float) (AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) / layoutParams.height;
                    scale2 = Math.min(scaleX, scaleY);
                }
                float width = layoutParams.width * scale * scale2;
                float height = layoutParams.height * scale * scale2;
                float xPos = (windowView.getMeasuredWidth() - width) / 2.0f;
                float yPos;
                if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    float statusBarHeight = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                    float measuredHeight = (float) photoCropView.getMeasuredHeight() - statusBarHeight;
                    yPos = (measuredHeight - height) / 2.0f;
                } else {
                    yPos = ((AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - height) / 2.0f;
                }
                for (int i = 0; i < animatingImageViews.length; i++) {
                    animatingImageViews[i].setLayoutParams(layoutParams);
                    animatingImageViews[i].setTranslationX(xPos + translationX);
                    animatingImageViews[i].setTranslationY(yPos + translationY);
                    animatingImageViews[i].setScaleX(scale * scale2);
                    animatingImageViews[i].setScaleY(scale * scale2);
                }

                if (object != null) {
                    int clipHorizontal = (int) Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                    int clipVertical = (int) Math.abs(drawRegion.top - object.imageReceiver.getImageY());

                    if (object.imageReceiver.isAspectFit()) {
                        clipHorizontal = 0;
                    }

                    int[] coords2 = new int[2];
                    object.parentView.getLocationInWindow(coords2);
                    int clipTop = (int) (coords2[1] - 0 - (object.viewY + drawRegion.top) + object.clipTopAddition);
                    if (clipTop < 0) {
                        clipTop = 0;
                    }
                    int clipBottom = (int) (object.viewY + drawRegion.top + (drawRegion.bottom - drawRegion.top) - (coords2[1] + object.parentView.getHeight() - 0) + object.clipBottomAddition);
                    if (clipBottom < 0) {
                        clipBottom = 0;
                    }

                    clipTop = Math.max(clipTop, clipVertical);
                    clipBottom = Math.max(clipBottom, clipVertical);

                    animationValues[0][0] = animatingImageView.getScaleX();
                    animationValues[0][1] = animatingImageView.getScaleY();
                    animationValues[0][2] = animatingImageView.getTranslationX();
                    animationValues[0][3] = animatingImageView.getTranslationY();
                    animationValues[0][4] = 0;
                    animationValues[0][5] = 0;
                    animationValues[0][6] = 0;
                    animationValues[0][7] = 0;
                    animationValues[0][8] = 0;
                    animationValues[0][9] = 0;
                    animationValues[0][10] = 0;
                    animationValues[0][11] = 0;
                    animationValues[0][12] = 0;

                    animationValues[1][0] = object.scale;
                    animationValues[1][1] = object.scale;
                    animationValues[1][2] = object.viewX + drawRegion.left * object.scale;
                    animationValues[1][3] = object.viewY + drawRegion.top * object.scale;
                    animationValues[1][4] = clipHorizontal * object.scale;
                    animationValues[1][5] = clipTop * object.scale;
                    animationValues[1][6] = clipBottom * object.scale;
                    for (int a = 0; a < 4; a++) {
                        animationValues[1][7 + a] = object.radius != null ? object.radius[a] : 0;
                    }
                    animationValues[1][11] = clipVertical * object.scale;
                    animationValues[1][12] = clipHorizontal * object.scale;

                    ArrayList<Animator> animators = new ArrayList<>((sendPhotoType == SELECT_TYPE_AVATAR ? 3 : 2) + animatingImageViews.length + (animatingImageViews.length > 1 ? 1 : 0));
                    for (int i = 0; i < animatingImageViews.length; i++) {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(animatingImageViews[i], AnimationProperties.CLIPPING_IMAGE_VIEW_PROGRESS, 0.0f, 1.0f);
                        if (i == 0) {
                            animator.addUpdateListener(animation -> {
                                clippingImageProgress = (float) animation.getAnimatedValue();
                                invalidateBlur();
                            });
                        }
                        animators.add(animator);
                    }
                    if (animatingImageViews.length > 1) {
                        animators.add(ObjectAnimator.ofFloat(animatingImageView, View.ALPHA, 0f));
                        animatingImageViews[1].setAdditionalTranslationX(-getLeftInset());
                    }
                    animators.add(ObjectAnimator.ofInt(backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 0));
                    animators.add(ObjectAnimator.ofFloat(containerView, View.ALPHA, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(navigationBar, View.ALPHA, 0.0f));
                    if (sendPhotoType == SELECT_TYPE_AVATAR) {
                        animators.add(ObjectAnimator.ofFloat(photoCropView, View.ALPHA, 0.0f));
                    }
                    animatorSet.playTogether(animators);
                } else {
                    int h = (AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0));
                    ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);
                    progressAnimator.addUpdateListener(animation -> clippingImageProgress = (float) animation.getAnimatedValue());
                    animatorSet.playTogether(
                            progressAnimator,
                            ObjectAnimator.ofInt(backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 0),
                            ObjectAnimator.ofFloat(animatingImageView, View.ALPHA, 0.0f),
                            ObjectAnimator.ofFloat(animatingImageView, View.TRANSLATION_Y, translationY >= 0 ? h : -h),
                            ObjectAnimator.ofFloat(containerView, View.ALPHA, 0.0f),
                            ObjectAnimator.ofFloat(navigationBar, View.ALPHA, 0.0f)
                    );
                }

                if (placeProvider != null) {
                    placeProvider.onPreClose();
                }
                animationEndRunnable = () -> {
                    animationEndRunnable = null;
                    containerView.setLayerType(View.LAYER_TYPE_NONE, null);
                    animationInProgress = 0;
                    invalidateBlur();
                    onPhotoClosed(object);
                    MediaController.getInstance().tryResumePausedAudio();
                    if (stickerEmpty && !stickerEmptySent && imagesArrLocals != null) {
                        for (Object obj : imagesArrLocals) {
                            if (obj instanceof MediaController.PhotoEntry) {
                                MediaController.PhotoEntry entry = (MediaController.PhotoEntry) obj;
                                entry.deleteAll();
                            }
                        }
                    }
                };

                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        AndroidUtilities.runOnUIThread(() -> {
                            if (animationEndRunnable != null) {
                                animationEndRunnable.run();
                                animationEndRunnable = null;
                            }
                        });
                    }
                });
                start[0] = () -> {
                    if (object != null && !object.keepImageReceiverVisible) {
                        object.imageReceiver.setVisible(false, true);
                    }
                    if (!doneButtonPressed) {
                        releasePlayer(true);
                    }
                    animationInProgress = 3;
                    containerView.invalidate();
                    transitionAnimationStartTime = System.currentTimeMillis();
                    containerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    animatorSet.start();
                };
                if (allowStart[0]) {
                    start[0].run();
                }
            } else {
                AnimatorSet animatorSet = new AnimatorSet();
                ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);
                progressAnimator.addUpdateListener(animation -> clippingImageProgress = (float) animation.getAnimatedValue());
                animatorSet.playTogether(
                        progressAnimator,
                        ObjectAnimator.ofFloat(containerView, View.SCALE_X, 0.9f),
                        ObjectAnimator.ofFloat(containerView, View.SCALE_Y, 0.9f),
                        ObjectAnimator.ofInt(backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 0),
                        ObjectAnimator.ofFloat(containerView, View.ALPHA, 0.0f),
                        ObjectAnimator.ofFloat(navigationBar, View.ALPHA, 0.0f)
                );
                if (placeProvider != null) {
                    placeProvider.onPreClose();
                }
                animationEndRunnable = () -> {
                    animationEndRunnable = null;
                    if (containerView == null) {
                        return;
                    }
                    containerView.setLayerType(View.LAYER_TYPE_NONE, null);
                    animationInProgress = 0;
                    onPhotoClosed(object);
                    containerView.setScaleX(1.0f);
                    containerView.setScaleY(1.0f);
                    MediaController.getInstance().tryResumePausedAudio();
                    if (stickerEmpty && !stickerEmptySent && imagesArrLocals != null) {
                        for (Object obj : imagesArrLocals) {
                            if (obj instanceof MediaController.PhotoEntry) {
                                MediaController.PhotoEntry entry = (MediaController.PhotoEntry) obj;
                                entry.deleteAll();
                            }
                        }
                    }
                };
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        clippingImageProgress = 1;
                        if (animationEndRunnable != null) {
                            ChatActivity chatActivity = parentChatActivity;
                            if (chatActivity == null && parentAlert != null) {
                                BaseFragment baseFragment = parentAlert.getBaseFragment();
                                if (baseFragment instanceof ChatActivity) {
                                    chatActivity = (ChatActivity) baseFragment;
                                }
                            }
                            if (chatActivity != null) {
                                chatActivity.doOnIdle(animationEndRunnable);
                            } else {
                                animationEndRunnable.run();
                                animationEndRunnable = null;
                            }
                        }
                    }
                });
                start[0] = () -> {
                    if (!doneButtonPressed) {
                        releasePlayer(true);
                    }
                    animationInProgress = 2;
                    transitionAnimationStartTime = System.currentTimeMillis();
                    containerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    animatorSet.start();
                    if (object != null) {
                        object.imageReceiver.setVisible(true, true);
                    }
                };
                if (allowStart[0]) {
                    start[0].run();
                }
            }
            if (currentAnimation != null) {
                currentAnimation.removeSecondParentView(containerView);
                currentAnimation = null;
                centerImage.setImageBitmap((Drawable) null);
                centerBlur.destroy();
            }
            if (placeProvider != null && !placeProvider.canScrollAway()) {
                placeProvider.cancelButtonPressed();
            }
        }
        if (parentFragment != null && parentFragment.getFragmentView() != null) {
            View view = parentFragment.getFragmentView();
            view.setScaleX(wasScaleX);
            view.setScaleY(wasScaleY);

            if (parentAlert != null) {
                view = parentAlert.getContainer();
                view.setScaleX(wasScaleAlertX);
                view.setScaleY(wasScaleAlertY);
            }
        }
    }

    private ClippingImageView[] getAnimatingImageViews(PlaceProviderObject object) {
        final boolean hasSecondAnimatingImageView = !AndroidUtilities.isTablet() && object != null && object.animatingImageView != null;
        final ClippingImageView[] animatingImageViews = new ClippingImageView[1 + (hasSecondAnimatingImageView ? 1 : 0)];
        animatingImageViews[0] = animatingImageView;
        if (hasSecondAnimatingImageView) {
            animatingImageViews[1] = object.animatingImageView;
            object.animatingImageView.setAdditionalTranslationY(object.animatingImageViewYOffset);
        }
        return animatingImageViews;
    }

    private void removeObservers() {
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.customStickerCreated);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.dialogPhotosUpdate);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.dialogDeleted);
        ConnectionsManager.getInstance(currentAccount).cancelRequestsForGuid(classGuid);
    }

    public void destroyPhotoViewer() {
        if (parentActivity == null || windowView == null) {
            return;
        }
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss();
        }
        removeObservers();
        releasePlayer(false);
        try {
            if (windowView.getParent() != null) {
                WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(windowView);
                onHideView();
            }
            windowView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (currentThumb != null) {
            currentThumb.release();
            currentThumb = null;
        }
        animatingImageView.setImageBitmap(null);
//        if (captionEdit.editText != null) {
//            captionEdit.editText.onDestroy();
//        }
        if (this == PipInstance) {
            PipInstance = null;
        } else {
            Instance = null;
        }
        onHideView();
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        if (doneButtonPressed) {
            releasePlayer(true);
        }
        if (currentMessageObject != null && !currentMessageObject.putInDownloadsStore) {
            FileLoader.getInstance(currentAccount).cancelLoadFile(currentMessageObject.getDocument());
        }
        isVisible = false;
        isVisibleOrAnimating = false;
        cropInitied = false;
        disableShowCheck = true;
        currentMessageObject = null;
        currentBotInlineResult = null;
        currentFileLocation = null;
        currentFileLocationVideo = null;
        currentSecureDocument = null;
        currentPageBlock = null;
        currentPathObject = null;
        dialogPhotos = null;
        if (ads != null) {
            ads.stop();
            ads = null;
        }
        if (videoPlayerControlFrameLayout != null) {
            setVideoPlayerControlVisible(false, false);
        }
        if (captionScrollView != null) {
            captionScrollView.reset();
        }
        sendPhotoType = 0;
        sendPhotoTypeIsGif = false;
        sendPhotoTypeIsPollMedia = false;
        sendPhotoTypeIsPollMediaEdit = false;
        isDocumentsPicker = false;
        if (currentThumb != null) {
            currentThumb.release();
            currentThumb = null;
        }
        parentAlert = null;
        if (parentAlertWindowVisibilityController != null) {
            parentAlertWindowVisibilityController.destroy();
            parentAlertWindowVisibilityController = null;
        }
        if (currentAnimation != null) {
            currentAnimation.removeSecondParentView(containerView);
            currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            if (photoProgressViews[a] != null) {
                photoProgressViews[a].setBackgroundState(PROGRESS_NONE, false, true);
            }
        }
        requestVideoPreview(0);
        if (videoTimelineView != null) {
            videoTimelineView.destroy();
        }
        hintView.hide(false, 0);
        centerImage.setImageBitmap((Bitmap) null);
        centerBlur.destroy();
        leftImage.setImageBitmap((Bitmap) null);
        leftBlur.destroy();
        rightImage.setImageBitmap((Bitmap) null);
        rightBlur.destroy();
        containerView.post(() -> {
            animatingImageView.setImageBitmap(null);
            if (object != null && !AndroidUtilities.isTablet() && object.animatingImageView != null) {
                object.animatingImageView.setImageBitmap(null);
            }
            try {
                if (windowView.getParent() != null) {
                    WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
                    wm.removeView(windowView);
                    onHideView();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
        if (placeProvider != null) {
            placeProvider.willHidePhotoViewer();
        }
        groupedPhotosListView.clear();
        if (placeProvider != null) {
            placeProvider.onClose();
        }
        placeProvider = null;
        selectedPhotosAdapter.notifyDataSetChanged();
        pageBlocksAdapter = null;
        disableShowCheck = false;
        shownControlsByEnd = false;
        videoCutStart = 0;
        videoCutEnd = 1f;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
        if (parentChatActivity != null) {
            parentChatActivity.getFragmentView().invalidate();
        }
        if (videoFrameBitmap != null) {
            videoFrameBitmap.recycle();
            videoFrameBitmap = null;
        }
    }

    private void redraw(final int count) {
        if (count < 6) {
            if (containerView != null) {
                containerView.invalidate();
                AndroidUtilities.runOnUIThread(() -> redraw(count + 1), 100);
            }
        }
    }

    public void onResume() {
        redraw(0); //workaround for camera bug
        updateWindowHdrColorMode();
        if (videoPlayer != null) {
            videoPlayer.seekTo(videoPlayer.getCurrentPosition() + 1);
            if (playerLooping) {
                videoPlayer.setLooping(true);
            }
        }
        if (photoPaintView != null) {
            photoPaintView.onResume();
        }
        if (pausedOnPause && NekoConfig.autoPauseVideo.Bool() && videoPlayer != null && !videoPlayer.isPlaying()) {
            pausedOnPause = false;
            videoPlayer.play();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {}

    public void onPause() {
        setWindowHdrColorMode(false);
        if (currentAnimation != null) {
            closePhoto(false, false);
            return;
        }
        if (lastTitle != null) {
            closeCaptionEnter(true);
        }
        if (videoPlayer != null && playerLooping) {
            videoPlayer.setLooping(allowLoopingOnPause());
        }
        if (NekoConfig.autoPauseVideo.Bool() && videoPlayer != null && videoPlayer.isPlaying()) {
            pausedOnPause = true;
            videoPlayer.pause();
        }
    }

    private boolean allowLoopingOnPause() {
        return AndroidUtilities.isInPictureInPictureMode(parentActivity);
    }

    public boolean isVisible() {
        return isVisible && placeProvider != null;
    }

    private void updateMinMax(float scale) {
        if (aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == View.VISIBLE && textureUploaded) {
            View view = usedSurfaceView ? videoSurfaceView : videoTextureView;
            scale *= Math.min(getContainerViewWidth() / (float) view.getMeasuredWidth(), getContainerViewHeight() / (float) view.getMeasuredHeight());
        }
        float w = centerImage.getImageWidth();
        float h = centerImage.getImageHeight();
        if (editState.cropState != null) {
            w *= editState.cropState.cropPw;
            h *= editState.cropState.cropPh;
        }
        int maxW = sendPhotoType == SELECT_TYPE_STICKER ? (int) (scale * w) : (int) (w * scale - getContainerViewWidth()) / 2;
        int maxH = sendPhotoType == SELECT_TYPE_STICKER ? (int) (scale * h) : (int) (h * scale - getContainerViewHeight()) / 2;
        if (maxW > 0) {
            minX = -maxW;
            maxX = maxW;
        } else {
            minX = maxX = 0;
        }
        if (maxH > 0) {
            minY = -maxH;
            maxY = maxH;
        } else {
            minY = maxY = 0;
        }
        if (photoPaintView != null) {
            photoPaintView.updateZoom(scale <= 1.1f);
        }
    }

    private int getAdditionX(int mode) {
        if (mode == EDIT_MODE_CROP || mode == EDIT_MODE_NONE && sendPhotoType == SELECT_TYPE_AVATAR) {
            return dp(16);
        } else if (mode != EDIT_MODE_NONE && mode != EDIT_MODE_COVER && mode != EDIT_MODE_STICKER_MASK && mode != EDIT_MODE_PAINT) {
            return dp(14);
        }
        return 0;
    }

    private int getAdditionY(int mode) {
        if (mode < 0)
            return 0;
        if (mode == EDIT_MODE_CROP || mode == EDIT_MODE_NONE && sendPhotoType == SELECT_TYPE_AVATAR) {
            return dp(16) + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
        } else if (mode == EDIT_MODE_PAINT && photoPaintView != null) {
            return dp(8) + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + photoPaintView.getAdditionalTop();
        } else if (mode != EDIT_MODE_NONE && mode != EDIT_MODE_STICKER_MASK && mode != EDIT_MODE_COVER) {
            return dp(14) + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
        }
        return 0;
    }

    private int getContainerViewWidth() {
        return getContainerViewWidth(currentEditMode);
    }

    private int getContainerViewWidth(int mode) {
        int width = containerView.getWidth();
        if (mode == EDIT_MODE_CROP || mode == EDIT_MODE_NONE && sendPhotoType == SELECT_TYPE_AVATAR) {
            width -= dp(32);
        } else if (mode != EDIT_MODE_NONE && mode != EDIT_MODE_STICKER_MASK && mode != EDIT_MODE_COVER && mode != EDIT_MODE_PAINT) {
            width -= dp(28);
        }
        return width;
    }

    private int getContainerViewHeight() {
        return getContainerViewHeight(currentEditMode);
    }

    private int getContainerViewHeight(int mode) {
        return getContainerViewHeight(false, mode);
    }

    private int getContainerViewHeight(boolean trueHeight, int mode) {
        int height;
        if (trueHeight || inBubbleMode) {
            height = containerView.getMeasuredHeight();
        } else {
            height = AndroidUtilities.displaySize.y;
            height += AndroidUtilities.navigationBarHeight - insets.bottom;
            if ((mode == EDIT_MODE_NONE || mode == EDIT_MODE_STICKER_MASK || mode == EDIT_MODE_COVER) && sendPhotoType != SELECT_TYPE_AVATAR && isStatusBarVisible()) {
                height += AndroidUtilities.statusBarHeight;
            }
//            if (mode == EDIT_MODE_NONE && sendPhotoType == 2) {
//                height += AndroidUtilities.navigationBarHeight;
//            }
        }
        if (mode == EDIT_MODE_NONE && sendPhotoType == SELECT_TYPE_AVATAR || mode == EDIT_MODE_CROP) {
            height -= dp(48 + 32 + 64);
        } else if (mode == EDIT_MODE_FILTER) {
            height -= dp(154 + 60);
        } else if (mode == EDIT_MODE_PAINT) {
            height -= dp(48) + photoPaintView.getAdditionalBottom() + ActionBar.getCurrentActionBarHeight() + photoPaintView.getAdditionalTop();
        }
        return height;
    }

    float lastX;
    float longPressX;
    Runnable longPressRunnable = this::onLongPress;

    private boolean onTouchEvent(MotionEvent ev) {
        lastX = ev.getX();
        if (currentEditMode == EDIT_MODE_PAINT && animationStartTime != 0 && (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)) {
            if (ev.getPointerCount() >= 2) {
                cancelMoveZoomAnimation();
            } else {
                return true;
            }
        }
        if (animationInProgress != 0 || animationStartTime != 0) {
            return false;
        }

        if (longVideoPlayerRewinder.rewindCount > 0) {
            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                longVideoPlayerRewinder.cancelRewind();
                return false;
            }
            return true;
        } else if (videoPlayerRewinder.rewinding) {
            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                videoPlayerRewinder.cancelRewind();
                return false;
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                videoPlayerRewinder.setX(ev.getX());
                return true;
            }
        }

        if (currentEditMode == EDIT_MODE_FILTER) {
            photoFilterView.onTouch(ev);
            return true;
        } else if (currentEditMode == EDIT_MODE_CROP || currentEditMode != EDIT_MODE_PAINT && (sendPhotoType == SELECT_TYPE_AVATAR)) {
            return true;
        }

        if (getCaptionView().editText.isPopupShowing() || getCaptionView().keyboardShown && isVisible && animationInProgress == 0) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                closeCaptionEnter(true);
            }
            return true;
        }

        if (currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR && ev.getPointerCount() == 1 && gestureDetector.onTouchEvent(ev)) {
            if (doubleTap) {
                doubleTap = false;
                moving = false;
                zooming = false;
                checkMinMax(false);
                return true;
            }
        }

        if (tooltip != null) {
            tooltip.hide();
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            discardTap = false;
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }
            if (!draggingDown && !changingPage) {
                if (canZoom && ev.getPointerCount() == 2) {
                    if (paintViewTouched == 1) {
                        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                        photoPaintView.onTouch(event);
                        event.recycle();
                        paintViewTouched = 2;
                    } else if (maskPaintViewTouched == 1) {
                        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                        maskPaintView.onTouch(event);
                        event.recycle();
                        maskPaintViewTouched = 2;
                    }
                    pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                    pinchStartAngle = (float) Math.atan2(ev.getY(1) - ev.getY(0), ev.getX(1) - ev.getX(0));
                    pinchStartScale = scale;
                    pinchStartRotate = rotate;
                    pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                    pinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                    pinchStartX = translationX;
                    pinchStartY = translationY;
                    zooming = true;
                    moving = false;
                    if (currentEditMode == EDIT_MODE_PAINT || sendPhotoType == SELECT_TYPE_STICKER) {
                        moveStartX = pinchCenterX;
                        moveStartY = pinchCenterY;
                        draggingDown = false;
                        canDragDown = false;
                    }
                    hidePressedDrawables();
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                } else if (ev.getPointerCount() == 1) {
                    if (currentEditMode == EDIT_MODE_PAINT) {
                        if (paintViewTouched == 0) {
                            View v = photoPaintView.getView();
                            v.getHitRect(hitRect);
                            if (hitRect.contains((int) ev.getX(), (int) ev.getY())) {
                                MotionEvent event = MotionEvent.obtain(ev);
                                event.offsetLocation(-v.getX(), -v.getY());
                                photoPaintView.onTouch(event);
                                event.recycle();
                                paintViewTouched = 1;
                            }
                        }
                    } else if (currentEditMode == EDIT_MODE_STICKER_MASK) {
                        if (maskPaintViewTouched == 0) {
                            View v = maskPaintView;
                            v.getHitRect(hitRect);
                            if (hitRect.contains((int) ev.getX(), (int) ev.getY())) {
                                MotionEvent event = MotionEvent.obtain(ev);
                                event.offsetLocation(-v.getX(), -v.getY());
                                maskPaintView.onTouch(event);
                                event.recycle();
                                maskPaintViewTouched = 1;
                            }
                        }
                    } else {
                        moveStartX = ev.getX();
                        dragY = moveStartY = ev.getY();
                        draggingDown = false;
                        canDragDown = true;
                        if (velocityTracker != null) {
                            velocityTracker.clear();
                        }
                    }
                }
            }
            if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
                longPressX = ev.getX();
                AndroidUtilities.runOnUIThread(longPressRunnable, 300);
            } else {
                AndroidUtilities.cancelRunOnUIThread(longPressRunnable);
            }
        } else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(longPressX - lastX) > AndroidUtilities.touchSlop) {
                AndroidUtilities.cancelRunOnUIThread(longPressRunnable);
            }

            if (canZoom && ev.getPointerCount() == 2 && !draggingDown && zooming && !changingPage) {
                discardTap = true;
                if (currentEditMode == EDIT_MODE_PAINT || sendPhotoType == SELECT_TYPE_STICKER) {
                    float newPinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                    float newPinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                    float moveDx = moveStartX - newPinchCenterX;
                    float moveDy = moveStartY - newPinchCenterY;
                    moveStartX = newPinchCenterX;
                    moveStartY = newPinchCenterY;
                    if (translationX < minX || translationX > maxX) {
                        moveDx /= 3.0f;
                    }
                    if (translationY < minY || translationY > maxY) {
                        moveDy /= 3.0f;
                    }
                    pinchStartX = (pinchCenterX - getContainerViewWidth() / 2) - ((pinchCenterX - getContainerViewWidth() / 2) - translationX) / (scale / pinchStartScale) - moveDx;
                    pinchStartY = (pinchCenterY - getContainerViewHeight() / 2) - ((pinchCenterY - getContainerViewHeight() / 2) - translationY) / (scale / pinchStartScale) - moveDy;
                    pinchCenterX = newPinchCenterX;
                    pinchCenterY = newPinchCenterY;
                }
                if (sendPhotoType == SELECT_TYPE_STICKER && currentEditMode == EDIT_MODE_NONE) {
                    rotate = (float) ((Math.atan2(ev.getY(1) - ev.getY(0), ev.getX(1) - ev.getX(0)) - pinchStartAngle) / Math.PI * 180 + pinchStartRotate);
                }
                scale = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0)) / pinchStartDistance * pinchStartScale;
                translationX = (pinchCenterX - getContainerViewWidth() / 2) - ((pinchCenterX - getContainerViewWidth() / 2) - pinchStartX) * (scale / pinchStartScale);
                translationY = (pinchCenterY - getContainerViewHeight() / 2) - ((pinchCenterY - getContainerViewHeight() / 2) - pinchStartY) * (scale / pinchStartScale);
                updateMinMax(scale);
                invalidateBlur();
                containerView.invalidate();
            } else if (ev.getPointerCount() == 1) {
                if (paintViewTouched == 1 && photoPaintView != null) {
                    View v = photoPaintView.getView();
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.offsetLocation(-v.getX(), -v.getY());
                    photoPaintView.onTouch(event);
                    event.recycle();
                    return true;
                } else if (maskPaintViewTouched == 1 && maskPaintView != null) {
                    View v = maskPaintView;
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.offsetLocation(-v.getX(), -v.getY());
                    maskPaintView.onTouch(event);
                    event.recycle();
                    return true;
                }
                if (velocityTracker != null) {
                    velocityTracker.addMovement(ev);
                }
                float dx = Math.abs(ev.getX() - moveStartX);
                float dy = Math.abs(ev.getY() - dragY);
                if (dx > touchSlop || dy > touchSlop) {
                    discardTap = true;
                    hidePressedDrawables();
                    AndroidUtilities.cancelRunOnUIThread(longPressRunnable);
                    if (qualityChooseView != null && qualityChooseView.getVisibility() == View.VISIBLE) {
                        return true;
                    }
                }
                if (placeProvider.canScrollAway() && currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR && sendPhotoType != SELECT_TYPE_STICKER && canDragDown && !draggingDown && scale == 1 && dy >= dp(30) && dy / 2 > dx) {
                    draggingDown = true;
                    hidePressedDrawables();
                    moving = false;
                    dragY = ev.getY();
                    if (isActionBarVisible && containerView.getTag() != null) {
                        toggleActionBar(false, true);
                    } else if (pickerView.getVisibility() == View.VISIBLE) {
                        toggleActionBar(false, true);
                        togglePhotosListView(false, true);
                        toggleCheckImageView(false);
                    }
                    return true;
                } else if (draggingDown) {
                    translationY = ev.getY() - dragY;
                    containerView.invalidate();
                } else if (!invalidCoords && animationStartTime == 0) {
                    float moveDx = moveStartX - ev.getX();
                    float moveDy = moveStartY - ev.getY();
                    if (moving || currentEditMode != EDIT_MODE_NONE || sendPhotoType == SELECT_TYPE_STICKER || scale == 1 && Math.abs(moveDy) + dp(12) < Math.abs(moveDx) || scale != 1) {
                        if (!moving) {
                            moveDx = 0;
                            moveDy = 0;
                            moving = true;
                            canDragDown = false;
                            hidePressedDrawables();
                        }

                        moveStartX = ev.getX();
                        moveStartY = ev.getY();
                        updateMinMax(scale);
                        if (translationX < minX && (currentEditMode != EDIT_MODE_NONE || !rightImage.hasImageSet()) || translationX > maxX && (currentEditMode != EDIT_MODE_NONE || !leftImage.hasImageSet())) {
                            moveDx /= 3.0f;
                        }
                        if (maxY == 0 && minY == 0 && currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR && sendPhotoType != SELECT_TYPE_STICKER) {
                            if (translationY - moveDy < minY) {
                                translationY = minY;
                                moveDy = 0;
                            } else if (translationY - moveDy > maxY) {
                                translationY = maxY;
                                moveDy = 0;
                            }
                        } else {
                            if (translationY < minY || translationY > maxY) {
                                moveDy /= 3.0f;
                            }
                        }

                        translationX -= moveDx;
                        if (scale != 1 || currentEditMode != EDIT_MODE_NONE || sendPhotoType == SELECT_TYPE_STICKER) {
                            translationY -= moveDy;
                        }
                        invalidateBlur();
                        containerView.invalidate();
                    }
                } else {
                    invalidCoords = false;
                    moveStartX = ev.getX();
                    moveStartY = ev.getY();
                }
            }
        } else if (ev.getActionMasked() == MotionEvent.ACTION_CANCEL || ev.getActionMasked() == MotionEvent.ACTION_UP || ev.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            hidePressedDrawables();
            AndroidUtilities.cancelRunOnUIThread(longPressRunnable);
            if (paintViewTouched == 1) {
                if (photoPaintView != null) {
                    View v = photoPaintView.getView();
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.offsetLocation(-v.getX(), -v.getY());
                    photoPaintView.onTouch(event);
                    event.recycle();
                }
                maskPaintViewTouched = paintViewTouched = 0;
                return true;
            }
            if (maskPaintViewTouched == 1) {
                if (maskPaintView != null) {
                    View v = maskPaintView;
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.offsetLocation(-v.getX(), -v.getY());
                    maskPaintView.onTouch(event);
                    event.recycle();
                }
                maskPaintViewTouched = paintViewTouched = 0;
                return true;
            }
            paintViewTouched = 0;
            maskPaintViewTouched = 0;
            if (zooming) {
                invalidCoords = true;
                float maxScale = sendPhotoType == SELECT_TYPE_STICKER ? 10.0f : 3.0f;
                float minScale = sendPhotoType == SELECT_TYPE_STICKER ? 0.33f : 1f;
                if (scale < minScale) {
                    updateMinMax(minScale);
                    animateTo(minScale, 0, 0, true);
                } else if (scale > maxScale) {
                    float atx = (pinchCenterX - getContainerViewWidth() / 2) - ((pinchCenterX - getContainerViewWidth() / 2) - pinchStartX) * (maxScale / pinchStartScale);
                    float aty = (pinchCenterY - getContainerViewHeight() / 2) - ((pinchCenterY - getContainerViewHeight() / 2) - pinchStartY) * (maxScale / pinchStartScale);
                    updateMinMax(maxScale);
                    if (atx < minX) {
                        atx = minX;
                    } else if (atx > maxX) {
                        atx = maxX;
                    }
                    if (aty < minY) {
                        aty = minY;
                    } else if (aty > maxY) {
                        aty = maxY;
                    }
                    animateTo(maxScale, atx, aty, true);
                } else {
                    checkMinMax(true);
                    if (currentEditMode == EDIT_MODE_PAINT) {
                        float moveToX = translationX;
                        float moveToY = translationY;
                        updateMinMax(scale);
                        if (translationX < minX) {
                            moveToX = minX;
                        } else if (translationX > maxX) {
                            moveToX = maxX;
                        }
                        if (translationY < minY) {
                            moveToY = minY;
                        } else if (translationY > maxY) {
                            moveToY = maxY;
                        }
                        animateTo(scale, moveToX, moveToY, false);
                    }
                }
                zooming = false;
                moving = false;
            } else if (draggingDown) {
                if (Math.abs(dragY - ev.getY()) > getContainerViewHeight() / 6.0f) {
                    if (enableSwipeToPiP() && (dragY - ev.getY() > 0)) {
                        switchToPip(true);
                    } else {
                        closePhoto(true, false);
                    }
                } else {
                    if (pickerView.getVisibility() == View.VISIBLE) {
                        toggleActionBar(true, true);
                        toggleCheckImageView(true);
                    }
                    animateTo(1, 0, 0, false);
                }
                draggingDown = false;
            } else if (moving) {
                float moveToX = translationX;
                float moveToY = translationY;
                updateMinMax(scale);
                moving = false;
                canDragDown = true;
                float velocity = 0;
                if (velocityTracker != null && scale == 1) {
                    velocityTracker.computeCurrentVelocity(1000);
                    velocity = velocityTracker.getXVelocity();
                }

                if (currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR && sendPhotoType != SELECT_TYPE_STICKER) {
                    if ((translationX < minX - getContainerViewWidth() / 3 || velocity < -dp(650)) && rightImage.hasImageSet()) {
                        goToNext();
                        return true;
                    }
                    if ((translationX > maxX + getContainerViewWidth() / 3 || velocity > dp(650)) && leftImage.hasImageSet()) {
                        goToPrev();
                        return true;
                    }
                }

                if (translationX < minX) {
                    moveToX = minX;
                } else if (translationX > maxX) {
                    moveToX = maxX;
                }
                if (translationY < minY) {
                    moveToY = minY;
                } else if (translationY > maxY) {
                    moveToY = maxY;
                }
                animateTo(scale, moveToX, moveToY, false);
            }
        }
        return false;
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = translationX;
        float moveToY = translationY;
        updateMinMax(scale);
        if (translationX < minX) {
            moveToX = minX;
        } else if (translationX > maxX) {
            moveToX = maxX;
        }
        if (translationY < minY) {
            moveToY = minY;
        } else if (translationY > maxY) {
            moveToY = maxY;
        }
        animateTo(scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0;
        if (scale != 1) {
            extra = (getContainerViewWidth() - centerImage.getImageWidth()) / 2 * scale;
        }
        switchImageAfterAnimation = 1;
        animateTo(scale, minX - getContainerViewWidth() - extra - dp(30) / 2, translationY, false);
    }

    private void goToPrev() {
        float extra = 0;
        if (scale != 1) {
            extra = (getContainerViewWidth() - centerImage.getImageWidth()) / 2 * scale;
        }
        switchImageAfterAnimation = 2;
        animateTo(scale, maxX + getContainerViewWidth() + extra + dp(30) / 2, translationY, false);
    }

    private void cancelMoveZoomAnimation() {
        if (imageMoveAnimation == null) {
            return;
        }

        float ts = scale + (animateToScale - scale) * animationValue;
        float tx = translationX + (animateToX - translationX) * animationValue;
        float ty = translationY + (animateToY - translationY) * animationValue;
        float tr = rotate + (animateToRotate - rotate) * animationValue;
        imageMoveAnimation.cancel();
        scale = ts;
        translationX = tx;
        translationY = ty;
        animationStartTime = 0;
        rotate = tr;
        updateMinMax(scale);
        zoomAnimation = false;
        containerView.invalidate();
    }

    public void zoomOut() {
        animateTo(1f, 0, 0, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, 250);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (scale == newScale && translationX == newTx && translationY == newTy) {
            return;
        }
        zoomAnimation = isZoom;
        animateToScale = newScale;
        animateToX = newTx;
        animateToY = newTy;
        animationStartTime = System.currentTimeMillis();
        imageMoveAnimation = new AnimatorSet();
        imageMoveAnimation.playTogether(
                ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0, 1)
        );
        imageMoveAnimation.setInterpolator(interpolator);
        imageMoveAnimation.setDuration(duration);
        imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageMoveAnimation = null;
                containerView.invalidate();
            }
        });
        imageMoveAnimation.start();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public List<Object> getImagesArrLocals() {
        return imagesArrLocals;
    }

    @Keep
    public void setAnimationValue(float value) {
        animationValue = value;
        containerView.invalidate();
        invalidateBlur();
    }

    @Keep
    public float getAnimationValue() {
        return animationValue;
    }

    private void switchToNextIndex(int add, boolean init) {
        if (currentMessageObject != null) {
            releasePlayer(false);
            FileLoader.getInstance(currentAccount).cancelLoadFile(currentMessageObject.getDocument());
        } else if (currentPageBlock != null) {
            final TLObject media = pageBlocksAdapter.getMedia(currentIndex);
            if (media instanceof TLRPC.Document) {
                releasePlayer(false);
                FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.Document) media);
            }
        }
        if (groupedPhotosListView != null) {
            groupedPhotosListView.setAnimateBackground(true);
        }
        playerAutoStarted = false;
        setImageIndex(currentIndex + add, init, true);
        if (shouldMessageObjectAutoPlayed(currentMessageObject) || shouldIndexAutoPlayed(currentIndex)) {
            playerAutoStarted = true;
            onActionClick(true);
            checkProgress(0, false, true);
        }
        checkFullscreenButton();

        try {
            CastSync.check(CastSync.TYPE_PHOTOVIEWER);
            if (ChromecastController.getInstance().isCasting()) {
                ChromecastController.getInstance().setCurrentMediaAndCastIfNeeded(getCurrentChromecastMedia());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean shouldMessageObjectAutoPlayed(MessageObject messageObject) {
        return messageObject != null && messageObject.isVideo() && (messageObject.mediaExists || messageObject.attachPathExists || messageObject.hasVideoQualities() || messageObject.canStreamVideo() && SharedConfig.streamMedia) && SharedConfig.isAutoplayVideo();
    }

    private boolean shouldIndexAutoPlayed(int index) {
        if (pageBlocksAdapter != null) {
            if ((pageBlocksAdapter.isVideo(index) || pageBlocksAdapter.isHardwarePlayer(index)) && SharedConfig.isAutoplayVideo()) {
                final File mediaFile = pageBlocksAdapter.getFile(index);
                if (mediaFile != null && mediaFile.exists()) {
                    return true;
                }
                if (SharedConfig.streamMedia && pageBlocksAdapter.getMedia(index) instanceof TLRPC.Document) {
                    return true;
                }
            }
        }
        return false;
    }

    private float getCropFillScale(boolean rotated) {
        int width = rotated ? centerImage.getBitmapHeight() : centerImage.getBitmapWidth();
        int height = rotated ? centerImage.getBitmapWidth() : centerImage.getBitmapHeight();
        float statusBarHeight = (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
        float measuredHeight = (float) photoCropView.getMeasuredHeight() - dp(64) - statusBarHeight;
        float minSide = Math.min(photoCropView.getMeasuredWidth(), measuredHeight) - 2 * dp(16);
        return Math.max(minSide / width, minSide / height);
    }

    private boolean isStatusBarVisible() {
        return !inBubbleMode;
    }

    @SuppressLint({"NewApi", "DrawAllocation"})
    private void onDraw(Canvas canvas) {
        Canvas realCanvas = canvas;
        if (BLUR_RENDERNODE()) {
            if (renderNode == null) {
                renderNode = new RenderNode("photo viewer");
            }
            renderNode.setPosition(0, 0, canvas.getWidth(), canvas.getHeight() + AndroidUtilities.navigationBarHeight);
            canvas = renderNode.beginRecording();
        }
        if (parentFragment != null && parentFragment.getFragmentView() != null) {
            if (!scroller.isFinished()) {
                if (scroller.computeScrollOffset()) {
                    if (scroller.getStartX() < maxX && scroller.getStartX() > minX) {
                        translationX = scroller.getCurrX();
                    }
                    if (scroller.getStartY() < maxY && scroller.getStartY() > minY) {
                        translationY = scroller.getCurrY();
                    }
                    invalidateBlur();
                    containerView.invalidate();
                }
            }
            float progress = Math.abs(translationY) / (getContainerViewHeight() / 2f);
            if (clippingImageProgress != 0) {
                progress = progress + (1f - progress) * clippingImageProgress;
            }
            float scale = 1f + (1f - Utilities.clamp(progress, 1, 0)) * ZOOM_SCALE;
            if (!LiteMode.isEnabled(LiteMode.FLAG_CHAT_SCALE)) {
                scale = 1f;
            }
            View view = parentFragment.getFragmentView();
            /*if (AndroidUtilities.isTablet() && parentFragment.getParentActivity() instanceof LaunchActivity) {
                LaunchActivity activity = (LaunchActivity) parentFragment.getParentActivity();
                view = activity.getMainContainerFrameLayout();
            }*/

            if (view.getScaleX() != scale || view.getScaleY() != scale) {
                view.setPivotX(view.getWidth() / 2f);
                view.setPivotY(view.getHeight() / 2f);
                view.setScaleX(scale);
                view.setScaleY(scale);
            }

            if (parentAlert != null) {
                view = parentAlert.getContainer();
                if (view.getScaleX() != scale || view.getScaleY() != scale) {
                    view.setPivotX(view.getWidth() / 2f);
                    view.setPivotY(view.getHeight() / 2f);
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            }

            if (animationInProgress == 1 || animationInProgress == 2 || animationInProgress == 3 || pipAnimationInProgress) {
                containerView.invalidate();
            }
        }
        if (animationInProgress == 1) {
            float alpha = 1f;
            if (padImageForHorizontalInsets) {
                realCanvas.save();
                realCanvas.translate(getLeftInset() / 2f - getRightInset() / 2f, 0);
            }
            if (animatingImageView != null) {
                realCanvas.save();
                realCanvas.translate(
                    animatingImageView.getCenterX() - realCanvas.getWidth() / 2f,
                    animatingImageView.getCenterY() - realCanvas.getHeight() / 2f
                );
                float t = animatingImageView.getAnimationProgress();
                float scale = alpha = animationInProgress == 1 ? t : 1f - t;
                realCanvas.scale(scale, scale, realCanvas.getWidth() / 2f, realCanvas.getHeight() / 2f);
            }
            drawProgress(realCanvas, 0, 1, 0, alpha);
            if (animatingImageView != null) {
                realCanvas.restore();
            }
            if (padImageForHorizontalInsets) {
                realCanvas.restore();
            }

            if (animatingImageView != null && canvas != realCanvas) {
                canvas.save();
                canvas.translate(animatingImageView.getX(), animatingImageView.getY());
                canvas.scale(animatingImageView.getScaleX(), animatingImageView.getScaleY());
                animatingImageView.draw(canvas);
                canvas.restore();
            }

            if (BLUR_RENDERNODE()) {
                canvas = realCanvas;
                renderNode.endRecording();
            }
            drawFancyShadows(canvas);
            return;
        }
        if (animationInProgress == 3 || !isVisible && animationInProgress != 2 && !pipAnimationInProgress) {

            if (BLUR_RENDERNODE()) {
                canvas = realCanvas;
                renderNode.endRecording();
                canvas.drawRenderNode(renderNode);
            }
            return;
        }

        if (padImageForHorizontalInsets) {
            canvas.save();
            canvas.translate(getLeftInset() / 2 - getRightInset() / 2, 0);
        }

        currentCropScale = 1.0f;
        currentCropX = 0.0f;
        currentCropY = 0.0f;

        float currentTranslationY;
        float currentTranslationX;
        float currentScale;
        float currentRotation;
        float currentMirror;
        float aty = -1;
        long newUpdateTime = System.currentTimeMillis();
        long dt = newUpdateTime - videoCrossfadeAlphaLastTime;
        if (dt > 20) {
            dt = 17;
        }
        videoCrossfadeAlphaLastTime = newUpdateTime;

        if (imageMoveAnimation != null) {
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }

            float ts = scale + (animateToScale - scale) * animationValue;
            float tr = rotate + (animateToRotate - rotate) * animationValue;
            float tx = translationX + (animateToX - translationX) * animationValue;
            float ty = translationY + (animateToY - translationY) * animationValue;
            float tm = mirror + (animateToMirror - mirror) * animationValue;

            if (animateToScale == 1 && scale == 1 && translationX == 0) {
                aty = ty;
            }
            currentMirror = tm;
            currentScale = ts;
            currentRotation = tr;
            currentTranslationY = ty;
            currentTranslationX = tx;
            updateMinMax(currentScale);
            containerView.invalidate();
        } else {
            if (animationStartTime != 0) {
                translationX = animateToX;
                translationY = animateToY;
                scale = animateToScale;
                rotate = animateToRotate;
                animationStartTime = 0;
                updateMinMax(scale);
                zoomAnimation = false;
            }
            if (!scroller.isFinished()) {
                if (scroller.computeScrollOffset()) {
                    if (scroller.getStartX() < maxX && scroller.getStartX() > minX) {
                        translationX = scroller.getCurrX();
                    }
                    if (scroller.getStartY() < maxY && scroller.getStartY() > minY) {
                        translationY = scroller.getCurrY();
                    }
                    invalidateBlur();
                    containerView.invalidate();
                }
            }
            if (switchImageAfterAnimation != 0) {
                openedFullScreenVideo = false;
                if (!imagesArrLocals.isEmpty() && currentIndex >= 0 && currentIndex < imagesArrLocals.size()) {
                    Object object = imagesArrLocals.get(currentIndex);
                    if (object instanceof MediaController.MediaEditState) {
                        ((MediaController.MediaEditState) object).editedInfo = getCurrentVideoEditedInfo();
                    }
                }
                if (switchImageAfterAnimation == 1) {
                    AndroidUtilities.runOnUIThread(() -> switchToNextIndex(1, false));
                } else if (switchImageAfterAnimation == 2) {
                    AndroidUtilities.runOnUIThread(() -> switchToNextIndex(-1, false));
                }
                switchImageAfterAnimation = 0;
            }
            currentScale = scale;
            currentMirror = mirror;
            currentRotation = rotate;
            currentTranslationY = translationY;
            currentTranslationX = translationX;
            if (!moving) {
                aty = translationY;
            }
        }

        currentTranslationY += translateY;
        if (currentEditMode == EDIT_MODE_PAINT) {
            currentTranslationY += photoPaintView.getEmojiPadding(false) / 2f;
        }

        if (photoViewerWebView != null) {
            photoViewerWebView.setTranslationY(currentTranslationY);
        }

        if (isActionBarVisible) {
            if (currentScale <= 1.0001f) {
                if (!allowShowFullscreenButton && fullscreenButton[0].getTag() == null) {
                    fullscreenButton[0].animate().alpha(1.0f).setDuration(120).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fullscreenButton[0].setTag(null);
                        }
                    }).start();
                    fullscreenButton[0].setTag(1);
                    allowShowFullscreenButton = true;
                }
            } else {
                if (allowShowFullscreenButton) {
                    fullscreenButton[0].animate().alpha(0.0f).setDuration(120).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fullscreenButton[0].setTag(null);
                        }
                    }).start();
                    fullscreenButton[0].setTag(1);
                    allowShowFullscreenButton = false;
                }
            }
        }

        int containerWidth = getContainerViewWidth();
        int containerHeight = getContainerViewHeight();
        if (animationInProgress != 2 && animationInProgress != 4 && !pipAnimationInProgress && !isInline) {
            if (currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR && sendPhotoType != SELECT_TYPE_STICKER && scale == 1 && aty != -1 && !zoomAnimation) {
                float maxValue = containerWidth / 4.0f;
                backgroundDrawable.setAlpha((int) Math.max(127, 255 * (1.0f - (Math.min(Math.abs(aty), maxValue) / maxValue))));
            } else {
                backgroundDrawable.setAlpha(255);
            }
        } else if (animationInProgress == 4) {
            canvas.drawColor(0xff000000);
        }

        sideImage = null;
        if (currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR && sendPhotoType != SELECT_TYPE_STICKER) {
            if (scale >= 1.0f && !zoomAnimation && !zooming) {
                if (currentTranslationX > maxX + dp(5)) {
                    sideImage = leftImage;
                } else if (currentTranslationX < minX - dp(5)) {
                    sideImage = rightImage;
                } else {
                    groupedPhotosListView.setMoveProgress(0.0f);
                }
            }
            changingPage = sideImage != null;
        }

        for (int a = 0; a < 3; a++) {
            float offsetX;
            if (a == 1) {
                offsetX = 0;
            } else if (a == 2) {
                offsetX = -containerView.getMeasuredWidth() - dp(15) + (currentTranslationX - maxX);
            } else {
                offsetX = currentTranslationX < minX ? (currentTranslationX - minX) : 0;
            }
            fullscreenButton[a].setTranslationX(offsetX + containerView.getMeasuredWidth() - dp(48));
        }

        if (sideImage == rightImage) {
            float translateX = currentTranslationX;
            float scaleDiff = 0;
            float alpha = 1;
            if (!zoomAnimation && translateX < minX) {
                alpha = Math.min(1.0f, (minX - translateX) / containerWidth);
                scaleDiff = (1.0f - alpha) * 0.3f;
                translateX = -containerWidth - dp(30) / 2;
            }

            if (sideImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate(containerWidth / 2, containerHeight / 2);
                canvas.translate(containerWidth + dp(30) / 2 + translateX, 0);
                canvas.scale(1.0f - scaleDiff, 1.0f - scaleDiff);
                int bitmapWidth = sideImage.getBitmapWidth();
                int bitmapHeight = sideImage.getBitmapHeight();
                if (!rightImageIsVideo && rightCropState != null && rightCropTransform.hasViewTransform()) {
                    applyCrop(canvas, containerWidth, containerHeight, bitmapWidth, bitmapHeight, 1f, rightCropTransform, rightCropState);
                }

                float scaleX = containerWidth / (float) bitmapWidth;
                float scaleY = containerHeight / (float) bitmapHeight;
                float scale = Math.min(scaleX, scaleY);
                int width = (int) (bitmapWidth * scale);
                int height = (int) (bitmapHeight * scale);

                boolean mirror = false;
                if (!imagesArrLocals.isEmpty()) {
                    if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                        mirror = rightCropTransform.isMirrored();
                    } else {
                        mirror = rightCropState != null && rightCropState.mirrored;
                    }
                }
                if (mirror) {
                    canvas.scale(-1, 1);
                }

                sideImage.setAlpha(alpha);
                sideImage.setImageCoords(-width / 2, -height / 2, width, height);
                sideImage.draw(canvas);

                if (rightPaintingOverlay != null && rightPaintingOverlay.getVisibility() == View.VISIBLE) {
                    canvas.clipRect(-width / 2, -height / 2, width / 2, height / 2);
                    if (rightPaintingOverlay.getMeasuredWidth() != bitmapWidth || rightPaintingOverlay.getMeasuredHeight() != bitmapHeight) {
                        rightPaintingOverlay.measure(View.MeasureSpec.makeMeasureSpec(bitmapWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(bitmapHeight, View.MeasureSpec.EXACTLY));
                        rightPaintingOverlay.layout(0, 0, bitmapWidth, bitmapHeight);
                    }
                    canvas.scale(scale, scale);
                    canvas.translate(-bitmapWidth / 2, -bitmapHeight / 2);
                    rightPaintingOverlay.setAlpha(1.0f);
                    rightPaintingOverlay.draw(canvas);
                }

                canvas.restore();
            }
            groupedPhotosListView.setMoveProgress(-alpha);

            if (seekSpeedDrawable == null || !seekSpeedDrawable.isShown()) {
                canvas.save();
                canvas.translate(translateX, currentTranslationY / currentScale);
                canvas.translate((containerWidth * (scale + 1) + dp(30)) / 2, -currentTranslationY / currentScale);
                photoProgressViews[1].setScale(1.0f - scaleDiff);
                photoProgressViews[1].setAlpha(alpha);
                photoProgressViews[1].onDraw(canvas);

                if (isActionBarVisible) {
                    fullscreenButton[1].setAlpha(alpha);
                }

                canvas.restore();
            }
        } else {
            if (isActionBarVisible) {
                fullscreenButton[1].setAlpha(0.0f);
            }
        }

        float translateX = currentTranslationX;
        float scaleDiff = 0;
        float alpha = 1;
        if (!zoomAnimation && translateX > maxX && (currentEditMode == EDIT_MODE_NONE || currentEditMode == EDIT_MODE_STICKER_MASK) && sendPhotoType != SELECT_TYPE_AVATAR) {
            alpha = Math.min(1.0f, (translateX - maxX) / containerWidth);
            scaleDiff = alpha * 0.3f;
            alpha = 1.0f - alpha;
            translateX = maxX;
        }
        boolean drawTextureView = videoSizeSet && aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == View.VISIBLE;
        boolean drawCenterImage = false;
        float livePhotoVideoAlpha = 1.0f;
        if (centerImageIsLivePhoto && videoPlayer != null && sendPhotoType != SELECT_TYPE_STICKER) {
            if (!isUnalivePhoto()) {
                final long position = videoPlayer.getCurrentPosition();
                if (position <= 0 && !videoPlayer.isPlaying()) {
                    livePhotoVideoAlpha = 0.0f;
                } else if (videoPlayer.getDuration() > 0) {
                    livePhotoVideoAlpha = Utilities.clamp01((videoPlayer.getDuration() - 90 - position) / 500.0f);
                }
                if (livePhotoVideoAlpha <= 0) {
                    drawTextureView = false;
                } else if (livePhotoVideoAlpha < 1) {
                    drawCenterImage = true;
                }
            } else {
                drawTextureView = false;
                drawCenterImage = true;
            }
        }
        centerImageTransformLocked = false;
        centerImageTransform.reset();
        if (maskPaintView != null && !maskPaintViewShuttingDown && maskPaintView.getRenderView() != null) {
            alpha = maskPaintView.getRenderView().getAlpha() > .99f ? 0f : 1f;
        }
        if (centerImage.hasBitmapImage() || drawTextureView && textureUploaded) {
            if (stickerMakerView != null && stickerMakerView.outlineVisible) {
                boolean isCropped = false;
                try {
                    Object object = imagesArrLocals.get(currentIndex);
                    MediaController.PhotoEntry photoEntry = ((MediaController.PhotoEntry) object);
                    isCropped = photoEntry.isCropped;
                } catch (Exception e) {}
                if (isCropped) {
                    stickerMakerView.updateOutlineBounds(false);
                } else {
                    int stickerSize = containerWidth - dp(20);
                    stickerMakerView.outlineMatrix.reset();
                    stickerMakerView.outlineMatrix.postTranslate(-.5f, -.5f);
                    stickerMakerView.outlineMatrix.postScale(stickerSize, stickerSize);
                    stickerMakerView.outlineMatrix.postScale(1f / currentScale, 1f / currentScale);
                    stickerMakerView.outlineMatrix.postTranslate(-currentTranslationX / currentScale, -currentTranslationY / currentScale);
                    stickerMakerView.outlineMatrix.postRotate(-currentRotation);
                    stickerMakerView.updateOutlineBounds(true);
                }
            }

            canvas.save();
            canvas.translate(containerWidth / 2f + getAdditionX(currentEditMode), containerHeight / 2f + getAdditionY(currentEditMode));
            centerImageTransform.preTranslate(containerWidth / 2f + getAdditionX(currentEditMode), containerHeight / 2f + getAdditionY(currentEditMode));
            canvas.translate(translateX, currentTranslationY + (currentEditMode != EDIT_MODE_PAINT ? currentPanTranslationY : 0));
            centerImageTransform.preTranslate(translateX, currentTranslationY + (currentEditMode != EDIT_MODE_PAINT ? currentPanTranslationY : 0));
            canvas.scale(currentScale - scaleDiff, currentScale - scaleDiff);
            centerImageTransform.preScale(currentScale - scaleDiff, currentScale - scaleDiff);
            canvas.rotate(currentRotation);
            centerImageTransform.preRotate(currentRotation);
            if (currentEditMode == EDIT_MODE_PAINT && photoPaintView != null) {
                int trueH = getContainerViewHeight(true, 0);
                trueH -= photoPaintView.getEmojiPadding(Math.abs(AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight - trueH) < dp(20));
                int h = getContainerViewHeight(false, 0);
                canvas.translate(0, (trueH - h) / 2f * (1f - photoPaintView.adjustPanLayoutHelperProgress()));
                centerImageTransform.preTranslate(0, (trueH - h) / 2f * (1f - photoPaintView.adjustPanLayoutHelperProgress()));
            }

            if (!pipAnimationInProgress && (!drawTextureView || !textureUploaded && !videoSizeSet || !videoCrossfadeStarted || videoCrossfadeAlpha != 1.0f)) {
                if (videoFrameBitmap != null && isCurrentVideo) {
                    int w = videoFrameBitmap.getWidth(), h = videoFrameBitmap.getHeight(), l = -w / 2, t = -h / 2;
                    if (alpha < 1f) {
                        canvas.saveLayerAlpha(l, t, l + w, t + h, (int) (255 * alpha), Canvas.ALL_SAVE_FLAG);
                    }
                    canvas.drawBitmap(videoFrameBitmap, l, t, videoFrameBitmapPaint);
                    if (alpha < 1f) {
                        canvas.restore();
                    }
                } else {
                    centerImage.setAlpha(alpha);
                    int width = centerImage.getBitmapWidth();
                    int height = centerImage.getBitmapHeight();
                    float scale;
                    if (isCurrentVideo && currentEditMode == EDIT_MODE_NONE && sendPhotoType == SELECT_TYPE_AVATAR) {
                        scale = getCropFillScale(false);
                    } else {
                        scale = Math.min(containerWidth / (float) width, containerHeight / (float) height);
                    }
                    width *= scale;
                    height *= scale;
                    centerImage.setImageCoords(-width / 2, -height / 2, width, height);
                    if (isCurrentVideo && (!centerImageIsLivePhoto || isUnalivePhoto())) {
                        centerImage.draw(canvas);
                        centerImageTransformLocked = true;
                    } else {
                        drawCenterImage = true;
                    }
                }
            }

            int bitmapWidth, originalWidth;
            int bitmapHeight, originalHeight;
            if (drawTextureView && textureUploaded && videoSizeSet) {
                View view = usedSurfaceView ? videoSurfaceView : videoTextureView;
                originalWidth = bitmapWidth = view.getMeasuredWidth();
                originalHeight = bitmapHeight = view.getMeasuredHeight();
            } else {
                originalWidth = bitmapWidth = centerImage.getBitmapWidth();
                originalHeight = bitmapHeight = centerImage.getBitmapHeight();
            }

            float scale = Math.min(containerWidth / (float) originalWidth, containerHeight / (float) originalHeight);
            int width = (int) (originalWidth * scale);
            int height = (int) (originalHeight * scale);

            boolean applyCrop;
            float scaleToFitX = 1.0f;
            if (!imagesArrLocals.isEmpty()) {
                if (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT) {
                    applyCrop = true;
                } else if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    applyCrop = (switchingToMode == EDIT_MODE_NONE || currentEditMode != EDIT_MODE_FILTER);
                } else {
                    applyCrop = imageMoveAnimation != null && switchingToMode != -1 || currentEditMode == EDIT_MODE_NONE || currentEditMode == EDIT_MODE_STICKER_MASK || currentEditMode == EDIT_MODE_CROP || switchingToMode != -1;
                }
            } else {
                applyCrop = false;
            }
            if (applyCrop) {
                int rotatedWidth = originalWidth;
                int rotatedHeight = originalHeight;
                int orientation = cropTransform.getOrientation();
                if (orientation == 90 || orientation == 270) {
                    int temp = bitmapWidth;
                    bitmapWidth = bitmapHeight;
                    bitmapHeight = temp;

                    temp = rotatedWidth;
                    rotatedWidth = rotatedHeight;
                    rotatedHeight = temp;
                }
                float cropAnimationValue;
                if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT)) {
                    cropAnimationValue = 1.0f;
                } else if (imageMoveAnimation != null && switchingToMode != -1) {
                    if (currentEditMode == EDIT_MODE_CROP || switchingToMode == EDIT_MODE_CROP) {
                        cropAnimationValue = 1.0f;
                    } else if (switchingToMode == EDIT_MODE_NONE) {
                        cropAnimationValue = animationValue;
                    } else {
                        cropAnimationValue = 1.0f - animationValue;
                    }
                } else {
                    cropAnimationValue = currentEditMode == EDIT_MODE_FILTER || currentEditMode == EDIT_MODE_PAINT ? 0.0f : 1.0f;
                }
                float cropPw = cropTransform.getCropPw();
                float cropPh = cropTransform.getCropPh();
                bitmapWidth *= cropPw + (1.0f - cropPw) * (1.0f - cropAnimationValue);
                bitmapHeight *= cropPh + (1.0f - cropPh) * (1.0f - cropAnimationValue);
                scaleToFitX = containerWidth / (float) bitmapWidth;
                if (scaleToFitX * bitmapHeight > containerHeight) {
                    scaleToFitX = containerHeight / (float) bitmapHeight;
                }
                if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode != 1 || switchingToMode == EDIT_MODE_NONE) && editState.cropState != null) {
                    float startW = bitmapWidth * scaleToFitX;
                    float startH = bitmapHeight * scaleToFitX;
                    float originalScaleToFitX = containerWidth / (float) originalWidth;
                    if (originalScaleToFitX * originalHeight > containerHeight) {
                        originalScaleToFitX = containerHeight / (float) originalHeight;
                    }
                    float finalW = originalWidth * originalScaleToFitX / (currentScale - scaleDiff);
                    float finalH = originalHeight * originalScaleToFitX / (currentScale - scaleDiff);

                    float w = startW + (finalW - startW) * (1.0f - cropAnimationValue);
                    float h = startH + (finalH - startH) * (1.0f - cropAnimationValue);

                    canvas.clipRect(-w / 2, -h / 2, w / 2, h / 2);
                }
                if (sendPhotoType == SELECT_TYPE_AVATAR || cropTransform.hasViewTransform()) {
                    float cropScale;
                    if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                        if (videoTextureView != null) {
                            videoTextureView.setScaleX(cropTransform.isMirrored() ? -1.0f : 1.0f);
                            if (firstFrameView != null) {
                                firstFrameView.setScaleX(videoTextureView.getScaleX());
                            }
                        }
                        float trueScale = 1.0f + (cropTransform.getTrueCropScale() - 1.0f) * (1.0f - cropAnimationValue);
                        cropScale = cropTransform.getScale() / trueScale;
                        float scaleToFit = containerWidth / (float) rotatedWidth;
                        if (scaleToFit * rotatedHeight > containerHeight) {
                            scaleToFit = containerHeight / (float) rotatedHeight;
                        }
                        cropScale *= scaleToFit / scale;
                        if (sendPhotoType == SELECT_TYPE_AVATAR) {
                            if (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT) {
                                cropScale /= 1.0f + (cropTransform.getMinScale() - 1.0f) * (1.0f - cropAnimationValue);
                            } else if (switchingToMode == EDIT_MODE_NONE) {
                                cropScale /= cropTransform.getMinScale();
                            }
                        }
                    } else {
                        if (videoTextureView != null) {
                            videoTextureView.setScaleX(editState.cropState != null && editState.cropState.mirrored ? -1.0f : 1.0f);
                            if (firstFrameView != null) {
                                firstFrameView.setScaleX(videoTextureView.getScaleX());
                            }
                        }
                        cropScale = editState.cropState != null ? editState.cropState.cropScale : 1.0f;
                        float trueScale = 1.0f + (cropScale - 1.0f) * (1.0f - cropAnimationValue);
                        cropScale *= scaleToFitX / scale / trueScale;
                    }

                    canvas.translate(cropTransform.getCropAreaX() * cropAnimationValue, cropTransform.getCropAreaY() * cropAnimationValue);
                    if (!centerImageTransformLocked) centerImageTransform.preTranslate(cropTransform.getCropAreaX() * cropAnimationValue, cropTransform.getCropAreaY() * cropAnimationValue);
                    currentCropScale = cropScale;
                    canvas.scale(cropScale, cropScale);
                    if (!centerImageTransformLocked) centerImageTransform.preScale(cropScale, cropScale);
                    canvas.translate(currentCropX = cropTransform.getCropPx() * rotatedWidth * scale * cropAnimationValue, currentCropY = cropTransform.getCropPy() * rotatedHeight * scale * cropAnimationValue);
                    if (!centerImageTransformLocked) centerImageTransform.preTranslate(cropTransform.getCropPx() * rotatedWidth * scale * cropAnimationValue, cropTransform.getCropPy() * rotatedHeight * scale * cropAnimationValue);
                    float rotation = (cropTransform.getRotation() + orientation);
                    if (rotation > 180) {
                        rotation -= 360;
                    }
                    if (sendPhotoType == SELECT_TYPE_AVATAR && (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT)) {
                        canvas.rotate(rotation);
                        if (!centerImageTransformLocked) centerImageTransform.preRotate(rotation);
                    } else {
                        canvas.rotate(rotation * cropAnimationValue);
                        if (!centerImageTransformLocked) centerImageTransform.preRotate(rotation * cropAnimationValue);
                    }
                } else {
                    if (videoTextureView != null) {
                        videoTextureView.setScaleX(1.0f);
                        videoTextureView.setScaleY(1.0f);
                        if (firstFrameView != null) {
                            firstFrameView.setScaleX(1);
                            firstFrameView.setScaleY(1);
                        }
                    }
                }
            }
            if (currentEditMode == EDIT_MODE_PAINT) {
                photoPaintView.setTransform(currentScale, currentTranslationX, currentTranslationY + (sendPhotoType == SELECT_TYPE_AVATAR ? AndroidUtilities.statusBarHeight / 2f : 0) * photoPaintView.getRenderView().getScaleX(), bitmapWidth * scaleToFitX, bitmapHeight * scaleToFitX);
            } else if (currentEditMode == EDIT_MODE_STICKER_MASK) {
                maskPaintView.setTransform(currentScale, currentTranslationX, currentTranslationY, currentRotation, bitmapWidth * scaleToFitX, bitmapHeight * scaleToFitX);
            }

            if (drawCenterImage && !usedSurfaceView) {
                drawCenterImageInternal(canvas, currentMirror, alpha);
            }
            canvas.save();
            boolean restoreMirror = false;
            if (currentMirror > 0) {
                canvas.save();
                canvas.scale(1 - currentMirror * 2, 1f);
                canvas.skew(0, 4 * currentMirror * (1f - currentMirror) * .25f);
                restoreMirror = true;
            }
            canvas.translate(-width / 2, -height / 2);
            if (drawTextureView || paintingOverlay.getVisibility() == View.VISIBLE) {
                canvas.scale(scale, scale);
            }
            if (drawTextureView) {
                if (!videoCrossfadeStarted && ((usedSurfaceView && firstFrameRendered) || (textureUploaded && videoSizeSet))) {
                    videoCrossfadeStarted = true;
                    videoCrossfadeAlpha = 0.0f;
                    videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                    containerView.getMeasuredHeight();
                }
                if (videoTextureView != null) {
                    videoTextureView.setAlpha(alpha * videoCrossfadeAlpha * livePhotoVideoAlpha);
                }
                if (videoTextureView instanceof VideoEditTextureView) {
                    VideoEditTextureView videoEditTextureView = (VideoEditTextureView) videoTextureView;
                    videoEditTextureView.setViewRect((containerWidth - width) / 2f + getAdditionX(currentEditMode) + translateX, (containerHeight - height) / 2f + getAdditionY(currentEditMode) + currentTranslationY + currentPanTranslationY, width, height);
                }
                if (videoSurfaceView != null && waitingForDraw == 0 && !changingTextureView && !switchingInlineMode && !pipAnimationInProgress && videoSurfaceView.getVisibility() != View.VISIBLE) {
                    videoSurfaceView.setVisibility(View.VISIBLE);
                }
                if (!usedSurfaceView || firstFrameRendered) {
                    aspectRatioFrameLayout.draw(canvas);
                    if (framesRewinder != null) {
                        framesRewinder.draw(canvas, aspectRatioFrameLayout.getWidth(), aspectRatioFrameLayout.getHeight());
                    }
                }
                if (usedSurfaceView && alpha != 1f) {
                    if (surfaceBlackoutPaint == null) {
                        surfaceBlackoutPaint = new Paint();
                    }
                    surfaceBlackoutPaint.setAlpha((int) (255 * (1f - alpha)));
                    canvas.drawRect(-1f, -1f, aspectRatioFrameLayout.getWidth() + 1f, aspectRatioFrameLayout.getHeight() + 1f, surfaceBlackoutPaint);
                }

                if (videoCrossfadeStarted && videoCrossfadeAlpha < 1.0f) {
                    videoCrossfadeAlpha += dt / (playerInjected ? 100.0f : 200.0f);
                    containerView.invalidate();
                    invalidateBlur();
                    if (videoCrossfadeAlpha > 1.0f) {
                        videoCrossfadeAlpha = 1.0f;
                    }
                }
                paintingOverlay.setAlpha(alpha);
            }
            if (restoreMirror) {
                canvas.restore();
                canvas.translate(-width / 2, -height / 2);
                if (drawTextureView || paintingOverlay.getVisibility() == View.VISIBLE) {
                    canvas.scale(scale, scale);
                }
            }
            if (paintingOverlay.getVisibility() == View.VISIBLE && (isCurrentVideo || currentEditMode != EDIT_MODE_FILTER || switchingToMode != -1)) {
                if (sendPhotoType != SELECT_TYPE_STICKER) {
                    canvas.clipRect(0, 0, paintingOverlay.getMeasuredWidth(), paintingOverlay.getMeasuredHeight());
                }
                paintingOverlay.draw(canvas);
            }
            canvas.restore();

            if (drawCenterImage && usedSurfaceView && (videoCrossfadeAlpha != 1f || livePhotoVideoAlpha < 1)) {
                drawCenterImageInternal(canvas, currentMirror, Math.max(1f - videoCrossfadeAlpha, 1.0f - livePhotoVideoAlpha) * alpha);
            }
            canvas.restore();
            for (int a = 0; a < pressedDrawable.length; a++) {
                if (drawPressedDrawable[a] || pressedDrawableAlpha[a] != 0) {
                    pressedDrawable[a].setAlpha((int) (pressedDrawableAlpha[a] * 255));
                    if (a == 0) {
                        pressedDrawable[a].setBounds(0, 0, containerView.getMeasuredWidth() / 5, containerView.getMeasuredHeight());
                    } else {
                        pressedDrawable[a].setBounds(containerView.getMeasuredWidth() - containerView.getMeasuredWidth() / 5, 0, containerView.getMeasuredWidth(), containerView.getMeasuredHeight());
                    }
                    pressedDrawable[a].draw(canvas);
                }
                if (drawPressedDrawable[a]) {
                    if (pressedDrawableAlpha[a] < 1.0f) {
                        pressedDrawableAlpha[a] += dt / 180.0f;
                        if (pressedDrawableAlpha[a] > 1.0f) {
                            pressedDrawableAlpha[a] = 1.0f;
                        }
                        containerView.invalidate();
                    }
                } else {
                    if (pressedDrawableAlpha[a] > 0.0f) {
                        pressedDrawableAlpha[a] -= dt / 180.0f;
                        if (pressedDrawableAlpha[a] < 0.0f) {
                            pressedDrawableAlpha[a] = 0.0f;
                        }
                        containerView.invalidate();
                    }
                }
            }
        }
        drawProgress(canvas, translateX, currentScale, currentTranslationY, alpha);

        if (sideImage == leftImage) {
            if (sideImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate(containerWidth / 2, containerHeight / 2);
                canvas.translate(-(containerWidth * (scale + 1) + dp(30)) / 2 + currentTranslationX, 0);
                int bitmapWidth = sideImage.getBitmapWidth();
                int bitmapHeight = sideImage.getBitmapHeight();
                if (!leftImageIsVideo && leftCropState != null && leftCropTransform.hasViewTransform()) {
                    applyCrop(canvas, containerWidth, containerHeight, bitmapWidth, bitmapHeight, currentScale, leftCropTransform, leftCropState);
                }
                float scaleX = containerWidth / (float) bitmapWidth;
                float scaleY = containerHeight / (float) bitmapHeight;
                float scale = Math.min(scaleX, scaleY);
                int width = (int) (bitmapWidth * scale);
                int height = (int) (bitmapHeight * scale);

                boolean mirror = false;
                if (!imagesArrLocals.isEmpty()) {
                    if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                        mirror = leftCropTransform.isMirrored();
                    } else {
                        mirror = leftCropState != null && leftCropState.mirrored;
                    }
                }
                if (mirror) {
                    canvas.scale(-1, 1);
                }

                sideImage.setAlpha(1.0f);
                sideImage.setImageCoords(-width / 2, -height / 2, width, height);
                sideImage.draw(canvas);

                if (leftPaintingOverlay != null && leftPaintingOverlay.getVisibility() == View.VISIBLE) {
                    canvas.clipRect(-width/2, -height/2, width/2, height/2);
                    if (leftPaintingOverlay.getMeasuredWidth() != bitmapWidth || leftPaintingOverlay.getMeasuredHeight() != bitmapHeight) {
                        leftPaintingOverlay.measure(View.MeasureSpec.makeMeasureSpec(bitmapWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(bitmapHeight, View.MeasureSpec.EXACTLY));
                        leftPaintingOverlay.layout(0, 0, bitmapWidth, bitmapHeight);
                    }
                    canvas.scale(scale, scale);
                    canvas.translate(-bitmapWidth/2, -bitmapHeight/2);
                    leftPaintingOverlay.setAlpha(1.0f);
                    leftPaintingOverlay.draw(canvas);
                }

                canvas.restore();
            }
            groupedPhotosListView.setMoveProgress(1.0f - alpha);

            if (seekSpeedDrawable == null || !seekSpeedDrawable.isShown()) {
                canvas.save();
                canvas.translate(currentTranslationX, currentTranslationY / currentScale);
                canvas.translate(-(containerWidth * (scale + 1) + dp(30)) / 2, -currentTranslationY / currentScale);
                photoProgressViews[2].setScale(1.0f);
                photoProgressViews[2].setAlpha(1.0f);
                photoProgressViews[2].onDraw(canvas);

                if (isActionBarVisible) {
                    fullscreenButton[2].setAlpha(1.0f);
                }
                canvas.restore();
            }
        } else {
            if (isActionBarVisible) {
                fullscreenButton[2].setAlpha(0.0f);
            }
        }

        if (waitingForDraw != 0) {
            waitingForDraw--;
            if (waitingForDraw == 0) {
                if (changedTextureView != null && !usedSurfaceView) {
                    try {
                        currentBitmap = changedTextureView.getBitmap();
                    } catch (Throwable e) {
                        if (currentBitmap != null) {
                            currentBitmap.recycle();
                            currentBitmap = null;
                        }
                        FileLog.e(e);
                    }
                    if (currentBitmap != null) {
                        textureImageView.setVisibility(View.VISIBLE);
                        textureImageView.setImageBitmap(currentBitmap);
                    } else {
                        textureImageView.setImageDrawable(null);
                    }
                }
                if (usedSurfaceView) {
//                    if (videoSurfaceView != null) {
//                        videoSurfaceView.setVisibility(View.VISIBLE);
//                    }
                    AndroidUtilities.runOnUIThread(() -> {
                        checkChangedTextureView(false);
                        PipVideoOverlay.dismiss(true, true);
                    });

                } else {
                    PipVideoOverlay.dismiss(true);
                }
            } else {
                containerView.invalidate();
            }
        }

        if (padImageForHorizontalInsets) {
            canvas.restore();
        }

        if (aspectRatioFrameLayout != null) {
            int h = (int) (aspectRatioFrameLayout.getMeasuredHeight() * (currentScale - 1.0f)) / 2;
            if (videoForwardDrawable.isAnimating()) {
                videoForwardDrawable.setBounds(aspectRatioFrameLayout.getLeft(), aspectRatioFrameLayout.getTop() - h + (int) (currentTranslationY / currentScale), aspectRatioFrameLayout.getRight(), aspectRatioFrameLayout.getBottom() + h + (int) (currentTranslationY / currentScale));
                videoForwardDrawable.draw(canvas);
            }
            if (seekSpeedDrawable.isShown()) {
                seekSpeedDrawable.setBounds(aspectRatioFrameLayout.getLeft(), (int) (AndroidUtilities.statusBarHeight + dp(90) * actionBar.getAlpha()), aspectRatioFrameLayout.getRight(), aspectRatioFrameLayout.getBottom() + h + (int) (currentTranslationY / currentScale));
                seekSpeedDrawable.draw(canvas);
            }
        }

        if (BLUR_RENDERNODE()) {
            canvas = realCanvas;
            renderNode.endRecording();
            canvas.drawRenderNode(renderNode);
        }

        drawFancyShadows(canvas);
    }
    private Matrix m;

    private Path clipFancyShadows;
    private Paint topFancyShadowPaint, bottomFancyShadowPaint;
    private LinearGradient topFancyShadow, bottomFancyShadow;
    private Matrix topFancyShadowMatrix, bottomFancyShadowMatrix;

    private void drawFancyShadows(Canvas canvas) {
        if (!fancyShadows) {
            return;
        }
        float maxAlpha = !SharedConfig.photoViewerBlur ? 1f : blurAlpha.set(animationInProgress == 0 || animationInProgress == 2 || animationInProgress == 3);
        if (maxAlpha <= 0) {
            return;
        }

        int top = (int) (AndroidUtilities.statusBarHeight * 1.5f) + ActionBar.getCurrentActionBarHeight();
        int bottom = AndroidUtilities.navigationBarHeight + pickerView.getHeight() + (captionEdit.getVisibility() == View.VISIBLE ? captionEdit.getEditTextHeightClosedKeyboard() / 2 + dp(20) : 0);

        if (clipFancyShadows == null) {
            clipFancyShadows = new Path();
            topFancyShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            topFancyShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            bottomFancyShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bottomFancyShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            topFancyShadow = new LinearGradient(0, 0, 0, 16, new int[] { 0xff000000, 0 }, new float[] { 0, 1 }, Shader.TileMode.CLAMP);
            bottomFancyShadow = new LinearGradient(0, 0, 0, 16, new int[] { 0, 0xff000000 }, new float[] { 0, 1 }, Shader.TileMode.CLAMP);
            topFancyShadowMatrix = new Matrix();
            bottomFancyShadowMatrix = new Matrix();
            topFancyShadowPaint.setShader(topFancyShadow);
            bottomFancyShadowPaint.setShader(bottomFancyShadow);
        }

        canvas.saveLayerAlpha(0, 0, containerView.getWidth(), containerView.getHeight() + AndroidUtilities.navigationBarHeight, (int) (maxAlpha * (backgroundDrawable.getAlpha() - 127) * (1f / 127f * 255f)), Canvas.ALL_SAVE_FLAG);
        clipFancyShadows.rewind();
        clipFancyShadows.addRect(0, 0, containerView.getWidth(), top, Path.Direction.CW);
        clipFancyShadows.addRect(0, containerView.getHeight() + AndroidUtilities.navigationBarHeight - bottom, containerView.getWidth(), containerView.getHeight() + AndroidUtilities.navigationBarHeight, Path.Direction.CW);
        canvas.clipPath(clipFancyShadows);
        canvas.drawColor(0xff000000);
        drawCaptionBlur(canvas, shadowBlurer, 0, 0, true, true, false);
        canvas.save();
        topFancyShadowMatrix.reset();
        topFancyShadowMatrix.postScale(1, top / 16f);
        topFancyShadow.setLocalMatrix(topFancyShadowMatrix);
        topFancyShadowPaint.setAlpha(0xd0);
        canvas.drawRect(0, 0, containerView.getWidth(), top, topFancyShadowPaint);
        bottomFancyShadowMatrix.reset();
        bottomFancyShadowMatrix.postScale(1, bottom / 16f);
        bottomFancyShadowMatrix.postTranslate(0, containerView.getHeight() - bottom + AndroidUtilities.navigationBarHeight);
        bottomFancyShadow.setLocalMatrix(bottomFancyShadowMatrix);
        bottomFancyShadowPaint.setAlpha(0xbb);
        canvas.drawRect(0, containerView.getHeight() + AndroidUtilities.navigationBarHeight - bottom, containerView.getWidth(), containerView.getHeight() + AndroidUtilities.navigationBarHeight, bottomFancyShadowPaint);
        canvas.restore();
        canvas.restore();
    }

    private void drawCenterImageInternal(Canvas canvas, float currentMirror, float alpha) {
        boolean mirror = false;
        if (!imagesArrLocals.isEmpty()) {
            if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                mirror = cropTransform.isMirrored();
            } else {
                mirror = editState.cropState != null && editState.cropState.mirrored;
            }
        }
        boolean restore = false;
        if (mirror) {
            canvas.save();
            canvas.scale(-1, 1);
            restore = true;
        }
        if (currentMirror > 0) {
            if (!restore) {
                canvas.save();
                restore = true;
            }
            canvas.scale(1 - currentMirror * 2, 1f);
            canvas.skew(0, 4 * currentMirror * (1f - currentMirror) * .25f);
        }
        if (photoViewerWebView == null || !photoViewerWebView.isLoaded()) {
            if (!centerImageTransformLocked) centerImageTransform.preTranslate(centerImage.getImageX(), centerImage.getImageY());
            stickerMakerView.drawOutline(canvas, false, containerView, switchingToMode != -1);
            centerImage.setAlpha(alpha);
            centerImage.draw(canvas);
            stickerMakerView.drawOutline(canvas, true, containerView, switchingToMode != -1);
            stickerMakerView.drawSegmentBorderPath(canvas, centerImage, centerImageTransform, containerView);
            centerImageTransformLocked = true;
        }
        if (restore) {
            canvas.restore();
        }
    }

    private void drawProgress(Canvas canvas, float translateX, float currentScale, float currentTranslationY, float alpha) {
        boolean drawProgress;
        if (isCurrentVideo) {
            drawProgress = (videoTimelineView == null || !videoTimelineView.isDragging()) && (sendPhotoType != SELECT_TYPE_AVATAR || manuallyPaused) && (videoPlayer == null || !videoPlayer.isPlaying()) && switchingToMode != EDIT_MODE_COVER && currentEditMode != EDIT_MODE_COVER;
        } else {
            drawProgress = true;
        }
        if (centerImageIsLivePhoto && (isUnalivePhoto() || currentMessageObject != null)) {
            drawProgress = false;
        }
        boolean drawMiniProgress = miniProgressView.getVisibility() == View.VISIBLE || miniProgressAnimator != null;
        if (drawProgress) {
            final float tx = !zoomAnimation && -translateX > maxX ? translateX + maxX : 0;
            float ty = currentScale == 1.0f ? currentTranslationY : 0;
            float progressAlpha = alpha;
            if (drawMiniProgress) {
                progressAlpha *= 1f - miniProgressView.getAlpha();
            }
            if (pipAnimationInProgress) {
                progressAlpha *= actionBar.getAlpha();
            } else if (photoProgressViews[0].backgroundState == PROGRESS_PAUSE) {
                ty += dpf2(8) * (1f - actionBar.getAlpha());
            }
            if (seekSpeedDrawable == null || !seekSpeedDrawable.isShown()) {
                canvas.save();
                canvas.translate(tx, ty);
                photoProgressViews[0].setScale(1.0f);
                photoProgressViews[0].setAlpha(progressAlpha);
                photoProgressViews[0].onDraw(canvas);

                if (isActionBarVisible && allowShowFullscreenButton && fullscreenButton[0].getTag() == null) {
                    fullscreenButton[0].setAlpha(Math.min(fullscreenButton[0].getAlpha(), alpha));
                }

                canvas.restore();
            }
        }
        if (drawMiniProgress && !pipAnimationInProgress) {
            canvas.save();
            canvas.translate(miniProgressView.getLeft() + translateX, miniProgressView.getTop() + currentTranslationY / currentScale);
            miniProgressView.draw(canvas);
            canvas.restore();
        }
    }

    private int[] tempInt = new int[2];

    private int[] applyCrop(Canvas canvas, int containerWidth, int containerHeight, int bitmapWidth, int bitmapHeight, float currentScale, CropTransform cropTransform, MediaController.CropState cropState) {
        int originalWidth = bitmapWidth;
        int originalHeight = bitmapHeight;
        float scale = Math.min(containerWidth / (float) originalWidth, containerHeight / (float) originalHeight);
        int rotatedWidth = originalWidth;
        int rotatedHeight = originalHeight;
        int orientation = cropTransform.getOrientation();
        if (orientation == 90 || orientation == 270) {
            int temp = bitmapWidth;
            bitmapWidth = bitmapHeight;
            bitmapHeight = temp;

            temp = rotatedWidth;
            rotatedWidth = rotatedHeight;
            rotatedHeight = temp;
        }
        float cropAnimationValue;
        if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT)) {
            cropAnimationValue = 1.0f;
        } else if (imageMoveAnimation != null && switchingToMode != -1) {
            if (currentEditMode == EDIT_MODE_CROP || switchingToMode == EDIT_MODE_CROP || (currentEditMode == EDIT_MODE_FILTER || currentEditMode == EDIT_MODE_PAINT) && switchingToMode == -1) {
                cropAnimationValue = 1.0f;
            } else if (switchingToMode == EDIT_MODE_NONE) {
                cropAnimationValue = animationValue;
            } else {
                cropAnimationValue = 1.0f - animationValue;
            }
        } else {
            cropAnimationValue = currentEditMode == EDIT_MODE_FILTER || currentEditMode == EDIT_MODE_PAINT ? 0.0f : 1.0f;
        }
        float cropPw = cropTransform.getCropPw();
        float cropPh = cropTransform.getCropPh();
        bitmapWidth *= cropPw + (1.0f - cropPw) * (1.0f - cropAnimationValue);
        bitmapHeight *= cropPh + (1.0f - cropPh) * (1.0f - cropAnimationValue);
        float scaleToFitX = containerWidth / (float) bitmapWidth;
        if (scaleToFitX * bitmapHeight > containerHeight) {
            scaleToFitX = containerHeight / (float) bitmapHeight;
        }
        if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode != EDIT_MODE_CROP || switchingToMode == EDIT_MODE_NONE) && cropState != null) {
            float startW = bitmapWidth * scaleToFitX;
            float startH = bitmapHeight * scaleToFitX;
            float originalScaleToFitX = containerWidth / (float) originalWidth;
            if (originalScaleToFitX * originalHeight > containerHeight) {
                originalScaleToFitX = containerHeight / (float) originalHeight;
            }
            float finalW = originalWidth * originalScaleToFitX / currentScale;
            float finalH = originalHeight * originalScaleToFitX / currentScale;

            float w = startW + (finalW - startW) * (1.0f - cropAnimationValue);
            float h = startH + (finalH - startH) * (1.0f - cropAnimationValue);

            canvas.clipRect(-w / 2, -h / 2, w / 2, h / 2);
        }
        if (sendPhotoType == SELECT_TYPE_AVATAR || cropTransform.hasViewTransform()) {
            float cropScale;
            if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                float trueScale = 1.0f + (cropTransform.getTrueCropScale() - 1.0f) * (1.0f - cropAnimationValue);
                cropScale = cropTransform.getScale() / trueScale;
                float scaleToFit = containerWidth / (float) rotatedWidth;
                if (scaleToFit * rotatedHeight > containerHeight) {
                    scaleToFit = containerHeight / (float) rotatedHeight;
                }
                cropScale *= scaleToFit / scale;
                if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    if (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT) {
                        cropScale /= 1.0f + (cropTransform.getMinScale() - 1.0f) * (1.0f - cropAnimationValue);
                    } else if (switchingToMode == EDIT_MODE_NONE) {
                        cropScale /= cropTransform.getMinScale();
                    }
                }
            } else {
                cropScale = cropState != null ? cropState.cropScale : 1.0f;
                float trueScale = 1.0f + (cropScale - 1.0f) * (1.0f - cropAnimationValue);
                cropScale *= scaleToFitX / scale / trueScale;
            }

            canvas.translate(cropTransform.getCropAreaX() * cropAnimationValue, cropTransform.getCropAreaY() * cropAnimationValue);
            canvas.scale(cropScale, cropScale);
            canvas.translate(cropTransform.getCropPx() * rotatedWidth * scale * cropAnimationValue, cropTransform.getCropPy() * rotatedHeight * scale * cropAnimationValue);
            float rotation = (cropTransform.getRotation() + orientation);
            if (rotation > 180) {
                rotation -= 360;
            }
            if (sendPhotoType == SELECT_TYPE_AVATAR && (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT)) {
                canvas.rotate(rotation);
            } else {
                canvas.rotate(rotation * cropAnimationValue);
            }
        }
        tempInt[0] = bitmapWidth;
        tempInt[1] = bitmapHeight;
        return tempInt;
    }


    private void onActionClick(boolean download) {
        if (currentMessageObject == null && currentBotInlineResult == null && (pageBlocksAdapter == null || currentFileNames[0] == null) && sendPhotoType != SELECT_TYPE_NO_SELECT) {
            return;
        }
        ArrayList<VideoPlayer.Quality> videoUrises = null;
        Uri uri = null;
        File file = null;
        isStreaming = false;
        if (currentMessageObject != null) {
            if (currentMessageObject.messageOwner.attachPath != null && currentMessageObject.messageOwner.attachPath.length() != 0) {
                file = new File(currentMessageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file == null) {
                final TLRPC.Document original;
                final ArrayList<TLRPC.Document> alt_documents = new ArrayList<>();
                if (currentMessageObject.messageOwner != null && currentMessageObject.messageOwner.media != null && currentMessageObject.messageOwner.media.document != null) {
                    original = currentMessageObject.messageOwner.media.document;
                    alt_documents.addAll(currentMessageObject.messageOwner.media.alt_documents);
                } else {
                    original = currentMessageObject.getDocument();
                }
                if ((original != null ? 1 : 0) + alt_documents.size() <= 1) {
                    if (original != null) {
                        file = FileLoader.getInstance(currentAccount).getPathToAttach(original, false);
                    } else if (alt_documents.size() == 1) {
                        file = FileLoader.getInstance(currentAccount).getPathToAttach(alt_documents.get(0), false);
                    } else {
                        file = FileLoader.getInstance(currentAccount).getPathToMessage(currentMessageObject.messageOwner);
                    }
                    if (file == null || !file.exists()) {
                        file = null;
                        if (original != null) {
                            file = FileLoader.getInstance(currentAccount).getPathToAttach(original, true);
                        } else if (alt_documents.size() == 1) {
                            file = FileLoader.getInstance(currentAccount).getPathToAttach(alt_documents.get(0), true);
                        }
                    }
                }
                if (file == null || !file.exists()) {
                    String p = MessageHelper.getPathToMessage(currentMessageObject);
                    if (!TextUtils.isEmpty(p)) {
                        File f = new File(p);
                        if (f.exists()) {
                            file = f;
                        }
                    }
                }
                if (file == null || !file.exists()) {
                    file = null;
                    if (currentMessageObject.isVideo() && (currentMessageObject.hasVideoQualities() || SharedConfig.streamMedia) && !DialogObject.isEncryptedDialog(currentMessageObject.getDialogId()) && currentMessageObject.canStreamVideo()) {
                        final int reference = FileLoader.getInstance(currentMessageObject.currentAccount).getFileReference(currentMessageObject);

                        videoUrises = new ArrayList<>();
                        videoUrises.addAll(VideoPlayer.getQualities(currentAccount, original, alt_documents, reference, false));
                        isStreaming = true;
                        checkProgress(0, false, false);
                    }
                }
            }
        } else if (currentBotInlineResult != null) {
            if (currentBotInlineResult.document != null) {
                file = FileLoader.getInstance(currentAccount).getPathToAttach(currentBotInlineResult.document);
                if (!file.exists()) {
                    file = null;
                }
            } else if (currentBotInlineResult.content instanceof TLRPC.TL_webDocument) {
                file = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.MD5(currentBotInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(currentBotInlineResult.content.url, "mp4"));
                if (!file.exists()) {
                    file = null;
                }
            }
        } else if (pageBlocksAdapter != null) {
            TLObject media = pageBlocksAdapter.getMedia(currentIndex);
            if (!(media instanceof TLRPC.Document)) {
                return;
            }
            file = pageBlocksAdapter.getFile(currentIndex);
            if (file != null && !file.exists()) {
                uri = FileStreamLoadOperation.prepareUri(currentAccount, (TLRPC.Document) media, pageBlocksAdapter.getParentObject());
                isStreaming = true;
            }
        } else if (sendPhotoType == SELECT_TYPE_NO_SELECT) {
            if (!imagesArrLocals.isEmpty() && currentIndex >= 0 && currentIndex < imagesArrLocals.size()) {
                Object object = imagesArrLocals.get(currentIndex);
                if (object instanceof MediaController.PhotoEntry) {
                    file = new File(((MediaController.PhotoEntry) object).path);
                }
            }
        }
        if (file != null && uri == null && videoUrises == null) {
            uri = Uri.fromFile(file);
        }
        if (uri == null && videoUrises == null) {
            if (download) {
                if (currentMessageObject != null) {
                    if (!FileLoader.getInstance(currentAccount).isLoadingFile(currentFileNames[0])) {
                        FileLoader.getInstance(currentAccount).loadFile(currentMessageObject.getDocument(), currentMessageObject, FileLoader.PRIORITY_NORMAL, 0);
                    } else {
                        FileLoader.getInstance(currentAccount).cancelLoadFile(currentMessageObject.getDocument());
                    }
                } else if (currentBotInlineResult != null) {
                    if (currentBotInlineResult.document != null) {
                        if (!FileLoader.getInstance(currentAccount).isLoadingFile(currentFileNames[0])) {
                            FileLoader.getInstance(currentAccount).loadFile(currentBotInlineResult.document, currentMessageObject, FileLoader.PRIORITY_NORMAL, 0);
                        } else {
                            FileLoader.getInstance(currentAccount).cancelLoadFile(currentBotInlineResult.document);
                        }
                    } else if (currentBotInlineResult.content instanceof TLRPC.TL_webDocument) {
                        if (!ImageLoader.getInstance().isLoadingHttpFile(currentBotInlineResult.content.url)) {
                            ImageLoader.getInstance().loadHttpFile(currentBotInlineResult.content.url, "mp4", currentAccount);
                        } else {
                            ImageLoader.getInstance().cancelLoadHttpFile(currentBotInlineResult.content.url);
                        }
                    }
                } else if (pageBlocksAdapter != null) {
                    if (!FileLoader.getInstance(currentAccount).isLoadingFile(currentFileNames[0])) {
                        FileLoader.getInstance(currentAccount).loadFile((TLRPC.Document) pageBlocksAdapter.getMedia(currentIndex), pageBlocksAdapter.getParentObject(), FileLoader.PRIORITY_NORMAL, 1);
                    } else {
                        FileLoader.getInstance(currentAccount).cancelLoadFile((TLRPC.Document) pageBlocksAdapter.getMedia(currentIndex));
                    }
                }
                Drawable drawable = centerImage.getStaticThumb();
                if (drawable instanceof OtherDocumentPlaceholderDrawable) {
                    ((OtherDocumentPlaceholderDrawable) drawable).checkFileExist();
                }
            }
        } else {
            if (sharedMediaType == MediaDataController.MEDIA_FILE && currentMessageObject != null && !currentMessageObject.canPreviewDocument()) {
                AndroidUtilities.openDocument(currentMessageObject, parentActivity, null);
                return;
            }
            preparePlayer(videoUrises, uri, true, false, currentMessageObject != null && currentMessageObject.isLivePhoto());
            videoSizeSet = true;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!doubleTap && checkImageView.getVisibility() != View.VISIBLE && !drawPressedDrawable[0] && !drawPressedDrawable[1]) {
            float x = e.getX();
            int side = Math.min(135, containerView.getMeasuredWidth() / 8);
            if (x < side) {
                if (leftImage.hasImageSet()) {
                    drawPressedDrawable[0] = true;
                    containerView.invalidate();
                }
            } else if (x > containerView.getMeasuredWidth() - side) {
                if (rightImage.hasImageSet()) {
                    drawPressedDrawable[1] = true;
                    containerView.invalidate();
                }
            }
        }
        return false;
    }

    @Override
    public boolean canDoubleTap(MotionEvent e) {
        if (checkImageView.getVisibility() != View.VISIBLE && !drawPressedDrawable[0] && !drawPressedDrawable[1]) {
            float x = e.getX();
            int side = Math.min(135, containerView.getMeasuredWidth() / 8);
            if (x < side || x > containerView.getMeasuredWidth() - side) {
                return currentMessageObject == null || (currentMessageObject.isVideo() || photoViewerWebView != null && photoViewerWebView.isControllable()) && (SystemClock.elapsedRealtime() - lastPhotoSetTime) >= 500 && canDoubleTapSeekVideo(e);
            }
        }
        return true;
    }

    private void hidePressedDrawables() {
        drawPressedDrawable[0] = drawPressedDrawable[1] = false;
        containerView.invalidate();
    }

    @Override
    public void onUp(MotionEvent e) {
        hidePressedDrawables();
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (!canZoom && !doubleTapEnabled) {
            return onSingleTapConfirmed(e);
        }
        if (containerView != null && containerView.getTag() != null && photoProgressViews[0] != null) {
            float x = e.getX();
            float y = e.getY();
            boolean rez = false;
            if (x >= (getContainerViewWidth() - dp(100)) / 2.0f && x <= (getContainerViewWidth() + dp(100)) / 2.0f &&
                    y >= (getContainerViewHeight() - dp(100)) / 2.0f && y <= (getContainerViewHeight() + dp(100)) / 2.0f) {
                rez = onSingleTapConfirmed(e);
            }
            if (rez) {
                discardTap = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent ev) {

    }

    public void onLongPress() {
        if (videoPlayer != null && scale <= 1.35f) {
            long current = videoPlayer.getCurrentPosition();
            long total = videoPlayer.getDuration();
            if (current == C.TIME_UNSET || total < 8 * 1000) {
                return;
            }
            float x = longPressX;
            int width = getContainerViewWidth();
            if (total > 180 * 1000) {
                boolean forward;
                if (x >= width / 3 * 2) {
                    forward = true;
                } else if (x < width / 3) {
                    forward = false;
                } else {
                    return;
                }
                longVideoPlayerRewinder.startRewind(videoPlayer, forward, currentVideoSpeed);
            } else {
                final boolean forward = x > width / 3;
                videoPlayerRewinder.startRewind(videoPlayer, forward, longPressX, currentVideoSpeed, seekSpeedDrawable);
            }
        }
    }

    public VideoPlayerRewinder getVideoPlayerRewinder() {
        return videoPlayerRewinder;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (scale != 1 && sendPhotoType != SELECT_TYPE_STICKER) {
            scroller.abortAnimation();
            scroller.fling(Math.round(translationX), Math.round(translationY), Math.round(velocityX), Math.round(velocityY), (int) minX, (int) maxX, (int) minY, (int) maxY);
            containerView.postInvalidate();
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (discardTap) {
            return false;
        }
        float x = e.getX();
        float y = e.getY();
        if (checkImageView.getVisibility() != View.VISIBLE) {
            if (SharedConfig.nextMediaTap && sendPhotoType != SELECT_TYPE_STICKER && y > ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + dp(40)) {
                int side = Math.min(135, containerView.getMeasuredWidth() / 8);
                if (x < side) {
                    if (leftImage.hasImageSet()) {
                        switchToNextIndex(-1, true);
                        return true;
                    }
                } else if (x > containerView.getMeasuredWidth() - side) {
                    if (rightImage.hasImageSet()) {
                        switchToNextIndex(1, true);
                        return true;
                    }
                }
            }
        }
        if (currentMessageObject != null && currentMessageObject.isSponsored()) {
            if (x >= (getContainerViewWidth() - centerImage.getImageWidth()) / 2.0f && x <= (getContainerViewWidth() + centerImage.getImageWidth()) / 2.0f &&
                y >= (getContainerViewHeight() - centerImage.getImageHeight()) / 2.0f && y <= (getContainerViewHeight() + centerImage.getImageHeight()) / 2.0f) {
                if (parentFragment instanceof ChatActivity) {
                    ((ChatActivity) parentFragment).logSponsoredClicked(currentMessageObject, true, true);
                }
                closePhoto(true, false);
                if (currentMessageObject.sponsoredUrl != null) {
                    Browser.openUrl(LaunchActivity.instance != null ? LaunchActivity.instance : activityContext, Uri.parse(currentMessageObject.sponsoredUrl), true, false, false, null, null, false, MessagesController.getInstance(currentAccount).sponsoredLinksInappAllow, false);
                }
                return true;
            }
        }
        if (photoViewerWebView != null && photoViewerWebView.isControllable() && isActionBarVisible) {
            View v = photoViewerWebView.getWebView();

            if (x >= v.getX() && x <= v.getX() + v.getWidth() && y >= v.getY() && y <= v.getY() + v.getHeight()) {
                MotionEvent ev = MotionEvent.obtain(e);
                ev.setAction(MotionEvent.ACTION_DOWN);
                ev.offsetLocation(-v.getX(), -v.getY());
                v.dispatchTouchEvent(ev);
                ev.setAction(MotionEvent.ACTION_UP);
                v.dispatchTouchEvent(ev);
                ev.recycle();

                scheduleActionBarHide();
                return true;
            }
        }
        if (containerView.getTag() != null) {
            boolean drawTextureView = aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == View.VISIBLE || photoViewerWebView != null && photoViewerWebView.isControllable();

            if (sharedMediaType == MediaDataController.MEDIA_FILE && currentMessageObject != null) {
                if (!currentMessageObject.canPreviewDocument()) {
                    float vy = (getContainerViewHeight() - dp(360)) / 2.0f;
                    if (y >= vy && y <= vy + dp(360)) {
                        onActionClick(true);
                        return true;
                    }
                }
            } else {
                if (photoProgressViews[0] != null && containerView != null) {
                    int state = photoProgressViews[0].backgroundState;
                    if (x >= (getContainerViewWidth() - dp(100)) / 2.0f && x <= (getContainerViewWidth() + dp(100)) / 2.0f &&
                        y >= (getContainerViewHeight() - dp(100)) / 2.0f && y <= (getContainerViewHeight() + dp(100)) / 2.0f ||
                        centerImageIsLivePhoto
                    ) {
                        if (!drawTextureView) {
                            if (state > PROGRESS_EMPTY && state <= PROGRESS_PLAY) {
                                onActionClick(true);
                                checkProgress(0, false, true);
                                return true;
                            }
                        } else {
                            if (state == PROGRESS_PLAY || state == PROGRESS_PAUSE) {
                                if (photoProgressViews[0].isVisible()) {
                                    manuallyPaused = true;
                                    toggleVideoPlayer();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            if ((photoViewerWebView == null || !photoViewerWebView.isControllable() || photoViewerWebView.isPlaying() || !isActionBarVisible) && !(currentMessageObject != null && currentMessageObject.isSponsored())) {
                toggleActionBar(!isActionBarVisible, true);
            }
        } else if (sendPhotoType == 0 || sendPhotoType == 4) {
            if (isCurrentVideo) {
                if (videoPlayer != null && !muteVideo && sendPhotoType != SELECT_TYPE_AVATAR) {
                    videoPlayer.setVolume(1.0f);
                }
                manuallyPaused = true;
                toggleVideoPlayer();
            } else {
                checkImageView.performClick();
            }
        } else if (currentBotInlineResult != null && (currentBotInlineResult.type.equals("video") || MessageObject.isVideoDocument(currentBotInlineResult.document))) {
            int state = photoProgressViews[0].backgroundState;
            if (state > 0 && state <= 3) {
                if (x >= (getContainerViewWidth() - dp(100)) / 2.0f && x <= (getContainerViewWidth() + dp(100)) / 2.0f &&
                        y >= (getContainerViewHeight() - dp(100)) / 2.0f && y <= (getContainerViewHeight() + dp(100)) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, false, true);
                    return true;
                }
            }
        } else if (sendPhotoType == 2) {
            if (isCurrentVideo) {
                manuallyPaused = true;
                toggleVideoPlayer();
            }
        }
        return true;
    }

    private boolean canDoubleTapSeekVideo(MotionEvent e) {
        if (videoPlayer == null && !(photoViewerWebView != null && photoViewerWebView.isControllable())) {
            return false;
        }
        int width = getContainerViewWidth();
        float x = e.getX();
        boolean forward = x >= width / 3 * 2;
        long current = getCurrentVideoPosition();
        long total = getVideoDuration();
        long seekDuration = NaConfig.getDoubleTapSeekDurationMs();
        return current != C.TIME_UNSET && total > 0 && (forward ? total - current > seekDuration : seekDuration <= 9000 || current >= seekDuration - 9000);
    }

    long totalRewinding;

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if ((videoPlayer != null || photoViewerWebView != null && photoViewerWebView.isControllable()) && videoPlayerControlVisible) {
            long current = getCurrentVideoPosition();
            long total = getVideoDuration();
            float x = e.getX();
            int width = getContainerViewWidth();
            boolean forward = x >= width / 3 * 2;
            if (canDoubleTapSeekVideo(e)) {
                long old = current;
                long seekDuration = NaConfig.getDoubleTapSeekDurationMs();
                if (x >= width / 3 * 2) {
                    current += seekDuration;
                } else if (x < width / 3) {
                    current -= seekDuration;
                }
                if (old != current) {
                    boolean apply = true;
                    if (current > total) {
                        current = total;
                    } else if (current < 0) {
                        if (current < -9000) {
                            apply = false;
                        }
                        current = 0;
                    }
                    if (apply) {
                        videoForwardDrawable.setOneShootAnimation(true);
                        videoForwardDrawable.setLeftSide(x < width / 3);
                        videoForwardDrawable.addTime(seekDuration);
                        seekVideoOrWebTo(current);
                        containerView.invalidate();
                        videoPlayerSeekbar.setProgress(current / (float) total, true);
                        videoPlayerSeekbarView.invalidate();
                    }
                    return true;
                }
            }
        }
        if (!canZoom || scale == 1.0f && (translationY != 0 || translationX != 0)) {
            return false;
        }
        if (animationStartTime != 0 || animationInProgress != 0) {
            return false;
        }
        if (photoProgressViews[0] != null && photoProgressViews[0].isVisible() && photoProgressViews[0].backgroundState != PROGRESS_NONE && Math.sqrt(Math.pow(AndroidUtilities.displaySize.x / 2f - e.getX(), 2) + Math.pow((AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) / 2f - e.getY(), 2)) < dp(40)) {
            return false; // play button
        }
        if (scale == 1.0f) {
            float atx = (e.getX() - getContainerViewWidth() / 2) - ((e.getX() - getContainerViewWidth() / 2) - translationX) * (3.0f / scale);
            float aty = (e.getY() - getContainerViewHeight() / 2) - ((e.getY() - getContainerViewHeight() / 2) - translationY) * (3.0f / scale);
            updateMinMax(3.0f);
            if (atx < minX) {
                atx = minX;
            } else if (atx > maxX) {
                atx = maxX;
            }
            if (aty < minY) {
                aty = minY;
            } else if (aty > maxY) {
                aty = maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(1.0f, 0, 0, true);
        }
        doubleTap = true;
        hidePressedDrawables();
        return true;
    }

    private boolean enableSwipeToPiP() {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    // video edit start
    private QualityChooseView qualityChooseView;
    private PickerBottomLayoutViewer qualityPicker;
    private RadialProgressView progressView;
    private FrameLayout videoTimelineViewContainer;
    private VideoTimelinePlayView videoTimelineView;
    private TextView videoAvatarTooltip;
    private AnimatorSet qualityChooseViewAnimation;

    private long captureFrameAtTime = -1;
    private long captureFrameReadyAtTime = -1;
    private long needCaptureFrameReadyAtTime = -1;

    private volatile int selectedCompression;
    private volatile int compressionsCount = -1;
    private int previousCompression;

    private int rotationValue;
    private volatile int originalWidth;
    private volatile int originalHeight;
    private volatile int resultWidth;
    private volatile int resultHeight;
    private volatile int bitrate;
    private volatile int originalBitrate;
    private float videoDuration;
    private int videoFramerate;
    private volatile boolean videoConvertSupported;
    private volatile boolean isH264Video;
    private long startTime;
    private long endTime;
    private float videoCutStart;
    private float videoCutEnd;
    private long audioFramesSize;
    private long videoFramesSize;
    private long estimatedSize;
    private long estimatedDuration;
    private long originalSize;
    private long avatarStartTime;
    private float avatarStartProgress;

    private Runnable currentLoadingVideoRunnable;
    private MessageObject videoPreviewMessageObject;
    private boolean tryStartRequestPreviewOnFinish;
    private boolean loadInitialVideo;
    private boolean inPreview;
    private int previewViewEnd;
    private boolean requestingPreview;

    private String currentSubtitle;

    private class QualityChooseView extends View {

        private Paint paint;
        private TextPaint textPaint;

        private int circleSize;
        private int gapSize;
        private int sideSide;
        private int lineSize;

        private String lowQualityDescription;
        private String hightQualityDescription;

        private int startMovingQuality;

        public QualityChooseView(Context context) {
            super(context);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(dp(14));
            textPaint.setColor(0xffcdcdcd);

            lowQualityDescription = getString("AccDescrVideoCompressLow", R.string.AccDescrVideoCompressLow);
            hightQualityDescription = getString("AccDescrVideoCompressHigh", R.string.AccDescrVideoCompressHigh);

            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            setFocusable(true);
            setAccessibilityDelegate(new IntSeekBarAccessibilityDelegate() {
                @Override
                protected int getProgress() {
                    return selectedCompression;
                }

                @Override
                protected void setProgress(int progress) {
                    if (compressionsCount <= 0) {
                        return;
                    }
                    int clamped = Math.max(0, Math.min(compressionsCount - 1, progress));
                    if (clamped == selectedCompression) {
                        return;
                    }
                    startMovingQuality = selectedCompression;
                    selectedCompression = clamped;
                    didChangedCompressionLevel(false);
                    invalidate();
                    if (selectedCompression != startMovingQuality) {
                        requestVideoPreview(1);
                    }
                }

                @Override
                protected int getMaxValue() {
                    return Math.max(0, compressionsCount - 1);
                }

                @Override
                protected CharSequence getContentDescription(View host) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(getString("AccDescrVideoQuality", R.string.AccDescrVideoQuality));
                    if (compressionsCount > 0) {
                        sb.append(", ").append(selectedCompression + 1).append(" / ").append(compressionsCount);
                    }
                    sb.append(", ").append(lowQualityDescription).append(" – ").append(hightQualityDescription);
                    return sb.toString();
                }
            });
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startMovingQuality = selectedCompression;
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {

                for (int a = 0; a < compressionsCount; a++) {
                    int cx = sideSide + (lineSize + gapSize * 2 + circleSize) * a + circleSize / 2;
                    int diff = lineSize / 2 + circleSize / 2 + gapSize;
                    if (x > cx - diff && x < cx + diff) {
                        if (selectedCompression != a) {
                            selectedCompression = a;
                            didChangedCompressionLevel(false);
                            invalidate();
                        }
                        break;
                    }
                }

            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (selectedCompression != startMovingQuality) {
                    requestVideoPreview(1);
                }
                moving = false;
            }
            return true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            circleSize = dp(8);
            gapSize = dp(2);
            sideSide = dp(18);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (compressionsCount != 1) {
                lineSize = (getMeasuredWidth() - circleSize * compressionsCount - gapSize * (compressionsCount * 2 - 2) - sideSide * 2) / (compressionsCount - 1);
            } else {
                lineSize = (getMeasuredWidth() - circleSize * compressionsCount - gapSize * 2 - sideSide * 2);
            }
            int cy = getMeasuredHeight() / 2 + dp(6);
            for (int a = 0; a < compressionsCount; a++) {
                int cx = sideSide + (lineSize + gapSize * 2 + circleSize) * a + circleSize / 2;
                if (a <= selectedCompression) {
                    paint.setColor(0xff53aeef);
                } else {
                    paint.setColor(0x66ffffff);
                }

                canvas.drawCircle(cx, cy, a == selectedCompression ? dp(6) : circleSize / 2, paint);

                if (a != 0) {
                    int x = cx - circleSize / 2 - gapSize - lineSize;
                    float startPadding = a == (selectedCompression + 1) ? dpf2(2) : 0;
                    float endPadding = a == selectedCompression ? dpf2(2) : 0;
                    canvas.drawRect(x + startPadding, cy - dp(1), x + lineSize - endPadding, cy + dp(2), paint);
                }
            }

            canvas.drawText(lowQualityDescription, sideSide, cy - dp(16), textPaint);
            float width = textPaint.measureText(hightQualityDescription);
            canvas.drawText(hightQualityDescription, getMeasuredWidth() - sideSide - width, cy - dp(16), textPaint);
        }
    }

    public void updateMuteButton() {
        if (videoPlayer != null) {
            videoPlayer.setMute(CastSync.isActive() || muteVideo);
        }
        if (!videoConvertSupported) {
            muteButton.setEnabled(false);
            muteButton.setClickable(false);
            muteButton.animate().alpha(0.5f).setDuration(180).start();
            videoTimelineView.setMode(VideoTimelinePlayView.MODE_VIDEO);
        } else {
            muteButton.setEnabled(true);
            muteButton.setClickable(true);
            muteButton.animate().alpha(1f).setDuration(180).start();
            if (muteVideo) {
                if (customTitle == null) {
                    actionBarContainer.setSubtitle(getString("SoundMuted", R.string.SoundMuted));
                }
                muteDrawable.setMuted(true, true);
                if (compressItem.getTag() != null) {
                    compressItem.setAlpha(0.5f);
                    compressItem.setEnabled(false);
                }
                if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    videoTimelineView.setMaxProgressDiff(9600.0f / videoDuration);
                    videoTimelineView.setMode(VideoTimelinePlayView.MODE_AVATAR);
                    updateVideoInfo();
                } else {
                    videoTimelineView.setMaxProgressDiff(1.0f);
                    videoTimelineView.setMode(VideoTimelinePlayView.MODE_VIDEO);
                }
//                muteItem.setContentDescription(getString("NoSound", R.string.NoSound));
            } else {
                actionBarContainer.setSubtitle(currentSubtitle);
                muteDrawable.setMuted(false, true);
//                muteItem.setContentDescription(getString("Sound", R.string.Sound));
                if (compressItem.getTag() != null) {
                    compressItem.setAlpha(1.0f);
                    compressItem.setEnabled(true);
                }
                videoTimelineView.setMaxProgressDiff(1.0f);
                videoTimelineView.setMode(VideoTimelinePlayView.MODE_VIDEO);
            }
        }
    }

    private void didChangedCompressionLevel(boolean request) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(String.format("compress_video_%d", compressionsCount), selectedCompression);
        editor.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (request) {
            requestVideoPreview(1);
        }
    }

    private void calculateEstimatedVideoSize(boolean needEncoding, boolean isMute) {
        if (needEncoding) {
            estimatedSize = (long) (((isMute ? 0 : audioFramesSize) + videoFramesSize) * ((float) estimatedDuration / videoDuration));
            estimatedSize += estimatedSize / (32 * 1024) * 16;
        } else {
            estimatedSize = (long) (originalSize * ((float) estimatedDuration / videoDuration));
            if (isMute)
                estimatedSize -= (long) (audioFramesSize * ((float) estimatedDuration / videoDuration));
        }
    }

    private boolean needEncoding() {
        if (bitrate == -2) return false;
        Object mediaEntities = editState.croppedPaintPath != null
                ? (editState.croppedMediaEntities != null && !editState.croppedMediaEntities.isEmpty() ? editState.croppedMediaEntities : null)
                : (editState.mediaEntities != null && !editState.mediaEntities.isEmpty() ? editState.mediaEntities : null);
        Object paintPath = editState.croppedPaintPath != null ? editState.croppedPaintPath : editState.paintPath;
        return !isH264Video || videoCutStart != 0 || rotationValue != 0 || resultWidth != originalWidth || resultHeight != originalHeight
                || editState.cropState != null || mediaEntities != null || paintPath != null || editState.savedFilterState != null || sendPhotoType == SELECT_TYPE_AVATAR;
    }

    private void updateItemsState(boolean hide) {
        if (hide) {
            showVideoTimeline(false, true);
            videoAvatarTooltip.setVisibility(View.GONE);
            cropItem.setVisibility(View.GONE);
            cropItem.setTag(null);
            cropItem.setColorFilter(null);
            tuneItem.setVisibility(View.GONE);
            tuneItem.setTag(null);
            tuneItem.setColorFilter(null);
            paintItem.setVisibility(View.GONE);
            paintItem.setTag(null);
            paintItem.setColorFilter(null);
            AndroidUtilities.updateViewVisibilityAnimated(muteButton, false, 1f, true);
        } else {
            showVideoTimeline(true, true);
            videoAvatarTooltip.setVisibility(View.GONE);
            cropItem.setVisibility(View.VISIBLE);
            cropItem.setTag(1);
            tuneItem.setVisibility(View.VISIBLE);
            tuneItem.setTag(1);
            paintItem.setVisibility(View.VISIBLE);
            paintItem.setTag(1);
            AndroidUtilities.updateViewVisibilityAnimated(muteButton, true, 1f, true);
        }
    }

    private void updateVideoInfo() {
        if (actionBar == null) {
            return;
        }
        if (compressionsCount == 0) {
            actionBarContainer.setSubtitle(null);
            return;
        }
        if (bitrate == -2) {
            qualityPicker.originalButton.setTextColor(getThemedColor(Theme.key_chat_editMediaButton));
        } else {
            qualityPicker.originalButton.setTextColor(0xffffffff);
        }
        if (!centerImageIsLivePhoto) {
            compressItem.setState(videoConvertSupported && compressionsCount > 1, muteVideo, Math.min(resultWidth, resultHeight));
        }
        itemsLayout.requestLayout();

        estimatedDuration = (long) Math.ceil((videoTimelineView.getRightProgress() - videoTimelineView.getLeftProgress()) * videoDuration);
        videoCutStart = videoTimelineView.getLeftProgress();
        videoCutEnd = videoTimelineView.getRightProgress();

        int width = rotationValue == 90 || rotationValue == 270 ? resultHeight : resultWidth;
        int height = rotationValue == 90 || rotationValue == 270 ? resultWidth : resultHeight;

        boolean needEncoding = needEncoding();
        if (muteVideo) {
            int bitrate;
            if (sendPhotoType == SELECT_TYPE_AVATAR) {
                if (estimatedDuration <= 2000) {
                    bitrate = 2600000;
                } else if (estimatedDuration <= 5000) {
                    bitrate = 2200000;
                } else {
                    bitrate = 1560000;
                }
            } else {
                bitrate = 921600;
            }
            estimatedSize = (long) (bitrate / 8 * (estimatedDuration / 1000.0f));
            estimatedSize += estimatedSize / (32 * 1024) * 16;
        } else if (bitrate == -2) {
            estimatedSize = originalSize;
        } else {
            calculateEstimatedVideoSize(needEncoding, sendPhotoType == SELECT_TYPE_AVATAR);
        }

        if (videoCutStart == 0) {
            startTime = -1;
        } else {
            startTime = (long) (videoCutStart * videoDuration) * 1000;
        }
        if (videoCutEnd == 1) {
            endTime = -1;
        } else {
            endTime = (long) (videoCutEnd * videoDuration) * 1000;
        }

        String videoDimension = String.format("%dx%d", width, height);
        String videoTimeSize = String.format("%s, ~%s", AndroidUtilities.formatShortDuration((int) (estimatedDuration / 1000)), AndroidUtilities.formatFileSize(estimatedSize));
        currentSubtitle = String.format("%s, %s", videoDimension, videoTimeSize);
        actionBar.beginDelayedTransition();
        if (customTitle == null) {
            actionBarContainer.setSubtitle(muteVideo ? getString("SoundMuted", R.string.SoundMuted) : currentSubtitle);
        }
    }

    private void requestVideoPreview(int request) {
        if (videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(videoPreviewMessageObject);
        }
        boolean wasRequestingPreview = requestingPreview && !tryStartRequestPreviewOnFinish;
        requestingPreview = false;
        loadInitialVideo = false;
        progressView.setVisibility(View.INVISIBLE);
        if (request == 1) {
            if (resultHeight == originalHeight && resultWidth == originalWidth) {
                tryStartRequestPreviewOnFinish = false;
                photoProgressViews[0].setProgress(0, photoProgressViews[0].backgroundState == 0 || photoProgressViews[0].previousBackgroundState == 0);
                photoProgressViews[0].setBackgroundState(PROGRESS_PLAY, false, true);
                if (!wasRequestingPreview) {
                    preparePlayer(currentPlayingVideoQualityFiles, currentPlayingVideoFile, false, false, editState.savedFilterState, false, 0);
                    videoPlayer.seekTo((long) (videoTimelineView.getLeftProgress() * videoDuration));
                } else {
                    loadInitialVideo = true;
                }
            } else {
                releasePlayer(false);
                if (videoPreviewMessageObject == null) {
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.id = 0;
                    message.message = "";
                    message.media = new TLRPC.TL_messageMediaEmpty();
                    message.action = new TLRPC.TL_messageActionEmpty();
                    message.dialog_id = currentDialogId;
                    videoPreviewMessageObject = new MessageObject(UserConfig.selectedAccount, message, false, false);
                    videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), "video_preview.mp4").getAbsolutePath();
                    videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
                    videoPreviewMessageObject.videoEditedInfo.rotationValue = rotationValue;
                    videoPreviewMessageObject.videoEditedInfo.originalWidth = originalWidth;
                    videoPreviewMessageObject.videoEditedInfo.originalHeight = originalHeight;
                    videoPreviewMessageObject.videoEditedInfo.framerate = videoFramerate;
                    if (currentPlayingVideoFile == null) {
                        // TODO
                        videoPreviewMessageObject.videoEditedInfo.originalPath = currentPlayingVideoFile.getPath();
                    } else {
                        videoPreviewMessageObject.videoEditedInfo.originalPath = currentPlayingVideoFile.getPath();
                    }
                }
                long start = videoPreviewMessageObject.videoEditedInfo.startTime = startTime;
                long end = videoPreviewMessageObject.videoEditedInfo.endTime = endTime;
                if (start == -1) {
                    start = 0;
                }
                if (end == -1) {
                    end = (long) (videoDuration * 1000);
                }
                if (end - start > 5000000) {
                    videoPreviewMessageObject.videoEditedInfo.endTime = start + 5000000;
                }
                videoPreviewMessageObject.videoEditedInfo.bitrate = bitrate;
                videoPreviewMessageObject.videoEditedInfo.resultWidth = resultWidth;
                videoPreviewMessageObject.videoEditedInfo.resultHeight = resultHeight;
                videoPreviewMessageObject.videoEditedInfo.needUpdateProgress = true;
                videoPreviewMessageObject.videoEditedInfo.originalDuration = (long) (videoDuration * 1000);

                if (!MediaController.getInstance().scheduleVideoConvert(videoPreviewMessageObject, true, true, true)) {
                    tryStartRequestPreviewOnFinish = true;
                }
                requestingPreview = true;

                photoProgressViews[0].setProgress(0, photoProgressViews[0].backgroundState == 0 || photoProgressViews[0].previousBackgroundState == 0);
                photoProgressViews[0].setBackgroundState(PROGRESS_EMPTY, false, true);
            }
        } else {
            tryStartRequestPreviewOnFinish = false;
            photoProgressViews[0].setBackgroundState(PROGRESS_PLAY, false, true);
            if (request == 2) {
                preparePlayer(currentPlayingVideoQualityFiles, currentPlayingVideoFile, false, false, editState.savedFilterState, false, 0);
                videoPlayer.seekTo((long) (videoTimelineView.getLeftProgress() * videoDuration));
            }
        }
        containerView.invalidate();
    }

    private Size calculateResultVideoSize() {
        if (compressionsCount == 1) {
            return new Size(originalWidth, originalHeight);
        }
        float maxSize;
        int resultWidth;
        int resultHeight;
        switch (selectedCompression) {
            case 0:
                maxSize = 480.0f;
                break;
            case 1:
                maxSize = 854.0f;
                break;
            case 2:
                maxSize = 1280.0f;
                break;
            case 3:
            default:
                maxSize = 1920.0f;
                break;
            case 4:
                maxSize = 2560.0f;
                break;
            case 5:
                maxSize = 3840.0f;
                break;
        }
        float scale = originalWidth > originalHeight ? maxSize / originalWidth : maxSize / originalHeight;
        if (selectedCompression == compressionsCount - 1 && scale >= 1f) {
            resultWidth = originalWidth;
            resultHeight = originalHeight;
        } else {
            resultWidth = Math.round(originalWidth * scale / 2) * 2;
            resultHeight = Math.round(originalHeight * scale / 2) * 2;
        }
        if (resultWidth % 4 != 0 || resultHeight % 4 != 0) {
            resultWidth -= resultWidth % 4;
            resultWidth -= resultHeight % 4;
        }
        return new Size(resultWidth, resultHeight);
    }

    private void prepareRealEncoderBitrate() {
        if (bitrate != 0 && sendPhotoType != SELECT_TYPE_AVATAR) {
            Size resultSize = calculateResultVideoSize();
            if (resultSize.getWidth() == originalWidth && resultSize.getHeight() == originalHeight) {
                MediaController.extractRealEncoderBitrate(resultSize.getWidth(), resultSize.getHeight(), originalBitrate, false);
            } else {
                int targetBitrate = MediaController.makeVideoBitrate(originalHeight, originalWidth, originalBitrate, resultSize.getHeight(), resultSize.getWidth());
                MediaController.extractRealEncoderBitrate(resultSize.getWidth(), resultSize.getHeight(), targetBitrate, false);
            }
        }
    }

    private void updateWidthHeightBitrateForCompression() {
        if (compressionsCount <= 0) {
            return;
        }
        if (selectedCompression == -2) {
            resultWidth = originalWidth;
            resultHeight = originalHeight;
            bitrate = -2;
            return;
        }
        if (selectedCompression >= compressionsCount) {
            selectedCompression = compressionsCount - 1;
        }

        if (sendPhotoType == SELECT_TYPE_AVATAR) {
            float scale = Math.max(800.0f / originalWidth, 800.0f / originalHeight);
            resultWidth = Math.round(originalWidth * scale / 2) * 2;
            resultHeight = Math.round(originalHeight * scale / 2) * 2;
        } else {
            Size resultSize = calculateResultVideoSize();
            resultWidth = resultSize.getWidth();
            resultHeight = resultSize.getHeight();
        }

        if (bitrate != 0) {
            final int encoderBitrate;
            if (sendPhotoType == SELECT_TYPE_AVATAR) {
                bitrate = 1560000;
                encoderBitrate = bitrate;
            } else if (resultWidth == originalWidth && resultHeight == originalHeight) {
                bitrate = originalBitrate;
                encoderBitrate = MediaController.extractRealEncoderBitrate(resultWidth, resultHeight, bitrate, false);
            } else {
                bitrate = MediaController.makeVideoBitrate(originalHeight, originalWidth, originalBitrate, resultHeight, resultWidth);
                encoderBitrate = MediaController.extractRealEncoderBitrate(resultWidth, resultHeight, bitrate, false);
            }
            videoFramesSize = (long) (encoderBitrate / 8 * videoDuration / 1000);
        }
    }

    private void showQualityView(final boolean show) {
        if (show && textureUploaded && videoSizeSet && !changingTextureView && videoTextureView != null) {
            videoFrameBitmap = videoTextureView.getBitmap();
        }

        if (show) {
            previousCompression = selectedCompression;
        }
        if (qualityChooseViewAnimation != null) {
            qualityChooseViewAnimation.cancel();
        }
        qualityChooseViewAnimation = new AnimatorSet();
        if (show) {
            if (fancyShadows) {
                navigationBar.setVisibility(View.VISIBLE);
                navigationBar.setAlpha(sendPhotoType == SELECT_TYPE_STICKER ? 1f : 0f);
                navigationBar.setBackgroundColor((sendPhotoType == SELECT_TYPE_STICKER ? 0x66000000 : 0x7f000000));
            }
            qualityChooseView.setTag(1);
            qualityChooseViewAnimation.playTogether(
                    ObjectAnimator.ofFloat(pickerView, View.TRANSLATION_Y, 0, pickerView.getHeight() + captionEdit.getEditTextHeight() + (isCurrentVideo ? dp(58) : 0)),
                    ObjectAnimator.ofFloat(pickerView, View.ALPHA, 0),
                    ObjectAnimator.ofFloat(pickerViewSendButton, View.TRANSLATION_Y, 0, dp(158)),
                    ObjectAnimator.ofFloat(navigationBar, View.ALPHA, fancyShadows ? 0 : 1, 1)
            );
        } else {
            qualityChooseView.setTag(null);
            qualityChooseViewAnimation.playTogether(
                    ObjectAnimator.ofFloat(qualityChooseView, View.TRANSLATION_Y, 0, dp(166)),
                    ObjectAnimator.ofFloat(qualityPicker, View.TRANSLATION_Y, 0, dp(166)),
                    ObjectAnimator.ofFloat(navigationBar, View.ALPHA, 1, fancyShadows ? 0 : 1)
            );
        }
        qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!animation.equals(qualityChooseViewAnimation)) {
                    return;
                }
                qualityChooseViewAnimation = new AnimatorSet();
                if (show) {
                    qualityChooseView.setVisibility(View.VISIBLE);
                    qualityPicker.setVisibility(View.VISIBLE);
                    qualityChooseViewAnimation.playTogether(
                            ObjectAnimator.ofFloat(qualityChooseView, View.TRANSLATION_Y, 0),
                            ObjectAnimator.ofFloat(qualityPicker, View.TRANSLATION_Y, 0)
                    );
                } else {
                    if (fancyShadows) {
                        navigationBar.setVisibility(View.GONE);
                        navigationBar.setAlpha(0f);
                        navigationBar.setBackgroundColor((sendPhotoType == SELECT_TYPE_STICKER ? 0xFF000000 : 0x7f000000));
                    }
                    qualityChooseView.setVisibility(View.INVISIBLE);
                    qualityPicker.setVisibility(View.INVISIBLE);
                    qualityChooseViewAnimation.playTogether(
                            ObjectAnimator.ofFloat(pickerView, View.TRANSLATION_Y, 0),
                            ObjectAnimator.ofFloat(pickerView, View.ALPHA, 1),
                            ObjectAnimator.ofFloat(pickerViewSendButton, View.TRANSLATION_Y, 0)
                    );
                }
                qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(qualityChooseViewAnimation)) {
                            qualityChooseViewAnimation = null;
                        }
                    }
                });
                qualityChooseViewAnimation.setDuration(200);
                qualityChooseViewAnimation.setInterpolator(AndroidUtilities.decelerateInterpolator);
                qualityChooseViewAnimation.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                qualityChooseViewAnimation = null;
            }
        });
        qualityChooseViewAnimation.setDuration(200);
        qualityChooseViewAnimation.setInterpolator(AndroidUtilities.accelerateInterpolator);
        qualityChooseViewAnimation.start();

        if (muteButton.getVisibility() == View.VISIBLE) {
            muteButton.animate().scaleX(show ? 0.25f : 1f)
                    .scaleY(show ? 0.25f : 1f)
                    .alpha(show ? 0 : 1)
                    .setDuration(200);
        }
        if (livePhotoButton.getVisibility() == View.VISIBLE) {
            livePhotoButton.animate().scaleX(show ? 0.25f : 1f)
                    .scaleY(show ? 0.25f : 1f)
                    .alpha(show ? 0 : 1)
                    .setDuration(200);
        }
        if (editCoverButton.getVisibility() == View.VISIBLE) {
            editCoverButton.animate().scaleX(show ? 0.25f : 1f)
                    .scaleY(show ? 0.25f : 1f)
                    .alpha(show ? 0 : 1)
                    .setDuration(200);
        }
    }

    private ByteArrayInputStream cleanBuffer(byte[] data) {
        byte[] output = new byte[data.length];
        int inPos = 0;
        int outPos = 0;
        while (inPos < data.length) {
            if (data[inPos] == 0 && data[inPos + 1] == 0 && data[inPos + 2] == 3) {
                output[outPos] = 0;
                output[outPos + 1] = 0;
                inPos += 3;
                outPos += 2;
            } else {
                output[outPos] = data[inPos];
                inPos++;
                outPos++;
            }
        }
        return new ByteArrayInputStream(output, 0, outPos);
    }

    private void processOpenVideo(final String videoPath, long videoPathOffset, boolean muted, float start, float end, final int compressQuality, long livePhotoTimestampUs) {
        if (currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(currentLoadingVideoRunnable);
            currentLoadingVideoRunnable = null;
        }
        videoTimelineView.setVideoPath(videoPath, videoPathOffset, start, end, livePhotoTimestampUs);
        videoPreviewMessageObject = null;
        muteVideo = muted || sendPhotoType == SELECT_TYPE_AVATAR;

        compressionsCount = -1;
        rotationValue = 0;
        videoFramerate = 25;
        File file = new File(videoPath);
        originalSize = file.length();

        Utilities.globalQueue.postRunnable(currentLoadingVideoRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentLoadingVideoRunnable != this) {
                    return;
                }
                int videoBitrate = MediaController.getVideoBitrate(videoPath);
                int[] params = new int[AnimatedFileInfo.PARAM_NUM_COUNT];
                AnimatedFileNative.getVideoInfo(videoPath, params, videoPathOffset);

                final boolean hasAudio = params[AnimatedFileInfo.PARAM_NUM_HAS_AUDIO] != 0;
                videoConvertSupported = params[AnimatedFileInfo.PARAM_NUM_SUPPORTED_VIDEO_CODEC] != 0 &&  (!hasAudio || params[AnimatedFileInfo.PARAM_NUM_SUPPORTED_AUDIO_CODEC] != 0);
                originalBitrate = bitrate = videoBitrate == -1 ? params[AnimatedFileInfo.PARAM_NUM_BITRATE] : videoBitrate;

                if (videoConvertSupported) {
                    resultWidth = originalWidth = params[AnimatedFileInfo.PARAM_NUM_WIDTH];
                    resultHeight = originalHeight = params[AnimatedFileInfo.PARAM_NUM_HEIGHT];
                    updateCompressionsCount(originalWidth, originalHeight);
                    selectedCompression = compressQuality == -1 ? selectCompression() : compressQuality;
                    prepareRealEncoderBitrate();
                    isH264Video = MediaController.isH264Video(videoPath);
                }

                if (currentLoadingVideoRunnable != this) {
                    return;
                }
                Runnable thisFinal = this;
                AndroidUtilities.runOnUIThread(() -> {
                    if (parentActivity == null || thisFinal != currentLoadingVideoRunnable) {
                        return;
                    }
                    currentLoadingVideoRunnable = null;
                    audioFramesSize = params[AnimatedFileInfo.PARAM_NUM_AUDIO_FRAME_SIZE];
                    videoDuration = params[AnimatedFileInfo.PARAM_NUM_DURATION];
                    videoFramerate = params[AnimatedFileInfo.PARAM_NUM_FRAMERATE];
                    videoFramesSize = (long) (bitrate / 8 * videoDuration / 1000);

                    if (videoConvertSupported) {
                        rotationValue = params[AnimatedFileInfo.PARAM_NUM_ROTATION];
                        updateWidthHeightBitrateForCompression();

                        if (selectedCompression > compressionsCount - 1) {
                            selectedCompression = compressionsCount - 1;
                        }

                        if (!centerImageIsLivePhoto) {
                            compressItem.setState(compressionsCount > 1, muteVideo, Math.min(resultWidth, resultHeight));
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("compressionsCount = " + compressionsCount + " w = " + originalWidth + " h = " + originalHeight + " r = " + rotationValue);
                        }
                        qualityChooseView.invalidate();
                    } else {
                        if (!centerImageIsLivePhoto) {
                            compressItem.setState(false, muteVideo, Math.min(resultWidth, resultHeight));
                        }
                        compressionsCount = 0;
                    }

                    updateVideoInfo();
                    updateMuteButton();
                });
            }
        });
    }

    private int selectCompression() {
        //1GB
        if (originalSize > 1024L * 1024L * 1000L) {
            return compressionsCount - 1;
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        int compressionsCount = this.compressionsCount;
        int maxCompression = 5;
        while (compressionsCount < 8) {
            int selectedCompression = preferences.getInt(String.format(Locale.US, "compress_video_%d", compressionsCount), -1);
            if (selectedCompression >= 0) {
                return Math.min(selectedCompression, maxCompression);
            }
            compressionsCount++;
        }
        return Math.min(maxCompression, Math.round(DownloadController.getInstance(currentAccount).getMaxVideoBitrate() / (100f / compressionsCount)) - 1);
    }

    private void updateCompressionsCount(int h, int w) {
        int maxSize = Math.max(h, w);
        if (maxSize > 3840) {
            compressionsCount = 7;
        } else if (maxSize > 2560) {
            compressionsCount = 6;
        } else if (maxSize > 1920) {
            compressionsCount = 5;
        } else if (maxSize > 1280) {
            compressionsCount = 4;
        } else if (maxSize > 854) {
            compressionsCount = 3;
        } else if (maxSize > 640) {
            compressionsCount = 2;
        } else {
            compressionsCount = 1;
        }
    }

    private void updateAccessibilityOverlayVisibility() {
        if (playButtonAccessibilityOverlay != null) {
            final int state = photoProgressViews[0].backgroundState;
            if (photoProgressViews[0].isVisible() && (state == PROGRESS_PLAY || state == PROGRESS_PAUSE || state == PROGRESS_LOAD || state == PROGRESS_CANCEL)) {
                if (state == PROGRESS_PLAY) {
                    playButtonAccessibilityOverlay.setContentDescription(getString("AccActionPlay", R.string.AccActionPlay));
                } else if (state == PROGRESS_LOAD) {
                    playButtonAccessibilityOverlay.setContentDescription(getString("AccActionDownload", R.string.AccActionDownload));
                } else if (state == PROGRESS_CANCEL) {
                    playButtonAccessibilityOverlay.setContentDescription(getString("AccActionCancelDownload", R.string.AccActionCancelDownload));
                } else {
                    playButtonAccessibilityOverlay.setContentDescription(getString("AccActionPause", R.string.AccActionPause));
                }
                playButtonAccessibilityOverlay.setVisibility(View.VISIBLE);
            } else {
                playButtonAccessibilityOverlay.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public int getItemCount() {
            if (placeProvider != null && placeProvider.getSelectedPhotosOrder() != null) {
                return placeProvider.getSelectedPhotosOrder().size();
            }
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PhotoPickerPhotoCell cell = new PhotoPickerPhotoCell(mContext);
            cell.checkFrame.setOnClickListener(v -> {
                Object photoEntry = ((View) v.getParent()).getTag();
                int idx = imagesArrLocals.indexOf(photoEntry);
                if (idx >= 0) {
                    int num = placeProvider.setPhotoChecked(idx, getCurrentVideoEditedInfo());
                    boolean checked = placeProvider.isPhotoChecked(idx);
                    if (idx == currentIndex) {
                        checkImageView.setChecked(-1, checked, true);
                    }
                    if (num >= 0) {
                        selectedPhotosAdapter.notifyItemRemoved(num);
                        if (num == 0) {
                            selectedPhotosAdapter.notifyItemChanged(0);
                        }
                    }
                    updateSelectedCount();
                } else {
                    int num = placeProvider.setPhotoUnchecked(photoEntry);
                    if (num >= 0) {
                        selectedPhotosAdapter.notifyItemRemoved(num);
                        if (num == 0) {
                            selectedPhotosAdapter.notifyItemChanged(0);
                        }
                        updateSelectedCount();
                    }
                }
            });
            return new RecyclerListView.Holder(cell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) holder.itemView;
            cell.setItemWidth(dp(85), position != 0 ? dp(6) : 0);
            BackupImageView imageView = cell.imageView;
            boolean showing;
            imageView.setOrientation(0, true);
            ArrayList<Object> order = placeProvider.getSelectedPhotosOrder();
            Object object = placeProvider.getSelectedPhotos().get(order.get(position));
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                cell.setTag(photoEntry);
                cell.videoInfoContainer.setVisibility(View.INVISIBLE);
                if (photoEntry.thumbPath != null) {
                    imageView.setImage(photoEntry.thumbPath, null, mContext.getResources().getDrawable(R.drawable.nophotos));
                } else if (photoEntry.path != null) {
                    imageView.setOrientation(photoEntry.orientation, photoEntry.invert, true);
                    if (photoEntry.isVideo && !photoEntry.isLivePhoto()) {
                        cell.videoInfoContainer.setVisibility(View.VISIBLE);
                        cell.videoTextView.setText(AndroidUtilities.formatShortDuration(photoEntry.duration));
                        imageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, mContext.getResources().getDrawable(R.drawable.nophotos));
                    } else {
                        imageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, mContext.getResources().getDrawable(R.drawable.nophotos));
                    }
                } else {
                    imageView.setImageResource(R.drawable.nophotos);
                }
                cell.setChecked(-1, true, false);
                cell.checkBox.setVisibility(View.VISIBLE);
            } else if (object instanceof MediaController.SearchImage) {
                MediaController.SearchImage photoEntry = (MediaController.SearchImage) object;
                cell.setTag(photoEntry);
                cell.setImage(photoEntry);
                cell.videoInfoContainer.setVisibility(View.INVISIBLE);
                cell.setChecked(-1, true, false);
                cell.checkBox.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }
    }

    private class FirstFrameView extends ImageView {
        public FirstFrameView(Context context) {
            super(context);
            setAlpha(0f);
        }

        public void clear() {
            hasFrame = false;
            gotError = false;
            if (gettingFrame) {
                gettingFrameIndex++;
                gettingFrame = false;
            }
            setImageResource(android.R.color.transparent);
        }

        private int gettingFrameIndex = 0;
        private boolean gettingFrame = false;
        private boolean hasFrame = false;
        private boolean gotError = false;
        private VideoPlayer currentVideoPlayer;
        public void checkFromPlayer(VideoPlayer videoPlayer) {
            if (currentVideoPlayer != videoPlayer) {
                gotError = false;
                clear();
            }

            if (videoPlayer != null && !videoPlayer.isHDR()) {
                long timeToEnd = videoPlayer.getDuration() - videoPlayer.getCurrentPosition();
                if (!hasFrame && !gotError && !gettingFrame && timeToEnd < 1000 * 5 + fadeDuration) { // 5 seconds to get the first frame
                    final Uri uri = videoPlayer.getCurrentUri();
                    final int index = ++gettingFrameIndex;
                    Utilities.globalQueue.postRunnable(() -> {
                        try {
                            final AnimatedFileDrawable drawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0, 0, null, null, null, 0, UserConfig.selectedAccount, false, AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y, null);
                            final Bitmap bitmap = drawable.getFrameAtTime(0);
                            drawable.recycle();
                            AndroidUtilities.runOnUIThread(() -> {
                                if (index == gettingFrameIndex) {
                                    setImageBitmap(bitmap);
                                    hasFrame = true;
                                    gettingFrame = false;
                                }
                            });
                        } catch (Throwable e) {
                            FileLog.e(e);
                            AndroidUtilities.runOnUIThread(() -> {
                                gotError = true;
                            });
                        }
                    });
                    gettingFrame = true;
                }
            }

            currentVideoPlayer = videoPlayer;
        }

        public boolean containsFrame() {
            return hasFrame;
        }

        public final static float fadeDuration = 250;
        private final TimeInterpolator fadeInterpolator = CubicBezierInterpolator.EASE_IN;

        private ValueAnimator fadeAnimator;
        private void updateAlpha() {
            if (videoPlayer == null || videoPlayer.getDuration() == C.TIME_UNSET) {
                if (fadeAnimator != null) {
                    fadeAnimator.cancel();
                    fadeAnimator = null;
                }
                setAlpha(0f);
                return;
            }
            long toDuration = Math.max(0, videoPlayer.getDuration() - videoPlayer.getCurrentPosition());
            float alpha = 1f - Math.max(Math.min(toDuration / fadeDuration, 1), 0);
            if (alpha <= 0) {
                if (fadeAnimator != null) {
                    fadeAnimator.cancel();
                    fadeAnimator = null;
                }
                setAlpha(0f);
            } else if (videoPlayer.isPlaying()) {
                if (fadeAnimator == null) {
                    fadeAnimator = ValueAnimator.ofFloat(alpha, 1f);
                    fadeAnimator.addUpdateListener(a -> {
                        setAlpha((float) a.getAnimatedValue());
                    });
                    fadeAnimator.setDuration(toDuration);
                    fadeAnimator.setInterpolator(fadeInterpolator);
                    fadeAnimator.start();
                    setAlpha(alpha);
                }
            } else {
                if (fadeAnimator != null) {
                    fadeAnimator.cancel();
                    fadeAnimator = null;
                }
                setAlpha(alpha);
            }
        }
    }

    private int getThemedColor(int key) {
        if (resourcesProvider != null) {
            return resourcesProvider.getColor(key);
        }
        return Theme.getColor(key);
    }

    private final AnimatedFloat blurAlpha = new AnimatedFloat(this::invalidateBlur, 180, CubicBezierInterpolator.EASE_OUT);
    private AnimatedFloat[] centerImageInsideBlur;
    private RectF blurBounds;
    private RectF imageBounds;
    private Matrix imageBoundsMatrix;
    private float[] imageBoundsPoints;

    private RenderNode renderNode;
    private RenderNode renderNodeBlurred;
    private RenderNode renderNodeGlassed;
    private final Blur3HashImpl renderNodeHashBuilder = new Blur3HashImpl();
    private final LongSparseArray<RenderNode> matrixRenderNodes = new LongSparseArray<>();

    public boolean BLUR_RENDERNODE() {
        return !textureViewSkipRender
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && SharedConfig.useNewBlur
            && SharedConfig.getDevicePerformanceClass() >= SharedConfig.PERFORMANCE_CLASS_HIGH
            && !AndroidUtilities.makingGlobalBlurBitmap;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    private RenderNode getRenderNodeBlurred(boolean glass) {
        if (renderNode == null) {
            return null;
        }

        final float scale = glass ? 3 : 6;
        RenderNode renderNodeT = glass ? renderNodeGlassed : renderNodeBlurred;

        if (renderNodeT == null) {
            renderNodeT = new RenderNode("pv_s_blur_" + glass);
            final float r = DownscaleScrollableNoiseSuppressor.downscaleRadius(dp(glass ? 1.66f : 40), scale);
            renderNodeT.setRenderEffect(RenderEffect.createBlurEffect(r, r, Shader.TileMode.CLAMP));
            if (glass) {
                renderNodeGlassed = renderNodeT;
            } else {
                renderNodeBlurred = renderNodeT;
            }
        }

        final int w = (int) Math.ceil(renderNode.getWidth() / scale);
        final int h = (int) Math.ceil(renderNode.getHeight() / scale);

        boolean needUpdateDisplayList = !renderNodeT.hasDisplayList();
        needUpdateDisplayList |= renderNodeT.setPosition(0, 0, w, h);

        if (needUpdateDisplayList) {
            Canvas c = renderNodeT.beginRecording();
            c.save();
            c.scale((float) w / renderNode.getWidth(), (float) h / renderNode.getHeight());
            c.drawRenderNode(renderNode);
            c.restore();
            renderNodeT.endRecording();
        }

        return renderNodeT;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private RenderNode getRenderNodeColorMatrix(ColorMatrix colorMatrix, boolean glass) {
        RenderNode renderNodeBlurred = getRenderNodeBlurred(glass);
        if (renderNodeBlurred == null) {
            return null;
        }

        renderNodeHashBuilder.start();
        renderNodeHashBuilder.add(colorMatrix);
        renderNodeHashBuilder.add(glass);
        final long hash = renderNodeHashBuilder.get();
        RenderNode renderNode = matrixRenderNodes.get(hash);
        if (renderNode == null) {
            renderNode = new RenderNode("pv_mat_" + hash + "_" + glass);
            renderNode.setRenderEffect(RenderEffect.createColorFilterEffect(new ColorMatrixColorFilter(colorMatrix)));
        }

        boolean needUpdateDisplayList = !renderNode.hasDisplayList();
        needUpdateDisplayList |= renderNode.setPosition(0, 0, renderNodeBlurred.getWidth(), renderNodeBlurred.getHeight());

        if (needUpdateDisplayList) {
            Canvas c = renderNode.beginRecording();
            c.drawRenderNode(renderNodeBlurred);
            renderNode.endRecording();
        }
        return renderNode;
    }

    public void drawCaptionBlur(Canvas canvas, BlurringShader.StoryBlurDrawer drawer, int bgColor, int overlayColor, boolean clip, boolean allowTransparent, boolean allowCrossfade) {
        drawCaptionBlur(canvas, drawer, bgColor, overlayColor, clip, allowTransparent, allowCrossfade, false);
    }

    public void drawCaptionBlur(Canvas canvas, BlurringShader.StoryBlurDrawer drawer, int bgColor, int overlayColor, boolean clip, boolean allowTransparent, boolean allowCrossfade, boolean glass) {
        if (BLUR_RENDERNODE()) {
            final RenderNode renderNode = getRenderNodeColorMatrix(drawer.colorMatrix, glass);
            final int mBgColor = AndroidUtilities.applyColorMatrix(bgColor, drawer.colorMatrix);

            if (this.renderNode != null && renderNode != null && canvas.isHardwareAccelerated()) {
                canvas.drawColor(mBgColor);
                canvas.save();
                canvas.scale(
                        (float) this.renderNode.getWidth() / renderNode.getWidth(),
                        (float) this.renderNode.getHeight() / renderNode.getHeight());
                canvas.drawRenderNode(renderNode);
                canvas.restore();
                canvas.drawColor(overlayColor);
            } else {
                canvas.drawColor(ColorUtils.compositeColors(overlayColor, mBgColor));
            }
            return;
        }
        float maxAlpha = !SharedConfig.photoViewerBlur ? 1f : blurAlpha.set(animationInProgress == 0 || animationInProgress == 2 || animationInProgress == 3);

        drawer.paint.setShader(null);
        if (bgColor != 0) {
            drawer.paint.setColor(bgColor);
            drawer.paint.setAlpha((int) (drawer.paint.getAlpha() * lerp(.7f, 1f, allowTransparent ? maxAlpha : 1f)));
            canvas.drawPaint(drawer.paint);
        }

        if (!SharedConfig.photoViewerBlur || animationInProgress != 0) {
            blurAlpha.set(0, true);
            if (overlayColor != 0) {
                drawer.paint.setColor(overlayColor);
                drawer.paint.setAlpha((int) (drawer.paint.getAlpha() * lerp(.7f, 1f, allowTransparent ? maxAlpha : 1f)));
                canvas.drawPaint(drawer.paint);
            }
            return;
        }

        if (allowCrossfade) {
            if (centerImageInsideBlur == null) {
                centerImageInsideBlur = new AnimatedFloat[3];
                centerImageInsideBlur[0] = new AnimatedFloat(this::invalidateBlur, 180, CubicBezierInterpolator.EASE_OUT); // right
                centerImageInsideBlur[1] = new AnimatedFloat(this::invalidateBlur, 180, CubicBezierInterpolator.EASE_OUT); // center
                centerImageInsideBlur[2] = new AnimatedFloat(this::invalidateBlur, 180, CubicBezierInterpolator.EASE_OUT); // left
            }
            // TODO: support left and right crossfades:
            centerImageInsideBlur[0].set(1f, true);
            centerImageInsideBlur[2].set(1f, true);
            if (blurBounds == null) {
                blurBounds = new RectF();
            }
            if (imageBounds == null) {
                imageBounds = new RectF();
            }
            if (imageBoundsMatrix == null) {
                imageBoundsMatrix = new Matrix();
            }
            if (imageBoundsPoints == null) {
                imageBoundsPoints = new float[8];
            }
            blurBounds.set(captionEdit.getBounds());
            blurBounds.offset(captionEditContainer.getX(), captionEditContainer.getY());
            blurBounds.offset(captionEdit.getX(), captionEdit.getY());
            imageBoundsMatrix.reset();
        }

        final int restoreCount = canvas.getSaveCount();
        if (padImageForHorizontalInsets) {
            canvas.save();
            canvas.translate(getLeftInset() / 2 - getRightInset() / 2, 0);
            if (allowCrossfade) {
                imageBoundsMatrix.preTranslate(getLeftInset() / 2 - getRightInset() / 2, 0);
            }
        }

        float currentTranslationY;
        float currentTranslationX;
        float currentScale;
        float currentRotation;
        float currentMirror;
        if (imageMoveAnimation != null) {
            currentMirror = lerp(mirror, animateToMirror, animationValue);
            currentScale = lerp(scale, animateToScale, animationValue);
            currentRotation = lerp(rotate, animateToRotate, animationValue);
            currentTranslationY = lerp(translationY, animateToY, animationValue);
            currentTranslationX = lerp(translationX, animateToX, animationValue);
        } else {
            currentScale = scale;
            currentMirror = mirror;
            currentRotation = rotate;
            currentTranslationY = translationY;
            currentTranslationX = translationX;
            if (animationStartTime != 0) {
                currentTranslationX = animateToX;
                currentTranslationY = animateToY;
                currentScale = animateToScale;
            }
        }

        int containerWidth = getContainerViewWidth();
        int containerHeight = getContainerViewHeight();
        ImageReceiver sideImage = null;
        if (currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR) {
            if (scale >= 1.0f && !zoomAnimation && !zooming) {
                if (currentTranslationX > maxX + dp(5)) {
                    sideImage = leftImage;
                } else if (currentTranslationX < minX - dp(5)) {
                    sideImage = rightImage;
                }
            }
        }
        if (sideImage == rightImage) {
            float rightMaxAlpha = allowCrossfade ? centerImageInsideBlur[0].set(1f, true) : 1f;
            float translateX = currentTranslationX;
            float scaleDiff = 0;
            float alpha = 1;
            if (!zoomAnimation && translateX < minX) {
                alpha = Math.min(1.0f, (minX - translateX) / containerWidth);
                scaleDiff = (1.0f - alpha) * 0.3f;
                translateX = -containerWidth - dp(30) / 2;
            }

            if (sideImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate(containerWidth / 2, containerHeight / 2);
                canvas.translate(containerWidth + dp(30) / 2 + translateX, 0);
                canvas.scale(1.0f - scaleDiff, 1.0f - scaleDiff);
                int bitmapWidth = sideImage.getBitmapWidth();
                int bitmapHeight = sideImage.getBitmapHeight();
                if (!rightImageIsVideo && rightCropState != null && rightCropTransform.hasViewTransform()) {
                    applyCrop(canvas, containerWidth, containerHeight, bitmapWidth, bitmapHeight, 1f, rightCropTransform, rightCropState);
                }
                float scaleX = containerWidth / (float) bitmapWidth;
                float scaleY = containerHeight / (float) bitmapHeight;
                float scale = Math.min(scaleX, scaleY);
                int width = (int) (bitmapWidth * scale);
                int height = (int) (bitmapHeight * scale);
                boolean mirror = false;
                if (!imagesArrLocals.isEmpty()) {
                    if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                        mirror = rightCropTransform.isMirrored();
                    } else {
                        mirror = rightCropState != null && rightCropState.mirrored;
                    }
                }
                if (mirror) {
                    canvas.scale(-1, 1);
                }

                float p = 1.5f;
                Bitmap blurBitmap = rightBlur.getBitmap(sideImage);
                if (blurBitmap != null) {
                    drawer.paint.setShader(null);
                    drawer.paint.setAlpha((int) (0xFF * alpha * maxAlpha * rightMaxAlpha));
                    canvas.scale((float) blurBitmap.getWidth() / (blurBitmap.getWidth() - 2 * p), (float) blurBitmap.getHeight() / (blurBitmap.getHeight() - 2 * p));
                    canvas.translate(-width / 2, -height / 2);
                    canvas.scale(1f * width / blurBitmap.getWidth(), 1f * height / blurBitmap.getHeight());
                    if (clip) {
                        int pp = (int) (p - 1.5f);
                        canvas.clipRect(pp, pp, blurBitmap.getWidth() - pp, blurBitmap.getHeight() - pp);
                    }
                    canvas.drawBitmap(blurBitmap, 0, 0, drawer.paint);
                }

                canvas.restore();
            }
        }

        float translateX = currentTranslationX;
        float scaleDiff = 0;
        float alpha = 1;
        if (!zoomAnimation && translateX > maxX && currentEditMode == EDIT_MODE_NONE && sendPhotoType != SELECT_TYPE_AVATAR) {
            alpha = Math.min(1.0f, (translateX - maxX) / containerWidth);
            scaleDiff = alpha * 0.3f;
            alpha = 1.0f - alpha;
            translateX = maxX;
        }
        boolean drawnCenterImage = false;
        boolean drawTextureView = videoSizeSet && aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == View.VISIBLE;
        if (centerImage.hasBitmapImage() || drawTextureView && textureUploaded) {
            canvas.save();
            canvas.translate(containerWidth / 2 + getAdditionX(currentEditMode), containerHeight / 2 + getAdditionY(currentEditMode));
            canvas.translate(translateX, currentTranslationY + (currentEditMode != EDIT_MODE_PAINT ? currentPanTranslationY : 0));
            canvas.scale(currentScale - scaleDiff, currentScale - scaleDiff);
            canvas.rotate(currentRotation);
            if (allowCrossfade) {
                imageBoundsMatrix.preTranslate(containerWidth / 2 + getAdditionX(currentEditMode), containerHeight / 2 + getAdditionY(currentEditMode));
                imageBoundsMatrix.preTranslate(translateX, currentTranslationY + (currentEditMode != EDIT_MODE_PAINT ? currentPanTranslationY : 0));
                imageBoundsMatrix.preScale(currentScale - scaleDiff, currentScale - scaleDiff, 0, 0);
                imageBoundsMatrix.preRotate(currentRotation);
            }
            if (currentEditMode == EDIT_MODE_PAINT && photoPaintView != null) {
                int trueH = getContainerViewHeight(true, 0);
                trueH -= photoPaintView.getEmojiPadding(Math.abs(AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight - trueH) < dp(20));
                int h = getContainerViewHeight(false, 0);
                canvas.translate(0, (trueH - h) / 2f * (1f - photoPaintView.adjustPanLayoutHelperProgress()));
                if (allowCrossfade) {
                    imageBoundsMatrix.preTranslate(0, (trueH - h) / 2f * (1f - photoPaintView.adjustPanLayoutHelperProgress()));
                }
            }

            int bitmapWidth, originalWidth;
            int bitmapHeight, originalHeight;
            if (drawTextureView && textureUploaded && videoSizeSet) {
                View view = usedSurfaceView ? videoSurfaceView : videoTextureView;
                originalWidth = bitmapWidth = view.getMeasuredWidth();
                originalHeight = bitmapHeight = view.getMeasuredHeight();
            } else {
                originalWidth = bitmapWidth = centerImage.getBitmapWidth();
                originalHeight = bitmapHeight = centerImage.getBitmapHeight();
            }

            float scale = Math.min(containerWidth / (float) originalWidth, containerHeight / (float) originalHeight);
            int width = (int) (originalWidth * scale);
            int height = (int) (originalHeight * scale);
            float W = width, H = height;
            if (!pipAnimationInProgress && (!drawTextureView || !textureUploaded && !videoSizeSet || !videoCrossfadeStarted || videoCrossfadeAlpha != 1.0f)) {
                if (!(videoFrameBitmap != null && isCurrentVideo)) {
                    W = centerImage.getBitmapWidth();
                    H = centerImage.getBitmapHeight();
                    float S;
                    if (isCurrentVideo && currentEditMode == EDIT_MODE_NONE && sendPhotoType == SELECT_TYPE_AVATAR) {
                        S = getCropFillScale(false);
                    } else {
                        S = Math.min(containerWidth / W, containerHeight / H);
                    }
                    W *= S;
                    H *= S;
                    if (isCurrentVideo) {
                        float centerMaxAlpha = 1f;
                        if (allowCrossfade) {
                            imageBoundsMatrix.preTranslate(-W / 2, -H / 2);
                            imageBoundsPoints[0] = 0;
                            imageBoundsPoints[1] = 0;
                            imageBoundsPoints[2] = W;
                            imageBoundsPoints[3] = 0;
                            imageBoundsPoints[4] = W;
                            imageBoundsPoints[5] = H;
                            imageBoundsPoints[6] = 0;
                            imageBoundsPoints[7] = H;
                            imageBoundsMatrix.mapPoints(imageBoundsPoints);
                            imageBounds.set(
                                    Math.min(Math.min(imageBoundsPoints[0], imageBoundsPoints[2]), Math.min(imageBoundsPoints[4], imageBoundsPoints[6])),
                                    Math.min(Math.min(imageBoundsPoints[1], imageBoundsPoints[3]), Math.min(imageBoundsPoints[5], imageBoundsPoints[7])),
                                    Math.max(Math.max(imageBoundsPoints[0], imageBoundsPoints[2]), Math.max(imageBoundsPoints[4], imageBoundsPoints[6])),
                                    Math.max(Math.max(imageBoundsPoints[1], imageBoundsPoints[3]), Math.max(imageBoundsPoints[5], imageBoundsPoints[7]))
                            );
                            centerMaxAlpha = centerImageInsideBlur[1].set(blurBounds.intersect(imageBounds));
                        }

                        float p = 1.5f;
                        Bitmap blurBitmap = null;
                        if (videoCrossfadeAlpha < 1) {
                            blurBitmap = centerBlur.getBitmap(centerImage);
                            if (blurBitmap != null) {
                                canvas.save();
                                drawer.paint.setShader(null);
                                drawer.paint.setAlpha((int) (0xFF * centerMaxAlpha * alpha * (1f - videoCrossfadeAlpha) * maxAlpha));
                                canvas.scale((float) blurBitmap.getWidth() / (blurBitmap.getWidth() - 2 * p), (float) blurBitmap.getHeight() / (blurBitmap.getHeight() - 2 * p));
                                canvas.translate(-W / 2, -H / 2);
                                canvas.scale(1f * W / blurBitmap.getWidth(), 1f * H / blurBitmap.getHeight());
                                if (clip) {
                                    int pp = (int) (p - 1.5f);
                                    canvas.clipRect(pp, pp, blurBitmap.getWidth() - pp, blurBitmap.getHeight() - pp);
                                }
                                canvas.drawBitmap(blurBitmap, 0, 0, drawer.paint);
                                canvas.restore();
                            }
                        }
                        if (videoCrossfadeAlpha > 0) {
                            blurBitmap = blurManager.getBitmap();
                            if (blurBitmap != null) {
                                canvas.save();
                                drawer.paint.setShader(null);
                                drawer.paint.setAlpha((int) (0xFF * centerMaxAlpha * alpha * videoCrossfadeAlpha * maxAlpha));
                                canvas.scale((float) blurBitmap.getWidth() / (blurBitmap.getWidth() - 2 * p), (float) blurBitmap.getHeight() / (blurBitmap.getHeight() - 2 * p));
                                canvas.translate(-W / 2, -H / 2);
                                canvas.scale(1f * W / blurBitmap.getWidth(), 1f * H / blurBitmap.getHeight());
                                if (clip) {
                                    int pp = (int) (p - 1.5f);
                                    canvas.clipRect(pp, pp, blurBitmap.getWidth() - pp, blurBitmap.getHeight() - pp);
                                }
                                canvas.drawBitmap(blurBitmap, 0, 0, drawer.paint);
                                canvas.restore();
                            }
                        }
                        drawnCenterImage = true;
                    }
                }
            }

            boolean applyCrop;
            float scaleToFitX = 1.0f;
            if (!imagesArrLocals.isEmpty()) {
                if (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT) {
                    applyCrop = true;
                } else if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    applyCrop = (switchingToMode == EDIT_MODE_NONE || currentEditMode != EDIT_MODE_PAINT && currentEditMode != EDIT_MODE_FILTER);
                } else {
                    applyCrop = imageMoveAnimation != null && switchingToMode != -1 || currentEditMode == EDIT_MODE_NONE || currentEditMode == EDIT_MODE_CROP || switchingToMode != -1;
                }
            } else {
                applyCrop = false;
            }
            if (applyCrop) {
                int rotatedWidth = originalWidth;
                int rotatedHeight = originalHeight;
                int orientation = cropTransform.getOrientation();
                if (orientation == 90 || orientation == 270) {
                    int temp = bitmapWidth;
                    bitmapWidth = bitmapHeight;
                    bitmapHeight = temp;

                    temp = rotatedWidth;
                    rotatedWidth = rotatedHeight;
                    rotatedHeight = temp;
                }
                float cropAnimationValue;
                if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT)) {
                    cropAnimationValue = 1.0f;
                } else if (imageMoveAnimation != null && switchingToMode != -1) {
                    if (currentEditMode == EDIT_MODE_CROP || switchingToMode == EDIT_MODE_CROP || (currentEditMode == EDIT_MODE_FILTER || currentEditMode == EDIT_MODE_PAINT) && switchingToMode == -1) {
                        cropAnimationValue = 1.0f;
                    } else if (switchingToMode == EDIT_MODE_NONE) {
                        cropAnimationValue = animationValue;
                    } else {
                        cropAnimationValue = 1.0f - animationValue;
                    }
                } else {
                    cropAnimationValue = currentEditMode == EDIT_MODE_FILTER || currentEditMode == EDIT_MODE_PAINT ? 0.0f : 1.0f;
                }
                float cropPw = cropTransform.getCropPw();
                float cropPh = cropTransform.getCropPh();
                bitmapWidth *= cropPw + (1.0f - cropPw) * (1.0f - cropAnimationValue);
                bitmapHeight *= cropPh + (1.0f - cropPh) * (1.0f - cropAnimationValue);
                scaleToFitX = containerWidth / (float) bitmapWidth;
                if (scaleToFitX * bitmapHeight > containerHeight) {
                    scaleToFitX = containerHeight / (float) bitmapHeight;
                }
                if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode != 1 || switchingToMode == EDIT_MODE_NONE) && editState.cropState != null) {
                    float startW = bitmapWidth * scaleToFitX;
                    float startH = bitmapHeight * scaleToFitX;
                    float originalScaleToFitX = containerWidth / (float) originalWidth;
                    if (originalScaleToFitX * originalHeight > containerHeight) {
                        originalScaleToFitX = containerHeight / (float) originalHeight;
                    }
                    float finalW = originalWidth * originalScaleToFitX / (currentScale - scaleDiff);
                    float finalH = originalHeight * originalScaleToFitX / (currentScale - scaleDiff);

                    float w = startW + (finalW - startW) * (1.0f - cropAnimationValue);
                    float h = startH + (finalH - startH) * (1.0f - cropAnimationValue);

                    canvas.clipRect(-w / 2, -h / 2, w / 2, h / 2);
                }
                if (sendPhotoType == SELECT_TYPE_AVATAR || cropTransform.hasViewTransform()) {
                    float cropScale;
                    if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                        if (videoTextureView != null) {
                            videoTextureView.setScaleX(cropTransform.isMirrored() ? -1.0f : 1.0f);
                            if (firstFrameView != null) {
                                firstFrameView.setScaleX(videoTextureView.getScaleX());
                            }
                        }
                        float trueScale = 1.0f + (cropTransform.getTrueCropScale() - 1.0f) * (1.0f - cropAnimationValue);
                        cropScale = cropTransform.getScale() / trueScale;
                        float scaleToFit = containerWidth / (float) rotatedWidth;
                        if (scaleToFit * rotatedHeight > containerHeight) {
                            scaleToFit = containerHeight / (float) rotatedHeight;
                        }
                        cropScale *= scaleToFit / scale;
                        if (sendPhotoType == SELECT_TYPE_AVATAR) {
                            if (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT) {
                                cropScale /= 1.0f + (cropTransform.getMinScale() - 1.0f) * (1.0f - cropAnimationValue);
                            } else if (switchingToMode == EDIT_MODE_NONE) {
                                cropScale /= cropTransform.getMinScale();
                            }
                        }
                    } else {
                        if (videoTextureView != null) {
                            videoTextureView.setScaleX(editState.cropState != null && editState.cropState.mirrored ? -1.0f : 1.0f);
                            if (firstFrameView != null) {
                                firstFrameView.setScaleX(videoTextureView.getScaleX());
                            }
                        }
                        cropScale = editState.cropState != null ? editState.cropState.cropScale : 1.0f;
                        float trueScale = 1.0f + (cropScale - 1.0f) * (1.0f - cropAnimationValue);
                        cropScale *= scaleToFitX / scale / trueScale;
                    }

                    canvas.translate(cropTransform.getCropAreaX() * cropAnimationValue, cropTransform.getCropAreaY() * cropAnimationValue);
                    canvas.scale(cropScale, cropScale);
                    canvas.translate(cropTransform.getCropPx() * rotatedWidth * scale * cropAnimationValue, cropTransform.getCropPy() * rotatedHeight * scale * cropAnimationValue);
                    if (allowCrossfade) {
                        imageBoundsMatrix.preTranslate(cropTransform.getCropAreaX() * cropAnimationValue, cropTransform.getCropAreaY() * cropAnimationValue);
                        imageBoundsMatrix.preScale(cropScale, cropScale);
                        imageBoundsMatrix.preTranslate(cropTransform.getCropPx() * rotatedWidth * scale * cropAnimationValue, cropTransform.getCropPy() * rotatedHeight * scale * cropAnimationValue);
                    }
                    float rotation = (cropTransform.getRotation() + orientation);
                    if (rotation > 180) {
                        rotation -= 360;
                    }
                    if (sendPhotoType == SELECT_TYPE_AVATAR && (currentEditMode == EDIT_MODE_PAINT || switchingToMode == EDIT_MODE_PAINT)) {
                        canvas.rotate(rotation);
                        if (allowCrossfade) {
                            imageBoundsMatrix.preRotate(rotation);
                        }
                    } else {
                        canvas.rotate(rotation * cropAnimationValue);
                        if (allowCrossfade) {
                            imageBoundsMatrix.preRotate(rotation * cropAnimationValue);
                        }
                    }
                }
            }

            if (!drawnCenterImage) {
                boolean mirror = false;
                if (!imagesArrLocals.isEmpty()) {
                    if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                        mirror = cropTransform.isMirrored();
                    } else {
                        mirror = editState.cropState != null && editState.cropState.mirrored;
                    }
                }
                boolean restore = false;
                if (mirror) {
                    canvas.save();
                    canvas.scale(-1, 1);
                    restore = true;
                }
                if (currentMirror > 0) {
                    if (!restore) {
                        canvas.save();
                        restore = true;
                    }
                    canvas.scale(1 - currentMirror * 2, 1f);
                    canvas.skew(0, 4 * currentMirror * (1f - currentMirror) * .25f);
                }
                if (photoViewerWebView == null || !photoViewerWebView.isLoaded()) {
                    float centerMaxAlpha = 1f;
                    if (allowCrossfade) {
                        imageBoundsMatrix.preTranslate(-W / 2, -H / 2);
                        imageBoundsPoints[0] = 0;
                        imageBoundsPoints[1] = 0;
                        imageBoundsPoints[2] = W;
                        imageBoundsPoints[3] = 0;
                        imageBoundsPoints[4] = W;
                        imageBoundsPoints[5] = H;
                        imageBoundsPoints[6] = 0;
                        imageBoundsPoints[7] = H;
                        imageBoundsMatrix.mapPoints(imageBoundsPoints);
                        imageBounds.set(
                            Math.min(Math.min(imageBoundsPoints[0], imageBoundsPoints[2]), Math.min(imageBoundsPoints[4], imageBoundsPoints[6])),
                            Math.min(Math.min(imageBoundsPoints[1], imageBoundsPoints[3]), Math.min(imageBoundsPoints[5], imageBoundsPoints[7])),
                            Math.max(Math.max(imageBoundsPoints[0], imageBoundsPoints[2]), Math.max(imageBoundsPoints[4], imageBoundsPoints[6])),
                            Math.max(Math.max(imageBoundsPoints[1], imageBoundsPoints[3]), Math.max(imageBoundsPoints[5], imageBoundsPoints[7]))
                        );
                        centerMaxAlpha = centerImageInsideBlur[1].set(blurBounds.intersect(imageBounds));
                    }

                    float p = 1.5f;
                    Bitmap blurBitmap = blurManager.getBitmap();
                    if (blurBitmap == null) {
                        blurBitmap = centerBlur.getBitmap(centerImage);
                    }
                    if (blurBitmap != null) {
                        drawer.paint.setShader(null);
                        drawer.paint.setAlpha((int) (0xFF * alpha * centerMaxAlpha * maxAlpha));
                        canvas.scale((float) blurBitmap.getWidth() / (blurBitmap.getWidth() - 2 * p), (float) blurBitmap.getHeight() / (blurBitmap.getHeight() - 2 * p));
                        canvas.translate(-W / 2, -H / 2);
                        canvas.scale(1f * W / blurBitmap.getWidth(), 1f * H / blurBitmap.getHeight());
                        if (clip) {
                            int pp = (int) (p - 1.5f);
                            canvas.clipRect(pp, pp, blurBitmap.getWidth() - pp, blurBitmap.getHeight() - pp);
                        }
                        canvas.drawBitmap(blurBitmap, 0, 0, drawer.paint);
                    }
                }
                if (restore) {
                    canvas.restore();
                }
                drawnCenterImage = true;
            }
            canvas.restore();
        }
        if (!drawnCenterImage && animatingImageView.getVisibility() == View.VISIBLE) {
            canvas.save();
            if (padImageForHorizontalInsets) {
                canvas.translate(getRightInset() / 2 - getLeftInset() / 2, 0);
            }
            canvas.translate(animatingImageView.getX(), animatingImageView.getY());
            canvas.scale(animatingImageView.getScaleX(), animatingImageView.getScaleY(), animatingImageView.getPivotX(), animatingImageView.getPivotY());

            float p = 1.5f;
            Bitmap blurBitmap = centerBlur.getBitmap(animatingImageView.getBitmapHolder());
            if (blurBitmap != null) {
                canvas.save();
                drawer.paint.setShader(null);
                drawer.paint.setAlpha((int) (0xFF * maxAlpha));
                canvas.scale(1f * animatingImageView.getWidth() / blurBitmap.getWidth(), 1f * animatingImageView.getHeight() / blurBitmap.getHeight());
                canvas.scale((float) blurBitmap.getWidth() / (blurBitmap.getWidth() - 2 * p), (float) blurBitmap.getHeight() / (blurBitmap.getHeight() - 2 * p), blurBitmap.getWidth() / 2f, blurBitmap.getHeight() / 2f);
                canvas.drawBitmap(blurBitmap, 0, 0, drawer.paint);
                if (clip) {
                    int pp = (int) (p - 1.5f);
                    canvas.clipRect(pp, pp, blurBitmap.getWidth() - pp, blurBitmap.getHeight() - pp);
                }
                canvas.restore();
            }

            canvas.restore();
            drawnCenterImage = true;
        }

        if (sideImage == leftImage) {
            float leftMaxAlpha = allowCrossfade ? centerImageInsideBlur[0].set(1f, true) : 1f;
            if (sideImage != null && sideImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate(containerWidth / 2, containerHeight / 2);
                canvas.translate(-(containerWidth * (scale + 1) + dp(30)) / 2 + currentTranslationX, 0);
                int bitmapWidth = sideImage.getBitmapWidth();
                int bitmapHeight = sideImage.getBitmapHeight();
                if (!leftImageIsVideo && leftCropState != null && leftCropTransform.hasViewTransform()) {
                    applyCrop(canvas, containerWidth, containerHeight, bitmapWidth, bitmapHeight, currentScale, leftCropTransform, leftCropState);
                }
                float scaleX = containerWidth / (float) bitmapWidth;
                float scaleY = containerHeight / (float) bitmapHeight;
                float scale = Math.min(scaleX, scaleY);
                int width = (int) (bitmapWidth * scale);
                int height = (int) (bitmapHeight * scale);

                boolean mirror = false;
                if (!imagesArrLocals.isEmpty()) {
                    if (currentEditMode == EDIT_MODE_CROP || sendPhotoType == SELECT_TYPE_AVATAR) {
                        mirror = leftCropTransform.isMirrored();
                    } else {
                        mirror = leftCropState != null && leftCropState.mirrored;
                    }
                }
                if (mirror) {
                    canvas.scale(-1, 1);
                }

                Bitmap blurBitmap = leftBlur.getBitmap(sideImage);
                float p = 1.5f;
                if (blurBitmap != null) {
                    drawer.paint.setShader(null);
                    drawer.paint.setAlpha((int) (0xFF * maxAlpha * leftMaxAlpha));
                    canvas.scale((float) blurBitmap.getWidth() / (blurBitmap.getWidth() - 2 * p), (float) blurBitmap.getHeight() / (blurBitmap.getHeight() - 2 * p));
                    canvas.translate(-width / 2, -height / 2);
                    canvas.scale(1f * width / (blurBitmap.getWidth()), 1f * height / (blurBitmap.getHeight()));
                    if (clip) {
                        int pp = (int) (p - 1.5f);
                        canvas.clipRect(pp, pp, blurBitmap.getWidth() - pp, blurBitmap.getHeight() - pp);
                    }
                    canvas.drawBitmap(blurBitmap, 0, 0, drawer.paint);
                }

                canvas.restore();
            }
        }

        canvas.restoreToCount(restoreCount);

        if (overlayColor != 0) {
            drawer.paint.setColor(overlayColor);
            drawer.paint.setAlpha((int) (drawer.paint.getAlpha() * lerp(.7f, 1f, maxAlpha)));
            canvas.drawPaint(drawer.paint);
        }
    }

    private void cancelStickerClippingMode() {
        if (sendPhotoType == SELECT_TYPE_STICKER && cutOutBtn.isCancelState()) {
            cutOutBtn.setCutOutState(true);
            showEditStickerMode(true, true);
            stickerMakerView.disableClippingMode();
            containerView.invalidate();
        }
    }

    private void invalidateBlur() {
        if (stickerMakerView != null && stickerMakerView.isThanosInProgress) {
            return;
        }
//        if (animationInProgress != 0) {
//            return;
//        }

        invalidateAllGlassAttachedViews();


        if (captionEdit != null) {
            captionEdit.invalidateBlur();
        }
        if (topCaptionEdit != null) {
            topCaptionEdit.invalidateBlur();
        }
        if (cutOutBtn != null) {
            cutOutBtn.invalidateBlur();
        }
        if (eraseBtn != null) {
            eraseBtn.invalidateBlur();
        }
        if (restoreBtn != null) {
            restoreBtn.invalidateBlur();
        }
        if (undoBtn != null) {
            undoBtn.invalidateBlur();
        }
        if (outlineBtn != null) {
            outlineBtn.invalidateBlur();
        }
        if (videoTimelineView != null) {
            videoTimelineView.invalidateBlur();
        }
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    private class BlurButton extends StickerCutOutBtn {
        public BlurButton() {
            super(stickerMakerView, activityContext, resourcesProvider, blurManager);
        }

        private final Path path = new Path();
        private boolean active;
        private final AnimatedFloat activeFloat = new AnimatedFloat(this, 0, 420, CubicBezierInterpolator.EASE_OUT_QUINT);

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            path.rewind();
            path.addRoundRect(bounds, dp(rad), dp(rad), Path.Direction.CW);
            canvas.clipPath(path);
            canvas.translate(-getX(), -getY());
            if (this == eraseBtn || this == restoreBtn) {
                canvas.translate(-btnLayout.getX(), -btnLayout.getY());
            }
            drawCaptionBlur(canvas, blurDrawer, 0xFF2b2b2b, 0x33000000, false, true, false);
            float active = activeFloat.set(this.active);
            if (active > 0) {
                canvas.drawColor(Theme.multAlpha(Color.WHITE, active));
            }
            setTextColor(ColorUtils.blendARGB(0xFFFFFFFF, 0xFF000000, active));
            canvas.restore();
            super.onDraw(canvas);
        }

        @Override
        public void onDrawForeground(Canvas canvas) {
            canvas.save();
            canvas.clipPath(path);
            super.onDrawForeground(canvas);
            canvas.restore();
        }

        public void setActive(boolean active, boolean animated) {
            this.active = active;
            if (!animated) {
                activeFloat.set(active, true);
            }
            invalidate();
        }

        public boolean isActive() {
            return active;
        }
    }

    private void applyTransformToOutline(Canvas canvas) {
        float currentTranslationY;
        float currentTranslationX;
        float currentScale;
        float currentRotation;
        float currentMirror;
        if (imageMoveAnimation != null) {
            currentMirror = lerp(mirror, animateToMirror, animationValue);
            currentScale = lerp(scale, animateToScale, animationValue);
            currentRotation = lerp(rotate, animateToRotate, animationValue);
            currentTranslationY = lerp(translationY, animateToY, animationValue);
            currentTranslationX = lerp(translationX, animateToX, animationValue);
        } else {
            currentScale = scale;
            currentMirror = mirror;
            currentRotation = rotate;
            currentTranslationY = translationY;
            currentTranslationX = translationX;
            if (animationStartTime != 0) {
                currentTranslationX = animateToX;
                currentTranslationY = animateToY;
                currentScale = animateToScale;
            }
        }

        int containerWidth = getContainerViewWidth();
        int containerHeight = getContainerViewHeight();

        canvas.translate(getAdditionX(currentEditMode), getAdditionY(currentEditMode));
        canvas.translate(currentTranslationX, currentTranslationY + (currentEditMode != EDIT_MODE_PAINT ? currentPanTranslationY : 0));
        canvas.scale(currentScale, currentScale);
        canvas.rotate(currentRotation);

        int bitmapWidth, originalWidth;
        int bitmapHeight, originalHeight;
        originalWidth = bitmapWidth = centerImage.getBitmapWidth();
        originalHeight = bitmapHeight = centerImage.getBitmapHeight();

        float scale = Math.min(containerWidth / (float) originalWidth, containerHeight / (float) originalHeight);

        float scaleToFitX = 1.0f;
        int rotatedWidth = originalWidth;
        int rotatedHeight = originalHeight;
        int orientation = cropTransform.getOrientation();
        if (orientation == 90 || orientation == 270) {
            int temp = bitmapWidth;
            bitmapWidth = bitmapHeight;
            bitmapHeight = temp;

            temp = rotatedWidth;
            rotatedWidth = rotatedHeight;
            rotatedHeight = temp;
        }
        float cropAnimationValue = 1.0f;
        float cropPw = cropTransform.getCropPw();
        float cropPh = cropTransform.getCropPh();
        bitmapWidth *= cropPw + (1.0f - cropPw) * (1.0f - cropAnimationValue);
        bitmapHeight *= cropPh + (1.0f - cropPh) * (1.0f - cropAnimationValue);
        scaleToFitX = containerWidth / (float) bitmapWidth;
        if (scaleToFitX * bitmapHeight > containerHeight) {
            scaleToFitX = containerHeight / (float) bitmapHeight;
        }
        if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode != 1 || switchingToMode == EDIT_MODE_NONE) && editState.cropState != null) {
            float startW = bitmapWidth * scaleToFitX;
            float startH = bitmapHeight * scaleToFitX;
            float originalScaleToFitX = containerWidth / (float) originalWidth;
            if (originalScaleToFitX * originalHeight > containerHeight) {
                originalScaleToFitX = containerHeight / (float) originalHeight;
            }
            float finalW = originalWidth * originalScaleToFitX / (currentScale);
            float finalH = originalHeight * originalScaleToFitX / (currentScale);

            float w = startW + (finalW - startW) * (1.0f - cropAnimationValue);
            float h = startH + (finalH - startH) * (1.0f - cropAnimationValue);

            canvas.clipRect(-w / 2, -h / 2, w / 2, h / 2);
        }
        if (sendPhotoType == SELECT_TYPE_AVATAR || cropTransform.hasViewTransform()) {
            float cropScale;
            if (videoTextureView != null) {
                videoTextureView.setScaleX(editState.cropState != null && editState.cropState.mirrored ? -1.0f : 1.0f);
                if (firstFrameView != null) {
                    firstFrameView.setScaleX(videoTextureView.getScaleX());
                }
            }
            cropScale = editState.cropState != null ? editState.cropState.cropScale : 1.0f;
            float trueScale = 1.0f + (cropScale - 1.0f) * (1.0f - cropAnimationValue);
            cropScale *= scaleToFitX / scale / trueScale;

            canvas.translate(cropTransform.getCropAreaX() * cropAnimationValue, cropTransform.getCropAreaY() * cropAnimationValue);
            canvas.scale(cropScale, cropScale);
            canvas.translate(cropTransform.getCropPx() * rotatedWidth * scale * cropAnimationValue, cropTransform.getCropPy() * rotatedHeight * scale * cropAnimationValue);
            float rotation = (cropTransform.getRotation() + orientation);
            if (rotation > 180) {
                rotation -= 360;
            }
            canvas.rotate(rotation);
        }

        boolean mirror = false;
        if (!imagesArrLocals.isEmpty()) {
            mirror = editState.cropState != null && editState.cropState.mirrored;
        }
        if (mirror) {
            canvas.scale(-1, 1);
        }
        if (currentMirror > 0) {
            canvas.scale(1 - currentMirror * 2, 1f);
            canvas.skew(0, 4 * currentMirror * (1f - currentMirror) * .25f);
        }
    }

    public MessageObject getCurrentMessageObject() {
        return currentMessageObject;
    }

    private void applyTransformToMatrix(Matrix matrix) {
        float currentTranslationY;
        float currentTranslationX;
        float currentScale;
        float currentRotation;
        float currentMirror;
        if (imageMoveAnimation != null) {
            currentMirror = lerp(mirror, animateToMirror, animationValue);
            currentScale = lerp(scale, animateToScale, animationValue);
            currentRotation = lerp(rotate, animateToRotate, animationValue);
            currentTranslationY = lerp(translationY, animateToY, animationValue);
            currentTranslationX = lerp(translationX, animateToX, animationValue);
        } else {
            currentScale = scale;
            currentMirror = mirror;
            currentRotation = rotate;
            currentTranslationY = translationY;
            currentTranslationX = translationX;
            if (animationStartTime != 0) {
                currentTranslationX = animateToX;
                currentTranslationY = animateToY;
                currentScale = animateToScale;
            }
        }

        int containerWidth = getContainerViewWidth();
        int containerHeight = getContainerViewHeight();

//        matrix.postTranslate(containerWidth / 2f + getAdditionX(currentEditMode), containerHeight / 2f + getAdditionY(currentEditMode));
        matrix.preTranslate(currentTranslationX, currentTranslationY + (currentEditMode != EDIT_MODE_PAINT ? currentPanTranslationY : 0));
        matrix.preScale(currentScale, currentScale);
        matrix.preRotate(currentRotation);

        int bitmapWidth, originalWidth;
        int bitmapHeight, originalHeight;
        originalWidth = bitmapWidth = centerImage.getBitmapWidth();
        originalHeight = bitmapHeight = centerImage.getBitmapHeight();

        float scale = Math.min(containerWidth / (float) originalWidth, containerHeight / (float) originalHeight);

        float scaleToFitX = 1.0f;
        int rotatedWidth = originalWidth;
        int rotatedHeight = originalHeight;
        int orientation = cropTransform.getOrientation();
        if (orientation == 90 || orientation == 270) {
            int temp = bitmapWidth;
            bitmapWidth = bitmapHeight;
            bitmapHeight = temp;

            temp = rotatedWidth;
            rotatedWidth = rotatedHeight;
            rotatedHeight = temp;
        }
        float cropAnimationValue = 1.0f;
        float cropPw = cropTransform.getCropPw();
        float cropPh = cropTransform.getCropPh();
        bitmapWidth *= cropPw + (1.0f - cropPw) * (1.0f - cropAnimationValue);
        bitmapHeight *= cropPh + (1.0f - cropPh) * (1.0f - cropAnimationValue);
        scaleToFitX = containerWidth / (float) bitmapWidth;
        if (scaleToFitX * bitmapHeight > containerHeight) {
            scaleToFitX = containerHeight / (float) bitmapHeight;
        }
//        if (sendPhotoType != SELECT_TYPE_AVATAR && (currentEditMode != 1 || switchingToMode == EDIT_MODE_NONE) && editState.cropState != null) {
//            float startW = bitmapWidth * scaleToFitX;
//            float startH = bitmapHeight * scaleToFitX;
//            float originalScaleToFitX = containerWidth / (float) originalWidth;
//            if (originalScaleToFitX * originalHeight > containerHeight) {
//                originalScaleToFitX = containerHeight / (float) originalHeight;
//            }
//            float finalW = originalWidth * originalScaleToFitX / (currentScale);
//            float finalH = originalHeight * originalScaleToFitX / (currentScale);
//
//            float w = startW + (finalW - startW) * (1.0f - cropAnimationValue);
//            float h = startH + (finalH - startH) * (1.0f - cropAnimationValue);
//
//            canvas.clipRect(-w / 2, -h / 2, w / 2, h / 2);
//        }
        if (sendPhotoType == SELECT_TYPE_AVATAR || cropTransform.hasViewTransform()) {
            float cropScale;
            if (videoTextureView != null) {
                videoTextureView.setScaleX(editState.cropState != null && editState.cropState.mirrored ? -1.0f : 1.0f);
                if (firstFrameView != null) {
                    firstFrameView.setScaleX(videoTextureView.getScaleX());
                }
            }
            cropScale = editState.cropState != null ? editState.cropState.cropScale : 1.0f;
            float trueScale = 1.0f + (cropScale - 1.0f) * (1.0f - cropAnimationValue);
            cropScale *= scaleToFitX / scale / trueScale;

            matrix.preTranslate(cropTransform.getCropAreaX() * cropAnimationValue, cropTransform.getCropAreaY() * cropAnimationValue);
            matrix.preScale(cropScale, cropScale);
            matrix.preTranslate(cropTransform.getCropPx() * rotatedWidth * scale * cropAnimationValue, cropTransform.getCropPy() * rotatedHeight * scale * cropAnimationValue);
            float rotation = (cropTransform.getRotation() + orientation);
            if (rotation > 180) {
                rotation -= 360;
            }
            matrix.preRotate(rotation);
        }

        boolean mirror = false;
        if (!imagesArrLocals.isEmpty()) {
            mirror = editState.cropState != null && editState.cropState.mirrored;
        }
        if (mirror) {
            matrix.preScale(-1, 1);
        }
        if (currentMirror > 0) {
            matrix.preScale(1 - currentMirror * 2, 1f);
            matrix.preSkew(0, 4 * currentMirror * (1f - currentMirror) * .25f);
        }
    }

    public void openAdsMenu() {
        if (currentMessageObject == null || !currentMessageObject.isSponsored() || menuItem.getAlpha() <= 0.5f) return;

        final int account = currentMessageObject.currentAccount;
        final Theme.ResourcesProvider resourcesProvider = new DarkThemeResourceProvider();

        final ItemOptions o = ItemOptions.makeOptions(containerView, resourcesProvider, menuItem, true);
        o.translate(0, -dp(46));
        o.setGravity(Gravity.RIGHT);

//        if (!currentMessageObject.sponsoredCanReport) {
//            FrameLayout sponsoredAbout = new FrameLayout(activityContext);
//            sponsoredAbout.setMinimumHeight(AndroidUtilities.dp(56));
//            sponsoredAbout.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector, resourcesProvider), 6, 0));
//            sponsoredAbout.setPadding(AndroidUtilities.dp(18), 0, AndroidUtilities.dp(18), 0);
//
//            ImageView infoImage = new ImageView(activityContext);
//            infoImage.setScaleType(ImageView.ScaleType.CENTER);
//            infoImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, resourcesProvider), PorterDuff.Mode.MULTIPLY));
//            infoImage.setImageResource(R.drawable.msg_info);
//            sponsoredAbout.addView(infoImage, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 40, Gravity.CENTER_VERTICAL | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT)));
//
//            TextView infoText = new TextView(activityContext) {
//                @Override
//                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//                    if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST && getLayout() != null) {
//                        Layout layout = getLayout();
//                        int width = 0;
//                        for (int i = 0; i < layout.getLineCount(); ++i) {
//                            width = Math.max(width, (int) Math.ceil(layout.getLineWidth(i)));
//                        }
//                        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getPaddingLeft() + width + getPaddingRight(), MeasureSpec.EXACTLY);
//                    }
//                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//                }
//            };
//            infoText.setMaxLines(3);
//            infoText.setGravity(Gravity.LEFT);
//            infoText.setEllipsize(TextUtils.TruncateAt.END);
//            infoText.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
//            infoText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//            infoText.setMaxWidth(AndroidUtilities.dp(240));
//            infoText.setText(LocaleController.getString(R.string.SponsoredMessageInfo));
//            infoText.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43), 0, LocaleController.isRTL ? AndroidUtilities.dp(43) : 0, 0);
//            sponsoredAbout.addView(infoText, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL));
//
//            o.addView(sponsoredAbout);
//            sponsoredAbout.setOnClickListener(v1 -> {
//                if (activityContext == null) {
//                    return;
//                }
//                BottomSheet.Builder builder = new BottomSheet.Builder(activityContext, false, resourcesProvider);
//                BottomSheet[] sheet = new BottomSheet[1];
//                builder.setCustomView(new SponsoredMessageInfoView(activityContext, () -> {
//                    sheet[0].dismiss();
//                    closePhoto(true, false);
//                }, resourcesProvider));
//                sheet[0] = builder.show();
//                o.dismiss();
//            });
//            o.addGap();
//        }

        if (currentMessageObject.sponsoredInfo != null || currentMessageObject.sponsoredAdditionalInfo != null || currentMessageObject.sponsoredUrl != null && !currentMessageObject.sponsoredUrl.startsWith("https://" + MessagesController.getInstance(currentAccount).linkPrefix)) {
            ItemOptions info = o.makeSwipeback();

            ActionBarMenuSubItem backCell = new ActionBarMenuSubItem(activityContext, true, false, resourcesProvider);
            backCell.setItemHeight(44);
            backCell.setTextAndIcon(getString(R.string.Back), R.drawable.msg_arrow_back);
            backCell.getTextView().setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(40), 0, LocaleController.isRTL ? AndroidUtilities.dp(40) : 0, 0);
            backCell.setOnClickListener(v1 -> o.closeSwipeback());
            info.addView(backCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            info.addView(new ActionBarPopupWindow.GapView(activityContext, resourcesProvider), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 8));

            ArrayList<View> sections = new ArrayList<>();

            if (currentMessageObject.sponsoredUrl != null && !TextUtils.equals(AndroidUtilities.getHostAuthority(currentMessageObject.sponsoredUrl), MessagesController.getInstance(currentAccount).linkPrefix)) {
                TextView textView = new TextView(activityContext);
                textView.setTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setPadding(AndroidUtilities.dp(18), AndroidUtilities.dp(10), AndroidUtilities.dp(18), AndroidUtilities.dp(10));
                textView.setMaxWidth(AndroidUtilities.dp(300));
                Uri uri = Uri.parse(currentMessageObject.sponsoredUrl);
                textView.setText(Browser.replaceHostname(uri, Browser.IDN_toUnicode(uri.getHost()), null));
                textView.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 0, currentMessageObject.sponsoredAdditionalInfo == null ? 6 : 0));
                textView.setOnClickListener(e -> {
                    if (currentMessageObject == null) {
                        return;
                    }
                    o.dismiss();
                    if (parentFragment instanceof ChatActivity) {
                        ((ChatActivity) parentFragment).logSponsoredClicked(currentMessageObject, false, true);
                    }
                    Browser.openUrl(activityContext, Uri.parse(currentMessageObject.sponsoredUrl), true, false, false, null, null, false, MessagesController.getInstance(currentAccount).sponsoredLinksInappAllow, false);
                });
                textView.setOnLongClickListener(e -> {
                    if (currentMessageObject == null) {
                        return false;
                    }
                    if (AndroidUtilities.addToClipboard(currentMessageObject.sponsoredUrl)) {
                        BulletinFactory.of(Bulletin.BulletinWindow.make(activityContext), resourcesProvider).createCopyLinkBulletin().show();
                    }
                    return true;
                });
                sections.add(textView);
            }

            if (currentMessageObject.sponsoredInfo != null) {
                TextView textView = new TextView(activityContext);
                textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setPadding(AndroidUtilities.dp(18), AndroidUtilities.dp(10), AndroidUtilities.dp(18), AndroidUtilities.dp(10));
                textView.setMaxWidth(AndroidUtilities.dp(300));
                textView.setText(currentMessageObject.sponsoredInfo);
                textView.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 0, currentMessageObject.sponsoredAdditionalInfo == null ? 6 : 0));
                textView.setOnClickListener(e -> {
                    if (AndroidUtilities.addToClipboard(currentMessageObject.sponsoredInfo)) {
                        BulletinFactory.of(Bulletin.BulletinWindow.make(activityContext), resourcesProvider).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
                    }
                });
                sections.add(textView);
            }

            if (currentMessageObject.sponsoredAdditionalInfo != null) {
                TextView textView = new TextView(activityContext);
                textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setPadding(AndroidUtilities.dp(18), AndroidUtilities.dp(10), AndroidUtilities.dp(18), AndroidUtilities.dp(10));
                textView.setMaxWidth(AndroidUtilities.dp(300));
                textView.setText(currentMessageObject.sponsoredAdditionalInfo);
                textView.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 0, 6));
                textView.setOnClickListener(e -> {
                    if (AndroidUtilities.addToClipboard(currentMessageObject.sponsoredAdditionalInfo)) {
                        BulletinFactory.of(Bulletin.BulletinWindow.make(activityContext), resourcesProvider).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
                    }
                });
                sections.add(textView);
            }

            for (int i = 0; i < sections.size(); ++i) {
                View section = sections.get(i);
                if (i > 0) {
                    FrameLayout separator = new FrameLayout(activityContext);
                    separator.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
                    LinearLayout.LayoutParams params = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1);
                    params.height = 1;
                    info.addView(separator, params);
                }
                info.addView(section, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            }
            o.add(R.drawable.msg_channel, getString(R.string.SponsoredMessageSponsorReportable), () -> o.openSwipeback(info));
        }

        if (!UserConfig.getInstance(account).isPremium() && !MessagesController.getInstance(currentAccount).premiumFeaturesBlocked() && !currentMessageObject.sponsoredCanReport) {
            o.add(R.drawable.msg_block2, getString(R.string.HideAd), () -> {
                if (UserConfig.getInstance(account).isPremium()) {
                    BulletinFactory.of(containerView, resourcesProvider)
                            .createAdReportedBulletin(LocaleController.getString(R.string.AdHidden))
                            .show();
                    MessagesController.getInstance(account).disableAds(true);
                    if (parentFragment instanceof ChatActivity) {
                        ChatActivity chatActivity = (ChatActivity) parentFragment;
                        chatActivity.removeFromSponsored(currentMessageObject);
                        chatActivity.removeMessageWithThanos(currentMessageObject);
                    }
                } else {
                    new PremiumFeatureBottomSheet(parentFragment, PremiumPreviewFragment.PREMIUM_FEATURE_ADS, true).show();
                }
            });
        }
        if (currentMessageObject.sponsoredCanReport) {
            o.add(R.drawable.msg_info, getString(R.string.AboutRevenueSharingAds), () -> {
                RevenueSharingAdsInfoBottomSheet.showAlert(activityContext, parentFragment, false, resourcesProvider);
            });
            if (parentFragment instanceof ChatActivity && !MessagesController.getInstance(account).premiumFeaturesBlocked()) {
                o.addGap();
                o.add(R.drawable.msg_cancel, getString(R.string.RemoveAds), () -> {
                    if (UserConfig.getInstance(account).isPremium()) {
                        BulletinFactory.of(containerView, resourcesProvider)
                                .createAdReportedBulletin(LocaleController.getString(R.string.AdHidden))
                                .show();
                        MessagesController.getInstance(account).disableAds(true);
                        if (parentFragment instanceof ChatActivity) {
                            ChatActivity chatActivity = (ChatActivity) parentFragment;
                            chatActivity.removeFromSponsored(currentMessageObject);
                            chatActivity.removeMessageWithThanos(currentMessageObject);
                        }
                    } else {
                        new PremiumFeatureBottomSheet(parentFragment, PremiumPreviewFragment.PREMIUM_FEATURE_ADS, true).show();
                    }
                });
            }
        }

        if (o.getItemsCount() <= 0) return;
        o.show();
    }

    private static CharSequence sponsoredCaption(MessageObject messageObject, CharSequence str) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(messageObject.sponsoredTitle)) {
            sb.append(messageObject.sponsoredTitle);
            sb.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new LineHeightSpan() {
                @Override
                public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
                    fm.descent += dp(4);
                    fm.ascent = fm.ascent;
                }
            }, 0, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append("\n");
        }
        sb.append(str);
//        sb.append("\n");
//        sb.setSpan(new LineHeightSpan() {
//            @Override
//            public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
//                final int originHeight = fm.descent - fm.ascent;
//                if (originHeight <= 0) {
//                    return;
//                }
//                final float ratio = dp(72) * 1.0f / originHeight;
//                fm.descent = Math.round(fm.descent * ratio);
//                fm.ascent = fm.descent - dp(72);
//            }
//        }, sb.length() - 1, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private void createAdButtonView() {
        if (adButtonView != null) return;

        adButtonView = new FrameLayout(activityContext);
        adButtonView.setBackground(Theme.createRadSelectorDrawable(0x24FFFFFF, 0x15FFFFFF, 8, 8));
        ScaleStateListAnimator.apply(adButtonView, .05f, 1.25f);

        adButtonTextView = new TextView(activityContext);
        adButtonTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        adButtonTextView.setTextColor(0xFFFFFFFF);
        adButtonTextView.setTypeface(AndroidUtilities.bold());
        adButtonView.addView(adButtonTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        adButtonView.setOnClickListener(v -> {
            if (currentMessageObject == null || !currentMessageObject.isSponsored()) return;
            if (parentFragment instanceof ChatActivity) {
                ((ChatActivity) parentFragment).logSponsoredClicked(currentMessageObject, false, true);
            }
            closePhoto(true, false);
            if (currentMessageObject.sponsoredUrl != null) {
                Browser.openUrl(LaunchActivity.instance != null ? LaunchActivity.instance : activityContext, Uri.parse(currentMessageObject.sponsoredUrl), true, false, false, null, null, false, MessagesController.getInstance(currentAccount).sponsoredLinksInappAllow, false);
            }
        });
    }

    private void chooseSpeed(float speed, boolean isFinal, boolean closeMenu) {
        if (speed != currentVideoSpeed) {
            currentVideoSpeed = speed;
            if (currentMessageObject != null) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("playback_speed", Activity.MODE_PRIVATE);
                if (Math.abs(currentVideoSpeed - 1.0f) < 0.001f) {
                    preferences.edit().remove("speed" + currentMessageObject.getDialogId() + "_" + currentMessageObject.getId()).commit();
                } else {
                    preferences.edit().putFloat("speed" + currentMessageObject.getDialogId() + "_" + currentMessageObject.getId(), currentVideoSpeed).commit();
                }
            }
            if (videoPlayer != null) {
                videoPlayer.setPlaybackSpeed(currentVideoSpeed);
            }
            if (photoViewerWebView != null) {
                photoViewerWebView.setPlaybackSpeed(currentVideoSpeed);
            }
        }
        setMenuItemIcon(true, isFinal);
        if (closeMenu) {
            videoItem.toggleSubMenu();
        }
    }

    private void toggleCaptionAbove() {
        if (placeProvider == null) return;
        if (!placeProvider.canMoveCaptionAbove()) return;

        applyCaption();

        final CaptionContainerView fromView = getCaptionView();
        placeProvider.moveCaptionAbove(!placeProvider.isCaptionAbove());
        showEditCaption(true, true);
        final CaptionContainerView toView = getCaptionView();
        final boolean above = placeProvider.isCaptionAbove();

        if (fromView != toView) {
            fromView.editText.hidePopup(true);
            toView.setText(AnimatedEmojiSpan.cloneSpans(fromView.getText()));
            toView.editText.getEditText().setAllowTextEntitiesIntersection(fromView.editText.getEditText().getAllowTextEntitiesIntersection());
            if (fromView.editText.getEditText().isFocused()) {
                toView.editText.getEditText().requestFocus();
                toView.editText.getEditText().setSelection(
                    fromView.editText.getEditText().getSelectionStart(),
                    fromView.editText.getEditText().getSelectionEnd()
                );
            }
            if (fromView.mentionContainer != null) {
                AndroidUtilities.removeFromParent(fromView.mentionContainer);
                fromView.mentionContainer = null;
            }

            actionBar.animate().alpha(isActionBarVisible && (getCaptionView() != topCaptionEdit || !topCaptionEdit.keyboardNotifier.keyboardVisible()) ? 1.0f : 0.0f).start();
            if (pickerView.getVisibility() == View.VISIBLE) {
                toggleOnlyCheckImageView(isActionBarVisible && (getCaptionView() != topCaptionEdit || !topCaptionEdit.keyboardNotifier.keyboardVisible()));
            }
        }
        if (MessagesController.getInstance(currentAccount).shouldShowMoveCaptionHint()) {
            MessagesController.getInstance(currentAccount).incrementMoveCaptionHint();
            BulletinFactory.of(above ? bottomBulletinUnderCaption : topBulletinUnderCaption, new DarkThemeResourceProvider())
                .createSimpleBulletin(
                    above ? R.raw.caption_up : R.raw.caption_down,
                    getString(above ? R.string.MovedCaptionUp : R.string.MovedCaptionDown),
                    getString(above ? R.string.MovedCaptionUpText : R.string.MovedCaptionDownText)
                )
                .setImageScale(.8f)
                .show(!above);
        }

        if (captionEdit != null) {
            captionEdit.closeKeyboard();
        }
    }

    public static float getSavedProgressFast(MessageObject msg) {
        final int duration = (int) msg.getDuration();
        final String name = msg.isEmbedVideo() ? msg.messageOwner.media.webpage.url : msg.getFileNameFast();
        if (!TextUtils.isEmpty(name)) {
            if (duration >= 10) {
                final SavedVideoPosition videoPosition = savedVideoPositions.get(name);
                if (msg != null && msg.forceSeekTo < 0 && videoPosition != null) {
                    float pos = videoPosition.position;
                    if (pos > 0 && pos < 0.999f) {
                        return pos;
                    }
                }
            }
        }
        return 0;
    }

    public static float getSavedProgress(MessageObject msg) {
        final int duration = (int) msg.getDuration();
        final String name = msg.isEmbedVideo() ? msg.messageOwner.media.webpage.url : msg.getFileNameFast();
        if (!TextUtils.isEmpty(name)) {
            if (duration >= 10) {
                SavedVideoPosition videoPosition = savedVideoPositions.get(name);
                if (msg != null && msg.forceSeekTo < 0 && videoPosition != null) {
                    float pos = videoPosition.position;
                    if (pos > 0 && pos < 0.999f) {
                        return pos;
                    }
                }
            }
            if (duration >= 2 * 60) {
                if (msg.forceSeekTo < 0) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", Activity.MODE_PRIVATE);
                    float pos = preferences.getFloat(name, -1);
                    if (pos > 0 && pos < 0.999f) {
                        return pos;
                    }
                }
            }
        }
        return 0;
    }

    private ChromecastMediaVariations getCurrentChromecastMedia() {
        if (currentMessageObject == null) {
            return null;
        }

        String title = null;
        final TLRPC.Document document = currentMessageObject.getDocument();
        String subtitle = currentMessageObject.getDocumentName();
        if (TextUtils.isEmpty(subtitle)) {
            subtitle = currentMessageObject.getFileName();
        }

        final long dialogId = currentMessageObject.getDialogId();
        if (DialogObject.isUserDialog(dialogId)) {
            final TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(dialogId);
            if (user != null) {
                title = ContactsController.formatName(user.first_name, user.last_name);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(-dialogId);
            if (chat != null) {
                title = chat.title;
            }
        }

        if (currentMessageObject.isPhoto()) {
            final File file = FileLoader.getInstance(currentMessageObject.currentAccount).getPathToMessage(currentMessageObject.messageOwner);
            if (file == null || !file.exists()) {
                return null;
            }

            final Uri uri = Uri.parse("file://" + file.getAbsolutePath());
            final ChromecastMedia media = ChromecastMedia.Builder.fromUri(uri, "/photo_" + currentMessageObject.getId(), ChromecastMedia.IMAGE_JPEG)
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .build();

            return ChromecastMediaVariations.of(media);
        }

        if (videoPlayer != null) {
            return videoPlayer.getCurrentChromecastMedia((document != null ? document.id : currentMessageObject.getId()) + "", title, subtitle);
        }

        return null;
    }

    private boolean ignorePlayerUpdate;
    public void syncCastedPlayer() {
        if (!isVisible()) return;
        ignorePlayerUpdate = true;

        if (videoPlayer != null) {
            videoPlayer.setMute(CastSync.isActive() || muteVideo);
        }
        if (videoPlayer != null && CastSync.isActive() && !CastSync.isUpdatePending()) {
            final long castedPosition = CastSync.getPosition();
            if (castedPosition >= 0 && Math.abs(videoPlayer.getCurrentPosition() - castedPosition) > 1000) {
                videoPlayer.seekTo(castedPosition);
            }
            if (CastSync.isPlaying()) {
                videoPlayer.play();
            } else {
                videoPlayer.pause();
            }
            if (activityContext != null && Math.abs(CastSync.getDeviceVolume() - CastSync.getVolume()) > 0.05f) {
                final AudioManager audioManager = (AudioManager) activityContext.getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int minVolume = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
                }
                int newVolume = minVolume + (int) ((maxVolume - minVolume) * CastSync.getVolume());
                if (newVolume != audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI);
                }
            }
            chooseSpeed(CastSync.getSpeed(), true, false);
        }
        if (videoItemIcon != null) {
            videoItemIcon.setCasting(CastSync.isActive(), true);
        }

        ignorePlayerUpdate = false;
    }

    public long getCurrentPosition() {
        if (videoPlayer == null) return -1;
        return videoPlayer.getCurrentPosition();
    }


    @Override
    public void onFactorChanged(int id, float factor, float fraction, FactorAnimator callee) {
        if (id == ANIMATOR_ID_POLL_ATTACH_BUTTONS_VISIBLE) {
            pollAttachButtons.setTranslationY(dp(36) * (1f - factor));
            pollAttachButtons.setAlpha(factor);
            pollAttachButtons.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);

            if (sendPhotoTypeIsPollMediaEdit) {
                pickerView.setVisibility(factor < 1 ? View.VISIBLE : View.GONE);
                pickerView.setAlpha(1f - factor);
                pickerView.setTranslationY(dp(36) * factor);
                pickerViewSendButton.setVisibility(factor < 1 ? View.VISIBLE : View.GONE);
                pickerViewSendButton.setAlpha(1f - factor);
                pickerViewSendButton.setTranslationY(dp(36) * factor);
            }
        }
    }

    /* Pip V2 */

    private void pipInvalidateAvailability() {
        if (pipSource != null) {
            pipSource.invalidateAvailability();
        }
        if (PipVideoOverlay.getPipSource() != null) {
            PipVideoOverlay.getPipSource().invalidateAvailability();
        }
    }

    private View pipPlaceholderView;
    public Runnable pipFirstFrameCallback;
    private TextureView pipTextureView;
    private boolean windowViewSkipRender;
    private boolean textureViewSkipRender;

    @Override
    public boolean pipIsAvailable() {
        return pipItem != null && pipItem.isEnabled() && isPlaying;
    }

    @Override
    public Bitmap pipCreatePrimaryWindowViewBitmap() {
        if (videoTextureView != null) {
            return videoTextureView.getBitmap();
        }
        if (usedSurfaceView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Bitmap bitmap = Bitmaps.createBitmap(videoSurfaceView.getWidth(), videoSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
                AndroidUtilities.getBitmapFromSurface(videoSurfaceView, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    @Override
    public void pipRenderBackground(Canvas canvas) {
        canvas.drawColor(0xFF000000);
    }


    @Override
    public void pipRenderForeground(Canvas canvas) {
        textureViewSkipRender = true;
        windowView.draw(canvas);
        textureViewSkipRender = false;
    }

    @Override
    public View pipCreatePictureInPictureView() {
        pipTextureView = new TextureView(parentActivity);
        pipTextureView.setOpaque(false);
        if (pipPlaceholderView != null) {
            pipPlaceholderView.bringToFront();
        }

        return pipTextureView;
    }

    @Override
    public void pipHidePrimaryWindowView(Runnable firstFrameCallback) {
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss(false);
        }

        this.pipFirstFrameCallback = firstFrameCallback;

        if (videoPlayer != null) {
            videoPlayer.setSurfaceView(null);
            videoPlayer.setTextureView(null);
            videoPlayer.play();
            videoPlayer.setTextureView(pipTextureView);
        };

        WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(windowView);
        windowViewSkipRender = true;
        windowView.invalidate();
    }

    @Override
    public Bitmap pipCreatePictureInPictureViewBitmap() {
        if (pipTextureView == null || !pipTextureView.isAvailable()) {
            return null;
        }

        return pipTextureView.getBitmap();
    }

    @Override
    public void pipShowPrimaryWindowView(Runnable firstFrameCallback) {
        this.pipFirstFrameCallback = firstFrameCallback;

        windowViewSkipRender = false;
        if (windowView != null) {
            WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
            wm.addView(windowView, windowLayoutParams);
            windowView.invalidate();
        }

        if (videoPlayer == null) {
            return;
        }

        videoPlayer.setSurfaceView(null);
        videoPlayer.setTextureView(null);
        videoPlayer.play();
        if (videoTextureView != null) {
            videoPlayer.setTextureView(videoTextureView);
        } else if (videoSurfaceView != null) {
            videoPlayer.setSurfaceView(videoSurfaceView);
        }
    }

    private void showPhotoQualityHint(boolean highQuality) {
        if (compressPhotoHint != null) {
            compressPhotoHint.hide();
            compressPhotoHint = null;
        }
        if (activityContext == null) return;
        compressPhotoHint = new HintView2(activityContext, HintView2.DIRECTION_BOTTOM);
        final SpannableStringBuilder sb = new SpannableStringBuilder("x ").append(getString(highQuality ? R.string.PhotoWillBeSentInHD : R.string.PhotoWillBeSentInSD));
        sb.setSpan(new ColoredImageSpan(highQuality ? R.drawable.menu_quality_hd_filled : R.drawable.menu_quality_sd_filled), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        compressPhotoHint.setText(sb);
        containerView.addView(compressPhotoHint, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 100, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0, 0, 48));
        compressPhotoHint.setTranslationY(pickerView.getTranslationY());
        compressPhotoHint.setJointPx(0, itemsLayout.getX() + compressItem.getX() + compressItem.getWidth() / 2.f);
        final View thisHint = compressPhotoHint;
        compressPhotoHint.setOnHiddenListener(() -> AndroidUtilities.removeFromParent(thisHint));
        compressPhotoHint.setDuration(3500);
        compressPhotoHint.show();
    }

    private boolean captureCurrentVideoFrameToClipboard() {
        if (usedSurfaceView && videoSurfaceView != null && videoSurfaceView.getWidth() > 0 && videoSurfaceView.getHeight() > 0) {
            Bitmap bitmap = Bitmaps.createBitmap(videoSurfaceView.getWidth(), videoSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
            AndroidUtilities.getBitmapFromSurface(videoSurfaceView, bitmap, () -> MessageHelper.saveFrameBitmapToClipboard(bitmap, containerView, resourcesProvider));
            return true;
        } else if (videoTextureView != null && videoTextureView.getWidth() > 0 && videoTextureView.getHeight() > 0) {
            Bitmap bitmap = videoTextureView.getBitmap();
            if (bitmap != null) {
                MessageHelper.saveFrameBitmapToClipboard(bitmap, containerView, resourcesProvider);
                return true;
            }
        }
        return false;
    }

    private void setWindowHdrColorMode(boolean enabled) {
        if (Build.VERSION.SDK_INT < 34 || windowLayoutParams == null) {
            return;
        }
        if (windowColorModeHdr == enabled) {
            return;
        }
        windowColorModeHdr = enabled;
        try {
            windowLayoutParams.setColorMode(enabled ? ActivityInfo.COLOR_MODE_HDR : ActivityInfo.COLOR_MODE_DEFAULT);
            if (parentActivity != null && windowView != null && windowView.getParent() != null) {
                WindowManager wm = (WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE);
                wm.updateViewLayout(windowView, windowLayoutParams);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void updateWindowHdrColorMode() {
        if (Build.VERSION.SDK_INT < 34) {
            return;
        }
        if (windowDisplayHdrCapable == null) {
            windowDisplayHdrCapable = AndroidUtil.isScreenHDR();
        }
        if (!windowDisplayHdrCapable) {
            setWindowHdrColorMode(false);
            return;
        }
        Bitmap bitmap = centerImage != null ? centerImage.getBitmap() : null;
        boolean enabled = !centerImageIsVideo && AndroidUtil.hasGainmap(bitmap);
        setWindowHdrColorMode(enabled);
    }

    private class PhotoViewerWindowView extends FrameLayout {

        public PhotoViewerWindowView(@NonNull Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return isVisible && super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return isVisible && PhotoViewer.this.onTouchEvent(event);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (!muteVideo && sendPhotoType != SELECT_TYPE_AVATAR && isCurrentVideo && videoPlayer != null && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {
                videoPlayer.setVolume(1.0f);
            }
            return super.dispatchKeyEvent(event);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (videoPlayerControlVisible && isPlaying) {
                switch (ev.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        AndroidUtilities.cancelRunOnUIThread(hideActionBarRunnable);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (currentMessageObject == null || !currentMessageObject.isSponsored()) {
                            scheduleActionBarHide();
                        }
                        break;
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override
        protected boolean drawChild(@NonNull Canvas canvas, View child, long drawingTime) {
            boolean result;
            try {
                result = super.drawChild(canvas, child, drawingTime);
            } catch (Throwable ignore) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            if (!inBubbleMode) {
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    heightSize += AndroidUtilities.statusBarHeight;
                }
            }

            setMeasuredDimension(widthSize, heightSize);

            int bottomInsets = insets.bottom;
            heightSize -= bottomInsets;
            widthSize -= getPaddingLeft() + getPaddingRight();
            heightSize -= getPaddingBottom();
            // setMeasuredDimension(widthSize, heightSize);
            ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
            animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.AT_MOST));
            containerView.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
            navigationBar.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(navigationBarHeight, MeasureSpec.EXACTLY));
        }

        @Override
        public void requestLayout() {
            super.requestLayout();
            AndroidUtilities.printStackTrace("requestLayout");
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            animatingImageView.layout(getPaddingLeft(), 0, getPaddingLeft() + animatingImageView.getMeasuredWidth(), animatingImageView.getMeasuredHeight());
            containerView.layout(getPaddingLeft(), 0, getPaddingLeft() + containerView.getMeasuredWidth(), containerView.getMeasuredHeight());
            navigationBar.layout(getPaddingLeft(), containerView.getMeasuredHeight(), navigationBar.getMeasuredWidth(), containerView.getMeasuredHeight() + navigationBar.getMeasuredHeight());
            wasLayout = true;
            if (changed) {
                if (!dontResetZoomOnFirstLayout) {
                    scale = scale1();
                    translationX = 0;
                    translationY = 0;
                    updateMinMax(scale);
                }

                if (checkImageView != null) {
                    checkImageView.post(() -> {
                        LayoutParams layoutParams = (LayoutParams) checkImageView.getLayoutParams();
                        WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService(Activity.WINDOW_SERVICE);
                        int rotation = manager.getDefaultDisplay().getRotation();
                        int newMargin = (ActionBar.getCurrentActionBarHeight() - dp(34)) / 2 + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                        if (newMargin != layoutParams.topMargin) {
                            layoutParams.topMargin = newMargin;
                            checkImageView.setLayoutParams(layoutParams);
                        }

                        layoutParams = (LayoutParams) photosCounterView.getLayoutParams();
                        newMargin = (ActionBar.getCurrentActionBarHeight() - dp(40)) / 2 + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                        if (layoutParams.topMargin != newMargin) {
                            layoutParams.topMargin = newMargin;
                            photosCounterView.setLayoutParams(layoutParams);
                        }
                    });
                }
            }
            if (dontResetZoomOnFirstLayout) {
                setScaleToFill();
                dontResetZoomOnFirstLayout = false;
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            centerImage.onAttachedToWindow();
            leftImage.onAttachedToWindow();
            rightImage.onAttachedToWindow();
            attachedToWindow = true;
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            centerImage.onDetachedFromWindow();
            leftImage.onDetachedFromWindow();
            rightImage.onDetachedFromWindow();
            attachedToWindow = false;
            wasLayout = false;
        }

        @Override
        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (textSelectionHelper.isInSelectionMode()) {
                    textSelectionHelper.clear();
                }
                if (isCaptionOpen()) {
                    closeCaptionEnter(true);
                    return false;
                }
                if (ContentPreviewViewer.getInstance().isVisible()) {
                    ContentPreviewViewer.getInstance().closeWithMenu();
                    return false;
                }
                PhotoViewer.getInstance().closePhoto(true, false);
                return true;
            }
            return super.dispatchKeyEventPreIme(event);
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            if (stickerMakerBackgroundView != null && stickerMakerBackgroundView.getVisibility() == View.VISIBLE) {
                View parent = (View) stickerMakerBackgroundView.getParent();
                float alpha = Math.min(stickerMakerBackgroundView.getAlpha(), parent != null ? parent.getAlpha() : 1f);
                if (alpha > 0) {
                    canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int) (0xFF * alpha), Canvas.ALL_SAVE_FLAG);
                    stickerMakerBackgroundView.draw(canvas);
                    canvas.restore();
                }
            }
            if (isVisible) {
                blackPaint.setAlpha(backgroundDrawable.getAlpha());
                canvas.drawRect(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + insets.bottom, blackPaint);
            }
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (windowViewSkipRender) {
                return;
            }
            super.draw(canvas);
        }

        @Override
        protected void dispatchDraw(@NonNull Canvas canvas) {
            super.dispatchDraw(canvas);
            if (parentChatActivity != null) {
                View undoView = parentChatActivity.getUndoView();
                if (undoView != null && undoView.getVisibility() == View.VISIBLE) {
                    canvas.save();
                    View parent = (View) undoView.getParent();
                    canvas.clipRect(parent.getX(), parent.getY(), parent.getX() + parent.getWidth(), parent.getY() + parent.getHeight());
                    canvas.translate(undoView.getX(), undoView.getY());
                    undoView.draw(canvas);
                    canvas.restore();
                    invalidate();
                }
            }
        }
    }
}
