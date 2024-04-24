public class TimetableController {

    private final RequestParser parser;
    private final TimetableModel timetableModel;

    // Constructor initialises the parser and the model
    public TimetableController() {
        parser = new RequestParser();
        timetableModel = new TimetableModel();
    }

    // Method to add a schedule. It parses the request into a ScheduleModel and tries to add it to the model
    public String addSchedule(String request) {
        ScheduleModel schedule = parser.parseScheduleRequest(request);
        try {
            return timetableModel.addSchedule(schedule);
        } catch (IncorrectActionException e) {
            return e.message;
        }
    }

    // Method to remove a class. It parses the request into a ScheduleModel and tries to remove it from the model
    public String removeClass(String request){
        ScheduleModel schedule = parser.parseScheduleRequest(request);
        try {
            return timetableModel.removeSchedule(schedule);
        } catch (IncorrectActionException e) {
            return e.message;
        }
    }

    // Method to display the timetable for a class. It tries to get the schedules for the given class from the model
    public String displayTimetable(String className) {
        try {
            return timetableModel.displaySchedules(className);
        } catch (IncorrectActionException e) {
            return e.message;
        }
    }

    // Method to request early lectures for a course. It tries to reschedule the lectures for the given course in the model
    public String requestEarlyLectures(String courseID) {
        try{
            return timetableModel.requestEarlyScheduling(courseID);
        } catch (IncorrectActionException e) {
            return e.message;
        }
    }
}