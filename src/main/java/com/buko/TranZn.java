package com.buko;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;

/**
 * @author 徐健威
 */
@Data
@Slf4j
public class TranZn {
    private String point = ".";
    private static final String[] NUMBERS = {"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};

    private static final String[] INT_UNIT = {"拾","佰","仟"};

    private static final String[] DEC_UNIT = {"角","分","厘"};

    private static final String[] BIG_UNIT = {"元","万","亿"};

    public String compute(double i) {
        // 将 double 转为字符串并格式化 12345.12 => 1,2345.12
        DecimalFormat decimalFormat = new DecimalFormat("#,###0.000");
        String sign = decimalFormat.format(i);

        StringBuilder sb = new StringBuilder();
        // 字串分层处理
        String[] nums = sign.split(",");

        for (int n = 0; n < nums.length; n++) {
            // 包含小数的字串
            if (nums[n].contains(point)) {
                String[] x = nums[n].split("\\.");
                sb.append(tran(x[0], false));
                // 小数处理
                sb.append(tran(x[1], true));
            }
            // 普通字串
            else {
                sb.append(tran(nums[n], false));
            }
            // 加入大单位
            if (n != nums.length - 1) {
                sb.append(BIG_UNIT[nums.length - n - 1]);
            }
        }
        return sb.toString();
    }

    public String tran(String s, Boolean decimal) {
        StringBuilder sb = new StringBuilder();
        int length = s.length();
        boolean zero = s.charAt(0) == '0';
        for (int i = 0; i < length; i++) {
            char now = s.charAt(i);
            // 加入数字
            if (now == '0') {
                if (!zero) {
                    zero = true;
                    sb.append(NUMBERS[now - '0']);
                }
            }
            else {
                zero = false;
                sb.append(NUMBERS[now - '0']);
            }
            // 加入小单位
            if (!decimal) {
                if (i != length - 1 && now != '0') {
                    sb.append(INT_UNIT[length - i - 2]);
                }
            }
            else {
                if (now != '0') {
                    sb.append(DEC_UNIT[i]);
                }
            }

        }
        return sb.toString();
    }


    public static void main(String[] args) {
        TranZn zn = new TranZn();
        log.debug(zn.compute(123456789.123));
    }
}
