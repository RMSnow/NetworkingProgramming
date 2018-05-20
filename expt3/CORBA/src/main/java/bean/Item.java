package bean;

import java.util.Date;

/**
 * Created by snow on 2018/5/17.
 */
public class Item {
    Date startTime;
    Date endTime;
    String description;

    public Item(String s, String e, String d) {
        startTime = new Date(s);
        endTime = new Date(e);
        description = d;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
