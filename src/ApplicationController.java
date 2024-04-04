public class ApplicationController {

    private Timetables timetables;
    private RequestParser parser;
    private ClassScheduler classScheduler;
    private boolean requestOutcome;
    private Timetable timetable;
    private ClassSchedule classSchedule;
    private String className;

    public ApplicationController() {
        timetables = new Timetables();
        parser = new RequestParser();
        classScheduler = new ClassScheduler();
    }

    public boolean addClass(String request) throws IncorrectActionException {
        classSchedule = parser.parseScheduleRequest(request);
        className = classSchedule.getClassName();

        timetable = timetables.getTimetable(className);

        if (timetable == null) {
            timetable = new Timetable();
            timetable.setClassName(className);
            timetables.addTimetable(timetable);
        }

        requestOutcome = classScheduler.addClass(classSchedule);

        if (requestOutcome) {
            timetable.addClass(classSchedule);
        }

        return requestOutcome;
    }

    public boolean removeClass(String request) throws IncorrectActionException {
        classSchedule = parser.parseScheduleRequest(request);
        className = classSchedule.getClassName();
        timetable = timetables.getTimetable(className);

        if (timetable == null) {
            return false;
        }

        requestOutcome = classScheduler.removeClass(classSchedule);

        if (requestOutcome) {
            timetable.removeClass(classSchedule);
        }

        return requestOutcome;

    }

    public String displayTimetable(String className) {
        return timetables.displayTimetable(className);
    }
}