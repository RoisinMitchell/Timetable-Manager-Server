public class TimetableManagerController {
    private final RequestParser parser;
    private final TimetableManagerModel timetableManagerModel;
    private ScheduleModel schedule;

    public TimetableManagerController() {
        parser = new RequestParser();
        timetableManagerModel = new TimetableManagerModel();
    }

    public String addSchedule(String request) {
        schedule = parser.parseScheduleRequest(request);
        String responseMessage;

        try {
            responseMessage = timetableManagerModel.addSchedule(schedule);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println(responseMessage);
        }

        return responseMessage;
    }

    public String removeClass(String request){
        schedule = parser.parseScheduleRequest(request);
        String responseMessage;

        try {
            responseMessage = timetableManagerModel.removeSchedule(schedule);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println(responseMessage);
        }

        return responseMessage;
    }

    public String displayTimetable(String className) {
        String responseMessage;
        try {
            responseMessage = timetableManagerModel.displaySchedules(className);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println(responseMessage);
        }
        return responseMessage;
    }

    public String requestEarlyLectures(String courseID) {
        String responseMessage;
        try {
            responseMessage = timetableManagerModel.requestEarlyScheduling(courseID);
        } catch (IncorrectActionException e){
            responseMessage = e.message;
            System.out.println(responseMessage);
        }
        return responseMessage;
    }
}