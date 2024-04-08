import java.time.LocalTime;
import java.util.ArrayList;

public class ScheduleManager {
    private ArrayList<ClassSchedule> classSchedules;

    public ScheduleManager(){
        this.classSchedules = new ArrayList<>();
    }

    public String addClass(ClassSchedule classSchedule) throws IncorrectActionException {

        for (ClassSchedule existingClass : classSchedules) {
            String room = existingClass.getRoom();
            String day = existingClass.getDay();
            LocalTime existingStartTime = existingClass.getStartTime();
            LocalTime existingEndTime = existingClass.getEndTime();

            if (room.equalsIgnoreCase(classSchedule.getRoom())) { // Is the room matching
                if (day.equalsIgnoreCase(classSchedule.getDay())) { // Is the day matching
                    LocalTime newStartTime = classSchedule.getStartTime();
                    LocalTime newEndTime = classSchedule.getEndTime();

                    if (isTimeOverlapped(newStartTime, existingStartTime, newEndTime, existingEndTime)) { // Are the times overlapping
                        throw new IncorrectActionException("The room is not available for this time!");
                    }
                }
            }
        }
        // If no overlap is found add the class
        classSchedules.add(classSchedule);
        return "Request add class successful!";
    }


    public String removeClass(ClassSchedule classSchedule) throws IncorrectActionException {

        for (ClassSchedule existingClass : classSchedules) {
            if (existingClass.equals(classSchedule)) {
                classSchedules.remove(existingClass);
                return "Request remove class successful";
            }
        }

        throw new IncorrectActionException("The schedule does not exist!");
    }

    public String displaySchedule(String classId) throws IncorrectActionException {

        String schedule = "";

        for(ClassSchedule existingClass : classSchedules){
            if(existingClass.getClassId().equalsIgnoreCase(classId)){
                schedule += existingClass.toString() + "\n";
            }
        }

        if(schedule.trim().isEmpty()){
            throw new IncorrectActionException("The schedule is empty, cannot display");
        }else{
            return schedule;
        }
    }

    private boolean isTimeOverlapped(LocalTime newStartTime, LocalTime existingStartTime, LocalTime newEndTime, LocalTime existingEndTime) {
        boolean startsBeforeExistingEnd = newStartTime.isBefore(existingEndTime); // Is new schedule before existing one ends
        boolean endsAfterExistingStart = newEndTime.isAfter(existingStartTime); // Does new schedule end after existing one starts
        return startsBeforeExistingEnd && endsAfterExistingStart; // If overlap return true, if no overlap return false
    }
}
