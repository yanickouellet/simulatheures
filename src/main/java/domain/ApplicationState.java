package domain;

public class ApplicationState {
    private Coordinate currentPosition;
    private EditionMode currentMode;
    private Map map;

    public ApplicationState() {
        currentPosition = new Coordinate();
        currentMode  = EditionMode.None;
        map = new Map();
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coordinate currentPosition) {
        this.currentPosition = currentPosition;
    }

    public EditionMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(EditionMode currentMode) {
        this.currentMode = currentMode;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}