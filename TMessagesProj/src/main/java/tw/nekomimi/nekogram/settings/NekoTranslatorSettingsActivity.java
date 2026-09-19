package tw.nekomimi.nekogram.settings;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.LocaleController.getPluralString;
import static tw.nekomimi.nekogram.settings.NekoSettingsLocale.getString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.RestrictedLanguagesSelectActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import kotlin.Unit;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.ConfigCellCustom;
import tw.nekomimi.nekogram.config.cell.ConfigCellDivider;
import tw.nekomimi.nekogram.config.cell.ConfigCellHeader;
import tw.nekomimi.nekogram.config.cell.ConfigCellSelectBox;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextCheck;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextDetail;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextInput;
import tw.nekomimi.nekogram.translate.Translator;
import tw.nekomimi.nekogram.translate.TranslatorKt;
import tw.nekomimi.nekogram.utils.AndroidUtil;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("NotifyDataSetChanged")
public class NekoTranslatorSettingsActivity extends BaseNekoXSettingsActivity {

    @Override
    protected RecyclerListView.SelectionAdapter getListAdapter() {
        return listAdapter;
    }

    @Override
    protected CellGroup getCellGroup() {
        return cellGroup;
    }

    @Override
    protected String getSettingsPrefix() {
        return "translator";
    }

