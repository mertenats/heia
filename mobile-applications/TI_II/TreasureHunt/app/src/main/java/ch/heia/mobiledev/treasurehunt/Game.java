package ch.heia.mobiledev.treasurehunt;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * This class is used to store the state of the game after a pression on the "back" button
 * We use Gson to serialize the class/create a instance
 */

public class Game {
    private int mCurrentBeaconMinor; // beacon's minor value to discover
    private int mDiscoverableBeaconsIndex;
    private ArrayList<DiscoverableBeacon> mDiscoverableBeacons;

    public Game() {
        // create manually the list of beacons to be discover during the game
        ArrayList<DiscoverableBeacon> discoverableBeacons = new ArrayList<>();
        discoverableBeacons.add(new DiscoverableBeacon(246, "https://onedrive.live.com/download.aspx?cid=9029D0C756D0A25C&resid=9029d0c756d0a25c%21434&authkey=%21AOyXHfk56pXiPPQ&canary=", -1, -1, -1));
        discoverableBeacons.add(new DiscoverableBeacon(247, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21435&authkey=AF_xhDl4O43BH5c", -1, -1, -1));
        discoverableBeacons.add(new DiscoverableBeacon(248, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21345&authkey=AFom4lFP0egO6Qs", -1, -1, -1));
        discoverableBeacons.add(new DiscoverableBeacon(249, "https://onedrive.live.com/download?cid=9029D0C756D0A25C&resid=9029D0C756D0A25C%21436&authkey=AIWa3ZShZQ-hJ8Q", -1, -1, -1));

        // assign the first beacon's ID and the beacoon's list to the game instance
        this.mDiscoverableBeacons = discoverableBeacons;
        this.mCurrentBeaconMinor = this.mDiscoverableBeacons.get(0).getMinor();
        this.mDiscoverableBeaconsIndex = 0;
    }

    /*
    public void initGame() {
        for (int i = 0; i < mDiscoverableBeacons.size(); i++) {
            if (this.mDiscoverableBeacons.get(i).isFound()) {
                this.mCurrentBeaconMinor = this.mDiscoverableBeacons.get(i).getMinor();
                this.mDiscoverableBeaconsIndex = i;
            } else {

            }
        }
    }
    */

    public ArrayList<DiscoverableBeacon> getDiscoverableBeacons() {
        return mDiscoverableBeacons;
    }

    public void setDiscoverableBeacons(ArrayList<DiscoverableBeacon> mDiscoverableBeacons) {
        this.mDiscoverableBeacons = mDiscoverableBeacons;
    }

    public int getDiscoverableBeaconsIndex() {
        return mDiscoverableBeaconsIndex;
    }

    public void setDiscoverableBeaconsIndex(int mDiscoverableBeaconsIndex) {
        this.mDiscoverableBeaconsIndex = mDiscoverableBeaconsIndex;
    }

    public int getCurrentBeaconMinor() {
        return mCurrentBeaconMinor;
    }

    public void setCurrentBeaconMinor(int mCurrentBeaconMinor) {
        this.mCurrentBeaconMinor = mCurrentBeaconMinor;
    }

    public static Game loadGame(String json) {
        Gson gson = new Gson();
        Game game = gson.fromJson(json, Game.class);
        return game;
    }
}
