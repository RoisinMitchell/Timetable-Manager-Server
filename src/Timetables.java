import java.util.ArrayList;

public class Timetables {
    private ArrayList<Timetable> timetables;

    public Timetables(){
        this.timetables = new ArrayList<>();
    }

    public void addTimetable(Timetable timetable){
        timetables.add(timetable);
    }

    public Timetable getTimetable(String className){
        if(!timetables.isEmpty()){
            for (Timetable timetable : timetables) {
                if (timetable.getClassName().equalsIgnoreCase(className)) {
                    return timetable;
                }
            }
        }
        return null;
    }

    public String displayTimetable(String className){

        if(!timetables.isEmpty()){
            for (Timetable timetable : timetables) {
                if (timetable.getClassName().equalsIgnoreCase(className)) {
                    return timetable.toString();
                }
            }
        }

        return "Timetable is not there (Timetables class)";
    }
}

