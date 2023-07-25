import java.util.HashMap;

 public class ModelWholeRoom {
    public  int roomImage;
    public  String roomName;
    public  String turnOnInstructions;
    public  String turnOffInstructions;
    public  HashMap<Integer, DeviceData> roomDevices = new HashMap<Integer, DeviceData>();
    public  Boolean isFavourite;


}
