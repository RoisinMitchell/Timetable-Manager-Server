import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Stream;

public class TimetableModel {
    private final Map<DayOfWeek, Map<String, List<ScheduleModel>>> schedules;

    public TimetableModel() {
        this.schedules = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            schedules.put(day, new HashMap<>());
        }
    }

    public String addSchedule(ScheduleModel schedule) throws IncorrectActionException {
        synchronized (schedules) {
            // Check if the new schedule is overlapping with any existing schedule
            if (isOverlapping(schedule)) {
                throw new IncorrectActionException("ERROR - There is an overlap in bookings!");
            }
            DayOfWeek day = schedule.getDay();
            String courseId = schedule.getCourseID();

            Map<String, List<ScheduleModel>> schedulesForDay = schedules.get(day);
            List<ScheduleModel> courseSchedules = schedulesForDay.computeIfAbsent(courseId, k -> new ArrayList<>());

            courseSchedules.add(schedule);
        }
        return "Request add class successful!";
    }

    public String removeSchedule(ScheduleModel schedule) throws IncorrectActionException {
        synchronized (schedules) {
            List<ScheduleModel> schedulesByCourseID = schedules.get(schedule.getDay()).get(schedule.getCourseID());
            // Remove the schedule if it exists
            if (schedulesByCourseID != null && schedulesByCourseID.remove(schedule)) {
                return "Request remove class successful";
            }
        }
        throw new IncorrectActionException("ERROR - The schedule does not exist!");
    }

    public String displaySchedules(String courseID) throws IncorrectActionException {
        synchronized (schedules) {

            Collection<Map<String, List<ScheduleModel>>> allDaySchedules = schedules.values();

            // Using a stream to process the day schedules easily
            Stream<Map<String, List<ScheduleModel>>> daySchedulesStream = allDaySchedules.stream();
            boolean courseDoesNotExist = daySchedulesStream.noneMatch(daySchedules -> daySchedules.containsKey(courseID));

            if (courseDoesNotExist) {
                throw new IncorrectActionException("ERROR - Course ID does not exist!");
            }

            StringBuilder schedule = new StringBuilder();

            for (DayOfWeek day : DayOfWeek.values()) {
                // Get the list of schedules for the course ID
                List<ScheduleModel> schedulesForCourse = schedules.get(day).getOrDefault(courseID, new ArrayList<>());

                // Sort the schedules by start time
                schedulesForCourse.sort(Comparator.comparing(ScheduleModel::getStartTime));

                // Add each schedule to the schedule string
                for (ScheduleModel existingClass : schedulesForCourse) {
                    schedule.append(existingClass.toString()).append("-");
                }
            }
            if (schedule.isEmpty()) {
                throw new IncorrectActionException("ERROR - No schedule found!");
            }
            return schedule.toString();
        }
    }

    public String requestEarlyScheduling(String courseID) throws IncorrectActionException {
        synchronized (schedules) {
            Collection<Map<String, List<ScheduleModel>>> allDaySchedules = schedules.values();

            Stream<Map<String, List<ScheduleModel>>> daySchedulesStream = allDaySchedules.stream();
            boolean courseDoesNotExist = daySchedulesStream.noneMatch(daySchedules -> daySchedules.containsKey(courseID));

            if (courseDoesNotExist) {
                throw new IncorrectActionException("ERROR - Course ID does not exist!");
            }

            ForkJoinPool pool = new ForkJoinPool();
            // Invoke a new EarlyScheduling task for each day of the week
            for (DayOfWeek day : DayOfWeek.values()) {
                pool.invoke(new EarlyScheduling(courseID, day));
            }
            // Shutdown the ForkJoinPool
            pool.shutdown();
            return "Request early scheduling successful!";
        }
    }

    private boolean isOverlapping(ScheduleModel newClass) {
        for (List<ScheduleModel> scheduleList : schedules.get(newClass.getDay()).values()) {
            for (ScheduleModel existingClass : scheduleList) {
                boolean sameRoom = existingClass.getRoom().equalsIgnoreCase(newClass.getRoom());
                boolean existingEndsAfterNewStarts = existingClass.getEndTime().isAfter(newClass.getStartTime());
                boolean existingStartsBeforeNewEnds = existingClass.getStartTime().isBefore(newClass.getEndTime());

                if (sameRoom && existingEndsAfterNewStarts && existingStartsBeforeNewEnds) {
                    return true;
                }
            }
        }
        return false;
    }

    // RecursiveAction class to handle early scheduling
    private class EarlyScheduling extends RecursiveAction {
        private final String courseID;
        private final DayOfWeek day;

        public EarlyScheduling(String courseID, DayOfWeek day) {
            this.courseID = courseID;
            this.day = day;
        }

        @Override
        protected void compute() {
            List<ScheduleModel> schedulesForCourse = schedules.get(day).getOrDefault(courseID, new ArrayList<>());

            // Sorting schedules by start time
            schedulesForCourse.sort(Comparator.comparing(ScheduleModel::getStartTime));

            LocalTime newStartTime = LocalTime.of(9, 0);

            for (ScheduleModel bookedSchedule : schedulesForCourse) {

                // Calculate the new end time
                LocalTime newEndTime = newStartTime.plusMinutes(bookedSchedule.getDuration());

                // Check if the new schedule is overlapping with any schedule
                if (isOverlapping(new ScheduleModel(bookedSchedule.getCourseID(), bookedSchedule.getModule(),
                        bookedSchedule.getRoom(), day, newStartTime, newEndTime))) {
                    return;
                }


                // Update the start and end times of the schedule
                bookedSchedule.setStartTime(newStartTime);
                bookedSchedule.setEndTime(newEndTime);
                newStartTime = newEndTime;
            }
        }
    }
}