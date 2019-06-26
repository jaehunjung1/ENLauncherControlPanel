package hcil.snu.ac.kr.enlaunchercontrolpanel.viewmodel;

public enum StaticMode {
    SNAKE, FLOWER, PIZZA, PROGRESS;

    @Override
    public String toString() {
        String orig = super.toString();
        return orig.substring(0, 1) + orig.substring(1).toLowerCase();
    }
}
