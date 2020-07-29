package space.badboyin.smap.utilities;


import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TextUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private static SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yy HH:mm");
    private static SimpleDateFormat sdfmy = new SimpleDateFormat("MM - yyyy");
    private static SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");

    private static SimpleDateFormat fromUserDate = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat timeFormatServer = new SimpleDateFormat("HH:mm:ss");


    public static long formatTanggal(@NonNull String input) {
        try {
            Date date = sdf.parse(input);
            return date.getTime();
        } catch (ParseException e) {
        }
        return 0;
    }


    public static String formatTanggal(@NonNull long input) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(input);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
        }
        return "-";
    }

    public static String formatYear(@NonNull long input) {
        if (input == 0)
            return "-";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);
        return sdfy.format(calendar.getTime());
    }

    public static String formatMonthYear(@NonNull long input) {
        if (input == 0)
            return "-";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);
        return sdfmy.format(calendar.getTime());
    }

    public static String formatWaktu(@NonNull String input) {
        if (input == null || input.isEmpty())
            return "-";
        try {
            return timeFormat.format(timeFormatServer.parse(input));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-x";
        }
    }

    public static long formatTimeSort(@NonNull String input) {
        if (input.isEmpty())
            return 0;
        try {
            return fromUserDate.parse(input).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String tanggalSort() {
        Calendar calendar = Calendar.getInstance();
        return sdfs.format(calendar.getTime());
    }

    public static String tanggal() {
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }


    public static String formatTanggalSort(@NonNull long input) {
        if (input == 0)
            return "0";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);
        return sdfs.format(calendar.getTime());
    }

    private static DecimalFormat decimalFormatIdn = (DecimalFormat) DecimalFormat.getCurrencyInstance();


    static {
        DecimalFormatSymbols formatCurr = new DecimalFormatSymbols(Locale.US);
        formatCurr.setCurrencySymbol("");
        formatCurr.setGroupingSeparator('.');
        decimalFormatIdn.setDecimalFormatSymbols(formatCurr);
    }

    public static String formatCurrencyIdn(@NonNull String input) {
        if (input == null || input.isEmpty())
            return input;
        try {
            String res = decimalFormatIdn.format(Double.parseDouble(input));
            if (res.length() > 3 && (res.charAt(res.length() - 3) == '.' || res.charAt(res.length() - 3) == ','))
                return res.substring(0, res.length() - 3);
            return res;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return input;
        }
    }

    public static String formatCurrencyIdn(@NonNull double input) {
        if (input == 0)
            return "0";
        try {
            String res = decimalFormatIdn.format(input);
            if (res.length() > 3 && (res.charAt(res.length() - 3) == '.' || res.charAt(res.length() - 3) == ','))
                return res.substring(0, res.length() - 3);
            return res;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            try {
                return String.valueOf(input);
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
                return "0";
            }
        }
    }

    public static double removeFormatCurrencyIdn(String arg0) {
        try {
            return Double.parseDouble(arg0.replaceAll("[Rp,.]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
