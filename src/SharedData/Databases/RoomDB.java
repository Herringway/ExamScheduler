package SharedData.Databases;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Room;

//~--- JDK imports ------------------------------------------------------------

import java.util.Hashtable;
import java.util.Set;

public class RoomDB {
    private static RoomDB singleton;
    private static Hashtable<String, Room> database;

    private RoomDB() {
        database = new Hashtable<String, Room>();
    }

    public static RoomDB getRoomDB() {
        if (singleton == null) {
            singleton = new RoomDB();
        }

        return singleton;
    }

    public static void clear() {
        database = new Hashtable<String, Room>();
    }

    public Room getRoom(String roomIdentifier) {
        return database.get(roomIdentifier);
    }

    public String[] getRooms() {
        Set<String> roomIdentifiers = database.keySet();
        String[] roomList = roomIdentifiers.toArray(new String[] {});

        return roomList;
    }

    public void addRoom(String roomIdentifier, Room room) {
        database.put(roomIdentifier, room);
    }

    public boolean contains(String roomIdentifier) {
        return database.containsKey(roomIdentifier);
    }
}
