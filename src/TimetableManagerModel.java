import java.time.LocalTime;
import java.util.ArrayList;
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
            pool.invoke(new EarlySchedulesShiftTask(0, classSchedules.size(), classSchedules, courseID));
            pool.shutdown();
            return "Request early scheduling successful!";
        }
    }

    public class EarlySchedulesShiftTask extends RecursiveAction {
        private final int threshold = 4; // Threshold for sublist size
        private final int start;
        private final int end;
        private final ArrayList<ClassSchedule> schedules;
        private final String courseID;

        public EarlySchedulesShiftTask(int start, int end, ArrayList<ClassSchedule> schedules, String courseID) {
            this.start = start;
            this.end = end;
            this.schedules = schedules;
            this.courseID = courseID;
        }

        @Override
        protected void compute() {
            int length = end - start;
            if (length <= threshold) {
                processSchedules();
            } else {
                int middle = start + length / 2;
                invokeAll(
                        new EarlySchedulesShiftTask(start, middle, schedules, courseID),
                        new EarlySchedulesShiftTask(middle, end, schedules, courseID)
                );
            }
        }

        private void processSchedules() {
            for (int i = start; i < end; i++) {
                ClassSchedule schedule = schedules.get(i);
                if (schedule.getClassId().equalsIgnoreCase(courseID)) {
                    synchronized (schedule) {
                        long duration = schedule.getDuration();

                        // Find earliest available time slot
                        LocalTime earliestStartTime = findEarliestStartTime(schedule.getRoom(), duration);
                        schedule.setStartTime(earliestStartTime);
                        schedule.setEndTime(earliestStartTime.plusMinutes(duration));
                    }
                }
            }
        }

        private LocalTime findEarliestStartTime(String room, long duration) {
            LocalTime earliestStartTime = LocalTime.of(9, 0); // Start from 9:00 AM
            for (ClassSchedule existingSchedule : schedules) {
                if (existingSchedule.getRoom().equals(room) && existingSchedule.getStartTime().equals(earliestStartTime)) {
                    earliestStartTime = existingSchedule.getEndTime();
                }
            }
            return earliestStartTime;
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