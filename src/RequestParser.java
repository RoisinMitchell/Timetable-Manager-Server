import java.time.LocalTime;

public class RequestParser {

    public String getRequestType(String clientRequest){
        String[] parts = clientRequest.split(",");
        return parts[0].trim();
    }

    public ClassSchedule parseScheduleRequest(String clientRequest){

        ClassSchedule classSchedule = new ClassSchedule();
        String[] scheduleParts = clientRequest.split(",");

        classSchedule.setClassId(scheduleParts[1].trim());
        classSchedule.setModule(scheduleParts[2].trim());
        classSchedule.setRoom(scheduleParts[3].trim());
        classSchedule.setStartTime(parseTime(scheduleParts[4].trim()));
        classSchedule.setEndTime(parseTime(scheduleParts[5].trim()));
        classSchedule.setDay(scheduleParts[6].trim());

        return classSchedule;
    }

    private LocalTime parseTime(String timeString) {
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        return LocalTime.of(hour, 0);
    }

}