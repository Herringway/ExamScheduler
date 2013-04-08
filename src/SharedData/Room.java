package SharedData;

//~--- JDK imports ------------------------------------------------------------

/*Hey guys, I made some changes to room so that the Optimizer can use them
* I only added functionality, so you can essentially ignore any changes I've made.
* Be sure to include them though.
*
* -Zack Delaney
 */
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

public class Room {

    // instance variables are in flux. Don't make huge plans to
    // use an instance variable here, we're still working on it
    private Hashtable<Resource, Integer> resources;

    // private Schedule bookings;
    private String _name;

    public Room(String name) {
        _name = name;
        resources = new Hashtable<>();
    }

    // A deep copy function, needed for Optimization, as we make multiple copies of each room to symbolize them loosing resources as people fill them up.
    public Room(Room oldRoom) {
        System.out.println("COPYING A ROOM");

        _name = oldRoom.getName();

        System.out.println("The room's name to be copied is : " + _name);

        resources = new Hashtable<>();

        for (Resource r : Resource.values()) {
            System.out.println("Copying resources...");

            int resVal = oldRoom.getResource(r);

            System.out.println("Resource being copied is : " + r);
            System.out.println("Amount of resources available for this resouce : " + resVal);

            if (resVal == 0) {
                continue;
            }

            resources.put(r, new Integer(resVal));
        }

        System.out.println("WE HAVE COPIED THE RESOUCES");
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

    /*
     * Returns the quantity of of a given resource,
     * -1 means unknown (but exists),
     * 0 means none
     * x>0 is the quantity
     */
    public int getResource(Resource kind) {
        Integer quant = resources.get(kind);

        return (quant == null)
               ? 0
               : quant;    // may be null
    }

    /*
     * Sets the quantity of of a given resource,
     * -1 means unknown (but exists),
     * 0 means none (need not call this then)
     * x>0 is the quantity
     */
    public void addResource(Resource kind, Integer amount) {
        resources.put(kind, amount);
    }

    public Set<Entry<Resource, Integer>> getResources() {
        return resources.entrySet();
    }

    /**
     * Some resources may not specify how many are in a room. This is just a
     * default for Data Optimization to know about it
     */
    public static int getDefaultNumber() {
        return -1;
    }

    /*
     * Should add other gettings, I'm not spending time writing a million when
     * they may only want one or two that I don't think of.
     */
}
