import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimetableModelTest {
    private TimetableModel timetable;

    @BeforeEach
    void setUp() {
        timetable = new TimetableModel();
    }

    @Test
    void addingNonOverlappingScheduleIsSuccessful() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertEquals("Request add class successful!", timetable.addSchedule(schedule));
    }

    @Test
    void addingOverlappingScheduleThrowsException() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetable.addSchedule(schedule);
        ScheduleModel overlappingSchedule = new ScheduleModel("CS102", "Data Structures", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));
        assertThrows(IncorrectActionException.class, () -> timetable.addSchedule(overlappingSchedule));
    }

    @Test
    void removingExistingScheduleIsSuccessful() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetable.addSchedule(schedule);
        assertEquals("Request remove class successful", timetable.removeSchedule(schedule));
    }

    @Test
    void removingNonExistingScheduleThrowsException() {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertThrows(IncorrectActionException.class, () -> timetable.removeSchedule(schedule));
    }

    @Test
    void displayingSchedulesForExistingCourseIsSuccessful() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetable.addSchedule(schedule);
        assertDoesNotThrow(() -> timetable.displaySchedules("CS101"));
    }

    @Test
    void displayingSchedulesForNonExistingCourseThrowsException() {
        assertThrows(IncorrectActionException.class, () -> timetable.displaySchedules("CS101"));
    }

    @Test
    void requestingEarlySchedulingForExistingCourseIsSuccessful() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetable.addSchedule(schedule);
        assertDoesNotThrow(() -> timetable.requestEarlyScheduling("CS101"));
    }

    @Test
    void requestingEarlySchedulingForNonExistingCourseThrowsException() {
        assertThrows(IncorrectActionException.class, () -> timetable.requestEarlyScheduling("CS101"));
    }
}