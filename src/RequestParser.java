import java.time.DayOfWeek;
import java.time.LocalTime;

public class RequestParser {

    public String getRequestType(String clientRequest){
        String[] parts = clientRequest.split(",");
        return parts[0].trim();
    }

    public ScheduleModel parseScheduleRequest(String clientRequest){

        ScheduleModel schedule = new ScheduleModel();
        String[] scheduleParts = clientRequest.split(",");

        schedule.setCourseID(scheduleParts[1].trim());
        schedule.setModule(scheduleParts[2].trim());
        schedule.setRoom(scheduleParts[3].trim());
        schedule.setStartTime(parseTime(scheduleParts[4].trim()));
        schedule.setEndTime(parseTime(scheduleParts[5].trim()));
        schedule.setDay(DayOfWeek.valueOf(scheduleParts[6].trim().toUpperCase()));

        return schedule;
    }

    private LocalTime parseTime(String timeString) {
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        return LocalTime.of(hour, 0);
    }

}