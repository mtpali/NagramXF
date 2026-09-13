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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
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

        int editableCount = 0;
        for (int i = 0; i < messages.size(); i++) {
            MessageObject message = messages.get(i);
            String text = getEditableText(message);
            if (text == null) {
                continue;
            }
            editableCount++;

            TextView label = new TextView(context);
            label.setText(messages.size() == 1
                    ? getString(R.string.AdvancedForwardText)
                    : getString(R.string.AdvancedForwardText) + " " + editableCount);
            label.setTextSize(14);
            label.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
            label.setGravity(Gravity.LEFT);
            content.addView(label, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 12, 0, 8));

            EditTextBoldCursor editor = new EditTextBoldCursor(context);
            editor.setText(text);
            editor.setTextSize(16);
            editor.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            editor.setHintTextColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
            editor.setHint(getString(R.string.AdvancedForwardHint));
            editor.setGravity(Gravity.TOP | Gravity.LEFT);
            editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            editor.setSingleLine(false);
            editor.setMinHeight(dp(112));
            editor.setPadding(dp(12), dp(10), dp(12), dp(10));
            GradientDrawable background = new GradientDrawable();
            background.setColor(getThemedColor(Theme.key_windowBackgroundGray));
            background.setCornerRadius(dp(10));
            background.setStroke(dp(1), getThemedColor(Theme.key_divider));
            editor.setBackground(background);
            editor.setSelection(editor.length());
            editors.put(getMessageKey(message), editor);
            content.addView(editor, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
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
        HashMap<String, String> editedTexts = new HashMap<>();
        for (MessageObject message : messages) {
            String messageKey = getMessageKey(message);
            EditTextBoldCursor editor = editors.get(messageKey);
            if (editor != null) {
                editedTexts.put(messageKey, editor.getText().toString());
            }
        }
        Delegate currentDelegate = delegate;
        finishFragment();
        if (currentDelegate != null) {
            AndroidUtilities.runOnUIThread(() -> currentDelegate.onPrepared(editedTexts), 180);
        }
    }

    public static String getMessageKey(MessageObject message) {
        return message.getDialogId() + ":" + message.getId();
    }
}
