package ndkteset.lin.com.androidano;

public class MyViewInfo {
    public String name;
    public String superClass;

    @Override
    public String toString() {
        StringBuffer sb =new StringBuffer();
        sb.append("name=");
        sb.append(name);
        sb.append("superClass=");
        sb.append(superClass);
        return sb.toString();
    }
}
