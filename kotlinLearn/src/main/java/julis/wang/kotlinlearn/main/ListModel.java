package julis.wang.kotlinlearn.main;

/*******************************************************
 *
 * Created by julis.wang on 2019/09/24 14:19
 *
 * Description :
 * History   :
 *
 *******************************************************/

public class ListModel {
    public String activityName;
    public Class<?> activityClass;

    public ListModel(String activityName, Class<?> activityClass) {
        this.activityName = activityName;
        this.activityClass = activityClass;
    }
}
