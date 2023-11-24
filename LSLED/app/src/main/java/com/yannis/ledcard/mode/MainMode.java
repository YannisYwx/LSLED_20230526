package com.yannis.ledcard.mode;


import android.util.Log;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.bean.SendContent;
import com.yannis.ledcard.contract.MainContract;
import com.yannis.ledcard.util.DataUtils;
import com.yannis.ledcard.util.LedDataUtil;

import java.util.HashMap;
import java.util.List;

/**
 * File:com.ls.yannis.mode.MainMode.java
 *
 * @version V1.0 <描述当前版本功能>
 * Email:923080261@qq.com
 * @Description: ${todo}
 * Author Yannis
 * Create on: 2017-07-27 14:17
 */
public class MainMode implements MainContract.Mode {

    public static String[] picture11Content = new String[]{
            "0000111000000110001100010000000100101000101010101010101100000000011000000000101010001010010011100100011000110000001110000",
            "0001110000000010100000000101111110010000000111000000111000000000010000000011100000000001000000001111110000000100011111111",
            "0000001000000000100000000111111100010000010001000001000100000010001000000010010000000010010000000010010011101000011000100",
            "1100000000111000000111110000111111100011111111011111111111111111111101111111111000111111110000111111100000011111000000001",
            "0011111110000100000100111111111110101010101001010101010010101010100101010101001010101010010101010100101010101001111111110",
            "0000000000000000000000111111111111100000001110100000101100100010011010101010111000100011111111111110000000000000000000000",
            "0000010010000001110100000111111000011111110001111111110111111111110011111110000110001100001100011000011000110000111111100",
            "0000010000001000100010001011101000001000100000100000100111000001110010000010000010001000001011101000100010001000000100000",
            "0000001000000000100000000011000000001100000000111000000011111111000000011100000000110000000011000000000100000000010000000",
            "0000111000000110001100010001000100100010001010000100001100001110011000000000101000000010010000000100011000110000001110000",
            "0111111111010000000001100000000011001101100110011011001100000000011000000000101111000110000010010000001011000000111000000",
            "1100010000001000100001010001000100101010101001001110010001001001000010000010000011111000000000000000011000110000110001100",
            "0010000010001010001010001000001000010000010000100000100111111111111111111111111110001111110000000111100000001110000000001",
            "0000000000011111111000101000110001001010100011011001000100111010001010001100011111111000011111111000001111111100000000000",
            "0001111100000100000100001000001000010000010000100000100001000001000010000010000100000100001111111000011101110000011111000",
            "0011000110001001010010100001000011000000000110000000001100000000010100000001000100000100000100010000000101000000000100000",
    };

    public static String[] picture12Content = new String[]{
            "000011110000001100001100010000000010010100001010101010010101100000000001100000000001100000000001010010010010010001100010001100001100000011110000",
            "000111000000000101000000000101000000001101111111111000000001000000001111000000000001000000001111000000000001000000001111111100000001000111111111",
            "000000010000000000100000000111111110011000000100010000001000100000001000100000001000100000000100010000000010010000000001001001110010000110001100",
            "110000000001110000000111110000011111110001111111110111111111111111111111110111111111110001111111110000011111110000000111110000000001000000000000",
            "001111111100001000000100001000000100111111111111010010010010010010010010010010010010010010010010010010010010010010010010010010010010011111111110",
            "000000000000000000000000111111111110110000000110101000001010100100010010100110110010101001001010110000000110111111111110000000000000000000000000",
            "000001001000000011101000000111111000001111111000011111111100111111111110011111111100011100011100011100011100011100011100011111111100011111111100",
            "000001000000010001000100001011101000000100010000001000001000111000001110001000001000000100010000001011101000010001000100000001000000000000000000",
            "000000001100000000011000000000111000000001110000000011110000000111111110001111111100000001111000000001110000000011100000000011000000000110000000",
            "000011100000001100011000010001000100010001000100100001000010100001110010100000000010010000000100010000000100001100011000000011100000000000000000",
            "011111111110100000000001100000000001101101101101101101101101100000000001100000000001011111110010000000100100000001011000000011100000000000000000",
            "000000000000110000100000010000100001010000100001001010101010001001110010000100100100000110001100000011111000000000000000000110001100000110001100",
            "001000001000010100010100001000001000001000001000111111111110101010101010111111111110111111111110100100010010101000001010110000000110100000000010",
            "111111111100100000000100101111110100101010110100101101010100101111110100100000000100111111111100011111111110011111111110001111111111000000000000",
            "001111111100010000000010010000000010010000000010010000000010011111111110011110011110011101101110011101101110011110011110011111111110001111111100",
            "001100011000011110111100111011101110110001000110110000000110110000000110011000001100001100011000000110110000000011100000000001000000000000000000",
    };

