package wang.julis.jproject.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.julis.distance.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************
 *
 * Created by julis.wang on 2021/05/10 10:40
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class StringUtils {
    private static Locale locale;

    //Get all strings.xml's string.
    private static Map<String, String> getStringValueKeyMap(Context context) {
        Map<String, String> languageKeyMap = new HashMap<>();
        Resources resources = context.getResources();
        if (locale != null) {
            resources = getResourcesByLocale(resources, locale);
        }

        int resourceId;
        String resourceString;

        String packageName = context.getPackageName();
        try {
            Class rStringClass = Class.forName(packageName + ".R$string");
            Field[] stringFields = rStringClass.getFields();
            for (Field stringField : stringFields) {
                try {
                    resourceId = stringField.getInt(R.string.class);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
                resourceString = resources.getString(resourceId);
                if (!TextUtils.isEmpty(resourceString)) { // replace "%1s$" with "%s"
                    Pattern p = Pattern.compile("%\\d\\$s");
                    Matcher matcher = p.matcher(resourceString);
                    String result = matcher.replaceAll("%s");
                    languageKeyMap.put(stringField.getName(), result);
                }
            }
            return languageKeyMap;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private static Resources getResourcesByLocale(Resources res, Locale locale) {
        Configuration conf = new Configuration(res.getConfiguration());
        conf.locale = locale;
        return new Resources(res.getAssets(), res.getDisplayMetrics(), conf);
    }


}
