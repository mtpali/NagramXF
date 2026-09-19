package tw.nekomimi.nekogram.menu.translate;

import static org.telegram.messenger.LocaleController.getString;

import org.telegram.messenger.R;

import java.util.HashMap;

public class TranslateItem {
    public static final int ID_CHANGE_PROVIDER = 2035;

    static final int[] ITEM_IDS = new int[]{
            ID_CHANGE_PROVIDER,
    };

    static final HashMap<Integer, String> ITEM_TITLES = new HashMap<>() {{
        put(ID_CHANGE_PROVIDER, getString(R.string.ChangeTranslateProvider));
    }};
}
