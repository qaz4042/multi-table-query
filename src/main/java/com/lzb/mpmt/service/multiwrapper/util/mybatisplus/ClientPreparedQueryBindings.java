package com.lzb.mpmt.service.multiwrapper.util.mybatisplus;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/***
 * 直接参考 PreparedStatement java.sql相关代码
 * {@link com.mysql.cj.ClientPreparedQueryBindings}
 * @author Administrator
 */
public class ClientPreparedQueryBindings {

    public static CharsetEncoder charsetEncoder = StandardCharsets.UTF_8.newEncoder();

    public static String sqlAvoidAttack(String originValue) {
        int stringLength = originValue.length();

        String convertedValue = originValue;
        if (isEscapeNeededForString(originValue, stringLength)) {
            StringBuilder buf = new StringBuilder((int) ((double) originValue.length() * 1.1D));
            buf.append('\'');

            for (int i = 0; i < stringLength; ++i) {
                char c = originValue.charAt(i);
                switch (c) {
                    case '\u0000':
                        buf.append('\\');
                        buf.append('0');
                        break;
                    case '\n':
                        buf.append('\\');
                        buf.append('n');
                        break;
                    case '\r':
                        buf.append('\\');
                        buf.append('r');
                        break;
                    case '\u001a':
                        buf.append('\\');
                        buf.append('Z');
                        break;
                    case '"':
                        //数据库是否支持双引号
//                        if (this.session.getServerSession().useAnsiQuotedIdentifiers()) {
                        buf.append('\\');
//                        }
                        buf.append('"');
                        break;
                    case '\'':
                        buf.append('\\');
                        buf.append('\'');
                        break;
                    case '\\':
                        buf.append('\\');
                        buf.append('\\');
                        break;
                    case '¥':
                    case '₩':
                        if (charsetEncoder != null) {
                            CharBuffer cbuf = CharBuffer.allocate(1);
                            ByteBuffer bbuf = ByteBuffer.allocate(1);
                            cbuf.put(c);
                            cbuf.position(0);
                            charsetEncoder.encode(cbuf, bbuf, true);
                            if (bbuf.get(0) == 92) {
                                buf.append('\\');
                            }
                        }

                        buf.append(c);
                        break;
                    default:
                        buf.append(c);
                }
            }

            buf.append('\'');
            convertedValue = buf.toString();
        }
        return convertedValue;
    }

    private static boolean isEscapeNeededForString(String x, int stringLength) {
        boolean needsHexEscape = false;

        for (int i = 0; i < stringLength; ++i) {
            char c = x.charAt(i);
            switch (c) {
                case '\u0000':
                case '\n':
                case '\r':
                case '\u001a':
                case '"':
                case '\'':
                case '\\':
                    needsHexEscape = true;
                default:
            }

            if (needsHexEscape) {
                break;
            }
        }

        return needsHexEscape;
    }
}
