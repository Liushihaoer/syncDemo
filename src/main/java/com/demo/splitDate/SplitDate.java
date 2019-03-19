package com.demo.splitDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SplitDate {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // 返利分类 1 按月 , 2 按季度, 3 按半年, 4 按年
        Integer rebateCycle = 0;
        // 2018-04-10
        String startDateStr = "";
        String endDateStr = "";
        doSplit(startDateStr, endDateStr, rebateCycle);

    }

    private static void doSplit(String startDateStr, String endDateStr, Integer rebateCycle) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(startDateStr);
        Date endDate = sdf.parse(endDateStr);
        if (endDate.before(startDate)) {
            throw new RuntimeException("开始日期不能大于结束日期!");
        }
        if (rebateCycle.equals(1)) {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int i = 0;
            Date start = null;
            Date end = null;
            // 按月
            while (calendar.before(endCalendar)) {
                i++;
                if (startCalendar.equals(calendar)) {
                    start = startCalendar.getTime();
                    if (startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                            && startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) && endCalendar
                                    .get(Calendar.DAY_OF_MONTH) < calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        end = endCalendar.getTime();
                        calendar.setTime(end);
                    } else {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        end = calendar.getTime();
                    }
                } else if (calendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
                        && calendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)) {
                    start = calendar.getTime();
                    end = endCalendar.getTime();
                    calendar.setTime(end);
                } else {
                    start = calendar.getTime();
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    end = calendar.getTime();
                }

                System.out.println("开始日期 : " + sdf.format(start));
                System.out.println("结束日期 : " + sdf.format(end));
                System.out.println("返利期 : " + i);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            if (calendar.get(Calendar.DATE) == 1 && calendar.equals(endCalendar)) {
                i++;
                System.out.println("开始日期 : " + sdf.format(calendar.getTime()));
                System.out.println("结束日期 : " + sdf.format(endCalendar.getTime()));
                System.out.println("返利期 : " + i);
            }
        } else if (rebateCycle.equals(2)) {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            Calendar cycleCalendar = Calendar.getInstance();
            cycleCalendar.setTime(startDate);
            int i = 0;
            Date start = null;
            Date end = null;
            Date quartDate = null;
            // 按季度
            while (calendar.before(endCalendar)) {
                i++;
                int month = calendar.get(Calendar.MONTH);
                switch (month) {
                case 0:
                case 1:
                case 2:
                    cycleCalendar.set(calendar.get(Calendar.YEAR), 2, 31);
                    break;
                case 3:
                case 4:
                case 5:
                    cycleCalendar.set(calendar.get(Calendar.YEAR), 5, 30);
                    break;
                case 6:
                case 7:
                case 8:
                    cycleCalendar.set(calendar.get(Calendar.YEAR), 8, 30);
                    break;
                case 9:
                case 10:
                case 11:
                    cycleCalendar.set(calendar.get(Calendar.YEAR), 11, 31);
                    break;
                }
                quartDate = cycleCalendar.getTime();
                if (calendar.equals(startCalendar)) {
                    start = startCalendar.getTime();
                    if ((cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                            && cycleCalendar.get(Calendar.MONTH) > endCalendar.get(Calendar.MONTH))
                            || (cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                                    && cycleCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
                                    && cycleCalendar.get(Calendar.DATE) >= endCalendar.get(Calendar.DATE))) {
                        end = endCalendar.getTime();
                    } else {
                        end = quartDate;
                    }
                } else if ((cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                        && cycleCalendar.get(Calendar.MONTH) > endCalendar.get(Calendar.MONTH))
                        || (cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                                && cycleCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
                                && cycleCalendar.get(Calendar.DATE) >= endCalendar.get(Calendar.DATE))) {
                    start = calendar.getTime();
                    end = endCalendar.getTime();
                } else {
                    start = calendar.getTime();
                    end = quartDate;
                }

                System.out.println("开始日期 : " + sdf.format(start));
                System.out.println("结束日期 : " + sdf.format(end));
                System.out.println("返利期 : " + i);
                calendar.setTime(end);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            if (calendar.get(Calendar.DATE) == 1 && calendar.equals(endCalendar)) {
                i++;
                System.out.println("开始日期 : " + sdf.format(calendar.getTime()));
                System.out.println("结束日期 : " + sdf.format(endCalendar.getTime()));
                System.out.println("返利期  : " + i);
            }

        } else if (rebateCycle.equals(3)) {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            Calendar cycleCalendar = Calendar.getInstance();
            cycleCalendar.setTime(startDate);
            int i = 0;
            Date start = null;
            Date end = null;
            Date quartDate = null;
            // 按半年
            while (calendar.before(endCalendar)) {
                i++;
                int month = calendar.get(Calendar.MONTH);
                switch (month) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    cycleCalendar.set(calendar.get(Calendar.YEAR), 5, 30);
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    cycleCalendar.set(calendar.get(Calendar.YEAR), 11, 31);
                    break;
                }
                quartDate = cycleCalendar.getTime();
                if (calendar.equals(startCalendar)) {
                    start = startCalendar.getTime();
                    if ((cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                            && cycleCalendar.get(Calendar.MONTH) > endCalendar.get(Calendar.MONTH))
                            || (cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                                    && cycleCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
                                    && cycleCalendar.get(Calendar.DATE) >= endCalendar.get(Calendar.DATE))) {
                        end = endCalendar.getTime();
                    } else {
                        end = quartDate;
                    }
                } else if ((cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                        && cycleCalendar.get(Calendar.MONTH) > endCalendar.get(Calendar.MONTH))
                        || (cycleCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                                && cycleCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
                                && cycleCalendar.get(Calendar.DATE) >= endCalendar.get(Calendar.DATE))) {
                    start = calendar.getTime();
                    end = endCalendar.getTime();
                } else {
                    start = calendar.getTime();
                    end = quartDate;
                }

                System.out.println("开始日期 : " + sdf.format(start));
                System.out.println("结束日期 : " + sdf.format(end));
                System.out.println("返利期 : " + i);
                calendar.setTime(end);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            if (calendar.get(Calendar.DATE) == 1 && calendar.equals(endCalendar)) {
                i++;
                System.out.println("开始日期 : " + sdf.format(calendar.getTime()));
                System.out.println("结束日期 : " + sdf.format(endCalendar.getTime()));
                System.out.println("返利期 : " + i);
            }

        } else if (rebateCycle.equals(4)) {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int i = 0;
            Date start = null;
            Date end = null;
            // 按年
            while (calendar.before(endCalendar)) {
                i++;
                if (startCalendar.equals(calendar)) {
                    start = startCalendar.getTime();
                    if (startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                            && endCalendar.get(Calendar.MONTH) <= 11 && endCalendar.get(Calendar.DAY_OF_MONTH) <= 31) {
                        end = endCalendar.getTime();
                        calendar.setTime(end);
                    } else {
                        calendar.set(calendar.get(Calendar.YEAR), 11, 31);
                        end = calendar.getTime();
                    }
                } else if (calendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
                        && endCalendar.get(Calendar.MONTH) <= 11 && endCalendar.get(Calendar.DAY_OF_MONTH) <= 31) {
                    start = calendar.getTime();
                    end = endCalendar.getTime();
                    calendar.setTime(end);
                } else {
                    start = calendar.getTime();
                    calendar.set(calendar.get(Calendar.YEAR), 11, 31);
                    end = calendar.getTime();
                }

                System.out.println("开始日期 : " + sdf.format(start));
                System.out.println("结束日期 : " + sdf.format(end));
                System.out.println("返利期 : " + i);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            if (calendar.get(Calendar.DATE) == 1 && calendar.equals(endCalendar)) {
                i++;
                System.out.println("开始日期 : " + sdf.format(calendar.getTime()));
                System.out.println("结束日期 : " + sdf.format(endCalendar.getTime()));
                System.out.println("返利期 : " + i);
            }
        } else {
            System.out.println("自定义................");
        }

    }
}
