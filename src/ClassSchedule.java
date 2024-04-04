import java.time.LocalDate;
import java.time.LocalTime;

public class ClassSchedule {
    private String className; // LM051-2022
    private String module; // CS4115
    private String roomCode; // S205
    private LocalTime startTime; // 12:00
    private LocalTime endTime; // 13:00
    private LocalDate date; // yyyy-mm-dd

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule() {
        return this.module;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() {
        return this.roomCode;
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

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public String getDayOfWeek() {
        return this.date.getDayOfWeek().toString();
    }

    @Override
    public String toString() {
        return this.className + ", " + this.module + ", " + this.roomCode + ", " + this.startTime + ", " + this.endTime + ", " + this.date;
    }

    public boolean equals(ClassSchedule classSchedule) {
        return this.getClassName().equals(classSchedule.getClassName()) &&
                this.getModule().equals(classSchedule.getModule()) &&
                this.getRoomCode().equals(classSchedule.getRoomCode()) &&
                this.getDate().equals(classSchedule.getDate()) &&
                this.getStartTime().equals(classSchedule.getStartTime()) &&
                this.getEndTime().equals(classSchedule.getEndTime());
    }
}