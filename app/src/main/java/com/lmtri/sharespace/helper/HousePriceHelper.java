package com.lmtri.sharespace.helper;

import android.content.Context;
import android.support.v4.util.Pair;

import com.lmtri.sharespace.R;

import java.util.Locale;

/**
 * Created by lmtri on 8/6/2017.
 */

public class HousePriceHelper {

    /**
     * Parse an unformatted house price by rounding it and removing its trailing zeros.
     *
     * @param   price      house price.
     * @param   context    current context to get <code>String</code> resources.
     * @return  a {@link Pair} of <code>String</code> containing a formatted house
     *          price with no trailing zeros and its respective price unit.
     */
    public static Pair<String, String> parseForHousing(int price, Context context) {
        if (price < 0) {
            return Pair.create(null, context.getString(R.string.activity_post_house_detailed_item_price_deal));
        } else if (Math.round(price / 1000f) <= 999) {
            return Pair.create(format(price / 1000f), context.getString(R.string.activity_post_house_detailed_item_price_thousand_per_month));
        }
        return Pair.create(format(price / 1000000f), context.getString(R.string.activity_post_house_detailed_item_price_million_per_month));
    }

    public static Pair<String, String> parseForShareHousing(int price, Context context) {
        if (price < 0) {
            return Pair.create(null, context.getString(R.string.activity_post_house_detailed_item_price_deal));
        } else if (Math.round(price / 1000f) <= 999) {
            return Pair.create(format(price / 1000f), context.getString(R.string.activity_post_share_house_detailed_item_price_thousand_per_month_per_one));
        }
        return Pair.create(format(price / 1000000f), context.getString(R.string.activity_post_share_house_detailed_item_price_million_per_month_per_one));
    }

    public static int convertForHousing(float price, String priceUnit, Context context) {
        if (priceUnit.equalsIgnoreCase(context.getString(R.string.activity_post_house_detailed_item_price_thousand_per_month))) {
            return (int) (price * 1000);
        } else if (priceUnit.equalsIgnoreCase(context.getString(R.string.activity_post_house_detailed_item_price_million_per_month))) {
            return (int) (price * 1000000);
        }
        return -1;  // Price 'Deal'.
    }

    public static int convertForShareHousing(float price, String priceUnit, Context context) {
        if (priceUnit.equalsIgnoreCase(context.getString(R.string.activity_post_share_house_detailed_item_price_thousand_per_month_per_one))) {
            return (int) (price * 1000);
        } else if (priceUnit.equalsIgnoreCase(context.getString(R.string.activity_post_share_house_detailed_item_price_million_per_month_per_one))) {
            return (int) (price * 1000000);
        }
        return -1;  // Price 'Deal'.
    }

    private static String format(float f) {
        if (f == (int) f) {
            return String.format(Locale.US, "%d", (int) f);
        }
        return String.format(Locale.US, "%s", f);
    }
}
