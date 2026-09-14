package tw.nekomimi.nekogram.ui;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.HashMap;

/** Lets the user edit message text/captions before sending a no-attribution copy. */
public class AdvancedForwardActivity extends BaseFragment {

    private static final int MENU_DONE = 1;

    public interface Delegate {
        void onPrepared(HashMap<String, String> editedTexts);
    }

    private final ArrayList<MessageObject> messages;
    private final HashMap<String, EditTextBoldCursor> editors = new HashMap<>();
    private Delegate delegate;
    private boolean submitting;

    public AdvancedForwardActivity(ArrayList<MessageObject> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(getString(R.string.AdvancedForward));
        actionBar.setAllowOverlayTitle(false);
        actionBar.createMenu().addItemWithWidth(MENU_DONE, R.drawable.ic_ab_done, dp(56), getString(R.string.Next));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == MENU_DONE) {
                    submit();
                }
            }
        });

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(12), dp(16), dp(24));
        scrollView.addView(content, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));

        TextView help = new TextView(context);
        help.setText(getString(R.string.AdvancedForwardHelp));
        help.setTextSize(14);
        help.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        help.setGravity(Gravity.START);
        help.setLineSpacing(dp(2), 1.0f);
        content.addView(help, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 12));

        int editableCount = 0;
        for (int i = 0; i < messages.size(); i++) {
            MessageObject message = messages.get(i);
            String text = getEditableText(message);
            if (text == null) {
                continue;
            }
            editableCount++;

            LinearLayout card = new LinearLayout(context);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(12), dp(12), dp(12));
            GradientDrawable cardBackground = new GradientDrawable();
            cardBackground.setColor(getThemedColor(Theme.key_windowBackgroundGray));
            cardBackground.setCornerRadius(dp(12));
            cardBackground.setStroke(dp(1), getThemedColor(Theme.key_divider));
            card.setBackground(cardBackground);

            TextView label = new TextView(context);
            String itemTitle = getMediaLabel(message);
            if (messages.size() > 1) {
                itemTitle = editableCount + ". " + itemTitle;
            }
            label.setText(itemTitle);
            label.setTextSize(14);
            label.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
            label.setGravity(Gravity.START);
            card.addView(label, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 8));

            addMediaPreview(context, card, message);

            EditTextBoldCursor editor = new EditTextBoldCursor(context);
            editor.setText(text);
            editor.setTextSize(16);
            editor.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            editor.setHintTextColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
            editor.setHint(getString(R.string.AdvancedForwardHint));
            editor.setGravity(Gravity.TOP | Gravity.START);
            editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            editor.setSingleLine(false);
            editor.setMinHeight(dp(message.isPhoto() || message.getDocument() != null ? 84 : 112));
            editor.setPadding(dp(12), dp(10), dp(12), dp(10));
            GradientDrawable background = new GradientDrawable();
            background.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
            background.setCornerRadius(dp(10));
            background.setStroke(dp(1), getThemedColor(Theme.key_divider));
            editor.setBackground(background);
            editor.setSelection(editor.length());
            editors.put(getMessageKey(message), editor);
            card.addView(editor, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            content.addView(card, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 12));
        }

        if (editableCount == 0) {
            TextView info = new TextView(context);
            info.setText(getString(R.string.AdvancedForwardNoText));
            info.setTextSize(16);
            info.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
            info.setGravity(Gravity.CENTER);
            content.addView(info, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 48, 0, 0));
        } else if (editors.size() == 1) {
            EditTextBoldCursor editor = editors.values().iterator().next();
            AndroidUtilities.runOnUIThread(() -> {
                editor.requestFocus();
                AndroidUtilities.showKeyboard(editor);
            }, 200);
        }

        fragmentView = scrollView;
        return fragmentView;
    }

    private void addMediaPreview(Context context, LinearLayout card, MessageObject message) {
        if (message == null || message.photoThumbs == null || message.photoThumbs.isEmpty()) {
            return;
        }
        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640, true, null, true);
        if (thumb == null) {
            return;
        }
        BackupImageView imageView = new BackupImageView(context);
        imageView.setRoundRadius(dp(10));
        imageView.getImageReceiver().setAspectFit(false);
        imageView.setImage(
                ImageLocation.getForObject(thumb, message.photoThumbsObject),
                "640_360",
                message.strippedThumb,
                thumb.size,
                message
        );
        card.addView(imageView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 180, 0, 0, 0, 10));
    }

    private String getMediaLabel(MessageObject message) {
        if (message.isPhoto()) {
            return getString(R.string.AttachPhoto) + " · " + getString(R.string.AdvancedForwardText);
        }
        if (message.isVideo()) {
            return getString(R.string.AttachVideo) + " · " + getString(R.string.AdvancedForwardText);
        }
        if (message.isGif()) {
            return getString(R.string.AttachGif) + " · " + getString(R.string.AdvancedForwardText);
        }
        if (message.isVoice()) {
            return getString(R.string.AttachAudio) + " · " + getString(R.string.AdvancedForwardText);
        }
        if (message.isMusic()) {
            return getString(R.string.AttachMusic) + " · " + getString(R.string.AdvancedForwardText);
        }
        if (message.getDocument() != null) {
            return getString(R.string.AttachDocument) + " · " + getString(R.string.AdvancedForwardText);
        }
        return getString(R.string.Message) + " · " + getString(R.string.AdvancedForwardText);
    }

    private String getEditableText(MessageObject message) {
        if (message == null || message.messageOwner == null || message.isAnyKindOfSticker()) {
            return null;
        }
        CharSequence caption = org.telegram.ui.ChatActivity.getMessageCaption(message, null, null);
        if (!TextUtils.isEmpty(caption)) {
            return caption.toString();
        }
        if (!TextUtils.isEmpty(message.messageOwner.message)) {
            return message.messageOwner.message;
        }
        return message.isPhoto() || message.getDocument() != null ? "" : null;
    }

    private void submit() {
        if (submitting) {
            return;
        }
        submitting = true;
        HashMap<String, String> editedTexts = new HashMap<>();
        for (MessageObject message : messages) {
            String messageKey = getMessageKey(message);
            EditTextBoldCursor editor = editors.get(messageKey);
            if (editor != null) {
                editedTexts.put(messageKey, editor.getText().toString());
            }
        }
        Delegate currentDelegate = delegate;
        AndroidUtilities.hideKeyboard(fragmentView);
        // Close synchronously before opening the dialogs picker. Presenting the
        // picker while the editor's close animation is still running is rejected
        // by ActionBarLayout, leaving the user with no destination selection.
        boolean closed = finishFragment(false);
        if (!closed) {
            submitting = false;
            return;
        }
        if (currentDelegate != null) {
            currentDelegate.onPrepared(editedTexts);
        }
    }

    public static String getMessageKey(MessageObject message) {
        return message.getDialogId() + ":" + message.getId();
    }
}
