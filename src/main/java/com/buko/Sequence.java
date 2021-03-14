package com.buko;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 徐健威
 */
@Data
@Slf4j
public class Sequence {
    public int compute(int n) {
        if (n < 0) {
            return -1;
        }
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return compute(n - 1) + compute(n - 2);
    }

    public int compute2(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        int a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return a;
    }

    public int compute3(int result) {
        int a = 0, b = 1;
        int count = 0;
        while (a + b <= result) {
            count++;
            int temp = a + b;
            a = b;
            b = temp;
        }
        return count + 1;
    }


    public static void main(String[] args) {
        Sequence sequence = new Sequence();
        log.debug(String.valueOf(sequence.compute2(25)));
        log.debug(String.valueOf(sequence.compute3(100000)));
    }
}
