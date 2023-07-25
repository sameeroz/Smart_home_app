public class DeviceData {
    public int imageId;
    public String deviceName;
    public String roomName;
    public String turnOnInstructions;
    public String turnOffInstructions;

    public DeviceData(int imageId, String deviceName, String roomName, String turnOnInstructions, String turnOffInstructions) {
        this.imageId = imageId;
        this.deviceName = deviceName;
        this.roomName = roomName;
        this.turnOnInstructions = turnOnInstructions;
        this.turnOffInstructions = turnOffInstructions;
    }
}
