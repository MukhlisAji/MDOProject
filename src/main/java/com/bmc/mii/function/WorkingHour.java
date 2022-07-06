/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bmc.mii.function;

import com.bmc.mii.domain.PublicHoliday;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

/**
 *
 * @author MukhlisAj
 */
public class WorkingHour {

    protected static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("Processing Working Hour: ");
    private static final int WORK_HOUR_START = 7;
    private static final int WORK_HOUR_END = 16;
    private static final long MINUTES = 60;

    private static final long WORKING_HOURS_PER_DAY = WORK_HOUR_END - WORK_HOUR_START;
    private static final long WORKING_MINUTES_PER_DAY = WORKING_HOURS_PER_DAY * MINUTES;

    public int getWorkingMinutesSince(final Timestamp startTime, PublicHoliday publicHoliday) {
        Timestamp now = Timestamp.from(Instant.now());
        return getWorkingMinutes(startTime, now, publicHoliday);
    }

    private int getWorkingMinutes(final Timestamp startTime, final Timestamp endTime, PublicHoliday publicHoliday) {
        if (null == startTime || null == endTime) {
            throw new IllegalStateException();
        }
        if (endTime.before(startTime)) {
            return 0;
        }

        LocalDateTime from = startTime.toLocalDateTime();
        LocalDateTime to = endTime.toLocalDateTime();
        LocalDate fromDay = from.toLocalDate();
        logger.info("Start time : " + fromDay);
        LocalDate toDay = to.toLocalDate();
        logger.info("End time : " + toDay);

        int allDaysBetween = (int) (ChronoUnit.DAYS.between(fromDay, toDay) + 1);
        logger.info("all day between : " + allDaysBetween);
        int workDayBetween = (int) IntStream.range(0, allDaysBetween)
                .filter(i -> isWorkingDay(from.plusDays(i)))
                .count();
        int actualWorkDay = (int) IntStream.range(0, workDayBetween).filter(i -> !isntHoliday(fromDay.plusDays(i), publicHoliday)).count();
        logger.info("actual work days : " + actualWorkDay);

        long allWorkingMinutes = actualWorkDay * WORKING_MINUTES_PER_DAY;

        // from - working_day_from_start
        long tailRedundantMinutes = 0;
        if (isWorkingDay(from)) {
            if (!isntHoliday(fromDay, publicHoliday)) {
                if (isWorkingHours(from)) {
                    tailRedundantMinutes = Duration.between(fromDay.atTime(WORK_HOUR_START, 0), from).toMinutes();
                } else if (from.getHour() > WORK_HOUR_START) {
                    tailRedundantMinutes = WORKING_MINUTES_PER_DAY;
                }
            }
        }

        // working_day_end - to
        long headRedundanMinutes = 0;
        if (isWorkingDay(to)) {
            if (!isntHoliday(toDay, publicHoliday)) {
                if (isWorkingHours(to)) {
                    headRedundanMinutes = Duration.between(to, toDay.atTime(WORK_HOUR_END, 0)).toMinutes();
                } else if (from.getHour() < WORK_HOUR_START) {
                    headRedundanMinutes = WORKING_MINUTES_PER_DAY;
                }
            }
        }
        logger.info("AllworkMinutes : " + allWorkingMinutes + " tailRedundantMinutes : " + tailRedundantMinutes + " headRedundantMinutes : " + headRedundanMinutes);
        return (int) (allWorkingMinutes - tailRedundantMinutes - headRedundanMinutes);
    }

    private boolean isWorkingDay(final LocalDateTime time) {
        return time.getDayOfWeek().getValue() < DayOfWeek.SATURDAY.getValue();
    }

    private boolean isWorkingHours(final LocalDateTime time) {
        int hour = time.getHour();
        return WORK_HOUR_START <= hour && hour < WORK_HOUR_END;
    }

    private boolean isntHoliday(final LocalDate fromDate, PublicHoliday publicHoliday) {

        
        return publicHoliday.getHolidayDate().contains(fromDate.toString());
    }
}
