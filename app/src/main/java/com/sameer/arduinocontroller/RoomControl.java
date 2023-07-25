package com.sameer.arduinocontroller;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomControl {
    public static Drawable roomImage;
    public static int roomNumber;
    public static List< ModelWholeRoom> allRoomsData = new ArrayList<ModelWholeRoom>();
    public static HashMap<String, TimerData> timers_data = new HashMap<String, TimerData>();

}
