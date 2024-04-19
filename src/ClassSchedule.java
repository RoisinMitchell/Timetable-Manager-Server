import java.time.Duration;
import java.time.LocalTime;

public class ClassSchedule {
    private String courseID; // LM051-2022
    private String module; // CS4115
    private String room; // S205
    private LocalTime startTime; // 12:00
    private LocalTime endTime; // 13:00
    private String day; // yyyy-mm-dd

    public ClassSchedule(String courseID, String module, String room, LocalTime startTime, LocalTime endTime, String day){
        this.courseID = courseID;
        this.module = module;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
    }

    public ClassSchedule(){

    }

    public void setClassId(String className) {
        this.courseID = className;
    }

    public String getClassId() {
        return this.courseID;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule() {
        return this.module;
    }

    public void setRoom(String roomCode) {
        this.room = roomCode;
    }

    public String getRoom() {
        return this.room;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setDay(String day){
        this.day = day;
    }

    public String getDay(){
        return this.day;
    }

    public long getDuration(){
        Duration durationObject = Duration.between(startTime, endTime);
        return durationObject.toMinutes();
    }

    @Override
    public String toString() {
        return this.courseID + " - " + this.module + " - " + this.room + " - " + this.startTime + " - " + this.endTime + " - " + this.day;
    }

}