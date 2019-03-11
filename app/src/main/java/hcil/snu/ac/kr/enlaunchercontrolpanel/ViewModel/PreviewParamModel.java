package hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/* *
* enavShape - 0: circle, 1: rectangle
* enavColor - ENAVs' color hex code
* kNum - if k >= 0, k: # of independent ENAVs / if k == -1, k = N
* */
public class PreviewParamModel extends ViewModel {
    private MutableLiveData<Integer> enavShapeLiveData;
    private MutableLiveData<Integer> enavColorLiveData;
    private MutableLiveData<Integer> kNumLiveData;

    public void init(int k, int shape, int color) {
        enavShapeLiveData = new MutableLiveData<>();
        enavShapeLiveData.setValue(shape);
        enavColorLiveData = new MutableLiveData<>();
        enavColorLiveData.setValue(color);
        kNumLiveData = new MutableLiveData<>();
        kNumLiveData.setValue(k);
    }

    public void setKNumLiveData(int k) {
        kNumLiveData.setValue(k);
    }

    public void setEnavShapeLiveData(int shape) {
        enavShapeLiveData.setValue(shape);
    }

    public void setEnavColorLiveData(int color) {
        enavColorLiveData.setValue(color);
    }

    public LiveData<Integer> getKNumLiveData() {
        return kNumLiveData;
    }

    public LiveData<Integer> getEnavShapeLiveData() {
        return enavShapeLiveData;
    }

    public LiveData<Integer> getEnavColorLiveData() {
        return enavColorLiveData;
    }

}
