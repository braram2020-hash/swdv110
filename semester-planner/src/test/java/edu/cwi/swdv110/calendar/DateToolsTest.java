package edu.cwi.swdv110.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class DateToolsTest {

    // ---------- Part 1 ----------
    @Test
    void holidaysFromRules() {
        assertEquals(LocalDate.of(2027, 1, 18),
                DateTools.nthWeekday(2027, Month.JANUARY, 3, DayOfWeek.MONDAY));
        assertEquals(LocalDate.of(2027, 11, 25),
                DateTools.nthWeekday(2027, Month.NOVEMBER, 4, DayOfWeek.THURSDAY));
        assertEquals(LocalDate.of(2027, 5, 31),
                DateTools.lastWeekday(2027, Month.MAY, DayOfWeek.MONDAY));
    }

    @Test
    void formatShort() {
        assertEquals("Mon, Jan 11", DateTools.formatShort(LocalDate.of(2027, 1, 11)));
        assertEquals("Wed, Mar 3", DateTools.formatShort(LocalDate.of(2027, 3, 3)));
    }

    @Test
    void weekend() {
        assertTrue(DateTools.isWeekend(LocalDate.of(2027, 1, 9)));
        assertTrue(DateTools.isWeekend(LocalDate.of(2027, 1, 10)));
        assertFalse(DateTools.isWeekend(LocalDate.of(2027, 1, 11)));
    }

    // ---------- Part 2 ----------
    @Test
    void parsesThreeFormats() {
        LocalDate expected = LocalDate.of(2027, 3, 15);
        assertEquals(expected, DateTools.parseFlexible("2027-03-15"));
        assertEquals(expected, DateTools.parseFlexible("3/15/2027"));
        assertEquals(expected, DateTools.parseFlexible(" Mar 15, 2027 "));
    }

    @Test
    void rejectsBadDates() {
        assertThrows(IllegalArgumentException.class, () -> DateTools.parseFlexible("next tuesday"));
        assertThrows(IllegalArgumentException.class, () -> DateTools.parseFlexible("2/30/2027"));
        assertThrows(IllegalArgumentException.class, () -> DateTools.parseFlexible("15/3/2027"));
    }

    @Test
    void schoolDaysSkipWeekendsAndHolidays() {
        Set<LocalDate> none = Set.of();
        assertEquals(LocalDate.of(2027, 1, 11),
                DateTools.addSchoolDays(LocalDate.of(2027, 1, 8), 1, none));
        assertEquals(LocalDate.of(2027, 1, 18),
                DateTools.addSchoolDays(LocalDate.of(2027, 1, 11), 5, none));
        assertEquals(LocalDate.of(2027, 1, 19),
                DateTools.addSchoolDays(LocalDate.of(2027, 1, 11), 5, Set.of(LocalDate.of(2027, 1, 18))));
    }

    @Test
    void latePenalty() {
        LocalDateTime due = LocalDateTime.of(2027, 2, 1, 23, 59);
        assertEquals(0, DateTools.latePenaltyPercent(due, due));
        assertEquals(0, DateTools.latePenaltyPercent(due, due.minusHours(3)));
        assertEquals(10, DateTools.latePenaltyPercent(due, due.plusMinutes(1)));
        assertEquals(10, DateTools.latePenaltyPercent(due, due.plusHours(24)));
        assertEquals(20, DateTools.latePenaltyPercent(due, due.plusHours(24).plusMinutes(1)));
        assertEquals(50, DateTools.latePenaltyPercent(due, due.plusDays(7)));
    }

    @Test
    void timeZones() {
        ZonedDateTime boise = ZonedDateTime.of(2027, 1, 11, 18, 0, 0, 0, ZoneId.of("America/Boise"));
        assertEquals("Mon, Jan 11 at 6:00 PM MST", DateTools.inZone(boise, ZoneId.of("America/Boise")));
        assertEquals("Mon, Jan 11 at 8:00 PM EST", DateTools.inZone(boise, ZoneId.of("America/New_York")));
        assertEquals("Tue, Jan 12 at 10:00 AM JST", DateTools.inZone(boise, ZoneId.of("Asia/Tokyo")));
    }

    @Test
    void daylightSavingTime() {
        ZoneId phoenix = ZoneId.of("America/Phoenix");
        ZonedDateTime jan = ZonedDateTime.of(2027, 1, 11, 18, 0, 0, 0, ZoneId.of("America/Boise"));
        ZonedDateTime apr = ZonedDateTime.of(2027, 4, 5, 18, 0, 0, 0, ZoneId.of("America/Boise"));
        assertEquals("Mon, Jan 11 at 6:00 PM MST", DateTools.inZone(jan, phoenix));
        assertEquals("Mon, Apr 5 at 5:00 PM MST", DateTools.inZone(apr, phoenix));
    }
}