package com.buko;

/**
 * @author 徐健威
 */
public class SQL {
    public static void main(String[] args) {
        String s =
                "select name from employee e, work w " +
                "where e.id = w.employee_id " +
                "and w.time > TIMESTAMP(2020-03-01 06-00-00) " +
                "and w.time < TIMESTAMP(2020-03-01 12-00-00)";
    }
}
