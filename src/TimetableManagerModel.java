import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class TimetableManagerModel {
    private final Map<DayOfWeek, Map<String, List<ScheduleModel>>> schedules;

    public TimetableManagerModel() {
        this.schedules = new LinkedHashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            schedules.put(day, new HashMap<>());
        }
    }

    public String addSchedule(ScheduleModel schedule) throws IncorrectActionException {
        synchronized (schedules) {
            String courseID = schedule.getCourseID();
            DayOfWeek day = schedule.getDay();

            // Checking for overlap within the same day and for the same courseID
            Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(day);
            List<ScheduleModel> schedulesByCourseID = schedulesByDay.getOrDefault(courseID, new ArrayList<>());

            for (ScheduleModel bookedSchedule : schedulesByCourseID) {
                if (isOverlap(bookedSchedule, schedule)) {
                    throw new IncorrectActionException("There is an overlap in bookings!");
                }
            }

            schedulesByCourseID.add(schedule);
            schedulesByDay.put(courseID, schedulesByCourseID);
            schedules.put(day, schedulesByDay);
        }
        return "Request add class successful!";
    }

    public String removeSchedule(ScheduleModel schedule) throws IncorrectActionException {
        synchronized (schedules) {
            String courseID = schedule.getCourseID();
            DayOfWeek dayOfWeek = schedule.getDay();

            Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(dayOfWeek);
            List<ScheduleModel> schedulesByCourseID = schedulesByDay.get(courseID);

            if (schedulesByCourseID != null && schedulesByCourseID.remove(schedule)) {
                return "Request remove class successful";
            }
        }
        throw new IncorrectActionException("The schedule does not exist!");
    }

    public String displaySchedules(String courseID) throws IncorrectActionException {
        synchronized (schedules) {
            StringBuilder schedule = new StringBuilder();
            boolean foundSchedule = false;

            // Iterate over the days of the week in the correct order
            for (DayOfWeek day : DayOfWeek.values()) {
                Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(day);
                List<ScheduleModel> schedulesForCourse = schedulesByDay.getOrDefault(courseID, new ArrayList<>());

                // Sort schedules by start time
                schedulesForCourse.sort(Comparator.comparing(ScheduleModel::getStartTime));

                for (ScheduleModel existingClass : schedulesForCourse) {
                    schedule.append(existingClass.toString()).append("-");
                    foundSchedule = true;
                }
            }

            if (!foundSchedule) {
                throw new IncorrectActionException("No schedule found!");
            }

            return schedule.toString();
        }
    }

    public String requestEarlyScheduling(String courseID) {
        synchronized (schedules) {
            ForkJoinPool pool = new ForkJoinPool();
            for (DayOfWeek day : DayOfWeek.values()) {
                pool.invoke(new EarlyScheduling(courseID, day));
            }
            pool.shutdown();
            return "Request early scheduling successful!";
        }
    }

    public class EarlyScheduling extends RecursiveAction {
        private final String courseID;
        private final DayOfWeek day;

        public EarlyScheduling(String courseID, DayOfWeek day) {
            this.courseID = courseID;
            this.day = day;
        }

        @Override
        protected void compute() {
            Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(day);
            List<ScheduleModel> schedulesForCourse = schedulesByDay.getOrDefault(courseID, new ArrayList<>());

            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(13, 0);

            // If the earliest start time is within the range and not already at 9:00, move classes to the earliest time slot
            if (!schedulesForCourse.isEmpty()) {
                // If there is an early slot, shift classes
                List<ScheduleModel> newSchedulesByCourse = new ArrayList<>();
                LocalTime newStartTime = startTime;

                for (ScheduleModel bookedSchedule : schedulesForCourse) {
                    LocalTime newEndTime = newStartTime.plusMinutes(bookedSchedule.getDuration());
                    if (newEndTime.isAfter(endTime)) {
                        newEndTime = endTime;
                    }

                    // Check availability at new time
                    boolean isRoomAvailable = true;
                    for (ScheduleModel schedule : newSchedulesByCourse) {
                        if (isOverlap(schedule, new ScheduleModel(bookedSchedule.getCourseID(), bookedSchedule.getModule(), bookedSchedule.getRoom(), day, newStartTime, newEndTime))) {
                            isRoomAvailable = false;
                            break;
                        }
                    }

                    if (isRoomAvailable) {
                        // Add the class to the new schedule
                        ScheduleModel newSchedule = new ScheduleModel(bookedSchedule.getCourseID(), bookedSchedule.getModule(), bookedSchedule.getRoom(), day, newStartTime, newEndTime);
                        newSchedulesByCourse.add(newSchedule);

                        // Update start time for the next class
                        newStartTime = newEndTime;
                    }
                }

                // Replace old schedules with the new schedules
                schedulesByDay.put(courseID, newSchedulesByCourse);
                schedules.put(day, schedulesByDay);
            }
        }

    }

    private boolean isOverlap(ScheduleModel existingClass, ScheduleModel newClass) {
        return existingClass.getRoom().equalsIgnoreCase(newClass.getRoom()) &&
                existingClass.getEndTime().isAfter(newClass.getStartTime()) &&
                existingClass.getStartTime().isBefore(newClass.getEndTime());
    }
}