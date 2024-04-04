import java.time.LocalDate;
import java.time.LocalTime;

public class RequestParser {

    public String getRequestType(String clientRequest){
        String[] parts = clientRequest.split(",");
        return parts[0].trim();
    }

    public ClassSchedule parseScheduleRequest(String clientRequest){

        ClassSchedule classSchedule = new ClassSchedule();
        String[] scheduleParts = clientRequest.split(",");

        classSchedule.setClassName(scheduleParts[1].trim());
        classSchedule.setModule(scheduleParts[2].trim());
        classSchedule.setRoomCode(scheduleParts[3].trim());
        classSchedule.setStartTime(parseTime(scheduleParts[4].trim()));
        classSchedule.setEndTime(parseTime(scheduleParts[5].trim()));
        classSchedule.setDate(parseDate(scheduleParts[6].trim()));

        return classSchedule;
    }

    private LocalTime parseTime(String timeString) {
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        return LocalTime.of(hour, 0);
    }

    private LocalDate parseDate(String dateString) {
        String[] dateParts = dateString.split("-");
        int year = Integer.parseInt(dateParts[0].trim());
        int month = Integer.parseInt(dateParts[1].trim());
        int day = Integer.parseInt(dateParts[2].trim());
        return LocalDate.of(year, month, day);
    }
}