package edu.cwi.swdv110.calendar;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SemesterTest {

    @Test
    void testSemesterInitializationAndHolidays() {
        LocalDate start = LocalDate.of(2027, 1, 11);
        LocalDate end = LocalDate.of(2027, 5, 7);
        Semester semester = new Semester(2027, start, end);

        assertEquals(2027, semester.getYear());
        assertEquals(start, semester.getStartDate());
        assertEquals(end, semester.getEndDate());

        Set<LocalDate> holidays = semester.getHolidays();
        // Verificar que incluya MLK Day (18 de enero de 2027)
        assertTrue(holidays.contains(LocalDate.of(2027, 1, 18)));
        // Verificar que incluya Presidents' Day (15 de febrero de 2027)
        assertTrue(holidays.contains(LocalDate.of(2027, 2, 15)));
    }

    @Test
    void testDateAfterSchoolDaysInSemester() {
        LocalDate start = LocalDate.of(2027, 1, 11);
        LocalDate end = LocalDate.of(2027, 5, 7);
        Semester semester = new Semester(2027, start, end);

        // Sumar 5 días escolares desde el 11 de enero (saltando fin de semana y MLK Day el 18 de enero)
        LocalDate target = semester.dateAfterSchoolDays(LocalDate.of(2027, 1, 11), 5);
        assertEquals(LocalDate.of(2027, 1, 19), target);
    }
}