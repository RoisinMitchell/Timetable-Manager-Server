import java.time.DayOfWeek;
import java.time.LocalTime;

public class RequestParser {

    // Method to extract the request type from the client request
    public String getRequestType(String clientRequest){
        String[] parts = clientRequest.split(","); // Split the request into parts
        return parts[0].trim(); // Return the first part as the request type (The request type is always the first part)
    }

    // Method to parse a schedule request from the client
    public ScheduleModel parseScheduleRequest(String clientRequest){

        ScheduleModel schedule = new ScheduleModel();
        String[] scheduleParts = clientRequest.split(","); // Split the request into parts

        // Set the properties of the ScheduleModel object from the parts of the request
        schedule.setCourseID(scheduleParts[1].trim());
        schedule.setModule(scheduleParts[2].trim());
        schedule.setRoom(scheduleParts[3].trim());
        schedule.setStartTime(parseTime(scheduleParts[4].trim()));
        schedule.setEndTime(parseTime(scheduleParts[5].trim()));
        schedule.setDay(DayOfWeek.valueOf(scheduleParts[6].trim().toUpperCase()));

        return schedule;
    }

    // Method to parse a time string into a LocalTime object
    private LocalTime parseTime(String timeString) {
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        return LocalTime.of(hour, 0);
    }

}