    public static String[] picture16Content = new String[]{
            "0000011111000000000110000011000000100000000010000100000000000100010010000010010010010100010100101000000000000010100000000000001010000000000000101000001110000010010001000100010001000011100001000010000000001000000110000011000000000111110000000000000000000000",
            "0000011100000000000001010000000000000101000000000000010111111100000010000000001000010000000000100010000011111110110000000000001000000000000000100000000011111110000000000000001000000000000000100000000011111110111000000000001000011100000000100000001111111100",
            "0000000001100000000000001110000000000001110000000000000110000000000111000011100000111111111111000011111111111000011111111111100001111111111110000111111111111000011111111111100000111111111111000011111111111110000111111111110000011111111110000000111001110000",
            "1100000000000010110000000000011011000000000111101100000001111110110000011111111011000111111111101101111111111110111111111111111011011111111111101100011111111110110000011111111011000000011111101100000000011110110000000000011011000000000000100000000000000000",
            "0001111111110000000100000001000000010000000100000001000000010000111111111111111000101010101010000010101010101000001010101010100000101010101010000010101010101000001010101010100000101010101010000010101010101000001010101010100000101010101010000011111111111000",
            "0000000000000000000000000000000000000000000000001111111111111110110000000000011010100000000010101001000000010010100010000010001010001100011000101001001010010010101000010000101011000000000001101111111111111110000000000000000000000000000000000000000000000000",
            "0000000100010000000000111001000000000111110100000000111111110000000111111111000000111111111110000111111111111100111111111111111000111111111110000011111111111000001111000111100000111100011110000011110001111000001111111111100000111111111110000011111111111000",
            "0000000100000000000000010000000000100001000010000001001110010000000011000110000000001000001000000001000000010000111100000001111000010000000100000000100000100000000011000110000000010011100100000010000100001000000000010000000000000001000000000000000000000000",
            "0000000000100000000000000110000000000000110000000000000111000000000000111000000000000111100000000000111100000000000111111111100000111111111100000000000111100000000000111100000000000011100000000000011100000000000001100000000000001100000000000000100000000000",
            "0000011111000000000110000011000000100001000010000100000100000100010000010000010010000001000000101000000100000010100000011111001010000000000000101000000000000010010000000000010001000000000001000010000000001000000110000011000000000111110000000000000000000000",
            "0000000000000000011111111111110010000000000000101000000000000010100000000000001010110110110110101011011011011010100000000000001010000000000000101000000000000010011111111000010000000001000110000000001000100000000001001100000000001011000000000001110000000000",
            "1100000000000000010000001000000001000000100000000010000010000010001000001000001000100000100000100001000010000100000100001000010000010010101001000000100111001000000010001000100000001000000010000000111111110000000000000000000000011000000110000001100000011000",
            "0001000000010000001010000010100000010000000100000001000000010000000100000001000000010000000100001111111111111110101010101010101011111111111111101111111111111110111110000011111011100000000011101100000000000110110000000000011010000000000000101000000000000010",
            "0000000000000000000000000000000011111111111110001000000000101000101111111010100010111111101010001010101010101000101111111010100011000000001010001111111111111000011111111111110000111111111111100001111111111111000000000000000000000000000000000000000000000000",
            "0000111111110000000100000000100000010000000010000001000000001000000100000000100000010000000010000001000000001000000100000000100000010000000010000001000000001000000100000000100000010000000010000001111111111000000111100111100000011110011110000000111111110000",
            "0001100000110000001001000100100001000010100001001000000100000010100000000000001010000000000000101000000000000010100000000000001010000000000000100100000000000100001000000000100000010000000100000000100000100000000001000100000000000010100000000000000100000000",
    };

