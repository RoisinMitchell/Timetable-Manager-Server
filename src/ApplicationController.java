public class ApplicationController {
    private RequestParser parser;
    private ScheduleManager scheduleManager;
    private ClassSchedule classSchedule;
    private String responseMessage;

    public ApplicationController() {
        parser = new RequestParser();
        scheduleManager = new ScheduleManager();
    }

    public String addClass(String request) {
        classSchedule = parser.parseScheduleRequest(request);

        try {
            responseMessage = scheduleManager.addClass(classSchedule);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println("Error occurred:\n" + responseMessage); // Server app display
        }

        return responseMessage;
    }

    public String removeClass(String request){

        classSchedule = parser.parseScheduleRequest(request);

        try {
            responseMessage = scheduleManager.removeClass(classSchedule);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println("Error occurred:\n" + responseMessage);
        }

        return responseMessage;
    }

    public String displayTimetable(String className) {
        try {
            responseMessage = scheduleManager.displaySchedule(className);
        } catch (IncorrectActionException e) {
            responseMessage = e.message;
            System.out.println("Error occurred:\n" + responseMessage);
        }

        return responseMessage;
    }
}