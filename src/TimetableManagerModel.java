import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class TimetableManagerModel {
    private final ArrayList<ClassSchedule> classSchedules;

    public TimetableManagerModel() {
        this.classSchedules = new ArrayList<>();
    }

    public String addClass(ClassSchedule classSchedule) throws IncorrectActionException {
        synchronized (classSchedules) {
            for (ClassSchedule existingClass : classSchedules) {
                if (isOverlap(existingClass, classSchedule)) {
                    throw new IncorrectActionException("The room is not available for this time!");
                }
            }
            classSchedules.add(classSchedule);
        }
        return "Request add class successful!";
    }

    public String removeClass(ClassSchedule classSchedule) throws IncorrectActionException {
        synchronized (classSchedules) {
            if (classSchedules.remove(classSchedule)) {
                return "Request remove class successful";
            }
        }
        throw new IncorrectActionException("The schedule does not exist!");
    }

    public String displaySchedule(String classId) throws IncorrectActionException {
        synchronized (classSchedules) {
            StringBuilder schedule = new StringBuilder();
            for (ClassSchedule existingClass : classSchedules) {
                if (existingClass.getClassId().equalsIgnoreCase(classId)) {
                    schedule.append(existingClass.toString()).append("\n");
                }
            }
            if (schedule.isEmpty()) {
                throw new IncorrectActionException("The schedule is empty, cannot display");
            }
            return schedule.toString();
        }
    }

    public String requestEarlyScheduling(String courseID) {
        synchronized (classSchedules) {
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(new EarlySchedulesShiftTask(0, classSchedules.size(), classSchedules));
            pool.shutdown();
            return "Request early scheduling successful!";
        }
    }

    private class EarlySchedulesShiftTask extends RecursiveAction {
        private final int threshold = 1; // Threshold for sublist size
        private final int start;
        private final int end;
        private final ArrayList<ClassSchedule> schedules;

        public EarlySchedulesShiftTask(int start, int end, ArrayList<ClassSchedule> schedules) {
            this.start = start;
            this.end = end;
            this.schedules = schedules;
        }

        @Override
        protected void compute() {
            int length = end - start;
            if (length <= threshold) {
                processSchedules();
            } else {
                int middle = start + length / 2;
                invokeAll(
                        new EarlySchedulesShiftTask(start, middle, schedules),
                        new EarlySchedulesShiftTask(middle, end, schedules)
                );
            }
        }

        private void processSchedules() {
            for (int i = start; i < end; i++) {
                ClassSchedule schedule = schedules.get(i);
                synchronized (schedule) {
                    // Calculate duration
                    long duration = schedule.getEndTime().toSecondOfDay() - schedule.getStartTime().toSecondOfDay();

                    // Check room availability
                    boolean roomAvailable = isRoomAvailable(schedule.getRoom(), schedule.getStartTime(), duration);

                    // If room is available, assign early morning timeslot
                    if (roomAvailable) {
                        LocalTime earlyMorningStartTime = LocalTime.of(9, 0);
                        schedule.setStartTime(earlyMorningStartTime);
                        schedule.setEndTime(earlyMorningStartTime.plusSeconds(duration));
                    }
                }
            }
        }

        private boolean isRoomAvailable(String room, LocalTime startTime, long duration) {
            for (ClassSchedule existingSchedule : schedules) {
                synchronized (existingSchedule) {
                    // Check if the existing schedule is for the same room
                    if (existingSchedule.getRoom().equals(room)) {
                        // Calculate the end time of the existing schedule
                        LocalTime existingEndTime = existingSchedule.getEndTime();

                        // Check for overlap between existing schedule and the given timeslot
                        if (existingEndTime.isAfter(startTime) && existingSchedule.getStartTime().isBefore(startTime.plusSeconds(duration))) {
                            return false; // Room is not available for the entire duration
                        }
                    }
                }
            }
            return true; // Room is available for the entire duration
        }
    }

    // Method to check if two class schedules overlap
    private boolean isOverlap(ClassSchedule existingClass, ClassSchedule newClass) {
        return existingClass.getRoom().equalsIgnoreCase(newClass.getRoom()) &&
                existingClass.getDay().equalsIgnoreCase(newClass.getDay()) &&
                existingClass.getEndTime().isAfter(newClass.getStartTime()) &&
                existingClass.getStartTime().isBefore(newClass.getEndTime());
    }
}