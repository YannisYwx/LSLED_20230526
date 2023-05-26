
package com.yannis.ledcard.util;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.Character.UnicodeBlock;
import java.util.Locale;

public class LangUtils {

    private static final String JAPANESE_LANGUAGE = Locale.JAPANESE.getLanguage().toLowerCase();
    private static final String KOREAN_LANGUAGE = Locale.KOREAN.getLanguage().toLowerCase();
    // This includes simplified and traditional Chinese
    private static final String CHINESE_LANGUAGE = Locale.CHINESE.getLanguage().toLowerCase();

    public static boolean isUserFontLib(@Nullable String text) {
        boolean isUserFontLib;
        assert text != null;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            //首先检验是否是数字
            boolean isAscll = false;
            boolean isCJK = false;
            if (c < 0x80) {
                //说明是数字或者英文或者字符
                isAscll = true;
            } else {
                String tempText = text.substring(i, i + 1);
                boolean isC = isChinese(tempText);
                boolean isJ = isJapanese(tempText);
                boolean isK = isKorean(tempText);
                boolean isB = true;
                //               boolean isB =true;
                if (c < 0x3000 || c > 0x9FFF)
                    if (c < 0x0391 || c > 0x03c9)
                        if (c < 0x0410 || c > 0x0451)
                            if (c < 0x2014 || c > 0x203b)
                                if (c < 0x2160 || c > 0x2193)
                                    if (c < 0x1100 || c > 0x11FF)
                                        if (c > 0x80)
                                            if (c < 0xff00)
                                                isB = false;
                isCJK = isC || isJ || isK || isB;
            }
            isUserFontLib = isAscll || isCJK;
            if (!isUserFontLib) {
                return false;
            }
        }
        return true;
    }

    /**
     * 该字符是否来自字库
     *
     * @param c
     * @return
     */
    public static boolean charIsFromLib(char c) {
        boolean isUserFontLib;
        boolean isAscll = false;
        boolean isCJK = false;
        if (c < 0x80) {
            //说明是数字或者英文或者字符
            isAscll = true;
        } else {
            String tempText = String.valueOf(c);
            boolean isC = isChinese(tempText);
            boolean isJ = isJapanese(tempText);
            boolean isK = isKorean(tempText);
            isCJK = isC || isJ || isK;
            Log.e("abc", tempText + " isC = " + isC + " isJ = " + isJ + " isC = " + isK);
        }
        isUserFontLib = isAscll || isCJK;
        Log.e("abc", " isAscll = " + isAscll + " is C J K = " + isCJK);
        return isUserFontLib;
    }

    /**
     * @return true if the text contains Chinese characters
     */
    public static boolean isChinese(String text) {
        int fullNameStyle = guessFullNameStyle(text);
        if (fullNameStyle == FullNameStyle.CJK) {
            fullNameStyle = getAdjustedFullNameStyle(fullNameStyle);
        }
        return fullNameStyle == FullNameStyle.CHINESE;
    }

    public static boolean isJapanese(String text) {
        int fullNameStyle = guessFullNameStyle(text);
        if (fullNameStyle == FullNameStyle.CJK) {
            fullNameStyle = getAdjustedFullNameStyle(fullNameStyle);
        }
        return fullNameStyle == FullNameStyle.JAPANESE;
    }

    public static boolean isCJK(String text) {
        return guessFullNameStyle(text) == FullNameStyle.CJK;
    }

    public static boolean isKorean(String text) {
        int fullNameStyle = guessFullNameStyle(text);
        if (fullNameStyle == FullNameStyle.CJK) {
            fullNameStyle = getAdjustedFullNameStyle(fullNameStyle);
        }
        return fullNameStyle == FullNameStyle.KOREAN;
    }

    public static int guessFullNameStyle(String name) {
        if (name == null) {
            return FullNameStyle.UNDEFINED;
        }

        int nameStyle = FullNameStyle.UNDEFINED;
        int length = name.length();
        int offset = 0;
        while (offset < length) {
            int codePoint = Character.codePointAt(name, offset);
            if (Character.isLetter(codePoint)) {
                UnicodeBlock unicodeBlock = UnicodeBlock.of(codePoint);

                if (!isLatinUnicodeBlock(unicodeBlock)) {

                    if (isCJKUnicodeBlock(unicodeBlock)) {
                        // We don't know if this is Chinese, Japanese or Korean -
                        // trying to figure out by looking at other characters in the name
                        return guessCJKNameStyle(name, offset + Character.charCount(codePoint));
                    }

                    if (isJapanesePhoneticUnicodeBlock(unicodeBlock)) {
                        return FullNameStyle.JAPANESE;
                    }

                    if (isKoreanUnicodeBlock(unicodeBlock)) {
                        return FullNameStyle.KOREAN;
                    }
                }
                nameStyle = FullNameStyle.WESTERN;
            }
            offset += Character.charCount(codePoint);
        }
        return nameStyle;
    }

    /**
     * If the supplied name style is undefined, returns a default based on the
     * language, otherwise returns the supplied name style itself.
     *
     * @param nameStyle See {@link FullNameStyle}.
     */
    public static int getAdjustedFullNameStyle(int nameStyle) {
        String mLanguage = Locale.getDefault().getLanguage().toLowerCase();
        if (nameStyle == FullNameStyle.UNDEFINED) {
            if (JAPANESE_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.JAPANESE;
            } else if (KOREAN_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.KOREAN;
            } else if (CHINESE_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.CHINESE;
            } else {
                return FullNameStyle.WESTERN;
            }
        } else if (nameStyle == FullNameStyle.CJK) {
            if (JAPANESE_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.JAPANESE;
            } else if (KOREAN_LANGUAGE.equals(mLanguage)) {
                return FullNameStyle.KOREAN;
            } else {
                return FullNameStyle.CHINESE;
            }
        }
        return nameStyle;
    }

    private static boolean isLatinUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.BASIC_LATIN ||
                unicodeBlock == UnicodeBlock.LATIN_1_SUPPLEMENT ||
                unicodeBlock == UnicodeBlock.LATIN_EXTENDED_A ||
                unicodeBlock == UnicodeBlock.LATIN_EXTENDED_B ||
                unicodeBlock == UnicodeBlock.LATIN_EXTENDED_ADDITIONAL;
    }

    private static boolean isCJKUnicodeBlock(UnicodeBlock block) {
        return block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || block == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || block == UnicodeBlock.CJK_RADICALS_SUPPLEMENT
                || block == UnicodeBlock.CJK_COMPATIBILITY
                || block == UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
    }

    private static boolean isKoreanUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.HANGUL_SYLLABLES ||
                unicodeBlock == UnicodeBlock.HANGUL_JAMO ||
                unicodeBlock == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
    }

    private static boolean isJapanesePhoneticUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.KATAKANA ||
                unicodeBlock == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS ||
                unicodeBlock == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                unicodeBlock == UnicodeBlock.HIRAGANA;
    }

    private static int guessCJKNameStyle(String name, int offset) {
        int length = name.length();
        while (offset < length) {
            int codePoint = Character.codePointAt(name, offset);
            if (Character.isLetter(codePoint)) {
                UnicodeBlock unicodeBlock = UnicodeBlock.of(codePoint);
                if (isJapanesePhoneticUnicodeBlock(unicodeBlock)) {
                    return FullNameStyle.JAPANESE;
                }
                if (isKoreanUnicodeBlock(unicodeBlock)) {
                    return FullNameStyle.KOREAN;
                }
            }
            offset += Character.charCount(codePoint);
        }

        return FullNameStyle.CJK;
    }

    /**
     * Constants for various styles of combining given name, family name etc
     * into a full name. For example, the western tradition follows the pattern
     * 'given name' 'middle name' 'family name' with the alternative pattern
     * being 'family name', 'given name' 'middle name'. The CJK tradition is
     * 'family name' 'middle name' 'given name', with Japanese favoring a space
     * between the names and Chinese omitting the space.
     */
    public interface FullNameStyle {
        int UNDEFINED = 0;
        int WESTERN = 1;

        /**
         * Used if the name is written in Hanzi/Kanji/Hanja and we could not
         * determine which specific language it belongs to: Chinese, Japanese or
         * Korean.
         */
        int CJK = 2;

        int CHINESE = 3;
        int JAPANESE = 4;
        int KOREAN = 5;
    }

    public interface ChartType {
        int NUMBER = 0;
        int SPECIAL_CHARACTER = 1;
        int ENGLISH = 2;
        int CHINESE = 3;
        int JAPANESE = 4;
        int KOREAN = 5;
    }

    public boolean checkText(String text) {
        if (text == null) {
            return false;
        }

        return false;
    }
}
