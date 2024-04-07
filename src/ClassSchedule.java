import java.time.LocalTime;

public class ClassSchedule {
    private String classId; // LM051-2022
    private String module; // CS4115
    private String room; // S205
    private LocalTime startTime; // 12:00
    private LocalTime endTime; // 13:00
    private String day; // yyyy-mm-dd

    public void setClassId(String className) {
        this.classId = className;
    }

    public String getClassId() {
        return this.classId;
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

    @Override
    public String toString() {
        return this.classId + " - " + this.module + " - " + this.room + " - " + this.startTime + " - " + this.endTime + " - " + this.day;
    }

    public boolean equals(ClassSchedule classSchedule) {
        return this.getClassId().equalsIgnoreCase(classSchedule.getClassId()) &&
                this.getModule().equalsIgnoreCase(classSchedule.getModule()) &&
                this.getRoom().equalsIgnoreCase(classSchedule.getRoom()) &&
                this.getDay().equalsIgnoreCase(classSchedule.getDay()) &&
                this.getStartTime().equals(classSchedule.getStartTime()) &&
                this.getEndTime().equals(classSchedule.getEndTime());
    }
}