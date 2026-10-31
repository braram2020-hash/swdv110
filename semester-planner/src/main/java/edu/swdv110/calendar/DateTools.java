package edu.cwi.swdv110.calendar;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Static date helpers.*/
public final class DateTools {

    // Formateador para "Mon, Jan 11" usando Locale.US
    private static final DateTimeFormatter SHORT_FORMATTER = 
        DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US);

    
    private static final DateTimeFormatter DATETIME_FORMATTER = 
        DateTimeFormatter.ofPattern("EEE, MMM d 'at' h:mm a z", Locale.US);

    // Lista de los tres formatos de entrada aceptados con validación STRICT y 'uuuu' para el año
    private static final List<DateTimeFormatter> INPUT_FORMATTERS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("M/d/uuuu", Locale.US).withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.US).withResolverStyle(ResolverStyle.STRICT)
    );

    private DateTools() { }

    //Part 1

    /**Given weekday of a month, (2027, JANUARY, 3, MONDAY) -> 2027-01-18 (MLK Day). */
    public static LocalDate nthWeekday(int year, Month month, int n, DayOfWeek day) {
        return LocalDate.of(year, month, 1)
                        .with(TemporalAdjusters.dayOfWeekInMonth(n, day));
    }

    /** The last given weekday of a month, (2027, MAY, MONDAY) -> 2027-05-31 (Memorial Day). */
    public static LocalDate lastWeekday(int year, Month month, DayOfWeek day) {
        return LocalDate.of(year, month, 1)
                        .with(TemporalAdjusters.lastInMonth(day));
    }

    /** "Mon, Jan 11" */
    public static String formatShort(LocalDate date) {
        return date.format(SHORT_FORMATTER);
    }

    /** True for Saturday and Sunday. */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    // Part 2

    public static LocalDate parseFlexible(String text) {
        String trimmed = text.trim();
        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Probar con el siguiente formateador
            }
        }
        throw new IllegalArgumentException("Unrecognized date: " + text);
    }

   
    public static LocalDate addSchoolDays(LocalDate start, int days, Set<LocalDate> holidays) {
        LocalDate current = start;
        int added = 0;
        while (added < days) {
            current = current.plusDays(1);
            if (!isWeekend(current) && !holidays.contains(current)) {
                added++;
            }
        }
        return current;
    }

   
    public static int latePenaltyPercent(LocalDateTime due, LocalDateTime submitted) {
        if (!submitted.isAfter(due)) {
            return 0;
        }
        long minutes = Duration.between(due, submitted).toMinutes();
        long blocks = (minutes + 1439) / 1440; 
        int penalty = (int) (blocks * 10);
        return Math.min(penalty, 50);
    }

    public static String inZone(ZonedDateTime when, ZoneId zone) {
        ZonedDateTime converted = when.withZoneSameInstant(zone);
        return converted.format(DATETIME_FORMATTER);
    }
}