    public static byte[][] picture11 = {
            {(byte) 0x0e, (byte) 0x00, (byte) 0x31, (byte) 0x80, (byte) 0x40, (byte) 0x40, (byte) 0x51, (byte) 0x40, (byte) 0xAA, (byte) 0xA0, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x51, (byte) 0x40, (byte) 0x4E, (byte) 0x40, (byte) 0x31, (byte) 0x80, (byte) 0x0e, (byte) 0x00},
            {(byte) 0x1C, (byte) 0x00, (byte) 0x14, (byte) 0x00, (byte) 0x17, (byte) 0xE0, (byte) 0x20, (byte) 0x20, (byte) 0xC0, (byte) 0xE0, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0xE0, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0xE0, (byte) 0xE0, (byte) 0x20, (byte) 0x1F, (byte) 0xE0},
            {(byte) 0x02, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x1F, (byte) 0xC0, (byte) 0x20, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0x81, (byte) 0x00, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x40, (byte) 0x40, (byte) 0x20, (byte) 0x27, (byte) 0x40, (byte) 0x18, (byte) 0x80},
            {(byte) 0xC0, (byte) 0x20, (byte) 0xC0, (byte) 0xE0, (byte) 0xC3, (byte) 0xE0, (byte) 0xC7, (byte) 0xE0, (byte) 0xDF, (byte) 0xE0, (byte) 0xFF, (byte) 0xE0, (byte) 0xDF, (byte) 0xE0, (byte) 0xC7, (byte) 0xE0, (byte) 0xC3, (byte) 0xE0, (byte) 0xC0, (byte) 0xE0, (byte) 0xC0, (byte) 0x20},
            {(byte) 0x3F, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0xFF, (byte) 0xE0, (byte) 0x55, (byte) 0x40, (byte) 0x55, (byte) 0x40, (byte) 0x55, (byte) 0x40, (byte) 0x55, (byte) 0x40, (byte) 0x55, (byte) 0x40, (byte) 0x55, (byte) 0x40, (byte) 0x55, (byte) 0x40, (byte) 0x7F, (byte) 0xC0},
            {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xE0, (byte) 0xC0, (byte) 0x60, (byte) 0xA0, (byte) 0xA0, (byte) 0x91, (byte) 0x20, (byte) 0xAA, (byte) 0xA0, (byte) 0xC4, (byte) 0x60, (byte) 0xFF, (byte) 0xE0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x04, (byte) 0x80, (byte) 0x0e, (byte) 0x80, (byte) 0x1F, (byte) 0x80, (byte) 0x3F, (byte) 0x80, (byte) 0x7F, (byte) 0xC0, (byte) 0xFF, (byte) 0xE0, (byte) 0x3F, (byte) 0x80, (byte) 0x31, (byte) 0x80, (byte) 0x31, (byte) 0x80, (byte) 0x31, (byte) 0x80, (byte) 0x3F, (byte) 0x80},
            {(byte) 0x04, (byte) 0x00, (byte) 0x44, (byte) 0x40, (byte) 0x2E, (byte) 0x80, (byte) 0x11, (byte) 0x00, (byte) 0x20, (byte) 0x80, (byte) 0xE0, (byte) 0xE0, (byte) 0x20, (byte) 0x80, (byte) 0x11, (byte) 0x00, (byte) 0x2E, (byte) 0x80, (byte) 0x44, (byte) 0x40, (byte) 0x04, (byte) 0x00},
            {(byte) 0x02, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x7F, (byte) 0x80, (byte) 0x07, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00},
            {(byte) 0x0e, (byte) 0x00, (byte) 0x31, (byte) 0x80, (byte) 0x44, (byte) 0x40, (byte) 0x44, (byte) 0x40, (byte) 0x84, (byte) 0x20, (byte) 0x87, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x31, (byte) 0x80, (byte) 0x0e, (byte) 0x00},
            {(byte) 0x7F, (byte) 0xC0, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x9B, (byte) 0x20, (byte) 0x9B, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x78, (byte) 0xC0, (byte) 0x09, (byte) 0x00, (byte) 0x16, (byte) 0x00, (byte) 0x38, (byte) 0x00},
            {(byte) 0xC4, (byte) 0x00, (byte) 0x44, (byte) 0x20, (byte) 0x44, (byte) 0x40, (byte) 0x55, (byte) 0x40, (byte) 0x4E, (byte) 0x40, (byte) 0x24, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x1F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x80, (byte) 0x31, (byte) 0x80},
            {(byte) 0x20, (byte) 0x80, (byte) 0x51, (byte) 0x40, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0xFF, (byte) 0xE0, (byte) 0xFF, (byte) 0xE0, (byte) 0xF1, (byte) 0xE0, (byte) 0xC0, (byte) 0x60, (byte) 0xC0, (byte) 0x60, (byte) 0x80, (byte) 0x20},
            {(byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0xA3, (byte) 0x00, (byte) 0x95, (byte) 0x00, (byte) 0xD9, (byte) 0x00, (byte) 0x9D, (byte) 0x00, (byte) 0xA3, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x7F, (byte) 0x80, (byte) 0x1F, (byte) 0xE0, (byte) 0x00, (byte) 0x00},
            {(byte) 0x1F, (byte) 0x00, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x3F, (byte) 0x80, (byte) 0x3B, (byte) 0x80, (byte) 0x1F, (byte) 0x00},
            {(byte) 0x31, (byte) 0x80, (byte) 0x4A, (byte) 0x40, (byte) 0x84, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x40, (byte) 0x40, (byte) 0x20, (byte) 0x80, (byte) 0x11, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x04, (byte) 0x00},
    };

    public static byte[][] picture12 = {
            {(byte) 0x0f, (byte) 0x00, (byte) 0x30, (byte) 0xc0, (byte) 0x40, (byte) 0x20, (byte) 0x50, (byte) 0xa0, (byte) 0xa9, (byte) 0x50, (byte) 0x80, (byte) 0x10, (byte) 0x80, (byte) 0x10, (byte) 0x80, (byte) 0x10, (byte) 0x49, (byte) 0x20, (byte) 0x46, (byte) 0x20, (byte) 0x30, (byte) 0xc0, (byte) 0x0f, (byte) 0x00},
            {(byte) 0x1c, (byte) 0x00, (byte) 0x14, (byte) 0x00, (byte) 0x14, (byte) 0x00, (byte) 0x37, (byte) 0xf0, (byte) 0xe0, (byte) 0x10, (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0xf0, (byte) 0xf0, (byte) 0x10, (byte) 0x1f, (byte) 0xf0},
            {(byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x1f, (byte) 0xe0, (byte) 0x60, (byte) 0x40, (byte) 0x40, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x40, (byte) 0x40, (byte) 0x20, (byte) 0x40, (byte) 0x10, (byte) 0x27, (byte) 0x20, (byte) 0x18, (byte) 0xc0},
            {(byte) 0xc0, (byte) 0x10, (byte) 0xc0, (byte) 0x70, (byte) 0xc1, (byte) 0xf0, (byte) 0xc7, (byte) 0xf0, (byte) 0xdf, (byte) 0xf0, (byte) 0xff, (byte) 0xf0, (byte) 0xdf, (byte) 0xf0, (byte) 0xc7, (byte) 0xf0, (byte) 0xc1, (byte) 0xf0, (byte) 0xc0, (byte) 0x70, (byte) 0xc0, (byte) 0x10, (byte) 0x00, (byte) 0x00},
            {(byte) 0x3f, (byte) 0xc0, (byte) 0x20, (byte) 0x40, (byte) 0x20, (byte) 0x40, (byte) 0xff, (byte) 0xf0, (byte) 0x49, (byte) 0x20, (byte) 0x49, (byte) 0x20, (byte) 0x49, (byte) 0x20, (byte) 0x49, (byte) 0x20, (byte) 0x49, (byte) 0x20, (byte) 0x49, (byte) 0x20, (byte) 0x49, (byte) 0x20, (byte) 0x7f, (byte) 0xe0},
            {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xe0, (byte) 0xc0, (byte) 0x60, (byte) 0xa0, (byte) 0xa0, (byte) 0x91, (byte) 0x20, (byte) 0x9b, (byte) 0x20, (byte) 0xa4, (byte) 0xa0, (byte) 0xc0, (byte) 0x60, (byte) 0xff, (byte) 0xe0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x04, (byte) 0x80, (byte) 0x0e, (byte) 0x80, (byte) 0x1f, (byte) 0x80, (byte) 0x3f, (byte) 0x80, (byte) 0x7f, (byte) 0xc0, (byte) 0xff, (byte) 0xe0, (byte) 0x7f, (byte) 0xc0, (byte) 0x71, (byte) 0xc0, (byte) 0x71, (byte) 0xc0, (byte) 0x71, (byte) 0xc0, (byte) 0x7f, (byte) 0xc0, (byte) 0x7f, (byte) 0xc0},
            {(byte) 0x04, (byte) 0x00, (byte) 0x44, (byte) 0x40, (byte) 0x2e, (byte) 0x80, (byte) 0x11, (byte) 0x00, (byte) 0x20, (byte) 0x80, (byte) 0xe0, (byte) 0xe0, (byte) 0x20, (byte) 0x80, (byte) 0x11, (byte) 0x00, (byte) 0x2e, (byte) 0x80, (byte) 0x44, (byte) 0x40, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x00, (byte) 0xc0, (byte) 0x01, (byte) 0x80, (byte) 0x03, (byte) 0x80, (byte) 0x07, (byte) 0x00, (byte) 0x0f, (byte) 0x00, (byte) 0x1f, (byte) 0xe0, (byte) 0x3f, (byte) 0xc0, (byte) 0x07, (byte) 0x80, (byte) 0x07, (byte) 0x00, (byte) 0x0e, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x18, (byte) 0x00},
            {(byte) 0x0e, (byte) 0x00, (byte) 0x31, (byte) 0x80, (byte) 0x44, (byte) 0x40, (byte) 0x44, (byte) 0x40, (byte) 0x84, (byte) 0x20, (byte) 0x87, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x31, (byte) 0x80, (byte) 0x0e, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x7f, (byte) 0xe0, (byte) 0x80, (byte) 0x10, (byte) 0x80, (byte) 0x10, (byte) 0xb6, (byte) 0xd0, (byte) 0xb6, (byte) 0xd0, (byte) 0x80, (byte) 0x10, (byte) 0x80, (byte) 0x10, (byte) 0x7f, (byte) 0x20, (byte) 0x02, (byte) 0x40, (byte) 0x05, (byte) 0x80, (byte) 0x0e, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x00, (byte) 0x00, (byte) 0xc2, (byte) 0x00, (byte) 0x42, (byte) 0x10, (byte) 0x42, (byte) 0x10, (byte) 0x2a, (byte) 0xa0, (byte) 0x27, (byte) 0x20, (byte) 0x12, (byte) 0x40, (byte) 0x18, (byte) 0xc0, (byte) 0x0f, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0xc0, (byte) 0x18, (byte) 0xc0},
            {(byte) 0x20, (byte) 0x80, (byte) 0x51, (byte) 0x40, (byte) 0x20, (byte) 0x80, (byte) 0x20, (byte) 0x80, (byte) 0xff, (byte) 0xe0, (byte) 0xaa, (byte) 0xa0, (byte) 0xff, (byte) 0xe0, (byte) 0xff, (byte) 0xe0, (byte) 0x91, (byte) 0x20, (byte) 0xa0, (byte) 0xa0, (byte) 0xc0, (byte) 0x60, (byte) 0x80, (byte) 0x20},
            {(byte) 0xff, (byte) 0xc0, (byte) 0x80, (byte) 0x40, (byte) 0xbf, (byte) 0x40, (byte) 0xab, (byte) 0x40, (byte) 0xb5, (byte) 0x40, (byte) 0xbf, (byte) 0x40, (byte) 0x80, (byte) 0x40, (byte) 0xff, (byte) 0xc0, (byte) 0x7f, (byte) 0xe0, (byte) 0x7f, (byte) 0xe0, (byte) 0x3f, (byte) 0xf0, (byte) 0x00, (byte) 0x00},
            {(byte) 0x3F, (byte) 0xc0, (byte) 0x40, (byte) 0x20, (byte) 0x40, (byte) 0x20, (byte) 0x40, (byte) 0x20, (byte) 0x40, (byte) 0x20, (byte) 0x7f, (byte) 0xe0, (byte) 0x79, (byte) 0xe0, (byte) 0x76, (byte) 0xe0, (byte) 0x76, (byte) 0xe0, (byte) 0x79, (byte) 0xe0, (byte) 0x7f, (byte) 0xe0, (byte) 0x3f, (byte) 0xc0},
            {(byte) 0x31, (byte) 0x80, (byte) 0x7b, (byte) 0xc0, (byte) 0xee, (byte) 0xe0, (byte) 0xc4, (byte) 0x60, (byte) 0xc0, (byte) 0x60, (byte) 0xc0, (byte) 0x60, (byte) 0x60, (byte) 0xc0, (byte) 0x31, (byte) 0x80, (byte) 0x1b, (byte) 0x00, (byte) 0x0e, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00}
    };
    public static byte[][] picture16 = {
            {(byte) 0x07, (byte) 0xC0, (byte) 0x18, (byte) 0x30, (byte) 0x20, (byte) 0x08, (byte) 0x40, (byte) 0x04, (byte) 0x48, (byte) 0x24, (byte) 0x94, (byte) 0x52, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x83, (byte) 0x82, (byte) 0x44, (byte) 0x44, (byte) 0x43, (byte) 0x84, (byte) 0x20, (byte) 0x08, (byte) 0x18, (byte) 0x30, (byte) 0x07, (byte) 0xC0, (byte) 0x00, (byte) 0x00},
            {(byte) 0x07, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x05, (byte) 0xFC, (byte) 0x08, (byte) 0x02, (byte) 0x10, (byte) 0x02, (byte) 0x20, (byte) 0xFE, (byte) 0xC0, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0xFE, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0xFE, (byte) 0xE0, (byte) 0x02, (byte) 0x1C, (byte) 0x02, (byte) 0x03, (byte) 0xFC},
            {(byte) 0x00, (byte) 0x60, (byte) 0x00, (byte) 0xE0, (byte) 0x01, (byte) 0xC0, (byte) 0x01, (byte) 0x80, (byte) 0x1C, (byte) 0x38, (byte) 0x3F, (byte) 0xFC, (byte) 0x3F, (byte) 0xF8, (byte) 0x7F, (byte) 0xF8, (byte) 0x7F, (byte) 0xF8, (byte) 0x7F, (byte) 0xF8, (byte) 0x7F, (byte) 0xF8, (byte) 0x3F, (byte) 0xFC, (byte) 0x3F, (byte) 0xFE, (byte) 0x1F, (byte) 0xFC, (byte) 0x1F, (byte) 0xF8, (byte) 0x0e, (byte) 0x70},
            {(byte) 0xC0, (byte) 0x02, (byte) 0xC0, (byte) 0x06, (byte) 0xC0, (byte) 0x1E, (byte) 0xC0, (byte) 0x7E, (byte) 0xC1, (byte) 0xFE, (byte) 0xC7, (byte) 0xFE, (byte) 0xDF, (byte) 0xFE, (byte) 0xFF, (byte) 0xFE, (byte) 0xDF, (byte) 0xFE, (byte) 0xC7, (byte) 0xFE, (byte) 0xC1, (byte) 0xFE, (byte) 0xC0, (byte) 0x7E, (byte) 0xC0, (byte) 0x1E, (byte) 0xC0, (byte) 0x06, (byte) 0xC0, (byte) 0x02, (byte) 0x00, (byte) 0x00},
            {(byte) 0x1F, (byte) 0xF0, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0xFF, (byte) 0xFE, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x2A, (byte) 0xA8, (byte) 0x3F, (byte) 0xF8},
            {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFE, (byte) 0xC0, (byte) 0x06, (byte) 0xA0, (byte) 0x0a, (byte) 0x90, (byte) 0x12, (byte) 0x88, (byte) 0x22, (byte) 0x8C, (byte) 0x62, (byte) 0x92, (byte) 0x92, (byte) 0xA1, (byte) 0x0a, (byte) 0xC0, (byte) 0x06, (byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x01, (byte) 0x10, (byte) 0x03, (byte) 0x90, (byte) 0x07, (byte) 0xD0, (byte) 0x0f, (byte) 0xF0, (byte) 0x1F, (byte) 0xF0, (byte) 0x3F, (byte) 0xF8, (byte) 0x7F, (byte) 0xFC, (byte) 0xFF, (byte) 0xFE, (byte) 0x3F, (byte) 0xF8, (byte) 0x3F, (byte) 0xF8, (byte) 0x3C, (byte) 0x78, (byte) 0x3C, (byte) 0x78, (byte) 0x3C, (byte) 0x78, (byte) 0x3F, (byte) 0xF8, (byte) 0x3F, (byte) 0xF8, (byte) 0x3F, (byte) 0xF8},
            {(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x21, (byte) 0x08, (byte) 0x13, (byte) 0x90, (byte) 0x0c, (byte) 0x60, (byte) 0x08, (byte) 0x20, (byte) 0x10, (byte) 0x10, (byte) 0xF0, (byte) 0x1E, (byte) 0x10, (byte) 0x10, (byte) 0x08, (byte) 0x20, (byte) 0x0c, (byte) 0x60, (byte) 0x13, (byte) 0x90, (byte) 0x21, (byte) 0x08, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x60, (byte) 0x00, (byte) 0xC0, (byte) 0x01, (byte) 0xC0, (byte) 0x03, (byte) 0x80, (byte) 0x07, (byte) 0x80, (byte) 0x0f, (byte) 0x00, (byte) 0x1F, (byte) 0xF8, (byte) 0x3F, (byte) 0xF0, (byte) 0x01, (byte) 0xE0, (byte) 0x03, (byte) 0xC0, (byte) 0x03, (byte) 0x80, (byte) 0x07, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x08, (byte) 0x00},
            {(byte) 0x07, (byte) 0xC0, (byte) 0x18, (byte) 0x30, (byte) 0x21, (byte) 0x08, (byte) 0x41, (byte) 0x04, (byte) 0x41, (byte) 0x04, (byte) 0x81, (byte) 0x02, (byte) 0x81, (byte) 0x02, (byte) 0x81, (byte) 0xF2, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x40, (byte) 0x04, (byte) 0x40, (byte) 0x04, (byte) 0x20, (byte) 0x08, (byte) 0x18, (byte) 0x30, (byte) 0x07, (byte) 0xC0, (byte) 0x00, (byte) 0x00},
            {(byte) 0x00, (byte) 0x00, (byte) 0x7F, (byte) 0xFC, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0xB6, (byte) 0xDA, (byte) 0xB6, (byte) 0xDA, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x7F, (byte) 0x84, (byte) 0x01, (byte) 0x18, (byte) 0x02, (byte) 0x20, (byte) 0x04, (byte) 0xC0, (byte) 0x0b, (byte) 0x00, (byte) 0x1C, (byte) 0x00},
            {(byte) 0xC0, (byte) 0x00, (byte) 0x40, (byte) 0x80, (byte) 0x40, (byte) 0x80, (byte) 0x20, (byte) 0x82, (byte) 0x20, (byte) 0x82, (byte) 0x20, (byte) 0x82, (byte) 0x10, (byte) 0x84, (byte) 0x10, (byte) 0x84, (byte) 0x12, (byte) 0xA4, (byte) 0x09, (byte) 0xC8, (byte) 0x08, (byte) 0x88, (byte) 0x08, (byte) 0x08, (byte) 0x0f, (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18},
            {(byte) 0x10, (byte) 0x10, (byte) 0x28, (byte) 0x28, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0x10, (byte) 0xFF, (byte) 0xFE, (byte) 0xAA, (byte) 0xAA, (byte) 0xFF, (byte) 0xFE, (byte) 0xFF, (byte) 0xFE, (byte) 0xF8, (byte) 0x3E, (byte) 0xE0, (byte) 0x0e, (byte) 0xC0, (byte) 0x06, (byte) 0xC0, (byte) 0x06, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02},
            {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xF8, (byte) 0x80, (byte) 0x28, (byte) 0xBF, (byte) 0xA8, (byte) 0xBF, (byte) 0xA8, (byte) 0xAA, (byte) 0xA8, (byte) 0xBF, (byte) 0xA8, (byte) 0xC0, (byte) 0x28, (byte) 0xFF, (byte) 0xF8, (byte) 0x7F, (byte) 0xFC, (byte) 0x3F, (byte) 0xFE, (byte) 0x1F, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
            {(byte) 0x0f, (byte) 0xF0, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x1F, (byte) 0xF8, (byte) 0x1E, (byte) 0x78, (byte) 0x1E, (byte) 0x78, (byte) 0x0f, (byte) 0xF0},
            {(byte) 0x18, (byte) 0x30, (byte) 0x24, (byte) 0x48, (byte) 0x42, (byte) 0x84, (byte) 0x81, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x80, (byte) 0x02, (byte) 0x40, (byte) 0x04, (byte) 0x20, (byte) 0x08, (byte) 0x10, (byte) 0x10, (byte) 0x08, (byte) 0x20, (byte) 0x04, (byte) 0x40, (byte) 0x02, (byte) 0x80, (byte) 0x01, (byte) 0x00}
    };

    private int msgByteLength = 0;
    /**
     * 点阵
     */
    private int matrix;
    /**
     * 需要发送的包数
     */
    private int sendPackageSizeCount;

    private HashMap<Integer, byte[]> byteArrays = new HashMap<>();

    @Override
    public byte[] getSendHeader(List<SendContent> sendContentList, int pix) {
        return LedDataUtil.get64(sendContentList, pix);
    }

    @Override
    public HashMap<Integer, byte[]> getSendLedData(List<SendContent> sendContentList, int matrix) {
        for (SendContent sendContent : sendContentList) {
            String msg = sendContent.getMessage();
            Log.e("读取字库数据", " msg = " + msg);
            msg = msg.replace((char) 12288, ' ');
            Log.e("读取字库数据", msg + " length = " + msg.length());
            sendContent.setMessage(msg);
        }

        this.matrix = matrix;
        byteArrays.clear();
        //信息长度
        msgByteLength = LedDataUtil.getTotalMessageLength(matrix, sendContentList);
        sendPackageSizeCount = 0;
        //获取头部64个数据
        byte[] data64 = getSendHeader(sendContentList, matrix);
        Log.e("data64", "data64 = " + DataUtils.byteArray2StringWithSpaces(data64));
        //获取发送消息数据 bitmap形式获取数据
        byte[][] msgData = LedDataUtil.getMsgBytes1(LedBleApplication.instance, matrix, sendContentList);
        msgByteLength = msgData[0].length;
        byte[] matrixData = convertSendByteArray(msgData);
        addData64ToByteArray(data64);
        addMatrixData2ByteArray(matrixData);
        return byteArrays;
    }

    @Override
    public int getSendPackageSize() {
        return sendPackageSizeCount;
    }

    /**
     * 添加头数据
     *
     * @param data64 头数据
     */
    private void addData64ToByteArray(byte[] data64) {
        byte[] head641 = new byte[16];
        byte[] head642 = new byte[16];
        byte[] head643 = new byte[16];
        byte[] head644 = new byte[16];
        for (int i = 0; i < 64; i++) {
            if (i >= 0 && i < 16) {
                head641[i] = data64[i];
            }
            if (i >= 16 && i < 32) {
                head642[i - 16] = data64[i];
            }
            if (i >= 32 && i < 48) {
                head643[i - 32] = data64[i];
            }
            if (i >= 48 && i < 64) {
                head644[i - 48] = data64[i];
            }
        }
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                byteArrays.put(i, head641);
            } else if (i == 1) {
                byteArrays.put(i, head642);
            } else if (i == 2) {
                byteArrays.put(i, head643);
            } else if (i == 3) {
                byteArrays.put(i, head644);
            }
        }
    }

    /**
     * 添加点阵数据到发送数据
     *
     * @param matrixData
     */
    private void addMatrixData2ByteArray(byte[] matrixData) {
        int dzLength = matrixData.length;

        int packageSize = dzLength / 16;
        if (packageSize * 16 < dzLength) {
            packageSize += 1;
        }
        sendPackageSizeCount = 4 + packageSize;
        for (int i = 0; i < packageSize; i++) {
            byte[] sendData = new byte[16];
            for (int j = 0; j < 16; j++) {
                if ((i * 16 + j) >= dzLength) {
                    sendData[j] = 0x00;
                } else {
                    sendData[j] = matrixData[i * 16 + j];
                }
            }
            byteArrays.put(i + 4, sendData);
        }

        Log.e("读取字库数据", " 数据字节数 length = " + dzLength);

    }

    /**
     * 将二维点数据转化为一维点阵数据
     *
     * @param msgData
     * @return
     */
    private byte[] convertSendByteArray(byte[][] msgData) {
        Log.e("读取字库数据", "convertSendByteArray -- 数据字节数 msgByteLength = " + msgByteLength);
        byte[][] newSend = new byte[msgByteLength][matrix];
        for (int i = 0; i < msgByteLength; i++) {
            for (int j = 0; j < matrix; j++) {
                newSend[i][j] = msgData[j][i];
            }
        }
        int length = msgByteLength * matrix;
        byte[] data = new byte[length];

        for (int i = 0; i < msgByteLength; i++) {
            if (matrix >= 0) System.arraycopy(newSend[i], 0, data, i * matrix, matrix);
        }
        return data;
    }
}
