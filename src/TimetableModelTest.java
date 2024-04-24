import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

public class TimetableModelTest {
    private TimetableModel timetableModel;

    @BeforeEach
    public void setup() {
        timetableModel = new TimetableModel();
    }

    @Test
    public void addScheduleSuccessfully() {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertDoesNotThrow(() -> timetableModel.addSchedule(schedule));
    }

    @Test
    public void addScheduleWithOverlapThrowsException() throws IncorrectActionException {
        ScheduleModel schedule1 = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        ScheduleModel schedule2 = new ScheduleModel("CS102", "Data Structures", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetableModel.addSchedule(schedule1);
        assertThrows(IncorrectActionException.class, () -> timetableModel.addSchedule(schedule2));
    }

    @Test
    public void removeExistingScheduleSuccessfully() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetableModel.addSchedule(schedule);
        assertDoesNotThrow(() -> timetableModel.removeSchedule(schedule));
    }

    @Test
    public void removeNonExistingScheduleThrowsException() {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertThrows(IncorrectActionException.class, () -> timetableModel.removeSchedule(schedule));
    }

    @Test
    public void displaySchedulesForExistingCourse() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetableModel.addSchedule(schedule);
        assertDoesNotThrow(() -> timetableModel.displaySchedules("CS101"));
    }

    @Test
    public void displaySchedulesForNonExistingCourseThrowsException() {
        assertThrows(IncorrectActionException.class, () -> timetableModel.displaySchedules("CS101"));
    }

    @Test
    public void requestEarlySchedulingForExistingCourse() throws IncorrectActionException {
        ScheduleModel schedule = new ScheduleModel("CS101", "Intro to CS", "Room 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        timetableModel.addSchedule(schedule);
        assertDoesNotThrow(() -> timetableModel.requestEarlyScheduling("CS101"));
    }

    @Test
    public void requestEarlySchedulingForNonExistingCourseThrowsException() {
        assertThrows(IncorrectActionException.class, () -> timetableModel.requestEarlyScheduling("CS101"));
    }
}