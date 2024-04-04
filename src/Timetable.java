import java.util.ArrayList;

public class Timetable {
    private ArrayList<ClassSchedule> classes;
    private String className;

    public Timetable() {
        classes = new ArrayList<>();
    }

    public void addClass(ClassSchedule classSchedule) {
        this.className = classSchedule.getClassName();
        this.classes.add(classSchedule);
    }

    public void removeClass(ClassSchedule classSchedule) {
        classes.removeIf(bookedClass -> bookedClass.equals(classSchedule));
    }

    public void setClassName(String className){
        this.className = className;
    }

    public String getClassName(){
        return this.className;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Timetable for ").append(className).append(":\n");

        for (ClassSchedule schedule : classes) {
            sb.append(schedule.toString()).append("\n");
        }

        return sb.toString();
    }
}