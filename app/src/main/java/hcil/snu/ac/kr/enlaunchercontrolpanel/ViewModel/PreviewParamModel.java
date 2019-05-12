package hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


/* *
* enavShape - 0: circle, 1: rectangle
* enavColor - ENAVs' color hex code
* kNum - if k > 0, k: # of independent ENAVs
* */
public class PreviewParamModel extends ViewModel {
    private MutableLiveData<Integer> enavShapeLiveData;
    private MutableLiveData<String> enavColorLiveData;
    private MutableLiveData<Integer> kNumLiveData;
    private MutableLiveData<StaticMode> staticModeLiveData;

    public void init(StaticMode staticMode, int k, int shape, String color) {
        staticModeLiveData = new MutableLiveData<>();
        staticModeLiveData.setValue(staticMode);
        enavShapeLiveData = new MutableLiveData<>();
        enavShapeLiveData.setValue(shape);
        enavColorLiveData = new MutableLiveData<>();
        enavColorLiveData.setValue(color);
        kNumLiveData = new MutableLiveData<>();
        kNumLiveData.setValue(k);

    }

    public void setStaticModeLiveData(StaticMode staticMode) {
        staticModeLiveData.setValue(staticMode);
    }

    public void setKNumLiveData(int k) {
        kNumLiveData.setValue(k);
    }

    public void setEnavShapeLiveData(int shape) {
        enavShapeLiveData.setValue(shape);
    }

    public void setEnavColorLiveData(String color) {
        enavColorLiveData.setValue(color);
    }

    public LiveData<StaticMode> getStaticModeLiveData() {
        return staticModeLiveData;
    }

    public LiveData<Integer> getKNumLiveData() {
        return kNumLiveData;
    }

    public LiveData<Integer> getEnavShapeLiveData() {
        return enavShapeLiveData;
    }

    public LiveData<String> getEnavColorLiveData() {
        return enavColorLiveData;
    }



}
