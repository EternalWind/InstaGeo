package com.etern.locationscout;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by etern on 6/12/2017.
 */

public final class Helpers {
    private Helpers() {}

    public interface Transformer<T1, T2> {
        T2 transform(T1 orig);
    }

    public static <T1, T2> List<T2> map(List<T1> orig, Transformer transformer)
    {
        List<T2> result = new ArrayList<>(orig.size());
        for (T1 obj: orig) {
            result.add((T2)transformer.transform(obj));
        }

        return result;
    }

    public static String getAppName(Context context) {
        return context.getResources().getString(R.string.app_name);
    }

    public static void log(Context context, String msg) {
        Log.d(getAppName(context), msg);
    }
}
