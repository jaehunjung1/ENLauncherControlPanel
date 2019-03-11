package hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView;

import java.util.ArrayList;

/* *
 *  각 AuraPreview의 모든 visual paramter들을 가지고 있는 container
 *  ControlPanel -> Preview로 drawing logic을 전달할 때 사용
 * */
public class VisualParamContainer {

    // AuraPreview 전체의 parameters
    public int kNum;
    public int enavShape;
    public int enavColor;

    // ENAV 각각의 parameters
    public ArrayList<Integer> enavVisualParamList;

    public VisualParamContainer(int kNum, int shape, int color, ArrayList<Integer> enavVisualParamList) {
        this.kNum = kNum;
        this.enavShape = shape;
        this.enavColor = color;
        this.enavVisualParamList = enavVisualParamList;
    }

}
