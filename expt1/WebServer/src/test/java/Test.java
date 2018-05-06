/**
 * Created by snow on 2018/4/17.
 */
public class Test {
    public static void main(String[] args){
        String str = "Content-Disposition: form-data; name=\"username\"";

        String[] contents = str.split("\\s+");
        String[] names = contents[2].split("\"");
        String key = names[1];

        System.out.println(key);
    }
}
