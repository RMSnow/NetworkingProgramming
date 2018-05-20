package TodoList;

import bean.Item;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by snow on 2018/5/17.
 */
public class UserOperationServantImpl extends UserOperationPOA {
    private ArrayList<Item> todoList = new ArrayList<Item>();

    public boolean add(String startTime, String endTime, String description) {
        Item item = new Item(startTime, endTime, description);
        todoList.add(item);
        return true;
    }

    public String query(String startTime, String endTime) {
        String result = "";

        String boarder = "************************************************************";
        System.out.println(boarder);
        String head = "任务序号\t起始时间\t截止时间\t任务描述";
        System.out.println(head);
        result += boarder + "\n" + head + "\n";

        for (int i = 0; i < todoList.size(); i++) {
            Item item = todoList.get(i);
            if (item.getStartTime().after(new Date(startTime))
                    && item.getEndTime().before(new Date(endTime))) {
                String task = i + "\t" + item.getStartTime() + "\t" + item.getEndTime() + "\t" + item.getDescription();
                System.out.println(task);
                result += task + "\n";
            }

        }
        System.out.println(boarder);
        result += boarder;
        return result;
    }

    public boolean delete(String key) {
        try {
            int num = Integer.getInteger(key);
            todoList.remove(num);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clear() {
        todoList = new ArrayList<Item>();
        return true;
    }

    public String show() {
        String result = "";

        String boarder = "************************************************************";
        System.out.println(boarder);
        String head = "任务序号\t起始时间\t截止时间\t任务描述";
        System.out.println(head);
        result += boarder + "\n" + head + "\n";

        for (int i = 0; i < todoList.size(); i++) {
            Item item = todoList.get(i);
            String task = i + "\t" + item.getStartTime() + "\t" + item.getEndTime() + "\t" + item.getDescription();
            System.out.println(task);
            result += task + "\n";
        }

        System.out.println(boarder);
        result += boarder;
        return result;
    }
}