    private final CellGroup cellGroup = new CellGroup(this);
    private final AbstractConfigCell headerOptions = cellGroup.appendCell(new ConfigCellHeader(getString(R.string.TranslatorOptions)));
    private final AbstractConfigCell showTranslateRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.showTranslate, null, getString(R.string.ShowTranslateButton)));
    private final AbstractConfigCell useTelegramUIAutoTranslateRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getTelegramUIAutoTranslate()));
    private final AbstractConfigCell keepMarkdownRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getTranslatorKeepMarkdown()));
    private final AbstractConfigCell dividerOptions = cellGroup.appendCell(new ConfigCellDivider());

    // Translation
    private final AbstractConfigCell headerTranslation = cellGroup.appendCell(new ConfigCellHeader(getString(R.string.Translate)));
    private final AbstractConfigCell translationProviderRow = cellGroup.appendCell(new ConfigCellCustom(NekoConfig.translationProvider.getKey(), CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true, R.string.TranslationProvider));
    private final AbstractConfigCell translatorModeRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getTranslatorMode(), new String[]{
            getString(R.string.TranslatorWithOriginalTextOff),
            getString(R.string.TranslatorWithOriginalTextManualOnly),
            getString(R.string.TranslatorWithOriginalTextOn),
    }, null));
    private final AbstractConfigCell translateToLangRow = cellGroup.appendCell(new ConfigCellCustom("TranslateTo", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true, R.string.TransToLang));
    private final AbstractConfigCell doNotTranslateRow = cellGroup.appendCell(new ConfigCellCustom("DoNotTranslate", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell preferredTranslateTargetLangRow = cellGroup.appendCell(
            new ConfigCellTextInput(
                    getString(R.string.PreferredTranslateTargetLangName),
                    NaConfig.INSTANCE.getPreferredTranslateTargetLang(),
                    getString(R.string.PreferredTranslateTargetLangExample),
                    null,
                    (value) -> {
                        NaConfig.INSTANCE.getPreferredTranslateTargetLang().setConfigString(value);
                        NaConfig.INSTANCE.updatePreferredTranslateTargetLangList();
                        return value;
                    }
            )
    );
    private final AbstractConfigCell googleCloudTranslateKeyRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.googleCloudTranslateKey, (view, position) -> showConfigDialog(position, NekoConfig.googleCloudTranslateKey, getString(R.string.GoogleCloudTransKeyNotice), getString(R.string.LlmApiKey)), getString(R.string.None), true));
    private final AbstractConfigCell deepLTranslateKeyRow = cellGroup.appendCell(new ConfigCellTextDetail(NaConfig.INSTANCE.getDeepLTranslateKey(), (view, position) -> showConfigDialog(position, NaConfig.INSTANCE.getDeepLTranslateKey(), getString(R.string.DeepLTranslateKeyNotice), getString(R.string.LlmApiKey)), getString(R.string.None), true));

    private final AbstractConfigCell dividerTranslation = cellGroup.appendCell(new ConfigCellDivider());

    // article translation
    private final AbstractConfigCell headerArticleTranslation = cellGroup.appendCell(new ConfigCellHeader(getString(R.string.InstantViewTranslation)));
    private final AbstractConfigCell enableSeparateArticleTranslatorRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getEnableSeparateArticleTranslator()));
    private final AbstractConfigCell articleTranslationProviderRow = cellGroup.appendCell(new ConfigCellCustom("ArticleTranslationProvider", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell dividerArticleTranslation = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerExperimental = cellGroup.appendCell(new ConfigCellHeader(getString(R.string.Experimental)));
    private final AbstractConfigCell googleTranslateExpRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getGoogleTranslateExp()));
    private final AbstractConfigCell keepTranslatorPrefRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getKeepTranslatorPreferences(), getString(R.string.KeepTranslatorPreferencesNotice)));
    private final AbstractConfigCell dividerExperimental = cellGroup.appendCell(new ConfigCellDivider());

    private ListAdapter listAdapter;
    private final boolean isAutoTranslateEnabled;

    public NekoTranslatorSettingsActivity() {
        isAutoTranslateEnabled = NaConfig.INSTANCE.getTelegramUIAutoTranslate().Bool();
        rebuildRows();
        addRowsToMap(cellGroup);
    }

    private CharSequence getEnhancedSubtitleWithLink(ConfigItem bind, CharSequence originalSubtitle) {
        String providerUrl = getProviderKeyUrl(bind);
        if (providerUrl != null) {
            return AndroidUtilities.replaceSingleTag(
                    originalSubtitle + "\n**" + getString(R.string.HowToObtain) + "**",
                    -1,
                    AndroidUtilities.REPLACING_TAG_TYPE_LINKBOLD,
                    () -> Browser.openUrl(getParentActivity(), providerUrl),
                    getResourceProvider()
            );
        }
        return originalSubtitle;
    }

    private String getProviderKeyUrl(ConfigItem bind) {
        if (bind == NekoConfig.googleCloudTranslateKey) {
            return "https://console.cloud.google.com/apis/credentials";
        } else if (bind == NaConfig.INSTANCE.getDeepLTranslateKey()) {
            return "https://www.deepl.com/your-account/keys";
        }
        return null;
    }

    private void showProviderSelectionPopup(View view, ConfigItem configItem, Runnable onSelected) {
        List<ProviderInfo> filteredProviders = new ArrayList<>();
        for (ProviderInfo provider : ProviderInfo.PROVIDERS) {
            filteredProviders.add(provider);
        }
        int currentProvider = configItem.Int();
        String[] itemNames = new String[filteredProviders.size()];
        for (int i = 0; i < filteredProviders.size(); i++) {
            itemNames[i] = getString(filteredProviders.get(i).nameResId);
        }
        int[] selectedIndex = new int[]{-1};
        for (int i = 0; i < filteredProviders.size(); i++) {
            if (filteredProviders.get(i).providerConstant == currentProvider) {
                selectedIndex[0] = i;
                break;
            }
        }
        showSingleChoiceDialog(view.getContext(), R.string.TranslationProvider, itemNames, selectedIndex[0], null, i -> {
            configItem.setConfigInt(filteredProviders.get(i).providerConstant);
            onSelected.run();
        });
    }

    private String getProviderName(int providerConstant) {
        for (ProviderInfo info : ProviderInfo.PROVIDERS) {
            if (info.providerConstant == providerConstant) {
                return getString(info.nameResId);
            }
        }
        return "Unknown";
    }

    private record ProviderInfo(int providerConstant, int nameResId) {

        public static final ProviderInfo[] PROVIDERS = {
                new ProviderInfo(Translator.providerGoogle, R.string.ProviderGoogleTranslate),
                new ProviderInfo(Translator.providerYandex, R.string.ProviderYandexTranslate),
                new ProviderInfo(Translator.providerLingo, R.string.ProviderLingocloud),
                new ProviderInfo(Translator.providerMicrosoft, R.string.ProviderMicrosoftTranslator),
                new ProviderInfo(Translator.providerRealMicrosoft, R.string.ProviderRealMicrosoftTranslator),
                new ProviderInfo(Translator.providerDeepL, R.string.ProviderDeepLTranslate),
                new ProviderInfo(Translator.providerTelegram, R.string.ProviderTelegramAPI),
                new ProviderInfo(Translator.providerTranSmart, R.string.ProviderTranSmartTranslate),
        };
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        View superView = super.createView(context);

        listAdapter = new ListAdapter(context);

        listView.setAdapter(listAdapter);

        setupDefaultListeners();

        // Cells: Set OnSettingChanged Callbacks
        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NaConfig.INSTANCE.getPreferredTranslateTargetLang().getKey())) {
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(translateToLangRow));
            } else if (key.equals(NaConfig.INSTANCE.getEnableSeparateArticleTranslator().getKey())) {
                checkSeparateArticleTranslatorRows((boolean) newValue);
            } else if (key.equals(NaConfig.INSTANCE.getGoogleTranslateExp().getKey())) {
                checkTranslationKeyRows();
            }
        };

        return superView;
    }

    @Override
    protected void handleCellClick(View view, int position, float x, float y) {
        if (position == cellGroup.rows.indexOf(useTelegramUIAutoTranslateRow)) {
            int provider = NekoConfig.translationProvider.Int();
            boolean telegramUIAutoTranslateEnabled = NaConfig.INSTANCE.getTelegramUIAutoTranslate().Bool();
            boolean isRealPremium = UserConfig.getInstance(currentAccount).isPremium();
            if (provider == Translator.providerTelegram && !telegramUIAutoTranslateEnabled && !isRealPremium) {
                BulletinFactory.of(this).createSimpleBulletin(R.raw.info, getString(R.string.LoginEmailResetPremiumRequiredTitle)).show();
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                AndroidUtilities.shakeViewSpring(view, -4);
                return;
            }
        }
        super.handleCellClick(view, position, x, y);
    }

    @Override
    protected void onCustomCellClick(View view, int position, float x, float y) {
        if (position == cellGroup.rows.indexOf(translationProviderRow)) {
            showProviderSelectionPopup(view, NekoConfig.translationProvider, () -> {
                int provider = NekoConfig.translationProvider.Int();
                if (provider == Translator.providerTelegram) {
                    boolean isRealPremium = UserConfig.getInstance(currentAccount).isPremium();
                    if (isAutoTranslateEnabled && !isRealPremium) {
                        NaConfig.INSTANCE.getTelegramUIAutoTranslate().setConfigBool(false);
                        listAdapter.notifyItemChanged(cellGroup.rows.indexOf(useTelegramUIAutoTranslateRow));
                        BulletinFactory.of(this).createSimpleBulletin(R.raw.info, getString(R.string.LoginEmailResetPremiumRequiredTitle)).show();
                        AndroidUtil.showInputError(((ConfigCellTextCheck) useTelegramUIAutoTranslateRow).cell);
                    }
                } else {
                    NaConfig.INSTANCE.getTelegramUIAutoTranslate().setConfigBool(isAutoTranslateEnabled);
                    listAdapter.notifyItemChanged(cellGroup.rows.indexOf(useTelegramUIAutoTranslateRow));
                }
                checkTranslationKeyRows();
                listAdapter.notifyItemChanged(position);
            });
        } else if (position == cellGroup.rows.indexOf(translateToLangRow)) {
            Translator.showTargetLangSelect(view, false, (locale) -> {
                NekoConfig.translateToLang.setConfigString(TranslatorKt.getLocale2code(locale));
                listAdapter.notifyItemChanged(position);
                return Unit.INSTANCE;
            });
        } else if (position == cellGroup.rows.indexOf(doNotTranslateRow)) {
            presentFragment(new RestrictedLanguagesSelectActivity());
        } else if (position == cellGroup.rows.indexOf(articleTranslationProviderRow)) {
            showProviderSelectionPopup(view, NaConfig.INSTANCE.getArticleTranslationProvider(), () -> listAdapter.notifyItemChanged(position));
        }
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        protected void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof TextSettingsCell textCell) {
                if (position == cellGroup.rows.indexOf(translationProviderRow)) {
                    if (NekoConfig.translationProvider.Int() == Translator.providerTelegram) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            textCell.setTextAndValue(getString(R.string.TranslationProvider), addPremiumStar(getProviderName(NekoConfig.translationProvider.Int())), true);
                        } else {
                            textCell.setTextAndValue(getString(R.string.TranslationProvider), getProviderName(NekoConfig.translationProvider.Int()), true);
                        }
                    } else {
                        textCell.setTextAndValue(getString(R.string.TranslationProvider), getProviderName(NekoConfig.translationProvider.Int()), true);
                    }
                } else if (position == cellGroup.rows.indexOf(translateToLangRow)) {
                    String value = TextUtils.isEmpty(NekoConfig.translateToLang.String()) ? getString(R.string.TranslationTargetApp) : NekoXConfig.formatLang(NekoConfig.translateToLang.String());
                    textCell.setTextAndValue(getString(R.string.TransToLang), value, true);
                } else if (position == cellGroup.rows.indexOf(doNotTranslateRow)) {
                    textCell.setTextAndValue(getString(R.string.DoNotTranslate), getRestrictedLanguages(), true, true);
                } else if (position == cellGroup.rows.indexOf(articleTranslationProviderRow)) {
                    textCell.setTextAndValue(getString(R.string.ArticleTranslationProvider), getProviderName(NaConfig.INSTANCE.getArticleTranslationProvider().Int()), true);
                }
            }
        }

    }

    @Override
    public int getBaseGuid() {
        return 13000;
    }

    @Override
    public int getDrawable() {
        return R.drawable.ic_translate;
    }

    @Override
    public String getTitle() {
        return getString(R.string.TranslatorSettings);
    }

    private void rebuildRows() {
        cellGroup.rows.clear();

        cellGroup.appendCell(headerOptions);
        cellGroup.appendCell(showTranslateRow);
        cellGroup.appendCell(useTelegramUIAutoTranslateRow);
        cellGroup.appendCell(keepMarkdownRow);
        cellGroup.appendCell(dividerOptions);

        cellGroup.appendCell(headerTranslation);
        cellGroup.appendCell(translationProviderRow);
        cellGroup.appendCell(translatorModeRow);
        cellGroup.appendCell(translateToLangRow);
        cellGroup.appendCell(doNotTranslateRow);
        cellGroup.appendCell(preferredTranslateTargetLangRow);
        if (shouldShowGoogleCloudTranslateKeyRow()) {
            cellGroup.appendCell(googleCloudTranslateKeyRow);
        } else if (shouldShowDeepLTranslateKeyRow()) {
            cellGroup.appendCell(deepLTranslateKeyRow);
        }
        cellGroup.appendCell(dividerTranslation);

        cellGroup.appendCell(headerArticleTranslation);
        cellGroup.appendCell(enableSeparateArticleTranslatorRow);
        if (NaConfig.INSTANCE.getEnableSeparateArticleTranslator().Bool()) {
            cellGroup.appendCell(articleTranslationProviderRow);
        }
        cellGroup.appendCell(dividerArticleTranslation);

        cellGroup.appendCell(headerExperimental);
        cellGroup.appendCell(googleTranslateExpRow);
        cellGroup.appendCell(keepTranslatorPrefRow);
        cellGroup.appendCell(dividerExperimental);
    }

    private SpannableString premiumStar;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private CharSequence addPremiumStar(String text) {
        if (premiumStar == null) {
            premiumStar = new SpannableString("★");
            Drawable drawable = new AnimatedEmojiDrawable.WrapSizeDrawable(PremiumGradient.getInstance().premiumStarMenuDrawable, dp(18), dp(18));
            drawable.setBounds(0, 0, dp(18), dp(18));
            premiumStar.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_CENTER), 0, premiumStar.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return new SpannableStringBuilder(text).append("  ").append(premiumStar);
    }

    private String getRestrictedLanguages() {
        HashSet<String> langCodes = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
        if (langCodes.isEmpty()) return "";
        String doNotTranslateCellValue = null;
        try {
            if (langCodes.size() < 3) {
                List<String> names = new ArrayList<>();
                for (String lang : langCodes) {
                    String name = TranslateAlert2.languageName(lang, null);
                    if (name != null) {
                        names.add(TranslateAlert2.capitalFirst(name));
                    }
                }
                doNotTranslateCellValue = TextUtils.join(", ", names);
            }
        } catch (Exception ignore) {
        }
        if (TextUtils.isEmpty(doNotTranslateCellValue)) {
            doNotTranslateCellValue = String.format(getPluralString("Languages", langCodes.size()), langCodes.size());
        }
        return doNotTranslateCellValue;
    }

    /** @noinspection deprecation*/
    private void showConfigDialog(int position, ConfigItem bind, String subtitle, String hint) {
        Context context = getParentActivity();
        if (context == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, getResourceProvider());
        builder.setTitle(getString(bind.getKey()));
        builder.setCustomViewOffset(0);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditTextBoldCursor editText = new EditTextBoldCursor(context);
        editText.setSingleLine(true);
        editText.setTextSize(18);
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        editText.setBackground(Theme.createEditTextDrawable(context, true));
        editText.setHint(hint);
        editText.setText(bind.String());
        editText.setSelection(editText.length());
        layout.addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 0, 24, 0, 24, 0));

        builder.setMessage(getEnhancedSubtitleWithLink(bind, subtitle));
        builder.setView(layout);
        builder.setNegativeButton(getString(R.string.Cancel), null);
        builder.setPositiveButton(getString(R.string.OK), null);

        AlertDialog dialog = builder.create();
        showDialog(dialog);
        View button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (button != null) {
            button.setOnClickListener(v -> {
                String value = editText.getText() != null ? editText.getText().toString().trim() : "";
                bind.setConfigString(value.isEmpty() ? null : value);
                if (listAdapter != null) {
                    listAdapter.notifyItemChanged(position);
                }
                dialog.dismiss();
            });
        }
    }

    private void checkSeparateArticleTranslatorRows(boolean enabled) {
        if (NaConfig.INSTANCE.getEnableSeparateArticleTranslator().Bool() != enabled) {
            NaConfig.INSTANCE.getEnableSeparateArticleTranslator().setConfigBool(enabled);
        }
        if (enabled) {
            if (!cellGroup.rows.contains(articleTranslationProviderRow)) {
                final int index = cellGroup.rows.indexOf(enableSeparateArticleTranslatorRow) + 1;
                cellGroup.rows.add(index, articleTranslationProviderRow);
                listAdapter.notifyItemInserted(index);
            }
        } else {
            if (cellGroup.rows.contains(articleTranslationProviderRow)) {
                final int index = cellGroup.rows.indexOf(articleTranslationProviderRow);
                cellGroup.rows.remove(articleTranslationProviderRow);
                listAdapter.notifyItemRemoved(index);
            }
        }
        listAdapter.notifyItemChanged(cellGroup.rows.indexOf(enableSeparateArticleTranslatorRow));
        addRowsToMap(cellGroup);
    }

    private boolean shouldShowGoogleCloudTranslateKeyRow() {
        return NekoConfig.translationProvider.Int() == Translator.providerGoogle && !NaConfig.INSTANCE.getGoogleTranslateExp().Bool();
    }

    private boolean shouldShowDeepLTranslateKeyRow() {
        return NekoConfig.translationProvider.Int() == Translator.providerDeepL;
    }

    private void checkTranslationKeyRows() {
        boolean changed = false;
        changed |= removeTranslationKeyRowIfHidden(googleCloudTranslateKeyRow, shouldShowGoogleCloudTranslateKeyRow());
        changed |= removeTranslationKeyRowIfHidden(deepLTranslateKeyRow, shouldShowDeepLTranslateKeyRow());
        changed |= addTranslationKeyRowIfVisible(googleCloudTranslateKeyRow, shouldShowGoogleCloudTranslateKeyRow());
        changed |= addTranslationKeyRowIfVisible(deepLTranslateKeyRow, shouldShowDeepLTranslateKeyRow());
        if (changed) {
            addRowsToMap(cellGroup);
        }
    }

    private boolean removeTranslationKeyRowIfHidden(AbstractConfigCell row, boolean visible) {
        int index = cellGroup.rows.indexOf(row);
        if (visible || index == -1) {
            return false;
        }
        cellGroup.rows.remove(row);
        if (listAdapter != null) {
            listAdapter.notifyItemRemoved(index);
        }
        return true;
    }

    private boolean addTranslationKeyRowIfVisible(AbstractConfigCell row, boolean visible) {
        if (!visible || cellGroup.rows.contains(row)) {
            return false;
        }
        int index = cellGroup.rows.indexOf(dividerTranslation);
        if (index < 0) {
            index = cellGroup.rows.indexOf(preferredTranslateTargetLangRow) + 1;
        }
        row.bindCellGroup(cellGroup);
        cellGroup.rows.add(index, row);
        if (listAdapter != null) {
            listAdapter.notifyItemInserted(index);
        }
        return true;
    }
}
