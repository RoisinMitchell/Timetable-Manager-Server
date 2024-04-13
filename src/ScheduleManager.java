import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduleManager {
    private final ArrayList<ClassSchedule> classSchedules;

    public ScheduleManager() {
        this.classSchedules = new ArrayList<>();
    }

    public String addClass(ClassSchedule classSchedule) throws IncorrectActionException {
        for (ClassSchedule existingClass : classSchedules) {
            if (isOverlap(existingClass, classSchedule)) {
                throw new IncorrectActionException("The room is not available for this time!");
            }
        }
        classSchedules.add(classSchedule);
        return "Request add class successful!";
    }

    public String removeClass(ClassSchedule classSchedule) throws IncorrectActionException {
        if (classSchedules.remove(classSchedule)) {
            return "Request remove class successful";
        }
        throw new IncorrectActionException("The schedule does not exist!");
    }

    public String displaySchedule(String classId) throws IncorrectActionException {
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



    public void requestEarlyLectures() {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new EarlyLecturesRequest(classSchedules, new AtomicBoolean(false)));
        pool.shutdown();
    }

    private boolean isOverlap(ClassSchedule existingClass, ClassSchedule newClass) {
        return existingClass.getRoom().equalsIgnoreCase(newClass.getRoom()) &&
                existingClass.getDay().equalsIgnoreCase(newClass.getDay()) &&
                existingClass.getEndTime().isAfter(newClass.getStartTime()) &&
                existingClass.getStartTime().isBefore(newClass.getEndTime());
    }

    private static class EarlyLecturesRequest extends RecursiveAction {
        private final ArrayList<ClassSchedule> classSchedules;
        private final AtomicBoolean earlyLectureScheduled;

        public EarlyLecturesRequest(ArrayList<ClassSchedule> classSchedules, AtomicBoolean earlyLectureScheduled) {
            this.classSchedules = classSchedules;
            this.earlyLectureScheduled = earlyLectureScheduled;
        }

        @Override
        protected void compute() {
            if (earlyLectureScheduled.get()) {
                return;
            }

            for (DayOfWeek day : DayOfWeek.values()) {
                ArrayList<ClassSchedule> daySchedules = getDaySchedules(day);
                if (!daySchedules.isEmpty()) {
                    if (checkEarlyTimesAvailability(daySchedules)) {
                        scheduleEarlyClasses(daySchedules);
                        earlyLectureScheduled.set(true);
                        return;
                    }
                }
            }
        }

        private ArrayList<ClassSchedule> getDaySchedules(DayOfWeek day) {
            ArrayList<ClassSchedule> daySchedules = new ArrayList<>();
            for (ClassSchedule classSchedule : classSchedules) {
                if (classSchedule.getDay().equalsIgnoreCase(day.name())) {
                    daySchedules.add(classSchedule);
                }
            }
            return daySchedules;
        }

        private boolean checkEarlyTimesAvailability(ArrayList<ClassSchedule> daySchedules) {
            // Check if the early morning time slot is available for scheduling
            // Implement logic to check against existing schedule
            // Return true if the slot is available, false otherwise
            // This method should be implemented according to the requirements of your application
            return true; // Placeholder implementation
        }

        private void scheduleEarlyClasses(ArrayList<ClassSchedule> daySchedules) {
            // Schedule classes in the early morning time slots
            // Modify daySchedules accordingly
            // This method should be implemented according to the requirements of your application
            System.out.println("Early lectures scheduled.");
        }
    }
}