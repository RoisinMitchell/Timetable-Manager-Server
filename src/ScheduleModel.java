import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public class ScheduleModel {
    private String courseID;
    private String module;
    private String room;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek day;

    public ScheduleModel(String courseID, String module, String room, DayOfWeek day, LocalTime startTime, LocalTime endTime){
        this.courseID = courseID;
        this.module = module;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
    }

    public ScheduleModel(){

    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseID() {
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

    public void setDay(DayOfWeek day){
        this.day = day;
    }

    public DayOfWeek getDay(){
        return this.day;
    }

    public long getDuration(){
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMinutes();
    }

    @Override
    public String toString() {
        return this.courseID + " - " + this.module + " - " + this.room + " - " + this.startTime + " - " + this.endTime + " - " + this.day;
    }

    @Override
    public boolean equals(Object object) {
        ScheduleModel schedule = (ScheduleModel) object;
        return courseID.equals(schedule.courseID) &&
                module.equals(schedule.module) &&
                room.equals(schedule.room) &&
                day.equals(schedule.day) &&
                startTime.equals(schedule.startTime) &&
                endTime.equals(schedule.endTime);
    }

}