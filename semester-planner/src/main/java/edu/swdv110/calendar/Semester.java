package edu.cwi.swdv110.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

public class Semester {
    private int year;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<LocalDate> holidays;

    public Semester(int year, LocalDate startDate, LocalDate endDate) {
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.holidays = calculateHolidays(year);
    }

    public int getYear() {
        return year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Set<LocalDate> getHolidays() {
        return holidays;
    }

    /** Automatically calculates official semester holidays. */
    private static Set<LocalDate> calculateHolidays(int year) {
        Set<LocalDate> set = new HashSet<>();
        
        // Martin Luther King Jr. Day: 3rd Monday of January
        set.add(DateTools.nthWeekday(year, Month.JANUARY, 3, DayOfWeek.MONDAY));
        
        // Presidents' Day: 3rd Monday of February
        set.add(DateTools.nthWeekday(year, Month.FEBRUARY, 3, DayOfWeek.MONDAY));
        
        // Memorial Day: Last Monday of May
        set.add(DateTools.lastWeekday(year, Month.MAY, DayOfWeek.MONDAY));
        
        // Juneteenth: June 19
        set.add(LocalDate.of(year, Month.JUNE, 19));
        
        // Independence Day: July 4
        set.add(LocalDate.of(year, Month.JULY, 4));
        
        // Labor Day: 1st Monday of September
        set.add(DateTools.nthWeekday(year, Month.SEPTEMBER, 1, DayOfWeek.MONDAY));
        
        // Veterans Day: November 11
        set.add(LocalDate.of(year, Month.NOVEMBER, 11));
        
        // Thanksgiving: 4th Thursday of November
        set.add(DateTools.nthWeekday(year, Month.NOVEMBER, 4, DayOfWeek.THURSDAY));
        
        // Christmas Day: December 25
        set.add(LocalDate.of(year, Month.DECEMBER, 25));
        
        return set;
    }

    /** Calculates  date after a given number of school days, skipping weekends and holidays. */
    public LocalDate dateAfterSchoolDays(LocalDate start, int schoolDays) {
        return DateTools.addSchoolDays(start, schoolDays, holidays);
    }
}