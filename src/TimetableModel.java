import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class TimetableModel {
    // A map to store schedules by day of week and course ID
    private final Map<DayOfWeek, Map<String, List<ScheduleModel>>> schedules;

    public TimetableModel() {
        // Initialise the schedules map
        this.schedules = new LinkedHashMap<>();

        // Create a new map for each day of the week
        for (DayOfWeek day : DayOfWeek.values()) {
            schedules.put(day, new HashMap<>());
        }
    }

    // Method to add a schedule
    public String addSchedule(ScheduleModel schedule) throws IncorrectActionException {
        synchronized (schedules) {
            // Extract course ID and day from the schedule
            String courseID = schedule.getCourseID();
            DayOfWeek day = schedule.getDay();

            // Get the schedules for the given day
            Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(day);

            // Check for overlapping schedules
            for (List<ScheduleModel> schedulesByCourseID : schedulesByDay.values()) {
                for (ScheduleModel bookedSchedule : schedulesByCourseID) {
                    if (isThereOverlap(bookedSchedule, schedule)) {
                        throw new IncorrectActionException("ERROR - There is an overlap in bookings!");
                    }
                }
            }

            // Add the new schedule and update the maps
            List<ScheduleModel> schedulesByCourseID = schedulesByDay.getOrDefault(courseID, new ArrayList<>());
            schedulesByCourseID.add(schedule);
            schedulesByDay.put(courseID, schedulesByCourseID);
            schedules.put(day, schedulesByDay);
        }
        return "Request add class successful!";
    }

    // Method to remove a schedule
    public String removeSchedule(ScheduleModel schedule) throws IncorrectActionException {
        synchronized (schedules) {
            // Extract course ID and day of week from the schedule
            String courseID = schedule.getCourseID();
            DayOfWeek dayOfWeek = schedule.getDay();

            // Get the schedules for the given day and course ID
            Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(dayOfWeek);
            List<ScheduleModel> schedulesByCourseID = schedulesByDay.get(courseID);

            // Remove the schedule if it exists
            if (schedulesByCourseID != null && schedulesByCourseID.remove(schedule)) {
                return "Request remove class successful";
            }
        }
        throw new IncorrectActionException("ERROR - The schedule does not exist!");
    }

    // Method to display schedules for a course
    public String displaySchedules(String courseID) throws IncorrectActionException {
        synchronized (schedules) {
            // Check if the course exists
            boolean courseExists = schedules.values().stream()
                    .anyMatch(daySchedules -> daySchedules.containsKey(courseID));

            if (!courseExists) {
                throw new IncorrectActionException("ERROR - Course ID does not exist!");
            }

            StringBuilder schedule = new StringBuilder();
            boolean foundSchedule = false;

            // Iterate over the days of the week in the correct order
            for (DayOfWeek day : DayOfWeek.values()) {
                Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(day);
                List<ScheduleModel> schedulesForCourse = schedulesByDay.getOrDefault(courseID, new ArrayList<>());

                // Sort schedules by start time
                schedulesForCourse.sort(Comparator.comparing(ScheduleModel::getStartTime));

                // Append each schedule to the string builder
                for (ScheduleModel existingClass : schedulesForCourse) {
                    schedule.append(existingClass.toString()).append("-");
                    foundSchedule = true;
                }
            }

            if (!foundSchedule) {
                throw new IncorrectActionException("ERROR - No schedule found!");
            }
            return schedule.toString();
        }
    }

    // Method to request early scheduling for a course
    public String requestEarlyScheduling(String courseID) throws IncorrectActionException {
        synchronized (schedules) {
            // Check if the course exists
            boolean courseExists = schedules.values().stream()
                    .anyMatch(daySchedules -> daySchedules.containsKey(courseID));

            if (!courseExists) {
                throw new IncorrectActionException("ERROR - Course ID does not exist!");
            }

            // Create a fork join pool and invoke a new EarlyScheduling task for each day of the week
            ForkJoinPool pool = new ForkJoinPool();
            for (DayOfWeek day : DayOfWeek.values()) {
                pool.invoke(new EarlyScheduling(courseID, day));
            }
            pool.shutdown();
            return "Request early scheduling successful!";
        }
    }

    // RecursiveAction class to handle early scheduling
    public class EarlyScheduling extends RecursiveAction {
        private final String courseID;
        private final DayOfWeek day;

        public EarlyScheduling(String courseID, DayOfWeek day) {
            this.courseID = courseID;
            this.day = day;
        }

        @Override
        protected void compute() {
            // Get the schedules for the given day and course ID
            Map<String, List<ScheduleModel>> schedulesByDay = schedules.get(day);
            List<ScheduleModel> schedulesForCourse = schedulesByDay.getOrDefault(courseID, new ArrayList<>());

            // Define the start and end times for early scheduling
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(13, 0);

            // If there are schedules for the course, reschedule them
            if (!schedulesForCourse.isEmpty()) {
                List<ScheduleModel> newSchedulesByCourse = new ArrayList<>();
                LocalTime newStartTime = startTime;

                // Iterate over the existing schedules
                for (ScheduleModel bookedSchedule : schedulesForCourse) {
                    // Calculate the new end time
                    LocalTime newEndTime = newStartTime.plusMinutes(bookedSchedule.getDuration());
                    if (newEndTime.isAfter(endTime)) {
                        newEndTime = endTime;
                    }

                    // Check if the room is available at the new time
                    boolean isRoomAvailable = true;
                    for (ScheduleModel schedule : newSchedulesByCourse) {
                        if (isThereOverlap(schedule, new ScheduleModel(bookedSchedule.getCourseID(), bookedSchedule.getModule(), bookedSchedule.getRoom(), day, newStartTime, newEndTime))) {
                            isRoomAvailable = false;
                            break;
                        }
                    }

                    // If the room is available, add the class to the new schedule
                    if (isRoomAvailable) {
                        ScheduleModel newSchedule = new ScheduleModel(bookedSchedule.getCourseID(), bookedSchedule.getModule(), bookedSchedule.getRoom(), day, newStartTime, newEndTime);
                        newSchedulesByCourse.add(newSchedule);

                        // Update the start time for the next class
                        newStartTime = newEndTime;
                    }
                }

                // Replace the old schedules with the new schedules
                schedulesByDay.put(courseID, newSchedulesByCourse);
                schedules.put(day, schedulesByDay);
            }
        }
    }

    // Method to check if two schedules overlap
    private boolean isThereOverlap(ScheduleModel existingClass, ScheduleModel newClass) {
        return existingClass.getRoom().equalsIgnoreCase(newClass.getRoom()) &&
                existingClass.getEndTime().isAfter(newClass.getStartTime()) &&
                existingClass.getStartTime().isBefore(newClass.getEndTime());
    }
}