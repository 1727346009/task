package com.buko;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author 徐健威
 */
@Data
@Slf4j
public class FastSorted {
    private int[] nums;
    public void compute(int start, int end) {
        if (start >= end - 1) {
            return;
        }
        int count = 0;
        int tail = nums[end - 1];
        for (int i = start; i < end - 1; i++) {
            if (tail > nums[i]) {
                int temp = nums[i];
                nums[i] = nums[start + count];
                nums[start + count] = temp;
                count++;
            }
        }
        nums[end - 1] = nums[start + count];
        nums[start + count] = tail;
        log.debug(Arrays.toString(nums));
        compute(start, start + count);
        compute(start + count, end);
    }

    public void start(int[] nums) {
        this.nums = nums;
        compute(0, nums.length);
    }

    public static void main(String[] args) {
        FastSorted fastSorted = new FastSorted();
        fastSorted.start(new int[]{6, 1, 2, 7, 9, 3, 4, 5, 10, 8});
    }
}
