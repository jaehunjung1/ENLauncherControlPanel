package hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.StaticMode;


/* TODO convert this to AppHaloConfig
 * knumLiveData = maxNumOfIndependentNotifications
 *
 */


/* *
 *  각 AuraPreview의 모든 visual paramter들을 가지고 있는 container
 *  ControlPanel -> Preview로 drawing logic을 전달할 때 사용
 * */
public class VisualParamContainer {

    // AuraPreview 전체의 parameters
    public StaticMode staticMode;
    public int kNum;
    public int enavShape; // 0: circle | 1: square
    public String enavColor; // color palette name, or hex color code (Progress Mode)

    // ENAV 각각의 parameters
    public ArrayList<Integer> enavVisualParamList;

    public VisualParamContainer(StaticMode staticMode, int kNum, int shape, String color,
                                ArrayList<Integer> enavVisualParamList) {
        this.staticMode = staticMode;
        this.kNum = kNum;
        this.enavShape = shape;
        this.enavColor = color;
        this.enavVisualParamList = enavVisualParamList;
    }

}
