package tw.nekomimi.nekogram.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.StringRes;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;

import java.util.Locale;

/** English-only strings for the N-Settings surface, independent of app language. */
public final class NekoSettingsLocale {

    private static volatile Resources englishResources;

    private NekoSettingsLocale() {
    }

    private static Resources resources() {
        Resources result = englishResources;
        if (result == null) {
            synchronized (NekoSettingsLocale.class) {
                result = englishResources;
                if (result == null) {
                    Context context = ApplicationLoader.applicationContext;
                    Configuration configuration = new Configuration(context.getResources().getConfiguration());
                    configuration.setLocale(Locale.ENGLISH);
                    configuration.setLayoutDirection(Locale.ENGLISH);
                    result = context.createConfigurationContext(configuration).getResources();
                    englishResources = result;
                }
            }
        }
        return result;
    }

    public static String getString(@StringRes int resId) {
        try {
            return resources().getString(resId);
        } catch (Exception ignore) {
            return "LOC_ERR:" + resId;
        }
    }

    public static String getString(String key) {
        int resId = LocaleController.getStringResId(key);
        return resId == 0 ? key : getString(resId);
    }

    public static String formatString(@StringRes int resId, Object... args) {
        try {
            return resources().getString(resId, args);
        } catch (Exception ignore) {
            return getString(resId);
        }
    }

    public static String formatString(String key, Object... args) {
        int resId = LocaleController.getStringResId(key);
        return resId == 0 ? key : formatString(resId, args);
    }

    public static String formatPluralString(String key, int count) {
        int resId = LocaleController.getStringResId(key + (count == 1 ? "_one" : "_other"));
        return resId == 0 ? key : formatString(resId, count);
    }
}
