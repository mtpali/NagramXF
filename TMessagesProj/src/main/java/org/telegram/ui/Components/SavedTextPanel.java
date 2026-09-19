package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

import java.util.ArrayList;

/** A small local library of reusable text snippets shown as an EmojiView page. */
public class SavedTextPanel extends FrameLayout {

    public interface Listener {
        void onTextSelected(String text);
    }

    private static final String PREF_KEY = "saved_text_snippets_v1";

    private final Theme.ResourcesProvider resourcesProvider;
    private final Listener listener;
    private final ArrayList<String> texts = new ArrayList<>();
    private final RecyclerListView listView;
    private final TextView emptyView;
    private final Adapter adapter;

    public SavedTextPanel(Context context, Theme.ResourcesProvider resourcesProvider, Listener listener) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.listener = listener;
        setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground, resourcesProvider));

        TextView addButton = new TextView(context);
        addButton.setText(LocaleController.getString(R.string.AddSavedText));
        addButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        addButton.setTypeface(Typeface.DEFAULT_BOLD);
        addButton.setGravity(Gravity.CENTER_VERTICAL);
        addButton.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);
        addButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider));
        addButton.setBackground(Theme.getSelectorDrawable(false));
        addButton.setOnClickListener(v -> showEditDialog(-1));
        addView(addButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.TOP));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter = new Adapter());
        listView.setOnItemClickListener((view, position) -> {
            if (position >= 0 && position < texts.size() && listener != null) {
                listener.onTextSelected(texts.get(position));
            }
        });
        listView.setOnItemLongClickListener((view, position) -> {
            if (position < 0 || position >= texts.size()) return false;
            new AlertDialog.Builder(getContext(), resourcesProvider)
                    .setItems(new CharSequence[]{
                            LocaleController.getString(R.string.Edit),
                            LocaleController.getString(R.string.Delete)
                    }, (dialog, which) -> {
                        if (which == 0) {
                            showEditDialog(position);
                        } else {
                            texts.remove(position);
                            save();
                            refresh();
                        }
                    })
                    .show();
            return true;
        });
        addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 48, 0, 0));

        emptyView = new TextView(context);
        emptyView.setText(LocaleController.getString(R.string.NoSavedTexts));
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 24, 48, 24, 0));
        listView.setEmptyView(emptyView);

        load();
        refresh();
    }

    public void refresh() {
        load();
        adapter.notifyDataSetChanged();
    }

    private void showEditDialog(int index) {
        Context context = getContext();
        EditTextBoldCursor input = new EditTextBoldCursor(context);
        input.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        input.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        input.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint, resourcesProvider));
        input.setHint(LocaleController.getString(R.string.SavedTextHint));
        input.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setMinLines(3);
        input.setMaxLines(8);
        input.setBackground(Theme.createEditTextDrawable(context, true));
        if (index >= 0 && index < texts.size()) {
            input.setText(texts.get(index));
            input.setSelection(input.length());
        }

        FrameLayout container = new FrameLayout(context);
        container.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(8), AndroidUtilities.dp(24), 0);
        container.addView(input, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        AlertDialog dialog = new AlertDialog.Builder(context, resourcesProvider)
                .setTitle(LocaleController.getString(index >= 0 ? R.string.EditSavedText : R.string.AddSavedText))
                .setView(container)
                .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
                .setPositiveButton(LocaleController.getString(R.string.Save), null)
                .create();
        dialog.setOnShowListener(d -> {
            View positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                String value = input.getText() == null ? "" : input.getText().toString().trim();
                if (TextUtils.isEmpty(value)) {
                    AndroidUtilities.shakeViewSpring(input, -4);
                    return;
                }
                if (index >= 0 && index < texts.size()) {
                    texts.set(index, value);
                } else {
                    texts.add(0, value);
                }
                save();
                refresh();
                dialog.dismiss();
            });
        });
        dialog.show();
        input.requestFocus();
        AndroidUtilities.showKeyboard(input);
    }

    private void load() {
        texts.clear();
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        String raw = preferences.getString(PREF_KEY, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                String text = array.optString(i, "").trim();
                if (!TextUtils.isEmpty(text)) texts.add(text);
            }
        } catch (Exception ignore) {
        }
    }

    private void save() {
        JSONArray array = new JSONArray();
        for (String text : texts) array.put(text);
        MessagesController.getGlobalEmojiSettings().edit().putString(PREF_KEY, array.toString()).apply();
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override
        public int getItemCount() {
            return texts.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            textView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
            textView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10));
            textView.setMinHeight(AndroidUtilities.dp(52));
            textView.setMaxLines(4);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setBackground(Theme.getSelectorDrawable(false));
            return new RecyclerListView.Holder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(texts.get(position));
        }
    }
}
