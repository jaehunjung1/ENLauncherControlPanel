package hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class PreviewParamModel extends ViewModel {
    private MutableLiveData<Integer> enavShapeLiveData;
    private MutableLiveData<Integer> enavColorLiveData;

    public void init(int shape, int color) {
        enavShapeLiveData = new MutableLiveData<>();
        enavShapeLiveData.setValue(shape);
        enavColorLiveData = new MutableLiveData<>();
        enavColorLiveData.setValue(color);
    }

    public void setEnavShapeLiveData(int shape) {
        enavShapeLiveData.setValue(shape);
    }

    public void setEnavColorLiveData(int color) {
        enavColorLiveData.setValue(color);
    }

    public LiveData<Integer> getEnavShapeLiveData() {
        return enavShapeLiveData;
    }

    public LiveData<Integer> getEnavColorLiveData() {
        return enavColorLiveData;
    }
}
