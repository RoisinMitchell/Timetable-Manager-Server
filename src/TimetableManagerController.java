public class TimetableManagerController {
    private final RequestParser parser;
    private final TimetableManagerModel timetableManagerModel;
    private ClassSchedule classSchedule;

    public TimetableManagerController() {
        parser = new RequestParser();
        timetableManagerModel = new TimetableManagerModel();
    }

    public String addClass(String request) {
        classSchedule = parser.parseScheduleRequest(request);
        String responseMessage;

        try {
            responseMessage = timetableManagerModel.addClass(classSchedule);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println("Error occurred:\n" + responseMessage);
        }

        return responseMessage;
    }

    public String removeClass(String request){
        classSchedule = parser.parseScheduleRequest(request);
        String responseMessage;

        try {
            responseMessage = timetableManagerModel.removeClass(classSchedule);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println("Error occurred:\n" + responseMessage);
        }

        return responseMessage;
    }

    public String displayTimetable(String className) {
        String responseMessage;
        try {
            responseMessage = timetableManagerModel.displaySchedule(className);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println("Error occurred:\n" + responseMessage);
        }
        return responseMessage;
    }

    public String requestEarlyLectures(String courseID){
        String responseMessage;
        responseMessage = timetableManagerModel.requestEarlyScheduling(courseID);
        return responseMessage;
    }
}