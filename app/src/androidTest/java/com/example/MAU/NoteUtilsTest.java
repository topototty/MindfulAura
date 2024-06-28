package com.example.MAU;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class NoteUtilsTest {

    private Calendar calendar;

    @Before
    public void setUp() {
        calendar = Calendar.getInstance();
    }

    @Test
    public void testFormatDate() {
        NoteUtils.setDate(calendar, 2023, Calendar.JUNE, 27);
        String formattedDate = NoteUtils.formatDate(calendar);
        assertEquals("27.06.2023", formattedDate);
    }

    @Test
    public void testSetDate() {
        NoteUtils.setDate(calendar, 2023, Calendar.JUNE, 27);
        assertEquals(2023, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        assertEquals(27, calendar.get(Calendar.DAY_OF_MONTH));
    }
}
