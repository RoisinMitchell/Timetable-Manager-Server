import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ClassScheduler {
    private final TreeMap<LocalDate, Map<LocalTime, ClassSchedule>> classSchedules;
    private String errorMessage;

    public ClassScheduler(){
        this.classSchedules = new TreeMap<>();
    }

    public boolean addClass(ClassSchedule classSchedule) throws IncorrectActionException{

        LocalDate date = classSchedule.getDate();
        LocalTime startTime = classSchedule.getStartTime();
        LocalTime endTime = classSchedule.getEndTime();
        String roomCode = classSchedule.getRoomCode();

        if(!isRoomAvailable(date, startTime, endTime, roomCode)){
            throw new IncorrectActionException("\nThe room/time is not available!");
        }else{
            for(int week = 0; week < 12; week++){
                bookClass(date, startTime, classSchedule);
                date = date.plusWeeks(1); // Move to next week's day
            }
            return true;
        }
    }

    public boolean removeClass(ClassSchedule classSchedule) throws IncorrectActionException {

        boolean removed = false;
        LocalDate date = classSchedule.getDate();
        LocalTime startTime = classSchedule.getStartTime();

        Map<LocalTime, ClassSchedule> dailySchedules = classSchedules.get(date);

        if(dailySchedules == null){
            throw new IncorrectActionException("\nSchedule does not exist!");
        }

        for (int week = 0; week < 12; week++) {

            ClassSchedule removedSchedule = dailySchedules.remove(startTime);
            if (removedSchedule != null) {
                removed = true;
                // If there are no more classes scheduled for the day, remove the date entry as well
                if (dailySchedules.isEmpty()) {
                    classSchedules.remove(date);
                }
            }
            date = date.plusWeeks(1); // Move to the same day of the next week
        }
        return removed;
    }

    private boolean isRoomAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, String room) throws IncorrectActionException{
        Map<LocalTime, ClassSchedule> dailySchedules = classSchedules.get(date);
        if (dailySchedules == null) return true; // No classes scheduled on this date

        for (ClassSchedule existingSchedule : dailySchedules.values()) {
            if (existingSchedule.getRoomCode().equals(room) &&
                    !(startTime.isAfter(existingSchedule.getEndTime()) || endTime.isBefore(existingSchedule.getStartTime()))) {
                return false; // Time overlap in same room
            }
        }
        return true;
    }

    private void bookClass(LocalDate date, LocalTime startTime, ClassSchedule classSchedule) {
        // If the date doesn't have any class schedules yet create a new map to store them
        if (!classSchedules.containsKey(date)) {
            classSchedules.put(date, new HashMap<>());
        }
        // Associate the start time of the class with its schedule for the date
        classSchedules.get(date).put(startTime, classSchedule);
    }

    public String getErrorMessage(){
        return this.errorMessage;
    }
}